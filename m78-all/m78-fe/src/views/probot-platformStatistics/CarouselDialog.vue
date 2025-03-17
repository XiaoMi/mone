<template>
  <el-dialog v-model="dialogVisible" width="90%" :draggable="true" :append-to-body="true">
    <template #header>
      <div class="flex justify-start items-end">
        <span class="text-20px mr-10px">调用日志</span>
        <span class="color-#666">{{ props.subtext }}</span>
      </div>
    </template>
    <div class="m-20px" v-loading="listState.loading">
      <el-form ref="formRef" :model="listState.search" inline @submit.native.prevent>
        <el-form-item prop="invokeUserName" label="名称：">
          <el-input
            v-model="listState.search.invokeUserName"
            placeholder="请输入名称"
            clearable
            @keyup.enter="handleDetail()"
          />
        </el-form-item>
      </el-form>
      <el-table :data="listState.tableData" style="width: 100%">
        <el-table-column prop="invokeTime" label="时间" v-slot="{ row }">
          {{ getLocalTime(row.invokeTime) }}
        </el-table-column>
        <el-table-column prop="invokeUserName" label="用户名" />
        <el-table-column prop="inputs" label="输入" width="400px">
          <template #default="scope">
            <div class="inputs">
              <div :class="scope.row.isInputsExpanded ? '' : 'content-fold'">
                {{ scope.row.inputs }}
              </div>
              <div
                class="text-expand"
                v-if="scope.row.inputs.length > 100"
                @click="handleInputsExpanded(scope.row.id)"
              >
                <i
                  title="收起"
                  v-if="scope.row.isInputsExpanded"
                  class="iconfont icon-xiala-copy-copy"
                ></i>
                <i title="展开" v-else class="iconfont icon-xiala-copy"></i>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="outputs" label="输出" width="400px">
          <template #default="scope">
            <el-tooltip
              class="box-item"
              effect="dark"
              :content="scope.row.outputs"
              placement="top-start"
            >
              <div class="content-fold cursor-pointer">
                {{ scope.row.outputs === 'null' ? '' : scope.row.outputs }}
              </div>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
      <div class="float-right py-20px" v-if="listState.total">
        <el-pagination
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
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, watch, watchEffect } from 'vue'
import { listHistoryDetails } from '@/api/probot-platformStatistics'
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: Array,
    default: () => []
  },
  subtext: {
    type: String,
    default: ''
  }
})
const emits = defineEmits(['update:modelValue', 'onOk'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

watch(
  () => props.modelValue,
  (val) => {
    console.log('props.modelValue', val)
    if (val) {
      handleDetail()
    }
  }
)
const listState = reactive({
  carouselDialogVisible: false,
  tableData: [],
  search: {
    pageSize: 10,
    page: 1,
    invokeTimeBegin: '',
    invokeTimeEnd: '',
    relateId: '',
    invokeUserName: ''
  },
  total: 0,
  loading: false
})
const handleCurrentChange = (page: number) => {
  handleDetail(page)
}
const handleSizeChange = (page: number) => {
  listState.search.pageSize = page
  handleDetail()
}
watchEffect(() => {
  listState.search = props.data
})

import { ElMessage } from 'element-plus'
async function handleDetail(page = 1) {
  try {
    listState.loading = true
    listState.search.page = page
    const res = await listHistoryDetails(listState.search)
    if (res.data) {
      listState.tableData = res.data.list
      listState.total = res.data.totalPage * listState.search.pageSize || 0
    } else {
      listState.tableData = []
      listState.total = 0
    }
  } catch (err) {
    console.log(err)
    ElMessage.error('获取失败')
  } finally {
    listState.loading = false
  }
}
function getLocalTime(n: string) {
  if (!n) return ''
  // .replace(/:\d{1,2}$/, ' ')
  return new Date(parseInt(n)).toLocaleString()
}
const handleInputsExpanded = (id) => {
  listState.tableData.forEach((item) => {
    if (item.id === id) {
      item.isInputsExpanded = !item.isInputsExpanded
    }
  })
}
</script>

<style lang="scss">
.inputs {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: end;
}

.text-expand {
  margin-left: 10px;
  cursor: pointer;
}

.content-fold {
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}
</style>
