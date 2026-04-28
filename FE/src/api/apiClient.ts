import axios from 'axios';
import { getAuthToken, logout } from '../app/lib/auth';

const BASE_URL = (import.meta as any).env?.VITE_API_URL ?? 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

apiClient.interceptors.request.use((config) => {
  const token = getAuthToken();
  if (token) config.headers['Authorization'] = `Bearer ${token}`;
  return config;
});

apiClient.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) logout();
    return Promise.reject(err);
  }
);

export const uploadFile = (url: string, formData: FormData) =>
  apiClient.post(url, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

export default apiClient;