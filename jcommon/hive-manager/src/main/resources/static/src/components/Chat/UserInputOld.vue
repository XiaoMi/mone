<!-- 202408261版本之前使用 -->
<template>
    <div
      class="sc-user-input-wrapper"
      @keydown.tab="close"
      @keydown.esc="handleKeyEscape"
      @keydown="handleKeyDown"
      @keydown.up="highlight(highlightedIndex - 1)"
      @keydown.down="highlight(highlightedIndex + 1)"
    >
      <div class="sc-user-input" :class="{ active: inputActive }">
      <div class="sc-user-input-body">
        <div class="inputs-box">
          <AutoCompleteInput
            ref="autoCompleteInput"
            v-model="text"
            :cmd="[activeCommondLabel || activeKnowledgeBasesLabel]"
            :placeholder="placeholder"
            :initCodePrompt="initCodePrompt"
            :setInputActive="setInputActive"
            @submit="submitText"
          />
        </div>
        <div class="bt-box">
          <div class="sc-user-input--hbuttons">
            <el-dropdown @command="setContextMaxNum">
              <div class="sc-user-input--hbutton">
                <IconLines color="red" />
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="(item, index) of maxNums" :key="item" :command="item"
                    >{{ index == maxNums.length - 1 ? '携带全部对话信息' : `携带最近${item - 1}条对话信息` }}</el-dropdown-item
                  >
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <div class="sc-user-input--hbutton" @click.native.prevent="deleteMsg">
              <IconDelete />
            </div>
            <div
              class="sc-user-input--hbutton"
              @click.native.prevent="toggleContext"
            >
              <el-badge
                v-if="chatContext.isAllow"
                :value="chatContext.maxNum == maxNums[maxNums.length - 1] ? '全部' : chatContext.maxNum - 1"
                class="item"
                type="primary"
              >
                <IconContext :color="iconContextColor" />
              </el-badge>
              <IconContext v-else :color="iconContextColor" />
            </div>
            <el-popover
              placement="top-start"
              title=""
              :width="200"
              trigger="hover"
              :content="`${
                bizUpdate
                  ? '代码上下文已经更新:' + percentage + '%'
                  : '点击将刷新代码上下文信息'
              }`"
            >
              <template #reference>
                <div
                  v-if="!bizUpdate"
                  class="sc-user-input--hbutton"
                  @click.native.prevent="flushBiz"
                >
                  <font-awesome-icon
                    style="height: 12px; color: aqua"
                    :icon="['fas', 'database']"
                  />
                </div>
                <div class="sc-user-input--rbutton" v-else>
                  <el-progress
                    :width="30"
                    type="circle"
                    :percentage="percentage"
                  />
                </div>
              </template>
            </el-popover>
            <div v-if="allSuggestions.length" class="sc-user-input--hcbutton" @click="toggleCommond">
              <div><span style="color: aqua;">/</span><span>指令</span></div>
            </div>
            <div v-if="allKnowledgeBases.length" class="sc-user-input--hcbutton" @click="toggleKnowledgeBase">
              <div><span style="color: aqua;">#</span><span>知识库</span></div>
            </div>
          </div>
          <div class="sc-user-input--buttons h-100">
            <div v-if="vision" class="sc-user-input--button">
              <Recoder @submit="submitAudio" />
            </div>
            <div v-if="vision" class="sc-user-input--button test">
              <ImageUpload :limit="1" v-model="images" />
            </div>
            <div v-if="vision" class="sc-user-input--button">
              <Screenshot v-model="screenshotImages" />
            </div>
            <div class="send-btn-box">
              <UserInputButton
                :color="iconSendColor"
                tooltip="Send"
                @click.native.prevent="submitText"
                class="send-btn"
              >
                <IconSend />
              </UserInputButton>
            </div>
          </div>
        </div>
        </div>
      </div>
      <div v-if="suggestionVisible" class="sc-user-input--commonds">
        <div class="sc-user-input--commonds-list">
          <div
            v-for="(item, index) of suggestions"
            @click="setSuggestion(item.value)"
            :class="`sc-user-input--commonds-item${index === highlightedIndex ? ' active' : ''}`"
            :key="item.value"
          >
            <span style="color: aqua;">{{ item.label.slice(0, 1) }}</span>{{ item.label.slice(1) }}
          </div>
        </div>
      </div>
      <div v-if="cKnowledgeBasesVisible" class="sc-user-input--commonds">
        <div class="sc-user-input--commonds-list">
          <div
            v-for="(item, index) of knowledgeBases"
            @click="setKnowledge(item.value)"
            :class="`sc-user-input--commonds-item${index === highlightedIndex ? ' active' : ''}`"
            :key="item.value"
          >
            <span style="color: aqua;">{{ item.label.slice(0, 1) }}</span>{{ item.label.slice(1) }}
          </div>
        </div>
      </div>
    </div>
  </template>

  <script lang="ts">
  import { mapState } from "pinia";
  import util from "@/libs/util";
  import { useUserStore } from "@/stores/user";
  import { useEditStore } from "@/stores/edit";
  import { useChatContextStore, legalMaxNums } from "@/stores/chat-context";
  import { useIdeaInfoStore } from "@/stores/idea-info";
  import Recoder from "@/components/recorder/index.vue";
  import UserInputButton from "./UserInputButton.vue";
  import IconSend from "./components/icons/IconSend.vue";
  import IconLines from "./components/icons/IconLines.vue";
  import IconDelete from "./components/icons/IconDelete.vue";
  import IconContext from "./components/icons/IconContext.vue";
  import ImageUpload from "./components/image-upload/index.vue";
  import Screenshot from "./components/screenshot/index.vue";
  import AutoCompleteInput from "./AutoCompleteInput.vue";

  const { disableContext, enableContext, setMaxNum } = useChatContextStore();

  export default {
    components: {
      UserInputButton,
      IconSend,
      IconLines,
      IconDelete,
      IconContext,
      Recoder,
      ImageUpload,
      Screenshot,
      AutoCompleteInput,
    },
    props: {
      onSubmit: {
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
    watch: {
      text(newValue, oldValue) {
        if (newValue != oldValue) {
          this.querySuggestion(newValue);
          this.queryKnowledgeBases(newValue);
        }
      },
    },
    data() {
      return {
        text: "",
        inputActive: false,
        percentage: 0,
        bizUpdate: false,
        screenshotImages: [] as {
          mediaType: string;
          url: string;
          input: string;
        }[],
        images: [] as {
          mediaType: string;
          url: string;
          input: string;
        }[],
        maxNums: legalMaxNums,
        activeCommond: '',
        commandVisible: false,
        knowledgeBasesId: '',
        knowledgeBasesVisible: false,
        allSuggestions: [] as {
          id: string,
          label: string
          value: string
          cmd: string
          params: any
        }[],
        suggestions: [] as {
          id: string,
          label: string
          value: string
          cmd: string
          params: any
        }[],
        knowledgeBases: [] as {
          id: string,
          label: string
          value: string
        }[],
        allKnowledgeBases: [] as {
          id: string
          label: string
          value: string
        }[],
        highlightedIndex: 0,
      };
    },
    computed: {
      ...mapState(useUserStore, ["user"]),
      ...mapState(useEditStore, ["edit"]),
      ...mapState(useChatContextStore, ["chatContext"]),
      ...mapState(useIdeaInfoStore, ["vision"]),
      iconSendColor() {
        const text = (this.text || "").trim();
        return text && this.edit.isEdit ? "rgba(255, 255, 255, 0.8)" : "#565867";
      },
      iconContextColor() {
        const isAllow = this.chatContext.isAllow;
        return isAllow ? "#67C23A" : "#F56C6C";
      },
      activeCommondLabel() {
        const cmd = this.allSuggestions.find(it => it.value === this.activeCommond)
        return cmd ? cmd.label : ''
      },
      activeCommondObj() {
        const cmd = this.allSuggestions.find(it => it.value === this.activeCommond)
        return cmd ? cmd : null
      },
      activeKnowledgeBasesLabel() {
        const item = this.allKnowledgeBases.find(it => it.value === this.knowledgeBasesId)
        return item ? item.label : ''
      },
      cKnowledgeBasesVisible() {
        return this.knowledgeBasesVisible && this.knowledgeBases.length > 0;
      },
      suggestionVisible() {
        return this.commandVisible && this.suggestions.length > 0;
      },
    },
    created() {
      this.allKnowledgeBases = [];
      util.fetchCommonds().then((res) => {
        // console.log('fetchCommonds', res)
        if (Array.isArray(res)) {
          this.allSuggestions = (res || []).map((it: any) => {
            return {
                id: it.id,
                label: `${it.label}`,
                value: it.id,
                cmd: it.value,
                params: {
                  ...it
                }
              }
          });
        }
      })
      this.allSuggestions = [];
      util.fetchKnowledgeBases().then((res) => {
        try {
          const jRes = JSON.parse(res);
          console.log('fetchKnowledgeBases:', jRes);
          if (jRes.code == 0 && Array.isArray(jRes.data)) {
            this.allKnowledgeBases = jRes.data.map((it: any) => {
              return {
                id: it.id,
                label: `#${it.name}`,
                value: it.id,
              }
            })
          }
        } catch (e) {
          // this.allKnowledgeBases = [];
        }
      }, () => {
        console.log("error");
      });
    },
    methods: {
      querySuggestion(text: string) {
        if (text.startsWith("/")) {
          this.suggestions = this.allSuggestions
            .filter((it) => it.label.startsWith(text))
            .map((it) => {
             return it;
            });
            this.commandVisible = true;
        } else {
          this.commandVisible = false;
          this.suggestions = [];
        }
      },
      queryKnowledgeBases(text: string) {
        if (text.startsWith("#")) {
          this.knowledgeBases = this.allKnowledgeBases.filter((it) => it.label.startsWith(text)).map(it => {
            return it;
          })
          this.knowledgeBasesVisible = true;
        } else {
          this.knowledgeBasesVisible = false;
          this.knowledgeBases = [];
        }
      },
      highlight(index: number) {
        console.log(index);
        if (index < 0) {
          this.highlightedIndex = -1;
          return;
        }
        if (this.suggestionVisible) {
          const suggestions = this.suggestions;
          if (index >= suggestions.length) {
            index = suggestions.length - 1;
          }
          this.highlightedIndex = index;
        }
        if (this.cKnowledgeBasesVisible) {
          const knowledgeBases = this.knowledgeBases;
          if (index >= knowledgeBases.length) {
            index = knowledgeBases.length - 1;
          }
          this.highlightedIndex = index;
        }
      },
      handleKeyEscape(evt: Event) {
        if (this.suggestionVisible) {
          evt.preventDefault();
          evt.stopPropagation();
          this.close();
        }
      },
      handleKeyDown(event: KeyboardEvent) {
        const suggestions = this.suggestions;
        const knowledgeBases = this.knowledgeBases;
        const highlightedIndex = this.highlightedIndex;
        const suggestionVisible = this.suggestionVisible;
        const cKnowledgeBasesVisible = this.cKnowledgeBasesVisible;
        if (event.key === "Delete" || event.key === "Backspace") {
          if (this.text == '' && (this.activeCommond || this.knowledgeBasesId)) {
            event.preventDefault();
            event.stopPropagation();
            this.activeCommond = "";
            this.knowledgeBasesId = "";
            this.close();
          }
        } else if (event.key === 'Enter' && !event.shiftKey) {
          if (suggestionVisible && suggestions[highlightedIndex]) {
            event.preventDefault();
            event.stopPropagation();
            this.setSuggestion(suggestions[highlightedIndex].value);
            this.close();
          } else if ((suggestionVisible || cKnowledgeBasesVisible) && highlightedIndex === -1) {
            event.preventDefault();
            event.stopPropagation();
            this.close();
          } else if (cKnowledgeBasesVisible && knowledgeBases[highlightedIndex]) {
            event.preventDefault();
            event.stopPropagation();
            this.setKnowledge(knowledgeBases[highlightedIndex].value);
            this.close();
          }
        } else if (event.key === 'Enter' && event.shiftKey) {
          this.submitText();
          event.preventDefault();
          event.stopPropagation();
          this.close();
        }
      },
      close() {
        this.text = "";
        this.knowledgeBases = [];
        this.suggestions = [];
        this.highlightedIndex = -1;
        // @ts-ignore
        this.$refs.autoCompleteInput?.cleanTextContent();
      },
      toggleKnowledgeBase() {
        this.highlightedIndex = -1;
        this.commandVisible = false;
        this.knowledgeBases = [ ...this.allKnowledgeBases ];
        this.knowledgeBasesVisible = !this.knowledgeBasesVisible
      },
      toggleCommond() {
        this.highlightedIndex = -1;
        this.knowledgeBasesVisible = false;
        this.suggestions = [ ...this.allSuggestions ];
        this.commandVisible = !this.commandVisible;
      },
      setKnowledge(knowledgeBasesId: string) {
        this.activeCommond = '';
        this.knowledgeBasesId = knowledgeBasesId;
        this.knowledgeBasesVisible = false;
        this.close();
      },
      setSuggestion(suggestion: string) {
        this.activeCommond = suggestion;
        this.knowledgeBasesId = "";
        this.close();
      },
      setInputActive(onoff: boolean) {
        this.inputActive = onoff;
      },
      // async submitImage(item: { image_url: string }) {
      //   if (item && item.image_url) {
      //     util
      //       .myVision({
      //         ...item,
      //       })
      //       .then(() => {
      //         this.onSubmit({
      //           type: "image",
      //           mete: {
      //             role: "IDEA",
      //           },
      //           author: {
      //             username: this.user.username,
      //             cname: this.user.cname,
      //             avatar: this.user.avatar,
      //           },
      //           data: {
      //             text: item.image_url,
      //             content: this.visionForm.text,
      //           },
      //         });
      //       })
      //       .catch((e) => {
      //         console.error(e);
      //       });
      //   } else {
      //     console.error("item null", item);
      //   }
      // },
      async submitAudio(url: string) {
        await this.onSubmit({
          type: "audio",
          mete: {
            role: "IDEA",
          },
          author: {
            username: this.user.username,
            cname: this.user.cname,
            avatar: this.user.avatar,
          },
          data: {
            text: url,
          },
        });
      },
      async submitText() {
        const activeCommond = this.activeCommond;
        const activeCommondLabel = this.activeCommondLabel;
        const activeKnowledgeBasesLabel = this.activeKnowledgeBasesLabel;
        const knowledgeBasesId = this.knowledgeBasesId;
        const text = this.text;
        if (this.edit.isEdit && text && text.length > 0) {
          if (text === "?clear" || activeCommond == 'clear') {
            //清除
            this.deleteMsg();
          } else if (text === "?state_ask") {
            this.askState();
          } else if (text.startsWith("?state_fullback=")) {
            const ss = text.split("=");
            this.setStateFullback(ss[1]);
          } else if ((this.images && this.images.length > 0)
            || (this.screenshotImages && this.screenshotImages.length > 0)) {
            const image = this.images[0] || this.screenshotImages[0];
            this.onSubmit({
              type: "image",
              mete: {
                role: "IDEA",
              },
              author: {
                username: this.user.username,
                cname: this.user.cname,
                avatar: this.user.avatar,
              },
              data: {
                text: image.url,
                content: text,
              },
            });
            await util.myVision({
              mediaType: image.mediaType,
              input: image.input,
              postscript: text,
            });
            this.images = [];
            this.screenshotImages = [];
          } else {
            let code = "";
            code = await util.modifyPrompt(text);
            await this.onSubmit({
              type: "md",
              meta: {
                role: code.startsWith("?") ? "IDEA" : "USER",
              },
              author: {
                username: this.user.username,
                cname: this.user.cname,
                avatar: this.user.avatar,
              },
              data: {
                text: code,
                cmd: activeCommond ? {
                  id: this.activeCommondObj?.id,
                  label: activeCommondLabel,
                  value: this.activeCommondObj?.cmd,
                  params: this.activeCommondObj?.params
                } : undefined,
                knowledgeBase: knowledgeBasesId ? {
                  label: activeKnowledgeBasesLabel,
                  value: knowledgeBasesId
                } : undefined,
              },
            });
          }
          this.text = "";
          this.activeCommond = "";
          this.knowledgeBasesId = "";
        }
      },
      async deleteMsg() {
        // try {
        //   await util.clearMessage();
        //   this.$props.initCodePrompt && this.$props.initCodePrompt();
        // } catch (e) {
        //   console.error(e);
        // }
        await this.onSubmit({
          type: "md",
          meta: {
            role: "IDEA",
          },
          author: {
            username: this.user.username,
            cname: this.user.cname,
            avatar: this.user.avatar,
          },
          data: {
            text: '',
            cmd: {
              label: '/清空会话',
              value: 'clear'
            },
            knowledgeBase: undefined,
          },
        });
      },
      async askState() {
        try {
          const res = await util.askState();
          console.log("res", res);
          window.showErrorCode(
            decodeURIComponent(
              JSON.stringify({
                type: "list",
                code: 0,
                message: res,
              })
            )
          );
        } catch (e) {
          console.error(e);
        }
      },
      async setStateFullback(index: string | undefined) {
        if (!index) {
          // @ts-ignore
          this.$message.error("回退的index不能为空");
          return;
        }
        try {
          const res = await util.setStateFullback(index);
          // @ts-ignore
          this.$message.success(res);
          //回退提示
        } catch (e) {
          // @ts-ignore
          this.$message.error("执行失败");
          console.error(e);
        }
      },
      setContextMaxNum(num: number) {
        setMaxNum(num);
      },
      toggleContext() {
        const isAllow = this.chatContext.isAllow;
        if (isAllow) {
          disableContext();
          // @ts-ignore
          this.$message.warning("当前模式下, 发送消息不会携带之前的聊天记录");
        } else {
          enableContext();
          // @ts-ignore
          this.$message.success("当前模式下, 发送消息会携带之前的聊天记录");
        }
      },
      async getPercentage(target: any) {
        try {
          const { msg } = await this.commond("embedding_status");
          if (!msg) {
            target.bizUpdate = false;
            return;
          }
          console.log(msg);
          const data = JSON.parse(msg.substring(0, msg.lastIndexOf(":")));
          if (data?.success === "true" || data.successCnt >= data.total) {
            target.bizUpdate = false;
            return;
          }
          if (data.total > 0) {
            this.percentage = Number(
              ((data.successCnt / data.total) * 100).toFixed(2)
            );
          }
          setTimeout(() => {
            this.getPercentage(target);
          }, 300);
        } catch (e) {
          target.bizUpdate = false;
          console.error(e);
        }
      },
      async commond(cmd: string) {
        return util.getAiGuide(cmd);
      },
      async flushBiz() {
        let err = false;
        try {
          this.bizUpdate = true;
          const { msg } = await this.commond("flush_biz");
          window.showErrorCode(
            decodeURIComponent(
              JSON.stringify({
                code: 0,
                message: msg,
              })
            )
          );
        } catch (e) {
          console.error(e);
          err = true;
          this.bizUpdate = false;
        }
        if (!err) {
          await this.getPercentage(this);
        }
      },
    },
  };
  </script>

  <style scoped lang="scss">
  .sc-user-input-wrapper {
    position: relative;
    flex-shrink: 0;
    flex-grow: 0;
  }

  .sc-user-input {
    flex: 1;
    min-height: 55px;
    margin: 0px;
    position: relative;
    bottom: 0;
    padding: 2px;
    // display: flex;
    background-color: rgb(38, 38, 38);
    border-bottom-left-radius: 10px;
    border-bottom-right-radius: 10px;
    transition: background-color 0.2s ease, box-shadow 0.2s ease;
    align-items: center;
  }

  .sc-user-input--hbuttons {
    display: flex;
    align-items: center;
    .sc-user-input--hbutton {
      svg {
        display: inline-block;
        width: 12px;
        height: 12px;
      }
    }
  }
  .send-btn-box {
    height: 100%;
  }
  .send-btn {
    height: 100%;
    width: 30px;
    // &:hover {
    //   background: #565857;
    // }
  }

  .sc-user-input--hbutton {
    display: flex;
    justify-content: center;
    align-items: center;

    // margin-left: 5px;
    padding: 5px;

    width: 30px;
    height: 30px;

    cursor: pointer;

    &:hover {
      background-color: #565867;
      // border-radius: 2px;
    }
  }

  .sc-user-input--hcbutton {
    display: flex;
    justify-content: center;
    align-items: center;

    // margin-left: 5px;
    padding: 5px;

    // width: 30px;
    height: 30px;

    cursor: pointer;

    font-size: 12px;
    color: #FFF;

    &:hover {
      // background-color: #565867;
      // border-radius: 2px;
    }
  }

  .sc-user-input--rbutton {
    display: flex;
    justify-items: center;
    align-items: center;

    margin-left: 5px;
    padding: 5px;

    width: 30px;
    height: 30px;
  }

  .sc-user-input--text {
    width: 100%;
    resize: none;
    border: none;
    outline: none;
    border-bottom-left-radius: 10px;
    box-sizing: border-box;
    padding: 18px;
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
    align-items: center;
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

    cursor: pointer;
  }

  .sc-user-input.active {
    box-shadow: none;
    // background-color: rgb(18, 18, 18);
    // box-shadow: 0px -5px 20px 0px rgba(18, 18, 18, 0.2);
    background-image: linear-gradient(to bottom, #B2C8A8, #9454A0);
    border-radius: 10px;
    .sc-user-input-body {
      background-color: rgb(18, 18, 18);
      border-radius: 10px;
    }

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
  .inputs-box {
    // display: flex;
  }
  .bt-box {
    display: flex;
    justify-content: space-between;
    padding: 0 10px;
    align-items: center;
    border-top: solid 1px #484848;
    height: 30px;
  }

  .sc-user-input--commonds {
    position: absolute;

    color: #FFF;

    left: 5px;
    right: 5px;
    bottom: 90px;

    &-list {
      padding: 10px;
      background-color: rgb(38, 38, 38);
      border-radius: 10px;
    }

    &-item {
      cursor: pointer;
      padding: 5px;

      border-radius: 6px;

      &:hover {
        background-color: cornflowerblue;
      }

      &.active {
        background-color: cornflowerblue;
      }
    }
  }
  </style>
