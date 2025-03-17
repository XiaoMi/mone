<template>
  <el-form-item :prop="props.propName" :label="props.label">
    <el-table :data="params" style="width: 100%">
      <el-table-column prop="key" label="key" width="220">
        <template #default="scoped">
          <el-input
            :autofocus="false"
            v-model="scoped.row.key"
            :placeholder="t('plugin.pleaseEnter')" 
            style="height: 25px;"
            size="small"
          />
        </template>
      </el-table-column>
      <el-table-column prop="value" label="value">
        <template #default="scoped">
          <el-input
            :autofocus="false"
            v-model="scoped.row.value"
            :placeholder="t('plugin.pleaseEnter')" 
            style="height: 25px;"
            size="small"
          />
        </template>
      </el-table-column>
      <el-table-column :label="t('plugin.operate')" width="80">
        <template #default="scoped">
          <el-button link @click="handleAdd(scoped)" :icon="Plus"> </el-button>
          <el-button link :disabled="params.length == 1" @click="handleDel(scoped)" :icon="Minus">
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-form-item>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { t } from '@/locales'
import { Plus, Minus } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => {
      return []
    }
  },
  label: {
    type: String,
    default: ''
  },
  propName: {
    type: String,
    default: () => ''
  }
})

const emits = defineEmits(['update:modelValue'])

const params = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const handleAdd  = (scoped) => {
  params.value.push({ key: '', value: '' })
}

const handleDel = (scoped) => {
  emits(
    'update:modelValue',
    params.value.filter((_, i) => i !== scoped.$index)
  )
}
</script>
<style lang="scss" scoped>
.oz-table {
  &:deep(tr) {
    th.oz-table__cell {
      background-color: #f5f7fa;
      padding: 4px 0;
      color: #333;
      font-weight: normal;
      font-size: 12px;
    }
  }
}
</style>
