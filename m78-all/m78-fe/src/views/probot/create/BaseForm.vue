<!--
 * @Description:
 * @Date: 2024-03-04 16:35:15
 * @LastEditTime: 2024-03-22 15:44:07
-->
<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-position="left"
    status-icon
    :size="formSize"
    class="probot-base-form"
    label-width="140px"
  >
    <el-descriptions title="" :column="3" class="descriptions-info" v-if="form.id">
      <el-descriptions-item label="创建人：" label-class-name="descriptions-info-label">{{
        form.creator
      }}</el-descriptions-item>
      <el-descriptions-item label="创建时间：" label-class-name="descriptions-info-label">{{
        dateFormat(form.createTime, 'yyyy-mm-dd HH:MM:ss')
      }}</el-descriptions-item>
      <el-descriptions-item label="发布时间：" label-class-name="descriptions-info-label">{{
        dateFormat(form.publishTime, 'yyyy-mm-dd HH:MM:ss')
      }}</el-descriptions-item>
      <el-descriptions-item label="修改人：" label-class-name="descriptions-info-label">{{
        form.updator
      }}</el-descriptions-item>
      <el-descriptions-item label="修改时间：" label-class-name="descriptions-info-label">{{
        dateFormat(form.updateTime, 'yyyy-mm-dd HH:MM:ss')
      }}</el-descriptions-item>
      <el-descriptions-item label="发布状态：" label-class-name="descriptions-info-label">{{
        form.publishStatus == '1' ? '已发布' : '未发布'
      }}</el-descriptions-item>
    </el-descriptions>
    <el-form-item label="Probot工作介绍：" prop="remark" class="job-introduction">
      <el-input
        v-model="form.remark"
        type="textarea"
        :autosize="{ minRows: 4 }"
        :disabled="props.disabled"
      />
    </el-form-item>
    <BaseAvatar
      :disabled="props.disabled"
      :formData="formData"
      :remark="form.remark"
      v-model="form.avatarUrl"
    ></BaseAvatar>
    <div class="publish-calendar">
      <h3>发布日历</h3>
      <BaseCommitLog :data="formData?.publishRecordDTOS"></BaseCommitLog>
    </div>
  </el-form>
</template>

<script lang="ts" setup>
import { reactive, ref, watch, defineExpose } from 'vue'
import { type FormInstance, type FormRules } from 'element-plus'
import BaseCommitLog from './BaseCommitLog.vue'
import { submitForm } from '@/common/formMethod'
import BaseAvatar from '@/views/probot/components/BaseAvatar.vue'
import dateFormat from 'dateformat'

const props = defineProps({
  formData: {
    type: Object,
    default: () => ({})
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

interface RuleForm {
  avatarUrl: string
  creator: string
  remark: string
  publishStatus: string
  createTime: string
  id: string
  updateTime: string
  updator: string
  publishTime: string
}

const iconIndex = Math.floor(Math.random() * 10)
const formRef = ref<FormInstance>()
const form = ref<RuleForm>({
  avatarUrl: String(Math.floor(Math.random() * 10)),
  creator: '',
  remark: '',
  publishStatus: '',
  createTime: '',
  id: '',
  updateTime: '',
  updator: '',
  publishTime: ''
})
const rules = reactive<FormRules<RuleForm>>({
  remark: [{ required: true, message: '请输入Probot工作介绍', trigger: 'blur' }],
  avatarUrl: [{ required: true, message: '图标不能为空', trigger: 'blur' }]
})
const formSize = ref('large') //default\large

watch(
  () => props.formData,
  (formData) => {
    console.log(formData)
    const { botInfo } = formData
    form.value = {
      avatarUrl: botInfo?.avatarUrl || iconIndex,
      creator: botInfo?.creator,
      remark: botInfo?.remark,
      publishStatus: botInfo?.publishStatus,
      createTime: botInfo?.createTime,
      id: botInfo?.id,
      updateTime: botInfo?.updateTime,
      updator: botInfo?.updator,
      publishTime: botInfo?.publishTime
    }
  }
)

defineExpose({
  submit: () => {
    return submitForm(formRef.value, form.value)
  }
})
</script>

<style lang="scss">
.probot-base-form {
  width: 100%;
  .descriptions-info {
    width: 100%;
    padding-bottom: 10px;
    .descriptions-info-label {
      display: inline-block;
      width: 124px !important;
    }
  }
  .job-introduction {
    padding-bottom: 10px;
  }
  .publish-calendar {
    padding-top: 10px;
    h3 {
      font-size: 14px;
    }
  }
}
</style>
