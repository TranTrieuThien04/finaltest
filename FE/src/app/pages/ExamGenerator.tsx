import React, { useEffect, useState } from "react";
import apiClient from "../../api/apiClient";
import { toast } from "sonner";
import {
  FilePlus, Trash2, Eye, Clock, Hash, Book,
  Loader2, PlusCircle, X, CheckCircle, Circle,
  ChevronRight, AlertCircle
} from "lucide-react";

// ─── Types ──────────────────────────────────────────────────────────────────
interface Topic       { topicId: number; name: string; }
interface Choice      { questionChoiceId: number; content: string; correct: boolean; }
interface Question    { questionId: number; content: string; difficulty: string; choices: Choice[]; }
interface ExamSummary { examId: number; title: string; duration: number; totalQuestions: number; createdAt: string; }
interface ExamDetail  extends ExamSummary { questions: Question[]; }

// ─── Component ───────────────────────────────────────────────────────────────
export default function ExamGenerator() {
  const [exams,      setExams]      = useState<ExamSummary[]>([]);
  const [topics,     setTopics]     = useState<Topic[]>([]);
  const [loading,    setLoading]    = useState(false);
  const [isCreating, setIsCreating] = useState(false);

  // Detail modal
  const [detailExam,    setDetailExam]    = useState<ExamDetail | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [showModal,     setShowModal]     = useState(false);

  const [formData, setFormData] = useState({
    title: "", duration: 45, totalQuestions: 10, topicId: "",
  });

  // ─── Data fetching ────────────────────────────────────────────────────────
  useEffect(() => { fetchInitialData(); }, []);

  const fetchInitialData = async () => {
    setLoading(true);
    try {
      const [examRes, topicRes] = await Promise.all([
        apiClient.get("/api/v1/exams"),
        apiClient.get("/api/v1/topics"),
      ]);
      setExams(Array.isArray(examRes.data) ? examRes.data : []);
      setTopics(Array.isArray(topicRes.data) ? topicRes.data : []);
    } catch {
      toast.error("Không thể tải dữ liệu ban đầu");
    } finally {
      setLoading(false);
    }
  };

  // ─── Handlers ────────────────────────────────────────────────────────────
  const handleCreateExam = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.topicId) return toast.error("Vui lòng chọn chủ đề");
    setIsCreating(true);
    try {
      await apiClient.post("/api/v1/exams", {
        ...formData,
        topicId: Number(formData.topicId),
      });
      toast.success("Tạo đề thi thành công!");
      setFormData({ title: "", duration: 45, totalQuestions: 10, topicId: "" });
      fetchInitialData();
    } catch (err: any) {
      const msg = err?.response?.data?.message || "Lỗi khi tạo đề thi";
      toast.error(msg);
    } finally {
      setIsCreating(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Chắc chắn xóa đề thi này?")) return;
    try {
      await apiClient.delete(`/api/v1/exams/${id}`);
      toast.success("Đã xóa đề thi");
      setExams(prev => prev.filter(e => e.examId !== id));
    } catch {
      toast.error("Lỗi khi xóa");
    }
  };

  // FIX: Nút Eye — gọi GET /exams/{id}, mở modal
  const handleViewDetail = async (id: number) => {
    setShowModal(true);
    setDetailExam(null);
    setDetailLoading(true);
    try {
      const res = await apiClient.get(`/api/v1/exams/${id}`);
      setDetailExam(res.data);
    } catch {
      toast.error("Không thể tải chi tiết đề thi");
      setShowModal(false);
    } finally {
      setDetailLoading(false);
    }
  };

  const closeModal = () => {
    setShowModal(false);
    setDetailExam(null);
  };

  // ─── Helpers ─────────────────────────────────────────────────────────────
  const difficultyBadge = (d: string) => {
    const map: Record<string, string> = {
      EASY:   "bg-green-100 text-green-700",
      MEDIUM: "bg-amber-100 text-amber-700",
      HARD:   "bg-red-100 text-red-700",
    };
    const label: Record<string, string> = {
      EASY: "Dễ", MEDIUM: "Trung bình", HARD: "Khó",
    };
    return (
      <span className={`text-xs px-2 py-0.5 rounded-full font-semibold ${map[d] ?? "bg-gray-100 text-gray-600"}`}>
        {label[d] ?? d}
      </span>
    );
  };

  // ─── Render ───────────────────────────────────────────────────────────────
  return (
    <div className="p-6 space-y-8 bg-gray-50 min-h-screen">

      {/* Header */}
      <div className="flex items-center gap-3">
        <div className="p-2 bg-indigo-600 rounded-lg text-white"><FilePlus size={24} /></div>
        <h1 className="text-3xl font-bold text-gray-900">Thiết Kế Đề Thi</h1>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">

        {/* ── Form tạo đề ──────────────────────────────────────────────── */}
        <div className="lg:col-span-1">
          <form onSubmit={handleCreateExam}
            className="bg-white p-6 rounded-2xl shadow-sm border space-y-5">
            <h2 className="text-xl font-bold flex items-center gap-2">
              <PlusCircle size={20} className="text-indigo-500" /> Tạo đề mới
            </h2>

            {/* Tiêu đề */}
            <div className="space-y-1.5">
              <label className="text-sm font-semibold text-gray-700">Tiêu đề</label>
              <input
                required
                className="w-full border rounded-xl px-4 py-2.5 outline-none focus:ring-2 ring-indigo-500"
                placeholder="VD: Kiểm tra Hóa học Chương 1"
                value={formData.title}
                onChange={e => setFormData({ ...formData, title: e.target.value })}
              />
            </div>

            {/* Thời gian + số câu */}
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <label className="text-sm font-semibold text-gray-700 flex items-center gap-1">
                  <Clock size={13} /> Phút
                </label>
                <input
                  type="number" required min={1}
                  className="w-full border rounded-xl px-4 py-2.5 outline-none focus:ring-2 ring-indigo-500"
                  value={formData.duration}
                  onChange={e => setFormData({ ...formData, duration: Number(e.target.value) })}
                />
              </div>
              <div className="space-y-1.5">
                <label className="text-sm font-semibold text-gray-700 flex items-center gap-1">
                  <Hash size={13} /> Số câu
                </label>
                <input
                  type="number" required min={1}
                  className="w-full border rounded-xl px-4 py-2.5 outline-none focus:ring-2 ring-indigo-500"
                  value={formData.totalQuestions}
                  onChange={e => setFormData({ ...formData, totalQuestions: Number(e.target.value) })}
                />
              </div>
            </div>

            {/* Chủ đề */}
            <div className="space-y-1.5">
              <label className="text-sm font-semibold text-gray-700 flex items-center gap-1">
                <Book size={13} /> Chủ đề
              </label>
              <select
                required
                className="w-full border rounded-xl px-4 py-2.5 outline-none focus:ring-2 ring-indigo-500 bg-white"
                value={formData.topicId}
                onChange={e => setFormData({ ...formData, topicId: e.target.value })}
              >
                <option value="">Chọn chủ đề...</option>
                {topics.map(t => (
                  <option key={t.topicId} value={t.topicId}>{t.name}</option>
                ))}
              </select>
            </div>

            <button
              type="submit" disabled={isCreating}
              className="w-full bg-indigo-600 text-white font-bold py-3 rounded-xl hover:bg-indigo-700 disabled:opacity-50 flex items-center justify-center gap-2"
            >
              {isCreating
                ? <><Loader2 className="animate-spin" size={16} /> Đang tạo...</>
                : <><ChevronRight size={16} /> XÁC NHẬN TẠO</>}
            </button>
          </form>
        </div>

        {/* ── Danh sách đề thi ─────────────────────────────────────────── */}
        <div className="lg:col-span-2 bg-white rounded-2xl shadow-sm border overflow-hidden">
          <div className="p-4 bg-gray-50 border-b flex items-center justify-between">
            <h2 className="font-bold text-gray-800">Danh sách đề thi</h2>
            <span className="text-sm text-gray-400">{exams.length} đề</span>
          </div>
          <table className="w-full text-left text-sm">
            <thead className="bg-gray-50 border-b">
              <tr>
                <th className="p-4">Tiêu đề</th>
                <th className="p-4 text-center">Thời gian</th>
                <th className="p-4 text-center">Số câu</th>
                <th className="p-4 text-center">Thao tác</th>
              </tr>
            </thead>
            <tbody className="divide-y">
              {loading ? (
                <tr><td colSpan={4} className="p-10 text-center">
                  <Loader2 className="animate-spin mx-auto text-indigo-600" />
                </td></tr>
              ) : exams.length === 0 ? (
                <tr><td colSpan={4} className="p-10 text-center text-gray-400 text-sm">
                  Chưa có đề thi nào. Tạo đề đầu tiên!
                </td></tr>
              ) : exams.map(exam => (
                <tr key={exam.examId} className="hover:bg-gray-50">
                  <td className="p-4 font-semibold text-gray-800">{exam.title}</td>
                  <td className="p-4 text-center text-gray-600 font-medium">{exam.duration} phút</td>
                  <td className="p-4 text-center">
                    <span className="bg-indigo-100 text-indigo-700 px-2.5 py-1 rounded-lg font-bold text-xs">
                      {exam.totalQuestions} câu
                    </span>
                  </td>
                  <td className="p-4">
                    <div className="flex items-center justify-center gap-2">
                      {/* FIX: Nút Eye có onClick */}
                      <button
                        onClick={() => handleViewDetail(exam.examId)}
                        title="Xem chi tiết"
                        className="p-1.5 rounded-lg text-indigo-600 hover:bg-indigo-50 transition-colors"
                      >
                        <Eye size={17} />
                      </button>
                      <button
                        onClick={() => handleDelete(exam.examId)}
                        title="Xóa đề thi"
                        className="p-1.5 rounded-lg text-red-500 hover:bg-red-50 transition-colors"
                      >
                        <Trash2 size={17} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* ── Modal xem chi tiết ─────────────────────────────────────────── */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-3xl max-h-[90vh] flex flex-col">

            {/* Modal header */}
            <div className="flex items-center justify-between p-6 border-b">
              <div>
                <h2 className="text-xl font-bold text-gray-900">
                  {detailLoading ? "Đang tải..." : detailExam?.title ?? "Chi tiết đề thi"}
                </h2>
                {detailExam && (
                  <p className="text-sm text-gray-500 mt-1 flex items-center gap-3">
                    <span className="flex items-center gap-1">
                      <Clock size={13} /> {detailExam.duration} phút
                    </span>
                    <span className="flex items-center gap-1">
                      <Hash size={13} /> {detailExam.totalQuestions} câu
                    </span>
                  </p>
                )}
              </div>
              <button
                onClick={closeModal}
                className="p-2 rounded-xl hover:bg-gray-100 text-gray-500 transition-colors"
              >
                <X size={20} />
              </button>
            </div>

            {/* Modal body */}
            <div className="overflow-y-auto flex-1 p-6 space-y-4">
              {detailLoading ? (
                <div className="flex flex-col items-center py-12 gap-3 text-gray-400">
                  <Loader2 size={32} className="animate-spin text-indigo-600" />
                  <p className="text-sm">Đang tải câu hỏi...</p>
                </div>
              ) : !detailExam?.questions?.length ? (
                <div className="flex flex-col items-center py-12 gap-3 text-gray-400">
                  <AlertCircle size={32} />
                  <p className="text-sm">Không có câu hỏi nào trong đề thi này</p>
                </div>
              ) : (
                detailExam.questions.map((q, index) => (
                  <div key={q.questionId}
                    className="border rounded-xl p-5 hover:border-indigo-200 transition-colors">
                    {/* Question header */}
                    <div className="flex items-start justify-between gap-3 mb-3">
                      <p className="font-semibold text-gray-800 text-sm leading-relaxed flex-1">
                        <span className="text-indigo-600 font-bold mr-2">Câu {index + 1}.</span>
                        {q.content}
                      </p>
                      {difficultyBadge(q.difficulty)}
                    </div>

                    {/* Choices */}
                    {q.choices?.length > 0 && (
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 mt-3">
                        {q.choices.map((ch, ci) => (
                          <div key={ch.questionChoiceId}
                            className={`flex items-center gap-2 px-3 py-2 rounded-lg text-sm
                              ${ch.correct
                                ? "bg-green-50 border border-green-200 text-green-800 font-semibold"
                                : "bg-gray-50 border border-gray-100 text-gray-700"}`}
                          >
                            {ch.correct
                              ? <CheckCircle size={14} className="text-green-600 shrink-0" />
                              : <Circle     size={14} className="text-gray-400 shrink-0" />}
                            <span>
                              <span className="font-bold mr-1">
                                {String.fromCharCode(65 + ci)}.
                              </span>
                              {ch.content}
                            </span>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                ))
              )}
            </div>

            {/* Modal footer */}
            <div className="p-4 border-t flex justify-end">
              <button
                onClick={closeModal}
                className="px-6 py-2.5 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold rounded-xl transition-colors"
              >
                Đóng
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}