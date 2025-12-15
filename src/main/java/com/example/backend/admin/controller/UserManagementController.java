package com.example.backend.admin.controller;

import com.example.backend.admin.dto.UserManagementResponse;
import com.example.backend.admin.service.UserManagementService;
import com.example.backend.common.dto.ApiResponse;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
// ❌ KHÔNG DÙNG @PreAuthorize ở đây nữa
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserManagementResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean status,
            HttpServletRequest request) {

        // 1. Lấy thông tin user hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        // Lấy danh sách quyền dưới dạng String để dễ kiểm tra
        List<String> roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();

        // 2. In Log ra Console để debug (Quan trọng)
        log.info("==================================================");
        log.info("API GET /api/admin/users ĐANG ĐƯỢC GỌI");
        log.info("User đang đăng nhập: {}", currentUsername);
        log.info("Quyền (Roles) hiện có: {}", roles);
        log.info("==================================================");

        // 3. Kiểm tra quyền THỦ CÔNG (Bỏ qua @PreAuthorize)
        // Nếu không phải ADMIN và không phải LAWYER thì chặn
        boolean isAdmin = roles.contains("ADMIN") || roles.contains("ROLE_ADMIN");
        boolean isLawyer = roles.contains("LAWYER") || roles.contains("ROLE_LAWYER");

        if (!isAdmin && !isLawyer) {
            log.error(">>> TỪ CHỐI TRUY CẬP: User {} không có quyền ADMIN hoặc LAWYER", currentUsername);
            throw new AppException(ErrorType.FORBIDDEN, "Bạn không có quyền truy cập danh sách người dùng.");
        }

        // 4. Nếu có quyền thì chạy logic bình thường
        Page<UserManagementResponse> users = userManagementService.getAllUsers(page, size, search, status);

        ApiResponse<Page<UserManagementResponse>> response = ApiResponse.<Page<UserManagementResponse>>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách người dùng thành công")
                .data(users)
                .timestamp(Instant.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }

    // Các API khác giữ nguyên logic cũ hoặc bỏ @PreAuthorize nếu cần
    @PutMapping("/{id}/lock")
    public ResponseEntity<ApiResponse<Void>> lockUser(@PathVariable Long id, HttpServletRequest request) {
        checkAdminRole(); // Gọi hàm check quyền riêng
        userManagementService.lockUser(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Khóa thành công").build());
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<ApiResponse<Void>> unlockUser(@PathVariable Long id, HttpServletRequest request) {
        checkAdminRole();
        userManagementService.unlockUser(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Mở khóa thành công").build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        checkAdminRole();
        userManagementService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder().success(true).message("Xóa thành công").build());
    }

    // Hàm phụ trợ check quyền Admin
    private void checkAdminRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            throw new AppException(ErrorType.FORBIDDEN, "Chỉ Admin mới được thực hiện thao tác này");
        }
    }
}