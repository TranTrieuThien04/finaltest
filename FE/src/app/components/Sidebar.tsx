import React from "react";
import { Link, useLocation } from "react-router-dom";
import { 
  LayoutDashboard, BookOpen, Wand2, FileText, ScanLine,
  CheckSquare, Users, Package, ClipboardList, FileSearch,
  Terminal, LogOut 
} from "lucide-react";
import { cn } from "./ui/utils";
import { UserRole } from "../lib/auth";

interface Props { 
  role: UserRole; 
  onLogout: () => void; 
}

const menu: Record<UserRole, any[]> = {
  teacher: [
    { icon: LayoutDashboard, label: "Dashboard",       href: "/teacher" },
    { icon: BookOpen,        label: "Ngân hàng câu hỏi", href: "/teacher/question-bank" },
    { icon: Wand2,           label: "Tạo bài tập",       href: "/teacher/exercise-generator" },
    { icon: FileText,        label: "Tạo đề thi",        href: "/teacher/exam-generator" },
    { icon: ScanLine,        label: "Chấm điểm OCR",     href: "/teacher/ocr-grading" },
  ],
  admin: [
    { icon: Users,           label: "Quản lý Users",     href: "/admin/users" },
    { icon: LayoutDashboard, label: "Teacher Tools",     href: "/teacher" },
  ],
  manager: [
    { icon: ClipboardList,   label: "Đơn hàng",          href: "/manager/orders" },
    { icon: Package,         label: "Gói dịch vụ",       href: "/manager/packages" },
    { icon: CheckSquare,     label: "Duyệt nội dung",    href: "/manager/approval" },
  ],
  staff: [
    { icon: FileSearch,      label: "Duyệt giáo án",     href: "/staff/lesson-plans" },
    { icon: Terminal,        label: "Prompt Templates",  href: "/staff/prompts" },
  ],
};

export default function Sidebar({ role, onLogout }: Props) {
  const { pathname } = useLocation();
  
  return (
    <div className="w-64 border-r bg-white flex flex-col h-screen">
      <div className="h-16 flex items-center px-6 font-black text-indigo-600 text-xl tracking-tighter">
        PLANBOOKAI
      </div>
      
      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        {menu[role]?.map((item) => {
          const isBaseRoute = ["/teacher", "/admin", "/manager", "/staff"].includes(item.href);
          const isActive = isBaseRoute ? pathname === item.href : pathname.startsWith(item.href);

          return (
            <Link 
              key={item.href} 
              to={item.href} 
              className={cn(
                "flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-bold transition-all", 
                isActive ? "bg-indigo-50 text-indigo-700" : "text-gray-400 hover:bg-gray-50"
              )}
            >
              <item.icon size={20} /> {item.label}
            </Link>
          );
        })}
      </nav>

      <button 
        onClick={onLogout} 
        className="m-4 flex items-center gap-3 px-4 py-3 text-red-500 font-bold hover:bg-red-50 rounded-xl transition-all"
      >
        <LogOut size={20}/> Đăng xuất
      </button>
    </div>
  );
}