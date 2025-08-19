<!--
 * @Description: 
 * @Date: 2024-09-05 14:45:40
 * @LastEditTime: 2024-09-29 11:05:29
-->
<template>
  <div class="card-wrap">
    <div class="card-filter">
      <el-form :model="state.search" inline class="card-form">
        <el-form-item label="名称:">
          <el-input
            v-model="state.search.name"
            placeholder="请输入卡片名称"
            @keyup.enter="handleSearch"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="创建人:">
          <el-input
            v-model="state.search.userName"
            placeholder="创建人"
            @keyup.enter="handleSearch"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="类型:">
          <el-select
            style="width: 120px"
            placeholder="类型"
            v-model="state.search.type"
            @change="handleSearch"
            clearable
          >
            <el-option v-for="(item, key) in typeOptions" :key="key" :label="item" :value="key" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型:">
          <el-select
            style="width: 120px"
            placeholder="状态"
            v-model="state.search.status"
            @change="handleSearch"
            clearable
          >
            <el-option v-for="(item, key) in statusOptions" :key="key" :label="item" :value="key" />
          </el-select>
        </el-form-item>
        <el-button type="primary" plain @click="handleSearch">查询</el-button>
      </el-form>
      <div class="create-btn">
        <el-button type="primary" @click="create">新建卡片</el-button>
      </div>
    </div>
    <div class="card-table">
      <el-table :data="state.tableList" style="width: 100%" v-loading="state.loading">
        <el-table-column label="卡片名称" width="220" v-slot="{ row, $index }">
          <BaseInfo
            :data="{
              name: row.name || '----',
              describe: row.description,
              avatarUrl: row.avatarUrl || $index + ''
            }"
            size="small"
          />
        </el-table-column>
        <el-table-column prop="type" label="类型" v-slot="{ row }">{{
          typeOptions[row.type]
        }}</el-table-column>
        <el-table-column prop="status" label="状态" v-slot="{ row }">{{
          statusOptions[row.status]
        }}</el-table-column>
        <el-table-column prop="creator" label="创建人" />
        <el-table-column prop="updater" label="更新人" />
        <el-table-column prop="ctime" label="创建时间" v-slot="{ row }">
          {{ dateFormat(row.ctime, 'yyyy-mm-dd HH:MM:ss') }}</el-table-column
        >
        <el-table-column prop="utime" label="修改时间" v-slot="{ row }">
          {{ dateFormat(row.utime, 'yyyy-mm-dd HH:MM:ss') }}</el-table-column
        >
        <el-table-column fixed="right" label="操作" v-slot="{ row, $index }" width="130px">
          <el-button type="primary" size="small" class="btn-item" @click="edit(row, $index)" text
            >编辑</el-button
          >
          <el-button type="primary" size="small" class="btn-item" @click="manage(row)" text
            >卡片管理</el-button
          >
        </el-table-column>
      </el-table>
    </div>
    <div class="pager">
      <el-pagination
        small
        background
        layout="prev, pager, next"
        :total="state.total"
        :page-size="state.search.pageSize"
        :v-model:current-page="state.search.pageNum"
        @change="handleChangePage"
      />
    </div>
    <CardDialog v-model="cardVisible" :data="cardData" @onOk="cardDialogCallback"></CardDialog>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { listCards } from '@/api/probot-card'
import dateFormat from 'dateformat'
import BaseInfo from '@/components/BaseInfo.vue'
import { useRoute, useRouter } from 'vue-router'
import CardDialog from './CardDialog.vue'
import { useProbotStore } from '@/stores/probot'
import { useProbotCardStore } from '@/stores/card'
const probotStore = useProbotStore()
const cardStore = useProbotCardStore()
const spaceOptions = computed(() => probotStore.workspaceList)

const route = useRoute()
const router = useRouter()

const workSpaceId = computed(() => {
  return (route.params.id || spaceOptions?.value[0]?.id) as string
})

const typeOptions = ref([])
const statusOptions = ref([])
const state = reactive({
  tableList: [],
  search: {
    name: undefined,
    userName: undefined,
    type: '',
    status: '',
    pageSize: 10,
    pageNum: 1
  },
  total: 0,
  loading: true
})
const cardVisible = ref(false)
const cardData = ref({})

const getList = async () => {
  state.loading = true
  const { code, data } = await listCards({
    ...state.search,
    workSpaceId: workSpaceId.value
  })
  if (code === 0) {
    state.tableList = data?.list
    state.total = data?.totalPage * state.search.pageSize
  } else {
    state.tableList = []
    state.total = 0
  }
  state.loading = false
}
const handleSearch = () => {
  getList()
}
const handleChangePage = (page: number) => {
  getList()
}
const create = () => {
  cardVisible.value = true
  cardData.value = {}
}
const edit = (row: any, $index: number) => {
  cardVisible.value = true
  cardData.value = {
    avatarUrl: row.avatarUrl || $index + '',
    ...row
  }
}
const manage = (row: any) => {
  let routeDate = router.resolve({
    path: `/probot-card-manage/${workSpaceId.value}/${row.id}`
  })
  window.open(routeDate.href, '_blank')
}
const cardDialogCallback = async (data: string) => {
  await getList()
  if (data) {
    const { href } = router.resolve({
      path: `/probot-card-manage/${workSpaceId.value}/${data}`
    })
    window.open(href, '_blank')
  }
}

onMounted(async () => {
  getList()
  typeOptions.value = await cardStore.getTypeOptions()
  statusOptions.value = await cardStore.getStatusOptions()
})

watch(
  () => route.params.id,
  () => {
    getList()
  }
)
</script>

<style lang="scss" scoped>
.card-filter {
  display: flex;
  justify-content: space-between;
}
.card-form {
  display: flex;
}
.card-table {
  .btn-item {
    padding-left: 4px;
    padding-right: 4px;
  }
}
.create-btn {
  padding-left: 10px;
}

.pager {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
</style>
