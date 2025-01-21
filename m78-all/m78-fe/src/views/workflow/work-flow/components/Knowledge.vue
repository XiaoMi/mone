<template>
  <el-form
    :model="node"
    label-position="top"
    inline
    size="small"
    class="knowledge-form"
    ref="knowFormRef"
  >
    <el-collapse v-model="activeNames" class="collapse-item">
      <el-collapse-item name="1">
        <template #title>
          <div class="top-t">
            <CollapseTitle
              :activeNames="activeNames"
              title="输入"
              content="输入从知识中匹配的信息"
              tipClass="title-tooltip"
              :showAdd="false"
            />
          </div>
        </template>
        <div class="output-box" v-for="(item, i) in node.inputs" :key="item">
          <el-form-item label="参数名">
            <span>Query</span>
            <span>*</span>
          </el-form-item>
          <div class="right-form">
            <el-form-item label="参数值">
              <OutputTypeSel v-model="item.type" />
            </el-form-item>
            <el-form-item
              label=""
              class="empty-form-item"
              :prop="'outputs.' + i + '.value'"
              :rules="{
                validator: (rule, value, cb) => {
                  validateRef(rule, value, cb, node.inputs[i])
                },
                trigger: 'blur'
              }"
              v-if="item.type == 'value'"
            >
              <el-input v-model="item.value" style="width: 100px" placeholder="请输入参数值" />
            </el-form-item>
            <el-form-item
              label=""
              class="empty-form-item"
              :prop="'outputs.' + i + '.referenceInfo'"
              :rules="{
                validator: (rule, value, cb) => {
                  validateRef(rule, value, cb, node.inputs[i])
                },
                trigger: 'blur'
              }"
              v-else
            >
              <QuotaCas style="width: 100px" v-model="item.referenceInfo" :options="referOps" />
            </el-form-item>
          </div>
        </div>
      </el-collapse-item>
    </el-collapse>
    <el-collapse v-model="activeNames1" class="collapse-item">
      <el-collapse-item name="1">
        <template #title>
          <div class="top-box">
            <div class="top-t">
              <CollapseTitle
                :activeNames="activeNames1"
                title="知识库"
                content="选择需要匹配的知识范围，仅从选定的知识里召回信息"
                tipClass="title-tooltip"
                :showAdd="false"
              />
            </div>
            <!-- <el-button type="primary" link @click.stop="addParams">
              <i class="iconfont icon-plus1"></i>
            </el-button> -->
          </div>
        </template>
        <div class="knowledge-box">
          <el-form-item
            prop="coreSetting.knowledgeBaseId"
            :rules="{
              validator: (rule, value, cb) => {
                validateKnowledgeRef(rule, value, cb)
              },
              trigger: 'blur'
            }"
          >
            <div class="sel-box">
              <KnowldgeTypesSel v-model="node.coreSetting.knowledgeRefType" @change="changeType" />
              <template v-if="node.coreSetting.knowledgeRefType == 'reference'">
                <QuotaCas
                  style="width: 300px"
                  v-for="(item, index) in node.knowledgeInputs"
                  :key="index"
                  v-model="item.referenceInfo"
                  :options="knowledgeRefOps"
                />
              </template>
              <KnowledgeSel
                style="width: 300px"
                v-else
                v-model="node.coreSetting.knowledgeBaseId"
                class="sel"
                :workspaceId="workspaceId"
              />
            </div>
          </el-form-item>
          <!-- <el-form-item label="搜索策略" class="label-left">
            <SearchStrategySel v-model="node.searchStrategy" />
          </el-form-item> -->
          <el-form-item label="最大召回数量" class="label-left slider-box">
            <div class="nodrag slider-box-item">
              <el-slider v-model="node.coreSetting.maxRecall" :marks="cbMarks" :min="1" :max="10" />
            </div>
          </el-form-item>
          <el-form-item label="最小匹配度%" class="label-left slider-box">
            <div class="nodrag slider-box-item">
              <el-slider
                v-model="node.coreSetting.minMatch"
                :marks="matchMarks"
                :min="1"
                :max="99"
              />
            </div>
          </el-form-item>
        </div>
      </el-collapse-item>
    </el-collapse>
    <el-collapse v-model="activeNames2" class="collapse-item">
      <el-collapse-item name="1">
        <template #title>
          <div class="top-t">
            <CollapseTitle
              :activeNames="activeNames2"
              title="输出"
              content="输出列表是所有选定知识中召回的与输入参数最匹配的信息"
              tipClass="title-tooltip"
              :showAdd="false"
            />
          </div>
        </template>
        <el-tree
          :data="node.outputs"
          node-key="id"
          default-expand-all
          :expand-on-click-node="false"
          class="out-tree"
        >
          <template #default="{ node, data }">
            <span class="custom-tree-node">
              <span>{{ node.label }}</span>
              <span class="desc-label">{{ data.desc }}</span>
            </span>
          </template>
        </el-tree>
      </el-collapse-item>
    </el-collapse>
  </el-form>
</template>

<script setup>
import { ref, computed, defineExpose } from 'vue'
import TitleTooltip from './TitleTooltip.vue'
import OutputTypeSel from './components/OutputTypeSel.vue'
import KnowledgeSel from './components/KnowledgeSel'
// import SearchStrategySel from './components/SearchStrategySel'
import QuotaCas from './components/QuotaCas.vue'
import { validateRef, getReferOps, filterRefByTypes } from '../baseInfo'
import { useVueFlow } from '@vue-flow/core'
import { useWfStore } from '@/stores/workflow1'
import CollapseTitle from './components/CollapseTitle.vue'
import KnowldgeTypesSel from './components/KnowldgeTypesSel.vue'

const wfStore = useWfStore()
const draging = computed(() => wfStore.nodeDragging)

const { toObject } = useVueFlow()

const props = defineProps({
  modelValue: {},
  lines: {},
  nodes: {},
  getDetailed: {},
  referOps: {},
  workspaceId: {}
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

const validateKnowledgeRef = (rule, value, cb) => {
  if (node.value.coreSetting.knowledgeRefType == 'reference') {
    if (node.value.knowledgeInputs[0].referenceInfo.length == 0) {
      return cb(new Error('请选择知识库'))
    }
  } else {
    if (!node.value.coreSetting.knowledgeBaseId) {
      return cb(new Error('请选择知识库'))
    }
  }
  return cb()
}

const activeNames = ref(['1'])
const activeNames2 = ref(['1'])
const activeNames1 = ref(['1'])
const cbMarks = ref({ 1: '1', 3: '默认值', 10: '10' })
const matchMarks = ref({ 1: '1%', 49: '默认值', 99: '99%' })

const changeType = () => {
  node.value.knowledgeInputs[0].referenceInfo = []
  node.value.coreSetting.knowledgeBaseId = ''
}

const knowledgeRefOps = computed(() => {
  return filterRefByTypes(props.referOps, ['String', 'Integer'])
})

const knowFormRef = ref(null)
const validate = async () => {
  try {
    return await knowFormRef.value.validate()
  } catch (error) {
    return false
  }
}
defineExpose({ validate })
</script>

<style lang="scss" scoped>
.knowledge {
  width: 344px;
}
.top-t {
  display: flex;
  align-items: center;
  .title-tooltip {
    margin-left: 4px;
  }
}
.collapse-item {
  width: 100%;
}

.right-form {
  display: flex;
}
.knowledge-form {
  .label-left {
    display: flex;
    margin-top: 20px;
  }
  :deep(.oz-form-item) {
    .oz-form-item__label {
      flex: 1;
    }
    .oz-form-item__content {
      flex: none;
    }
  }
  :deep(.oz-form-item).slider-box {
    margin: 0;

    .oz-form-item__content {
      flex: 1;
    }
  }
}
.output-box {
  display: flex;
  justify-content: space-between;
}
.top-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
}

.knowledge-box {
  width: 100%;
  .sel-box {
    width: 100%;
    display: flex;
    .sel {
      flex: 1;
    }
  }
}
.icon-plus1 {
  font-size: 13px;
}
.icon-jian {
  color: rgba(28, 29, 35, 0.35);
}
.slider-box {
  width: 100%;
  padding: 0 10px;
  :deep(.oz-slider__bar) {
    background-color: #4d53e8;
    height: 4px;
  }
  :deep(.oz-slider__stop) {
    width: 4px;
    height: 4px;
  }
  :deep(.oz-slider__button) {
    border: none;
    box-shadow:
      0 4px 6px 0 rgba(0, 0, 0, 0.1),
      0 0 1px 0 rgba(0, 0, 0, 0.3);
  }
  :deep(.oz-slider__marks-text) {
    color: #4d53e8;
    font-size: 12px;
    margin-top: 10px;
  }
  :deep(.oz-slider__runway) {
    height: 4px;
  }
  :deep(.oz-slider__button-wrapper) {
    top: -17px;
  }
}
.desc-label {
  background: hsla(210, 9%, 91%, 0.76);
  font-size: 12px;
  color: rgba(28, 31, 35, 0.6);
  padding: 2px 8px;
  border-radius: 3px;
  margin-left: 4px;
}
.out-tree {
  margin-bottom: 10px;
}
.slider-box-item {
  width: 100%;
  height: 40px;
}
</style>
