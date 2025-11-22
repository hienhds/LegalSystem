package com.example.backend.search.controller;

import com.example.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class LawyerSearchController {

    @PostMapping("/lawyers")
    public ResponseEntity<ApiResponse<Object>> searchLawyers(@RequestBody Map<String, Object> request) {
        log.info("Searching lawyers with criteria: {}", request);
        
        Map<String, Object> mockLawyer = new HashMap<>();
        mockLawyer.put("lawyerId", 1L);
        mockLawyer.put("fullName", "Luật sư Nguyễn Văn A");
        mockLawyer.put("email", "lawyer@example.com");
        mockLawyer.put("phoneNumber", "0123456789");
        mockLawyer.put("licenseNumber", "LS001");
        mockLawyer.put("yearsOfExperience", 5);
        mockLawyer.put("description", "Chuyên về luật dân sự");
        mockLawyer.put("averageRating", 4.5);
        mockLawyer.put("totalReviews", 100);
        mockLawyer.put("consultationFee", 500000.0);
        mockLawyer.put("isVerified", true);
        mockLawyer.put("city", "Hà Nội");
        mockLawyer.put("district", "Ba Đình");
        mockLawyer.put("isAvailableToday", true);
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", List.of(mockLawyer));
        response.put("totalElements", 1L);
        response.put("totalPages", 1);
        response.put("size", 10);
        response.put("number", 0);
        
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Tìm thấy 1 luật sư phù hợp")
                .data(response)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/lawyers")
    public ResponseEntity<ApiResponse<Object>> searchLawyersSimple(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> specializationIds,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("Simple search: keyword={}, city={}, minRating={}", keyword, city, minRating);
        
        Map<String, Object> mockLawyer = new HashMap<>();
        mockLawyer.put("lawyerId", 1L);
        mockLawyer.put("fullName", "Luật sư Trần Thị B");
        mockLawyer.put("email", "lawyer2@example.com");
        mockLawyer.put("city", Objects.requireNonNullElse(city, "Hà Nội"));
        mockLawyer.put("averageRating", Objects.requireNonNullElse(minRating, 4.8));
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", List.of(mockLawyer));
        response.put("totalElements", 1L);
        
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Tìm thấy 1 luật sư phù hợp")
                .data(response)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Object>> getSearchHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> mockHistory = new HashMap<>();
        mockHistory.put("searchId", 1L);
        mockHistory.put("searchQuery", "luật dân sự");
        mockHistory.put("searchType", "LAWYER_SEARCH");
        mockHistory.put("resultsCount", 15);
        mockHistory.put("searchTimestamp", Instant.now().toString());
        
        Map<String, Object> response = new HashMap<>();
        response.put("content", List.of(mockHistory));
        response.put("totalElements", 1L);
        
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Lấy lịch sử tìm kiếm thành công")
                .data(response)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<Object>> getTrendingSearches(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<String> trending = List.of(
            "luật dân sự",
            "luật hình sự", 
            "hợp đồng lao động",
            "tranh chấp đất đai",
            "thủ tục ly hôn"
        );
        
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Lấy tìm kiếm xu hướng thành công")
                .data(trending.subList(0, Math.min(limit, trending.size())))
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/popular-lawyers")
    public ResponseEntity<ApiResponse<Object>> getPopularLawyers(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Long> popularLawyers = List.of(1L, 2L, 3L, 4L, 5L);
        
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Lấy luật sư phổ biến thành công")
                .data(popularLawyers.subList(0, Math.min(limit, popularLawyers.size())))
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/track-click")
    public ResponseEntity<ApiResponse<Object>> trackLawyerClick(@RequestParam Long lawyerId) {
        log.info("Tracking click for lawyer: {}", lawyerId);
        
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Tracking successful")
                .data(null)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<Object>> getSearchSuggestions(@RequestParam String query) {
        
        List<String> suggestions = List.of(
            query + " hà nội",
            query + " tphcm", 
            query + " chuyên nghiệp",
            query + " kinh nghiệm",
            query + " uy tín"
        );
        
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Lấy gợi ý tìm kiếm thành công")
                .data(suggestions)
                .timestamp(Instant.now())
                .build();
        
        return ResponseEntity.ok(apiResponse);
    }
}