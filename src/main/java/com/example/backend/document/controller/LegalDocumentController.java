package com.example.backend.document.controller;

import com.example.backend.document.dto.DocumentCategoryResponse;
import com.example.backend.document.dto.DocumentSearchRequest;
import com.example.backend.document.dto.LegalDocumentResponse;
import com.example.backend.document.entity.LegalDocument;
import com.example.backend.document.service.LegalDocumentService;
import com.example.backend.common.dto.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class LegalDocumentController {

    private final LegalDocumentService legalDocumentService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<LegalDocumentResponse>>> searchDocuments(
            @Valid @ModelAttribute DocumentSearchRequest request) {
        
        log.info("Search request: keyword='{}', category='{}', page={}, size={}", 
                 request.getKeyword(), request.getCategory(), request.getPage(), request.getSize());
        
        Page<LegalDocument> documents = legalDocumentService.advancedSearch(
                request.getCleanKeyword(),
                request.getCleanCategory(),
                request.getPage(),
                request.getSize()
        );
        
        Page<LegalDocumentResponse> responseDocuments = documents.map(LegalDocumentResponse::fromEntity);
        
        ApiResponse<Page<LegalDocumentResponse>> apiResponse = ApiResponse.<Page<LegalDocumentResponse>>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Tìm kiếm văn bản thành công")
                .data(responseDocuments)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LegalDocumentResponse>> getDocumentById(@PathVariable Long id) {
        
        log.info("Getting document with ID: {}", id);
        
        LegalDocument document = legalDocumentService.getDocumentById(id);
        
        if (document == null) {
            ApiResponse<LegalDocumentResponse> apiResponse = ApiResponse.<LegalDocumentResponse>builder()
                    .success(false)
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Không tìm thấy văn bản pháp luật với ID: " + id)
                    .data(null)
                    .timestamp(Instant.now())
                    .build();
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
        
        LegalDocumentResponse response = LegalDocumentResponse.fromEntity(document);
        
        ApiResponse<LegalDocumentResponse> apiResponse = ApiResponse.<LegalDocumentResponse>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Lấy thông tin văn bản thành công")
                .data(response)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<Page<LegalDocumentResponse>>> getDocumentsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Getting documents by category: {}, page: {}, size: {}", category, page, size);
        
        Page<LegalDocument> documents = legalDocumentService.getDocumentsByCategory(category, page, size);
        Page<LegalDocumentResponse> responseDocuments = documents.map(LegalDocumentResponse::fromEntity);
        
        ApiResponse<Page<LegalDocumentResponse>> apiResponse = ApiResponse.<Page<LegalDocumentResponse>>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message(String.format("Lấy danh sách văn bản thuộc danh mục '%s' thành công", category))
                .data(responseDocuments)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<Page<LegalDocumentResponse>>> getTrendingDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Getting trending documents, page: {}, size: {}", page, size);
        
        Page<LegalDocument> documents = legalDocumentService.getTrendingDocuments(page, size);
        Page<LegalDocumentResponse> responseDocuments = documents.map(LegalDocumentResponse::fromEntity);
        
        ApiResponse<Page<LegalDocumentResponse>> apiResponse = ApiResponse.<Page<LegalDocumentResponse>>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách văn bản phổ biến thành công")
                .data(responseDocuments)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<DocumentCategoryResponse.DocumentCategoriesResponse>> getAllCategories() {
        
        log.info("Getting all available categories");
        
        List<String> categories = legalDocumentService.getAllCategories();
        DocumentCategoryResponse.DocumentCategoriesResponse response = 
            DocumentCategoryResponse.DocumentCategoriesResponse.fromCategories(categories);
        
        ApiResponse<DocumentCategoryResponse.DocumentCategoriesResponse> apiResponse = 
            ApiResponse.<DocumentCategoryResponse.DocumentCategoriesResponse>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách danh mục thành công")
                .data(response)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/categories/stats")
    public ResponseEntity<ApiResponse<DocumentCategoryResponse.DocumentCategoriesResponse>> getCategoryStats() {
        
        log.info("Getting category statistics");
        
        List<Object[]> rawStats = legalDocumentService.getDocumentCountsByCategory();
        List<DocumentCategoryResponse> categoryStats = rawStats.stream()
                .map(stats -> DocumentCategoryResponse.fromCategoryStats(
                        (String) stats[0], 
                        ((Number) stats[1]).longValue()))
                .collect(Collectors.toList());
        
        DocumentCategoryResponse.DocumentCategoriesResponse response = 
            DocumentCategoryResponse.DocumentCategoriesResponse.fromCategoryStats(categoryStats);
        
        ApiResponse<DocumentCategoryResponse.DocumentCategoriesResponse> apiResponse = 
            ApiResponse.<DocumentCategoryResponse.DocumentCategoriesResponse>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Lấy thống kê danh mục thành công")
                .data(response)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<LegalDocumentResponse>>> getAllDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        log.info("Getting all documents, page: {}, size: {}, sortBy: {}, direction: {}", 
                 page, size, sortBy, sortDirection);
        
        Page<LegalDocument> documents = legalDocumentService.getAllDocuments(page, size, sortBy, sortDirection);
        Page<LegalDocumentResponse> responseDocuments = documents.map(LegalDocumentResponse::fromEntity);
        
        ApiResponse<Page<LegalDocumentResponse>> apiResponse = ApiResponse.<Page<LegalDocumentResponse>>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Lấy danh sách văn bản thành công")
                .data(responseDocuments)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/general-search")
    public ResponseEntity<ApiResponse<Page<LegalDocumentResponse>>> generalSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("General search with keyword: '{}', page: {}, size: {}", keyword, page, size);
        
        Page<LegalDocument> documents = legalDocumentService.generalSearch(keyword, page, size);
        Page<LegalDocumentResponse> responseDocuments = documents.map(LegalDocumentResponse::fromEntity);
        
        String message = keyword != null && !keyword.trim().isEmpty() 
                ? String.format("Tìm kiếm với từ khóa '%s' thành công", keyword)
                : "Lấy tất cả văn bản thành công";
        
        ApiResponse<Page<LegalDocumentResponse>> apiResponse = ApiResponse.<Page<LegalDocumentResponse>>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message(message)
                .data(responseDocuments)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }
}