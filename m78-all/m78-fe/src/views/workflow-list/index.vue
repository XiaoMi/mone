<template>
  <div class="list-box">
    <div class="top-b">
      <el-form :model="formInline" inline class="search-form" @submit.prevent>
        <el-row :gutter="15">
          <!-- <el-form-item label="状态">
          <StatusSel v-model="formInline.status" />
        </el-form-item> -->
          <el-form-item label="名称:" prop="name">
            <el-input
              v-model="formInline.name"
              placeholder="请输入工作流名称"
              clearable
              @keyup.enter="handleSearch"
              @clear="handleSearch"
            />
          </el-form-item>
          <el-button type="primary" plain @click.stop="handleSearch">{{
            t('common.search')
          }}</el-button>
        </el-row>
      </el-form>
      <el-button type="primary" plain @click="importFn" v-if="route.name != 'AI Probot My Collect'">{{ t('common.import') }}</el-button>
      <el-button type="primary" @click="create" v-if="route.name != 'AI Probot My Collect'"
        >创建工作流</el-button
      >
    </div>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column label="工作流" min-width="220" v-slot="{ row }">
        <BaseInfo @click="toDetail(row)" :data="row?.flowBaseInfo" size="small" />
      </el-table-column>
      <el-table-column label="参数" v-slot="{ row }">
        <p>
          <span
            v-for="item in row?.flowBaseInfo?.inputs?.filter((item) => item.name) || []"
            :key="item.name"
            class="input-i"
          >
            {{ item.name }}
          </span>
        </p>
      </el-table-column>
      <!-- <el-table-column prop="name" label="状态" /> -->
      <el-table-column prop="name" label="编辑时间" v-slot="{ row }">
        {{ dateFormat(row.flowBaseInfo.utime, 'yyyy-mm-dd HH:MM:ss') }}
      </el-table-column>
      <el-table-column prop="address" label="操作" width="180" v-if="route.name != 'AI Probot My Collect'">
        <template #default="scope">
          <!-- <el-button link :icon="CopyDocument"> </el-button> -->
          <el-button
            class="btn-item"
            text
            size="small"
            type="primary"
            @click.stop="copyItem(scope.row)"
            :loading="exportLoading"
          >
            {{ t('common.export') }}</el-button
          >
          <CopyFlowBtn
            :originalId="scope.row?.flowBaseInfo?.id"
            type="text"
            @copySuc="copySuc"
            size="small"
            btnType="primary"
          />
          <el-button
            class="btn-item"
            text
            size="small"
            type="primary"
            @click.stop="deleteItem(scope.row)"
            >{{ t('common.delete') }}</el-button
          >
          <el-button link size="small" type="primary" @click.stop="handleApi(scope.row)">
            API
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="page-box">
      <el-pagination
        background
        layout="prev, pager, next"
        :page-count="totalPage"
        v-model:current-page="formInline.pageNum"
        :page-size="formInline.pageSize"
        small
        @current-change="changePage"
      />
    </div>
    <CreateFlow v-model="showCreate" @createSuc="getList" />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import StatusSel from './components/StatusSel.vue'
import dateFormat from 'dateformat'
import CreateFlow from './CreateFlow.vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CodeImg from '@/views/workflow/imgs/icon-Code.png'
import { useRouter, useRoute } from 'vue-router'
import { getFlowList, deleteFlow, getFlowDetail, importFlow } from '@/api/workflow'
import { t } from '@/locales'
import BaseInfo from '@/components/BaseInfo.vue'
import CopyFlowBtn from '@/components/CopyFlowBtn.vue'
import useClipboard from 'vue-clipboard3'
import mitt from '@/utils/bus'

const showCreate = ref(false)
const formInline = ref({
  // status: 'all',
  name: '',
  pageNum: 1,
  pageSize: 20
})
const totalPage = ref(0)
const tableData = ref([])
const loading = ref(false)

mitt.on('updateFlowDataList', async () => {
  getList()
})
const handleApi = (row) => {
  const { href } = router.resolve({
    path: '/probot-api',
    query: {
      workspaceId: row?.flowBaseInfo?.workSpaceId,
      flowId: row?.flowBaseInfo?.id
    }
  })
  window.open(href, '_blank')
}

const switchShowCreat = () => {
  showCreate.value = !showCreate.value
}
const changePage = (page) => {
  formInline.value.page = page
  getList()
}
const create = () => {
  switchShowCreat()
  console.log('create')
}
const getList = () => {
  loading.value = true
  const p = {
    ...formInline.value,
    workSpaceId: route?.params?.id,
    scale: route.name == 'AI Probot My Collect' ? 'favorite' : ''
  }
  getFlowList(p)
    .then(({ data }) => {
      const tList = data?.records || []
      const newList = tList.map((item) => {
        const avatarUrl = item.flowBaseInfo.avatarUrl || '1'
        return {
          ...item,
          flowBaseInfo: {
            ...item.flowBaseInfo,
            avatarUrl
          }
        }
      })
      tableData.value = newList
      totalPage.value = data?.totalPage
    })
    .finally(() => {
      loading.value = false
    })
}
const handleSearch = () => {
  formInline.value.pageNum = 1
  getList()
}
const delApi = (flowBaseId) => {
  deleteFlow(flowBaseId).then((res) => {
    if (res.code == 0) {
      ElMessage.success('删除成功！')
      getList()
    } else {
      ElMessage.error(res.message)
    }
  })
}
const deleteItem = (item) => {
  ElMessageBox.confirm('确认要删除工作流吗？', '删除工作流', {
    type: 'warning'
  })
    .then(() => {
      delApi(item.flowBaseInfo.id)
    })
    .catch(() => {})
}
const route = useRoute()
const router = useRouter()
const toDetail = (item) => {
  toDetailById(item.flowBaseInfo.id)
}

const toDetailById = (id) => {
  const { href } = router.resolve({
    name: 'AI Probot workflowItem',
    params: {
      id
    }
  })
  window.open(href, '_blank') //打开新的窗口
}

const refresh = () => {
  formInline.value = {
    ...formInline,
    ...{
      pageNum: 1,
      pageSize: 20
    }
  }
  getList()
}

const copySuc = () => {
  refresh()
}

// 导出
const exportLoading = ref(false)
const { toClipboard } = useClipboard()
const copyItem = async (item) => {
  exportLoading.value = true
  const { data } = await getFlowDetail(item.flowBaseInfo?.id)
  if (!data) {
    exportLoading.value = false
    warningTip()
    return
  }
  const { flowBaseInfo, flowSettingInfo } = data
  if (!flowBaseInfo || !flowSettingInfo) {
    exportLoading.value = false
    warningTip()
    return
  }
  const copyP = JSON.stringify({ flowBaseInfo, flowSettingInfo })
  try {
    await toClipboard(copyP)
    exportLoading.value = false
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error(t('common.copyError'))
    exportLoading.value = false
  }
}
const warningTip = () => {
  ElMessage.warning('该条流水线数据有问题,无法进行导入、导出操作！')
}
// 导入
const importFn = async () => {
  const clipVal = await navigator.clipboard.readText()
  if (!clipVal) {
    ElMessage.warning('请先导入工作流！')
    return
  }
  try {
    const { flowBaseInfo, flowSettingInfo } = JSON.parse(clipVal)
    if (!flowBaseInfo || !flowSettingInfo) {
      warningTip()
      return
    }
    try {
      const { data } = await importFlow({
        flowBaseInfo: {
          ...flowBaseInfo,
          workSpaceId: route.params.id
        },
        flowSettingInfo
      })
      const hasPlugin = flowSettingInfo?.nodes?.find((item) => item.nodeType == 'plugin')
      ElMessage({
        type: hasPlugin ? 'warning' : 'success',
        message: `导入成功！${
          hasPlugin
            ? '但是该工作流中存在插件节点（环境变化对插件影响极大），请及时校验该节点能否正常运行'
            : ''
        }`,
        duration: hasPlugin ? 10000 : 5000
      })
      toDetailById(data)
    } catch (error) {
      console.log(error, '报错了！')
    }
  } catch (error) {
    console.log('error', error)
    ElMessage.warning('请检查剪切板数据！')
  }
}

// watch(
//   () => route.name,
//   (val, oldV) => {
//     if (val != oldV) {
//       refresh()
//     }
//   }
// )
onMounted(() => {
  getList()
})
watch(
  () => route.params.id,
  (val) => {
    if (route.name === 'AI Probot Space' || route.name == 'AI Probot My Collect') {
      getList()
    }
  }
)
</script>

<style lang="scss" scoped>
.top-b {
  display: flex;
  justify-content: space-between;

  .search-form {
    flex: 1;
  }
}

.input-i {
  padding: 3px 5px;
  background: #eee;
  border-radius: 6px;
}

.input-i + .input-i {
  margin-left: 5px;
}

.page-box {
  padding-top: 10px;
  display: flex;
  flex-direction: row-reverse;
}

.btn-item {
  padding-left: 4px;
  padding-right: 4px;
}
</style>
