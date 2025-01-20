<template>
  <el-collapse v-model="activeNames" v-if="resOutputs.errorInfo">
    <el-collapse-item name="1">
      <template #title>
        错误
        <CopyBtn
          :arr="[
            {
              name: 'Error',
              value: resOutputs.errorInfo
            }
          ]"
        />
      </template>
      <div class="outputs">
        <p class="text-p err">Error:{{ resOutputs.errorInfo }}</p>
      </div>
    </el-collapse-item>
  </el-collapse>
</template>

<script setup>
import { ref } from 'vue'
import CopyBtn from './CopyBtn'
const props = defineProps({
  resOutputs: {}
})
const activeNames = ref(['1'])
</script>

<style lang="scss" scoped>
.err {
  word-break: break-word;
}
</style>
