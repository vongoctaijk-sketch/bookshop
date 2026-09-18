import api from "../config/axios";

const AiService = {
  chatWithAi: (prompt) => api.post("/ai/chat", { prompt }),
};

export default AiService;
