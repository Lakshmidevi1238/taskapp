import { create } from "zustand";
import api from "../services/api";

export const useListStore = create((set) => ({

  lists: [],

  loadLists: async (boardId) => {
    const res = await api.get(`/boards/${boardId}/lists`);
    set({ lists: res.data });
  },

  createList: async (boardId, title) => {
    const res = await api.post(
      `/boards/${boardId}/lists`,
      { title }
    );

    set(s => ({
      lists: [...s.lists, res.data]
    }));
  },

  reorderListsLocal: (newLists) =>
    set({ lists: newLists }),

}));
