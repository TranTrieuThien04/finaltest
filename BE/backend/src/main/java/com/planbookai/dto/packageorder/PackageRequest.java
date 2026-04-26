package com.planbookai.dto.packageorder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PackageRequest {
    @NotBlank
    private String name;

    @NotNull
    private BigDecimal price;

    @NotNull
    private Integer durationDays;

    private String description;
}
