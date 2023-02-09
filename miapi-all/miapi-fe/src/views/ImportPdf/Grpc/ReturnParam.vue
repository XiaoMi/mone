<template>
  <div class="d-detail-returns">
		<h4><el-icon><Reading /></el-icon>{{$i18n.t('returnParameter')}}</h4>
		<div class="d-detail-returns-content">
			<div>
				<el-table
					:data="tableData"
					border
					row-key="random"
					default-expand-all
					:tree-props="{children: 'childList', hasChildren: 'hasChildren'}"
					style="width: 100%; margin-top: 16px">
					<el-table-column
						fixed
						show-overflow-tooltip
						prop="paramKey"
					>
          <template #header>
            <span class="common-table-title">{{$i18n.t('table.name')}}</span>
          </template>
					<template #default="scope">
						<span>{{scope.row.paramKey || '--'}}</span>
					</template>
					</el-table-column>
					<el-table-column
						prop="paramType"
						show-overflow-tooltip
					>
          <template #header>
            <span class="common-table-title">{{$i18n.t('table.parameterType')}}</span>
          </template>
					<template #default="scope">
							<span>{{scope.row.paramType}}</span>
						</template>
					</el-table-column>
					<el-table-column
						prop="paramNotNull"
						show-overflow-tooltip
						width="55"
					>
          <template #header>
            <span class="common-table-title">{{$i18n.t('table.required')}}</span>
          </template>
					<template #default="scope">
						<span>{{scope.row.paramNotNull ? $i18n.t('yes'):$i18n.t('no')}}</span>
					</template>
					</el-table-column>
					<el-table-column
						prop="paramValue"
						show-overflow-tooltip
						width="140"
					>
          <template #header>
            <span class="common-table-title">{{$i18n.t('table.parameterValue')}}</span>
          </template>
					</el-table-column>
					<el-table-column
						prop="paramNote"
					>
          <template #header>
            <span class="common-table-title">{{$i18n.t('table.instruction')}}</span>
          </template>
					</el-table-column>
				</el-table>
      </div>
		</div>
  </div>
</template>
<script>

export default {
  name: 'ReturnParam',
  data () {
    return {
      tableData: []
    }
  },
  props: {
    apiInfo: {
      type: Object,
      default: () => {
        return {
          resultInfo: []
        }
      }
    }
  },
  watch: {
    'apiInfo': {
      handler (val) {
        if (val.resultInfo) {
          this.tableData = [val.resultInfo].map((item, index) => {
            let obj = {
              childList: [],
              paramValue: "",
              paramNote: "",
              ...item,
              random: new Date().getTime() + (index + 1) * Math.random() * 10
            }
            if (Array.isArray(item.childList) && item.childList.length) {
              obj.paramValue = ''
              obj.childList = this.handleReserveValue(item.childList)
            }
            return obj
          })
        } else {
          this.tableData = []
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleReserveValue (arr) {
      let values = []
      for (let i = 0; i < arr.length; i++) {
        let t = new Date().getTime() + (i + 1) * Math.random() * 1000
        let obj = {
          paramValue: "",
          paramNote: "",
          ...arr[i],
          random: t,
          childList: []
        }
        if (Array.isArray(arr[i].childList) && arr[i].childList.length) {
          obj.paramValue = ''
          obj.childList = this.handleReserveValue(arr[i].childList)
        }
        values.push(obj)
      }
      return values
    }
  }
}
</script>
<style scoped>
.d-detail-returns >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 20px;
}
.d-detail-returns .d-detail-returns-content {
	padding: 0 20px 20px;
}
.d-detail-returns .request-params-json{
	height: 300px;
}
.d-detail-returns .request-params-json >>> .jsoneditor {
	border-color: #e6e6e6;
}
.d-detail-returns .request-params-json >>> .ace_gutter {
	background: #f1f0f0;
}
</style>
