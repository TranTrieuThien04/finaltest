package com.planbookai.dto.analytics;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class AnalyticsSummaryResponse {

    // Users
    private long totalUsers;
    private long activeUsers;

    // Questions
    private long totalQuestions;
    private long pendingQuestions;
    private long approvedQuestions;
    private long rejectedQuestions;

    // Orders
    private long totalOrders;
    private long pendingOrders;
    private long approvedOrders;

    // Revenue
    private BigDecimal totalRevenue;
}
