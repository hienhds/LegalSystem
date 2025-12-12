import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { caseService } from "../services/caseService";
import Layout from "../components/Layout";

export default function CaseList() {
  const [cases, setCases] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCases = async () => {
      try {
        const res = await caseService.getMyCases();
        if (res.data.success) {
          setCases(res.data.data.content);
        }
      } catch (error) {
        console.error("Lỗi tải danh sách:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchCases();
  }, []);

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
          <Link 
            to="/create-case" 
            className="bg-primary text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-600 flex items-center gap-2"
          >
            <span className="material-symbols-outlined">add</span>
            Tạo vụ án mới
          </Link>
        </div>

        {loading ? (
          <div className="text-center py-10">Đang tải dữ liệu...</div>
        ) : cases.length === 0 ? (
          <div className="text-center py-20 bg-white dark:bg-slate-900 rounded-xl shadow-sm">
            <span className="material-symbols-outlined text-6xl text-slate-300">folder_open</span>
            <p className="mt-4 text-slate-500">Bạn chưa có hồ sơ vụ án nào.</p>
            <Link to="/create-case" className="text-primary mt-2 inline-block hover:underline">
              Tạo hồ sơ ngay
            </Link>
          </div>
        ) : (
          <div className="grid gap-4">
            {cases.map((c) => (
              <div 
                key={c.caseId} 
                onClick={() => navigate(`/cases/${c.caseId}`)}
                className="bg-white dark:bg-slate-900 p-5 rounded-xl shadow-sm border border-slate-200 dark:border-slate-800 cursor-pointer hover:shadow-md transition-shadow flex justify-between items-center"
              >
                <div>
                  <h3 className="text-lg font-bold text-slate-800 dark:text-slate-100 mb-1">{c.title}</h3>
                  <p className="text-sm text-slate-500 line-clamp-1">{c.description}</p>
                  <div className="mt-2 text-xs text-slate-400 flex gap-4">
                    <span>Luật sư: {c.lawyerName}</span>
                    <span>Ngày tạo: {new Date(c.createdAt).toLocaleDateString()}</span>
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