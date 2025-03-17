<template>
  <el-dialog v-model="dialogVisible" title="自动生成" width="500" append-to-body>
    <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules" label-position="top">
      <el-form-item label="数据表" prop="tableValue">
        <TableListSel v-model="ruleForm.tableValue" :workspaceId="workspaceId" />
      </el-form-item>
      <el-form-item label="模型" prop="model">
        <LLMModelSel v-model="ruleForm.model" />
      </el-form-item>
      <el-form-item label="SQL描述" prop="comment">
        <el-input v-model="ruleForm.comment" type="textarea" :autosize="autosizeObj" />
      </el-form-item>
      <div class="gen-btns">
        <el-button link @click.stop="genFn" type="primary" size="small" :loading="gening">
          <i class="iconfont icon-class-a" style="margin-right: 2px"></i>
          自动生成
        </el-button>
      </div>
      <el-form-item label="SQL">
        <el-input v-model="sql" type="textarea" :disabled="true" :autosize="autosizeObj"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirm" :disabled="!sql"> 使用 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, ref } from 'vue'
import { getSQLGen } from '@/api/workflow'
import { ElMessage } from 'element-plus'
import TableListSel from './TableListSel'
import LLMModelSel from '@/components/LLMModelSel'
const props = defineProps({
  modelValue: {},
  workspaceId: {}
})
const emits = defineEmits(['update:modelValue', 'sqlGenRes'])
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emits('update:modelValue', val)
})

const ruleForm = ref({})
const confirm = () => {
  emits('sqlGenRes', sql.value)
  dialogVisible.value = false
}
const ruleFormRef = ref(null)
const rules = ref({
  comment: [
    {
      required: true,
      message: '请输入SQL描述',
      trigger: 'blur'
    }
  ],
  tableValue: [
    {
      required: true,
      message: '请选择表',
      trigger: 'blur'
    }
  ],
  model: [
    {
      required: true,
      message: '请选择模型',
      trigger: 'blur'
    }
  ]
})
const autosizeObj = ref({ minRows: 3, maxRows: 8 })
const sql = ref()
const gening = ref(false)
const genFn = async () => {
  const valid = await ruleFormRef.value.validate()
  if (!valid) return
  gening.value = true
  const { comment, model, tableValue } = ruleForm.value
  const params = {
    comment,
    model,
    tableName: tableValue.tableName,
    type: tableValue.type
  }
  getSQLGen(params)
    .then((res) => {
      if (res.code != 0) {
        ElMessage.error(res.message || '获取失败！')
        return
      }
      sql.value = res.data
    })
    .finally(() => {
      gening.value = false
    })
}
</script>

<style lang="scss" scoped>
.gen-btns {
  text-align: right;
}
</style>
