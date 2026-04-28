import apiClient from "../../api/apiClient";

const TOKEN_KEY         = "planbookai_token";
const REFRESH_TOKEN_KEY = "planbookai_refresh_token";  // FIX
const EXPIRES_AT_KEY    = "planbookai_expires_at";      // FIX
const USER_KEY          = "planbookai_user";

export type UserRole = "teacher" | "admin" | "manager" | "staff";
export interface User { id: number; email: string; role: UserRole; name: string; }

export const getAuthToken    = () => localStorage.getItem(TOKEN_KEY);
export const getRefreshToken = () => localStorage.getItem(REFRESH_TOKEN_KEY);

export const getCurrentUser = (): User | null => {
  const str = localStorage.getItem(USER_KEY);
  return str ? JSON.parse(str) : null;
};

// FIX: Kiểm tra access token có sắp hết hạn không (buffer 5 phút)
export const isAccessTokenExpiringSoon = (): boolean => {
  const expiresAt = localStorage.getItem(EXPIRES_AT_KEY);
  if (!expiresAt) return true;
  const bufferMs = 5 * 60 * 1000; // 5 phút
  return Date.now() + bufferMs >= Number(expiresAt);
};

export const logout = () => {
  // Gọi API logout để thu hồi refresh token (best-effort, không block)
  const token = getAuthToken();
  if (token) {
    apiClient.post("/api/auth/logout").catch(() => {});
  }
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(EXPIRES_AT_KEY);
  localStorage.removeItem(USER_KEY);
  if (window.location.pathname !== "/login") window.location.href = "/login";
};

// FIX: Hàm lấy access token mới từ refresh token
export const refreshAccessToken = async (): Promise<string | null> => {
  const refreshToken = getRefreshToken();
  if (!refreshToken) return null;

  try {
    const res = await apiClient.post("/api/auth/refresh", { refreshToken });
    const { token, expiresIn } = res.data;
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(EXPIRES_AT_KEY, String(Date.now() + expiresIn));
    return token;
  } catch {
    // Refresh token hết hạn hoặc bị thu hồi → logout
    logout();
    return null;
  }
};

export async function loginWithCredentials(email: string, password: string): Promise<User> {
  const res = await apiClient.post("/api/auth/login", { email, password });
  const { token, refreshToken, expiresIn } = res.data;

  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);                    // FIX
  localStorage.setItem(EXPIRES_AT_KEY, String(Date.now() + expiresIn));     // FIX

  const meRes = await apiClient.get("/api/auth/me");
  const raw = meRes.data;

  const user: User = {
    id:    raw.id,
    email: raw.email,
    name:  raw.fullName ?? raw.name ?? "User",
    role:  (Array.isArray(raw.roles)
              ? raw.roles[0]
              : (raw.role || "teacher")
           ).toLowerCase().replace("role_", "") as UserRole,
  };

  localStorage.setItem(USER_KEY, JSON.stringify(user));
  return user;
}