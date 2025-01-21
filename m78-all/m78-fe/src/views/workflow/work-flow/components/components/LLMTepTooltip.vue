<template>
  <span class="label">Temperature</span>
  <el-tooltip effect="dark" :content="tips" placement="top" raw-content>
    <el-icon class="tem-tips"><Warning /></el-icon>
  </el-tooltip>
</template>

<script setup>
import { ref } from 'vue'
import { Warning } from '@element-plus/icons-vue'
const tips = ref(
  '用于控制大模型生成文本的随机性。较高时模型会生成更多</br>样化的文本，增加不确定性；较低时模型会生成高概率的词</br>汇，降低不确定性'
)
</script>

<style lang="scss" scoped>
.tem-tips {
  vertical-align: middle;
  margin-left: 2px;
  font-size: 12px;
}
</style>
