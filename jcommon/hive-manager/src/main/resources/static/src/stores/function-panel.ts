import { ref } from "vue";
import { defineStore } from "pinia";
import { LocalStorage } from "lowdb/browser";
import { LowSync } from "lowdb";

export interface FunctionPanelState {
  webSearchEnabled: boolean;
  ragEnabled: boolean;
  showFunctionPanel: boolean;
}

const db = new LowSync<FunctionPanelState>(
  new LocalStorage("function-panel"),
  {
    webSearchEnabled: false,
    ragEnabled: false,
    showFunctionPanel: false,
  }
);

export const useFunctionPanelStore = defineStore("function-panel", () => {
  db.read();
  const data = db.data;

  const webSearchEnabled = ref<boolean>(data.webSearchEnabled);
  const ragEnabled = ref<boolean>(data.ragEnabled);
  const showFunctionPanel = ref<boolean>(data.showFunctionPanel);

  function setWebSearchEnabled(enabled: boolean) {
    webSearchEnabled.value = enabled;
    db.data.webSearchEnabled = enabled;
    db.write();
  }

  function setRagEnabled(enabled: boolean) {
    ragEnabled.value = enabled;
    db.data.ragEnabled = enabled;
    db.write();
  }

  function setShowFunctionPanel(show: boolean) {
    showFunctionPanel.value = show;
    db.data.showFunctionPanel = show;
    db.write();
  }

  function toggleWebSearch() {
    setWebSearchEnabled(!webSearchEnabled.value);
  }

  function toggleRag() {
    setRagEnabled(!ragEnabled.value);
  }

  function toggleFunctionPanel() {
    setShowFunctionPanel(!showFunctionPanel.value);
  }

  function getActiveFunctionCount(): number {
    let count = 0;
    if (webSearchEnabled.value) count++;
    if (ragEnabled.value) count++;
    return count;
  }

  function resetAllFunctions() {
    setWebSearchEnabled(false);
    setRagEnabled(false);
  }

  return {
    // state
    webSearchEnabled,
    ragEnabled,
    showFunctionPanel,

    // actions
    setWebSearchEnabled,
    setRagEnabled,
    setShowFunctionPanel,
    toggleWebSearch,
    toggleRag,
    toggleFunctionPanel,
    getActiveFunctionCount,
    resetAllFunctions,
  };
});
