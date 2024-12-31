<template>
  <div>
    <LLMBatchSel v-model="flowNode.batchType" @change="changeIsBatch" />
    <el-form
      :model="flowNode"
      size="small"
      label-position="top"
      inline
      class="ddl-form"
      ref="llmFormRef"
      :rules="rules"
      :disabled="disabled"
    >
      <div class="model-box">
        <el-form-item label="模型" class="flex-1">
          <LLMModelSel v-model="flowNode.coreSetting.gptModel" />
        </el-form-item>
        <el-form-item label="Temperature" class="flex-1" prop="coreSetting.temperature">
          <el-input-number
            v-model.number="flowNode.coreSetting.temperature"
            :min="1"
            size="small"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="超时时间" class="flex-1" prop="coreSetting.timeout">
          <el-input-number
            v-model.number="flowNode.coreSetting.timeout"
            size="small"
            controls-position="right"
          />
          ms
        </el-form-item>
      </div>
      <!-- 批处理 -->
      <div class="inputs-box" v-if="flowNode.batchType == 'batch'">
        <el-collapse v-model="activeNames3">
          <el-collapse-item name="1">
            <template #title>
              <div class="t-box">
                <div class="t-left">
                  <el-icon>
                    <ArrowDown v-if="activeNames3.includes('1')" />
                    <ArrowRight v-else />
                  </el-icon>
                  <TitleTooltip
                    title="批处理"
                    raw-content
                    content="批处理模式下节点会多次运行，在每一次运行中，批处理列</br>表将按照顺序将列表中的一项赋值给批处理变量，直到达到</br>批处理上限或列表最大长度"
                    class="title-tooltip"
                    :showAdd="false"
                  />
                </div>
                <el-button link @click.stop="addBatch" size="small">
                  <i class="iconfont icon-plus1"></i>
                </el-button>
              </div>
            </template>
            <div v-for="(item, i) in flowNode.batchInfo.arr" :key="item" class="output-item">
              <el-form-item
                :label="i > 0 ? '' : '参数名'"
                :class="i > 0 ? 'empty-label' : ''"
                :prop="'batchInfo.arr.' + i + '.name'"
                :rules="{
                  required: true,
                  message: '参数值不可为空',
                  trigger: 'blur'
                }"
              >
                <el-input v-model="item.name" placeholder="请输入参数名" style="width: 200px" />
              </el-form-item>
              <div class="val-box">
                <el-form-item
                  label=""
                  class="empty-item batch-refer-form-item"
                  :class="i > 0 ? 'empty-label' : ''"
                  :prop="'batchInfo.arr.' + i + '.refDetail'"
                  :rules="{
                    validator: (rule, value, cb) => {
                      validateRef(rule, value, cb, flowNode.inputs[i])
                    },
                    trigger: 'blur'
                  }"
                >
                  <QuotaCasBatch
                    :nodeId="flowNode.id"
                    v-model="item.referenceInfo"
                    class="batch-refer"
                  />
                </el-form-item>
                <el-form-item class="empty-item" :class="i > 0 ? 'empty-label' : ''">
                  <el-button
                    link
                    @click.stop="
                      () => {
                        delBatch(i)
                      }
                    "
                  >
                    <i class="iconfont icon-jian" style="font-size: 14px"></i>
                  </el-button>
                </el-form-item>
              </div>
            </div>
            <el-form-item>
              <span class="max-batch">最大批处理次数</span>
              <el-input
                v-model="flowNode.batchInfo.$$TY_BATCH_MAX_TIMES$$"
                controls-position="right"
                class="max-batch-input"
                @change="changeMax"
              ></el-input>
            </el-form-item>
          </el-collapse-item>
        </el-collapse>
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
                <el-button link @click.stop="addParam" size="small">
                  <i class="iconfont icon-plus1"></i>
                </el-button>
              </div>
            </template>
            <div v-for="(item, i) in flowNode.inputs" :key="item" class="output-item">
              <el-form-item
                :label="i > 0 ? '' : '参数名'"
                :class="i > 0 ? 'empty-label' : ''"
                :prop="'inputs.' + i + '.name'"
                :rules="{
                  required: true,
                  message: '参数值不可为空',
                  trigger: 'blur'
                }"
              >
                <el-input v-model="item.name" placeholder="请输入参数名" style="width: 200px" />
              </el-form-item>
              <div class="val-box">
                <el-form-item
                  class="m-r-0"
                  :label="i > 0 ? '' : '变量值'"
                  :class="i > 0 ? 'empty-label' : ''"
                >
                  <OutputTypeSel v-model="item.type" />
                </el-form-item>
                <el-form-item
                  label=""
                  class="empty-item"
                  :class="i > 0 ? 'empty-label' : ''"
                  :prop="'inputs.' + i + '.value'"
                  :rules="{
                    validator: (rule, value, cb) => {
                      validateRef(rule, value, cb, flowNode.inputs[i])
                    },
                    trigger: 'blur'
                  }"
                  v-if="item.type == 'value'"
                >
                  <el-input v-model="item.value" placeholder="请输入参数值" class="inputs-width" />
                </el-form-item>
                <el-form-item
                  v-else
                  label=""
                  class="empty-item"
                  :class="i > 0 ? 'empty-label' : ''"
                  :prop="'inputs.' + i + '.referenceInfo'"
                  :rules="{
                    validator: (rule, value, cb) => {
                      validateRef(rule, value, cb, flowNode.inputs[i])
                    },
                    trigger: 'blur'
                  }"
                >
                  <QuotaCasLLM
                    :nodeId="flowNode.id"
                    v-model="item.referenceInfo"
                    class="inputs-width"
                    v-if="flowNode.batchType == 'batch'"
                  />
                  <QuotaCas
                    v-else
                    :nodeId="flowNode.id"
                    v-model="item.referenceInfo"
                    class="inputs-width"
                  />
                </el-form-item>
                <el-form-item class="empty-item" :class="i > 0 ? 'empty-label' : ''">
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
                    <ArrowDown v-if="activeNames1.includes('1')" />
                    <ArrowRight v-else />
                  </el-icon>
                  <TitleTooltip
                    title="提示词"
                    content="编辑大模型的提示词以实现对应功能。可以使用{变量名}的</br>方式引入输入参数中的变量"
                    class="title-tooltip"
                    :showAdd="false"
                  />
                </div>
              </div>
            </template>
            <el-form-item class="tips" prop="coreSetting.promptContent">
              <el-input
                v-model="flowNode.coreSetting.promptContent"
                placeholder="可以使用${变量名}的方式引入输入参数中的变量"
                type="textarea"
                :autosize="{ minRows: 4, maxRows: 10 }"
                class="tips-text nowheel"
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
                    content="大模型运行完成后生成的内容"
                    class="title-tooltip"
                    :showAdd="false"
                  />
                </div>
                <el-button
                  link
                  @click.stop="addOutput"
                  size="small"
                  :disabled="flowNode.batchType == 'batch'"
                >
                  <i class="iconfont icon-plus1"></i>
                </el-button>
              </div>
            </template>
            <div v-if="flowNode.batchType == 'single'">
              <div class="output-item" v-for="(item, i) in flowNode.outputs" :key="item.id">
                <el-form-item
                  :label="i == 0 ? '参数名' : ''"
                  :prop="'outputs.' + i + '.name'"
                  :rules="{
                    required: true,
                    message: '参数值不可为空',
                    trigger: 'blur'
                  }"
                >
                  <el-input v-model="item.name" placeholder="请输入参数名" style="width: 150px" />
                </el-form-item>
                <el-form-item :label="i == 0 ? '变量类型' : ''">
                  <LLMOutputsTypeSel v-model="item.valueType" style="width: 150px" />
                </el-form-item>
                <el-form-item :label="i == 0 ? '变量描述' : ''">
                  <el-input
                    v-model="item.desc"
                    :autosize="{ minRows: 2, maxRows: 4 }"
                    placeholder="请输入参数描述"
                  />
                </el-form-item>
                <el-form-item class="empty-item" :class="i > 0 ? 'empty-label' : ''">
                  <el-button
                    link
                    @click.stop="
                      () => {
                        delOutFn(i)
                      }
                    "
                    :disabled="flowNode.outputs.length === 1"
                  >
                    <i class="iconfont icon-jian" style="font-size: 14px"></i>
                  </el-button>
                </el-form-item>
              </div>
            </div>
            <div v-else>
              <el-tree
                :data="flowNode.outputs"
                node-key="id"
                default-expand-all
                :expand-on-click-node="false"
                class="outputs-tree"
              >
                <template #default="{ node, data }">
                  <span class="custom-tree-node">
                    <el-input
                      v-model="data.name"
                      placeholder="请输入参数名"
                      :class="node.level == 1 ? 'tree-parent' : 'tree-child'"
                      :disabled="flowNode.batchType == 'batch' && node.level == 1"
                    />
                    <LLMOutputsTypeSel
                      v-model="data.valueType"
                      style="width: 110px"
                      :disabled="flowNode.batchType == 'batch' && node.level == 1"
                    />
                    <el-input
                      v-model="data.desc"
                      :autosize="{ minRows: 2, maxRows: 4 }"
                      placeholder="请输入参数描述"
                      class="out-desc"
                      :disabled="flowNode.batchType == 'batch' && node.level == 1"
                    />
                    <span class="tree-btn">
                      <el-button link @click.stop="remove(node, data)" :disabled="node.level == 1">
                        <i class="iconfont icon-jian" style="font-size: 14px"></i>
                      </el-button>
                      <el-button
                        link
                        @click.stop="append(data)"
                        :icon="Plus"
                        v-if="node.level == 1"
                      >
                      </el-button>
                    </span>
                  </span>
                </template>
              </el-tree>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, computed, defineExpose, watch } from 'vue'
import LLMModelSel from '@/components/LLMModelSel.vue'
import TitleTooltip from './TitleTooltip.vue'
import OutputTypeSel from './components/OutputTypeSel.vue'
import QuotaCas from './components/QuotaCas'
import { validateRef } from '../baseInfo'
import LLMBatchSel from './components/LLMBatchSel'
import LLMOutputsTypeSel from './components/LLMOutputsTypeSel'
import { Plus } from '@element-plus/icons-vue'
import QuotaCasBatch from './components/QuotaCasBatch'
import QuotaCasLLM from './components/QuotaCasLLM'

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

const rules = ref({
  'coreSetting.temperature': [{ required: true, message: '参数值不能为空', trigger: 'blur' }],
  'coreSetting.promptContent': [{ required: true, message: '提示词不能为空', trigger: 'blur' }]
})
const activeNames = ref('1')
const activeNames1 = ref('1')
const activeNames2 = ref('1')
const activeNames3 = ref('1')
let id = 1000
const addParam = () => {
  flowNode.value.inputs.push({ name: '', valType: '' })
}

const addBatch = () => {
  flowNode.value.batchInfo.arr.push({
    name: '',
    type: 'reference'
  })
}
const delBatch = (i) => {
  flowNode.value.batchInfo.arr.splice(i, 1)
}
const delInFn = (i) => {
  flowNode.value.inputs.splice(i, 1)
}
const delOutFn = (i) => {
  flowNode.value.outputs.splice(i, 1)
}
const addOutput = () => {
  if (!flowNode.value.outputs) flowNode.value.outputs = []
  flowNode.value.outputs.push({ name: '', valueType: 'String', desc: '', need: true })
}
const llmFormRef = ref(null)

const changeIsBatch = (val) => {
  if (val == 'batch') {
    const children = flowNode.value.outputs
    flowNode.value.outputs = [
      { name: 'outputList', valueType: 'Array<Object>', desc: '', children }
    ]
  } else if (val == 'single') {
    const children = (flowNode.value.outputs && flowNode.value.outputs[0]?.children) || []
    flowNode.value.outputs = children
  }
}
const changeMax = (val) => {
  const num = Number(val)
  if (isNaN(num)) {
    flowNode.value.batchInfo.$$TY_BATCH_MAX_TIMES$$ = '1'
  } else {
    if (num > 10) {
      flowNode.value.batchInfo.$$TY_BATCH_MAX_TIMES$$ = '10'
    } else {
      flowNode.value.batchInfo.$$TY_BATCH_MAX_TIMES$$ = parseInt(num)
    }
  }
}

const remove = (node, data) => {
  console.log('node', node, 'data', data)
  const parent = node.parent
  const children = parent.data.children || parent.data
  const index = children.findIndex((d) => d.id === data.id)
  children.splice(index, 1)
  flowNode.value.outputs = [...flowNode.value.outputs]
}

const append = (data) => {
  const newChild = { id: id++, name: 'test', children: [] }
  if (!data.children) {
    data.children = []
  }
  data.children.push(newChild)
  flowNode.value.outputs = [...flowNode.value.outputs]
}

const validate = async () => {
  try {
    return await llmFormRef.value.validate()
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
  .val-box {
    display: flex;
  }
}
.max-batch {
  font-size: 12px;
  display: inline-block;
  width: 210px;
  color: #ababad;
}
.max-batch-input {
  width: 230px;
}
.tips-text {
  :deep(.oz-textarea__inner) {
    color: #2c2c2c;
    border-radius: unset;
  }
}
.outputs-tree {
  background: transparent;
  margin-bottom: 10px;
}
.out-desc {
  width: 140px;
}
.tree-parent {
  width: 153px;
}
.tree-child {
  width: 135px;
}
.tree-btn {
  :deep(.oz-button + .oz-button) {
    margin-left: 1px;
  }
  :deep(.oz-icon) {
    font-size: 15px;
  }
}
.inputs-width {
  width: 170px;
}
.batch-refer-form-item {
  :deep(.batch-refer) {
    width: 230px;
  }
}
</style>
