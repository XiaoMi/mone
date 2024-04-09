<template>
  <el-form-item :label="props.label">
    <el-table :data="params" style="width: 100%">
      <el-table-column prop="paramKey" :label="t('plugin.key')" width="220">
        <template #default="scoped">
          <el-select
            v-model="scoped.row.paramKey"
            allow-create
            filterable
            default-first-option
            style="width: 100%"
            :placeholder="t('plugin.pleaseSelect')"
          >
            <el-option
              v-for="(v, i) in HEADER"
              :disabled="handleDisabledHeader(v)"
              :key="i"
              :label="v"
              :value="v"
            />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column prop="paramValue" :label="t('plugin.value')">
        <template #default="scoped">
          <el-input
            :autofocus="false"
            v-model="scoped.row.paramValue"
            :placeholder="t('plugin.pleaseEnter')"
          />
        </template>
      </el-table-column>
      <el-table-column :label="t('plugin.operate')" width="68">
        <template #default="scoped">
          <el-button
            type="primary"
            text
            size="small"
            class="table-cell-text-btn"
            :disabled="!scoped.row.paramKey && !scoped.row.paramValue"
            @click="handleDel(scoped)"
          >
            <el-icon>
              <Delete />
            </el-icon>
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-form-item>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { HEADER } from '../constants'
import { t } from '@/locales'

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

const handleDel = (scoped) => {
  emits(
    'update:modelValue',
    params.value.filter((_, i) => i !== scoped.$index)
  )
}

const handleDisabledHeader = (val: string): boolean => {
  return params.value.some((item: any) => item.paramKey === val)
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
