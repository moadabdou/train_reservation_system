import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth';

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
}

export const login = async (credentials: LoginRequest): Promise<AuthResponse> => {
  const response = await axios.post(`${API_URL}/login`, credentials);
  const data = response.data;
  
  // Store the token in localStorage
  if (data.token) {
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify({ email: data.email, name: data.name }));
  }
  
  return data;
};

export const register = async (userData: RegisterRequest): Promise<AuthResponse> => {
  const response = await axios.post(`${API_URL}/register`, userData);
  return response.data;
};

export const logout = (): void => {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
};

export const getToken = (): string | null => {
  return localStorage.getItem('token');
};

export const getCurrentUser = (): { email: string; name: string } | null => {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    return JSON.parse(userStr);
  }
  return null;
};

export const isAuthenticated = (): boolean => {
  return getToken() !== null;
};
