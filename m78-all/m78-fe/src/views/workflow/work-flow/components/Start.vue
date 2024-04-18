<template>
  <div>
    <div>
      <TitleTooltip
        title="输入"
        content="定义启动工作流需要的输入参数，这些内容将在Bot对话过程</br>中被LLM阅读，使LLM可以在合适的时候启动工作流并填入</br>正确的信息"
        @add="addFn"
      />
    </div>
    <el-form
      :model="node"
      label-position="top"
      size="small"
      ref="startFormRef"
      :disabled="disabled"
    >
      <el-collapse v-model="item.activeNames" v-for="(item, i) in node.inputs" :key="item.id">
        <el-collapse-item name="1">
          <template #title>
            <div class="t-box">
              <div class="left-col-t">
                <el-icon>
                  <ArrowDown v-if="item.activeNames.includes('1')" />
                  <ArrowRight v-else />
                </el-icon>
                <span class="col-item-name">{{ item.name }}</span>
              </div>
              <el-button
                link
                @click.stop="
                  () => {
                    delFn(i)
                  }
                "
                size="small"
              >
                <i class="iconfont icon-jian"></i>
              </el-button>
            </div>
          </template>
          <el-form-item
            label="参数名"
            :prop="'inputs.' + i + '.name'"
            :rules="{
              validator: (rule, value, cb) => {
                validPName(rule, value, cb, node.inputs)
              },
              trigger: 'blur'
            }"
          >
            <el-input v-model="item.name" placeholder="请输入参数名" :maxlength="20" />
          </el-form-item>
          <el-form-item label="变量类型">
            <el-select v-model="item.valueType" placeholder="请选择参数类型">
              <el-option label="String" value="String" />
              <el-option label="Integer" value="Integer" />
            </el-select>
          </el-form-item>
          <el-form-item label="变量描述">
            <el-input
              v-model="item.desc"
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 4 }"
              placeholder="请输入参数描述"
            />
          </el-form-item>
          <el-form-item label="是否必要">
            <el-switch v-model="item.required" />
          </el-form-item>
        </el-collapse-item>
      </el-collapse>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, defineExpose, nextTick } from 'vue'
import TitleTooltip from './TitleTooltip'
import { validPName } from '../baseInfo'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {},
  disabled: {}
})
const startFormRef = ref(null)
const emits = defineEmits(['update:modelValue'])
const node = computed({
  get() {
    const modelValue = props.modelValue
    modelValue.inputs?.forEach((it) => (it.activeNames = '1'))
    return modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const form = ref({
  arr: []
})
const delFn = (i) => {
  node.value.inputs.splice(i, 1)
}
const addFn = () => {
  node.value.inputs.push({ name: '', type: 'String', desc: '', required: true, activeNames: '1' })
}
onMounted(() => {
  nextTick(() => {})
})

const validate = async () => {
  try {
    return await startFormRef.value.validate()
  } catch (error) {
    return false
  }
}
defineExpose({ validate })
</script>

<style lang="scss" scoped>
.llm {
  width: 340px;
}

.icon-jian {
  font-size: 15px;
  color: rgba(28, 29, 35, 0.6);
}
.col-item-name {
  color: #1d1c23;
  font-size: 14px;
  font-style: normal;
  font-weight: 600;
  line-height: 22px;
  margin-left: 4px;
}
.left-col-t {
  display: flex;
  align-items: center;
}
</style>
