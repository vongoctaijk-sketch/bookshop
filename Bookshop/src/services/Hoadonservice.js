import api from "../config/axios";

const hoaDonService = {
  getAll: () => api.get("/hoa-don"),

  getById: (id) => api.get(`/hoa-don/${id}`),

  create: (data) => api.post("/hoa-don", data),

  update: (id, data) => api.put(`/hoa-don/${id}`, data),

  delete: (id) => api.delete(`/hoa-don/${id}`),
};

export default hoaDonService;
