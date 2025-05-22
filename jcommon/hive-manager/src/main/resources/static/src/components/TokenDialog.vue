<template>
  <el-dialog
    v-model="dialogVisible"
    title="Token 管理"
    width="500px"
  >
    <div class="token-container">
      <div v-if="currentToken" class="token-display">
        <span>当前Token：</span>
        <el-input
          v-model="currentToken"
          readonly
          type="text"
          class="token-input"
        >
          <template #append>
            <el-button type="primary" class="copy-btn" @click="copyToken">复制</el-button>
          </template>
        </el-input>
      </div>
      <div v-else class="no-token">
        暂无Token
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="handleCreateToken" v-if="!currentToken">
          创建新Token
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getInfo, createToken } from '@/api/toekn'

const dialogVisible = ref(false)
const currentToken = ref('')

// 获取Token
const fetchToken = async () => {
  try {
    const res = await getInfo()
    if (res.data.code === 200) {
      currentToken.value = res.data.data.token || ''
    }
  } catch (error) {
    ElMessage.error('获取Token失败')
  }
}

// 创建新Token
const handleCreateToken = async () => {
  try {
    const res = await createToken()
    if (res.data.code === 200) {
      currentToken.value = res.data.data || ''
      ElMessage.success('创建Token成功')
    }
  } catch (error) {
    ElMessage.error('创建Token失败')
  }
}

// 复制Token
const copyToken = async () => {
  try {
    await navigator.clipboard.writeText(currentToken.value)
    ElMessage.success('复制成功')
  } catch (error) {
    ElMessage.error('复制失败')
  }
}

// 打开弹窗时获取Token
const open = () => {
  dialogVisible.value = true
  fetchToken()
}

defineExpose({
  open
})
</script>

<style scoped>
.token-container {
  padding: 20px 0;
}

.token-display {
  display: flex;
  align-items: center;
  gap: 10px;
}

.token-input {
  flex: 1;
}

.no-token {
  text-align: center;
  color: #909399;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.copy-btn {
}
</style>
