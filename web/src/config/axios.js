import axios from "axios";

// Base URL matches backend port and API prefix
const api = axios.create({
    baseURL: "http://localhost:8081/api",
    headers: { "Content-Type": "application/json" },
});

// Helpers to manage tokens
export const setTokens = ({ accessToken, refreshToken }) => {
    if (accessToken) localStorage.setItem("accessToken", accessToken);
    if (refreshToken) localStorage.setItem("refreshToken", refreshToken);
};

export const clearTokens = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
};

// Attach access token to every request
api.interceptors.request.use(
    (config) => {
        const accessToken = localStorage.getItem("accessToken");
        if (accessToken) {
            config.headers.Authorization = `Bearer ${accessToken}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Refresh token flow on 401 responses
let isRefreshing = false;
let pendingRequests = [];

const processQueue = (error, token = null) => {
    pendingRequests.forEach(({ resolve, reject }) => {
        if (error) reject(error);
        else resolve(token);
    });
    pendingRequests = [];
};

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const { response, config: originalRequest } = error || {};
        const status = response?.status;

        if (status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            const refreshToken = localStorage.getItem("refreshToken");
            if (!refreshToken) {
                clearTokens();
                return Promise.reject(error);
            }

            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    pendingRequests.push({ resolve, reject });
                })
                    .then((newAccessToken) => {
                        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                        return api(originalRequest);
                    })
                    .catch((err) => Promise.reject(err));
            }

            isRefreshing = true;
            try {
                const { data } = await api.post("/auth/refresh", { refreshToken });
                const newAccessToken = data?.accessToken;
                if (!newAccessToken) throw new Error("No accessToken in refresh response");

                setTokens({ accessToken: newAccessToken });
                api.defaults.headers.Authorization = `Bearer ${newAccessToken}`;
                processQueue(null, newAccessToken);

                originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                return api(originalRequest);
            } catch (refreshErr) {
                clearTokens();
                processQueue(refreshErr, null);
                return Promise.reject(refreshErr);
            } finally {
                isRefreshing = false;
            }
        }

        console.error("API Error:", error);
        return Promise.reject(error);
    }
);

export default api;
