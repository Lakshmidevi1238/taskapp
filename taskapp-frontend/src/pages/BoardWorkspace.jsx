import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import api from "../services/api";
import BoardCanvas from "../components/board/BoardCanvas";
import ActivityPanel from "../components/board/ActivityPanel";
import { useTaskStore } from "../stores/taskStore";

export default function BoardWorkspace() {

  const { id } = useParams();
  const [board, setBoard] = useState(null);
  const [showActivity, setShowActivity] = useState(false);
  const [query, setQuery] = useState("");

  const loadTasks = useTaskStore(s => s.loadTasks);

  useEffect(() => {
    api.get(`/boards/${id}`).then(r => setBoard(r.data));
  }, [id]);

  
  const handleSearch = async (value) => {
    setQuery(value);

    if (!value.trim()) {
      await loadTasks(id);
      return;
    }

    const res = await api.get(
      `/boards/${id}/tasks/search?q=${value}&size=100`
    );

    const map = {};
    res.data.content.forEach(t => {
      if (!map[t.listId]) map[t.listId] = [];
      map[t.listId].push(t);
    });

    useTaskStore.setState({ tasksByList: map });
  };

  return (
    <div className="flex w-full h-screen overflow-hidden">

      {/* MAIN SECTION */}
      <div className="flex flex-col flex-1">

        {/* ===== TOP BAR ===== */}
        <div className="h-14 border-b px-6 flex items-center justify-between bg-white">

          <div className="text-lg font-semibold">
            {board?.title}
          </div>

          <div className="flex items-center gap-4">

            <input
              value={query}
              onChange={e => handleSearch(e.target.value)}
              placeholder="Search tasks..."
              className="border px-3 py-1 rounded w-64 text-sm"
            />

            <button
              onClick={() => setShowActivity(v => !v)}
              className="text-sm text-gray-600 hover:text-black"
            >
              Activity
            </button>

          </div>
        </div>

        {/* ===== BOARD CANVAS ===== */}
        <div className="flex-1 overflow-auto bg-gray-50">
          <BoardCanvas boardId={id} />
        </div>

      </div>

      {/* ===== ACTIVITY SLIDE PANEL ===== */}
      {showActivity && (
        <div className="w-80 border-l bg-gray-100 shadow-lg">
          <ActivityPanel boardId={id} />
        </div>
      )}

    </div>
  );
}
