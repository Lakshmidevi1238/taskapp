import { create } from "zustand";
import api from "../services/api";

export const useBoardStore = create((set) => ({

  boards: [],
  currentBoard: null,
  loading: false,

  setCurrentBoard: (board) =>
    set({ currentBoard: board }),

  loadBoards: async () => {
    try {
      set({ loading: true });

      const res = await api.get("/api/boards/my");

      set({ boards: res.data, loading: false });

    } catch (e) {
      console.error("loadBoards failed", e);
      set({ loading: false });
    }
  },

  createBoard: async (title, description) => {

    const res = await api.post("/api/boards", { 
      title,
      description
    });

    set(s => ({
      boards: [res.data, ...s.boards]
    }));

    return res.data;
  }

}));