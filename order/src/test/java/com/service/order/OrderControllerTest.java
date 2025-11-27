package com.service.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.order.dto.InventoryResponse;
import com.service.order.dto.OrderRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void placeOrder_shouldReturnCreated_whenOrderIsPlaced() throws Exception {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId("1");
        orderRequest.setQuantity(5);

        InventoryResponse[] inventoryResponses = new InventoryResponse[]{
                new InventoryResponse("1", "B1", 100, null)
        };

        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(inventoryResponses);

        // Act & Assert
        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value("1"))
                .andExpect(jsonPath("$.quantity").value(5));
    }

    @Test
    void placeOrder_shouldReturnBadRequest_whenStockIsInsufficient() throws Exception {
        // Arrange
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setProductId("1");
        orderRequest.setQuantity(15);

        InventoryResponse[] inventoryResponses = new InventoryResponse[]{
                new InventoryResponse("1", "B1", 10, null)
        };

        when(restTemplate.getForObject(anyString(), any(Class.class)))
                .thenReturn(inventoryResponses);

        // Act & Assert
        mockMvc.perform(post("/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Product out of stock"));
    }
}
