<template>
  <el-form
    :model="flowNode"
    size="small"
    label-position="top"
    inline
    class="ddl-form"
    ref="codeFormRef"
    :disabled="disabled"
  >
    <div class="inputs-box">
      <el-collapse v-model="activeNames">
        <el-collapse-item name="1">
          <template #title>
            <div class="t-box">
              <div class="t-left">
                <el-icon>
                  <ArrowDown v-if="activeNames.includes('1')" />
                  <ArrowRight v-else />
                </el-icon>
                <TitleTooltip
                  title="输入"
                  content="输入需要添加到提示词的信息，这些信息可以被下方的提示词引用"
                  class="title-tooltip"
                  :showAdd="false"
                />
              </div>
              <el-button link @click.stop="addParam" size="small">
                <i class="iconfont icon-plus1"></i>
              </el-button>
            </div>
          </template>
          <div v-for="(item, i) in flowNode.inputs" :key="item" class="output-item">
            <el-form-item
              :label="i < 1 ? '变量名' : ''"
              :prop="'inputs.' + i + '.name'"
              :rules="{
                required: true,
                message: '参数值不可为空',
                trigger: 'blur'
              }"
            >
              <el-input
                v-model="item.name"
                placeholder="请输入参数名"
                style="width: 200px"
                maxlength="20"
              />
            </el-form-item>
            <div class="val-box">
              <el-form-item
                :label="i < 1 ? '变量值' : ''"
                :prop="'inputs.' + i + '.type'"
                :rules="{
                  required: true,
                  message: '参数值不可为空',
                  trigger: 'blur'
                }"
              >
                <OutputTypeSel v-model="item.type" />
              </el-form-item>
              <el-form-item
                label=""
                :class="i == 0 ? 'empty-item' : ''"
                :prop="'inputs.' + i + '.value'"
                :rules="{
                  validator: (rule, value, cb) => {
                    validateRef(rule, value, cb, flowNode.inputs[i])
                  },
                  trigger: 'blur'
                }"
                v-if="item.type == 'value'"
              >
                <el-input
                  v-model="item.value"
                  placeholder="请输入参数值"
                  :style="refreStyle"
                  maxlength="20"
                />
              </el-form-item>
              <el-form-item
                v-else
                label=""
                :class="i == 0 ? 'empty-item' : ''"
                :prop="'inputs.' + i + '.referenceInfo'"
                :rules="{
                  validator: (rule, value, cb) => {
                    validateRef(rule, value, cb, flowNode.inputs[i])
                  },
                  trigger: 'blur'
                }"
              >
                <QuotaCas :nodeId="flowNode.id" v-model="item.referenceInfo" :style="refreStyle" />
              </el-form-item>
              <el-form-item :class="i == 0 ? 'empty-item' : ''">
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
              </el-form-item>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div class="inputs-box">
      <el-collapse v-model="activeNames1">
        <el-collapse-item name="1">
          <template #title>
            <div class="t-box">
              <div class="t-left">
                <el-icon>
                  <ArrowDown v-if="activeNames.includes('1')" />
                  <ArrowRight v-else />
                </el-icon>
                <TitleTooltip
                  title="代码"
                  content="参考代码示例编写一个函数的结构，你可以直接使用输入参</br>数中的变量，并通过return一个对象来输出处理结果此功能</br>不支持编写多个函数即使仅有一个输出值，也务必保持以对</br>象的形式return"
                  class="title-tooltip"
                  :showAdd="false"
                />
              </div>
              <div>
                <el-button link @click.stop="showGenFn">
                  <i class="iconfont icon-code" style="margin-right: 2px"></i>
                  生成代码
                </el-button>
                <el-button link :icon="Edit" @click.stop="showIde">在IDE中编辑</el-button>
              </div>
            </div>
          </template>
          <el-form-item class="tips nowheel">
            <Codemirror
              v-model:value="flowNode.coreSetting.code"
              placeholder=""
              :options="cmOptions"
              border
              :height="140"
              class="code-mirror-item"
            />
          </el-form-item>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div class="inputs-box">
      <el-collapse v-model="activeNames2">
        <el-collapse-item name="1">
          <template #title>
            <div class="t-box">
              <div class="t-left">
                <el-icon>
                  <ArrowDown v-if="activeNames2.includes('1')" />
                  <ArrowRight v-else />
                </el-icon>
                <TitleTooltip
                  title="输出"
                  raw-content
                  content="代码运行完成后输出的变最，必须保证此处定义的变量名、</br>变量类型与代码的return对象中完全一致"
                  class="title-tooltip"
                  :showAdd="false"
                />
              </div>
              <el-button link @click.stop="addFn" size="small">
                <i class="iconfont icon-plus1"></i>
              </el-button>
            </div>
          </template>
          <el-form-item
            class="tree-form-item"
            :rules="{
              validator: validateTree,
              trigger: 'blur'
            }"
            prop="outputs"
          >
            <div class="tree-t">
              <p class="var-name var-label">变量名</p>
              <p class="var-type var-label">变量类型</p>
            </div>
            <el-tree
              :data="flowNode.outputs"
              node-key="id"
              default-expand-all
              :expand-on-click-node="false"
              class="var-tree"
              :props="{ children: 'schema' }"
            >
              <template #default="{ node, data }">
                <span class="custom-tree-node">
                  <div class="input-box">
                    <el-input v-model="data.name" placeholder="变量名" maxlength="20" />
                    <VariateTypeSel v-model="data.valueType" />
                  </div>
                  <div class="btns">
                    <el-button
                      link
                      @click="remove(node, data)"
                      :disabled="flowNode.outputs.length == 1"
                    >
                      <i class="iconfont icon-jian icon-btn"></i>
                    </el-button>
                    <el-button
                      link
                      @click="append(data)"
                      v-if="['Object', 'Array<Object>'].indexOf(data.valueType) > -1"
                    >
                      <i class="iconfont icon-plus1 icon-btn"></i>
                    </el-button>
                  </div>
                </span>
              </template>
            </el-tree>
          </el-form-item>
        </el-collapse-item>
      </el-collapse>
    </div>
    <CodeEditor v-model="showEditor" v-model:code="flowNode.coreSetting.code" />
    <CodeGenerator v-model="showGenerator" @codeGenRes="codeGenRes" />
  </el-form>
</template>

<script setup>
import { ref, computed, defineExpose } from 'vue'
import TitleTooltip from './TitleTooltip.vue'
import OutputTypeSel from './components/OutputTypeSel.vue'
import VariateTypeSel from './components/VariateTypeSel'
import { validateRef, validPName } from '../baseInfo'
import Codemirror from 'codemirror-editor-vue3'
import 'codemirror/theme/erlang-dark.css'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/mode/groovy/groovy.js'
import QuotaCas from './components/QuotaCas'
import { Edit } from '@element-plus/icons-vue'
import CodeEditor from './components/CodeEditor'
import CodeGenerator from './components/CodeGenerator'

const showEditor = ref(false)
const cmOptions = {
  mode: 'text/groovy', // 语言模式
  lineNumbers: false, // 显示行号
  theme: 'erlang-dark',
  readOnly: true
}

const props = defineProps({
  modelValue: {},
  nodes: {},
  lines: {},
  disabled: {}
})
const emits = defineEmits(['update:modelValue'])
const flowNode = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const refreStyle = ref({
  width: '170px'
})
const activeNames = ref('1')
const activeNames1 = ref('1')
const activeNames2 = ref('1')
const showGenerator = ref(false)

const switchShowGen = () => {
  showGenerator.value = !showGenerator.value
}
const showGenFn = () => {
  switchShowGen()
}
const addParam = () => {
  flowNode.value.inputs.push({ name: '', val: '' })
}
const delInFn = (i) => {
  flowNode.value.inputs.splice(i, 1)
}
let id = 1000
const append = (data) => {
  // eslint-disable-next-line no-const-assign
  const newChild = { id: id++, label: 'testtest', schema: [] }
  if (!data.schema) {
    data.schema = []
  }
  data.schema.push(newChild)
}
const addFn = () => {
  const newObj = {
    id: id++,
    name: '',
    type: 'String'
  }
  flowNode.value.outputs.push(newObj)
}

const remove = (treeNode, data) => {
  const parent = treeNode.parent
  const schema = parent.data.schema || parent.data
  const index = schema.findIndex((d) => d.id === data.id)
  schema.splice(index, 1)
}

const switchShowIde = () => {
  showEditor.value = !showEditor.value
}

const showIde = () => {
  switchShowIde()
}
const delFn = (i) => {
  flowNode.value.outputs.splice(i, 1)
}
const codeGenRes = (res) => {
  flowNode.value.coreSetting.code = res.code
  const resInputs = res.params.map((item) => {
    return {
      ...item
    }
  })
  const resOutputs = res.outs.map((item) => {
    return {
      ...item,
      valueType: item.type
    }
  })
  console.log('resInputs', resInputs, resOutputs)
  flowNode.value.inputs = [...flowNode.value.inputs, ...resInputs]
  flowNode.value.outputs = [...flowNode.value.outputs, ...resOutputs]
}
const codeFormRef = ref(null)
const validate = async () => {
  try {
    return await codeFormRef.value.validate()
  } catch (error) {
    return false
  }
}
const isValidName = (name) => {
  // 检查是否为空
  if (!name.trim()) {
    return false
  }
  // 以字母或下划线开头且仅包含字母,数字,下划线
  if (!/^[a-zA-Z_]([a-zA-Z0-9_]+)?$/.test(name)) {
    return false
  }
  return true
}
const validTree = (data, parentNames = new Set()) => {
  const namesSet = new Set()
  for (const item of data) {
    // 检查当前元素的名称
    if (!isValidName(item.name)) {
      return { isFailed: true, msg: '变量名必填,只可包含字母,数字,下划线,以字母或下划线开头' }
    }
    // 检查是否存在相同名称的子元素
    if (namesSet.has(item.name)) {
      return { isFailed: true, msg: '每个父级元素下的子元素名称要唯一' }
    }
    namesSet.add(item.name)
    // 检查子元素（如果存在）
    if (item.schema && item.schema.length > 0) {
      const isValidChildren = validTree(item.schema, [...parentNames, item.name])
      if (isValidChildren.isFailed) {
        return isValidChildren
      }
    }
  }
  return { idFailed: false }
}

const validateTree = (rule, value, callback) => {
  const validRes = validTree(flowNode.value.outputs)
  const { isFailed, msg } = validRes
  if (isFailed) {
    callback(new Error(msg))
  } else {
    callback()
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
  flex: 1;
  display: flex;
  padding: 0 0 5px 25px;
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
.tree-form-item {
  :deep(.oz-form-item__content) {
    display: block;
  }
  :deep(.oz-form-item__error) {
    padding-left: 22px;
  }
}
</style>
