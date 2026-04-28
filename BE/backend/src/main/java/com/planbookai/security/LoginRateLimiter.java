// BE/src/main/java/com/planbookai/security/LoginRateLimiter.java
package com.planbookai.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory rate limiter cho /api/auth/login.
 * Giới hạn: tối đa 5 lần thất bại / IP / 15 phút.
 * Sau 5 lần thất bại → block 15 phút.
 */
@Component
public class LoginRateLimiter {

    private static final int    MAX_ATTEMPTS   = 5;
    private static final long   WINDOW_MS      = 15 * 60 * 1000L; // 15 phút
    private static final long   BLOCK_MS       = 15 * 60 * 1000L; // block 15 phút

    private record AttemptRecord(int count, Instant firstAttempt, Instant blockedUntil) {}

    private final ConcurrentHashMap<String, AttemptRecord> attempts = new ConcurrentHashMap<>();

    /** Kiểm tra IP có đang bị block không */
    public boolean isBlocked(String ip) {
        AttemptRecord record = attempts.get(ip);
        if (record == null) return false;

        // Hết block window → xóa record
        if (record.blockedUntil() != null && Instant.now().isAfter(record.blockedUntil())) {
            attempts.remove(ip);
            return false;
        }

        return record.blockedUntil() != null;
    }

    /** Ghi nhận 1 lần đăng nhập thất bại */
    public void recordFailure(String ip) {
        Instant now = Instant.now();

        attempts.compute(ip, (key, existing) -> {
            if (existing == null || now.isAfter(existing.firstAttempt().plusMillis(WINDOW_MS))) {
                // Lần đầu hoặc đã qua window cũ → reset
                return new AttemptRecord(1, now, null);
            }

            int newCount = existing.count() + 1;
            if (newCount >= MAX_ATTEMPTS) {
                // Đạt ngưỡng → block
                return new AttemptRecord(newCount, existing.firstAttempt(),
                        now.plusMillis(BLOCK_MS));
            }

            return new AttemptRecord(newCount, existing.firstAttempt(), null);
        });
    }

    /** Đăng nhập thành công → xóa lịch sử thất bại */
    public void recordSuccess(String ip) {
        attempts.remove(ip);
    }

    /** Trả về thời gian còn bị block (giây), 0 nếu không bị block */
    public long getBlockRemainingSeconds(String ip) {
        AttemptRecord record = attempts.get(ip);
        if (record == null || record.blockedUntil() == null) return 0L;
        long remaining = record.blockedUntil().toEpochMilli() - Instant.now().toEpochMilli();
        return Math.max(0L, remaining / 1000L);
    }

    /** Số lần thất bại còn lại trước khi bị block */
    public int getRemainingAttempts(String ip) {
        AttemptRecord record = attempts.get(ip);
        if (record == null) return MAX_ATTEMPTS;
        return Math.max(0, MAX_ATTEMPTS - record.count());
    }
}