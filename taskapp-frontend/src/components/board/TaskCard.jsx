import { useDrag } from "react-dnd";
import { useState } from "react";
import { DND_TYPES } from "../../utils/dndTypes";
import api from "../../services/api";
import { useTaskStore } from "../../stores/taskStore";

export default function TaskCard({ task }) {

  if (!task) return null;

  const [editing, setEditing] = useState(false);
  const [title, setTitle] = useState(task.title);
  const [showAssign, setShowAssign] = useState(false);

  const loadTasks = useTaskStore(s => s.loadTasks);
  const boardMembers = useTaskStore(s => s.boardMembers);

  const [{ isDragging }, drag] = useDrag(() => ({
    type: DND_TYPES.TASK_CARD,
    item: {
      id: task.id,
      listId: task.listId,
      boardId: task.boardId
    },
    collect: m => ({
      isDragging: m.isDragging()
    })
  }));

  // ================= SAVE TITLE =================
  const saveTitle = async () => {
    await api.put(`/tasks/${task.id}`, {
      title,
      description: task.description
    });

    await loadTasks(task.boardId);
    setEditing(false);
  };

  // ================= DELETE TASK =================
  const remove = async () => {
    await api.delete(`/tasks/${task.id}`);
    loadTasks(task.boardId);
  };

  // ================= ASSIGN / UNASSIGN =================
  const toggleAssign = async (userId) => {

    const alreadyAssigned = task.assignees?.some(
      a => a.id === userId
    );

    if (alreadyAssigned) {
      await api.delete(`/tasks/${task.id}/assignees`, {
        data: { userId }
      });
    } else {
      await api.post(`/tasks/${task.id}/assignees`, {
        userId
      });
    }

    loadTasks(task.boardId);
  };

  // ================= DIRECT UNASSIGN =================
  const unassignDirectly = async (userId) => {
    await api.delete(`/tasks/${task.id}/assignees`, {
      data: { userId }
    });

    loadTasks(task.boardId);
  };

  return (
    <div
      ref={drag}
      className={`bg-white rounded shadow p-2 text-sm
      flex flex-col gap-2 cursor-move
      ${isDragging ? "opacity-40" : ""}`}
    >

      {/* TITLE */}
      {editing ? (
        <input
          value={title}
          onChange={e => setTitle(e.target.value)}
          onBlur={saveTitle}
          onKeyDown={e => e.key === "Enter" && saveTitle()}
          className="border rounded p-1 text-sm"
          autoFocus
        />
      ) : (
        <div className="flex justify-between items-center">
          <span
            onClick={() => setEditing(true)}
            className="cursor-pointer"
          >
            {task.title}
          </span>
          <button onClick={remove}>✕</button>
        </div>
      )}

      {/* ASSIGNEES */}
      <div className="flex items-center gap-2 flex-wrap">

        {task.assignees?.map(a => (
          <div
            key={a.id}
            onClick={() => unassignDirectly(a.id)}
            className="relative group w-6 h-6 rounded-full
                       bg-blue-500 text-white text-xs
                       flex items-center justify-center
                       cursor-pointer"
          >
            {a.name?.[0]?.toUpperCase()}

            {/* Hover X */}
            <div className="absolute inset-0 hidden group-hover:flex
                            items-center justify-center
                            bg-red-500 rounded-full text-[10px]">
              ✕
            </div>
          </div>
        ))}

        {/* Add Button */}
        <button
          onClick={() => setShowAssign(!showAssign)}
          className="w-6 h-6 rounded-full border text-xs"
        >
          +
        </button>
      </div>

      {/* ASSIGN DROPDOWN */}
      {showAssign && (
        <div className="bg-gray-100 p-2 rounded text-xs">
          {boardMembers.map(m => {
            const isAssigned = task.assignees?.some(a => a.id === m.id);

            return (
              <div
                key={m.id}
                onClick={() => toggleAssign(m.id)}
                className={`cursor-pointer p-1 rounded
                  hover:bg-gray-200
                  ${isAssigned ? "font-semibold text-blue-600" : ""}
                `}
              >
                {m.name}
              </div>
            );
          })}
        </div>
      )}

    </div>
  );
}
