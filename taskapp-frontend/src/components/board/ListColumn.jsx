import { useDrop } from "react-dnd";
import { DND_TYPES } from "../../utils/dndTypes";
import TaskCard from "./TaskCard";
import { useTaskStore } from "../../stores/taskStore";
import api from "../../services/api";

export default function ListColumn({ list, boardId }) {

  const tasksByList = useTaskStore(s => s.tasksByList);
  const loadTasks = useTaskStore(s => s.loadTasks);

  const tasks = tasksByList[list.id] || [];

  const [{ isOver }, drop] = useDrop(() => ({
    accept: [DND_TYPES.TASK_CARD, DND_TYPES.TASK_ICON],

    drop: async (item) => {

      try {

        // ===== CREATE NEW TASK FROM SIDEBAR =====
        if (item.type === DND_TYPES.TASK_ICON) {

          await api.post(`/lists/${list.id}/tasks`, {
            title: "New Task",
            description: ""
          });

          await loadTasks(boardId);
          return;
        }

        // ===== MOVE EXISTING TASK =====
        if (item.listId === list.id) return;

        await api.put(`/tasks/${item.id}/move`, {
          toListId: list.id,
          position: tasks.length
        });

        // 🔥 THIS FIXES FIRST DROP ISSUE
        await loadTasks(boardId);

      } catch (err) {
        console.error("Drop failed:", err);
      }
    },

    collect: monitor => ({
      isOver: monitor.isOver()
    })
  }));

  return (
    <div
      ref={drop}
      className={`w-72 rounded p-3 flex flex-col gap-3
      ${isOver ? "bg-blue-100" : "bg-gray-100"}`}
    >
      <h3 className="font-semibold">{list.title}</h3>

      <div className="space-y-2">
        {tasks.map(t => (
          <TaskCard key={t.id} task={t} />
        ))}
      </div>
    </div>
  );
}
