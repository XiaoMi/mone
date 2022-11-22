<template>
	<div class="error-code-container">
		<h4><el-icon><Reading /></el-icon>{{$i18n.t('detailList.errorCodeExplanation')}}</h4>
		<div class="error-code-table">
			<el-table
				:data="tableData"
				border
				header-cell-class-name="table-header-cell"
				style="width: 100%">
				<el-table-column
					fixed
					prop="errorCodeName"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.errorCode')}}</span>
        </template>
				<template #default="scope">
					<span>{{scope.row.errorCodeName || '--'}}</span>
				</template>
				</el-table-column>
				<el-table-column
					prop="errorDesc"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.wrongInformation')}}</span>
        </template>
				</el-table-column>
				<el-table-column
					prop="plan"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.solution')}}</span>
        </template>
				</el-table-column>
			</el-table>
		</div>
	</div>
</template>

<script>
export default {
  name: 'ErrorCode',
  data () {
    return {
      tableData: []
    }
  },
  props: {
    apiErrorCodes: {
      type: Array,
      default () {
        return []
      }
    }
  },
  watch: {
    apiErrorCodes: {
      handler (val) {
        this.tableData = val || []
      },
      immediate: true,
      deep: true
    }
  },
}
</script>

<style scoped>
.error-code-container >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	height: 20px;
}
.error-code-container .error-code-table {
	padding: 10px 20px 20px;
}
</style>
