import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Login.css";
import { setupTokenRefreshTimer } from "../utils/api";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    try {
      const res = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });
      const data = await res.json();
      if (!res.ok || data.success === false) {
        // Nếu tài khoản chưa kích hoạt
        if (data.message && data.message.toLowerCase().includes("chưa được kích hoạt")) {
          setError(data.message);
        } else {
          setError("Sai tài khoản hoặc mật khẩu!");
        }
        return;
      }
      // Lưu accessToken và refreshToken vào localStorage
      if (data.accessToken) {
        localStorage.setItem("accessToken", data.accessToken);
      }
      if (data.refreshToken) {
        localStorage.setItem("refreshToken", data.refreshToken);
      }
      
      // Setup token refresh timer and dispatch login event
      setupTokenRefreshTimer();
      window.dispatchEvent(new CustomEvent('user-login'));
      
      navigate("/home");
    } catch {
      setError("Sai tài khoản hoặc mật khẩu!");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-background-light dark:bg-background-dark font-display">
      <div className="flex w-full max-w-4xl shadow-lg rounded-xl overflow-hidden">
        <div className="hidden lg:flex w-1/2 items-center justify-center p-8 bg-slate-100 dark:bg-slate-900">
          <div
            className="w-full max-w-lg aspect-[2/3] bg-center bg-no-repeat bg-cover rounded-xl"
            data-alt="A stylized image of the scales of justice, representing law and fairness."
            style={{
              backgroundImage:
                'url("https://lh3.googleusercontent.com/aida-public/AB6AXuCWdiJIwbB-npu-tzX11L19n0LqpEw-5DDb-ccMGnmrYSUP5EVZ0plOUgdeOBUFNDvSh1hRpVvxPrMPf6sVIePZYFlM2qjELxZAG7ZQBSMHKM_GtEROf0gKwZ_sUIUptmRL93vTfbJwtdwG9Jsc3yTctxQUlMgsM29BHnj6FNlHnA1gMaxFD-chLWzrrp-N2Vko_GiV26K9nPKWftbgSj0dXzQjWTtd48dwKyEcgB70_JavUeAnlC2ALR3aTMSi6f4_QBUyW_CIO2k")',
            }}
          ></div>
        </div>
        <div className="w-full lg:w-1/2 flex items-center justify-center p-6 sm:p-12">
          <form className="w-full max-w-md space-y-8" onSubmit={handleLogin}>
            {/* ...existing form code... */}
            <div>
              <div className="flex items-center gap-3 mb-4">
                <span className="material-symbols-outlined text-primary text-4xl">account_balance</span>
                <p className="text-2xl font-bold text-slate-800 dark:text-slate-200">Luật Pháp</p>
              </div>
              <div className="flex min-w-72 flex-col gap-2">
                <p className="text-slate-900 dark:text-slate-50 text-3xl font-black leading-tight tracking-[-0.033em]">Chào mừng trở lại</p>
                <p className="text-slate-500 dark:text-slate-400 text-base font-normal leading-normal">Đăng nhập vào tài khoản của bạn để tiếp tục</p>
              </div>
            </div>
            <div className="space-y-6">
              <label className="flex flex-col flex-1">
                <div className="relative flex w-full flex-1 items-stretch">
                  <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-500">person</span>
                  <input
                    className="form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-slate-900 dark:text-slate-50 focus:outline-0 focus:ring-2 focus:ring-primary/50 border border-slate-300 dark:border-slate-700 bg-background-light dark:bg-background-dark focus:border-primary h-12 placeholder:text-slate-400 dark:placeholder:text-slate-500 pl-12 pr-4 py-2.5 text-base font-normal leading-normal"
                    placeholder="Nhập email hoặc tên người dùng"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
              </label>
              <label className="flex flex-col flex-1">
                <div className="relative flex w-full flex-1 items-stretch">
                  <span className="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-500">lock</span>
                  <input
                    className="form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-slate-900 dark:text-slate-50 focus:outline-0 focus:ring-2 focus:ring-primary/50 border border-slate-300 dark:border-slate-700 bg-background-light dark:bg-background-dark focus:border-primary h-12 placeholder:text-slate-400 dark:placeholder:text-slate-500 pl-12 pr-12 py-2.5 text-base font-normal leading-normal"
                    placeholder="Nhập mật khẩu của bạn"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                  <button className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-400 dark:text-slate-500 hover:text-slate-600 dark:hover:text-slate-300" type="button">
                    <span className="material-symbols-outlined">visibility</span>
                  </button>
                </div>
              </label>
            </div>
            {error && <div className="text-red-500 text-sm">{error}</div>}
            <div className="flex items-center justify-end">
              <span
                className="text-sm font-medium text-primary hover:underline cursor-pointer"
                onClick={() => navigate("/forgot-password")}
              >
                Quên mật khẩu?
              </span>
            </div>
            <div className="flex flex-col gap-4">
              <button
                type="submit"
                className="flex items-center justify-center w-full h-12 px-6 bg-primary text-white rounded-lg text-base font-bold transition-colors hover:bg-primary/90 focus:outline-none focus:ring-2 focus:ring-primary/50"
                disabled={loading}
              >
                {loading ? "Đang đăng nhập..." : "Đăng nhập"}
              </button>
              <div className="relative flex items-center py-2">
                <div className="flex-grow border-t border-slate-300 dark:border-slate-700"></div>
                <div className="flex-grow border-t border-slate-300 dark:border-slate-700"></div>
              </div>
            </div>
            <p className="text-center text-sm text-slate-500 dark:text-slate-400">
              Chưa có tài khoản?
              <span
                className="font-medium text-primary hover:underline cursor-pointer"
                onClick={() => navigate("/register")}
              >
                Đăng ký ngay
              </span>
            </p>
          </form>
        </div>
      </div>
    </div>
  );
}