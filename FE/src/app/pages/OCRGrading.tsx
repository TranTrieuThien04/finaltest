import { useState } from "react";
import { Button } from "../components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "../components/ui/card";
import { toast } from "sonner";

export default function OCRGrading() {
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [res, setRes] = useState<any>(null);

  const handleAction = async (path: string) => {
    if (!file) return toast.error("Vui lòng chọn ảnh!");
    setLoading(true);
    const formData = new FormData();
    formData.append("file", file);
    formData.append("examCode", "1032");

    try {
      const response = await fetch(`http://localhost:8081/api/v1/ocr${path}`, {
        method: "POST",
        body: formData,
      });
      const data = await response.json();
      if (response.ok) {
        path === "/upload" ? setRes(data) : toast.success("Đã quét đáp án mẫu!");
      } else { toast.error(data.message || "Lỗi xử lý"); }
    } catch (e) { toast.error("Lỗi kết nối Backend 8081"); }
    finally { setLoading(false); }
  };

  return (
    <div className="p-10 space-y-6 max-w-4xl mx-auto">
      <h1 className="text-3xl font-bold text-blue-900">PlanbookAI - Chấm điểm tự động</h1>
      <Card>
        <CardHeader><CardTitle>Bảng điều khiển OCR</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          <input type="file" onChange={(e) => setFile(e.target.files?.[0] || null)} className="border p-2 w-full rounded" />
          <div className="flex gap-4">
            <Button onClick={() => handleAction("/scan-answer-key")} variant="outline" disabled={loading}>
              1. Quét đáp án mẫu
            </Button>
            <Button onClick={() => handleAction("/upload")} disabled={loading} className="bg-blue-600">
              2. Bắt đầu chấm bài học sinh
            </Button>
          </div>
        </CardContent>
      </Card>
      {res && (
        <Card className="p-6 bg-blue-50 border-blue-200 animate-in fade-in">
          <h2 className="text-xl font-bold">Học sinh: {res.studentName}</h2>
          <p className="text-4xl font-black text-blue-700">Điểm: {res.score}</p>
        </Card>
      )}
    </div>
  );
}