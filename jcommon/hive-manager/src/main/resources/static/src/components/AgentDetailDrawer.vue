<template>
    <el-drawer
      v-model="visible"
      direction="rtl"
      size="60%"
      :title="agentDetail?.name"
      :destroy-on-close="true"
      class="agent-detail-drawer"
      @close="handleClose"
    >
      <template v-if="agentDetail">
        <div class="detail-content">
          <div class="detail-item">
            <div class="label">名称</div>
            <div class="value">{{ agentDetail.name }}</div>
          </div>
          <div class="detail-item">
            <div class="label">描述</div>
            <div class="value description">{{ agentDetail.description }}</div>
          </div>
          <div class="detail-item">
            <div class="label">Agent URL</div>
            <div class="value url">{{ agentDetail.agentUrl }}</div>
          </div>
          <div class="detail-item">
            <div class="label">是否公开</div>
            <div class="value">
              <el-tag :type="agentDetail.isPublic ? 'success' : 'info'">
                {{ agentDetail.isPublic ? '是' : '否' }}
              </el-tag>
            </div>
          </div>
          <div class="detail-item">
            <div class="label">tools</div>
            <div class="value">{{ agentDetail.toolMap }}</div>
          </div>
          <div class="detail-item">
            <div class="label">mcpTools</div>
            <div class="value">{{ agentDetail.mcpToolMap }}</div>
          </div>
          <div class="detail-item">
            <div class="label">创建时间</div>
            <div class="value">{{ formatDate(agentDetail.ctime) }}</div>
          </div>
          <div class="detail-item">
            <div class="label">更新时间</div>
            <div class="value">{{ formatDate(agentDetail.utime) }}</div>
          </div>
          
          <!-- 添加技能列表组件 -->
          <div class="skills-section">
            <SkillList :agent-id="agent.id" />
          </div>
        </div>
      </template>
    </el-drawer>
  </template>
  
  <script setup lang="ts">
  import { ref, watch, computed } from 'vue'
  import type { Agent } from '@/api/agent'
  import { getAgentDetail } from '@/api/agent'
  import SkillList from './SkillList.vue'
  
  const props = defineProps<{
    modelValue: boolean
    agent: object
  }>()
  
  const emit = defineEmits<{
    (e: 'update:modelValue', value: boolean): void
  }>()
  
  const visible = ref(props.modelValue)
  
  const agentDetail = ref<Agent | null>(null)
  
  const fetchAgentDetail = async () => {
    try {
      const { data } = await getAgentDetail(props.agent.id)
      agentDetail.value = data.data || null
    } catch (error) {
      console.error('获取Agent详情失败:', error)
    }
  }
  
  watch(() => props.modelValue, (val) => {
    visible.value = val
    if (val) {
      fetchAgentDetail()
    }
  }, { immediate: true })

  const handleClose = () => {
    emit('update:modelValue', false)
  }

  const formatDate = (date: string) => {
    return new Date(date).toLocaleString()
  }
  </script>
  
  <style>
  .detail-content {
    padding: 20px;
  }
  
  .detail-item {
    margin-bottom: 12px;
    transition: all 0.3s ease;
    padding: 12px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 16px;
    
    &:hover {
      background: rgba(48, 54, 61, 0.2);
      transform: translateX(4px);
    }
    
    .label {
      color: #31e8f9;
      font-size: 14px;
      margin-bottom: 0;
      text-shadow: 0 0 10px rgba(49, 232, 249, 0.3);
      min-width: 80px;
    }
    
    .value {
      color: #fff;
      font-size: 16px;
      word-break: break-all;
      flex: 1;
    }
  
    .description {
      white-space: pre-wrap;
    }
  
    .url {
      font-family: monospace;
      padding: 12px;
      background: rgba(22, 27, 34, 0.4);
      border-radius: 8px;
      border: 1px solid rgba(49, 232, 249, 0.3);
      transition: all 0.3s ease;
      
      &:hover {
        border-color: rgba(49, 232, 249, 0.8);
        box-shadow: 0 0 15px rgba(49, 232, 249, 0.2);
      }
    }
  }
  
  .skills-section {
    margin-top: 24px;
    padding: 20px;
    background: rgba(48, 54, 61, 0.2);
    border-radius: 8px;
  }
  
  .skills-title {
    color: #31e8f9;
    font-size: 18px;
    margin-bottom: 16px;
    text-shadow: 0 0 10px rgba(49, 232, 249, 0.3);
  }
  </style>