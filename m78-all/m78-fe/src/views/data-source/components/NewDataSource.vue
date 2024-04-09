<template>
  <el-dialog v-model="dialogVisible" :title="title" width="80%">
    <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" label-width="80px">
      <el-form-item label="HOST" prop="host">
        <el-input v-model="ruleForm.host"></el-input>
      </el-form-item>
      <el-form-item label="端口" prop="port">
        <el-input v-model="ruleForm.port"></el-input>
      </el-form-item>
      <el-form-item label="数据库" prop="database">
        <el-input v-model="ruleForm.database"></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="user">
        <el-input v-model="ruleForm.user"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="pwd">
        <el-input v-model="ruleForm.pwd" type="password"></el-input>
      </el-form-item>
      <el-form-item label="jdbcUrl" prop="jdbcUrl">
        <el-input v-model="ruleForm.jdbcUrl"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm(ruleFormRef)">提交</el-button>
        <el-button @click="resetForm(ruleFormRef)">重置</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { createDataSource, updateDataSource } from '@/api/data-source'
import { useUserStore } from '@/stores/user'

interface RuleForm {
  id?: string
  host: string
  port: string
  database: string
  user: string
  pwd: string
  jdbcUrl: string
}

const props = defineProps<{
  modelValue: boolean
  initForm?: RuleForm
}>()

const emits = defineEmits(['update:modelValue', 'submit'])

const userStore = useUserStore()
const isUpdate = computed(() => props.initForm?.id)
const title = computed(() => {
  return isUpdate.value ? '更新数据源' : '新增数据源'
})
const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(dialogVisible) {
    emits('update:modelValue', dialogVisible)
  }
})

const ruleFormRef = ref<FormInstance>()
const ruleForm = reactive<RuleForm>({
  host: '',
  port: '',
  database: '',
  user: '',
  pwd: '',
  jdbcUrl: ''
})

watch(
  () => props.initForm,
  (form, preForm) => {
    console.log(form, preForm)
    if (form && preForm != form) {
      ruleForm.id = form.id
      ruleForm.host = form.host
      ruleForm.port = form.port
      ruleForm.database = form.database
      ruleForm.user = form.user
      ruleForm.pwd = form.pwd
      ruleForm.jdbcUrl = form.jdbcUrl
    }
  }
)

const rules = reactive<FormRules<RuleForm>>({
  host: [
    { required: true, message: '必填字段', trigger: 'blur' },
    { min: 3, max: 200, message: 'Length should be 3 to 200', trigger: 'blur' }
  ],
  port: [
    { required: true, message: '必填字段', trigger: 'blur' },
    { min: 3, max: 15, message: 'Length should be 3 to 15', trigger: 'blur' }
  ],
  database: [
    { required: true, message: '必填字段', trigger: 'blur' },
    { min: 3, max: 200, message: 'Length should be 3 to 200', trigger: 'blur' }
  ],
  user: [
    { required: true, message: '必填字段', trigger: 'blur' },
    { min: 3, max: 200, message: 'Length should be 3 to 200', trigger: 'blur' }
  ],
  pwd: [
    { required: true, message: '必填字段', trigger: 'blur' },
    { min: 3, max: 200, message: 'Length should be 3 to 200', trigger: 'blur' }
  ],
  jdbcUrl: [{ required: true, message: '必填字段', trigger: 'blur' }]
})

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      const fn = isUpdate.value ? updateDataSource : createDataSource
      fn({ ...ruleForm, userName: userStore.userInfo.username }).then(
        ({ code }) => {
          if (code == 0) {
            emits('update:modelValue', false)
            emits('submit', { ...ruleForm })
          } else {
            ElMessage.error('失败')
          }
        },
        (e) => {
          console.error(e)
          ElMessage.error('失败')
        }
      )
    } else {
      console.log('error submit!', fields)
    }
  })
}

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}
</script>
