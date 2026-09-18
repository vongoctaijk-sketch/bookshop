import api from "../config/axios";

const sachService = {
  getAll: (params) => {
    if (params && Object.keys(params).length > 0) {
      return api.get("/sach/search", { params });
    }

    return api.get("/sach");
  },

  gettop3: () => api.get("/sach/top-ban-chay"),

  getById: (id) => api.get(`/sach/${id}`),

  create: (data) => api.post("/sach", data),

  createWithImage: (payload, file) => {
    const formData = new FormData();
    formData.append("sach", JSON.stringify(payload));

    if (file) {
      formData.append("fileAnh", file);
    }

    return api.post("/sach", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  update: (id, data) => api.put(`/sach/${id}`, data),

  updateWithImage: (id, payload, file) => {
    const formData = new FormData();
    formData.append("sach", JSON.stringify(payload));

    if (file) {
      formData.append("fileAnh", file);
    }

    return api.put(`/sach/${id}`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },

  delete: (id) => api.delete(`/sach/${id}`),
};

export default sachService;
