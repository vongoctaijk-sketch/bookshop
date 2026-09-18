import { createSlice } from "@reduxjs/toolkit";
import { tokenStorage } from "../../utils/tokenStorage";

const normalizeAuthorities = (authorities) => {
  if (!Array.isArray(authorities)) return [];
  return authorities;
};

const buildUser = (payload) => {
  const authorities = normalizeAuthorities(payload?.authorities);
  const id = payload?.id ?? payload?.user?.id ?? null;
  const username = payload?.username || payload?.user?.username || null;
  const isAdmin = authorities.some(
    (authority) =>
      authority.toUpperCase() === "ROLE_ADMIN" ||
      authority.toUpperCase() === "ADMIN",
  );

  return {
    id,
    username,
    authorities,
    role: isAdmin ? "admin" : "user",
  };
};

const initialState = {
  user: null,
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
    loginStart: (state) => {
      state.loading = true;
      state.error = null;
    },
    loginSuccess: (state, action) => {
      const { accessToken, refreshToken } = action.payload;
      const user = buildUser(action.payload);

      state.user = user;
      state.username = user.username;
      state.accessToken = accessToken;
      state.refreshToken = refreshToken;
      state.isAuthenticated = true;
      state.loading = false;
      state.error = null;

      tokenStorage.setTokens(accessToken, refreshToken);
    },
    loginFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
      state.isAuthenticated = false;
    },

    logout: (state) => {
      state.user = null;
      state.username = null;
      state.accessToken = null;
      state.refreshToken = null;
      state.isAuthenticated = false;
      state.loading = false;
      state.error = null;

      tokenStorage.clearTokens();
    },

    registerStart: (state) => {
      state.loading = true;
      state.error = null;
    },
    registerSuccess: (state, action) => {
      state.username = action.payload?.username || null;
      state.loading = false;
      state.error = null;
    },
    registerFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
    },

    getCurrentUserStart: (state) => {
      state.loading = true;
    },
    getCurrentUserSuccess: (state, action) => {
      const user = buildUser(action.payload);
      state.user = user;
      state.username = user.username;
      state.loading = false;
      state.error = null;
    },
    getCurrentUserFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
    },

    updateProfileStart: (state) => {
      state.loading = true;
      state.error = null;
    },
    updateProfileSuccess: (state, action) => {
      const user = buildUser(action.payload);
      state.user = user;
      state.username = user.username;
      state.loading = false;
      state.error = null;
    },
    updateProfileFailure: (state, action) => {
      state.loading = false;
      state.error = action.payload;
    },

    refreshTokenSuccess: (state, action) => {
      const { accessToken, refreshToken } = action.payload;
      state.accessToken = accessToken;
      state.refreshToken = refreshToken;

      localStorage.setItem("authToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);
    },
    refreshTokenFailure: (state) => {
      state.user = null;
      state.accessToken = null;
      state.refreshToken = null;
      state.isAuthenticated = false;
      state.username = null;

      localStorage.removeItem("authToken");
      localStorage.removeItem("refreshToken");
    },

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
