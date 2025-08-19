<template>
  <el-popover :visible="visible" :width="560" trigger="click" @show="showFn" placement="bottom">
    <el-collapse style="width: 100%">
      <el-collapse-item
        :name="index"
        v-for="(param, index) in detail.botPluginMeta?.dubboMethodParamtypes"
        :key="index"
        style="width: 100%"
      >
        <template #title>
          <div class="param-box">{{ param }}</div>
        </template>
        <div class="input-box">
          <OutPutsTree
            v-model="inputs[index]"
            :showDesc="true"
            :isCanEmpty="true"
            :showDisplay="true"
            :disabled="true"
          />
        </div>
      </el-collapse-item>
    </el-collapse>
    <template #reference>
      <el-button type="primary" link class="detail-btn" :loading="loading">详情</el-button>
    </template>
  </el-popover>
</template>

<script setup lang="ts">
import { defineProps, ref } from 'vue'
import { getBotPluginParameter } from '@/api/probot'
import OutPutsTree from '@/views/workflow/work-flow/components/components/OutPutsTree.vue'

const props = defineProps({
  nodeData: {}
})
const loading = ref(false)
const detail = ref({})
const inputs = ref([])
const showFn = async () => {
  console.log('show', props.nodeData.coreSetting.pluginId)
  const id = props.nodeData.coreSetting.pluginId
  if (id) {
    loading.value = true
    const { code, data } = await getBotPluginParameter({ id })
    if (code == 0) {
      detail.value = data
      inputs.value = data?.botPluginMeta?.input.map((item) => [item])
    }
    loading.value = false
  }
}
</script>

<style scoped>
.detail-btn {
  font-size: 12px;
}
.param-box {
  border: solid 1px #eee;
  border-radius: 3px;
  height: 32px;
  line-height: 32px;
  padding: 0 10px;
  width: 500px;
  text-align: left;
  background: #f5f7fa;
  /* overflow-x: scroll; */
}
</style>
