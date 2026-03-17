import { create } from "zustand";
import api from "../services/api";

export const useCanvasStore = create((set) => ({

  lists: [],

  loadLists: async (boardId) => {

    if (!boardId) return;

    const res = await api.get(`/boards/${boardId}/lists`);

    console.log("LIST RESPONSE:", res.data);
const cleaned = res.data.map(l => ({
  id: l.id,
  title: l.title,
  position: l.position,
  boardId: boardId   
}));


    set({ lists: cleaned });
  }

}));
