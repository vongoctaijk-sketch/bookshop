import api from "../config/axios";

const theloaiService = {
  getAll: () => api.get("/the-loai"),

  // Lấy thể loại theo ID
  getById: (id) => api.get(`/the-loai/${id}`),

  // Thêm thể loại
  create: (data) => api.post("/the-loai", data),

  // Cập nhật thể loại
  update: (id, data) => api.put(`/the-loai/${id}`, data),

  // Xóa thể loại
  delete: (id) => api.delete(`/the-loai/${id}`),
};

export default theloaiService;
