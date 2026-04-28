import React, { useEffect, useState } from "react";
import apiClient from "../../api/apiClient";
import { toast } from "sonner";
import { UserPlus, Edit, Trash2, X, Loader2 } from "lucide-react";

export default function AdminUserManagement() {
  const [users, setUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingUser, setEditingUser] = useState<any>(null);
  const [formData, setFormData] = useState({ email: "", password: "", fullName: "", role: "TEACHER" });

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const res = await apiClient.get("/api/v1/users");
      setUsers(res.data);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { fetchUsers(); }, []);

  const handleDelete = async (id: number) => {
    if (!confirm("Xóa người dùng này?")) return;
    try {
      await apiClient.delete(`/api/v1/users/${id}`);
      toast.success("Đã xóa");
      fetchUsers();
    } catch {
      toast.error("Lỗi khi xóa");
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingUser) {
        await apiClient.put(`/api/v1/users/${editingUser.id}`, {
          fullName: formData.fullName,
          status: "active",
          roles: [formData.role], 
        });
      } else {
        await apiClient.post("/api/v1/users", {
          email: formData.email,
          password: formData.password,
          fullName: formData.fullName,
          status: "active",
          roles: [formData.role],
        });
      }
      setShowModal(false);
      fetchUsers();
      toast.success("Thành công");
    } catch (e: any) {
      toast.error(e.response?.data?.message ?? "Thao tác thất bại");
    }
  };

  return (
    <div className="p-6 space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Quản lý người dùng</h1>
        <button onClick={() => { setEditingUser(null); setFormData({email:"", password:"", fullName:"", role:"TEACHER"}); setShowModal(true); }} className="bg-indigo-600 text-white px-4 py-2 rounded-xl flex items-center gap-2 font-bold">
          <UserPlus size={18} /> Thêm User
        </button>
      </div>

      <div className="bg-white border rounded-2xl overflow-hidden">
        <table className="w-full text-left">
          <thead className="bg-gray-50 border-b text-xs font-bold uppercase text-gray-500">
            <tr>
              <th className="p-4">Họ tên</th>
              <th className="p-4">Email</th>
              <th className="p-4">Vai trò</th>
              <th className="p-4 text-center">Thao tác</th>
            </tr>
          </thead>
          <tbody className="divide-y">
            {loading ? <tr><td colSpan={4} className="p-10 text-center"><Loader2 className="animate-spin mx-auto"/></td></tr> :
              users.map(u => (
                <tr key={u.id} className="hover:bg-gray-50">
                  <td className="p-4 font-bold">{u.fullName}</td>
                  <td className="p-4 text-gray-500">{u.email}</td>
                  <td className="p-4"><span className="bg-indigo-50 text-indigo-600 px-2 py-1 rounded text-[10px] font-black uppercase">{u.role || u.roles?.[0]}</span></td>
                  <td className="p-4 flex justify-center gap-2">
                    <button onClick={() => { setEditingUser(u); setFormData({...u, role: u.roles?.[0]?.replace('ROLE_', '') || 'TEACHER'}); setShowModal(true); }} className="p-2 text-gray-400 hover:text-indigo-600"><Edit size={16}/></button>
                    <button onClick={() => handleDelete(u.id)} className="p-2 text-gray-400 hover:text-red-600"><Trash2 size={16}/></button>
                  </td>
                </tr>
              ))
            }
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex items-center justify-center z-50 p-4">
          <form onSubmit={handleSubmit} className="bg-white w-full max-w-md rounded-3xl p-8 shadow-2xl space-y-4">
            <div className="flex justify-between items-center mb-2">
              <h2 className="text-xl font-bold">{editingUser ? "Cập nhật User" : "Tạo User mới"}</h2>
              <button type="button" onClick={() => setShowModal(false)}><X /></button>
            </div>
            <input required placeholder="Họ tên" className="w-full border rounded-xl p-3 outline-none" value={formData.fullName} onChange={e => setFormData({...formData, fullName: e.target.value})} />
            <input required type="email" placeholder="Email" className="w-full border rounded-xl p-3 outline-none" value={formData.email} onChange={e => setFormData({...formData, email: e.target.value})} disabled={!!editingUser} />
            {!editingUser && <input required type="password" placeholder="Mật khẩu" className="w-full border rounded-xl p-3 outline-none" value={formData.password} onChange={e => setFormData({...formData, password: e.target.value})} />}
            <select className="w-full border rounded-xl p-3 outline-none" value={formData.role} onChange={e => setFormData({...formData, role: e.target.value})}>
              <option value="TEACHER">Giáo viên</option>
              <option value="ADMIN">Quản trị viên</option>
              <option value="MANAGER">Quản lý</option>
              <option value="STAFF">Nhân viên</option>
            </select>
            <button type="submit" className="w-full bg-indigo-600 text-white font-bold py-4 rounded-xl hover:bg-indigo-700 transition-all uppercase tracking-widest">Xác nhận lưu</button>
          </form>
        </div>
      )}
    </div>
  );
}