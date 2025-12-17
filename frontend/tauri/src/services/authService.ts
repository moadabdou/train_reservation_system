import axios from "axios";

const API_URL = "http://localhost:8080/api/auth";

export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    name: string;
    email: string;
    password: string;
}

export interface AuthResponse {
    token: string;
    email: string;
    name: string;
    role: string;
}

export const login = async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await axios.post(`${API_URL}/login`, credentials);
    const data = response.data;

    // Store the token in localStorage
    if (data.token) {
        localStorage.setItem("token", data.token);
        localStorage.setItem(
            "user",
            JSON.stringify({
                email: data.email,
                name: data.name,
                role: data.role,
            })
        );
    }

    return data;
};

export const register = async (userData: RegisterRequest): Promise<AuthResponse> => {
    const response = await axios.post(`${API_URL}/register`, userData);
    return response.data;
};

export const logout = (): void => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
};

export const validateToken = async (): Promise<AuthResponse> => {
    const token = localStorage.getItem("token");
    if (!token) {
        throw new Error("No token found");
    }
    const response = await axios.get(`${API_URL}/me`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
    return response.data;
};

export const getToken = (): string | null => {
    return localStorage.getItem("token");
};

export const getCurrentUser = (): { email: string; name: string; role: string } | null => {
    const userStr = localStorage.getItem("user");
    if (userStr) {
        return JSON.parse(userStr);
    }
    return null;
};

export const isAuthenticated = (): boolean => {
    return getToken() !== null;
};
