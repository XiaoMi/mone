<template>
  <ProbotBaseTitle title="平台统计"></ProbotBaseTitle>
  <div class="probot-platformStatistics">
    <div class="ml-20px mt-20px flex w-[fit-content]">
      <el-form ref="formRef" :model="searchForm" inline @submit.native.prevent>
        <el-form-item prop="invokeTimeBegin" label="日期范围：">
          <el-date-picker
            v-model="searchForm.invokeTime"
            type="datetimerange"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            date-format="YYYY/MM/DD ddd"
            time-format="A hh:mm:ss"
            value-format="x"
          />
        </el-form-item>
      </el-form>
    </div>
    <div class="mx-20px mb-20px flex items-end">
      <span class="color-#666 text-20px">平台调用情况</span>
      <span class="ml-10px">{{ subtext }}</span>
    </div>
    <div class="flex justify-between items-center mb-20px mx-20px">
      <div class="contentBox w-48% p-20px text-center bg-#fff rounded-10px">
        <p class="text-38px font-700">{{ allInvokeCounts }}</p>
        <p class="text-18px py-10px">平台总调用数</p>
        <p class="color-#666 text-12px ml-10px">{{ subtext }}</p>
      </div>
      <div class="contentBox w-48% p-20px text-center bg-#fff rounded-10px">
        <p class="text-38px font-700">{{ allInvokeUsers }}</p>
        <p class="text-18px py-10px">平台总调用用户数</p>
        <p class="color-#666 text-12px ml-10px">{{ subtext }}</p>
      </div>
    </div>
    <div class="flex justify-between items-center mb-20px mx-20px">
      <div class="contentBox w-48% h-400px p-20px text-center bg-#fff rounded-10px overflow-hidden">
        <Chart
          title="平台总调用数类型占比"
          :subtext="subtext"
          :data="InvokeCounts"
          @chatClick="chatClick"
        ></Chart>
      </div>
      <div class="contentBox w-48% h-400px p-20px text-center bg-#fff rounded-10px overflow-hidden">
        <Chart
          title="平台总调用用户数类型占比"
          :subtext="subtext"
          :data="InvokeUsers"
          @chatClick="chatClick"
        ></Chart>
      </div>
    </div>
    <div class="mx-20px mb-20px mt-40px flex items-end">
      <span class="color-#666 text-20px">平台调用记录</span>
      <span class="ml-10px">{{ subtext }}</span>
    </div>
    <el-form class="mx-20px" ref="formRef" :model="searchListForm" inline @submit.native.prevent>
      <el-form-item prop="listType" label="类型：">
        <el-select
          v-model="searchListForm.listType"
          style="width: 100%"
          placeholder="请选择类型"
          @change="getList(1)"
          clearable
        >
          <el-option
            v-for="item in actions"
            :key="item?.id"
            :label="item?.name"
            :value="item?.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="relateName" label="名称：">
        <el-input
          v-model="searchListForm.relateName"
          placeholder="请输入名称"
          clearable
          @keyup.enter="getList(1)"
        />
      </el-form-item>
    </el-form>
    <div class="contentBox mx-20px p-20px rounded-10px bg-#fff">
      <el-table :data="listState.tableData" style="width: 100%" v-loading="listState.loading">
        <el-table-column fixed prop="type" label="类型" v-slot="{ row }">
          {{ actions.find((v) => v.id === row.type)?.name || '' }}
        </el-table-column>
        <!-- <el-table-column prop="relateId" label="relateId" /> -->
        <el-table-column prop="invokeDay" label="调用时间" v-slot="{ row }">
          {{ getLocalTime(row.invokeDay) }}
        </el-table-column>
        <el-table-column prop="invokeCounts" label="平台调用数" />
        <el-table-column prop="invokeUsers" label="平台调用用户数" />
        <el-table-column prop="invokeDay" label="名称" v-slot="{ row, $index }" width="200px">
          <div v-if="row.relateName">{{ row.relateName }}</div>
          <BaseLink v-else name="查看" @click.stop="getUser(row, $index)"></BaseLink>
        </el-table-column>
        <el-table-column prop="invokeDay" label="头像" v-slot="{ row, $index }" width="200px">
          <template v-if="row.avatarUrl">
            <img
              class="w-40px h-40px"
              v-if="row.avatarUrl?.includes('http')"
              :src="row.avatarUrl"
            />
            <BaseIcon v-else :index="row.avatarUrl" size="small"></BaseIcon>
          </template>
          <BaseLink v-else name="查看" @click.stop="getUser(row, $index)"></BaseLink>
        </el-table-column>
        <el-table-column fixed="right" label="日志" v-slot="{ row, $index }">
          <BaseLink name="查看" @click.stop="handleDetail(row)"></BaseLink>
        </el-table-column>
      </el-table>
      <div class="py-20px h-40px" v-if="listState.total">
        <el-pagination
          class="float-right"
          small
          background
          layout="sizes, prev, pager, next"
          :total="listState.total"
          v-model:page-size="listState.search.pageSize"
          v-model:current-page="listState.search.page"
          :page-sizes="[10, 20, 30, 40]"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    <CarouselDialog
      v-model="listState.carouselDialogVisible"
      :data="dialogData"
      :subtext="subtext"
    />
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, watch } from 'vue'
import BaseIcon from '@/components/BaseIcon.vue'
import BaseLink from '@/components/probot/BaseLink.vue'
import CarouselDialog from './CarouselDialog.vue'
import ProbotBaseTitle from '@/components/probot/ProbotBaseTitle.vue'
import {
  listAllPerdayInfos,
  listPerdayInfosByAdmin,
  getBotSimpleInfo,
  flowDetail,
  orgGetById
} from '@/api/probot-platformStatistics'
import Chart from './Chart.vue'

const searchForm = ref({
  invokeTime: []
})

const subtext = ref('')
function getLocalTime(n: string) {
  if (!n) return ''
  return new Date(parseInt(n)).toLocaleString().split(' ')[0]
}

const allInvokeCounts = ref(0)
const allInvokeUsers = ref(0)
const InvokeCounts = ref([])
const InvokeUsers = ref([])
const actions = [
  { id: 1, name: 'bot' },
  { id: 2, name: 'flow' },
  { id: 3, name: 'plugin' }
]
async function getNumber() {
  try {
    const params = {
      invokeTimeBegin: searchForm.value.invokeTime[0],
      invokeTimeEnd: searchForm.value.invokeTime[1]
    }
    const res = await listAllPerdayInfos(params)
    if (res.data) {
      allInvokeCounts.value = res.data.reduce((a, { allInvokeCounts }) => a + allInvokeCounts, 0)
      allInvokeUsers.value = res.data.reduce((a, { allInvokeUsers }) => a + allInvokeUsers, 0)

      InvokeCounts.value = res.data.map(({ type, allInvokeCounts }) => ({
        id: type,
        name: actions.find((v) => v.id === type)?.name || '',
        value: allInvokeCounts
      }))
      InvokeUsers.value = res.data.map(({ type, allInvokeUsers }) => ({
        id: type,
        name: actions.find((v) => v.id === type)?.name || '',
        value: allInvokeUsers
      }))
      if (params.invokeTimeBegin && params.invokeTimeEnd) {
        subtext.value = `${getLocalTime(params.invokeTimeBegin)} ~ ${getLocalTime(params.invokeTimeEnd)}`
      } else {
        subtext.value = `时间不限`
      }
    } else {
      allInvokeCounts.value = 0
      allInvokeUsers.value = 0
      InvokeCounts.value = []
      InvokeUsers.value = []
    }
  } catch (error) {
    console.log(error)
  }
}

const listState = reactive({
  carouselDialogVisible: false,
  tableData: [],
  search: {
    pageSize: 10,
    page: 1
  },
  total: 0,
  loading: false
})
const searchListForm = ref({
  listType: '',
  relateName: ''
})
const chatClick = (e) => {
  console.log(e)
}
async function getList(page = 1) {
  try {
    listState.loading = true
    listState.search.page = page
    const params = {
      ...listState.search,
      invokeTimeBegin: searchForm.value.invokeTime[0],
      invokeTimeEnd: searchForm.value.invokeTime[1],
      relateName: searchListForm.value.relateName,
      type: searchListForm.value.listType
    }
    const res = await listPerdayInfosByAdmin(params)
    if (res.data) {
      listState.tableData = res.data.list
      listState.total = res.data.totalPage * listState.search.pageSize || 0
    } else {
      listState.tableData = []
      listState.total = 0
    }
  } catch (error) {
    console.log(error)
  } finally {
    listState.loading = false
  }
}
const handleCurrentChange = (page: number) => {
  getList(page)
}
const handleSizeChange = (page: number) => {
  listState.search.pageSize = page
  getList()
}
const getUser = (row, index: number) => {
  // 此功能未使用，因为列表已经返回，逻辑暂时保留
  let res
  switch (row.type) {
    case 1:
      getBotSimpleInfoFun(row.relateId, index)
      break
    case 2:
      getFlowDetailFun(row.relateId, index)
      break
    case 3:
      getOrgGetByIdFun(row.relateId, index)
      break
  }
  console.log('resss', res)
}
import { ElMessage } from 'element-plus'
async function getBotSimpleInfoFun(relateId, index) {
  try {
    listState.loading = true
    const res = await getBotSimpleInfo({
      botId: relateId
    })
    if (res.data) {
      const { name, avatarUrl = '10' } = res.data.botInfo
      listState.tableData[index].relateName = name
      listState.tableData[index].avatarUrl = avatarUrl
    } else {
      ElMessage.error('获取失败')
    }
  } catch (err) {
    console.log(err)
  } finally {
    listState.loading = false
  }
}
async function getFlowDetailFun(relateId, index) {
  try {
    listState.loading = true
    const res = await flowDetail({
      id: relateId
    })
    if (res.data) {
      const { name, avatarUrl = '10' } = res.data.flowBaseInfo
      listState.tableData[index].relateName = name
      listState.tableData[index].avatarUrl = avatarUrl
    } else {
      ElMessage.error('获取失败')
    }
  } catch (err) {
    console.log(err)
  } finally {
    listState.loading = false
  }
}
async function getOrgGetByIdFun(relateId, index) {
  try {
    listState.loading = true
    const res = await orgGetById({
      id: relateId
    })
    if (res.data) {
      const { pluginOrgName, avatarUrl = '10' } = res.data
      listState.tableData[index].relateName = pluginOrgName
      listState.tableData[index].avatarUrl = avatarUrl
    } else {
      ElMessage.error('获取失败')
    }
  } catch (err) {
    console.log(err)
  } finally {
    listState.loading = false
  }
}
const dialogData = ref({
  relateId: '',
  invokeTimeBegin: '',
  invokeTimeEnd: '',
  invokeUserName: '',
  pageSize: 10,
  page: 1
})
async function handleDetail(row) {
  Object.assign(dialogData.value, {
    relateId: row.relateId,
    invokeTimeBegin: searchForm.value.invokeTime[0],
    invokeTimeEnd: searchForm.value.invokeTime[1],
    pageSize: 10,
    page: 1
  })
  listState.carouselDialogVisible = true
}
watch(
  () => searchForm.value.invokeTime,
  (newVal) => {
    if (!newVal) searchForm.value.invokeTime = []
    search()
  }
)

function search() {
  getNumber()
  getList()
}
search()
</script>
<style lang="scss" scoped>
.contentBox {
  box-shadow:
    0 0 rgba(0, 0, 0, 0),
    0 0 rgba(0, 0, 0, 0),
    0 0 rgba(0, 0, 0, 0),
    0 0 rgba(0, 0, 0, 0),
    0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -4px rgba(0, 0, 0, 0.1);
  border: 1px solid rgb(229, 231, 235);
}
</style>
