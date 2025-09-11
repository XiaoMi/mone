<template>
  <div class="custome-confirm">
    <el-form ref="confirmFormRef" :model="form">
      <p class="empty-p"></p>
      <template v-if="node?.resOutputs?.status == 5">
        <el-radio-group
          v-model="radio1"
          class="radio-group"
          size="small"
          @change="changeRadio"
          :disabled="loading"
        >
          <el-radio-button label="run">继续执行</el-radio-button>
          <el-radio-button label="runToNode" :disabled="isRetry"
            >跳转到某个节点继续执行</el-radio-button
          >
        </el-radio-group>
        <el-form-item v-if="radio1 == 'run'">
          <el-button type="primary" @click="confirmFn" size="small" :loading="loading"
            >继续执行</el-button
          >
        </el-form-item>
        <el-form-item
          v-if="radio1 == 'runToNode' && !isRetry"
          prop="nodeId"
          :rules="[{ required: true, message: '请输入节点ID' }]"
        >
          <i class="tips">跳转到节点:</i>
          <el-input-number
            v-model="form.nodeId"
            size="small"
            :controls="false"
            class="nodeid-input"
            placeholder="请输入节点ID"
          />
          <el-button @click="confirmFn" type="primary" size="small" :loading="loading"
            >继续执行</el-button
          >
        </el-form-item>
      </template>
    </el-form>
  </div>
</template>

<script setup>
import { Check } from '@element-plus/icons-vue'
import { ref, computed, inject } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

const route = useRoute()
const form = ref({
  nodeId: null
})
const loading = ref(false)
const changeRadio = () => {
  form.value.nodeId = null
}
// 这里的需要从地址栏获取
const flowRecordId = computed(() => route.query.flowRecordId)
const props = defineProps({
  modelValue: {},
  botFlowRecordId: {},
  isRetry: {
    default: false
  }
})
const node = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const operateFlowFn = inject('operateFlowFn')
const radio1 = ref('run')
const confirmFn = async () => {
  const valid = await confirmFormRef.value.validate()
  if (!valid) return
  loading.value = true
  const params = {
    cmd: radio1.value == 'run' ? 'manualConfirmFlow' : 'gotoFlow',
    flowRecordId: props.botFlowRecordId || flowRecordId.value,
    meta: {
      // nodeId暂时保存，后面会下掉
      nodeId: form.value.nodeId,
      gotoNodeId: form.value.nodeId,
      targetNodeId: node.value.id
    }
  }
  operateFlowFn && operateFlowFn(params)
  loading.value = false
}
const confirmFormRef = ref(null)
const validate = async () => {
  try {
    return await confirmFormRef.value.validate()
  } catch (error) {
    return false
  }
}
defineExpose({ validate })
</script>

<style lang="scss" scoped>
.empty-p {
  height: 20px;
}
.radio-group {
  margin-bottom: 10px;
}
.nodeid-input {
  margin: 0 2px;
  :deep(.oz-input__inner) {
    text-align: left;
  }
}
.tips {
  font-size: 12px;
  color: #606266;
}
</style>
