import { ref } from "vue";
import { defineStore } from "pinia";
import { LocalStorage } from "lowdb/browser";
import { LowSync } from "lowdb";

export type GptRole = "USER" | "SYSTEM" | "ASSISTANT";

export interface ChatContextMsg {
  content: string;
  role: GptRole;
}

export interface ChatContext {
  isAllow: boolean;
  maxNum: number;
  scope: "method" | "class" | "module";
  project: string;
  module: string;
}

export interface TokenUsage {
  usedTokens: number;
  totalTokens: number;
  inputTokens: number;
  outputTokens: number;
  compressedTokens: number;
  lastUpdate: Date;
}

export type MessageMeta = {
  ask?: {
    prompt: string;
  };
  role: GptRole | "IDEA";
  serverId?: string;
  local?: boolean;
  chatContextMsg?: ChatContextMsg[];
  separators?: string;
  type?: string;
};

export type Message = {
  id?: string | number;
  type: string;
  author: {
    cname: string;
    username: string;
    avatar: string;
  };
  meta: MessageMeta;
  data: {
    isLast?: boolean;
    flowData?: any;
    content?: string;
    files?: {
      mediaType: string;
      url: string;
      input: string;
    }[];
    cmd?: {
      id: string;
      label: string;
      value: string;
      params: Record<string, any>;
    };
    knowledgeBase?: {
      label: string;
      value: string;
      params?: object;
    };
    type?: string;
    data?: any;
    meta?: any;
    origin?: string;
    text?: string;
    refRealContent?: string | null;
    sound?: string;
    hello?: string;
    links?: {
      prefix: string;
      label: string;
      suffix: string;
      value: string;
      type: string;
      src: string;
      params: {
        prompt: string;
        showDialog: string;
        meta: any;
        desc: string;
      };
    }[];
  };
};

export type MessageList = Message[];

export const legalMaxNums = [2, 4, 7, 10, 100];

const db = new LowSync<{
  isLoading: boolean;
  knowledgeLoading: boolean;
  messageList: MessageList;
  chatContext: ChatContext;
  knowledgeData: any;
  inputingIds: string[];
  discardIds: string[];
  showDiscardBtn: boolean;
  scrollToBottom: boolean;
}>(new LocalStorage("chat-context"), {
  isLoading: false,
  knowledgeLoading: false,
  knowledgeData: "",
  messageList: [],
  chatContext: {
    isAllow: true,
    maxNum: 10,
    scope: "method",
    project: "",
    module: "",
  },
  inputingIds: [],
  discardIds: [],
  showDiscardBtn: false,
  scrollToBottom: false,
});

export const useChatContextStore = defineStore("chat-context", () => {
  // db.read();
  const data = db.data;
  // console.log("db data:", data);

  const messageList = ref<MessageList>([]);
  const knowledgeData = ref<any>();
  const isLoading = ref<boolean>(false);
  const knowledgeLoading = ref<boolean>(false);
  const chatContext = ref<ChatContext>(data.chatContext);
  const inputingIds = ref<string[]>([]);
  const discardIds = ref<string[]>([]);
  const showDiscardBtn = ref<boolean>(false);
  const scrollToBottom = ref<boolean>(true);

  // Token使用量状态
  const tokenUsage = ref<TokenUsage>({
    usedTokens: 0,
    totalTokens: 1000000, // 默认总量
    inputTokens: 0,
    outputTokens: 0,
    compressedTokens: 0,
    lastUpdate: new Date()
  });

  if (!legalMaxNums.find((it) => it == chatContext.value.maxNum)) {
    chatContext.value.maxNum = 10;
  }

  function getChatContext(): ChatContextMsg[] {
    JSON.stringify(messageList.value);
    if (chatContext.value.isAllow) {
      const maxNum = chatContext.value.maxNum;
      return (messageList.value || [])
        .slice(0, -1)
        .filter((it) => it.meta && it.meta.role !== "IDEA")
        .slice(0, maxNum)
        .map((it) => {
          return {
            refRealContent: it.data.refRealContent || null,
            content: it.data.text as string,
            role: it.meta.role as GptRole,
          };
        });
    } else {
      return [];
    }
  }

  function setKnowledge(str: string) {
    try {
      let _d = JSON.parse(str);
      console.log(_d);
      knowledgeData.value = _d;
      // db.data.knowledgeData = _d;
      // db.write();
    } catch (error) {
      //
    }
  }

  function setLoading(bool: boolean) {
    isLoading.value = bool;
    // db.data.isLoading = bool;
    // db.write();
  }

  function setKnowledgeLoading(bool: boolean) {
    knowledgeLoading.value = bool;
    // db.data.knowledgeLoading = bool;
    // db.write();
  }

  function setMaxNum(num: number) {
    chatContext.value.maxNum = num;

    // db.data.chatContext.maxNum = num;
    // db.write();
  }

  function addMessage(message: Message) {
    messageList.value.push(message);
  }
  function clearMessageAfterId(id) {
    const index = messageList.value.findIndex((msg) => msg.data?.text?.includes(id))
    if (
      messageList.value[index]?.data?.text?.replace(
        new RegExp(`<hive-msg-id>${id}<\\/hive-msg-id>[\\s\\S]*$`),
        '',
      ) === ''
    ) {
      messageList.value.length = index
      return
    }
    messageList.value[index].data.text = messageList.value[index]?.data?.text?.replace(
      new RegExp(`<hive-msg-id>${id}<\\/hive-msg-id>[\\s\\S]*$`),
      '',
    )
  }
  function setMessageList(list: MessageList) {
    messageList.value.length = 0;  // 清空现有数组
    messageList.value.push(...list);  // 添加新的元素

    // db.data.messageList = list;
    // db.write();
  }

  function disableContext() {
    chatContext.value.isAllow = false;

    // db.data.chatContext.isAllow = false;
    // db.write();
  }

  function setProject(project: string) {
    chatContext.value.project = project;
  }

  function setModule(module: string) {
    chatContext.value.module = module;
  }

  function enableContext() {
    chatContext.value.isAllow = true;

    // db.data.chatContext.isAllow = true;
    // db.write();
  }

  function clearContext() {
    // chatContext.value.maxNum = 0;
  }

  function addInputingId(id: string) {
    inputingIds.value.push(id);
  }

  function removeInputingId(id: string): boolean {
    if (inputingIds.value?.length > 0) {
      inputingIds.value = inputingIds.value.filter((it) => it !== id);
    }
    return inputingIds.value?.length === 0;
  }

  function addDiscardId() {
    if (inputingIds.value?.length > 0) {
      discardIds.value.push(...inputingIds.value);
    }
    showDiscardBtn.value = false;
  }

  function setShowDiscardBtn(bool: boolean) {
    showDiscardBtn.value = bool;
  }

  function setScrollToBottom(bool: boolean) {
    scrollToBottom.value = bool;
  }

  // Token使用量相关方法
  function updateTokenUsage(inputTokens: number, outputTokens: number, compressedTokens: number = 0) {
    tokenUsage.value.inputTokens += inputTokens;
    tokenUsage.value.outputTokens += outputTokens;
    tokenUsage.value.compressedTokens += compressedTokens;
    tokenUsage.value.usedTokens = tokenUsage.value.inputTokens + tokenUsage.value.outputTokens - tokenUsage.value.compressedTokens;
    tokenUsage.value.lastUpdate = new Date();
  }

  function setTotalTokens(total: number) {
    tokenUsage.value.totalTokens = total;
  }

  function resetTokenUsage() {
    Object.assign(tokenUsage.value, {
      usedTokens: 0,
      inputTokens: 0,
      outputTokens: 0,
      compressedTokens: 0,
      lastUpdate: new Date()
    });
  }

  function getTokenUsagePercentage(): number {
    if (tokenUsage.value.totalTokens <= 0) return 0;
    return Math.min((tokenUsage.value.usedTokens / tokenUsage.value.totalTokens) * 100, 100);
  }

  return {
    knowledgeData,
    setKnowledge,
    setKnowledgeLoading,
    knowledgeLoading,
    isLoading,
    messageList,
    chatContext,
    setMessageList,
    addMessage,
    setMaxNum,
    getChatContext,
    disableContext,
    enableContext,
    clearContext,
    setProject,
    setModule,
    setLoading,
    inputingIds,
    discardIds,
    addInputingId,
    removeInputingId,
    addDiscardId,
    showDiscardBtn,
    setShowDiscardBtn,
    scrollToBottom,
    setScrollToBottom,
    // Token使用量相关
    tokenUsage,
    updateTokenUsage,
    setTotalTokens,
    resetTokenUsage,
    getTokenUsagePercentage,
    clearMessageAfterId
  };
});
