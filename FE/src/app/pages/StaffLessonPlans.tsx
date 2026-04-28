import React, { useEffect, useState } from "react";
import apiClient from "../../api/apiClient";
import { toast } from "sonner";
import { FileSearch, Check, X, Eye, Loader2, Download, Filter, FileText } from "lucide-react";

export default function StaffLessonPlans() {
  const [plans, setPlans] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("PENDING");

  const fetchPlans = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get(`/api/v1/lesson-plans?status=${filter}`);
      setPlans(res.data);
    } catch {
      toast.error("Lỗi đồng bộ giáo án");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchPlans(); }, [filter]);

  const handleReview = async (id: number, status: string) => {
    try {
      await apiClient.patch(`/api/v1/lesson-plans/${id}/status`, { status });
      toast.success(status === 'APPROVED' ? "Đã duyệt giáo án" : "Đã từ chối");
      fetchPlans();
    } catch {
      toast.error("Thao tác thất bại");
    }
  };

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-center">
        <div className="flex items-center gap-3">
          <div className="p-2 bg-amber-500 rounded-xl text-white shadow-lg shadow-amber-100">
            <FileSearch size={28} />
          </div>
          <div>
            <h1 className="text-2xl font-bold text-gray-900">Thẩm Định Giáo Án</h1>
            <p className="text-sm text-gray-400 font-medium">Phê duyệt nội dung giảng dạy từ giáo viên</p>
          </div>
        </div>
        <div className="bg-gray-100 p-1.5 rounded-2xl flex gap-1">
          {["PENDING", "APPROVED", "REJECTED"].map(s => (
            <button 
              key={s} 
              onClick={() => setFilter(s)}
              className={`px-5 py-2 rounded-xl text-xs font-black uppercase tracking-widest transition-all ${filter === s ? 'bg-white text-indigo-600 shadow-sm' : 'text-gray-400 hover:text-gray-600'}`}
            >
              {s}
            </button>
          ))}
        </div>
      </div>

      <div className="grid grid-cols-1 gap-4">
        {loading ? <div className="py-20 text-center"><Loader2 className="animate-spin mx-auto text-indigo-600" /></div> :
        plans.length === 0 ? <div className="py-20 text-center bg-white border-2 border-dashed rounded-3xl text-gray-300 font-bold italic">Không có giáo án nào trong danh sách này</div> :
        plans.map(plan => (
          <div key={plan.id} className="bg-white border border-gray-100 p-5 rounded-3xl flex items-center justify-between hover:shadow-xl hover:scale-[1.01] transition-all group">
            <div className="flex items-center gap-5">
              <div className="w-14 h-14 bg-indigo-50 rounded-2xl flex items-center justify-center text-indigo-500 group-hover:bg-indigo-600 group-hover:text-white transition-colors">
                <FileText size={28} />
              </div>
              <div>
                <h3 className="font-bold text-gray-800 text-lg">{plan.name}</h3>
                <div className="flex items-center gap-3 text-xs font-medium text-gray-400 mt-1">
                  <span className="bg-gray-100 px-2 py-0.5 rounded text-gray-500">Khối {plan.grade}</span>
                  <span>•</span>
                  <span>GV: {plan.teacherName}</span>
                  <span>•</span>
                  <span>{new Date(plan.createdAt).toLocaleDateString()}</span>
                </div>
              </div>
            </div>
            
            <div className="flex items-center gap-3">
              <button className="p-3 text-gray-400 hover:bg-gray-50 rounded-2xl transition-all" title="Xem chi tiết"><Eye size={20}/></button>
              <button className="p-3 text-gray-400 hover:bg-gray-50 rounded-2xl transition-all" title="Tải xuống PDF"><Download size={20}/></button>
              {filter === 'PENDING' && (
                <div className="flex gap-2 ml-4">
                  <button onClick={() => handleReview(plan.id, 'APPROVED')} className="px-6 py-3 bg-green-500 text-white rounded-2xl font-bold hover:bg-green-600 shadow-lg shadow-green-100 transition-all flex items-center gap-2"><Check size={18}/> DUYỆT</button>
                  <button onClick={() => handleReview(plan.id, 'REJECTED')} className="px-6 py-3 bg-white border-2 border-red-50 text-red-500 rounded-2xl font-bold hover:bg-red-500 hover:text-white transition-all">TỪ CHỐI</button>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}