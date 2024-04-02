<!--
 * @Description:
 * @Date: 2024-03-04 17:35:16
 * @LastEditTime: 2024-03-22 15:56:08
-->
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
        <el-input
          v-model="form.setting"
          type="textarea"
          :autosize="{ minRows: 5, maxRows: 10 }"
          :disabled="props.disabled"
          placeholder="请填写Probot的基础人设。可以从【性格特点】、【功能和技能】、【用户群体】、【要求和限制】等方面，用自然语言进行描述。"
        />
      </el-form-item>
    </BaseGroup>
    <BaseGroup title="开场白">
      <el-form-item prop="openingRemarks" label="开场白文案：" label-width="120px">
        <el-input
          v-model="form.openingRemarks"
          type="textarea"
          :autosize="{ minRows: 5, maxRows: 5 }"
          :disabled="props.disabled"
          placeholder="请填写Probot的开场白文案。可以进行【友好而亲切的问候】、【介绍身份和目的】、【提供指导或提示】、【表达愿意帮助】等等。"
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
        <el-input
          v-model="form.customizePrompt"
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 4 }"
          :disabled="props.disabled"
        />
      </el-form-item>
    </BaseGroup>
    <BaseGroup title="语音">
      <el-form-item prop="timbreSwitch" label="是否启用：" label-width="120px">
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
          style="margin-left: 20px"
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
        ></BaseSounds>
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
  dialogueTurns: number
  setting: string
  openingRemarks: string
  openingQues: Array<string>
  customizePromptSwitch: number
  customizePrompt: string
  timbreSwitch: number
  timbre: string
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
  dialogueTurns: 5,
  setting: '',
  openingRemarks: '',
  openingQues: [''],
  customizePromptSwitch: 0,
  customizePrompt: '',
  timbreSwitch: 0,
  timbre: ''
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

watch(
  () => props.formData,
  ({ botSetting }) => {
    form.value = {
      aiModel: botSetting?.aiModel,
      dialogueTurns: botSetting?.dialogueTurns,
      setting: botSetting?.setting,
      openingRemarks: botSetting?.openingRemarks,
      openingQues: botSetting?.openingQues || [''],
      customizePromptSwitch: botSetting?.customizePromptSwitch,
      customizePrompt: botSetting?.customizePrompt,
      timbreSwitch: botSetting?.timbreSwitch,
      timbre: botSetting?.timbre
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
    }

    .oz-slider__marks-text:nth-child(2) {
      left: calc(100% + 10px) !important;
    }
  }
}
</style>
