import React from "react";
import { BrowserRouter, Routes, Route, Navigate, useLocation } from "react-router-dom";
import Navbar from "./components/Navbar";
import Dashboard from "./components/Dashboard";
import Login from "./components/Login";
import Register from "./components/Register";
import Profile from "./components/Profile";

// Simple auth gate using localStorage tokens
const RequireAuth = ({ children }) => {
    const authed = Boolean(localStorage.getItem("accessToken"));
    return authed ? children : <Navigate to="/login" replace />;
};

// Layout inside Router so it re-renders on navigation
const AppShell = () => {
    const location = useLocation();
    const authed = Boolean(localStorage.getItem("accessToken"));
    return (
        <div data-route={location.pathname}>
            {authed && <Navbar />}
            <Routes>
                <Route path="/" element={<RequireAuth><Dashboard /></RequireAuth>} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/profile" element={<RequireAuth><Profile /></RequireAuth>} />
                <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
        </div>
    );
};

function MyApp() {
    return (
        <BrowserRouter>
            <AppShell />
        </BrowserRouter>
    );
}

export default MyApp;
