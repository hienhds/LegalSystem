import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { caseService } from "../services/caseService";
import Layout from "../components/Layout";
import useUserProfile from "../hooks/useUserProfile"; // Để check quyền user

export default function CaseDetail() {
  const { id } = useParams();
  const { user } = useUserProfile();
  const [caseData, setCaseData] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // State cho form update tiến độ
  const [updateForm, setUpdateForm] = useState({ title: "", description: "", status: "" });
  const [showUpdateModal, setShowUpdateModal] = useState(false);

  // Load dữ liệu
  const fetchCaseDetail = async () => {
    try {
      const res = await caseService.getCaseDetail(id);
      if (res.data.success) {
        setCaseData(res.data.data);
      }
    } catch (error) {
      console.error("Failed to load case:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCaseDetail();
  }, [id]);

  // Xử lý upload file
  const handleFileUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    try {
      await caseService.uploadDocument(id, file);
      alert("Upload tài liệu thành công!");
      fetchCaseDetail(); // Reload lại data
    } catch (error) {
      alert("Lỗi upload: " + error.message);
    }
  };

  // Xử lý cập nhật tiến độ
  const handleUpdateProgress = async (e) => {
    e.preventDefault();
    try {
      // Nếu không chọn status mới, giữ nguyên status cũ (hoặc không gửi field này)
      const payload = { ...updateForm };
      if (!payload.status) delete payload.status;

      await caseService.updateProgress(id, payload);
      alert("Cập nhật tiến độ thành công!");
      setShowUpdateModal(false);
      setUpdateForm({ title: "", description: "", status: "" });
      fetchCaseDetail();
    } catch (error) {
      alert("Lỗi cập nhật: " + error.message);
    }
  };

  if (loading) return <Layout><div>Đang tải...</div></Layout>;
  if (!caseData) return <Layout><div>Không tìm thấy vụ án</div></Layout>;

  // Kiểm tra xem user hiện tại có phải là Luật sư của vụ án này không
  // Lưu ý: Logic này phụ thuộc vào cấu trúc user object của bạn
  const isLawyerOfThisCase = user?.role === "LAWYER" && user?.fullName === caseData.lawyerName; 

  return (
    <Layout>
      <div className="max-w-6xl mx-auto p-4 space-y-6">
        {/* Header Thông tin chung */}
        <div className="bg-white dark:bg-slate-900 p-6 rounded-xl shadow-sm border dark:border-slate-800">
          <div className="flex justify-between items-start">
            <div>
              <h1 className="text-3xl font-bold text-slate-800 dark:text-white mb-2">{caseData.title}</h1>
              <span className={`px-3 py-1 rounded-full text-sm font-semibold ${
                caseData.status === 'COMPLETED' ? 'bg-green-100 text-green-700' : 
                caseData.status === 'IN_PROGRESS' ? 'bg-blue-100 text-blue-700' : 'bg-gray-100 text-gray-700'
              }`}>
                {caseData.status}
              </span>
            </div>
            <div className="text-right text-sm text-slate-500">
              <p>Ngày tạo: {new Date(caseData.createdAt).toLocaleDateString()}</p>
              <p>Mã hồ sơ: #{caseData.caseId}</p>
            </div>
          </div>
          
          <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-4 bg-slate-50 dark:bg-slate-800 rounded-lg">
              <p className="text-sm text-slate-500">Khách hàng</p>
              <p className="font-medium text-lg">{caseData.clientName}</p>
            </div>
            <div className="p-4 bg-slate-50 dark:bg-slate-800 rounded-lg">
              <p className="text-sm text-slate-500">Luật sư phụ trách</p>
              <p className="font-medium text-lg">{caseData.lawyerName}</p>
            </div>
          </div>
          
          <div className="mt-6">
            <h3 className="font-semibold mb-2">Mô tả vụ việc:</h3>
            <p className="text-slate-700 dark:text-slate-300 leading-relaxed bg-slate-50 dark:bg-slate-800 p-4 rounded-lg">
              {caseData.description}
            </p>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Cột trái: Tiến độ vụ án (Timeline) */}
          <div className="lg:col-span-2 bg-white dark:bg-slate-900 p-6 rounded-xl shadow-sm border dark:border-slate-800">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-bold">Tiến độ xử lý</h2>
              {isLawyerOfThisCase && (
                <button 
                  onClick={() => setShowUpdateModal(true)}
                  className="bg-primary text-white px-4 py-2 rounded-lg text-sm hover:bg-blue-600"
                >
                  + Cập nhật tiến độ
                </button>
              )}
            </div>

            <div className="space-y-6 relative border-l-2 border-slate-200 dark:border-slate-700 ml-3 pl-6">
              {caseData.updates && caseData.updates.map((update) => (
                <div key={update.updateId} className="relative">
                  <div className="absolute -left-[31px] top-0 w-4 h-4 rounded-full bg-blue-500 border-4 border-white dark:border-slate-900"></div>
                  <div className="mb-1 flex justify-between items-center">
                    <h3 className="font-semibold text-lg">{update.title}</h3>
                    <span className="text-xs text-slate-500">{new Date(update.createdAt).toLocaleDateString()}</span>
                  </div>
                  <p className="text-slate-600 dark:text-slate-400 text-sm mb-2">{update.description}</p>
                  <p className="text-xs text-slate-400 italic">Cập nhật bởi: {update.createdByName}</p>
                </div>
              ))}
              {(!caseData.updates || caseData.updates.length === 0) && (
                <p className="text-slate-500 italic">Chưa có cập nhật nào.</p>
              )}
            </div>
          </div>

          {/* Cột phải: Tài liệu */}
          <div className="bg-white dark:bg-slate-900 p-6 rounded-xl shadow-sm border dark:border-slate-800 h-fit">
            <h2 className="text-xl font-bold mb-4">Tài liệu hồ sơ</h2>
            
            <div className="space-y-3 mb-6">
              {caseData.documents && caseData.documents.map((doc) => (
                <a 
                  key={doc.docId} 
                  href={`http://localhost:8080${doc.fileUrl}`} 
                  target="_blank" 
                  rel="noreferrer"
                  className="flex items-center p-3 rounded-lg border hover:bg-slate-50 dark:hover:bg-slate-800 transition"
                >
                  <span className="material-symbols-outlined text-red-500 mr-3">description</span>
                  <div className="overflow-hidden">
                    <p className="text-sm font-medium truncate">{doc.fileName}</p>
                    <p className="text-xs text-slate-500">
                      {new Date(doc.uploadedAt).toLocaleDateString()} • {doc.uploadedByName}
                    </p>
                  </div>
                </a>
              ))}
              {(!caseData.documents || caseData.documents.length === 0) && (
                <p className="text-sm text-slate-500">Chưa có tài liệu nào.</p>
              )}
            </div>

            {/* Nút upload tài liệu */}
            <div className="border-t pt-4">
              <label className="block w-full cursor-pointer">
                <span className="sr-only">Chọn tài liệu</span>
                <input 
                  type="file" 
                  className="block w-full text-sm text-slate-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
                  onChange={handleFileUpload}
                />
              </label>
            </div>
          </div>
        </div>
      </div>

      {/* Modal Cập nhật tiến độ */}
      {showUpdateModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
          <div className="bg-white dark:bg-slate-900 rounded-xl p-6 w-full max-w-md">
            <h3 className="text-xl font-bold mb-4">Cập nhật tiến độ mới</h3>
            <form onSubmit={handleUpdateProgress} className="space-y-4">
              <input
                type="text"
                placeholder="Tiêu đề (VD: Đã nộp đơn lên tòa)"
                className="w-full p-2 border rounded"
                value={updateForm.title}
                onChange={e => setUpdateForm({...updateForm, title: e.target.value})}
                required
              />
              <textarea
                placeholder="Mô tả chi tiết..."
                className="w-full p-2 border rounded"
                rows="3"
                value={updateForm.description}
                onChange={e => setUpdateForm({...updateForm, description: e.target.value})}
              />
              <select
                className="w-full p-2 border rounded"
                value={updateForm.status}
                onChange={e => setUpdateForm({...updateForm, status: e.target.value})}
              >
                <option value="">-- Giữ nguyên trạng thái cũ --</option>
                <option value="IN_PROGRESS">Đang xử lý</option>
                <option value="PENDING_APPROVAL">Chờ duyệt</option>
                <option value="COMPLETED">Hoàn thành</option>
                <option value="CANCELLED">Đã hủy</option>
              </select>
              <div className="flex justify-end gap-2 pt-2">
                <button type="button" onClick={() => setShowUpdateModal(false)} className="px-4 py-2 text-slate-600">Hủy</button>
                <button type="submit" className="px-4 py-2 bg-primary text-white rounded">Lưu cập nhật</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </Layout>
  );
}