import axios from 'axios';
const apiClient = axios.create({
  baseURL: "http://localhost:8080/api/v1", // Phải là 8080 như trong Log BE thầy gửi
  headers: { "Content-Type": "application/json" }
});
export const fetchData = (endpoint: string) => apiClient.get(endpoint);