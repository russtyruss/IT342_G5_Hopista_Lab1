import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../config/axios";

const Register = () => {
    const navigate = useNavigate();
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        setSuccess("");

        if (!username || !email || !password) {
            setError("Please fill in all required fields.");
            return;
        }
        if (password !== confirmPassword) {
            setError("Passwords do not match.");
            return;
        }

        setLoading(true);
        try {
            await api.post("/auth/register", { username, email, password });

            setSuccess("Account created! You can now sign in.");
            setTimeout(() => navigate("/login", { replace: true }), 600);
        } catch (err) {
            console.error("Registration failed", err);
            const message =
                err?.response?.data?.message || err?.message || "Registration failed";
            setError(message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: 480, margin: "40px auto" }}>
            <h2>Create Account</h2>
            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: 12 }}>
                    <label htmlFor="username">Username</label>
                    <input
                        id="username"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="yourname"
                        style={{ width: "100%", padding: 8, marginTop: 6 }}
                        autoComplete="username"
                    />
                </div>
                <div style={{ marginBottom: 12 }}>
                    <label htmlFor="email">Email</label>
                    <input
                        id="email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="you@example.com"
                        style={{ width: "100%", padding: 8, marginTop: 6 }}
                        autoComplete="email"
                    />
                </div>
                <div style={{ marginBottom: 12 }}>
                    <label htmlFor="password">Password</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="••••••••"
                        style={{ width: "100%", padding: 8, marginTop: 6 }}
                        autoComplete="new-password"
                    />
                </div>
                <div style={{ marginBottom: 12 }}>
                    <label htmlFor="confirmPassword">Confirm Password</label>
                    <input
                        id="confirmPassword"
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        placeholder="••••••••"
                        style={{ width: "100%", padding: 8, marginTop: 6 }}
                        autoComplete="new-password"
                    />
                </div>
                {error && (
                    <div style={{ color: "#b00020", marginBottom: 12 }}>{error}</div>
                )}
                {success && (
                    <div style={{ color: "#0b8e00", marginBottom: 12 }}>{success}</div>
                )}
                <button
                    type="submit"
                    disabled={loading}
                    style={{ width: "100%", padding: 10 }}
                >
                    {loading ? "Creating account..." : "Register"}
                </button>
            </form>
            <div style={{ marginTop: 12, textAlign: "center" }}>
                <span>Already have an account? </span>
                <button
                    type="button"
                    onClick={() => navigate("/login")}
                    style={{ padding: 6 }}
                >
                    Go to Login
                </button>
            </div>
        </div>
    );
};

export default Register;
