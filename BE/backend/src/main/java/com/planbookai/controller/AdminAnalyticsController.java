package com.planbookai.controller;

import com.planbookai.dto.analytics.AnalyticsSummaryResponse;
import com.planbookai.dto.analytics.RevenueMonthlyResponse;
import com.planbookai.entity.enums.OrderStatus;
import com.planbookai.entity.enums.QuestionStatus;
import com.planbookai.repository.QuestionRepository;
import com.planbookai.repository.UserOrderRepository;
import com.planbookai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
public class AdminAnalyticsController {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final UserOrderRepository orderRepository;

    /**
     * Tổng hợp số liệu nhanh cho admin dashboard.
     * GET /api/v1/admin/analytics/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummaryResponse> summary() {
        long totalUsers   = userRepository.count();
        long activeUsers  = userRepository.countActiveUsers();

        long totalQ    = questionRepository.count();
        long pendingQ  = questionRepository.countByStatus(QuestionStatus.PENDING);
        long approvedQ = questionRepository.countByStatus(QuestionStatus.APPROVED);
        long rejectedQ = questionRepository.countByStatus(QuestionStatus.REJECTED);

        long totalOrders   = orderRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long approvedOrders= orderRepository.countByStatus(OrderStatus.APPROVED);

        BigDecimal totalRevenue = orderRepository.totalRevenue();

        return ResponseEntity.ok(AnalyticsSummaryResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalQuestions(totalQ)
                .pendingQuestions(pendingQ)
                .approvedQuestions(approvedQ)
                .rejectedQuestions(rejectedQ)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .approvedOrders(approvedOrders)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .build());
    }

    /**
     * Doanh thu theo tháng.
     * GET /api/v1/admin/analytics/revenue?year=2025
     */
    @GetMapping("/revenue")
    public ResponseEntity<List<RevenueMonthlyResponse>> revenue(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") int year
    ) {
        List<Object[]> rows = orderRepository.revenueByMonth(year);
        List<RevenueMonthlyResponse> result = rows.stream()
                .map(row -> new RevenueMonthlyResponse(
                        year,
                        ((Number) row[0]).intValue(),
                        row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO
                ))
                .toList();
        return ResponseEntity.ok(result);
    }

    /**
     * Số đơn hàng theo trạng thái.
     * GET /api/v1/admin/analytics/orders/summary
     */
    @GetMapping("/orders/summary")
    public ResponseEntity<Object> ordersSummary() {
        return ResponseEntity.ok(java.util.Map.of(
                "total",    orderRepository.count(),
                "pending",  orderRepository.countByStatus(OrderStatus.PENDING),
                "approved", orderRepository.countByStatus(OrderStatus.APPROVED),
                "rejected", orderRepository.countByStatus(OrderStatus.REJECTED),
                "cancelled",orderRepository.countByStatus(OrderStatus.CANCELLED)
        ));
    }

    /**
     * Số câu hỏi theo trạng thái.
     * GET /api/v1/admin/analytics/questions/summary
     */
    @GetMapping("/questions/summary")
    public ResponseEntity<Object> questionsSummary() {
        return ResponseEntity.ok(java.util.Map.of(
                "total",    questionRepository.count(),
                "pending",  questionRepository.countByStatus(QuestionStatus.PENDING),
                "approved", questionRepository.countByStatus(QuestionStatus.APPROVED),
                "rejected", questionRepository.countByStatus(QuestionStatus.REJECTED)
        ));
    }
}
