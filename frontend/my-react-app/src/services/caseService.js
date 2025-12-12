import axiosInstance from "../utils/axiosInstance";

export const caseService = {
  // 1. Tạo vụ án mới
  createCase: async (data) => {
    // data: { title, description, lawyerId }
    return await axiosInstance.post("/api/cases", data);
  },

  // 2. Lấy chi tiết vụ án
  getCaseDetail: async (caseId) => {
    return await axiosInstance.get(`/api/cases/${caseId}`);
  },

  // 3. Cập nhật tiến độ (Chỉ dành cho Luật sư)
  updateProgress: async (caseId, data) => {
    // data: { title, description, status }
    return await axiosInstance.post(`/api/cases/${caseId}/updates`, data);
  },

  // 4. Upload tài liệu vụ án
  uploadDocument: async (caseId, file) => {
    const formData = new FormData();
    formData.append("file", file);
    return await axiosInstance.post(`/api/cases/${caseId}/documents`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },
  
  // (Optional) Lấy danh sách vụ án của user hiện tại
  // Lưu ý: Bạn cần đảm bảo Backend có endpoint này (vd: /api/cases/my-cases)
  // 5. Lấy danh sách vụ án của tôi
  getMyCases: async (page = 0, size = 10) => {
    return await axiosInstance.get(`/api/cases?page=${page}&size=${size}`);
  }
};