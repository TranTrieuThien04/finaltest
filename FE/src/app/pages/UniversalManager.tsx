import { useEffect, useState } from "react";
import { BaseService } from "../services/api.service";

export default function UniversalManager({ title, endpoint }: { title: string, endpoint: string }) {
  const [data, setData] = useState([]);

  useEffect(() => {
    BaseService.getAll(endpoint).then(res => setData(res.data));
  }, [endpoint]);

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">{title}</h1>
      <table className="w-full border shadow-sm">
        <thead className="bg-gray-100">
          <tr>
            <th className="p-2 border">ID</th>
            <th className="p-2 border">Nội dung dữ liệu (JSON)</th>
          </tr>
        </thead>
        <tbody>
          {data.map((item: any) => (
            <tr key={item.id}>
              <td className="p-2 border">{item.id || item.ocrResultId}</td>
              <td className="p-2 border text-xs font-mono">{JSON.stringify(item).substring(0, 100)}...</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}