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

<script lang="ts">
import mdKatex from "@traptitech/markdown-it-katex";
// @ts-ignore
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
import { nextTick } from "vue";
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

export default {
  props: {
    message: {
      type: Object,
      required: true,
    },
    id: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      collapseTimer: 0,
    };
  },
  computed: {
    messageText() {
      return mdi.render(this.message.data.text);
    },
    cmd() {
      if (this.message.data.cmd) {
        return this.message.data.cmd.label;
      }
      return "";
    },
    knowledgeBase() {
      if (this.message.data.knowledgeBase) {
        if (this.message.data.knowledgeBase.label.startsWith("@")) {
          return this.message.data.knowledgeBase.label.replaceAll(
            "@",
            `<span style="color: aqua;">@</span>`
          );
        }
        return `<span style="color: aqua;">${this.message.data.knowledgeBase.label.slice(
          0,
          1
        )}</span>${this.message.data.knowledgeBase.label.slice(1)}`;
      }
      return "";
    },
  },
  mounted() {
    this.addApply();
    this.addCopyEvents();
    this.addInsertEvents();
    this.addDiffEvents();
    this.collapseFn();
    this.addBoltToggleEvents();
    this.addToolResultToggleEvents();
    this.addFileUrlLinkEvents();
    this.addPidButtonEvents();
    this.addTerminalAppendEvents();
  },
  updated() {
    this.addApply();
    this.addCopyEvents();
    this.addInsertEvents();
    this.addDiffEvents();
    this.collapseFn();
    this.bindCollapse();
    this.addBoltToggleEvents();
    this.addToolResultToggleEvents();
    this.addFileUrlLinkEvents();
    this.addPidButtonEvents();
    this.addTerminalAppendEvents();
  },
  unmounted() {
    this.removeEvents();
    this.removeColapseEvents();
    clearTimeout(this.collapseTimer);
  },
  methods: {
    bindCollapse() {
      const currentTime = Date.now();
      clearTimeout(this.collapseTimer);
      this.collapseTimer = setTimeout(() => {
        const textRef = this.$refs.textCtxRef;
        if (textRef instanceof HTMLElement) {
          const collapseBtn = textRef.querySelectorAll(".code-block-footer");
          if (collapseBtn.length > 0) {
            collapseBtn.forEach((btn: any) => {
              const code = btn?.parentElement?.querySelector(".code-block-body");
              code?.classList.toggle("show-code");
              btn?.classList.toggle("show-all-code");
            })
          }
        }
      }, 1500);
    },
    // 展开
    collapseFn() {
      const textRef = this.$refs.textRef as HTMLDivElement;
      if (textRef) {
        const collapseBtn = textRef.querySelectorAll(".code-block-footer");
        collapseBtn.forEach((btn: any) => {
          btn.addEventListener("click", () => {
            const code = btn.parentElement.querySelector(".code-block-body");
            code.classList.toggle("show-code");
            const footer = btn.parentElement.nextElementSibling;
            btn.classList.toggle("show-all-code");
          });
        });
      }
    },
    addCopyEvents() {
      const textRef = this.$refs.textRef as HTMLDivElement;
      if (textRef) {
        const copyBtn = textRef.querySelectorAll(".code-block-header__copy");
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
    },
    addApply() {
      // this.$nextTick(() =>{
      //   if (this.message.data.isLast) {
      //     const textRef = this.$refs.textRef as HTMLDivElement;
      //     if (textRef) {
      //       const applyDivs = textRef.querySelectorAll(".code-block-header");
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
      //     const textRef = this.$refs.textRef as HTMLDivElement;
      //     if (textRef) {
      //       const applyButtons = textRef.querySelectorAll(
      //         ".code-block-header__apply"
      //       );
      //       applyButtons.forEach((button) => button.remove());
      //     }
      //   }
      //   this.addApplyEvents();
      // })
    },
    addApplyEvents() {
      const textRef = this.$refs.textRef as HTMLDivElement;
      if (textRef) {
        const applyBtn = textRef.querySelectorAll(".code-block-header__apply");
        applyBtn.forEach((btn: any) => {
          const applyHandler = btn._applyHandler || (() => {
            util.applyCode();
          });

          btn.removeEventListener("click", btn._applyHandler);
          btn.addEventListener("click", applyHandler);
          btn._applyHandler = applyHandler;
        });
      }
    },
    addDiffEvents() {
      const textRef = this.$refs.textRef as HTMLDivElement;
      if (textRef) {
        const insertBtn = textRef.querySelectorAll(".code-block-header__diff");
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
    },
    addInsertEvents() {
      const textRef = this.$refs.textRef as HTMLDivElement;
      if (textRef) {
        const insertBtn = textRef.querySelectorAll(
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
    },
    addBoltToggleEvents() {
      const textRef = this.$refs.textRef as HTMLDivElement;
      if (textRef) {
        // 先移除旧的事件监听器
        const toggleBtns = textRef.querySelectorAll('.bolt-action-header-toggle');
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
    },
    addToolResultToggleEvents() {
      const textRef = this.$refs.textRef as HTMLDivElement;
      if (textRef) {
        const toolResultHeaders = textRef.querySelectorAll('.tool-result-header');
        toolResultHeaders.forEach((header: any) => {
          const toggleHandler = header._toggleHandler || (() => {
            const content = header.parentElement.querySelector('.tool-result-content');
            const toggleIcon = header.querySelector('.toggle-icon');

            if (content.classList.contains('is-active')) {
              content.classList.remove('is-active');
              toggleIcon.classList.remove('expanded');
            } else {
              content.classList.add('is-active');
              toggleIcon.classList.add('expanded');
            }
          });

          // 先移除旧的事件(如果存在)
          header.removeEventListener('click', header._toggleHandler);
          // 添加新的事件
          header.addEventListener('click', toggleHandler);

          // 将handler存储在DOM元素上,方便之后移除
          header._toggleHandler = toggleHandler;
        });
      }
    },
    addFileUrlLinkEvents() {
      const textRef = this.$refs.textRef as HTMLDivElement;
      if (textRef) {
        const fileLinks = textRef.querySelectorAll('.file-url-link');
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
    },
    async callPidAction(pid: string, action: string) {
      // 通过事件向父组件发送PID动作请求
      this.$emit('pidAction', { pid, action });
    },
    addPidButtonEvents() {
      const textRef = this.$refs.textRef as HTMLDivElement;
      if (textRef) {
        // 处理杀死进程按钮
        const killButtons = textRef.querySelectorAll('.pid-kill-button');
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
                await this.callPidAction(pid, '/kill');

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
        const detachButtons = textRef.querySelectorAll('.pid-detach-button');
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
                await this.callPidAction(pid, '/detach');

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
    },
    addTerminalAppendEvents() {
      const textRef = this.$refs.textRef as HTMLDivElement;
      if (textRef) {
        // 处理终端追加更新指令（只更新计数，不更新DOM内容）
        const updateElements = textRef.querySelectorAll('.terminal-append-update');
        updateElements.forEach((updateEl: Element) => {
          const htmlUpdate = updateEl as HTMLElement;
          const pid = htmlUpdate.getAttribute('data-pid');
          const newLines = htmlUpdate.getAttribute('data-new-lines');

          if (pid && newLines) {
            // 查找对应的终端组件，更新计数显示
            const targetTerminal = textRef.querySelector(`.terminal-process-block[data-pid="${pid}"]`) as HTMLElement;
            if (targetTerminal) {
              const headerSpan = targetTerminal.querySelector('.terminal-process-header span') as HTMLElement;
              if (headerSpan && (window as any).updateTerminalLineCount) {
                const totalLines = (window as any).updateTerminalLineCount(pid);
                headerSpan.textContent = `进程 ${pid} (${totalLines}条日志)`;

                // 如果当前终端内容是可见的，重新加载内容
                const contentContainer = targetTerminal.querySelector('.terminal-process-content') as HTMLElement;
                if (contentContainer && contentContainer.style.display !== 'none') {
                  const lines = (window as any).getTerminalContent(pid);
                  const preElement = contentContainer.querySelector('pre');
                  if (preElement && lines.length > 0) {
                    preElement.textContent = lines.join('\n');
                    contentContainer.scrollTop = contentContainer.scrollHeight;
                  }
                }
              }

              // 如果当前终端内容是可见的，重新加载内容
              const contentContainer = targetTerminal.querySelector('.terminal-process-content') as HTMLElement;
              if (contentContainer && contentContainer.style.display !== 'none') {
                // 只有在内容可见时才重新加载并滚动
                const pid = targetTerminal.getAttribute('data-pid');
                if (pid && (window as any).getTerminalContent) {
                  const lines = (window as any).getTerminalContent(pid);
                  const preElement = contentContainer.querySelector('pre');
                  if (preElement && lines.length > 0) {
                    preElement.textContent = lines.join('\n');
                    contentContainer.scrollTop = contentContainer.scrollHeight;
                  }
                }
              }
            }

            // 移除处理过的更新指令
            htmlUpdate.remove();
          }
        });

        // 为所有终端组件设置滚动到底部的行为和切换按钮事件
        const terminalBlocks = textRef.querySelectorAll('.terminal-process-block');
        terminalBlocks.forEach((block: Element) => {
          const htmlBlock = block as HTMLElement;
          const contentContainer = htmlBlock.querySelector('.terminal-process-content') as HTMLElement;
          const toggleBtn = htmlBlock.querySelector('.terminal-toggle-btn') as HTMLElement;

          if (contentContainer) {
            // 确保新内容显示时滚动到底部（滚动条在容器上）
            contentContainer.scrollTop = contentContainer.scrollHeight;
          }

          // 添加切换按钮事件（点击时动态加载内容）
          if (toggleBtn && !(toggleBtn as any)._toggleHandler) {
            const toggleHandler = () => {
              const content = htmlBlock.querySelector('.terminal-process-content') as HTMLElement;
              const icon = toggleBtn.querySelector('i');
              const text = toggleBtn.childNodes[toggleBtn.childNodes.length - 1];
              const pid = htmlBlock.getAttribute('data-pid');

              if (content.style.display === 'none') {
                // 显示内容时，从内存中加载实际内容
                if (pid && (window as any).getTerminalContent) {
                  const lines = (window as any).getTerminalContent(pid);
                  let preElement = content.querySelector('pre');

                  // 如果没有pre元素，创建一个
                  if (!preElement) {
                    preElement = document.createElement('pre');
                    content.innerHTML = ''; // 清空占位内容
                    content.appendChild(preElement);
                  }

                  if (lines.length > 0) {
                    preElement.textContent = lines.join('\n');
                    console.log('Loading content for PID:', pid, 'lines:', lines.length);
                  } else {
                    preElement.textContent = '暂无日志内容';
                    console.log('No content found for PID:', pid);
                  }
                }

                content.style.display = 'block';
                if (icon) icon.className = 'fa-solid fa-eye-slash';
                if (text) text.textContent = ' 隐藏日志';
                // 显示时滚动到底部
                setTimeout(() => {
                  content.scrollTop = content.scrollHeight;
                }, 50);
              } else {
                content.style.display = 'none';
                if (icon) icon.className = 'fa-solid fa-eye';
                if (text) text.textContent = ' 查看日志';
                // 隐藏时清空内容节省内存
                const preElement = content.querySelector('pre');
                if (preElement) {
                  preElement.textContent = '';
                }
              }
            };

            toggleBtn.addEventListener('click', () => {
              nextTick(() => {
                toggleHandler();
              });
            });
            (toggleBtn as any)._toggleHandler = toggleHandler;
          }
        });
      }
    },
    removeEvents() {
      const textRef = this.$refs.textRef as HTMLDivElement;
      if (textRef) {
        // 移除copy事件
        const copyBtn = textRef.querySelectorAll(".code-block-header__copy");
        copyBtn.forEach((btn: any) => {
          if (btn._copyHandler) {
            btn.removeEventListener("click", btn._copyHandler);
            delete btn._copyHandler;
          }
        });

        // 移除apply事件
        const applyBtn = textRef.querySelectorAll(".code-block-header__apply");
        applyBtn.forEach((btn: any) => {
          if (btn._applyHandler) {
            btn.removeEventListener("click", btn._applyHandler);
            delete btn._applyHandler;
          }
        });

        // 移除insert事件
        const insertBtn = textRef.querySelectorAll(".code-block-header__insert");
        insertBtn.forEach((btn: any) => {
          if (btn._insertHandler) {
            btn.removeEventListener("click", btn._insertHandler);
            delete btn._insertHandler;
          }
        });

        // 删除apply绑定事件
        const applyButtons = textRef.querySelectorAll(
          ".code-block-header__apply"
        );
        applyButtons.forEach((button) => button.remove());
        const diffBtn = textRef.querySelectorAll(".code-block-header__diff");
        diffBtn.forEach((btn: any) => {
          btn.onclick = null;
        });
        // 正确移除bolt toggle事件
        const toggleBtns = textRef.querySelectorAll('.bolt-action-header-toggle');
        toggleBtns.forEach((btn: any) => {
          if (btn._toggleHandler) {
            btn.removeEventListener('click', btn._toggleHandler);
            delete btn._toggleHandler;
          }
        });
        // 移除tool-result toggle事件
        const toolResultHeaders = textRef.querySelectorAll('.tool-result-header');
        toolResultHeaders.forEach((header: any) => {
          if (header._toggleHandler) {
            header.removeEventListener('click', header._toggleHandler);
            delete header._toggleHandler;
          }
        });

        // 移除file-url-link事件
        const fileLinks = textRef.querySelectorAll('.file-url-link');
        fileLinks.forEach((link: any) => {
          if (link._clickHandler) {
            link.removeEventListener('click', link._clickHandler);
            delete link._clickHandler;
          }
        });

        // 移除pid按钮事件
        const killButtons = textRef.querySelectorAll('.pid-kill-button');
        killButtons.forEach((button: any) => {
          if (button._clickHandler) {
            button.removeEventListener('click', button._clickHandler);
            delete button._clickHandler;
          }
        });

        const detachButtons = textRef.querySelectorAll('.pid-detach-button');
        detachButtons.forEach((button: any) => {
          if (button._clickHandler) {
            button.removeEventListener('click', button._clickHandler);
            delete button._clickHandler;
          }
        });

        // 移除terminal toggle按钮事件
        const terminalToggleBtns = textRef.querySelectorAll('.terminal-toggle-btn');
        terminalToggleBtns.forEach((btn: Element) => {
          const button = btn as HTMLElement;
          if ((button as any)._toggleHandler) {
            button.removeEventListener('click', (button as any)._toggleHandler);
            delete (button as any)._toggleHandler;
          }
        });
      }
    },
    removeColapseEvents() {
      const textRef = this.$refs.textRef as HTMLDivElement;
      if (textRef) {
        const copyBtn = textRef.querySelectorAll(".code-block-footer");
        copyBtn.forEach((btn: any) => {
          btn.removeEventListener("click", () => {});
        });
        const insertBtn = textRef.querySelectorAll(
          ".code-block-header__insert"
        );
        insertBtn.forEach((btn: any) => {
          btn.onclick = null;
        });
      }
    },
  },
};
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
    font-family: "Courier New", Courier, monospace !important;
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
