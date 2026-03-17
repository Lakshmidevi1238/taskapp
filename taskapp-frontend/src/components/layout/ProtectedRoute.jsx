import { Navigate } from "react-router-dom";
import { useAuthStore } from "../../stores/authStore";

export default function ProtectedRoute({ children }) {

  const token = useAuthStore(s => s.accessToken);
  const hydrated = useAuthStore(s => s.hydrated);

  if (!hydrated) return null;  

  if (!token) {
    return <Navigate to="/login" />;
  }

  return children;
}
