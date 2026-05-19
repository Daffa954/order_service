package com.example.order_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class APIResponse<T> {
    private int status;
    private String message;
    private T data;
}