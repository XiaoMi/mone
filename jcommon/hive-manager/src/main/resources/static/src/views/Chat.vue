<template>
  <div class="chat-container">
    <div id="particles-js"></div>
    <ChatWindow
      placeholder="placeholder"
      :messageList="messageList"
      :isOpen="true"
      :alwaysScrollToBottom="true"
      :onMessageClick="messageClick"
      :onMessageCmd="onMessageCmd"
      :onUserInputSubmit="sendMessage"
      :initCodePrompt="initCodePrompt"
      @scrollToTop="scrollToTop"
    />
  </div>
</template>
<script setup lang="ts">
import ChatWindow from '@/components/Chat/ChatWindow.vue'
import { useUserStore } from "@/stores/user";
import {
  useChatContextStore,
  type Message,
} from "@/stores/chat-context";
import { onMounted, onBeforeUnmount } from "vue"
import { getAgentDetail } from '@/api/agent'
import { useRoute } from 'vue-router'
import { connectWebSocket } from '@/api/wsConnect'
const route = useRoute()
const { getChatContext, setMessageList, addMessage, setProject, setModule, setLoading, messageList } =
  useChatContextStore();
const { user } = useUserStore();

  const scrollToTop = () => {
    // 滚动到顶部
  }
  const initCodePrompt = () => {
      setMessageList([]);
    //   this.getCodePrompt();
    }
  const sendMessage = async (message: Message) => {
    addMessage(message);
    // 发送给ws
  }

const messageClick = async (item: { type: string; text: string; params: any }) => {
      // 发消息
      addMessage({
        type: "md",
        author: {
          cname: user.cname,
          username: user.username,
          avatar: user.avatar,
        },
        meta: {
          role: "USER",
        },
        data: {
          text: item.text,
        },
      });
      if (item.type === "question") {
        addMessage(addHelloMessage(item.params.prompt));
      }
    }

    const addHelloMessage = (item: any): Message => {
      if (item.msg.startsWith("<stock-transaction>") || item.msg.startsWith("<stock-order>")) {
        return {
          type: "md",
          author: {
          cname: user.cname,
          username: user.username,
          avatar: user.avatar,
          },
          meta: {
            role: "IDEA",
          },
          data: {
            text: item.msg,
          },
        }
      }
      return {
        type: "hello",
        author: {
          cname: "this.mioneName",
          username: "this.mioneName",
          avatar: "this.mioneUrl",
        },
        meta: {
          role: "IDEA",
        },
        data: {
          hello: (item.msg || "")
            .replace("${username}", user.username)
            .replace("${version}", `版本`),
          links:
            (item.promptInfoList &&
              item.promptInfoList.map((it) => {
                return {
                  prefix: it.prefix || "",
                  suffix: it.suffix || "",
                  label: it.desc || "",
                  src: it.src || "",
                  value: it.promptName,
                  type: it.type,
                  params: {
                    prompt: it.promptName,
                    showDialog: it.showDialog || "false",
                    meta: it.meta,
                    desc: it.desc,
                  },
                };
              })) ||
            [],
        },
      };
    }
    const messageDelete = async (item: Message) => {
      const messageList: Message[] = [];
      messageList.forEach((it) => {
        if (it !== item) {
          messageList.push(it);
        }
      });
      setMessageList(messageList);
    }
    const onMessageCmd = async (type: string, item: Message) => {
      switch (type) {
        case "delete":
          try {
            await messageDelete(item);
          } catch (e) {
            // 
          }
          break;
        case "refresh":
        //   await this.resendMessage(item);
          break;
        case "audio":
          try {
            // await this.myPlaySound(item);
          } catch (e) {
            //
          }
          break;
        default:
          break;
      }
    }
onBeforeUnmount(() => {
    initCodePrompt()
})
onMounted(async () => {
    try {
      const { data } = await getAgentDetail(Number(route.query.serverAgentId))
      if (data.code === 200) {
        const agent = data.data
        addMessage({
            type: "md",
            author: {
                cname: agent.name,
                username: agent.name,
                avatar: `data:image/jpeg;base64,${agent.image}`,
            },
            meta: {
                role: "IDEA",
            },
            data: {
                text: `你好，我是 ${agent.name}，有什么可以帮你的吗？`,
            },
        });
        connectWebSocket(() => {
            console.log('WebSocket connection closed');
        });
      }
    } catch (error) {
      console.error('获取Agent详情失败:', error)
    }

    // 初始化粒子效果
    particlesJS("particles-js", {
      particles: {
        number: {
          value: 80,
          density: {
            enable: true,
            value_area: 800
          }
        },
        color: {
          value: "#00ffff"
        },
        shape: {
          type: "circle"
        },
        opacity: {
          value: 0.5,
          random: true,
          anim: {
            enable: true,
            speed: 1,
            opacity_min: 0.1,
            sync: false
          }
        },
        size: {
          value: 3,
          random: true,
          anim: {
            enable: true,
            speed: 2,
            size_min: 0.1,
            sync: false
          }
        },
        line_linked: {
          enable: true,
          distance: 150,
          color: "#00ffff",
          opacity: 0.3,
          width: 1
        },
        move: {
          enable: true,
          speed: 1,
          direction: "none",
          random: true,
          straight: false,
          out_mode: "out",
          bounce: false,
          attract: {
            enable: true,
            rotateX: 600,
            rotateY: 1200
          }
        }
      },
      interactivity: {
        detect_on: "canvas",
        events: {
          onhover: {
            enable: true,
            mode: "grab"
          },
          onclick: {
            enable: true,
            mode: "push"
          },
          resize: true
        },
        modes: {
          grab: {
            distance: 140,
            line_linked: {
              opacity: 1
            }
          },
          push: {
            particles_nb: 4
          }
        }
      },
      retina_detect: true
    });
})
</script>

<style scoped>
.chat-container {
  width: 100%;
  height: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #0f0c29 0%, #302b63 50%, #24243e 100%);
  position: relative;
  overflow: hidden;
}

#particles-js {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

/* 添加量子背景效果 */
.chat-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    linear-gradient(rgba(100, 100, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(100, 100, 255, 0.1) 1px, transparent 1px);
  background-size: 30px 30px;
  animation: gridMove 20s linear infinite;
  z-index: 0;
}

/* 添加发光边框效果 */
.chat-container::after {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, #00dbde, #fc00ff, #00dbde, #fc00ff);
  background-size: 400%;
  z-index: -1;
  filter: blur(5px);
  animation: glowing 20s linear infinite;
}

.chat-container .sc-chat-window {
  width: 70%;
  height: 100%;
  margin: 0 auto;
  background: rgba(15, 15, 35, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  border: 1px solid rgba(100, 100, 255, 0.2);
  box-shadow: 0 0 30px rgba(0, 100, 255, 0.3);
  position: relative;
  z-index: 2;
}

/* 添加量子光环效果 */
.quantum-ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(0, 255, 255, 0.3);
  z-index: 1;
}

.ring-1 {
  width: 300px;
  height: 300px;
  top: -150px;
  right: -150px;
  animation: rotate 30s linear infinite;
}

.ring-2 {
  width: 200px;
  height: 200px;
  bottom: -100px;
  left: -100px;
  animation: rotate 20s linear infinite reverse;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes gridMove {
  0% {
    transform: translateY(0);
  }
  100% {
    transform: translateY(30px);
  }
}

@keyframes glowing {
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
</style>
