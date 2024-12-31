<template>
  <el-dialog v-model="dialogVisible" title="生成代码" width="500" append-to-body>
    <el-form ref="ruleForm" :model="form" :rules="rules" label-position="top" size="small">
      <el-form-item label="模型" prop="model">
        <LLMModelSel v-model="form.model" />
      </el-form-item>
      <el-form-item label="描述" prop="comment">
        <el-input v-model="form.comment" placeholder="请输入描述" type="textarea" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirm" :loading="loading"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, ref } from 'vue'
import { getCodeGen } from '@/api/workflow'
import LLMModelSel from '@/components/LLMModelSel.vue'

const emits = defineEmits(['update:modelValue', 'codeGenRes'])
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})
const ruleForm = ref(null)
const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const loading = ref(false)
const form = ref({
  comment: '',
  model: 'gpt4_1106_2'
})
const rules = ref({
  comment: [{ required: true, message: '请填写描述', trigger: 'blur' }],
  model: [{ required: true, message: '请选择', trigger: 'blur' }]
})
const confirm = () => {
  loading.value = true
  getCodeGen(form.value)
    .then(({ data }) => {
      emits('codeGenRes', data?.code)
      dialogVisible.value = false
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<style lang="scss" scoped></style>
