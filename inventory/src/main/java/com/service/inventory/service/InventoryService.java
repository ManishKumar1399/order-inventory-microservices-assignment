package com.service.inventory.service;

import com.service.inventory.dto.InventoryResponse;
import com.service.inventory.dto.UpdateRequest;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse> getInventoryByProductId(String productId);
    void updateInventory(UpdateRequest updateRequest);
}
