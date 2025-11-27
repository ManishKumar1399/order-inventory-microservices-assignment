package com.service.inventory.repository;

import com.service.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByProductIdOrderByExpiryDateAsc(String productId);
    Optional<Inventory> findByProductIdAndBatchId(String productId, String batchId);
}