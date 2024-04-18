<template>
  <el-dialog
    v-model="dialogVisible"
    :title="props.row?.id ? t('plugin.editPluginTitle') : t('plugin.createPluginBtn')"
    width="500"
    custom-class="common-dialog"
    :draggable="true"
    :append-to-body="true"
  >
    <el-form ref="formRef" :model="state.form" :rules="rules" label-position="top" status-icon>
      <el-form-item :label="`${t('plugin.pluginName')}：`" prop="pluginOrgName">
        <el-input
          v-model="state.form.pluginOrgName"
          autocomplete="off"
          :placeholder="t('plugin.enterPluginName')"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>
      <el-form-item :label="`${t('plugin.desc')}：`" prop="pluginOrgDesc">
        <el-input
          v-model="state.form.pluginOrgDesc"
          type="textarea"
          autocomplete="off"
          :placeholder="t('plugin.pleaseEnterDescription')"
          :autosize="{ minRows: 4 }"
          maxlength="2000"
          show-word-limit
        />
      </el-form-item>
      <BaseAvatar
        :formData="state.form"
        :remark="state.form.pluginOrgDesc"
        v-model="state.form.avatarUrl"
        :tips="t('plugin.photoTips')"
        type="plugin"
      ></BaseAvatar>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ t('common.cancle') }}</el-button>
        <el-button type="primary" @click="handleSubmit">{{ t('common.save') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, watch, ref, computed } from 'vue'
import { t } from '@/locales'
import { saveOrUpdate } from '@/api/plugins'
import { ElMessage } from 'element-plus'
import BaseAvatar from '@/views/probot/components/BaseAvatar.vue'
import { useRoute } from 'vue-router'
import { submitForm, resetForm } from '@/common/formMethod'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  row: {
    type: Object,
    default() {
      return {}
    }
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

const route = useRoute()
const formRef = ref()

const rules = {
  pluginOrgName: [
    { required: true, message: t('plugin.enterPluginName'), trigger: ['blur', 'change'] }
  ],
  pluginOrgDesc: [
    { required: true, message: t('plugin.pleaseEnterDescription'), trigger: ['blur', 'change'] }
  ]
}

const state = reactive({
  form: {
    pluginOrgName: undefined,
    avatarUrl: Math.floor(Math.random() * 10) + '',
    pluginOrgDesc: undefined,
    workspaceId: route.params.id
  }
})

watch(
  () => props.modelValue,
  (val) => {
    resetForm(formRef.value)
    if (!val) {
      state.form.pluginOrgDesc = undefined
      state.form.pluginOrgName = undefined
      state.form.avatarUrl = Math.floor(Math.random() * 10) + ''
    } else if (props.row?.id) {
      state.form.pluginOrgDesc = props.row.pluginOrgDesc
      state.form.pluginOrgName = props.row.pluginOrgName
      state.form.avatarUrl = props.row.avatarUrl || Math.floor(Math.random() * 10) + ''
    }
  },
  {
    immediate: true
  }
)

const handleSubmit = () => {
  submitForm(formRef.value, state.form).then(() => {
    saveOrUpdate({
      ...props.row,
      ...state.form
    })
      .then((data) => {
        if (data.data) {
          ElMessage.success(t('common.saveSuccess'))
          emits('onOk')
          dialogVisible.value = false
        } else {
          ElMessage.error(data.message!)
        }
      })
      .catch((e) => {
        console.log(e)
      })
  })
}
</script>

<style lang="scss" scoped>
.common-dialog {
  .icon-container {
    display: flex;
    width: 100%;
    padding-bottom: 10px;

    .generate {
      flex: 1;
      height: 100px;
      align-items: center;
      background-color: #f0f0f5;
      border-radius: 8px;
      box-sizing: border-box;
      display: flex;
      margin-left: 16px;
      padding: 14px 13px;
      .create-icon {
        align-items: center;
        background-color: #f7f7fa;
        border: 1px solid rgba(29, 28, 35, 0.12);
        border-radius: 8px;
        color: #4d53e8;
        cursor: pointer;
        display: flex;
        flex-direction: column;
        height: 68px;
        justify-content: center;
        width: 68px;
      }
      .iconfont {
        font-size: 16px;
        line-height: 20px;
      }
      span {
        font-size: 12px;
        line-height: 20px;
      }
    }
  }
}
</style>
