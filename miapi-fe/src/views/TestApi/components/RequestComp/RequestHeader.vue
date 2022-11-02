<template>
	<div class="request-header">
		<div class="import-btn">
			<el-button @click="handleToggle" class="btn" text type="primary"><img alt=".png" :src="getImg" />{{!isShow ? `${this.apiTestDefaultHeader.length} ${$i18n.t('apiTest.hidden')}` : $i18n.t('apiTest.hideAutomaticallyHeader')}}</el-button>
			<!-- <el-button @click="handleOpenImport" class="btn" type="text">导入</el-button> -->
		</div>
		<el-table
			ref="multipleTable"
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
          <span class="common-table-title">{{$i18n.t('label')}}</span>
        </template>
				<template #default="scope">
          <el-select
						allow-create
						filterable
						default-first-option
						v-model="scope.row.paramKey"
						:placeholder="$i18n.t('placeholder.pleaseChoose')">
						<el-option
							v-for="item in headers"
              :disabled="handleDisabledKey(item)"
							:key="item"
							:label="item"
							:value="item">
						</el-option>
					</el-select>
        </template>
			</el-table-column>
			<el-table-column
				prop="paramValue">
        <template #header>
          <span class="common-table-title">{{$i18n.t('apiTest.content')}}</span>
        </template>
				<template #default="scope"><el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="scope.row.paramValue"/></template>
			</el-table-column>
			<el-table-column
        width="90px"
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
        导入请求头部 <el-popover placement="bottom-start" trigger="hover">
          <div>
            如: {"Content-Type": "multipart/form-data; boundary=something"}
          </div>
          <template #reference>
            <el-icon color="#1890FF" :size="14"><QuestionFilled /></el-icon>
          </template>
        </el-popover>
      </div>
    </template>
      <ApiTestImportDialog type="header" @onCancel="handleCloseImport" @onOk="handleImport"/>
    </el-dialog>
	</div>
</template>

<script>
import { HEADER } from '@/views/constant'
import ApiTestImportDialog from './ImportDialog'
import Look from './images/look.png'
import UnLook from './images/unlook.png'
import { mapGetters } from 'vuex'

export default {
  name: 'RequestHeader',
  components: {
    ApiTestImportDialog
  },
  data () {
    return {
      tableData: [],
      multipleSelection: [],
      importVisible: false,
      isShow: true,
      isFromStore: false
    }
  },
  computed: {
    ...mapGetters([
      'apiTestHeaders',
      'isTestRequestEnd',
      'requestEnvHeader',
      'apiTestDefaultHeader'
    ]),
    headers () {
      return HEADER
    },
    getImg () {
      return this.isShow ? UnLook : Look
    }
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
        let multipleSelection = this.multipleSelection.filter(v => v.paramKey && v.paramValue)
        this.$nextTick(() => {
          val.forEach(v => {
            for (let i = 0; i < multipleSelection.length; i++) {
              let item = multipleSelection[i]
              if (v.paramKey === item.paramKey && v.paramValue === item.paramValue) {
                this.$refs.multipleTable && this.$refs.multipleTable.toggleRowSelection(v, true)
                break
              } else {
                this.$refs.multipleTable && this.$refs.multipleTable.toggleRowSelection(v, false)
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
          let defaultVal = val.filter(v => v.default)
          let reqHeaders = val.filter(v => v.isRequest)
          if (!this.isShow) {
            defaultVal = this.apiTestDefaultHeader
          }
    	    this.$store.dispatch('apitest/changeApiTestTarget', { requestEnvHeader: reqHeaders, headers: val.filter(v => !v.default && !v.isRequest), defaultHeader: defaultVal })
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
    },
    isShow: {
      handler (bool) {
        if (bool) {
          let headers = [...this.apiTestDefaultHeader, ...this.requestEnvHeader, ...this.apiTestHeaders]
          this.tableData = [...headers]
          this.multipleSelection = [...headers].filter(v => v.paramKey && v.paramValue)
        } else {
          let headers = [...this.apiTestHeaders, ...this.requestEnvHeader]
          this.tableData = [...headers]
          this.multipleSelection = [...headers].filter(v => v.paramKey && v.paramValue)
        }
      },
      deep: true
    }
  },
  methods: {
    init () {
      if (this.apiTestHeaders && Array.isArray(this.apiTestHeaders)) {
        this.isFromStore = true
        let headers = [...this.apiTestDefaultHeader, ...this.requestEnvHeader, ...this.apiTestHeaders]
        this.tableData = headers
        this.multipleSelection = headers.filter(v => v.paramKey && v.paramValue)
      }
    },
    handleSelectable (row) {
      return row.paramKey && row.paramValue
    },
    handleToggle () {
      this.isShow = !this.isShow
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
    handleImport (obj) {
      this.tableData = Object.keys(obj).map(key => {
        return {
          paramKey: key,
          paramValue: JSON.stringify(obj[key])
        }
      })
    }
  }
}
</script>
<style scoped>
.request-header{
  padding: 0 20px;
}
.request-header .import-btn {
	height: 50px;
	line-height: 50px;
	border-top: none;
	border-bottom: none;
}
.request-header .import-btn img {
  width: 18px;
  height: 18px;
  vertical-align: -4px;
  margin-right: 2px
}
.request-header .import-btn .btn {
	font-size: 12px;
	color: rgba(0, 0, 0, 0.65);
	font-weight: normal;
}
.request-header >>> .el-table .el-table__header th {
  background: rgba(250, 250, 250, 1);
}
.request-header >>> .el-table td, .request-header >>> .el-table th.is-leaf{
	border-color: #e6e6e6;
	height: 50px;
}
.request-header >>> .el-table .cell{
  padding-left: 16px;
  padding-right: 16px;
}
</style>
