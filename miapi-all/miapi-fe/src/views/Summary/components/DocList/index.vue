<template>
	<div class="d-doc-list">
		<div>
			<div class="doc-list-header">
				<!-- <h4>项目文档</h4> -->
				<el-button @click="editDialog = true" type="primary">{{$i18n.t('addDocument')}}</el-button>
			</div>
      <!-- @row-click="handleClickRow" -->
			<el-table
				:data="docList"
				class="doc-table"
				header-cell-class-name="table-header-cell"
				style="width: 100%; font-size: 14px">
				<el-table-column
					show-overflow-tooltip
					prop="title"
				>
          <template #header>
            <span class="common-table-title">{{$i18n.t('name')}}</span>
          </template>
				</el-table-column>
				<el-table-column
					prop="createUserName"
					show-overflow-tooltip
				>
          <template #header>
            <span class="common-table-title">{{$i18n.t('creator')}}</span>
          </template>
        	<template #default="scope">
						<span>{{scope.row.createUserName || '--'}}</span>
					</template>
				</el-table-column>
				<el-table-column
					prop="updateTime"
					show-overflow-tooltip
				>
          <template #header>
            <span class="common-table-title">{{$i18n.t('uptateTime')}}</span>
          </template>
				</el-table-column>
				<el-table-column
					prop="contentType"
					show-overflow-tooltip
				>
          <template #header>
            <span class="common-table-title">{{$i18n.t('documentType')}}</span>
          </template>
					<template #default="scope">
						<span>{{scope.row.contentType === 0 ? $i18n.t('richText') : 'Markdown'}}</span>
					</template>
				</el-table-column>
				<el-table-column
					fixed="right"
					width="140">
          <template #header>
            <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
          </template>
					<template #default="scope">
						<el-button @click.stop="handleModify(scope.row)" text type="primary" size="small">{{$i18n.t('edit')}}</el-button>
						<el-button @click.stop="handleDel(scope.row)" text type="primary" size="small">{{$i18n.t('delete')}}</el-button>
						<el-button @click.stop="handlePreview(scope.row)" text type="primary" size="small">{{$i18n.t('btnText.preview')}}</el-button>
					</template>
				</el-table-column>
        <template #empty>
          <div>
            <Empty/>
          </div>
        </template>
			</el-table>
		</div>
    <!-- <el-dialog
      fullscreen
      class="dialog-watermarked"
      :modal="false"
      :destroy-on-close="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      v-model="showDetail"
      append-to-body
    >
    <div v-if="showDetail">
      <DocDetail @changeShowDetail="closeDetailDialog" :row="row"/>
    </div>
    </el-dialog> -->
    <el-dialog
      custom-class="editor-dialog"
      class="dialog-watermarked"
      v-model="editDialog"
      :modal="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      :destroy-on-close="true"
      fullscreen>
      <EditDialog :documentID="documentID" @onClose="editDialog = false"/>
    </el-dialog>
	</div>
</template>

<script>
import DocDetail from './DocDetail'
import { getAllDocumentList, deleteDocuments } from '@/api/projectdoc'
import { mapGetters } from 'vuex'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import Empty from '@/components/Empty'
import { PATH } from '@/router/constant'
import drawWaterMark from '@/common/waterMark'
import EditDialog from "./EditDialog"

export default {
  name: 'DocList',
  components: {
    DocDetail,
    Empty,
    EditDialog
  },
  data () {
    return {
      showDetail: false,
      row: {},
      documentID: '',
      editDialog: false
    }
  },
  computed: {
    ...mapGetters([
      'docList',
      'projectDetail',
      'selfUserInfo'
    ]),
    projectID () {
      return this.$utils.getQuery('projectID')
    }
  },
  watch: {
    editDialog: {
      handler (val) {
        if (!val) {
        	this.init()
          this.documentID = ''
        }
      }
    },
    'selfUserInfo.name': {
      handler (val) {
        if (val) {
          const option = {
            content: val,
            className: 'dialog-watermarked'
          }
          drawWaterMark(option)
        }
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      getAllDocumentList({ projectID: this.projectID }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$store.dispatch('projectdoc/changeDocList', data.data || [])
          this.$nextTick(() => {
            let editDocId = this.$utils.getQuery('editDocID')
            if (editDocId && data.data && data.data.length && data.data.some(v => v.documentID === Number(editDocId))) {
              this.handleModify(data.data.filter(v => v.documentID === Number(editDocId))[0])
              window.location.hash = `#${PATH.SUMMARY}?projectID=${this.$utils.getQuery('projectID')}`
            }
          })
        }
      }).catch(e => {})
    },
    handleModify (row) {
      this.documentID = row.documentID
      this.editDialog = true
    },
    handlePreview (row) {
      let url = `${window.location.origin}/#${PATH.SHARE_DOC}?documentID=${row.documentID}&projectName=${this.projectDetail.projectName}`
      window.open(url)
    },
    handleClickRow (row) {
      this.row = { ...row, projectName: this.projectDetail.projectName }
      this.showDetail = !this.showDetail
    },
    closeDetailDialog () {
      this.showDetail = !this.showDetail
    },
    showList () {
      this.showDetail = false
    },
    handleDel (row) {
      this.$confirm(`${this.$i18n.t('deleteDocument.first')} ${row.title}, ${this.$i18n.t('deleteDocument.sec')}`, this.$i18n.t('prompt'), {
        confirmButtonText: this.$i18n.t('btnText.ok'),
        cancelButtonText: this.$i18n.t('btnText.cancel'),
        type: 'warning'
      }).then(() => deleteDocuments({ projectID: this.projectID, documentIDs: JSON.stringify([row.documentID]) })).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message({
            type: 'success',
            message: this.$i18n.t('successDeleted')
          })
          this.init()
        }
      }).catch((e) => {})
    }
  }
}
</script>

<style scoped>
.d-doc-list {
	padding: 18px 24px 0;
  height: 100%;
  overflow-y: auto;
}
.d-doc-list::-webkit-scrollbar{
  display: none;
}
.d-doc-list .doc-list-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 18px;
}
.d-doc-list >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border: 1px solid rgba(232, 232, 232, 1);
	border-right: none;
	border-left: none;
	height: 52px;
}
.d-doc-list >>> .table-header-cell:first-child{
	border-left: 1px solid rgba(232, 232, 232, 1);
	border-top-left-radius: 4px;
}
.d-doc-list >>> .table-header-cell:last-child{
	border-right: 1px solid rgba(232, 232, 232, 1);
	border-top-right-radius: 4px;
}
.d-doc-list >>> .el-table td {
  padding: 11px 0;
}
.d-doc-list >>> .el-dialog.editor-dialog {
  padding: 0;
	background-color: rgb(245, 245, 245);
}
.d-doc-list >>> .el-dialog.editor-dialog .el-dialog__header {
  display: none;
}
.d-doc-list >>> .el-dialog.editor-dialog {
  overflow: hidden;
}
.d-doc-list >>> .el-dialog.editor-dialog .el-dialog__body {
  padding-bottom: 0 !important;
}
</style>
