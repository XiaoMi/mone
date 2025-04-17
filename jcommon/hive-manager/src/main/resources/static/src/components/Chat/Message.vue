<template>
  <div :id="message.id" class="sc-message" :class="{'sc-message--user': message.meta.role === 'USER'}">
    <div class="sc-msg-inner">
      <slot name="user-avatar" :message="message" :user="user">
        <div style="display: flex;">
          <Avatar
            v-if="authorName"
            class="sc-message--avatar"
            :username="authorName"
            :src="avatarImage"
            :isUser="message.meta.role === 'USER'"
          ></Avatar>
          <FlowData v-if="message.data.flowData" :flowData="message.data.flowData" />
        </div>
      </slot>
      <div class="sc-message--content">
        <div class="sc-message--user-content">
          <MarkdownMessage v-if="message.type === 'md'" :id="id" :message="message">
            <template v-slot:default="scopedProps">
              <slot
                name="text-message-body"
                :message="scopedProps.message"
                :messageText="scopedProps.messageText"
              >
              </slot>
            </template>
            <template v-slot:text-message-toolbox="scopedProps">
              <slot name="text-message-toolbox" :message="scopedProps.message">
              </slot>
            </template>
          </MarkdownMessage>
          <HelloMessage
            v-else-if="message.type === 'hello'"
            :message="message"
            :onMessageClick="onMessageClick"
          />
          <MarkdownMessage
            v-else-if="message.type === 'typewriter'"
            :message="message"
            :id="id"
          >
            <template v-slot:default="scopedProps">
              <MarkdownRender
                name="text-message-body"
                :message="scopedProps.message"
                :messageText="scopedProps.messageText"
              >
              </MarkdownRender>
            </template>
          </MarkdownMessage>
          <MediaMessage
            v-else-if="message.type === 'audio' || message.type === 'image'  || message.type === 'images'"
            :message="message"
          />
          <FormMessage v-else-if="message.type === 'form'" :message="message" />
          <ListMessage v-else-if="message.type === 'list'" :message="message" />
          <UnknownMessage v-else />
          <!-- <div class="sc-message--footer">
          <div
            class="sc-message--ops-btn"
            @click="onMessageCmd('delete', message)"
          >
            <el-icon class="sc-message--ops-btn-icon">
              <Delete />
            </el-icon>
            <span>删除</span>
          </div>
          <div
            v-if="message.meta && message.meta.ask && message.meta.ask.prompt"
            class="sc-message--ops-btn"
            @click="onMessageCmd('refresh', message)"
          >
            <el-icon class="sc-message--ops-btn-icon"><Refresh /></el-icon>
            <span>重新生成</span>
          </div>
          <div class>
          </div>
        </div> -->
        </div>
        <div v-if="message.data.sound">
          <audio ref="audio" :src="message.data.sound" autoplay />
        </div>
        <div class="sc-message--ops">
          <div
            v-if="message.meta && message.meta.ask && message.meta.ask.prompt"
            class="sc-message--ops-item"
            @click="onMessageCmd('refresh', message)"
          >
            <el-icon :size="14" color="#FFF">
              <font-awesome-icon icon="fa-solid fa-rotate" />
            </el-icon>
          </div>
          <div v-else></div>
          <!-- <el-popover placement="right">
            <template #reference>
              <div class="sc-message--ops-item" style="display: flex">
                <el-icon :size="14" color="#FFF">
                  <font-awesome-icon icon="fa-solid fa-ellipsis-vertical" />
                </el-icon>
              </div>
            </template>
            <div>
              <div
                class="sc-message--ops-item"
                @click="onMessageCmd('delete', message)"
              >
                <el-icon :size="14" color="#FFF">
                  <font-awesome-icon icon="fa-solid fa-trash" />
                </el-icon>
                <span style="margin-left: 4px">删除</span>
              </div>
              <div
                v-if="message.type === 'md' || message.type === 'hello'"
                class="sc-message--ops-item"
                @click="onMessageCmd('audio', message)"
              >
                <el-icon :size="14" color="#FFF">
                  <font-awesome-icon icon="fa-solid fa-volume-high" />
                </el-icon>
                <span style="margin-left: 4px">朗读</span>
              </div>
              <div
                v-if="message.type === 'audio'"
                class="sc-message--ops-item"
                @click="onMessageCmd('text', message)"
              >
                <el-icon :size="14" color="#FFF">
                  <font-awesome-icon icon="fa-solid fa-file-lines" />
                </el-icon>
                <span style="margin-left: 4px">文字</span>
              </div>
            </div>
          </el-popover> -->
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Avatar from "./Avatar.vue";
import HelloMessage from "./messages/HelloMessage.vue";
import MarkdownMessage from "./messages/MarkdownMessage.vue";
import MarkdownRender from "./messages/MarkdownRender.vue";
import MediaMessage from "./messages/MediaMessage.vue";
import FormMessage from "./messages/FormMessage.vue";
import UnknownMessage from "./messages/UnknownMessage.vue";
import ListMessage from "./messages/ListMessage.vue";
import FlowData from "./FlowData/index.vue";
import AudioPlayer from "@/components/audio-player/index.vue";

export default {
  components: {
    Avatar,
    HelloMessage,
    MarkdownMessage,
    MarkdownRender,
    MediaMessage,
    FormMessage,
    UnknownMessage,
    ListMessage,
    FlowData,
    AudioPlayer
  },
  props: {
    id: {
      type: Number,
      required: true,
    },
    message: {
      type: Object,
      required: true,
    },
    user: {
      type: Object,
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
  },
  computed: {
    authorName() {
      return this.user && (this.user.cname || this.user.username);
    },
    avatarImage() {
      return this.user && this.user.avatar;
    },
  },
  methods: {
    playAudio(cmd: string, message: Record<string, any>) {
      if (message.data.sound) {
        const audio = this.$refs.audio;
        //@ts-ignore
        this.$message.info("开始播放");
        if (audio) {
          (audio as HTMLAudioElement).play();
        }
      } else {
        this.onMessageCmd(cmd, message);
      }
    },
  },
};
</script>

<style scoped lang="scss">
.sc-message {
  margin-bottom: 15px;
  width: 100%;
  animation: fadeIn 0.3s ease-out;

  .sc-msg-inner {
    width: 100%;
    display: flex;
    flex-direction: column;
  }
  &.sc-message--user {
    .sc-msg-inner {
      align-items: flex-end;
    }
    .sc-message--content {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
    }
  }
}
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.sc-message--content {
  display: inline-flex;
  flex-direction: column;
  max-width: 100%;
}

.sc-message--user-content {
  display: inline-block;
  max-width: 100%;
  width: fit-content;
  padding: 5px 20px;
  border-radius: 6px;
  font-weight: 300;
  font-size: 14px;
  position: relative;
  -webkit-font-smoothing: subpixel-antialiased;
  // background-color: rgb(39, 39, 39);
  color: #fff;
  background-color: rgba(58, 58, 58, 0.5);
}

.sc-message--footer {
  padding: 5px 20px;
  display: flex;

  & .sc-message--ops-btn {
    margin-right: 18px;
    padding: 3px 6px;
    border-radius: 6px;
    display: flex;
    align-items: center;

    cursor: pointer;

    color: rgba(224, 236, 255, 0.8);
    font-size: 12px;

    &:hover {
      color: #fff;
      background-color: rgb(39, 39, 39);
    }
  }
}

.sc-message--ops-btn-icon {
  padding-right: 2px;
  font-size: 20px;
}

.sc-message--avatar {
  background-repeat: no-repeat;
  background-size: 100%;
  background-position: center;
  min-width: 30px;
  min-height: 30px;
  border-radius: 50%;
  margin-right: 15px;
}

.sc-message--text {
  // padding: 5px 20px;
  // border-radius: 6px;
  font-weight: 300;
  font-size: 14px;
  line-height: 1.4;
  position: relative;
  -webkit-font-smoothing: subpixel-antialiased;
  .sc-message--text-body {
    .sc-message--text-content {
      white-space: pre-wrap;
    }
  }
  &:hover .sc-message--toolbox {
    left: -20px;
    opacity: 1;
  }
  &.confirm-delete:hover .sc-message--toolbox {
    left: -90px;
  }
  &.confirm-delete .sc-message--toolbox {
    width: auto;
  }
  .sc-message--toolbox {
    transition: left 0.2s ease-out 0s;
    white-space: normal;
    opacity: 0;
    position: absolute;
    left: 0px;
    width: 25px;
    top: 0;
    button {
      background: none;
      border: none;
      padding: 0px;
      margin: 0px;
      outline: none;
      width: 100%;
      text-align: center;
      cursor: pointer;
      &:focus {
        outline: none;
      }
    }
  }
}

.sc-message--text code {
  font-family: "Courier New", Courier, monospace !important;
}

.sc-message--ops {
  margin-left: 0px;
  margin-right: 0px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.sc-message--ops-item {
  padding: 5px 0;
  display: flex;
  align-items: center;
  font-size: 12px;

  cursor: pointer;
}
</style>
