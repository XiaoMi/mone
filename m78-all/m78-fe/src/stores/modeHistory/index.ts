import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useModeHistory = defineStore('modeHistory', () => {
  const historyList = ref([])
  const activeHistory = ref(-1)
  const setHisList = (val: any) => {
    historyList.value = val
  }
  const setHisActive = (val: any) => {
    activeHistory.value = val
  }
  const unshiftHistory = (item: any) => {
    historyList.value.unshift(item)
    console.log('historyList.value', historyList.value)
    setHisActive(0)
  }
  const updateHistory = (item: any) => {
    historyList.value[activeHistory.value] = item
  }
  return {
    historyList,
    setHisList,
    setHisActive,
    activeHistory,
    unshiftHistory,
    updateHistory
  }
})
