package com.planbookai.service;

import com.planbookai.dto.packageorder.OrderCreateRequest;
import com.planbookai.dto.packageorder.OrderResponse;
import com.planbookai.dto.packageorder.OrderStatusUpdateRequest;
import org.springframework.lang.NonNull;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderCreateRequest request);

    List<OrderResponse> listByRole();

    OrderResponse updateStatus(@NonNull Long orderId, OrderStatusUpdateRequest request);
}
