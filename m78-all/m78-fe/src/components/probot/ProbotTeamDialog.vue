<!--
 * @Description: 
 * @Date: 2024-03-06 11:30:12
 * @LastEditTime: 2024-09-13 16:30:58
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    :title="props.teamInfo?.id ? t('probot.editTeam') : t('probot.createTeam')"
    width="500"
    :draggable="true"
    :append-to-body="true"
  >
    <div class="create-team-dialog-container">
      <div class="title">
        <h3>通过创建一个空间，支持Probot、插件、工作流、知识库的协作与共享。</h3>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" status-icon>
        <el-form-item label="空间名称：" prop="name">
          <el-input
            v-model="form.name"
            autocomplete="off"
            placeholder="请输入空间名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="描述：" prop="desc">
          <el-input
            v-model="form.desc"
            type="textarea"
            autocomplete="off"
            placeholder="请输入空间描述"
            :autosize="{ minRows: 4 }"
            maxlength="2000"
            show-word-limit
          />
        </el-form-item>
        <BaseAvatar
          :name="form.name"
          :remark="form.desc"
          v-model="form.avatarUrl"
          tips="输入空间名称和描述后，点击自动生成图标。"
        ></BaseAvatar>
      </el-form>
    </div>
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
import { t } from '@/locales'
import { createTeam, updateTeam } from '@/api/probot-team'
import { submitForm, resetForm } from '@/common/formMethod'
import BaseAvatar from '@/components/probot/BaseAvatar.vue'
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  teamInfo: {
    type: Object,
    default() {
      return {}
    }
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
  desc: string
  avatarUrl: string
}

const formRef = ref<FormInstance>()
const form = reactive<RuleForm>({
  name: '',
  desc: '',
  avatarUrl: ''
})
const rules = reactive<FormRules<RuleForm>>({
  name: [{ required: true, message: '请输入空间名称', trigger: 'blur' }],
  desc: [{ required: true, message: '请输入空间描述', trigger: 'blur' }],
  avatarUrl: [{ required: true, message: '请上传或生成空间图标', trigger: 'change' }]
})
const iconIndex = String(Math.floor(Math.random() * 10))
const loading = ref(false)

watch(
  () => props.modelValue,
  (val) => {
    resetForm(formRef.value)
    if (val && props.teamInfo?.id) {
      form.desc = props.teamInfo?.remark
      form.name = props.teamInfo?.name
      form.avatarUrl = props.teamInfo?.avatarUrl
    } else {
      form.avatarUrl = iconIndex
    }
  }
)

const sure = () => {
  submitForm(formRef.value, form).then(() => {
    loading.value = true
    let request = props.teamInfo?.id ? updateTeam : createTeam
    request({
      workspaceId: props.teamInfo?.id,
      workspaceName: form.name,
      remark: form.desc,
      avatarUrl: form.avatarUrl
    })
      .then((data) => {
        if (data.data) {
          ElMessage.success(props.teamInfo?.id ? '编辑成功！' : '创建成功！')
          emits('onOk')
          dialogVisible.value = false
          if (!props.teamInfo?.id) {
            router.push({
              path: '/probot-space/' + data.data
            })
          }
        } else {
          ElMessage.error(data.message || (props.teamInfo?.id ? '编辑失败！' : '创建失败！'))
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

<style lang="scss">
.create-team-dialog-container {
  .title {
    h3 {
      font-size: 14px;
      padding: 0 0px 20px;
      color: rgba(0, 0, 0, 0.7);
    }
  }
}
</style>
