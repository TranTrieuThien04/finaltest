import { getAccessToken } from "./session";

const viteEnv = (import.meta as unknown as { env: { VITE_API_URL?: string } }).env;
const rawBase = viteEnv.VITE_API_URL?.trim();
const baseURL =
  rawBase && rawBase.length > 0 ? rawBase.replace(/\/+$/, "") : "http://localhost:8080";

export class ApiError extends Error {
  constructor(
    message: string,
    readonly status: number,
    readonly data: unknown
  ) {
    super(message);
    this.name = "ApiError";
  }
}

function buildUrl(path: string, params?: Record<string, string | number | undefined>): string {
  const url = new URL(path.startsWith("/") ? path : `/${path}`, `${baseURL}/`);
  if (params) {
    for (const [key, value] of Object.entries(params)) {
      if (value !== undefined && value !== null) {
        url.searchParams.set(key, String(value));
      }
    }
  }
  return url.toString();
}

async function readJsonBody<T>(res: Response): Promise<T> {
  const text = await res.text();
  if (!text) {
    return undefined as T;
  }
  return JSON.parse(text) as T;
}

async function ensureOk(res: Response): Promise<void> {
  if (res.ok) {
    return;
  }
  let data: unknown = null;
  try {
    data = await readJsonBody(res.clone());
  } catch {
    data = { message: res.statusText };
  }
  const message =
    data &&
    typeof data === "object" &&
    data !== null &&
    "message" in data &&
    typeof (data as { message: unknown }).message === "string"
      ? (data as { message: string }).message
      : res.statusText || "Request failed";
  throw new ApiError(message, res.status, data);
}

type QueryParams = Record<string, string | number | undefined>;

async function request<T>(
  method: string,
  path: string,
  options?: { params?: QueryParams; body?: unknown }
): Promise<T> {
  const url = buildUrl(path, options?.params);
  const headers: Record<string, string> = {
    Accept: "application/json",
  };
  const token = getAccessToken();
  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }
  let body: string | undefined;
  if (options?.body !== undefined) {
    headers["Content-Type"] = "application/json";
    body = JSON.stringify(options.body);
  }

  const res = await fetch(url, { method, headers, body });

  if (res.status === 204) {
    await ensureOk(res);
    return undefined as T;
  }

  await ensureOk(res);
  return readJsonBody<T>(res);
}

/** Axios-like surface so feature modules stay thin. */
export const api = {
  get<T>(path: string, config?: { params?: QueryParams }): Promise<{ data: T }> {
    return request<T>("GET", path, { params: config?.params }).then((data) => ({ data }));
  },

  post<T>(path: string, body: unknown): Promise<{ data: T }> {
    return request<T>("POST", path, { body }).then((data) => ({ data }));
  },

  delete(path: string): Promise<void> {
    return request<void>("DELETE", path);
  },
};

export function getApiErrorMessage(error: unknown, fallback = "Request failed"): string {
  if (error instanceof ApiError) {
    if (
      error.data &&
      typeof error.data === "object" &&
      error.data !== null &&
      "message" in error.data &&
      typeof (error.data as { message: unknown }).message === "string"
    ) {
      return (error.data as { message: string }).message;
    }
    return error.message;
  }
  if (error instanceof Error) {
    return error.message;
  }
  return fallback;
}
