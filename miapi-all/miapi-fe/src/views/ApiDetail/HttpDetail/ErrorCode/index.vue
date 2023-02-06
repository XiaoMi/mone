<template>
	<div class="error-code-container">
		<h4>{{$i18n.t('detailList.errorCodeExplanation')}}</h4>
		<div class="error-code-table">
			<EditErrorCode :defaultApiErrorCodes="tableData" @onChange="handleChangeCode" v-if="isEditDetail"/>
			<el-table
				v-else
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
import { mapGetters } from 'vuex'
import EditErrorCode from './EditErrorCode'
import debounce from '@/common/debounce'
export default {
  name: 'ErrorCode',
  components: {
    EditErrorCode
  },
  data () {
    return {
      tableData: [{
        errorCodeName: '错误码',
        errorDesc: '错误消错',
        plan: '解决方案'
      }, {
        errorCodeName: '错误码',
        errorDesc: '错误消息',
        plan: '解决方案'
      }]
    }
  },
  computed: {
    ...mapGetters([
      'isEditDetail'
    ])
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
  methods: {
    handleChangeCode: debounce(function (val) {
      this.$emit('onChange', val)
    }, 300),
  }
}
</script>

<style scoped>
.error-code-container >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	height: 50px;
}
.error-code-container .error-code-table {
	padding: 20px;
}
</style>
