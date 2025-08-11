<!--
 * @Description:
 * @Date: 2024-03-04 15:13:43
 * @LastEditTime: 2024-09-13 10:16:09
-->
<template>
  <div class="probot-create">
    <div class="head">
      <HeadForm
        ref="headFormRef"
        :formData="formData"
        :disabled="disabled"
        @update-bot-name="updateBotName"
        @update="getDetail(formData?.botId)"
      ></HeadForm>
    </div>
    <div class="content">
      <BaseTabs :activeName="activeName" :tabsData="tabsData">
        <template #config
          ><ConfigForm ref="configFormRef" :formData="formData" :disabled="disabled"></ConfigForm
        ></template>
        <template #flow
          ><FlowForm ref="flowFormRef" :formData="formData" :disabled="disabled"></FlowForm
        ></template>
        <template #task><TaskForm :formData="formData" :disabled="disabled" /></template>
        <template #database
          ><DatabaseForm ref="databaseRef" :formData="formData" :disabled="disabled"
        /></template>
        <template #context><ContextForm :disabled="disabled"></ContextForm></template>
        <template #history><History :data="formData"></History></template>
      </BaseTabs>
    </div>
    <Deploy
      v-model="deployVisible"
      :data="formData"
      @update="
        () => {
          getDetail(formData.botId)
        }
      "
    ></Deploy>
    <Debugger v-model="debugerVisible" :data="formData" />
    <div class="foot">
      <el-button type="primary" size="large" @click="copy" v-if="type === 'view'">复制</el-button>
      <template v-else>
        <el-button type="primary" plain size="large" @click="save" :disabled="saveLoading"
          >保存Probot</el-button
        >
        <el-button type="primary" plain size="large" @click="test" :disabled="saveLoading"
          >保存并调试</el-button
        >
        <el-button v-if="robId" type="primary" size="large" @click="publish">保存并发布</el-button>
      </template>
      <el-button type="primary" plain size="large" @click="goBack">返回空间</el-button>
      <el-button type="primary" plain size="large" @click="handleApi">API</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import BaseTabs from '@/components/probot/BaseTabs.vue'
import HeadForm from './components/HeadForm.vue'
import ConfigForm from './config/index.vue'
import FlowForm from './flow/index.vue'
import ContextForm from './knowlege/index.vue'
import DatabaseForm from './database/index.vue'
import TaskForm from './task/index.vue'
import History from './history/index.vue'
import Debugger from './components/Debugger.vue'
import Deploy from './components/Deploy.vue'
import { createBot, getBotDetail, updateBot } from '@/api/probot'
import { useProbotStore } from '@/stores/probot'

const probotStore = useProbotStore()

const route = useRoute()
const router = useRouter()
const robId = computed(() => probotStore.createdRobtId)
const debugerVisible = ref(false)
const deployVisible = ref(false)
const activeName = ref('config')
const databaseRef = ref()
const tabsData = [
  {
    name: 'config',
    labelIcon: 'icon-peizhi1',
    label: 'AI配置'
  },
  {
    name: 'flow',
    labelIcon: 'icon-liuchengbianpai',
    label: '流程编排'
  },
  {
    name: 'context',
    labelIcon: 'icon-rizhishangxiawenzanweifabu',
    label: '知识库绑定'
  },
  {
    name: 'database',
    labelIcon: 'icon-database',
    label: '数据库绑定'
  },
  // {
  //   name: 'task',
  //   labelIcon: 'icon-renwu',
  //   label: '计划任务'
  // },
  {
    name: 'history',
    labelIcon: 'icon-fabulishi1',
    label: '发布历史'
  }
]
const formData = ref({})
const headFormRef = ref()
const configFormRef = ref()
const flowFormRef = ref()
const saveLoading = ref(false)
const disabled = ref(false) //查看
const type = ref('') //查看，新建，编辑
const path = ref('')

const updateBotName = (botName: string) => {
  formData.value.botName = botName
}

const create = async (type?: string) => {
  // 头部表单
  return headFormRef.value.submit().then((headFormValue: any) => {
    return configFormRef.value
      .submit()
      .then((configFormValue: any) => {
        return flowFormRef.value.submit().then((flowFormValue: any) => {
          const createFlag = type === 'copy' || !formData.value?.botId || route.query.copy == '1'
          let request = createFlag ? createBot : updateBot
          saveLoading.value = true
          let meta = {}
          configFormValue.meta?.forEach((v: any) => {
            meta[v.key] = v.value
          })
          const botInfo = {
            ...formData.value?.botInfo,
            id: formData.value?.botInfo?.id,
            name: headFormValue.name + (type === 'copy' ? '-复制' : ''), //机器人名称
            workspaceId: headFormValue.workspaceId, //工作空间id
            avatarUrl: headFormValue.avatarUrl, //机器人头像
            remark: headFormValue.remark, //备注
            meta: meta
          }
          formData.value.botSetting = {
            ...formData.value?.botSetting,
            setting: configFormValue.setting, //机器人设定
            aiModel: configFormValue.aiModel, //模型
            dialogueTurns: configFormValue.dialogueTurns, //对话轮次
            temperature: configFormValue?.config_temperature_range
              ? configFormValue?.temperature
              : 0, //温度
            openingRemarks: configFormValue.openingRemarks, //开场白
            openingQues: configFormValue.openingQues, //开场白问题问题
            customizePromptSwitch: configFormValue.customizePromptSwitch || 0,
            customizePrompt: configFormValue.customizePrompt,
            timbreSwitch: configFormValue.timbreSwitch || 0,
            timbre: configFormValue.timbre, //音色
            streaming: configFormValue.streaming, // 流式
            systemSetting: configFormValue.systemSetting // 系统设置
          }
          const botSetting = {
            ...formData.value.botSetting
          }
          if (createFlag) {
            delete botInfo.id
            delete botSetting.id
            delete botSetting.botId
          }
          return request({
            // 机器人信息
            botInfo,
            //机器人设置信息
            botSetting,
            //知识库
            botExtensionBo: {
              knowledgeBaseId: probotStore.bindKnowlege.map((it) => it.knowledgeBaseId),
              flowBaseId: flowFormValue.flowBaseId,
              pluginId: flowFormValue.pluginId,
              dbTableId: getBindTableIds()
            }
          }).finally(() => {
            saveLoading.value = false
          })
        })
      })
      .catch(() => {
        router.push({
          query: {
            tab: 'config'
          }
        })
      })
  })
}

const getBindTableIds = () => {
  const arr = databaseRef.value.tables || []
  const arrChecked = arr.filter((it) => it.checked)
  return arrChecked.map((it) => it.id)
}

const getDetail = (botId: string, isCopy?: boolean) => {
  getBotDetail({
    botId
  }).then((res) => {
    if (res.code === 0) {
      if (isCopy) {
        res.data.botName = `${res.data.botName}-复制`
      }
      formData.value = res.data
      probotStore.setCreatedRobtId(res.data.botId || '')
      probotStore.setBindKnowlege(
        (res.data.knowledgeBoList || []).map(
          (it: {
            knowledgeBaseId: string
            knowledgeName: string
            creator: string
            remark: string
          }) => {
            return {
              ...it,
              id: it.knowledgeBaseId,
              name: it.knowledgeName,
              bind: true,
              desc: it.remark
            }
          }
        )
      )
    } else {
      ElMessage.error(res?.message)
    }
  })
}

const save = async () => {
  try {
    const res = await create()
    if (res?.code === 0) {
      ElMessage.success(
        route.query.copy == '1' ? '复制成功' : formData.value?.botId ? '编辑成功' : '创建成功'
      )
    } else {
      ElMessage.error(res.message)
    }
    //如果是新建跳编辑
    if (res?.data && (!formData.value?.botId || route.query.copy == '1')) {
      router.push({
        path: '/probot-edit/' + res?.data
      })
    }
  } catch (e) {
    // ElMessage.error('失败')
    console.error(e)
  }
}

const test = async () => {
  try {
    const res = await create()
    if (res?.code == 0) {
      if (res?.data && (!formData.value?.botId || route.query.copy == '1')) {
        //新建然后test
        formData.value.botId = res?.data
        router.push({
          path: '/probot-edit/' + formData.value?.botId
        })
      } else {
        await getDetail(formData.value?.botId)
      }
      debugerVisible.value = true
      probotStore.setCreatedRobtId(`${formData.value.botId}`)
    } else {
      probotStore.setCreatedRobtId('')
      ElMessage.error(res?.message)
    }
  } catch (e) {
    console.error(e)
  }
}

const publish = async () => {
  if (probotStore.createdRobtId) {
    const res = await create()
    if (res?.code == 0) {
      deployVisible.value = true
    } else {
      ElMessage.warning('保存失败，请重试')
    }
  } else {
    ElMessage.warning('请先保存测试再发布')
  }
}
const copy = async () => {
  try {
    const res = await create('copy')
    if (res?.code === 0) {
      ElMessage.success('复制成功')
    }
    router.push({
      path: '/probot-edit/' + res?.data
    })
  } catch (e) {
    console.error(e)
  }
}

const goBack = () => {
  router.push({
    path: '/probot-space/' + probotStore.workspaceId
  })
}

const handleApi = () => {
  const { href } = router.resolve({
    path: '/probot-api',
    query: {
      workspaceId: probotStore.workspaceId,
      botId: route?.params?.id
    }
  })
  window.open(href, '_blank')
}

watch(
  () => route,
  async (val) => {
    if (path.value != val.path) {
      path.value = val.path
      if (val.name == 'AI Probot View' || val.name == 'AI Probot Edit') {
        activeName.value = val.query.tab || 'config'
        getDetail(val.params.id, !!val.query.copy)
        if (val.name == 'AI Probot View') {
          disabled.value = true
          type.value = 'view'
        } else {
          disabled.value = false
          type.value = ''
        }
      }
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style lang="scss">
.probot-create {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  .head {
    margin: 20px 50px;
    padding: 0px 20px 0px;
    background-color: rgba(255, 255, 255, 0.7);
    border-radius: 5px;
  }
  .content {
    margin: 0px 50px 0px;
    padding: 10px 10px;
    background-color: rgba(255, 255, 255, 0.7);
    box-shadow:
      (0 0 #0000, 0 0 #0000),
      (0 0 #0000, 0 0 #0000),
      0 10px 15px -3px rgba(0, 0, 0, 0.1),
      0 4px 6px -4px rgba(0, 0, 0, 0.1);
    background-color: hsl(0 0% 100%);
    color: hsl(224 71.4% 4.1%);
    border-radius: 10px;
    flex: 1;
    overflow: auto;
  }
  .foot {
    margin-top: 20px;
    padding: 16px 50px;
    background-color: rgba(255, 255, 255, 0.7);
  }
}
</style>
