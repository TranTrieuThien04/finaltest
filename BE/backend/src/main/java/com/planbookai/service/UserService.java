package com.planbookai.service;

import com.planbookai.dto.user.UserCreateRequest;
import com.planbookai.dto.user.UserResponse;
import com.planbookai.dto.user.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import java.util.List;

public interface UserService {

    /** Giữ để tương thích ngược */
    List<UserResponse> findAll();

    /** Phân trang — dùng cho admin list */
    Page<UserResponse> findAllPaged(Pageable pageable);

    UserResponse findById(@NonNull Long userId);

    UserResponse create(UserCreateRequest request);

    UserResponse update(@NonNull Long userId, UserUpdateRequest request);

    void delete(@NonNull Long userId);
}
