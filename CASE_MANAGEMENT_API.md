📂 CASE MANAGEMENT API DOCUMENTATION

(Module Quản lý Vụ Án – Phụ trách: Hoàng)

🔗 Base Configuration

Base URL: /api/cases

Authentication: Header Authorization: Bearer {jwt_token}

1. 📝 Tạo Vụ Án Mới

POST /api/cases

Request Body:
{
"title": "Tranh chấp đất đai tại xã A",
"description": "Nội dung chi tiết vụ việc: Hàng xóm lấn chiếm 2m đất...",
"serviceType": "CONSULTATION",
"lawyerId": 5,
"budget": 5000000
}

Response (201 Created):
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

GET /api/cases/{id}

Path Parameter:
id – ID vụ án

Response (200 OK):
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

POST /api/cases/{id}/updates

Request Body:
{
"content": "Đã hoàn tất nộp án phí sơ thẩm",
"stage": "PREPARING_TRIAL",
"note": "Khách hàng cần giữ lại biên lai"
}

Response (200 OK):
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

POST /api/cases/{id}/documents

Headers:
Content-Type: multipart/form-data

Form Data:
file – PDF / JPG / PNG / DOCX

Response (200 OK):
{
"success": true,
"message": "Upload tài liệu thành công",
"data": "http://localhost:8080/uploads/cases/101/bang_chung_v1.pdf
"
}