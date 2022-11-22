
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
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="scope.row.paramKey" autocomplete="off"></el-input>
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
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="scope.row.paramType" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="paramNotNull"
				:width="isEN ? '86px' : '52px'"
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
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="scope.row.paramValue" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="paramNote"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.instruction')}}</span>
        </template>
				<template #default="scope">
					<el-input :rows="1" type="textarea" :placeholder="$i18n.t('placeholder.enterDesc')" v-model="scope.row.paramNote" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				width="84px">
        <template #header>
          <span class="common-table-title">{{$i18n.t('addTo')}}</span>
        </template>
				<template #default="scope">
					<el-button @click="handleAddChildren(scope)" text type="primary" size="small">{{$i18n.t('addSubdata')}}</el-button>
				</template>
			</el-table-column>
			<el-table-column
				:width="isEN ? '86px' : '54px'"
				align="center">
        <template #header>
          <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
        </template>
				<template #default="scope">
          <el-button text type="primary" size="small" @click="handledelete(scope)">{{$i18n.t('btnText.delete')}}</el-button>
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
        paramType: DATA_TYPE_KEY.object,
        paramKey: '',
        paramValue: '',
        paramNote: '',
        random: 'root',
        childList: [{
          paramNotNull: false,
          paramType: '',
          paramKey: '',
          paramValue: '',
          paramNote: '',
          random: new Date().getTime(),
          childList: []
        }]
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
    init (initData) {
      initData = initData || this.formJsonData
      if (initData && initData.length) {
        let tableData = []
        tableData = initData.map((item, index) => {
          let obj = {
            childList: [],
            paramNotNull: !!item.paramNotNull,
            paramType: item.paramType,
            paramKey: item.paramKey,
            paramValue: item.paramValue,
            paramNote: item.paramNote,
            random: new Date().getTime() + (index + 1) * Math.random() * 10
          }
          if (Array.isArray(item.childList) && item.childList.length) {
            obj.childList = this.handleReserveValue(item.childList)
          }
          return obj
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
    handleReserveValue (arr) {
      let values = []
      for (let i = 0; i < arr.length; i++) {
        let t = new Date().getTime() + (i + 1) * Math.random() * 1000
        let obj = {
          paramNotNull: !!arr[i].paramNotNull,
          paramType: arr[i].paramType,
          paramKey: arr[i].paramKey,
          paramValue: arr[i].paramValue,
          paramNote: arr[i].paramNote,
          random: t,
          childList: []
        }
        if (Array.isArray(arr[i].childList) && arr[i].childList.length) {
          obj.childList = this.handleReserveValue(arr[i].childList)
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
          array.push({
            ...o,
            paramNotNull: !!o.paramNotNull,
            paramKey: item.childList[i].paramKey,
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
      this.handleDeep(this.tableData, scope.row.random)
    },
    handleDeep (arr, target) {
      for (let i = 0; i < arr.length; i++) {
        if (arr[i].random === target) {
          arr[i].childList.push({
            paramNotNull: false,
            paramType: '',
            paramKey: '',
            paramValue: '',
            paramNote: '',
            random: new Date().getTime() + 1,
            childList: []
          })
          break
        };
        if (arr[i].childList) {
          this.handleDeep(arr[i].childList, target)
        };
      }
    }
  },
  watch: {
    tableData: {
      handler: function (val, oldVal) {
        if (this.tableData.every(item => !!item.paramType && !!item.paramKey)) {
          this.tableData.push({
            paramNotNull: false,
            paramType: '',
            paramKey: '',
            paramValue: '',
            paramNote: '',
            random: new Date().getTime(),
            childList: []
          })
        }
        let arr = val.map(item => {
          let o = { ...item }
          delete o.random
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
