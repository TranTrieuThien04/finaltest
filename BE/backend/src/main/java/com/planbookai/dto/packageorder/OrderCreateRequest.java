package com.planbookai.dto.packageorder;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateRequest {
    @NotNull
    private Long packageId;
}
