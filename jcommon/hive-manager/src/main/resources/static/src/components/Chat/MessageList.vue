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
        @onClick2Conversion="(id) => {
            $emit('onClick2Conversion', id)
          }"
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
    <!-- 永久跟随按钮 -->
    <div class="follow-toggle-btn" @click="toggleFollow">
      <el-tooltip :content="isFollow ? '点击取消自动滚动到底部' : '点击开启自动滚动到底部'" placement="left">
        <div class="follow-btn" :class="{ active: isFollow }">
          {{ isFollow ? '跟随' : '停止' }}
        </div>
      </el-tooltip>
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
    ...mapState(useEditStore, ['isFollow', 'showFollow']),

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
    if (this.shouldScrollToBottom()) this.$nextTick(() => this._scrollDown());
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
    toggleFollow() {
      const editStore = useEditStore();
      editStore.setIsFollow(!this.isFollow);
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

.follow-toggle-btn {
  position: absolute;
  bottom: 20px;
  right: 20px;
  z-index: 100;
  
  .follow-btn {
    min-width: 60px;
    height: 36px;
    padding: 8px 16px;
    border-radius: 18px;
    font-size: 12px;
    font-weight: 500;
    cursor: pointer;
    user-select: none;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    
    // 默认状态（停止跟随）
    background: rgba(0, 0, 0, 0.4);
    color: rgba(255, 255, 255, 0.8);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    
    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);
      background: rgba(0, 0, 0, 0.6);
      color: rgba(255, 255, 255, 0.95);
      border-color: rgba(255, 255, 255, 0.3);
    }
    
    // 激活状态（跟随中）
    &.active {
      background: linear-gradient(135deg, #00d4ff 0%, #0099cc 100%);
      color: #ffffff;
      border-color: #00d4ff;
      box-shadow: 0 2px 12px rgba(0, 212, 255, 0.3);
      
      &:hover {
        background: linear-gradient(135deg, #00b8e6 0%, #0088bb 100%);
        box-shadow: 0 4px 20px rgba(0, 212, 255, 0.4);
        transform: translateY(-2px);
      }
      
      &::before {
        content: '';
        position: absolute;
        top: -1px;
        left: -1px;
        right: -1px;
        bottom: -1px;
        background: linear-gradient(135deg, #00d4ff, #0099cc);
        border-radius: 19px;
        z-index: -1;
        opacity: 0.5;
        animation: pulse-glow 2s infinite;
      }
    }
  }
}

@keyframes pulse-glow {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.05);
  }
}

</style>
