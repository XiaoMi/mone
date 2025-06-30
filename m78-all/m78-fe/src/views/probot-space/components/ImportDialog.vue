<!--
 * @Description: 
 * @Date: 2024-07-17 15:33:36
 * @LastEditTime: 2024-07-18 10:53:52
-->
<template>
 <el-dialog v-model="dialogVisible" 
    title="导入配置"
    width="750px"
    :close-on-click-modal="false"
  >
    <el-form ref="formRef" :model="configureForm" :rules="rules">
      <el-form-item label="配置" prop="str">
        <el-input
          type="textarea"
          :autosize="{ minRows: 10 }"
          placeholder="请输入内容"
          v-model="configureForm.str"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button  @click="dialogVisible = false">取 消</el-button>
      <el-button type="primary"  @click="submitConfigureForm">确 定</el-button>
    </span>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})
const emits = defineEmits(['update:modelValue', 'callback'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const configureForm = ref({
  str: ''
})
const isJson = (str: string) => {
  try {
    const parseData = JSON.parse(str)
    if (typeof parseData == 'object') {
      return true
    }
  } catch (e) {}
  return false
}
const rules = {
  str: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          return callback(new Error('请输入内容！'))
        } else {
          if (!isJson(value)) {
            return callback(new Error('输入格式不对，请修改！'))
          }
        }
        return callback()
      },
      trigger: 'blur'
    }
  ]
}
const formRef = ref()

const submitConfigureForm = () => {
  // 校验 
  formRef.value.validate((valid: boolean) => {
    if (valid) {
      emits('callback',configureForm.value.str)
      dialogVisible.value = false
    }
  })
}
</script>

<style scoped lang="scss">
.dialog-footer{
  display: flex;
  justify-content: flex-end;
}</style>
