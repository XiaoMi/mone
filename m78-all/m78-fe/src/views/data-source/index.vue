<template>
  <div class="h-full">
    <CommontPage
      v-model="rightActive"
      class="flex-1"
      uuid="data-cube"
      :title="t('dataSource.title')"
      :showBtns="true"
      :showDefaultBtns="false"
      :inputLoading="loading"
      :showChatHistory="true"
      @editHisChatCb="editHisChat"
      @delHisChatCb="delHisChat"
      :hisChatData="hisChatList"
      @addInput="addInput"
      @stopReq="stopReq"
    >
      <template #left>
        <AnalysisLoading loadingText="正在处理中..." v-if="loading" />
        <div class="left-container h-full">
          <Content
            class="p-[20px]"
            :db="db"
            :table-name="tableName"
            :json="content"
            @updateTableDatas="updateTableDatas"
          />
          <div class="left-pagination">
            <CommonPagination @handlePage="handlePage" :pageInfo="pageInfo"> </CommonPagination>
          </div>
        </div>
      </template>
      <template #btnsTop>
        <MyBtn
          class="mt-20px shrink-0"
          bgColor="var(--oz-color-primary)"
          iconClass="icon-icon-test"
          @click="handleAdd"
          :text="t('dataSource.newButton')"
        />
      </template>
      <template #btnsBtm>
        <MyBtn
          bgColor="#ce9de7"
          iconClass="icon-daochu"
          :text="t('excle.exportApi')"
          @click="clickExportApi"
        />
      </template>
      <template #chatList>
        <div class="h-full flex flex-col">
          <div class="shrink-0 max-h-full overflow-x-auto">
            <Tree :tree-data="treeData" @update-tree-data="updateTreeData" @nodeClick="nodeClick" />
          </div>
          <div class="flex-1 overflow-hidden">
            <ChatList v-model="inputsData" />
          </div>
        </div>
      </template>
      <template #otherShow>
        <ExportApiTable v-if="activeShow == 'exportApi'" @closeFn="closeHistory" />
      </template>
    </CommontPage>
    <NewDataSource @submit="submit" v-model="dialogVisible" :init-form="undefined" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, reactive } from 'vue'
import CommontPage from '@/components/common-page/CommonPage.vue'
import { t } from '@/locales'
import NewDataSource from './components/NewDataSource.vue'
import Tree from './components/Tree.vue'
import Content from './components/Content.vue'
import CommonPagination from '@/components/CommonPagination.vue'
import { executeSql, getDataSources, getLabels, queryTableData } from '@/api/data-source'
import ChatList from '@/views/doc/components/ChatList.vue'
import { useDataSourceStore } from '@/stores/data-source'
import { ElMessage } from 'element-plus'
import AnalysisLoading from '@/components/AnalysisLoading.vue'
import ExportApiTable from '@/components/export-http/ExportApiTable'

// let controller = new AbortController()

const dataSourceStore = useDataSourceStore()

const content = ref<Record<string, string>[]>([])
// const connectionId = ref('')
const inputsData = ref<DataSource.ChatItem[]>([])
const loading = ref(false)
// let controller = new AbortController()
const dialogVisible = ref(false)
const hisChatList = ref<DataSource.HistoryChat[]>([])
const treeData = ref<DataSource.Tree[]>([])

const connectionId = computed(() => dataSourceStore.activeDbId)
const tableName = computed(() => dataSourceStore.activeTableName)
const db = computed(() => dataSourceStore.curDbInfo)
const pageInfo = reactive<{
  currentPage: number //当前页
  total: number //总条数
  pageSize: number //每一页条数
}>({
  currentPage: 1,
  total: 0,
  pageSize: 20
}) //分页
const currentComment = ref('')
const currentType = ref('input') //当前操作类型是输入框输入还是节点点击
const item = ref()
const rightActive = ref(1)
const activeShow = ref('exportApi')

watch(
  () => dataSourceStore.activeDbId,
  (id, preId) => {
    if (id && id != preId) {
      updateLabels()
    }
  }
)

// 增加聊天list
const addChatItem = ({ text = '', loading = false, inversion = false }) => {
  const timer = new Date()
  const len = timer.getTime() + '_' + inputsData.value.length
  const item: DataSource.ChatItem = {
    hideMore: true,
    error: false,
    dateTime: timer.toLocaleString(),
    indexKey: len,
    text,
    loading,
    inversion // 是否是自己
  }
  inputsData.value.push(item)
  return item
}

const updateChatItem = (items: DataSource.ChatItem[]) => {
  inputsData.value = [...items]
}

const submit = async () => {
  await updateTreeData()
}

const handleAdd = () => {
  dialogVisible.value = true
}

const fetchDataSources = async () => {
  const { code, data } = await getDataSources()
  if (code == 0 && Array.isArray(data)) {
    return data.map((it) => {
      return {
        ...it,
        leaf: false,
        name: it.database
      }
    })
  }
  return []
}

const updateTreeData = async () => {
  const data = await fetchDataSources()
  treeData.value = data
}

const getSqlList = (isUpdateLabels) => {
  executeSql({
    connectionId: connectionId.value,
    comment: currentComment.value,
    customKnowledge: db.value?.customKnowledge || '',
    upperBound: pageInfo.pageSize,
    lowerBound: (pageInfo.currentPage - 1) * pageInfo.pageSize
  })
    .then(({ code, data }) => {
      if (code == 0 && Array.isArray(data.data)) {
        content.value = data.data
        pageInfo.total = data.total
        item.value.text = '处理成功'
        updateChatItem(inputsData.value)
        isUpdateLabels && updateLabels()
      } else {
        content.value = []
        item.value.text = '处理失败'
        updateChatItem(inputsData.value)
      }
    })
    .finally(() => {
      loading.value = false
    })
}
const addInput = (res: { inputV: string }) => {
  const comment = res.inputV
  currentComment.value = comment
  if (!db.value) {
    ElMessage.error('请选择数据库')
    return
  }
  if (loading.value == true) {
    return
  }
  loading.value = true
  addChatItem({ text: comment, inversion: true })
  item.value = addChatItem({ text: '处理中' })
  pageInfo.currentPage = 1
  currentType.value = 'input'
  getSqlList(true)
}

//操作分页
const handlePage = (opts?: { currentPage?: number; pageSize?: number }) => {
  pageInfo.pageSize = opts?.pageSize || pageInfo.pageSize
  pageInfo.currentPage = opts?.currentPage || pageInfo.currentPage
  if (currentType.value == 'input') {
    getSqlList()
  } else {
    nodeClick(currentType.value)
  }
}

const stopReq = () => {
  loading.value = false
}

const editHisChat = async () => {
  updateLabels()
}

const delHisChat = () => {
  updateLabels()
}

const updateLabels = async () => {
  if (connectionId.value) {
    const { code, data } = await getLabels({
      connectionId: connectionId.value
    })
    if (code == 0 && Array.isArray(data)) {
      hisChatList.value = data
    } else {
      hisChatList.value = []
    }
  } else {
    hisChatList.value = []
  }
}

const updateTableDatas = () => {
  queryTableData({
    connectionId: String(connectionId.value),
    tableName: dataSourceStore.activeTableName,
    upperBound: pageInfo.pageSize,
    lowerBound: (pageInfo.currentPage - 1) * pageInfo.pageSize
  }).then(({ code, data }) => {
    if (code == 0 && Array.isArray(data.data)) {
      content.value = data.data
      pageInfo.total = data.total
    } else {
      content.value = []
    }
  })
}

const nodeClick = (data, currentPage) => {
  pageInfo.currentPage = currentPage ? currentPage : pageInfo.currentPage
  currentType.value = data
  if (connectionId.value) {
    queryTableData({
      connectionId: connectionId.value,
      tableName: currentType.value.tableName,
      upperBound: pageInfo.pageSize,
      lowerBound: (pageInfo.currentPage - 1) * pageInfo.pageSize
    }).then(({ code, data }) => {
      if (code == 0 && Array.isArray(data.data)) {
        content.value = data.data
        pageInfo.total = data.total
      } else {
        content.value = []
      }
    })
  } else {
    ElMessage.error('请选择数据库')
  }
}

const clickExportApi = () => {
  activeShow.value = 'exportApi'
  rightActive.value = 4
}

const closeHistory = () => {
  rightActive.value = 1
}

const init = async () => {
  await updateTreeData()
}

init()
</script>

<style lang="scss" scoped>
.left-container {
  display: flex;
  flex-direction: column;
  .left-pagination {
    padding: 0 10px 10px;
  }
}
</style>
./data-source
