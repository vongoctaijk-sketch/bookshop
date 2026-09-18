import axios from "axios";
import { tokenStorage } from "../utils/tokenStorage";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080/api",
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});

const unwrapApiResponse = (response) => {
  const body = response?.data;

  if (
    body &&
    typeof body === "object" &&
    Object.prototype.hasOwnProperty.call(body, "success") &&
    Object.prototype.hasOwnProperty.call(body, "data")
  ) {
    return {
      ...response,
      data: body.data,
      meta: {
        success: body.success,
        code: body.code,
        message: body.message,
      },
    };
  }

  return response;
};

// ==================== REQUEST INTERCEPTOR ====================
api.interceptors.request.use(
  (config) => {
    // Lấy accessToken từ localStorage
    const token = tokenStorage.getAccessToken();

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    console.log(
      `%c[API REQUEST] ${config.method?.toUpperCase()} ${config.baseURL}${config.url}`,
      "color: #2196f3; font-weight: bold;",
    );

    console.log("Params:", config.params);
    console.log("Body:", config.data);

    return config;
  },
  (error) => {
    console.error("[API REQUEST ERROR]", error);
    return Promise.reject(error);
  },
);

// ==================== RESPONSE INTERCEPTOR ====================
api.interceptors.response.use(
  (response) => {
    const normalizedResponse = unwrapApiResponse(response);

    console.log(
      `%c[API SUCCESS] ${normalizedResponse.status} ${normalizedResponse.config.url}`,
      "color: #4caf50; font-weight: bold;",
    );

    console.log("Response:", normalizedResponse.data);

    return normalizedResponse.data;
  },

  async (error) => {
    // Request đã gửi nhưng backend trả response
    if (error.response) {
      const { status, data, config } = error.response;

      console.group(
        `%c[API ERROR] ${status} ${config.method?.toUpperCase()} ${config.url}`,
        "color: #f44336; font-weight: bold;",
      );

      console.error("Status:", status);
      console.error("URL:", config.url);
      console.error("Method:", config.method?.toUpperCase());
      console.error("Params:", config.params);
      console.error("Request Body:", config.data);
      console.error("Response:", data);

      console.groupEnd();

      // 401 - Unauthorized (Token hết hạn hoặc không hợp lệ)
      if (status === 401) {
        console.warn("[AUTH] Unauthorized - Token has expired or is invalid");

        // Lấy refresh token từ cookie
        const refreshToken = tokenStorage.getRefreshToken();

        if (refreshToken && !config.url.includes("/auth/refresh")) {
          try {
            // Gọi refresh token endpoint
            const refreshResponse = await axios.post(
              `${import.meta.env.VITE_API_URL || "http://localhost:8080/api"}/auth/refresh`,
              { refreshToken },
            );

            const { token: newToken, refreshToken: newRefreshToken } =
              refreshResponse.data;

            // Cập nhật tokens (accessToken vào localStorage, refreshToken vào cookie)
            tokenStorage.setTokens(newToken, newRefreshToken);

            // Cập nhật token trong request hiện tại
            config.headers.Authorization = `Bearer ${newToken}`;

            // Retry request với token mới
            return api(config);
          } catch (refreshError) {
            console.error("[AUTH] Refresh token failed, logging out");
            // Xóa tokens và redirect to login
            tokenStorage.clearTokens();
            window.location.href = "/login";
          }
        } else {
          // Không có refresh token, redirect to login
          tokenStorage.clearTokens();
          window.location.href = "/login";
        }
      }

      // 403 - Forbidden
      if (status === 403) {
        console.warn("[AUTH] Forbidden - Không có quyền");
      }

      // 404 - Not Found
      if (status === 404) {
        console.warn("[API] Không tìm thấy endpoint/resource");
      }

      // 400 - Bad Request
      if (status === 400) {
        console.warn("[API] Request không hợp lệ");
      }

      // 500+
      if (status >= 500) {
        console.error("[SERVER] Backend đang gặp lỗi");
      }
    }

    // Request không nhận được response
    else if (error.request) {
      console.error("[NETWORK ERROR] Không nhận được response từ backend");

      console.error("Request:", error.request);
      console.error("Có thể backend chưa chạy, sai port hoặc CORS.");
    }

    // Lỗi khi setup request
    else {
      console.error("[AXIOS ERROR]", error.message);
    }

    return Promise.reject(error);
  },
);

export default api;
