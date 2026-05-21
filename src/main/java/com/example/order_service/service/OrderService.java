package com.example.order_service.service;

import com.example.order_service.DTO.CheckoutItemDto;
import com.example.order_service.DTO.CheckoutRequest;
import com.example.order_service.DTO.OrderListResponse;
import com.example.order_service.DTO.ShippingCourierDto;
import com.example.order_service.DTO.ShopOrderDto;
import com.example.order_service.controller.CheckoutResponse;
import com.example.order_service.entity.Order;
import com.example.order_service.entity.OrderItem;
import com.example.order_service.entity.Payment;
import com.example.order_service.enums.OrderStatus;
import com.example.order_service.repository.OrderRepository;
import com.example.order_service.repository.PaymentRepository;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public OrderService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    // Mengambil semua data order
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // getOrderBasedOnUserId
    public List<OrderListResponse> getOrderListBasedOnUserId(Long userId) {
        List<Order> orders = orderRepository.findByCustomerId(userId);

        // Proses mengubah Entity Order menjadi DTO OrderListResponse
        return orders.stream().map(order -> new OrderListResponse(
                order.getId(),
                order.getTransactionId(),
                order.getShopName(),
                order.getTotalAmount(),
                order.getOrderStatus().toString(),
                order.getOrderDate())).collect(Collectors.toList());

    }

    @Transactional // Wajib agar jika Midtrans error, data DB otomatis di-rollback
    public CheckoutResponse createOrder(CheckoutRequest request) throws MidtransError {

        // 1. Generate ID Transaksi
        String transactionId = generateTransactionId(request.getCustomerId());

        // 2. Kelompokkan & Buat Order per Toko
        List<Order> bulkOrders = buildOrdersPerShop(request, transactionId);

        // 3. Simpan semua Order ke Database
        orderRepository.saveAll(bulkOrders);

        // 4. Hitung Grand Total dari seluruh toko
        BigDecimal grandTotalAllShops = calculateGrandTotal(bulkOrders);

        // 5. Tembak API Midtrans
        CheckoutResponse midtransResponse = requestMidtransTransaction(transactionId, grandTotalAllShops,
                request.getCustomerName());

        // 6. Simpan riwayat Payment awal
        savePendingPayment(transactionId, grandTotalAllShops);

        return midtransResponse;
    }

    // ===================================================================================
    // PRIVATE HELPER METHODS (Fungsi-fungsi kecil pendukung Clean Code)
    // ===================================================================================

    private String generateTransactionId(Long customerId) {
        // 1. Ambil waktu sampai tingkat detik (Format: YYMMDDHHMMSS)
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));

        // 2. Ambil 4 digit UUID acak (dipendekkan agar ID tidak terlalu panjang di
        // database)
        String shortUuid = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        // 3. Gabungkan semuanya
        return "TX-" + timestamp + "-" + customerId + "-" + shortUuid;
    }

    private List<Order> buildOrdersPerShop(CheckoutRequest request, String transactionId) {
        List<Order> bulkOrders = new ArrayList<>();

        // Loop langsung dari shopOrders (karena dari React sudah dikelompokkan per
        // toko)
        for (ShopOrderDto shopGroup : request.getShopOrders()) {
            Order order = buildSingleShopOrder(request, shopGroup, transactionId);
            bulkOrders.add(order);
        }
        return bulkOrders;
    }

    private Order buildSingleShopOrder(CheckoutRequest request, ShopOrderDto shopGroup, String transactionId) {
        Order order = new Order();
        order.setTransactionId(transactionId);
        order.setCustomerId(request.getCustomerId());
        order.setCustomerName(request.getCustomerName());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());

        // Setup Alamat Lengkap (Dari request global)
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingProvince(request.getShippingProvince());
        order.setShippingCity(request.getShippingCity());
        order.setShippingDistrict(request.getShippingDistrict());

        // Setup Info Toko
        order.setShopId(shopGroup.getShopId());
        order.setShopName(shopGroup.getItems().get(0).getShopName());

        // Setup Logistik (Ambil spesifik dari kurir pilihan toko ini)
        ShippingCourierDto courier = shopGroup.getShippingCourier();
        order.setCourierName(courier.getName());
        order.setCourierCode(courier.getName().toLowerCase());
        order.setCourierService(courier.getService());
        order.setEtd(courier.getEtd());

        // Ambil ongkir (fallback ke 0 jika kosong)
        Double actualShippingCost = courier.getCost() != null ? courier.getCost() : 0.0;
        order.setShippingCost(actualShippingCost);

        BigDecimal shopTotalItemsPrice = BigDecimal.ZERO;
        int totalWeightPerShop = 0;

        // Setup Items yang ada di dalam toko ini
        for (CheckoutItemDto itemDto : shopGroup.getItems()) {
            OrderItem item = buildOrderItem(itemDto, order);
            order.getOrderItems().add(item);

            shopTotalItemsPrice = shopTotalItemsPrice.add(item.getSubTotalPrice());
            totalWeightPerShop += (itemDto.getWeight() * itemDto.getQuantity());
        }

        order.setTotalWeight((double) totalWeightPerShop);

        // Hitung final harga toko ini (Total Barang + Ongkir Toko Ini)
        BigDecimal finalShopPrice = shopTotalItemsPrice.add(BigDecimal.valueOf(actualShippingCost));
        order.setTotalAmount(finalShopPrice);

        return order;
    }

    private OrderItem buildOrderItem(CheckoutItemDto itemDto, Order order) {
        OrderItem item = new OrderItem();
        item.setProductId(itemDto.getProductId());
        item.setProductName(itemDto.getProductName());
        item.setQuantity(itemDto.getQuantity());
        item.setPricePerUnit(itemDto.getPricePerUnit());
        item.setWeight(itemDto.getWeight());

        BigDecimal itemTotalPrice = itemDto.getPricePerUnit().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
        item.setSubTotalPrice(itemTotalPrice);

        // Relasikan dengan Order Induk
        item.setOrder(order);
        return item;
    }

    private BigDecimal calculateGrandTotal(List<Order> bulkOrders) {
        return bulkOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CheckoutResponse requestMidtransTransaction(String transactionId, BigDecimal grandTotal,
            String customerName) throws MidtransError {
        Map<String, Object> params = new HashMap<>();

        Map<String, String> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", transactionId);
        transactionDetails.put("gross_amount", grandTotal.toBigInteger().toString());
        params.put("transaction_details", transactionDetails);

        Map<String, String> customerDetails = new HashMap<>();
        customerDetails.put("first_name", customerName);
        params.put("customer_details", customerDetails);

        String snapToken = SnapApi.createTransactionToken(params);
        String redirectUrl = SnapApi.createTransactionRedirectUrl(params);

        return new CheckoutResponse(transactionId, snapToken, redirectUrl);
    }

    private void savePendingPayment(String transactionId, BigDecimal grandTotal) {
        Payment payment = new Payment();
        payment.setTransactionId(transactionId);
        payment.setPaymentAmount(grandTotal);
        payment.setPaymentStatus("PENDING");
        paymentRepository.save(payment);
    }

}