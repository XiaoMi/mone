<template>
  <el-drawer v-model="visible" direction="rtl" class="plugin-drawer" size="740">
    <template #header>
      <h2>{{ props.row.id ? t('plugin.editComponent') : t('plugin.addComponent') }}</h2>
    </template>
    <template #default>
      <div class="step-wrap">
        <Steps :active="state.active" />
      </div>
      <el-form
        ref="refForm"
        class="plugin-form"
        :model="state.form"
        label-width="90"
        :rules="rules"
        :label-position="appStore.language === 'en-US' ? 'top' : 'right'"
      >
        <template v-if="state.active === 0">
          <el-form-item :label="`${t('plugin.compNameLabel')}:`" prop="name">
            <el-input
              v-model="state.form.name"
              :placeholder="t('plugin.pleaseEnterComponentName')"
            />
          </el-form-item>
          <el-form-item :label="`${t('plugin.compDesc')}:`" prop="desc">
            <el-input
              v-model="state.form.desc"
              type="textarea"
              :autosize="{ minRows: 5, maxRows: 10 }"
              :placeholder="t('plugin.pleaseEnterComponentDesc')"
            />
          </el-form-item>
        </template>
        <template v-else-if="state.active === 1">
          <el-form-item :label="`${t('plugin.compType')}:`" prop="type">
            <el-select
              v-model="state.form.type"
              style="width: 100%"
              :placeholder="t('plugin.pleaseSelectTypeComponent')"
            >
              <el-option
                v-for="item in state.options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item :label="`${t('plugin.requestMethod')}:`" prop="http_method">
            <el-select
              v-model="state.form.botPluginMeta.http_method"
              style="width: 100%"
              :placeholder="t('plugin.pleaseSelectMethod')"
            >
              <el-option
                v-for="item in Object.keys(METHOD_LIST)"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="URL:" prop="apiUrl">
            <el-input v-model="state.form.apiUrl" :placeholder="t('plugin.pleaseEnterURL')" />
          </el-form-item>
          <Headers
            :label="`${t('plugin.requestHeader')}:`"
            v-model="state.form.botPluginMeta.http_header"
          />
          <Params :label="`${t('plugin.arguments')}:`" v-model="state.form.botPluginMeta.input" />
          <Params :label="`${t('plugin.output')}:`" v-model="state.form.botPluginMeta.output" />
        </template>
        <template v-else>
          <Debug :id="state.debugId" @onOk="emits('onOk')" />
        </template>
      </el-form>
    </template>
    <template #footer>
      <div>
        <el-button @click="emits('update:modelValue', false)">{{ t('plugin.close') }}</el-button>
        <el-button type="primary" @click="handlePre" v-if="state.active > 0">{{
          t('plugin.previousStep')
        }}</el-button>
        <el-button type="primary" @click="handleClick" v-if="state.active < 2">{{
          state.active < 1 ? t('plugin.nextStep') : t('common.save')
        }}</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<script lang="ts" setup>
import { computed, reactive, ref, watch } from 'vue'
import Steps from './Steps.vue'
import { t } from '@/locales'
import { createComp, update } from '@/api/plugins'
import { METHOD_LIST } from '../constants'
import Params from './Params.vue'
import Headers from './Headers.vue'
import Debug from './Debug.vue'
import { ElMessage } from 'element-plus'
import { useAppStore } from '@/stores/app'

const appStore = useAppStore()
const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  row: {
    type: Object,
    default() {
      return {}
    }
  },
  orgId: {
    type: Number,
    default: undefined
  }
})

const emits = defineEmits(['update:modelValue', 'onOk'])

const refForm = ref()

const visible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const state = reactive({
  options: [
    {
      label: 'http',
      value: 'http'
    }
  ],
  active: 0,
  debugId: 0,
  form: {
    id: undefined,
    orgId: props.orgId,
    name: undefined,
    desc: undefined,
    apiUrl: undefined,
    type: 'http',
    botPluginMeta: {
      desc: undefined,
      http_header: [] as any,
      http_method: 'post',
      input: [] as any,
      output: [] as any
    }
  }
})

const rules = {
  name: [
    { required: true, message: t('plugin.pleaseEnterComponentName'), trigger: ['blur', 'change'] }
  ],
  desc: [
    { required: true, message: t('plugin.pleaseEnterComponentDesc'), trigger: ['blur', 'change'] }
  ],
  type: [
    { required: true, message: t('plugin.pleaseSelectTypeComponent'), trigger: ['blur', 'change'] }
  ],
  'state.form.botPluginMeta.http_method': [
    { required: true, message: t('plugin.pleaseSelectMethod'), trigger: ['blur', 'change'] }
  ],
  apiUrl: [{ required: true, message: t('plugin.pleaseEnterURL'), trigger: ['blur', 'change'] }]
}

watch(
  () => state.form.botPluginMeta.http_header,
  (val) => {
    if (val.every((v) => v.paramKey && v.paramValue)) {
      state.form.botPluginMeta.http_header.push({
        paramKey: undefined,
        paramValue: undefined
      })
    }
  },
  {
    deep: true
  }
)

watch(
  () => state.form.botPluginMeta.input,
  (val) => {
    if (val.every((v) => v.name && v.desc)) {
      state.form.botPluginMeta.input.push({
        name: undefined,
        desc: undefined
      })
    }
  },
  {
    deep: true
  }
)

watch(
  () => state.form.botPluginMeta.output,
  (val) => {
    if (val.every((v) => v.name && v.desc)) {
      state.form.botPluginMeta.output.push({
        name: undefined,
        desc: undefined
      })
    }
  },
  {
    deep: true
  }
)

watch(
  () => props.modelValue,
  (val) => {
    if (!val) {
      state.active = 0
      state.debugId = 0
      state.form = {
        id: undefined,
        orgId: undefined,
        name: undefined,
        desc: undefined,
        apiUrl: undefined,
        type: 'http',
        botPluginMeta: {
          desc: undefined,
          http_header: [],
          http_method: 'post',
          input: [],
          output: []
        }
      }
    } else if (props.row?.id) {
      state.debugId = props.row.id
      let headers = []
      let input = []
      let output = []
      if (props.row.botPluginMeta.http_headers) {
        Object.keys(props.row.botPluginMeta.http_headers).forEach((k) => {
          headers.push({
            paramKey: k,
            paramValue: props.row.botPluginMeta.http_headers[k]
          })
        })
      }
      if (props.row.botPluginMeta.input?.length) {
        input = [...props.row.botPluginMeta.input]
      }
      if (props.row.botPluginMeta.output?.length) {
        output = [...props.row.botPluginMeta.output]
      }
      state.form = {
        id: props.row.id,
        orgId: props.orgId,
        name: props.row.name,
        desc: props.row.desc,
        apiUrl: props.row.apiUrl,
        type: props.row.type,
        botPluginMeta: {
          desc: props.row.botPluginMeta.desc,
          http_method: props.row.botPluginMeta.http_method,
          http_header: [
            ...headers,
            {
              paramKey: undefined,
              paramValue: undefined
            }
          ],
          input: [
            ...input,
            {
              name: undefined,
              desc: undefined
            }
          ],
          output: [
            ...output,
            {
              name: undefined,
              desc: undefined
            }
          ]
        }
      }
    } else {
      state.debugId = 0
      state.form = {
        id: undefined,
        orgId: props.orgId,
        name: undefined,
        desc: undefined,
        apiUrl: undefined,
        type: 'http',
        botPluginMeta: {
          desc: undefined,
          http_header: [
            {
              paramKey: undefined,
              paramValue: undefined
            }
          ],
          http_method: 'post',
          input: [
            {
              name: undefined,
              desc: undefined
            }
          ],
          output: [
            {
              name: undefined,
              desc: undefined
            }
          ]
        }
      }
    }
  }
)

const handlePre = () => {
  if (state.active > 0) {
    state.active--
  }
}

const handleClick = () => {
  refForm.value.validate((bool: boolean) => {
    if (bool) {
      if (state.active < 1) {
        state.active++
      } else {
        let header = {} as any
        let input = [] as any
        let output = [] as any
        if (state.form.botPluginMeta.http_header?.length) {
          state.form.botPluginMeta.http_header
            .filter((v: any) => v.paramKey && v.paramValue)
            .forEach((v: any) => {
              header[v.paramKey] = v.paramValue
            })
        }
        if (state.form.botPluginMeta.input?.length) {
          input = state.form.botPluginMeta.input.filter((v: any) => v.name && v.desc)
        }
        if (state.form.botPluginMeta.output?.length) {
          output = state.form.botPluginMeta.output.filter((v: any) => v.name && v.desc)
        }
        let params = {
          ...props.row,
          ...state.form,
          botPluginMeta: {
            desc: state.form.botPluginMeta.desc,
            http_method: state.form.botPluginMeta.http_method,
            http_headers: header,
            input,
            output
          }
        }
        if (props.row?.id || state.form.id) {
          update(params)
            .then((data) => {
              if (data.data) {
                state.active++
                ElMessage.success(t('common.editSuccess'))
                emits('onOk')
              } else {
                ElMessage.error(data.message!)
              }
            })
            .catch((e) => {
              console.log(e)
            })
        } else {
          createComp(params)
            .then((data) => {
              if (data.data) {
                state.active++
                state.debugId = data.data
                state.form.id = data.data
                ElMessage.success(t('common.saveSuccess'))
                emits('onOk')
              } else {
                ElMessage.error(data.message!)
              }
            })
            .catch((e) => {
              console.log(e)
            })
        }
      }
    }
  })
}
</script>

<style lang="scss">
.plugin-drawer {
  .oz-drawer__header {
    border-color: #ddd;
    h2 {
      color: #333;
    }
  }
  .oz-drawer__body {
    padding: 8px 16px;
    .step-wrap {
      margin: 0 16px;
    }
    .oz-form-item {
      margin-bottom: 24px;
    }
    .oz-form-item__label {
      color: #333;
    }
    .plugin-form {
      padding: 20px;
    }
  }
  .oz-drawer__footer {
    border-top: 1px solid #ddd;
    padding: 10px;
  }
}
</style>
