<template>
	<div id="change-log-container" class="change-log-container">
		<div class="change-log-table">
			<el-table
				:data="logList"
				class="doc-table"
				header-cell-class-name="table-header-cell"
				style="width: 100%; font-size: 14px">
				<el-table-column
					show-overflow-tooltip
					prop="id"
					:width="100"
				>
					<template #header>
						<span class="common-table-title">{{$i18n.t('table.versionID')}}</span>
					</template>
					<template #default="scope">
						<div class="version-id">
							<span>{{scope.row.id || '--'}}</span>
							<var v-if="scope.row.isNow">{{$i18n.t('now')}}</var>
						</div>
					</template>
				</el-table-column>
				<el-table-column
					prop="updateUser"
					show-overflow-tooltip
				>
					<template #header>
						<span class="common-table-title">{{$i18n.t('table.changePerson')}}</span>
					</template>
					<template #default="scope">
						<span>{{scope.row.updateUser || '--'}}</span>
					</template>
				</el-table-column>
				<el-table-column
					prop="updateTime"
					show-overflow-tooltip
				>
				<template #header>
					<span class="common-table-title">{{$i18n.t('uptateTime')}}</span>
				</template>
				<template #default="scope">
					{{moment(new Date(scope.row.updateTime)).format('YYYY-MM-DD HH:mm:ss')}}
				</template>
				</el-table-column>
				<el-table-column
					prop="updateMsg"
					show-overflow-tooltip
				>
					<template #header>
						<span class="common-table-title">{{$i18n.t('table.changedesc')}}</span>
					</template>
				</el-table-column>
				<el-table-column
					fixed="right"
					width="100">
					<template #header>
						<span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
					</template>
					<template #default="scope">
						<el-button v-if="!scope.row.isNow" @click.stop="handleComp(scope.row)" text type="primary" size="small">{{$i18n.t('btnText.compared')}}</el-button>
						<el-button @click="handleRollback(scope.row)" v-if="!scope.row.isNow" text type="primary" size="small">{{$i18n.t('btnText.rollback')}}</el-button>
					</template>
				</el-table-column>
				<template #empty>
					<div>
						<Empty/>
					</div>
				</template>
			</el-table>
			<p v-if="logList.length" class="page-bar">
				<el-pagination
					background
					:pager-count="5"
					:page-size="pageSize"
					:current-page="pageNo"
					:page-sizes="[5, 10, 20, 50]"
					layout="prev, pager, next"
					@current-change="handleChangePage"
					:total="logCount">
				</el-pagination>
			</p>
		</div>
		<el-dialog
      :destroy-on-close="true"
      :center="false"
      width="740px"
			top="10vh"
			custom-class="compare-dialog"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :title="$i18n.t('versionDifference')"
      v-model="compareVisible"
      append-to-body
    >
      <CompareDialog :compareData="compareData"/>
			<template #footer>
				<div class="btns">
					<el-button @click="handleClose" size="medium">{{$i18n.t('btnText.cancel')}}</el-button>
				</div>
			</template>
    </el-dialog>
	</div>
</template>

<script>
import { getHistoryRecordList, compareWithOldVersion, getRollbackToHis } from '@/api/apilog'
import moment from 'moment'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import Empty from '@/components/Empty'
import CompareDialog from './CompareDialog.vue'
export default {
  name: 'ChangeLog',
  components: {
    Empty,
    CompareDialog
  },
  data () {
    return {
      logList: [],
      compareVisible: false,
      compareData: {},
      pageNo: 1,
      pageSize: 15,
      logCount: 0
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        this.init()
      },
      immediate: true
    }
  },
  computed: {
    moment () {
      return moment
    }
  },
  methods: {
		handleRollback (row) {
			getRollbackToHis({
          apiID: this.$utils.getQuery('apiID'),
          targetHisID: row.id
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
						this.$message({
							type: 'success',
							message: this.$i18n.t('submittedSuccessfully')
						})
          } else {
						this.$message({
							type: 'error',
							message: data.message
						})
					}
        }).catch(e => {})
		},
    handleChangePage (page) {
      this.pageNo = page
      this.init()
      document.querySelector('#change-log-container').scrollTo({ top: 0 })
    },
    init () {
      let apiID = this.$utils.getQuery('apiID')
      if (apiID) {
        getHistoryRecordList({
          apiID,
          pageNo: this.pageNo,
          pageSize: this.pageSize
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.logCount = data.data.total || 0
            this.logList = data.data.recordList || []
          }
        }).catch(e => {})
      } else {
        this.logList = []
        this.logCount = 0
      }
    },
    handleComp (row) {
      this.compareVisible = true
      compareWithOldVersion({
      	apiID: this.$utils.getQuery('apiID'),
      	recordID: row.id
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.compareData = {
            ...this.$utils.deepParse(data.data || '{}'),
            nowId: this.logList[0].id,
            preId: row.id
          }
        }
      }).catch(e => {})
    },
    handleClose () {
      this.compareVisible = false
    }
  }
}
</script>

<style scoped>
.change-log-container {
	padding: 18px 24px 0;
  height: 100%;
  overflow-y: auto;
}
.change-log-container::-webkit-scrollbar{
  display: none;
}
.change-log-container .change-log-table {
	padding: 20px;
	background: #fff;
}
.change-log-container >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border: 1px solid rgba(232, 232, 232, 1);
	border-right: none;
	border-left: none;
	height: 52px;
}
.change-log-container >>> .table-header-cell:first-child{
	border-left: 1px solid rgba(232, 232, 232, 1);
	border-top-left-radius: 4px;
}
.change-log-container >>> .table-header-cell:last-child{
	border-right: 1px solid rgba(232, 232, 232, 1);
	border-top-right-radius: 4px;
}
.change-log-container >>> .el-table td {
  padding: 11px 0;
}
.change-log-container .version-id {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.change-log-container .version-id var {
	padding: 0px 5px 0px 5px;
	color: #1890FF;
	font-size: 12px;
	line-height: 18px;
	border: 1px solid rgba(145, 213, 255, 1);
	border-radius: 2px;
	background: rgba(230, 247, 255, 1);
	font-style: normal;
	display: inline-block;
	margin-left: 4px;
}
.change-log-container .page-bar{
	padding-top: 8px;
	text-align: right;
}
</style>
