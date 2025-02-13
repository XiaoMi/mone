<!--
 * @Description: 
 * @Date: 2024-07-18 14:47:06
 * @LastEditTime: 2024-08-06 16:18:18
-->
<template>
  <ModeContainer @onSubmit="onSubmit" :result="curItem">
    <template #left>
      <el-form :model="form" :rules="rules" ref="formRef">
        <FormItemMode v-model="form.model" apiType="m78-image-understand-model" />
        <FormItemUpload v-model="form.imageUrls" prop="imageUrls"></FormItemUpload>
        <FormItem title="要求描述" prop="input">
          <el-input
            v-model="form.input"
            type="textarea"
            placeholder="写下你的要求描述"
            maxlength="1000"
            show-word-limit
            :autosize="{ minRows: 8, maxRows: 8 }"
          />
        </FormItem>
      </el-form>
    </template>
    <template #right>
      <History
        ref="hisRef"
        @setActiveItem="changeHistory"
        :type="1"
        showContent="text"
        :gening="loading"
      />
    </template>
  </ModeContainer>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import ModeContainer from '@/components/mode-container/index.vue'
import FormItem from '../components/FormItem.vue'
import FormItemMode from '../components/FormItemMode.vue'
import FormItemUpload from '../components/FormItemUpload.vue'
import { ImageUnderstanding } from '@/api/probot-mode'
import type { FormInstance, FormRules } from 'element-plus'
import History from '../components/history/History.vue'
import LLMImgModelSel from '@/components/LLMImgModelSel.vue'
import { ElMessage } from 'element-plus'

interface RuleForm {
  model: string
  imageUrls: string[]
  input: string
}
const hisRef = ref(null)
const form = reactive({
  model: '',
  imageUrls: [],
  input: ''
})

const formRef = ref<FormInstance>()
const rules = reactive<FormRules<RuleForm>>({
  model: [{ required: true, message: '请选择模型', trigger: 'change' }],
  imageUrls: [{ required: true, message: '请上传图片', trigger: 'change' }],
  input: [{ required: true, message: '请输入要求描述', trigger: 'blur' }]
})
const loading = ref(false)
const result = ref('')

const addTextItem = (setting) => {
  const val = {
    multiModalResourceOutput: [],
    runStatus: 0,
    setting
  }
  hisRef.value?.initList(val)
}
const onSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      result.value = ''
      loading.value = true
      const setting = {
        model: form.model,
        cmd: 'ImageUnderstanding',
        imageUrls: form.imageUrls,
        input: form.input
      }
      addTextItem(setting)
      ImageUnderstanding(setting)
        .then((res: any) => {
          if (res.code === 0) {
            const val = {
              multiModalResourceOutput: [res.data],
              loading: false,
              setting
            }
            hisRef.value?.updateHistory(val)
            setActiveItem(val)
          } else {
            ElMessage.error(res.message || '出错了！')
          }
        })
        .finally(() => {
          loading.value = false
        })
    } else {
      console.log('error submit!', fields)
    }
  })
}

const curItem = ref({})
const setActiveItem = (item) => {
  curItem.value = item
}
const changeHistory = (item) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  curItem.value = item
  Object.assign(form, item.setting || {})
}
</script>

<style scoped lang="scss">
.result-container {
  height: 100%;
  line-height: 30px;
  font-size: 14px;
  color: #666;
}
</style>
