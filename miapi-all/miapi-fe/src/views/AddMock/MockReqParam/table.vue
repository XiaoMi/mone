<template>
	<div class="mock-req-param">
		<el-table
			:data="tableData"
			border
			row-key="random"
			header-cell-class-name="table-header-cell"
			default-expand-all
			:tree-props="{children: 'children', hasChildren: 'hasChildren'}"
			style="width: 100%">
			<el-table-column
				prop="paramKey"
				class-name="table-paramKey"
				width="180">
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.name')}}</span>
        </template>
				<template #default="scope">
					<el-input :disabled="isDisabled" :placeholder="$i18n.t('placeholder.pleaseEnterParameterName')" v-model="scope.row.paramKey" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="paramValue"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterValue')}}</span>
        </template>
				<template #default="scope">
					<el-input :disabled="isDisabled" :placeholder="$i18n.t('placeholder.pleaseEnterParameterValue')" v-model="scope.row.paramValue" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				:width="isEN ? '90px' : '68px'">
        <template #header>
          <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
        </template>
				<template #default="scope">
          <el-button :disabled="isDisabled" @click="handledelete(scope)" text type="primary" size="small">{{$i18n.t('btnText.delete')}}</el-button>
				</template>
			</el-table-column>
		</el-table>
	</div>
</template>
<script>

import { DATA_TYPE } from '@/views/constant'

export default {
  name: 'MockReqTable',
  data () {
    return {
      isDisabled: false,
      isEN: false,
      tableData: [{
        // paramType: '',
        paramKey: '',
        paramValue: '',
        random: new Date().getTime(),
        // children: [],
        level: 1
      }]
    }
  },
  computed: {
    dataType () {
      return DATA_TYPE
    },
    mockData () {
      return this.$store.getters.mockData
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
  },
  watch: {
    tableData: {
      handler: function (val, oldVal) {
        if (this.tableData.every(item => !!item.paramKey)) {
          this.tableData.push({
            // paramType: '',
            paramKey: '',
            paramValue: '',
            random: new Date().getTime(),
            // children: [],
            level: 1
          })
        }
        // let obj = {}
        // val.forEach(item => {
        //   if (item.paramKey && (item.paramValue || (item.children && item.children.length))) {
        //     obj[item.paramKey] = this.handleValue(item)
        //   }
        // })
        let arr = val.map(item => {
          return {
            paramKey: item.paramKey,
            paramValue: item.paramValue
          }
        })
        this.$store.dispatch('addmock/changeAddMockData', {
          paramsJson: arr
        })
      },
      deep: true
    },
    "mockData.requestTime": {
      handler (val) {
        this.isDisabled = this.mockData.isDefault
        let paramsJson = this.mockData.paramsJson
        try {
          paramsJson = JSON.parse(this.mockData.paramsJson)
        } catch (error) {}
        if (Array.isArray(paramsJson)) {
          this.tableData = paramsJson
        } else if (paramsJson && Object.keys(paramsJson).length) {
          let tableData = Object.keys(paramsJson).map((key, index) => {
            let obj = {
              // paramType: '',
              paramKey: key,
              paramValue: paramsJson[key],
              random: new Date().getTime() + (index + 1) * Math.random() * 10,
              // children: [],
              level: 1
            }
            // if (Object.prototype.toString.call(paramsJson[key]) === '[object Object]') {
            //   obj.paramValue = ''
            //   obj.children = this.handleReserveValue(paramsJson[key], 1)
            // }
            return obj
          })
          tableData.push({
            // paramType: '',
            paramKey: '',
            paramValue: '',
            random: new Date().getTime(),
            // children: [],
            level: 1
          })
          this.tableData = tableData
        }
      }
    }
  },
  methods: {
    // changeParamType(val, scope){
    // 	if (val !== '2' && val !== '12' && val !== '13') {
    // 		this.tableData = this.clearJsonAndArray(this.tableData, scope.row.random)
    // 	}
    // },
    // clearJsonAndArray(arr, random){
    // 	let newArr = [...arr]
    // 	newArr = newArr.map((item)=>{
    // 		if (item.random === random) {
    // 			return {
    // 				...item,
    // 				children: []
    // 			}
    // 		} else if (item.children && item.children.length) {
    // 			return {
    // 				...item,
    // 				children: this.clearJsonAndArray(item.children, random)
    // 			}
    // 		}
    // 		return item
    // 	})
    // 	return newArr
    // },
    disabledChildrenTree (scope) {
      // return (scope.row.level >= 3) || !scope.row.paramKey || !scope.row.paramType || (scope.row.paramType !== '2' && scope.row.paramType !== '12' && scope.row.paramType !== '13')
      return (scope.row.level >= 3) || !scope.row.paramKey
    },
    handleAddChildren (scope) {
      this.handleDeep(this.tableData, scope.row.random, scope.row.level)
    },
    handleDeep (arr, target, level) {
      level++
      for (let i = 0; i < arr.length; i++) {
        if (arr[i].random === target) {
          arr[i].children.push({
            // paramType: '',
            paramKey: '',
            paramValue: '',
            random: new Date().getTime() + 1,
            // children: [],
            level
          })
          break
        };
        if (arr[i].children) {
          this.handleDeep(arr[i].children, target, level)
        };
      }
    },
    handleValue (val) {
      if (val.children && val.children.length) {
        let obj = {}
        val.children.forEach(item => {
          if (item.paramKey && (item.paramValue || (item.children && item.children.length))) {
            obj[item.paramKey] = this.handleValue(item)
          }
        })
        return obj
      } else {
        return val.paramValue
      }
    },
    handleReserveValue (obj, level) {
      let values = []
      level++
      Object.keys(obj).forEach((key, i) => {
        let t = new Date().getTime() + (i + 1) * Math.random() * 1000
        let o = {
          // paramType: '',
          paramKey: key,
          paramValue: obj[key],
          random: t,
          // children: [],
          level
        }
        if (Object.prototype.toString.call(obj[key]) === '[object Object]') {
          o.paramValue = ''
          o.children = this.handleReserveValue(obj[key], level)
        }
        values.push(o)
      })
      return values
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
        if (array[i].children && array[i].children.length) {
          array[i].children = this.handleDeleteDeep(array[i].children, target)
        };
      }
      return array
    }
  }
}
</script>
<style scoped>
.mock-req-param >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 50px;
}
.mock-req-param .icon-modify {
	cursor: pointer;
	color: #1890FF;
	font-size: 14px;
}
.mock-req-param .active_btn{
	background: #646669;
	border-color: #1890FF;
	color: #FFF;
}
.mock-req-param >>> .cell {
	display: flex;
	align-items: center;
	justify-content: flex-start;
	padding: 0 16px !important;
}
</style>
