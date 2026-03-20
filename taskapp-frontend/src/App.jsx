import { Routes, Route } from "react-router-dom";

import Login from "./pages/Login";
import Register from "./pages/Register";
import BoardsPage from "./pages/BoardsPage";
import BoardWorkspace from "./pages/BoardWorkspace";

import AppLayout from "./components/layout/AppLayout";
import ProtectedRoute from "./components/layout/ProtectedRoute";
import JoinPage from "./pages/JoinPage";


export default function App() {
  return (
    <Routes>

      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      {/* Boards list */}
      <Route
        path="/app/boards"
        element={
          <ProtectedRoute>
            <AppLayout>
              <BoardsPage />
            </AppLayout>
          </ProtectedRoute>
        }
      />

      {/* Single board workspace */}
      <Route
        path="/app/boards/:id"
        element={
          <ProtectedRoute>
            <AppLayout>
              <BoardWorkspace />
            </AppLayout>
          </ProtectedRoute>
        }
      />
      <Route
  path="/app/join/:code"
  element={
    <ProtectedRoute>
      <JoinPage />
    </ProtectedRoute>
  }
/>


    </Routes>
  );
}
