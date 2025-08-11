<template>
  <ModeContainerNew
    @onSubmit="onSubmit"
    :result="curItem"
    type="img"
    :historyType="6"
    @changeHistory="changeHistory"
    ref="containerRef"
    :apiName="repaintApi"
  >
    <template #left>
      <el-form :model="form" :rules="rules" ref="formRef">
        <FormItemMode v-model="form.model" apiType="m78-image-create-model" />
        <FormItemUpload v-model="form.baseImageUrl" prop="baseImageUrl" :limit="1"></FormItemUpload>
        <FormItemImage
          v-model:data="imageStyles"
          v-model="form.styleIndex"
          title="重绘风格"
          prop="styleIndex"
        ></FormItemImage>
      </el-form>
    </template>
  </ModeContainerNew>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import ModeContainerNew from '@/components/mode-container/ModeContainerNew.vue'
import FormItemMode from '../components/FormItemMode.vue'
import FormItemUpload from '../components/FormItemUpload.vue'
import FormItemImage from '../components/FormItemImage.vue'
import type { FormInstance, FormRules } from 'element-plus'
import { repaintApi } from '@/api/probot-mode'

interface RuleForm {
  model: string
  baseImageUrl: string[]
  styleIndex: object
}

const imageStyles = ref([
  // {
  //   key: '-1',
  //   label: '参考上传图像风格',
  //   image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/1.png`
  // },
  {
    key: '0',
    label: '复古漫画',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/redraw/2.png`
  },
  {
    key: '1',
    label: '3D童话',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/redraw/3.png`
  },
  {
    key: '2',
    label: '二次元',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/redraw/4.png`
  },
  {
    key: '3',
    label: '小清新',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/redraw/5.png`
  },
  {
    key: '4',
    label: '未来科技',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/redraw/6.png`
  },
  {
    key: '5',
    label: '国画古风',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/redraw/7.png`
  },
  {
    key: '6',
    label: '将军百战',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/redraw/8.png`
  },
  {
    key: '7',
    label: '炫彩卡通',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/redraw/9.png`
  },
  {
    key: '8',
    label: '清雅国风',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/redraw/10.png`
  },
  {
    key: '9',
    label: '喜迎新年',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/redraw/11.png`
  }
])
const form = reactive({
  model: 'wanx',
  baseImageUrl: [],
  styleIndex: imageStyles.value[0]
})

const formRef = ref<FormInstance>()
const rules = reactive<FormRules<RuleForm>>({
  model: [{ required: true, message: '请选择模型', trigger: 'change' }],
  baseImageUrl: [{ required: true, message: '请选上传图片', trigger: 'change' }]
})
const loading = ref(false)
const result = ref('')

const containerRef = ref(null)
const curItem = ref(null)
const changeHistory = (val) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  const { setting } = val
  if (!setting) return
  Object.assign(form, {
    ...setting,
    baseImageUrl: [setting.baseImageUrl],
    styleIndex: imageStyles.value.find((item) => item.key == setting.styleIndex)
  })
  console.log('form', form)
}
const onSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      result.value = ''
      loading.value = true

      containerRef.value.submitFn({
        model: form.model,
        cmd: 'StyleRepaint',
        baseImageUrl: form.baseImageUrl[0],
        styleIndex: Number(form.styleIndex.key)
      })
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
