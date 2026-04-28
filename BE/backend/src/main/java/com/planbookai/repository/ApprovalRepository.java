package com.planbookai.repository;

import com.planbookai.entity.Approval;
import com.planbookai.entity.enums.ApprovalContentType;
import com.planbookai.entity.enums.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalRepository extends JpaRepository<Approval, Long> {

    Page<Approval> findByContentType(ApprovalContentType contentType, Pageable pageable);

    Page<Approval> findByStatus(ApprovalStatus status, Pageable pageable);

    Page<Approval> findByContentTypeAndStatus(ApprovalContentType contentType,
                                               ApprovalStatus status,
                                               Pageable pageable);
}
