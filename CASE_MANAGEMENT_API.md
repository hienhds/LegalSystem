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