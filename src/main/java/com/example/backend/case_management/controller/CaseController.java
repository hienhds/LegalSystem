package com.example.backend.case_management.controller;

import com.example.backend.case_management.dto.CaseResponse;
import com.example.backend.case_management.dto.CaseUpdateResponse;
import com.example.backend.case_management.dto.CreateCaseRequest;
import com.example.backend.case_management.dto.UpdateProgressRequest;
import com.example.backend.case_management.service.CaseService;
import com.example.backend.common.dto.ApiResponse;
import com.example.backend.common.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page; // Nhớ import Page
import org.springframework.data.domain.PageRequest; // Nhớ import PageRequest
import org.springframework.data.domain.Pageable; // Nhớ import Pageable
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    // 1. TẠO VỤ ÁN (Đã sửa OK)
    @PostMapping
    @PreAuthorize("hasAuthority('LAWYER')") // <--- THÊM: Chỉ luật sư mới được gọi
    public ResponseEntity<ApiResponse<CaseResponse>> createCase(
            @RequestBody CreateCaseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest servletRequest
    ) {
        // Người đang đăng nhập là Luật sư
        Long lawyerId = userDetails.getUser().getUserId();
        
        // Gọi service với lawyerId là người tạo, request chứa clientId
        CaseResponse caseResponse = caseService.createCase(lawyerId, request);

        ApiResponse<CaseResponse> response = ApiResponse.<CaseResponse>builder()
                .success(true)
                .message("Tạo vụ án thành công")
                .data(caseResponse)
                .path(servletRequest.getRequestURI())
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. LẤY CHI TIẾT VỤ ÁN (Cần sửa chỗ này thì mới test được)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CaseResponse>> getCase(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        // GỌI SERVICE LẤY CHI TIẾT
        CaseResponse caseDetail = caseService.getCaseDetail(id);

        ApiResponse<CaseResponse> response = ApiResponse.<CaseResponse>builder()
                .success(true)
                .message("Lấy thông tin vụ án thành công")
                .data(caseDetail)
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // 3. CẬP NHẬT TIẾN ĐỘ (Đã OK)
    @PostMapping("/{id}/updates")
    public ResponseEntity<ApiResponse<CaseUpdateResponse>> addUpdate(
            @PathVariable Long id,
            @RequestBody UpdateProgressRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest servletRequest
    ) {
        Long userId = userDetails.getUser().getUserId();
        CaseUpdateResponse updateResponse = caseService.addCaseUpdate(id, userId, request);

        ApiResponse<CaseUpdateResponse> response = ApiResponse.<CaseUpdateResponse>builder()
                .success(true)
                .message("Cập nhật tiến độ thành công")
                .data(updateResponse)
                .path(servletRequest.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // 4. UPLOAD TÀI LIỆU (Đã OK)
    @PostMapping(value = "/{id}/documents", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<String>> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest servletRequest
    ) {
        Long userId = userDetails.getUser().getUserId();
        String url = caseService.uploadCaseDocument(id, userId, file);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Upload tài liệu thành công")
                .data(url)
                .path(servletRequest.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }
    // 5. LẤY DANH SÁCH VỤ ÁN CỦA TÔI (API này đang thiếu)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CaseResponse>>> getMyCases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request
    ) {
        Long userId = userDetails.getUser().getUserId();
        
        // ✅ THAY ĐỔI: Thêm Sort.by(...).descending() để luôn lấy mới nhất
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());
        
        Page<CaseResponse> cases = caseService.getMyCases(userId, pageable);

        ApiResponse<Page<CaseResponse>> response = ApiResponse.<Page<CaseResponse>>builder()
                .success(true)
                .message("Lấy danh sách vụ án thành công")
                .data(cases)
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }
}