package com.service.inventory.controller;

import com.service.inventory.dto.InventoryResponse;
import com.service.inventory.dto.UpdateRequest;
import com.service.inventory.factory.InventoryServiceFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
    @Autowired
    private final InventoryServiceFactory inventoryServiceFactory;

    @Operation(summary = "Get inventory batches by product ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found inventory batches"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<List<InventoryResponse>> getInventoryByProductId(@PathVariable String productId) {
        List<InventoryResponse> inventory = inventoryServiceFactory.getService().getInventoryByProductId(productId);
        return ResponseEntity.ok(inventory);
    }

    @Operation(summary = "Update inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/update")
    public ResponseEntity<Void> updateInventory(@RequestBody UpdateRequest updateRequest) {
        inventoryServiceFactory.getService().updateInventory(updateRequest);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
