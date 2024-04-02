<!--
 * @Description: 
 * @Date: 2024-01-11 21:40:39
 * @LastEditTime: 2024-02-20 15:08:08
-->
<template>
  <div class="head">
    <el-button type="primary" link @click="custom">{{ t('translate.styleTitle') }}</el-button
    ><span>（{{ t('translate.styleTip') }}）</span>
  </div>
  <el-dialog
    v-model="dialogVisible"
    :title="t('translate.styleTitle')"
    width="50%"
    draggable
    :append-to-body="true"
  >
    <div v-html="t('translate.styleContent')"></div>
    <div v-html="t('translate.styleContent2')" class="boder-content"></div>
    <Dynamic ref="dynamicRef" :data="currentStyle"></Dynamic>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ t('common.cancle') }}</el-button>
        <el-button type="primary" @click="save"> {{ t('translate.styleSave') }} </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { t } from '@/locales'
import Dynamic from '@/components/common/Dynamic.vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: []
  }
})
const emits = defineEmits(['update:modelValue'])

const currentStyle = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const dialogVisible = ref(false)
const dynamicRef = ref()

const custom = () => {
  dialogVisible.value = true
  currentStyle.value = JSON.parse(localStorage.getItem('translationStyle'))
}

const save = () => {
  dialogVisible.value = false
  const value = dynamicRef.value.getArrValue()
  currentStyle.value = value
  localStorage.setItem('translationStyle', JSON.stringify(value))
}
</script>

<style scoped lang="scss">
.head {
  padding-bottom: 20px;
  display: flex;
  align-items: center;
  span {
    font-size: 12px;
    color: var(--oz-text-color-secondary);
  }
}
.boder-content {
  border-radius: 5px;
  margin: 10px 0;
  padding: 10px;
  border: 1px dotted #ddd;
  line-height: 24px;
}
</style>
