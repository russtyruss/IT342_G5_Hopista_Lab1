import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api, { setTokens } from "../config/axios";

const Login = () => {
    const navigate = useNavigate();
    const [usernameOrEmail, setUsernameOrEmail] = useState("");
    const [password, setPassword] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");
        if (!usernameOrEmail || !password) {
            setError("Please enter your username/email and password.");
            return;
        }
        setLoading(true);
        try {
            const { data } = await api.post("/auth/login", {
                usernameOrEmail,
                password,
            });

            const { accessToken, refreshToken } = data || {};
            if (!accessToken || !refreshToken) {
                throw new Error("Login response missing tokens");
            }

            setTokens({ accessToken, refreshToken });
            navigate("/", { replace: true });
        } catch (err) {
            console.error("Login failed", err);
            const message =
                err?.response?.data?.message || err?.message || "Login failed";
            setError(message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: 420, margin: "40px auto" }}>
            <h2>Sign In</h2>
            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: 12 }}>
                    <label htmlFor="usernameOrEmail">Username or Email</label>
                    <input
                        id="usernameOrEmail"
                        type="text"
                        value={usernameOrEmail}
                        onChange={(e) => setUsernameOrEmail(e.target.value)}
                        placeholder="yourname or you@example.com"
                        style={{ width: "100%", padding: 8, marginTop: 6 }}
                        autoComplete="username"
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
                        autoComplete="current-password"
                    />
                </div>
                {error && (
                    <div style={{ color: "#b00020", marginBottom: 12 }}>{error}</div>
                )}
                <button
                    type="submit"
                    disabled={loading}
                    style={{ width: "100%", padding: 10 }}
                >
                    {loading ? "Signing in..." : "Sign In"}
                </button>
            </form>
            <div style={{ marginTop: 12, textAlign: "center" }}>
                <span>Don't have an account? </span>
                <button
                    type="button"
                    onClick={() => navigate("/register")}
                    style={{ padding: 6 }}
                >
                    Go to Register
                </button>
            </div>
        </div>
    );
};

export default Login;
