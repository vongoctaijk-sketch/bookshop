/**
 * Cookie utilities untuk menyimpan refreshToken
 */

export const cookieUtils = {
  /**
   * Set cookie
   * @param {string} name - Cookie name
   * @param {string} value - Cookie value
   * @param {number} days - Expiry days (default: 7)
   */
  setCookie: (name, value, days = 7) => {
    const date = new Date();
    date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
    const expires = `expires=${date.toUTCString()}`;
    document.cookie = `${name}=${value};${expires};path=/;SameSite=Strict`;
  },

  /**
   * Get cookie value
   * @param {string} name - Cookie name
   * @returns {string|null}
   */
  getCookie: (name) => {
    const nameEQ = `${name}=`;
    const cookies = document.cookie.split(";");
    for (let cookie of cookies) {
      cookie = cookie.trim();
      if (cookie.indexOf(nameEQ) === 0) {
        return cookie.substring(nameEQ.length);
      }
    }
    return null;
  },

  /**
   * Delete cookie
   * @param {string} name - Cookie name
   */
  deleteCookie: (name) => {
    document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/`;
  },
};

/**
 * Token storage utilities
 */
export const tokenStorage = {
  /**
   * Lưu tokens
   */
  setTokens: (accessToken, refreshToken) => {
    // Lưu accessToken vào localStorage
    localStorage.setItem("accessToken", accessToken);
    // Lưu refreshToken vào cookie
    cookieUtils.setCookie("refreshToken", refreshToken, 7);
  },

  /**
   * Lấy accessToken từ localStorage
   */
  getAccessToken: () => {
    return localStorage.getItem("accessToken");
  },

  /**
   * Lấy refreshToken từ cookie
   */
  getRefreshToken: () => {
    return cookieUtils.getCookie("refreshToken");
  },

  /**
   * Xóa tất cả tokens
   */
  clearTokens: () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("authToken"); // compatibility
    localStorage.removeItem("refreshToken"); // compatibility
    cookieUtils.deleteCookie("refreshToken");
  },

  /**
   * Check nếu user đã login
   */
  isLoggedIn: () => {
    return !!localStorage.getItem("accessToken");
  },
};
