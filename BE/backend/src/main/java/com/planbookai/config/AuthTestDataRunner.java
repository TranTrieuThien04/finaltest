package com.planbookai.config;

import com.planbookai.entity.Role;
import com.planbookai.entity.User;
import com.planbookai.repository.RoleRepository;
import com.planbookai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AuthTestDataRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEMO_PASSWORD = "Password123!";

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // 1. Seed roles
        for (String roleName : List.of("ADMIN", "MANAGER", "STAFF", "TEACHER")) {
            roleRepository.findByName(roleName).orElseGet(() -> {
                Role r = new Role();
                r.setName(roleName);
                return roleRepository.save(r);
            });
        }

        // 2. Seed demo users (idempotent - chỉ tạo nếu chưa có)
        List<Map<String, String>> demoUsers = List.of(
            Map.of("email", "teacher@planbookai.com", "name", "Demo Teacher", "role", "TEACHER"),
            Map.of("email", "admin@planbookai.com",   "name", "Demo Admin",   "role", "ADMIN"),
            Map.of("email", "manager@planbookai.com", "name", "Demo Manager", "role", "MANAGER"),
            Map.of("email", "staff@planbookai.com",   "name", "Demo Staff",   "role", "STAFF")
        );

        for (Map<String, String> demo : demoUsers) {
            if (userRepository.findByEmail(demo.get("email")).isEmpty()) {
                Role role = roleRepository.findByName(demo.get("role")).orElseThrow();
                User user = new User();
                user.setEmail(demo.get("email"));
                user.setFullName(demo.get("name"));
                user.setPasswordHash(passwordEncoder.encode(DEMO_PASSWORD));
                user.setStatus("active");
                user.setCreatedAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                user.getRoles().add(role);
                userRepository.save(user);
            }
        }
    }
}