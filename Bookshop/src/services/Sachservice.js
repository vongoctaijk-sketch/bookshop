import api from "../config/axios";

const sachService = {
  // Lấy danh sách + tìm kiếm + lọc + phân trang
  getAll: (params) => api.get("/sach/search", { params }),

  // Lấy sách theo ID
  getById: (id) => api.get(`/sach/${id}`),

  // Thêm sách
  create: (data) => api.post("/sach", data),

  // Cập nhật sách
  update: (id, data) => api.put(`/sach/${id}`, data),

  // Xóa sách
  delete: (id) => api.delete(`/sach/${id}`),
};

export default sachService;
