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
          <el-button type="primary" plain @click.stop="handleSearch" color="#40a3ff">{{
            t('probot.search')
          }}</el-button>
        </el-row>
      </el-form>
      <el-button type="primary" @click="create" v-if="route.name != 'AI Probot My Collect'"
        >创建工作流</el-button
      >
    </div>
    <el-table :data="tableData" style="width: 100%">
      <el-table-column label="工作流" width="220" v-slot="{ row }">
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
      <el-table-column prop="address" label="操作" width="100">
        <template #default="scope">
          <!-- <el-button link :icon="CopyDocument"> </el-button> -->
          <el-button
            class="btn-item"
            text
            size="small"
            type="primary"
            @click.stop="deleteItem(scope.row)"
            >{{ t('common.delete') }}</el-button
          >
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
import { getFlowList, deleteFlow } from '@/api/workflow'
import { t } from '@/locales'
import BaseInfo from '@/components/BaseInfo.vue'

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
  console.log('route', route, router)
  const p = {
    ...formInline.value,
    workSpaceId: route?.params?.id,
    scale: route.name == 'AI Probot My Collect' ? 'favorite' : ''
  }
  getFlowList(p)
    .then(({ data }) => {
      tableData.value = data?.records || []
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
      console.log('确认', item)
      delApi(item.flowBaseInfo.id)
    })
    .catch(() => {})
}
const route = useRoute()
const router = useRouter()
const toDetail = (item) => {
  router.push({
    name: 'AI Probot workflowItem',
    params: {
      id: item.flowBaseInfo.id
    }
  })
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

watch(
  () => route.name,
  (val, oldV) => {
    if (val != oldV) {
      refresh()
    }
  }
)
onMounted(() => {
  getList()
})
watch(
  () => route.params.id,
  () => {
    getList()
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
