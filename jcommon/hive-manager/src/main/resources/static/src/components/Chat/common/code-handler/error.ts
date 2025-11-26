import { useChatContextStore } from "@/stores/chat-context";
import type { AiMessage } from ".";

export function errorHandler (data: AiMessage) {
  const { addMessage } = useChatContextStore();
  addMessage({
    type: "md",
    author: {
      cname: "",
      username: "error",
      avatar: "",
    },
    meta: {
      role: "IDEA",
    },
    data: {
      text: data.message
        ? data.message
        : "啊哦，不好意思，出错了！！！",
    },
  });
}