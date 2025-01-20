<template>
  <ModeContainer @onSubmit="onSubmit">
    <template #left>
      <el-form :model="form" :rules="rules" ref="formRef">
        <FormItemMode v-model="form.name"></FormItemMode>
       声音复刻
      </el-form>
    </template>
    <template #center>
      <div v-loading="loading" class="result-container">
        <div v-if="result.length">{{ result }}</div>
        <el-empty :image-size="200" v-else description="快去左侧输入您的创意吧～～" />
      </div>
    </template>
  </ModeContainer>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import ModeContainer from '@/components/mode-container/index.vue'
import FormItem from '../components/FormItem.vue'
import FormItemMode from '../components/FormItemMode.vue'
import FormItemUpload from '../components/FormItemUpload.vue'
import type { FormInstance, FormRules } from 'element-plus'

interface RuleForm {
 
}

const form = reactive({
  name: '',
  resource: '',
  desc: ''
})
const formRef = ref<FormInstance>()
const rules = reactive<FormRules<RuleForm>>({
  model: [{ required: true, message: '请选择模型', trigger: 'change' }],
})
const loading = ref(false)
const result = ref('')

const onSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      result.value = ''
      loading.value = true
      // ImageUnderstanding({
      //   model: form.model,
      //   cmd: 'ImageUnderstanding',
      //   imageUrls: form.imageUrls,
      //   input: form.input
      // })
      //   .then((res: any) => {
      //     if (res.code === 0) {
      //       result.value = res.data
      //     }
      //   })
      //   .finally(() => {
      //     loading.value = false
      //   })
    } else {
      console.log('error submit!', fields)
    }
  })
}
</script>

<style scoped lang="scss">
.result-container {
}
</style>
