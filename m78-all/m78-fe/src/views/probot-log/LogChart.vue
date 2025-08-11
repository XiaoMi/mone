<!--
 * @Description: 
 * @Date: 2024-08-30 15:45:39
 * @LastEditTime: 2024-09-24 09:34:40
-->
<template>
  <div class="chart-wrap">
    <div class="chart-title">
      <h1>调用日志</h1>
      <p>日志记录了应用的执行情况</p>
    </div>
    <div class="filter">
      <div class="filter-content">
        <span>用户名：</span>
        <el-input v-model="invokeUserName" style="width: 240px" placeholder="请输入用户名" />
        <span>时间：</span>
        <el-date-picker
          v-model="logTime"
          type="datetimerange"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          format="YYYY-MM-DD HH:mm:ss"
          date-format="YYYY/MM/DD ddd"
          time-format="A hh:mm:ss"
        />
      </div>
      <el-button type="primary" @click="getList" plain>查询</el-button>
    </div>
    <div class="chart-box">
      <div class="chart-item" style="width: 100%">
        <div class="chart-content">
          <el-table :data="tableData" style="width: 100%">
            <el-table-column prop="invokeTime" label="时间">
              <template #default="scope">
                {{ dateFormat(scope.row.invokeTime, 'yyyy-mm-dd HH:MM:ss') }}
              </template>
            </el-table-column>
            <el-table-column prop="invokeUserName" label="用户名" />
            <el-table-column prop="invokeWay" label="调用方式">
              <template #default="scope">
                {{ invokeWay[scope.row.invokeWay] }}
              </template>
            </el-table-column>
            <el-table-column prop="inputs" label="输入">
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
            <el-table-column prop="outputs" label="输出">
              <template #default="scope">
                <div class="outputs">
                  <div :class="scope.row.isOutputsExpanded ? '' : 'content-fold'">
                    {{ scope.row.outputs }}
                  </div>
                  <div
                    class="text-expand"
                    v-if="scope.row.outputs.length > 100"
                    @click="handleOutputsExpanded(scope.row.id)"
                  >
                    <i
                      title="收起"
                      v-if="scope.row.isOutputsExpanded"
                      class="iconfont icon-xiala-copy-copy"
                    ></i>
                    <i title="展开" v-else class="iconfont icon-xiala-copy"></i>
                  </div>
                </div>
              </template>
            </el-table-column>
          </el-table>
          <CommonPagination
            @handlePage="handlePage"
            :pageInfo="pageInfo"
            layout="'sizes, prev, pager, next, jumper"
          >
          </CommonPagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { listHistoryDetails } from '@/api/probot-log'
import { useRoute } from 'vue-router'
import dateFormat from 'dateformat'
import CommonPagination from '@/components/CommonPagination.vue'
import moment from 'moment'

const logTime = ref('')
const invokeUserName = ref()
const invokeWay = ['页面', '接口', '系统内部', '调试', 'WebSocket']
const route = useRoute()
const tableData = ref([])
const pageInfo = reactive<{
  currentPage: number //当前页
  total: number //总条数
  pageSize: number //每一页条数
}>({
  currentPage: 1,
  total: 0,
  pageSize: 10
}) //分页

//操作分页
const handlePage = (opts?: { currentPage?: number; pageSize?: number }) => {
  pageInfo.pageSize = opts?.pageSize || pageInfo.pageSize
  pageInfo.currentPage = opts?.currentPage || pageInfo.currentPage
  getList()
}
const getList = async () => {
  await listHistoryDetails({
    relateId: route?.query?.botId,
    page: pageInfo.currentPage,
    pageSize: pageInfo.pageSize,
    invokeUserName: invokeUserName.value,
    invokeTimeBegin: logTime.value[0] ? moment(logTime.value[0]).valueOf() : '',
    invokeTimeEnd: logTime.value[1] ? moment(logTime.value[1]).valueOf() : ''
  }).then((res) => {
    res.data.list = res.data.list.map((item) => {
      return {
        ...item,
        isInputsExpanded: false,
        isOutputsExpanded: false
      }
    })
    tableData.value = res.data.list
    pageInfo.total = res.data.totalPage * pageInfo.pageSize
  })
}

// 处理展开和收起
const handleOutputsExpanded = (id) => {
  tableData.value.forEach((item) => {
    if (item.id === id) {
      item.isOutputsExpanded = !item.isOutputsExpanded
    }
  })
}

const handleInputsExpanded = (id) => {
  tableData.value.forEach((item) => {
    if (item.id === id) {
      item.isInputsExpanded = !item.isInputsExpanded
    }
  })
}
onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.filter-content {
  display: flex;
  align-items: center;
  padding-right: 10px;
}
.outputs,
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
