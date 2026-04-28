package com.planbookai.controller;

import com.planbookai.dto.packageorder.OrderCreateRequest;
import com.planbookai.dto.packageorder.OrderResponse;
import com.planbookai.dto.packageorder.OrderStatusUpdateRequest;
import com.planbookai.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /** Teacher tạo đơn mua gói */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    /**
     * Danh sách đơn hàng có phân trang.
     * MANAGER/ADMIN xem tất cả; Teacher xem của mình.
     * GET /api/v1/orders?page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<OrderResponse> listOrders(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return orderService.listByRolePaged(pageable);
    }

    /** MANAGER/ADMIN cập nhật trạng thái đơn */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public OrderResponse updateStatus(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        return orderService.updateStatus(id, request);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public OrderResponse patchStatus(
            @PathVariable @NonNull Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        return orderService.updateStatus(id, request);
    }
}
