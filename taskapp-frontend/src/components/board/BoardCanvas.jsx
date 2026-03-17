import { useDrop } from "react-dnd";
import { DND_TYPES } from "../../utils/dndTypes";
import { useEffect } from "react";
import api from "../../services/api";
import { useCanvasStore } from "../../stores/canvasStore";
import { useTaskStore } from "../../stores/taskStore";
import ListColumn from "./ListColumn";
import { connectBoardSocket, disconnectSocket }
  from "../../services/websocket";

export default function BoardCanvas({ boardId }) {

  const lists = useCanvasStore(s => s.lists);
  const loadLists = useCanvasStore(s => s.loadLists);

  const loadTasks = useTaskStore(s => s.loadTasks);
  const loadBoardMembers = useTaskStore(s => s.loadBoardMembers);

  // ================= INITIAL LOAD =================
  useEffect(() => {
    if (!boardId) return;

    let mounted = true;

    const init = async () => {
      await loadLists(boardId);
      await loadTasks(boardId);
      await loadBoardMembers(boardId);
    };

    init();

    connectBoardSocket(boardId, async (event) => {
      if (!mounted || !event?.type) return;

      if (
        event.type === "TASK_MOVED" ||
        event.type === "TASK_CREATED" ||
        event.type === "TASK_DELETED" ||
        event.type === "TASK_UPDATED" ||
        event.type === "TASK_ASSIGNED" ||
        event.type === "TASK_UNASSIGNED"
      ) {
        await loadTasks(boardId);
      }

      if (
        event.type === "LIST_CREATED" ||
        event.type === "LIST_DELETED" ||
        event.type === "LIST_UPDATED" ||
        event.type === "LIST_REORDERED"
      ) {
        await loadLists(boardId);
      }
    });

    return () => {
      mounted = false;
      disconnectSocket();
    };

  }, [boardId]);

  // ================= DROP AREA =================
  const [{ isOver }, drop] = useDrop(() => ({
    accept: [DND_TYPES.LIST_ICON, DND_TYPES.LIST_CARD],

    drop: async (item) => {

      if (item.type === DND_TYPES.LIST_ICON) {
        await api.post(`/boards/${boardId}/lists`, {
          title: "New List"
        });
        await loadLists(boardId);
        return;
      }

      if (item.type === DND_TYPES.LIST_CARD) {
        const dragged = lists.find(l => l.id === item.id);
        if (!dragged) return;

        const filtered = lists.filter(l => l.id !== item.id);
        filtered.push(dragged);

        useCanvasStore.setState({ lists: filtered });
      }
    },

    collect: m => ({ isOver: m.isOver() })
  }));

  return (
    <div
      ref={drop}
      className={`
        p-8 min-h-full w-full
        flex flex-wrap gap-6 items-start
        ${isOver ? "bg-blue-50" : "bg-gray-50"}
      `}
    >

      {Array.isArray(lists) && lists.map(l => (
        <ListColumn key={l.id} list={l} boardId={boardId} />
      ))}

      {Array.isArray(lists) && lists.length === 0 && (
        <div className="w-full text-center text-gray-400 mt-20">
          Drag LIST icon anywhere here
        </div>
      )}

    </div>
  );
}
