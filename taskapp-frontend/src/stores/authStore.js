import { create } from "zustand";
import { persist } from "zustand/middleware";
import api from "../services/api";

export const useAuthStore = create(
  persist(
    (set) => ({

      accessToken: null,
      refreshToken: null,
      user: null,
      loading: false,

      hydrated: false,   

      // ================= LOGIN =================

      login: async (email, password) => {
        set({ loading: true });

        try {
          const res = await api.post("/api/auth/login", {
            email,
            password
          });

          set({
            accessToken: res.data.accessToken,
            refreshToken: res.data.refreshToken,
            user: {
              id: res.data.userId,
              email: res.data.email,
              name: res.data.name
            },
            loading: false
          });

        } catch (err) {
          set({ loading: false });
          throw err;
        }
      },

      // ================= REGISTER =================

      register: async (name, email, password) => {
        set({ loading: true });

        try {
          const res = await api.post("/api/auth/register", {
            name,
            email,
            password
          });

          set({
            accessToken: res.data.accessToken,
            refreshToken: res.data.refreshToken,
            user: {
              id: res.data.userId,
              email: res.data.email,
              name: res.data.name
            },
            loading: false
          });

        } catch (err) {
          set({ loading: false });
          throw err;
        }
      },

      // ================= LOGOUT =================

      logout: () => {
        set({
          accessToken: null,
          refreshToken: null,
          user: null
        });
      }

    }),
    {
  name: "taskapp_auth",
  onRehydrateStorage: () => (state) => {
    if (state) {
      state.hydrated = true;
    }
  }
}

  )
);
