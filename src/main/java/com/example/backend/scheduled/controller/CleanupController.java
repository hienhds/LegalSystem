package com.example.backend.scheduled.controller;

import com.example.backend.scheduled.SearchHistoryCleanupJob;
import com.example.backend.search.dto.CleanupStatsResponse;
import com.example.backend.search.service.SearchHistoryService;
import com.example.backend.common.dto.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin controller để quản lý cleanup jobs
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/cleanup")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CleanupController {

    private final SearchHistoryCleanupJob cleanupJob;
    private final SearchHistoryService searchHistoryService;

    @Value("${search.history.cleanup.retention.days:30}")
    private int defaultRetentionDays;

    /**
     * Lấy thông tin cấu hình cleanup job
     */
    @GetMapping("/search-history/config")
    public ResponseEntity<BaseResponse<Object>> getCleanupConfig() {
        try {
            Object config = new Object() {
                public final boolean enabled = cleanupJob.isCleanupEnabled();
                public final int retentionDays = cleanupJob.getRetentionDays();
                public final int batchSize = cleanupJob.getBatchSize();
            };

            return ResponseEntity.ok(BaseResponse.success("Lấy cấu hình cleanup thành công", config));
        } catch (Exception e) {
            log.error("Error getting cleanup config: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.error("Lỗi khi lấy cấu hình cleanup"));
        }
    }

    /**
     * Lấy thống kê cleanup
     */
    @GetMapping("/search-history/stats")
    public ResponseEntity<BaseResponse<CleanupStatsResponse>> getCleanupStats(
            @RequestParam(defaultValue = "30") int retentionDays) {
        try {
            CleanupStatsResponse stats = searchHistoryService.getCleanupStatistics(retentionDays);
            return ResponseEntity.ok(BaseResponse.success("Lấy thống kê cleanup thành công", stats));
        } catch (Exception e) {
            log.error("Error getting cleanup stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.error("Lỗi khi lấy thống kê cleanup"));
        }
    }

    /**
     * Thực hiện cleanup manual
     */
    @PostMapping("/search-history/manual")
    public ResponseEntity<BaseResponse<Object>> manualCleanup(
            @RequestParam(required = false) Integer retentionDays) {
        try {
            int daysToUse = retentionDays != null ? retentionDays : defaultRetentionDays;
            
            if (daysToUse < 1) {
                return ResponseEntity.badRequest()
                        .body(BaseResponse.error("Số ngày retention phải >= 1"));
            }

            log.info("Admin trigger manual cleanup with retention {} days", daysToUse);
            int deletedCount = cleanupJob.manualCleanup(daysToUse);

            Object result = new Object() {
                public final int deletedRecords = deletedCount;
                public final int retentionDaysUsed = daysToUse;
                public final String message = String.format("Đã xóa %d records cũ hơn %d ngày", deletedCount, daysToUse);
            };

            return ResponseEntity.ok(BaseResponse.success("Manual cleanup hoàn thành", result));
        } catch (Exception e) {
            log.error("Error in manual cleanup: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.error("Lỗi khi thực hiện manual cleanup: " + e.getMessage()));
        }
    }

    /**
     * Test cleanup dry-run - chỉ kiểm tra số lượng sẽ xóa mà không thực sự xóa
     */
    @GetMapping("/search-history/dry-run")
    public ResponseEntity<BaseResponse<Object>> dryRunCleanup(
            @RequestParam(defaultValue = "30") int retentionDays) {
        try {
            if (retentionDays < 1) {
                return ResponseEntity.badRequest()
                        .body(BaseResponse.error("Số ngày retention phải >= 1"));
            }

            long oldRecords = searchHistoryService.getOldRecordsCount(retentionDays);
            long totalCount = searchHistoryService.getTotalSearchHistoryCount();

            Object result = new Object() {
                public final long totalRecords = totalCount;
                public final long recordsToDelete = oldRecords;
                public final long recordsToKeep = totalCount - oldRecords;
                public final int retentionDaysUsed = retentionDays;
                public final double deletePercentage = totalCount > 0 ? (double) oldRecords / totalCount * 100 : 0;
                public final String message = String.format("Sẽ xóa %d/%d records (%.1f%%) cũ hơn %d ngày", 
                        oldRecords, totalCount, deletePercentage, retentionDays);
            };

            return ResponseEntity.ok(BaseResponse.success("Dry-run cleanup thành công", result));
        } catch (Exception e) {
            log.error("Error in dry-run cleanup: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.error("Lỗi khi thực hiện dry-run cleanup: " + e.getMessage()));
        }
    }

    /**
     * Kiểm tra trạng thái cleanup job
     */
    @GetMapping("/search-history/health")
    public ResponseEntity<BaseResponse<Object>> checkCleanupHealth() {
        try {
            long totalRecords = searchHistoryService.getTotalSearchHistoryCount();
            long oldRecords = searchHistoryService.getOldRecordsCount(defaultRetentionDays);
            
            boolean needsCleanup = oldRecords > 1000; // Threshold for cleanup recommendation
            
            Object health = new Object() {
                public final boolean cleanupEnabled = cleanupJob.isCleanupEnabled();
                public final long totalRecordsCount = totalRecords;
                public final long oldRecordsCount = oldRecords;
                public final boolean recommendCleanup = needsCleanup;
                public final String status = needsCleanup ? "NEEDS_CLEANUP" : "HEALTHY";
                public final String message = needsCleanup ? 
                    String.format("Có %d records cũ, nên chạy cleanup", oldRecords) :
                    "Database search history trong tình trạng tốt";
            };

            return ResponseEntity.ok(BaseResponse.success("Kiểm tra health thành công", health));
        } catch (Exception e) {
            log.error("Error checking cleanup health: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.error("Lỗi khi kiểm tra health cleanup"));
        }
    }
}