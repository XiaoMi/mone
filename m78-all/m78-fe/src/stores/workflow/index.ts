import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useCounterStore = defineStore('workflow', () => {
  const _pluginList = ref([])
  const pluginList = computed(() => _pluginList.value)

  const setPluginList = () => {}

  return { pluginList, setPluginList }
})
