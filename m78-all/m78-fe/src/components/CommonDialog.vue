<template>
  <el-dialog
    v-model="dialog"
    :width="props.width"
    :title="dialogTitle"
    :before-close="emit('close')"
    @open="emit('open')"
    :append-to-body="props['append-to-body']"
    :destroy-on-close="props['destroy-on-close']"
  >
    <slot></slot>
    <template #footer v-if="props.footerVisible">
      <div style="flex: auto">
        <el-button @click="dialog = false" v-if="props.footerCancleVisible">取消</el-button>
        <el-button type="primary" @click="confirmClick" v-if="props.footerSureVisible">{{
          props.footerSureTxt
        }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { defineProps, defineEmits, computed } from 'vue'

/**  传递过来的props和emit */
const props = defineProps({
  //是否可见
  dialogVisible: {
    type: Boolean,
    default: false
  },
  //标题
  dialogTitle: {
    type: String,
    default: '标题'
  },
  //是否显示底部
  footerVisible: {
    type: Boolean,
    default: true
  },
  footerCancleVisible: {
    type: Boolean,
    default: true
  },
  footerSureVisible: {
    type: Boolean,
    default: true
  },
  footerSureTxt: {
    type: String,
    default: '确认'
  },
  width: {
    type: String,
    default: '80%'
  },
  ['append-to-body']: {
    type: Boolean,
    default: false
  },
  ['destroy-on-close']: {
    type: Boolean,
    default: false
  }
})
const emit = defineEmits(['confirmClick', 'open', 'hide', 'close'])

const dialog = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emit('update:modelValue', val)
  }
})

/**  methods */
//确定
const confirmClick = () => {
  emit('confirmClick')
}
</script>
<style scoped></style>
