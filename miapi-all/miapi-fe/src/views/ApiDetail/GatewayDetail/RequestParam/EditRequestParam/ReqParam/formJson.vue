
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
					<el-input :disabled="disabledValue(scope)" :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="scope.row.paramValue" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="paramNote"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.instruction')}}</span>
        </template>
				<template #default="scope">
					<el-input :disabled="scope.row.isRoot" :rows="1" type="textarea" :placeholder="$i18n.t('placeholder.enterDesc')" v-model="scope.row.paramNote" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				width="84px">
        <template #header>
          <span class="common-table-title">{{$i18n.t('addTo')}}</span>
        </template>
				<template #default="scope">
					<el-button :disabled="disabledChildrenTree(scope)" @click="handleAddChildren(scope)" text type="primary" size="small">{{$i18n.t('addSubdata')}}</el-button>
				</template>
			</el-table-column>
			<el-table-column
				:width="isEN ? '86px' : '54px'"
				align="center">
        <template #header>
          <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
        </template>
				<template #default="scope">
          <el-button :disabled="scope.row.paramKey === 'rootArr'" text type="primary" size="small" @click="handledelete(scope)">{{$i18n.t('btnText.delete')}}</el-button>
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
      isEN: false,
      isInit: true,
      tableData: [{
        paramNotNull: false,
        paramType: DATA_TYPE_KEY.dubboArray,
        paramKey: 'rootArr',
        paramValue: '',
        paramNote: '',
        default: 0,
        random: 'rootArr',
        childList: [{
          paramNotNull: false,
          paramType: '',
          paramKey: 'root',
          paramValue: '',
          paramNote: '',
          default: 0,
          random: new Date().getTime(),
          childList: [],
          parentType: DATA_TYPE_KEY.dubboArray,
          isRoot: true
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
      return {
        ...DATA_TYPE,
        '15': '[dubboArray]'
      }
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
          paramNotNull: true,
          paramType: DATA_TYPE_KEY.dubboArray,
          paramKey: 'rootArr',
          paramValue: '',
          paramNote: '',
          default: 0,
          random: 'rootArr',
          childList: [],
          parentType: '',
          isRoot: true
        }]
        let childList = initData
        if (initData[0].paramKey === 'rootArr') {
          childList = initData[0].childList
        }
        tableData[0].childList = childList.map((item, index) => {
          let obj = {
            paramNotNull: item.paramNotNull,
            paramType: item.paramType,
            paramKey: 'root',
            paramValue: item.paramValue,
            paramNote: item.paramNote,
            default: item.default,
            random: new Date().getTime() + index,
            childList: [],
            parentType: DATA_TYPE_KEY.dubboArray,
            isRoot: true
          }
          if (Array.isArray(item.childList) && item.childList.length) {
            obj.paramValue = ''
            obj.childList = this.handleReserveValue(item.childList, item.paramType)
          }
          return obj
        })
        tableData[0].childList.push({
          paramNotNull: false,
          paramType: '',
          paramKey: 'root',
          paramValue: '',
          paramNote: '',
          default: 0,
          random: new Date().getTime() + Math.random(),
          childList: [],
          parentType: DATA_TYPE_KEY.dubboArray,
          isRoot: true
        })
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
          if (o.paramKey === 'root' && !o.paramType) {
            continue
          }
          delete o.random
          delete o.parentType
          delete o.isRoot
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
    disabledValue (scope) {
      if (scope.row.paramKey === 'rootArr') {
        return true
      }
      return scope.row.paramType === DATA_TYPE_KEY.dubboArray || scope.row.paramType === DATA_TYPE_KEY.json || scope.row.paramType === DATA_TYPE_KEY.array || scope.row.paramType === DATA_TYPE_KEY.object
    },
    disabledChildrenTree (scope) {
      return scope.row.paramKey === 'rootArr' || !(scope.row.paramType === DATA_TYPE_KEY.json || scope.row.paramType === DATA_TYPE_KEY.array || scope.row.paramType === DATA_TYPE_KEY.object || scope.row.paramType === DATA_TYPE_KEY.dubboArray)
    },
    changeParamType (val, scope) {
      this.tableData = this.clearJsonAndArray(this.tableData, scope.row.random, val, scope.row.isRoot)
    },
    clearJsonAndArray (arr, random, currentType, isRoot) {
      let newArr = [...arr]
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
      if (row.paramKey === 'rootArr') {
        return value !== DATA_TYPE_KEY.dubboArray
      }
      return value === DATA_TYPE_KEY.dubboArray
    }
  },
  watch: {
    tableData: {
      handler: function (val, oldVal) {
        if (this.tableData[0].childList.every(item => !!item.paramType)) {
          this.tableData[0].childList.push({
            paramNotNull: false,
            paramType: '',
            paramKey: 'root',
            paramValue: '',
            paramNote: '',
            default: 0,
            random: new Date().getTime(),
            childList: [],
            parentType: DATA_TYPE_KEY.dubboArray,
            isRoot: true
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
.form-json .form-json-table >>> textarea::placeholder{
  font-size: 13px;
}
</style>
