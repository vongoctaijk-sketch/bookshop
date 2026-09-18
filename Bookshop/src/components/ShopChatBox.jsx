import React from "react";
import { Avatar, Button, Drawer, FloatButton, Input, List, Spin } from "antd";
import {
  CustomerServiceOutlined,
  SendOutlined,
  UserOutlined,
} from "@ant-design/icons";
import AiService from "../services/AiService";

const initialMessages = [
  {
    id: "welcome",
    role: "assistant",
    content:
      "Hello! I can help you find a book or answer questions about our shop.",
  },
];

function ShopChatBox() {
  const [open, setOpen] = React.useState(false);
  const [input, setInput] = React.useState("");
  const [messages, setMessages] = React.useState(initialMessages);
  const [isSending, setIsSending] = React.useState(false);

  const handleSend = async () => {
    const prompt = input.trim();

    if (!prompt || isSending) {
      return;
    }

    setInput("");
    setMessages((currentMessages) => [
      ...currentMessages,
      { id: `user-${Date.now()}`, role: "user", content: prompt },
    ]);
    setIsSending(true);

    try {
      const response = await AiService.chatWithAi(prompt);
      const answer = response?.answer || response?.data?.answer;

      setMessages((currentMessages) => [
        ...currentMessages,
        {
          id: `assistant-${Date.now()}`,
          role: "assistant",
          content: answer || "I could not generate an answer right now.",
        },
      ]);
    } catch {
      setMessages((currentMessages) => [
        ...currentMessages,
        {
          id: `error-${Date.now()}`,
          role: "assistant",
          content:
            "Sorry, the assistant is temporarily unavailable. Please try again.",
        },
      ]);
    } finally {
      setIsSending(false);
    }
  };

  return (
    <>
      <FloatButton
        icon={<CustomerServiceOutlined />}
        type="primary"
        tooltip="Ask Folio AI"
        onClick={() => setOpen(true)}
        style={{ right: 24, bottom: 24 }}
      />
      <Drawer
        title="Folio AI"
        placement="right"
        open={open}
        onClose={() => setOpen(false)}
        width="min(380px, calc(100vw - 32px))"
        styles={{
          body: { padding: 0, display: "flex", flexDirection: "column" },
        }}
      >
        <List
          dataSource={messages}
          split={false}
          style={{ flex: 1, overflowY: "auto", padding: "16px" }}
          renderItem={(message) => (
            <List.Item
              style={{
                border: 0,
                justifyContent:
                  message.role === "user" ? "flex-end" : "flex-start",
                padding: "0 0 16px",
              }}
            >
              <div
                style={{
                  display: "flex",
                  alignItems: "flex-start",
                  gap: 8,
                  maxWidth: "88%",
                  flexDirection:
                    message.role === "user" ? "row-reverse" : "row",
                }}
              >
                <Avatar
                  size="small"
                  icon={
                    message.role === "user" ? (
                      <UserOutlined />
                    ) : (
                      <CustomerServiceOutlined />
                    )
                  }
                  style={
                    message.role === "user"
                      ? { backgroundColor: "#18352a" }
                      : undefined
                  }
                />
                <div
                  style={{
                    background: message.role === "user" ? "#18352a" : "#f0f2f5",
                    color: message.role === "user" ? "#fff" : "#1f1f1f",
                    borderRadius: 10,
                    padding: "8px 12px",
                    whiteSpace: "pre-wrap",
                    wordBreak: "break-word",
                  }}
                >
                  {message.content}
                </div>
              </div>
            </List.Item>
          )}
        />
        <div style={{ borderTop: "1px solid #f0f0f0", padding: 16 }}>
          <Input.TextArea
            value={input}
            onChange={(event) => setInput(event.target.value)}
            onPressEnter={(event) => {
              if (!event.shiftKey) {
                event.preventDefault();
                handleSend();
              }
            }}
            placeholder="Ask about books..."
            autoSize={{ minRows: 2, maxRows: 5 }}
            disabled={isSending}
          />
          <Button
            type="primary"
            icon={isSending ? <Spin size="small" /> : <SendOutlined />}
            onClick={handleSend}
            loading={isSending}
            disabled={!input.trim()}
            block
            style={{ marginTop: 8, background: "#18352a" }}
          >
            Send
          </Button>
        </div>
      </Drawer>
    </>
  );
}

export default ShopChatBox;
