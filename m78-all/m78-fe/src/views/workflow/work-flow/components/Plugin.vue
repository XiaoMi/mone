<template>
  <el-form
    :model="node"
    size="small"
    label-position="top"
    inline
    class="plugin-form"
    ref="codeFormRef"
  >
    <template v-if="node.coreSetting.pluginType == 'dubbo'">
      <CommonInputs v-model="node.inputs" :referOps="referOps" />
      <CommonCollapse
        title="dubbo入参"
        content="dubbo入参"
        :showAdd="false"
        v-if="node.inputsPlugin"
      >
        <el-form-item
          label=""
          class="nowheel"
          @wheel="handleNoWheelFn"
          prop="inputsPlugin.dubboParam"
          :rules="{
            validator: (rule, value, cb) => {
              testDubboParam(rule, value, cb)
            },
            trigger: 'blur'
          }"
        >
          <div class="dubbo-params">
            <el-input
              v-model="node.inputsPlugin.dubboParam"
              type="textarea"
              :autosize="{ minRows: 3, maxRows: 5 }"
              :placeholder="dubboPlaceholder"
            />
          </div>
        </el-form-item>
      </CommonCollapse>
    </template>
    <template v-else>
      <div class="inputs-box">
        <el-collapse v-model="activeNames">
          <el-collapse-item name="1">
            <template #title>
              <div class="t-box">
                <div class="t-left">
                  <CollapseTitle
                    :activeNames="activeNames"
                    title="输入"
                    content="输入需要添加到提示词的信息，这些信息可以被下方的提示词引用"
                    tipClass="title-tooltip"
                    :showAdd="false"
                  />
                </div>
              </div>
            </template>
            <div v-for="(item, i) in node.inputs" :key="item" class="output-item">
              <el-form-item
                label="参数名"
                :prop="'inputs.' + i + '.name'"
                :rules="{
                  required: item.required,
                  message: '参数名不可为空',
                  trigger: 'blur'
                }"
              >
                <!-- <el-input v-model="item.name" placeholder="请输入参数名" style="width: 150px" /> -->
                <TitleTooltip
                  :title="item.name"
                  :content="item.desc || '没有相关参数描述'"
                  class="title-tooltip"
                  :showAdd="false"
                />
              </el-form-item>
              <div class="val-box">
                <el-form-item
                  label="参量值"
                  :prop="'inputs.' + i + '.type'"
                  :rules="{
                    required: item.required,
                    message: '请选择',
                    trigger: 'blur'
                  }"
                >
                  <OutputTypeSel v-model="item.type" />
                </el-form-item>
                <el-form-item
                  label=""
                  class="empty-item"
                  :prop="'inputs.' + i + '.value'"
                  :rules="{
                    validator: (rule, value, cb) => {
                      testFn(rule, value, cb, node.inputs[i])
                    },
                    trigger: 'blur'
                  }"
                  v-if="item.type == 'value'"
                >
                  <el-input v-model="item.value" placeholder="请输入参数值" :style="refreStyle" />
                </el-form-item>
                <el-form-item
                  v-else
                  label=""
                  class="empty-item"
                  :prop="'inputs.' + i + '.referenceInfo'"
                  :rules="{
                    validator: (rule, value, cb) => {
                      testFn(rule, value, cb, node.inputs[i])
                    },
                    trigger: 'blur'
                  }"
                >
                  <QuotaCas v-model="item.referenceInfo" :style="refreStyle" :options="referOps" />
                </el-form-item>
                <!-- <el-form-item class="empty-item">
                <el-button
                  link
                  @click.stop="
                    () => {
                      delInFn(i)
                    }
                  "
                >
                  <i class="iconfont icon-jian" style="font-size: 14px"></i>
                </el-button>
              </el-form-item> -->
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </template>
    <div class="inputs-box">
      <el-collapse v-model="activeNames2">
        <el-collapse-item name="1">
          <template #title>
            <div class="t-box">
              <div class="t-left">
                <CollapseTitle
                  :activeNames="activeNames2"
                  title="输出"
                  content="插件运行完成后生成的内容"
                  tipClass="title-tooltip"
                  :showAdd="false"
                />
              </div>
            </div>
          </template>
          <OutPutsTree v-model="node.outputs" :showDesc="true" :disabled="true" />
        </el-collapse-item>
      </el-collapse>
    </div>
  </el-form>
</template>

<script setup>
import { ref, computed, defineExpose, nextTick } from 'vue'
import TitleTooltip from './TitleTooltip.vue'
import OutputTypeSel from './components/OutputTypeSel.vue'
import VariateTypeSel from './components/VariateTypeSel'
import { validateRef, validPName, getReferOps } from '../baseInfo'
import 'codemirror/theme/erlang-dark.css'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/mode/groovy/groovy.js'
import QuotaCas from './components/QuotaCas'
import { useVueFlow } from '@vue-flow/core'
import { useWfStore } from '@/stores/workflow1'
import OutPutsTree from '@/views/workflow/work-flow/components/components/OutPutsTree.vue'
import CollapseTitle from './components/CollapseTitle.vue'
import CommonInputs from '@/views/workflow/work-flow/components/components/CommonInputs.vue'
import CommonCollapse from './components/CommonCollapse.vue'
import { handleNoWheel } from '@/views/workflow/work-flow/baseInfo.js'

const handleNoWheelFn = ref(handleNoWheel)
const wfStore = useWfStore()
const draging = computed(() => wfStore.nodeDragging)
const props = defineProps({
  modelValue: {},
  nodes: {},
  lines: {},
  getDetailed: {},
  referOps: {}
})
const emits = defineEmits(['update:modelValue'])
const node = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const dubboPlaceholder = ref(
  '可以使用${变量名}的方式引入输入参数中的变量，例：[{"page":${page},"pageSize":2,"name":${name}}]'
)
const { toObject } = useVueFlow()

const refreStyle = ref({
  width: '170px'
})
const activeNames = ref(['1'])
const activeNames2 = ref(['1'])

const addParam = () => {
  node.value.inputs.push({ name: '', val: '' })
}
const delInFn = (i) => {
  node.value.inputs.splice(i, 1)
}
let id = 1000
const addFn = () => {
  const newObj = {
    id: id++,
    name: '',
    type: 'String'
  }
  node.value.outputs.push(newObj)
}

const delFn = (i) => {
  node.value.outputs.splice(i, 1)
}

const testFn = (rule, value, cb, input) => {
  if (input.required) {
    validateRef(rule, value, cb, input)
  } else {
    return cb()
  }
}

// 判断字符串必须以"["开头，并且以"]"结尾的
const isWrappedInBrackets = (str) => {
  // 使用正则表达式检查字符串格式
  return /^\[.*\]$/.test(str)
}
// 校验dubbo入参
const testDubboParam = (rule, value, callback) => {
  if (!value) {
    return callback(new Error('此项为必填项'))
  } else {
    try {
      // 判断字符串必须以"["开头，并且以"]"结尾的
      if (!isWrappedInBrackets(value)) {
        return callback(new Error('此项应为数组！'))
      } else {
        return callback()
      }
    } catch (error) {
      return callback('请检查，格式错误', error)
    }
  }
}
const validateType = (rule, value, callback, curObj) => {
  const { required } = curObj
  if ((required == null || required) && !value) {
    return callback(new Error('此为必填项'))
  } else {
    return callback()
  }
}

const codeFormRef = ref(null)
const validate = async () => {
  try {
    return await codeFormRef.value.validate()
  } catch (error) {
    return false
  }
}
defineExpose({ validate })
</script>
<style lang="scss" scoped>
.model-box {
  display: flex;
  width: 100%;
  background: rgba(46, 46, 56, 0.04);
  border-radius: 5px;
  padding: 10px 10px 0 10px;
  margin-bottom: 10px;
  .flex-1 {
    flex: 1;
  }
  :deep(.oz-select) {
    width: 100%;
  }
}
.inputs-box {
  width: 100%;
  margin-bottom: 10px;
}
.t-left {
  display: flex;
  align-items: center;
}
.title-tooltip {
  margin-left: 4px;
}
.output-item {
  display: flex;
  justify-content: space-between;
  .name-box {
    flex: 1;
  }
}
.val-box {
  display: flex;
}
.empty-item {
  padding-top: 24px;
}
.btns {
  width: 58px;
  flex-basis: 48px;
  padding-left: 4px;
  display: flex;
  justify-content: space-between;
  .oz-button + .oz-button {
    margin-left: 5px;
  }
  .icon-btn {
    font-size: 14px;
  }
}
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
  .input-box {
    flex: 1;
    display: flex;
  }
}
.var-tree {
  background-color: transparent;
}
.tree-t {
  display: flex;
  padding-bottom: 5px;
  .var-label {
    font-size: 12px;
    font-weight: 600;
  }
  .var-type {
    flex-basis: 180px;
  }
  .var-name {
    flex: 1;
  }
}
.code-mirror-item {
  :deep(.CodeMirror) {
    font-size: 14px;
    line-height: 150%;
  }
}
.arr-item {
  display: flex;
  align-items: center;
}
.dubbo-params {
  width: 100%;
  max-height: 300px;
  overflow-y: auto;
}
</style>
