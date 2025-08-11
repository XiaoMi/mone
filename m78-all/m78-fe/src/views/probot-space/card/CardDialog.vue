<!--
 * @Description: 
 * @Date: 2024-09-10 14:10:33
 * @LastEditTime: 2024-10-09 15:44:54
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    :title="props.data.id ? '编辑卡片' : '创建卡片'"
    width="500"
    :draggable="true"
    :append-to-body="true"
    @open="open"
  >
    <div class="create-dialog-container">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" status-icon>
        <el-form-item label="名称：" prop="name">
          <el-input
            v-model="form.name"
            autocomplete="off"
            placeholder="请输入名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="描述：" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            autocomplete="off"
            placeholder="请输入描述"
            :autosize="{ minRows: 4 }"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <template v-if="props.type !== 'detail'">
          <BaseAvatar
            :name="form.name"
            :remark="form.description"
            v-model="form.avatarUrl"
            tips="输入 名称和介绍后，点击自动生成头像"
          ></BaseAvatar>
          <el-form-item label="类型：" prop="type">
            <el-select v-model="form.type" placeholder="请选择" filterable clearable>
              <el-option v-for="(item, key) in typeOptions" :key="key" :label="item" :value="key">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态：" prop="status">
            <el-select v-model="form.status" placeholder="请选择" filterable clearable>
              <el-option v-for="(item, key) in statusOptions" :key="key" :label="item" :value="key">
              </el-option>
            </el-select>
          </el-form-item>
        </template>
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
import { useRoute } from 'vue-router'
import { addCardBasic, updateCardBasic } from '@/api/probot-card'
import { submitForm, resetForm } from '@/common/formMethod'
import BaseAvatar from '@/components/probot/BaseAvatar.vue'
import { useProbotCardStore } from '@/stores/card'
const cardStore = useProbotCardStore()

const route = useRoute()
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: Object,
    default: () => ({})
  },
  type: {
    type: String,
    default: 'list'
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
  type: string
  name: string
  avatarUrl: string
  description: string
  status: string
}

const formInit = {
  avatarUrl: Math.floor(Math.random() * 10) + '',
  type: '',
  name: '',
  description: '',
  status: ''
}
const formRef = ref<FormInstance>()
const form = ref<RuleForm>(formInit)
const rules = reactive<FormRules<RuleForm>>({
  name: [{ required: true, message: '请输入卡片名称', trigger: 'blur' }],
  avatarUrl: [{ required: true, message: '请选择卡片图标', trigger: 'change' }],
  description: [{ required: true, message: '请输入卡片描述', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})
const loading = ref(false)
const statusOptions = ref([])
const typeOptions = ref([])

const open = async () => {
  typeOptions.value = await cardStore.getTypeOptions()
  statusOptions.value = await cardStore.getStatusOptions()
}

const setFormValue = (val: any) => {
  if (val.id) {
    form.value = {
      name: val?.name,
      description: val?.description || '',
      avatarUrl: val?.avatarUrl || '',
      type: val?.type,
      status: val?.status + ''
    }
  } else {
    form.value = { ...formInit }
  }
}
/**  watch */
watch(
  () => props.data,
  (val) => {
    resetForm(formRef.value)
    setFormValue(val)
  },
  {
    immediate: true,
    deep: true
  }
)
const sure = () => {
  submitForm(formRef.value, form).then(() => {
    loading.value = true
    let request = props.data.id ? updateCardBasic : addCardBasic
    request({
      ...props.data,
      ...form.value,
      workSpaceId: route.params.id
    })
      .then((data) => {
        if (data.data) {
          ElMessage.success(props.data.id ? '编辑成功！' : '创建成功！')
          emits('update:modelValue', false)
          emits('onOk', !props.data?.id ? data.data : '')
        } else {
          ElMessage.error(data.message || '创建失败')
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
.create-dialog-container {
  .oz-input,
  .oz-select {
    width: 100%;
  }
}
</style>
