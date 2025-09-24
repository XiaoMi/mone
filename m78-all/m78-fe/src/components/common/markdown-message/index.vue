<template>
  <div class="sc-message--text">
    <slot :messageText="messageText()">
      <div class="sc-message--text-content">
        <div ref="textRef" class="markdown-body" v-html="messageText()"></div>
      </div>
    </slot>
  </div>
</template>

<script lang="ts" setup>
import { defineProps } from "vue";
import mdKatex from "@traptitech/markdown-it-katex";
import mila from "markdown-it-link-attributes";
import hljs from "highlight.js";
import MarkdownIt from "markdown-it";

import "./assets/scss/github-markdown.scss";
import "./assets/scss/highlight.scss";
import "./assets/scss/MarkdownMessage.scss";

/**  传递过来的props和emit */
const props = defineProps({
  message: {
    type: String,
    default: "",
  },
});

/**  init */
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

function highlightBlock(str: string, lang?: string) {
  return `<pre class="code-block-wrapper"><div class="code-block-header"><span class="code-block-header__lang">${lang}</span></div><code class="hljs code-block-body ${lang}">${str}</code></pre>`;
}

const messageText = () => {
  return mdi.render(props.message);
};
</script>

<style scoped lang="scss">
.sc-message--text {
  padding: 5px 20px;
  border-radius: 6px;
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
