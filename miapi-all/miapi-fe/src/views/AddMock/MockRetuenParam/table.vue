
<template>
  <div class="form-json">
    <el-table
			:data="tableData"
			border
			class="form-json-table"
			header-cell-class-name="table-header-cell"
			row-key="random"
      :indent="24"
			default-expand-all
			:tree-props="{children: 'childList', hasChildren: 'hasChildren'}"
			style="width: 100%">
			<el-table-column
				prop="paramKey"
				width="220">
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.name')}}</span>
        </template>
				<template #default="scope">
					<el-input :disabled="handleKeyDisabled(scope.row)" :placeholder="$i18n.t('placeholder.pleaseEnterParameterName')" v-model="scope.row.paramKey" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="paramType"
				width="140px"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterType')}}</span>
        </template>
				<template #default="scope">
					<el-select
						v-model="scope.row.paramType"
						@change="changeParamType($event, scope)"
						:placeholder="$i18n.t('placeholder.pleaseChoose')">
						<el-option
							v-for="(name, value) in dataType"
							:key="value"
              :disabled="handleDisabledOption(scope.row, value)"
							:label="name"
							:value="value">
						</el-option>
					</el-select>
				</template>
			</el-table-column>
			<el-table-column
				prop="paramValue"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterValue')}}</span>
        </template>
				<template #default="scope">
					<el-input :disabled="!disabledChildrenTree(scope)" :placeholder="$i18n.t('placeholder.pleaseEnterParameterValue')" v-model="scope.row.paramValue" autocomplete="off"></el-input>
				</template>
			</el-table-column>
      <el-table-column
				prop="rule"
				>
        <template #header>
          <span class="rule-title">{{$i18n.t('table.generationRules')}}<a target="_blank" href="http://mockjs.com/examples.html"><el-icon :size="14"><Link /></el-icon></a></span>
        </template>
				<template #default="scope">
					<el-input :disabled="disabledRule(scope)" :placeholder="$i18n.t('placeholder.pleaseEnterParameterValueRule')" v-model="scope.row.rule" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				width="84px">
        <template #header>
          <span class="common-table-title">{{$i18n.t('addTo')}}</span>
        </template>
				<template #default="scope">
					<el-button :disabled="disabledChildrenTree(scope) || scope.row.isRoot" @click="handleAddChildren(scope)" text type="primary" size="small">{{$i18n.t('addSubdata')}}</el-button>
				</template>
			</el-table-column>
			<el-table-column
				:width="isEN ? '90px' : '54px'"
				align="center">
        <template #header>
          <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
        </template>
				<template #default="scope">
          <el-button :disabled="scope.row.isRoot" text type="primary" size="small" @click="handledelete(scope)">{{$i18n.t('btnText.delete')}}</el-button>
				</template>
			</el-table-column>
		</el-table>
  </div>
</template>
<script>
import { DATA_TYPE, DATA_TYPE_KEY } from '@/views/constant'

export default {
  name: 'FormJson',
  data () {
    return {
      isInit: true,
      isEN: false,
      tableData: [{
        paramNotNull: false,
        paramType: DATA_TYPE_KEY.object,
        paramKey: 'root',
        paramValue: '',
        paramNote: '',
        rule: '',
        default: 0,
        random: 'root',
        childList: [{
          paramNotNull: false,
          paramType: '',
          paramKey: '',
          paramValue: '',
          paramNote: '',
          rule: '',
          default: 0,
          random: new Date().getTime(),
          childList: [],
          parentType: DATA_TYPE_KEY.object
        }],
        parentType: '',
        isRoot: true
      }]
    }
  },
  props: {
    formJsonData: {
      type: Array,
      default () {
        return []
      }
    }
  },
  computed: {
    dataType () {
      return DATA_TYPE
    }
  },
  methods: {
    handleKeyDisabled (row) {
      if (row.parentType === DATA_TYPE_KEY.array || row.isRoot) {
        return true
      } else if (row.parentType === DATA_TYPE_KEY.object || row.parentType === DATA_TYPE_KEY.json || row.parentType === '') {
        return false
      }
      return row.paramType !== DATA_TYPE_KEY.json && row.paramType !== DATA_TYPE_KEY.object && row.paramType !== DATA_TYPE_KEY.array
    },
    init () {
      if (this.formJsonData && this.formJsonData.length) {
        let tableData = [{
          paramNotNull: false,
          paramType: this.formJsonData.length === 1 ? DATA_TYPE_KEY.array : DATA_TYPE_KEY.object,
          paramKey: 'root',
          paramValue: '',
          paramNote: '',
          rule: '',
          default: 0,
          random: 'root',
          childList: [],
          parentType: '',
          isRoot: true
        }]
        let childList = this.formJsonData
        if (this.formJsonData[0].paramKey === 'root') {
          tableData = [{
            paramNotNull: false,
            paramType: this.formJsonData[0].paramType,
            paramKey: 'root',
            paramValue: this.formJsonData[0].paramValue,
            paramNote: this.formJsonData[0].paramNote,
            rule: this.formJsonData[0].rule,
            default: this.formJsonData[0].default,
            random: 'root',
            childList: [],
            parentType: '',
            isRoot: true
          }]
          childList = this.formJsonData[0].childList
        }
        tableData[0].childList = childList.map((item, index) => {
          let obj = {
            childList: [],
            rule: '',
            ...item,
            random: new Date().getTime() + (index + 1) * Math.random() * 10,
            parentType: this.formJsonData[0].paramType
          }
          if (Array.isArray(item.childList) && item.childList.length) {
            obj.paramValue = ''
            obj.childList = this.handleReserveValue(item.childList, item.paramType)
          }
          return obj
        })
        if (
          tableData[0].childList.every(item => !!item.paramType) &&
          ((tableData[0].paramType === DATA_TYPE_KEY.object) || (tableData[0].paramType === DATA_TYPE_KEY.json))
        ) {
          tableData[0].childList.push({
            paramNotNull: false,
            paramType: '',
            paramKey: '',
            paramValue: '',
            paramNote: '',
            rule: '',
            default: 0,
            random: new Date().getTime(),
            childList: [],
            parentType: tableData[0].paramType
          })
        }
        this.tableData = tableData
      }
    },
    handledelete (scope) {
      this.tableData = this.handleDeleteDeep(this.tableData, scope.row.random)
    },
    handleDeleteDeep (arr, target) {
      let array = [].concat(arr)
      for (let i = 0; i < array.length; i++) {
        if (array[i].random === target) {
          array.splice(i, 1)
          break
        };
        if (array[i].childList && array[i].childList.length) {
          array[i].childList = this.handleDeleteDeep(array[i].childList, target)
        };
      }
      return array
    },
    handleReserveValue (arr, parentType) {
      let values = []
      for (let i = 0; i < arr.length; i++) {
        let t = new Date().getTime() + (i + 1) * Math.random() * 1000
        let obj = {
          rule: '',
          ...arr[i],
          random: t,
          childList: [],
          parentType
        }
        if (Array.isArray(arr[i].childList) && arr[i].childList.length) {
          obj.paramValue = ''
          obj.childList = this.handleReserveValue(arr[i].childList, arr[i].paramType)
        }
        values.push(obj)
      }
      return values
    },
    handleValue (item) {
      if (item.childList && item.childList.length) {
        let array = []
        for (let i = 0; i < item.childList.length; i++) {
          let o = { ...item.childList[i] }
          delete o.random
          delete o.parentType
          array.push({
            ...o,
            paramNotNull: !!o.paramNotNull,
            paramKey: this.handleKeyDisabled(item.childList[i]) ? '' : item.childList[i].paramKey,
            paramValue: o.childList.length ? '' : o.paramValue,
            childList: this.handleValue(item.childList[i])
          })
        }
        return array
      } else {
        return []
      }
    },
    handleAddChildren (scope) {
      this.handleDeep(this.tableData, scope.row.random, scope.row.paramType)
    },
    handleDeep (arr, target, parentType) {
      for (let i = 0; i < arr.length; i++) {
        if (arr[i].random === target) {
          arr[i].childList.push({
            paramNotNull: false,
            paramType: '',
            paramKey: '',
            paramValue: '',
            paramNote: '',
            rule: '',
            default: 0,
            random: new Date().getTime() + 1,
            childList: [],
            parentType
          })
          break
        };
        if (arr[i].childList) {
          this.handleDeep(arr[i].childList, target, parentType)
        };
      }
    },
    disabledChildrenTree (scope) {
      return !scope.row.paramType || (scope.row.paramType !== DATA_TYPE_KEY.json && scope.row.paramType !== DATA_TYPE_KEY.array && scope.row.paramType !== DATA_TYPE_KEY.object)
    },
    disabledRule (scope) {
      if (scope.row.isRoot) {
        return (scope.row.paramType === DATA_TYPE_KEY.json || scope.row.paramType === DATA_TYPE_KEY.array || scope.row.paramType === DATA_TYPE_KEY.object)
      }
      return false
    },
    disabledNote (scope) {
      return scope.row.isRoot && (scope.row.paramType === DATA_TYPE_KEY.json || scope.row.paramType === DATA_TYPE_KEY.array || scope.row.paramType === DATA_TYPE_KEY.object)
    },
    changeParamType (val, scope) {
      this.tableData = this.clearJsonAndArray(this.tableData, scope.row.random, val, scope.row.isRoot)
    },
    clearJsonAndArray (arr, random, currentType, isRoot) {
      let newArr = [...arr]
      if (isRoot && ((currentType === DATA_TYPE_KEY.json) || (currentType === DATA_TYPE_KEY.object) || (currentType === DATA_TYPE_KEY.array))) {
        return [{ ...newArr[0], paramValue: '', rule: '', childList: [] }]
      } else if (isRoot && ((currentType !== DATA_TYPE_KEY.json) && (currentType !== DATA_TYPE_KEY.object) && (currentType !== DATA_TYPE_KEY.array))) {
        return [{ ...newArr[0], childList: [] }]
      }
      newArr = newArr.map((item) => {
        if (item.random === random) {
          return {
            ...item,
            paramValue: "",
            paramNote: "",
            // paramKey: ((currentType === DATA_TYPE_KEY.json) || (currentType === DATA_TYPE_KEY.object) || (currentType === DATA_TYPE_KEY.array)) ? item.paramKey : undefined,
            childList: []
          }
        } else if (item.childList && item.childList.length) {
          return {
            ...item,
            childList: this.clearJsonAndArray(item.childList, random, currentType)
          }
        }
        return item
      })
      return newArr
    },
    handleDisabledOption (row, value) {
      return false
      // return row.isRoot && (value !== DATA_TYPE_KEY.json && value !== DATA_TYPE_KEY.object && value !== DATA_TYPE_KEY.array)
    }
  },
  watch: {
    tableData: {
      handler: function (val, oldVal) {
        if (val[0].isRoot && (val[0].paramType !== DATA_TYPE_KEY.array && val[0].paramType !== DATA_TYPE_KEY.json && val[0].paramType !== DATA_TYPE_KEY.object)) {
          // do something
        } else if (val[0].isRoot && val[0].paramType === DATA_TYPE_KEY.array && !val[0].childList.length) {
          this.tableData[0].childList.push({
            paramNotNull: false,
            paramType: '',
            paramKey: '',
            paramValue: '',
            paramNote: '',
            rule: '',
            default: 0,
            random: new Date().getTime(),
            childList: [],
            parentType: this.tableData[0].paramType
          })
        } else if (this.tableData[0].childList.every(item => !!item.paramType) && !(val[0].isRoot && val[0].paramType === DATA_TYPE_KEY.array)) {
          this.tableData[0].childList.push({
            paramNotNull: false,
            paramType: '',
            paramKey: '',
            paramValue: '',
            paramNote: '',
            rule: '',
            default: 0,
            random: new Date().getTime(),
            childList: [],
            parentType: this.tableData[0].paramType
          })
        }
        let arr = val.map(item => {
          let o = { ...item }
          delete o.random
          delete o.parentType
          delete o.isRoot
          return {
            ...o,
            paramNotNull: !!o.paramNotNull,
            paramValue: o.childList.length ? '' : o.paramValue,
            childList: this.handleValue(item)
          }
        })
        this.isInit = false
        this.$emit('changeFormJsonData', arr)
      },
      deep: true
    },
    formJsonData: {
      handler (_, old) {
        if ((!old || !old.length) && this.isInit) {
          this.init()
        }
      },
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
    this.init()
  }
}
</script>
<style scoped>
.form-json h4{
	font-weight: normal;
	margin: 0 0 10px;
}
.form-json .form-json-table >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 50px;
}
.form-json .icon-modify {
	cursor: pointer;
	color: #1890FF;
	font-size: 14px;
}
.form-json .form-json-table >>> .cell {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.form-json .form-json-table .rule-title {
  color: rgba(0, 0, 0, 0.64);
  font-size: 14px;
}
.form-json .form-json-table .rule-title a {
  padding: 0 0 0 4px;
  display: inline-block;
  vertical-align: middle;
}
</style>
