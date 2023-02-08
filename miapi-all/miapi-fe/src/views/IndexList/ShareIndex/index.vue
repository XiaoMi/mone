<template>
	<div class="share-index-dialog-container">
		<el-table
			ref="multipleTable"
			:data="indexGroupList"
			tooltip-effect="dark"
			height="327px"
			header-cell-class-name="table-header-cell"
			style="width: 100%"
			@selection-change="handleSelectionChange">
			<el-table-column
				type="selection"
				width="55">
			</el-table-column>
			<el-table-column
				show-overflow-tooltip
				prop="groupName"
				width="200">
				<template #header>
					<span class="common-table-title">{{$i18n.t('collectionName')}}</span>
				</template>
			</el-table-column>
			<el-table-column
				prop="groupDesc"
				show-overflow-tooltip>
				<template #header>
					<span class="common-table-title">{{$i18n.t('collectionDescription')}}</span>
				</template>
			</el-table-column>
		</el-table>
		<p class="btns">
			<el-button @click="handleCancel" plain>{{$i18n.t('btnText.cancel')}}</el-button>
			<el-button @click="handleOk" :disabled="!ids.length" type="primary">{{$i18n.t('btnText.createLink')}}</el-button>
		</p>
	</div>
</template>

<script>
import { mapGetters } from 'vuex'
import { PATH } from '@/router/constant'
export default {
  name: 'ShareIndexDialog',
  computed: {
    ...mapGetters([
      'indexGroupList'
    ])
  },
  data () {
    return {
      ids: []
    }
  },
  methods: {
    handleSelectionChange (arr) {
      let ids = []
      arr.forEach(v => {
        ids.push(v.groupID)
      })
      this.ids = ids
    },
    handleOk () {
      let url = `${window.location.origin}/#${PATH.SHARE_INDEX}?project=${this.$utils.getQuery('projectID')}&ids=${this.ids.join(',')}`
      let outer = `https://xxx/#${PATH.SHARE_OUTER_INDEX}?ids=${this.ids.join(',')}`
	  	this.$emit('onOk', { url, outer })
    //   this.handleCancel()
    },
    handleCancel () {
      this.$store.dispatch('apiindex/changeShareDialogBool', false)
    }
  }
}
</script>
<style scoped>
.share-index-dialog-container {
	padding: 10px 0 0;
}
.share-index-dialog-container >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border: 1px solid rgba(232, 232, 232, 1);
	border-right: none;
	border-left: none;
	height: 52px;
}
.share-index-dialog-container >>> .table-header-cell:first-child{
	border-left: 1px solid rgba(232, 232, 232, 1);
	border-top-left-radius: 4px;
	padding-left: 4px;
}
.share-index-dialog-container >>> .table-header-cell:last-child{
	border-right: 1px solid rgba(232, 232, 232, 1);
	border-top-right-radius: 4px;
}
.share-index-dialog-container >>> .el-table td {
  padding: 11px 0;
	height: 55px;
	font-size: 14px;
	color: rgba(0, 0, 0, 0.65);
}
.share-index-dialog-container .btns {
	padding: 20px 0 0;
	text-align: right;
}
</style>
