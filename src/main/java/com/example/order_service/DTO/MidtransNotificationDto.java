package com.example.order_service.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MidtransNotificationDto {
    
    @JsonProperty("order_id")
    private String orderId;
    
    @JsonProperty("transaction_status")
    private String transactionStatus;
    
    @JsonProperty("status_code")
    private String statusCode;
    
    @JsonProperty("gross_amount")
    private String grossAmount;
    
    @JsonProperty("signature_key")
    private String signatureKey;
    
    @JsonProperty("payment_type")
    private String paymentType;

    @JsonProperty("transaction_id")
    private String midtransTransactionId;

    @JsonProperty("transaction_time")
    private String transactionTime;
}