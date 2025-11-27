package com.service.order.service;

import com.service.order.dto.OrderRequest;
import com.service.order.dto.OrderResponse;

public interface OrderService {
    OrderResponse placeOrder(OrderRequest orderRequest);
}
