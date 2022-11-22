<template>
	<div class="request-query">
		<div class="import-btn">
			<!-- <el-button @click="handleOpenImport" class="btn" type="text">导入</el-button> -->
		</div>
		<el-table
			ref="multipleQueryTable"
			:data="tableData"
			tooltip-effect="dark"
			style="width: 100%"
			border
      @select="handleSelect"
			@select-all="handleSelectAll">
			<el-table-column
				type="selection"
        align='center'
        :selectable="handleSelectable"
				width="55">
			</el-table-column>
			<el-table-column
				width="260">
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.name')}}</span>
        </template>
				<template #default="scope"><el-input :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="scope.row.paramKey"/></template>
			</el-table-column>
			<el-table-column
				prop="paramValue">
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterValue')}}</span>
        </template>
				<template #default="scope"><el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="scope.row.paramValue"/></template>
			</el-table-column>
			<el-table-column
        width="90"
        align="center">
        <template #header>
          <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
        </template>
				<template #default="scope"><el-button @click="handledelete(scope)" text type="primary">{{$i18n.t('btnText.delete')}}</el-button></template>
			</el-table-column>
		</el-table>
    <el-dialog
      :destroy-on-close="true"
      :center="false"
      width="640px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      v-model="importVisible"
      append-to-body
    >
      <template #title>
        <div>
          导入Query参数 <el-popover placement="bottom-start" trigger="hover">
            <div>
              如: name=xxx&age=1<br/>
              或 {"name": "xxx", "age": 1}
            </div>
            <template #reference>
              <el-icon color="#1890FF" :size="14"><QuestionFilled /></el-icon>
            </template>
          </el-popover>
        </div>
      </template>
      <ApiTestImportDialog type="query" @onCancel="handleCloseImport" @onOk="handleImport"/>
    </el-dialog>
	</div>
</template>

<script>
import ApiTestImportDialog from './ImportDialog'
import { mapGetters } from 'vuex'

export default {
  name: 'RequestQuery',
  components: {
    ApiTestImportDialog
  },
  data () {
    return {
      tableData: [],
      multipleSelection: [],
      importVisible: false,
      isFromStore: false
    }
  },
  computed: {
    ...mapGetters([
      'apiTestQuery',
      'isTestRequestEnd'
    ])
  },
  watch: {
    'tableData': {
      handler: function (val, oldVal) {
        if (this.tableData.every(item => !!item.paramKey)) {
          this.tableData.push({
            paramKey: '',
            paramValue: ''
          })
        }
        this.multipleSelection = this.multipleSelection.filter(v => v.paramKey && v.paramValue)
        this.$nextTick(() => {
          val.forEach(v => {
            for (let i = 0; i < this.multipleSelection.length; i++) {
              let item = this.multipleSelection[i]
              if (v.paramKey === item.paramKey && v.paramValue === item.paramValue) {
                this.$refs.multipleQueryTable && this.$refs.multipleQueryTable.toggleRowSelection(v, true)
                break
              } else {
                this.$refs.multipleQueryTable && this.$refs.multipleQueryTable.toggleRowSelection(v, false)
              }
            }
          })
        })
      },
      immediate: true,
      deep: true
    },
    multipleSelection: {
      handler (val) {
        if (!this.isFromStore) {
    	    this.$store.dispatch('apitest/changeApiTestTarget', { query: val })
        } else {
          this.isFromStore = false
        }
      },
      deep: true
    },
    isTestRequestEnd: {
      handler () {
        this.init()
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    init () {
      if (this.apiTestQuery && Array.isArray(this.apiTestQuery)) {
        this.isFromStore = true
        this.tableData = [...this.apiTestQuery]
        this.multipleSelection = [...this.apiTestQuery].filter(v => v.paramKey && v.paramValue)
      }
    },
    handleSelectable (row) {
      return row.paramKey && row.paramValue
    },
    handleSelect (val) {
      this.multipleSelection = val.filter(v => v.paramKey && v.paramValue)
    },
    handleSelectAll (val) {
      this.multipleSelection = val.filter(v => v.paramKey && v.paramValue)
    },
    handledelete (scope) {
      if (this.tableData.length > 1) {
        this.multipleSelection = this.multipleSelection.filter(item => !(item.paramValue === scope.row.paramValue && item.paramKey === scope.row.paramKey))
        this.tableData = this.tableData.filter(item => !(item.paramValue === scope.row.paramValue && item.paramKey === scope.row.paramKey))
      }
    },
    handleDisabledKey (key) {
      return this.tableData.some(item => item.paramKey === key)
    },
    handleOpenImport () {
      this.importVisible = true
    },
    handleCloseImport () {
      this.importVisible = false
    },
    handleImport (arg) {
      if (Object.prototype.toString.call(arg) === '[object Object]') {
        this.tableData = Object.keys(arg).map((key) => {
          return {
            paramKey: key,
            paramValue: JSON.stringify(arg[key])
          }
        })
      }
    }
  }
}
</script>
<style scoped>
.request-query{
  padding: 0 20px;
}
.request-query .import-btn {
	height: 50px;
	line-height: 50px;
	border-top: none;
	border-bottom: none;
}
.request-query .import-btn .btn {
	font-size: 12px;
	color: rgba(0, 0, 0, 0.65);
	font-weight: normal;
}
.request-query >>> .el-table .el-table__header th {
  background: rgba(250, 250, 250, 1);
}
.request-query >>> .el-table td, .request-query >>> .el-table th.is-leaf{
	border-color: #e6e6e6;
	height: 50px;
}
.request-query >>> .el-table .cell{
  padding-left: 16px;
  padding-right: 16px;
}
</style>
