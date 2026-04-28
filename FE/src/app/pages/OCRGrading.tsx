import React, { useEffect, useState } from "react";
import apiClient, { uploadFile } from "../../api/apiClient";
import { toast } from "sonner";
import {
  UploadCloud, Loader2, CheckCircle2, XCircle,
  BookOpen, User, Star, AlertTriangle
} from "lucide-react";

interface Exam { examId: number; title: string; totalQuestions: number; }
interface GradeResult {
  ocrResultId: number;
  examId: number;
  studentName: string;
  score: number;
  resultJson: string;
  gradedAt: string;
}

export default function OCRGrading() {
  const [exams, setExams] = useState<Exam[]>([]);
  const [selectedExamId, setSelectedExamId] = useState<string>("");
  const [studentName, setStudentName] = useState<string>("");
  const [loading, setLoading] = useState(false);
  const [loadingExams, setLoadingExams] = useState(true);
  const [results, setResults] = useState<GradeResult[]>([]);
  const [historyLoading, setHistoryLoading] = useState(false);

  // Load danh sách đề thi
  useEffect(() => {
    apiClient.get("/api/v1/exams")
      .then(res => setExams(Array.isArray(res.data) ? res.data : []))
      .catch(() => toast.error("Không thể tải danh sách đề thi"))
      .finally(() => setLoadingExams(false));
  }, []);

  // Khi chọn đề thi → load lịch sử kết quả
  useEffect(() => {
    if (!selectedExamId) { setResults([]); return; }
    setHistoryLoading(true);
    apiClient.get(`/api/v1/ocr/results/${selectedExamId}`)
      .then(res => setResults(Array.isArray(res.data) ? res.data : []))
      .catch(() => setResults([]))
      .finally(() => setHistoryLoading(false));
  }, [selectedExamId]);

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files?.length) return;

    // Validate
    if (!selectedExamId) {
      toast.error("Vui lòng chọn đề thi trước khi chấm");
      return;
    }
    if (!studentName.trim()) {
      toast.error("Vui lòng nhập tên học sinh");
      return;
    }

    setLoading(true);
    let successCount = 0;
    const newResults: GradeResult[] = [];

    for (const file of Array.from(files)) {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("examId", selectedExamId);
      formData.append("studentName", studentName.trim());

      try {
        const res = await uploadFile("/api/v1/ocr/grade", formData);
        newResults.push(res.data);
        successCount++;
      } catch (err: any) {
        const msg = err?.response?.data?.message || `Lỗi xử lý ${file.name}`;
        toast.error(msg);
      }
    }

    if (successCount > 0) {
      toast.success(`Đã chấm xong ${successCount}/${files.length} bài!`);
      setResults(prev => [...newResults, ...prev]);
      setStudentName(""); // Reset tên sau khi chấm
    }

    // Reset input file
    e.target.value = "";
    setLoading(false);
  };

  const selectedExam = exams.find(e => String(e.examId) === selectedExamId);

  return (
    <div className="p-6 space-y-6 bg-gray-50 min-h-screen">
      <h1 className="text-2xl font-bold flex items-center gap-2">
        <BookOpen className="text-indigo-600" /> Chấm Điểm Tự Động (OCR + AI)
      </h1>

      {/* Form chấm bài */}
      <div className="bg-white rounded-2xl border shadow-sm p-6 space-y-5">
        <h2 className="text-lg font-bold text-gray-800">Thông tin chấm bài</h2>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Chọn đề thi */}
          <div className="space-y-2">
            <label className="text-sm font-semibold text-gray-700 flex items-center gap-1">
              <BookOpen size={14} /> Chọn đề thi
            </label>
            {loadingExams ? (
              <div className="flex items-center gap-2 text-gray-400 text-sm">
                <Loader2 size={14} className="animate-spin" /> Đang tải...
              </div>
            ) : (
              <select
                className="w-full border rounded-xl px-4 py-2.5 outline-none focus:ring-2 ring-indigo-500 bg-white"
                value={selectedExamId}
                onChange={e => setSelectedExamId(e.target.value)}
              >
                <option value="">-- Chọn đề thi --</option>
                {exams.map(exam => (
                  <option key={exam.examId} value={exam.examId}>
                    {exam.title} ({exam.totalQuestions} câu)
                  </option>
                ))}
              </select>
            )}
            {selectedExam && (
              <p className="text-xs text-indigo-600 font-medium">
                ✓ Đề: {selectedExam.title} — {selectedExam.totalQuestions} câu
              </p>
            )}
          </div>

          {/* Tên học sinh */}
          <div className="space-y-2">
            <label className="text-sm font-semibold text-gray-700 flex items-center gap-1">
              <User size={14} /> Tên học sinh
            </label>
            <input
              type="text"
              placeholder="Nhập tên học sinh (AI sẽ tự nhận diện nếu bỏ trống)"
              className="w-full border rounded-xl px-4 py-2.5 outline-none focus:ring-2 ring-indigo-500"
              value={studentName}
              onChange={e => setStudentName(e.target.value)}
            />
            <p className="text-xs text-gray-400">
              AI sẽ cố nhận diện tên từ ảnh nếu bạn không nhập
            </p>
          </div>
        </div>

        {/* Upload zone */}
        <div className="bg-gray-50 border-2 border-dashed border-indigo-300 rounded-2xl p-8 flex flex-col items-center gap-4">
          <UploadCloud size={48} className="text-indigo-400" />
          <p className="text-gray-500 text-sm text-center">
            Upload ảnh bài làm của học sinh<br />
            <span className="text-xs text-gray-400">Hỗ trợ: JPG, PNG, WEBP (tối đa 10MB/file)</span>
          </p>
          <label className={`px-6 py-3 rounded-xl font-bold cursor-pointer text-white transition-all ${
            (!selectedExamId || loading)
              ? "bg-gray-300 cursor-not-allowed"
              : "bg-indigo-600 hover:bg-indigo-700"
          }`}>
            {loading ? (
              <span className="flex items-center gap-2">
                <Loader2 size={16} className="animate-spin" /> Đang chấm...
              </span>
            ) : "Chọn Ảnh Để Chấm"}
            <input
              type="file"
              multiple
              accept="image/*"
              className="hidden"
              onChange={handleUpload}
              disabled={!selectedExamId || loading}
            />
          </label>
          {!selectedExamId && (
            <p className="text-amber-500 text-xs flex items-center gap-1">
              <AlertTriangle size={12} /> Chọn đề thi trước khi upload
            </p>
          )}
        </div>
      </div>

      {/* Bảng kết quả */}
      {selectedExamId && (
        <div className="bg-white rounded-2xl border shadow-sm overflow-hidden">
          <div className="p-4 border-b bg-gray-50 flex items-center justify-between">
            <h2 className="font-bold text-gray-800 flex items-center gap-2">
              <Star className="text-amber-500" size={18} />
              Kết quả chấm bài — {selectedExam?.title}
            </h2>
            <span className="text-sm text-gray-500">{results.length} bài đã chấm</span>
          </div>

          {historyLoading ? (
            <div className="p-10 text-center">
              <Loader2 className="animate-spin mx-auto text-indigo-600" />
            </div>
          ) : results.length === 0 ? (
            <div className="p-10 text-center text-gray-400 text-sm">
              Chưa có bài nào được chấm cho đề thi này
            </div>
          ) : (
            <table className="w-full text-left text-sm">
              <thead className="bg-gray-50 border-b">
                <tr>
                  <th className="p-4">Học sinh</th>
                  <th className="p-4 text-center">Điểm</th>
                  <th className="p-4 text-center">Trạng thái</th>
                  <th className="p-4">Thời gian</th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {results.map((r) => (
                  <tr key={r.ocrResultId} className="hover:bg-gray-50">
                    <td className="p-4 font-semibold text-gray-800">{r.studentName}</td>
                    <td className="p-4 text-center">
                      <span className={`text-lg font-bold ${
                        r.score >= 8 ? "text-green-600" :
                        r.score >= 5 ? "text-amber-600" : "text-red-500"
                      }`}>
                        {r.score.toFixed(1)}
                      </span>
                      <span className="text-gray-400 text-xs">/10</span>
                    </td>
                    <td className="p-4 text-center">
                      {r.resultJson?.includes('"error"') ? (
                        <span className="flex items-center justify-center gap-1 text-red-500 text-xs">
                          <XCircle size={14} /> Lỗi OCR
                        </span>
                      ) : (
                        <span className="flex items-center justify-center gap-1 text-green-600 text-xs">
                          <CheckCircle2 size={14} /> Hoàn tất
                        </span>
                      )}
                    </td>
                    <td className="p-4 text-gray-500 text-xs">
                      {new Date(r.gradedAt).toLocaleString("vi-VN")}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  );
}