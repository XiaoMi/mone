<template>
  <el-form-item :prop="props.propName" :label="props.label">
    <el-table :data="params" style="width: 100%">
      <el-table-column prop="name" :label="t('plugin.parameterName')" width="220">
        <template #default="scoped">
          <el-input
            :autofocus="false"
            v-model="scoped.row.name"
            :placeholder="t('plugin.pleaseEnter')" 
            style="height: 25px;"
            size="small"
          />
        </template>
      </el-table-column>
      <el-table-column prop="valueType" label="变量类型">
        <template #default="scoped">
          <VariateTypeSel
            v-model="scoped.row.valueType"
            style="width: 90px;height: 25px;"
            size="small"
            @change="
              (val) => {
                changeType(val, data)
              }
            "
          />
        </template>
      </el-table-column>
      <el-table-column prop="desc" :label="t('plugin.desc')">
        <template #default="scoped">
          <el-input
            :autofocus="false"
            v-model="scoped.row.desc"
            :placeholder="t('plugin.pleaseEnter')" 
            style="height: 25px;"
            size="small"
          />
        </template>
      </el-table-column>
      <el-table-column prop="required" label="必填" width="60">
        <template #default="scoped">
          <el-switch v-model="scoped.row.required" size="small" />
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
import VariateTypeSel from '@/views/workflow/work-flow/components/components/VariateTypeSel.vue'

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

const handleDel = (scoped) => {
  emits(
    'update:modelValue',
    params.value.filter((_, i) => i !== scoped.$index)
  )
}
const changeType = (val, data) => {
  console.log('val', val, data)
  // if (!hasChildType.value.includes(val)) {
  //   node.data.children = []
  // }
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
