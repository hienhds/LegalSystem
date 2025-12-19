import axiosInstance from "../utils/axiosInstance";

export const caseService = {
  // 1. Tạo vụ án mới
  createCase: async (data) => {
    // data: { title, description, clientId }
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
  
  // 5. LẤY DANH SÁCH VỤ ÁN CỦA TÔI (Đã thêm keyword)
  getMyCases: async (page = 0, size = 10, keyword = "") => {
    return await axiosInstance.get(`/api/cases`, {
      params: { 
        page, 
        size, 
        keyword // Truyền keyword vào đây
      }
    });
  }
};
//download document 
downloadDocument: async (caseId, docId) => {
    return await axiosInstance.get(`/cases/${caseId}/documents/${docId}/download`, {
        responseType: 'blob', // Quan trọng: báo cho axios biết server trả về file
    });
}