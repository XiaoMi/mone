<template>
  <el-dropdown @command="handleCommand">
    <span>
      <el-button size="small" plain>
        <span class="menu-show">
          <el-icon> <component :is="showV?.iconComponent" /></el-icon>
          <i class="label-i">{{ showV?.label }}</i>
        </span>
        <el-icon class="el-icon--right"><arrow-down /></el-icon>
      </el-button>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="item in options"
          :key="item.value"
          :command="item.value"
          :disabled="item.value == innerV"
        >
          <el-icon> <component :is="item.iconComponent" /></el-icon>
          {{ item.label }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { t } from '@/locales'
const options = computed(() => [
  {
    label: t('excle.read'),
    value: 'read',
    iconComponent: 'Reading'
  },
  {
    label: t('excle.edit'),
    value: 'edit',
    iconComponent: 'EditPen'
  }
])
const props = defineProps({
  modelValue: {
    type: String,
    default: 'read'
  }
})
const emits = defineEmits(['update:modelValue', 'change'])
const innerV = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
    emits('change', val)
  }
})
const handleCommand = (command) => {
  innerV.value = command
  const obj = options.value.find((item) => item.value == command)
  ElMessage.success(t('excle.hasChangeMode', { modeName: obj.label }))
}

const showV = computed(() => {
  return options.value.find((item) => item.value == innerV.value)
})
</script>
<style scoped>
.menu-show {
  display: flex;
  align-items: center;
  padding-right: 5px;
}
.label-i {
  margin-left: 3px;
}
</style>
