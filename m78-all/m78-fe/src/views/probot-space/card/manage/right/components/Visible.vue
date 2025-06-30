<!--
 * @Description: 
 * @Date: 2024-09-19 10:54:46
 * @LastEditTime: 2024-10-16 16:07:00
-->
<template>
  <el-form :model="form" ref="formRef">
    <FormItem
      title="显隐设置"
      tip="显隐设置：支持设置组件满足某条件时不展示。如可以通过列表的长度来控制是否在卡片中展示“查看更多”按钮"
    >
      <el-select v-model="form.visibilityType" placeholder="Select" style="width: 100%">
        <el-option v-for="(item, key) in listData" :key="key" :label="item" :value="key" />
      </el-select>
    </FormItem>
    <FormItem
      title="具体条件"
      v-if="
        form?.visibilityType == 'HideWithConditions' ||
        form?.visibilityType == 'DisplayWithConditions'
      "
    >
      <template #topRight>
        <el-button type="primary" link @click="edit">编辑</el-button>
      </template>
      <div class="condition-container">
        <template v-if="form.key">
          {{ variableList.find((item) => item.id == form.key)?.name || form.key }}
          <span>{{ form.operator }}</span
          >{{ '"' + form.value + '"' }}
        </template>
        <template v-else> 暂未配置 </template>
      </div>
    </FormItem>
    <VisibleDialog v-model="visible" :data="form" @onOk="visibleDialogCallback"> </VisibleDialog>
  </el-form>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import FormItem from './FormItem.vue'
import VisibleDialog from './VisibleDialog'
import { useProbotCardStore } from '@/stores/card'
const cardStore = useProbotCardStore()
const variableList = computed(() => cardStore.variableList)

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({})
  }
})

const emits = defineEmits(['update:modelValue'])

const form = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const listData = ref({})
onMounted(async () => {
  listData.value = await cardStore.getVisibility()
})

const visible = ref(false)
const visibleDialogCallback = (data) => {
  form.value = { ...form.value, ...data }
}

const edit = () => {
  visible.value = true
}
</script>

<style scoped lang="scss">
.condition-container {
  width: 100%;
  background-color: #f1f1f1;
  padding: 4px 10px;
  border-radius: 5px;
  color: #666;
  span {
    color: var(--oz-menu-active-color);
  }
}
</style>
