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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    // ... (Các hàm createCase, getCase cũ giữ nguyên) ...

    @PostMapping
    public ResponseEntity<ApiResponse<CaseResponse>> createCase(
            @RequestBody CreateCaseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // Giả sử logic là Khách hàng tạo yêu cầu gửi đến Luật sư
        Long clientId = userDetails.getUser().getUserId();
        CaseResponse caseResponse = caseService.createCase(clientId, request);

        ApiResponse<CaseResponse> response = ApiResponse.<CaseResponse>builder()
                .success(true)
                .message("Tạo vụ án thành công")
                .data(caseResponse)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CaseResponse>> getCase(@PathVariable Long id, HttpServletRequest request) {
        // ... code cũ ...
        return ResponseEntity.ok(null); // (Giữ code cũ của bạn)
    }

    // --- API MỚI: THÊM CẬP NHẬT ---
    @PostMapping("/{id}/updates")
    public ResponseEntity<ApiResponse<CaseUpdateResponse>> addUpdate(
            @PathVariable Long id,
            @RequestBody UpdateProgressRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        CaseUpdateResponse updateResponse = caseService.addCaseUpdate(id, userId, request);

        ApiResponse<CaseUpdateResponse> response = ApiResponse.<CaseUpdateResponse>builder()
                .success(true)
                .message("Cập nhật tiến độ thành công")
                .data(updateResponse)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // --- API MỚI: UPLOAD TÀI LIỆU ---
    @PostMapping(value = "/{id}/documents", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<String>> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        String url = caseService.uploadCaseDocument(id, userId, file);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Upload tài liệu thành công")
                .data(url)
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }
}