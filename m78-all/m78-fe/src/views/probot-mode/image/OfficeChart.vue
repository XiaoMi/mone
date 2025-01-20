<!--
 * @Description: 
 * @Date: 2024-07-18 14:47:06
 * @LastEditTime: 2024-08-27 10:35:26
-->
<template>
  <ModeContainer @onSubmit="onSubmit" :result="curItem">
    <template #left>
      <el-form :model="form" :rules="rules" ref="formRef">
        <FormItemMode v-model="form.model" apiType="m78-image-workchart-model" />
        <FormItem title="图表类型" prop="chartType">
          <el-select v-model="form.chartType" placeholder="请选择图表类型" style="width: 100%">
            <el-option
              v-for="item in chartTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </FormItem>
        <FormItem prop="input">
          <el-input
            v-model="form.input"
            type="textarea"
            placeholder="写下你的要求描述"
            maxlength="1000"
            show-word-limit
            :autosize="{ minRows: 8, maxRows: 8 }"
          />
        </FormItem>
      </el-form>
    </template>
    <template #center>
      <DragContainer>
        <template #left>
          <VMonacoEditor
            language="javascript"
            theme="vs"
            :options="{
              automaticLayout: true,
              foldingStrategy: 'indentation',
              renderLineHighlight: 'all',
              selectOnLineNumbers: true,
              minimap: {
                enabled: false
              },
              readOnly: false,
              contextmenu: true,
              fontSize: 16,
              scrollBeyondLastLine: false,
              overviewRulerBorder: false
            }"
            v-model="curItem.multiModalResourceOutput[0]"
          ></VMonacoEditor>
        </template>
        <template #main>
          <RenderMarkdown :content="curItem.multiModalResourceOutput[0] || ''" v-if="!loading" />
        </template>
      </DragContainer>
    </template>
    <template #right>
      <History
        ref="hisRef"
        @setActiveItem="changeHistory"
        :type="8"
        showContent="text"
        :gening="loading"
      />
    </template>
  </ModeContainer>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import ModeContainer from '@/components/mode-container/index.vue'
import FormItem from '../components/FormItem.vue'
import FormItemMode from '../components/FormItemMode.vue'
import { workChartGen } from '@/api/probot-mode'
import type { FormInstance, FormRules } from 'element-plus'
import History from '../components/history/History.vue'
import { ElMessage } from 'element-plus'
import RenderMarkdown from '../components/RenderMarkdown/index.vue'
import DragContainer from '../components/DragContainer.vue'
import VMonacoEditor from '@/components/monaco-editor/index.vue'

interface RuleForm {
  model: string
  chartType: string
  input: string
}
const hisRef = ref(null)
const form = reactive({
  model: 'Claude-3.5-Sonnet-company',
  chartType: '',
  input: ''
})

const formRef = ref<FormInstance>()
const rules = reactive<FormRules<RuleForm>>({
  model: [{ required: true, message: '请选择模型', trigger: 'change' }],
  chartType: [{ required: true, message: '请选择图表类型', trigger: 'change' }],
  input: [{ required: true, message: '请输入要求描述', trigger: 'blur' }]
})
const loading = ref(false)
const result = ref('')

const chartTypeOptions = [
  {
    value: 'flowchart',
    label: '流程图'
  },
  {
    value: 'sequenceDiagram',
    label: '序列图'
  },
  {
    value: 'classDiagram',
    label: '类图'
  },
  {
    value: 'stateDiagram',
    label: '状态图'
  },
  {
    value: 'entityRelationshipDiagram',
    label: '实体关系图'
  },
  {
    value: 'userJourneyDiagram',
    label: '用户旅程图'
  },
  {
    value: 'ganttDiagram',
    label: '甘特图'
  },
  {
    value: 'pieChart',
    label: '饼图'
  },
  {
    value: 'quadrantChart',
    label: '象限图'
  },
  {
    value: 'requirementDiagram',
    label: '需求图'
  },
  {
    value: 'gitDiagram',
    label: 'Git图'
  },
  {
    value: 'mindMap',
    label: '思维导图'
  },
  {
    value: 'timelineDiagram',
    label: '时间线图'
  }
]
const addTextItem = (setting) => {
  const val = {
    multiModalResourceOutput: [],
    runStatus: 0,
    setting
  }
  hisRef.value?.initList(val)
}
const onSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate((valid, fields) => {
    if (valid) {
      result.value = ''
      loading.value = true
      const setting = {
        model: form.model,
        cmd: 'workChartGen',
        chartType: form.chartType,
        input: form.input
      }
      addTextItem(setting)
      workChartGen(setting)
        .then((res: any) => {
          if (res.code === 0) {
            const val = {
              multiModalResourceOutput: [res.data],
              loading: false,
              setting
            }
            hisRef.value?.updateHistory(val)
            setActiveItem(val)
          } else {
            ElMessage.error(res.message || '出错了！')
          }
        })
        .finally(() => {
          loading.value = false
        })
    } else {
      console.log('error submit!', fields)
    }
  })
}

const curItem = ref({})
const setActiveItem = (item) => {
  curItem.value = item
}
const changeHistory = (item) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  curItem.value = item
  Object.assign(form, item.setting || {})
}
</script>

<style scoped lang="scss">
.result-container {
  height: 100%;
  line-height: 30px;
  font-size: 14px;
  color: #666;
}
</style>
