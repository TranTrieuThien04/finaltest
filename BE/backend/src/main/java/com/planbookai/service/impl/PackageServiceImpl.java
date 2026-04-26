package com.planbookai.service.impl;

import com.planbookai.dto.packageorder.PackageRequest;
import com.planbookai.dto.packageorder.PackageResponse;
import com.planbookai.entity.SubscriptionPackage;
import com.planbookai.repository.SubscriptionPackageRepository;
import com.planbookai.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {

    private final SubscriptionPackageRepository packageRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PackageResponse> findAll() {
        return packageRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public PackageResponse create(PackageRequest request) {
        SubscriptionPackage entity = new SubscriptionPackage();
        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity.setDurationDays(request.getDurationDays());
        entity.setDescription(request.getDescription());
        entity.setCreatedAt(LocalDateTime.now());
        return toResponse(packageRepository.save(entity));
    }

    @Override
    @Transactional
    public PackageResponse update(@NonNull Long packageId, PackageRequest request) {
        SubscriptionPackage entity = getPackage(packageId);
        entity.setName(request.getName());
        entity.setPrice(request.getPrice());
        entity.setDurationDays(request.getDurationDays());
        entity.setDescription(request.getDescription());
        return toResponse(packageRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(@NonNull Long packageId) {
        packageRepository.delete(getPackage(packageId));
    }

    private @NonNull SubscriptionPackage getPackage(@NonNull Long packageId) {
        return Objects.requireNonNull(
                packageRepository.findById(packageId)
                        .orElseThrow(() -> new IllegalArgumentException("Package not found: " + packageId))
        );
    }

    private PackageResponse toResponse(SubscriptionPackage entity) {
        return PackageResponse.builder()
                .packageId(entity.getPackageId())
                .name(entity.getName())
                .price(entity.getPrice())
                .durationDays(entity.getDurationDays())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
