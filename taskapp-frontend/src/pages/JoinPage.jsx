import { useParams, useNavigate } from "react-router-dom";
import { useEffect } from "react";
import api from "../services/api";
import { useAuthStore } from "../stores/authStore";

export default function JoinPage() {

  const { code } = useParams();
  const nav = useNavigate();

  const accessToken = useAuthStore(s => s.accessToken);
  const hydrated = useAuthStore(s => s.hydrated);

  useEffect(() => {

    
    if (!hydrated) return;

    
    if (!accessToken) {
      nav("/login");
      return;
    }

    async function join() {
      try {
        await api.post(`/boards/join/${code}`);
        nav("/app/boards");   
      } catch (e) {
        console.error(e);
        alert("Join failed");
      }
    }

    join();

  }, [code, hydrated, accessToken]);

  return (
    <div className="p-10 text-center">
      Joining board...
    </div>
  );
}
