<!--
  Copyright 2020 Xiaomi

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  -->

<template>
  <div>
    <el-transfer
      :titles="['可选用filter', '已启用filter']"
      :value="selectedList"
      @input="onInput"
      :data="data"
    />
    <el-form :model="form" ref="commonFilterForm" label-width="120px">
      <div v-for="item of filterAllParams" :key="item.id">
        <div v-if="item.params.length" class="filter-param">
          <div class="filter-param-header">{{item.filterName}}</div>
          <template v-for="param of item.params">
            <div v-if="param.isArray" :key="param.paramName">
              <div
                v-for="(aFormItem, index) in form[item.id][param.paramName]"
                :key="index"
              >
                <template v-if="param.options && param.options.length">
                  <el-form-item :label="`${param.labelName}${index}`">
                    <div class="opt-icons">
                      <el-button
                        @click="removeNewItem(form[item.id][param.paramName], index)"
                        :disabled="form[item.id][param.paramName].length <= 1"
                        size="mini"
                        type="primary"
                        icon="el-icon-minus"
                        circle/>
                    </div>
                  </el-form-item>
                  <div class="array-form-item">
                    <div class="array-form-item-content">
                      <el-form-item
                        v-for="(aParamItem) in param.options"
                        :key="aParamItem.paramName"
                        :label="`${aParamItem.labelName}`"
                        :prop="`${item.id}.${param.paramName}.${index}.${aParamItem.paramName}`"
                        :rules="genFormItemRules(aParamItem)"
                      >
                        <form-item
                          v-model="form[item.id][param.paramName][index][aParamItem.paramName]"
                          :param="aParamItem"
                        />
                      </el-form-item>
                    </div>
                  </div>
                </template>
                <template v-else>
                  <el-form-item
                    :label="`${param.labelName}${index}`"
                    :prop="`${item.id}.${param.paramName}.${index}`"
                    :rules="genFormItemRules(param)"
                  >
                    <div class="array-form-item">
                      <form-item
                        v-model="form[item.id][param.paramName][index]"
                        :param="param"
                      />
                      <div class="array-form-item-btn">
                        <el-button
                          @click="removeNewItem(form[item.id][param.paramName], index)"
                          :disabled="form[item.id][param.paramName].length <= 1"
                          size="mini"
                          type="primary"
                          icon="el-icon-minus"
                          circle/>
                      </div>
                    </div>
                  </el-form-item>
                </template>
              </div>
              <div class="opt-icons">
                  <el-button
                    @click="addNewItem(form[item.id][param.paramName], param)"
                    size="mini"
                    type="primary"
                    icon="el-icon-plus"
                    circle></el-button>
                </div>
            </div>
            <el-form-item
              v-else
              :key="param.paramName"
              :prop="`${item.id}.${param.paramName}`"
              :label="param.labelName"
              :rules="genFormItemRules(param)"
            >
              <form-item
                v-model="form[item.id][param.paramName]"
                :param="param"
              />
            </el-form-item>
          </template>
        </div>
      </div>
    </el-form>
  </div>
</template>
<script>
import request from '@/plugin/axios/index'
import FormItem from './form-item'

const checkJson = function (rule, value, callback) {
  try {
    JSON.parse(value)
    callback()
  } catch (e) {
    callback(new Error('不是合法json'))
  }
}

export default {
  name: 'common-filter',
  components: {
    FormItem
  },
  props: {
    value: {
      type: Array,
      default: () => []
    }
  },
  computed: {
    selectedList () {
      const value = this.value
      return value.map(item => item.id)
    },
    filterAllParams () {
      const data = this.data
      const selectedList = this.selectedList
      const newfilterAllParams = []
      for (const id of selectedList) {
        const item = data.find(mitem => {
          return mitem.key === id
        })
        if (item) {
          newfilterAllParams.push({
            id,
            filterName: `${item.label}`,
            params: item.params
          })
        }
      }
      return newfilterAllParams
    }
  },
  data () {
    return {
      data: [],
      form: {}
    }
  },
  created () {
    this.otherInfo = {}
    this.getList()
  },
  methods: {
    genFormItemRules (param) {
      const rules = [{required: param.required || false, message: '必填字段', trigger: 'blur'}]
      if (Array.isArray(param.rules)) {
        for (const rule of param.rules) {
          if (rule.requiredJson) {
            rules.push({
              validator: checkJson,
              trigger: 'blur'
            })
          } else {
            rules.push(rule)
          }
        }
      }
      return rules
    },
    onInput (value) {
      const form = this.form
      const otherInfo = this.otherInfo
      const newSelectedList = []
      for (const id of value) {
        newSelectedList.push({
          id,
          name: otherInfo[id].name,
          stringifyParams: otherInfo[id].stringifyParams,
          params: form[id]
        })
      }
      this.$emit('input', newSelectedList)
    },
    getList () {
      const url = `/filter/effect/list`
      request({ url }).then(filterList => {
        if (Array.isArray(filterList)) {
          // 过滤不在审核通过列表中的filter, 之前通过后有删除
          const value = this.value
          const newValue = []
          for (const item of value) {
            const filter = filterList.find(filter => filter.id === item.id)
            if (filter) newValue.push(item)
          }
          this.data = filterList.map(item => {
            const defaultValue = newValue.find(iVal => iVal.id === item.id)
            let params = []
            try {
              params = JSON.parse(item.params || '[]')
            } catch (e) {
              params = []
            }
            this.form[item.id] = {}
            if (defaultValue && defaultValue.params) {
              this.form[item.id] = defaultValue.params
            } else {
              for (const param of params) {
                let defaultValue = param.defaultValue
                if (defaultValue != null
                  && typeof defaultValue == 'object') {
                  defaultValue = { ...defaultValue }
                }
                if (this.isArray(param) && param.options && param.options.length) {
                  defaultValue = typeof defaultValue == 'object' ? defaultValue : {}
                } else {
                  defaultValue = defaultValue != null ? defaultValue : ''
                }
                this.form[item.id][param.paramName] = this.isArray(param) ? [defaultValue] : defaultValue
              }
            }
            this.otherInfo[item.id] = {
              name: item.name,
              stringifyParams: params.filter(item => this.isArray(item)).map(item => item.paramName) // params中需要调用stringify的字段
            }
            return {
              key: item.id,
              label: item.cname || item.name,
              params: params,
              disabled: !!item.isSystem
            }
          })
          // 触发更新value
          if (value.length && value.length !== newValue.length) { this.$emit('input', newValue) }
        }
      })
    },
    isArray(item) {
      return item.isArray || item.type == 'checkbox';
    },
    addNewItem (arr, param) {
      const defaultValue = param.defaultValue || null
      const item = (param.options && param.options.length) ? { ...defaultValue } : (defaultValue || '')
      arr.push(item)
    },
    removeNewItem (arr, index) {
      if (index != null) {
        arr.splice(index, 1)
      } else {
        arr.pop()
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.filter-param {
  margin-top: 15px;
  padding: 15px;
  border: 1px dashed #ccc;
  &-header {
    font-size: 15px;
    font-weight: bolder;
  }
}
.el-form-item .el-form-item {
  margin-bottom: 22px;
}
.array-form-item {
  display: flex;
  
  &-content {
    flex: 1;
  }

  &-btn {
    flex: 0;
    margin-left: 5px;
  } 
}
.opt-icons {
  display: flex;
  margin-bottom: 22px;
  justify-content: flex-end;
  font-size: 20px;

  .opt-icon {
    margin-left: 5px;
  }
}
</style>
