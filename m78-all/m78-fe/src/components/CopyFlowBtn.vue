<template>
  <el-tooltip effect="dark" :content="t('common.copy')" placement="top">
    <el-button
      :link="type == 'icon' ? false : true"
      @click.stop="copyFn"
      plain
      :size="size"
      :type="btnType"
      :disabled="disabled"
      >{{ type == 'text' ? t('common.copy') : '' }}
      <i class="iconfont icon-copy4" v-if="type == 'icon'"></i>
    </el-button>
  </el-tooltip>
</template>

<script setup>
import { t } from '@/locales'
import { copyFlowApi } from '@/api/workflow'

const emits = defineEmits(['copySuc'])
const props = defineProps({
  type: {
    default: 'icon'
  },
  originalId: {
    required: true
  },
  size: {
    default: 'default'
  },
  btnType: {},
  disabled: {
    default: false
  }
})
const copyFn = async () => {
  const p = {
    originalId: props.originalId
  }
  const { code, data } = await copyFlowApi(p)
  const flowId = data
  if (code != 0) return
  emits('copySuc', flowId)
}
</script>

<style lang="scss" scoped></style>
