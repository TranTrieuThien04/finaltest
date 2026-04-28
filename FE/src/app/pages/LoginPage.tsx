import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginWithCredentials } from "../lib/auth";
import { toast } from "sonner";
import { GraduationCap, Mail, Lock, Loader2 } from "lucide-react";

export default function LoginPage() {
  const [email, setEmail] = useState("teacher@planbookai.com");
  const [password, setPassword] = useState("Password123!");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    e.stopPropagation();

    if (loading) return;
    setLoading(true);

    try {
      const user = await loginWithCredentials(email, password);
      toast.success("Đăng nhập thành công!");

      const role = user.role.toLowerCase();
      if (role.includes("admin")) {
        navigate("/admin");
      } else if (role.includes("manager")) {
        navigate("/manager");
      } else if (role.includes("staff")) {
        navigate("/staff");
      } else {
        navigate("/teacher");
      }

    } catch (error: any) {
      toast.error(error.message || "Sai tài khoản hoặc mật khẩu");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100 p-4">
      <div className="w-full max-w-md rounded-2xl bg-white p-8 shadow-2xl">
        <div className="text-center mb-8">
          <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-indigo-600 text-white shadow-lg">
            <GraduationCap size={40} />
          </div>
          <h2 className="mt-4 text-4xl font-black text-indigo-900 tracking-tight">PlanbookAI</h2>
        </div>

        <form onSubmit={handleLogin} className="space-y-6">
          <div className="space-y-4">
            <div className="relative">
              <Mail className="absolute left-3 top-3.5 text-gray-400" size={20} />
              <input
                type="email"
                required
                className="w-full rounded-xl border-2 border-gray-100 bg-gray-50 pl-10 pr-4 py-3 focus:border-indigo-500 focus:bg-white outline-none transition-all"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
            <div className="relative">
              <Lock className="absolute left-3 top-3.5 text-gray-400" size={20} />
              <input
                type="password"
                required
                className="w-full rounded-xl border-2 border-gray-100 bg-gray-50 pl-10 pr-4 py-3 focus:border-indigo-500 focus:bg-white outline-none transition-all"
                placeholder="Mật khẩu"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>

          <button
            type="submit"
            className="w-full rounded-xl bg-indigo-600 py-4 text-lg font-bold text-white shadow-lg shadow-indigo-200 hover:bg-indigo-700 active:scale-95 transition-all flex items-center justify-center"
            disabled={loading}
          >
            {loading ? <Loader2 className="animate-spin" /> : "ĐĂNG NHẬP NGAY"}
          </button>
        </form>

        <div className="mt-8 flex gap-2">
          <button 
            type="button"
            onClick={() => setEmail("teacher@planbookai.com")} 
            className="flex-1 text-[10px] p-2 border rounded hover:bg-indigo-50 font-semibold text-gray-600"
          >
            GV Demo
          </button>
          <button 
            type="button"
            onClick={() => setEmail("admin@planbookai.com")} 
            className="flex-1 text-[10px] p-2 border rounded hover:bg-indigo-50 font-semibold text-gray-600"
          >
            Admin Demo
          </button>
        </div>
      </div>
    </div>
  );
}