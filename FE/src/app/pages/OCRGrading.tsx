import React, { useState } from "react";
import { uploadFile } from "../../api/apiClient";
import { toast } from "sonner";
import { UploadCloud, Loader2, CheckCircle2, XCircle } from "lucide-react";

export default function OCRGrading() {
  const [loading, setLoading] = useState(false);
  const [results, setResults] = useState<any[]>([]);

  const handleUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files?.length) return;
    const formData = new FormData();
    Array.from(e.target.files).forEach(file => formData.append("files", file));

    setLoading(true);
    try {
      const res = await uploadFile("/api/v1/ocr/upload", formData);
      const dataArray = Array.isArray(res.data) ? res.data : [];
      setResults(dataArray);
      const successCount = dataArray.filter(r => r.status === "success").length;
      toast.success(`Đã chấm xong ${successCount}/${dataArray.length} bài!`);
    } catch {
      toast.error("Lỗi khi tải file lên");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">Chấm điểm tự động (OCR)</h1>
      <div className="bg-white p-8 rounded-2xl border-2 border-dashed flex flex-col items-center">
        <UploadCloud size={48} className="text-indigo-500 mb-4" />
        <label className="bg-indigo-600 text-white px-6 py-3 rounded-xl font-bold cursor-pointer hover:bg-indigo-700">
          Chọn File Ảnh
          <input type="file" multiple accept="image/*" className="hidden" onChange={handleUpload} disabled={loading} />
        </label>
        {loading && <Loader2 className="mt-4 animate-spin text-indigo-600" />}
      </div>
      
      {results.length > 0 && (
        <div className="bg-white border rounded-2xl overflow-hidden">
          <table className="w-full text-left text-sm">
            <thead className="bg-gray-50 border-b">
              <tr><th className="p-4">Tên file</th><th className="p-4">Nội dung / Lỗi</th><th className="p-4">Trạng thái</th></tr>
            </thead>
            <tbody className="divide-y">
              {results.map((r, i) => (
                <tr key={i}>
                  <td className="p-4 font-bold">{r.file}</td>
                  <td className="p-4 text-gray-600">{r.text || r.error}</td>
                  <td className="p-4">{r.status === "success" ? <CheckCircle2 className="text-green-500"/> : <XCircle className="text-red-500"/>}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}