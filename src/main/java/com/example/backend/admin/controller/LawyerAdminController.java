package com.example.backend.admin.controller;

import com.example.backend.common.dto.ApiResponse;
import com.example.backend.common.security.CustomUserDetails;
import com.example.backend.lawyer.dto.request.FilterLawyerRequest;
import com.example.backend.lawyer.dto.response.LawyerDetailResponse;
import com.example.backend.lawyer.dto.response.LawyerListResponse;
import com.example.backend.lawyer.entity.VerificationStatus;
import com.example.backend.lawyer.service.LawyerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/lawyers")
@RequiredArgsConstructor
public class LawyerAdminController {

    private final LawyerService lawyerService;

    // 1. Get list
    @GetMapping
    public ResponseEntity<ApiResponse<Page<LawyerListResponse>>> getAllLawyers(
            FilterLawyerRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest servletRequest
    ) {

        Page<LawyerListResponse> lawyerList = lawyerService.getAllLawyers(filter, page, size);

        ApiResponse<Page<LawyerListResponse>> response =
                ApiResponse.<Page<LawyerListResponse>>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("Lawyer list retrieved successfully")
                        .data(lawyerList)
                        .path(servletRequest.getRequestURI())
                        .timestamp(Instant.now())
                        .traceId(UUID.randomUUID().toString())
                        .build();

        return ResponseEntity.ok(response);
    }


    // 2. Get detail
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LawyerDetailResponse>> getDetail(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        LawyerDetailResponse detail = lawyerService.getDetail(id);

        ApiResponse<LawyerDetailResponse> response =
                ApiResponse.<LawyerDetailResponse>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("Lawyer detail retrieved successfully")
                        .data(detail)
                        .path(request.getRequestURI())
                        .timestamp(Instant.now())
                        .traceId(UUID.randomUUID().toString())
                        .build();

        return ResponseEntity.ok(response);
    }


    // 3. Update status
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<String>> updateStatus(
            @PathVariable Long id,
            @RequestParam VerificationStatus status,
            HttpServletRequest request
    ) {
        String msg = lawyerService.updateStatus(id, status);

        ApiResponse<String> response =
                ApiResponse.<String>builder()
                        .success(true)
                        .status(HttpStatus.OK.value())
                        .message("Status updated successfully")
                        .data(msg)
                        .path(request.getRequestURI())
                        .timestamp(Instant.now())
                        .traceId(UUID.randomUUID().toString())
                        .build();

        return ResponseEntity.ok(response);
    }
}
