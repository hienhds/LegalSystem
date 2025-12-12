import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { caseService } from "../services/caseService";
import Layout from "../components/Layout";

export default function CreateCase() {
  const navigate = useNavigate();
  const location = useLocation();
  // Nhận lawyerId nếu được chuyển từ trang Chi tiết luật sư
  const initialLawyerId = location.state?.lawyerId || "";
  
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    lawyerId: initialLawyerId,
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await caseService.createCase(formData);
      if (res.data.success) {
        alert("Tạo vụ án thành công!");
        // Chuyển hướng đến trang chi tiết vụ án vừa tạo
        navigate(`/cases/${res.data.data.caseId}`);
      }
    } catch (error) {
      console.error(error);
      alert("Lỗi khi tạo vụ án: " + (error.response?.data?.message || "Vui lòng thử lại"));
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout>
      <div className="max-w-2xl mx-auto p-6 bg-white dark:bg-slate-900 rounded-xl shadow-md mt-10">
        <h1 className="text-2xl font-bold mb-6 text-slate-900 dark:text-white">Tạo Hồ Sơ Vụ Án Mới</h1>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium mb-1 dark:text-slate-300">Tiêu đề vụ việc</label>
            <input
              type="text"
              required
              className="w-full p-2 border rounded-lg dark:bg-slate-800 dark:border-slate-700"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              placeholder="Vd: Tranh chấp đất đai tại..."
            />
          </div>
          
          <div>
            <label className="block text-sm font-medium mb-1 dark:text-slate-300">ID Luật sư phụ trách</label>
            <input
              type="number"
              required
              className="w-full p-2 border rounded-lg dark:bg-slate-800 dark:border-slate-700"
              value={formData.lawyerId}
              onChange={(e) => setFormData({ ...formData, lawyerId: e.target.value })}
              placeholder="Nhập ID luật sư"
            />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1 dark:text-slate-300">Mô tả chi tiết</label>
            <textarea
              required
              rows="5"
              className="w-full p-2 border rounded-lg dark:bg-slate-800 dark:border-slate-700"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="Mô tả nội dung vụ việc..."
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-primary text-white py-2 rounded-lg hover:bg-blue-600 transition disabled:opacity-50"
          >
            {loading ? "Đang xử lý..." : "Tạo Hồ Sơ"}
          </button>
        </form>
      </div>
    </Layout>
  );
}