<!--
 * @Description: 
 * @Date: 2024-03-27 20:33:08
 * @LastEditTime: 2024-08-30 19:10:24
-->
<template>
  <el-dialog v-model="dialogVisible" title="配置飞书openId" width="500" @open="open">
    <div class="openId-container">
      <el-form ref="ruleFormRef" :model="ruleForm" :rules="rules">
        <el-form-item label="app id" prop="openId">
          <el-input v-model="ruleForm.openId" />
        </el-form-item>
        <el-form-item label="secret" prop="secret">
          <el-input v-model="ruleForm.secret" />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="sure"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElButton, ElDialog } from 'element-plus'

const ruleFormRef = ref(null)
const ruleForm = ref({
  openId: '',
  secret: ''
})
const rules = ref({
  openId: [{ required: true, message: '请输入openId', trigger: 'change' }],
  secret: [{ required: true, message: '请输入secret', trigger: 'change' }]
})
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  data: {
    type: String,
    default: ''
  }
})
const emits = defineEmits(['update:modelValue', 'submit'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})
const openId = ref(props.data)
const open = () => {
  ruleForm.value.openId = props.data.openId
}
const sure = async () => {
  if (!ruleFormRef.value) return
  await ruleFormRef.value.validate((valid, fields) => {
    if (valid) {
      emits('submit', ruleForm.value)
      dialogVisible.value = false
    } else {
      console.log('error submit!', fields)
    }
  })
}
</script>

<style scoped lang="scss">
.openId-container {
  .openId-content {
    display: flex;
    align-items: center;
    padding: 0 20px 20px 20px;
    justify-content: space-between;
    &-title {
      padding-right: 10px;
    }
  }
  .openId-describe {
    border-top: 1px solid #ddd;
    padding: 20px 20px;
    dd {
      display: flex;
      line-height: 20px;
      padding-top: 20px;
    }
    img {
      height: 200px;
      vertical-align: top;
    }
  }
}
</style>
