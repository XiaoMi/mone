import { useChatContextStore, type GptRole } from "@/stores/chat-context";
import type { AiMessage } from ".";
// import MioneUrl from "@/views/code/chat/icons/mione.png";
import { useUserStore } from "@/stores/user";
import { useIdeaInfoStore } from "@/stores/idea-info";
const { user } = useUserStore();

const getUserRole = (role: string): { cname:string,username: string, avatar: string, role: GptRole | "IDEA" } => {
  if (role == "user") {
    return {
      cname: user.cname || user.username,
      username: user.cname || user.username,
      avatar: user.avatar,
      role: "USER"
    }
  } else { // role == "assistant" || role == "idea"
    const { mioneName, mioneUrl } = useIdeaInfoStore();
    return {
      cname: mioneName,
      username: mioneName,
      avatar: mioneUrl,
      role: "ASSISTANT"
    }
  }
}

export function imageHandler (data: AiMessage) {
  const { addMessage } = useChatContextStore();
  let img = data.message;
  addMessage({
    type: "images",
    author: getUserRole(data.role),
    meta: {
      role: "IDEA",
      serverId: data.id || undefined,
    },
    data: {
      text: img,
    },
  });
}

export function successHandler(data: AiMessage) {
  if (data.type) {
    showFormMessage(data);
  } else {
    const sound = data.sound && "data:audio/mpeg;base64," + data.sound;
    const { addMessage } = useChatContextStore();
    let message = data.message;
    if (message) {
      message = message.replace(/(?<![\n\r])(<boltArtifact)/g, '\n$1');
    }
    addMessage({
      type: "md",
      author: getUserRole(data.role),
      meta: {
        role: "IDEA",
        serverId: data.id || undefined,
      },
      data: {
        text: message ? message : "ok！！！",
        sound,
      },
    });
  }
}

function showFormMessage(data: AiMessage) {
  const { addMessage } = useChatContextStore();
  if (data.type === "list") {
    addMessage({
      type: "list",
      author: getUserRole(data.role),
      meta: {
        role: "IDEA",
        type: data.type,
        serverId: data.id || undefined,
      },
      data: {
        text: data.message ? data.message : "不好意思，没有数据了",
      },
    });
  } else {
    addMessage({
      type: "form",
      author: getUserRole(data.role),
      meta: {
        role: "IDEA",
        type: data.type,
        serverId: data.id || undefined,
      },
      data: {
        type: data.type,
        data: data.data,
        meta: data.meta,
      },
    });
  }
}
