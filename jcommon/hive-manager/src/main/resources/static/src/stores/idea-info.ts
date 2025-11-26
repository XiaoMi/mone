import { ref, computed } from 'vue';
import { defineStore } from "pinia";

export type IdeaInfo = {
  gptModel: string;
  vision: boolean;
  legacy: boolean;
  aiProxyDebug: boolean;
  uiConfig: Record<string, string>;
  local: boolean;
  send: boolean;
  pluginVersion: string;
  agentName: string;
  mioneUrl: string;
}

export const useIdeaInfoStore = defineStore("idea-info", () => {
  const ideaInfo = ref<IdeaInfo>({
    gptModel: 'gpt35',
    vision: false,
    legacy: false,
    uiConfig: {},
    aiProxyDebug: false,
    local: false,
    send: false,
    pluginVersion: '',
    agentName: '小助手',
    mioneUrl: '',
  });

  const vision = computed(() => ideaInfo.value.vision);
  const legacy = computed(() => ideaInfo.value.legacy);
  const showZtoken = computed(() => !(ideaInfo.value.uiConfig['conf.ui.hide.ztoken'] === 'true'));
  const showFileMenu = computed(() => !(ideaInfo.value.uiConfig['conf.ui.hide.file.menu'] === 'true'));
  const pluginVersion = computed(() => ideaInfo.value.pluginVersion);
  const mioneName = computed(() => ideaInfo.value.agentName);
  const mioneUrl = computed(() => ideaInfo.value.mioneUrl);
  const isShowFile = computed(() => {
    if (ideaInfo.value.pluginVersion) {
      return Number(ideaInfo.value.pluginVersion.replace(/\./ig, '')) >= 202408261
    }
    return false;
  });

  const isShowPause = computed(() => {
    if (ideaInfo.value.pluginVersion) {
      return Number(ideaInfo.value.pluginVersion.replace(/\./ig, '')) >= 202411262
    }
    return false;
  });

  async function fetchIdeaInfo() {
    // const data = await util.getIdeaInfo();
    // console.log("fetchIdeaInfo", data)
    // ideaInfo.value = {
  //     ...ideaInfo.value,
  //     ...data,
  //   };
  }

  return { vision, legacy, pluginVersion, isShowFile, fetchIdeaInfo, isShowPause, showZtoken, showFileMenu, mioneName, mioneUrl };
});
