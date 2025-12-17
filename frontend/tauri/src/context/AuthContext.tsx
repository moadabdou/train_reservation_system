import React, { createContext, useContext, useState, useEffect, ReactNode } from "react";
import { getToken, logout as authLogout, validateToken } from "../services/authService";

interface User {
    email: string;
    name: string;
    role: string;
}

interface AuthContextType {
    user: User | null;
    token: string | null;
    isLoggedIn: boolean;
    setAuth: (user: User, token: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);

    useEffect(() => {
        const checkAuth = async () => {
            const storedToken = getToken();
            if (storedToken) {
                try {
                    const userData = await validateToken();
                    setToken(storedToken);
                    setUser({
                        email: userData.email,
                        name: userData.name,
                        role: userData.role,
                    });
                } catch (error) {
                    console.error("Token validation failed:", error);
                    logout();
                }
            }
        };
        checkAuth();
    }, []);

    const setAuth = (user: User, token: string) => {
        setUser(user);
        setToken(token);
    };

    const logout = () => {
        authLogout();
        setUser(null);
        setToken(null);
    };

    return (
        <AuthContext.Provider value={{ user, token, isLoggedIn: !!token, setAuth, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};
