<!-- 202408261版本之后使用 -->
<template>
  <div class="sc-user-input-wrapper" @keydown.tab="close" @keydown.esc="handleKeyEscape" @keydown="handleKeyDown"
    @keydown.up="highlight(highlightedIndex - 1)" @keydown.down="highlight(highlightedIndex + 1)">
    <!-- 独立功能面板 -->
    <transition name="panel-slide">
      <div v-show="showFunctionPanel" class="function-panel">
        <div class="function-item">
          <el-switch
            v-model="webSearchModel"
            size="small"
            active-text=""
            inactive-text="搜索"
            :active-value="true"
            :inactive-value="false"
          />
        </div>
        <div class="function-item">
          <el-switch
            v-model="ragModel"
            size="small"
            active-text=""
            inactive-text="RAG"
            :active-value="true"
            :inactive-value="false"
          />
        </div>
      </div>
    </transition>
    <div class="sc-user-input" :class="{ active: inputActive }">
      <div class="sc-user-input-body">
        <!-- <div class="file-list">
          <div v-if="showFileMenu" class="add-file" @click.stop="toggleKnowledgeBase(false)">
            <el-icon :size="12"><Plus /></el-icon>
            <span>file</span>
          </div>
          <ul v-if="knowledgeBasesValue.length">
            <li v-for="(item, i) in knowledgeBasesValue" :key="i">
              <span>{{ item.label }}</span>
              <el-icon @click="handleDeleteFile(item)" :size="12" color="#fff">
                <Close />
              </el-icon>
            </li>
          </ul>
        </div> -->
        <div class="inputs-box">
          <AutoCompleteInput ref="autoCompleteInput" v-model="text" :cmd="[...cmds]"
            :placeholder="isEnter ? 'Enter = 发送' : 'Shift + Enter = 发送'" :initCodePrompt="initCodePrompt"
            :setInputActive="setInputActive" @submit="submitText" />
        </div>
        <div class="bt-box">
          <div class="sc-user-input--hbuttons">
            <!-- <el-dropdown @command="setContextMaxNum">
              <div class="sc-user-input--hbutton">
                <IconLines color="red" />
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-for="(item, index) of maxNums" :key="item" :command="item">{{
                    index == maxNums.length - 1
                      ? "携带全部对话信息"
                      : `携带最近${item - 1}条对话信息`
                  }}</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <div class="sc-user-input--hbutton" @click.native.prevent="deleteMsg">
              <IconDelete />
            </div>
            <div class="sc-user-input--hbutton" @click.native.prevent="toggleContext">
              <el-badge v-if="chatContext.isAllow" :value="chatContext.maxNum == maxNums[maxNums.length - 1]
                  ? '全部'
                  : chatContext.maxNum - 1
                " class="item" type="primary">
                <IconContext :color="iconContextColor" />
              </el-badge>
              <IconContext v-else :color="iconContextColor" />
            </div>
            <div v-if="allSuggestions.length" class="sc-user-input--hcbutton" @click="toggleCommond">
              <div><span style="color: aqua;">/</span><span>指令</span></div>
            </div> -->
            <div class="sc-user-input--hcbutton" @click="toggleSendShiftEnter">
              <div>
                <span v-if="isEnter" style="color: aqua">Enter</span>
                <span v-else style="color: aqua">Shift+Enter</span>
              </div>
            </div>
            <div class="sc-user-input--hcbutton">
              <div>
                <el-radio-group v-model="sendMethod" @change="toggleSendMethod">
                  <el-radio label="sse">SSE</el-radio>
                  <el-radio label="ws">WebSocket</el-radio>
                </el-radio-group>
              </div>
            </div>
          </div>
          <div class="sc-user-input--buttons h-100">
            <div class="sc-user-input--button">
              <Screenshot v-model="screenshotImages" />
            </div>
            <div class="sc-user-input--button">
              <div class="function-toggle" @click="toggleFunctionPanel">
                <el-icon :size="16" :color="showFunctionPanel ? '#67C23A' : '#565867'">
                  <component :is="'Operation'" />
                </el-icon>
              </div>
            </div>
            <div class="sc-user-input--button test">
              <ImageUpload :limit="1" v-model="files" type="file" />
            </div>
            <div class="sc-user-input--button">
              <Recoder @submit="submitAudio" />
            </div>
            <div class="sc-user-input--button test">
              <ImageUpload :limit="1" v-model="images" type="image" />
            </div>
            <div class="sc-user-input--button test">
              <PasteImage v-model="screenshotImages" />
            </div>
            <div class="send-btn-box">
              <UserInputButton :color="iconSendColor" tooltip="Send" @click.native.prevent="sendText" class="send-btn">
                <IconSend />
              </UserInputButton>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="suggestionVisible" class="sc-user-input--commonds">
      <div class="sc-user-input--commonds-list">
        <div v-for="(item, index) of suggestions" @click="setSuggestion(item.value)" :class="`sc-user-input--commonds-item${index === highlightedIndex ? ' active' : ''
          }`" :key="item.value">
          <span style="color: aqua">{{ item.label.slice(0, 1) }}</span>{{ item.label.slice(1) }}
        </div>
      </div>
    </div>
    <div v-if="cFileVisible" class="sc-user-input--commonds" v-loading="knowledgeLoading"
      v-click-outside="handleClickOutside">
      <div class="sc-user-input--commonds-list files">
        <!-- 二级路径开始显示 -->
        <div :class="`sc-user-input--commonds-item pre`" key="pre" @click.stop>
          <template v-if="selectedKeys?.length > 1">
            <el-icon @click="setPreItem()" :size="16">
              <Back />
            </el-icon>
            <el-input v-if="rootKey == TYPE_LIST.file" v-model="filterKeyword" @input="searchFiles(filterKeyword)"
              placeholder="请输入搜索内容" />
          </template>
          <div v-else></div>
          <el-icon @click="handleKeyEscape" :size="16">
            <Close />
          </el-icon>
        </div>
        <!-- 文档为空时显示 -->
        <div :class="`sc-user-input--commonds-file sc-user-input--commonds-item`" key="addDoc"
          v-if="rootKey == TYPE_LIST.doc && cFileList?.[0].value == 'noDoc'">
          <span class="sc-user-input--commonds-file-item">暂无数据</span>
        </div>
        <!-- 二级列表 -->
        <div v-else v-for="(item, index) of cFileList" @click="setFileItem(item)" :class="`sc-user-input--commonds-file sc-user-input--commonds-item${index === highlightedIndex ? ' active' : ''
          }`" :key="`${item.value}-${item.id}`">
          <div class="sc-user-input--commonds-file-left">
            <KnowledgeIcon :item="item" :selectedKeys="selectedKeys" :rootKey="rootKey" />
            <el-tooltip effect="dark" :content="`${item.params.id}`" placement="top" v-if="rootKey == TYPE_LIST.git">
              <span class="sc-user-input--commonds-file-item">{{ item.label }}
                <em class="sc-user-input--commonds-file-item-subTitle" v-if="item.subTitle">({{ item.subTitle
                  }})</em></span>
            </el-tooltip>
            <span v-else class="sc-user-input--commonds-file-item">{{ item.label }}
              <em class="sc-user-input--commonds-file-item-subTitle" v-if="item.subTitle">({{ item.subTitle
                }})</em></span>
            <el-icon v-if="item.expend" :size="12" color="#fff">
              <Right />
            </el-icon>
          </div>
          <div class="sc-user-input--commonds-file-right"
            v-if="rootKey == TYPE_LIST.doc && item.params?.value != 'noDoc'">
            <span @click.stop="handleDelete(item)"><el-icon :size="12" color="#fff">
                <Delete />
              </el-icon></span>
          </div>
        </div>
        <!-- 文档类型显示 -->
        <div @click="addDoc()" :class="`sc-user-input--commonds-file-add sc-user-input--commonds-item`" key="addDoc"
          v-if="rootKey == TYPE_LIST.doc && selectedKeys?.length > 1">
          <el-icon :size="12" color="#fff">
            <Plus />
          </el-icon><span class="sc-user-input--commonds-file-item">新增</span>
        </div>
      </div>
      <div class="composer_config">
        <el-checkbox-group v-model="composerList">
          <el-checkbox size="small" v-for="item in composerConfig" :key="item" :label="item" :value="item" />
        </el-checkbox-group>
      </div>
    </div>
  </div>
  <AddDoc :visible="addDocVisible" @onCancel="addDocVisible = false" :groupId="TYPE_LIST.doc" />
  <FormParams v-bind:dialog-visible="showFormParams" :form-ui="formUi" @submit="updateText" />
</template>

<script lang="ts">
import { mapState } from "pinia";
import util from "@/libs/util";
import { useUserStore } from "@/stores/user";
import { useEditStore } from "@/stores/edit";
import { useChatContextStore, legalMaxNums } from "@/stores/chat-context";
import { useIdeaInfoStore } from "@/stores/idea-info";
import { useFunctionPanelStore } from "@/stores/function-panel";
import Recoder from "@/components/recorder/index.vue";
import UserInputButton from "./UserInputButton.vue";
import IconSend from "./components/icons/IconSend.vue";
import IconLines from "./components/icons/IconLines.vue";
import IconDelete from "./components/icons/IconDelete.vue";
import IconContext from "./components/icons/IconContext.vue";
import ImageUpload from "./components/image-upload/index.vue";
import Screenshot from "./components/screenshot/index.vue";
import PasteImage from "./components/paste-image/index.vue";
import FormParams from "./components/form-params/index.vue";
import AutoCompleteInput from "./AutoCompleteInput.vue";
import { ElMessage } from "element-plus";
import AddDoc from "./components/add-doc/index.vue";
import KnowledgeIcon from "./components/knowledge-icon/index.vue";
import { vClickOutside } from '@/plugins/click-outside'
import { voiceToText } from "@/api/audio";
import { ArrowUp, ArrowDown, Operation } from '@element-plus/icons-vue';
const { disableContext, enableContext, setMaxNum, setKnowledgeLoading } =
  useChatContextStore();
const functionPanelStore = useFunctionPanelStore();

interface IFileItem {
  id: string | number;
  label: string;
  value: string;
  expend: boolean;
  path: string;
  params: any;
  subTitle?: string;
}

interface IItems {
  fileName: string;
  path: string;
  relativePath: string;
  dir: boolean;
  recentOpen: boolean;
}

const TYPE_LIST = {
  project: -1,
  floder: -2,
  file: -3,
  git: -4,
  doc: -5,
  miapi: -6,
};

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
    PasteImage,
    FormParams,
    AutoCompleteInput,
    AddDoc,
    KnowledgeIcon,
    ArrowUp,
    ArrowDown,
  },
  directives: {
    clickOutside: vClickOutside
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
    changeSendMethod: {
      type: Function,
      required: true,
    },
  },
  watch: {
    images(newValue) {
      if (newValue.length > 0 && this.screenshotImages.length > 0) {
        this.screenshotImages = []
      }
    },
    screenshotImages(newValue) {
      if (this.images.length > 0 && newValue.length > 0) {
        this.images = []
      }
    },
    text(newValue, oldValue) {
      if (newValue?.trim() == "@") {
        this.toggleKnowledgeBase(true);
      }
      // else if (
      //   (this.rootKey == TYPE_LIST.file || this.rootKey == TYPE_LIST.floder) &&
      //   this.cFileVisible
      // ) {
      //   this.searchFiles(newValue);
      // }
      else if (newValue != oldValue) {
        this.querySuggestion(newValue);
        // this.queryKnowledgeBases(newValue);
        if (!newValue) {
          // this.activeCommond = "";
          // this.knowledgeBasesId = "";
        }
      }
    },
    knowledgeLoading(newValue) {
      if (!newValue) {
        clearTimeout(this.gitTimer);
      }
    },
    knowledgeData(newValue) {
      if (this.rootKey != TYPE_LIST.file && !this.cFileVisible) {
        this.handleInitKnowledge(newValue);
        this.highlightedIndex = 0;
        //@ts-ignore
        window.setUserCode?.("");
      } else if (this.cFileVisible && this.knowledgeLoading) {
        this.handleKnowledge(this.currentSelectItem, newValue);
        //@ts-ignore
        window.setUserCode?.("");
        this.highlightedIndex = 0;
      }
      setKnowledgeLoading(false);
    },
    rootKey(val, old) {
      if (val == TYPE_LIST.file) {
        this.knowledgeBasesValue = [];
      }
    },
    cFileVisible(val) {
      if (!val) {
        this.filterKeyword = undefined
      }
    }
  },
  data() {
    let isEnter = localStorage.getItem("isEnter") == "true";
    return {
      isEnter,
      TYPE_LIST,
      text: "",
      inputActive: false,
      percentage: 0,
      bizUpdate: false,
      screenshotImages: [] as {
        mediaType: string;
        url: string;
        input: string;
      }[],
      files: [] as {
        mediaType: string;
        url: string;
        input: string;
      }[],
      images: [] as {
        mediaType: string;
        url: string;
        input: string;
      }[],
      formUi: {
        labelWidth: 80,
        listUi: [
          {
            label: "你好",
            value: "text",
            type: "input" as "input",
          },
        ],
      },
      showFormParams: false,
      maxNums: legalMaxNums,
      activeCommond: "",
      commandVisible: false,
      knowledgeBasesValue: [] as Array<IFileItem>,
      filesVisible: false,
      addDocVisible: false,
      currentSelectItem: {} as IFileItem,
      rootKey: 0,
      filterKeyword: undefined,
      selectedKeys: [] as string[],
      allSuggestions: [] as {
        id: string;
        label: string;
        value: string;
        cmd: string;
        params: any;
      }[],
      suggestions: [] as {
        id: string;
        label: string;
        value: string;
        cmd: string;
        params: any;
      }[],
      originalBases: {} as {
        [propName: string]: IFileItem[];
      },
      originalList: [] as Array<IItems>,
      highlightedIndex: 0,
      gitTimer: 0,
      composerConfig: ['bugfix', 'bizJar', 'Codebase', 'Analyze', 'Knowledge', 'UnitTest'],
      composerList: [],
      sendMethod: 'sse',
    };
  },
  computed: {
    ...mapState(useUserStore, ["user"]),
    ...mapState(useEditStore, ["edit"]),
    ...mapState(useChatContextStore, [
      "chatContext",
      "knowledgeLoading",
      "knowledgeData",
    ]),
    ...mapState(useIdeaInfoStore, ["vision", "showFileMenu"]),
    ...mapState(useFunctionPanelStore, ["webSearchEnabled", "ragEnabled", "showFunctionPanel"]),
    cmds() {
      if (this.activeCommondLabel?.length) {
        return this.activeCommondLabel;
      }
      return [];
    },
    iconSendColor() {
      const text = (this.text || "").trim();
      return text && this.edit.isEdit ? "rgba(255, 255, 255, 0.8)" : "#565867";
    },
    iconContextColor() {
      const isAllow = this.chatContext.isAllow;
      return isAllow ? "#67C23A" : "#F56C6C";
    },
    activeCommondLabel() {
      const cmd = this.allSuggestions.find(
        (it) => it.value === this.activeCommond
      );
      return cmd ? [cmd.label] : [];
    },
    activeCommondObj() {
      const cmd = this.allSuggestions.find(
        (it) => it.value === this.activeCommond
      );
      return cmd ? cmd : null;
    },
    activeKnowledgeBasesLabel() {
      return this.knowledgeBasesValue?.length
        ? this.knowledgeBasesValue.map((v) => `@${v.label}`)
        : [];
    },
    cFileVisible() {
      return this.filesVisible && this.cFileList?.length > 0;
    },
    suggestionVisible() {
      return this.commandVisible && this.suggestions.length > 0;
    },
    cFileList() {
      if (this.selectedKeys?.length) {
        return (
          this.originalBases[this.selectedKeys[this.selectedKeys.length - 1]] ||
          []
        );
      }
      return [];
    },
    activeFunctionCount() {
      return functionPanelStore.getActiveFunctionCount();
    },
    webSearchModel: {
      get() {
        return this.webSearchEnabled;
      },
      set(value: boolean) {
        functionPanelStore.setWebSearchEnabled(value);
      }
    },
    ragModel: {
      get() {
        return this.ragEnabled;
      },
      set(value: boolean) {
        functionPanelStore.setRagEnabled(value);
      }
    },
  },
  created() {
    this.allSuggestions = [];
    // util.fetchCommonds().then((res) => {
    //   if (Array.isArray(res)) {
    //     this.allSuggestions = (res || []).map((it: any) => {
    //       return {
    //         id: it.id,
    //         label: `${it.label}`,
    //         value: it.id,
    //         cmd: it.value,
    //         params: {
    //           ...it,
    //         },
    //       };
    //     });
    //   }
    // });
  },
  mounted() {
    const that = this;
    // window.useSubmitText = (text: string) => {
    //   that.text = text;
    //   that.submitText();
    // };
  },
  methods: {
    handleClickOutside() {
      if (this.filesVisible) {
        this.handleKeyEscape()
      }
    },
    handleDeleteFile(item) {
      if (item.path) { // 文件、文件夹、mi-api、文档
        this.knowledgeBasesValue = this.knowledgeBasesValue.filter(v => v.path !== item.path)
      } else if (this.rootKey == TYPE_LIST.miapi) {
        this.knowledgeBasesValue = []
      } else if (this.rootKey == TYPE_LIST.git) {
        this.knowledgeBasesValue = this.knowledgeBasesValue.filter(v => v.id !== item.id)
      }
    },
    searchFiles(newValue) {
      let search = (newValue || "").trim();
      if (newValue?.indexOf("@") != -1) {
        search = newValue.split("@").pop();
      }
      let key = this.selectedKeys[this.selectedKeys.length - 1];
      if (!search) {
        if (this.originalList.some((v) => v.recentOpen)) {
          this.originalBases[key] = this.originalList
            .filter((v) => v.recentOpen)
            .map((v) => ({ ...this.handleFileItem(v) }))
            .map((v: any, i: number) => ({
              ...this.handleFileItem(v, true, i),
            }));
        } else {
          this.originalBases[key] = this.filterJavaFile(this.originalList)
            .map((v) => ({ ...this.handleFileItem(v) }))
            .map((v: any, i: number) => ({
              ...this.handleFileItem(v, true, i),
            }));
        }
      } else {
        let arr = this.originalList
          .map((v) => ({ ...this.handleFileItem(v) }))
          .filter(
            (v) =>
              v.fileName.toLowerCase().indexOf(search.toLowerCase()) != -1
          );
        if (arr?.length) {
          this.originalBases[key] = arr.map((v: any, i: number) => ({
            ...this.handleFileItem(v, true, i),
          }));
        }
      }
    },
    toggleSendShiftEnter() {
      if (this.isEnter) {
        localStorage.setItem("isEnter", "false");
      } else {
        localStorage.setItem("isEnter", "true");
      }
      this.isEnter = !this.isEnter;
    },
    toggleSendMethod(val: string) {
      this.$props.changeSendMethod(val)
      // if (this.isEnter) {
      //   localStorage.setItem("isEnter", "false");
      // } else {
      //   localStorage.setItem("isEnter", "true");
      // }
      // this.isEnter = !this.isEnter;
    },
    toggleFunctionPanel() {
      functionPanelStore.toggleFunctionPanel();
    },
    getFunctionConfig() {
      return {
        webSearch: this.webSearchEnabled,
        rag: this.ragEnabled,
      };
    },
    filterJavaFile(list: Array<IItems>) {
      if (this.rootKey == TYPE_LIST.file) {
        return list.filter((v) => v.fileName.endsWith(".java"));
      }
      return list;
    },
    handleFileItem(v: any, bool?: boolean, i?: number) {
      if (bool) {
        return {
          id: new Date().getTime() + i! * 100,
          label: v.name,
          value: v.name,
          expend: v.expend,
          path: v.path,
          subTitle: v.subTitle,
          params: {
            ...v,
          },
        };
      }
      return {
        ...v,
        name: v.fileName,
        value: v.path,
        subTitle: v.path,
        expend: false,
      };
    },
    handleKnowLedgeItem(it: {
      id: number;
      name: string;
      auth: number;
      expend: boolean;
      path: string;
      groupId: string;
    }) {
      return {
        id: it.id,
        label: `${it.name}`,
        value: it.id,
        expend: it.expend,
        path: it.path,
        params: {
          ...it,
        },
      };
    },
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
    highlight(index: number) {
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
      } else if (this.cFileVisible) {
        const suggestions = this.cFileList;
        if (index >= suggestions.length) {
          index = suggestions.length - 1;
        }
        this.highlightedIndex = index;
      }
    },
    handleKeyEscape(evt: Event) {
      if (this.suggestionVisible) {
        evt?.preventDefault();
        evt?.stopPropagation();
        this.close();
      } else if (this.cFileVisible) {
        evt?.preventDefault();
        evt?.stopPropagation();
        this.highlightedIndex = -1;
        this.commandVisible = false;
        this.currentSelectItem = {} as IFileItem;
        this.filesVisible = false;
        this.close();
      }
    },
    handleKeyDown(event: KeyboardEvent) {
      const suggestions = this.suggestions;
      const highlightedIndex = this.highlightedIndex;
      const suggestionVisible = this.suggestionVisible;
      // const cKnowledgeBasesVisible = this.cKnowledgeBasesVisible;
      if (event.key === "Delete" || event.key === "Backspace") {
        if (
          this.text == "" &&
          (this.activeCommond || this.knowledgeBasesValue?.length)
        ) {
          event.preventDefault();
          event.stopPropagation();
          this.activeCommond = "";
          if (this.knowledgeBasesValue?.length) {
            this.knowledgeBasesValue.pop()
          }
          this.close();
        }
      } else if (
        (event.key === "Enter" && !event.shiftKey && !this.isEnter) ||
        (this.isEnter && event.key === "Enter" && event.shiftKey)
      ) {
        if (suggestionVisible && suggestions[highlightedIndex]) {
          event.preventDefault();
          event.stopPropagation();
          this.setSuggestion(suggestions[highlightedIndex].value);
          this.close();
        } else if (this.cFileVisible && this.cFileList) {
          event.preventDefault();
          event.stopPropagation();
          this.setFileItem(this.cFileList[highlightedIndex]);
          this.close();
        }
      } else if (
        (event.key === "Enter" && event.shiftKey && !this.isEnter) ||
        (event.key === "Enter" && !event.shiftKey && this.isEnter)
      ) {
        if (this.text.trim() !== "" || this.images.length > 0 || this.screenshotImages.length > 0 || this.files.length > 0) {
          this.submitText();
        }
        event.preventDefault();
        event.stopPropagation();
        this.close();
      }
    },
    close() {
      this.text = "";
      this.suggestions = [];
      this.highlightedIndex = -1;
      this.currentSelectItem = {} as IFileItem;
      // @ts-ignore
      this.$refs?.autoCompleteInput?.cleanTextContent();
    },
    sendText() {
      this.submitText();
      this.close();
    },
    async toggleKnowledgeBase(isWatch: boolean) {
      this.highlightedIndex = -1;
      this.commandVisible = false;
      this.currentSelectItem = {} as IFileItem;
      if (!isWatch) {
        if (this.filesVisible) {
          this.filesVisible = false;
          if (this.rootKey != TYPE_LIST.file) {
            this.originalBases = {};
            this.selectedKeys = [];
          }
          return;
        }
      }
      if (!this.rootKey || this.rootKey != TYPE_LIST.file) {
        this.originalBases = {};
        this.originalList = [];
        this.selectedKeys = [];
        this.rootKey = 0;
        util.fetchKnowledgeBases();
        clearTimeout(this.gitTimer);
        setKnowledgeLoading(true);
        this.gitTimer = setTimeout(() => {
          setKnowledgeLoading(false);
        }, 5000);
      } else {
        this.filesVisible = true;
        this.highlightedIndex = 0;
      }
    },
    handleInitKnowledge(jRes: any) {
      console.log("fetchKnowledgeBases:", jRes);
      if (
        jRes.code != 0 ||
        (Array.isArray(jRes.data) && jRes.data.length == 0)
      ) {
        ElMessage.error("请先绑定知识库");
        return;
      }
      let arr = jRes.data.map((it: any) => {
        return this.handleKnowLedgeItem(it);
      });
      this.originalBases = {
        root: [...arr],
      };
      this.selectedKeys = ["root"];
      this.filesVisible = true;
    },
    async toggleCommond() {
      const res = await util.fetchCommonds();
      if (Array.isArray(res)) {
        this.allSuggestions = res.map((it: any) => {
          return {
            id: it.id,
            label: `${it.label}`,
            value: it.id,
            cmd: it.value,
            params: {
              ...it,
            },
          };
        });
      }
      this.highlightedIndex = -1;
      this.filesVisible = false;
      this.selectedKeys = [];
      this.originalBases = {};
      this.suggestions = [...this.allSuggestions];
      this.commandVisible = !this.commandVisible;
    },
    handleSelectKnowItem(item: IFileItem) {
      if (item.params.value == "noDoc") {
        return;
      }
      if (this.rootKey == TYPE_LIST.file) {
        if (this.knowledgeBasesValue.some(v => v.path == item.path)) {
          this.knowledgeBasesValue = this.knowledgeBasesValue.filter(v => v.path != item.path)
        } else {
          this.knowledgeBasesValue.push(item);
        }
      } else {
        this.knowledgeBasesValue = [item];
        this.originalBases = {};
        this.selectedKeys = [];
      }

      if (this.rootKey != TYPE_LIST.file) {
        this.filesVisible = false;
        this.close();
      }

    },
    async setFileItem(item: IFileItem) {
      this.currentSelectItem = item;
      this.activeCommond = "";
      // 存储第一层类型
      if (this.selectedKeys?.length == 1) {
        this.rootKey = item.id as number;
      }
      // 不是文件夹则是选中当前内容
      if (!item.expend) {
        this.handleSelectKnowItem(item);
        return;
      } else if (this.rootKey !== TYPE_LIST.file) {
        // 只有在不是文件的情况下才会清空当前选项
        this.knowledgeBasesValue = [];
      }
      // 不同类型请求的入参不同
      let param = {};
      if (this.rootKey == TYPE_LIST.file || this.rootKey == TYPE_LIST.floder) {
        param = {
          groupId: item.params.id || item.params.groupId,
          path: item.params.path,
        };
      } else if (this.rootKey == TYPE_LIST.doc) {
        param = {
          groupId: item.params.id,
          path: item.params.path,
          getDocList: 1,
        };
      } else {
        param = {
          groupId: item.params.id || item.params.groupId,
        };
      }
      await util.filesBases(param);
      clearTimeout(this.gitTimer);
      setKnowledgeLoading(true);
      this.gitTimer = setTimeout(() => {
        setKnowledgeLoading(false);
      }, 5000);
    },
    handleKnowledge(item: IFileItem, list: any) {
      if (this.rootKey == TYPE_LIST.doc) {
        if (list?.length == 0) {
          // 为了显示出新增按钮，填充一个不可点击的数据
          list = [
            {
              name: "暂无文档",
              value: "noDoc",
              expend: false,
            },
          ];
        } else {
          list = list.map((v: any) => ({
            name: v.fileName,
            value: v.id,
            baseId: v.knowledgeBaseId,
            expend: false,
            path: v.filePath,
          }));
        }
      } else if (this.rootKey == TYPE_LIST.git) {
        list = list.map((v: any) => ({
          name: v.title,
          value: v.id,
          expend: false,
          subTitle: v.committerName,
          ...v,
        }));
      } else if (
        this.rootKey == TYPE_LIST.file ||
        this.rootKey == TYPE_LIST.floder
      ) {
        this.originalList = [...list].filter((v: IItems) =>
          this.rootKey === TYPE_LIST.file ? !v.dir : v.dir
        );
        if (this.originalList.some((v) => v.recentOpen)) {
          list = this.originalList
            .filter((v) => v.recentOpen)
            .map((v: any) => this.handleFileItem(v));
        } else {
          list = this.filterJavaFile(this.originalList).map((v: any) =>
            this.handleFileItem(v)
          );
        }
      }
      if (!list?.length) {
        this.filesVisible = false;
        this.selectedKeys = [];
        return;
      }
      let key = `${item.id}&${item.label}`;
      this.selectedKeys.push(key);
      this.originalBases[key] = list.map((v: any, i: number) => ({
        id: new Date().getTime() + i! * 100,
        label: v.name,
        value: v.name,
        expend: v.expend,
        path: v.path,
        subTitle: v.subTitle,
        params: {
          ...v,
        },
      }));
    },
    setPreItem() {
      this.filterKeyword = undefined
      let arr = [...this.selectedKeys];
      let pre = arr.pop();
      this.selectedKeys = arr;
      delete this.originalBases[pre!];
      if (arr?.length == 1) {
        this.rootKey = 0;
      }
      if (this.cFileVisible) {
        this.highlightedIndex = 0;
      } else {
        this.highlightedIndex = -1;
      }
    },
    addDoc() {
      this.addDocVisible = true;
      this.filesVisible = false;
      this.selectedKeys = [];
      this.rootKey = 0;
      this.originalBases = {};
      this.close();
    },
    async handleDelete(item: IFileItem) {
      await util.filesBases({
        deleteDoc: 1,
        knowledgeId: item.params.baseId,
        fileId: item.params.value,
        groupId: this.rootKey,
      });

      this.filesVisible = false;
      this.selectedKeys = [];
      this.originalBases = {};
      this.rootKey = 0;
      this.close();
    },
    setSuggestion(suggestion: string) {
      // this.showFormParams = true
      this.activeCommond = suggestion;
      this.knowledgeBasesValue = [];
      this.close();
    },
    setInputActive(onoff: boolean) {
      this.inputActive = onoff;
    },
    async submitAudio(url: string, base64: string) {
      this.onSubmit({
        type: "audio",
        meta: {
          role: "USER",
        },
        author: {
          username: this.user.username,
          cname: this.user.cname,
          avatar: this.user.avatar,
        },
        data: {
          composer_config: this.composerList,
          text: url,
          content: base64,
          ...this.getFunctionConfig(),
        },
      });
    },
    updateText(text: string) {
      console.log("updateText", text);
      this.text = "" + text;
      this.showFormParams = false;
    },
    async submitText() {
      this.handleKeyEscape()
      const activeCommond = this.activeCommond;
      const activeCommondLabel = this.activeCommondLabel[0];
      const activeKnowledgeBasesLabel = this.activeKnowledgeBasesLabel.join();
      let knowledgeBasesValue = this.knowledgeBasesValue
        .map((v) => v.value)
        .join(",");
      const text = this.text;
      if (
        this.edit.isEdit &&
        ((text && text.length > 0) ||
          this.knowledgeBasesValue ||
          this.activeCommond)
      ) {
        if (text === "?clear" || activeCommond == "clear") {
          //清除
          this.deleteMsg();
        } else if (text === "?state_ask") {
          this.askState();
        } else if (text.startsWith("?state_fullback=")) {
          const ss = text.split("=");
          this.setStateFullback(ss[1]);
        } else if (
          (this.images && this.images.length > 0) ||
          (this.screenshotImages && this.screenshotImages.length > 0) ||
          (this.files && this.files.length > 0)
        ) {
          const image = this.images[0] || this.screenshotImages[0];
          this.onSubmit({
            type: "image",
            meta: {
              role: "USER",
            },
            author: {
              username: this.user.username,
              cname: this.user.cname,
              avatar: this.user.avatar,
            },
            data: {
              composer_config: this.composerList,
              text: image?.url,
              content: text,
              files: this.files,
              ...this.getFunctionConfig(),
            },
          });
          await util.myVision({
            mediaType: image?.mediaType,
            input: image?.input,
            postscript: text,
          });
          this.images = [];
          this.screenshotImages = [];
          this.files = [];
          // this.composerList = []
        } else {
          let code = text;
          // code = await util.modifyPrompt(text);
          let _p = {};
          if (this.rootKey == TYPE_LIST.floder) {
            _p = {
              path: this.knowledgeBasesValue
                .map((v) => v.params.path)
                .join(","),
            };
          } else if (this.rootKey == TYPE_LIST.doc) {
            knowledgeBasesValue = this.knowledgeBasesValue[0].params.baseId;
            _p = {
              fileId: this.knowledgeBasesValue[0].params.value,
            };
          } else if (this.rootKey == TYPE_LIST.project) {
            knowledgeBasesValue = `${this.rootKey}`;
          } else if (this.rootKey == TYPE_LIST.git) {
            _p = {
              commitId: this.knowledgeBasesValue[0].params.id,
            };
          }
          if (this.rootKey == TYPE_LIST.file && this.knowledgeBasesValue.length) {
            let str = "";
            this.knowledgeBasesValue.forEach(v => {
              str += `@${v.label}`
            })
            code = `${str}  ${code}`
          }
          try {
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
                composer_config: this.composerList,
                text: code,
                cmd: activeCommond
                  ? {
                    id: this.activeCommondObj?.id,
                    label: activeCommondLabel,
                    value: this.activeCommondObj?.cmd,
                    params: this.activeCommondObj?.params,
                  }
                  : undefined,
                knowledgeBase:
                  (this.rootKey == TYPE_LIST.floder || knowledgeBasesValue) && this.rootKey != TYPE_LIST.file
                    ? {
                      label: activeKnowledgeBasesLabel,
                      value:
                        this.rootKey == TYPE_LIST.floder
                          ? ""
                          : knowledgeBasesValue,
                      params: {
                        // groupId: this.rootKey,
                        ..._p,
                      },
                    }
                    : undefined,
                ...this.getFunctionConfig(),
              },
            });
          } catch (e) {
            // ElMessage.error("处理失败");
            console.error("onSubmit", e);
          }
        }
        this.text = "";
        this.activeCommond = "";
        // this.knowledgeBasesValue = [];
        // this.composerList = [];
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
          text: "",
          cmd: {
            label: "/清空会话",
            value: "clear",
          },
          knowledgeBase: undefined,
          ...this.getFunctionConfig(),
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
  background: rgba(20, 20, 50, 0.5);
  border-top: 1px solid rgba(100, 100, 255, 0.2);
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
  display: flex;
  align-items: center;
  justify-content: center;

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
  color: #fff;

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
  padding: 12px 15px;
  background: rgba(10, 10, 30, 0.7);
  border: 1px solid rgba(100, 100, 255, 0.3);
  border-radius: 25px;
  color: #e0e0e0;
  font-size: 16px;
  outline: none;
  transition: all 0.3s;
}

.sc-user-input--text:focus {
  border-color: #00ffff;
  box-shadow: 0 0 15px rgba(0, 255, 255, 0.3);
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
  // background-image: linear-gradient(to bottom, #b2c8a8, #9454a0);
  border-radius: 10px;

  .sc-user-input-body {
    background-color: rgba(0, 0, 0, 0.5);
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

  color: #fff;

  left: 5px;
  right: 5px;
  bottom: 120px;

  &-list {
    padding: 10px;
    background-color: rgb(38, 38, 38);
    border-top-left-radius: 10px;
    border-top-right-radius: 10px;
    max-height: 60vh;
    overflow-y: auto;

    &.files {
      padding-top: 43px;
    }

    &::-webkit-scrollbar {
      display: none;
    }
  }

  &-file {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;

    &-item {
      display: inline-block;
      margin: 0 4px;

      &-subTitle {
        font-style: normal;
        color: #aaaaaa;
        font-size: 14px;
      }
    }

    &-left {
      display: flex;
      align-items: center;
      justify-content: flex-start;
      flex: 1;
    }

    &-right {
      display: flex;
      align-items: center;
      justify-content: flex-end;
      width: 80px;

      span {
        cursor: pointer;
      }
    }

    &-add {
      font-size: 14px;
    }
  }

  &-item {
    cursor: pointer;
    padding: 5px;

    &.pre {
      position: absolute;
      top: 0;
      width: calc(100% - 20px);
      z-index: 1;
      padding: 5px 0 0 0;
      height: 38px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-size: 12px;
      background-color: rgb(38, 38, 38);

      &:hover {
        background-color: inherit;
      }

      .el-icon {
        &:hover {
          color: aqua;
        }
      }

      &.active {
        background-color: inherit;
      }

      .el-input {
        flex: 1;
        margin: 0 12px;
      }
    }

    &:hover {
      background-color: rgb(106, 106, 106);
    }

    &.active {
      background-color: cornflowerblue;
    }
  }
}

.file-list {
  padding: 0 20px;
  position: relative;
  min-height: 24px;

  .add-file {
    position: absolute;
    left: 20px;
    top: 0;
    width: 50px;
    color: #aaaaaa;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: flex-start;

    &:hover {
      color: aqua;
    }

    .el-icon {
      margin-right: 4px;
    }
  }

  ul {
    display: flex;
    align-items: flex-start;
    justify-content: flex-start;
    flex-wrap: wrap;
    padding: 0;

    li {
      list-style: none;
      color: #fff;
      background-color: #565867;
      padding: 0 6px;
      border-radius: 5px;
      font-size: 12px;
      display: flex;
      align-items: center;
      justify-content: flex-start;
      margin: 4px 5px 0 0;

      .el-icon {
        margin-left: 4px;
        cursor: pointer;

        &:hover {
          color: aqua;
        }
      }
      &:first-child {
        margin-left: 50px;
      }
    }
  }
}

.composer_config {
  border-top: 1px solid #545454;
  background-color: rgb(38, 38, 38);
  border-bottom-right-radius: 10px;
  border-bottom-left-radius: 10px;
  padding: 10px;
}

.function-panel {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 8px 15px;
  background: rgba(20, 20, 50, 0.3);
  border-bottom: 1px solid rgba(100, 100, 255, 0.2);
  border-radius: 10px 10px 0 0;

  .function-item {
    display: flex;
    align-items: center;
    margin: 0 5px;

    .el-switch {
      --el-switch-on-color: #00ffff;
      --el-switch-off-color: #565867;

      :deep(.el-switch__label) {
        color: #e0e0e0;
        font-size: 12px;
      }

      :deep(.el-switch__label--left) {
        margin-right: 8px;
      }

      :deep(.el-switch__label--right) {
        margin-left: 8px;
      }
    }
  }
}

.function-toggle {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 过渡动画 */
.panel-slide-enter-active,
.panel-slide-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.panel-slide-enter-from {
  height: 0;
  opacity: 0;
  transform: translateY(-10px);
}

.panel-slide-leave-to {
  height: 0;
  opacity: 0;
  transform: translateY(-10px);
}

.panel-slide-enter-to,
.panel-slide-leave-from {
  height: auto;
  opacity: 1;
  transform: translateY(0);
}
</style>
