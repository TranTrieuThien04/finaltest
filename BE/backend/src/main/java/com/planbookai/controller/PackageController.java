package com.planbookai.controller;

import com.planbookai.dto.packageorder.PackageRequest;
import com.planbookai.dto.packageorder.PackageResponse;
import com.planbookai.service.PackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<PackageResponse> list() {
        return packageService.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<PackageResponse> create(@Valid @RequestBody PackageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(packageService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public PackageResponse update(@PathVariable @NonNull Long id, @Valid @RequestBody PackageRequest request) {
        return packageService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Long id) {
        packageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
