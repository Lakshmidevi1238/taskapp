import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuthStore } from "../stores/authStore";
import api from "../services/api";

import Input from "../components/ui/Input";
import Button from "../components/ui/Button";

export default function Login() {

  const login = useAuthStore(s => s.login);
  const loading = useAuthStore(s => s.loading);

  const nav = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  // Forgot Password
  const [showForgot, setShowForgot] = useState(false);
  const [resetEmail, setResetEmail] = useState("");
  const [resetToken, setResetToken] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [resetMessage, setResetMessage] = useState("");

  // ================= LOGIN =================
  const submit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      await login(email, password);
      nav("/app/boards");
    } catch (err) {
      setError(
        err?.response?.data?.message ||
        "Login failed"
      );
    }
  };

  // ================= GENERATE RESET TOKEN =================
  const handleForgot = async () => {
    setResetMessage("");
    setResetToken("");

    try {
      const res = await api.post("/api/auth/forgot-password", {
        email: resetEmail
      });

      setResetToken(res.data.resetToken);
      setResetMessage("Reset token generated.");
    } catch (err) {
      setResetMessage(
        err?.response?.data?.message ||
        "Failed to generate reset token"
      );
    }
  };

  // ================= RESET PASSWORD =================
  const handleReset = async () => {
    try {
      await api.post("/api/auth/reset-password", {
        token: resetToken,
        newPassword
      });

      setResetMessage("Password updated successfully.");
      setShowForgot(false);
    } catch (err) {
      setResetMessage(
        err?.response?.data?.message ||
        "Password reset failed"
      );
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">

      {/* ================= LOGIN FORM ================= */}
      <form
        onSubmit={submit}
        className="bg-white p-8 rounded-xl shadow w-96 flex flex-col gap-4"
      >
        <h2 className="text-2xl font-bold text-center">
          Login
        </h2>

        {error && (
          <div className="text-red-600 text-sm">
            {error}
          </div>
        )}

        <Input
          label="Email"
          type="email"
          value={email}
          onChange={e => setEmail(e.target.value)}
        />

        <Input
          label="Password"
          type="password"
          value={password}
          onChange={e => setPassword(e.target.value)}
        />

        <Button loading={loading}>
          Login
        </Button>

        {/* 🔹 Forgot Password Link */}
        <button
          type="button"
          onClick={() => setShowForgot(true)}
          className="text-sm text-blue-600 text-right"
        >
          Forgot password?
        </button>

        <p className="text-sm text-center">
          No account?{" "}
          <Link to="/register" className="text-blue-600">
            Register
          </Link>
        </p>

      </form>

      {/* ================= FORGOT PASSWORD MODAL ================= */}
      {showForgot && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center">

          <div className="bg-white p-6 rounded-lg w-96 flex flex-col gap-4">

            <h3 className="text-lg font-semibold">
              Reset Password
            </h3>

            {!resetToken && (
              <>
                <Input
                  label="Enter your email"
                  type="email"
                  value={resetEmail}
                  onChange={e => setResetEmail(e.target.value)}
                />

                <Button onClick={handleForgot}>
                  Generate Reset Token
                </Button>
              </>
            )}

            {resetToken && (
              <>
                <div className="text-xs bg-gray-100 p-2 rounded break-all">
                  Token: {resetToken}
                </div>

                <Input
                  label="New Password"
                  type="password"
                  value={newPassword}
                  onChange={e => setNewPassword(e.target.value)}
                />

                <Button onClick={handleReset}>
                  Update Password
                </Button>
              </>
            )}

            {resetMessage && (
              <div className="text-sm text-green-600">
                {resetMessage}
              </div>
            )}

            <button
              onClick={() => {
                setShowForgot(false);
                setResetToken("");
                setResetMessage("");
              }}
              className="text-xs text-gray-500"
            >
              Close
            </button>

          </div>
        </div>
      )}

    </div>
  );
}
