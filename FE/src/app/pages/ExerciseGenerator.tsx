import React, { useEffect, useState } from "react";
import apiClient from "../../api/apiClient";
import { toast } from "sonner";
import { Wand2, History, Sparkles, Loader2, Layers, Hash } from "lucide-react";

interface Topic { topicId: number; name: string; }
interface Exercise { exerciseId: number; title: string; createdAt: string; }

export default function ExerciseGenerator() {
  const [topics, setTopics] = useState<Topic[]>([]);
  const [history, setHistory] = useState<Exercise[]>([]);
  const [loading, setLoading] = useState(false);
  const [isGenerating, setIsGenerating] = useState(false);
  const [config, setConfig] = useState({ topicId: "", count: 5, title: "" });

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [topicsRes, historyRes] = await Promise.all([
        apiClient.get("/api/v1/topics"),
        apiClient.get("/api/v1/exercises"),
      ]);
      setTopics(topicsRes.data ?? []);
      setHistory(historyRes.data ?? []);
    } catch {
      toast.error("Lỗi đồng bộ dữ liệu");
    } finally {
      setLoading(false);
    }
  };

  // FIX: Gọi API thật — không dùng mock data
  const handleGenerate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!config.topicId) return toast.error("Vui lòng chọn chủ đề");
    if (!config.title.trim()) return toast.error("Vui lòng nhập tiêu đề bài tập");
    if (config.count < 1) return toast.error("Số câu phải lớn hơn 0");

    setIsGenerating(true);
    try {
      await apiClient.post("/api/v1/exercises", {
        title: config.title.trim(),
        topicId: Number(config.topicId),
        questionCount: config.count,   // FIX: dùng questionCount đúng với BE
      });
      toast.success(`Đã tạo bài tập "${config.title}" thành công!`);
      setConfig({ topicId: "", count: 5, title: "" });
      fetchData(); // Refresh lịch sử
    } catch (err: any) {
      const msg = err?.response?.data?.message || "Lỗi khi tạo bài tập";
      toast.error(msg);
    } finally {
      setIsGenerating(false);
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6 bg-gray-50 min-h-screen">
      <div className="flex items-center gap-3">
        <div className="p-2 bg-indigo-600 rounded-lg text-white"><Wand2 size={24} /></div>
        <h1 className="text-3xl font-bold text-gray-900">Tạo Bài Tập Nhanh</h1>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Form tạo bài tập */}
        <div className="lg:col-span-1">
          <form onSubmit={handleGenerate} className="bg-white p-6 rounded-2xl border shadow-sm space-y-4">
            <h2 className="font-bold text-lg flex items-center gap-2">
              <Sparkles size={18} className="text-amber-500" /> Cấu hình bài tập
            </h2>

            <div className="space-y-2">
              <label className="text-sm font-semibold text-gray-700">Tiêu đề bài tập</label>
              <input
                required
                className="w-full border rounded-xl px-4 py-2.5 outline-none focus:ring-2 ring-indigo-500"
                placeholder="VD: Bài tập Hóa học Chương 1"
                value={config.title}
                onChange={e => setConfig({ ...config, title: e.target.value })}
              />
            </div>

            <div className="space-y-2">
              <label className="text-sm font-semibold text-gray-700">Chủ đề</label>
              <select
                required
                className="w-full border rounded-xl px-4 py-2.5 outline-none focus:ring-2 ring-indigo-500 bg-white"
                value={config.topicId}
                onChange={e => setConfig({ ...config, topicId: e.target.value })}
              >
                <option value="">Chọn chủ đề...</option>
                {topics.map(t => (
                  <option key={t.topicId} value={t.topicId}>{t.name}</option>
                ))}
              </select>
            </div>

            <div className="space-y-2">
              <label className="text-sm font-semibold text-gray-700 flex items-center gap-1">
                <Hash size={14} /> Số câu hỏi
              </label>
              <input
                type="number"
                required
                min={1}
                max={50}
                className="w-full border rounded-xl px-4 py-2.5 outline-none focus:ring-2 ring-indigo-500"
                value={config.count}
                onChange={e => setConfig({ ...config, count: Number(e.target.value) })}
              />
            </div>

            <button
              type="submit"
              disabled={isGenerating}
              className="w-full bg-indigo-600 text-white font-bold py-4 rounded-xl hover:bg-indigo-700 disabled:opacity-50 flex items-center justify-center gap-2"
            >
              {isGenerating ? (
                <><Loader2 className="animate-spin" size={18} /> Đang tạo...</>
              ) : (
                <><Wand2 size={18} /> TẠO BÀI TẬP</>
              )}
            </button>
          </form>
        </div>

        {/* Lịch sử */}
        <div className="lg:col-span-2 bg-white rounded-2xl border shadow-sm">
          <div className="p-5 border-b flex items-center gap-2">
            <History size={18} className="text-gray-500" />
            <h2 className="font-bold text-lg">Lịch sử bài tập đã tạo</h2>
            <span className="ml-auto text-sm text-gray-400">{history.length} bài</span>
          </div>

          {loading ? (
            <div className="p-10 text-center"><Loader2 className="animate-spin mx-auto text-indigo-600" /></div>
          ) : history.length === 0 ? (
            <div className="p-12 flex flex-col items-center text-gray-400 gap-3">
              <Layers size={40} />
              <p className="text-sm">Chưa có bài tập nào. Tạo bài tập đầu tiên!</p>
            </div>
          ) : (
            <div className="divide-y">
              {history.map(ex => (
                <div key={ex.exerciseId} className="p-4 flex items-center justify-between hover:bg-gray-50">
                  <div>
                    <p className="font-semibold text-gray-800">{ex.title}</p>
                    <p className="text-xs text-gray-400">
                      {new Date(ex.createdAt).toLocaleDateString("vi-VN")}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}