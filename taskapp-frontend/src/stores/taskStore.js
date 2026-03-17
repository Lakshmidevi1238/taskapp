import { create } from "zustand";
import api from "../services/api";

export const useTaskStore = create((set) => ({

  tasksByList: {},
  boardMembers: [],

  // ================= LOAD TASKS =================
  loadTasks: async (boardId) => {
    const res = await api.get(
      `/boards/${boardId}/tasks/search?size=100`
    );

    const map = {};

    res.data.content.forEach(t => {
      const lid = t.listId;   

      if (!map[lid]) map[lid] = [];
      map[lid].push(t);
    });

    set({ tasksByList: map });
  },

  // ================= LOAD BOARD MEMBERS =================
  loadBoardMembers: async (boardId) => {
    const res = await api.get(`/boards/${boardId}/members`);
    set({ boardMembers: res.data });
  },

  // ================= CREATE TASK =================
  createTask: async (listId, title, description) => {

    const res = await api.post(
      `/lists/${listId}/tasks`,
      { title, description }
    );

    set(s => ({
      tasksByList: {
        ...s.tasksByList,
        [listId]: [
          ...(s.tasksByList[listId] || []),
          res.data
        ]
      }
    }));
  }

}));
