<template>
  <div class="debug">
    <span class="title">{{ t('plugin.inputParams') }}</span>
  </div>
  <el-form :model="state.form" label-position="top" class="debug-form">
    <el-form-item v-if="state.form.type === 'http'">
      <el-table :data="state.form.params" style="width: 100%" border>
        <el-table-column prop="name" :label="t('plugin.parameter')" width="240">
          <template #default="scope">
            <el-input
              :autofocus="false"
              v-model="scope.row.name"
              :placeholder="t('plugin.pleaseEnter')"
            />
          </template>
        </el-table-column>
        <el-table-column prop="value" :label="t('plugin.parameterVal')">
          <template #default="scope">
            <el-input
              :autofocus="false"
              v-model="scope.row.value"
              :placeholder="t('plugin.pleaseEnter')"
            />
          </template>
        </el-table-column>
        <el-table-column :label="t('plugin.operate')" width="84" align="center">
          <template #default="scope">
            <el-button
              type="primary"
              text
              size="small"
              class="table-cell-text-btn"
              :disabled="!scope.row.name && !scope.row.value"
              @click="handleDel(scope)"
              >{{ t('common.delete') }}</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-form-item>
    <el-form-item v-else-if="state.form.type === 'dubbo'">
      <el-table :data="state.form.params" style="width: 100%" border>
        <el-table-column prop="name" label="参数列表" width="240" class-name="dubbo-item">
          <template #default="scope">
            <el-input
              :autofocus="false"
              v-model="scope.row.name"
              :placeholder="t('plugin.pleaseEnter')"
              disabled
            />
          </template>
        </el-table-column>
        <el-table-column prop="value" :label="t('plugin.parameterVal')">
          <template #default="scope">
            <el-input
              type="textarea"
              :autosize="{ minRows: 4 ,maxRows: 8 }"
              :autofocus="false"
              v-model="scope.row.value"
              :placeholder="t('plugin.pleaseEnter')"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-form-item>
  </el-form>
  <div class="debug">
    <span class="title">{{ t('plugin.debuggingResults') }}</span>
    <el-button type="primary" size="small" @click="handleClick">{{ t('plugin.debug') }}</el-button>
  </div>
  <div>
    <Codemirror
      v-model:value="state.detail"
      :placeholder="t('plugin.debuggingResults')"
      :options="{ ...cmOptions, readOnly: true, theme: 'juejin' }"
      :border="true"
      :height="320"
    />
  </div>
</template>

<script lang="ts" setup>
import { reactive, watch } from 'vue'
import { getBot, testHttpDebug, testDubboDebug } from '@/api/plugins'
import { ElMessage } from 'element-plus'
import { t } from '@/locales'
import CodemirrorFn from '@/components/codeMirror'
import Codemirror from 'codemirror-editor-vue3'
import 'codemirror/theme/juejin.css'

let { cmOptions } = CodemirrorFn()

const emits = defineEmits(['onOk'])

const props = defineProps({
  data: {
    type: Object,
    default: () => ({})
  }
})

const state = reactive({
  form: {
    params: [] as any,
    type: ''
  },
  detail: ''
})

watch(
  () => state.form.params,
  (val) => {
    if (val.every((v: any) => v.name && v.value)) {
      // state.form.params.push({
      //   name: undefined,
      //   value: undefined
      // })
    }
  },
  {
    deep: true
  }
)

watch(
  () => props.data.id,
  (val) => {
    console.log('val',val)
    if (val) {
      state.detail = ''
      getBot(props.data.id)
        .then((data) => {
          state.form.type = data?.data?.type || ''
          if (data.data.meta) {
            try {
              let mate = JSON.parse(data.data.meta)
              if (state.form.type == 'http') {
                if (mate.input?.length) {
                  let arr = []
                  mate.input.forEach((v: any) => {
                    arr.push({
                      name: v.name,
                      value: undefined
                    })
                  })
                  state.form.params = arr
                } else {
                  state.form.params = [
                    {
                      name: undefined,
                      value: undefined
                    }
                  ]
                }
              } else if (state.form.type === 'dubbo') {
                //dubbo
                state.form.rpcContext = mate.rpcContext
                if (mate.dubboMethodParamtypes?.length) {
                  let arr = []
                  mate.dubboMethodParamtypes.forEach((v: any) => {
                    arr.push({
                      name: v,
                      value: undefined
                    })
                  })
                  state.form.params = arr
                } else {
                  state.form.params = [
                    {
                      name: undefined,
                      value: undefined
                    }
                  ]
                }
              }
            } catch (e) {
              state.form.params = [
                {
                  name: undefined,
                  value: undefined
                }
              ]
            }
          } else {
            ElMessage.error(t('common.wrong'))
          }
          console.log(data)
        })
        .catch((e) => {
          console.log(e)
        })
    }
  },
  {
    immediate: true
  }
)

const handleDel = (scope) => {
  state.form.params = state.form.params.filter((_, i) => i !== scope.$index)
}

const handleClick = () => {
  let params = {} as any
  if (state.form.type === 'http') {
    let input = {} as any
    state.form.params
      .filter((v) => v.name && v.value)
      .forEach((v) => {
        input[v.name] = v.value
      })
    params = {
      pluginId: props.data.id,
      input
    }
    testHttpDebug(params)
      .then((data) => {
        if (data.data) {
          try {
            state.detail = JSON.stringify(JSON.parse(data.data), null, 2)
          } catch (e) {
            state.detail = data.data
          }
        } else {
          ElMessage.error(data.message!)
        }
      })
      .catch((e) => {
        console.log(e)
      })
      .finally(() => {
        emits('onOk')
      })
  } else {
    let input = [] as any
    state.form.params
      .filter((v) => v.name && v.value)
      .forEach((v) => {
        try {
          input.push(JSON.parse(v.value))
        } catch (e) {
          input.push(v.value)
        }
      })
    params = {
      pluginId: props.data.id,
      params: input,
      rpcContext: state.form.rpcContext || {}
    }
    testDubboDebug(params)
      .then((data) => {
        if (data.data) {
          try {
            state.detail = JSON.stringify(data.data, null, 2)
          } catch (e) {
            state.detail = data.data
          }
        } else {
          ElMessage.error(data.message!)
        }
      })
      .catch((e) => {
        console.log(e)
      })
      .finally(() => {
        emits('onOk')
      })
  }
}
</script>
<style lang="scss" scoped>
.debug {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  .title {
    color: #333;
    font-size: 14px;
  }
}
.debug-form {
  &:deep(.oz-form-item) {
    margin-bottom: 18px !important;
  }
}
.oz-table {
  &:deep(tr) {
    th.oz-table__cell {
      background-color: #f5f7fa;
      padding: 4px 0;
      color: #333;
      font-weight: normal;
      font-size: 12px;
    }
    .dubbo-item {
      vertical-align: top;
    }
  }
}
</style>
