<template>
  <div class="sc-user-input--text-wrapper">
    <div class="sc-user-input--text-cmd">
      <span v-for="item of cmd">{{ item }}</span>
    </div>
    <div
      ref="userInputRef"
      tabIndex="0"
      contentEditable="true"
      :placeholder="placeholder"
      class="sc-user-input--text"
      @focus="setInputActive(true)"
      @blur="setInputActive(false)"
      @input="handleInput"
      @paste="handlePaste"
    ></div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, nextTick, watch } from 'vue';

const props = defineProps({
  modelValue: {
    type: String,
    required: true,
  },
  cmd: {
    type: [String, Array<String>],
    required: true,
  },
  setInputActive: {
    type: Function,
    required: true,
  },
  placeholder: {
    type: String,
    default: "Shift + Enter = 发送",
  },
  initCodePrompt: {
    type: Function,
    required: true,
  },
});

const emit = defineEmits(['update:modelValue']);

const userInputRef = ref<HTMLDivElement | null>(null);

// // 监听 modelValue 变化，同步到输入框
// watch(() => props.modelValue, (newValue) => {
//   if (userInputRef.value && userInputRef.value.textContent !== newValue) {
//     userInputRef.value.textContent = newValue;
//     // 将光标移到末尾
//     nextTick(() => {
//       const range = document.createRange();
//       const selection = window.getSelection();
//       if (userInputRef.value && userInputRef.value.childNodes.length > 0) {
//         range.selectNodeContents(userInputRef.value);
//         range.collapse(false);
//         selection?.removeAllRanges();
//         selection?.addRange(range);
//       }
//     });
//   }
// });

onMounted(() => {
  init();
  // @ts-ignore
  // window.setUserCode = function (code: string) {
  //   updateModelValue(window.decodeURIComponent(code));
  //   focusUserInput();
  //   props.setInputActive(true);
  // };
});

onBeforeUnmount(() => {
  // @ts-ignore
  window.setUserCode = null;
});

function init() {
  if (userInputRef.value) {
    userInputRef.value.textContent = props.modelValue;
  }
}

function getFormattedContent(element: HTMLElement) {
  // 用临时元素来处理
  const temp = document.createElement('div');
  temp.innerHTML = element.innerHTML;
  
  // 将<br>转换为换行符
  temp.querySelectorAll('br').forEach(br => {
    br.replaceWith('\n');
  });
  
  // 将div/p转换为换行符
  temp.querySelectorAll('div, p').forEach(el => {
    el.replaceWith('\n' + el.textContent);
  });
  
  // 处理空格（&nbsp;）
  return temp.textContent?.replace(/\u00A0/g, ' ');
}

function handleInput(event: InputEvent) {
  
  // const content = (event.target as HTMLElement).textContent || "";
  // updateModelValue(content);
  const content = getFormattedContent(event.target as HTMLElement) || "";
  updateModelValue(content);
  
  // 检查内容是否为空，如果为空，确保div仍然保持为空
  if (!content && userInputRef.value) {
    userInputRef.value.textContent = '';
  }
}

function updateModelValue(text: string) {
  emit("update:modelValue", text);
}

function focusUserInput() {
  nextTick(() => {
    userInputRef.value?.focus();
  });
}

function cleanTextContent() {
  if (userInputRef.value) {
    userInputRef.value.textContent = '';
  }
}

function setTextContent(text: string) {
  if (userInputRef.value) {
    userInputRef.value.textContent = text;
    updateModelValue(text);
    // 将光标移到末尾
    nextTick(() => {
      const range = document.createRange();
      const selection = window.getSelection();
      if (userInputRef.value && userInputRef.value.childNodes.length > 0) {
        range.selectNodeContents(userInputRef.value);
        range.collapse(false);
        selection?.removeAllRanges();
        selection?.addRange(range);
      }
    });
  }
}

function handlePaste(event: ClipboardEvent) {
  // 阻止默认粘贴行为
  event.preventDefault();
  
  // 获取剪贴板中的纯文本
  const text = event.clipboardData?.getData('text/plain') || '';
  
  // 将纯文本插入到当前光标位置
  document.execCommand('insertText', false, text);
}

defineExpose({
  cleanTextContent,
  focus: focusUserInput,
  setTextContent,
})
</script>

<style lang="scss">
.sc-user-input--text-wrapper {
  display: flex;
  padding: 18px;
  align-items: center;

  .sc-user-input--text-cmd {
    margin-right: 6px;
    flex-shrink: 0;
    color: aqua;
  }
}
</style>

<style scoped lang="scss">
.autocomplete-item {
  min-width: 260px;
  padding: 10px;
  color: #fff;

  cursor: pointer;

  &:hover {
    background-color: rgb(38, 38, 38);
  }
}

.highlighted {
  background-color: rgb(38, 38, 38);
}

.sc-user-input-wrapper {
  flex-shrink: 0;
  flex-grow: 0;
}

.sc-user-input {
  flex: 1;
  min-height: 55px;
  margin: 0px;
  position: relative;
  bottom: 0;
  display: flex;
  background-color: rgb(38, 38, 38);
  border-bottom-left-radius: 10px;
  border-bottom-right-radius: 10px;
  transition: background-color 0.2s ease, box-shadow 0.2s ease;
  align-items: center;
}

.sc-user-input--hbuttons {
  display: flex;
  align-items: center;
}

.sc-user-input--hbutton {
  display: flex;
  justify-items: center;
  align-items: center;

  margin-left: 5px;
  padding: 5px;

  width: 30px;
  height: 30px;

  cursor: pointer;

  &:hover {
    background-color: #565867;
    border-radius: 50%;
  }
}

.sc-user-input--text {
  width: 100%;
  resize: none;
  border: none;
  outline: none;
  box-sizing: border-box;
  font-size: 15px;
  font-weight: 400;
  line-height: 1.33;
  white-space: pre-wrap;
  word-wrap: break-word;
  color: rgba(255, 255, 255, 0.6);
  -webkit-font-smoothing: antialiased;
  max-height: 200px;
  overflow: scroll;
  bottom: 0;
  overflow-x: hidden;
  overflow-y: auto;

}

.sc-user-input--text:empty:before {
  content: attr(placeholder);
  display: block;
  /* For Firefox */
  /* color: rgba(86, 88, 103, 0.3); */
  filter: contrast(15%);
  outline: none;
  cursor: text;
}

.sc-user-input--buttons {
  display: flex;
  justify-content: flex-end;
}

// .sc-user-input--button:first-of-type {
//   width: 40px;
// }

.sc-user-input--button {
  width: 30px;
  // height: 55px;
  margin-left: 2px;
  margin-right: 2px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.sc-user-input.active {
  box-shadow: none;
  background-color: rgb(18, 18, 18);
  box-shadow: 0px -5px 20px 0px rgba(18, 18, 18, 0.2);

  .sc-user-input--text {
    color: rgba(255, 255, 255, 0.8);
  }
}

.sc-user-input--button label {
  position: relative;
  height: 24px;
  padding-left: 3px;
  cursor: pointer;
}

.sc-user-input--button label:hover path {
  fill: rgba(86, 88, 103, 1);
}

.sc-user-input--button input {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  z-index: 99999;
  height: 100%;
  opacity: 0;
  cursor: pointer;
  overflow: hidden;
}

.file-container {
  background-color: #f4f7f9;
  border-top-left-radius: 10px;
  padding: 5px 20px;
  color: #565867;
}

.delete-file-message {
  font-style: normal;
  float: right;
  cursor: pointer;
  color: #c8cad0;
}

.delete-file-message:hover {
  color: #5d5e6d;
}

.icon-file-message {
  margin-right: 5px;
}
</style>
