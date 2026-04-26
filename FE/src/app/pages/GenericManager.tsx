import { useEffect, useState } from "react";
import { fetchData } from "../../api/apiClient"; // Nhảy ra pages -> nhảy ra app -> vào api

export default function GenericManager({ title, endpoint }: { title: string, endpoint: string }) {
  const [data, setData] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    fetchData(endpoint)
      .then((res) => {
        const result = Array.isArray(res.data) ? res.data : [res.data];
        setData(result);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Lỗi API:", err);
        setLoading(false);
      });
  }, [endpoint]);

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mb-4 text-blue-900">{title}</h1>
      <div className="bg-white p-4 shadow rounded-lg border">
        <pre className="text-[10px] font-mono bg-gray-50 p-4 overflow-auto max-h-[500px]">
          {JSON.stringify(data, null, 2)}
        </pre>
      </div>
    </div>
  );
}