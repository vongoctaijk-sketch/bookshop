import { useSelector, useDispatch } from "react-redux";
import { useMutation, useQuery } from "@tanstack/react-query";
import { authAPI } from "../services/authService";
import {
  loginStart,
  loginSuccess,
  loginFailure,
  registerStart,
  registerSuccess,
  registerFailure,
  logout,
  getCurrentUserStart,
  getCurrentUserSuccess,
  getCurrentUserFailure,
  updateProfileStart,
  updateProfileSuccess,
  updateProfileFailure,
  clearError,
} from "../store/slices/authSlice";

/**
 * Hook để sử dụng authentication state
 */
export const useAuthState = () => {
  const { user, accessToken, isAuthenticated, loading, error } = useSelector(
    (state) => state.auth,
  );

  return {
    user,
    token: accessToken,
    accessToken,
    isAuthenticated,
    loading,
    error,
  };
};

/**
 * Hook cho Login mutation
 */
export const useLoginMutation = () => {
  const dispatch = useDispatch();

  return useMutation({
    mutationFn: async (credentials) => {
      dispatch(loginStart());
      try {
        const payload = await authAPI.login(
          credentials.username,
          credentials.password,
        );
        dispatch(loginSuccess(payload));
        return payload;
      } catch (error) {
        const errorMessage =
          error.response?.data?.message || "Đăng nhập thất bại";
        dispatch(loginFailure(errorMessage));
        throw error;
      }
    },
    onSuccess: (data) => {
      console.log("Login successful:", data);
    },
    onError: (error) => {
      console.error("Login error:", error);
    },
  });
};

/**
 * Hook cho Register mutation
 */
export const useRegisterMutation = () => {
  const dispatch = useDispatch();

  return useMutation({
    mutationFn: async (formData) => {
      dispatch(registerStart());
      try {
        const payload = await authAPI.register(formData);
        dispatch(registerSuccess(payload));
        return payload;
      } catch (error) {
        const errorMessage =
          error.response?.data?.message || "Đăng ký thất bại";
        dispatch(registerFailure(errorMessage));
        throw error;
      }
    },
    onSuccess: (data) => {
      console.log("Register successful:", data);
    },
    onError: (error) => {
      console.error("Register error:", error);
    },
  });
};

/**
 * Hook cho Logout
 */
export const useLogout = () => {
  const dispatch = useDispatch();

  return useMutation({
    mutationFn: async () => {
      try {
        await authAPI.logout();
      } catch (error) {
        console.error("Logout error:", error);
      } finally {
        dispatch(logout());
      }
    },
    onSuccess: () => {
      console.log("Logout successful");
    },
  });
};

/**
 * Hook để lấy thông tin user hiện tại
 */
export const useGetCurrentUser = () => {
  const dispatch = useDispatch();
  const { token } = useAuthState();

  return useQuery({
    queryKey: ["currentUser", token],
    queryFn: async () => {
      dispatch(getCurrentUserStart());
      try {
        const payload = await authAPI.getCurrentUser();
        dispatch(getCurrentUserSuccess(payload));
        return payload;
      } catch (error) {
        dispatch(
          getCurrentUserFailure(
            error.response?.data?.message || "Failed to fetch user",
          ),
        );
        throw error;
      }
    },
    enabled: !!token, // Chỉ chạy khi có token
    staleTime: 5 * 60 * 1000, // 5 minutes
    gcTime: 10 * 60 * 1000, // 10 minutes (formerly cacheTime)
  });
};

/**
 * Hook để update profile
 */
export const useUpdateProfileMutation = () => {
  const dispatch = useDispatch();

  return useMutation({
    mutationFn: async (profileData) => {
      dispatch(updateProfileStart());
      try {
        const payload = await authAPI.updateProfile(profileData);
        dispatch(updateProfileSuccess(payload.user ?? payload));
        return payload;
      } catch (error) {
        dispatch(
          updateProfileFailure(
            error.response?.data?.message || "Update failed",
          ),
        );
        throw error;
      }
    },
    onSuccess: (data) => {
      console.log("Profile updated successfully:", data);
    },
    onError: (error) => {
      console.error("Update profile error:", error);
    },
  });
};

/**
 * Hook để change password
 */
export const useChangePasswordMutation = () => {
  return useMutation({
    mutationFn: async (passwords) => {
      return authAPI.changePassword(
        passwords.currentPassword,
        passwords.newPassword,
      );
    },
    onSuccess: (data) => {
      console.log("Password changed successfully:", data);
    },
    onError: (error) => {
      console.error("Change password error:", error);
    },
  });
};

/**
 * Hook để forgot password
 */
export const useForgotPasswordMutation = () => {
  return useMutation({
    mutationFn: async (username) => {
      return authAPI.forgotPassword(username);
    },
    onSuccess: (data) => {
      console.log("Password reset email sent:", data);
    },
    onError: (error) => {
      console.error("Forgot password error:", error);
    },
  });
};

/**
 * Hook để clear error từ Redux state
 */
export const useClearAuthError = () => {
  const dispatch = useDispatch();
  return () => dispatch(clearError());
};
