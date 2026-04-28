import React, { useEffect, useState } from "react";
import apiClient from "../../api/apiClient";
import { toast } from "sonner";
import { Wand2, History, Sparkles, Loader2, ChevronRight, Layers, Database } from "lucide-react";

export default function ExerciseGenerator() {
  const [topics, setTopics] = useState<any[]>([]);
  const [history, setHistory] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [isGenerating, setIsGenerating] = useState(false);
  const [config, setConfig] = useState({ topicId: "", count: 5, difficulty: "EASY" });
  const [generatedQuestions, setGeneratedQuestions] = useState<any[]>([]);

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [topicsRes, historyRes] = await Promise.all([
        apiClient.get("/api/v1/topics"),
        apiClient.get("/api/v1/exercises")
      ]);
      setTopics(topicsRes.data);
      setHistory(historyRes.data);
    } catch {
      toast.error("Lỗi đồng bộ dữ liệu");
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateLocal = () => {
    if (!config.topicId) return toast.error("Vui lòng chọn chủ đề");
    setIsGenerating(true);
    setTimeout(() => {
      setGeneratedQuestions([
        { question: "Mẫu câu hỏi tự động 1?", options: ["A", "B", "C", "D"], answer: "A" },
        { question: "Mẫu câu hỏi tự động 2?", options: ["A", "B", "C", "D"], answer: "B" }
      ]);
      toast.success("Đã tạo câu hỏi (Demo Local)!");
      setIsGenerating(false);
    }, 1000);
  };

  const handleSaveToBank = async () => {
    try {
      setLoading(true);
      await apiClient.post("/api/v1/exercises", {
        title: "Bài tập tự động",
        topicId: Number(config.topicId),
        questionIds: [] 
      });
      toast.success("Đã lưu Bài tập");
      setGeneratedQuestions([]);
      fetchData();
    } catch {
      toast.error("Lỗi khi lưu dữ liệu");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-7xl mx-auto space-y-6">
      <div className="flex items-center gap-3">
        <div className="p-2 bg-indigo-600 rounded-lg text-white"><Wand2 size={24} /></div>
        <h1 className="text-3xl font-bold text-gray-900">Tạo Bài Tập Nhanh</h1>
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="space-y-6">
          <div className="bg-white p-6 rounded-2xl border shadow-sm space-y-4">
            <h2 className="font-bold text-lg flex items-center gap-2"><Sparkles size={18} className="text-amber-500" /> Cấu hình</h2>
            <select className="w-full border rounded-xl p-3" value={config.topicId} onChange={(e) => setConfig({...config, topicId: e.target.value})}>
              <option value="">Chọn chủ đề...</option>
              {topics.map(t => <option key={t.topicId || t.id} value={t.topicId || t.id}>{t.name}</option>)}
            </select>
            <button onClick={handleGenerateLocal} disabled={isGenerating} className="w-full bg-indigo-600 text-white font-bold py-4 rounded-xl hover:bg-indigo-700 disabled:opacity-50">
              {isGenerating ? <Loader2 className="animate-spin mx-auto"/> : "BẮT ĐẦU TẠO"}
            </button>
          </div>
          <div className="bg-white p-6 rounded-2xl border shadow-sm">
            <h2 className="font-bold text-lg mb-4 flex items-center gap-2"><History size={18} /> Lịch sử</h2>
            <div className="space-y-3">
              {history.slice(0, 5).map(e => (
                <div key={e.exerciseId} className="flex justify-between p-3 bg-gray-50 rounded-xl"><p className="text-sm font-bold">{e.title}</p></div>
              ))}
            </div>
          </div>
        </div>
        <div className="lg:col-span-2">
          {generatedQuestions.length > 0 ? (
            <div className="space-y-4">
              <button onClick={handleSaveToBank} className="bg-white text-indigo-600 border border-indigo-200 px-4 py-2 rounded-xl font-bold hover:bg-indigo-600 hover:text-white flex items-center gap-2"><Database size={18} /> LƯU BÀI TẬP</button>
              {generatedQuestions.map((q, i) => (
                <div key={i} className="bg-white p-6 rounded-2xl border"><p className="font-bold">Câu {i + 1}: {q.question}</p></div>
              ))}
            </div>
          ) : (
            <div className="h-full min-h-[400px] flex flex-col items-center justify-center bg-gray-50 border-2 border-dashed rounded-3xl p-12 text-center text-gray-400">
              <Layers size={40} className="mb-4" /> <p>Chưa có câu hỏi nào</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}