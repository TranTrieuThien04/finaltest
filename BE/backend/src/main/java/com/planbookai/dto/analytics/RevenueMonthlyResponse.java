package com.planbookai.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class RevenueMonthlyResponse {
    private int year;
    private int month;
    private BigDecimal revenue;
}
