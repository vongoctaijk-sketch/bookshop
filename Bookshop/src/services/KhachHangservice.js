import api from "../config/axios";

const khachHangService = {
  getAll: () => api.get("/khach-hang"),

  getById: (id) => api.get(`/khach-hang/${id}`),

  create: (data) => api.post("/khach-hang", data),

  update: (id, data) => api.put(`/khach-hang/${id}`, data),

  delete: (id) => api.delete(`/khach-hang/${id}`),
};

export default khachHangService;
