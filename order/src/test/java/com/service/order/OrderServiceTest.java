package com.service.order;

import com.service.order.dto.InventoryResponse;
import com.service.order.dto.OrderRequest;
import com.service.order.dto.OrderResponse;
import com.service.order.entity.Order;
import com.service.order.repository.OrderRepository;
import com.service.order.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(orderService, "inventoryServiceUrl", "http://localhost:8080/inventory");
    }

    @Test
    void placeOrder_shouldSucceed_whenStockIsAvailable() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId("1");
        orderRequest.setQuantity(10);

        InventoryResponse[] inventoryResponses = new InventoryResponse[]{
                new InventoryResponse("1", "B1", 100, LocalDate.now().plusDays(10))
        };

        Order savedOrder = new Order(1L, "1", 10);

        when(restTemplate.getForObject(anyString(), eq(InventoryResponse[].class))).thenReturn(inventoryResponses);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(restTemplate.postForObject(anyString(), any(), eq(Void.class))).thenReturn(null);

        // Act
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);

        // Assert
        assertNotNull(orderResponse);
        assertEquals(savedOrder.getId(), orderResponse.getId());
        assertEquals(savedOrder.getProductId(), orderResponse.getProductId());
        assertEquals(savedOrder.getQuantity(), orderResponse.getQuantity());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(InventoryResponse[].class));
        verify(restTemplate, times(1)).postForObject(anyString(), any(), eq(Void.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void placeOrder_shouldFail_whenProductNotFound() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId("nonexistent");
        orderRequest.setQuantity(10);

        when(restTemplate.getForObject(anyString(), eq(InventoryResponse[].class))).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder(orderRequest);
        });
        assertEquals("Product not found in inventory", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(InventoryResponse[].class));
        verify(orderRepository, times(0)).save(any(Order.class));
        verify(restTemplate, times(0)).postForObject(anyString(), any(), eq(Void.class));
    }

    @Test
    void placeOrder_shouldFail_whenStockIsInsufficient() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId("1");
        orderRequest.setQuantity(100);

        InventoryResponse[] inventoryResponses = new InventoryResponse[]{
                new InventoryResponse("1", "B1", 10, LocalDate.now().plusDays(10))
        };

        when(restTemplate.getForObject(anyString(), eq(InventoryResponse[].class))).thenReturn(inventoryResponses);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder(orderRequest);
        });
        assertEquals("Product out of stock", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(InventoryResponse[].class));
        verify(orderRepository, times(0)).save(any(Order.class));
        verify(restTemplate, times(0)).postForObject(anyString(), any(), eq(Void.class));
    }

    @Test
    void placeOrder_shouldSucceed_withMultipleBatches() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId("2");
        orderRequest.setQuantity(70);

        InventoryResponse[] inventoryResponses = new InventoryResponse[]{
                new InventoryResponse("2", "B1", 20, LocalDate.now().plusDays(5)), // Earliest expiry
                new InventoryResponse("2", "B2", 30, LocalDate.now().plusDays(10)),
                new InventoryResponse("2", "B3", 50, LocalDate.now().plusDays(15))
        };

        Order savedOrder = new Order(1L, "2", 70);

        when(restTemplate.getForObject(anyString(), eq(InventoryResponse[].class))).thenReturn(inventoryResponses);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(restTemplate.postForObject(anyString(), any(), eq(Void.class))).thenReturn(null);

        // Act
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);

        // Assert
        assertNotNull(orderResponse);
        assertEquals(savedOrder.getId(), orderResponse.getId());
        assertEquals(savedOrder.getProductId(), orderResponse.getProductId());
        assertEquals(savedOrder.getQuantity(), orderResponse.getQuantity());

        // Verify calls to update inventory for each batch
        verify(restTemplate, times(1)).getForObject(anyString(), eq(InventoryResponse[].class));
        // Expecting 3 calls for postForObject as 70 units from 20+30+50 batches
        verify(restTemplate, times(3)).postForObject(anyString(), any(), eq(Void.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }
}
