<template>
  <div class="sc-chat-window" :class="{ opened: isOpen, closed: !isOpen }">
    <MessageList
      :messages="messages"
      :always-scroll-to-bottom="alwaysScrollToBottom"
      :onMessageCmd="onMessageCmd"
      :onMessageClick="onMessageClick"
      @scrollToTop="$emit('scrollToTop')"
    >
      <template v-slot:user-avatar="scopedProps">
        <slot
          name="user-avatar"
          :user="scopedProps.user"
          :message="scopedProps.message"
        >
        </slot>
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
        <slot
          name="text-message-toolbox"
          :message="scopedProps.message"
          :me="scopedProps.me"
        >
        </slot>
      </template>
    </MessageList>
    <div v-if="showApprove" class="approve-box">
      <div class="approve-box-title">
        <el-tag type="warning"><font-awesome-icon :icon="['fas', 'clipboard-question']" /><span style="margin-left: 6px;">您是否要继续往下执行</span></el-tag>
      </div>
      <div class="approve-box-btn">
        <el-button type="primary" size="small" @click="approve">继续</el-button>
        <el-button type="danger" size="small" @click="cancel">取消</el-button>
      </div>
    </div>
    <UserInput
      :on-submit="onUserInputSubmit"
      :placeholder="placeholder"
      @onType="$emit('onType', $event)"
      @edit="$emit('edit', $event)"
      :initCodePrompt="initCodePrompt"
    />
  </div>
</template>

<script lang="ts">
import MessageList from "./MessageList.vue";
// 202408261版本之后使用
import UserInput from "./UserInput.vue";
// 202408261版本之前使用
import UserInputOld from "./UserInputOld.vue";
import { useIdeaInfoStore } from "@/stores/idea-info";
import { mapState } from "pinia";
import { useEditStore } from "@/stores/edit";
import util from "@/libs/util";
export default {
  components: {
    MessageList,
    UserInput,
    UserInputOld,
  },
  props: {
    onUserInputSubmit: {
      type: Function,
      required: true,
    },
    onMessageClick: {
      type: Function,
      required: true,
    },
    onMessageCmd: {
      type: Function,
      required: true,
    },
    initCodePrompt: {
      type: Function,
      required: true,
    },
    messageList: {
      type: Array,
      default: () => [],
    },
    isOpen: {
      type: Boolean,
      default: () => false,
    },
    placeholder: {
      type: String,
      required: true,
    },
    alwaysScrollToBottom: {
      type: Boolean,
      required: true,
    },
  },
  data() {
    return {};
  },
  computed: {
    ...mapState(useIdeaInfoStore, ["isShowFile"]),
    ...mapState(useEditStore, ["showApprove", "setShowApprove", "disableEdit", "enableEdit"]),
    messages() {
      // console.log("messageList", this.messageList);
      // 将最后一条个属性isLast:true, 否则是false
      let messages = this.messageList.map((it, index) => {
        return {
          ...it,
          data: {
            ...it.data,
            isLast: index == this.messageList.length - 1,
          },
        };
      });
      console.log("messages>>", messages);
      return messages;
    },
  },
  methods: {
    approve() {
      try {
        util.approve({
          message: 'approve',
        });
      } catch (error) {
        console.error(error);
      } finally {
        this.enableEdit();
        this.setShowApprove(false);
      }
    },
    cancel() {
      try {
        util.approve({
          message: 'cancel',
        });
      } catch (error) {
        console.error(error);
      } finally {
        this.enableEdit();
        this.setShowApprove(false);
      }
    },
  },
};
</script>

<style scoped lang="scss">
.sc-chat-window {
  height: 80vh;
  background: rgba(15, 15, 35, 0.7);
  border-radius: 15px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(100, 100, 255, 0.2);
  box-shadow: 0 0 30px rgba(0, 100, 255, 0.3);
  z-index: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

.sc-chat-window::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, #00dbde, #fc00ff, #00dbde, #fc00ff);
  background-size: 400%;
  z-index: -1;
  border-radius: 16px;
  opacity: 0.7;
  animation: glowing-border 20s linear infinite;
}

@keyframes glowing-border {
  0% { background-position: 0 0; }
  50% { background-position: 400% 0; }
  100% { background-position: 0 0; }
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

.approve-box {
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

.approve-box-btn {
  display: flex;
}
</style>
