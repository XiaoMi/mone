<!--
 * @Description: 
 * @Date: 2024-09-25 14:48:00
 * @LastEditTime: 2024-09-25 16:22:42
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    :title="props.data.id ? '编辑变量' : '创建变量'"
    width="500"
    :draggable="true"
    :append-to-body="true"
    @open="open"
  >
    <div class="create-dialog-container">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" status-icon>
        <el-form-item label="变量名称：" prop="name">
          <el-input
            v-model="form.name"
            autocomplete="off"
            placeholder="请输入变量名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="变量类型：" prop="classType">
          <el-select v-model="form.classType" placeholder="请选择变量类型" filterable clearable>
            <el-option v-for="(item, key) in variableTypes" :key="key" :label="item" :value="item">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="变量默认值：" prop="defaultValue">
          <el-input v-model="form.defaultValue" autocomplete="off" placeholder="请输入变量默认值" />
        </el-form-item>
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
import { addCardVariable, updateCardVariable } from '@/api/probot-card'
import { submitForm, resetForm } from '@/common/formMethod'
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
  classType: string
  defaultValue: string
}

const formInit = {
  name: '',
  classType: '',
  defaultValue: ''
}
const formRef = ref<FormInstance>()
const form = ref<RuleForm>(formInit)
const rules = reactive<FormRules<RuleForm>>({
  name: [{ required: true, message: '请输入变量名称', trigger: 'blur' }],
  classType: [{ required: true, message: '请选择变量类型', trigger: 'change' }],
  defaultValue: [{ required: true, message: '请输入变量默认值', trigger: 'blur' }]
})
const loading = ref(false)
const variableTypes = ref([])

const open = async () => {
  variableTypes.value = await cardStore.getVariableTypes()
}

const setFormValue = (val: any) => {
  if (val.id) {
    form.value = {
      name: val?.name,
      classType: val?.classType,
      defaultValue: val?.defaultValue
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
    let request = props.data.id ? updateCardVariable : addCardVariable
    request({
      ...props.data,
      ...form.value,
      cardId: route.params.cardId
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
