import api from "../config/axios";

const tacGiaService = {
  getAll: () => api.get("/tac-gia"),

  getById: (id) => api.get(`/tac-gia/${id}`),

  create: (data) => api.post("/tac-gia", data),

  update: (id, data) => api.put(`/tac-gia/${id}`, data),

  delete: (id) => api.delete(`/tac-gia/${id}`),
};

export default tacGiaService;
