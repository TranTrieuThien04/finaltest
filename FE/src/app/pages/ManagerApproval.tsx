import React, { useEffect, useState } from "react";
import apiClient from "../../api/apiClient";
import { toast } from "sonner";
import { Check, X, Loader2 } from "lucide-react";

export default function ManagerApproval() {
  const [questions, setQuestions] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchPending = async () => {
    try {
      const res = await apiClient.get("/api/v1/questions?status=PENDING");
      setQuestions(res.data);
    } catch {
      toast.error("Lỗi tải danh sách chờ duyệt");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchPending(); }, []);

  const handleApprove = async (id: number) => {
    try {
      await apiClient.patch(`/api/v1/questions/${id}/approve`);
      toast.success("Duyệt thành công!");
      fetchPending();
    } catch {
      toast.error("Lỗi khi duyệt");
    }
  };

  const handleReject = async (id: number) => {
    try {
      await apiClient.patch(`/api/v1/questions/${id}/reject`);
      toast.success("Từ chối thành công!");
      fetchPending();
    } catch {
      toast.error("Lỗi khi từ chối");
    }
  };

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold">Duyệt Nội Dung</h1>
      <div className="bg-white border rounded-2xl overflow-hidden">
        <table className="w-full text-left">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="p-4">Nội dung</th>
              <th className="p-4 text-center">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {loading ? <tr><td colSpan={2} className="p-10 text-center"><Loader2 className="animate-spin mx-auto"/></td></tr> :
              questions.map(q => (
                <tr key={q.questionId}>
                  <td className="p-4 font-medium">{q.content}</td>
                  <td className="p-4 flex justify-center gap-2">
                    <button onClick={() => handleApprove(q.questionId)} className="p-2 bg-green-500 text-white rounded-lg"><Check size={16}/></button>
                    <button onClick={() => handleReject(q.questionId)} className="p-2 bg-red-500 text-white rounded-lg"><X size={16}/></button>
                  </td>
                </tr>
              ))
            }
          </tbody>
        </table>
      </div>
    </div>
  );
}