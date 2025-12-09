# FindLawyer API Documentation

## Tổng quan

Tài liệu này mô tả tất cả các API đã được bổ sung và cải thiện cho trang **FindLawyer**.

---

## 📋 Danh sách APIs đã cải thiện

### ✅ **1. Search & Filter APIs**

#### **GET /api/search/lawyers**
Tìm kiếm và lọc luật sư với nhiều tiêu chí.

**Query Parameters:**
- `keyword` (string, optional): Tìm theo tên, email, license, bio
- `specializationIds` (array, optional): Lọc theo chuyên ngành (vd: `1,2,3`)
- `barAssociationId` (long, optional): Lọc theo Đoàn Luật Sư (địa phương)
- `minYearsOfExp` (integer, optional): Số năm kinh nghiệm tối thiểu
- `maxYearsOfExp` (integer, optional): Số năm kinh nghiệm tối đa
- `minRating` (double, optional): Đánh giá tối thiểu (0.0 - 5.0)
- `page` (integer, default: 0): Số trang (0-indexed)
- `size` (integer, default: 10): Kích thước trang
- `sortBy` (string, default: "lawyerId"): Trường sắp xếp
- `sortDir` (string, default: "DESC"): Hướng sắp xếp (ASC/DESC)

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Tìm thấy 15 luật sư phù hợp",
  "data": {
    "content": [
      {
        "lawyerId": 1,
        "fullName": "Nguyễn Văn An",
        "email": "an@example.com",
        "phoneNumber": "0123456789",
        "avatarUrl": "https://...",
        "barLicenseId": "LS123456",
        "barAssociationName": "Đoàn Luật Sư TP.HCM",
        "verificationStatus": "APPROVED",
        "certificateUrl": "https://...",
        "yearsOfExp": 10,
        "bio": "Chuyên về luật dân sự...",
        "officeAddress": "123 Nguyễn Huệ, Q1, TPHCM",
        "specializations": ["Dân sự", "Hình sự"],
        "createdAt": "2024-01-01T00:00:00",
        "averageRating": 4.5,
        "reviewCount": 25
      }
    ],
    "totalElements": 15,
    "totalPages": 2,
    "size": 10,
    "number": 0
  },
  "timestamp": "2025-11-30T..."
}
```

---

### ✅ **2. Lawyer Details API**

#### **GET /api/lawyers/{id}**
Lấy thông tin chi tiết của một luật sư.

**Path Parameter:**
- `id` (long): ID của luật sư

**Response:** Giống như format trong search results, nhưng chỉ trả về 1 object.

---

### ✅ **3. Statistics API**

#### **GET /api/lawyers/stats**
Lấy thống kê tổng quan về luật sư trong hệ thống.

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Lấy thống kê luật sư thành công",
  "data": {
    "totalLawyers": 150,
    "activeLawyers": 120,
    "verifiedLawyers": 120,
    "averageRating": 4.3,
    "totalReviews": 543,
    "satisfactionRate": 87.5,
    "totalAppointments": 1200,
    "completedAppointments": 1050
  },
  "timestamp": "2025-11-30T..."
}
```

**Giải thích:**
- `totalLawyers`: Tổng số luật sư đã đăng ký
- `activeLawyers`: Số luật sư đã được xác minh (APPROVED)
- `verifiedLawyers`: Giống activeLawyers
- `averageRating`: Đánh giá trung bình từ tất cả appointments
- `totalReviews`: Tổng số đánh giá
- `satisfactionRate`: % appointments hoàn thành thành công
- `totalAppointments`: Tổng số cuộc hẹn
- `completedAppointments`: Số cuộc hẹn đã hoàn thành

---

### ✅ **4. Specializations API**

#### **GET /api/specialization**
Lấy danh sách tất cả chuyên ngành pháp lý.

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "specId": 1,
      "specName": "Dân sự"
    },
    {
      "specId": 2,
      "specName": "Hình sự"
    },
    {
      "specId": 3,
      "specName": "Lao động"
    }
  ]
}
```

---

### ✅ **5. Bar Associations API**

#### **GET /api/bar-association**
Lấy danh sách tất cả Đoàn Luật Sư (địa phương).

**Response:**
```json
{
  "success": true,
  "status": 200,
  "message": "Thành công",
  "data": [
    {
      "barAssociationId": 1,
      "associationName": "Đoàn Luật Sư TP. Hồ Chí Minh",
      "address": "123 Đường ABC, Q1",
      "contactEmail": "contact@hcmbar.org",
      "phoneNumber": "0281234567"
    }
  ]
}
```

---

### ✅ **6. Popular Lawyers API**

#### **GET /api/search/popular-lawyers**
Lấy danh sách luật sư phổ biến/nổi bật.

**Query Parameters:**
- `limit` (integer, default: 10): Số lượng luật sư trả về

**Response:** Danh sách LawyerListResponse (không có pagination).

---

### ✅ **7. Search Features**

#### **GET /api/search/suggestions**
Gợi ý tự động khi người dùng gõ tìm kiếm.

**Query Parameters:**
- `query` (string): Từ khóa tìm kiếm

**Response:**
```json
{
  "success": true,
  "data": [
    "luật hà nội",
    "luật tphcm",
    "luật chuyên nghiệp",
    "luật kinh nghiệm",
    "luật uy tín"
  ]
}
```

#### **GET /api/search/trending**
Lấy các từ khóa tìm kiếm xu hướng.

**Query Parameters:**
- `limit` (integer, default: 10): Số lượng từ khóa

#### **POST /api/search/track-click**
Tracking khi user click vào profile luật sư.

**Query Parameters:**
- `lawyerId` (long): ID của luật sư được click

---

## 🔧 Các cải tiến đã thực hiện

### **1. FilterLawyerRequest DTO**
Đã thêm các fields mới:
- `specializationIds`: List<Long> - Lọc theo nhiều chuyên ngành
- `minYearsOfExp`: Integer - Kinh nghiệm tối thiểu
- `maxYearsOfExp`: Integer - Kinh nghiệm tối đa
- `minRating`: Double - Đánh giá tối thiểu

### **2. LawyerListResponse DTO**
Đã thêm các fields mới:
- `averageRating`: Double - Đánh giá trung bình từ appointments
- `reviewCount`: Long - Số lượng đánh giá
- `officeAddress`: String - Địa chỉ văn phòng

### **3. LawyerStatsResponse DTO** (Mới)
DTO mới cho thống kê tổng quan với các fields:
- totalLawyers, activeLawyers, verifiedLawyers
- averageRating, totalReviews
- satisfactionRate, totalAppointments, completedAppointments

### **4. LawyerRepository**
Đã thêm các methods:
- `findByIdWithDetails()`: Fetch lawyer với relations
- `getAverageRating()`: Tính rating trung bình
- `getReviewCount()`: Đếm số reviews

### **5. AppointmentRepository**
Đã thêm các statistics queries:
- `countReviewsByLawyerId()`
- `getAverageRatingByLawyerId()`
- `countCompletedAppointments()`
- `countTotalAppointments()`
- `getOverallAverageRating()`
- `countTotalReviews()`

### **6. LawyerService**
Đã thêm methods mới:
- `getLawyerById()`: Lấy chi tiết 1 luật sư với rating
- `getStats()`: Tính toán thống kê tổng quan
- Cải thiện `getAllLawyers()`: Thêm tính rating cho mỗi luật sư

### **7. LawyerSpec**
Đã cải thiện specification để support:
- Filter theo specializationIds (JOIN với lawyer_specialization)
- Filter theo yearsOfExp (min/max)
- Distinct results khi join

### **8. LawyerController**
Đã thêm endpoints mới:
- `GET /api/lawyers/{id}`
- `GET /api/lawyers/stats`

---

## 📊 Database Schema sử dụng

### **Appointments Table**
Rating được lưu trong bảng `appointments`:
- `rating` (int): Đánh giá từ 1-5
- `review_comment` (varchar): Nhận xét
- `status` (enum): COMPLETED appointments mới có rating

### **Lawyer Table**
- `lawyer_id`, `bar_license_id`, `bio`
- `years_of_exp`, `office_address`
- `verification_status`: PENDING, APPROVED, REJECTED

### **Lawyer_Specialization Table**
Many-to-many relationship giữa Lawyer và Specialization

---

## 🧪 Testing với Postman

File **FindLawyer_API.postman_collection.json** đã được tạo với:

### **Collection Structure:**
1. **Search & Filter**
   - Search Lawyers - Simple (GET)
   - Search Lawyers - Advanced (POST)

2. **Lawyer Details**
   - Get Lawyer by ID
   - Get Popular Lawyers

3. **Statistics**
   - Get Lawyer Stats

4. **Specializations & Locations**
   - Get All Specializations
   - Get All Bar Associations

5. **Search Features**
   - Get Search Suggestions
   - Get Trending Searches
   - Track Lawyer Click

6. **Authentication** (Optional)
   - Login (auto-saves accessToken)

### **Variables:**
- `baseUrl`: http://localhost:8080
- `accessToken`: Tự động lưu sau khi login

### **Cách sử dụng:**
1. Import file `FindLawyer_API.postman_collection.json` vào Postman
2. Đảm bảo backend đang chạy ở `localhost:8080`
3. Run các requests theo thứ tự:
   - Test GET Specializations (để biết specId)
   - Test GET Bar Associations (để biết barAssociationId)
   - Test Search Lawyers với filters
   - Test Get Lawyer by ID
   - Test Get Stats

---

## 📝 Lưu ý khi sử dụng

### **Pagination:**
- Tất cả search endpoints đều support pagination
- Page index bắt đầu từ 0
- Default size = 10

### **Filtering:**
- Có thể combine nhiều filters cùng lúc
- specializationIds có thể truyền nhiều values: `?specializationIds=1,2,3`
- Tất cả filters đều optional

### **Sorting:**
- sortBy có thể là: `lawyerId`, `createdAt`, `yearsOfExp`, `fullName`
- sortDir: `ASC` hoặc `DESC`

### **Rating:**
- Rating từ appointments table
- Chỉ appointments có status = COMPLETED mới được tính
- averageRating = 0.0 nếu chưa có review nào

---

## ✅ Checklist hoàn thành

- [x] Cải thiện FilterLawyerRequest với specializationIds, experience, rating
- [x] Thêm averageRating và reviewCount vào LawyerListResponse
- [x] Tạo API GET /api/lawyers/{id}
- [x] Tạo API GET /api/lawyers/stats
- [x] Cải thiện LawyerService với rating calculation
- [x] Update LawyerSpec để support new filters
- [x] Thêm queries vào AppointmentRepository
- [x] Update SearchController với new parameters
- [x] Export Postman Collection

---

## 🚀 Next Steps

Để tích hợp với Frontend (FindLawyer.jsx):

1. **Thay mock data bằng API calls:**
   ```javascript
   const fetchLawyers = async () => {
     const response = await axiosInstance.get('/api/search/lawyers', {
       params: {
         keyword: filters.keyword,
         specializationIds: filters.specialties.join(','),
         minYearsOfExp: getMinExp(filters.experience),
         minRating: filters.rating,
         page: pagination.page,
         size: pagination.size
       }
     });
     setLawyers(response.data.data.content);
     setPagination({...pagination, totalPages: response.data.data.totalPages});
   };
   ```

2. **Load specializations cho filter:**
   ```javascript
   const fetchSpecializations = async () => {
     const response = await axiosInstance.get('/api/specialization');
     // Map to filter options
   };
   ```

3. **Load stats cho hero section:**
   ```javascript
   const fetchStats = async () => {
     const response = await axiosInstance.get('/api/lawyers/stats');
     // Display in hero: totalLawyers, averageRating, satisfactionRate
   };
   ```

---

**Tạo bởi:** AI Assistant  
**Ngày:** 30/11/2025  
**Version:** 1.0
