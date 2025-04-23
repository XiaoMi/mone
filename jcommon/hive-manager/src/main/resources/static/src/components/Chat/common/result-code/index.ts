import { useChatContextStore, type GptRole } from "@/stores/chat-context";
import { useUserStore } from "@/stores/user";
import { useIdeaInfoStore } from "@/stores/idea-info";
// import MioneUrl from "@/views/code/chat/icons/mione.png";

const getUserRole = (
  role: string,
  user: any
): {
  cname: string;
  username: string;
  avatar: string;
  role: GptRole | "IDEA";
} => {
  const { agent } = useUserStore();
  if (role == "user") {
    return {
      cname: user.cname || user.username,
      username: user.cname || user.username,
      avatar: user.avatar,
      role: "USER",
    };
  } else {
    // role == "assistant" || role == "idea"
    return {
      cname: agent?.name || "",
      username: agent?.name || "",
      avatar: `data:image/jpeg;base64,${agent?.image}`,
      role: "IDEA",
    };
  }
};

export function resultCodeHandler(res: string) {
  const decodeRes = window.decodeURIComponent(res);
  const {
    messageList,
    setMessageList,
    setLoading,
    setKnowledge,
    addInputingId,
    removeInputingId,
    discardIds,
    setShowDiscardBtn,
    inputingIds,
  } = useChatContextStore();
  const { user } = useUserStore();
  const { isShowFile } = useIdeaInfoStore();
  setLoading(false);
  const data = JSON.parse(decodeRes);

  if (data.category == "knowledge" && isShowFile) {
    setKnowledge(data.text);
    return;
  }
  // console.log("========data", data.id, data);
  if (data.category === "flow") {
    let existData = messageList.find((item) => item.id === data.id);
    if (!existData) {
      existData = {
        id: data.id,
        type: "md",
        author: getUserRole(data.role, user),
        meta: {
          role: getUserRole(data.role, user).role,
          serverId: data.id || undefined,
        },
        data: {
          text: "",
          flowData: JSON.parse(data.text || "{}"),
        },
      };
      setMessageList([...messageList, existData]);
    } else {
      existData.data.flowData = JSON.parse(data.text || "{}");
    }
  } else {
    if (data.type === "begin" && !discardIds.includes(data.id)) {
      addInputingId(data.id);
      setShowDiscardBtn(true);
      const text = data.text ? window.decodeURIComponent(data.text) : "";
      // const separators = data.code ? "\n ``` \n" : "";
      const separators = "";
      let existData = messageList.find((item) => item.id === data.id);
      if (!existData) {
        existData = {
          id: data.id,
          type: "md",
          author: getUserRole(data.role, user),
          meta: {
            role: getUserRole(data.role, user).role,
            separators,
            serverId: data.id || undefined,
          },
          data: {
            origin: text,
            text: `${separators}${text}${separators}`,
          },
        };
        setMessageList([...messageList, existData]);
      } else {
        existData.meta.separators = separators;
        existData.data.origin = existData.data.origin ?? "" + text;
        existData.data.text = `${separators}${existData.data.origin}${separators}`;
      }
    } else if (data.type === "process" && !discardIds.includes(data.id)) {
      if (!data.text) {
        return;
      }
      const text = window.decodeURIComponent(data.text);

      // 是否存在在数据里
      const existData = messageList.find((item) => item.id === data.id);
      if (existData) {
        //存在
        const separators = existData.meta.separators || "";
        existData.data.origin = existData.data.origin + text;
        existData.data.text = `${separators}${existData.data.origin}${separators}`;
        // 处理特殊格式
        const formatHandlers = [
          {
            // XML格式处理
            match: (text: string) =>
              text.includes("```xml") && text.includes("<use_mcp_tool>"),
            replace: (text: string) =>
              text.replace(
                /```xml\s*([\s\S]*?)<use_mcp_tool>([\s\S]*?)\s*```/g,
                "$1<use_mcp_tool>$2"
              ),
          },
          {
            // XML格式处理
            match: (text: string) =>
              text.includes("```xml") && text.includes("<attempt_completion>"),
            replace: (text: string) =>
              text.replace(
                /```xml\s*([\s\S]*?)<attempt_completion>([\s\S]*?)\s*```/g,
                "$1<attempt_completion>$2"
              ),
          },
          {
            // XML格式处理
            match: (text: string) =>
              text.includes("```xml") && text.includes("<ask_followup_question>"),
            replace: (text: string) =>
              text.replace(
                /```xml\s*([\s\S]*?)<ask_followup_question>([\s\S]*?)\s*```/g,
                "$1<ask_followup_question>$2"
              ),
          },
          {
            // XML格式处理
            match: (text: string) =>
              text.includes("```xml") && text.includes("<chat>"),
            replace: (text: string) =>
              text.replace(
                /```xml\r?\n?([\s\S]*?)<chat>([\s\S]*?)(?=```|$)/g,
                "$1<chat>$2"
              ),
          },
          {
            // MCP工具格式处理
            match: (text: string) => text.includes("<use_mcp_tool>"),
            replace: (text: string) => {
              return text.replace(
                /(<arguments>[\s\n]*{[\s\n]*}[\s\n]*<\/arguments>)/g,
                "<arguments>无数据</arguments>"
              );
            },
          },
          {
            // boltArtifact 格式处理
            match: (text: string) => text.includes("<boltArtifact"),
            replace: (text: string) => {
              return text.replace(/(?<![\n\r])(<boltArtifact)/g, '\n$1');
            },
          },
          {
            // boltArtifact 格式处理
            match: (text: string) => text.includes("</boltArtifact"),
            replace: (text: string) => {
              return text.replace(/(<\/boltArtifact>)(?![\n\r])/g, '$1\n');
            },
          },
        ];

        for (const handler of formatHandlers) {
          if (handler.match(existData.data.text)) {
            existData.data.text = handler.replace(existData.data.text);
          }
        }
      }
    } else if (data.type === "success" || data.type === "failure") {
      removeInputingId(data.id);
      setShowDiscardBtn(false);
      // console.log("完成了", data.type, data);
    }
  }
}



export function fluxCodeHandler(res: string, uuid: string) {
  const {
    messageList,
    setMessageList,
    setLoading,
    setKnowledge,
    addInputingId,
    removeInputingId,
    discardIds,
    setShowDiscardBtn,
    inputingIds,
  } = useChatContextStore();
  const { user } = useUserStore();
  setLoading(false);

  const existData = messageList.find((item) => item.id === uuid);
  if (existData) {
    //存在
    const separators = existData.meta.separators || "";
    existData.data.origin = existData.data.origin + res;
    existData.data.text = `${separators}${existData.data.origin}${separators}`;
    // 处理特殊格式
    const formatHandlers = [
      {
        // chat标签格式处理
        match: (text: string) => text.includes("<") && text.includes("chat"),
        replace: (text: string) => {
          return text
            .replace(/<[\s\r\n]+chat/g, '<chat')
            .replace(/chat[\s\r\n]+>/g, 'chat>');
        },
      },
      {
        // thinking标签格式处理
        match: (text: string) => text.includes("<") && text.includes("thinking"),
        replace: (text: string) => {
          return text
            .replace(/<[\s\r\n]+thinking/g, '<thinking')
            .replace(/thinking[\s\r\n]+>/g, 'thinking>');
        },
      },
      {
        // XML格式处理
        match: (text: string) =>
          text.includes("```xml") && text.includes("<use_mcp_tool>"),
        replace: (text: string) =>
          text.replace(
            /```xml\s*([\s\S]*?)<use_mcp_tool>([\s\S]*?)\s*```/g,
            "$1<use_mcp_tool>$2"
          ),
      },
      {
        // XML格式处理
        match: (text: string) =>
          text.includes("```xml") && text.includes("<attempt_completion>"),
        replace: (text: string) =>
          text.replace(
            /```xml\s*([\s\S]*?)<attempt_completion>([\s\S]*?)\s*```/g,
            "$1<attempt_completion>$2"
          ),
      },
      {
        // XML格式处理
        match: (text: string) =>
          text.includes("```xml") && text.includes("<ask_followup_question>"),
        replace: (text: string) =>
          text.replace(
            /```xml\s*([\s\S]*?)<ask_followup_question>([\s\S]*?)\s*```/g,
            "$1<ask_followup_question>$2"
          ),
      },
      {
        // XML格式处理
        match: (text: string) =>
          text.includes("```xml") && text.includes("<chat>"),
        replace: (text: string) =>
          text.replace(
            /```xml\r?\n?([\s\S]*?)<chat>([\s\S]*?)(?=```|$)/g,
            "$1<chat>$2"
          ),
      },
      {
        // MCP工具格式处理
        match: (text: string) => text.includes("<use_mcp_tool>"),
        replace: (text: string) => {
          return text.replace(
            /(<arguments>[\s\n]*{[\s\n]*}[\s\n]*<\/arguments>)/g,
            "<arguments>无数据</arguments>"
          );
        },
      },
      {
        // boltArtifact 格式处理
        match: (text: string) => text.includes("<boltArtifact"),
        replace: (text: string) => {
          return text.replace(/(?<![\n\r])(<boltArtifact)/g, '\n$1');
        },
      },
      {
        // boltArtifact 格式处理
        match: (text: string) => text.includes("</boltArtifact"),
        replace: (text: string) => {
          return text.replace(/(<\/boltArtifact>)(?![\n\r])/g, '$1\n');
        },
      },
    ];
    for (const handler of formatHandlers) {
      if (handler.match(existData.data.text)) {
        existData.data.text = handler.replace(existData.data.text);
      }
    }
  } else {
    const separators = "";
    let existData = messageList.find((item) => item.id === uuid);
    if (!existData) {
      existData = {
        id: uuid,
        type: "md",
        author: getUserRole("idea", user),
        meta: {
          role: getUserRole("idea", user).role,
          separators,
          serverId: uuid || undefined,
        },
        data: {
          origin: res,
          text: `${separators}${res}${separators}`,
        },
      };
      setMessageList([...messageList, existData]);
    } else {
      existData.meta.separators = separators;
      existData.data.origin = existData.data.origin ?? "" + res;
      existData.data.text = `${separators}${existData.data.origin}${separators}`;
    }
  }
}
