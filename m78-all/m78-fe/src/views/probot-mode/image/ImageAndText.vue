<template>
  <ModeContainerNew
    @onSubmit="onSubmit"
    :result="curItem"
    type="img"
    :historyType="5"
    @changeHistory="changeHistory"
    ref="containerRef"
    :apiName="TextAndImage"
  >
    <template #left>
      <el-form :model="form" :rules="rules" ref="formRef">
        <FormItemMode v-model="form.model" apiType="m78-image-create-model" />
        <FormItem prop="input">
          <el-input
            v-model="form.input"
            type="textarea"
            placeholder="写下你的要求描述"
            maxlength="1000"
            show-word-limit
            :autosize="{ minRows: 8, maxRows: 8 }"
          />
        </FormItem>
        <FormItemUpload
          v-model="form.maskImageUrl"
          prop="maskImageUrl"
          :limit="1"
          title="文字标识的图片"
          tip="宽和高像素要求范围256px~768px，且必须为64px的整数倍。"
        ></FormItemUpload>
        <FormItem title="负向提示词" prop="negativeInput">
          <el-input v-model="form.negativeInput" placeholder="请输入" show-word-limit />
        </FormItem>
        <FormItemNum v-model="form.num" prop="num"></FormItemNum>
      </el-form>
    </template>
  </ModeContainerNew>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import ModeContainerNew from '@/components/mode-container/ModeContainerNew.vue'
import FormItem from '../components/FormItem.vue'
import FormItemMode from '../components/FormItemMode.vue'
import FormItemUpload from '../components/FormItemUpload.vue'
import FormItemNum from '../components/FormItemNum.vue'
import type { FormInstance, FormRules } from 'element-plus'
import { TextAndImage } from '@/api/probot-mode'
import { ElMessage } from 'element-plus'

interface RuleForm {
  model: string
  input: string
  maskImageUrl: string[]
}

const form = reactive({
  model: 'wanx',
  input: '',
  maskImageUrl: [],
  num: 0,
  negativeInput: ''
})

const formRef = ref<FormInstance>()
const rules = reactive<FormRules<RuleForm>>({
  model: [{ required: true, message: '请选择模型', trigger: 'change' }],
  input: [{ required: true, message: '请输入要求描述', trigger: 'blur' }],
  maskImageUrl: [{ required: true, message: '请选上传图片', trigger: 'change' }]
})
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
    num: setting.num - 1,
    maskImageUrl: [setting.maskImageUrl]
  })
}

const checkWh = (whVal) => {
  return whVal >= 256 && whVal <= 768 && whVal % 64 == 0 ? true : false
}
const onSubmit = async () => {
  // TextAndImage
  if (!formRef.value) return
  let imageWidth = 0
  let imageHeight = 0
  if (form.maskImageUrl[0]) {
    const { imgW, imgH } = await getImageDimensions(form.maskImageUrl[0])
    if (!(checkWh(imgW) && checkWh(imgH))) {
      return ElMessage.error(
        '图片尺寸不符合要求；宽和高像素要求范围256px~768px，且必须为64px的整数倍。'
      )
    }
    imageWidth = imgW
    imageHeight = imgH
  }
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      containerRef.value.submitFn({
        model: form.model,
        cmd: 'TextAndImage',
        input: form.input,
        maskImageUrl: form.maskImageUrl[0],
        num: form.num + 1,
        imageWidth,
        imageHeight,
        negativeInput: form.negativeInput
      })
    } else {
      console.log('error submit!', fields)
    }
  })
}
/**
 * 根据图片URL获取图片的宽度和高度
 *
 * @param {string} imageUrl 图片的URL地址
 * @returns {Promise<{width: number, height: number}>} 包含图片宽度和高度的Promise对象
 */
const getImageDimensions = (imageUrl) => {
  return new Promise((resolve, reject) => {
    // 创建一个新的Image对象
    const img = new Image()

    // 设置图片加载完成后的回调函数
    img.onload = function () {
      resolve({
        imgW: img.width,
        imgH: img.height
      })
    }

    // 设置图片加载失败的回调函数
    img.onerror = function () {
      reject(new Error('Failed to load image'))
    }

    // 设置图片的src为传入的URL
    img.src = imageUrl
  })
}
</script>

<style scoped lang="scss">
.result-container {
}
</style>
