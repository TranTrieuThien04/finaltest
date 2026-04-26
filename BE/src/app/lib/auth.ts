import { getMe, postLogin } from "./authApi";
import type { MeResponseBody } from "./authApi";
import { clearAccessToken, getAccessToken, setAccessToken } from "./session";

export type UserRole = "teacher" | "admin" | "manager" | "staff";

export interface User {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  avatar?: string;
}

const USER_KEY = "planbookai_user";

const ROLE_PRIORITY: UserRole[] = ["admin", "manager", "staff", "teacher"];

export function mapMeToUser(me: MeResponseBody): User {
  const normalized = new Set(me.roles.map((r) => r.toUpperCase()));
  let primary: UserRole = "teacher";
  for (const candidate of ROLE_PRIORITY) {
    if (normalized.has(candidate.toUpperCase())) {
      primary = candidate;
      break;
    }
  }
  return {
    id: String(me.id),
    name: me.fullName || me.email,
    email: me.email,
    role: primary,
  };
}

export async function loginWithCredentials(email: string, password: string): Promise<User> {
  const { token } = await postLogin({ email, password });
  setAccessToken(token);
  const me = await getMe();
  const user = mapMeToUser(me);
  saveUser(user);
  return user;
}

/** Reloads profile from `/api/auth/me` using the stored token. */
export async function refreshSessionUser(): Promise<User> {
  const me = await getMe();
  const user = mapMeToUser(me);
  saveUser(user);
  return user;
}

export function logout(): void {
  clearAccessToken();
  localStorage.removeItem(USER_KEY);
}

export function getAuthToken(): string | null {
  return getAccessToken();
}

export function getCurrentUser(): User | null {
  const userStr = localStorage.getItem(USER_KEY);
  if (!userStr) {
    return null;
  }
  try {
    return JSON.parse(userStr) as User;
  } catch {
    return null;
  }
}

export function saveUser(user: User): void {
  localStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function hasStoredSession(): boolean {
  return Boolean(getAccessToken());
}
