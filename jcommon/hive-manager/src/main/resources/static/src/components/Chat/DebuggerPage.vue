<template>
  <div class="panel">
    <div class="panel-body">
      <div id="scrollRef" ref="scrollRef" style="overflow: auto; height: 100%">
        <Message v-for="(item, index) of conversions" :avatarUrl="item.avatar" :avatar="item.avatar"
          :username="item.name" :key="index" :date-time="item.dateTime" :text="item.text" :inversion="item.inversion"
          :error="item.error" :textType="item.textType" :loading="item.loading" :show-cursor="item.showCursor"
          :type="item.type" :show-operate="item.isShowOperate" :voice="item.voice"
          :class="[{ 'message-item-question': item.type === 'question' }]"
          @click="item.type === 'question' ? sendText(item.text) : null" @onTryAgain="tryAgain"
          :flowData="item.flowData" :language="props.data?.botSetting?.timbre" :multimodal="item.multimodal"
          :fontColor="item.fontColor" :name="item.name" />
      </div>
    </div>
    <div class="panel-footer">
      <!-- <div class="question-list" v-if="questionList?.length">
        <span @click="handleSelect(item)" v-for="(item, index) in questionList" :key="index">{{item.text}}</span>
      </div> -->
      <div class="footer-opts">
        <CommmonTextarea ref="inputRef" v-model="text" class="flex-1" :placeholder="placeholder" @enterFn="handleEnter"
          :disabled="loading" size="large"></CommmonTextarea>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  ref,
  computed,
  nextTick,
  onMounted,
  onBeforeUnmount,
  onUnmounted,
  watch,
} from "vue";
import { executeBot, /* getPresetQuestion */ } from "@/api/probot";
import { ElMessage } from "element-plus";
import { Message } from "./common-message";
import CommmonTextarea from "./CommmonTextarea.vue";
// import SockJS from "sockjs-client";
// import CryptoJS from "crypto-js";
// import createSourceFunc from "@/common/createEventSource.js";
import { USER_ROLE } from "@/common/constants";

type ScrollElement = HTMLDivElement | null;

const props = defineProps<{
  data: any;
  type: string;
  topicId: string;
  initMsg: string;
  userList: any[];
}>();

const emits = defineEmits(["changeCurrent"]);

let sock: WebSocket | null = null;
let params: any | null = null;
let lastConversionRes: any | null = null;
const isInit = ref(false);
const questionList = ref([]);

// const _isBotStream = "BOT_STREAM";
const _botStreamBegin = "BOT_STREAM_BEGIN";
const _botStreamResult = "BOT_STREAM_RESULT";
const _botStreamEvent = "BOT_STREAM_EVENT";

// const avatar = computed(() => {
//   return "/images/logo/claude3.png";
// });

// const voiceSetting = computed(() => {
//   return {
//     open: !!props.data?.botSetting?.timbreSwitch,
//     language: props.data?.botSetting?.timbre,
//   };
// });

const aiModel = computed(() => {
  const setting = props.data?.botSetting;
  if (setting && setting?.aiModel) {
    return setting?.aiModel;
  }
  return "";
});

const detailData = ref(props.data);

interface Conversion {
  textType?: string;
  text: string;
  msgType: string;
  inversion: boolean;
  loading: boolean;
  dateTime: string;
  name: string;
  avatar: string;
  voice: string;
  fontColor?: string;
}

const conversions = ref<Conversion[]>([]);
const isShow = ref(true);
const loading = ref(false);
const placeholder = ref("Shift+Enter发送消息");
const text = ref("");
const scrollRef = ref<ScrollElement>(null);
const flag = ref(false);

const getUuid = () => {
  return props.topicId;
};

const sendText = (propmt: string) => {
  text.value = propmt;
  onConversion();
};

const handleEnter = (params?: {
  multimodal: number;
  mediaType?: string;
  input?: string;
  url?: string;
}) => {
  onConversion(params);
};

const scrollToBottom = async () => {
  await nextTick();
  if (scrollRef.value) scrollRef.value.scrollTop = scrollRef.value.scrollHeight;
};

const tryAgain = () => {
  const propmt = conversions.value[conversions.value.length - 2];
  if (propmt.inversion) {
    conversions.value = conversions.value.splice(
      0,
      conversions.value.length - 1
    );
    onConversion({
      isTry: true,
      textStr: propmt.text,
    });
  }
};

const getQi = async (params?: any) => {
  if (params) {
    // const data = {
    //   type: 'question',
    //   text: '',
    //   loading: true,
    //   inversion: false
    // }
    // conversions.value.push(data)
    // getPresetQuestion(params)
    //   .then((res) => {
    //     // conversions.value = conversions.value.filter((item) => item.type !== 'question')
    //     if (res.data.contents) {
    //       questionList.value = res.data.contents.forEach((item: { question: string }) => {
    //         if (item) {
    //           return {
    //             type: 'question',
    //             msgType: 'question',
    //             text: item.question,
    //             inversion: false
    //           }
    //         }
    //         return null
    //       })
    //     }
    //   })
    //   .catch(() => {
    //     // conversions.value = conversions.value.filter((item) => item.type !== 'question')
    //   })
    //   .finally(() => {})
  } else {
    questionList.value = props.data?.botSetting?.openingQues.map(
      (item: any) => {
        if (item) {
          return {
            type: "question",
            msgType: "question",
            text: item,
            inversion: false,
          };
        }
        return null;
      }
    );
  }
  await scrollToBottom();
};

const onConversion = async (opts?: {
  isTry?: boolean;
  textStr?: string;
  multimodal?: number;
  mediaType?: string;
  input?: string;
  url?: string;
}) => {
  conversions.value = conversions.value.filter(
    (item) => item.type !== "question"
  );
  conversions.value = conversions.value.map((v) => {
    v.isShowOperate = false;
    return v;
  });
  lastConversionRes = {
    text: "",
    textType: "",
    msgType: "ASSISTANT",
    inversion: false,
    avatar: USER_ROLE.PERSON.img,
    aiModel: aiModel.value,
    loading: true,
    dateTime: new Date().toLocaleString(),
    showCursor: false,
    isShowOperate: true,
    fontColor: USER_ROLE.PERSON.fontColor,
    name: USER_ROLE.PERSON.name,
  };
  try {
    if (loading.value) return;
    let propmt = text.value;
    if (opts?.isTry) {
      propmt = opts?.textStr || "";
    } else {
      const user = props.userList.find((v) => v.roleType == "USER");
      if (opts?.multimodal === 2) {
        conversions.value.push({
          textType: "img",
          text: opts.url,
          msgType: "USER",
          inversion: true,
          loading: false,
          dateTime: new Date().toLocaleString(),
          name: user.name,
          avatar: user.img,
          voice: user.voice,
          fontColor: user.fontColor,
        });
      }
      propmt &&
        conversions.value.push({
          text: propmt,
          msgType: "USER",
          inversion: true,
          loading: false,
          dateTime: new Date().toLocaleString(),
          name: user.name,
          avatar: user.img,
          fontColor: user.fontColor,
          voice: user.voice,
        });
    }
    isShow.value = false;
    text.value = "";
    loading.value = true;
    // 倒数第二项
    const lastItem = conversions.value[conversions.value.length - 2];
    // 上一条数据是否是机器人返回的提示信息， 如果是则需要增加参数
    const newObj = {
      message: propmt,
      flowRecordId: flowRecordId.value,
      msgType: "answer",
    };
    const inputObj = flowRecordId.value
      ? { input: JSON.stringify(newObj) }
      : { input: propmt };
    const msgTypeObj = flowRecordId.value ? { msgType: "answer" } : {};
    let imgObj = {};
    if (opts?.multimodal === 2) {
      imgObj = {
        multimodal: opts.multimodal,
        mediaType: opts.mediaType,
        input: opts.input,
        postscript: inputObj.input,
      };
      lastConversionRes = {
        ...lastConversionRes,
        ...imgObj,
      };
    }
    params = {
      multimodal: 1,
      botId: props.data?.botId,
      topicId: getUuid(),
      history: (conversions.value || [])
        .filter((it) => it.msgType === "USER" || it.msgType === "ASSISTANT")
        .slice(0, -1)
        .map((item) => {
          return {
            role: item.msgType, // item.inversion ? 'USER' : 'ASSISTANT',
            content: item.text,
          };
        }),
      ...inputObj,
      ...msgTypeObj,
      ...imgObj,
    };
    sendMessage(params, params.input);
    conversions.value.push(lastConversionRes);
    await scrollToBottom();
  } catch (e) {
    console.log(e);
    lastConversionRes.loading = false;
    lastConversionRes.showCursor = false;
    lastConversionRes.text = `出错了: ${e}`;
  } finally {
    loading.value = false;
    await scrollToBottom();
  }
};

// const init = async (data: {
//   botSetting: any;
// }, topicId?: string) => {
//   conversions.value = [];
//   isShow.value = true;
//   if (data?.botSetting?.openingRemarks) {
//     conversions.value.push({
//       text: data?.botSetting?.openingRemarks,
//       msgType: "openingRemarks",
//       avatar: USER_ROLE.PERSON.img,
//       name: USER_ROLE.PERSON.name,
//       aiModel: aiModel.value,
//       inversion: false,
//       loading: false,
//       dateTime: new Date().toLocaleString(),
//       fontColor: USER_ROLE.PERSON.fontColor,
//     });
//     await getQi();
//   }
//   flag.value = false;
// };

const updateLastConversion = async ({
  code,
  data,
  message,
}: {
  code: any;
  data: any;
  message: any;
}) => {
  getQi(params);
  const res = lastConversionRes;
  if (code == 0) {
    let resText = data;
    let textType = "";
    try {
      const tryJson = JSON.parse(data);
      if (!tryJson) return;
      const { type, display, call_plugin: callPlugin, data: src } = tryJson;
      if (type === "llm") {
        if (typeof tryJson.content !== "string") {
          resText = JSON.stringify(tryJson.content);
        } else {
          resText = tryJson.content;
        }
      } else if (type === "plugin") {
        if (callPlugin === "图片生成" && src) {
          resText = src;
          textType = "img";
        } else if (display) {
          const tres = tryJson[display];
          if (typeof tres === "string") {
            resText = tres;
          } else {
            resText = JSON.stringify(tres);
          }
        }
      } else if (tryJson?.type === "bar_chart") {
        textType = "chartColumnar";
      }
    } catch (e) {
      console.log(e);
    }
    if (textType === "img") {
      res.loading = false;
      res.text = resText;
      res.textType = textType;
    } else if (textType.includes("chart")) {
      res.loading = false;
      res.text = resText;
      res.textType = textType;
    } else {
      res.loading = false;
      res.showCursor = true;
      for (const c of resText) {
        res.text += c;
        conversions.value = [...conversions.value];
        // await scrollToBottom()
        // await new Promise((resolve) => {
        //   setTimeout(() => {
        //     resolve(1)
        //   }, 0)
        // })
      }
      res.showCursor = false;
    }
  } else {
    res.loading = false;
    res.showCursor = false;
    res.text = message || `code=${code}`;
    ElMessage.error(message || `code=${code}`);
  }
};

const sendMessage = async (params, str = "") => {
  if (sock) {
    // sock?.send(JSON.stringify(params));
    sock?.send(str);
  } else {
    const { code, data, message } = await executeBot(params);
    updateLastConversion({ code, data, message });
  }
};
// 如果有flow正在执行，则flowRecordId为true
const flowRecordId = ref(null);

const updateLastConversionFlow = (obj) => {
  const newFlowRecordId = obj.flowRecordId;
  const hasUpdated = conversions.value.find(
    (item) => item?.flowData?.flowRecordId == newFlowRecordId
  );
  const res = hasUpdated ? hasUpdated : lastConversionRes;
  res.flowData = obj;
  // 已结束，则清空
  if (obj.end == true) {
    flowRecordId.value = null;
  } else {
    flowRecordId.value = obj.flowRecordId;
  }
  conversions.value = [...conversions.value];
};

const updateLastConversionMsg = async (obj) => {
  const res = lastConversionRes;
  if (obj.meta?.message.includes('\\"type\\":\\"table\\",')) {
    res.textType = "table";
    res.text = res.text + "---table---" + obj.meta?.message;
  } else {
    res.text = res.text + obj.meta?.message;
  }
  res.loading = false;
  res.isBotMessage = true;
  conversions.value = [...conversions.value];
  await scrollToBottom();
};

const updateStreamLastConversion = async (text: string, user: any) => {
  const res = lastConversionRes;
  res.text = res.text + text;
  res.loading = false;
  res.isBotMessage = true;
  if (user) {
    res.name = user.name;
    res.fontColor = user.fontColor;
  }
  conversions.value = [...conversions.value];
  await scrollToBottom();
};

const initWS = () => {
  sock = new WebSocket(`${window.location.origin}/multi/chat`);

  sock.onopen = function () {
    isInit.value = true;
    console.log("WebSocket is open now.");
  };

  sock.onmessage = function (event: { data: string }) {
    const message = event.data;
    const msgObj = JSON.parse(message);
    // const text = msgObj.message
    //   ? CryptoJS.enc.Base64.parse(msgObj.message).toString(CryptoJS.enc.Utf8)
    //   : "";
    //  工作流
    // if ((msgObj.messageType ?? "").startsWith(_isBotStream)) {
    const messageType = msgObj.messageType;
    if (_botStreamBegin == messageType) {
      if (!lastConversionRes) {
        lastConversionRes = {
          text: "",
          textType: "",
          msgType: "ASSISTANT",
          inversion: false,
          avatar: USER_ROLE.PERSON.img,
          aiModel: aiModel.value,
          loading: true,
          dateTime: new Date().toLocaleString(),
          showCursor: false,
          isShowOperate: true,
          fontColor: USER_ROLE.PERSON.fontColor,
          name: USER_ROLE.PERSON.name,
        };
        conversions.value.push(lastConversionRes);
      }
      emits("changeCurrent", msgObj.roleId);
      const uesr = props.userList.find((v) => v.id == msgObj.roleId);
      // 开始目前不做处理
      const res = lastConversionRes;
      res.text = "";
      res.loading = false;
      res.isBotMessage = true;
      res.name = uesr.name;
      res.fontColor = uesr.fontColor;
      res.avatar = uesr.img;
      res.voice = uesr.voice;
      conversions.value = [...conversions.value];
    } else if (_botStreamEvent == messageType) {
      // 连续处理
      // const text = msgObj.message
      //   ? CryptoJS.enc.Base64.parse(msgObj.message).toString(CryptoJS.enc.Utf8)
      //   : "";
      updateStreamLastConversion(msgObj.content, null);
    } else if (_botStreamResult == messageType) {
      // 结束, 如果有message，在末尾在展示
      if (msgObj.message) {
        const conversionRes = { ...lastConversionRes };
        if (typeof msgObj.message == "string") {
          conversionRes.text = msgObj.message;
        } else {
          let text = JSON.stringify(msgObj.message);
          if (msgObj.message?.display) {
            const key = msgObj.message?.display;
            text =
              typeof msgObj.message[key] == "string"
                ? msgObj.message[key]
                : JSON.stringify(msgObj.message[key]);
          }
          conversionRes.text = text;
        }
        conversions.value = [...conversions.value, conversionRes];
      } else {
        const res = lastConversionRes;
        res.loading = false;
        res.isBotMessage = true;
        conversions.value = [...conversions.value];
        lastConversionRes = null;
      }
    }
    // } else if (msgObj.messageType == "FLOW_EXECUTE_STATUS") {
    //   updateLastConversionFlow(msgObj);
    //   // 机器人消息
    // } else if (msgObj.messageType == "FLOW_EXECUTE_MESSAGE") {
    //   updateLastConversionMsg(msgObj);
    // } // messageType == BOT_RESULT
    // else if (msgObj.messageType == "BOT_RESULT") {
    //   updateLastConversion(msgObj);
    // } else if (msgObj.messageType == "BOT_STATE_RESULT") {
    //   const conversionRes = { ...lastConversionRes };
    //   conversionRes.text = msgObj.data;
    //   conversionRes.loading = false;
    //   conversionRes.isBotMessage = true;
    //   conversions.value = [...conversions.value, conversionRes];
    // }
    // ANSWER_RESULT 不处理
  };
  sock.onclose = function () {
    console.log("WebSocket is closed now.");
    sock?.close();
    sock = null;
  };

  sock.onerror = function (event: any) {
    console.log(event);
    sock = null;
  };
};

function releaseResource() {
  // 在这里执行一些清理工作
  console.log("在这里执行一些清理工作");
  sock?.close();
}

const handleSelect = (item) => {
  text.value = item.text;
  handleEnter();
};

watch(
  () => [props.initMsg, isInit.value],
  ([val, bool]) => {
    if (val && bool) {
      text.value = val;
      handleEnter();
    }
  },
  {
    immediate: true,
    deep: true,
  }
);

onMounted(() => {
  initWS();
  window.addEventListener("beforeunload", releaseResource);
});

onUnmounted(() => {
  window.removeEventListener("beforeunload", releaseResource);
  lastConversionRes = null;
  conversions.value = [];
});

onBeforeUnmount(() => {
  sock?.close();
  sock = null;
  isInit.value = false;
});

watch(
  () => [props.data, props.topicId],
  ([val, topicIdValue]) => {
    if (val && topicIdValue && !flag.value) {
      flag.value = true;
      detailData.value = val;
      // init(val, topicIdValue);
    }
  },
  {
    immediate: true,
    deep: true,
  }
);
</script>

<style lang="scss" scoped>
.tip {
  margin-bottom: 10px;
  padding: 10px;

  cursor: pointer;
  color: #fff;
  background-color: #409eff;
  border-radius: 4px;
  opacity: 0.6;

  &:hover {
    opacity: 1;
  }
}

.panel {
  display: flex;
  flex-direction: column;
  height: 100%;

  &-header {
    display: flex;
    flex-direction: column;
    justify-content: center;
    justify-items: center;
    flex-grow: 0;
    flex-shrink: 0;

    &-icon {
      display: flex;
      padding-bottom: 16px;
      justify-content: center;
      justify-items: center;
    }

    &-name {
      display: flex;
      padding-bottom: 16px;
      justify-content: center;
      justify-items: center;
    }

    &-desc {
      display: flex;
      padding-bottom: 16px;
      justify-content: center;
    }
  }

  &-body {
    overflow: hidden;
    flex: 1;
    margin: 0 36px;
    padding: 16px 16px 0;
    background: rgba(255, 255, 255, 0.5);

    #scrollRef::-webkit-scrollbar {
      display: none;
    }
  }

  &-footer {
    display: flex;
    flex-direction: column;
    flex-shrink: 0;
    padding: 0 36px 10px;

    .question-list {
      padding: 4px 0;
      display: flex;
      align-items: center;
      justify-items: flex-start;
      overflow-x: auto;
      width: 100%;

      &::-webkit-scrollbar {
        display: none;
      }

      span {
        display: inline-block;
        margin-right: 4px;
        cursor: pointer;
        border: 1px solid #e2edff;
        border-radius: 4px;
        font-size: 12px;
        white-space: nowrap;
        padding: 2px 4px;
        background-color: #e2edff;

        &:hover {
          color: var(--el-color-primary);
        }
      }
    }

    .footer-opts {
      display: flex;
      align-items: center;
    }
  }
}

.footer-clear-popper {
  min-width: 90px;

  .clear-tip {
    text-align: center !important;
  }
}

.message-item-question {
  margin-top: -1.5rem;
  cursor: pointer !important;
  opacity: 0.6;
}
</style>
