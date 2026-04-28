import React, { useEffect, useState } from "react";
import apiClient from "../../api/apiClient";
import { toast } from "sonner";
import { Loader2, Plus, Trash2, Filter, X } from "lucide-react";

export default function QuestionBank() {
  const [questions, setQuestions] = useState<any[]>([]);
  const [topics, setTopics] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [filterTopic, setFilterTopic] = useState("");
  const [showModal, setShowModal] = useState(false);
  
  const [formData, setFormData] = useState({
    content: "",
    topicId: "",
    difficulty: "EASY",
    options: [
      { content: "", correct: true },
      { content: "", correct: false },
      { content: "", correct: false },
      { content: "", correct: false }
    ]
  });

  const fetchData = async () => {
    setLoading(true);
    try {
      const [qRes, tRes] = await Promise.all([
        apiClient.get("/api/v1/questions"),
        apiClient.get("/api/v1/topics")
      ]);
      setQuestions(qRes.data);
      setTopics(tRes.data);
    } catch {
      toast.error("Lỗi tải dữ liệu ngân hàng");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchData(); }, []);

  const handleDelete = async (id: number) => {
    if (!confirm("Xóa câu hỏi này khỏi ngân hàng?")) return;
    try {
      await apiClient.delete(`/api/v1/questions/${id}`);
      toast.success("Đã xóa câu hỏi");
      fetchData();
    } catch {
      toast.error("Lỗi khi xóa");
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.topicId) return toast.error("Vui lòng chọn chủ đề");
    
    try {
      await apiClient.post("/api/v1/questions", {
        content: formData.content,
        type: "MCQ",
        difficulty: formData.difficulty,
        topicId: Number(formData.topicId),
        choices: formData.options
      });
      toast.success("Thêm câu hỏi thành công");
      setShowModal(false);
      setFormData({
        content: "", topicId: "", difficulty: "EASY",
        options: [{ content: "", correct: true }, { content: "", correct: false }, { content: "", correct: false }, { content: "", correct: false }]
      });
      fetchData();
    } catch {
      toast.error("Thêm câu hỏi thất bại");
    }
  };

  const updateOption = (index: number, val: string) => {
    const newOptions = [...formData.options];
    newOptions[index].content = val;
    setFormData({ ...formData, options: newOptions });
  };

  const filteredQuestions = filterTopic 
    ? questions.filter(q => q.topicId?.toString() === filterTopic) 
    : questions;

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Ngân Hàng Câu Hỏi</h1>
        <button 
          onClick={() => setShowModal(true)} 
          className="bg-indigo-600 text-white px-4 py-2 rounded-xl flex items-center gap-2 font-bold shadow-lg shadow-indigo-100 hover:bg-indigo-700"
        >
          <Plus size={18} /> Thêm Câu Hỏi
        </button>
      </div>

      <div className="flex items-center gap-2 bg-white p-3 rounded-xl border w-fit">
        <Filter size={18} className="text-gray-400" />
        <select 
          className="border-none outline-none bg-transparent text-sm font-bold text-gray-700"
          value={filterTopic}
          onChange={e => setFilterTopic(e.target.value)}
        >
          <option value="">Tất cả chủ đề</option>
          {topics.map(t => <option key={t.topicId || t.id} value={t.topicId || t.id}>{t.name}</option>)}
        </select>
      </div>

      {loading ? (
        <div className="py-20 text-center"><Loader2 className="animate-spin mx-auto text-indigo-600" /></div>
      ) : (
        <div className="grid gap-4">
          {filteredQuestions.length === 0 ? (
            <div className="text-center py-10 text-gray-400 italic bg-white rounded-2xl border">Không có câu hỏi nào</div>
          ) : filteredQuestions.map((q: any) => (
            <div key={q.questionId || q.id} className="bg-white p-5 rounded-2xl border hover:shadow-md transition-all group">
              <div className="flex justify-between items-start gap-4">
                <div className="space-y-2">
                  <div className="flex items-center gap-2">
                    <span className={`px-2 py-1 text-[10px] font-black rounded-md ${q.difficulty === 'HARD' ? 'bg-red-100 text-red-700' : q.difficulty === 'MEDIUM' ? 'bg-amber-100 text-amber-700' : 'bg-green-100 text-green-700'}`}>
                      {q.difficulty || 'EASY'}
                    </span>
                    <span className={`px-2 py-1 text-[10px] font-black rounded-md ${q.status === 'APPROVED' ? 'bg-indigo-100 text-indigo-700' : 'bg-gray-100 text-gray-600'}`}>
                      {q.status || 'PENDING'}
                    </span>
                  </div>
                  <p className="font-bold text-gray-900">{q.content}</p>
                </div>
                <button onClick={() => handleDelete(q.questionId || q.id)} className="p-2 text-gray-300 hover:text-red-500 opacity-0 group-hover:opacity-100 transition-all">
                  <Trash2 size={18} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {showModal && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white w-full max-w-xl rounded-3xl p-8 shadow-2xl overflow-y-auto max-h-[90vh]">
            <div className="flex justify-between items-center mb-6">
              <h2 className="text-xl font-bold">Thêm Câu Hỏi Trắc Nghiệm</h2>
              <button onClick={() => setShowModal(false)}><X /></button>
            </div>
            <form onSubmit={handleCreate} className="space-y-4">
              <textarea required placeholder="Nội dung câu hỏi..." className="w-full border-2 rounded-xl p-3 outline-none focus:border-indigo-500 h-24" value={formData.content} onChange={e => setFormData({...formData, content: e.target.value})} />
              
              <div className="grid grid-cols-2 gap-4">
                <select required className="w-full border-2 rounded-xl p-3 outline-none" value={formData.topicId} onChange={e => setFormData({...formData, topicId: e.target.value})}>
                  <option value="">-- Chọn chủ đề --</option>
                  {topics.map(t => <option key={t.topicId || t.id} value={t.topicId || t.id}>{t.name}</option>)}
                </select>
                <select className="w-full border-2 rounded-xl p-3 outline-none" value={formData.difficulty} onChange={e => setFormData({...formData, difficulty: e.target.value})}>
                  <option value="EASY">Dễ</option>
                  <option value="MEDIUM">Trung bình</option>
                  <option value="HARD">Khó</option>
                </select>
              </div>

              <div className="space-y-2 pt-2">
                <label className="text-sm font-bold text-gray-500">Các đáp án (Đánh dấu tích vào đáp án đúng)</label>
                {formData.options.map((opt, idx) => (
                  <div key={idx} className="flex items-center gap-3">
                    <input type="radio" name="correctAnswer" checked={opt.correct} onChange={() => {
                      const newOpts = formData.options.map((o, i) => ({ ...o, correct: i === idx }));
                      setFormData({ ...formData, options: newOpts });
                    }} className="w-4 h-4 text-indigo-600" />
                    <input required placeholder={`Đáp án ${idx + 1}`} className="flex-1 border rounded-lg p-2 outline-none" value={opt.content} onChange={e => updateOption(idx, e.target.value)} />
                  </div>
                ))}
              </div>

              <button type="submit" className="w-full bg-indigo-600 text-white font-bold py-4 rounded-xl hover:bg-indigo-700 mt-4">LƯU CÂU HỎI</button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}