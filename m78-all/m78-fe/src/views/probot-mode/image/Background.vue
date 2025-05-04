<template>
  <ModeContainerNew
    @onSubmit="onSubmit"
    :result="curItem"
    type="img"
    :historyType="3"
    @changeHistory="changeHistory"
    ref="containerRef"
    :apiName="backGen"
  >
    <template #left>
      <el-form :model="form" ref="formRef">
        <FormItemMode v-model="form.model" apiType="m78-image-create-model" />
        <FormItemUpload
          v-model="form.baseImageUrl"
          prop="baseImageUrl"
          :limit="1"
          title="主图上传"
          tip="透明背景的主体图像URL,需要为带透明背景的RGBA 四通道图像"
        ></FormItemUpload>

        <FormItemImage
          title="使用场景"
          v-model:data="imageStyles"
          v-model="form.sceneType"
          prop="styleIndex"
        ></FormItemImage>
        <FormItem prop="input" title="文字引导" tip="引导文本提示词，支持中英双语，不超过70个单词">
          <el-input
            v-model="form.input"
            placeholder="文字引导"
            maxlength="70"
            type="textarea"
            :autosize="{ minRows: 4, maxRows: 4 }"
          />
        </FormItem>

        <FormItemUpload
          v-model="form.refImageUrl"
          prop="refImageUrl"
          :limit="1"
          title="图片引导"
          tip="引导图URL, 支持 jpg, png，webp等常见格式图像"
        ></FormItemUpload>
        <FormItem
          prop="title"
          title="主标题"
          tip="图像上添加文字主标题。算法自动确定文字的大小和位置，限制1-8个字符"
        >
          <el-input v-model="form.title" placeholder="主标题" maxlength="8" minlength="1" />
        </FormItem>
        <FormItem
          prop="subTitle"
          title="副标题"
          tip="图像上添加文字副标题。算法自动确定文字的大小和位置，限制1-10个字符"
        >
          <el-input v-model="form.subTitle" placeholder="副标题" maxlength="10" minlength="1" />
        </FormItem>
        <FormItemNum v-model="form.num" prop="num"></FormItemNum>
      </el-form>
    </template>
  </ModeContainerNew>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onUnmounted, inject } from 'vue'
import ModeContainerNew from '@/components/mode-container/ModeContainerNew.vue'
import FormItem from '../components/FormItem.vue'
import FormItemMode from '../components/FormItemMode.vue'
import FormItemUpload from '../components/FormItemUpload.vue'
import FormItemNum from '../components/FormItemNum.vue'
import FormItemImage from '../components/FormItemImage.vue'
import { FormInstance, FormRules, ElMessage } from 'element-plus'
import { backGen, getTaskInfo } from '@/api/probot-mode.ts'
import History from '../components/history/History.vue'

interface RuleForm {}

const containerRef = ref(null)
const curItem = ref({})

const form = reactive({
  num: 0,
  model: '',
  resource: '',
  desc: '',
  input: '',
  title: '',
  subTitle: '',
  sceneType: {
    key: 'GENERAL'
  },
  baseImageUrl: [],
  refImageUrl: []
})

const changeHistory = (val) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  const { setting } = val
  if (!setting) return
  Object.assign(form, {
    ...val.setting,
    sceneType: {
      key: setting.sceneType
    },
    baseImageUrl: [setting.baseImageUrl],
    refImageUrl: [setting.refImageUrl],
    num: setting.num - 1
  })
}
const formRef = ref<FormInstance>()
const rules = reactive<FormRules<RuleForm>>({
  model: [{ required: true, message: '请选择模型', trigger: 'change' }],
  input: [{ required: true, message: '请输入文字引导', trigger: 'change' }],
  title: [{ required: true, message: '请输入主标题', trigger: 'change' }],
  subTitle: [{ required: true, message: '请输入副标题', trigger: 'change' }]
})
const result = ref('')
const imageStyles = ref([
  {
    key: 'GENERAL',
    label: '通用场景',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/back/common.png`
  },
  {
    key: 'ROOM',
    label: '室内家居',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/back/room.png`
  },
  {
    key: 'COSMETIC',
    label: '美妆场景',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/back/makeup.png`
  }
])

const onSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      if (!validOther) return
      containerRef.value.submitFn({
        ...form,
        cmd: 'BackgroundGen',
        baseImageUrl: form.baseImageUrl[0] || '',
        refImageUrl: form.refImageUrl[0] || '',
        num: form.num + 1,
        sceneType: form.sceneType.key
      })
    } else {
      console.log('error submit!', fields)
    }
  })
}
const validOther = () => {
  if (form.baseImageUrl.length < 1) {
    ElMessage.warning('请上传主图')
    return false
  }
  return true
}
</script>

<style scoped lang="scss">
.result-container {
}
</style>
