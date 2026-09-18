import api from "../config/axios";

const nhaCungCapService = {
  getAll: () => api.get("/nha-cung-cap"),

  getById: (id) => api.get(`/nha-cung-cap/${id}`),

  create: (data) => api.post("/nha-cung-cap", data),

  update: (id, data) => api.put(`/nha-cung-cap/${id}`, data),

  delete: (id) => api.delete(`/nha-cung-cap/${id}`),
};

export default nhaCungCapService;
