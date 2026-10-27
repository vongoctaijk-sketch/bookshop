import api from "../config/axios";

const phieuNhapService = {
  getAll: () => api.get("/phieu-nhap"),
  getById: (id) => api.get(`/phieu-nhap/${id}`),
  create: (data) => api.post("/phieu-nhap", data),
  update: (id, data) => api.put(`/phieu-nhap/${id}`, data),
  cancel: (id) => api.put(`/phieu-nhap/${id}/cancel`),
};

export default phieuNhapService;
