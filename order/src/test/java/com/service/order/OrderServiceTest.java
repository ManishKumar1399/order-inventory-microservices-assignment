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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        ReflectionTestUtils.setField(orderService, "inventoryServiceUrl", "http://localhost:8081/api/inventory");
    }

    @Test
    void placeOrder_shouldSucceed_whenStockIsAvailable() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId(1L);
        orderRequest.setQuantity(10);

        InventoryResponse inventoryResponse = new InventoryResponse();
        inventoryResponse.setProductId(1L);
        inventoryResponse.setQuantity(100);

        Order savedOrder = new Order(1L, 1L, 10);

        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(inventoryResponse);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Act
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);

        // Assert
        assertEquals(savedOrder.getId(), orderResponse.getOrderId());
    }

    @Test
    void placeOrder_shouldFail_whenStockIsInsufficient() {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId(1L);
        orderRequest.setQuantity(100);

        InventoryResponse inventoryResponse = new InventoryResponse();
        inventoryResponse.setProductId(1L);
        inventoryResponse.setQuantity(10);

        when(restTemplate.getForObject(anyString(), any(Class.class))).thenReturn(inventoryResponse);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder(orderRequest);
        });
    }
}
