import axios from "axios";
import { useAuthStore } from "../stores/authStore";

const api = axios.create({
  baseURL: "http://localhost:8080/api"
});


api.interceptors.request.use(config => {

  const store = useAuthStore.getState();

 
  if (!store.hydrated) {
    console.warn("Auth not hydrated yet — delaying request");
    return config;
  }

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
