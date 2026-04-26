package com.planbookai.service;

import com.planbookai.dto.packageorder.PackageRequest;
import com.planbookai.dto.packageorder.PackageResponse;
import org.springframework.lang.NonNull;

import java.util.List;

public interface PackageService {
    List<PackageResponse> findAll();

    PackageResponse create(PackageRequest request);

    PackageResponse update(@NonNull Long packageId, PackageRequest request);

    void delete(@NonNull Long packageId);
}
