package com.service.order.service;

import com.service.order.dto.InventoryResponse;
import com.service.order.dto.OrderRequest;
import com.service.order.dto.OrderResponse;
import com.service.order.dto.UpdateRequest;
import com.service.order.entity.Order;
import com.service.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Override
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        // 1. Check inventory
        InventoryResponse[] inventoryBatches = restTemplate.getForObject(
                inventoryServiceUrl + "/" + orderRequest.getProductId(),
                InventoryResponse[].class);

        if (inventoryBatches == null || inventoryBatches.length == 0) {
            throw new IllegalArgumentException("Product not found in inventory");
        }

        List<InventoryResponse> batches = Arrays.asList(inventoryBatches);
        int totalQuantity = batches.stream().mapToInt(InventoryResponse::getQuantity).sum();

        if (totalQuantity < orderRequest.getQuantity()) {
            throw new IllegalArgumentException("Product out of stock");
        }

        // 2. Reduce stock in inventory service
        int remainingQuantityToFulfill = orderRequest.getQuantity();
        for (InventoryResponse batch : batches) {
            if (remainingQuantityToFulfill == 0) {
                break;
            }

            int quantityToTakeFromBatch = Math.min(batch.getQuantity(), remainingQuantityToFulfill);

            UpdateRequest updateRequest = new UpdateRequest(
                    batch.getProductId(),
                    batch.getBatchId(),
                    -quantityToTakeFromBatch,
                    batch.getExpiryDate()
            );

            restTemplate.postForObject(inventoryServiceUrl + "/update", updateRequest, Void.class);

            remainingQuantityToFulfill -= quantityToTakeFromBatch;
        }


        // 3. Save order
        Order order = new Order();
        order.setProductId(orderRequest.getProductId());
        order.setQuantity(orderRequest.getQuantity());
        order = orderRepository.save(order);

        return new OrderResponse(order.getId(), order.getProductId(), order.getQuantity());
    }
}
