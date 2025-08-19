<template>
  <el-dialog
    v-model="dialogVisible"
    :title="`${type == 'edit' ? '编辑' : '新建'}工作流`"
    width="500"
    :draggable="true"
    :append-to-body="true"
  >
    <el-form
      ref="formEl"
      style="width: 100%"
      :model="ruleForm"
      :rules="rules"
      status-icon
      label-position="top"
    >
      <el-form-item label="工作流名称" prop="name">
        <el-input
          v-model="ruleForm.name"
          autocomplete="off"
          placeholder="请输入工作流名称"
          :maxlength="20"
          show-word-limit
        />
      </el-form-item>
      <el-form-item label="工作流描述" prop="desc">
        <el-input
          v-model="ruleForm.desc"
          type="textarea"
          autocomplete="off"
          :autosize="{ minRows: 4, maxRows: 6 }"
          placeholder="请输入描述，让大模型理解什么情况下应该调用此工作流"
          :maxlength="50"
          show-word-limit
        />
      </el-form-item>
      <BaseAvatar
        :name="ruleForm.name"
        :remark="ruleForm.desc"
        v-model="ruleForm.avatarUrl"
        tips="输入工作流名称和描述后，点击自动生成图标。"
      ></BaseAvatar>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sure"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { createFlow, editBase } from '@/api/workflow'
import { ref, computed, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import BaseAvatar from '@/components/probot/BaseAvatar.vue'
import { submitForm, resetForm } from '@/common/formMethod'
import { useRoute, useRouter } from 'vue-router'
import { useProbotStore } from '@/stores/probot'

const probotStore = useProbotStore()

const workspaceList = computed(() => probotStore.workspaceList)

const props = defineProps({
  modelValue: {},
  preInfo: {
    default: {}
  },
  type: {
    default: 'create'
  }
})

const emits = defineEmits(['update:modelValue', 'createSuc'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const formEl = ref()
const ruleForm = ref({
  id: '',
  name: '',
  desc: '',
  avatarUrl: ''
})
const rules = ref({
  name: [
    { required: true, message: '请填写工作流名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度需要2到20之间', trigger: 'blur' }
  ]
  // desc: [{ required: true, message: '请填写工作流描述', trigger: 'blur' }]
})
const loading = ref(false)
const iconIndex = String(Math.floor(Math.random() * 10))
const route = useRoute()
const router = useRouter()

watch(
  () => props.modelValue,
  (val) => {
    resetForm(formEl.value)
    if (val && props.preInfo?.id) {
      const { id, name, desc, avatarUrl } = props.preInfo
      ruleForm.value.id = id || ''
      ruleForm.value.name = name || ''
      ruleForm.value.desc = desc || ''
      ruleForm.value.avatarUrl = avatarUrl || iconIndex
    } else {
      ruleForm.value.avatarUrl = iconIndex
    }
  }
)

const sure = () => {
  submitForm(formEl.value, ruleForm.value).then(() => {
    loading.value = true
    let request = ruleForm.value.id ? editBase : createFlow
    const p = {
      ...props.preInfo,
      ...ruleForm.value
    }
    request({ flowBaseInfo: { ...p, workSpaceId: route.params.id || workspaceList.value[0]?.id } })
      .then((data) => {
        if (data.data) {
          ElMessage.success(props.preInfo?.id ? '编辑成功！' : '创建成功！')
          dialogVisible.value = false
          emits('createSuc')
          if (!props.preInfo?.id) {
            const { href } = router.resolve({
              name: 'AI Probot workflowItem',
              params: {
                id: data.data
              }
            })
            window.open(href, '_blank')
          }
        } else {
          ElMessage.error(data.message || (props.preInfo?.id ? '编辑失败！' : '创建失败！'))
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
