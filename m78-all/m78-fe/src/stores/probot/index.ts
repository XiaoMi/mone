import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useProbotStore = defineStore('probot', () => {
  const _bindKnowlege = ref<
    {
      id: string
      name: string
      desc: string
      creator: string
    }[]
  >([])
  const bindKnowlege = computed(() => _bindKnowlege.value)

  const _createdRobtId = ref('')
  const createdRobtId = computed(() => _createdRobtId.value)
  const categoryTypeList = ref([])
  const categoryList = ref<{
    '': Array<{
      id: number
      name: string
    }>
    '1': Array<{
      id: number
      name: string
    }>
    '2': Array<{
      id: number
      name: string
    }>
  }>({
    '': [],
    '1': [],
    '2': []
  })
  const statusList = [
    {
      value: '',
      label: '全部'
    },
    {
      value: '0',
      label: '私有'
    },
    {
      value: '1',
      label: '公开'
    }
  ]
  const workspaceId = ref()
  const workspaceList = ref([])

  const setBindKnowlege = (bindKnowlege: any) => {
    _bindKnowlege.value = bindKnowlege
  }

  const setCreatedRobtId = (createdRobtId: string) => {
    _createdRobtId.value = createdRobtId
  }
  const setCategoryTypeList = (data: any) => {
    categoryTypeList.value = data
  }
  const setCategoryList = (data: any) => {
    categoryList.value = {
      ...categoryList.value,
      ...data
    }
  }

  const setWorkspaceId = (data: any) => {
    workspaceId.value = data
  }
  const setWorkspaceList = (data: any) => {
    workspaceList.value = data
  }

  return {
    bindKnowlege,
    createdRobtId,
    setBindKnowlege,
    setCreatedRobtId,
    categoryTypeList,
    setCategoryTypeList,
    categoryList,
    setCategoryList,
    workspaceList,
    setWorkspaceList,
    workspaceId,
    setWorkspaceId,
    statusList
  }
})
