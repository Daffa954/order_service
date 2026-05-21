package com.example.order_service.controller;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CheckoutResponse {
    private String transactionId;
    private String snapToken;
    private String redirectUrl;
}
