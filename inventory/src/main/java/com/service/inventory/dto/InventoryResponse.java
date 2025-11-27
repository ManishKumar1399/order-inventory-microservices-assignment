package com.service.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private String productId;
    private String batchId;
    private Integer quantity;
    private LocalDate expiryDate;
}
