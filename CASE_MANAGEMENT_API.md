# 📂 **CASE MANAGEMENT API DOCUMENTATION**
**(Module Quản lý Vụ án - Phụ trách: Hoàng)**

---

## 🔗 **Base Configuration**
- **Base URL**: `/api/cases`
- **Authentication**: Yêu cầu Header `Authorization: Bearer {jwt_token}` cho tất cả các request.

---

## 1. 📝 **Tạo Vụ Án Mới**

Người dùng (Citizen) tạo hồ sơ vụ án mới để nhờ luật sư tư vấn hoặc theo dõi.

### **POST** `/api/cases`

**Request Body:**
```json
{
  "title": "Tranh chấp đất đai tại xã A",
  "description": "Nội dung chi tiết vụ việc: Hàng xóm lấn chiếm 2m đất...",
  "serviceType": "CONSULTATION",
  "lawyerId": 5,                   // ID luật sư muốn thuê (nếu có)
  "budget": 5000000                // Ngân sách dự kiến (nếu có)
}
Response (201 Created):

JSON

{
  "success": true,
  "message": "Tạo vụ án thành công",
  "data": {
    "id": 101,
    "title": "Tranh chấp đất đai tại xã A",
    "status": "OPEN",
    "createdAt": "2025-12-10T08:00:00Z"
  }
}
2. 🔍 Lấy Chi Tiết Vụ Án
Xem thông tin đầy đủ của một vụ án cụ thể, bao gồm cả tiến độ và tài liệu đính kèm.

GET /api/cases/{id}
Parameters:

id (path): ID của vụ án (Ví dụ: 101)

Response (200 OK):

JSON

{
  "success": true,
  "message": "Lấy thông tin vụ án thành công",
  "data": {
    "id": 101,
    "title": "Tranh chấp đất đai tại xã A",
    "description": "Nội dung chi tiết vụ việc...",
    "status": "IN_PROGRESS",
    "lawyerName": "Nguyễn Văn Luật",
    "documents": [
      {
        "id": 1,
        "fileName": "So_do.jpg",
        "fileUrl": "http://localhost:8080/uploads/..."
      }
    ],
    "progressUpdates": [
      {
        "stage": "THU_LY",
        "content": "Tòa án đã thụ lý hồ sơ",
        "updatedAt": "2025-12-11T09:00:00Z"
      }
    ]
  }
}
3. 📈 Cập Nhật Tiến Độ Vụ Án
Cập nhật trạng thái hoặc các mốc quan trọng mới của vụ án.

POST /api/cases/{id}/updates
Request Body:

JSON

{
  "content": "Đã hoàn tất nộp án phí sơ thẩm",
  "stage": "PREPARING_TRIAL",
  "note": "Khách hàng cần giữ lại biên lai"
}
Response (200 OK):

JSON

{
  "success": true,
  "message": "Cập nhật tiến độ thành công",
  "data": {
    "id": 55,
    "caseId": 101,
    "content": "Đã hoàn tất nộp án phí sơ thẩm",
    "updatedAt": "2025-12-12T10:30:00Z"
  }
}
4. 📎 Upload Tài Liệu Vụ Án
Tải lên các file bằng chứng, giấy tờ liên quan vào hồ sơ vụ án.

POST /api/cases/{id}/documents
Headers:

Content-Type: multipart/form-data

Form Data:

file: (File object - .pdf, .jpg, .png, .docx)

Response (200 OK):

JSON

{
  "success": true,
  "message": "Upload tài liệu thành công",
  "data": "http://localhost:8080/uploads/cases/101/bang_chung_v1.pdf"
}