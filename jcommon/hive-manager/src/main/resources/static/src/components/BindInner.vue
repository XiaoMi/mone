<template>
    <el-dialog
      v-model="dialogVisible"
      title="绑定内部账号"
      width="500px"
    >
      <div class="inner-container">
        <div class="inner-display">
          <span>当前内部账号：</span>
          <el-input
            v-model="currentInner"
            type="text"
            class="inner-input"
            placeholder="请输入内部账号"
          >
          </el-input>
        </div>
      </div>
      <!-- <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="handleBindInner" :disabled="!currentInner">
            绑定
          </el-button>
        </span>
      </template> -->
    </el-dialog>
  </template>
  
  <script setup lang="ts">
  import { ref, onMounted } from 'vue'
  import { ElMessage } from 'element-plus'
  import { getInfo, bindInner } from '@/api/toekn'
  
  const dialogVisible = ref(false)
  const currentInner = ref('')
  
  // 获取
  const fetchInner = async () => {
    try {
      const res = await getInfo()
      if (res.data.code === 200) {
        currentInner.value = res.data.data?.internalAccount || ''
      }
    } catch (error) {
      ElMessage.error('获取Token失败')
    }
  }
  
  // 创建新
  const handleBindInner = async () => {
    try {
      const res = await bindInner({
        internalAccount: currentInner.value
      })
      if (res.data.code === 200) {
        ElMessage.success('绑定成功')
        dialogVisible.value = false
      }
    } catch (error) {
      ElMessage.error('绑定失败')
    }
  }
  
  // 打开弹窗时获取
  const open = () => {
    dialogVisible.value = true
    fetchInner()
  }
  
  defineExpose({
    open
  })
  </script>
  
  <style scoped>
  .inner-container {
    padding: 20px 0;
  }
  
  .inner-display {
    display: flex;
    align-items: center;
    gap: 10px;
  }
  
  .inner-input {
    flex: 1;
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
  }
  
  </style>
  