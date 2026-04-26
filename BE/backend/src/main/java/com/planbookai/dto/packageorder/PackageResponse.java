package com.planbookai.dto.packageorder;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PackageResponse {
    private Long packageId;
    private String name;
    private BigDecimal price;
    private Integer durationDays;
    private String description;
    private LocalDateTime createdAt;
}
