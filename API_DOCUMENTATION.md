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
6. [🔍 Search History APIs](#6--search-history-apis)
7. [🛠️ Admin Cleanup APIs](#7--admin-cleanup-apis)
8. [❌ Error Handling](#8--error-handling)
9. [🧪 Testing Examples](#9--testing-examples)

---

## 6. 🔍 **Search History APIs**

### 📝 **Tạo Lịch Sử Tìm Kiếm**
```http
POST /api/search-history
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
    "searchKeyword": "luật lao động",
    "searchModule": "LAWYER",
    "searchType": "GENERAL", 
    "category": "Labor Law",
    "resultCount": 15,
    "filters": {
        "specialization": "Labor Law",
        "experience": "5+",
        "location": "Hà Nội"
    },
    "executionTime": 250
}
```

**Response:**
```json
{
    "success": true,
    "message": "Search history saved successfully",
    "data": {
        "id": 123,
        "keyword": "luật lao động",
        "searchModule": "LAWYER",
        "searchType": "GENERAL",
        "resultCount": 15,
        "filters": "{\"specialization\":\"Labor Law\"}",
        "executionTime": 250,
        "searchTime": "2025-11-22T19:30:00",
        "userId": 456,
        "username": "user123"
    }
}
```

### 📋 **Lấy Lịch Sử Tìm Kiếm**
```http
GET /api/search-history/user/{userId}?page=0&size=10
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
    "success": true,
    "data": {
        "histories": [
            {
                "id": 123,
                "keyword": "luật lao động",
                "searchModule": "LAWYER", 
                "searchType": "GENERAL",
                "resultCount": 15,
                "filters": "{\"specialization\":\"Labor Law\"}",
                "executionTime": 250,
                "searchTime": "2025-11-22T19:30:00",
                "userId": 456,
                "username": "user123"
            }
        ],
        "totalElements": 50,
        "currentPage": 0,
        "pageSize": 10
    }
}
```

### 🔍 **Lấy Lịch Sử Với Bộ Lọc**
```http
POST /api/search-history/filter
Authorization: Bearer {jwt_token}
```

**Request Body:**
```json
{
    "userId": 456,
    "keyword": "luật",
    "searchModule": "LAWYER",
    "searchType": "GENERAL",
    "category": "Labor Law",
    "fromDate": "2025-11-01T00:00:00",
    "toDate": "2025-11-22T23:59:59",
    "minResultCount": 5,
    "maxResultCount": 100,
    "page": 0,
    "size": 20
}
```

### 🏆 **Từ Khóa Phổ Biến**
```http
GET /api/search-history/popular-keywords?limit=10&period=week
Authorization: Bearer {jwt_token} (ADMIN)
```

**Response:**
```json
{
    "success": true,
    "data": {
        "keywords": [
            {
                "keyword": "luật lao động",
                "searchCount": 150,
                "percentage": 25.5
            },
            {
                "keyword": "tư vấn pháp lý",
                "searchCount": 120,
                "percentage": 20.4
            }
        ],
        "totalSearches": 588,
        "period": "week"
    }
}
```

### 📊 **Thống Kê Tìm Kiếm**
```http
GET /api/search-history/statistics?period=month
Authorization: Bearer {jwt_token} (ADMIN)
```

**Response:**
```json
{
    "success": true,
    "data": {
        "totalSearches": 1250,
        "uniqueUsers": 89,
        "averageResultsPerSearch": 12.5,
        "moduleStats": [
            {
                "module": "LAWYER",
                "searchCount": 650,
                "percentage": 52.0
            },
            {
                "module": "LEGAL_DOCUMENT",
                "searchCount": 400,
                "percentage": 32.0
            }
        ],
        "typeStats": [
            {
                "type": "GENERAL",
                "searchCount": 800,
                "percentage": 64.0
            },
            {
                "type": "ADVANCED",
                "searchCount": 300,
                "percentage": 24.0
            }
        ],
        "period": "month",
        "generatedAt": "2025-11-22T19:30:00"
    }
}
```

### 💡 **Đề Xuất Từ Khóa**
```http
GET /api/search-history/suggestions?q=luật&module=LAWYER&limit=5
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
    "success": true,
    "data": [
        "luật lao động",
        "luật hôn nhân",
        "luật dân sự",
        "luật kinh doanh",
        "luật hình sự"
    ]
}
```

### 📈 **Xu Hướng Tìm Kiếm**
```http
GET /api/search-history/trending?module=LAWYER&days=7&minCount=5
Authorization: Bearer {jwt_token} (ADMIN)
```

**Response:**
```json
{
    "success": true,
    "data": [
        {
            "keyword": "tư vấn pháp lý online",
            "recentCount": 45,
            "growthRate": 150.0,
            "trending": true
        }
    ]
}
```

### 🗑️ **Xóa Lịch Sử Tìm Kiếm**
```http
DELETE /api/search-history/{id}
Authorization: Bearer {jwt_token} (ADMIN)
```

```http
DELETE /api/search-history/user/{userId}
Authorization: Bearer {jwt_token} (ADMIN)
```

**Enums:**
- **SearchModule**: `APPOINTMENT`, `FORUM`, `LAWYER`, `LEGAL_DOCUMENT`, `USER`
- **SearchType**: `GENERAL`, `ADVANCED`, `AUTOCOMPLETE`, `BY_ID`, `CATEGORY`, `FILTER`, `TRENDING`

---

## 7. 🛠️ **Admin Cleanup APIs**

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

# 6. 🔍 **Search History APIs**

## **Lấy lịch sử tìm kiếm của người dùng**

### **GET** `/api/search-history`
Lấy danh sách lịch sử tìm kiếm của người dùng hiện tại (cần authentication).

**Headers:** `Authorization: Bearer {jwt_token}`

**Query Parameters:**
- `page` (default: 0): Số trang
- `size` (default: 20): Kích thước trang

**Example:** `/api/search-history?page=0&size=10`

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy lịch sử tìm kiếm thành công",
  "data": {
    "histories": [
      {
        "id": 15,
        "userId": 2,
        "searchKeyword": "hợp đồng lao động",
        "searchModule": "LEGAL_DOCUMENT",
        "searchType": "GENERAL",
        "filters": {
          "category": "Lao động",
          "documentType": "Luật"
        },
        "resultCount": 24,
        "executionTime": 156,
        "searchedAt": "2024-01-15 10:30:00"
      },
      {
        "id": 14,
        "userId": 2,
        "searchKeyword": "luật sư dân sự",
        "searchModule": "LAWYER",
        "searchType": "ADVANCED",
        "filters": {
          "specialization": "Dân sự",
          "experience": "5+"
        },
        "resultCount": 12,
        "executionTime": 89,
        "searchedAt": "2024-01-15 09:15:00"
      }
    ],
    "total_count": 45,
    "page": 0,
    "page_size": 20,
    "total_pages": 3,
    "has_next": true,
    "has_previous": false
  }
}
```

## **Lấy lịch sử tìm kiếm với bộ lọc nâng cao**

### **POST** `/api/search-history/history/filter`
Lọc lịch sử tìm kiếm theo nhiều tiêu chí (cần authentication).

**Headers:** `Authorization: Bearer {jwt_token}`

**Request Body:**
```json
{
  "search_module": "LEGAL_DOCUMENT",
  "search_type": "GENERAL",
  "keyword": "hợp đồng",
  "start_date": "2024-01-01T00:00:00",
  "end_date": "2024-01-31T23:59:59",
  "has_results": true,
  "min_result_count": 5,
  "max_result_count": 100,
  "page": 0,
  "size": 20,
  "sort_by": "searchTime",
  "sort_direction": "desc"
}
```

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy lịch sử tìm kiếm với filters thành công",
  "data": {
    "histories": [
      {
        "id": 15,
        "userId": 2,
        "searchKeyword": "hợp đồng lao động",
        "searchModule": "LEGAL_DOCUMENT",
        "searchType": "GENERAL",
        "resultCount": 24,
        "executionTime": 156,
        "searchedAt": "2024-01-15 10:30:00"
      }
    ],
    "total_count": 12,
    "page": 0,
    "page_size": 20,
    "total_pages": 1,
    "has_next": false,
    "has_previous": false
  }
}
```

## **Lưu lịch sử tìm kiếm mới**

### **POST** `/api/search-history`
Lưu một lượt tìm kiếm mới của người dùng (cần authentication).

**Headers:** 
- `Authorization: Bearer {jwt_token}`
- `Content-Type: application/json`

**Request Body:**
```json
{
  "searchKeyword": "tranh chấp đất đai",
  "searchModule": "LEGAL_DOCUMENT",
  "searchType": "ADVANCED",
  "filters": {
    "category": "Dân sự",
    "documentType": "Nghị định",
    "year": "2024"
  },
  "resultCount": 18,
  "executionTime": 245
}
```

**Response:**
```json
{
  "success": true,
  "status": 201,
  "message": "Lưu lịch sử tìm kiếm thành công",
  "data": {
    "id": 16,
    "userId": 2,
    "searchKeyword": "tranh chấp đất đai",
    "searchModule": "LEGAL_DOCUMENT",
    "searchType": "ADVANCED",
    "filters": {
      "category": "Dân sự",
      "documentType": "Nghị định",
      "year": "2024"
    },
    "resultCount": 18,
    "executionTime": 245,
    "searchedAt": "2024-01-15 11:45:00"
  }
}
```

## **Lấy từ khóa tìm kiếm phổ biến**

### **GET** `/api/search-history/popular-keywords`
Lấy danh sách từ khóa được tìm kiếm nhiều nhất (không cần authentication).

**Query Parameters:**
- `limit` (default: 10): Số lượng từ khóa trả về (tối đa 100)
- `period` (default: "all_time"): Thời gian thống kê ("today", "week", "month", "all_time")

**Example:** `/api/search-history/popular-keywords?limit=5&period=week`

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy từ khóa phổ biến thành công",
  "data": {
    "keywords": [
      {
        "keyword": "hợp đồng lao động",
        "search_count": 156,
        "unique_users": 45,
        "average_results": 22.5,
        "percentage": 18.7
      },
      {
        "keyword": "luật giao thông",
        "search_count": 142,
        "unique_users": 38,
        "average_results": 31.2,
        "percentage": 17.1
      },
      {
        "keyword": "bảo hiểm xã hội",
        "search_count": 98,
        "unique_users": 28,
        "average_results": 14.8,
        "percentage": 11.8
      }
    ],
    "period": "week",
    "total_count": 5,
    "generated_at": "2024-01-15T12:00:00"
  }
}
```

## **Lấy thống kê tìm kiếm cá nhân**

### **GET** `/api/search-history/user-statistics`
Lấy thống kê chi tiết về hoạt động tìm kiếm của người dùng (cần authentication).

**Headers:** `Authorization: Bearer {jwt_token}`

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy thống kê tìm kiếm cá nhân thành công",
  "data": {
    "total_searches": 45,
    "unique_users": 1,
    "searches_today": 3,
    "searches_this_week": 12,
    "searches_this_month": 28,
    "popular_keywords": {
      "hợp đồng lao động": 8,
      "luật giao thông": 6,
      "tranh chấp đất đai": 4
    },
    "search_by_module": {
      "LEGAL_DOCUMENT": 32,
      "LAWYER": 8,
      "FORUM": 5
    },
    "search_by_type": {
      "GENERAL": 28,
      "ADVANCED": 12,
      "CATEGORY": 5
    },
    "average_execution_time_ms": 167.5,
    "total_results_found": 812,
    "average_results_per_search": 18.04
  }
}
```

## **Lấy thống kê tìm kiếm toàn hệ thống**

### **GET** `/api/search-history/system-statistics`
Lấy thống kê tổng quan về hoạt động tìm kiếm trên toàn hệ thống (không cần authentication).

**Query Parameters:**
- `days` (default: 30): Thống kê trong số ngày gần nhất

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy thống kê hệ thống thành công",
  "data": {
    "total_searches": 12450,
    "unique_users": 856,
    "searches_today": 234,
    "searches_this_week": 1890,
    "searches_this_month": 7234,
    "popular_keywords": {
      "hợp đồng lao động": 890,
      "luật giao thông": 567,
      "bảo hiểm xã hội": 445
    },
    "search_by_module": {
      "LEGAL_DOCUMENT": 8765,
      "LAWYER": 2234,
      "FORUM": 1123,
      "APPOINTMENT": 234,
      "USER": 94
    },
    "search_by_type": {
      "GENERAL": 7890,
      "ADVANCED": 3456,
      "CATEGORY": 890,
      "FILTER": 214
    },
    "average_execution_time_ms": 156.7,
    "total_results_found": 286350,
    "average_results_per_search": 23.0
  }
}
```

## **Lấy gợi ý từ khóa**

### **GET** `/api/search-history/history-suggestions`
Lấy gợi ý từ khóa dựa trên lịch sử tìm kiếm cá nhân (cần authentication).

**Headers:** `Authorization: Bearer {jwt_token}`

**Query Parameters:**
- `keyword` (required): Từ khóa để tìm gợi ý
- `limit` (default: 10): Số lượng gợi ý (tối đa 20)

**Example:** `/api/search-history/history-suggestions?keyword=hợp&limit=5`

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy gợi ý từ khóa thành công",
  "data": [
    "hợp đồng lao động",
    "hợp đồng thuê nhà",
    "hợp đồng mua bán",
    "hợp tác xã",
    "hợp đồng dịch vụ"
  ]
}
```

## **Xóa lịch sử tìm kiếm cụ thể**

### **DELETE** `/api/search-history/{id}`
Xóa một bản ghi lịch sử tìm kiếm cụ thể (cần authentication).

**Headers:** `Authorization: Bearer {jwt_token}`

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Xóa lịch sử tìm kiếm thành công"
}
```

## **Xóa toàn bộ lịch sử tìm kiếm**

### **DELETE** `/api/search-history/clear`
Xóa toàn bộ lịch sử tìm kiếm của người dùng (cần authentication).

**Headers:** `Authorization: Bearer {jwt_token}`

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Xóa lịch sử tìm kiếm thành công",
  "data": {
    "deleted_count": 23,
    "success": true,
    "message": "Successfully deleted 23 search history records",
    "deleted_at": "2024-01-15T12:30:00"
  }
}
```

---

### **Enum Values:**

#### **SearchModule** - Các module tìm kiếm:
- `LEGAL_DOCUMENT`: Tìm kiếm văn bản pháp luật
- `LAWYER`: Tìm kiếm luật sư  
- `FORUM`: Tìm kiếm forum/Q&A
- `APPOINTMENT`: Tìm kiếm lịch hẹn
- `USER`: Tìm kiếm người dùng

#### **SearchType** - Loại tìm kiếm:
- `GENERAL`: Tìm kiếm chung theo keyword
- `ADVANCED`: Tìm kiếm nâng cao với nhiều filters
- `FILTER`: Tìm kiếm theo bộ lọc cụ thể
- `CATEGORY`: Tìm kiếm theo danh mục
- `TRENDING`: Xem nội dung phổ biến
- `BY_ID`: Truy cập trực tiếp theo ID
- `AUTOCOMPLETE`: Auto-suggest/completion

### **Validation Rules:**
- `searchKeyword`: Không được trống, tối đa 500 ký tự
- `searchModule` và `searchType`: Bắt buộc
- `limit` cho popular keywords: 1-100
- `limit` cho suggestions: 1-20
- Các filter trong `SearchHistoryFilterRequest` đều optional nhưng có validation riêng
```

---

# 7. 🛠️ **Admin Cleanup APIs**

## **Lấy cấu hình cleanup job**

### **GET** `/api/admin/cleanup/search-history/config`
Lấy thông tin cấu hình của scheduled cleanup job (cần quyền ADMIN).

**Headers:** 
- `Authorization: Bearer {admin_jwt_token}`

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy cấu hình cleanup thành công",
  "data": {
    "enabled": true,
    "retentionDays": 30,
    "batchSize": 1000
  }
}
```

## **Lấy thống kê cleanup**

### **GET** `/api/admin/cleanup/search-history/stats`
Lấy thống kê chi tiết về cleanup (cần quyền ADMIN).

**Headers:** 
- `Authorization: Bearer {admin_jwt_token}`

**Query Parameters:**
- `retentionDays` (default: 30): Số ngày để tính toán thống kê

**Example:** `/api/admin/cleanup/search-history/stats?retentionDays=30`

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy thống kê cleanup thành công",
  "data": {
    "totalRecords": 125000,
    "oldRecords": 45000,
    "recordsToKeep": 80000,
    "retentionDays": 30,
    "cutoffDate": "2024-10-23T00:00:00",
    "cleanupPercentage": 36.0,
    "estimatedTotalSizeKB": 62500.0,
    "estimatedOldSizeKB": 22500.0,
    "status": "SUCCESS"
  }
}
```

## **Thực hiện cleanup thủ công**

### **POST** `/api/admin/cleanup/search-history/manual`
Chạy cleanup job thủ công ngay lập tức (cần quyền ADMIN).

**Headers:** 
- `Authorization: Bearer {admin_jwt_token}`

**Query Parameters:**
- `retentionDays` (optional): Số ngày retention tùy chỉnh

**Example:** `/api/admin/cleanup/search-history/manual?retentionDays=60`

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Manual cleanup hoàn thành",
  "data": {
    "deletedRecords": 23456,
    "retentionDaysUsed": 60,
    "message": "Đã xóa 23456 records cũ hơn 60 ngày"
  }
}
```

## **Kiểm tra dry-run cleanup**

### **GET** `/api/admin/cleanup/search-history/dry-run`
Kiểm tra số lượng records sẽ bị xóa mà không thực sự xóa (cần quyền ADMIN).

**Headers:** 
- `Authorization: Bearer {admin_jwt_token}`

**Query Parameters:**
- `retentionDays` (default: 30): Số ngày để tính toán

**Example:** `/api/admin/cleanup/search-history/dry-run?retentionDays=45`

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Dry-run cleanup thành công",
  "data": {
    "totalRecords": 125000,
    "recordsToDelete": 67000,
    "recordsToKeep": 58000,
    "retentionDays": 45,
    "deletePercentage": 53.6,
    "message": "Sẽ xóa 67000/125000 records (53.6%) cũ hơn 45 ngày"
  }
}
```

## **Kiểm tra trạng thái cleanup**

### **GET** `/api/admin/cleanup/search-history/health`
Kiểm tra tình trạng database và đề xuất cleanup (cần quyền ADMIN).

**Headers:** 
- `Authorization: Bearer {admin_jwt_token}`

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Kiểm tra health thành công",
  "data": {
    "cleanupEnabled": true,
    "totalRecords": 125000,
    "oldRecords": 45000,
    "recommendCleanup": true,
    "status": "NEEDS_CLEANUP",
    "message": "Có 45000 records cũ, nên chạy cleanup"
  }
}
```

### **Trạng thái Health:**
- `HEALTHY`: Database trong tình trạng tốt
- `NEEDS_CLEANUP`: Có nhiều records cũ, nên cleanup
- `ERROR`: Có lỗi khi kiểm tra

---

# 8. ❌ **Error Handling**

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

# 9. 🧪 **Testing Examples**

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

### **6. Search History Management Flow**
```bash
# 1. Save search history after performing search
POST /api/search-history
Headers: Authorization: Bearer {jwt_token}
{
  "searchKeyword": "hợp đồng lao động",
  "searchModule": "LEGAL_DOCUMENT",
  "filters": {
    "category": "Lao động",
    "documentType": "Luật"
  },
  "resultCount": 24,
  "executionTime": 156
}

# 2. Get user search history
GET /api/search-history?searchModule=LEGAL_DOCUMENT&page=0&size=10
Headers: Authorization: Bearer {jwt_token}

# 3. Get popular keywords
GET /api/search-history/popular-keywords?searchModule=LEGAL_DOCUMENT&limit=5&days=7

# 4. Get user statistics
GET /api/search-history/user-statistics?days=30
Headers: Authorization: Bearer {jwt_token}

# 5. Get system statistics
GET /api/search-history/system-statistics?days=30

# 6. Delete specific search history
DELETE /api/search-history/15
Headers: Authorization: Bearer {jwt_token}

# 7. Clear all search history for a module
DELETE /api/search-history/clear?searchModule=LEGAL_DOCUMENT&olderThanDays=30
Headers: Authorization: Bearer {jwt_token}
```

### **7. Profile Management Flow**
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

### **9. Admin Cleanup Management Flow**
```bash
# 1. Check cleanup health status
GET /api/admin/cleanup/search-history/health
Headers: Authorization: Bearer {admin_jwt_token}

# 2. Get cleanup configuration
GET /api/admin/cleanup/search-history/config
Headers: Authorization: Bearer {admin_jwt_token}

# 3. Check cleanup statistics
GET /api/admin/cleanup/search-history/stats?retentionDays=30
Headers: Authorization: Bearer {admin_jwt_token}

# 4. Dry-run cleanup to see what would be deleted
GET /api/admin/cleanup/search-history/dry-run?retentionDays=45
Headers: Authorization: Bearer {admin_jwt_token}

# 5. Manual cleanup execution
POST /api/admin/cleanup/search-history/manual?retentionDays=60
Headers: Authorization: Bearer {admin_jwt_token}
```

### **10. Lawyer Registration Flow**
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

### **Search History Operations**
```bash
# Save search history
curl -X POST http://localhost:8080/api/search-history \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "searchKeyword": "hợp đồng lao động",
    "searchModule": "LEGAL_DOCUMENT",
    "filters": {
      "category": "Lao động"
    },
    "resultCount": 24,
    "executionTime": 156
  }'

# Get user search history
curl -X GET "http://localhost:8080/api/search-history?searchModule=LEGAL_DOCUMENT&page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get popular keywords
curl -X GET "http://localhost:8080/api/search-history/popular-keywords?limit=5&days=7"

# Get user statistics
curl -X GET "http://localhost:8080/api/search-history/user-statistics?days=30" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Delete specific search history
curl -X DELETE http://localhost:8080/api/search-history/15 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Clear old search history
curl -X DELETE "http://localhost:8080/api/search-history/clear?searchModule=LEGAL_DOCUMENT&olderThanDays=30" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 📞 **Support & Contact**

- **Technical Support**: dev-team@legalsystem.com
- **API Issues**: api-support@legalsystem.com  
- **Documentation**: https://docs.legalsystem.com

**Last Updated:** November 22, 2024  
**API Version:** v1.0  
**Documentation Version:** 1.0