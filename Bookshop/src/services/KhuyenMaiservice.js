import api from "../config/axios";

const khuyenMaiService = {
  getAll: () => api.get("/khuyen-mai"),
  create: (data) => api.post("/khuyen-mai", data),
  update: (id, data) => api.put(`/khuyen-mai/${id}`, data),
  delete: (id) => api.delete(`/khuyen-mai/${id}`),
};

export default khuyenMaiService;
