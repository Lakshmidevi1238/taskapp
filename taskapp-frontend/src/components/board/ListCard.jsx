import { useState } from "react";
import api from "../../services/api";
import { useCanvasStore } from "../../stores/canvasStore";

export default function ListCard({ list }) {

  const [editing, setEditing] = useState(false);
  const [title, setTitle] = useState(list?.title || "");

  const loadLists = useCanvasStore(s => s.loadLists);

  if (!list) return null;

  const save = async () => {

    if (!list?.id) return; // safety

    try {
      await api.put(`/lists/${list.id}`, {
        title
      });

      await loadLists(list.boardId);
      setEditing(false);

    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="bg-gray-100 rounded p-3 w-64">

      {editing ? (
        <input
          value={title}
          onChange={e => setTitle(e.target.value)}
          onBlur={save}
          onKeyDown={e => e.key === "Enter" && save()}
          className="w-full border rounded p-1 text-sm"
          autoFocus
        />
      ) : (
        <h3
          onClick={() => setEditing(true)}
          className="font-semibold cursor-pointer"
        >
          {list.title}
        </h3>
      )}

    </div>
  );
}
