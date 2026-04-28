package com.planbookai.controller;

import com.planbookai.dto.auth.LoginRequest;
import com.planbookai.dto.auth.LoginResponse;
import com.planbookai.dto.auth.MeResponse;
import com.planbookai.entity.RefreshToken;
import com.planbookai.security.AuthenticatedUser;
import com.planbookai.security.CustomUserDetailsService;
import com.planbookai.security.JwtService;
import com.planbookai.security.LoginRateLimiter;
import com.planbookai.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final LoginRateLimiter loginRateLimiter;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);

        if (loginRateLimiter.isBlocked(clientIp)) {
            long remaining = loginRateLimiter.getBlockRemainingSeconds(clientIp);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of(
                            "message", "Quá nhiều lần đăng nhập thất bại. Vui lòng thử lại sau " + remaining + " giây.",
                            "retryAfterSeconds", remaining
                    ));
        }

        try {
            log.info("Login attempt for email={}, ip={}", request.getEmail(), clientIp);

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

        } catch (BadCredentialsException ex) {
            loginRateLimiter.recordFailure(clientIp);
            int left = loginRateLimiter.getRemainingAttempts(clientIp);

            String msg = left > 0
                    ? "Sai email hoặc mật khẩu. Còn " + left + " lần thử."
                    : "Tài khoản bị tạm khóa 15 phút do quá nhiều lần đăng nhập sai.";

            log.warn("Login failed for email={}, ip={}, attemptsLeft={}", request.getEmail(), clientIp, left);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", msg));

        } catch (AuthenticationException ex) {
            log.error("Auth error for {}: {}", request.getEmail(), ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi server khi xác thực"));
        }

        loginRateLimiter.recordSuccess(clientIp);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
        String accessToken = jwtService.generateToken(userDetails);

        AuthenticatedUser authUser = (AuthenticatedUser) userDetails;
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(authUser.getId());

        log.info("Login SUCCESS for email={}, ip={}", request.getEmail(), clientIp);
        return ResponseEntity.ok(LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(86400000L)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshTokenStr = body.get("refreshToken");
        if (refreshTokenStr == null || refreshTokenStr.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "refreshToken là bắt buộc"));
        }

        try {
            RefreshToken refreshToken = refreshTokenService.verifyAndGet(refreshTokenStr);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(
                    refreshToken.getUser().getEmail());
            String newAccessToken = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(Map.of(
                    "token", newAccessToken,
                    "expiresIn", 86400000L
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof AuthenticatedUser user) {
            refreshTokenService.revokeByUserId(user.getId());
        }
        return ResponseEntity.ok(Map.of("message", "Đã đăng xuất thành công"));
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isBlank()) {
            return xfHeader.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }
        return request.getRemoteAddr();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof AuthenticatedUser user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        List<String> roles = user.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .map(authority -> authority.replaceFirst("^ROLE_", ""))
                .toList();

        return ResponseEntity.ok(
                MeResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .roles(roles)
                        .build()
        );
    }
}