<!--
 * @Description: 
 * @Date: 2024-05-21 14:25:47
 * @LastEditTime: 2024-05-21 14:25:48
-->
<template>
  <el-dialog v-model="dialogVisible" title="自定义知识解析" width="500">
    <el-form :model="form" ref="ruleFormRef" :rules="rules">
      <el-form-item label="自定义知识解析分隔符" :label-width="formLabelWidth" prop="separator">
        <el-select
          v-model="form.separator"
          filterable
          allow-create
          default-first-option
          :reserve-keyword="false"
          placeholder="请输入自定义知识解析分隔符"
          style="width: 240px"
        >
          <el-option label="不分割" value="!!_7!!7_!!" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sure(ruleFormRef)">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, computed, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  row: {
    type: Object,
    default: () => ({})
  }
})

const emits = defineEmits(['update:modelValue', 'submit'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const formLabelWidth = '180px'

const form = reactive({
  separator: ''
})
const ruleFormRef = ref<FormInstance>()
interface RuleForm {
  separator: string
}
const rules = reactive<FormRules<RuleForm>>({
  separator: [
    {
      required: true,
      message: '请输入自定义知识解析分隔符',
      trigger: 'change'
    }
  ]
})
const sure = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      emits('submit', form.separator)
    } else {
      console.log('error submit!', fields)
    }
  })
}
</script>

<style scoped></style>
