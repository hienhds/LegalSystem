package com.example.backend.case_management.repository;

import com.example.backend.case_management.entity.Case;
import com.example.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {
    
    Page<Case> findByClient(User client, Pageable pageable);
    Page<Case> findByLawyer(User lawyer, Pageable pageable);

    // --- THÊM ĐOẠN NÀY ---
    // Tìm tất cả vụ án mà tôi tham gia (dù là Khách hàng hay Luật sư)
    @Query("SELECT c FROM Case c WHERE c.client.userId = :userId OR c.lawyer.userId = :userId ORDER BY c.createdAt DESC")
    Page<Case> findAllCasesByUserId(@Param("userId") Long userId, Pageable pageable);
}