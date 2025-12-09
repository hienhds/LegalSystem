# 🗂️ **SearchHistories Database Design**

## 📋 **Tổng quan**

Bảng `search_histories` lưu trữ lịch sử tìm kiếm của người dùng để:
- Theo dõi hành vi tìm kiếm 
- Đề xuất từ khóa phổ biến
- Phân tích thống kê sử dụng
- Cải thiện trải nghiệm người dùng

---

## 🏗️ **Database Schema**

### **Bảng: search_histories**

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | ID duy nhất |
| `user_id` | BIGINT | FOREIGN KEY → users(user_id), NOT NULL | ID người dùng |
| `search_module` | VARCHAR(50) | NOT NULL | Module tìm kiếm (LAWYER, LEGAL_DOCUMENT, FORUM) |
| `keyword` | VARCHAR(500) | NULL | Từ khóa tìm kiếm |
| `category` | VARCHAR(100) | NULL | Danh mục tìm kiếm (theo từng module) |
| `search_type` | VARCHAR(50) | NOT NULL, DEFAULT 'GENERAL' | Loại tìm kiếm (GENERAL, ADVANCED, FILTER) |
| `filters_json` | JSON | NULL | Bộ lọc chi tiết (specialization, experience, location...) |
| `result_count` | INT | NOT NULL, DEFAULT 0 | Số kết quả trả về |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Thời gian tìm kiếm |

### **Indexes**

```sql
-- Performance indexes
CREATE INDEX idx_search_histories_user_id ON search_histories(user_id);
CREATE INDEX idx_search_histories_created_at ON search_histories(created_at);
CREATE INDEX idx_search_histories_keyword ON search_histories(keyword);
CREATE INDEX idx_search_histories_category ON search_histories(category);
CREATE INDEX idx_search_histories_module ON search_histories(search_module);
CREATE INDEX idx_search_histories_user_time ON search_histories(user_id, created_at DESC);
CREATE INDEX idx_search_histories_module_time ON search_histories(search_module, created_at DESC);

-- Composite index for popular keywords by module
CREATE INDEX idx_search_histories_module_keyword_time ON search_histories(search_module, keyword, created_at);
```

### **Foreign Keys**

```sql
ALTER TABLE search_histories 
ADD CONSTRAINT fk_search_histories_user_id 
FOREIGN KEY (user_id) REFERENCES users(user_id) 
ON DELETE CASCADE;
```

---

## 🎯 **Enums Definition**

### **SearchModule Enum**
```java
public enum SearchModule {
    LEGAL_DOCUMENT,    // Tìm kiếm văn bản pháp luật
    LAWYER,           // Tìm kiếm luật sư  
    FORUM,            // Tìm kiếm forum/Q&A
    APPOINTMENT,      // Tìm kiếm lịch hẹn
    USER              // Tìm kiếm người dùng
}
```

### **SearchType Enum**
```java
public enum SearchType {
    GENERAL,        // Tìm kiếm chung theo keyword
    ADVANCED,       // Tìm kiếm nâng cao với nhiều filters
    FILTER,         // Tìm kiếm theo bộ lọc cụ thể
    CATEGORY,       // Tìm kiếm theo danh mục
    TRENDING,       // Xem nội dung phổ biến
    BY_ID,          // Truy cập trực tiếp theo ID
    AUTOCOMPLETE    // Auto-suggest/completion
}
```

---

## 📊 **Entity Relationships**

### **User ↔ SearchHistory (One-to-Many)**
```java
// User Entity
@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<SearchHistory> searchHistories = new ArrayList<>();

// SearchHistory Entity  
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

---

## 🔍 **Use Cases & Queries**

### **1. Lấy lịch sử tìm kiếm của user theo module**
```sql
SELECT * FROM search_histories 
WHERE user_id = ? AND search_module = ?
ORDER BY created_at DESC 
LIMIT 50;
```

### **2. Từ khóa phổ biến theo module (7 ngày qua)**
```sql
-- Từ khóa phổ biến cho Legal Documents
SELECT keyword, COUNT(*) as search_count
FROM search_histories 
WHERE search_module = 'LEGAL_DOCUMENT' 
AND keyword IS NOT NULL 
AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY keyword 
ORDER BY search_count DESC 
LIMIT 20;

-- Từ khóa phổ biến cho Lawyers
SELECT keyword, COUNT(*) as search_count
FROM search_histories 
WHERE search_module = 'LAWYER' 
AND keyword IS NOT NULL 
AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY keyword 
ORDER BY search_count DESC 
LIMIT 20;
```

### **3. Thống kê tìm kiếm theo danh mục và module**
```sql
SELECT search_module, category, COUNT(*) as category_count
FROM search_histories 
WHERE category IS NOT NULL 
AND created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY search_module, category 
ORDER BY search_module, category_count DESC;
```

### **4. Filters phổ biến cho Lawyer search**
```sql
SELECT 
    JSON_EXTRACT(filters_json, '$.specialization') as specialization,
    JSON_EXTRACT(filters_json, '$.experience') as experience,
    JSON_EXTRACT(filters_json, '$.location') as location,
    COUNT(*) as usage_count
FROM search_histories 
WHERE search_module = 'LAWYER' 
AND filters_json IS NOT NULL
AND created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY specialization, experience, location
ORDER BY usage_count DESC
LIMIT 20;
```

### **5. Tìm kiếm tương tự theo module**
```sql
SELECT DISTINCT keyword 
FROM search_histories 
WHERE search_module = ?
AND keyword LIKE CONCAT('%', ?, '%') 
AND keyword IS NOT NULL 
ORDER BY created_at DESC 
LIMIT 10;
```

### **6. Thống kê module sử dụng nhiều nhất**
```sql
SELECT 
    search_module,
    COUNT(*) as total_searches,
    COUNT(DISTINCT user_id) as unique_users,
    AVG(result_count) as avg_results
FROM search_histories 
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY search_module 
ORDER BY total_searches DESC;
```

---

## ⚡ **Performance Considerations**

### **1. Data Partitioning**
```sql
-- Partition theo tháng cho performance tốt hơn
CREATE TABLE search_histories (
    ...
) PARTITION BY RANGE (YEAR(created_at) * 100 + MONTH(created_at)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    ...
);
```

### **2. Archive Strategy**
- **Hot Data**: 3 tháng gần nhất (truy vấn thường xuyên)
- **Warm Data**: 3-12 tháng (thống kê periodical) 
- **Cold Data**: >12 tháng (archive hoặc delete)

### **3. Query Optimization**
- Limit kết quả trả về (max 100 records)
- Cache popular keywords (Redis)
- Batch insert cho performance tốt hơn

---

## 🔒 **Security & Privacy**

### **1. Data Anonymization**
- Không lưu query parameters nhạy cảm
- Hash IP address nếu cần thiết
- Tuân thủ GDPR/privacy regulations

### **2. Data Retention**
- Auto-delete sau 90 ngày (configurable)
- User có thể xóa lịch sử cá nhân
- Admin có thể clear bulk data

### **3. Access Control**
- User chỉ xem được lịch sử của mình
- Admin có thể xem aggregated statistics
- Không expose raw search data

---

## 📝 **Implementation Notes**

### **1. AsyncSearchHistoryService**
```java
@Async
public void saveSearchHistoryAsync(SearchHistoryRequest request) {
    // Non-blocking save to avoid impacting search performance
}
```

### **2. Rate Limiting**
- Giới hạn số lượng searches/user/hour
- Prevent spam và abuse

### **3. Monitoring**
- Track search volume trends
- Monitor popular keywords
- Alert on unusual patterns

---

## 🧪 **Testing Strategy**

### **1. Unit Tests**
- Entity validation
- Repository methods
- Service layer logic

### **2. Integration Tests**
- Database constraints
- Foreign key relationships
- Performance benchmarks

### **3. Load Tests**
- High-volume search scenarios
- Concurrent user simulations
- Database performance under load

---

## 📈 **Analytics Capabilities**

### **1. Search Trends**
- Popular keywords over time
- Category preferences
- Peak search hours

### **2. User Behavior**
- Search frequency per user
- Common search patterns
- Conversion rates (search → view)

### **3. Content Insights**
- Most searched but low-result keywords
- Categories needing more content
- Gap analysis for content strategy

---

## 🚀 **Future Enhancements**

### **1. Machine Learning**
- Search intent prediction
- Personalized recommendations
- Auto-complete suggestions

### **2. Advanced Analytics**
- Search funnel analysis
- A/B testing for search UX
- Real-time search trends dashboard

### **3. Enterprise Features**
- Search analytics API
- Custom reporting
- Data export capabilities

---

## 🔧 **Module-Specific Filters**

### **1. LEGAL_DOCUMENT Module**
```json
{
  "category": "Dân sự",
  "type": "Luật",
  "issuedBy": "Quốc hội", 
  "effectiveDate": {
    "from": "2023-01-01",
    "to": "2024-12-31"
  },
  "tags": ["hợp đồng", "tranh chấp"]
}
```

### **2. LAWYER Module**
```json
{
  "specialization": ["Dân sự", "Hình sự"],
  "experience": {
    "min": 5,
    "max": 15
  },
  "location": {
    "city": "TP.HCM",
    "district": "Quận 1"
  },
  "rating": {
    "min": 4.0
  },
  "verified": true,
  "available": true
}
```

### **3. FORUM Module**
```json
{
  "category": "Tư vấn pháp luật",
  "status": "ANSWERED",
  "hasExpertReply": true,
  "postDate": {
    "from": "2024-01-01",
    "to": "2024-11-22"
  },
  "tags": ["tư vấn", "hợp đồng"],
  "sortBy": "popularity"
}
```

### **4. APPOINTMENT Module**
```json
{
  "status": ["CONFIRMED", "COMPLETED"],
  "consultationType": "ONLINE",
  "appointmentDate": {
    "from": "2024-11-22",
    "to": "2024-12-22"
  },
  "duration": 60,
  "lawyerSpecialization": "Dân sự"
}
```

---

## 📋 **Migration Script**

```sql
-- Create table
CREATE TABLE search_histories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    search_module VARCHAR(50) NOT NULL,
    keyword VARCHAR(500),
    category VARCHAR(100),
    search_type VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    filters_json JSON,
    result_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_search_histories_user_id 
        FOREIGN KEY (user_id) REFERENCES users(user_id) 
        ON DELETE CASCADE,
    
    INDEX idx_search_histories_user_id (user_id),
    INDEX idx_search_histories_created_at (created_at),
    INDEX idx_search_histories_keyword (keyword),
    INDEX idx_search_histories_category (category),
    INDEX idx_search_histories_module (search_module),
    INDEX idx_search_histories_user_time (user_id, created_at DESC),
    INDEX idx_search_histories_module_time (search_module, created_at DESC),
    INDEX idx_search_histories_module_keyword_time (search_module, keyword, created_at)
);
```

---

**Created:** November 22, 2025  
**Version:** 1.0  
**Status:** ✅ **DESIGN COMPLETE**