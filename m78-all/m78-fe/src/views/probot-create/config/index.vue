<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-width="130px"
    class="probot-config-form"
    :size="formSize"
    status-icon
    :label-position="labelPosition"
  >
    <BaseGroup title="AI模型">
      <el-form-item label="模型选择：" prop="aiModel" label-position="left">
        <LLMModelSel v-model="form.aiModel" :disabled="props.disabled" placeholder="请选择AI模型" />
      </el-form-item>
      <el-form-item label="温度：" prop="temperature" v-if="form.config_temperature_range">
        <div class="temperature">
          <el-slider
            v-model="form.temperature"
            show-input
            :marks="marksTemperature"
            :max="Number(form.config_temperature_range[1])"
            :disabled="props.disabled"
            :step="0.1"
          />
        </div>
      </el-form-item>
      <el-form-item label="对话携带轮数：" prop="dialogueTurns">
        <div class="dialogue">
          <el-slider
            v-model="form.dialogueTurns"
            show-input
            :marks="marks"
            :max="30"
            :disabled="props.disabled"
          />
        </div>
      </el-form-item>
      <el-form-item prop="systemSetting" label="系统设置：">
        <el-input
          v-model="form.systemSetting"
          type="textarea"
          :rows="5"
          placeholder="system prompt（系统提示）指的是模型在生成文本或响应之前所接收的初始输入或指令"
          :disabled="props.disabled"
        />
      </el-form-item>
    </BaseGroup>
    <BaseGroup
      title="基础人设"
      :btn="{
        name: '优化',
        icon: 'icon-AIshengcheng',
        click: optimize,
        disabled: !form.setting || props.disabled
      }"
    >
      <el-form-item prop="setting" label-width="0px">
        <EditTextarea
          v-model="form.setting"
          type="textarea"
          :rows="5"
          :disabled="props.disabled"
          placeholder="请填写Probot的基础人设。可以从【性格特点】、【功能和技能】、【用户群体】、【要求和限制】等方面，用自然语言进行描述。"
        />
      </el-form-item>
    </BaseGroup>
    <BaseGroup title="开场白" tooltip="">
      <el-form-item prop="openingRemarks" label="开场白文案：" label-width="122px">
        <template #label>
          <div class="openingRemarks-label">
            开场白文案：
            <el-tooltip
              effect="dark"
              content="请填写Probot的开场白文案。可以进行【友好而亲切的问候】、【介绍身份和目的】、【提供指导或提示】、【表达愿意帮助】等等。"
              placement="top"
            >
              <el-link :underline="false" type="info" color="#909399" class="tooltip"
                ><el-icon> <QuestionFilled /> </el-icon
              ></el-link>
            </el-tooltip>
          </div>
        </template>

        <el-input
          v-model="form.openingRemarks"
          type="textarea"
          :rows="5"
          :disabled="props.disabled"
          placeholder="你好，有什么可以帮助你的吗"
        />
      </el-form-item>
      <el-form-item prop="openingQues" label="开场白问题：" label-width="120px">
        <ConfigDynamic
          :data="form.openingQues"
          ref="configDynamicRef"
          :disabled="props.disabled"
        ></ConfigDynamic>
      </el-form-item>
    </BaseGroup>
    <BaseGroup title="用户预置问题" tooltip="根据对话内容给用户自动推荐问题">
      <el-form-item prop="customizePromptSwitch" label="是否启用：" label-width="120px">
        <el-switch
          v-model="form.customizePromptSwitch"
          :active-value="1"
          :inactive-value="0"
          :disabled="props.disabled"
        />
      </el-form-item>
      <el-form-item prop="customizePrompt" label="自定义prompt：" label-width="120px">
        <EditTextarea
          v-model="form.customizePrompt"
          type="textarea"
          :rows="5"
          :disabled="props.disabled"
        />
      </el-form-item>
    </BaseGroup>
    <BaseGroup title="语音">
      <el-form-item prop="timbreSwitch" label="是否启用：" label-width="120px">
        <div style="display: flex; align-items: center">
          <el-switch
            v-model="form.timbreSwitch"
            :active-value="1"
            :inactive-value="0"
            :disabled="props.disabled"
            @change="timbreSwitchChange"
          />
          <el-select
            v-model="form.timbre"
            placeholder="请选择语音音色"
            :disabled="props.disabled"
            v-if="form.timbreSwitch"
            style="margin-left: 20px; width: 200px"
          >
            <el-option
              v-for="item in timbreOption"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <BaseSounds
            content="你好，我是你的专属聊天机器人，有什么可以帮助到你的吗？"
            :language="form.timbre"
            ref="soundsRef"
            style="margin-left: 20px"
            v-if="form.timbreSwitch"
            size="small"
          ></BaseSounds>
        </div>
      </el-form-item>
    </BaseGroup>
    <BaseGroup title="流式输出" tooltip="流式输出开启后，将直接与大模型对话，不再触发工作流流程。">
      <el-form-item prop="streaming" label="是否启用：" label-width="120px">
        <el-switch v-model="form.streaming" />
      </el-form-item>
    </BaseGroup>
    <BaseGroup title="元数据">
      <!-- 标签 -->
      <el-form-item label-width="0px">
        <div class="meta-wrap" v-if="form.meta.length">
          <el-form-item
            v-for="(domain, index) in form.meta"
            :key="index"
            :prop="'meta.' + index + '.key'"
            class="meta-form"
          >
            <div class="meta-container">
              <div class="meta-item">
                <el-input
                  v-model="domain.key"
                  placeholder="请输入key"
                  @change="keyChange"
                  :disabled="props.disabled"
                />
                <div>：</div>
                <el-input
                  v-model="domain.value"
                  placeholder="请输入value"
                  @change="valueChange"
                  :disabled="props.disabled"
                />
                <div class="meta-icon-container" v-if="!props.disabled">
                  <div class="meta-icon" @click="addMeta(index)">
                    <el-icon><Plus /></el-icon>
                  </div>
                  <div class="meta-icon" @click="deleteMeta(index)">
                    <el-icon><Minus /></el-icon>
                  </div>
                </div>
              </div>
              <div class="meta-tip">{{ domain.tip }}</div>
            </div>
          </el-form-item>
        </div>
        <el-button type="primary" @click="addMeta(0)" v-else :disabled="props.disabled"
          ><el-icon><Plus /></el-icon>添加元数据</el-button
        >
      </el-form-item>
    </BaseGroup>
  </el-form>
  <!-- ai优化弹窗 -->
  <ConfigOptimizeDialog
    v-model="optimizeDialogVisible"
    :data="form.setting"
    @use="useOptimize"
  ></ConfigOptimizeDialog>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { CSSProperties } from 'vue'
import BaseGroup from '../components/BaseGroup.vue'
import ConfigDynamic from './ConfigDynamic.vue'
import ConfigOptimizeDialog from './ConfigOptimizeDialog.vue'
import { submitForm } from '@/common/formMethod'
import BaseSounds from '@/components/BaseSounds.vue'
import LLMModelSel from '@/components/LLMModelSel.vue'
import EditTextarea from './EditTextarea.vue'
import { useProbotStore } from '@/stores/probot'

const probotStore = useProbotStore()

const props = defineProps({
  formData: {
    type: Object,
    default: () => ({})
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

interface RuleForm {
  aiModel: string
  config_temperature_range: string
  temperature: number
  dialogueTurns: number
  setting: string
  openingRemarks: string
  openingQues: Array<string>
  customizePromptSwitch: number
  streaming: boolean
  customizePrompt: string
  timbreSwitch: number
  timbre: string
  meta: Array<{
    tip: string
    key: string
    value?: string
  }>
  systemSetting: string | undefined
}
interface Mark {
  style: CSSProperties
  label: string
}
type Marks = Record<number, Mark | string>
const marks = reactive<Marks>({
  0: {
    style: {
      color: '#1989FA'
    },
    label: '0'
  },
  30: {
    style: {
      color: '#1989FA'
    },
    label: '30'
  }
})
const formSize = ref('default')
const labelPosition = ref('left')
const formRef = ref<FormInstance>()
const form = ref<RuleForm>({
  aiModel: '',
  temperature: 0,
  config_temperature_range: '',
  dialogueTurns: 5,
  setting: '',
  openingRemarks: '你好，有什么可以帮助你的吗',
  openingQues: [''],
  customizePromptSwitch: 0,
  customizePrompt: '',
  timbreSwitch: 0,
  timbre: '',
  meta: [],
  streaming: false,
  systemSetting: undefined
})
const rules = reactive<FormRules<RuleForm>>({
  aiModel: [{ required: true, message: '请选择模型', trigger: 'change' }],
  setting: [{ required: true, message: '请输入基础人设', trigger: 'blur' }],
  openingRemarks: [{ required: true, message: '请输入开场白文案', trigger: 'blur' }]
})
const optimizeDialogVisible = ref(false) // ai 优化
const configDynamicRef = ref()
const timbreOption = ref<{ value: string; label: string }[]>([
  {
    value: 'zh-CN-YunxiNeural',
    label: '男-普通话'
  },
  {
    value: 'zh-CN-XiaoxiaoNeural',
    label: '女-普通话'
  },
  {
    value: 'zh-CN-liaoning-YunbiaoNeural',
    label: '男-东北话'
  },
  {
    value: 'zh-CN-liaoning-XiaobeiNeural',
    label: '女-东北话'
  },
  {
    value: 'zh-CN-henan-YundengNeural',
    label: '男-河南话'
  },
  {
    value: 'zh-CN-shaanxi-XiaoniNeural',
    label: '女-陕西话'
  },
  {
    value: 'zh-CN-sichuan-YunxiNeural',
    label: '男-四川话'
  },
  {
    value: 'zh-TW-HsiaoChenNeural',
    label: '女-台湾话'
  },
  {
    value: 'zh-TW-YunJheNeural',
    label: '男-台湾话'
  },
  {
    value: 'zh-CN-shandong-YunxiangNeural',
    label: '男-山东话'
  }
])
const marksTemperature = ref({})

const LLMModelSelChange = (value: any) => {
  const val = probotStore.LLMModelSelList.find((item) => value === item.cname)
  form.value.config_temperature_range = ''
  if (val?.info) {
    try {
      const info = JSON.parse(val?.info)
      if (info?.config_temperature_range) {
        form.value.config_temperature_range = JSON.parse(info?.config_temperature_range)
        marksTemperature.value[form.value.config_temperature_range[0]] = {
          style: {
            color: '#1989FA'
          },
          label: form.value.config_temperature_range[0]
        }
        marksTemperature.value[form.value.config_temperature_range[1]] = {
          style: {
            color: '#1989FA'
          },
          label: form.value.config_temperature_range[1]
        }
      }
    } catch (error) {
      console.log('error', error)
    }
  }
}

const timbreSwitchChange = () => {
  if (!form.value.timbre) {
    form.value.timbre = 'zh-CN-YunxiNeural'
  }
}

const optimize = () => {
  optimizeDialogVisible.value = true
}
const useOptimize = (data: string) => {
  form.value.setting = data
}

//添加
const addMeta = (index: number) => {
  form.value.meta.splice(index + 1, 0, {
    tip: '',
    key: '',
    value: ''
  })
}
//删除
const deleteMeta = (index: number) => {
  form.value.meta.splice(index, 1)
}
const keyChange = () => {
  checkKey()
}
const valueChange = () => {
  checkKey()
}
const checkKey = () => {
  let res = true
  let obj = {}
  form.value.meta.forEach((v) => {
    v.tip = ''
    if (!v.key) {
      v.tip = 'key不能为空'
      res = false
    } else if (obj[v.key] !== undefined) {
      v.tip = 'key值不能重复'
      res = false
    }
    obj[v.key] = v.value
  })
  return res
}

watch(
  () => props.formData,
  ({ botSetting, botInfo }) => {
    let meta = []
    const metaObj = botInfo?.meta
    for (let key in metaObj) {
      meta.push({
        tip: '',
        key: key,
        value: metaObj[key]
      })
    }
    console.log(11)
    form.value = {
      aiModel: botSetting?.aiModel,
      temperature: Number(botSetting?.temperature) || 0,
      dialogueTurns: botSetting?.dialogueTurns,
      setting: botSetting?.setting,
      openingRemarks: botSetting?.openingRemarks || '你好，有什么可以帮助你的吗',
      openingQues: botSetting?.openingQues || [''],
      customizePromptSwitch: botSetting?.customizePromptSwitch,
      customizePrompt: botSetting?.customizePrompt,
      timbreSwitch: botSetting?.timbreSwitch,
      timbre: botSetting?.timbre,
      meta: meta,
      streaming: botSetting?.streaming || false,
      systemSetting: botSetting?.systemSetting
    }
  }
)
watch(
  () => [probotStore.LLMModelSelList, form.value?.aiModel],
  ([list, val], [preList, preVal]) => {
    if (val) {
      LLMModelSelChange(val)
    } else {
      console.log('val', val, list)
      const data = list.filter((item) => item.cname === 'gpt4_o_mini')
      form.value.aiModel = data.length ? data[0].cname : list[0].cname
    }
  }
)

defineExpose({
  submit: () => {
    form.value.openingQues = configDynamicRef.value.getArrValue()
    return submitForm(formRef.value, form.value)
  }
})
</script>

<style lang="scss">
.probot-config-form {
  width: 100%;

  .temperature,
  .dialogue {
    width: 390px;
    padding-left: 20px;
  }

  .oz-slider__runway.show-input {
    margin-right: 60px;
  }

  .oz-slider__marks {
    width: 100%;
    top: -28px;
    position: absolute;

    .oz-slider__marks-text:nth-child(1) {
      left: -30px !important;
      color: var(--oz-menu-active-color) !important;
    }

    .oz-slider__marks-text:nth-child(2) {
      left: calc(100% + 10px) !important;
      color: var(--oz-menu-active-color) !important;
    }
  }
}
</style>
<style lang="scss" scoped>
.openingRemarks-label {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.meta-wrap {
  padding: 10px 0;
  width: 100%;
  background: #f5f7fa;
}
.meta-form {
  padding: 10px;
}
.meta-container {
  width: 100%;
}
.meta-item {
  display: flex;
  width: 100%;
  .meta-icon-container {
    display: flex;
  }
  .meta-icon {
    margin-left: 10px;
    background: #eee;
    width: 30px;
    height: 30px;
    border-radius: 50%;
    text-align: center;
    cursor: pointer;
    &:hover {
      box-shadow: inset 0 0 5px #ddd;
    }
  }
}
.meta-tip {
  width: 100%;
  font-size: 12px;
  color: #f56c6c;
  line-height: 14px;
}
</style>
