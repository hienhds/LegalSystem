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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @PostMapping
    @PreAuthorize("hasAuthority('LAWYER')")
    public ResponseEntity<ApiResponse<CaseResponse>> createCase(
            @RequestBody CreateCaseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest servletRequest
    ) {
        Long lawyerId = userDetails.getUser().getUserId();
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CaseResponse>> getCase(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
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
    
    @DeleteMapping("/{id}/documents/{docId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable Long id,
            @PathVariable Long docId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest servletRequest
    ) {
        Long userId = userDetails.getUser().getUserId();
        caseService.deleteCaseDocument(id, docId, userId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa tài liệu thành công")
                .path(servletRequest.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }

    // API Xóa vụ án (MỚI THÊM)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('LAWYER')")
    public ResponseEntity<ApiResponse<Void>> deleteCase(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest servletRequest
    ) {
        Long userId = userDetails.getUser().getUserId();
        caseService.deleteCase(id, userId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Xóa vụ án thành công")
                .path(servletRequest.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CaseResponse>>> getMyCases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request
    ) {
        Long userId = userDetails.getUser().getUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CaseResponse> cases = caseService.getMyCases(userId, keyword, pageable);

        ApiResponse<Page<CaseResponse>> response = ApiResponse.<Page<CaseResponse>>builder()
                .success(true)
                .message("Lấy danh sách vụ án thành công")
                .data(cases)
                .path(request.getRequestURI())
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/documents/{docId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long id,
            @PathVariable Long docId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        Resource resource = caseService.downloadCaseDocument(id, docId, userId);
        String filename = resource.getFilename();
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }

    @GetMapping("/{id}/documents/{docId}/view")
    public ResponseEntity<Resource> viewDocument(
            @PathVariable Long id,
            @PathVariable Long docId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        Resource resource = caseService.downloadCaseDocument(id, docId, userId);

        String filename = resource.getFilename();
        String contentType = getContentType(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(resource);
    }

    private String getContentType(String filename) {
        if (filename == null) return "application/octet-stream";
        String name = filename.toLowerCase();
        
        if (name.endsWith(".pdf")) return "application/pdf";
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".txt")) return "text/plain";
        
        return "application/octet-stream";
    }
}