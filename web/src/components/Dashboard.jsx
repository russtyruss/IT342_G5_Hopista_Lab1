import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

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

const Dashboard = () => {
    const [username, setUsername] = useState("");

    useEffect(() => {
        const accessToken = localStorage.getItem("accessToken");
        if (accessToken) {
            const payload = decodeJwt(accessToken);
            setUsername(payload?.sub || "");
        } else {
            setUsername("");
        }
    }, []);

    return (
        <main style={{ maxWidth: 800, margin: "24px auto", padding: "0 16px" }}>
            <h2>Dashboard</h2>
            <p>
                {username ? (
                    <>Welcome, <strong>{username}</strong>!</>
                ) : (
                    <>Welcome!</>
                )}
            </p>
            
            <hr style={{ margin: "20px 0" }} />
            <p>
                This area is ready for backend-driven widgets (recent activity, stats,
                etc.). Once you expose endpoints like <code>/api/user/me</code> or
                <code>/api/dashboard</code>, we can fetch and render live data here.
            </p>
        </main>
    );
};

export default Dashboard;
