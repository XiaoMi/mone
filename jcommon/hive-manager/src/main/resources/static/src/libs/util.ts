import { id, pa } from "element-plus/es/locale/index.mjs";
import { useChatContextStore } from "@/stores/chat-context";

type T = any;

const ideaCmd = function (cmd: string, data = {}, json = false): Promise<T> {
  // const { chatContext } = useChatContextStore();
  // return new Promise((resolve, reject) => {
  //   if (!(window as any).cef) {
  //     reject(new Error("需要在idea运行"));
  //   }
  //   (window as any).cef({
  //     request:
  //       "click:" +
  //       JSON.stringify({
  //         cmd: cmd,
  //         data: {
  //           ...data,
  //           scope: chatContext.scope,
  //           project: chatContext.project,
  //           module: chatContext.module,
  //         },
  //       }),
  //     onSuccess: (response: string) => {
  //       if (json) {
  //         try {
  //           resolve(JSON.parse(response));
  //         } catch (e) {
  //           console.error("ideaCmd", e);
  //           resolve(response);
  //         }
  //       } else {
  //         resolve(response);
  //       }
  //     },
  //     onFailure: (errorCode: number, errorMessage: string) => {
  //       reject(new Error(`${errorCode}: ${errorMessage}`));
  //     },
  //   });
  // });
};

const ideaCmdWithoutProjectInfo = function (
  cmd: string,
  data = {},
  json = false
): Promise<T> {
  // return new Promise((resolve, reject) => {
  //   if (!(window as any).cef) {
  //     reject(new Error("需要在idea运行"));
  //   }
  //   (window as any).cef({
  //     request:
  //       "click:" +
  //       JSON.stringify({
  //         cmd: cmd,
  //         data: data,
  //       }),
  //     onSuccess: (response: string) => {
  //       if (json) {
  //         resolve(JSON.parse(response));
  //       } else {
  //         resolve(response);
  //       }
  //     },
  //     onFailure: (errorCode: number, errorMessage: string) => {
  //       reject(new Error(`${errorCode}: ${errorMessage}`));
  //     },
  //   });
  // });
};

const util = {};

util.fetchKnowledgeBases = function (): Promise<any> {
  return ideaCmd("knowledge_bases");
};

util.filesBases = function (data: Record<string, any>): Promise<any> {
  return ideaCmd("knowledge_bases", data);
};

/**
 * @description 更新标题
 * @param {String} title 标题
 */
util.title = function (titleText: string) {
  const processTitle = import.meta.env.VITE_APP_TITLE || "Idea";
  window.document.title = `${processTitle}${
    titleText ? ` | ${titleText}` : ""
  }`;
};

util.getApiKey = function () {
  return ideaCmd("miapi");
};

util.syncClientInfo = function (data: ClientInfo) {
  console.log("sync_client_data", data);
  return ideaCmdWithoutProjectInfo("sync_client_data", {
    syncData: JSON.stringify(data),
  });
};

util.getIdeaInfo = function (data = {}, json = true) {
  return ideaCmd("server_info", data, json);
};

util.getUserInfo = function () {
  return ideaCmd("user");
};

util.genCode = function (data = {}, json = false) {
  return ideaCmd("handler_generate", data, json);
};

util.insertCode = function (code: string) {
  return ideaCmd("idea_insert_code", { code }, false);
};

util.diffCode = function (code: string) {
  return ideaCmd("code_diff", { code }, false);
};

util.modifyPrompt = function (code: string) {
  return ideaCmd(
    "modifyPrompt",
    {
      prompt: code,
    },
    false
  );
};

util.getPromptInfo = function (data = {}, json = true) {
  return ideaCmd("prompt_info", data, json);
};

util.getInvokeType = function (data = {}, json = true) {
  return ideaCmd("get_invoke_type", data, json);
};

util.applyCode = function () {
  return ideaCmd("apply_code", {}, true);
};

util.getCodePrompt = function () {
  return ideaCmd("ai_code_prompt", {}, true);
};

util.getProjectInfo = function () {
  return ideaCmd("get_project_info", {}, true);
};

util.getMcp = function () {
  return ideaCmd("mcp_fetch", {}, true);
};

util.openMcp = function () {
  return ideaCmd("mcp_server_open_file", {}, false);
};

util.getMcpStatus = function (name: string) {
  return ideaCmd("mcp_server_status", {mcpServerName: name}, true);
};

util.getTools = function (name: string) {
  return ideaCmd("mcp_fetch_tools", {mcpServerName: name}, true);
};

util.mcpRetryConnection = function (name: string) {
  return ideaCmd("mcp_server_retry_connection", {mcpServerName: name}, true);
};
util.getSettingAll = function () {
  return ideaCmd("model_setting_all", {}, true);
};

util.saveSetting = function (obj: object) {
  return ideaCmd("model_setting_save", obj, true);
};

util.getMcpVersion = function (name: string) {
  return ideaCmd("mcp_server_version", {mcpServerName: name}, true);
};

util.getAiGuide = function (question: string, data: Record<string, any>) {
  // console.log('getAiGuide', data);
  return ideaCmd(
    "ai_guide",
    {
      ...data,
      question,
    },
    true
  );
};

util.sendSound = function (base64: string) {
  return ideaCmd(
    "sound",
    {
      sound: base64,
    },
    false
  );
};

util.playSound = function (input: string): Promise<string> {
  return ideaCmd(
    "play sound",
    {
      input,
    },
    false
  );
};

util.sendMessage = function (
  data: string,
  params: {
    msg_num: string | number;
    knowledgeBasesId: string;
    topicId: string | number;
  }
): Promise<string> {
  return ideaCmd(
    "local_call",
    {
      ...params,
      data,
    },
    false
  );
};

util.deleteMessage = function (msgId: string): Promise<any> {
  return ideaCmd(
    "del_msg",
    {
      msgId: msgId,
    },
    false
  );
};

util.appendMessage = function (data: MessageReq): Promise<any> {
  return ideaCmd(
    "append_msg",
    {
      data: JSON.stringify(data),
    },
    true
  );
};

util.stopWs = function () {
  return ideaCmd("stop_ws", {}, true);
};

(util.listMessage = function (): Promise<MessageRes[]> {
  return ideaCmd("list_msg", {}, true);
}),
  (util.eventMessage = function (data: MessageReq): Promise<EventRes> {
    return ideaCmd(
      "event_msg",
      {
        data: JSON.stringify(data),
      },
      true
    );
  });

util.myVision = function (data: Record<string, string>): Promise<string> {
  return ideaCmd(
    "vision",
    {
      ...data,
    },
    false
  );
};

util.getZToken = function () {
  return ideaCmd("z_token", {}, false);
};

util.clearMessage = function () {
  return ideaCmd("clear_msg", {}, false);
};
util.askState = function () {
  return ideaCmd("state_ask", {}, false);
};
util.setStateFullback = function (index: string) {
  return ideaCmd("state_fullback", { index }, false);
};

util.utf8ByteToUnicodeStr = function (utf8Bytes: string | any[]) {
  var unicodeStr = "";
  for (var pos = 0; pos < utf8Bytes.length; ) {
    var flag = utf8Bytes[pos];
    var unicode = 0;
    if (flag >>> 7 === 0) {
      unicodeStr += String.fromCharCode(utf8Bytes[pos]);
      pos += 1;
    } else if ((flag & 0xfc) === 0xfc) {
      unicode = (utf8Bytes[pos] & 0x3) << 30;
      unicode |= (utf8Bytes[pos + 1] & 0x3f) << 24;
      unicode |= (utf8Bytes[pos + 2] & 0x3f) << 18;
      unicode |= (utf8Bytes[pos + 3] & 0x3f) << 12;
      unicode |= (utf8Bytes[pos + 4] & 0x3f) << 6;
      unicode |= utf8Bytes[pos + 5] & 0x3f;
      unicodeStr += String.fromCharCode(unicode);
      pos += 6;
    } else if ((flag & 0xf8) === 0xf8) {
      unicode = (utf8Bytes[pos] & 0x7) << 24;
      unicode |= (utf8Bytes[pos + 1] & 0x3f) << 18;
      unicode |= (utf8Bytes[pos + 2] & 0x3f) << 12;
      unicode |= (utf8Bytes[pos + 3] & 0x3f) << 6;
      unicode |= utf8Bytes[pos + 4] & 0x3f;
      unicodeStr += String.fromCharCode(unicode);
      pos += 5;
    } else if ((flag & 0xf0) === 0xf0) {
      unicode = (utf8Bytes[pos] & 0xf) << 18;
      unicode |= (utf8Bytes[pos + 1] & 0x3f) << 12;
      unicode |= (utf8Bytes[pos + 2] & 0x3f) << 6;
      unicode |= utf8Bytes[pos + 3] & 0x3f;
      unicodeStr += String.fromCharCode(unicode);
      pos += 4;
    } else if ((flag & 0xe0) === 0xe0) {
      unicode = (utf8Bytes[pos] & 0x1f) << 12;
      unicode |= (utf8Bytes[pos + 1] & 0x3f) << 6;
      unicode |= utf8Bytes[pos + 2] & 0x3f;
      unicodeStr += String.fromCharCode(unicode);
      pos += 3;
    } else if ((flag & 0xc0) === 0xc0) {
      // 110
      unicode = (utf8Bytes[pos] & 0x3f) << 6;
      unicode |= utf8Bytes[pos + 1] & 0x3f;
      unicodeStr += String.fromCharCode(unicode);
      pos += 2;
    } else {
      unicodeStr += String.fromCharCode(utf8Bytes[pos]);
      pos += 1;
    }
  }
  return unicodeStr;
};

util.approve = function (data: {
  message: string;
}) {
  return ideaCmd("message_break", {
    data: data.message,
  }, false);
};

export default util;
