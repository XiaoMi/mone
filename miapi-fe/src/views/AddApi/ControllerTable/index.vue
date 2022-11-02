<template>
	<div class="controller-table">
		<div class="controller-table-title">
			<strong><span>{{$i18n.t('itemName')}}：{{projectDetail.projectName}}</span><span>{{$i18n.t('include')}} {{list.length || 0}}{{$i18n.t('items')}} {{$i18n.t('importable')}}{{isDubbo ? $i18n.t('serviceName'):'Controller'}}</span></strong>
			<el-checkbox v-if="onlyControler" v-model="isCurrent">{{$i18n.t('controllerTable.onlyImportCurrent')}}{{isDubbo ? $i18n.t('serviceName'):'Controller'}}{{$i18n.t('controllerTable.method')}}</el-checkbox>
		</div>
		<el-table
			ref="multipleTable"
			:data="tableList"
			tooltip-effect="dark"
			header-cell-class-name="table-header-cell"
			style="width: 100%"
			@selection-change="handleSelectionChange">
			<el-table-column
				type="selection"
				show-overflow-tooltip>
			</el-table-column>
			<el-table-column
				prop="moduleClassName">
        <template #header>
          <span class="common-table-title">{{isDubbo ? $i18n.t('importableServices') : $i18n.t('importableController')}}</span>
        </template>
				<template #default="scope">{{ scope.row.moduleClassName }}</template>
			</el-table-column>
			<el-table-column
				prop="selectedNum"
				width="140px">
        <template #header>
          <span class="common-table-title">{{isDubbo ? $i18n.t('importableDubboAPI') : $i18n.t('importableAPI')}}</span>
        </template>
				<template #default="scope"><span @click="handleShowApiList(scope.row)" style="color:#1890FF;cursor: pointer;">{{scope.row.selectedNum}}个</span></template>
			</el-table-column>
			<el-table-column
				prop="groupId"
				width="200px">
        <template #header>
          <span class="common-table-title">{{$i18n.t('APIClassification')}}</span>
        </template>
				<template #default="scope">
					<el-select v-model="scope.row.groupId" :placeholder="`${$i18n.t('placeholder.selectGroup')}`">
						<el-option v-for="v in groupList" :key="v.groupID" :label="v.groupName" :value="v.groupID"></el-option>
					</el-select>
				</template>
			</el-table-column>
      <template #empty>
        <Empty :imageSize="300" :description="$i18n.t('controllerTable.emptyText')"/>
      </template>
		</el-table>
		<el-dialog
			:destroy-on-close="true"
			:center="false"
			:show-close="false"
			:close-on-click-modal="false"
			:close-on-press-escape="false"
			:title="$i18n.t('chooseAPI')"
			v-model="dialogData.visible"
			width="640px"
			append-to-body
		>
			<SelectApiDialog :isDubbo="isDubbo" :controllers="dialogData.data" @onCancel="handleCancel" @onOk="handleOk"/>
		</el-dialog>
	</div>
</template>

<script>
import { reactive } from "vue"
import { mapGetters } from 'vuex'
import SelectApiDialog from './SelectApiDialog.vue'
import Empty from '@/components/Empty'
export default {
  name: 'ControllerTable',
  components: {
    SelectApiDialog,
    Empty
  },
  data () {
    return {
      isCurrent: true,
      tableList: [],
      multipleSelection: [],
      dialogData: {
        visible: false,
        data: {}
      }
    }
  },

  props: {
    list: {
      type: Array,
      default () {
        return []
      }
    },
    onlyControler: {
      type: Boolean,
      default: true
    },
    serviceName: {
      type: String,
      default: ''
    },
    isDubbo: {
      type: Boolean,
      default: false
    }
  },

  watch: {
    list: {
      handler (val) {
        let tableList = []
        if (val.length) {
        	tableList = val.map(v => {
            v.groupId = this.groupID || undefined;
            if (Array.isArray(v.apiList)) {
              v.selectedNum = v.apiList.length
              v.apiList = v.apiList.map(item => {
                item.checked = true;
                return item
              })
            } else {
              v.selectedNum = 0;
            }
            return v
          })
        } else {
          tableList = []
          this.isCurrent = true
        }
        this.tableList = reactive(tableList.filter(item => item.moduleClassName === this.serviceName))
        if (this.tableList.length) {
          this.$refs.multipleTable.toggleAllSelection()
        }
      },
      immediate: true
    },
    multipleSelection: {
      handler (val) {
        // console.log(val)
      }
    },
    isCurrent: {
      handler (val) {
        if (val) {
          this.tableList = this.list.filter(item => item.moduleClassName === this.serviceName)
        } else {
          this.tableList = this.list
        }
      },
      immediate: true
    }
  },

  computed: {
    ...mapGetters([
      'groupList',
      'projectDetail',
      'groupID'
    ])
  },

  methods: {
    toggleSelection (rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.multipleTable.toggleRowSelection(row)
        })
      } else {
        this.$refs.multipleTable.clearSelection()
      }
    },
    handleSelectionChange (val) {
      this.multipleSelection = val
    },
    handleShowApiList (row) {
      this.dialogData = {
        visible: true,
        data: row
      }
    },
    handleCancel () {
      this.dialogData = {
        visible: false,
        data: {}
      }
    },
    handleOk ({ serviceName, paths }) {
      this.tableList.forEach(item => {
        if (item.moduleClassName === serviceName) {
          item.selectedNum = paths.length
          item.apiList.forEach(v => {
            if (paths.includes(v.apiName)) {
              v.checked = true
            } else {
              v.checked = false
            }
          })
        }
      })
    },
    handleGetSelect () {
      return this.multipleSelection
    }
  }
}
</script>
<style scoped>
.controller-table{
  width: 100%;
}
.controller-table >>> .el-table{
	font-size: 14px !important;
}
.controller-table .controller-table-title {
	margin-bottom: 20px;
  display: flex;
}
.controller-table .controller-table-title strong {
	margin-right: 20px;
}
.controller-table .controller-table-title strong span:first-child {
	margin-right: 20px;
}
.controller-table >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border: 1px solid rgba(232, 232, 232, 1);
	border-right: none;
	border-left: none;
	padding-left: 3px;
}
.controller-table >>> .table-header-cell:first-child{
	border-left: 1px solid rgba(232, 232, 232, 1);
	border-top-left-radius: 4px;
}
.controller-table >>> .table-header-cell:last-child{
	border-right: 1px solid rgba(232, 232, 232, 1);
	border-top-right-radius: 4px;
}
.controller-table >>> .el-table__row {
	height: 43px;
}
.controller-table >>> .el-table__row td{
	padding-left: 3px;
}
.controller-table >>> .el-table__row td:nth-child(1) {
	padding-left: 0;
}
.controller-table .el-empty {
  padding: 0;
}
</style>
