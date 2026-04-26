package com.planbookai.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank
    @Size(max = 255)
    private String fullName;

    @NotBlank
    private String status;

    @NotEmpty
    private Set<String> roles;
}
