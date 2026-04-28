import axios, { AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios';
import {
  getAuthToken,
  isAccessTokenExpiringSoon,
  refreshAccessToken,
  logout,
} from '../app/lib/auth';

const BASE_URL = (import.meta as any).env?.VITE_API_URL ?? 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

// FIX: Biến flag để tránh gọi refresh đồng thời nhiều lần
let isRefreshing = false;
let refreshQueue: Array<(token: string | null) => void> = [];

const processQueue = (token: string | null) => {
  refreshQueue.forEach(cb => cb(token));
  refreshQueue = [];
};

// ─── Request interceptor: gắn token + silent refresh nếu sắp hết hạn ──────
apiClient.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    // Không refresh cho chính endpoint login/refresh
    const skipRefresh =
      config.url?.includes('/api/auth/login') ||
      config.url?.includes('/api/auth/refresh');

    if (!skipRefresh && isAccessTokenExpiringSoon()) {
      if (!isRefreshing) {
        isRefreshing = true;
        const newToken = await refreshAccessToken();
        isRefreshing = false;
        processQueue(newToken);
        if (newToken) {
          config.headers['Authorization'] = `Bearer ${newToken}`;
          return config;
        }
        // refreshAccessToken đã gọi logout() bên trong
        return Promise.reject(new Error('Session expired'));
      } else {
        // Có request khác đang refresh — đợi trong queue
        return new Promise((resolve, reject) => {
          refreshQueue.push((token) => {
            if (token) {
              config.headers['Authorization'] = `Bearer ${token}`;
              resolve(config);
            } else {
              reject(new Error('Session expired'));
            }
          });
        });
      }
    }

    const token = getAuthToken();
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ─── Response interceptor: xử lý 401 ─────────────────────────────────────
apiClient.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      // Token thực sự không hợp lệ (không phải chỉ sắp hết hạn)
      logout();
    }
    return Promise.reject(err);
  }
);

export const uploadFile = (url: string, formData: FormData) =>
  apiClient.post(url, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

export default apiClient;