import axiosInstance from "../config/axios";

// API endpoints for authentication
export const authAPI = {
  // Login
  login: async (username, password) => {
    return axiosInstance.post("/auth/login", {
      username,
      password,
    });
  },

  // Register
  register: async (data) => {
    return axiosInstance.post("/auth/register", data);
  },

  // Logout
  logout: async () => {
    return axiosInstance.post("/auth/logout");
  },

  // Get current user
  getCurrentUser: async () => {
    return axiosInstance.get("/auth/me");
  },

  // Refresh token
  refreshToken: async (refreshToken) => {
    return axiosInstance.post("/auth/refresh", {
      refreshToken,
    });
  },

  // Update profile
  updateProfile: async (data) => {
    return axiosInstance.put("/auth/profile", data);
  },

  // Change password
  changePassword: async (currentPassword, newPassword) => {
    return axiosInstance.post("/auth/change-password", {
      currentPassword,
      newPassword,
    });
  },

  // Forgot password
  forgotPassword: async (email) => {
    return axiosInstance.post("/auth/forgot-password", {
      email,
    });
  },

  // Reset password
  resetPassword: async (token, newPassword) => {
    return axiosInstance.post("/auth/reset-password", {
      token,
      newPassword,
    });
  },
};
