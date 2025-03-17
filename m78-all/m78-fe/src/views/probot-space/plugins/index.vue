<template>
  <div class="plugin-wrap" v-loading="state.loading">
    <div class="search">
      <el-form :model="state.search" inline>
        <el-row :gutter="15">
          <el-form-item label="名称:">
            <el-input
              v-model="state.search.name"
              :placeholder="t('plugin.enterPluginName')"
              @keyup.enter="handleSearch"
              clearable
              @clear="handleSearch"
            />
          </el-form-item>
          <el-form-item :label="`${t('plugin.username')}:`">
            <el-input
              v-model="state.search.userName"
              :placeholder="t('plugin.enterUserName')"
              @keyup.enter="handleSearch"
              clearable
              @clear="handleSearch"
            />
          </el-form-item>
          <el-button type="primary" @click="handleSearch" size="default" plain class="btn-item">{{
            t('common.search')
          }}</el-button>
        </el-row>
      </el-form>
      <div v-if="route.name != 'AI Probot My Collect'">
        <el-button type="primary" plain @click="handleImport">{{ t('common.import') }}</el-button>
        <el-button type="primary" @click="state.editVisible = true" size="default">{{
          t('plugin.createPluginBtn')
        }}</el-button>
      </div>
    </div>
    <!-- table空状态状态统一，不用做特殊处理 -->
    <TableList :tableList="state.tableList" @onOk="getList" @handleExport="handleExport" />
    <div class="pager" v-if="state.total">
      <el-pagination
        small
        background
        layout="prev, pager, next"
        :total="state.total"
        :page-size="state.search.pageSize"
        :v-model:current-page="state.search.pageNum"
        hide-on-single-page
        @change="handleChangePage"
      />
    </div>
  </div>
  <EditPluginGroup v-model="state.editVisible" @onOk="getList" />
  <ImportDialog v-model="importDialogVisible" @callback="importDialogCallback"></ImportDialog>
</template>

<script lang="ts" setup>
import { pluginList, type IPluginItem, botpluginOrgImport } from '@/api/plugins'
import EditPluginGroup from './EditPluginGroup.vue'
import { reactive, onBeforeMount, watch, ref } from 'vue'
import TableList from './TableList.vue'
import Lodash from 'lodash'
import { t } from '@/locales'
import { useRoute, useRouter } from 'vue-router'
import useClipboard from 'vue-clipboard3'
import ImportDialog from '../components/ImportDialog.vue'
import { ElMessage } from 'element-plus'

const { toClipboard } = useClipboard()
const router = useRouter()
const route = useRoute()

const state = reactive({
  editVisible: false,
  tableList: [] as Array<IPluginItem>,
  search: {
    name: undefined,
    userName: undefined,
    pageSize: 16,
    pageNum: 1,
    workspaceId: route.params.id
  },
  total: 0,
  loading: true
})
const importDialogVisible = ref(false)

const getList = (page = 1) => {
  state.loading = true
  state.search.pageNum = page
  pluginList(state.search)
    .then((data) => {
      if (data?.data.records?.length) {
        state.tableList = data.data.records || []
        state.total = data.data.totalRow || 0
      } else {
        state.tableList = []
        state.total = 0
      }
    })
    .catch((e) => {
      console.log(e)
    })
    .finally(() => {
      state.loading = false
    })
}

const loadList = Lodash.debounce(getList, 500)

const handleSearch = () => {
  loadList(1)
}
const handleChangePage = (page: number) => {
  getList(page)
}

const handleExport = (item: any) => {
  return toClipboard(JSON.stringify(item))
    .then(() => {
      ElMessage.success(t('common.copySuccess'))
    })
    .catch(() => {
      ElMessage.error(t('common.copyError'))
    })
}

const handleImport = async () => {
  // 请求剪贴板权限
  const permission = await navigator.permissions.query({ name: 'clipboard-read' })
  if (permission.state === 'granted' || permission.state === 'prompt') {
    // 读取剪贴板内容
    const text = await navigator.clipboard.readText()
    //导入
    try {
      if (text) {
        toImport()
      } else {
        importDialogVisible.value = true
      }
    } catch (error) {
      importDialogVisible.value = true
    }
  }
}
const importDialogCallback = (str: string) => {
  handleExport(str).then(() => {
    toImport()
  })
}

const toImport = async () => {
  // 请求剪贴板权限
  const permission = await navigator.permissions.query({ name: 'clipboard-read' })
  if (permission.state === 'granted' || permission.state === 'prompt') {
    // 读取剪贴板内容
    const text = await navigator.clipboard.readText()
    //导入
    try {
      if (text) {
        const params = JSON.parse(text)
        params.pluginOrgName = params.pluginOrgName + '_copy'
        params.workspaceId = route?.params?.id
        delete params.id
        delete params.createTime
        delete params.modifyTime
        params.plugins.forEach((item) => {
          delete item.id
          delete item.createTime
        })
        botpluginOrgImport(params).then((res) => {
          if (res.data) {
            ElMessage.success(t('common.importSuccess'))
          }
          getList()
        })
      } else {
      }
    } catch (error) {}
  }
}

onBeforeMount(() => {
  getList()
})
watch(
  () => route.params.id,
  () => {
    if (route.name === 'AI Probot Space' || route.name == 'AI Probot My Collect') {
      state.search = {
        name: undefined,
        userName: undefined,
        pageSize: 16,
        pageNum: 1,
        workspaceId: route.params.id
      }
      if (route.name == 'AI Probot My Collect') {
        state.search.isMyCollect = true
      }
      getList()
    }
  }
)
</script>

<style lang="scss" scoped>
.plugin-wrap {
  .search {
    display: flex;
    justify-content: space-between;
    .btn-item {
      margin-bottom: 18px;
    }
  }
  .pager {
    margin-top: 10px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
  }
}
</style>
