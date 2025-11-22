# 🕐 **Scheduled Jobs Documentation**

## Tổng quan

Hệ thống **Legal System** sử dụng Spring Scheduling để tự động thực hiện các task bảo trì database, đặc biệt là cleanup dữ liệu cũ để tối ưu hóa performance.

---

## 📋 **Search History Cleanup Job**

### **Mục đích**
- Tự động xóa lịch sử tìm kiếm cũ hơn 30 ngày (configurable)
- Tránh database phình to và giảm performance
- Chạy vào thời điểm ít traffic (2:00 AM hàng ngày)

### **Cấu hình**

**File: `application.properties`**
```properties
# Bật/tắt cleanup job
search.history.cleanup.enabled=true

# Số ngày giữ lại data (mặc định 30 ngày)
search.history.cleanup.retention.days=30

# Batch size để tránh lock database
search.history.cleanup.batch.size=1000

# Cron expression (mặc định 2:00 AM hàng ngày)
search.history.cleanup.cron=0 0 2 * * *

# Thread pool cho scheduling
spring.task.scheduling.pool.size=2
```

### **Cron Expressions**

| Giá trị | Ý nghĩa |
|---------|---------|
| `0 0 2 * * *` | 2:00 AM hàng ngày |
| `0 0 3 * * SUN` | 3:00 AM Chủ nhật hàng tuần |
| `0 0 1 1 * *` | 1:00 AM ngày đầu tháng |
| `0 */30 * * * *` | Mỗi 30 phút (testing) |

### **Monitoring & Logging**

**Log Levels:**
- `INFO`: Job start/finish, tổng kết cleanup
- `DEBUG`: Chi tiết batch processing
- `ERROR`: Lỗi trong quá trình cleanup

**Log Examples:**
```
2024-01-22 02:00:00 INFO  - === BẮT ĐẦU CLEANUP SEARCH HISTORY JOB ===
2024-01-22 02:00:01 INFO  - Xóa lịch sử tìm kiếm cũ hơn 30 ngày (trước 2023-12-23T02:00:00)
2024-01-22 02:00:02 INFO  - Đã xóa 1000 records trong batch này. Tổng đã xóa: 1000
2024-01-22 02:00:03 INFO  - Đã xóa 856 records trong batch này. Tổng đã xóa: 1856
2024-01-22 02:00:04 INFO  - === HOÀN THÀNH CLEANUP SEARCH HISTORY JOB ===
2024-01-22 02:00:04 INFO  - Tổng số records đã xóa: 1856
2024-01-22 02:00:04 INFO  - Thời gian thực hiện: 4023 ms
2024-01-22 02:00:05 INFO  - Số lượng search history còn lại sau cleanup: 23144
```

---

## 🛠️ **Admin Management APIs**

### **Health Check**
```bash
GET /api/admin/cleanup/search-history/health
```
Kiểm tra trạng thái database và khuyến nghị cleanup

### **Configuration**
```bash  
GET /api/admin/cleanup/search-history/config
```
Xem cấu hình hiện tại của cleanup job

### **Statistics**
```bash
GET /api/admin/cleanup/search-history/stats?retentionDays=30
```
Thống kê số lượng records cũ/mới, size estimate

### **Dry Run**
```bash
GET /api/admin/cleanup/search-history/dry-run?retentionDays=45
```
Kiểm tra số lượng sẽ xóa mà không thực sự xóa

### **Manual Cleanup**
```bash
POST /api/admin/cleanup/search-history/manual?retentionDays=60
```
Trigger cleanup job ngay lập tức

---

## 📊 **Performance Considerations**

### **Batch Processing**
- Cleanup xử lý theo batch để tránh lock database quá lâu
- Mỗi batch xóa tối đa 1000 records
- Nghỉ 100ms giữa các batch

### **Database Impact**
- Chạy vào 2:00 AM để tránh peak traffic
- Sử dụng transaction để đảm bảo data consistency
- Index trên `created_at` field để tối ưu delete query

### **Memory Usage**
- Minimal memory footprint do xử lý batch
- Log rotation để tránh log file quá lớn

---

## 🚨 **Troubleshooting**

### **Job không chạy**
1. Kiểm tra `search.history.cleanup.enabled=true`
2. Verify `@EnableScheduling` trong `BackendApplication.java`
3. Check application logs cho scheduling errors

### **Performance chậm**
1. Tăng batch size nếu database có capacity
2. Điều chỉnh sleep time giữa batches
3. Kiểm tra database indexes

### **Memory issues**
1. Giảm batch size
2. Kiểm tra connection pool settings
3. Monitor heap memory usage

### **Common Errors**
```bash
# Connection timeout
ERROR - Error in batch cleanup: Connection timed out

# Lock timeout  
ERROR - Error in batch cleanup: Lock wait timeout exceeded

# Constraint violation
ERROR - Error in batch cleanup: Cannot delete referenced row
```

---

## 📈 **Metrics & Monitoring**

### **Business Metrics**
- Số records xóa mỗi ngày
- Thời gian thực hiện cleanup
- Tỷ lệ records cũ vs mới

### **Technical Metrics**  
- Database size sau cleanup
- Query execution time
- Memory usage during cleanup

### **Alerts** 
- Cleanup job fail
- Cleanup time > 10 minutes
- Database size > threshold
- Old records > 50% total

---

## 🔧 **Configuration Examples**

### **Production Environment**
```properties
# Conservative settings cho production
search.history.cleanup.enabled=true
search.history.cleanup.retention.days=60
search.history.cleanup.batch.size=500
search.history.cleanup.cron=0 0 3 * * SUN
```

### **Development Environment**  
```properties
# Aggressive cleanup cho development
search.history.cleanup.enabled=true
search.history.cleanup.retention.days=7
search.history.cleanup.batch.size=100
search.history.cleanup.cron=0 */15 * * * *
```

### **Testing Environment**
```properties
# Disabled cho unit tests
search.history.cleanup.enabled=false
```

---

## 📝 **Best Practices**

### **Configuration**
- Set retention period dựa trên business requirements
- Monitor database size và performance sau mỗi lần thay đổi
- Test cron expressions trước khi deploy production

### **Monitoring**
- Set up alerts cho job failures
- Track cleanup metrics theo thời gian
- Monitor database performance sau cleanup

### **Maintenance**
- Regular review logs cho errors
- Analyze cleanup statistics để optimize settings
- Update retention period khi cần

---

**Last Updated:** November 22, 2024  
**Scheduler Version:** 1.0  
**Spring Boot Version:** 3.x