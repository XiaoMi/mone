<!--
 * @Description: 
 * @Date: 2024-09-25 14:48:00
 * @LastEditTime: 2024-10-10 15:00:19
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    title="配置条件"
    width="800"
    :draggable="true"
    :append-to-body="true"
    @open="open"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top" inline>
      <el-form-item label="选择左值" prop="key">
        <el-select v-model="form.key" @change="keyChange">
          <el-option
            v-for="(item, key) in variableListData"
            :key="item.id + '' + key"
            :label="item.name"
            :value="item.id"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="选择操作符" prop="operator">
        <el-select v-model="form.operator">
          <el-option v-for="(item, key) in operatorTypes" :key="key" :label="item" :value="item">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="选择右值" prop="value">
        <el-input v-model="form.value">
          <template #prepend>
            <el-select v-model="form.valueType" style="width: 115px">
              <el-option v-for="(item, key) in valueTypes" :key="key" :label="item" :value="key">
              </el-option>
            </el-select>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sure" > 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, computed, watch } from 'vue'
import { type FormInstance, type FormRules } from 'element-plus'
import { useRoute } from 'vue-router'
import { submitForm, resetForm } from '@/common/formMethod'
import { getOperators, getCardVariablesByCardId, getVisibilityValueTypes } from '@/api/probot-card'

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
  key: string
  operator: string
  value: string
  valueType: string
}

const formInit = {
  key: '',
  operator: '',
  value: '',
  valueType: 'AlwaysDisplay'
}
const formRef = ref<FormInstance>()
const form = ref<RuleForm>(formInit)
const rules = reactive<FormRules<RuleForm>>({
  key: [{ required: true, message: '请选择左值', trigger: 'change' }],
  operator: [{ required: true, message: '请选择操作符', trigger: 'change' }],
  valueType: [{ required: true, message: '请选择右值类型', trigger: 'change' }],
  value: [{ required: true, message: '请输入右值', trigger: 'blur' }]
})
const variableListData = ref([])
const operatorTypes = ref({})
const valueTypes = ref({})

const open = async () => {
  resetForm(formRef.value)
  setFormValue(props.data)
  getCardVariablesByCardId({ cardId: route.params?.cardId }).then((res) => {
    variableListData.value = res.data
  })
  getVisibilityValueTypes().then((res) => {
    valueTypes.value = res.data
  })
}
const keyChange = (value: any) => {
  form.value.operator = ''
  const { classType } = variableListData.value.find((item) => item.id == value)
  getOperators({ type: classType }).then((res) => {
    operatorTypes.value = res.data
  })
}

const setFormValue = (val: any) => {
  form.value = {
    key: Number(val?.key),
    operator: val?.operator,
    value: val?.value,
    valueType: val?.valueType
  }
}
const sure = () => {
  submitForm(formRef.value, form).then(() => {
    emits('update:modelValue', false)
    emits('onOk', {
      ...props.data,
      ...form.value
    })
  })
}
</script>

<style lang="scss"></style>
