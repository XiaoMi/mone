<template>
  <el-form
    :model="node"
    label-position="top"
    size="small"
    class="condition-form if-box"
    ref="ifFormRef"
    :disabled="disabled"
  >
    <template v-if="node">
      <div class="top">
        <div>
          <span class="t"
            >({{ node.id.split('_')[1] }}){{ conditionPosition == 0 ? '' : '否则' }}如果</span
          >
          <span class="level" v-if="sameParentIfSon.length > 1"
            >优先级{{ conditionPosition + 1 }}</span
          >
        </div>
        <div>
          <el-button link @click="addCondition" type="primary">
            <i class="iconfont icon-plus1"></i>
          </el-button>
          <el-button link @click="removeCondition" type="primary">
            <el-icon><Remove /></el-icon>
          </el-button>
        </div>
      </div>
      <div v-for="(item, i) in node.coreSetting" :key="item" class="if-item">
        <div v-if="i == 0" class="label-span">条件</div>
        <el-form-item
          class="empty-form-item sel-box"
          :class="i > 0 ? 'empty-label' : ''"
          v-else
          :prop="'coreSetting.' + i + '.relationship'"
          :rules="{
            required: true,
            message: '参数值不可为空',
            trigger: 'blur'
          }"
        >
          <ConditionLabelSel v-model="item.relationship" />
        </el-form-item>
        <el-form-item
          :label="i == 0 ? '引用变量' : ''"
          :class="i > 0 ? 'empty-label' : ''"
          :prop="'coreSetting.' + i + '.referenceInfo'"
          :rules="{
            required: true,
            message: '参数值不可为空',
            trigger: 'blur'
          }"
        >
          <QuotaCas
            v-model="item.referenceInfo"
            style="width: 130px"
            :options="options"
            @change="
              () => {
                changeRef(i)
              }
            "
          />
        </el-form-item>
        <el-form-item
          :label="i == 0 ? '选择条件' : ''"
          :class="i > 0 ? 'empty-label' : ''"
          :prop="'coreSetting.' + i + '.operator'"
          :rules="{
            required: true,
            message: '参数值不可为空',
            trigger: 'blur'
          }"
        >
          <ConditionsSelNew
            v-model="item.operator"
            :referenceInfo="item.referenceInfo"
            :parentNodeType="parentNode.nodeType"
          />
        </el-form-item>
        <template v-if="!switchEmptyTypes.includes(item.operator)">
          <el-form-item
            :label="i == 0 ? '比较值' : ''"
            :class="i > 0 ? 'empty-label' : ''"
            :prop="'coreSetting.' + i + '.type2'"
            :rules="{
              required: true,
              message: '不可为空',
              trigger: 'blur'
            }"
          >
            <OutputTypeSel v-model="item.type2" />
          </el-form-item>
          <el-form-item
            class="empty-form-item"
            :class="i > 0 ? 'empty-label' : ''"
            :prop="'coreSetting.' + i + '.referenceInfo2'"
            :rules="{
              validator: (rule, value, cb) => {
                validateRef2(rule, value, cb, node.coreSetting[i])
              },
              trigger: 'blur'
            }"
            v-if="item.type2 == 'reference'"
          >
            <QuotaCas v-model="item.referenceInfo2" style="width: 130px" :options="options" />
          </el-form-item>
          <!-- 输入类型的输入框 -->
          <el-form-item
            v-else
            class="empty-form-item"
            :class="i > 0 ? 'empty-label' : ''"
            :prop="'coreSetting.' + i + '.value2'"
            :rules="{
              validator: (rule, value, cb) => {
                validateRef2(rule, value, cb, node.coreSetting[i])
              },
              trigger: 'blur'
            }"
          >
            <el-input style="width: 130px" v-model="item.value2" />
          </el-form-item>
        </template>
        <div class="emtpty-box" v-else></div>
        <el-button link v-if="i > 0" @click="removeItem(i)" class="del-btn" type="primary">
          <i class="iconfont icon-jian"></i>
        </el-button>
      </div>
    </template>
  </el-form>
  <Handle
    type="source"
    :position="Position.Right"
    :style="{ backgroundColor: '#4d53e8', filter: 'none' }"
  />
</template>

<script setup>
import { Handle, Position, useNode, useVueFlow } from '@vue-flow/core'
import ConditionLabelSel from './components/ConditionLabelSel'
import ConditionsSelNew from './components/ConditionsSelNew'
import { ref, computed } from 'vue'
import QuotaCas from './components/QuotaCas'
import OutputTypeSel from './components/OutputTypeSel'
import { validateRef2, getReferOps, filterRefByTypes } from '../baseInfo'
import { useWfStore } from '@/stores/workflow1'
import {
  getNodeHeight,
  getToChangeNodes,
  IfElseMargin,
  ifItemH,
  sortConditions,
  removeConditionFn
} from '../../../workflow/common/if-else.js'
import { typesDisabled, switchEmptyOps } from '@/views/workflow/common/base.js'

const switchEmptyTypes = ref(switchEmptyOps)
const wfStore = useWfStore()
const draging = computed(() => wfStore.nodeDragging)
const { toObject, updateNode, removeNodes, getNode } = useVueFlow()
const props = defineProps({
  modelValue: {},
  disabled: {},
  getDetailed: {},
  nodes: {},
  referOps: {}
})

const options = computed(() => {
  console.log('props.referOps', props.referOps)
  // image类型的不能选择
  return typesDisabled(props.referOps, ['Image'])
})
// 当前if节点的父节点的所有nodeIf节点
const sameParentIfSon = computed(() => {
  const { nodes } = toObject()
  // 所有同一个父节点下的，所有nodeif包括自己
  return nodes.filter((it) => it.parentNode == node.value.parentNode && it.nodeType == 'nodeif')
})

// 除了自己的其他的兄弟if节点
const conditionPosition = computed(() => {
  const sortArr = sortConditions(sameParentIfSon.value)
  const index = sortArr.findIndex((it) => it.id === node.value.id)
  return index
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

const parentNode = computed(() => {
  const parentNodeId = node.value.parentNode
  return getNode.value(parentNodeId)
})

const removeItem = (i) => {
  node.value.coreSetting.splice(i, 1)
  changeIFAndElse('remove')
}
const addCondition = () => {
  const obj = {
    condition: '1',
    conditionLabel: 'or',
    id: new Date().getTime(),
    type: 'reference'
  }
  node.value.coreSetting.push(obj)
  changeIFAndElse()
}

// 如果父节点是newPrecondition ，则需要清空这一条的判断条件
const changeRef = (index) => {
  if (parentNode.value.nodeType == 'newPrecondition') {
    node.value.coreSetting[index].operator = ''
  }
}

const removeCondition = () => {
  const { nodes } = toObject()
  removeConditionFn({ nodes, nodeIf: node.value, removeNodes, updateNode, getNode })
}
const changeIFAndElse = (type = 'add') => {
  const { nodes } = toObject()
  const changeH = ifItemH
  const realId = node.value.id.split('_')[1]
  // 1.将else和其他if节点 移动
  const otherBrother = nodes.filter(
    (item) =>
      item.parentNode == node.value.parentNode &&
      (item.id.split('_')[1] > realId || item.id.split('_')[1] < 0)
  )
  otherBrother.forEach((broItem) => {
    updateNode(broItem.id, {
      position: {
        ...broItem.position,
        y: type == 'add' ? broItem.position.y + changeH : broItem.position.y - changeH
      }
    })
  })
  // 2.将父节点增高或者降低
  const parentNodeId = node.value.parentNode
  const parentNode = nodes.filter((item) => item.id == parentNodeId)[0] || {}
  console.log('parentNode.style.width', parentNode.style)
  const { width, height } = parentNode.style
  const realH = height.replace('px', '')
  updateNode(parentNodeId, {
    style: {
      width,
      height: type == 'add' ? `${Number(realH) + changeH}px` : `${Number(realH) - changeH}px`
    }
  })
}
const ifFormRef = ref(null)

const validate = async () => {
  try {
    return await ifFormRef.value.validate()
  } catch (error) {
    return false
  }
}
defineExpose({ validate })
</script>

<style lang="scss" scoped>
.if-box {
  padding: 10px 20px;
  background: #f8f8f8;
  border-radius: 5px;
  width: 580px;
}

.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 4px;
}
.t {
  font-size: 12px;
  font-weight: 600;
  padding: 5px 0;
}
.if-item {
  display: flex;
  .empty-label {
    padding-top: 0;
  }
}
.icon-plus1 {
  font-size: 12px;
}
.label-span {
  display: inline-block;
  width: 60px;
  color: #939395;
  height: 47px;
  padding-top: 29px;
}
.empty-form-item {
  padding-top: 23px;
}
.del-btn {
  height: 23px;
  margin-bottom: 18px;
}
.level {
  margin-left: 10px;
  background: rgba(139, 139, 149, 0.15);
  font-size: 12px;
  padding: 1px 2px;
  color: reb(75, 74, 88);
  border-radius: 3px;
}
.icon-jian {
  font-size: 12px;
}
.emtpty-box {
  width: 190px;
}
</style>
