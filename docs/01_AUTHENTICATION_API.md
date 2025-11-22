# 🔐 **AUTHENTICATION APIs**

## **Base Path:** `/api/auth`

Authentication module cung cấp các tính năng đăng nhập, đăng ký, và quản lý token.

---

## 📋 **API Endpoints**

### **1. Đăng nhập hệ thống**
```http
POST /api/auth/login
Content-Type: application/json

{
  "emailOrPhone": "user@example.com",
  "password": "password123"
}
```

**Mục đích:** Đăng nhập để nhận JWT token

**Request Body:**
- `emailOrPhone` (string, required): Email hoặc số điện thoại
- `password` (string, required): Mật khẩu

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Đăng nhập thành công",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "user": {
      "userId": 1,
      "email": "user@example.com",
      "fullName": "Nguyễn Văn A",
      "role": "CITIZEN"
    }
  }
}
```

**Response Error (401):**
```json
{
  "success": false,
  "status": 401,
  "message": "Email hoặc mật khẩu không chính xác",
  "data": null
}
```

---

### **2. Làm mới token**
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Mục đích:** Lấy access token mới khi token cũ hết hạn

**Request Body:**
- `refreshToken` (string, required): Refresh token từ lúc login

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Làm mới token thành công",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

---

### **3. Đăng xuất**
```http
POST /api/auth/logout
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Mục đích:** Hủy refresh token và đăng xuất

**Request Body:**
- `refreshToken` (string, required): Refresh token cần hủy

**Response Success (204):**
```json
{
  "success": true,
  "status": 204,
  "message": "Đăng xuất thành công",
  "data": null
}
```

---

### **4. Đăng ký tài khoản**
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "newuser@example.com",
  "phoneNumber": "0901234567",
  "password": "password123",
  "fullName": "Nguyễn Văn B",
  "dateOfBirth": "1990-05-15",
  "gender": "MALE",
  "address": "123 Đường ABC, Quận 1, TP.HCM"
}
```

**Mục đích:** Đăng ký tài khoản citizen mới

**Request Body:**
- `email` (string, required): Email (unique)
- `phoneNumber` (string, required): Số điện thoại (unique)  
- `password` (string, required): Mật khẩu (min 6 chars)
- `fullName` (string, required): Họ tên đầy đủ
- `dateOfBirth` (date, required): Ngày sinh (YYYY-MM-DD)
- `gender` (string, required): MALE/FEMALE/OTHER
- `address` (string, required): Địa chỉ

**Response Success (201):**
```json
{
  "success": true,
  "status": 201,
  "message": "Đăng ký thành công! Vui lòng kiểm tra email để xác thực.",
  "data": {
    "userId": 15,
    "email": "newuser@example.com",
    "fullName": "Nguyễn Văn B",
    "isEmailVerified": false,
    "verificationToken": "abc123xyz"
  }
}
```

---

### **5. Xác thực email**
```http
GET /api/auth/verify?token=abc123xyz
```

**Mục đích:** Xác thực email sau khi đăng ký

**Query Parameters:**
- `token` (string, required): Token xác thực từ email

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Xác thực email thành công. Tài khoản đã được kích hoạt!",
  "data": {
    "userId": 15,
    "email": "newuser@example.com",
    "isEmailVerified": true
  }
}
```

---

### **6. Quên mật khẩu**
```http
POST /api/auth/forgot-password
Content-Type: application/json

{
  "email": "user@example.com"
}
```

**Mục đích:** Gửi link reset mật khẩu qua email

**Request Body:**
- `email` (string, required): Email tài khoản cần reset

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Link reset mật khẩu đã được gửi đến email của bạn",
  "data": null
}
```

---

### **7. Reset mật khẩu**
```http
POST /api/auth/reset-password
Content-Type: application/json

{
  "token": "reset_token_from_email",
  "newPassword": "newpassword123"
}
```

**Mục đích:** Đổi mật khẩu mới với token từ email

**Request Body:**
- `token` (string, required): Token reset từ email
- `newPassword` (string, required): Mật khẩu mới

**Response Success (200):**
```json
{
  "success": true,
  "status": 200,
  "message": "Đổi mật khẩu thành công!",
  "data": null
}
```

---

## 🔒 **Authentication Headers**

Sau khi login thành công, sử dụng access token cho các API khác:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## ⚠️ **Lưu ý bảo mật**

1. **Access Token**: Hết hạn sau 1 giờ
2. **Refresh Token**: Hết hạn sau 7 ngày  
3. **Password**: Minimum 6 characters
4. **Rate Limiting**: 5 requests/minute cho login
5. **Email Verification**: Bắt buộc trước khi sử dụng hệ thống

---

## 📝 **User Roles**

- **CITIZEN**: Người dân - đặt lịch tư vấn
- **LAWYER**: Luật sư - nhận và xử lý lịch hẹn
- **ADMIN**: Quản trị viên - quản lý toàn hệ thống