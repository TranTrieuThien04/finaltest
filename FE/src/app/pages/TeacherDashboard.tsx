import React, { useEffect, useState } from "react";
import apiClient from "../../api/apiClient";
import { BookOpen, FileText, CheckCircle2, TrendingUp } from "lucide-react";

export default function TeacherDashboard() {
  const [stats, setStats] = useState({ qCount: 0, eCount: 0 });

  useEffect(() => {
    Promise.all([
      apiClient.get("/api/v1/questions").catch(() => ({ data: [] })),
      apiClient.get("/api/v1/exams").catch(() => ({ data: [] }))
    ]).then(([qRes, eRes]) => setStats({ qCount: qRes.data.length, eCount: eRes.data.length }));
  }, []);

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Tổng Quan Workspace</h1>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-2xl border flex items-center gap-4">
          <div className="p-3 bg-blue-50 text-blue-600 rounded-xl"><BookOpen size={24} /></div>
          <div><p className="text-sm text-gray-500 font-medium">Câu hỏi ngân hàng</p><h2 className="text-2xl font-black">{stats.qCount}</h2></div>
        </div>
        <div className="bg-white p-6 rounded-2xl border flex items-center gap-4">
          <div className="p-3 bg-indigo-50 text-indigo-600 rounded-xl"><FileText size={24} /></div>
          <div><p className="text-sm text-gray-500 font-medium">Đề thi đã tạo</p><h2 className="text-2xl font-black">{stats.eCount}</h2></div>
        </div>
        <div className="bg-white p-6 rounded-2xl border flex items-center gap-4">
          <div className="p-3 bg-green-50 text-green-600 rounded-xl"><CheckCircle2 size={24} /></div>
          <div><p className="text-sm text-gray-500 font-medium">Tỷ lệ duyệt</p><h2 className="text-2xl font-black">98%</h2></div>
        </div>
      </div>
    </div>
  );
}