<!--
 * @Description: 
 * @Date: 2024-09-02 17:28:00
 * @LastEditTime: 2024-09-12 11:30:47
-->
<template>
  <el-dialog v-model="dialogVisible" title="飞书上传" width="500">
    <el-form :model="form" ref="ruleFormRef" :rules="rules">
      <el-form-item label="文档地址" :label-width="formLabelWidth" prop="urlPath">
        <template #label>
          <el-popover
            placement="top"
            :width="200"
            trigger="hover"
            content="飞书文档中需要添加应用“智轩”才能使用该功能"
          >
            <template #reference>
              <div style="display: flex; align-items: center">
                <el-icon color="#666" style="margin-left: 4px; margin-right: 4px">
                  <Warning />
                </el-icon>
                文档地址
              </div>
            </template>
          </el-popover>
        </template>
        <el-input v-model="form.urlPath" placeholder="请输入url地址" />
      </el-form-item>
      <el-form-item label="解析分隔符" :label-width="formLabelWidth" prop="separator">
        <el-select
          v-model="form.separator"
          filterable
          default-first-option
          :reserve-keyword="false"
          placeholder="请输入解析分隔符"
          style="width: 100%"
        >
          <el-option
            v-for="(item, index) in separatorOptions"
            :key="index"
            :label="item.value"
            :value="item.key"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="文件名称" :label-width="formLabelWidth" prop="fileName">
        <el-input v-model="form.fileName" placeholder="请输入文件名称" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sure(ruleFormRef)">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, computed, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emits = defineEmits(['update:modelValue', 'letterSubmit'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const formLabelWidth = '120px'

const form = reactive({
  urlPath: '',
  separator: '',
  fileName: ''
})
const ruleFormRef = ref<FormInstance>()
interface RuleForm {
  urlPath: string
  separator: string
  fileName: string
}
const rules = reactive<FormRules<RuleForm>>({
  urlPath: [
    {
      required: true,
      message: '请输入文件名称',
      trigger: 'change'
    }
  ],
  separator: [
    {
      required: true,
      message: '请输入自定义知识解析分隔符',
      trigger: 'change'
    }
  ],
  fileName: [
    {
      required: true,
      message: '请输入文件名称',
      trigger: 'change'
    }
  ]
})
const separatorOptions = [
  {
    key: '3',
    value: '标题 1'
  },
  {
    key: '4',
    value: '标题 2'
  },
  {
    key: '5',
    value: '标题 3'
  },
  {
    key: '6',
    value: '标题 4'
  },
  {
    key: '7',
    value: '标题 5'
  },
  {
    key: '8',
    value: '标题 6'
  },
  {
    key: '9',
    value: '标题 7'
  },
  {
    key: '10',
    value: '标题 8'
  },
  {
    key: '11',
    value: '标题 9'
  }
]

const sure = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      emits('letterSubmit', form)
    } else {
      console.log('error submit!', fields)
    }
  })
}
</script>

<style scoped></style>
