<template>
  <el-dialog v-model="show" title="任务绑定" width="1120" @before-close="beforeClose">
    <template #header>
      <div class="my-header">
        <h2>任务绑定</h2>
      </div>
    </template>
    <el-form ref="formRef" :model="form" label-position="top" :rules="rules">
      <el-form-item prop="taskName" label="任务名称">
        <el-input v-model="form.taskName" />
      </el-form-item>
      <el-form-item prop="taskType" label="任务类型">
        <TaskTypeSel v-model="form.taskType" @change="changeType" />
      </el-form-item>
      <el-form-item label="固定频率" prop="scheduledTime" v-if="form.taskType == 1">
        <el-input-number v-model="form.scheduledTime" :min="1" placeholder="固定频率（单位秒）" />
        <i style="padding-left: 4px">秒</i>
      </el-form-item>
      <el-form-item label="执行时间" prop="scheduledTime" v-if="form.taskType == 2">
        <el-date-picker
          v-model="form.scheduledTime"
          type="datetime"
          placeholder="请选择时间"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <div class="time-sel-box" v-if="form.taskType == 0">
        <el-form-item label="cron配置" prop="coreType">
          <CornYMDSel v-model="form.coreType" @change="changeCoreT" />
        </el-form-item>
        <el-form-item class="empty-label" prop="taskDetail.week" v-if="form.coreType == 'week'">
          <WeekdaySel v-model="form.taskDetail.week" />
        </el-form-item>
        <el-form-item class="empty-label" prop="taskDetail.month" v-if="form.coreType == 'month'">
          <DaySel v-model="form.taskDetail.month" />
        </el-form-item>
        <el-form-item class="empty-label" prop="timer">
          <el-time-picker v-model="form.timer" placeholder="请选择时间" value-format="H:m:s" />
        </el-form-item>
      </div>
      <el-form-item label="描述" prop="taskDescription">
        <el-input v-model="form.taskDescription" type="textarea"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="show = false">取消</el-button>
        <el-button type="primary" @click="confirm"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, watch, ref } from 'vue'
import { createTask, updateTask } from '@/api/probot-task.ts'
import { useRoute } from 'vue-router'
import CornYMDSel from '@/views/probot/components/CornYMDSel'
import WeekdaySel from '@/views/probot/components/WeekdaySel'
import DaySel from '@/views/probot/components/DaySel'
import TaskTypeSel from '@/views/probot/components/TaskTypeSel'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {},
  formData: {},
  itemV: {} // 编辑回显数据
})

const rules = ref({
  taskName: [
    {
      required: true,
      message: '任务名称为必填项',
      trigger: 'blur'
    }
  ],
  taskType: [
    {
      required: true,
      message: '任务类型为必填项',
      trigger: 'blur'
    }
  ],
  scheduledTime: [
    {
      required: true,
      message: '执行时间必填项',
      trigger: 'blur'
    }
  ],
  coreType: [
    {
      required: true,
      message: '此项为必填项',
      trigger: 'blur'
    }
  ],
  'teskDetail.week': [
    {
      required: true,
      message: '此项为必填项',
      trigger: 'blur'
    }
  ],
  'teskDetail.day': [
    {
      required: true,
      message: '此项为必填项',
      trigger: 'blur'
    }
  ],
  timer: [
    {
      required: true,
      message: '此项为必填项',
      trigger: 'blur'
    }
  ],
  taskDescription: [
    {
      required: true,
      message: '此项为必填项',
      trigger: 'blur'
    }
  ]
})
const visible = ref('false')
const emits = defineEmits(['update:modelValue', 'updateSuc'])
const form = ref({
  taskDetail: {}
})
const genForm = ref({})
const formRef = ref(null)

const show = computed({
  get: () => props.modelValue,
  set: (val) => emits('update:modelValue', val)
})
const reqFn = () => {
  form.value = {
    ...form.value,
    ...props.itemV
  }
  const { taskDetail } = props.itemV
  if (!taskDetail) return
  const { month, week, hour, minute, second } = taskDetail
  if (month != 'null') {
    form.value.coreType = 'month'
  } else if (week != 'null') {
    form.value.coreType = 'week'
  } else {
    form.value.coreType = 'day'
  }
  form.value.timer = `${hour}:${minute}:${second}`
}
const route = useRoute()

const transformTimer = () => {
  const { timer } = form.value
  if (!timer) return {}
  const timerArr = timer.split(':')
  return {
    hour: timerArr[0],
    minute: timerArr[1],
    second: timerArr[2]
  }
}
const transformMDW = () => {
  const { coreType, taskType, taskDetail } = form.value
  if (taskType != 0) return {}
  if (coreType == 'day') return {}
  if (coreType == 'month') {
    return {
      month: taskDetail.month
    }
  }
  if (coreType == 'week') {
    return {
      week: taskDetail.week
    }
  }
}

const confirm = async () => {
  const valid = formRef.value.validate()
  if (!valid) return
  const p = {
    botId: route.params.id,
    ...form.value,
    taskDetail: {
      ...transformMDW(),
      ...transformTimer()
    },
    botId: route.params.id,
    coreType: form.value.taskType != 0 ? null : form.value.coreType
  }
  const reqName = p.id ? updateTask : createTask
  reqName(p).then((res) => {
    if (res.code == 0) {
      show.value = false
      ElMessage.success(`${p.id ? '更新' : '创建'}成功`)
      emits('updateSuc')
    } else {
      ElMessage.warning(res.message)
    }
  })
}

const changeType = () => {
  form.value.scheduledTime = null
  form.value.timer = null
}

const changeCoreT = () => {
  form.value.taskDetail.month = null
  form.value.taskDetail.week = null
  form.value.taskDetail.day = null
}
const beforeClose = () => {
  if (!formRef.value) return
  formRef.value.resetFields()
}

watch(
  () => show.value,
  (val) => {
    if (val) {
      reqFn()
    }
  },
  {
    immediate: true
  }
)
</script>

<style lang="scss" scoped>
.my-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pop-btn {
  margin-right: 10px;
}
.btn-box {
  display: flex;
  flex-direction: row-reverse;
}
.list-li {
  display: flex;
  :deep(.oz-form-item + .oz-form-item) {
    margin-left: 20px;
  }
}
.list-ul {
  border: solid 1px #eee;
  padding: 20px;
}
.tooltip-save {
  width: 610px;
  padding: 20px;
  :deep(.oz-table__body-wrapper .cell) {
    color: #fff;
  }
  .desc {
    font-size: 15px;
  }
}
.time-sel-box {
  display: flex;
}
.empty-label {
  margin-top: 30px;
}
</style>
