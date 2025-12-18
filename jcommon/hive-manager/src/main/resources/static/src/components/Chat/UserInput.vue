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
    <div v-if="suggestionVisible" class="sc-user-input--commonds command-suggestions">
      <div class="sc-user-input--commonds-list">
        <div v-for="(item, index) of suggestions"
             @click="selectSuggestion(item.value)"
             :class="`sc-user-input--commonds-item command-item${index === highlightedIndex ? ' active' : ''}`"
             :key="item.value">
          <div class="command-main">
            <span class="command-slash">/</span><span class="command-name">{{ item.label.slice(1) }}</span>
          </div>
          <div class="command-description">
            {{ item.params?.description || '' }}
          </div>
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

<script setup lang="ts">
import { ref, computed, watch, nextTick, type PropType } from 'vue';
import { storeToRefs } from 'pinia';
import util from "@/libs/util";
import { useUserStore } from "@/stores/user";
import { useEditStore } from "@/stores/edit";
import { useChatContextStore, legalMaxNums } from "@/stores/chat-context";
import { useIdeaInfoStore } from "@/stores/idea-info";
import { useFunctionPanelStore } from "@/stores/function-panel";
import Recoder from "@/components/recorder/index.vue";
import UserInputButton from "./UserInputButton.vue";
import IconSend from "./components/icons/IconSend.vue";
import ImageUpload from "./components/image-upload/index.vue";
import Screenshot from "./components/screenshot/index.vue";
import PasteImage from "./components/paste-image/index.vue";
import FormParams from "./components/form-params/index.vue";
import AutoCompleteInput from "./AutoCompleteInput.vue";
import { ElMessage } from "element-plus";
import AddDoc from "./components/add-doc/index.vue";
import KnowledgeIcon from "./components/knowledge-icon/index.vue";
import { vClickOutside } from '@/plugins/click-outside';

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

interface ISuggestion {
  id: string;
  label: string;
  value: string;
  cmd: string;
  params: any;
}

interface IFormUi {
  labelWidth: number;
  listUi: Array<{
    label: string;
    value: string;
    type: 'input';
  }>;
}

interface IMediaItem {
  mediaType: string;
  url: string;
  input: string;
}

const TYPE_LIST = {
  project: -1,
  floder: -2,
  file: -3,
  git: -4,
  doc: -5,
  miapi: -6,
} as const;

// Props
const props = defineProps({
  onSubmit: {
    type: Function as PropType<(data: any) => Promise<void>>,
    required: true,
  },
  placeholder: {
    type: String,
    default: "Shift + Enter = 发送",
  },
  initCodePrompt: {
    type: Function as PropType<() => void>,
    required: true,
  },
  changeSendMethod: {
    type: Function as PropType<(val: string) => void>,
    required: true,
  },
});

// Stores
const userStore = useUserStore();
const editStore = useEditStore();
const chatContextStore = useChatContextStore();
const ideaInfoStore = useIdeaInfoStore();
const functionPanelStore = useFunctionPanelStore();

const { user } = storeToRefs(userStore);
const { edit } = storeToRefs(editStore);
const { chatContext, knowledgeLoading, knowledgeData } = storeToRefs(chatContextStore);
const { vision, showFileMenu } = storeToRefs(ideaInfoStore);
const { webSearchEnabled, ragEnabled, showFunctionPanel } = storeToRefs(functionPanelStore);

const { disableContext, enableContext, setMaxNum, setKnowledgeLoading } = chatContextStore;

// Refs
const autoCompleteInput = ref<InstanceType<typeof AutoCompleteInput>>();
const isEnter = ref(localStorage.getItem("isEnter") === "true");
const text = ref("");
const inputActive = ref(false);
const percentage = ref(0);
const bizUpdate = ref(false);
const isSelectingSuggestion = ref(false);
const screenshotImages = ref<IMediaItem[]>([]);
const files = ref<IMediaItem[]>([]);
const images = ref<IMediaItem[]>([]);
const formUi = ref<IFormUi>({
  labelWidth: 80,
  listUi: [
    {
      label: "你好",
      value: "text",
      type: "input" as const,
    },
  ],
});
const showFormParams = ref(false);
const maxNums = ref(legalMaxNums);
const activeCommond = ref("");
const commandVisible = ref(false);
const knowledgeBasesValue = ref<IFileItem[]>([]);
const filesVisible = ref(false);
const addDocVisible = ref(false);
const currentSelectItem = ref<IFileItem>({} as IFileItem);
const rootKey = ref(0);
const filterKeyword = ref<string | undefined>(undefined);
const selectedKeys = ref<string[]>([]);
const allSuggestions = ref<ISuggestion[]>([]);
const suggestions = ref<ISuggestion[]>([]);
const originalBases = ref<{ [propName: string]: IFileItem[] }>({});
const originalList = ref<IItems[]>([]);
const highlightedIndex = ref(0);
const gitTimer = ref(0);
const composerConfig = ref(['bugfix', 'bizJar', 'Codebase', 'Analyze', 'Knowledge', 'UnitTest']);
const composerList = ref<string[]>([]);
const sendMethod = ref('ws');

// Computed
const cmds = computed(() => {
  if (activeCommondLabel.value?.length) {
    return activeCommondLabel.value;
  }
  return [];
});

const iconSendColor = computed(() => {
  const textVal = (text.value || "").trim();
  return textVal && edit.value.isEdit ? "rgba(255, 255, 255, 0.8)" : "#565867";
});

const iconContextColor = computed(() => {
  const isAllow = chatContext.value.isAllow;
  return isAllow ? "#67C23A" : "#F56C6C";
});

const activeCommondLabel = computed(() => {
  const cmd = allSuggestions.value.find(
    (it) => it.value === activeCommond.value
  );
  return cmd ? [cmd.label] : [];
});

const activeCommondObj = computed(() => {
  const cmd = allSuggestions.value.find(
    (it) => it.value === activeCommond.value
  );
  return cmd ? cmd : null;
});

const activeKnowledgeBasesLabel = computed(() => {
  return knowledgeBasesValue.value?.length
    ? knowledgeBasesValue.value.map((v) => `@${v.label}`)
    : [];
});

const cFileVisible = computed(() => {
  return filesVisible.value && cFileList.value?.length > 0;
});

const suggestionVisible = computed(() => {
  return commandVisible.value && suggestions.value.length > 0;
});

const cFileList = computed(() => {
  if (selectedKeys.value?.length) {
    return (
      originalBases.value[selectedKeys.value[selectedKeys.value.length - 1]] ||
      []
    );
  }
  return [];
});

const activeFunctionCount = computed(() => {
  return functionPanelStore.getActiveFunctionCount();
});

const webSearchModel = computed({
  get() {
    return webSearchEnabled.value;
  },
  set(value: boolean) {
    functionPanelStore.setWebSearchEnabled(value);
  }
});

const ragModel = computed({
  get() {
    return ragEnabled.value;
  },
  set(value: boolean) {
    functionPanelStore.setRagEnabled(value);
  }
});

// Watchers
watch(images, (newValue) => {
  if (newValue.length > 0 && screenshotImages.value.length > 0) {
    screenshotImages.value = [];
  }
});

watch(screenshotImages, (newValue) => {
  if (images.value.length > 0 && newValue.length > 0) {
    images.value = [];
  }
});

watch(text, (newValue, oldValue) => {
  if (newValue?.trim() === "@") {
    toggleKnowledgeBase(true);
  } else if (newValue !== oldValue) {
    if (!isSelectingSuggestion.value) {
      querySuggestion(newValue);
    }
    if (!newValue) {
      // activeCommond.value = "";
    }
  }
});

watch(knowledgeLoading, (newValue) => {
  if (!newValue) {
    clearTimeout(gitTimer.value);
  }
});

watch(knowledgeData, (newValue) => {
  if (rootKey.value !== TYPE_LIST.file && !cFileVisible.value) {
    handleInitKnowledge(newValue);
    highlightedIndex.value = 0;
    //@ts-ignore
    window.setUserCode?.("");
  } else if (cFileVisible.value && knowledgeLoading.value) {
    handleKnowledge(currentSelectItem.value, newValue);
    //@ts-ignore
    window.setUserCode?.("");
    highlightedIndex.value = 0;
  }
  setKnowledgeLoading(false);
});

watch(rootKey, (val, old) => {
  if (val === TYPE_LIST.file) {
    knowledgeBasesValue.value = [];
  }
});

watch(cFileVisible, (val) => {
  if (!val) {
    filterKeyword.value = undefined;
  }
});

// Methods
const handleClickOutside = () => {
  if (filesVisible.value) {
    handleKeyEscape();
  }
};

const handleDeleteFile = (item: IFileItem) => {
  if (item.path) {
    knowledgeBasesValue.value = knowledgeBasesValue.value.filter(v => v.path !== item.path);
  } else if (rootKey.value === TYPE_LIST.miapi) {
    knowledgeBasesValue.value = [];
  } else if (rootKey.value === TYPE_LIST.git) {
    knowledgeBasesValue.value = knowledgeBasesValue.value.filter(v => v.id !== item.id);
  }
};

const searchFiles = (newValue: string) => {
  let search = (newValue || "").trim();
  if (newValue?.indexOf("@") !== -1) {
    search = newValue.split("@").pop() || "";
  }
  let key = selectedKeys.value[selectedKeys.value.length - 1];
  if (!search) {
    if (originalList.value.some((v) => v.recentOpen)) {
      originalBases.value[key] = originalList.value
        .filter((v) => v.recentOpen)
        .map((v) => ({ ...handleFileItem(v) }))
        .map((v: any, i: number) => ({
          ...handleFileItem(v, true, i),
        }));
    } else {
      originalBases.value[key] = filterJavaFile(originalList.value)
        .map((v) => ({ ...handleFileItem(v) }))
        .map((v: any, i: number) => ({
          ...handleFileItem(v, true, i),
        }));
    }
  } else {
    let arr = originalList.value
      .map((v) => ({ ...handleFileItem(v) }))
      .filter(
        (v) =>
          v.fileName.toLowerCase().indexOf(search.toLowerCase()) !== -1
      );
    if (arr?.length) {
      originalBases.value[key] = arr.map((v: any, i: number) => ({
        ...handleFileItem(v, true, i),
      }));
    }
  }
};

const toggleSendShiftEnter = () => {
  if (isEnter.value) {
    localStorage.setItem("isEnter", "false");
  } else {
    localStorage.setItem("isEnter", "true");
  }
  isEnter.value = !isEnter.value;
};

const toggleSendMethod = (val: string) => {
  props.changeSendMethod(val);
};

const toggleFunctionPanel = () => {
  functionPanelStore.toggleFunctionPanel();
};

const getFunctionConfig = () => {
  return {
    webSearch: webSearchEnabled.value,
    rag: ragEnabled.value,
  };
};

const filterJavaFile = (list: IItems[]) => {
  if (rootKey.value === TYPE_LIST.file) {
    return list.filter((v) => v.fileName.endsWith(".java"));
  }
  return list;
};

const handleFileItem = (v: any, bool?: boolean, i?: number): IFileItem | any => {
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
};

const handleKnowLedgeItem = (it: {
  id: string;
  name: string;
  auth: number;
  expend: boolean;
  path: string;
  groupId: string;
}): IFileItem => {
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
};

const querySuggestion = (textVal: string) => {
  if (textVal.startsWith("/")) {
    suggestions.value = allSuggestions.value
      .filter((it) => it.label.startsWith(textVal))
      .map((it) => {
        return it;
      });
    commandVisible.value = true;
    highlightedIndex.value = suggestions.value.length > 0 ? 0 : -1;
  } else {
    commandVisible.value = false;
    suggestions.value = [];
    highlightedIndex.value = -1;
  }
};

const highlight = (index: number) => {
  if (index < 0) {
    highlightedIndex.value = -1;
    return;
  }
  if (suggestionVisible.value) {
    const suggestionsList = suggestions.value;
    if (index >= suggestionsList.length) {
      index = suggestionsList.length - 1;
    }
    highlightedIndex.value = index;
  } else if (cFileVisible.value) {
    const suggestionsList = cFileList.value;
    if (index >= suggestionsList.length) {
      index = suggestionsList.length - 1;
    }
    highlightedIndex.value = index;
  }
};

const handleKeyEscape = (evt?: Event) => {
  if (suggestionVisible.value) {
    evt?.preventDefault();
    evt?.stopPropagation();
    close();
  } else if (cFileVisible.value) {
    evt?.preventDefault();
    evt?.stopPropagation();
    highlightedIndex.value = -1;
    commandVisible.value = false;
    currentSelectItem.value = {} as IFileItem;
    filesVisible.value = false;
    close();
  }
};

const handleKeyDown = (event: KeyboardEvent) => {
  const suggestionsList = suggestions.value;
  const highlightedIdx = highlightedIndex.value;
  const suggestionVis = suggestionVisible.value;
  
  if (event.key === "Delete" || event.key === "Backspace") {
    if (
      text.value === "" &&
      (activeCommond.value || knowledgeBasesValue.value?.length)
    ) {
      event.preventDefault();
      event.stopPropagation();
      activeCommond.value = "";
      if (knowledgeBasesValue.value?.length) {
        knowledgeBasesValue.value.pop();
      }
      close();
    }
  } else if (
    (event.key === "Enter" && !event.shiftKey && !isEnter.value) ||
    (isEnter.value && event.key === "Enter" && event.shiftKey)
  ) {
    if (suggestionVis && suggestionsList[highlightedIdx]) {
      event.preventDefault();
      event.stopPropagation();
      selectSuggestion(suggestionsList[highlightedIdx].value);
    } else if (cFileVisible.value && cFileList.value) {
      event.preventDefault();
      event.stopPropagation();
      setFileItem(cFileList.value[highlightedIdx]);
      close();
    }
  } else if (
    (event.key === "Enter" && event.shiftKey && !isEnter.value) ||
    (event.key === "Enter" && !event.shiftKey && isEnter.value)
  ) {
    if (text.value.trim() !== "" || images.value.length > 0 || screenshotImages.value.length > 0 || files.value.length > 0) {
      submitText();
    }
    event.preventDefault();
    event.stopPropagation();
    close();
  }
};

const close = () => {
  text.value = "";
  suggestions.value = [];
  highlightedIndex.value = -1;
  currentSelectItem.value = {} as IFileItem;
  autoCompleteInput.value?.cleanTextContent();
};

const closeSuggestions = () => {
  suggestions.value = [];
  commandVisible.value = false;
  highlightedIndex.value = -1;
};

const sendText = () => {
  submitText();
  close();
};

const toggleKnowledgeBase = async (isWatch: boolean) => {
  highlightedIndex.value = -1;
  commandVisible.value = false;
  currentSelectItem.value = {} as IFileItem;
  if (!isWatch) {
    if (filesVisible.value) {
      filesVisible.value = false;
      if (rootKey.value !== TYPE_LIST.file) {
        originalBases.value = {};
        selectedKeys.value = [];
      }
      return;
    }
  }
  if (!rootKey.value || rootKey.value !== TYPE_LIST.file) {
    originalBases.value = {};
    originalList.value = [];
    selectedKeys.value = [];
    rootKey.value = 0;
    util.fetchKnowledgeBases();
    clearTimeout(gitTimer.value);
    setKnowledgeLoading(true);
    gitTimer.value = window.setTimeout(() => {
      setKnowledgeLoading(false);
    }, 5000);
  } else {
    filesVisible.value = true;
    highlightedIndex.value = 0;
  }
};

const handleInitKnowledge = (jRes: any) => {
  console.log("fetchKnowledgeBases:", jRes);
  if (
    jRes.code !== 0 ||
    (Array.isArray(jRes.data) && jRes.data.length === 0)
  ) {
    ElMessage.error("请先绑定知识库");
    return;
  }
  let arr = jRes.data.map((it: any) => {
    return handleKnowLedgeItem(it);
  });
  originalBases.value = {
    root: [...arr],
  };
  selectedKeys.value = ["root"];
  filesVisible.value = true;
};

const toggleCommond = async () => {
  const res = await util.fetchCommonds();
  if (Array.isArray(res)) {
    allSuggestions.value = res.map((it: any) => {
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
  highlightedIndex.value = -1;
  filesVisible.value = false;
  selectedKeys.value = [];
  originalBases.value = {};
  suggestions.value = [...allSuggestions.value];
  commandVisible.value = !commandVisible.value;
};

const handleSelectKnowItem = (item: IFileItem) => {
  if (item.params.value === "noDoc") {
    return;
  }
  if (rootKey.value === TYPE_LIST.file) {
    if (knowledgeBasesValue.value.some(v => v.path === item.path)) {
      knowledgeBasesValue.value = knowledgeBasesValue.value.filter(v => v.path !== item.path);
    } else {
      knowledgeBasesValue.value.push(item);
    }
  } else {
    knowledgeBasesValue.value = [item];
    originalBases.value = {};
    selectedKeys.value = [];
  }

  if (rootKey.value !== TYPE_LIST.file) {
    filesVisible.value = false;
    close();
  }
};

const setFileItem = async (item: IFileItem) => {
  currentSelectItem.value = item;
  activeCommond.value = "";
  
  if (selectedKeys.value?.length === 1) {
    rootKey.value = item.id as number;
  }
  
  if (!item.expend) {
    handleSelectKnowItem(item);
    return;
  } else if (rootKey.value !== TYPE_LIST.file) {
    knowledgeBasesValue.value = [];
  }
  
  let param: any = {};
  if (rootKey.value === TYPE_LIST.file || rootKey.value === TYPE_LIST.floder) {
    param = {
      groupId: item.params.id || item.params.groupId,
      path: item.params.path,
    };
  } else if (rootKey.value === TYPE_LIST.doc) {
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
  clearTimeout(gitTimer.value);
  setKnowledgeLoading(true);
  gitTimer.value = window.setTimeout(() => {
    setKnowledgeLoading(false);
  }, 5000);
};

const handleKnowledge = (item: IFileItem, list: any) => {
  if (rootKey.value === TYPE_LIST.doc) {
    if (list?.length === 0) {
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
  } else if (rootKey.value === TYPE_LIST.git) {
    list = list.map((v: any) => ({
      name: v.title,
      value: v.id,
      expend: false,
      subTitle: v.committerName,
      ...v,
    }));
  } else if (
    rootKey.value === TYPE_LIST.file ||
    rootKey.value === TYPE_LIST.floder
  ) {
    originalList.value = [...list].filter((v: IItems) =>
      rootKey.value === TYPE_LIST.file ? !v.dir : v.dir
    );
    if (originalList.value.some((v) => v.recentOpen)) {
      list = originalList.value
        .filter((v) => v.recentOpen)
        .map((v: any) => handleFileItem(v));
    } else {
      list = filterJavaFile(originalList.value).map((v: any) =>
        handleFileItem(v)
      );
    }
  }
  if (!list?.length) {
    filesVisible.value = false;
    selectedKeys.value = [];
    return;
  }
  let key = `${item.id}&${item.label}`;
  selectedKeys.value.push(key);
  originalBases.value[key] = list.map((v: any, i: number) => ({
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
};

const setPreItem = () => {
  filterKeyword.value = undefined;
  let arr = [...selectedKeys.value];
  let pre = arr.pop();
  selectedKeys.value = arr;
  delete originalBases.value[pre!];
  if (arr?.length === 1) {
    rootKey.value = 0;
  }
  if (cFileVisible.value) {
    highlightedIndex.value = 0;
  } else {
    highlightedIndex.value = -1;
  }
};

const addDoc = () => {
  addDocVisible.value = true;
  filesVisible.value = false;
  selectedKeys.value = [];
  rootKey.value = 0;
  originalBases.value = {};
  close();
};

const handleDelete = async (item: IFileItem) => {
  await util.filesBases({
    deleteDoc: 1,
    knowledgeId: item.params.baseId,
    fileId: item.params.value,
    groupId: rootKey.value,
  });

  filesVisible.value = false;
  selectedKeys.value = [];
  originalBases.value = {};
  rootKey.value = 0;
  close();
};

const setSuggestion = (suggestion: string) => {
  text.value = suggestion;
  knowledgeBasesValue.value = [];
  nextTick(() => {
    autoCompleteInput.value?.setTextContent(suggestion);
    autoCompleteInput.value?.focus();
  });
};

const selectSuggestion = (suggestion: string) => {
  isSelectingSuggestion.value = true;
  setSuggestion(suggestion);
  closeSuggestions();
  nextTick(() => {
    isSelectingSuggestion.value = false;
  });
};

const setInputActive = (onoff: boolean) => {
  inputActive.value = onoff;
};

const submitAudio = async (url: string, base64: string) => {
  props.onSubmit({
    type: "audio",
    meta: {
      role: "USER",
    },
    author: {
      username: user.value.username,
      cname: user.value.cname,
      avatar: user.value.avatar,
    },
    data: {
      composer_config: composerList.value,
      text: url,
      content: base64,
      ...getFunctionConfig(),
    },
  });
};

const updateText = (textVal: string) => {
  console.log("updateText", textVal);
  text.value = "" + textVal;
  showFormParams.value = false;
};

const submitText = async () => {
  handleKeyEscape();
  const activeCommondVal = activeCommond.value;
  const activeCommondLabelVal = activeCommondLabel.value[0];
  const activeKnowledgeBasesLabelVal = activeKnowledgeBasesLabel.value.join();
  let knowledgeBasesVal = knowledgeBasesValue.value
    .map((v) => v.value)
    .join(",");
  const textVal = text.value;
  
  if (
    edit.value.isEdit &&
    ((textVal && textVal.length > 0) ||
      knowledgeBasesValue.value ||
      activeCommond.value)
  ) {
    if (textVal === "?clear" || activeCommondVal === "clear") {
      deleteMsg();
    } else if (textVal === "?state_ask") {
      askState();
    } else if (textVal.startsWith("?state_fullback=")) {
      const ss = textVal.split("=");
      setStateFullback(ss[1]);
    } else if (
      (images.value && images.value.length > 0) ||
      (screenshotImages.value && screenshotImages.value.length > 0) ||
      (files.value && files.value.length > 0)
    ) {
      const image = images.value[0] || screenshotImages.value[0];
      props.onSubmit({
        type: "image",
        meta: {
          role: "USER",
        },
        author: {
          username: user.value.username,
          cname: user.value.cname,
          avatar: user.value.avatar,
        },
        data: {
          composer_config: composerList.value,
          text: image?.url,
          content: textVal,
          files: files.value,
          ...getFunctionConfig(),
        },
      });
      await util.myVision({
        mediaType: image?.mediaType,
        input: image?.input,
        postscript: textVal,
      });
      images.value = [];
      screenshotImages.value = [];
      files.value = [];
    } else {
      let code = textVal;
      let _p: any = {};
      if (rootKey.value === TYPE_LIST.floder) {
        _p = {
          path: knowledgeBasesValue.value
            .map((v) => v.params.path)
            .join(","),
        };
      } else if (rootKey.value === TYPE_LIST.doc) {
        knowledgeBasesVal = knowledgeBasesValue.value[0].params.baseId;
        _p = {
          fileId: knowledgeBasesValue.value[0].params.value,
        };
      } else if (rootKey.value === TYPE_LIST.project) {
        knowledgeBasesVal = `${rootKey.value}`;
      } else if (rootKey.value === TYPE_LIST.git) {
        _p = {
          commitId: knowledgeBasesValue.value[0].params.id,
        };
      }
      if (rootKey.value === TYPE_LIST.file && knowledgeBasesValue.value.length) {
        let str = "";
        knowledgeBasesValue.value.forEach(v => {
          str += `@${v.label}`;
        });
        code = `${str}  ${code}`;
      }
      try {
        await props.onSubmit({
          type: "md",
          meta: {
            role: code.startsWith("?") ? "IDEA" : "USER",
          },
          author: {
            username: user.value.username,
            cname: user.value.cname,
            avatar: user.value.avatar,
          },
          data: {
            composer_config: composerList.value,
            text: code,
            cmd: activeCommondVal
              ? {
                id: activeCommondObj.value?.id,
                label: activeCommondLabelVal,
                value: activeCommondObj.value?.cmd,
                params: activeCommondObj.value?.params,
              }
              : undefined,
            knowledgeBase:
              (rootKey.value === TYPE_LIST.floder || knowledgeBasesVal) && rootKey.value !== TYPE_LIST.file
                ? {
                  label: activeKnowledgeBasesLabelVal,
                  value:
                    rootKey.value === TYPE_LIST.floder
                      ? ""
                      : knowledgeBasesVal,
                  params: {
                    ..._p,
                  },
                }
                : undefined,
            ...getFunctionConfig(),
          },
        });
      } catch (e) {
        console.error("onSubmit", e);
      }
    }
    text.value = "";
    activeCommond.value = "";
  }
};

const deleteMsg = async () => {
  await props.onSubmit({
    type: "md",
    meta: {
      role: "IDEA",
    },
    author: {
      username: user.value.username,
      cname: user.value.cname,
      avatar: user.value.avatar,
    },
    data: {
      text: "",
      cmd: {
        label: "/清空会话",
        value: "clear",
      },
      knowledgeBase: undefined,
      ...getFunctionConfig(),
    },
  });
};

const askState = async () => {
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
};

const setStateFullback = async (index: string | undefined) => {
  if (!index) {
    //@ts-ignore
    ElMessage.error("回退的index不能为空");
    return;
  }
  try {
    const res = await util.setStateFullback(index);
    //@ts-ignore
    ElMessage.success(res);
  } catch (e) {
    //@ts-ignore
    ElMessage.error("执行失败");
    console.error(e);
  }
};

const setContextMaxNum = (num: number) => {
  setMaxNum(num);
};

const toggleContext = () => {
  const isAllow = chatContext.value.isAllow;
  if (isAllow) {
    disableContext();
    //@ts-ignore
    ElMessage.warning("当前模式下, 发送消息不会携带之前的聊天记录");
  } else {
    enableContext();
    //@ts-ignore
    ElMessage.success("当前模式下, 发送消息会携带之前的聊天记录");
  }
};

const getPercentage = async (target: any) => {
  try {
    const { msg } = await commond("embedding_status");
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
      percentage.value = Number(
        ((data.successCnt / data.total) * 100).toFixed(2)
      );
    }
    setTimeout(() => {
      getPercentage(target);
    }, 300);
  } catch (e) {
    target.bizUpdate = false;
    console.error(e);
  }
};

const commond = async (cmd: string) => {
  return util.getAiGuide(cmd);
};

const flushBiz = async () => {
  let err = false;
  try {
    bizUpdate.value = true;
    const { msg } = await commond("flush_biz");
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
    bizUpdate.value = false;
  }
  if (!err) {
    await getPercentage({ bizUpdate });
  }
};

// Initialize
allSuggestions.value = [
  {
    id: 'init',
    label: '/init',
    value: '/init',
    cmd: '/init',
    params: {
      description: '分析代码库并创建MCODE.md文件'
    }
  },
  {
    id: 'clear',
    label: '/clear',
    value: '/clear',
    cmd: '/clear',
    params: {
      description: '清空对话历史记录'
    }
  },
  {
    id: 'cancel',
    label: '/cancel',
    value: '/cancel',
    cmd: '/cancel',
    params: {
      description: '取消当前正在执行的任务'
    }
  },
  {
    id: 'config',
    label: '/config',
    value: '/config',
    cmd: '/config',
    params: {
      description: '查看或修改配置'
    }
  },
  {
    id: 'interrupt',
    label: '/interrupt',
    value: '/interrupt',
    cmd: '/interrupt',
    params: {
      description: '中断Agent执行'
    }
  },
  {
    id: 'refresh',
    label: '/refresh',
    value: '/refresh',
    cmd: '/refresh',
    params: {
      description: '刷新配置'
    }
  },
  {
    id: 'switch',
    label: '/switch',
    value: '/switch',
    cmd: '/switch',
    params: {
      description: '切换Agent'
    }
  },
  {
    id: 'create',
    label: '/create',
    value: '/create',
    cmd: '/create',
    params: {
      description: '创建新的Agent'
    }
  }
];

// Expose methods for template refs
defineExpose({
  handleClickOutside,
  handleDeleteFile,
  searchFiles,
  toggleSendShiftEnter,
  toggleSendMethod,
  toggleFunctionPanel,
  getFunctionConfig,
  filterJavaFile,
  handleFileItem,
  handleKnowLedgeItem,
  querySuggestion,
  highlight,
  handleKeyEscape,
  handleKeyDown,
  close,
  closeSuggestions,
  sendText,
  toggleKnowledgeBase,
  handleInitKnowledge,
  toggleCommond,
  handleSelectKnowItem,
  setFileItem,
  handleKnowledge,
  setPreItem,
  addDoc,
  handleDelete,
  setSuggestion,
  selectSuggestion,
  setInputActive,
  submitAudio,
  updateText,
  submitText,
  deleteMsg,
  askState,
  setStateFullback,
  setContextMaxNum,
  toggleContext,
  getPercentage,
  commond,
  flushBiz,
});
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

  // Claude Code 风格的命令建议样式
  &.command-suggestions {
    .sc-user-input--commonds-list {
      padding: 6px;
      background: rgba(30, 30, 40, 0.95);
      backdrop-filter: blur(10px);
      border: 1px solid rgba(100, 100, 255, 0.2);
      border-radius: 8px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    }

    .command-item {
      padding: 6px 12px;
      border-radius: 6px;
      margin-bottom: 2px;
      transition: all 0.15s ease;
      display: flex;
      flex-direction: row;
      align-items: center;
      justify-content: flex-start;

      .command-main {
        display: flex;
        align-items: center;
        font-size: 13px;
        font-weight: 500;
        min-width: 120px; // 预留约15个字符的空间，足够容纳最长的命令
        flex-shrink: 0;

        .command-slash {
          color: rgba(0, 255, 255, 0.6);
          font-weight: 600;
          margin-right: 2px;
        }

        .command-name {
          color: rgba(255, 255, 255, 0.85);
        }
      }

      .command-description {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.45);
        line-height: 1.4;
        margin-left: 12px; // 减小间距，让命令和描述更紧凑
        flex: 1;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      &:hover {
        background: rgba(60, 60, 80, 0.5);

        .command-slash {
          color: rgba(0, 255, 255, 0.9);
        }

        .command-name {
          color: rgba(255, 255, 255, 1);
        }

        .command-description {
          color: rgba(255, 255, 255, 0.6);
        }
      }

      &.active {
        background: rgba(0, 180, 255, 0.2);
        border-left: 2px solid rgba(0, 255, 255, 0.8);

        .command-slash {
          color: rgba(0, 255, 255, 1);
        }

        .command-name {
          color: rgba(255, 255, 255, 1);
        }

        .command-description {
          color: rgba(255, 255, 255, 0.7);
        }
      }

      &:last-child {
        margin-bottom: 0;
      }
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
