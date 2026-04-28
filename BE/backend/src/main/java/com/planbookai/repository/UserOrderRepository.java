package com.planbookai.repository;

import com.planbookai.entity.UserOrder;
import com.planbookai.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {

    List<UserOrder> findByUser_UserId(Long userId);

    Page<UserOrder> findByUser_UserId(Long userId, Pageable pageable);

    Page<UserOrder> findAll(Pageable pageable);

    // ---- Analytics queries ----

    long countByStatus(OrderStatus status);

    /**
     * Doanh thu theo tháng trong một năm (chỉ đơn APPROVED).
     * Trả về mảng [month(int), revenue(BigDecimal)].
     */
    @Query("""
            SELECT MONTH(o.createdAt), SUM(p.price)
            FROM UserOrder o JOIN o.subscriptionPackage p
            WHERE o.status = 'APPROVED'
              AND YEAR(o.createdAt) = :year
            GROUP BY MONTH(o.createdAt)
            ORDER BY MONTH(o.createdAt)
            """)
    List<Object[]> revenueByMonth(@Param("year") int year);

    /**
     * Tổng doanh thu (chỉ đơn APPROVED).
     */
    @Query("""
            SELECT COALESCE(SUM(p.price), 0)
            FROM UserOrder o JOIN o.subscriptionPackage p
            WHERE o.status = 'APPROVED'
            """)
    java.math.BigDecimal totalRevenue();
}
