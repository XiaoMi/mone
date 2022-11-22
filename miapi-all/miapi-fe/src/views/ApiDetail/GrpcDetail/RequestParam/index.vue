<template>
  <div class="d-detail-headers">
		<h4>{{$i18n.t('requestParameter')}}</h4>
		<div class="d-detail-headers-content">
			<EditRequestParam v-if="isEditDetail"/>
			<div v-else>
				<el-table
					:data="tableData"
					border
					row-key="random"
					default-expand-all
					:tree-props="{children: 'childList', hasChildren: 'hasChildren'}"
					style="width: 100%;margin-top: 16px">
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
import EditRequestParam from './EditRequestParam'
import { DATA_TYPE } from '@/views/constant'
import { mapGetters } from 'vuex'

export default {
  name: 'RequestParam',
  components: {
    EditRequestParam
  },
  data () {
    return {
      tableData: []
    }
  },
  watch: {
    'apiInfo': {
      handler (val) {
        if (val.requestInfo && val.requestInfo.length) {
          this.tableData = val.requestInfo.map((item, index) => {
            let obj = {
              childList: [],
              paramNote: "",
              paramValue: "",
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
  computed: {
    ...mapGetters([
      'isEditDetail'
    ]),
    apiInfo () {
      return this.$store.getters.apiDetail ? this.$store.getters.apiDetail : {
        requestInfo: []
      }
    },
    dataType () {
      return DATA_TYPE
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
.d-detail-headers >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 50px;
}
.d-detail-headers .d-detail-headers-content {
	padding: 0 20px 20px;
}
.d-detail-headers .d-detail-headers-content .sub-title {
	font-size:12px;
	margin: 8px 0 4px;
	padding-left: 8px;
	position: relative;
}
.d-detail-headers .d-detail-headers-content .sub-title::before{
	position: absolute;
	content: "";
	left: 0px;
	top: 50%;
	transform: translateY(-50%);
	width: 2px;
	height: 8px;
	background: #1890FF;
	border-radius: 3px;
}
.d-detail-headers .request-params-json{
	height: 200px;
}
.d-detail-headers .request-params-json >>> .jsoneditor {
	border-color: #e6e6e6;
}
.d-detail-headers .request-params-json >>> .ace_gutter {
	background: #f1f0f0;
}
.d-detail-headers .d-detail-headers-content .el-radio-group {
	margin: 8px 0;
}
</style>
