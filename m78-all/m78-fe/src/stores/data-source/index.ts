import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import type { DataSource } from '@/views/data-source/data-source'
// import { setLocalState, getLocalState } from './helper'

export const useDataSourceStore = defineStore('dataSource', () => {
  const _activeDbId = ref('')
  const activeDbId = computed(() => _activeDbId.value)
  const _curDbInfo = ref<DataSource.DB | null>(null)
  const curDbInfo = computed(() => _curDbInfo.value)
  const _activeTableName = ref('')
  const activeTableName = computed(() => _activeTableName.value)

  const setActiveDbId = (id: string) => {
    _activeDbId.value = id
  }

  const setCurDbInfo = (dbInfo: DataSource.DB) => {
    _curDbInfo.value = dbInfo
  }

  const setActiveTableName = (tableName: string) => {
    _activeTableName.value = tableName
  }

  return { activeDbId, activeTableName, curDbInfo, setActiveDbId, setCurDbInfo, setActiveTableName }
})
