<!--
 * @Description: 
 * @Date: 2024-08-13 11:30:47
 * @LastEditTime: 2024-09-10 17:19:57
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    :title="props.formData?.botId ? '编辑Probot' : '新建Probot'"
    width="500"
    :draggable="true"
    :append-to-body="true"
    @open="open"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top" status-icon>
      <el-form-item label="工作空间：" prop="workspaceId">
        <el-select
          v-model="form.workspaceId"
          placeholder="请选择工作空间"
          :disabled="props.disabled"
          style="width: 100%"
        >
          <el-option
            v-for="item in workspaceList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="Probot名称：" prop="name">
        <el-input v-model="form.name" placeholder="请输入Probot名称" :disabled="props.disabled" />
      </el-form-item>
      <el-form-item label="Probot工作介绍：" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :autosize="{ minRows: 4 }"
          :disabled="props.disabled"
        />
      </el-form-item>
      <BaseAvatar
        :id="props.formData.id"
        :disabled="props.disabled"
        :name="form.name"
        :remark="form.remark"
        v-model="form.avatarUrl"
      ></BaseAvatar>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sure" :disabled="loading"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { createBot, updateBot } from '@/api/probot'
import { submitForm, resetForm } from '@/common/formMethod'
import BaseAvatar from '@/components/probot/BaseAvatar.vue'
import { useProbotStore } from '@/stores/probot'

const probotStore = useProbotStore()

import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()
const workspaceList = computed(() => probotStore.workspaceList)

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  formData: {
    type: Object,
    default() {
      return {}
    }
  },
  disabled: {
    type: Boolean,
    default: false
  },
  probotCreateType: {
    type: String,
    default: ''
  }
})
const emits = defineEmits(['update:modelValue', 'onOk'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

interface RuleForm {
  name: string
  remark: string
  avatarUrl: string
  workspaceId: number | string
}

const formRef = ref<FormInstance>()
const form = reactive<RuleForm>({
  name: '',
  remark: '',
  avatarUrl: String(Math.floor(Math.random() * 10)),
  workspaceId: ''
})
const rules = reactive<FormRules<RuleForm>>({
  name: [{ required: true, message: '请输入Probot名称', trigger: 'blur' }],
  remark: [{ required: true, message: '请输入Probot工作介绍', trigger: 'blur' }],
  avatarUrl: [{ required: true, message: '图标不能为空', trigger: 'blur' }],
  workspaceId: [{ required: true, message: '请选择工作空间', trigger: 'change' }]
})
const iconIndex = String(Math.floor(Math.random() * 10))
const loading = ref(false)

const initData = (data, val, id) => {
  if (data?.botId) {
    const { botInfo } = data
    form.name = botInfo?.name
    form.remark = botInfo?.remark
    form.avatarUrl = botInfo?.avatarUrl || iconIndex
    form.workspaceId = Number(botInfo?.workspaceId) || val[0]?.id
  } else {
    form.name = ''
    form.remark = ''
    form.avatarUrl = iconIndex
    form.workspaceId = Number(id) || val[0]?.id
  }
}
const open = () => {
  initData(props.formData, workspaceList.value, route?.params?.id)
}
watch(
  () => [props.formData, workspaceList.value, route?.params?.id],
  ([formData, val, id]) => {
    resetForm(formRef.value)
    initData(formData, val, id)
  }
)

const sure = () => {
  submitForm(formRef.value, form).then(() => {
    loading.value = true
    let request = props.formData?.botId ? updateBot : createBot
    request({
      botInfo: {
        ...props.formData.botInfo,
        workspaceId: form.workspaceId, //工作空间id
        name: form.name,
        remark: form.remark,
        avatarUrl: form.avatarUrl
      }
    })
      .then((data) => {
        if (data.data) {
          ElMessage.success(props.formData?.botId ? '编辑成功！' : '创建成功！')
          emits('onOk')
          dialogVisible.value = false
          resetForm(formRef.value)
          if (!props.formData?.botId) {
            const { href } = router.resolve({
              path: '/probot-edit/' + data.data,
              query: {
                type: props.probotCreateType,
                workspaceId: form.workspaceId
              }
            })
            window.open(href, '_blank')
          }
        } else {
          ElMessage.error(data.message || (props.formData?.botId ? '编辑失败！' : '创建失败！'))
        }
      })
      .catch((e) => {
        console.log(e)
      })
      .finally(() => {
        loading.value = false
      })
  })
}
</script>

<style lang="scss"></style>
