<!--
 * @Description: 
 * @Date: 2024-03-15 20:21:41
 * @LastEditTime: 2024-03-15 20:28:23
-->
<template>
  <el-dialog v-model="dialogVisible" width="500" title="参数列表">
    <div class="bind-dialog-content" v-if="parameterData">
      <div class="parameter-module">
        <h3>入参</h3>
        <json-viewer boxed copyable :value="parameterData?.input" expand-depth="20"></json-viewer>
      </div>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElDialog } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default: () => ({})
  }
})
const emits = defineEmits(['update:modelValue'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const parameterData = ref()

watch(
  () => props.data,
  (val) => {
    parameterData.value = val
  }
)
</script>

<style lang="scss">
.bind-dialog-content {
  .parameter-module {
    padding-bottom: 10px;
    h3 {
      font-size: 16px;
      line-height: 24px;
      padding: 10px 4px;
      color: rgb(71, 85, 105);
    }
  }
}
</style>
