<template>
  <div ref="scrollList" class="sc-message-list" @scroll="handleScroll">
    <div ref="scrollList1">
      <Demo></Demo>
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
      <div class="btns-wrap" v-else-if="showDiscardBtn && isShowPause">
        <div class="inputing" @click.stop="handleDiscard">
          <svg t="1731982340192" class="pause-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="7323" width="16" height="16"><path d="M512 0a512 512 0 1 0 0 1024A512 512 0 0 0 512 0z m0 928a416 416 0 1 1 0-832 416 416 0 0 1 0 832zM320 320h384v384H320z" p-id="7324" fill="currentColor"></path></svg>
          <span class="inputing-text">停止输出</span>
        </div>
        <div class="inputing" @click.stop="handleScrollToBottom">
          <svg t="1732181144291" v-if="scrollToBottom" class="pause-icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="6454" width="16" height="16"><path d="M512.005 62.008c-248.528 0-450 201.472-450 449.995 0 248.528 201.472 449.995 450 449.995 248.524 0 449.992-201.468 449.992-449.995 0-248.524-201.468-449.995-449.992-449.995z m359.996 449.997c0 69.339-19.61 134.096-53.577 189.038L265.996 249.182c64.361-60.27 150.873-97.175 246.008-97.175C710.821 152.007 872 313.182 872 512.003z m-719.994 0c0-71.436 20.805-138.011 56.69-193.998l553.556 452.78C697.475 833.443 609.242 872 512.003 872c-198.826 0-359.996-161.174-359.996-359.996z" p-id="6455" fill="currentColor"></path></svg>
          <svg t="1732181044851" v-else class="pause-icon" viewBox="0 0 1156 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4507" width="16" height="16"><path d="M1093.860207 540.871472c34.676456 0 62.411978-30.049167 62.411979-62.411978a62.143934 62.143934 0 0 0-62.411979-62.397871H613.101755a62.143934 62.143934 0 0 0-62.411979 62.397871 62.143934 62.143934 0 0 0 62.411979 62.411978zM728.657025 827.481271c-32.362812 0-60.098333 30.049167-60.098334 64.725623s27.735522 62.411978 60.098334 62.411978H1096.159745c34.662349 0 60.098333-27.735522 60.098333-62.411978 0-36.975993-27.735522-64.711515-60.098333-64.711515z m-140.991255-187.221827c-11.554116-13.881869-27.735522-18.495051-46.216465-18.495051a63.145573 63.145573 0 0 0-43.916928 18.495051L365.781594 792.847138V0.000578H240.957638v795.117882L109.206854 642.573089c-13.867761-13.867761-30.049167-20.808695-46.230572-20.808696s-32.362812 4.62729-43.916928 16.181406c-25.393662 25.393662-25.393662 64.725623 0 90.1475l238.079689 275.098006c13.867761 16.181406 30.049167 20.808695 48.544218 20.808695 13.867761 0 32.362812-6.926827 43.916927-18.480943L589.979415 728.093299c23.108232-23.108232 23.108232-62.411978-2.313645-87.833855zM1093.860207 124.824535a62.143934 62.143934 0 0 0 62.411979-62.411978A62.143934 62.143934 0 0 0 1093.860207 0.000578H613.101755a62.143934 62.143934 0 0 0-62.411979 62.411979 62.143934 62.143934 0 0 0 62.411979 62.411978z m0 0" fill="currentColor" p-id="4508"></path></svg>
          <span class="inputing-text">{{scrollToBottom ? '取消跟随' : '跟随输出'}}</span>
        </div>
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
import util from "@/libs/util";
import Demo from '@/components/Chat/components/tokenUsage/demo.vue'

const { addDiscardId, setScrollToBottom, setShowDiscardBtn } = useChatContextStore();

let resizeObserver: ResizeObserver;

export default {
  components: {
    Message,
    Demo,
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
    ...mapState(useChatContextStore, ["isLoading", "showDiscardBtn", "scrollToBottom"]),
    ...mapState(useIdeaInfoStore, ['isShowPause']),
  },
  mounted() {
    this.watchScrollList();
    this.$nextTick(this._scrollDown());
  },
  beforeUnmount() {
    resizeObserver.unobserve(this.$refs.scrollList);
  },
  updated() {
    if (this.shouldScrollToBottom()) this.$nextTick(this._scrollDown());
  },
  methods: {
    handleDiscard() {
      util.stopWs()
      setShowDiscardBtn(false)
      addDiscardId()
    },
    handleScrollToBottom() {
      setScrollToBottom(!this.scrollToBottom)
    },
    _scrollDown() {
      if (this.scrollToBottom) {
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
