import { createSlice } from "@reduxjs/toolkit";
import { tokenStorage } from "../../utils/tokenStorage";

const initialState = {
  username: null,
  accessToken: tokenStorage.getAccessToken(),
  refreshToken: tokenStorage.getRefreshToken(),
  isAuthenticated: tokenStorage.isLoggedIn(),
  loading: false,
  error: null,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    // Login actions
    loginStart: (state) => {
      state.loading = true;
      state.error = null;
    },
    loginSuccess: (state, action) => {
      const { username, accessToken, refreshToken } = action.payload;
      state.username = username;
      state.accessToken = accessToken;
      state.refreshToken = refreshToken;
      state.isAuthenticated = true;
      state.loading = false;
      state.error = null;

      // Lưu tokens: accessToken vào localStorage, refreshToken vào cookie
      tokenStorage.setTokens(accessToken, refreshToken);
    },
    loginFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
      state.isAuthenticated = false;
    },

    // Logout actions
    logout: (state) => {
      state.username = null;
      state.accessToken = null;
      state.refreshToken = null;
      state.isAuthenticated = false;
      state.loading = false;
      state.error = null;

      // Xóa tất cả tokens
      tokenStorage.clearTokens();
    },

    // Register actions
    registerStart: (state) => {
      state.loading = true;
      state.error = null;
    },
    registerSuccess: (state, action) => {
      // Chỉ lưu user info, KHÔNG auto login
      // User phải đăng nhập bằng credentials
      state.username = action.payload?.username || null;
      state.loading = false;
      state.error = null;
      // isAuthenticated vẫn = false (chưa login)
    },
    registerFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
    },

    // Get current user
    getCurrentUserStart: (state) => {
      state.loading = true;
    },
    getCurrentUserSuccess: (state, action) => {
      state.username = action.payload?.username || null;
      state.loading = false;
      state.error = null;
    },
    getCurrentUserFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
    },

    // Update profile
    updateProfileStart: (state) => {
      state.loading = true;
      state.error = null;
    },
    updateProfileSuccess: (state, action) => {
      state.username = action.payload?.username || null;
      state.loading = false;
      state.error = null;
    },
    updateProfileFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
    },

    // Refresh token
    refreshTokenSuccess: (state, action) => {
      const { accessToken, refreshToken } = action.payload;
      state.accessToken = accessToken;
      state.refreshToken = refreshToken;

      localStorage.setItem("authToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);
    },
    refreshTokenFailure: (state) => {
      state.accessToken = null;
      state.refreshToken = null;
      state.isAuthenticated = false;
      state.username = null;

      localStorage.removeItem("authToken");
      localStorage.removeItem("refreshToken");
    },

    // Clear error
    clearError: (state) => {
      state.error = null;
    },
  },
});

export const {
  loginStart,
  loginSuccess,
  loginFailure,
  logout,
  registerStart,
  registerSuccess,
  registerFailure,
  getCurrentUserStart,
  getCurrentUserSuccess,
  getCurrentUserFailure,
  updateProfileStart,
  updateProfileSuccess,
  updateProfileFailure,
  refreshTokenSuccess,
  refreshTokenFailure,
  clearError,
} = authSlice.actions;

export default authSlice.reducer;
