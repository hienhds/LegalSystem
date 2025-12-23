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

    // 1. Lấy danh sách cho Luật sư
    // FIX: Dùng @Query tường minh thay vì để Spring tự đoán (tránh lỗi biên dịch thiếu tham số)
    @Query("SELECT c FROM Case c WHERE c.lawyer.userId = :lawyerId")
    Page<Case> findByLawyer_UserId(@Param("lawyerId") Long lawyerId, Pageable pageable);

    // 2. Lấy danh sách cho Khách hàng
    // FIX: Dùng @Query tường minh
    @Query("SELECT c FROM Case c WHERE c.client.userId = :clientId")
    Page<Case> findByClient_UserId(@Param("clientId") Long clientId, Pageable pageable);

    // 3. Tìm kiếm cho LUẬT SƯ
    @Query("SELECT c FROM Case c " +
           "LEFT JOIN c.client cl " +
           "WHERE c.lawyer.userId = :lawyerId " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(cl.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Case> searchCasesForLawyer(@Param("lawyerId") Long lawyerId, 
                                    @Param("keyword") String keyword, 
                                    Pageable pageable);

    // 4. Tìm kiếm cho KHÁCH HÀNG
    @Query("SELECT c FROM Case c " +
           "LEFT JOIN c.lawyer l " +
           "WHERE c.client.userId = :clientId " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Case> searchCasesForCitizen(@Param("clientId") Long clientId, 
                                     @Param("keyword") String keyword, 
                                     Pageable pageable);
}