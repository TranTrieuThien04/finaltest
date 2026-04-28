import React, { useEffect, useState } from "react";
import apiClient from "../../api/apiClient";
import { toast } from "sonner";
import { FilePlus, List, Trash2, Eye, Clock, Hash, Book, Layout, Loader2, PlusCircle } from "lucide-react";

export default function ExamGenerator() {
  const [exams, setExams] = useState<any[]>([]);
  const [topics, setTopics] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [isCreating, setIsCreating] = useState(false);
  
  const [formData, setFormData] = useState({
    title: "",
    duration: 45,
    totalQuestions: 10,
    topicId: "",
    mode: "RANDOM"
  });

  useEffect(() => { fetchInitialData(); }, []);

  const fetchInitialData = async () => {
    setLoading(true);
    try {
      const [examRes, topicRes] = await Promise.all([
        apiClient.get("/api/v1/exams"),
        apiClient.get("/api/v1/topics")
      ]);
      setExams(examRes.data);
      setTopics(topicRes.data);
    } catch {
      toast.error("Không thể tải dữ liệu ban đầu");
    } finally {
      setLoading(false);
    }
  };

  const handleCreateExam = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.topicId) return toast.error("Vui lòng chọn chủ đề");
    setIsCreating(true);
    try {
      await apiClient.post("/api/v1/exams", {
        ...formData,
        topicId: Number(formData.topicId)
      });
      toast.success("Tạo đề thi thành công!");
      setFormData({ title: "", duration: 45, totalQuestions: 10, topicId: "", mode: "RANDOM" });
      fetchInitialData();
    } catch {
      toast.error("Lỗi khi tạo đề thi");
    } finally {
      setIsCreating(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Chắc chắn xóa?")) return;
    try {
      await apiClient.delete(`/api/v1/exams/${id}`);
      toast.success("Đã xóa");
      setExams(exams.filter(e => e.examId !== id));
    } catch {
      toast.error("Lỗi khi xóa");
    }
  };

  return (
    <div className="p-6 space-y-8 bg-gray-50 min-h-screen">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900 flex items-center gap-2"><FilePlus className="text-indigo-600" /> Thiết Kế Đề Thi</h1>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-1">
          <form onSubmit={handleCreateExam} className="bg-white p-6 rounded-2xl shadow-sm border space-y-5">
            <h2 className="text-xl font-bold flex items-center gap-2 mb-2"><PlusCircle size={20} className="text-indigo-500" /> Tạo đề mới</h2>
            <div className="space-y-2">
              <label className="text-sm font-semibold text-gray-700">Tiêu đề</label>
              <input required className="w-full border rounded-xl px-4 py-2.5 outline-none focus:ring-2 ring-indigo-500" value={formData.title} onChange={e => setFormData({...formData, title: e.target.value})} />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <label className="text-sm font-semibold text-gray-700 flex items-center gap-1"><Clock size={14}/> Phút</label>
                <input type="number" required className="w-full border rounded-xl px-4 py-2.5 outline-none" value={formData.duration} onChange={e => setFormData({...formData, duration: Number(e.target.value)})} />
              </div>
              <div className="space-y-2">
                <label className="text-sm font-semibold text-gray-700 flex items-center gap-1"><Hash size={14}/> Số câu</label>
                <input type="number" required className="w-full border rounded-xl px-4 py-2.5 outline-none" value={formData.totalQuestions} onChange={e => setFormData({...formData, totalQuestions: Number(e.target.value)})} />
              </div>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-semibold text-gray-700 flex items-center gap-1"><Book size={14}/> Chủ đề</label>
              <select required className="w-full border rounded-xl px-4 py-2.5 outline-none" value={formData.topicId} onChange={e => setFormData({...formData, topicId: e.target.value})}>
                <option value="">Chọn...</option>
                {topics.map(t => <option key={t.topicId || t.id} value={t.topicId || t.id}>{t.name}</option>)}
              </select>
            </div>
            <button type="submit" disabled={isCreating} className="w-full bg-indigo-600 text-white font-bold py-3 rounded-xl hover:bg-indigo-700 disabled:opacity-50">{isCreating ? <Loader2 className="animate-spin mx-auto"/> : "XÁC NHẬN TẠO"}</button>
          </form>
        </div>

        <div className="lg:col-span-2 bg-white rounded-2xl shadow-sm border overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-gray-50 border-b">
              <tr><th className="p-4">Tiêu đề</th><th className="p-4 text-center">Thời gian</th><th className="p-4 text-center">Số câu</th><th className="p-4 text-center">Thao tác</th></tr>
            </thead>
            <tbody className="divide-y">
              {loading ? <tr><td colSpan={4} className="p-10 text-center"><Loader2 className="animate-spin mx-auto"/></td></tr> :
                exams.map(exam => (
                  <tr key={exam.examId}>
                    <td className="p-4 font-bold text-gray-800">{exam.title}</td>
                    <td className="p-4 text-center font-medium text-gray-600">{exam.duration}p</td>
                    <td className="p-4 text-center"><span className="bg-indigo-100 text-indigo-700 px-2 py-1 rounded-md font-bold">{exam.questionsCount || exam.totalQuestions || 0}</span></td>
                    <td className="p-4 flex justify-center gap-3">
                      <button className="text-indigo-600 p-1"><Eye size={18}/></button>
                      <button onClick={() => handleDelete(exam.examId)} className="text-red-500 p-1"><Trash2 size={18}/></button>
                    </td>
                  </tr>
                ))
              }
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}