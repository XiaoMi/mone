
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
					<el-input :disabled="handleKeyDisabled(scope.row)" :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="scope.row.paramKey" autocomplete="off"></el-input>
				</template>
			</el-table-column>
      <el-table-column
        prop="paramName"
        show-overflow-tooltip
      >
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterName')}}</span>
        </template>
				<template #default="scope">
					<el-input :disabled="scope.row.isRoot" :placeholder="$i18n.t('placeholder.pleaseEnterParameterName')" v-model="scope.row.paramName" autocomplete="off"></el-input>
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
						:placeholder="$i18n.t('placeholder.pleaseChooseType')">
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
				prop="paramNotNull"
				:width="isEN ? '86px' : '54px'"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.required')}}</span>
        </template>
				<template #default="scope">
          <div style="width: 100%; text-align: center">
					  <el-checkbox v-model="scope.row.paramNotNull"></el-checkbox>
          </div>
				</template>
			</el-table-column>
			<el-table-column
				prop="paramValue"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterValue')}}</span>
        </template>
				<template #default="scope">
					<el-input :disabled="!disabledChildrenTree(scope)" :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="scope.row.paramValue" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="paramNote"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.instruction')}}</span>
        </template>
				<template #default="scope">
					<el-input :disabled="disabledNote(scope)" :rows="1" type="textarea" :placeholder="$i18n.t('placeholder.enterDesc')" v-model="scope.row.paramNote" autocomplete="off"></el-input>
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
				:width="isEN ? '86px' : '54px'"
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
import { DATA_TYPE, DATA_TYPE_KEY, PROTOCOL_TYPE } from '@/views/constant'

export default {
  name: 'FormJson',
  data () {
    return {
      isEN: false,
      isInit: true,
      tableData: [{
        paramNotNull: false,
        paramType: DATA_TYPE_KEY.object,
        paramKey: 'root',
        paramValue: '',
        paramName: "",
        paramNote: '',
        default: 0,
        random: 'root',
        childList: [{
          paramNotNull: false,
          paramType: '',
          paramKey: '',
          paramName: "",
          paramValue: '',
          paramNote: '',
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
    },
    apiProtocol: {
      type: String,
      default: ''
    },
    routingType: {
      type: Number,
      default: 0
    }
  },
  computed: {
    dataType () {
      return DATA_TYPE
    },
    isGatewayDubbo () {
      return this.apiProtocol === PROTOCOL_TYPE.Gateway && (this.routingType === 1 || this.routingType === 4)
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
    init (initData) {
      initData = initData || this.formJsonData
      if (initData && initData.length) {
        let tableData = [{
          paramNotNull: false,
          paramType: initData.length === 1 ? DATA_TYPE_KEY.array : DATA_TYPE_KEY.object,
          paramKey: 'root',
          paramName: "",
          paramValue: '',
          paramNote: '',
          default: 0,
          random: 'root',
          childList: [],
          parentType: '',
          isRoot: true
        }]
        let childList = initData
        if (initData[0].paramKey === 'root') {
          tableData = [{
            paramNotNull: false,
            paramType: initData[0].paramType,
            paramKey: 'root',
            paramName: "",
            paramValue: initData[0].paramValue,
            paramNote: initData[0].paramNote,
            default: initData[0].default,
            random: 'root',
            childList: [],
            parentType: '',
            isRoot: true
          }]
          childList = initData[0].childList
        }
        tableData[0].childList = childList.map((item, index) => {
          let obj = {
            childList: [],
            ...item,
            random: new Date().getTime() + (index + 1) * Math.random() * 10,
            parentType: initData[0].paramType
          }
          if (Array.isArray(item.childList) && item.childList.length) {
            obj.paramValue = ''
            obj.childList = this.handleReserveValue(item.childList, item.paramType)
          }
          return obj
        })
        if (
          tableData[0].childList.every(item => !!item.paramType) &&
          (this.isGatewayDubbo || (tableData[0].paramType === DATA_TYPE_KEY.object) || (tableData[0].paramType === DATA_TYPE_KEY.json))
        ) {
          tableData[0].childList.push({
            paramNotNull: false,
            paramType: '',
            paramKey: '',
            paramName: "",
            paramValue: '',
            paramNote: '',
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
            paramName: "",
            paramValue: '',
            paramNote: '',
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
    disabledNote (scope) {
      return scope.row.isRoot && (scope.row.paramType === DATA_TYPE_KEY.json || scope.row.paramType === DATA_TYPE_KEY.array || scope.row.paramType === DATA_TYPE_KEY.object)
    },
    changeParamType (val, scope) {
      this.tableData = this.clearJsonAndArray(this.tableData, scope.row.random, val, scope.row.isRoot)
    },
    clearJsonAndArray (arr, random, currentType, isRoot) {
      let newArr = [...arr]
      if (isRoot && ((currentType !== DATA_TYPE_KEY.json) && (currentType !== DATA_TYPE_KEY.object) && (currentType !== DATA_TYPE_KEY.array))) {
        return [{ ...newArr[0], childList: [] }]
      } else if (isRoot) {
        newArr[0].paramValue = ''
        newArr[0].paramNote = ''
      }
      newArr = newArr.map((item) => {
        if (item.random === random) {
          return {
            ...item,
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
            paramName: "",
            paramValue: '',
            paramNote: '',
            default: 0,
            random: new Date().getTime(),
            childList: [],
            parentType: this.tableData[0].paramType
          })
        } else if (this.tableData[0].childList.every(item => !!item.paramType) && (!(val[0].isRoot && val[0].paramType === DATA_TYPE_KEY.array) || this.isGatewayDubbo)) {
          this.tableData[0].childList.push({
            paramNotNull: false,
            paramType: '',
            paramKey: '',
            paramName: "",
            paramValue: '',
            paramNote: '',
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
</style>
