import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { setTokens, clearTokens } from "../config/axios";

const decodeJwt = (token) => {
    try {
        const base64Url = token.split(".")[1];
        const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split("")
                .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
                .join("")
        );
        return JSON.parse(jsonPayload);
    } catch (e) {
        return null;
    }
};

const fmt = (tsSeconds) => {
    if (!tsSeconds) return "-";
    const d = new Date(tsSeconds * 1000);
    return d.toLocaleString();
};

const Profile = () => {
    const navigate = useNavigate();
    const [claims, setClaims] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const loadClaims = () => {
        const accessToken = localStorage.getItem("accessToken");
        if (!accessToken) {
            setClaims(null);
            return;
        }
        const payload = decodeJwt(accessToken);
        setClaims(payload);
    };

    useEffect(() => {
        loadClaims();
        const onStorage = (e) => {
            if (e.key === "accessToken" || e.key === "refreshToken") {
                loadClaims();
            }
        };
        window.addEventListener("storage", onStorage);
        return () => window.removeEventListener("storage", onStorage);
    }, []);

    const handleRefresh = async () => {
        setLoading(true);
        setError("");
        try {
            const refreshToken = localStorage.getItem("refreshToken");
            if (!refreshToken) throw new Error("No refresh token found");
            const { data } = await api.post("/auth/refresh", { refreshToken });
            const newAccessToken = data?.accessToken;
            if (!newAccessToken) throw new Error("Refresh did not return accessToken");
            setTokens({ accessToken: newAccessToken });
            loadClaims();
        } catch (err) {
            console.error("Refresh error", err);
            setError(err?.response?.data?.message || err?.message || "Refresh failed");
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = async () => {
        setLoading(true);
        setError("");
        try {
            const refreshToken = localStorage.getItem("refreshToken");
            if (refreshToken) {
                await api.post("/auth/logout", { refreshToken });
            }
        } catch (err) {
            console.error("Logout error", err);
        } finally {
            clearTokens();
            setLoading(false);
            navigate("/login", { replace: true });
        }
    };

    const authed = Boolean(localStorage.getItem("accessToken"));

    if (!authed) {
        return (
            <div style={{ maxWidth: 640, margin: "40px auto" }}>
                <h2>Profile</h2>
                <p>You are not signed in.</p>
                <Link to="/login">Go to Login</Link>
            </div>
        );
    }

    return (
        <div style={{ maxWidth: 640, margin: "40px auto" }}>
            <h2>Profile</h2>
            {error && <div style={{ color: "#b00020", marginBottom: 12 }}>{error}</div>}
            <div style={{ padding: 12, border: "1px solid #eee", borderRadius: 6 }}>
                <div><strong>Username</strong>: {claims?.sub || "-"}</div>
                <div><strong>Issued At</strong>: {fmt(claims?.iat)}</div>
                <div><strong>Expires At</strong>: {fmt(claims?.exp)}</div>
            </div>

            <div style={{ display: "flex", gap: 8, marginTop: 16 }}>
                <button onClick={handleLogout} disabled={loading}>
                    {loading ? "Signing out..." : "Logout"}
                </button>
            </div>
        </div>
    );
};

export default Profile;
