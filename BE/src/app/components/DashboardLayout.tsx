import { Outlet, useNavigate } from "react-router";
import { useEffect, useState } from "react";
import { Sidebar } from "./Sidebar";
import { Header } from "./Header";
import {
  getCurrentUser,
  hasStoredSession,
  logout,
  refreshSessionUser,
  type User,
} from "../lib/auth";

export default function DashboardLayout() {
  const navigate = useNavigate();
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function resolveUser() {
      if (!hasStoredSession()) {
        navigate("/login");
        return;
      }
      let current = getCurrentUser();
      if (!current) {
        try {
          current = await refreshSessionUser();
        } catch {
          logout();
          navigate("/login");
          return;
        }
      }
      if (!cancelled) {
        setUser(current);
      }
    }

    void resolveUser();
    return () => {
      cancelled = true;
    };
  }, [navigate]);

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  if (!user) {
    return null;
  }

  return (
    <div className="flex h-screen bg-gray-50">
      <Sidebar role={user.role} onLogout={handleLogout} />
      <div className="flex flex-1 flex-col overflow-hidden">
        <Header user={user} />
        <main className="flex-1 overflow-y-auto p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
