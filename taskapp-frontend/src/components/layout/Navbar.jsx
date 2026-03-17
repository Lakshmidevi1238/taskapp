import { useParams } from "react-router-dom";
import { useBoardStore } from "../../stores/boardStore";
import { useAuthStore } from "../../stores/authStore";
import { useState } from "react";

export default function Navbar() {

  const { id } = useParams();
  const boards = useBoardStore(s => s.boards);
  const logout = useAuthStore(s => s.logout);

  const board = boards.find(b => b.id == id);

  const [editing, setEditing] = useState(false);
  const [name, setName] = useState(board?.title || "");

  return (
    <div className="h-14 border-b flex items-center justify-between px-6 bg-white">

      {/* ===== Breadcrumb ===== */}
      <div className="flex items-center gap-2 text-sm">

        <span className="font-medium">Home</span>

        {board && (
          <>
            <span>/</span>

            {editing ? (
              <input
                value={name}
                onChange={e => setName(e.target.value)}
                onBlur={() => setEditing(false)}
                onKeyDown={e => e.key === "Enter" && setEditing(false)}
                className="border px-2 py-1 text-sm"
                autoFocus
              />
            ) : (
              <span
                onClick={() => setEditing(true)}
                className="cursor-pointer font-semibold"
              >
                {board.title}
              </span>
            )}
          </>
        )}

      </div>

      <button
        onClick={logout}
        className="bg-red-500 text-white px-3 py-1 rounded text-sm"
      >
        Logout
      </button>

    </div>
  );
}
