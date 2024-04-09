<template>
  <div class="if-box">
    <el-form
      :model="node"
      label-position="top"
      size="small"
      class="condition-form"
      ref="ifFormRef"
      :disabled="disabled"
    >
      <div class="condition-item" v-if="node">
        <div class="top">
          <span class="t">如果</span>
          <el-button link @click="addCondition">
            <i class="iconfont icon-plus1"></i>
          </el-button>
        </div>
        <div v-for="(item, i) in node.coreSetting" :key="item" class="if-item">
          <div v-if="i == 0" class="empty-form-item">
            <span class="label-span">条件</span>
          </div>
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
            <QuotaCas v-model="item.referenceInfo" :nodeId="node.parentNode" style="width: 130px" />
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
            <ConditionsSel v-model="item.operator"></ConditionsSel>
          </el-form-item>
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
            <QuotaCas
              v-model="item.referenceInfo2"
              :nodeId="node.parentNode"
              style="width: 130px"
            />
          </el-form-item>
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
          <el-form-item class="empty-form-item" :class="i > 0 ? 'empty-label' : ''">
            <el-button link v-if="i > 0" @click="removeItem(i)">
              <i class="iconfont icon-jian"></i>
            </el-button>
          </el-form-item>
        </div>
      </div>
    </el-form>
    <Handle
      type="source"
      :position="Position.Right"
      :style="{ backgroundColor: '#4d53e8', filter: 'none' }"
    />
  </div>
</template>

<script setup>
import { Handle, Position, useNode } from '@vue-flow/core'
import ConditionLabelSel from './components/ConditionLabelSel'
import ConditionsSel from './components/ConditionsSel'
import { ref, computed } from 'vue'
import QuotaCas from './components/QuotaCas'
import OutputTypeSel from './components/OutputTypeSel'
import { useVueFlow } from '@vue-flow/core'
import { validateRef2 } from '../baseInfo'

const { toObject, updateNode } = useVueFlow()
const props = defineProps({
  modelValue: {},
  disabled: {}
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
const changeIFAndElse = (type = 'add') => {
  const { nodes } = toObject()
  const { elseId } = node.value
  const changeH = 42
  const elseNode = nodes.filter((item) => item.id == elseId)[0] || {}
  updateNode(elseId, {
    position: {
      x: elseNode.position.x,
      y: type == 'add' ? elseNode.position.y + changeH : elseNode.position.y - changeH
    }
  })
  const parentNodeId = elseNode.parentNode
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
  padding: 5px 10px;
  background: #f8f8f8;
  border-radius: 5px;
  width: 580px;
}
.condition-item {
  padding: 0px 10px;
  border-radius: 5px;
}
.condition-item + .condition-item {
  margin-top: 10px;
}
.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.t {
  font-size: 12px;
  font-weight: 600;
  padding: 5px 0;
}
.condition-form .if-item {
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
  padding: 0 5px;
  color: #939395;
}
.empty-form-item {
  padding-top: 23px;
}
</style>
