package com.planbookai.service;

import com.planbookai.dto.user.UserCreateRequest;
import com.planbookai.dto.user.UserResponse;
import com.planbookai.dto.user.UserUpdateRequest;
import org.springframework.lang.NonNull;

import java.util.List;

public interface UserService {
    List<UserResponse> findAll();

    UserResponse findById(@NonNull Long userId);

    UserResponse create(UserCreateRequest request);

    UserResponse update(@NonNull Long userId, UserUpdateRequest request);

    void delete(@NonNull Long userId);
}
