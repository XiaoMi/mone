<template>
  <div ref="scrollList" class="sc-message-list" @scroll="handleScroll">
    <div ref="scrollList1">
      <Message
        v-for="(message, idx) in messages"
        v-memo="[(message as TypeMessage).data.text]"
        :key="idx"
        :id="idx"
        :message="(message as TypeMessageList)"
        :user="profile((message as TypeMessage).author)"
        :onMessageClick="onMessageClick"
        :onMessageCmd="onMessageCmd"
        :onPlayAudio="onPlayAudio"
        @pidAction="handlePidAction"
      >
        <template v-slot:user-avatar="scopedProps">
          <slot
            name="user-avatar"
            :user="scopedProps.user"
            :message="scopedProps.message"
          ></slot>
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
          <slot name="system-message-body" :message="scopedProps.message">
          </slot>
        </template>
        <template v-slot:text-message-toolbox="scopedProps">
          <slot
            name="text-message-toolbox"
            :message="scopedProps.message"
            :me="scopedProps.me"
          >
          </slot>
        </template>
      </Message>
      <div class="loading" v-if="isLoading">
        <span></span>
        <span></span>
        <span></span>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import type {
  MessageList as TypeMessageList,
  Message as TypeMessage,
} from "@/stores/chat-context";
import Message from "./Message.vue";

import { useChatContextStore } from "@/stores/chat-context"
import { useIdeaInfoStore } from "@/stores/idea-info";
import { mapState } from "pinia";
import { useEditStore } from '@/stores/edit'

let resizeObserver: ResizeObserver;

export default {
  components: {
    Message,
  },
  props: {
    messages: {
      type: Array,
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
    alwaysScrollToBottom: {
      type: Boolean,
      required: true,
    },
    onPlayAudio: {
      type: Function,
      required: true,
    },
  },
  data() {
    return {
      initialScrollTop: null,
      isUserScrolling: false,
      scrollTimer: null,
      timer: 0,
    };
  },
  watch: {
    isLoading: {
      handler(val){
        console.log(val)
      },
      deep: true,
      immediate: true
    }
  },
  computed: {
    ...mapState(useChatContextStore, ["isLoading"]),
    ...mapState(useEditStore, ['isFollow', 'setShowFollow']),

  },
  mounted() {
    this.watchScrollList();
    this.$nextTick(this._scrollDown());
  },
  beforeUnmount() {
    resizeObserver.unobserve(this.$refs.scrollList);
    clearTimeout(this.timer);
  },
  updated() {
    clearTimeout(this.timer);
    this.timer = setTimeout(() => {
      this.setShowFollow(false);
      clearTimeout(this.timer);
    }, 3000);
    if (this.shouldScrollToBottom()) this.$nextTick(this._scrollDown());
  },
  methods: {
    _scrollDown() {
      if (this.isFollow) {
        this.$nextTick(() => {
          this.$refs.scrollList.scrollTop = this.$refs.scrollList.scrollHeight;
          if (!this.initialScrollTop) {
            this.initialScrollTop = this.$refs.scrollList.scrollTop;
          }
        });
      }
    },
    // _scrollDown() {
    //   this.$refs.scrollList.scrollTop = this.$refs.scrollList.scrollHeight;
    //   !this.initialScrollTop
    //     ? (this.initialScrollTop = this.$refs.scrollList.scrollTop)
    //     : "";
    // },
    handleScroll(e: any) {
      if (e.target.scrollTop === 0) {
        this.$emit("scrollToTop");
      }
    },
    isNearBottom() {
      const { scrollTop, scrollHeight, clientHeight } = this.$refs.scrollList;
      return scrollHeight - scrollTop - clientHeight < 100;
    },
    shouldScrollToBottom() {
      const scrollTop = this.$refs.scrollList.scrollTop;
      const scrollable = scrollTop > this.$refs.scrollList.scrollHeight - 600;
      return this.alwaysScrollToBottom || scrollable;
    },
    profile(author: { username: string; cname: string; avatar: string }) {
      return author;
    },
    watchScrollList() {
      resizeObserver = new ResizeObserver(() => {
        const value =
          this.$refs.scrollList.scrollHeight - this.$refs.scrollList.scrollTop;
        if (
          this.initialScrollTop === this.$refs.scrollList.scrollTop ||
          (this.$refs.scrollList.scrollTop > this.initialScrollTop &&
            value > this.$refs.scrollList.offsetHeight)
        ) {
          this._scrollDown();
        }
      });

      resizeObserver.observe(this.$refs.scrollList1);
    },
    handlePidAction(data: { pid: string; action: string }) {
      // 向上传递 pidAction 事件
      this.$emit('pidAction', data);
    },
  },
};
</script>

<style scoped lang="scss">
.sc-message-list {  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background-image:
    linear-gradient(rgba(100, 100, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(100, 100, 255, 0.1) 1px, transparent 1px);
  background-size: 30px 30px;
}

.sc-message-list::-webkit-scrollbar {
  display: none;
}

// loading
.loading {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.loading span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #00ffff;
  animation: pulse 1s infinite;
}

.loading span:nth-child(2) { animation-delay: 0.2s; }
.loading span:nth-child(3) { animation-delay: 0.4s; }

@keyframes pulse {
  0% { transform: scale(0.8); opacity: 0.5; }
  50% { transform: scale(1.2); opacity: 1; }
  100% { transform: scale(0.8); opacity: 0.5; }
}

@keyframes scale {
  0% {
    transform: scale(1);
  }
  100% {
    transform: scale(0.6);
  }
}
.btns-wrap {
  position: absolute;
  bottom: 120px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 10;
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: center;
}
.inputing {
  display: flex;
  align-items: center;
  cursor: pointer;
  border: 1px solid var(--el-color-warning);
  border-radius: 18px;
  padding: 5px 10px;
  user-select: none;
  color: var(--el-color-warning);
  background-color: rgba(48, 48, 48, 0.7);
  transform: scale(0.8);
}
.inputing-text {
  margin-left: 8px;
  font-size: 12px;
}
.inputing:hover {
  border-color: var(--el-color-primary);
  color: var(--el-color-primary);
  background-color: rgba(48, 48, 48, 1);
}
.pause-icon {
  transform: scale(0.9);
}
.inputing:hover .pause-icon {
  animation: infinite-scale 1.5s infinite;
}

@keyframes infinite-scale {
  0% {
    transform: scale(0.9);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(0.9);
  }
}

</style>
