<template>
  <div class="hello-world">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-icon><Star /></el-icon>
          <span>{{ title }}</span>
        </div>
      </template>
      
      <div class="content">
        <p>{{ message }}</p>
        <el-button type="primary" @click="handleClick">
          <el-icon><Thumb /></el-icon>
          点击我
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Star, Thumb } from '@element-plus/icons-vue'

interface Props {
  title?: string
  message?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: 'Hello World',
  message: '这是一个示例组件'
})

const emit = defineEmits<{
  click: [value: string]
}>()

const handleClick = () => {
  emit('click', 'Hello from HelloWorld component!')
}
</script>

<style scoped>
.hello-world {
  margin: 20px 0;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.card-header .el-icon {
  margin-right: 8px;
  color: #409eff;
}

.content {
  text-align: center;
}

.content p {
  margin: 15px 0;
  color: #606266;
}
</style>

