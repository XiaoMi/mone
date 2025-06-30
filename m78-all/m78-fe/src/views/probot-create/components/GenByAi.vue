<template>
  <el-popover placement="bottom-end" :width="500" trigger="manual" :visible="visible">
    <div class="top-box">
      <span>使用AI生成字段</span>
      <el-button link @click="switchShowPop"
        ><el-icon><Close /></el-icon
      ></el-button>
    </div>
    <el-form>
      <el-form-item label="模型">
        <LLMModelSel v-model="genForm.model" />
      </el-form-item>
      <el-form-item>
        <el-input
          v-model="genForm.tableDesc"
          placeholder="描述表的用途（例如记录阅读笔记）"
          type="textarea"
          :autosize="{ minRows: 1, maxRows: 10 }"
        />
      </el-form-item>
      <div class="btn-box">
        <el-button link @click="getAiGen" :loading="gening">
          <i class="iconfont icon-mofabang" style="margin-right: 6px"></i>
          生成</el-button
        >
      </div>
    </el-form>
    <template #reference>
      <el-button class="pop-btn" link @click="switchShowPop"
        >使用AI生成字段
        <el-icon><CaretBottom /></el-icon>
      </el-button>
    </template>
  </el-popover>
</template>

<script setup>
import { ref, defineExpose } from 'vue'
import { getAiGenApi } from '@/api/probot.ts'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const props = defineProps({
  workspaceId: {}
})
const visible = ref(false)
const genForm = ref({})
const gening = ref(false)
const route = useRoute()
const emits = defineEmits(['genFinish'])

const getAiGen = () => {
  const base = { botId: route.params.id, workspaceId: props.workspaceId }
  gening.value = true
  getAiGenApi({ ...genForm.value, ...base })
    .then((res) => {
      if (res.data && res.code == 0) {
        emits('genFinish', { ...res.data, tableDesc: genForm.value.tableDesc })
        hidePop()
      } else {
        ElMessage.warning(res.message || 'AI生成没有执行')
      }
    })
    .finally(() => {
      gening.value = false
    })
}
const switchShowPop = () => {
  visible.value = !visible.value
}
const hidePop = () => {
  visible.value = false
}
defineExpose({
  hidePop
})
</script>

<style lang="scss" scoped>
.top-box {
  display: flex;
  justify-content: space-between;
  padding-bottom: 10px;
}
.btn-box {
  display: flex;
  flex-direction: row-reverse;
}
</style>
