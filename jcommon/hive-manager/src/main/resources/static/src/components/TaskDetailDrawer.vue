<template>
    <el-drawer
      v-model="visible"
      title="任务详情"
      size="500px"
      :destroy-on-close="true"
    >
      <div v-if="task" class="task-detail">
        <div class="detail-item">
          <label>任务ID：</label>
          <span>{{task.id}}</span>
        </div>
        <div class="detail-item">
          <label>任务名称：</label>
          <span>{{task.title}}</span>
        </div>
        <div class="detail-item">
          <label>任务描述：</label>
          <span>{{task.description}}</span>
        </div>
        <div class="detail-item">
          <label>Client Agent ID：</label>
          <span>{{task.clientAgentId}}</span>
        </div>
        <div class="detail-item">
          <label>Server Agent ID：</label>
          <span>{{task.serverAgentId}}</span>
        </div>
        <div class="detail-item">
          <label>状态：</label>
          {{task.status}}
        </div>
        <div class="detail-item">
          <label>创建时间：</label>
          <span>{{formatDate(task.ctime)}}</span>
        </div>
      </div>
    </el-drawer>
  </template>
  
  <script setup lang="ts">
  import { computed } from 'vue'
  import type { Task } from '@/api/task'
  
  const props = defineProps<{
    modelValue: boolean,
    task: Task | null
  }>()
  
  const emit = defineEmits<{
    (e: 'update:modelValue', value: boolean): void
  }>()
  
  const visible = computed({
    get: () => props.modelValue,
    set: (value) => emit('update:modelValue', value)
  })
  
  const getStatusType = (status: string) => {
    const statusMap: Record<string, string> = {
      'running': 'primary',
      'completed': 'success',
      'failed': 'danger',
      'pending': 'warning'
    }
    return statusMap[status] || 'info'
  }
  
  const formatDate = (date: string) => {
    return new Date(date).toLocaleString()
  }
  </script>
  
  <style scoped>
  .task-detail {
    padding: 20px;
    color: #ffffff;
  }
  
  .detail-item {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
  }
  
  .detail-item label {
    width: 180px;
    color: #31e8f9;
    font-weight: 500;
  }
  
  .detail-item span {
    color: #ffffff;
  }
  </style>