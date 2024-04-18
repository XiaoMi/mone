<!--
 * @Description: 
 * @Date: 2024-03-06 11:30:12
 * @LastEditTime: 2024-03-27 20:09:23
-->
<template>
  <el-dialog v-model="dialogVisible" :title="'创建分类'" width="500">
    <div class="create-category-dialog-container">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        status-icon
        class="create-team-form"
      >
        <el-form-item label="分类类型：" prop="type">
          <el-select v-model="form.type" placeholder="请选择分类类型">
            <el-option
              v-for="(item, index) in props.categoryOptions"
              :key="index"
              :label="item"
              :value="index"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分类名称：" prop="categoryName">
          <el-input v-model="form.categoryName" maxlength="50" show-word-limit />
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
import { createCategory } from '@/api/probot-classification'
import { submitForm, resetForm } from '@/common/formMethod'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  categoryOptions: {}
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
  categoryName: string
  type: number | string
}

const formRef = ref<FormInstance>()
const form = reactive<RuleForm>({
  type: '',
  categoryName: ''
})
const rules = reactive<FormRules<RuleForm>>({
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择分类类型', trigger: 'change' }]
})
const loading = ref(false)

watch(
  () => props.modelValue,
  (val) => {
    resetForm(formRef.value)
  }
)

const sure = () => {
  submitForm(formRef.value, form).then(() => {
    loading.value = true
    createCategory({
      categoryName: form.categoryName,
      type: form.type
    })
      .then((data) => {
        if (data.data) {
          ElMessage.success('创建成功！')
          emits('onOk', form.type)
          emits('update:modelValue', false)
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
.create-category-dialog-container {
  .oz-input,
  .oz-select {
    width: 100%;
  }
}
</style>
