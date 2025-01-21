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
            style="width: 100%; height: 25px"
            :placeholder="t('plugin.pleaseSelect')"
            size="small"
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
          <el-select
            v-model="scoped.row.paramValue"
            allow-create
            filterable
            default-first-option
            style="width: 100%; height: 25px"
            :placeholder="t('plugin.pleaseSelect')"
            size="small"
          >
            <el-option
              v-for="(v, i) in HEADER_VALUE[scoped.row.paramKey]"
              :key="i"
              :label="v"
              :value="v"
            />
          </el-select>
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
import { HEADER ,HEADER_VALUE} from '../constants'
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

const handleAdd = () => {
  params.value.push({
    paramKey: '',
    paramValue: ''
  })
}
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
