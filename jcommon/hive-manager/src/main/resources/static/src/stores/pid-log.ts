import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

const MAX_LOG_LINES = 100;

export const usePidLogStore = defineStore('pidLog', () => {
  const _existingPidLog = ref<Record<string, string[]>>({});

  const existingPidLog = computed(() => _existingPidLog.value);


  function setLog(pid: string, logs: string[]) {
    _existingPidLog.value[pid] = logs;
  }

  function addLog(pid: string, logs: string[]) {
    if (!_existingPidLog.value[pid]) {
      _existingPidLog.value[pid] = [];
    }
    _existingPidLog.value[pid].push(...logs);
    if (_existingPidLog.value[pid].length > MAX_LOG_LINES) {
      _existingPidLog.value[pid] = _existingPidLog.value[pid].slice(-MAX_LOG_LINES);
    }
  }

  return { existingPidLog, setLog, addLog }
})
