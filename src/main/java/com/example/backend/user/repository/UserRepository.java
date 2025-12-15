package com.example.backend.user.repository;

import com.example.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsUserByPhoneNumber(String phoneNumber);
    
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    Page<User> findByEmailContainingOrFullNameContaining(String email, String fullName, Pageable pageable);
    
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);

    // 🔥 API MỚI: Chỉ tìm những user có role là USER (Khách hàng)
    // Loại bỏ ADMIN và LAWYER khỏi kết quả tìm kiếm ngay từ Database
    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r " +
           "WHERE r.roleName = 'USER' " +
           "AND (LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR u.phoneNumber LIKE CONCAT('%', :keyword, '%'))")
    Page<User> searchClients(@Param("keyword") String keyword, Pageable pageable);
}