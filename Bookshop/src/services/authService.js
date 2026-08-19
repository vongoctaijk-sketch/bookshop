import axiosInstance from "../config/axios";

// API endpoints for authentication
export const authAPI = {
  // Login
  login: async (username, password) => {
    const response = await axiosInstance.post("/auth/login", {
      username,
      password,
    });
    return response.data;
  },

  // Register
  register: async (data) => {
    const response = await axiosInstance.post("/auth/register", data);
    return response.data;
  },

  // Logout
  logout: async () => {
    const response = await axiosInstance.post("/auth/logout");
    return response.data;
  },

  // Get current user
  getCurrentUser: async () => {
    const response = await axiosInstance.get("/auth/me");
    return response.data;
  },

  // Refresh token
  refreshToken: async (refreshToken) => {
    const response = await axiosInstance.post("/auth/refresh", {
      refreshToken,
    });
    return response.data;
  },

  // Update profile
  updateProfile: async (data) => {
    const response = await axiosInstance.put("/auth/profile", data);
    return response.data;
  },

  // Change password
  changePassword: async (currentPassword, newPassword) => {
    const response = await axiosInstance.post("/auth/change-password", {
      currentPassword,
      newPassword,
    });
    return response.data;
  },

  // Forgot password
  forgotPassword: async (email) => {
    const response = await axiosInstance.post("/auth/forgot-password", {
      email,
    });
    return response.data;
  },

  // Reset password
  resetPassword: async (token, newPassword) => {
    const response = await axiosInstance.post("/auth/reset-password", {
      token,
      newPassword,
    });
    return response.data;
  },
};
