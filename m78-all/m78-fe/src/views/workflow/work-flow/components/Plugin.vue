<template>
  <el-form
    :model="node"
    size="small"
    label-position="top"
    inline
    class="ddl-form"
    ref="codeFormRef"
  >
    <div class="pt-[10px]">
      <el-form-item props="action">
        <el-radio-group v-model="node.action" size="small">
          <el-radio-button label="single" value="single">单次</el-radio-button>
          <el-radio-button label="batch" value="batch">批处理</el-radio-button>
        </el-radio-group>
      </el-form-item>
    </div>
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
            </div>
          </template>
          <div v-for="(item, i) in node.inputs" :key="item" class="output-item">
            <el-form-item
              label="变量名"
              :prop="'inputs.' + i + '.name'"
              :rules="{
                required: true,
                message: '参数值不可为空',
                trigger: 'blur'
              }"
            >
              <TitleTooltip
                :title="item.name"
                :content="item.desc || '没有相关参数描述'"
                class="title-tooltip"
                :showAdd="false"
              />
            </el-form-item>
            <div class="val-box">
              <el-form-item
                label="变量值"
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
                class="empty-item"
                :prop="'inputs.' + i + '.value'"
                :rules="{
                  validator: (rule, value, cb) => {
                    validateRef(rule, value, cb, node.inputs[i])
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
                    validateRef(rule, value, cb, node.inputs[i])
                  },
                  trigger: 'blur'
                }"
              >
                <QuotaCas :nodeId="node.id" v-model="item.referenceInfo" :style="refreStyle" />
              </el-form-item>
            </div>
          </div>
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
                  content="插件运行完成后生成的内容"
                  class="title-tooltip"
                  :showAdd="false"
                />
              </div>
              <!-- <el-button link @click.stop="addFn" size="small">
                <i class="iconfont icon-plus1"></i>
              </el-button> -->
            </div>
          </template>
          <div v-for="(item, i) in node.outputs" :key="item" class="arr-item">
            <el-form-item
              :label="i == 0 ? '变量名' : ''"
              :prop="'outputs.' + i + '.name'"
              :rules="{
                validator: (rule, value, cb) => {
                  validPName(rule, value, cb, node.outputs)
                },
                trigger: 'blur'
              }"
            >
              <el-input
                v-model="item.name"
                disabled
                style="width: 250px"
                placeholder="请输入参数名"
              />
            </el-form-item>
            <el-form-item :label="i == 0 ? '变量类型' : ''" class="m-r-0">
              <VariateTypeSel disabled v-model="item.type" />
            </el-form-item>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
  </el-form>
</template>

<script setup>
import { ref, computed, defineExpose } from 'vue'
import TitleTooltip from './TitleTooltip.vue'
import OutputTypeSel from './components/OutputTypeSel.vue'
import VariateTypeSel from './components/VariateTypeSel'
import { validateRef, validPName } from '../baseInfo'
import 'codemirror/theme/erlang-dark.css'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/mode/groovy/groovy.js'
import QuotaCas from './components/QuotaCas'

const props = defineProps({
  modelValue: {},
  nodes: {},
  lines: {}
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
const refreStyle = ref({
  width: '170px'
})
const activeNames = ref('1')
const activeNames2 = ref('1')

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
</style>
