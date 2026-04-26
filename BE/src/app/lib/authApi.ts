import { api } from "./api";

export interface LoginRequestBody {
  email: string;
  password: string;
}

export interface LoginResponseBody {
  token: string;
}

export interface MeResponseBody {
  id: number;
  email: string;
  fullName: string;
  roles: string[];
}

export async function postLogin(body: LoginRequestBody): Promise<LoginResponseBody> {
  const { data } = await api.post<LoginResponseBody>("/api/auth/login", body);
  return data;
}

export async function getMe(): Promise<MeResponseBody> {
  const { data } = await api.get<MeResponseBody>("/api/auth/me");
  return data;
}
