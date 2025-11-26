<template>
  <div class="sc-message--text">
    <slot :message="message" :messageText="messageText">
      <div class="sc-message--text-content" ref="textCtxRef">
        <div v-if="cmd">
          <span style="color: aqua">{{ cmd.slice(0, 1) }}</span
          >{{ cmd.slice(1) }}
        </div>
        <div v-if="knowledgeBase" v-html="knowledgeBase"></div>
        <div ref="textRef" class="markdown-body" v-html="messageText"></div>
      </div>
    </slot>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUpdated, onUnmounted, nextTick } from 'vue'
import mdKatex from "@traptitech/markdown-it-katex";
// @ts-expect-error No type definitions available for markdown-it-link-attributes
import mila from "markdown-it-link-attributes";
import hljs from "highlight.js";
import MarkdownIt from "markdown-it";
import util from "@/libs/util";
import { copyToClip } from "@/libs/copy";
import arrowUrl from "../assets/imgs/arrow1.png";

import "../assets/scss/github-markdown.scss";
import "../assets/scss/github-markdown-light.scss";
import "../assets/scss/highlight.scss";
import "../assets/scss/MarkdownMessage.scss";
import "@/styles/markdown-mcp.scss";
import { markdownItBolt } from '@/plugins/markdown-it-bolt'
import { markdownItMcp } from '@/plugins/markdown-it-mcp'
import { markdownItStock } from '@/plugins/markdown-it-stock'
import { useChatContextStore } from '@/stores/chat-context'
import { usePidLogStore } from '@/stores/pid-log'

// Props
const props = defineProps<{
  message: {
    data: {
      text: string
      cmd?: { label: string }
      knowledgeBase?: { label: string }
    }
  }
  id: number
}>()

// Emits
const emit = defineEmits<{
  onClick2Conversion: [{ id: string }]
  pidAction: [{ pid: string, action: string }]
}>()

// Stores
const { clearMessageAfterId } = useChatContextStore()
const { existingPidLog } = usePidLogStore()

// Refs
const textRef = ref<HTMLDivElement>()
const textCtxRef = ref<HTMLDivElement>()

// Reactive data
const collapseTimer = ref<number>(0)

const mdi = new MarkdownIt({
  html: false,
  linkify: true,
  highlight: (code: string, language: string) => {
    const validLang = !!(language && hljs.getLanguage(language));
    if (validLang) {
      const lang = language ?? "";
      return highlightBlock(
        hljs.highlight(code, { language: lang }).value,
        lang
      );
    }
    return highlightBlock(hljs.highlightAuto(code).value, "");
  },
});

mdi.use(mila, { attrs: { target: "_blank", rel: "noopener" } });
mdi.use(mdKatex, {
  blockClass: "katexmath-block rounded-md p-[10px]",
  errorColor: " #cc0000",
});
mdi.renderer.rules.fence = (tokens, idx) => {
  const t = tokens[idx];
  const code = t.content.trim();
  if (!code) {
    return '';
  }
  const res = hljs.highlightAuto(code);
  return highlightBlock(res.value, res.language);
}
mdi.use(markdownItBolt)
mdi.use(markdownItStock)
mdi.use(markdownItMcp)

function highlightBlock(str: string, lang?: string) {
  const len = str.split("\n").length;
  const maxLen = 20;

  return `<pre class="code-block-wrapper">
    <div class="code-block-header">
      <span class="code-block-header__lang">${lang}</span>
      <span class="code-block-header__copy">复制代码</span>
    </div>
    <code class="hljs code-block-body ${lang} hide-code show-code">${str}</code>${
    len > maxLen
      ? `<div class="code-block-footer"><img src="${arrowUrl}" class="arrow-img" /></div>`
      : ""
  }</pre>`;
}

// Computed
const messageText = computed(() => {
  return mdi.render(props.message.data.text);
})

const cmd = computed(() => {
  if (props.message.data.cmd) {
    return props.message.data.cmd.label;
  }
  return "";
})

const knowledgeBase = computed(() => {
  if (props.message.data.knowledgeBase) {
    if (props.message.data.knowledgeBase.label.startsWith("@")) {
      return props.message.data.knowledgeBase.label.replaceAll(
        "@",
        `<span style="color: aqua;">@</span>`
      );
    }
    return `<span style="color: aqua;">${props.message.data.knowledgeBase.label.slice(
      0,
      1
    )}</span>${props.message.data.knowledgeBase.label.slice(1)}`;
  }
  return "";
})

// Watch
watch(existingPidLog, (oldExistingPidLog, newExistingPidLog) => {
  // console.log("messageText changed", oldExistingPidLog, newExistingPidLog);
  nextTick(() => {
    Object.keys(newExistingPidLog).forEach((pid) => {
      const doc = window.document.getElementById(`process-${pid}`)
      if (doc) {
        doc.innerHTML = existingPidLog[pid]?.slice(-100).join('\n');
        doc.scrollTop = doc.scrollHeight;
      }
    });
  });
})

// Methods
const bindCollapse = () => {
  clearTimeout(collapseTimer.value);
  collapseTimer.value = setTimeout(() => {
    if (textCtxRef.value instanceof HTMLElement) {
      const collapseBtn = textCtxRef.value.querySelectorAll(".code-block-footer");
      if (collapseBtn.length > 0) {
        collapseBtn.forEach((btn: any) => {
          const code = btn?.parentElement?.querySelector(".code-block-body");
          code?.classList.toggle("show-code");
          btn?.classList.toggle("show-all-code");
        })
      }
    }
  }, 1500);
}
// 展开
const collapseFn = () => {
  if (textRef.value) {
    const collapseBtn = textRef.value.querySelectorAll(".code-block-footer");
    collapseBtn.forEach((btn: any) => {
      btn.addEventListener("click", () => {
        const code = btn.parentElement.querySelector(".code-block-body");
        code.classList.toggle("show-code");
        btn.classList.toggle("show-all-code");
      });
    });
  }
}
const addCopyEvents = () => {
  if (textRef.value) {
    const copyBtn = textRef.value.querySelectorAll(".code-block-header__copy");
    copyBtn.forEach((btn: any) => {
      const copyHandler = btn._copyHandler || (async () => {
        const code = btn.parentElement?.nextElementSibling?.textContent;
        if (code) {
          await copyToClip(code);
          btn.textContent = "复制成功";
          setTimeout(() => {
            btn.textContent = "复制代码";
          }, 1000);
        }
      });

      btn.removeEventListener("click", btn._copyHandler);
      btn.addEventListener("click", copyHandler);
      btn._copyHandler = copyHandler;
    });
  }
}
const addApply = () => {
  // nextTick(() =>{
  //   if (props.message.data.isLast) {
  //     if (textRef.value) {
  //       const applyDivs = textRef.value.querySelectorAll(".code-block-header");
  //       applyDivs.forEach((applyDiv: any) => {
  //         const applyBtn = applyDiv.querySelectorAll(".code-block-header__apply");
  //         if (applyBtn.length === 0) {
  //           // Insert the Apply button
  //           // 创建一个新的span元素
  //           var spanElement = document.createElement("span");

  //           // 为span元素设置类名
  //           spanElement.className = "code-block-header__apply";

  //           // 设置span元素的内容
  //           spanElement.textContent = "Apply";

  //           // 将span元素追加到applyDiv中
  //           applyDiv.appendChild(spanElement);
  //         }
  //       });
  //     }
  //   } else {
  //     // Remove existing Apply buttons if isLast is false or undefined
  //     if (textRef.value) {
  //       const applyButtons = textRef.value.querySelectorAll(
  //         ".code-block-header__apply"
  //       );
  //       applyButtons.forEach((button) => button.remove());
  //     }
  //   }
  //   addApplyEvents();
  // })
}
const addApplyEvents = () => {
  if (textRef.value) {
    const applyBtn = textRef.value.querySelectorAll(".code-block-header__apply");
    applyBtn.forEach((btn: any) => {
      const applyHandler = btn._applyHandler || (() => {
        util.applyCode();
      });

      btn.removeEventListener("click", btn._applyHandler);
      btn.addEventListener("click", applyHandler);
      btn._applyHandler = applyHandler;
    });
  }
}
const addDiffEvents = () => {
  if (textRef.value) {
    const insertBtn = textRef.value.querySelectorAll(".code-block-header__diff");
    insertBtn.forEach((btn: any) => {
      btn.onclick = async () => {
        const code = btn.parentElement?.nextElementSibling?.textContent;
        if (code) {
          try {
            await util.diffCode(code);
            btn.textContent = "代码比较打开中";
          } catch (e) {
            btn.textContent = "代码比较";
          }
          setTimeout(() => {
            btn.textContent = "代码比较";
          }, 1000);
        }
      };
    });
  }
}
const addInsertEvents = () => {
  if (textRef.value) {
    const insertBtn = textRef.value.querySelectorAll(
      ".code-block-header__insert"
    );
    insertBtn.forEach((btn: any) => {
      const insertHandler = btn._insertHandler || (async () => {
        const code = btn.parentElement?.nextElementSibling?.textContent;
        if (code) {
          try {
            await util.insertCode(code);
            btn.textContent = "插入IDEA成功";
          } catch (e) {
            btn.textContent = "插入IDEA失败";
          }
          setTimeout(() => {
            btn.textContent = "插入IDEA";
          }, 1000);
        }
      });

      btn.removeEventListener("click", btn._insertHandler);
      btn.addEventListener("click", insertHandler);
      btn._insertHandler = insertHandler;
    });
  }
}
const addBoltToggleEvents = () => {
  if (textRef.value) {
    // 先移除旧的事件监听器
    const toggleBtns = textRef.value.querySelectorAll('.bolt-action-header-toggle');
    toggleBtns.forEach((btn: any) => {
      // 存储事件处理函数的引用
      const toggleHandler = btn._toggleHandler || (() => {
        console.log("toggleHandler");
        const content = btn.parentElement.querySelector('.bolt-action-content');
        content.classList.toggle('expanded');
        const toggleIcon = btn.querySelector('.bolt-action-toggle');
        toggleIcon.classList.toggle('expanded');
      });

      // 先移除旧的事件(如果存在)
      btn.removeEventListener('click', toggleHandler);
      // 添加新的事件
      btn.addEventListener('click', toggleHandler);

      // 将handler存储在DOM元素上,方便之后移除
      btn._toggleHandler = toggleHandler;
    });
  }
}

const addFileUrlLinkEvents = () => {
  if (textRef.value) {
    const fileLinks = textRef.value.querySelectorAll('.file-url-link');
    fileLinks.forEach((link: any) => {
      const clickHandler = link._clickHandler || ((e: Event) => {
        e.preventDefault();
        const url = link.getAttribute('data-url');
        if (url) {
          // 解析URL和参数
          const urlObj = new URL(url);
          const params = new URLSearchParams(urlObj.search);

          // 创建表单
          const form = document.createElement('form');
          form.method = 'GET';
          form.action = urlObj.origin + urlObj.pathname;
          form.target = '_blank';
          form.style.display = 'none';

          // 添加所有参数作为隐藏的input
          params.forEach((value, key) => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = key;
            input.value = value;
            form.appendChild(input);
          });

          // 添加到文档并提交
          document.body.appendChild(form);
          form.submit();

          // 清理DOM
          setTimeout(() => {
            document.body.removeChild(form);
          }, 100);
        }
      });

      link.removeEventListener('click', link._clickHandler);
      link.addEventListener('click', clickHandler);
      link._clickHandler = clickHandler;
    });
  }
}
const callPidAction = async (pid: string, action: string) => {
  // 通过事件向父组件发送PID动作请求
  emit('pidAction', { pid, action });
}
const addPidButtonEvents = () => {
  if (textRef.value) {
    // 处理杀死进程按钮
    const killButtons = textRef.value.querySelectorAll('.pid-kill-button');
    killButtons.forEach((button: Element) => {
      const htmlButton = button as HTMLButtonElement;
      const clickHandler = (htmlButton as any)._clickHandler || (async (e: Event) => {
        e.preventDefault();
        const pid = htmlButton.getAttribute('data-pid');
        if (pid) {
          try {
            // 添加加载状态
            htmlButton.classList.add('loading');
            htmlButton.disabled = true;

            // 调用杀死进程的API
            await callPidAction(pid, '/kill');

            // 成功状态
            htmlButton.classList.remove('loading');
            htmlButton.classList.add('success');

            setTimeout(() => {
              htmlButton.classList.remove('success');
            }, 2000);
          } catch (error) {
            console.error('杀死进程失败:', error);

            // 错误状态
            htmlButton.classList.remove('loading');
            htmlButton.classList.add('error');

            setTimeout(() => {
              htmlButton.classList.remove('error');
            }, 2000);
          } finally {
            htmlButton.disabled = false;
          }
        }
      });

      htmlButton.removeEventListener('click', (htmlButton as any)._clickHandler);
      htmlButton.addEventListener('click', clickHandler);
      (htmlButton as any)._clickHandler = clickHandler;
    });

    // 处理后台运行按钮
    const detachButtons = textRef.value.querySelectorAll('.pid-detach-button');
    detachButtons.forEach((button: Element) => {
      const htmlButton = button as HTMLButtonElement;
      const clickHandler = (htmlButton as any)._clickHandler || (async (e: Event) => {
        e.preventDefault();
        const pid = htmlButton.getAttribute('data-pid');
        if (pid) {
          try {
            // 添加加载状态
            htmlButton.classList.add('loading');
            htmlButton.disabled = true;

            // 调用后台运行的API
            await callPidAction(pid, '/detach');

            // 成功状态
            htmlButton.classList.remove('loading');
            htmlButton.classList.add('success');

            setTimeout(() => {
              htmlButton.classList.remove('success');
            }, 2000);
          } catch (error) {
            console.error('后台运行失败:', error);

            // 错误状态
            htmlButton.classList.remove('loading');
            htmlButton.classList.add('error');

            setTimeout(() => {
              htmlButton.classList.remove('error');
            }, 2000);
          } finally {
            htmlButton.disabled = false;
          }
        }
      });

      htmlButton.removeEventListener('click', (htmlButton as any)._clickHandler);
      htmlButton.addEventListener('click', clickHandler);
      (htmlButton as any)._clickHandler = clickHandler;
    });
  }
}

// const addTerminalAppendEvents = () => {
//   // if (textRef.value) {
//   //   // 为所有终端组件设置滚动到底部的行为
//   //   const terminalBlocks = textRef.value.querySelectorAll('.terminal-process-block');
//   //   terminalBlocks.forEach((block: Element) => {
//   //     const htmlBlock = block as HTMLElement;
//   //     const contentContainer = htmlBlock.querySelector('.terminal-process-content') as HTMLElement;

//   //     if (contentContainer) {
//   //       // 确保新内容显示时滚动到底部（滚动条在容器上）
//   //       // contentContainer.scrollTop = contentContainer.scrollHeight;
//   //     }
//   //   });
//   // }
// }

const removeEvents = () => {
  if (textRef.value) {
    // 移除copy事件
    const copyBtn = textRef.value.querySelectorAll(".code-block-header__copy");
    copyBtn.forEach((btn: any) => {
      if (btn._copyHandler) {
        btn.removeEventListener("click", btn._copyHandler);
        delete btn._copyHandler;
      }
    });

    // 移除apply事件
    const applyBtn = textRef.value.querySelectorAll(".code-block-header__apply");
    applyBtn.forEach((btn: any) => {
      if (btn._applyHandler) {
        btn.removeEventListener("click", btn._applyHandler);
        delete btn._applyHandler;
      }
    });

    // 移除insert事件
    const insertBtn = textRef.value.querySelectorAll(".code-block-header__insert");
    insertBtn.forEach((btn: any) => {
      if (btn._insertHandler) {
        btn.removeEventListener("click", btn._insertHandler);
        delete btn._insertHandler;
      }
    });

    // 删除apply绑定事件
    const applyButtons = textRef.value.querySelectorAll(
      ".code-block-header__apply"
    );
    applyButtons.forEach((button) => button.remove());
    const diffBtn = textRef.value.querySelectorAll(".code-block-header__diff");
    diffBtn.forEach((btn: any) => {
      btn.onclick = null;
    });
    // 正确移除bolt toggle事件
    const toggleBtns = textRef.value.querySelectorAll('.bolt-action-header-toggle');
    toggleBtns.forEach((btn: any) => {
      if (btn._toggleHandler) {
        btn.removeEventListener('click', btn._toggleHandler);
        delete btn._toggleHandler;
      }
    });

    // 移除file-url-link事件
    const fileLinks = textRef.value.querySelectorAll('.file-url-link');
    fileLinks.forEach((link: any) => {
      if (link._clickHandler) {
        link.removeEventListener('click', link._clickHandler);
        delete link._clickHandler;
      }
    });

    // 移除pid按钮事件
    const killButtons = textRef.value.querySelectorAll('.pid-kill-button');
    killButtons.forEach((button: any) => {
      if (button._clickHandler) {
        button.removeEventListener('click', button._clickHandler);
        delete button._clickHandler;
      }
    });

    const detachButtons = textRef.value.querySelectorAll('.pid-detach-button');
    detachButtons.forEach((button: any) => {
      if (button._clickHandler) {
        button.removeEventListener('click', button._clickHandler);
        delete button._clickHandler;
      }
    });

    // 移除hive-checkpoint-btn事件
    const checkpointButtons = textRef.value.querySelectorAll('.hive-checkpoint-btn');
    checkpointButtons.forEach((button: any) => {
      if (button._clickHandler) {
        button.removeEventListener('click', button._clickHandler);
        delete button._clickHandler;
      }
    });


  }
}

const removeColapseEvents = () => {
  if (textRef.value) {
    const copyBtn = textRef.value.querySelectorAll(".code-block-footer");
    copyBtn.forEach((btn: any) => {
      btn.removeEventListener("click", () => {});
    });
    const insertBtn = textRef.value.querySelectorAll(
      ".code-block-header__insert"
    );
    insertBtn.forEach((btn: any) => {
      btn.onclick = null;
    });
  }
}

// Lifecycle hooks
onMounted(() => {
  addApply();
  addCopyEvents();
  addInsertEvents();
  addDiffEvents();
  collapseFn();
  addBoltToggleEvents();
  addFileUrlLinkEvents();
  addPidButtonEvents();
  // addTerminalAppendEvents();
  if (textRef.value) {
    textRef.value.querySelectorAll('.hive-btn').forEach((ele) => {
      ele?.removeEventListener('click', () => {
        const id = ele?.id
        clearMessageAfterId(id)
        emit('onClick2Conversion', { id })
      })
      ele?.addEventListener('click', () => {
        const id = ele?.id
        clearMessageAfterId(id)
        emit('onClick2Conversion', { id })
      })
    })
      textRef.value.querySelectorAll('.hive-checkpoint-btn').forEach((ele) => {
        ele?.removeEventListener('click', () => {
          const id = ele?.getAttribute('data-msg-id')
          clearMessageAfterId(id)
          emit('onClick2Conversion', { id })
        })
        ele?.addEventListener('click', () => {
          const id = ele?.getAttribute('data-msg-id')
          clearMessageAfterId(id)
          emit('onClick2Conversion', { id })
        })
      })
  }
})

onUpdated(() => {
  addApply();
  addCopyEvents();
  addInsertEvents();
  addDiffEvents();
  collapseFn();
  bindCollapse();
  addBoltToggleEvents();
  addFileUrlLinkEvents();
  addPidButtonEvents();
  // addTerminalAppendEvents();
  if (textRef.value) {
    textRef.value.querySelectorAll('.hive-btn').forEach((ele) => {
      ele?.removeEventListener('click', () => {
        const id = ele?.id
        clearMessageAfterId(id)
        emit('onClick2Conversion', { id })
      })
      ele?.addEventListener('click', () => {
        const id = ele?.id
        clearMessageAfterId(id)
        emit('onClick2Conversion', { id })
      })
    })
      textRef.value.querySelectorAll('.hive-checkpoint-btn').forEach((ele) => {
        ele?.removeEventListener('click', () => {
          const id = ele?.getAttribute('data-msg-id')
          clearMessageAfterId(id)
          emit('onClick2Conversion', { id })
        })
        ele?.addEventListener('click', () => {
          const id = ele?.getAttribute('data-msg-id')
          clearMessageAfterId(id)
          emit('onClick2Conversion', { id })
        })
      })
  }
})

onUnmounted(() => {
  removeEvents();
  removeColapseEvents();
  clearTimeout(collapseTimer.value);
})
</script>

<style scoped lang="scss">
.sc-message--text {
  // padding: 5px 20px;
  // border-radius: 6px;
  font-weight: 300;
  font-size: 14px;
  line-height: 1.4;
  position: relative;
  -webkit-font-smoothing: subpixel-antialiased;

  .sc-message--text-content {
    white-space: pre-wrap;
    line-height: 2;
  }

  &:hover .sc-message--toolbox {
    left: -20px;
    opacity: 1;
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

    & :deep(svg) {
      margin-left: 5px;
    }
  }

  code {
    font-family: 'Courier New', Courier, monospace !important;
  }
}

.sc-message--content.sent .sc-message--text {
  color: white;
  background-color: #4e8cff;
  max-width: calc(100% - 120px);
  word-wrap: break-word;
}

.sc-message--content.received .sc-message--text {
  color: #263238;
  background-color: #f4f7f9;
  margin-right: 40px;
}

a.chatLink {
  color: inherit !important;
}
</style>
