package com.service.inventory.service;

import com.service.inventory.dto.InventoryResponse;
import com.service.inventory.dto.UpdateRequest;
import com.service.inventory.model.Inventory;
import com.service.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultInventoryService implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public List<InventoryResponse> getInventoryByProductId(String productId) {
        return inventoryRepository.findByProductIdOrderByExpiryDateAsc(productId)
                .stream()
                .map(this::mapToInventoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateInventory(UpdateRequest updateRequest) {
        inventoryRepository.findByProductIdAndBatchId(updateRequest.getProductId(), updateRequest.getBatchId())
                .ifPresentOrElse(inventory -> {
                    // Update existing inventory
                    inventory.setQuantity(inventory.getQuantity() + updateRequest.getQuantity());
                    inventoryRepository.save(inventory);
                }, () -> {
                    // Create new inventory record
                    Inventory inventory = new Inventory();
                    inventory.setProductId(updateRequest.getProductId());
                    inventory.setBatchId(updateRequest.getBatchId());
                    inventory.setQuantity(updateRequest.getQuantity());
                    inventory.setExpiryDate(updateRequest.getExpiryDate());
                    inventoryRepository.save(inventory);
                });
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getProductId(),
                inventory.getBatchId(),
                inventory.getQuantity(),
                inventory.getExpiryDate()
        );
    }
}
