<!--
 * @Description: done
 * @Date: 2024-03-04 16:35:15
 * @LastEditTime: 2024-12-05 15:46:18
-->
<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-position="left"
    status-icon
    :size="formSize"
    class="probot-head-form"
    :inline="true"
  >
    <el-row :gutter="12">
      <el-col :span="10" style="display: flex; align-items: center">
        <div style="display: flex; width: 100%; align-items: flex-start">
          <div style="width: auto; overflow: hidden">
            <BaseInfo
              :data="{
                describe: props.formData?.botInfo?.remark || '----',
                name: props.formData?.botInfo?.name || '----',
                avatarUrl: props.formData?.botInfo?.avatarUrl || '10'
              }"
              size="small"
            ></BaseInfo>
          </div>
          <el-button
            link
            :icon="Edit"
            @click="editProbot"
            :disabled="props.disabled"
            type="primary"
          ></el-button>
        </div>
      </el-col>
      <el-col :span="14" class="probot-workspace"
        ><el-form-item label="工作空间：" prop="workspaceId" label-width="100px">
          <el-select
            v-model="form.workspaceId"
            placeholder="请选择工作空间"
            :disabled="props.disabled"
          >
            <el-option
              v-for="item in workspaceList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-link type="primary" @click="toCreateWorkspace" class="link-btn"
          >还没有工作空间，去创建</el-link
        >
      </el-col>
    </el-row>
  </el-form>
  <ProbotTeamDialog v-model="createTeamDialogVisible" @onOk="getList"></ProbotTeamDialog>
  <CreateProbot
    v-model="showProbotCreate"
    :formData="formData"
    @onOk="emits('update')"
  ></CreateProbot>
</template>

<script lang="ts" setup>
import { reactive, ref, watch, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { submitForm, resetForm } from '@/common/formMethod'
import { getWorkspaceList } from '@/api/probot'
import ProbotTeamDialog from '@/components/probot/ProbotTeamDialog.vue'
import { useProbotStore } from '@/stores/probot'
import { t } from '@/locales'
import BaseInfo from '@/components/BaseInfo.vue'
import CreateProbot from '@/views/probot-create/CreateProbot.vue'
import { Edit } from '@element-plus/icons-vue'

const probotStore = useProbotStore()
const workspaceList = computed(() => probotStore.workspaceList)
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

const emits = defineEmits(['updateBotName', 'update'])

interface RuleForm {
  name: string
  remark: string
  avatarUrl: string
  workspaceId: number | undefined
}

const formRef = ref<FormInstance>()
const form = ref<RuleForm>({
  name: '',
  remark: '',
  avatarUrl: '',
  workspaceId: undefined
})
const rules = reactive<FormRules<RuleForm>>({
  name: [{ required: true, message: t('probot.enterProbotName'), trigger: 'blur' }],
  workspaceId: [{ required: true, message: '请选择工作空间', trigger: 'change' }]
})
const formSize = ref('default') //default\large
const createTeamDialogVisible = ref(false)
const showProbotCreate = ref(false)

const toCreateWorkspace = () => {
  createTeamDialogVisible.value = true
}

const editProbot = () => {
  if (!props.disabled) {
    showProbotCreate.value = true
  }
}
const getList = () => {
  getWorkspaceList().then((res) => {
    probotStore.setWorkspaceList(res?.data)
  })
}
watch(
  () => props.formData,
  ({ botName, botInfo }) => {
    form.value = {
      name: botName,
      remark: botInfo?.remark,
      avatarUrl: botInfo?.avatarUrl,
      workspaceId: botInfo?.workspaceId
    }
  },
  {
    immediate: true,
    deep: true
  }
)

watch(
  () => form.value.name,
  (name, preName) => {
    if (name !== preName) {
      emits('updateBotName', name)
    }
  }
)
watch(
  () => form.value.workspaceId,
  (id) => {
    probotStore.setWorkspaceId(id)
  }
)

defineExpose({
  submit: () => {
    return submitForm(formRef.value, form.value)
  },
  reset: () => {
    return resetForm(formRef.value)
  }
})
</script>

<style lang="scss">
.probot-head-form {
  width: 100%;
  .oz-form-item,
  .oz-input,
  .oz-select {
    width: 100%;
  }
  .probot-workspace {
    width: 100%;
    display: flex;
    align-items: flex-start;
    padding-top: 18px;
    .oz-form-item {
      margin-right: 10px;
    }
    .link-btn {
      line-height: 32px;
      width: 240px;
    }
  }
}
</style>
