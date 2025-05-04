<template>
  <el-dialog
    v-model="dialogVisible"
    :title="props.row?.id ? t('codeBase.editTitle') : t('codeBase.createBtn')"
    width="500"
    custom-class="common-dialog"
    :draggable="true"
    :append-to-body="true"
  >
    <el-form ref="formRef" :model="state.form" :rules="rules" label-position="top" status-icon>
      <el-form-item :label="`${t('codeBase.name')}：`" prop="name">
        <el-input
          v-model="state.form.name"
          autocomplete="off"
          :placeholder="t('codeBase.namePlaceholder')"
          maxlength="50"
          show-word-limit
        />
      </el-form-item>
      <el-form-item :label="`${t('common.description')}：`" prop="desc">
        <el-input
          v-model="state.form.desc"
          type="textarea"
          autocomplete="off"
          :placeholder="t('codeBase.descPlaceholder')"
          :autosize="{ minRows: 4 }"
          maxlength="2000"
          show-word-limit
        />
      </el-form-item>
      <el-form-item :label="`${t('codeBase.language')}：`" prop="language">
        <ProbotCodeLanguage v-model="state.form.language"></ProbotCodeLanguage>
      </el-form-item>
      <el-form-item prop="code">
        <div class="code-head">
          <ProbotCodeHeadTitle />
          <ProbotCodeHead v-model:code="state.form.code" @codeGenRes="codeGenRes"> </ProbotCodeHead>
        </div>
        <ProbotCodemirror v-model="state.form.code"  height="140px"></ProbotCodemirror>
      </el-form-item>
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
import { saveOrUpdate } from '@/api/probot-code'
import { ElMessage } from 'element-plus'
import { submitForm, resetForm } from '@/common/formMethod'
import ProbotCodeLanguage from '@/components/ProbotCodeLanguage'
import ProbotCodemirror from '@/components/ProbotCodemirror'
import ProbotCodeHeadTitle from '@/components/ProbotCodeHeadTitle'
import ProbotCodeHead from '@/components/ProbotCodeHead'

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

const formRef = ref()

const rules = {
  name: [{ required: true, message: t('codeBase.namePlaceholder'), trigger: ['blur', 'change'] }],
  desc: [{ required: true, message: t('codeBase.descPlaceholder'), trigger: ['blur', 'change'] }]
}

const state = reactive({
  form: {
    name: undefined,
    desc: undefined,
    language: 'text/groovy',
    code: undefined
  }
})

watch(
  () => props.modelValue,
  (val) => {
    resetForm(formRef.value)
    if (!val) {
      state.form.desc = undefined
      state.form.name = undefined
      state.form.code = undefined
    } else if (props.row?.id) {
      state.form.desc = props.row.desc
      state.form.name = props.row.name
      state.form.code = props.row.code.code
    }
  },
  {
    immediate: true
  }
)

const handleSubmit = () => {
  submitForm(formRef.value, state.form).then(() => {
    saveOrUpdate({
      id:props.row?.id||undefined,
      name: state.form.name,
      desc: state.form.desc,
      type: 1,
      code: {
        code: state.form.code,
        language: state.form.language
      }
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

const codeGenRes = (res) => {
  state.form.code = res.code
}
</script>

<style lang="scss" scoped>
.code-head {
  display: flex;
  width: 100%;
  justify-content: space-between;
  align-items: center;
}
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
