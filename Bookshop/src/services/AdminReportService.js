import api from "../config/axios";

const adminReportService = {
  get: (params) => api.get("/admin/reports", { params }),
};

export default adminReportService;
