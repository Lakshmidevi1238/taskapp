import { useState, useEffect, useRef } from "react";
import {
  FolderKanban,
  Plus,
  LayoutList,
  CheckSquare,
  Send,
  LogIn,
  Home
} from "lucide-react";

import { useBoardStore } from "../../stores/boardStore";
import { useNavigate, useParams } from "react-router-dom";
import { useAuthStore } from "../../stores/authStore";
import { useDrag } from "react-dnd";
import { DND_TYPES } from "../../utils/dndTypes";

export default function Sidebar() {
  const { boards, loadBoards, createBoard } = useBoardStore();
  const hydrated = useAuthStore(s => s.hydrated);

  const nav = useNavigate();
  const { id: currentBoardId } = useParams();

  const [openBoards, setOpenBoards] = useState(false);
  const [creating, setCreating] = useState(false);
  const [title, setTitle] = useState("");

  const [showShare, setShowShare] = useState(false);
  const [showJoin, setShowJoin] = useState(false);
  const [joinLink, setJoinLink] = useState("");

  const popupRef = useRef(null);

  // ===== LOAD BOARDS =====
  useEffect(() => {
    if (hydrated) loadBoards();
  }, [hydrated]);

  // ===== CLOSE POPUPS ON OUTSIDE CLICK =====
  useEffect(() => {
    function handleClick(e) {
      if (popupRef.current && popupRef.current.contains(e.target)) return;

      setOpenBoards(false);
      setCreating(false);
      setShowShare(false);
      setShowJoin(false);
    }

    document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, []);

  // ===== CREATE BOARD =====
  const submit = async (e) => {
    if (e.key === "Enter" && title.trim()) {
      const newBoard = await createBoard(title, "");
      setTitle("");
      setCreating(false);
      setOpenBoards(false);
      nav(`/app/boards/${newBoard.id}`);
    }
  };

  const currentBoard =
    boards.find(b => String(b.id) === String(currentBoardId));

  const shareLink = currentBoard
    ? `${window.location.origin}/app/join/${currentBoard.inviteCode}`
    : "";

  const copyLink = async () => {
    await navigator.clipboard.writeText(shareLink);
    alert("Link copied");
  };

  const handleJoin = () => {
    if (!joinLink.trim()) return;
    const code = joinLink.split("/").pop();
    nav(`/app/join/${code}`);
  };

  // ===== DRAG ICON =====
  function DragIcon({ type, children, label }) {
    const [{ isDragging }, drag] = useDrag(() => ({
      type,
      item: { type },
      collect: m => ({ isDragging: m.isDragging() })
    }));

    return (
      <div
        ref={drag}
        className="flex flex-col items-center gap-1 cursor-grab text-gray-600"
        style={{ opacity: isDragging ? 0.4 : 1 }}
      >
        {children}
        <span className="text-[11px]">{label}</span>
      </div>
    );
  }

  // ===== ICON BUTTON =====
  function IconBtn({ icon, label, onClick }) {
    return (
      <button
        onClick={onClick}
        className="flex flex-col items-center gap-1 text-gray-700 hover:text-black"
      >
        {icon}
        <span className="text-[11px]">{label}</span>
      </button>
    );
  }

  return (
<div className="w-20 border-r bg-white flex flex-col items-center py-6 gap-8 relative shadow-sm">


      {/* ================= HOME ================= */}
      <IconBtn
        icon={<Home size={22} />}
        label="Home"
        onClick={() => nav("/app/boards")}
      />

      {/* ================= BOARDS ================= */}
      <div ref={popupRef} className="relative">
        <IconBtn
          icon={<FolderKanban size={22} />}
          label="Boards"
          onClick={() => setOpenBoards(v => !v)}
        />

        {openBoards && (
          <div className="absolute left-16 top-0 w-56 bg-white border rounded shadow p-3 z-50">

            <button
              onClick={() => setCreating(true)}
              className="flex items-center gap-2 text-sm text-blue-600 mb-2"
            >
              <Plus size={16} /> New Board
            </button>

            {creating && (
              <input
                autoFocus
                value={title}
                onChange={e => setTitle(e.target.value)}
                onKeyDown={submit}
                className="border p-2 w-full mb-2 text-sm"
              />
            )}

            {boards.map(b => (
              <button
                key={b.id}
                onClick={() => nav(`/app/boards/${b.id}`)}
                className="block w-full text-left hover:bg-gray-100 p-1 rounded text-sm"
              >
                {b.title}
              </button>
            ))}
          </div>
        )}
      </div>

      {/* ================= SHARE ================= */}
      <IconBtn
        icon={<Send size={22} />}
        label="Send"
        onClick={() => setShowShare(true)}
      />

      {showShare && currentBoard && (
        <div ref={popupRef}
             className="absolute left-24 top-40 w-64 bg-white border rounded shadow p-3 z-50">
          <input
            readOnly
            value={shareLink}
            className="border p-2 w-full text-xs mb-2"
          />
          <button
            onClick={copyLink}
            className="bg-blue-600 text-white px-3 py-1 rounded text-sm"
          >
            Copy
          </button>
        </div>
      )}

      {/* ================= JOIN ================= */}
      <IconBtn
        icon={<LogIn size={22} />}
        label="Join"
        onClick={() => setShowJoin(true)}
      />

      {showJoin && (
        <div ref={popupRef}
             className="absolute left-24 top-56 w-64 bg-white border rounded shadow p-3 z-50">
          <input
            value={joinLink}
            onChange={e => setJoinLink(e.target.value)}
            placeholder="Paste board link"
            className="border p-2 w-full mb-2 text-sm"
          />
          <button
            onClick={handleJoin}
            className="bg-green-600 text-white px-3 py-1 rounded text-sm"
          >
            Join
          </button>
        </div>
      )}

      {/* ================= DRAG ICONS ================= */}
      <DragIcon type={DND_TYPES.LIST_ICON} label="Lists">
        <LayoutList size={22} />
      </DragIcon>

      <DragIcon type={DND_TYPES.TASK_ICON} label="Tasks">
        <CheckSquare size={22} />
      </DragIcon>

    </div>
  );
}
