# 📋 **LEGAL SYSTEM - API DOCUMENTATION**

## 🌟 **Tổng quan hệ thống**

**Legal System** là hệ thống quản lý pháp lý với các tính năng chính:
- 📚 **Quản lý văn bản pháp luật** - Tìm kiếm, xem, phân loại
- 📅 **Đặt lịch tư vấn** - Citizen đặt lịch với Lawyer  
- 👤 **Quản lý người dùng** - Authentication, Profile management
- 👨‍💼 **Quản lý luật sư** - Registration, Verification
- 🔒 **Bảo mật** - JWT Authentication, Role-based access

---

## 🔗 **Base Configuration**

```
Base URL: http://localhost:8080
Content-Type: application/json
Authentication: Bearer {jwt_token}
```

### **Standard Response Format:**
```json
{
  "success": true,
  "status": 200,
  "message": "Thông báo kết quả",
  "data": { ... },
  "timestamp": "2025-11-22T11:30:00.000Z",
  "traceId": "abc123",
  "path": "/api/documents/1",
  "links": {
    "self": "http://localhost:8080/current/endpoint"
  }
}
```

---

## 📑 **Table of Contents**

1. [🔐 Authentication APIs](#1--authentication-apis)
2. [📚 Legal Document APIs](#2--legal-document-apis)
3. [📅 Appointment APIs](#3--appointment-apis)  
4. [👤 User Management APIs](#4--user-management-apis)
5. [👨‍💼 Lawyer Management APIs](#5--lawyer-management-apis)
6. [❌ Error Handling](#6--error-handling)
7. [🧪 Testing Examples](#7--testing-examples)

---

# 1. 🔐 **Authentication APIs**

## **Đăng nhập hệ thống**

### **POST** `/api/auth/login`
Đăng nhập với email và mật khẩu.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Đăng nhập thành công",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "refresh_token_here",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "fullName": "Nguyễn Văn A",
      "role": "CITIZEN"
    }
  }
}
```

## **Làm mới token**

### **POST** `/api/auth/refresh`
Làm mới JWT token bằng refresh token.

**Request Body:**
```json
{
  "refreshToken": "refresh_token_here"
}
```

## **Đăng xuất**

### **POST** `/api/auth/logout`
Đăng xuất và vô hiệu hóa refresh token.

**Request Body:**
```json
{
  "refreshToken": "refresh_token_here"
}
```

**Response:** `204 No Content`

## **Đăng ký tài khoản**

### **POST** `/api/auth/register`
Đăng ký tài khoản mới cho citizen.

**Request Body:**
```json
{
  "email": "newuser@example.com",
  "password": "password123",
  "fullName": "Nguyễn Văn B",
  "phoneNumber": "0123456789"
}
```

**Response:**
```json
{
  "success": true,
  "status": 201,
  "message": "Đăng ký thành công. Vui lòng kiểm tra email để xác minh tài khoản.",
  "data": {
    "userId": 2,
    "email": "newuser@example.com",
    "fullName": "Nguyễn Văn B",
    "role": "CITIZEN",
    "active": false
  },
  "links": {
    "verify": "/api/auth/verify?token={token}"
  }
}
```

## **Xác minh email**

### **GET** `/api/auth/verify?token={verification_token}`
Xác minh tài khoản qua token gửi trong email.

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Tài khoản đã được xác minh thành công.",
  "data": {
    "userId": 2,
    "email": "newuser@example.com",
    "active": true
  }
}
```

## **Quên mật khẩu**

### **POST** `/api/auth/forgot-password`
Gửi link đặt lại mật khẩu qua email.

**Request Body:**
```json
{
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Yêu cầu đặt lại mật khẩu đã được gửi đến email của bạn."
}
```

## **Đặt lại mật khẩu**

### **GET** `/api/auth/reset-password/validate?token={reset_token}`
Xác thực token đặt lại mật khẩu (redirect).

### **POST** `/api/auth/reset-password`
Đặt lại mật khẩu mới.

**Request Body:**
```json
{
  "token": "reset_token_here",
  "newPassword": "newpassword123"
}
```

---

# 2. 📚 **Legal Document APIs**

## **Tìm kiếm văn bản nâng cao**

### **GET** `/api/documents/search`
Tìm kiếm văn bản với nhiều bộ lọc.

**Query Parameters:**
- `keyword` (optional): Từ khóa tìm kiếm
- `category` (optional): Danh mục văn bản
- `page` (default: 0): Số trang
- `size` (default: 10): Kích thước trang

**Example:** `/api/documents/search?keyword=hợp đồng&category=Dân sự&page=0&size=10`

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Tìm kiếm văn bản thành công",
  "data": {
    "content": [
      {
        "id": 1,
        "title": "Luật Hợp đồng 2023",
        "number": "15/2023/QH15",
        "type": "Luật",
        "category": "Dân sự",
        "issuedBy": "Quốc hội",
        "issuedDate": "2023-06-15",
        "effectiveDate": "2024-01-01",
        "summary": "Quy định về hợp đồng trong quan hệ dân sự",
        "viewCount": 1520
      }
    ],
    "totalPages": 5,
    "totalElements": 48,
    "size": 10,
    "number": 0
  }
}
```

## **Lấy thông tin văn bản theo ID**

### **GET** `/api/documents/{id}`
Lấy chi tiết văn bản pháp luật.

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy thông tin văn bản thành công",
  "data": {
    "id": 1,
    "title": "Luật Hợp đồng 2023",
    "number": "15/2023/QH15",
    "type": "Luật",
    "category": "Dân sự",
    "issuedBy": "Quốc hội",
    "issuedDate": "2023-06-15",
    "effectiveDate": "2024-01-01",
    "summary": "Quy định về hợp đồng trong quan hệ dân sự",
    "content": "Nội dung đầy đủ văn bản...",
    "viewCount": 1521,
    "tags": ["hợp đồng", "dân sự", "nghĩa vụ"]
  }
}
```

## **Lấy văn bản theo danh mục**

### **GET** `/api/documents/category/{category}`
Lấy danh sách văn bản thuộc danh mục cụ thể.

**Query Parameters:**
- `page` (default: 0): Số trang
- `size` (default: 10): Kích thước trang

**Example:** `/api/documents/category/Hình sự?page=0&size=5`

## **Lấy văn bản phổ biến**

### **GET** `/api/documents/trending`
Lấy danh sách văn bản được xem nhiều nhất.

**Query Parameters:**
- `page` (default: 0): Số trang  
- `size` (default: 10): Kích thước trang

## **Lấy danh sách danh mục**

### **GET** `/api/documents/categories`
Lấy tất cả danh mục văn bản có sẵn.

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy danh sách danh mục thành công",
  "data": {
    "categories": [
      "Dân sự",
      "Hình sự", 
      "Hành chính",
      "Lao động",
      "Kinh doanh"
    ],
    "totalCategories": 5
  }
}
```

## **Thống kê danh mục**

### **GET** `/api/documents/categories/stats`
Lấy thống kê số lượng văn bản theo danh mục.

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy thống kê danh mục thành công",
  "data": {
    "categoryStats": [
      {
        "category": "Dân sự",
        "documentCount": 145
      },
      {
        "category": "Hình sự",
        "documentCount": 98
      }
    ],
    "totalCategories": 5,
    "totalDocuments": 456
  }
}
```

## **Lấy tất cả văn bản**

### **GET** `/api/documents`
Lấy danh sách tất cả văn bản với sắp xếp.

**Query Parameters:**
- `page` (default: 0): Số trang
- `size` (default: 10): Kích thước trang  
- `sortBy` (default: "createdAt"): Trường sắp xếp
- `sortDirection` (default: "desc"): Hướng sắp xếp (asc/desc)

## **Tìm kiếm chung**

### **GET** `/api/documents/general-search`
Tìm kiếm đơn giản theo từ khóa.

**Query Parameters:**
- `keyword` (optional): Từ khóa tìm kiếm
- `page` (default: 0): Số trang
- `size` (default: 10): Kích thước trang

---

# 3. 📅 **Appointment APIs**

## **Tạo lịch hẹn mới**

### **POST** `/api/appointments?citizenId={citizenId}`
Citizen đặt lịch tư vấn với lawyer.

**Request Body:**
```json
{
  "lawyerId": 5,
  "appointmentTime": "2024-01-15T14:30:00",
  "duration": 60,
  "consultationType": "OFFLINE",
  "description": "Tư vấn về tranh chấp hợp đồng lao động",
  "urgency": "NORMAL"
}
```

**Response:**
```json
{
  "success": true,
  "status": 201,
  "message": "Đặt lịch thành công! Chờ luật sư xác nhận.",
  "data": {
    "id": 10,
    "citizenId": 2,
    "citizenName": "Nguyễn Văn B",
    "lawyerId": 5,
    "lawyerName": "Luật sư Trần Thị C",
    "appointmentTime": "2024-01-15T14:30:00",
    "duration": 60,
    "consultationType": "OFFLINE",
    "status": "PENDING",
    "description": "Tư vấn về tranh chấp hợp đồng lao động",
    "urgency": "NORMAL",
    "createdAt": "2024-01-10T09:00:00"
  }
}
```

## **Lấy lịch hẹn của Citizen**

### **GET** `/api/appointments/citizen/{citizenId}`
Lấy danh sách lịch hẹn của citizen.

**Query Parameters:**
- `page` (default: 0): Số trang
- `size` (default: 10): Kích thước trang
- `status` (optional): Lọc theo trạng thái (PENDING, CONFIRMED, REJECTED, CANCELLED, COMPLETED, RATED)

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy danh sách lịch hẹn thành công",
  "data": {
    "content": [
      {
        "id": 10,
        "lawyerName": "Luật sư Trần Thị C",
        "appointmentTime": "2024-01-15T14:30:00",
        "status": "PENDING",
        "consultationType": "OFFLINE"
      }
    ],
    "totalPages": 1,
    "totalElements": 3
  }
}
```

## **Lấy lịch hẹn của Lawyer**

### **GET** `/api/appointments/lawyer/{lawyerId}`
Lấy danh sách lịch hẹn của lawyer.

**Query Parameters:** Giống như citizen API

## **Lấy thông tin lịch hẹn theo ID**

### **GET** `/api/appointments/{appointmentId}?userId={userId}&isLawyer={true/false}`
Lấy chi tiết lịch hẹn cụ thể.

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy thông tin lịch hẹn thành công",
  "data": {
    "id": 10,
    "citizenId": 2,
    "citizenName": "Nguyễn Văn B",
    "citizenPhone": "0123456789",
    "lawyerId": 5,
    "lawyerName": "Luật sư Trần Thị C",
    "appointmentTime": "2024-01-15T14:30:00",
    "duration": 60,
    "consultationType": "OFFLINE",
    "status": "CONFIRMED",
    "description": "Tư vấn về tranh chấp hợp đồng lao động",
    "lawyerNote": "Đã xác nhận. Hẹn gặp tại văn phòng.",
    "rating": null,
    "comment": null
  }
}
```

## **Xác nhận lịch hẹn (Lawyer)**

### **POST** `/api/appointments/{appointmentId}/confirm?lawyerId={lawyerId}`
Lawyer xác nhận lịch hẹn.

**Request Body (optional):**
```json
{
  "message": "Đã xác nhận lịch hẹn. Hẹn gặp bạn tại văn phòng vào 14:30 ngày 15/01."
}
```

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Xác nhận lịch hẹn thành công",
  "data": {
    "id": 10,
    "status": "CONFIRMED",
    "lawyerNote": "Đã xác nhận lịch hẹn..."
  }
}
```

## **Từ chối lịch hẹn (Lawyer)**

### **POST** `/api/appointments/{appointmentId}/reject?lawyerId={lawyerId}`
Lawyer từ chối lịch hẹn.

**Request Body:**
```json
{
  "reason": "Bận họp trong khung giờ này. Vui lòng chọn thời gian khác."
}
```

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Từ chối lịch hẹn thành công",
  "data": {
    "id": 10,
    "status": "REJECTED",
    "lawyerNote": "Bận họp trong khung giờ này..."
  }
}
```

## **Hủy lịch hẹn**

### **POST** `/api/appointments/{appointmentId}/cancel?userId={userId}&isLawyer={true/false}`
Hủy lịch hẹn (cả citizen và lawyer đều có thể hủy).

**Request Body:**
```json
{
  "reason": "Có việc đột xuất, cần hủy lịch hẹn."
}
```

## **Hoàn thành lịch hẹn (Lawyer)**

### **POST** `/api/appointments/{appointmentId}/complete?lawyerId={lawyerId}`
Lawyer đánh dấu lịch hẹn đã hoàn thành.

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Hoàn thành lịch hẹn thành công",
  "data": {
    "id": 10,
    "status": "COMPLETED"
  }
}
```

## **Đánh giá lịch hẹn (Citizen)**

### **POST** `/api/appointments/{appointmentId}/rate?citizenId={citizenId}`
Citizen đánh giá sau khi lịch hẹn hoàn thành.

**Request Body:**
```json
{
  "rating": 5,
  "comment": "Luật sư tư vấn rất chi tiết và nhiệt tình. Cảm ơn nhiều!"
}
```

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Đánh giá lịch hẹn thành công",
  "data": {
    "id": 10,
    "status": "RATED",
    "rating": 5,
    "comment": "Luật sư tư vấn rất chi tiết..."
  }
}
```

---

# 4. 👤 **User Management APIs**

## **Lấy thông tin người dùng**

### **GET** `/api/users/{id}`
Lấy thông tin công khai của người dùng theo ID.

**Response:**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "userId": 1,
    "email": "user@example.com",
    "fullName": "Nguyễn Văn A",
    "phoneNumber": "0123456789",
    "avatarUrl": "http://localhost:8080/uploads/avatar/1_avatar.jpg",
    "role": "CITIZEN",
    "active": true,
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

## **Cập nhật profile**

### **PUT** `/api/users/profile`
Cập nhật thông tin cá nhân (cần authentication).

**Headers:** `Authorization: Bearer {jwt_token}`

**Request Body:**
```json
{
  "fullName": "Nguyễn Văn A Updated",
  "phoneNumber": "0987654321",
  "address": "123 Đường ABC, Quận XYZ, TP.HCM",
  "dateOfBirth": "1990-05-15"
}
```

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Cập nhật hồ sơ thành công",
  "data": {
    "userId": 1,
    "fullName": "Nguyễn Văn A Updated",
    "phoneNumber": "0987654321",
    "address": "123 Đường ABC, Quận XYZ, TP.HCM",
    "dateOfBirth": "1990-05-15"
  }
}
```

## **Upload avatar**

### **POST** `/api/users/avatar`
Upload ảnh đại diện (cần authentication).

**Headers:** 
- `Authorization: Bearer {jwt_token}`
- `Content-Type: multipart/form-data`

**Form Data:**
- `file`: File ảnh (JPG, PNG, max 5MB)

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Upload avatar thành công",
  "data": "http://localhost:8080/uploads/avatar/1_avatar_1642567890123.jpg"
}
```

---

# 5. 👨‍💼 **Lawyer Management APIs**

## **Đăng ký làm luật sư**

### **POST** `/api/lawyers`
Đăng ký upgrade từ CITIZEN lên LAWYER (cần authentication).

**Headers:**
- `Authorization: Bearer {jwt_token}`
- `Content-Type: multipart/form-data`

**Form Data:**
```
data: {
  "licenseNumber": "LS123456789",
  "barAssociationId": 1,
  "specializationIds": [1, 3, 5],
  "experience": 5,
  "education": "Đại học Luật TP.HCM",
  "bio": "Luật sư với 5 năm kinh nghiệm trong lĩnh vực dân sự..."
}
certificate: [File] - Ảnh bằng cấp/chứng chỉ
```

**Response:**
```json
{
  "success": true,
  "status": 201,
  "message": "Dang ky thanh cong cho phe duyet",
  "data": {
    "id": 5,
    "userId": 10,
    "licenseNumber": "LS123456789",
    "barAssociation": "Đoàn luật sư TP.HCM",
    "specializations": ["Dân sự", "Hợp đồng", "Tranh chấp"],
    "experience": 5,
    "status": "PENDING_APPROVAL",
    "certificateUrl": "http://localhost:8080/uploads/certificates/10_cert.jpg"
  }
}
```

## **Upload chứng chỉ bổ sung**

### **POST** `/api/lawyers/certificates`
Upload thêm chứng chỉ cho luật sư (cần authentication).

**Headers:**
- `Authorization: Bearer {jwt_token}`
- `Content-Type: multipart/form-data`

**Form Data:**
- `file`: File chứng chỉ

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Upload certificate thành công", 
  "data": "http://localhost:8080/uploads/certificates/10_cert_1642567890123.jpg"
}
```

---

# 6. ❌ **Error Handling**

## **Standard Error Format**

```json
{
  "success": false,
  "status": 400,
  "message": "Validation failed",
  "errorCode": "VALIDATION_ERROR",
  "errors": [
    {
      "field": "email",
      "message": "Email không hợp lệ"
    }
  ],
  "timestamp": "2024-01-10T10:30:00.000Z",
  "traceId": "error-123",
  "path": "/api/auth/login"
}
```

## **HTTP Status Codes**

| Status | Ý nghĩa | Ví dụ |
|--------|---------|--------|
| `200` | OK | Thành công |
| `201` | Created | Tạo mới thành công |
| `204` | No Content | Xóa thành công |
| `400` | Bad Request | Dữ liệu đầu vào không hợp lệ |
| `401` | Unauthorized | Chưa đăng nhập |
| `403` | Forbidden | Không có quyền truy cập |
| `404` | Not Found | Không tìm thấy tài nguyên |
| `409` | Conflict | Dữ liệu đã tồn tại |
| `500` | Internal Error | Lỗi hệ thống |

## **Common Error Codes**

- `VALIDATION_ERROR`: Lỗi validation dữ liệu
- `AUTHENTICATION_FAILED`: Đăng nhập thất bại
- `ACCESS_DENIED`: Không có quyền truy cập
- `RESOURCE_NOT_FOUND`: Không tìm thấy tài nguyên
- `DUPLICATE_EMAIL`: Email đã tồn tại
- `APPOINTMENT_CONFLICT`: Xung đột lịch hẹn
- `FILE_UPLOAD_ERROR`: Lỗi upload file

---

# 7. 🧪 **Testing Examples**

## **Postman Collection Examples**

### **1. Authentication Flow**
```bash
# 1. Register
POST /api/auth/register
{
  "email": "test@example.com",
  "password": "password123",
  "fullName": "Test User"
}

# 2. Verify (via email link)
GET /api/auth/verify?token=verification_token

# 3. Login
POST /api/auth/login
{
  "email": "test@example.com", 
  "password": "password123"
}

# Response: Save the JWT token
{
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "refresh_token_here"
  }
}
```

### **2. Document Search Flow**
```bash
# 1. Get all categories
GET /api/documents/categories

# 2. Search documents
GET /api/documents/search?keyword=hợp đồng&category=Dân sự&page=0&size=5

# 3. Get document details  
GET /api/documents/1

# 4. Get trending documents
GET /api/documents/trending?page=0&size=10
```

### **3. Appointment Booking Flow**
```bash
# 1. Create appointment (with JWT token)
POST /api/appointments?citizenId=2
Headers: Authorization: Bearer {jwt_token}
{
  "lawyerId": 5,
  "appointmentTime": "2024-01-15T14:30:00",
  "consultationType": "OFFLINE",
  "description": "Tư vấn hợp đồng"
}

# 2. Lawyer confirms
POST /api/appointments/10/confirm?lawyerId=5
Headers: Authorization: Bearer {lawyer_jwt_token}
{
  "message": "Đã xác nhận lịch hẹn"
}

# 3. Complete appointment
POST /api/appointments/10/complete?lawyerId=5
Headers: Authorization: Bearer {lawyer_jwt_token}

# 4. Citizen rates
POST /api/appointments/10/rate?citizenId=2
Headers: Authorization: Bearer {citizen_jwt_token}
{
  "rating": 5,
  "comment": "Tư vấn tốt"
}
```

### **4. Profile Management Flow**
```bash
# 1. Update profile
PUT /api/users/profile
Headers: Authorization: Bearer {jwt_token}
{
  "fullName": "Updated Name",
  "phoneNumber": "0987654321"
}

# 2. Upload avatar
POST /api/users/avatar
Headers: 
  Authorization: Bearer {jwt_token}
  Content-Type: multipart/form-data
Form Data:
  file: [avatar_file.jpg]
```

### **5. Lawyer Registration Flow**
```bash
# 1. Register as lawyer
POST /api/lawyers
Headers:
  Authorization: Bearer {jwt_token}
  Content-Type: multipart/form-data
Form Data:
  data: {
    "licenseNumber": "LS123456789",
    "barAssociationId": 1,
    "specializationIds": [1, 3],
    "experience": 5
  }
  certificate: [certificate_file.jpg]

# 2. Upload additional certificates
POST /api/lawyers/certificates  
Headers:
  Authorization: Bearer {jwt_token}
  Content-Type: multipart/form-data
Form Data:
  file: [additional_cert.jpg]
```

## **cURL Examples**

### **Login Request**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### **Search Documents**
```bash
curl -X GET "http://localhost:8080/api/documents/search?keyword=hợp đồng&page=0&size=5"
```

### **Create Appointment with Authentication**
```bash
curl -X POST "http://localhost:8080/api/appointments?citizenId=2" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "lawyerId": 5,
    "appointmentTime": "2024-01-15T14:30:00",
    "consultationType": "OFFLINE",
    "description": "Tư vấn hợp đồng lao động"
  }'
```

### **Upload Avatar**
```bash
curl -X POST http://localhost:8080/api/users/avatar \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/avatar.jpg"
```

---

## 📞 **Support & Contact**

- **Technical Support**: dev-team@legalsystem.com
- **API Issues**: api-support@legalsystem.com  
- **Documentation**: https://docs.legalsystem.com

**Last Updated:** November 22, 2024  
**API Version:** v1.0  
**Documentation Version:** 1.0