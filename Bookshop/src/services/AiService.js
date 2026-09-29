import api from "../config/axios";

const AiService = {
  createConversation: () => api.post("/ai/conversations"),
  getMessages: (conversationId) =>
    api.get(`/ai/conversations/${conversationId}/messages`),
  sendMessage: (conversationId, prompt) =>
    api.post(`/ai/conversations/${conversationId}/chat`, { prompt }),
};

export default AiService;
