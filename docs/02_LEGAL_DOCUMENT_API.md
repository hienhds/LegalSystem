# 📚 **LEGAL DOCUMENT APIs**

## **Base Path:** `/api/documents`

Legal Document module cung cấp tính năng quản lý và tìm kiếm văn bản pháp luật.

---

## 📋 **API Endpoints**

### **1. Tìm kiếm văn bản nâng cao**
```http
GET /api/documents/search?keyword=luật&category=Luật%20Dân%20sự&page=0&size=10
```

**Mục đích:** Tìm kiếm văn bản theo từ khóa và danh mục với phân trang

**Query Parameters:**
- `keyword` (string, optional): Từ khóa tìm trong tiêu đề
- `category` (string, optional): Danh mục văn bản
- `page` (int, default: 0): Số trang (bắt đầu từ 0)
- `size` (int, default: 10): Số kết quả mỗi trang (max: 50)

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Tìm kiếm văn bản thành công",
  "data": {
    "content": [
      {
        "documentId": 1,
        "title": "Bộ luật Dân sự số 91/2015/QH13",
        "category": "Luật Dân sự",
        "fileUrl": "http://localhost:8080/documents/civil-law-2015.pdf",
        "status": "ACTIVE",
        "viewCount": 1250,
        "createdAt": "2025-01-15T09:00:00.000Z"
      },
      {
        "documentId": 5,
        "title": "Luật Đất đai số 55/2013/QH13",
        "category": "Luật Đất đai", 
        "fileUrl": "http://localhost:8080/documents/land-law-2013.pdf",
        "status": "ACTIVE",
        "viewCount": 890,
        "createdAt": "2025-02-20T14:30:00.000Z"
      }
    ],
    "totalElements": 25,
    "totalPages": 3,
    "size": 10,
    "number": 0,
    "numberOfElements": 10,
    "first": true,
    "last": false
  }
}
```

---

### **2. Lấy chi tiết văn bản**
```http
GET /api/documents/{id}
```

**Mục đích:** Lấy thông tin chi tiết văn bản và tự động tăng lượt xem

**Path Parameters:**
- `id` (long, required): ID của văn bản

**Example:**
```http
GET /api/documents/1
```

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy thông tin văn bản thành công",
  "data": {
    "documentId": 1,
    "title": "Bộ luật Dân sự số 91/2015/QH13",
    "category": "Luật Dân sự",
    "fileUrl": "http://localhost:8080/documents/civil-law-2015.pdf",
    "status": "ACTIVE",
    "viewCount": 1251,
    "createdAt": "2025-01-15T09:00:00.000Z"
  }
}
```

**Response Error (404):**
```json
{
  "success": false,
  "status": 404,
  "message": "Không tìm thấy văn bản pháp luật với ID: 999",
  "data": null
}
```

---

### **3. Lấy văn bản theo danh mục**
```http
GET /api/documents/category/{category}?page=0&size=10
```

**Mục đích:** Lấy danh sách văn bản thuộc một danh mục cụ thể

**Path Parameters:**
- `category` (string, required): Tên danh mục

**Query Parameters:**  
- `page` (int, default: 0): Số trang
- `size` (int, default: 10): Số kết quả mỗi trang

**Example:**
```http
GET /api/documents/category/Luật%20Dân%20sự?page=0&size=5
```

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy danh sách văn bản thuộc danh mục 'Luật Dân sự' thành công",
  "data": {
    "content": [
      {
        "documentId": 1,
        "title": "Bộ luật Dân sự số 91/2015/QH13",
        "category": "Luật Dân sự",
        "fileUrl": "http://localhost:8080/documents/civil-law-2015.pdf",
        "status": "ACTIVE",
        "viewCount": 1251,
        "createdAt": "2025-01-15T09:00:00.000Z"
      }
    ],
    "totalElements": 8,
    "totalPages": 2,
    "size": 5,
    "number": 0
  }
}
```

---

### **4. Lấy văn bản phổ biến (trending)**
```http
GET /api/documents/trending?page=0&size=10
```

**Mục đích:** Lấy danh sách văn bản được xem nhiều nhất

**Query Parameters:**
- `page` (int, default: 0): Số trang
- `size` (int, default: 10): Số kết quả mỗi trang

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy danh sách văn bản phổ biến thành công",
  "data": {
    "content": [
      {
        "documentId": 3,
        "title": "Luật Lao động số 45/2019/QH14",
        "category": "Luật Lao động",
        "fileUrl": "http://localhost:8080/documents/labor-law-2019.pdf",
        "status": "ACTIVE",
        "viewCount": 2850,
        "createdAt": "2025-03-10T10:15:00.000Z"
      },
      {
        "documentId": 1,
        "title": "Bộ luật Dân sự số 91/2015/QH13",
        "category": "Luật Dân sự",
        "fileUrl": "http://localhost:8080/documents/civil-law-2015.pdf",
        "status": "ACTIVE",
        "viewCount": 1251,
        "createdAt": "2025-01-15T09:00:00.000Z"
      }
    ]
  }
}
```

---

### **5. Lấy tất cả danh mục**
```http
GET /api/documents/categories
```

**Mục đích:** Lấy danh sách tất cả danh mục văn bản có trong hệ thống

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy danh sách danh mục thành công",
  "data": {
    "categories": [
      "Luật Dân sự",
      "Luật Hình sự", 
      "Luật Lao động",
      "Luật Đất đai",
      "Luật Doanh nghiệp",
      "Luật Thuế",
      "Luật Giao thông",
      "Nghị định",
      "Thông tư",
      "Quyết định"
    ],
    "totalCategories": 10
  }
}
```

---

### **6. Thống kê danh mục**
```http
GET /api/documents/categories/stats
```

**Mục đích:** Lấy thống kê số lượng văn bản theo từng danh mục

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy thống kê danh mục thành công",
  "data": {
    "categoryStats": [
      {
        "category": "Luật Dân sự",
        "documentCount": 25
      },
      {
        "category": "Luật Hình sự",
        "documentCount": 18
      },
      {
        "category": "Luật Lao động", 
        "documentCount": 15
      },
      {
        "category": "Nghị định",
        "documentCount": 45
      }
    ],
    "totalDocuments": 103
  }
}
```

---

### **7. Lấy tất cả văn bản**
```http
GET /api/documents?page=0&size=10&sortBy=createdAt&sortDirection=desc
```

**Mục đích:** Lấy danh sách tất cả văn bản với sắp xếp và phân trang

**Query Parameters:**
- `page` (int, default: 0): Số trang
- `size` (int, default: 10): Số kết quả mỗi trang
- `sortBy` (string, default: "createdAt"): Trường sắp xếp
  - Options: `title`, `category`, `viewCount`, `createdAt`
- `sortDirection` (string, default: "desc"): Hướng sắp xếp
  - Options: `asc`, `desc`

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy danh sách văn bản thành công",
  "data": {
    "content": [
      {
        "documentId": 10,
        "title": "Nghị định 15/2020/NĐ-CP",
        "category": "Nghị định",
        "fileUrl": "http://localhost:8080/documents/decree-15-2020.pdf",
        "status": "ACTIVE",
        "viewCount": 340,
        "createdAt": "2025-11-20T16:45:00.000Z"
      }
    ],
    "totalElements": 103,
    "totalPages": 11
  }
}
```

---

### **8. Tìm kiếm tổng quát**
```http
GET /api/documents/general-search?keyword=dân sự&page=0&size=10
```

**Mục đích:** Tìm kiếm đơn giản trong cả tiêu đề và danh mục

**Query Parameters:**
- `keyword` (string, optional): Từ khóa tìm kiếm
- `page` (int, default: 0): Số trang  
- `size` (int, default: 10): Số kết quả mỗi trang

**Note:** Nếu không có keyword, sẽ trả về tất cả văn bản

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Tìm kiếm với từ khóa 'dân sự' thành công",
  "data": {
    "content": [
      {
        "documentId": 1,
        "title": "Bộ luật Dân sự số 91/2015/QH13",
        "category": "Luật Dân sự",
        "viewCount": 1251
      }
    ]
  }
}
```

---

## 📊 **Document Status**

- **ACTIVE**: Văn bản có hiệu lực
- **INACTIVE**: Văn bản đã hết hiệu lực

---

## 🔍 **Search Tips**

1. **Keyword search**: Tìm trong tiêu đề văn bản
2. **Category filter**: Lọc theo danh mục chính xác
3. **Combined search**: Có thể kết hợp keyword + category
4. **Case insensitive**: Không phân biệt hoa thường
5. **Pagination**: Sử dụng page và size để phân trang

---

## 📈 **Performance Notes**

- **Caching**: Kết quả search được cache 5 phút
- **View tracking**: Mỗi lần GET document sẽ tăng view count
- **Rate limiting**: 100 requests/minute per IP
- **File serving**: PDF files served qua CDN