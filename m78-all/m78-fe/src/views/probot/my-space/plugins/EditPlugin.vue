<template>
  <el-dialog
    v-model="state.show"
    :title="props.row?.id ? t('plugin.editPluginTitle') : t('plugin.addPluginTitle')"
    width="500"
    custom-class="common-dialog"
  >
    <el-form ref="formRef" :model="state.form" label-position="top" :rules="rules">
      <el-form-item :label="t('plugin.pluginName')" prop="name">
        <el-input
          v-model="state.form.name"
          autocomplete="off"
          :placeholder="t('plugin.enterPluginName')"
        />
      </el-form-item>
      <el-form-item label="url" prop="apiUrl">
        <el-input
          v-model="state.form.apiUrl"
          autocomplete="off"
          :placeholder="t('plugin.enterPluginUrl')"
        />
      </el-form-item>
      <el-form-item :label="t('plugin.mateInfo')" prop="meta">
        <Codemirror
          v-model:value="state.form.meta"
          :placeholder="t('plugin.enterMateInfo')"
          :options="cmOptions"
          :border="true"
          :height="140"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="emits('onCancel')">{{ t('common.cancle') }}</el-button>
        <el-button type="primary" @click="handleCreate">{{ t('common.save') }}</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, watch, ref } from 'vue'
import { createPlugin, updatePlugin } from '@/api/plugins'
import CodemirrorFn from '@/components/codeMirror'
import Codemirror from 'codemirror-editor-vue3'
import { ElMessage } from 'element-plus'
import { t } from '@/locales'

let { cmOptions } = CodemirrorFn()

const props = defineProps({
  visible: {
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

const emits = defineEmits(['onCancel', 'onOk'])

const formRef = ref()

const state = reactive({
  show: false,
  form: {
    name: undefined,
    apiUrl: undefined,
    meta: undefined
  }
})

const rules = {
  name: [
    {
      required: true,
      message: t('plugin.enterPluginName'),
      trigger: ['blur', 'change']
    }
  ],
  apiUrl: [
    {
      required: true,
      message: t('plugin.enterPluginUrl'),
      trigger: ['blur', 'change']
    }
  ],
  meta: [
    {
      required: true,
      message: t('plugin.enterMateInfo'),
      trigger: ['blur', 'change']
    }
  ]
}

const handleCreate = () => {
  formRef.value.validate((bool: boolean) => {
    if (bool) {
      if (props.row?.id) {
        updatePlugin({
          ...props.row,
          ...state.form
        })
          .then((data) => {
            if (data.data) {
              ElMessage.success(t('common.editSuccess'))
              emits('onCancel')
              emits('onOk')
            } else {
              ElMessage.error(data.message!)
            }
          })
          .catch((e) => {
            console.log(e)
          })
      } else {
        createPlugin(state.form)
          .then((data) => {
            if (data.data) {
              ElMessage.success(t('common.saveSuccess'))
              emits('onCancel')
              emits('onOk')
            } else {
              ElMessage.error(data.message || t('common.saveError'))
            }
          })
          .catch((e) => {
            console.log(e)
          })
      }
    }
  })
}

watch(
  () => props.visible,
  (val) => {
    if (props.row?.id) {
      state.form.apiUrl = props.row.apiUrl
      state.form.meta = props.row.meta
      state.form.name = props.row.name
    }
    state.show = val
    if (!val) {
      state.form = {
        name: undefined,
        apiUrl: undefined,
        meta: undefined
      }
    }
  },
  {
    immediate: true
  }
)
</script>

<style lang="scss" scoped></style>
