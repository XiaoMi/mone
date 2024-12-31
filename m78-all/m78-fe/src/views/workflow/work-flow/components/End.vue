<template>
  <div>
    <p class="anwser-model">
      <span class="title">选择回答模式</span>
      <AnswerModelSel v-model="node.coreSetting.type" size="small" @change="changeAnswerType" />
    </p>
    <el-form
      :model="node"
      label-position="top"
      size="small"
      class="end-form"
      ref="endFormRef"
      :disabled="disabled"
    >
      <el-collapse v-model="activeNames">
        <el-collapse-item name="1">
          <template #title>
            <div class="t-box">
              <div class="left-t">
                <el-icon>
                  <ArrowDown v-if="activeNames.includes('1')" />
                  <ArrowRight v-else />
                </el-icon>
                <TitleTooltip
                  title="输出变量"
                  content="这些变量将在Bot调用工作流，完成后被输出。在”返回变量“模</br>式中，这些变量会被Bot总结后回复用户。在”直接回答“模式</br>中，Bot只会回复你设定的”回答内容“。但在任何模式中，这</br>些变量都可以在配置卡片时使用。"
                  class="title-tooltip"
                  :showAdd="false"
                />
              </div>
              <el-button link size="small" @click.stop="addFn">
                <i class="iconfont icon-plus1"></i>
              </el-button>
            </div>
          </template>

          <div v-for="(item, i) in node.outputs" :key="item" class="arr-item">
            <el-form-item
              :label="i == 0 ? '参数名' : ''"
              :prop="'outputs.' + i + '.name'"
              :rules="{
                validator: (rule, value, cb) => {
                  validPName(rule, value, cb, node.outputs)
                },
                trigger: 'blur'
              }"
            >
              <el-input v-model="item.name" style="width: 190px" placeholder="请输入参数名" />
            </el-form-item>
            <el-form-item :label="i == 0 ? '参数值' : ''" class="m-r-0">
              <OutputTypeSel v-model="item.type" />
            </el-form-item>
            <el-form-item
              v-if="item.type == 'value'"
              label=""
              :class="i == 0 ? 'empty-item' : ''"
              :prop="'outputs.' + i + '.value'"
              :rules="{
                validator: (rule, value, cb) => {
                  validateRef(rule, value, cb, node.outputs[i])
                },
                trigger: 'blur'
              }"
            >
              <el-input
                v-if="item.type == 'value'"
                v-model="item.value"
                style="width: 160px"
                placeholder="请输入参数值"
                maxlength="20"
              />
            </el-form-item>
            <el-form-item
              v-else
              label=""
              :class="i == 0 ? 'empty-item' : ''"
              :prop="'outputs.' + i + '.referenceInfo'"
              :rules="{
                validator: (rule, value, cb) => {
                  validateRef(rule, value, cb, node.outputs[i])
                },
                trigger: 'blur'
              }"
            >
              <QuotaCas :nodeId="node.id" style="width: 160px" v-model="item.referenceInfo" />
            </el-form-item>
            <el-form-item label="" :class="i == 0 ? 'empty-item' : ''">
              <el-button
                link
                size="small"
                @click.stop="
                  () => {
                    delFn(i)
                  }
                "
                class="del-btn"
              >
                <i class="iconfont icon-jian" style="font-size: 14px"></i>
                <!-- <el-icon class="del-icon"><CircleClose /></el-icon> -->
              </el-button>
            </el-form-item>
          </div>
        </el-collapse-item>
      </el-collapse>
      <el-collapse v-model="activeNames1" v-if="node.coreSetting.type == 'content'">
        <el-collapse-item name="1">
          <template #title>
            <div class="t-box">
              <div class="left-t">
                <el-icon>
                  <ArrowDown v-if="activeNames.includes('1')" />
                  <ArrowRight v-else />
                </el-icon>
                <TitleTooltip
                  title="回答内容"
                  content="编辑Bot的回复内容，工作流程完成后，Bot中的LLM将</br>不再组织语言，而是直接用此处编辑的原始内容回复对话</br>。您可以使用${变量名}格式引用输入参数中的变量。"
                  class="title-tooltip"
                  :showAdd="false"
                />
              </div>
            </div>
          </template>
          <div class="pad-b-20">
            <el-input
              v-model="node.coreSetting.answerContent"
              type="textarea"
              placeholder="可以使用${变量名}格式引用输入参数中的变量"
            />
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-form>
  </div>
</template>

<script setup>
import AnswerModelSel from './components/AnswerModelSel'
import OutputTypeSel from './components/OutputTypeSel'
import { ref, computed, defineExpose, onMounted } from 'vue'
import TitleTooltip from './TitleTooltip'
import QuotaCas from './components/QuotaCas'
import { validateRef, validPName } from '../baseInfo'

const props = defineProps({
  modelValue: {},
  nodes: {},
  lines: {},
  disabled: {}
})
const emits = defineEmits(['update:modelValue'])
const endFormRef = ref(null)
const node = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const activeNames = ref('1')
const activeNames1 = ref('1')

const addFn = () => {
  node.value.outputs.push({
    type: 'reference'
  })
}
const delFn = (i) => {
  node.value.outputs.splice(i, 1)
}
const validate = async () => {
  try {
    return await endFormRef.value.validate()
  } catch (error) {
    return false
  }
}
const changeAnswerType = () => {
  node.value.coreSetting.answerContent = ''
  node.value.resOutputs = {}
}

defineExpose({ validate })
</script>

<style lang="scss" scoped>
.anwser-model {
  margin-bottom: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-t {
  flex: 1;
  display: flex;
  align-items: center;
}
.title-tooltip {
  margin-left: 4px;
}
.title {
  font-weight: 600;
  font-size: 13px;
}
.end-form {
  :deep(.oz-form-item) {
    margin-right: 3px;
  }
}
.empty-label {
  padding-top: 24px;
}
.arr-item {
  display: flex;
  align-items: center;
}
.del-icon {
  font-size: 14px;
}
.del-btn {
  padding: 0;
}
.pad-b-20 {
  padding-bottom: 20px;
}
</style>
