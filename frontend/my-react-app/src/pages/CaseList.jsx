import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { caseService } from "../services/caseService";
import Layout from "../components/Layout";

export default function CaseList() {
  const [cases, setCases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null); // Thêm state lỗi
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCases = async () => {
      try {
        const res = await caseService.getMyCases();
        if (res.data.success) {
          setCases(res.data.data.content || []);
        }
      } catch (err) {
        console.error("Lỗi tải danh sách:", err);
        // Hiển thị thông báo lỗi thân thiện hơn thay vì crash
        setError("Không thể tải danh sách vụ án. Vui lòng thử lại sau.");
      } finally {
        setLoading(false);
      }
    };
    fetchCases();
  }, []);

  // ... (giữ nguyên hàm getStatusColor)
  const getStatusColor = (status) => {
      switch(status) {
        case 'IN_PROGRESS': return 'bg-blue-100 text-blue-800';
        case 'COMPLETED': return 'bg-green-100 text-green-800';
        case 'CANCELLED': return 'bg-red-100 text-red-800';
        default: return 'bg-gray-100 text-gray-800';
      }
  };

  return (
    <Layout>
      <div className="max-w-6xl mx-auto p-6">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-slate-800 dark:text-white">Hồ Sơ Vụ Án Của Tôi</h1>
          <Link to="/create-case" className="bg-primary text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-600 flex items-center gap-2">
            <span className="material-symbols-outlined">add</span>
            Tạo vụ án mới
          </Link>
        </div>

        {/* Hiển thị lỗi nếu có */}
        {error && (
          <div className="p-4 mb-4 text-red-700 bg-red-100 rounded-lg border border-red-400">
            {error} (Mã lỗi server: 500)
          </div>
        )}

        {loading ? (
          <div className="text-center py-10">Đang tải dữ liệu...</div>
        ) : cases.length === 0 && !error ? (
          <div className="text-center py-20 bg-white dark:bg-slate-900 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800">
            <span className="material-symbols-outlined text-6xl text-slate-300">folder_off</span>
            <p className="mt-4 text-slate-500 text-lg">Bạn chưa có hồ sơ vụ án nào.</p>
            <Link to="/create-case" className="text-primary mt-2 inline-block hover:underline font-medium">
              Tạo hồ sơ ngay
            </Link>
          </div>
        ) : (
          <div className="grid gap-4">
            {cases.map((c) => (
              <div 
                key={c.caseId} 
                onClick={() => navigate(`/cases/${c.caseId}`)}
                className="bg-white dark:bg-slate-900 p-5 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800 cursor-pointer hover:shadow-md transition-all flex justify-between items-center group"
              >
                <div>
                  <h3 className="text-lg font-bold text-slate-800 dark:text-slate-100 mb-1 group-hover:text-blue-600">{c.title}</h3>
                  <p className="text-sm text-slate-500 line-clamp-1">{c.description}</p>
                  <div className="mt-2 text-xs text-slate-400 flex gap-4">
                    <span className="flex items-center gap-1"><span className="material-symbols-outlined text-[14px]">person</span> {c.lawyerName || "Chưa có LS"}</span>
                    <span className="flex items-center gap-1"><span className="material-symbols-outlined text-[14px]">calendar_today</span> {new Date(c.createdAt).toLocaleDateString()}</span>
                  </div>
                </div>
                <div>
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusColor(c.status)}`}>
                    {c.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </Layout>
  );
}