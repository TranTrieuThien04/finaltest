package com.planbookai.dto.auth;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MeResponse {
    private Long id;
    private String email;
    private String fullName;
    private List<String> roles;
}
