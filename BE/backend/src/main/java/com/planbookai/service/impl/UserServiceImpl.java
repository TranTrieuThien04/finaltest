package com.planbookai.service.impl;

import com.planbookai.dto.user.UserCreateRequest;
import com.planbookai.dto.user.UserResponse;
import com.planbookai.dto.user.UserUpdateRequest;
import com.planbookai.entity.Role;
import com.planbookai.entity.User;
import com.planbookai.repository.RoleRepository;
import com.planbookai.repository.UserRepository;
import com.planbookai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findById(@NonNull Long userId) {
        return toResponse(getUser(userId));
    }

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already exists");
        });

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setStatus(request.getStatus());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRoles(resolveRoles(request.getRoles()));

        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse update(@NonNull Long userId, UserUpdateRequest request) {
        User user = getUser(userId);
        user.setFullName(request.getFullName());
        user.setStatus(request.getStatus());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRoles(resolveRoles(request.getRoles()));
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(@NonNull Long userId) {
        User user = getUser(userId);
        userRepository.delete(user);
    }

    private @NonNull User getUser(@NonNull Long userId) {
        return Objects.requireNonNull(
                userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId))
        );
    }

    private Set<Role> resolveRoles(Set<String> roleNames) {
        return roleNames.stream()
                .map(name -> roleRepository.findByName(name.toUpperCase())
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + name)))
                .collect(java.util.stream.Collectors.toSet());
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .status(user.getStatus())
                .roles(user.getRoles().stream().map(Role::getName).sorted().toList())
                .build();
    }
}
