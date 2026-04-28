package com.planbookai.service;

import com.planbookai.dto.packageorder.OrderCreateRequest;
import com.planbookai.dto.packageorder.OrderResponse;
import com.planbookai.dto.packageorder.OrderStatusUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import java.util.List;

public interface OrderService {

    OrderResponse create(OrderCreateRequest request);

    /** Không phân trang — giữ tương thích ngược */
    List<OrderResponse> listByRole();

    /** Phân trang — dùng cho production list */
    Page<OrderResponse> listByRolePaged(Pageable pageable);

    OrderResponse updateStatus(@NonNull Long orderId, OrderStatusUpdateRequest request);
}
