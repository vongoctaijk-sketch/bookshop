import api from "../config/axios";

const nhaXuatBanService = {
  getAll: () => api.get("/nha-xuat-ban"),

  getById: (id) => api.get(`/nha-xuat-ban/${id}`),

  create: (data) => api.post("/nha-xuat-ban", data),

  update: (id, data) => api.put(`/nha-xuat-ban/${id}`, data),

  delete: (id) => api.delete(`/nha-xuat-ban/${id}`),
};

export default nhaXuatBanService;
