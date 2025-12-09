package com.example.backend.scheduled;

import com.example.backend.search.service.SearchHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Scheduled job để tự động xóa lịch sử tìm kiếm cũ
 * Chạy hàng ngày lúc 2:00 AM để dọn dẹp database
 */
@Component
@ConditionalOnProperty(value = "search.history.cleanup.enabled", havingValue = "true", matchIfMissing = true)
public class SearchHistoryCleanupJob {

    private static final Logger logger = LoggerFactory.getLogger(SearchHistoryCleanupJob.class);

    @Autowired
    private SearchHistoryService searchHistoryService;

    @Value("${search.history.cleanup.retention.days:30}")
    private int retentionDays;

    @Value("${search.history.cleanup.batch.size:1000}")
    private int batchSize;

    /**
     * Scheduled method chạy theo cron expression được cấu hình
     * Default: 0 0 2 * * * (hàng ngày lúc 2:00 AM)
     */
    @Scheduled(cron = "${search.history.cleanup.cron:0 0 2 * * *}")
    public void cleanupOldSearchHistory() {
        logger.info("=== BẮT ĐẦU CLEANUP SEARCH HISTORY JOB ===");
        long startTime = System.currentTimeMillis();
        
        try {
            // Tính toán ngày cutoff
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
            logger.info("Xóa lịch sử tìm kiếm cũ hơn {} ngày (trước {})", retentionDays, cutoffDate);

            // Thực hiện cleanup theo batch để tránh lock database quá lâu
            int totalDeleted = 0;
            int currentBatchDeleted;
            
            do {
                currentBatchDeleted = searchHistoryService.deleteOldSearchHistoriesBatch(cutoffDate, batchSize);
                totalDeleted += currentBatchDeleted;
                logger.info("Đã xóa {} records trong batch này. Tổng đã xóa: {}", 
                           currentBatchDeleted, totalDeleted);
                
                // Nghỉ một chút giữa các batch để không quá tải database
                if (currentBatchDeleted > 0) {
                    Thread.sleep(100);
                }
                
            } while (currentBatchDeleted > 0);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("=== HOÀN THÀNH CLEANUP SEARCH HISTORY JOB ===");
            logger.info("Tổng số records đã xóa: {}", totalDeleted);
            logger.info("Thời gian thực hiện: {} ms", duration);
            
            // Log thống kê sau cleanup nếu có xóa data
            if (totalDeleted > 0) {
                long remainingRecords = searchHistoryService.getTotalSearchHistoryCount();
                logger.info("Số lượng search history còn lại sau cleanup: {}", remainingRecords);
            }

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("=== LỖI TRONG QUÁT TRÌNH CLEANUP SEARCH HISTORY ===");
            logger.error("Lỗi sau {} ms: {}", duration, e.getMessage(), e);
            
            // Có thể thêm notification/alert ở đây nếu cần
            // notificationService.sendAlert("Search History Cleanup Job Failed", e.getMessage());
        }
    }

    /**
     * Cleanup manual cho testing hoặc emergency cleanup
     * Có thể gọi qua JMX hoặc actuator endpoint
     */
    public int manualCleanup(int customRetentionDays) {
        logger.info("=== BẮT ĐẦU MANUAL CLEANUP - RETENTION {} NGÀY ===", customRetentionDays);
        
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(customRetentionDays);
            
            int totalDeleted = 0;
            int currentBatchDeleted;
            
            do {
                currentBatchDeleted = searchHistoryService.deleteOldSearchHistoriesBatch(cutoffDate, batchSize);
                totalDeleted += currentBatchDeleted;
                
                if (currentBatchDeleted > 0) {
                    Thread.sleep(50); // Ngắn hơn cho manual cleanup
                }
                
            } while (currentBatchDeleted > 0);

            logger.info("Manual cleanup hoàn thành. Đã xóa {} records", totalDeleted);
            return totalDeleted;
            
        } catch (Exception e) {
            logger.error("Lỗi trong manual cleanup: {}", e.getMessage(), e);
            throw new RuntimeException("Manual cleanup failed", e);
        }
    }

    /**
     * Health check method để kiểm tra trạng thái job
     */
    public boolean isCleanupEnabled() {
        return true; // Component chỉ load khi enabled=true
    }

    public int getRetentionDays() {
        return retentionDays;
    }

    public int getBatchSize() {
        return batchSize;
    }
}