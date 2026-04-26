package com.planbookai.security;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public AuthenticatedUser requireAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new IllegalStateException("Unauthorized");
        }
        return user;
    }

    @NonNull
    public Long requireUserId() {
        return requireAuthenticatedUser().getId();
    }

    public boolean hasRole(String role) {
        AuthenticatedUser user = requireAuthenticatedUser();
        String expected = "ROLE_" + role.toUpperCase();
        return user.getAuthorities().stream().anyMatch(auth -> expected.equals(auth.getAuthority()));
    }
}
