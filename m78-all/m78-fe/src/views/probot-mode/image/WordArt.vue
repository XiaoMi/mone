<template>
  <ModeContainerNew
    @onSubmit="onSubmit"
    :result="curItem"
    type="img"
    :historyType="2"
    @changeHistory="changeHistory"
    ref="containerRef"
    :apiName="ArtWord"
  >
    <template #left>
      <el-form :model="form" :rules="rules" ref="formRef">
        <FormItemMode
          v-model="form.model"
          prop="model"
          apiType="m78-image-create-model"
        ></FormItemMode>
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
        <FormItem explain="支持1～5个汉字" prop="textContent">
          <el-input v-model="form.textContent" placeholder="请输入" maxlength="5" show-word-limit />
        </FormItem>
        <FormItem title="字体" prop="fontName">
          <el-select v-model="form.fontName" placeholder="请选择字体" style="width: 100%">
            <el-option
              v-for="item in fontNameOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </FormItem>
        <FormItemSize
          v-model="form.outputImageRatio"
          prop="outputImageRatio"
          :options="sizeOptions"
          title="图片宽高比"
          separator=":"
        ></FormItemSize>
        <FormItemImage
          v-model:data="textureStyleData"
          v-model="form.textureStyle"
          title="风格"
          prop="textureStyle"
          :rowNum="3"
        ></FormItemImage>
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
import FormItemNum from '../components/FormItemNum.vue'
import FormItemSize from '../components/FormItemSize.vue'
import FormItemImage from '../components/FormItemImage.vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ArtWord } from '@/api/probot-mode'

interface RuleForm {
  model: string
  input: string
  textContent: string
}

const fontNameOptions = [
  {
    value: 'dongfangdakai',
    label: '阿里妈妈东方大楷'
  },
  {
    value: 'puhuiti_m',
    label: '阿里巴巴普惠体'
  },
  {
    value: 'shuheiti',
    label: '阿里妈妈数黑体'
  },
  {
    value: 'jinbuti',
    label: '钉钉进步体'
  },
  {
    value: 'kuheiti',
    label: '站酷酷黑体'
  },
  {
    value: 'kuaileti',
    label: '站酷快乐体'
  },
  {
    value: 'wenyiti',
    label: '站酷文艺体'
  },
  {
    value: 'logoti',
    label: '站酷小薇LOGO体'
  },
  {
    value: 'cangeryuyangti_m',
    label: '站酷仓耳渔阳体'
  },
  {
    value: 'siyuansongti_b',
    label: '思源宋体'
  },
  {
    value: 'siyuanheiti_m',
    label: '思源黑体'
  },
  {
    value: 'fangzhengkaiti',
    label: '方正楷体'
  }
]
const sizeOptions = [
  {
    width: '1',
    height: '1'
  },
  {
    width: '16',
    height: '9'
  },
  {
    width: '9',
    height: '16'
  }
]
const textureStyleData = ref([
  {
    key: 'material',
    label: '立体材质',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/art1.png`
  },
  {
    key: 'scene',
    label: '场景融合',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/art2.png`
  },
  {
    key: 'lighting',
    label: '光影特效',
    image: `${import.meta.env.VITE_APP_STATIC_PATH}probot-mode-images/art3.png`
  }
])

const form = reactive({
  model: 'wanx',
  input: '',
  textContent: '',
  fontName: fontNameOptions[0].value,
  outputImageRatio: '',
  textureStyle: textureStyleData.value[0],
  num: 0
})
const formRef = ref<FormInstance>()
const rules = reactive<FormRules<RuleForm>>({
  model: [{ required: true, message: '请选择模型', trigger: 'change' }],
  input: [{ required: true, message: '请输入要求描述', trigger: 'blur' }],
  textContent: [{ required: true, message: '请输入文字', trigger: 'blur' }]
})
const containerRef = ref<any>(null)
const curItem = ref(null)
const changeHistory = (val: any) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  const { setting } = val
  if (!setting) return
  Object.assign(form, { ...setting, num: setting.num - 1 })
}
const onSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      containerRef.value?.submitFn({
        cmd: 'ArtWord',
        model: form.model,
        input: form.input,
        textContent: form.textContent,
        fontName: form.fontName,
        outputImageRatio: form.outputImageRatio,
        textureStyle: form.textureStyle.key,
        num: form.num + 1
      })
    } else {
      console.log('error submit!', fields)
    }
  })
}
</script>

<style scoped lang="scss"></style>
