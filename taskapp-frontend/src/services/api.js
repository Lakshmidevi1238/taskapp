import axios from "axios";
import { useAuthStore } from "../stores/authStore";

const api = axios.create({
  baseURL: "https://taskapp-production-556d.up.railway.app"
});


api.interceptors.request.use(config => {

  const store = useAuthStore.getState();



  let token = store.accessToken;

  
  if (!token) {
    const raw = localStorage.getItem("taskapp_auth");
    if (raw) {
      const parsed = JSON.parse(raw);
      token = parsed?.state?.accessToken;
    }
  }

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});


api.interceptors.response.use(
  res => res,
  err => {
    if (err.response?.status === 403) {
      console.warn("403 detected — logging out");
      useAuthStore.getState().logout();
      window.location.href = "/login";
    }
    return Promise.reject(err);
  }
);

export default api;
