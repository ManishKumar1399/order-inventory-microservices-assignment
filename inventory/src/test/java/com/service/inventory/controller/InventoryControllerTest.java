package com.service.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.inventory.dto.UpdateRequest;
import com.service.inventory.model.Inventory;
import com.service.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        inventoryRepository.deleteAll();
    }

    @Test
    void whenGetInventoryByProductId_thenReturnInventory() throws Exception {
        Inventory inventory = new Inventory(null, "prod1", "batch1", 100, LocalDate.now().plusDays(10));
        inventoryRepository.save(inventory);

        mockMvc.perform(get("/inventory/{productId}", "prod1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("prod1"));
    }

    @Test
    void whenUpdateInventory_thenCreateInventory() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest("prod1", "batch2", 50, LocalDate.now().plusDays(20));

        mockMvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/inventory/{productId}", "prod1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("prod1"))
                .andExpect(jsonPath("$[0].quantity").value(50));
    }

    @Test
    void whenUpdateExistingInventory_thenUpdateQuantity() throws Exception {
        Inventory existingInventory = new Inventory(null, "prod1", "batch1", 100, LocalDate.now().plusDays(10));
        inventoryRepository.save(existingInventory);

        UpdateRequest updateRequest = new UpdateRequest("prod1", "batch1", -20, null); // Expiry date is not needed for update

        mockMvc.perform(post("/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/inventory/{productId}", "prod1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("prod1"))
                .andExpect(jsonPath("$[0].quantity").value(80));
    }
}
