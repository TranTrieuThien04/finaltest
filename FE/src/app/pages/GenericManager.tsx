import { useEffect, useState } from "react";
import { api } from "../lib/auth";

export default function GenericManager({ title, endpoint }: { title: string, endpoint: string }) {
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;
    setLoading(true);

    api.get<any>(endpoint)
      .then((res) => {
        if (!isMounted) return;
        // Xử lý cả trường hợp res trực tiếp là mảng hoặc res.data là mảng
        const rawData = (res as any).data || res;
        setData(Array.isArray(rawData) ? rawData : [rawData]);
        setLoading(false);
      })
      .catch((err) => {
        if (isMounted) {
          console.error("Lỗi tải dữ liệu:", err);
          setData([]);
          setLoading(false);
        }
      });

    return () => { isMounted = false; };
  }, [endpoint]);

  if (loading) return <div className="p-8">Đang tải dữ liệu...</div>;

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mb-4">{title}</h1>
      <div className="bg-white p-6 shadow rounded-xl border">
        <pre className="text-xs bg-gray-50 p-4 overflow-auto max-h-[500px]">
          {data.length > 0 ? JSON.stringify(data, null, 2) : "Không có dữ liệu hiển thị."}
        </pre>
      </div>
    </div>
  );
}