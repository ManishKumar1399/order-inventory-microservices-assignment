package com.service.order.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String productId;
    private int quantity;
}
