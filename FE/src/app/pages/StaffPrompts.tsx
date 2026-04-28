import React, { useEffect, useState } from "react";
import apiClient from "../../api/apiClient";
import { toast } from "sonner";
import { Terminal, Plus, Edit2, Trash2, Loader2, X, Sparkles } from "lucide-react";

export default function StaffPrompts() {
  const [prompts, setPrompts] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState({ title: "", content: "", type: "LESSON" });

  const fetchPrompts = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get("/api/v1/prompts");
      setPrompts(res.data);
    } catch {
      toast.error("Lỗi tải dữ liệu");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchPrompts(); }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingId) {
        await apiClient.put(`/api/v1/prompts/${editingId}`, formData);
      } else {
        await apiClient.post("/api/v1/prompts", formData);
      }
      setShowModal(false);
      fetchPrompts();
      toast.success("Thành công!");
    } catch {
      toast.error("Lỗi khi lưu");
    }
  };

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-center text-indigo-900">
        <div className="flex items-center gap-3">
          <Terminal size={32} />
          <h1 className="text-2xl font-bold tracking-tight">Thư Viện Prompt Kỹ Thuật</h1>
        </div>
        <button onClick={() => { setEditingId(null); setFormData({title: "", content: "", type: "LESSON"}); setShowModal(true); }} className="bg-indigo-600 text-white px-6 py-3 rounded-2xl flex items-center gap-2 font-bold hover:scale-105 transition-all shadow-lg shadow-indigo-100">
          <Plus size={20} /> Tạo Mẫu Mới
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {loading ? <div className="col-span-full py-20"><Loader2 className="animate-spin mx-auto text-indigo-200" size={48} /></div> :
        prompts.map(p => (
          <div key={p.promptTemplateId} className="bg-white border-2 border-gray-50 p-6 rounded-3xl hover:border-indigo-100 hover:shadow-xl transition-all group relative">
            <span className="text-[10px] font-black bg-indigo-50 text-indigo-500 px-3 py-1 rounded-full uppercase tracking-widest">{p.type}</span>
            <h3 className="mt-3 text-lg font-bold text-gray-800 line-clamp-1">{p.title}</h3>
            <p className="mt-2 text-sm text-gray-400 line-clamp-3 leading-relaxed italic">"{p.content}"</p>
            <div className="mt-6 flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
              <button onClick={() => { setEditingId(p.promptTemplateId); setFormData({title: p.title, content: p.content, type: p.type}); setShowModal(true); }} className="p-2 bg-gray-50 text-indigo-600 rounded-xl hover:bg-indigo-600 hover:text-white transition-all"><Edit2 size={16}/></button>
              <button onClick={async () => { if(confirm("Xóa?")) { await apiClient.delete(`/api/v1/prompts/${p.promptTemplateId}`); fetchPrompts(); } }} className="p-2 bg-gray-50 text-red-600 rounded-xl hover:bg-red-600 hover:text-white transition-all"><Trash2 size={16}/></button>
            </div>
          </div>
        ))}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-indigo-900/20 backdrop-blur-md flex items-center justify-center z-50 p-4">
          <div className="bg-white w-full max-w-2xl rounded-[40px] p-10 shadow-2xl space-y-6">
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-black flex items-center gap-2"><Sparkles className="text-amber-400"/> Cấu Hình Prompt AI</h2>
              <button type="button" onClick={() => setShowModal(false)} className="bg-gray-100 p-2 rounded-full"><X size={20}/></button>
            </div>
            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="text-xs font-black text-gray-400 uppercase tracking-widest mb-2 block">Tiêu đề mẫu</label>
                <input required className="w-full bg-gray-50 border-0 rounded-2xl p-4 outline-none focus:ring-2 ring-indigo-500 font-bold" value={formData.title} onChange={e => setFormData({...formData, title: e.target.value})} />
              </div>
              <div>
                <label className="text-xs font-black text-gray-400 uppercase tracking-widest mb-2 block">Loại (Type)</label>
                <select required className="w-full bg-gray-50 border-0 rounded-2xl p-4 outline-none focus:ring-2 ring-indigo-500 font-bold" value={formData.type} onChange={e => setFormData({...formData, type: e.target.value})}>
                  <option value="LESSON">LESSON</option>
                  <option value="EXERCISE">EXERCISE</option>
                  <option value="EXAM">EXAM</option>
                </select>
              </div>
              <div>
                <label className="text-xs font-black text-gray-400 uppercase tracking-widest mb-2 block">Nội dung kỹ thuật (System Instruction)</label>
                <textarea required className="w-full bg-gray-50 border-0 rounded-2xl p-4 outline-none focus:ring-2 ring-indigo-500 h-48 text-sm leading-relaxed" value={formData.content} onChange={e => setFormData({...formData, content: e.target.value})} />
              </div>
              <button type="submit" className="w-full bg-indigo-600 text-white font-black py-5 rounded-3xl hover:bg-indigo-700 shadow-xl shadow-indigo-100 transition-all uppercase tracking-widest">Lưu vào hệ thống</button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}