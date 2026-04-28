import React from "react";
import { logout } from "../lib/auth";

interface HeaderProps {
  user: {
    name?: string;
    email: string;
    role: string;
  };
}

export function Header({ user }: HeaderProps) {
  if (!user) return null;

  const initials = (user.name ?? user.email ?? "U")
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);

  return (
    <header className="h-16 border-b bg-white flex items-center justify-between px-6 shadow-sm">
      <div className="font-semibold text-lg text-gray-800 tracking-tight">
        PlanbookAI Workspace
      </div>

      <div className="flex items-center gap-4">
        <div className="flex flex-col items-end">
          <span className="text-sm font-bold text-gray-900">
            {user.name ?? "User"}
          </span>
          <span className="text-[10px] uppercase tracking-wider text-gray-500 font-semibold">
            {user.role}
          </span>
        </div>

        <div className="h-10 w-10 rounded-full bg-indigo-600 flex items-center justify-center text-white font-bold ring-2 ring-indigo-50 shadow-sm">
          {initials}
        </div>

        <div className="h-6 w-[1px] bg-gray-200 mx-2" />

        <button
          onClick={logout}
          className="text-sm text-red-600 font-semibold hover:text-red-700 transition-colors"
        >
          Đăng xuất
        </button>
      </div>
    </header>
  );
}