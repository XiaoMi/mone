<template>
  <div class="sc-chat-window" :class="{ opened: isOpen, closed: !isOpen }">
    <InstanceSelect
      :onClearHistory="onClearHistory"
      :onOffline="onOffline"
      :onStopMsg="onStopMsg"
      :onSwitchAgent="onSwitchAgent"
      :onSwitchLlm="onSwitchLlm"
      :onExecuteMcpCommand="onExecuteMcpCommand"
      :onExecuteSystemCommand="onExecuteSystemCommand"
    />
    <MessageList
      :messages="messages"
      :always-scroll-to-bottom="alwaysScrollToBottom"
      :onMessageCmd="onMessageCmd"
      :onMessageClick="onMessageClick"
      @scrollToTop="$emit('scrollToTop')"
      :onPlayAudio="onPlayAudio"
      @pidAction="handlePidAction"
      @onClick2Conversion="(id) => {
            $emit('onClick2Conversion', id)
          }"
    >
      <template #user-avatar="scopedProps">
        <slot name="user-avatar" :user="scopedProps.user" :message="scopedProps.message"> </slot>
      </template>
      <template v-slot:text-message-body="scopedProps">
        <slot
          name="text-message-body"
          :message="scopedProps.message"
          :messageText="scopedProps.messageText"
          :messageColors="scopedProps.messageColors"
          :me="scopedProps.me"
        >
        </slot>
      </template>
      <template v-slot:system-message-body="scopedProps">
        <slot name="system-message-body" :message="scopedProps.message"> </slot>
      </template>
      <template v-slot:text-message-toolbox="scopedProps">
        <slot name="text-message-toolbox" :message="scopedProps.message" :me="scopedProps.me">
        </slot>
      </template>
    </MessageList>
    <div v-if="showApprove" class="approve-box">
      <div class="approve-box-title">
        <el-tag type="warning"
          ><font-awesome-icon :icon="['fas', 'clipboard-question']" /><span style="margin-left: 6px"
            >您是否要继续往下执行</span
          ></el-tag
        >
      </div>
      <div class="approve-box-btn">
        <el-button type="primary" size="small" @click="approve">继续</el-button>
        <el-button type="danger" size="small" @click="cancel">取消</el-button>
      </div>
    </div>
    <div class="follow-box" v-if="showFollow">
      <div class="follow-box-btn">
        <el-button type="warning" size="small" @click="setIsFollow(!isFollow)">{{ isFollow ? '取消跟随' : '跟随输出' }}</el-button>
      </div>
    </div>
    <McpServerNotification />
    <UserInput
      :on-submit="onUserInputSubmit"
      :placeholder="placeholder"
      @onType="$emit('onType', $event)"
      @edit="$emit('edit', $event)"
      :initCodePrompt="initCodePrompt"
      :changeSendMethod="changeSendMethod"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import MessageList from './MessageList.vue'
import UserInput from './UserInput.vue'
import McpServerNotification from './McpServerNotification.vue'
import { useIdeaInfoStore } from '@/stores/idea-info'
import { useEditStore } from '@/stores/edit'
import { storeToRefs } from 'pinia'
import util from '@/libs/util'
import InstanceSelect from './InstanceSelect.vue'
import { type MessageClickPayload } from "./messages/HelloMessage.vue";

import type {
  MessageList as TypeMessageList,
  Message as TypeMessage,
} from "@/stores/chat-context"

interface Props {
  onUserInputSubmit: (data: any) => Promise<void>;
  onMessageClick: (message: MessageClickPayload ) => Promise<void>;
  onMessageCmd: (cmd: string, message: TypeMessage) => Promise<void>;
  initCodePrompt: () => void;
  messageList?: TypeMessageList;
  isOpen?: boolean;
  placeholder: string;
  alwaysScrollToBottom: boolean;
  changeSendMethod: (method: string) => void;
  onPlayAudio: (text: string) => void;
  onClearHistory: () => void;
  onOffline: () => void;
  onStopMsg: () => void;
  onSwitchAgent?: (agent: any) => void;
  onSwitchLlm?: (llm: any) => void;
  onExecuteMcpCommand?: (command: any) => void;
  onExecuteSystemCommand?: (command: any) => void;
}

interface Emits {
  (e: 'scrollToTop'): void;
  (e: 'pidAction', data: { pid: string; action: string }): void;
  (e: 'onClick2Conversion', id: { id: string }): void;
  (e: 'onType', event: any): void;
  (e: 'edit', event: any): void;
}

const props = withDefaults(defineProps<Props>(), {
  messageList: () => [],
  isOpen: false,
});

const emit = defineEmits<Emits>();

// Store
const ideaInfoStore = useIdeaInfoStore();
const editStore = useEditStore();

const { isShowFile } = storeToRefs(ideaInfoStore);
const { 
  showApprove, 
  showFollow, 
  isFollow 
} = storeToRefs(editStore);

const {
  setShowApprove,
  disableEdit,
  enableEdit,
  setShowFollow,
  setIsFollow
} = editStore;

// Computed
const messages = computed(() => {
  // console.log("messageList", props.messageList);
  // 将最后一条个属性isLast:true, 否则是false
  return props.messageList.map((it, index) => {
    return {
      ...it,
      data: {
        ...it.data,
        isLast: index === props.messageList.length - 1,
      },
    };
  });
});

// Methods
const approve = () => {
  try {
    util.approve({
      message: 'approve',
    });
  } catch (error) {
    console.error(error);
  } finally {
    enableEdit();
    setShowApprove(false);
  }
};

const cancel = () => {
  try {
    util.approve({
      message: 'cancel',
    });
  } catch (error) {
    console.error(error);
  } finally {
    enableEdit();
    setShowApprove(false);
  }
};

const handlePidAction = (data: { pid: string; action: string }) => {
  // 向上传递 pidAction 事件到 Chat.vue
  emit('pidAction', data);
};
</script>

<style lang="scss">
.sc-chat-window {
  // height: 80vh;
  height: 100%;
  background: var(--el-color-chat-window-background);
  border-radius: 15px;
  backdrop-filter: blur(10px);
  border: 1px solid var(--el-color-chat-window-border-glow);
  box-shadow: 0 0 30px var(--el-color-chat-window-border-glow);
  z-index: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    background: var(--el-color-chat-window-border-glow);
    background-size: 400%;
    z-index: -1;
    border-radius: 16px;
    opacity: var(--el-color-chat-window-border-glow-opacity);
    animation: var(--el-color-chat-window-border-glow-animation);
  }
}

@keyframes glowing-border {
  0% {
    background-position: 0 0;
  }
  50% {
    background-position: 400% 0;
  }
  100% {
    background-position: 0 0;
  }
}

.sc-chat-window.closed {
  opacity: 0;
  display: none;
  bottom: 90px;
}

@keyframes fadeIn {
  0% {
    display: none;
    opacity: 0;
  }

  100% {
    display: flex;
    opacity: 1;
  }
}

.sc-message--me {
  text-align: right;
}
.sc-message--them {
  text-align: left;
}

.approve-box, .follow-box {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 10px;
  box-shadow: 0px 7px 40px 2px rgba(197, 198, 200, 0.1);
}

.approve-box-title {
  margin-bottom: 10px;
}

.approve-box-btn, .follow-box-btn {
  display: flex;
}
</style>
