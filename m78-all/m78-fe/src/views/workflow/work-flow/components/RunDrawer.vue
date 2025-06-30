<template>
  <el-drawer
    v-model="drawer"
    title="试运行"
    direction="rtl"
    size="50%"
    class="run-test-drawer"
    @wheel="handleNoWheelFn"
  >
    <template #header>
      <div class="custom-header">
        <h2 class="drawer-title">试运行</h2>
        <el-button type="primary" plain @click="readText"> 导入复制内容 </el-button>
      </div>
    </template>
    <el-form ref="formRef" :model="formV" label-position="top" class="run-test">
      <el-form-item
        v-for="(inputItem, index) in formV.inputs"
        :key="index"
        :prop="'inputs.' + index + '.param'"
        :rules="{
          required: inputItem.required,
          message: returnMsg(inputItem.name),
          trigger: 'blur'
        }"
      >
        <template #label>
          <span>{{ returnMsg(inputItem.name) }}</span>
          <i class="type-i">{{ inputItem.valueType }}</i>
        </template>

        <template v-if="jsonTypes.includes(inputItem.valueType)">
          <JsonEditorVue
            v-model="inputItem.param"
            mode="text"
            class="run-test-json"
            :onBlur="
              () => {
                judgeJson(inputItem, index)
              }
            "
          />
          <p class="oz-form-item__error">{{ inputItem.errMsg }}</p>
        </template>
        <BaseIconUpload
          v-if="inputItem.valueType == 'Image' || inputItem.valueType == 'Pdf'"
          @upload="
            (base64) => {
              inputItem.valueType == 'Pdf'
                ? onUploadBotPdf(index, base64)
                : onUploadBotAvatar(index, base64)
            }
          "
          :base64="inputItem.param"
          :defaultEmpty="true"
          :type="inputItem.valueType == 'Pdf' ? 'application/pdf' : 'image/*'"
        />
        <el-input
          v-if="inputItem.valueType == 'String'"
          v-model="inputItem.param"
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 10 }"
        />
        <el-input-number
          class="input-num"
          v-if="inputItem.valueType == 'Integer'"
          v-model="inputItem.param"
          :precision="0"
          :step="1"
          controls-position="right"
        />
        <RunTestSwitch v-model="inputItem.param" v-if="inputItem.valueType == 'Boolean'" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button
        type="primary"
        :icon="CaretRight"
        @click="submit"
        :loading="loading"
        class="submit-btn"
        >提交</el-button
      >
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { CaretRight } from '@element-plus/icons-vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import BaseIconUpload from '@/components/BaseIconUpload.vue'
import { uploadLLMImg } from '@/api/workflow'
import JsonEditorVue from 'json-editor-vue'
import { getValueType } from './common.js'
import { useVueFlow } from '@vue-flow/core'
import RunTestSwitch from './components/RunTestSwitch.vue'
import { handleNoWheel, specialNames } from '@/views/workflow/work-flow/baseInfo.js'
import { loopTreeToSchema, getOutputTree } from '../../common/base.js'
import Ajv from 'Ajv'

const handleNoWheelFn = ref(handleNoWheel)

const returnMsg = (name) => {
  return `请输入${specialNames[name] || name}`
}
const jsonTypeFn = (data) => {
  if (Array.isArray(data)) {
    return 'Array'
  } else if (typeof data === 'object' && data !== null) {
    return 'Object'
  } else {
    return 'other' // 可能是数字、布尔值、null 等其他类型
  }
}

const judgeJson = (item, index) => {
  item.errMsg = ''
  try {
    const { param, valueType } = item
    const ajv = new Ajv({
      strict: false, // 尝试关闭严格模式
      allErrors: true // 显示所有错误
    })
    const schema = loopTreeToSchema(formV.value.inputs[index])
    console.log('schema', schema)
    const data = JSON.parse(param)
    const valid = ajv.validate(schema, data)
    if (!valid) console.log(ajv.errors)
    item.errMsg = valid ? '' : ajv.errors
  } catch (error) {
    console.log('error', error)
    item.errMsg = '请输入正确的JSON结构'
  }
}

const myFlow = useVueFlow()
// const info = getNodes()
onMounted(() => {
  console.log('nodes', myFlow.nodes)
})
const props = defineProps({
  modelValue: {},
  nodes: {},
  toFillArr: {},
  //  运行方式是whole || single
  testType: {},
  // 运行单节点时候需要传
  testNodeInfo: {},
  // 试运行的节点Id
  toRunNodeId: {}
})
const jsonTypes = ref([
  'Object',
  'Array<Object>',
  'Array<String>',
  'Array<Integer>',
  'Array<Boolean>'
])
const emits = defineEmits(['update:modelValue', 'runStart'])
const drawer = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const formV = ref({})
const formRef = ref(null)
const loading = ref(false)
// 试运行的节点Id
const runNodeId = ref(null)
watch(
  () => props.modelValue,
  (val) => {
    loading.value = false
    if (!val) return
    const newInputs = getValueTypeFn()
    // 试运行的节点都变了
    if (props.toRunNodeId != runNodeId.value) {
      formV.value.inputs = newInputs
    } else {
      // 试运行的节点没变
      const oldInputs = formV.value.inputs
      console.log('oldInputs', oldInputs)
      const inputs = newInputs.map((newInput) => {
        console.log('item', newInput)
        const oldInput = oldInputs.find((oldInput) => {
          return newInput.name == oldInput.name
        })
        return {
          ...newInput,
          param: oldInput?.param || emptyVal(newInput.valueType)
        }
      })
      console.log('inputs', inputs)
      formV.value.inputs = inputs
    }
    // 存一下
    runNodeId.value = props.toRunNodeId
  }
)

const emptyVal = (valueType) => {
  console.log('valueType', valueType)
  let nullVal = ''
  if (valueType.startsWith('Array')) {
    nullVal = '[]'
  } else if (valueType.startsWith('Object')) {
    nullVal = '{}'
  }
}

const getValueTypeFn = (arr) => {
  // 如果是单节点测试，则需要计算valueType
  if (props.testType == 'single') {
    const res = props.toFillArr.map((item) => {
      const { referenceInfo } = item
      const referNodeId = referenceInfo[0]
      const referNodeInfo = myFlow.nodes.value.find((nodeItem) => nodeItem.id == referNodeId)
      console.log('item.referenceInfo', item.referenceInfo)
      const selItem = getValueType(item.referenceInfo, getOutputTree(referNodeInfo))
      return {
        ...item,
        valueType: selItem?.valueType
      }
    })
    return res
  } else {
    return props.toFillArr
  }
}

const route = useRoute()
const reqFn = () => {
  loading.value = true
  const obj = {}
  formV.value.inputs.forEach((item) => {
    obj[item.name] =
      item.param && jsonTypes.value.includes(item.valueType) && typeof item.param == 'string'
        ? JSON.parse(item.param)
        : item.param
  })
  const testSingleP =
    props.testType == 'single' ? { executeType: 1, nodeInfo: props.testNodeInfo } : {}
  emits('runStart', {
    flowId: route.params.id,
    inputs: obj,
    ...testSingleP
  })
}
const submit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (valid) {
    // 校验失败的json类型的输入
    const failedInputs = formV.value.inputs.filter(
      (item) => jsonTypes.value.includes(item.valueType) && item.errMsg
    )
    if (failedInputs.length > 0) return
    reqFn()
  } else {
    console.log('error submit!', fields)
  }
}

const onUploadBotAvatar = async (index, base64) => {
  try {
    let response = {}
    response = await uploadLLMImg({
      base64: base64,
      id: `${route.params.id}_${formV?.value?.inputs[index]?.name}`,
      isInner: false
    })

    if (response.code === 0) {
      console.log(response.data)
      formV.value.inputs[index].param = response.data
    } else {
      console.error(response.message)
      ElMessage.error(response.message || '出错了')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('出错了')
  }
}

const onUploadBotPdf = async (index, base64) => {
  try {
    let response = {}
    response = await uploadLLMPdf({
      base64: base64,
      id: `${route.params.id}_${formV?.value?.inputs[index]?.name}`,
      isInner: false
    })

    if (response.code === 0) {
      console.log(response.data)
      formV.value.inputs[index].param = response.data
    } else {
      console.error(response.message)
      ElMessage.error(response.message || '出错了')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('出错了')
  }
}

const readText = async () => {
  const text = await navigator.clipboard.readText()
  try {
    const obj = JSON.parse(text)
    for (let k in obj) {
      console.log('formV.inputs', formV.value.inputs)
      const item = formV.value.inputs.find((item) => item.name == k)
      if (item) {
        item.param = obj[k]
      }
    }
  } catch (error) {
    ElMessage.error('请检查剪切板数据内容!')
  }
  console.log('text', text)
}
</script>

<style lang="scss" scoped>
.run-test {
  :deep(.oz-form-item__label) {
    font-weight: 700;
  }
}

.submit-btn {
  padding: 5px 20px;
}
</style>
<style lang="scss">
.run-test-drawer .oz-drawer__footer {
  text-align: center;
}
.upload-demo {
  width: 100%;
  .oz-upload {
    width: 100%;
    .oz-upload-dragger {
      width: 100%;
    }
  }
}
.upload-inner {
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.type-i {
  padding: 3px;
  background: #eee;
  border-radius: 3px;
  margin-left: 8px;
  font-weight: 500;
  font-size: 12px;
}
.run-test-json {
  width: 100%;
  max-height: 500px;
  overflow-y: auto;
  border-top: solid 1px #d7d7d7;
  border-bottom: solid 1px #d7d7d7;
  .jse-status-bar.svelte-hhcn0f.svelte-hhcn0f:last-child,
  .jse-menu.svelte-7deygj.svelte-7deygj {
    display: none;
  }
}
.input-num {
  width: 100%;
  .oz-input__inner {
    text-align: left;
  }
}
.custom-header {
  display: flex;
  align-items: center;
}
.drawer-title {
  margin-right: 10px;
}
</style>
