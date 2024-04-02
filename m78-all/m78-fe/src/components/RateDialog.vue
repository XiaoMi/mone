<template>
  <el-dialog v-model="visible" width="800px">
    <el-form
      ref="ruleFormRef"
      :model="ruleForm"
      :rules="rules"
      label-width="100px"
      class="demo-ruleForm"
      size="default"
      status-icon
    >
      <el-form-item label="评分" prop="score">
        <el-rate v-model="ruleForm.score" />
      </el-form-item>
      <el-form-item label="评论内容" prop="commentContent">
        <el-input v-model="ruleForm.commentContent" :rows="6" type="textarea" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm(ruleFormRef)">提交</el-button>
        <el-button @click="resetForm(ruleFormRef)">重置</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, computed, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { updateComment, getCommentDetail } from '@/api/probot'

interface RuleForm {
  id?: string
  botId?: string
  score: number
  commentContent: string
}

const emits = defineEmits(['update:modelValue', 'update-rate'])

const props = defineProps<{
  botId: string
  type: number
  modelValue: boolean
}>()

const ruleFormRef = ref<FormInstance>()
const ruleForm = reactive<RuleForm>({
  score: 0,
  commentContent: ''
})

const visible = computed({
  get() {
    return props.modelValue
  },
  set(value) {
    emits('update:modelValue', value)
  }
})

const rules = reactive<FormRules<RuleForm>>({
  commentContent: [
    { required: true, message: '请输入评论内容', trigger: 'blur' },
    { min: 1, max: 5000, message: '长度在1到5000之间', trigger: 'blur' }
  ],
  score: [
    {
      required: true,
      message: '必须评分',
      trigger: 'change'
    }
  ]
})

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate(async (valid, fields) => {
    if (valid) {
      try {
        const { code, message } = await updateComment({
          ...ruleForm,
          itemId: props.botId,
          type: props.type
        })
        if (code === 0) {
          ElMessage.success('评论提交成功')
          visible.value = false
          emits('update-rate')
        } else {
          ElMessage.error(message || '评论提交失败')
        }
      } catch (e) {
        console.error(e)
        ElMessage.error('评论提交失败')
      }
    } else {
      console.log('error submit!', fields)
    }
  })
}

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}

const init = async () => {
  try {
    const { code, data, message } = await getCommentDetail({
      itemId: props.botId,
      type: props.type
    })
    if (code === 0) {
      console.log(data)
      if (data) {
        ruleForm.id = data.id
        ruleForm.score = data.score
        ruleForm.commentContent = data.commentContent
      }
    } else {
      ElMessage.error(message || '获取评论信息失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('获取评论信息失败')
  }
}

init()
</script>
