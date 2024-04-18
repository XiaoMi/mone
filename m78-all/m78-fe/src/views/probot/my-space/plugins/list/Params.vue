<template>
  <el-form-item :label="props.label">
    <el-table :data="params" style="width: 100%">
      <el-table-column prop="name" :label="t('plugin.parameterName')" width="220">
        <template #default="scoped">
          <el-input
            :autofocus="false"
            v-model="scoped.row.name"
            :placeholder="t('plugin.pleaseEnter')"
          />
        </template>
      </el-table-column>
      <el-table-column prop="desc" :label="t('plugin.desc')">
        <template #default="scoped">
          <el-input
            :autofocus="false"
            v-model="scoped.row.desc"
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
            :disabled="!scoped.row.name && !scoped.row.desc"
            @click="handleDel(scoped)"
            ><el-icon> <Delete /> </el-icon
          ></el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-form-item>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
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
