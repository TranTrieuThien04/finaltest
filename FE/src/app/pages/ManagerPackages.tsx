import React, { useEffect, useState } from "react";
import apiClient from "../../api/apiClient";
import { toast } from "sonner";
import { Package, Plus, Edit2, Trash2, Loader2, X } from "lucide-react";

export default function ManagerPackages() {
  const [packages, setPackages] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  
  const [formData, setFormData] = useState({
    name: "",
    price: 0,
    durationDays: 30,
    description: ""
  });

  const fetchPackages = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get("/api/v1/packages");
      setPackages(res.data);
    } catch {
      toast.error("Không thể tải danh sách gói");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchPackages(); }, []);

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingId) {
        await apiClient.put(`/api/v1/packages/${editingId}`, formData);
        toast.success("Đã cập nhật gói dịch vụ");
      } else {
        await apiClient.post("/api/v1/packages", formData);
        toast.success("Đã thêm gói dịch vụ mới");
      }
      setShowModal(false);
      fetchPackages();
    } catch {
      toast.error("Lỗi khi lưu dữ liệu");
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Xác nhận xóa gói này?")) return;
    try {
      await apiClient.delete(`/api/v1/packages/${id}`);
      toast.success("Đã xóa gói");
      fetchPackages();
    } catch {
      toast.error("Lỗi khi xóa");
    }
  };

  const openEdit = (pkg: any) => {
    setEditingId(pkg.packageId); 
    setFormData({ 
      name: pkg.name, 
      price: pkg.price, 
      durationDays: pkg.durationDays, 
      description: pkg.description 
    });
    setShowModal(true);
  };

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-center">
        <div className="flex items-center gap-3">
          <Package className="text-indigo-600" size={32} />
          <h1 className="text-2xl font-bold">Quản lý Gói Dịch Vụ</h1>
        </div>
        <button 
          onClick={() => { setEditingId(null); setFormData({name: "", price: 0, durationDays: 30, description: ""}); setShowModal(true); }}
          className="bg-indigo-600 text-white px-4 py-2 rounded-xl flex items-center gap-2 hover:bg-indigo-700 shadow-lg shadow-indigo-100"
        >
          <Plus size={18} /> Thêm Gói Mới
        </button>
      </div>

      <div className="bg-white border rounded-2xl overflow-hidden shadow-sm">
        <table className="w-full text-left">
          <thead className="bg-gray-50 border-b text-gray-500 font-semibold text-sm">
            <tr>
              <th className="p-4">Tên gói</th>
              <th className="p-4">Giá tiền</th>
              <th className="p-4">Thời hạn</th>
              <th className="p-4 text-center">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {loading ? (
              <tr><td colSpan={4} className="p-10 text-center"><Loader2 className="animate-spin mx-auto" /></td></tr>
            ) : packages.map(pkg => (
              <tr key={pkg.packageId} className="hover:bg-gray-50 transition-colors">
                <td className="p-4 font-bold text-gray-800">{pkg.name}</td>
                <td className="p-4 text-indigo-600 font-semibold">{pkg.price.toLocaleString()}đ</td>
                <td className="p-4 text-gray-600">{pkg.durationDays} ngày</td>
                <td className="p-4 flex justify-center gap-2">
                  <button onClick={() => openEdit(pkg)} className="p-2 text-gray-400 hover:text-indigo-600"><Edit2 size={18}/></button>
                  <button onClick={() => handleDelete(pkg.packageId)} className="p-2 text-gray-400 hover:text-red-600"><Trash2 size={18}/></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <div className="bg-white w-full max-w-md rounded-3xl p-8 shadow-2xl space-y-6">
            <div className="flex justify-between items-center">
              <h2 className="text-xl font-bold">{editingId ? "Cập nhật gói" : "Tạo gói mới"}</h2>
              <button onClick={() => setShowModal(false)}><X /></button>
            </div>
            <form onSubmit={handleSave} className="space-y-4">
              <div>
                <label className="text-xs font-bold text-gray-400 uppercase">Tên gói</label>
                <input required className="w-full border-2 rounded-xl p-3 outline-none focus:border-indigo-500" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="text-xs font-bold text-gray-400 uppercase">Giá (VNĐ)</label>
                  <input type="number" required className="w-full border-2 rounded-xl p-3 outline-none" value={formData.price} onChange={e => setFormData({...formData, price: Number(e.target.value)})} />
                </div>
                <div>
                  <label className="text-xs font-bold text-gray-400 uppercase">Ngày dùng</label>
                  <input type="number" required className="w-full border-2 rounded-xl p-3 outline-none" value={formData.durationDays} onChange={e => setFormData({...formData, durationDays: Number(e.target.value)})} />
                </div>
              </div>
              <div>
                <label className="text-xs font-bold text-gray-400 uppercase">Mô tả</label>
                <textarea className="w-full border-2 rounded-xl p-3 outline-none h-24" value={formData.description} onChange={e => setFormData({...formData, description: e.target.value})} />
              </div>
              <button type="submit" className="w-full bg-indigo-600 text-white font-bold py-4 rounded-xl hover:bg-indigo-700 shadow-lg shadow-indigo-100">XÁC NHẬN LƯU</button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}