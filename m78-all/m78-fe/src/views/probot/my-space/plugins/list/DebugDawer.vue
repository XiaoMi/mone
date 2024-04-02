<template>
  <el-drawer v-model="visible" direction="rtl" class="debug-drawer" size="640">
    <template #header>
      <h2>{{ t('plugin.debugTitle') }}</h2>
    </template>
    <template #default>
      <Debug :id="props.id" @onOk="emits('onOk')" />
    </template>
    <template #footer>
      <div>
        <el-button @click="emits('update:modelValue', false)">{{ t('plugin.close') }}</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import Debug from './Debug.vue'
import { t } from '@/locales'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  id: {
    type: Number,
    default: 0
  }
})

const emits = defineEmits(['update:modelValue', 'onOk'])

const visible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
</script>

<style lang="scss">
.debug-drawer {
  .oz-drawer__header {
    border-color: #ddd;
    h2 {
      color: #333;
    }
  }
  .oz-drawer__body {
    padding: 24px 24px;
  }
  .oz-drawer__footer {
    border-top: 1px solid #ddd;
    padding: 10px;
  }
}
</style>
