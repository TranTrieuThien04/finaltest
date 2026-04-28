import React, { useEffect, useState } from "react";
import apiClient from "../../api/apiClient";
import { toast } from "sonner";
import { ClipboardList, CheckCircle2, Clock, XCircle, Eye, Loader2 } from "lucide-react";

export default function ManagerOrders() {
  const [orders, setOrders] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("ALL");

  const fetchOrders = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get("/api/v1/orders");
      setOrders(res.data);
    } catch {
      toast.error("Lỗi tải đơn hàng");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchOrders(); }, []);

  const updateStatus = async (id: number, status: string) => {
    try {
      await apiClient.put(`/api/v1/orders/${id}/status`, { status }); 
      toast.success("Cập nhật trạng thái thành công");
      fetchOrders();
    } catch {
      toast.error("Lỗi cập nhật");
    }
  };

  const filteredOrders = filter === "ALL" ? orders : orders.filter(o => o.status === filter);

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-center">
        <div className="flex items-center gap-3">
          <ClipboardList className="text-indigo-600" size={32} />
          <h1 className="text-2xl font-bold">Lịch Sử Đơn Hàng</h1>
        </div>
        <div className="flex gap-2">
          {["ALL", "PENDING", "COMPLETED", "CANCELLED"].map(s => (
            <button 
              key={s} 
              onClick={() => setFilter(s)}
              className={`px-4 py-2 rounded-xl text-sm font-bold transition-all ${filter === s ? 'bg-indigo-600 text-white' : 'bg-white border text-gray-500'}`}
            >
              {s}
            </button>
          ))}
        </div>
      </div>

      <div className="bg-white border rounded-2xl overflow-hidden shadow-sm">
        <table className="w-full text-left">
          <thead className="bg-gray-50 border-b text-gray-500 font-semibold text-sm">
            <tr>
              <th className="p-4">Mã đơn</th>
              <th className="p-4">Khách hàng</th>
              <th className="p-4">Gói dịch vụ</th>
              <th className="p-4">Trạng thái</th>
              <th className="p-4 text-center">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y text-sm">
            {loading ? (
              <tr><td colSpan={5} className="p-10 text-center"><Loader2 className="animate-spin mx-auto" /></td></tr>
            ) : filteredOrders.map(order => (
              <tr key={order.orderId} className="hover:bg-gray-50 transition-colors">
                <td className="p-4 font-black text-indigo-600">#{order.orderId}</td>
                <td className="p-4 font-bold text-gray-800">User #{order.userId}</td>
                <td className="p-4 font-medium text-gray-700">Package #{order.packageId}</td>
                <td className="p-4">
                  <span className={`flex items-center gap-1 w-fit px-3 py-1 rounded-full text-[10px] font-black uppercase ${
                    order.status === 'COMPLETED' ? 'bg-green-100 text-green-700' : 
                    order.status === 'PENDING' ? 'bg-amber-100 text-amber-700' : 'bg-red-100 text-red-700'
                  }`}>
                    {order.status === 'COMPLETED' ? <CheckCircle2 size={12}/> : order.status === 'PENDING' ? <Clock size={12}/> : <XCircle size={12}/>}
                    {order.status}
                  </span>
                </td>
                <td className="p-4">
                  <div className="flex justify-center gap-2">
                    {order.status === 'PENDING' && (
                      <button onClick={() => updateStatus(order.orderId, 'COMPLETED')} className="p-2 text-green-600 hover:bg-green-50 rounded-lg">Duyệt</button>
                    )}
                    <button className="p-2 text-gray-400 hover:text-indigo-600"><Eye size={18}/></button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}