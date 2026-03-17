import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuthStore } from "../stores/authStore";

import Input from "../components/ui/Input";
import Button from "../components/ui/Button";

export default function Register() {

  const register = useAuthStore(s => s.register);
  const loading = useAuthStore(s => s.loading);

  const nav = useNavigate();

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const submit = async (e) => {
    e.preventDefault();
    setError("");

    try {
      await register(name, email, password);
      nav("/login");
    } catch (err) {
      setError(
        err?.response?.data?.message ||
        "Register failed"
      );
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">

      <form
        onSubmit={submit}
        className="bg-white p-8 rounded-xl shadow w-96 flex flex-col gap-4"
      >
        <h2 className="text-2xl font-bold text-center">
          Register
        </h2>

        {error && (
          <div className="text-red-600 text-sm">
            {error}
          </div>
        )}

        <Input
          label="Name"
          value={name}
          onChange={e => setName(e.target.value)}
        />

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
          Register
        </Button>

        <p className="text-sm text-center">
          Already have account?{" "}
          <Link to="/login" className="text-blue-600">
            Login
          </Link>
        </p>

      </form>
    </div>
  );
}
