package com.planbookai.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String token;           // access token (24h)
    private String refreshToken;    // FIX: thêm refresh token (30 ngày)
    private long   expiresIn;       // FIX: thêm để FE biết khi nào hết hạn (ms)
}