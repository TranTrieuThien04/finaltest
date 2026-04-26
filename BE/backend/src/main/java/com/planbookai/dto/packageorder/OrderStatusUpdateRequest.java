package com.planbookai.dto.packageorder;

import com.planbookai.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusUpdateRequest {
    @NotNull
    private OrderStatus status;
}
