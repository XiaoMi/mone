<!--
 * @Description: 
 * @Date: 2024-07-18 14:47:06
 * @LastEditTime: 2024-08-06 10:46:06
-->
<template>
  <ModeContainerNew
    @onSubmit="onSubmit"
    :result="curItem"
    type="img"
    :historyType="7"
    @changeHistory="changeHistory"
    ref="containerRef"
    :apiName="TextToImage"
  >
    <template #left>
      <el-form :model="form" :rules="rules" ref="formRef">
        <FormItemMode v-model="form.model" apiType="m78-image-create-model" />
        <FormItem prop="input">
          <el-input
            v-model="form.input"
            type="textarea"
            placeholder="描述你想生成的画面，越具体生成的效果越好哦～"
            maxlength="1000"
            show-word-limit
            :autosize="{ minRows: 6, maxRows: 6 }"
          />
          <div class="try-container">
            <div class="try-content">
              试一试：
              <el-button
                type="primary"
                link
                v-for="item in btns"
                :key="item.value"
                @click="
                  () => {
                    changeInput(item)
                  }
                "
                >{{ item.label }}</el-button
              >
            </div>
            <el-button type="primary" link :icon="Refresh" />
          </div>
        </FormItem>
        <FormItemImage
          v-model:data="imageStyles"
          v-model="form.style"
          title="图像风格"
          prop="style"
        ></FormItemImage>
        <FormItemUpload
          v-model="form.refImageUrl"
          prop="refImageUrl"
          title="参考图像"
          :limit="1"
        ></FormItemUpload>
        <FormItemSize v-model="form.size" prop="size"></FormItemSize>
        <FormItemNum v-model="form.num" prop="num"></FormItemNum>
      </el-form>
    </template>
    <template #center>
      <div v-loading="loading" class="result-container">
        <div v-if="result.length">{{ result }}</div>
        <el-empty :image-size="200" v-else description="快去左侧输入您的创意吧～～" />
      </div>
    </template>
  </ModeContainerNew>
</template>

<script setup lang="ts">
import { reactive, ref, onUnmounted } from 'vue'
import ModeContainerNew from '@/components/mode-container/ModeContainerNew.vue'
import FormItem from '../components/FormItem.vue'
import FormItemMode from '../components/FormItemMode.vue'
import FormItemUpload from '../components/FormItemUpload.vue'
import FormItemSize from '../components/FormItemSize.vue'
import FormItemNum from '../components/FormItemNum.vue'
import FormItemImage from '../components/FormItemImage.vue'
import { Refresh } from '@element-plus/icons-vue'
import { TextToImage } from '@/api/probot-mode'
import type { FormInstance, FormRules } from 'element-plus'
import History from '../components/history/History.vue'
import { getTaskInfo } from '@/api/probot-mode.ts'

interface RuleForm {
  model: string
  input: string
  style: {
    key: string
    label: string
    image: string
  }
  size: string
  num: 0
  refImageUrl: string[]
}
const hisRef = ref(null)
const curItem = ref({})
const btns = ref([
  {
    label: '海盗',
    value: 'pirate'
  },
  {
    label: '花朵',
    value: 'flower'
  }
])
const imageStyles = ref([
  {
    key: '<photography>',
    label: '摄影',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/1.png`
  },
  {
    key: '<portrait>',
    label: '3D卡通',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/2.png`
  },
  {
    key: '<anime>',
    label: '动画',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/3.png`
  },
  {
    key: '<oil painting>',
    label: '油画',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/4.png`
  },
  {
    key: '<watercolor>',
    label: '水彩',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/5.png`
  },
  {
    key: '<sketch>',
    label: '素描',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/6.png`
  },
  {
    key: '<chinese painting>',
    label: '中国画',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/7.png`
  },
  {
    key: '<flat illustration>',
    label: '扁平插画',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/8.png`
  },
  {
    key: '<auto>',
    label: '默认',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/9.png`
  }
])

const form = reactive({
  model: '',
  input: '',
  style: imageStyles.value[0],
  size: '1024*1024',
  num: 0,
  refImageUrl: []
})

const formRef = ref<FormInstance>()
const rules = reactive<FormRules<RuleForm>>({
  model: [{ required: true, message: '请选择模型', trigger: 'change' }],
  input: [{ required: true, message: '请输入要求描述', trigger: 'blur' }]
})
const loading = ref(false)
const result = ref('')
const containerRef = ref(null)

const onSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      result.value = ''
      loading.value = true
      containerRef.value.submitFn({
        model: form.model,
        cmd: 'TextToImage',
        input: form.input,
        num: form.num + 1,
        style: form.style.key,
        size: form.size,
        refImageUrl: form.refImageUrl[0]
      })
    } else {
      console.log('error submit!', fields)
    }
  })
}

const changeHistory = (val) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  const { setting } = val
  if (!setting) return
  Object.assign(form, {
    ...setting,
    style: imageStyles.value.find((item) => item.key === setting.style),
    size: setting.size,
    num: setting.num - 1,
    refImageUrl: [setting.refImageUrl]
  })
}
const changeInput = (item) => {
  form.input = item.label
}
</script>

<style scoped lang="scss">
.try-container {
  display: flex;
  align-items: center;
  font-size: 14px;
  padding-left: 4px;
  width: 100%;
  .try-content {
    flex: 1;
    display: flex;
    align-items: center;
  }
}
</style>
