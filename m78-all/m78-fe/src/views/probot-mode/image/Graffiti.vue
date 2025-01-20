<template>
  <ModeContainerNew
    @onSubmit="onSubmit"
    :result="curItem"
    type="img"
    :historyType="4"
    @changeHistory="changeHistory"
    ref="containerRef"
    :apiName="SketchToImage"
  >
    <template #left>
      <el-form :model="form" :rules="rules" ref="formRef">
        <FormItemMode v-model="form.model" apiType="m78-image-create-model" />
        <FormItemUpload v-model="form.baseImageUrl" prop="baseImageUrl" :limit="1"></FormItemUpload>
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
        <FormItem
          prop="sketchExtraction"
          title="边缘提取"
          tip="如果上传图片非sketch线稿，而是复杂RGB图片，可以对输入图片进行sketch边缘提取。默认值为False，设置为True时则进行提取"
        >
          <el-radio-group v-model="form.sketchExtraction">
            <el-radio :value="false">否</el-radio>
            <el-radio :value="true">是</el-radio>
          </el-radio-group></FormItem
        >
        <FormItem
          prop="sketchColor"
          title="线条颜色"
          tip="如果sketch线稿中的线条非黑色，而是其他一种或多种颜色，则将其rgb数值进行输入，默认值为[]"
        >
          <el-form-item
            v-for="(domain, index) in form.sketchColor"
            :key="index"
            :prop="'domains.' + index + '.value'"
            class="color-wrap"
          >
            <div class="color-container">
              <el-input
                v-model="domain.value"
                placeholder="请选择颜色"
                class="color-input"
                disabled
              />
              <el-color-picker v-model="domain.value" color-format="rgb" />
              <div class="color-icon-container">
                <div class="color-icon" @click.prevent="addColor(index)">
                  <el-icon><Plus /></el-icon>
                </div>
                <div
                  class="color-icon"
                  @click.prevent="deleteColor(index)"
                  v-if="form.sketchColor.length > 1"
                >
                  <el-icon><Minus /></el-icon>
                </div>
              </div>
            </div>
          </el-form-item>
        </FormItem>
        <FormItemSize
          v-model="form.size"
          prop="size"
          :options="[{ width: 768, height: 768 }]"
        ></FormItemSize>
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
import FormItemSize from '../components/FormItemSize.vue'
import FormItemNum from '../components/FormItemNum.vue'
import type { FormInstance, FormRules } from 'element-plus'
import { SketchToImage } from '@/api/probot-mode'
import { ElMessage } from 'element-plus'

interface RuleForm {
  model: string
  input: string
  size: string
  num: number
  baseImageUrl: string[]
  sketchColor: []
  sketchExtraction: boolean
}

const form = reactive({
  model: '',
  input: '',
  size: '768*768',
  num: 0,
  baseImageUrl: [],
  sketchExtraction: false, //边缘提取
  sketchColor: [
    {
      value: ''
    }
  ] //线条颜色
})

const formRef = ref<FormInstance>()
const rules = reactive<FormRules<RuleForm>>({
  model: [{ required: true, message: '请选择模型', trigger: 'change' }],
  baseImageUrl: [{ required: true, message: '请选上传图片', trigger: 'change' }],
  input: [{ required: true, message: '请输入要求描述', trigger: 'blur' }]
})
const loading = ref(false)
const result = ref('')
//添加
const addColor = (index: number) => {
  form.sketchColor.splice(index + 1, 0, {
    value: ''
  })
}
//删除
const deleteColor = (index: number) => {
  form.sketchColor.splice(index, 1)
}

const containerRef = ref(null)
const onSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      console.log('sketchColor', form.sketchColor)
      const sketchColor = form.sketchColor.map((item) => {
        return item.value.match(/\d+/g)
      })
      // 过滤掉空的
      const filterNull = sketchColor.filter((it) => it)
      // sketchColor的每一项是个数字类型的数组，如果是文字转为数字
      const numColor = filterNull.map((item) => {
        return item.map((str) => Number(str))
      })
      result.value = ''
      loading.value = true
      containerRef.value.submitFn({
        model: form.model,
        cmd: 'SketchToImage',
        input: form.input,
        baseImageUrl: form.baseImageUrl[0],
        num: form.num + 1,
        size: form.size,
        sketchExtraction: form.sketchExtraction, //边缘提取
        sketchColor: numColor //线条颜色
      })
    } else {
      console.log('error submit!', fields)
    }
  })
}
const curItem = ref(null)
const changeHistory = (val) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  const { setting } = val
  if (!setting) return
  const sketchColor = (setting.sketchColor || []).filter((item) => item)
  let color =
    sketchColor.length == 0
      ? []
      : sketchColor.map((item) => {
          const valStr = 'rgb(' + item.join(',') + ')'
          return { value: valStr }
        })
  if (color.length == 0) {
    color = { value: '' }
  }
  Object.assign(form, {
    ...setting,
    baseImageUrl: [setting.baseImageUrl],
    sketchColor: color,
    num: setting.num - 1
  })
}
</script>

<style scoped lang="scss">
.result-container {
}
.color-wrap {
  margin-bottom: 4px;
}
.color-container {
  display: flex;
  width: 100%;
  align-items: center;
  .color-input {
    margin-right: 10px;
  }
  .color-icon-container {
    width: 100px;
    display: flex;
  }
  .color-icon {
    margin: 0 4px;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    text-align: center;
    cursor: pointer;
    &:hover {
      background: #eee;
    }
  }
}
</style>
