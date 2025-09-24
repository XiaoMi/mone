
<template>
  <div class="probot-flow-form">
    <div class="flow-plug">
      <BaseGroup
        class="head-container"
        title="任务"
        tooltip="暂定"
        :btn="{
          name: '创建',
          icon: 'icon-plus1',
          click: addPlug,
          size: 'small',
          disabled: props.disabled
        }"
      ></BaseGroup>
      <el-table :data="list" style="width: 100%">
        <el-table-column prop="taskName" label="任务名称" />
        <el-table-column prop="name" label="任务类型">
          <template #default="scope">
            <TaskTypeSel v-model="scope.row.taskType" disabled style="width: 100px" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="cron配置">
          <template #default="scope">
            <IntervalCom :itemV="scope.row" />
          </template>
        </el-table-column>
        <el-table-column prop="scheduledTime" label="间隔配置">
          <template #default="scope">
            <span v-if="scope.row.taskType != 0">
              {{ scope.row.scheduledTime }}
              <i v-if="scope.row.taskType == 1">秒</i>
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="taskDescription" label="描述" />
        <el-table-column prop="address" label="操作" width="260">
          <template #default="scope">
            <el-button type="primary" @click="editItem(scope.row)">编辑</el-button>
            <el-button type="primary" @click="excuteItem(scope.row)">执行</el-button>
            <el-button
              :type="scope.row.status ? 'warning' : 'primary'"
              @click="changeItem(scope.row)"
              >{{ scope.row.status == 1 ? '暂停' : '恢复' }}</el-button
            >
            <el-button type="danger" @click="removeItem(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <BindTaskForm v-model="showBind" :formData="formData" :itemV="itemV" @updateSuc="updateSuc" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import BindTaskForm from './BindTaskForm.vue'
import BaseGroup from '../components/BaseGroup.vue'
import { queryTask, deleteTask, executeTask, changeStatus } from '@/api/probot-task'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import TaskTypeSel from './TaskTypeSel.vue'
import IntervalCom from './IntervalCom'

const showBind = ref(false)
const bindType = ref('add')
const itemV = ref({})
const addPlug = () => {
  showBind.value = true
  bindType.value = 'add'
  itemV.value = {
    id: null,
    taskType: null,
    taskDescription: '',
    taskName: '',
    coreType: null,
    timer: null
  }
}
const props = defineProps({
  formData: {
    type: Object,
    required: true
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const changeItem = (item: any) => {
  const desc = item.status == 1 ? '暂停' : '恢复'
  ElMessageBox.confirm(`确认${desc}此任务吗？`, 'Warning', {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    title: `${desc}任务`
  }).then(async () => {
    changeStatus({ id: item.id }).then((res) => {
      ElMessage.success(`${desc}成功`)
      getList()
    })
  })
}
const removeItem = (item: any) => {
  ElMessageBox.confirm('确认删除此任务吗？', 'Warning', {
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    title: '删除任务'
  }).then(async () => {
    deleteTask({ id: item.id }).then((res) => {
      ElMessage.success('删除成功')
      getList()
    })
  })
}
const editItem = (item) => {
  showBind.value = true
  bindType.value = 'edit'
  itemV.value = item
}
const excuteItem = (item) => {
  executeTask({ id: item.id }).then((res) => {
    if (res.code == 0) {
      ElMessage.success('执行成功')
    }
  })
}

const list = ref([])
const route = useRoute()
const getList = () => {
  queryTask({ botId: route.params.id }).then((res) => {
    list.value = res.data || []
  })
}
const updateSuc = () => {
  getList()
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.head-container {
  padding-bottom: 10px;
}
.list-container {
  border-top: 1px solid #ddd;
  li {
    border-bottom: 1px solid #ddd;
    display: flex;
    align-items: center;

    .left {
      padding: 10px 0;
      flex: 1;
    }
  }
}
.flow-workflow {
  padding-top: 20px;
  .list-container li {
    padding: 10px 0;
  }
}

.child-container {
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  padding: 10px 5px;
  font-size: 14px;
  line-break: 20px;
  color: rgba(0, 0, 0, 0.7);
  &:last-child {
    border: none;
  }
}
.btn-container {
  display: flex;
  justify-content: space-between;
  width: 300px;
  float: right;
}
</style>
