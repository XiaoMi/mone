<template>
  <div class="feed-back">
    <el-form :model="state.form" :rules="rules" ref="formRef" label-width="90px" size="large">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('about.name')" prop="contactName">
            <el-input :placeholder="t('about.namePlaceholder')" v-model="state.form.contactName" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('about.emial')" prop="contactEmail">
            <el-input
              :placeholder="t('about.emialPlaceholder')"
              v-model="state.form.contactEmail"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item :label="t('about.theme')" prop="contactSubject">
            <el-input
              :placeholder="t('about.themePlaceholder')"
              v-model="state.form.contactSubject"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item :label="t('about.content')" prop="contactContent">
            <el-input
              v-model="state.form.contactContent"
              type="textarea"
              resize="none"
              :placeholder="t('about.contentPlaceholder')"
              :autosize="{ minRows: 6, maxRows: 8 }"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20">
        <el-col :span="24">
          <div class="btns">
            <el-button v-loading="state.loading" type="primary" @click="handleSubmit">{{
              t('about.submit')
            }}</el-button>
          </div>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { feedback } from '@/api/feedback'
import { ElMessage } from 'element-plus'
import { t } from '@/locales'

const formRef = ref()
const state = reactive({
  loading: false,
  form: {
    contactName: undefined,
    contactEmail: undefined,
    contactSubject: undefined,
    contactContent: undefined
  }
})
const rules = {
  contactName: [
    { required: true, message: t('about.namePlaceholder'), trigger: ['change', 'blur'] }
  ],
  contactEmail: [
    { required: true, message: t('about.emialPlaceholder'), trigger: ['change', 'blur'] },
    {
      type: 'email',
      message: t('about.emailTip'),
      trigger: ['blur', 'change']
    }
  ],
  contactSubject: [
    { required: true, message: t('about.themePlaceholder'), trigger: ['change', 'blur'] }
  ],
  contactContent: [
    { required: true, message: t('about.contentPlaceholder'), trigger: ['change', 'blur'] }
  ]
}

const handleSubmit = () => {
  formRef.value?.validate((bool: boolean) => {
    if (bool) {
      state.loading = true
      feedback(state.form)
        .then((data) => {
          if (data.code === 0) {
            ElMessage.success(t('about.feekSuccess'))
            formRef.value?.resetFields()
          }
        })
        .catch((e) => {
          console.log(e)
        })
        .finally(() => {
          state.loading = false
        })
    }
  })
}
</script>

<style lang="scss" scoped>
.feed-back {
  padding-top: 10px;
  .oz-form {
    padding: 40px;
    border-radius: 5px;
    background-color: #fff;
    .btns {
      text-align: right;
    }
  }
}
</style>
