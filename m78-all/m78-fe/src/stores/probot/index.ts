import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { getModelData } from '@/api/index'

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
  const LLMModelSelObj = ref({})
  const LLMModelSelList = ref([])

  // const _formHead = ref<Record<string, string>>({})
  // const formHead = computed(() => _formHead.value)
  // const _formBase = ref<Record<string, string>>({})
  // const formBase = computed(() => _formBase.value)
  // const _formConfig = ref<Record<string, string>>({})
  // const formConfig = computed(() => _formConfig.value)

  // const setFormHead = (formHead: Record<string, string>) => {
  //   _formBase.value = formHead
  // }

  // const setFormBase = (formBase: Record<string, string>) => {
  //   _formHead.value = formBase
  // }

  // const setFormConfig = (formConfig: Record<string, string>) => {
  //   _formConfig.value = formConfig
  // }

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
  const setLLMModelSel = (data: any) => {
    LLMModelSelObj.value = data
    LLMModelSelList.value = Object.values(data)?.flat() || []
  }

  const getModelDataReq = () => {
    getModelData({}).then((res) => {
      setLLMModelSel(res?.data || {})
    })
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
    statusList,
    LLMModelSelList,
    LLMModelSelObj,
    setLLMModelSel,
    getModelDataReq
  }
})
