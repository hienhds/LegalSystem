package com.example.backend.case_management.repository;

import com.example.backend.case_management.entity.Case;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {

    // 1. Tìm kiếm dành cho LUẬT SƯ 
    // (Tìm theo: Tên vụ án, Tên khách, Email khách, SĐT khách)
    @Query("SELECT c FROM Case c WHERE c.lawyer.userId = :lawyerId " +
           "AND (" +
           "   LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(c.client.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(c.client.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR c.client.phoneNumber LIKE CONCAT('%', :keyword, '%') " +
           ")")
    Page<Case> searchCasesForLawyer(@Param("lawyerId") Long lawyerId, @Param("keyword") String keyword, Pageable pageable);

    // 2. Tìm kiếm dành cho NGƯỜI DÂN (Đã cập nhật theo yêu cầu của bạn)
    // (Tìm theo: Tên vụ án, Tên luật sư, Email luật sư, SĐT luật sư)
    @Query("SELECT c FROM Case c WHERE c.client.userId = :clientId " +
           "AND (" +
           "   LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(c.lawyer.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(c.lawyer.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR c.lawyer.phoneNumber LIKE CONCAT('%', :keyword, '%') " +
           ")")
    Page<Case> searchCasesForCitizen(@Param("clientId") Long clientId, @Param("keyword") String keyword, Pageable pageable);

    // 3. Fallback: Lấy tất cả nếu không có từ khóa
    Page<Case> findByLawyer_UserId(Long userId, Pageable pageable);
    Page<Case> findByClient_UserId(Long userId, Pageable pageable);
}