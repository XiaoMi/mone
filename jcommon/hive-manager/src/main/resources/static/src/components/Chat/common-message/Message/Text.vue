<script lang="ts" setup>
import {
  computed,
  onMounted,
  onUnmounted,
  onUpdated,
  ref,
  reactive,
} from "vue";
// @ts-ignore
import MarkdownIt from "markdown-it";
import mdKatex from "@traptitech/markdown-it-katex";
// @ts-ignore
import mila from "markdown-it-link-attributes";
import hljs from "highlight.js";
import copyToClip from "@/common/customCopy";
import Loading from "./Loading.vue";
// import { ElMessage } from "element-plus";
// import i18n from '@/lang'
import ChartComponent from "./Chart.vue";
import TableComponent from "./TableData.vue";
import BaseSounds from "@/components/BaseSounds.vue";

// const t = i18n.t

interface Props {
  inversion?: boolean;
  error?: boolean;
  text?: string;
  loading?: boolean;
  asRawText?: boolean;
  showCursor?: boolean;
  textType?: string; // 文本类型如果是translate怎展示 描述和翻译结果
  translateData?: {};
  showOperate?: boolean; // 是否是插件调试页面
  voice: string; // 语音播报配置
}

const props = defineProps<Props>();

const textRef = ref<HTMLElement>();

const soundRef = ref<HTMLElement>();

const resContent = ref<HTMLElement>();

const pos = reactive({ x: 0, y: 0 });

const mdi = new MarkdownIt({
  html: false,
  linkify: true,
  highlight(code, language) {
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

const wrapClass = computed(() => {
  return [
    "text-wrap",
    "min-w-[20px]",
    "rounded-md",
    "px-3 py-2",
    props.inversion ? "color-[#fff]" : "dark:color-[#fff]",
    props.inversion ? "bg-[var(--oz-color-primary)]" : "bg-[#f4f6f8]",
    props.inversion ? "dark:bg-[#80d4ff]" : "dark:bg-[#1e1e20]",
    props.inversion ? "message-request" : "message-reply",
    { "text-red-500": props.error },
  ];
});

const text = computed(() => {
  const value = props.text ?? "";
  if (!props.asRawText) return mdi.render(value);
  return value;
});

function highlightBlock(str: string, lang?: string) {
  return `<pre class="code-block-wrapper"><div class="code-block-header"><span class="code-block-header__lang">${lang}</span><span class="code-block-header__copy">复制</span></div><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
}

function addCopyEvents() {
  if (textRef.value) {
    const copyBtn = textRef.value.querySelectorAll(".code-block-header__copy");
    copyBtn.forEach((btn) => {
      btn.addEventListener("click", () => {
        const code = btn.parentElement?.nextElementSibling?.textContent;
        if (code) {
          copyToClip(code).then(() => {
            btn.textContent = "复制成功";
            setTimeout(() => {
              btn.textContent = "复制";
            }, 1000);
          });
        }
      });
    });
  }
}

function removeCopyEvents() {
  if (textRef.value) {
    const copyBtn = textRef.value.querySelectorAll(".code-block-header__copy");
    copyBtn.forEach((btn) => {
      btn.removeEventListener("click", () => { });
    });
  }
}

const newTextDom = ref();
const emits = defineEmits(["replaceFn", "onTryAgain"]);
// 复制到剪切板
function clickToClip() {
  copyToClip(props?.translateData?.toNewText || "");
}
function copyText(text) {
  copyToClip(text);
}
function replaceFn() {
  emits("replaceFn", props?.translateData?.toNewText || "");
}

function getLastTextNode(dom: HTMLElement | ChildNode) {
  const childNodes = dom.childNodes;
  for (let i = childNodes.length - 1; i >= 0; i--) {
    const node = childNodes[i];
    if (node.nodeType === Node.TEXT_NODE && /\S/.test(node.nodeValue)) {
      node.nodeValue = node.nodeValue?.replace(/\s+$/, "");
      return node;
    } else if (node.nodeType === Node.ELEMENT_NODE) {
      const lastTextNode = getLastTextNode(node);
      if (lastTextNode) {
        return lastTextNode;
      }
    }
  }
  return null;
}

function updateCursor() {
  const dom = resContent.value;
  if (dom && dom.childNodes[1]) {
    const contentDom = dom.childNodes[1] as HTMLElement;
    const lastTextNode = getLastTextNode(contentDom);
    // console.log(lastTextNode)
    const textNode = document.createTextNode("\u200b");
    if (lastTextNode) {
      lastTextNode.parentElement!.appendChild(textNode);
    } else {
      contentDom.appendChild(textNode);
    }
    const range = document.createRange();
    range.setStart(textNode, 0);
    range.setEnd(textNode, 0);
    const domRect = contentDom.getBoundingClientRect();
    const rect = range.getBoundingClientRect();
    pos.x = rect.left - domRect.left;
    pos.y = rect.top - domRect.top;
    textNode.remove();
  }
}

function tryAgainFn() {
  soundRef.value?.stopRead?.();
  emits("onTryAgain");
}

onMounted(() => {
  updateCursor();
  addCopyEvents();
});

onUpdated(() => {
  updateCursor();
  addCopyEvents();
});

onUnmounted(() => {
  removeCopyEvents();
});
</script>

<template>
  <div :class="wrapClass">
    <div ref="textRef" class="leading-relaxed break-words">
      <template v-if="loading">
        <Loading />
      </template>
      <template v-else>
        <div v-if="props.textType == 'translate'">
          <p class="text-t">{{ translateData?.aiDetail }}</p>
          <div class="text-detail" ref="newTextDom">
            <span class="new-text">
              {{ translateData?.toNewText }}
            </span>
            <div class="btn-box">
              <span class="copy-btn" @click="replaceFn"> 替换 </span>
              <span class="copy-btn" @click="clickToClip"> 拷贝 </span>
            </div>
          </div>
        </div>
        <template v-if="props.textType == 'img'">
          <div style="max-width: 200px; max-height: 200px">
            <el-image :src="props.text" fit="contain" />
          </div>
        </template>
        <ChartComponent v-if="props.textType?.includes('chart')" :chartType="props.textType" :data="props.text">
        </ChartComponent>
        <TableComponent v-if="props.textType == 'table'" :data="props.text"></TableComponent>
        <template v-else>
          <div v-if="!inversion" ref="resContent">
            <div v-if="showCursor" class="show-cursor"></div>
            <div v-if="!asRawText" class="markdown-body" v-html="text" />
            <div v-else class="whitespace-pre-wrap" v-text="text" />
          </div>
          <div v-else class="whitespace-pre-wrap" v-text="text" />
          <div v-if="props.showOperate && props.asRawText && !inversion" class="debug-operate">
            <el-tooltip content="复制">
              <el-button class="sounds-icon" @click="copyText(text)" text>
                <el-icon :size="16">
                  <CopyDocument />
                </el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="再试一次">
              <el-button class="sounds-icon" @click="tryAgainFn" text>
                <el-icon :size="16">
                  <RefreshRight />
                </el-icon>
              </el-button>
            </el-tooltip>
            <BaseSounds ref="soundRef" :content="text" :voice="props.voice" />
          </div>
        </template>
      </template>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@keyframes blink {
  0% {
    opacity: 1;
  }

  50% {
    opacity: 0;
  }

  100% {
    opacity: 1;
  }
}

.show-cursor {
  position: relative;

  &::after {
    content: "";
    width: 8px;
    height: 1.5em;
    position: absolute;
    background: #d5d9da;
    animation: blink 0.6s infinite;
    left: calc(v-bind("pos.x") * 1px);
    top: calc(v-bind("pos.y") * 1px);
  }
}

.debug-operate {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  color: #626060;

  &:deep(button) {
    margin-left: 0;
    padding-bottom: 0;
  }
}
</style>

<style lang="scss">
@import url(./style.scss);

.text-detail {
  margin: 5px 0;
  background: #e5e5e5;
  padding: 20px 10px 10px 10px;
  position: relative;
  border-radius: 0.375rem;

  .btn-box {
    position: absolute;
    right: 4px;
    top: 2px;
    font-size: 12px;
  }

  .copy-btn {
    cursor: pointer;
    color: #00a9ff;
  }
}
</style>
