import { useEffect } from "react";
import { Link } from "react-router-dom";
import { useBoardStore } from "../stores/boardStore";
import { useAuthStore } from "../stores/authStore";  

export default function BoardsPage() {

  const boards = useBoardStore(s => s.boards);
  const loadBoards = useBoardStore(s => s.loadBoards);
  const createBoard = useBoardStore(s => s.createBoard);

  const hydrated = useAuthStore(s => s.hydrated);  

  useEffect(() => {
    if (hydrated) loadBoards();   
  }, [hydrated]);                 

  const create = async () => {
    const b = await createBoard("New Board", "");
    window.location.href = `/app/boards/${b.id}`;
  };

  return (
    <div className="p-8">

      <button
        onClick={create}
        className="border rounded px-4 py-2 mb-6"
      >
        + New Board
      </button>

      <div className="grid grid-cols-3 gap-4">

        {boards.map(b => (
          <Link
            key={b.id}
            to={`/app/boards/${b.id}`}
            className="border rounded p-4 hover:bg-gray-50"
          >
            <h3 className="font-semibold">
              {b.title}
            </h3>
          </Link>
        ))}

        {boards.length === 0 && (
          <div className="text-gray-400">
            No boards found
          </div>
        )}

      </div>
    </div>
  );
}
