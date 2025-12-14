import React, { useState, useEffect, useRef } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { caseService } from "../services/caseService";
import Layout from "../components/Layout";
import axiosInstance from "../utils/axiosInstance";

export default function CreateCase() {
  const navigate = useNavigate();
  const location = useLocation();
  
  // Nhận lawyerId nếu được chuyển từ trang Chi tiết luật sư
  const initialLawyerId = location.state?.lawyerId || "";

  // State cho form
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    lawyerId: initialLawyerId,
  });

  // State cho tìm kiếm luật sư
  const [searchTerm, setSearchTerm] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [selectedLawyer, setSelectedLawyer] = useState(null);
  const [showDropdown, setShowDropdown] = useState(false);
  
  // State xử lý loading/error
  const [loading, setLoading] = useState(false);
  const [searching, setSearching] = useState(false);
  const searchTimeoutRef = useRef(null);

  // 1. Load thông tin luật sư ban đầu (nếu có ID)
  useEffect(() => {
    if (initialLawyerId) {
      const fetchInitialLawyer = async () => {
        try {
          const res = await axiosInstance.get(`/api/lawyers/${initialLawyerId}`);
          if (res.data.success) {
            setSelectedLawyer(res.data.data);
          }
        } catch (error) {
          console.error("Không tìm thấy thông tin luật sư ban đầu", error);
        }
      };
      fetchInitialLawyer();
    }
  }, [initialLawyerId]);

  // 2. Tìm kiếm luật sư (CHỈ LẤY APPROVED)
  useEffect(() => {
    if (!searchTerm.trim()) {
      setSearchResults([]);
      return;
    }

    if (searchTimeoutRef.current) clearTimeout(searchTimeoutRef.current);

    searchTimeoutRef.current = setTimeout(async () => {
      setSearching(true);
      try {
        // QUAN TRỌNG: Thêm status: 'APPROVED' để chỉ lấy luật sư đã xác minh
        const res = await axiosInstance.get(`/api/search/lawyers`, {
          params: { 
            keyword: searchTerm, 
            status: 'APPROVED', 
            page: 0, 
            size: 5 
          }
        });
        if (res.data.success) {
          setSearchResults(res.data.data.content || []);
          setShowDropdown(true);
        }
      } catch (error) {
        console.error("Lỗi tìm kiếm luật sư:", error);
      } finally {
        setSearching(false);
      }
    }, 500);

    return () => clearTimeout(searchTimeoutRef.current);
  }, [searchTerm]);

  // 3. Chọn luật sư
  const handleSelectLawyer = (lawyer) => {
    console.log("Đã chọn luật sư:", lawyer); // Log để debug
    setFormData({ ...formData, lawyerId: lawyer.lawyerId });
    setSelectedLawyer(lawyer);
    setShowDropdown(false);
    setSearchTerm("");
  };

  // 4. Bỏ chọn
  const handleRemoveLawyer = () => {
    setFormData({ ...formData, lawyerId: "" });
    setSelectedLawyer(null);
  };

  // 5. Submit
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.lawyerId) {
      alert("Vui lòng chọn một luật sư phụ trách!");
      return;
    }

    // Log payload trước khi gửi để kiểm tra
    console.log("Dữ liệu gửi đi:", formData);

    setLoading(true);
    try {
      const res = await caseService.createCase(formData);
      if (res.data.success) {
        alert(`Tạo hồ sơ vụ án thành công cho Luật sư ID: ${formData.lawyerId}`);
        navigate(`/cases/${res.data.data.caseId}`);
      }
    } catch (error) {
      console.error(error);
      alert("Lỗi khi tạo vụ án: " + (error.response?.data?.message || "Vui lòng thử lại"));
    } finally {
      setLoading(false);
    }
  };

  const getAvatar = (lawyer) => {
    if (lawyer.avatarUrl?.startsWith("http")) return lawyer.avatarUrl;
    if (lawyer.avatarUrl) return `http://localhost:8080${lawyer.avatarUrl}`;
    return `https://ui-avatars.com/api/?name=${encodeURIComponent(lawyer.fullName)}&background=random`;
  };

  return (
    <Layout>
      <div className="max-w-3xl mx-auto p-6 bg-white dark:bg-slate-900 rounded-xl shadow-md mt-10 border border-slate-200 dark:border-slate-800">
        <h1 className="text-2xl font-bold mb-6 text-slate-900 dark:text-white border-b pb-4 dark:border-slate-800">
          Tạo Hồ Sơ Vụ Án Mới
        </h1>
        
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-semibold mb-2 dark:text-slate-300">
              Tiêu đề vụ việc <span className="text-red-500">*</span>
            </label>
            <input
              type="text"
              required
              className="w-full p-3 border border-slate-300 dark:border-slate-700 rounded-lg dark:bg-slate-800 focus:ring-2 focus:ring-blue-500 outline-none"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              placeholder="Vd: Tranh chấp đất đai tại..."
            />
          </div>
          
          <div className="relative">
            <label className="block text-sm font-semibold mb-2 dark:text-slate-300">
              Luật sư phụ trách <span className="text-red-500">*</span>
            </label>

            {selectedLawyer ? (
              <div className="flex items-center justify-between p-3 border border-blue-500 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
                <div className="flex items-center gap-3">
                  <img 
                    src={getAvatar(selectedLawyer)} 
                    alt="Avatar" 
                    className="w-12 h-12 rounded-full object-cover border border-slate-300"
                  />
                  <div>
                    <p className="font-bold text-slate-800 dark:text-slate-200 text-base">
                      {selectedLawyer.fullName} 
                      {/* Hiển thị ID để đối chiếu */}
                      <span className="text-sm font-normal text-blue-600 ml-2">(ID: #{selectedLawyer.lawyerId})</span>
                    </p>
                    <p className="text-xs text-slate-500">
                      {selectedLawyer.barAssociationName || "Luật sư tự do"} • {selectedLawyer.yearsOfExp} năm KN
                    </p>
                  </div>
                </div>
                <button 
                  type="button"
                  onClick={handleRemoveLawyer}
                  className="text-red-500 hover:text-red-700 text-sm font-medium px-3 py-1 hover:bg-red-50 rounded"
                >
                  ✕ Bỏ chọn
                </button>
              </div>
            ) : (
              <div className="relative">
                <div className="flex items-center border border-slate-300 dark:border-slate-700 rounded-lg dark:bg-slate-800 overflow-hidden focus-within:ring-2 focus-within:ring-blue-500">
                  <span className="material-symbols-outlined px-3 text-slate-400">search</span>
                  <input
                    type="text"
                    className="w-full p-3 pl-0 outline-none bg-transparent"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    onFocus={() => { if(searchTerm) setShowDropdown(true); }}
                    placeholder="Nhập tên luật sư để tìm kiếm..."
                  />
                  {searching && (
                    <div className="px-3">
                      <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-blue-600"></div>
                    </div>
                  )}
                </div>

                {showDropdown && searchResults.length > 0 && (
                  <ul className="absolute z-10 w-full mt-1 bg-white dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-lg shadow-lg max-h-60 overflow-y-auto">
                    {searchResults.map((lawyer) => (
                      <li 
                        key={lawyer.lawyerId}
                        onClick={() => handleSelectLawyer(lawyer)}
                        className="flex items-center gap-3 p-3 hover:bg-slate-100 dark:hover:bg-slate-700 cursor-pointer border-b last:border-0 dark:border-slate-700 transition-colors"
                      >
                        <img 
                          src={getAvatar(lawyer)} 
                          alt={lawyer.fullName} 
                          className="w-10 h-10 rounded-full object-cover"
                        />
                        <div>
                          <p className="text-sm font-medium text-slate-800 dark:text-slate-200">
                            {lawyer.fullName}
                            <span className="text-xs text-blue-500 ml-1">#{lawyer.lawyerId}</span>
                          </p>
                          <p className="text-xs text-slate-500 truncate max-w-[200px]">{lawyer.email}</p>
                        </div>
                      </li>
                    ))}
                  </ul>
                )}
                
                {showDropdown && !searching && searchTerm && searchResults.length === 0 && (
                   <div className="absolute z-10 w-full mt-1 bg-white dark:bg-slate-800 p-3 text-center text-slate-500 text-sm border rounded-lg shadow-lg">
                     Không tìm thấy luật sư đã xác minh nào.
                   </div>
                )}
              </div>
            )}
          </div>

          <div>
            <label className="block text-sm font-semibold mb-2 dark:text-slate-300">
              Mô tả chi tiết <span className="text-red-500">*</span>
            </label>
            <textarea
              required
              rows="6"
              className="w-full p-3 border border-slate-300 dark:border-slate-700 rounded-lg dark:bg-slate-800 focus:ring-2 focus:ring-blue-500 outline-none"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              placeholder="Mô tả nội dung vụ việc, yêu cầu của bạn..."
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed flex justify-center items-center gap-2"
          >
            {loading ? (
              <>
                <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white"></div>
                Đang xử lý...
              </>
            ) : (
              "Tạo Hồ Sơ Vụ Án"
            )}
          </button>
        </form>
      </div>
    </Layout>
  );
}