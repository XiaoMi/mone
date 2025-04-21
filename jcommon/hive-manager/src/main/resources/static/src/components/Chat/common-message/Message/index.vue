<script setup lang="ts">
import { computed, ref } from "vue";
import { ElMessage } from "element-plus";
import { Refresh, MoreFilled } from "@element-plus/icons-vue";
import AvatarComponent from "./Avatar.vue";
import TextComponent from "./Text.vue";
// import MiPng from "./favicon.png";
// import i18n from '@/lang'
import copyToClip from "@/common/customCopy";
import BaseSounds from "@/components/BaseSounds.vue";
// import FlowData from './FlowData.vue'

// const t = i18n.t

interface TranslateData {
  text?: string;
  from?: string;
  to?: string;
}

interface Props {
  multimodal?: number;
  language?: string;
  type?: string;
  username?: string;
  dateTime?: string;
  text?: string;
  inversion?: boolean;
  avatar?: string;
  avatarUrl?: string;
  error?: boolean;
  loading?: boolean;
  showCursor?: boolean;
  hideMore?: boolean; // 是否隐藏刷新、删除、复制
  textType?: string; // 文本类型 translate类型则展示翻译
  translateData?: TranslateData;
  showOperate?: boolean; // 是否是插件调试页面
  voice: string; // 语音播报配置
  flowData?: object; // 工作流数据
  fontColor?: string;
  name?: string;
}

interface Emit {
  (ev: "regenerate"): void;
  (ev: "delete"): void;
  (ev: "replaceFn", text: string): void;
  (ev: "onTryAgain"): void;
}

const props = defineProps<Props>();

const emit = defineEmits<Emit>();

const textRef = ref<HTMLElement>();

const asRawText = computed(() => props.inversion);

const messageRef = ref<HTMLElement>();

const options = computed(() => {
  const common = [
    {
      label: "复制",
      key: "copyText",
    },
    {
      label: "删除",
      key: "delete",
    },
  ];

  if (!props.inversion) {
    common.unshift({
      label: asRawText.value ? "预览" : "展示",
      key: "toggleRenderType",
    });
  }

  return common;
});

type OptionKey = "copyText" | "delete" | "toggleRenderType";

function handleSelect(key: string) {
  switch (key as OptionKey) {
    case "copyText":
      handleCopy();
      return;
    case "toggleRenderType":
      asRawText.value = !asRawText.value;
      return;
    case "delete":
      emit("delete");
  }
}

function handleRegenerate() {
  messageRef.value?.scrollIntoView();
  emit("regenerate");
}

function replaceFn(text: string) {
  emit("replaceFn", text);
}

async function handleCopy() {
  try {
    await copyToClip(props.text || "");
  } catch {
    ElMessage.error("copyError");
  }
}


defineOptions({
  name: 'CommonMessage'
})
</script>

<template>
  <div ref="messageRef" class="flex w-full mb-6 overflow-hidden" :class="[{ 'flex-row-reverse': inversion }]">
    <div class="flex items-center justify-center flex-shrink-0 h-8 overflow-hidden rounded-full basis-8"
      :class="[inversion ? 'ml-2' : 'mr-2']">
      <AvatarComponent :username="props.username" :avatar="props.avatar" :avatarUrl="!inversion ? props.avatarUrl : ''"
        :image="inversion" v-if="props.type !== 'question'" />
    </div>
    <div class="overflow-hidden text-sm" :class="[inversion ? 'items-end' : 'items-start']">
      <p class="text-xs text-[#b4bbc4]" :class="[inversion ? 'text-right' : 'text-left']"
        :style="{ color: props.fontColor }">
        <template v-if="inversion">
          <em>{{ dateTime }}</em>&emsp;<strong>{{ props.name }}</strong>
        </template>
        <template v-else>
          <strong>{{ props.name }}</strong>&emsp;<em>{{ dateTime }}</em>
        </template>
        <!-- <FlowData v-if="flowData" :flowData="flowData" /> -->
      </p>
      <div class="flex items-end gap-1 mt-2" :class="[inversion ? 'flex-row-reverse' : 'flex-row']">
        <TextComponent ref="textRef" :inversion="inversion" :error="error" :text="text" :loading="loading"
          :showCursor="showCursor" :as-raw-text="props.type === 'question' || asRawText" :textType="props.textType"
          :translateData="props.translateData" :showOperate="props.showOperate" :voice="props.voice"
          @replaceFn="replaceFn" @onTryAgain="emit('onTryAgain')" />
        <!-- 有内容并且在左侧有语音标识 -->
        <BaseSounds v-if="text && !inversion" :content="text" :voice="props.voice" ref="soundsRef"
          style="margin-left: 10px" size="small"></BaseSounds>
        <div class="flex flex-col" v-if="hideMore">
          <el-icon v-if="!inversion" @click="handleRegenerate">
            <Refresh v-if="!inversion"
              class="mb-2 transition text-neutral-300 hover:text-neutral-800 dark:hover:text-neutral-300" :size="14" />
          </el-icon>
          <el-popover trigger="hover" :placement="!inversion ? 'right' : 'left'">
            <template #reference>
              <el-icon style="transform: rotate(90deg)">
                <MoreFilled class="transition text-neutral-300 hover:text-neutral-800 dark:hover:text-neutral-200"
                  :size="14" />
              </el-icon>
            </template>
            <div>
              <div v-for="item of options" :key="item.key" :command="item.key" @click="handleSelect(item.key)">
                <el-link :underline="false">{{ item.label }}</el-link>
              </div>
            </div>
          </el-popover>
        </div>
        <div v-else>
          <el-popover trigger="hover" :placement="!inversion ? 'right' : 'left'">
            <template #reference>
              <el-icon style="transform: rotate(90deg)">
                <MoreFilled class="transition text-neutral-300 hover:text-neutral-800 dark:hover:text-neutral-200"
                  :size="14" />
              </el-icon>
            </template>
            <div>
              <div @click="handleSelect('copyText')">
                <el-link :underline="false">{{ "复制" }}</el-link>
              </div>
            </div>
          </el-popover>
        </div>
      </div>
    </div>
  </div>
</template>
