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
        label-width="100"
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
              @change="typeChange"
            >
              <el-option
                v-for="item in state.options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <!-- http -->
          <div v-if="state.form.type === 'http'">
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
            <div class="ouput-box">
              <p class="ouput-t">入参:</p>
              <div class="com-box">
                <el-button link @click="addHttpInput" class="add-btn" size="small"
                  ><i class="iconfont icon-plus1 icon-btn"></i
                ></el-button>
                <OutPutsTree
                  v-model="state.form.botPluginMeta.input"
                  :showDesc="true"
                  :isCanEmpty="true"
                  :showDisplay="true"
                  :showRequired="true"
                  :check="false"
                />
              </div>
            </div>
          </div>
          <div v-else-if="state.form.type === 'dubbo'">
            <!-- dubbo -->
            <el-form-item label="服务名称:" prop="botPluginMeta.dubboServiceName">
              <el-input
                v-model="state.form.botPluginMeta.dubboServiceName"
                placeholder="服务名称"
              />
            </el-form-item>
            <el-form-item label="服务分组:" prop="dubboServiceGroup">
              <el-input
                v-model="state.form.botPluginMeta.dubboServiceGroup"
                placeholder="服务分组"
              />
            </el-form-item>
            <el-form-item label="服务版本:" prop="dubboServiceVersion">
              <el-input
                v-model="state.form.botPluginMeta.dubboServiceVersion"
                placeholder="服务版本"
              />
            </el-form-item>
            <el-form-item label="方法名:" prop="botPluginMeta.dubboMethodName">
              <el-input v-model="state.form.botPluginMeta.dubboMethodName" placeholder="方法名" />
            </el-form-item>
            <el-form-item label="参数列表:">
              <el-collapse style="width: 100%" v-model="activeNames">
                <el-collapse-item
                  :name="index"
                  v-for="(param, index) in state.form.botPluginMeta.dubboMethodParamtypes"
                  :key="index"
                  style="width: 100%"
                >
                  <template #title>
                    <div>
                      <el-input
                        v-model="param.type"
                        class="second-input"
                        style="width: 430px"
                        placeholder="请按顺序依次填写入参字段类型，e.g. java.lang.String"
                      ></el-input>
                      <el-button link @click.stop="addParam(index)" :icon="Plus"> </el-button>
                      <el-button
                        link
                        @click.stop="delParam(index)"
                        :disabled="state.form.botPluginMeta.dubboMethodParamtypes.length == 1"
                        style="margin-left: 10px"
                        :icon="Minus"
                      >
                      </el-button>
                    </div>
                  </template>
                  <div class="input-box">
                    <div class="com-box">
                      <el-button
                        link
                        @click="addInput(index)"
                        class="add-btn"
                        size="small"
                        v-if="param.input.length == 0"
                        ><i class="iconfont icon-plus1 icon-btn"></i
                      ></el-button>
                      <OutPutsTree
                        v-model="param.input"
                        :showDesc="true"
                        :isCanEmpty="true"
                        :showDisplay="true"
                        :check="false"
                      />
                    </div>
                  </div>
                </el-collapse-item>
              </el-collapse>
              <div></div>
            </el-form-item>
            <RpcContextConfig
              prop-name="botPluginMeta.rpcContext"
              label="RpcContext:"
              v-model="state.form.botPluginMeta.rpcContext"
            />
          </div>
          <div class="ouput-box">
            <p class="ouput-t">出参:</p>
            <div class="com-box">
              <el-button link @click="addOp" class="add-btn" size="small"
                ><i class="iconfont icon-plus1 icon-btn"></i
              ></el-button>
              <OutPutsTree
                v-model="state.form.botPluginMeta.output"
                :showDesc="true"
                :isCanEmpty="true"
                :showDisplay="true"
                 :check="false"
              />
            </div>
          </div>
          <el-form-item label="聊天展示字段:" prop="display">
            <el-input v-model="state.form.display" placeholder="Bot聊天展示字段" />
            <el-alert title="只支持一个字段，需要展示多个字段信息，需拼接信息进行展示" type="warning" :closable="false" show-icon style="padding:0px"/>
          </el-form-item>
          <el-form-item label="返回内容类型:" prop="resContentType">
            <ResSel v-model="state.form.resContentType" />
          </el-form-item>
        </template>
        <template v-else>
          <Debug
            :data="{
              id: state.debugId
            }"
            @onOk="emits('onOk')"
          />
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
import Headers from './Headers.vue'
import Debug from './Debug.vue'
import { ElMessage } from 'element-plus'
import { useAppStore } from '@/stores/app'
import OutPutsTree from '@/views/workflow/work-flow/components/components/OutPutsTree.vue'
import ResSel from '@/components/ResSel.vue'
import { v4 as uuidv4 } from 'uuid'
import RpcContextConfig from './RpcContextConfig.vue'
import { Plus, Minus } from '@element-plus/icons-vue'

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
    },
    {
      label: 'dubbo',
      value: 'dubbo'
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
    display: undefined,
    resContentType: undefined,
    botPluginMeta: {
      desc: undefined,
      http_header: [] as any,
      http_method: 'post',
      input: [] as any,
      output: [] as any,
      dubboServiceName: undefined,
      dubboServiceGroup: undefined,
      dubboServiceVersion: undefined,
      dubboMethodName: undefined,
      dubboMethodParamtypes: [{ type: '', input: [] as any }],
      rpcContext: [
        {
          key: '',
          value: ''
        }
      ]
    }
  }
})
const rpcContextObj = ref({})
const activeNames = ref(['0'])

const checkParams = (rule: any, value: any, callback: any) => {
  if (value && value.length > 0) {
    for (let i = 0; i < value.length; i++) {
      const item = value[i]
      if (item.desc?.trim() && !item.name?.trim()) {
        callback(new Error(`第${i + 1}行描述不为空, 参数名称必填`))
      }
    }
  }
  callback()
}

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
  'botPluginMeta.input': [{ validator: checkParams, trigger: ['blur', 'change'] }],
  'botPluginMeta.output': [{ validator: checkParams, trigger: ['blur', 'change'] }],
  apiUrl: [{ required: true, message: t('plugin.pleaseEnterURL'), trigger: ['blur', 'change'] }],
  'botPluginMeta.dubboServiceName': [
    { required: true, message: '请输入服务名称', trigger: ['blur', 'change'] }
  ],
  'botPluginMeta.dubboMethodName': [
    { required: true, message: '请输入方法名', trigger: ['blur', 'change'] }
  ]
}
const typeChange = () => {
  // state.form.botPluginMeta.input = [initOutput()]
}
const initOutput = () => {
  return {
    id: uuidv4(),
    name: '',
    valueType: '',
    originalType: null,
    desc: '',
    children: [],
    required: false
  }
}

const addHttpInput = () => {
  state.form.botPluginMeta.input.push(initOutput())
}
const addOp = () => {
  state.form.botPluginMeta.output.push(initOutput())
}
const addInput = (index) => {
  state.form.botPluginMeta.dubboMethodParamtypes[index].input.push(initOutput())
}

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
          output: [],
          dubboServiceName: undefined,
          dubboServiceGroup: undefined,
          dubboServiceVersion: undefined,
          dubboMethodName: undefined,
          dubboMethodParamtypes: [{ type: '', input: [] }],
          rpcContext: [
            {
              key: '',
              value: ''
            }
          ]
        },
        display: undefined,
        resContentType: undefined
      }
      rpcContextObj.value = {}
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
      } else {
        input = [initOutput()]
      }
      if (props.row.botPluginMeta.output?.length) {
        output = [...props.row.botPluginMeta.output]
      }

      rpcContextObj.value = props.row.botPluginMeta.rpcContext
      let config = []
      if (props.row.botPluginMeta.rpcContext) {
        const getConfig = (config: {}) => {
          if (config) {
            return Object.keys(config)
          } else {
            return []
          }
        }
        const keys = getConfig(props.row.botPluginMeta.rpcContext)
        if (props.row.botPluginMeta.rpcContext && keys.length) {
          keys.forEach((v) => {
            if (v || props.row.botPluginMeta.rpcContext[v]) {
              config.push({
                key: v,
                value: props.row.botPluginMeta.rpcContext[v]
              })
            }
          })
        }
        if (config.length === 0) {
          config.push({
            key: '',
            value: ''
          })
        }
      } else {
        config.push({
          key: '',
          value: ''
        })
      }
      let dubboMethodParamtypes = [] as any
      if (props.row.botPluginMeta.dubboMethodParamtypes?.length) {
        activeNames.value = []
        props.row.botPluginMeta.dubboMethodParamtypes?.forEach((item, index) => {
          let paramInput = []
          if (Array.isArray(props.row.botPluginMeta.input[index])) {
            paramInput = props.row.botPluginMeta.input[index]
          } else if (typeof props.row.botPluginMeta.input[index] == 'object') {
            paramInput = [props.row.botPluginMeta.input[index]]
          }
          dubboMethodParamtypes.push({ type: item, input: paramInput })
          activeNames.value.push(index)
        })
      } else {
        dubboMethodParamtypes = [{ type: '', input: [initOutput()] }]
      }
      let resContentType
      if (props.row.meta) {
        resContentType = JSON.parse(props.row.meta).resContentType
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
          input: [...input],
          output,
          dubboServiceName: props.row.botPluginMeta.dubboServiceName,
          dubboServiceGroup: props.row.botPluginMeta.dubboServiceGroup,
          dubboServiceVersion: props.row.botPluginMeta.dubboServiceVersion,
          dubboMethodName: props.row.botPluginMeta.dubboMethodName,
          dubboMethodParamtypes: dubboMethodParamtypes,
          rpcContext: config
        },
        display: props.row.display,
        resContentType: resContentType
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
          input: [initOutput()],
          output: [initOutput()],
          dubboServiceName: undefined,
          dubboServiceGroup: undefined,
          dubboServiceVersion: undefined,
          dubboMethodName: undefined,
          dubboMethodParamtypes: [{ type: '', input: [] }],
          rpcContext: [
            {
              key: '',
              value: ''
            }
          ]
        },
        display: undefined,
        resContentType: undefined
      }
      rpcContextObj.value = {}
    }
  }
)

const handlePre = () => {
  if (state.active > 0) {
    state.active--
  }
}

const handleClick = () => {
  if (state.form.type === 'dubbo') {
    activeNames.value = []
    state.form.botPluginMeta.dubboMethodParamtypes.forEach(
      (item: { type: string; input: Array<any> }, index) => {
        activeNames.value.push(index)
      }
    )
  }
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
        if (state.form.botPluginMeta.output?.length) {
          output = state.form.botPluginMeta.output.filter((v: any) => v.name)
        }
        let params = {
          ...props.row,
          ...state.form
        }
        if (state.form.type === 'http') {
          if (state.form.botPluginMeta.input?.length) {
            input = state.form.botPluginMeta.input.filter((v: any) => v.name)
          }
          params.botPluginMeta = {
            desc: state.form.botPluginMeta.desc,
            http_method: state.form.botPluginMeta.http_method,
            http_headers: header,
            input,
            output
          }
        } else if (state.form.type === 'dubbo') {
          let dubboMethodParamtypes = [] as any
          state.form.botPluginMeta.dubboMethodParamtypes.forEach(
            (item: { type: string; input: Array<any> }, index) => {
              if (item.type) {
                dubboMethodParamtypes.push(item.type)
                input.push(item.input[0])
              }
            }
          )
          let rpcContextObj = {} as any
          state.form.botPluginMeta.rpcContext?.forEach((item: { key: string; value: string }) => {
            if (item.key || item.value) {
              rpcContextObj[item.key] = item.value
            }
          })
          params.botPluginMeta = {
            desc: state.form.botPluginMeta.desc,
            input,
            output,
            dubboServiceName: state.form.botPluginMeta.dubboServiceName,
            dubboServiceGroup: state.form.botPluginMeta.dubboServiceGroup,
            dubboServiceVersion: state.form.botPluginMeta.dubboServiceVersion,
            dubboMethodName: state.form.botPluginMeta.dubboMethodName,
            dubboMethodParamtypes: dubboMethodParamtypes,
            rpcContext: rpcContextObj
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
const addOneParam = () => {
  state.form.botPluginMeta.dubboMethodParamtypes.push({
    type: '',
    input: [initOutput()]
  })
}
const changeParams = (val, index) => {
  if (index == state.form.botPluginMeta.dubboMethodParamtypes.length - 1) {
    if (val.length > 0) {
      addOneParam()
    }
  }
}
const addParam = (index) => {
  state.form.botPluginMeta.dubboMethodParamtypes.splice(index + 1, 0, {
    type: '',
    input: [initOutput()]
  })
}
const delParam = (index) => {
  state.form.botPluginMeta.dubboMethodParamtypes.splice(index, 1)
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
<style lang="scss" scoped>
.input-box {
  display: flex;
}

.input-t {
  color: #333;
  text-align: right;
  padding: 0 12px 0 0;
  font-size: 14px;
}

.ouput-box {
  display: flex;
  margin-bottom: 10px;
}

.ouput-t {
  width: 100px;
  color: #333;
  text-align: right;
  padding: 0 12px 0 0;
  font-size: 14px;
}

.com-box {
  position: relative;
  flex: 1;

  :deep(.top-t) {
    background-color: #f5f7fa;
  }

  :deep(.var-label) {
    font-size: 12px;
    font-weight: 300;
  }
}

.add-btn {
  position: absolute;
  right: 5px;
  top: 2px;
}
:deep(.oz-collapse-item__content) {
  padding-bottom: 0px !important;
}
</style>

<style lang="scss" scoped>
.li-box {
  margin-bottom: 10px;
}
</style>
