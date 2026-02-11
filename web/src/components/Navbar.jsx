import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api, { clearTokens } from "../config/axios";

// Helper to decode JWT and read username from `sub`
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

const Navbar = () => {
    const navigate = useNavigate();
    const [isAuthed, setIsAuthed] = useState(false);
    const [username, setUsername] = useState("");

    const updateAuthState = () => {
        const accessToken = localStorage.getItem("accessToken");
        const authed = Boolean(accessToken);
        setIsAuthed(authed);
        if (authed) {
            const payload = decodeJwt(accessToken);
            const name = payload?.sub || "";
            setUsername(name);
        } else {
            setUsername("");
        }
    };

    useEffect(() => {
        updateAuthState();
        const onStorage = (e) => {
            if (e.key === "accessToken" || e.key === "refreshToken") {
                updateAuthState();
            }
        };
        window.addEventListener("storage", onStorage);
        return () => window.removeEventListener("storage", onStorage);
    }, []);

    const handleLogout = async () => {
        try {
            const refreshToken = localStorage.getItem("refreshToken");
            if (refreshToken) {
                await api.post("/auth/logout", { refreshToken });
            }
        } catch (err) {
            console.error("Logout error", err);
        } finally {
            clearTokens();
            updateAuthState();
            navigate("/login", { replace: true });
        }
    };

    return (
        <nav style={{ padding: "10px 16px", borderBottom: "1px solid #eee" }}>
            <div style={{ display: "flex", alignItems: "center", gap: 16 }}>
                <Link to="/" style={{ fontWeight: 600 }}>Home</Link>

                <div style={{ marginLeft: "auto", display: "flex", gap: 12 }}>
                    {!isAuthed ? (
                        <>
                            <Link to="/login">Login</Link>
                            <Link to="/register">Register</Link>
                        </>
                    ) : (
                        <>
                            <span style={{ opacity: 0.8 }}>Hello{username ? `, ${username}` : ""}</span>
                            <Link to="/profile">Profile</Link>
                            <button onClick={handleLogout}>Logout</button>
                        </>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
