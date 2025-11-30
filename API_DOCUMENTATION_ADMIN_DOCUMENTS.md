# API Documentation - Admin Document Management

## 📦 Import vào Postman

1. Mở Postman
2. Click **Import** ở góc trái trên
3. Chọn file: `LegalSystem_Admin_Document_API.postman_collection.json`
4. Collection sẽ được import với 15 APIs

## 🔐 Authentication

Tất cả API yêu cầu **Bearer Token** với role **ADMIN**.

### Bước 1: Login để lấy token
```
POST /api/auth/login
Body:
{
  "email": "admin@legalconnect.com",
  "password": "admin123"
}
```

Token sẽ tự động được lưu vào biến `{{access_token}}` và được sử dụng cho các request tiếp theo.

## 📋 Danh sách APIs

### 1. Thống kê
- **GET** `/api/admin/documents/stats`
- Response: `{ total, active, inactive }`

### 2. Lấy danh sách văn bản
- **GET** `/api/admin/documents`
- Query params:
  - `page` (int): Số trang (default: 0)
  - `size` (int): Số bản ghi/trang (default: 10)
  - `search` (string): Tìm kiếm theo tiêu đề
  - `category` (string): Lọc theo danh mục
  - `status` (string): ACTIVE hoặc INACTIVE
  - `sort` (string): field,direction (vd: `createdAt,desc`)

### 3. Lấy chi tiết văn bản
- **GET** `/api/admin/documents/{id}`

### 4. Thêm văn bản mới
- **POST** `/api/admin/documents`
- Content-Type: `multipart/form-data`
- Body:
  - `title` (string, required): Tiêu đề
  - `category` (string, required): Danh mục
  - `file` (file, required): File XML
  - `status` (string, optional): ACTIVE/INACTIVE

### 5. Cập nhật văn bản
- **PUT** `/api/admin/documents/{id}`
- Content-Type: `multipart/form-data`
- Body: (tất cả optional)
  - `title` (string)
  - `category` (string)
  - `file` (file): File XML mới
  - `status` (string)

### 6. Đổi trạng thái
- **PATCH** `/api/admin/documents/{id}/status`
- Body: `{ "status": "INACTIVE" }`

### 7. Xóa văn bản
- **DELETE** `/api/admin/documents/{id}`

### 8. Xóa nhiều văn bản
- **DELETE** `/api/admin/documents/bulk`
- Body: `{ "ids": [1, 2, 3] }`

### 9. Lấy danh sách danh mục
- **GET** `/api/admin/documents/categories`

### 10. Upload file XML
- **POST** `/api/admin/documents/upload`
- Content-Type: `multipart/form-data`
- Body: `file` (file)

### 11. Xem nội dung XML
- **GET** `/api/admin/documents/{id}/content`

## 🎯 Ví dụ sử dụng

### Tìm kiếm kết hợp:
```
GET /api/admin/documents?search=dân sự&category=Luật Dân sự&status=ACTIVE&sort=viewCount,desc
```

### Thêm văn bản:
```
POST /api/admin/documents
Content-Type: multipart/form-data

title: Bộ luật Test 2024
category: Luật Test
file: [chọn file .xml]
status: ACTIVE
```

### Đổi trạng thái:
```
PATCH /api/admin/documents/5/status
Content-Type: application/json

{
  "status": "INACTIVE"
}
```

## ⚙️ Configuration

File: `application.properties`
```properties
# Upload path
upload.documents.path=uploads/documents
# Server URL
server.base-url=http://localhost:8080
# Max file size
spring.servlet.multipart.max-file-size=10MB
```

## 📁 Cấu trúc file upload

```
LegalSystem/
└── uploads/
    └── documents/
        ├── abc-123-uuid.xml
        ├── def-456-uuid.xml
        └── ...
```

Files được đặt tên theo UUID để tránh trùng lặp.

## 🔒 Security

- Tất cả endpoints yêu cầu role `ADMIN`
- Token có thời gian sống: 15 phút (access token)
- Chỉ chấp nhận file `.xml`
- Max file size: 10MB

## ✅ Testing với Postman

1. **Đầu tiên**: Chạy API "Login" để lấy token
2. Token tự động được lưu vào collection variable
3. Các API khác sẽ tự động dùng token này
4. Nếu token hết hạn, login lại

## 🚀 Response Format

### Success:
```json
{
  "success": true,
  "message": "Thành công",
  "data": { ... }
}
```

### Error:
```json
{
  "success": false,
  "message": "Lỗi...",
  "data": null
}
```

### Pagination:
```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 10,
  "number": 0,
  "size": 10
}
```

## 📞 Support

Nếu gặp lỗi, check:
1. Backend đang chạy (port 8080)
2. Token còn hạn
3. Role user là ADMIN
4. File upload đúng định dạng XML
5. Thư mục `uploads/documents` có quyền ghi
