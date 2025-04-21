<template>
  <div class="sc-user-input--text-wrapper">
    <div class="sc-user-input--text-cmd">{{ cmd }}</div>
    <div
      ref="userInput"
      tabIndex="0"
      contentEditable="true"
      :placeholder="placeholder"
      class="sc-user-input--text"
      @focus="setInputActive(true)"
      @blur="setInputActive(false)"
      @input="handleInput"
    >
  </div>
  </div>
</template>

<script lang="ts">
import UserInputButton from "./UserInputButton.vue";
import IconSend from "./components/icons/IconSend.vue";
import IconDelete from "./components/icons/IconDelete.vue";
import IconContext from "./components/icons/IconContext.vue";

export default {
  components: {
    UserInputButton,
    IconSend,
    IconDelete,
    IconContext,
  },
  props: {
    modelValue: {
      type: String,
      required: true,
    },
    cmd: {
      type: String,
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
  },
  data(): {
    suggestionLoading: boolean;
    highlightedIndex: number;
    inputActive: boolean;
    suggestions: string[];
    pos: number;
  } {
    return {
      suggestionLoading: false,
      highlightedIndex: 0,
      inputActive: false,
      suggestions: [],
      pos: 0,
    };
  },
  computed: {
    suggestionVisible() {
      if (this.suggestions && this.suggestions.length <= 0) {
        return false;
      } else if (
        this.suggestions.length === 1 &&
        this.modelValue.indexOf(this.suggestions[0]) != -1
      ) {
        return false;
      }
      return true;
    },
  },
  mounted() {
    this.init();
    const that = this;
    // @ts-ignore
    that.setUserCode = window.setUserCode;
    // 要求转码
    // @ts-ignore
    window.setUserCode = function (code) {
      that.updateModelValue(window.decodeURIComponent(code));
      that.focusUserInput();
      that.setInputActive(true);
    };
  },
  unmounted() {
    // @ts-ignore
    window.setUserCode = this.setUserCode;
  },
  watch: {
    // cmd(newValue, value) {
    //   if (newValue && newValue !== value && this.$refs.userInput) {
    //     (this.$refs.userInput as HTMLDivElement).innerHTML = `<span class="sc-user-input--text-cmd" contentEditable="false">${this.cmd}</span>${this.modelValue}`
    //     this.setCursortPosition()
    //     // const editableDiv = this.$refs.userInput as Node;
    //     // // 设置光标到末尾
    //     // (editableDiv as HTMLDivElement).focus();
    //     // var range = document.createRange();
    //     // var selection = window.getSelection();

    //     // var lastChild = editableDiv.childNodes[editableDiv.childNodes.length - 1];
    //     // console.log(lastChild);
    //     // if (lastChild) {
    //     //   range.setStartAfter(lastChild);
    //     //   range.collapse(false); // 设置为false，表示光标在range的末尾
    //     // } else {
    //     //   // 如果div没有子节点，直接设置在div的末尾
    //     //   range.selectNodeContents(editableDiv);
    //     //   range.collapse(false);
    //     // }
    //     // // 清除Selection中的所有范围，并添加新的range
    //     // selection?.removeAllRanges();
    //     // selection?.addRange(range);
    //   } else if (!newValue && newValue !== value && this.$refs.userInput) {
    //     (this.$refs.userInput as HTMLDivElement).textContent = `${this.modelValue}`
    //   }
    // },
    modelValue(newValue, oldValue) {
      if (this.$refs.userInput) {
        if (newValue != oldValue) {
          (this.$refs.userInput as HTMLDivElement).textContent = newValue;
          // if (!this.cmd) {
          //   (this.$refs.userInput as HTMLDivElement).textContent = newValue;
          // } else {
          //   (this.$refs.userInput as HTMLDivElement).innerHTML = `<span class="sc-user-input--text-cmd" contentEditable="false">${this.cmd}</span>${this.modelValue}`;
          //   this.setCursortPosition();
          // }
        }
        // if (newValue != oldValue) {
        //   if (this.cmd) {
        //     (this.$refs.userInput as HTMLDivElement).innerHTML = `<span class="sc-user-input--text-cmd" contentEditable="false">${this.cmd}</span>${this.modelValue}`
        //     this.setCursortPosition();
        //   } else {
        //     (this.$refs.userInput as HTMLDivElement).textContent = newValue;
        //   }
        // }
        // const pos = this.getCursortPosition(this.$refs.userInput);
        // if (pos === 0) {
        //   this.setCaretPosition(
        //     this.$refs.userInput,
        //     this.pos + newValue.length - oldValue.length
        //   );
        // }
        // if (newValue !== oldValue) {
        //   this.querySuggestion(newValue);
        // }
      }
    },
  },
  methods: {
    setCursortPosition() {
      const editableDiv = this.$refs.userInput as Node;
      // 设置光标到末尾
      (editableDiv as HTMLDivElement).focus();
      var range = document.createRange();
      var selection = window.getSelection();

      var lastChild = editableDiv.childNodes[editableDiv.childNodes.length - 1];
      console.log(lastChild);
      if (lastChild) {
        range.setStartAfter(lastChild);
        range.collapse(false); // 设置为false，表示光标在range的末尾
      } else {
        // 如果div没有子节点，直接设置在div的末尾
        range.selectNodeContents(editableDiv);
        range.collapse(false);
      }
      // 清除Selection中的所有范围，并添加新的range
      selection?.removeAllRanges();
      selection?.addRange(range);
    },
    // // 获取当前光标位置
    // getCursortPosition: function (element) {
    //   var caretOffset = 0;
    //   var doc = element.ownerDocument || element.document;
    //   var win = doc.defaultView || doc.parentWindow;
    //   var sel;
    //   // 谷歌、火狐
    //   if (typeof win.getSelection != "undefined") {
    //     sel = win.getSelection();
    //     // 选中的区域
    //     if (sel.rangeCount > 0) {
    //       var range = win.getSelection().getRangeAt(0);
    //       // 克隆一个选中区域
    //       var preCaretRange = range.cloneRange();
    //       // 设置选中区域的节点内容为当前节点
    //       preCaretRange.selectNodeContents(element);
    //       // 重置选中区域的结束位置
    //       preCaretRange.setEnd(range.endContainer, range.endOffset);
    //       caretOffset = preCaretRange.toString().length;
    //     }
    //     // IE浏览器
    //   } else if ((sel = doc.selection) && sel.type != "Control") {
    //     var textRange = sel.createRange();
    //     var preCaretTextRange = doc.body.createTextRange();
    //     preCaretTextRange.moveToElementText(element);
    //     preCaretTextRange.setEndPoint("EndToEnd", textRange);
    //     caretOffset = preCaretTextRange.text.length;
    //   }
    //   return caretOffset;
    // },
    // setCaretPosition: function (element, pos) {
    //   var range, selection;
    //   // Firefox, Chrome, Opera, Safari, IE 9+
    //   if (document.createRange) {
    //     // 创建一个选中区域
    //     range = document.createRange();
    //     // 选中节点的内容
    //     range.selectNodeContents(element);
    //     if (element.innerHTML.length > 0) {
    //       // 设置光标起始为指定位置
    //       range.setStart(element.childNodes[0], pos);
    //     }
    //     // 设置选中区域为一个点
    //     range.collapse(true);
    //     // 获取当前选中区域
    //     selection = window.getSelection();
    //     // 移除所有的选中范围
    //     selection.removeAllRanges();
    //     // 添加新建的范围
    //     selection.addRange(range);
    //     //IE 8 and lower
    //   } else if (document.selection) {
    //     // 创建一个范围（范围与所选内容类似但不可见）
    //     range = document.body.createTextRange();
    //     // 选择范围的元素的全部内容
    //     range.moveToElementText(element);
    //     // 将范围折叠到终点
    //     range.collapse(false);
    //     // 选择范围
    //     range.select();
    //   }
    // },
    init() {
      // if (this.cmd) {
      //   (this.$refs.userInput as HTMLDivElement).innerHTML = `<span class="sc-user-input--text-cmd" contentEditable="false">${this.cmd}</span>`;
      // } else {
      //   (this.$refs.userInput as HTMLDivElement).textContent = this.modelValue;
      // }
      (this.$refs.userInput as HTMLDivElement).textContent = this.modelValue;
    },
    // querySuggestion(item: string) {
    //   if (item.startsWith("?")) {
    //     this.suggestions = suggestions
    //       .filter((it) => it.startsWith(item))
    //       .map((it) => it);
    //   } else {
    //     this.suggestions = [];
    //   }
    // },
    // handleSelect(suggestion: string) {
    //   const textDiv = this.$refs.userInput;
    //   textDiv.textContent = suggestion;
    //   this.updateModelValue(suggestion);
    //   this.setCursor(textDiv);
    //   this.close();
    // },
    // onHide() {
    //   this.highlightedIndex = -1;
    // },
    // highlight(index: number) {
    //   const suggestions = this.suggestions;
    //   if (!this.suggestionVisible) return;

    //   if (index < 0) {
    //     this.highlightedIndex = -1;
    //     return;
    //   }

    //   if (index >= suggestions.length) {
    //     index = suggestions.length - 1;
    //   }
    //   this.highlightedIndex = index;
    // },
    // setCursor(textDiv: any) {
    //   this.$nextTick(() => {
    //     const selectedText = window.getSelection();
    //     const selectedRange = document.createRange();
    //     selectedRange.setStart(
    //       textDiv.childNodes[0],
    //       textDiv.textContent.length
    //     );
    //     selectedRange.collapse(true);
    //     selectedText?.removeAllRanges();
    //     selectedText?.addRange(selectedRange);
    //     textDiv.focus();
    //   });
    // },
    handleInput(event: any) {
      // if (this.cmd) {
      //   this.updateModelValue(((event.target && event.target.textContent) || "").replace(this.cmd, ""));
      // } else {
      //   this.updateModelValue((event.target && event.target.textContent) || "");
      // }
      this.updateModelValue((event.target && event.target.textContent) || "");
    },
    updateModelValue(text: string) {
      this.$emit("update:modelValue", text);
    },
    focusUserInput() {
      this.$nextTick(() => {
        (this.$refs.userInput as any).focus();
      });
    },
    // async _submitText() {
    //   const text = this.$refs.userInput.textContent;
    //   this.$emit("submit", text);
    //   this._editFinish();
    // },
    // _editFinish() {
    //   this.$refs.userInput.textContent = "";
    // },
  },
};
</script>

<style lang="scss">
.sc-user-input--text-wrapper {
  display: flex;
  padding: 18px;
  align-items: center;

  .sc-user-input--text-cmd {
    flex-shrink: 0;
    color: aqua;
    max-width: 30%;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
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
  border-bottom-left-radius: 10px;
  box-sizing: border-box;
  padding-left: 8px;
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
  flex-grow: 1;
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
