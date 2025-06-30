<template>
  <LLMBatchSel v-model="flowNode.batchType" @change="changeIsBatch" :disabled="disabled" />
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
        <ModalLLMModelSel
          v-model="flowNode.coreSetting.gptModel"
          v-if="flowNode.nodeType == 'llmImageUnderstand'"
          apiType="m78-image-understand-model"
        />
        <LLMModelSel v-model="flowNode.coreSetting.gptModel" v-else />
      </el-form-item>
      <el-form-item label="Temperature" class="flex-1" prop="coreSetting.temperature">
        <template #label>
          <LLMTepTooltip />
        </template>
        <el-input-number
          v-model.number="flowNode.coreSetting.temperature"
          :max="1.9"
          :min="0"
          :precision="1"
          :step="0.1"
          size="small"
          controls-position="right"
          :controls="false"
        />
      </el-form-item>
      <el-form-item label="超时时间" class="flex-1" prop="coreSetting.timeout">
        <el-input-number
          v-model.number="flowNode.coreSetting.timeout"
          size="small"
          controls-position="right"
          :controls="false"
        />
        ms
      </el-form-item>
    </div>
    <!-- 批处理 -->
    <el-collapse v-model="activeNames3" class="inputs-box" v-if="flowNode.batchType == 'batch'">
      <el-collapse-item name="1">
        <template #title>
          <div class="t-box">
            <div class="t-left">
              <CollapseTitle
                :activeNames="activeNames3"
                title="批处理"
                content="批处理模式下节点会多次运行，在每一次运行中，批处理列</br>表将按照顺序将列表中的一项赋值给批处理变量，直到达到</br>批处理上限或列表最大长度"
                tipClass="title-tooltip"
                :showAdd="false"
              />
            </div>
            <el-button link @click.stop="addBatch" size="small">
              <i class="iconfont icon-plus1"></i>
            </el-button>
          </div>
        </template>
        <el-form-item class="config-line">
          <i class="flex1">间隔等待时间</i>
          <div>
            <el-input-number
              v-model="flowNode.batchInfo.$$TY_BATCH_TIME_INTERVAL$$"
              class="max-batch-input"
              :controls="false"
            ></el-input-number>
            <i style="margin-left: 3px">ms</i>
          </div>
        </el-form-item>
        <div v-for="(item, i) in flowNode.batchInfo.arr" :key="item" class="output-item">
          <el-form-item
            :label="i > 0 ? '' : '参数名'"
            :class="i > 0 ? 'empty-label' : ''"
            :prop="'batchInfo.arr.' + i + '.name'"
            :rules="{
              validator: (rule, value, cb) => {
                validPName(rule, value, cb, flowNode.batchInfo.arr)
              },
              trigger: 'change'
            }"
          >
            <RefInput
              v-model="item.name"
              placeholder="请输入参数名"
              style="width: 200px"
              :node="flowNode"
              :relSelf="true"
            />
          </el-form-item>
          <div class="val-box">
            <el-form-item
              label=""
              class="empty-item batch-refer-form-item"
              :class="i > 0 ? 'empty-label' : ''"
              :prop="'batchInfo.arr.' + i + '.refDetail'"
              :rules="{
                validator: (rule, value, cb) => {
                  validateRef(rule, value, cb, flowNode.batchInfo.arr[i])
                },
                trigger: 'blur'
              }"
            >
              <QuotaCas
                :nodeId="flowNode.id"
                v-model="item.referenceInfo"
                class="batch-refer"
                :options="batchOps"
                @change="changeBatchRef"
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
                :disabled="flowNode?.batchInfo?.arr?.length == 1"
              >
                <i class="iconfont icon-jian" style="font-size: 14px"></i>
              </el-button>
            </el-form-item>
          </div>
        </div>
        <!-- <el-form-item>
              <span class="max-batch">最大批处理次数</span>
              <el-input
                v-model="flowNode.batchInfo.$$TY_BATCH_MAX_TIMES$$"
                controls-position="right"
                class="max-batch-input"
                @change="changeMax"
              ></el-input>
            </el-form-item> -->
      </el-collapse-item>
    </el-collapse>
    <el-collapse v-model="activeNames" class="inputs-box">
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
              validator: (rule, value, cb) => {
                validPName(rule, value, cb, flowNode.inputs)
              },
              trigger: 'change'
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
              <!-- 输入 -->
              <QuotaCas
                v-model="item.referenceInfo"
                :options="flowNode.batchType == 'batch' ? referBatchOps : referOps"
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
    <el-collapse
      v-model="activeNamesImg"
      class="inputs-box"
      v-if="flowNode.nodeType == 'llmImageUnderstand'"
    >
      <el-collapse-item name="1">
        <template #title>
          <div class="t-box">
            <div class="t-left">
              <CollapseTitle
                :activeNames="activeNamesImg"
                title="图片理解"
                content="图片理解"
                tipClass="title-tooltip"
                :showAdd="false"
              />
            </div>
            <el-button link @click.stop="addImg" size="small">
              <i class="iconfont icon-plus1"></i>
            </el-button>
          </div>
        </template>
        <div v-for="(item, i) in flowNode.inputsImg" :key="item" class="output-item">
          <el-form-item
            :label="i > 0 ? '' : '参数名'"
            :class="i > 0 ? 'empty-label' : ''"
            :prop="'inputsImg.' + i + '.name'"
            :rules="{
              validator: (rule, value, cb) => {
                validPName(rule, value, cb, flowNode.inputsImg)
              },
              trigger: 'change'
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
              <OutputTypeSel v-model="item.type" :disabled="true" :showImg="true" />
            </el-form-item>
            <el-form-item
              label=""
              class="empty-item"
              :class="i > 0 ? 'empty-label' : ''"
              :prop="'inputsImg.' + i + '.value'"
              :rules="{
                validator: (rule, value, cb) => {
                  validateRef(rule, value, cb, flowNode.inputsImg[i])
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
              :prop="'inputsImg.' + i + '.referenceInfo'"
              :rules="{
                validator: (rule, value, cb) => {
                  validateRef(rule, value, cb, flowNode.inputsImg[i])
                },
                trigger: 'blur'
              }"
            >
              <!-- 图片引用 -->
              <QuotaCas v-model="item.referenceInfo" :options="imgReferOps" />
            </el-form-item>
            <el-form-item class="empty-item" :class="i > 0 ? 'empty-label' : ''">
              <el-button
                link
                @click.stop="
                  () => {
                    delInFnImg(i)
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
    <el-collapse v-model="activeNames1" class="inputs-box">
      <el-collapse-item name="1">
        <template #title>
          <div class="t-box">
            <div class="t-left">
              <CollapseTitle
                :activeNames="activeNames1"
                title="提示词"
                content="编辑大模型的提示词以实现对应功能。可以使用${变量名}的</br>方式引入输入参数中的变量"
                tipClass="title-tooltip"
                :showAdd="false"
              />
            </div>
            <el-button link @click.stop="editClick">在大屏编辑</el-button>
          </div>
        </template>
        <el-form-item class="tips" prop="coreSetting.promptContent">
          <FlowTextarea
            v-model="flowNode.coreSetting.promptContent"
            placeholder="可以使用${变量名}的方式引入输入参数中的变量"
            :autosize="{ minRows: 4, maxRows: 10 }"
          />
        </el-form-item>
      </el-collapse-item>
    </el-collapse>
    <el-collapse v-model="activeNames2" class="inputs-box">
      <el-collapse-item name="1">
        <template #title>
          <div class="t-box">
            <div class="t-left">
              <CollapseTitle
                :activeNames="activeNames2"
                title="输出"
                content="大模型运行完成后生成的内容"
                tipClass="title-tooltip"
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
        <OutPutsTree
          v-model="flowNode.outputs"
          :showDesc="true"
          :nodeType="flowNode.nodeType"
          :batchType="flowNode.batchType"
          :flowNode="flowNode"
        />
      </el-collapse-item>
    </el-collapse>
  </el-form>
  <el-drawer v-model="showEditor" title="提示词" size="50%" append-to-body>
    <el-input
      type="textarea"
      v-model="flowNode.coreSetting.promptContent"
      :autosize="{ minRows: 20 }"
      class="prompt-editor"
    ></el-input>
    <template #footer>
      <el-button type="primary" @click="showEditor = false"> 确定 </el-button>
    </template>
  </el-drawer>
</template>

<script setup>
import { ref, computed, defineExpose, inject } from 'vue'
import LLMModelSel from '@/components/LLMModelSel.vue'
import TitleTooltip from './TitleTooltip.vue'
import OutputTypeSel from './components/OutputTypeSel.vue'
import QuotaCas from './components/QuotaCas'
import {
  validateRef,
  validPName,
  validateTreeInput,
  getReferOps,
  getBatchRefer,
  filterImgRefTree,
  filterImgBatchRefTree,
  initOpsByNodeList,
  classifyNodes
} from '../baseInfo'
import LLMBatchSel from './components/LLMBatchSel'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import LLMTepTooltip from './components/LLMTepTooltip'
import { useVueFlow } from '@vue-flow/core'
import { useWfStore } from '@/stores/workflow1'
import OutPutsTree from './components/OutPutsTree'
import CollapseTitle from './components/CollapseTitle.vue'
import RefInput from '@/views/workflow/work-flow/components/components/RefInput.vue'
import FlowTextarea from './components/FlowTextarea.vue'

const wfStore = useWfStore()
const { setNodesPreNodes } = wfStore
const { toObject } = useVueFlow()
const draging = computed(() => wfStore.nodeDragging)
const nodesPreNodes = computed(() => wfStore.nodesPreNodes)
import ModalLLMModelSel from '@/components/ModalLLMModelSel.vue'

const props = defineProps({
  modelValue: {},
  nodes: {},
  lines: {},
  disabled: {},
  workspaceId: {},
  getDetailed: {},
  referOps: {},
  referBatchOps: {}
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
const imgReferOps = computed(() => {
  const { batchType, nodeType, id } = flowNode.value
  if (nodeType != 'llmImageUnderstand') return []
  const beginNodeRefImg =
    batchType == 'batch'
      ? filterImgBatchRefTree(props.referBatchOps, id)
      : filterImgRefTree(props.referOps)
  return beginNodeRefImg
})

const rules = ref({
  'coreSetting.temperature': [{ required: true, message: '参数值不能为空', trigger: 'blur' }],
  'coreSetting.promptContent': [{ required: true, message: '提示词不能为空', trigger: 'blur' }]
})

const activeNames = ref(['1'])
const activeNames1 = ref(['1'])
const activeNames2 = ref(['1'])
const activeNames3 = ref(['1'])
const activeNamesImg = ref(['1'])

const showEditor = ref(false)
const editClick = () => {
  switchShowEditor()
}
const switchShowEditor = () => {
  showEditor.value = !showEditor.value
}
let id = 1000
const addParam = () => {
  flowNode.value.inputs.push({ name: '', type: 'reference' })
}
const addImg = () => {
  console.log('flowNode.value.inputsImg', flowNode.value.inputsImg)
  flowNode.value.inputsImg.push({ name: '', type: 'imageReference' })
}

const addBatch = () => {
  flowNode.value.batchInfo.arr.push({
    name: '',
    type: 'reference'
  })
}

const delBatch = (i) => {
  // 删除选项
  flowNode.value.batchInfo.arr.splice(i, 1)
  // 修改当前节点的引用options
  changeBatchRef()
}

const delInFn = (i) => {
  flowNode.value.inputs.splice(i, 1)
}
const delInFnImg = (i) => {
  flowNode.value.inputsImg.splice(i, 1)
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
  flowNode.value.inputs = [{ name: '', type: 'reference', referenceInfo: [] }]
  if (val == 'batch') {
    const children = flowNode.value.outputs
    flowNode.value.outputs = [
      { name: 'outputList', valueType: 'Array<Object>', desc: '', children }
    ]
  } else if (val == 'single') {
    const children = (flowNode.value.outputs && flowNode.value.outputs[0]?.children) || []
    flowNode.value.outputs = children
  }
  flowNode.value.inputsImg = [{ type: 'imageReference' }]
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
  if (node?.parent?.data?.children?.length == 1) {
    ElMessage.warning('至少有一个，不能删除！')
    return
  }
  const parent = node.parent
  const children = parent.data.children || parent.data
  const index = children.findIndex((d) => d.id === data.id)
  children.splice(index, 1)
  flowNode.value.outputs = [...flowNode.value.outputs]
}

const append = (data) => {
  const newChild = { name: '', valueType: '', children: [] }
  if (!data.children) {
    data.children = []
  }
  data.children.push(newChild)
  flowNode.value.outputs = [...flowNode.value.outputs]
}

const loopChild = (arr = [], nodeType) => {
  const curLoop = []
  const type = nodeType == 'knowledge' ? 'desc' : 'valueType'
  const arrFilter = arr.filter((item) => item.valueType?.startsWith('Array<'))
  arrFilter.forEach((item) => {
    curLoop.push({
      ...item,
      children: loopChild(item.children, nodeType)
    })
  })
  return curLoop
}
const batchOps = computed(() => {
  // 先把最外层循环一次
  const arr = props.referOps.map((item) => {
    return {
      ...item,
      children: loopChild(item.children, item.nodeType)
    }
  })
  return arr
})

const emtpyRefData = inject('emtpyRefData')
// 修改批处理的引用项目时候
const changeBatchRef = (val) => {
  const { affectedNodes, unaffectedNodes } = classifyNodes(
    true,
    nodesPreNodes.value,
    flowNode.value.id
  )
  const newArr = [...initRelationNodesPreIds(affectedNodes), ...unaffectedNodes]
  setNodesPreNodes(newArr)
  // 置空绑定值
  emtpyRefData(affectedNodes, flowNode.value.id)
}

const initRelationNodesPreIds = (affNodes) => {
  const obj = toObject()
  return initOpsByNodeList(affNodes, obj)
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
  color: #606266;
  font-weight: 500;
}
.max-batch-input {
  width: 230px;
}
.outputs-tree {
  background: transparent;
  margin-bottom: 10px;
  :deep(.oz-tree-node__content) {
    height: auto;
    align-items: flex-start;
  }
}
.out-desc {
  width: 140px;
  height: 25px;
}
.tree-parent {
  width: 153px;
}
.tree-child {
  width: 135px;
}

.inputs-width {
  width: 161px;
}
.batch-refer-form-item {
  :deep(.batch-refer) {
    width: 230px;
  }
}
.custom-tree-node {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  font-size: 14px;
  padding-right: 8px;
  .input-box {
    flex: 1;
    display: flex;
  }
  :deep(.oz-form-item--small) {
    margin-bottom: 14px;
  }
  .tree-btn {
    width: 58px;
    :deep(.oz-button + .oz-button) {
      margin-left: 1px;
    }
    :deep(.oz-icon) {
      font-size: 15px;
    }
  }
}
.prompt-editor {
  height: 100%;
  :deep(.oz-textarea__inner) {
    height: 100% !important;
  }
}
.config-line {
  display: flex;
  justify-content: space-between;
  width: 100%;
}
.flex1 {
  flex: 1;
}
</style>
