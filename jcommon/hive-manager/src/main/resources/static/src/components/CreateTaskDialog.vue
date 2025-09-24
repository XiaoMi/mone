<template>
    <el-dialog
      v-model="dialogVisible"
      title="创建任务"
      width="500px"
    >
      <el-form :model="taskForm" label-width="100px">
        <!-- <el-form-item label="Server Agent">
          <el-select v-model="taskForm.serverAgentId" placeholder="请选择Server Agent" @change="handleServerAgentChange">
            <el-option
              v-for="agent in agentList"
              :key="agent.id"
              :label="agent.name"
              :value="agent.id"
            />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="技能">
          <el-select v-model="taskForm.skillId" placeholder="请选择要执行的技能">
            <el-option
              v-for="skill in skillList"
              :key="skill.id"
              :label="skill.name"
              :value="skill.id"
            />
          </el-select>
        </el-form-item> -->
        <el-form-item label="任务名称">
          <el-input v-model="taskForm.title" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="taskForm.description" type="textarea" placeholder="请输入任务描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleCancel">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </template>
  
  <script setup lang="ts">
  import { ref, defineProps, defineEmits, watch, onMounted } from 'vue'
  import type { CreateTaskRequest } from '@/api/task'
  import { getAgentList, getAgentSkills, type Agent, type Skill } from '@/api/agent'
  import { useRoute } from 'vue-router'
  
  const route = useRoute()
  
  const props = defineProps<{
    modelValue: boolean
  }>()
  
  const emit = defineEmits<{
    (e: 'update:modelValue', value: boolean): void
    (e: 'submit', form: CreateTaskRequest): void
  }>()
  
  const dialogVisible = ref(false)
  const taskForm = ref<CreateTaskRequest>({
    taskUuid: "",
    clientAgentId: null,
    serverAgentId: null,
    skillId: null,
    title: '',
    description: '',
    status: 'pending'
  })
  
  // const agentList = ref<Agent[]>([])
  // const skillList = ref<Skill[]>([])
  
  // 获取Agent列表
  // const fetchAgentList = async () => {
  //   try {
  //     const res = await getAgentList()
  //     if (res.data.data) {
  //       agentList.value = res.data.data
  //     }
  //   } catch (error) {
  //     console.error('获取Agent列表失败:', error)
  //   }
  // }
  
  // 获取技能列表
  // const handleServerAgentChange = async (agentId: number) => {
  //   try {
  //     const res = await getAgentSkills(agentId)
  //     if (res.data.data) {
  //       skillList.value = res.data.data
  //       // 清空之前选择的技能
  //       taskForm.value.skillId = null
  //     }
  //   } catch (error) {
  //     console.error('获取技能列表失败:', error)
  //   }
  // }
  
  onMounted(() => {
    // fetchAgentList()
    // 从URL中获取serverAgentId并设置
    const serverAgentId = Number(route.query.serverAgentId)
    if (serverAgentId) {
      taskForm.value.serverAgentId = serverAgentId
      taskForm.value.clientAgentId = serverAgentId
      // handleServerAgentChange(serverAgentId)
    }
  })
  
  watch(() => props.modelValue, (val) => {
    dialogVisible.value = val
  })
  
  watch(() => dialogVisible.value, (val) => {
    emit('update:modelValue', val)
    if (!val) {
      resetForm()
    }
  })
  
  const resetForm = () => {
    taskForm.value = {
      taskUuid: '',
      clientAgentId: null,
      serverAgentId: null,
      skillId: null,
      title: '',
      description: '',
      status: ''
    }
  }
  
  const handleCancel = () => {
    dialogVisible.value = false
  }
  
  const handleSubmit = () => {
    emit('submit', taskForm.value)
  }
  </script>