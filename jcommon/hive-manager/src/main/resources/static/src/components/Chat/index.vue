<template>
  <DebuggerPage v-if="formData" :data="formData" type="debug" :topicId="topicId" :initMsg="initMsg" :userList="userList"
    @changeCurrent="onChangeCurrent"></DebuggerPage>
  <LoadingAnimation v-else></LoadingAnimation>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import DebuggerPage from "./DebuggerPage.vue";
import { getBotDetail } from "@/api/probot";
import { ElMessage } from "element-plus";
import LoadingAnimation from "./LoadingAnimation.vue"; // 新增导入

const props = defineProps<{
  initMsg: string;
  userList: any[];
  onChangeCurrent: (id: number | string) => void;
}>();

const formData = ref(null);

const topicId = ref();

onMounted(() => {
  topicId.value = new Date().getTime();
  getDetail();
});

const getDetail = () => {
  getBotDetail({
    botId: "160511",
  }).then((res) => {
    if (res.code === 0) {
      formData.value = res.data;
    } else {
      ElMessage.error(res.message);
    }
  });
};

defineEmits<{
  (e: 'changeCurrent', id: number | string): void
}>()
</script>

<style>
@import url(./common-message//Message/style.scss);
@import url(./style.scss);
@import url(./scss/highlight.scss);
@import url(./scss/github-markdown.scss);
@import url(./scss/MarkdownMessage.scss);
</style>
