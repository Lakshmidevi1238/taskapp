import { useEffect, useState } from "react";
import api from "../../services/api";

export default function ActivityPanel({ boardId }) {

  const [logs, setLogs] = useState([]);
  const [page, setPage] = useState(0);

  const load = async () => {
    const res = await api.get(
      `/boards/${boardId}/activity?page=${page}&size=20`
    );

    setLogs(res.data.content);
  };

  useEffect(() => {
    if (!boardId) return;
    load();
  }, [boardId, page]);

  return (
    <div className="w-80 border-l p-4 bg-gray-50 h-screen overflow-y-auto">

      <h2 className="font-semibold mb-4">
        Activity
      </h2>

      {logs.map(log => (
        <div
          key={log.id}
          className="mb-3 text-sm bg-white p-2 rounded shadow"
        >
          <div className="font-medium">
            {log.actionType}
          </div>

          <div className="text-gray-500 text-xs">
            {new Date(log.createdAt).toLocaleString()}
          </div>
        </div>
      ))}

      <button
        onClick={() => setPage(p => p + 1)}
        className="mt-4 text-blue-600 text-sm"
      >
        Load More
      </button>

    </div>
  );
}
