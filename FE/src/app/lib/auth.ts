import apiClient from "../../api/apiClient";

const TOKEN_KEY = "planbookai_token";
const USER_KEY  = "planbookai_user";

export type UserRole = "teacher" | "admin" | "manager" | "staff";
export interface User { id: number; email: string; role: UserRole; name: string; }

export const getAuthToken = () => localStorage.getItem(TOKEN_KEY);
export const getCurrentUser = (): User | null => {
  const str = localStorage.getItem(USER_KEY);
  return str ? JSON.parse(str) : null;
};

export const logout = () => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
  if (window.location.pathname !== "/login") window.location.href = "/login";
};

export async function loginWithCredentials(email: string, password: string): Promise<User> {
  const res = await apiClient.post("/api/auth/login", { email, password });
  const { token } = res.data;
  localStorage.setItem(TOKEN_KEY, token);

  const meRes = await apiClient.get("/api/auth/me");
  const raw = meRes.data;

  const user: User = {
    id: raw.id,
    email: raw.email,
    name: raw.fullName ?? raw.name ?? "User",
    role: (Array.isArray(raw.roles) ? raw.roles[0] : (raw.role || "teacher")).toLowerCase().replace("role_", "") as UserRole
  };

  localStorage.setItem(USER_KEY, JSON.stringify(user));
  return user;
}