<template>
  <div class="counter">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-icon><DataAnalysis /></el-icon>
          <span>计数器示例</span>
        </div>
      </template>

      <div class="counter-content">
        <el-row :gutter="20" justify="center">
          <el-col :span="12">
            <el-card shadow="hover" class="counter-card">
              <div class="counter-display">
                <el-icon size="48" color="#409eff">
                  <Calculator />
                </el-icon>
                <div class="counter-value">{{ counterStore.count }}</div>
                <div class="counter-double">双倍值: {{ counterStore.doubleCount }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover" class="actions-card">
              <h3>操作面板</h3>
              <div class="action-buttons">
                <el-button 
                  type="primary" 
                  size="large" 
                  @click="counterStore.increment"
                  :icon="Plus"
                >
                  增加
                </el-button>
                <el-button 
                  type="danger" 
                  size="large" 
                  @click="counterStore.decrement"
                  :icon="Minus"
                >
                  减少
                </el-button>
                <el-button 
                  type="warning" 
                  size="large" 
                  @click="counterStore.reset"
                  :icon="Refresh"
                >
                  重置
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-divider />

        <div class="name-section">
          <h3>名称管理</h3>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-input
                v-model="newName"
                placeholder="输入新的名称"
                :prefix-icon="Edit"
              />
            </el-col>
            <el-col :span="12">
              <el-button 
                type="success" 
                @click="updateName"
                :icon="Check"
              >
                更新名称
              </el-button>
            </el-col>
          </el-row>
          <div class="current-name">
            <el-tag type="info" size="large">
              当前名称: {{ counterStore.name }}
            </el-tag>
          </div>
        </div>

        <el-divider />

        <div class="store-info">
          <h3>Store 信息</h3>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="当前计数">{{ counterStore.count }}</el-descriptions-item>
            <el-descriptions-item label="双倍计数">{{ counterStore.doubleCount }}</el-descriptions-item>
            <el-descriptions-item label="当前名称">{{ counterStore.name }}</el-descriptions-item>
            <el-descriptions-item label="Store ID">counter</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useCounterStore } from '../stores/counter'
import { Plus, Minus, Refresh, Edit, Check } from '@element-plus/icons-vue'

const counterStore = useCounterStore()
const newName = ref('')

const updateName = () => {
  if (newName.value.trim()) {
    counterStore.setName(newName.value.trim())
    newName.value = ''
    ElMessage.success('名称更新成功！')
  } else {
    ElMessage.warning('请输入有效的名称')
  }
}
</script>

<style scoped>
.counter {
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.card-header .el-icon {
  margin-right: 8px;
  color: #409eff;
}

.counter-content {
  text-align: center;
}

.counter-card {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.counter-display {
  text-align: center;
}

.counter-value {
  font-size: 48px;
  font-weight: bold;
  color: #409eff;
  margin: 10px 0;
}

.counter-double {
  font-size: 16px;
  color: #909399;
}

.actions-card {
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.actions-card h3 {
  margin: 0 0 20px 0;
  color: #303133;
}

.action-buttons {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}

.name-section {
  margin: 20px 0;
}

.name-section h3 {
  color: #303133;
  margin-bottom: 15px;
}

.current-name {
  margin-top: 15px;
}

.store-info {
  margin-top: 20px;
}

.store-info h3 {
  color: #303133;
  margin-bottom: 15px;
}
</style>

