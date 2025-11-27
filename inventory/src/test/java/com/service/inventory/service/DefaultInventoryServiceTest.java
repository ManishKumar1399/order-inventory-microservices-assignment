package com.service.inventory.service;

import com.service.inventory.dto.InventoryResponse;
import com.service.inventory.model.Inventory;
import com.service.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultInventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private DefaultInventoryService inventoryService;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory(1L, "prod1", "batch1", 100, LocalDate.now().plusDays(10));
    }

    @Test
    void whenGetInventoryByProductId_thenReturnInventoryList() {
        when(inventoryRepository.findByProductIdOrderByExpiryDateAsc("prod1")).thenReturn(Collections.singletonList(inventory));

        List<InventoryResponse> responses = inventoryService.getInventoryByProductId("prod1");

        assertEquals(1, responses.size());
        assertEquals("prod1", responses.get(0).getProductId());
    }
}
