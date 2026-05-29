# Order Service API Documentation

Layanan ini menangani manajemen pesanan, perhitungan ongkos kirim (RajaOngkir), dan integrasi *payment gateway* (Midtrans).

## 🔐 Global Security & Headers

Hampir seluruh *endpoint* dalam layanan ini dilindungi oleh dua lapis keamanan (kecuali Webhook Midtrans):

1. **API Gateway Password** (Wajib untuk semua *request* internal)
* **Header**: `X-Service-Password`
* **Value**: Nilai dari variabel environment `SERVICE_API_KEY`


2. **JWT Authentication** (Wajib untuk rute yang membutuhkan data *user* atau hak akses tertentu)
* **Header**: `Authorization`
* **Value**: `Bearer <JWT_TOKEN>`



---

## 🛒 1. Customer Order API (`/customer`)

Endpoint untuk pengguna (pembeli). Wajib menyertakan header `Authorization: Bearer <token>` dan `X-Service-Password`.

### 1.1 Ambil Riwayat Pesanan Customer

* **Method**: `GET`
* **URL**: `/customer/{customerId}`
* **Path Parameter**:
* `customerId` (Long) - ID milik customer


* **Response Sukses (200 OK)**:
```json
{
  "status": 200,
  "message": "Berhasil mengambil data riwayat order milik customer.",
  "data": [ /* Array of OrderListResponse */ ]
}

```



### 1.2 Checkout (Buat Pesanan Baru)

* **Method**: `POST`
* **URL**: `/customer/checkout`
* **Catatan**: ID Customer akan diambil otomatis dari Token JWT, sehingga lebih aman.
* **Request Body** (`CheckoutRequest`):
```json
{
  "addressId": 1,
  "items": [ /* Array of ShopOrderDto */ ]
}

```


* **Response Sukses (200 OK)**:
```json
{
  "status": 200,
  "message": "Order berhasil dibuat, silakan lanjut ke pembayaran",
  "data": {
    "transactionId": "ORD-12345",
    "snapToken": "midtrans-snap-token-xyz",
    "redirectUrl": "https://app.midtrans.com/snap/v2/vtweb/..."
  }
}

```



### 1.3 Detail Pesanan Customer

* **Method**: `GET`
* **URL**: `/customer/order/{transactionId}`
* **Path Parameter**:
* `transactionId` (String) - ID transaksi pesanan


* **Response Sukses (200 OK)**:
```json
{
  "status": 200,
  "message": "Berhasil mengambil detail order",
  "data": { /* Order Object */ }
}

```



---

## 🏪 2. Seller Order API (`/seller`)

Endpoint untuk manajemen pesanan oleh penjual. Wajib menyertakan header `Authorization: Bearer <token>` (dengan Role: `SELLER`) dan `X-Service-Password`.

### 2.1 Ambil Semua Pesanan & Filter

* **Method**: `GET`
* **URL**: `/seller`
* **Query Parameters** (Semua Opsional):
* `shopId` (Long): ID toko penjual
* `status` (String): Status pesanan (PENDING, PROCESSING, SHIPPED, dll)
* `startDate` (String): Tanggal mulai format `YYYY-MM-DD`
* `endDate` (String): Tanggal akhir format `YYYY-MM-DD`
* `page` (Integer): Halaman (Default: 0)
* `size` (Integer): Jumlah data per halaman (Default: 10)


* **Response Sukses (200 OK)**:
```json
{
  "message": "Berhasil mengambil data pesanan",
  "data": [ /* Array of Orders */ ],
  "totalPages": 5,
  "totalItems": 50,
  "currentPage": 0
}

```



### 2.2 Proses Pesanan (Update Status & Resi)

* **Method**: `PATCH`
* **URL**: `/seller/{orderId}/process`
* **Path Parameter**:
* `orderId` (Long) - ID pesanan database


* **Request Body** (`UpdateOrderStatusRequest`):
```json
{
  "status": "SHIPPED",
  "trackingNumber": "RESI12345678"
}

```


* **Response Sukses (200 OK)**:
```json
{
  "message": "Status pesanan berhasil diperbarui",
  "data": { /* Updated Order Object */ }
}

```



### 2.3 Statistik Dashboard Penjual

* **Method**: `GET`
* **URL**: `/seller/statistics`
* **Query Parameter**:
* `shopId` (Long, Opsional)


* **Response Sukses (200 OK)**:
```json
{
  "message": "Berhasil mengambil statistik dashboard",
  "data": { /* Statistics Object */ }
}

```



---

## 📦 3. RajaOngkir API (`/rajaongkir_o_s`)

Endpoint untuk operasi layanan pengiriman barang. Membutuhkan `X-Service-Password`.

### 3.1 Ambil Daftar Kecamatan

* **Method**: `GET`
* **URL**: `/rajaongkir_o_s/district/{cityId}`
* **Path Parameter**:
* `cityId` (String) - ID Kota


* **Response Sukses (200 OK)**:
```json
{
  "status": 200,
  "message": "Berhasil mengambil data kecamatan",
  "data": { /* RajaOngkir Response */ }
}

```



### 3.2 Kalkulasi Ongkir Keranjang

* **Method**: `POST`
* **URL**: `/rajaongkir_o_s/calculate-cart-options`
* **Request Body** (`CartShippingRequest`):
```json
{
  "originDistrictId": "123",
  "destinationDistrictId": "456",
  "items": [ /* Items Array */ ]
}

```


* **Response Sukses (200 OK)**:
```json
{
  "status": 200,
  "message": "Berhasil menghitung ongkir dari keranjang",
  "data": { /* List of Available Shipping Options */ }
}

```



---

## 💳 4. Payment API (`/payments`)

Layanan internal Webhook Midtrans. **TIDAK PERLU** `X-Service-Password` atau JWT, dikonfigurasi terbuka (`permitAll`) untuk menerima *post-back* dari Midtrans.

### 4.1 Midtrans Webhook

* **Method**: `POST`
* **URL**: `/payments/midtrans-webhook`
* **Request Body** (`MidtransNotificationDto`):
* (Format standar *notification push* dari Midtrans)


* **Response Sukses (200 OK)**:
```json
{
  "status": 200,
  "message": "Webhook Midtrans berhasil diproses",
  "data": { /* Updated Payment Entity */ }
}

```



---

## ⚙️ 5. General Order API (`/orders`)

Endpoint umum manajemen pesanan internal.

### 5.1 Ambil Semua Pesanan Sistem

* **Method**: `GET`
* **URL**: `/orders`
* **Response Sukses (200 OK)**:
```json
{
  "status": 200,
  "message": "There is order",
  "data": [ /* Array of All Orders */ ]
}

```



---

## ⚠️ Standard Error Responses

Sistem memiliki format error yang terpusat melalui Spring Security maupun Response Entity biasa.

* **401 Unauthorized**:
```json
{
  "status": 401,
  "message": "Akses ditolak: Anda belum login atau token tidak valid.",
  "data": null
}

```


* **403 Forbidden**:
```json
{
  "status": 403,
  "message": "Akses dilarang: Anda tidak memiliki izin (role) untuk rute ini.",
  "data": null
}

```


* **400 Bad Request / 500 Internal Server Error**:
```json
{
  "status": 400,
  "message": "Gagal: <Pesan detail error>",
  "data": null
}

```