package com.example.order_service.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MidtransNotificationDto {
    private String order_id;
    private String transaction_status;
    private String status_code;
    private String gross_amount;
    private String signature_key;
    private String payment_type;
}
