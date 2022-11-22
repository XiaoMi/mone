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
					:tree-props="{children: 'itemValue', hasChildren: 'hasChildren'}"
					style="width: 100%">
					<el-table-column
						fixed
						show-overflow-tooltip
						prop="itemName"
					>
						<template #header>
							<span class="common-table-title">{{$i18n.t('table.name')}}</span>
						</template>
					</el-table-column>
					<el-table-column
						fixed
						show-overflow-tooltip
						prop="exampleValue"
					>
						<template #header>
							<span class="common-table-title">{{$i18n.t('table.parameterName')}}</span>
						</template>
					</el-table-column>
					<el-table-column
						prop="itemClassStr"
						show-overflow-tooltip
					>
						<template #header>
							<span class="common-table-title">{{$i18n.t('table.parameterType')}}</span>
						</template>
					</el-table-column>
					<el-table-column
						prop="required"
						show-overflow-tooltip
					>
						<template #header>
							<span class="common-table-title">{{$i18n.t('table.required')}}</span>
						</template>
					<template #default="scope">
						<span>{{scope.row.required ? $i18n.t('yes'):$i18n.t('no')}}</span>
					</template>
					</el-table-column>
					<el-table-column
						prop="defaultValue"
						show-overflow-tooltip
					>
						<template #header>
							<span class="common-table-title">{{$i18n.t('table.parameterValue')}}</span>
						</template>
					</el-table-column>
					<el-table-column
						prop="desc"
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
  computed: {
    ...mapGetters([
      'isEditDetail',
      'apiDetail'
    ])
  },
  watch: {
    'apiDetail': {
      handler (val) {
        this.tableData = []
        if (!val.dubboApiBaseInfo || !val.dubboApiBaseInfo.methodparaminfo) {
          return
        }
        let paramsLayerList = this.apiDetail.dubboApiBaseInfo.methodparaminfo || {}
        try {
          paramsLayerList = JSON.parse(paramsLayerList)
        } catch (error) {}
        if (paramsLayerList && Array.isArray(paramsLayerList) && paramsLayerList.length) {
          let tableData = paramsLayerList.map((item, index) => {
            let obj = {
              ...item,
              random: new Date().getTime() + (index + 1) * Math.random() * 10
            }
            if (Array.isArray(item.itemValue) && item.itemValue.length) {
              obj.itemValue = this.handleReserveValue(item.itemValue)
            }
            return obj
          })
          this.tableData = tableData
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
				if (arr[i]) {
					let obj = {
						...arr[i],
						random: new Date().getTime() + (i + 1) * Math.random() * 1000
					}
					if (Array.isArray(arr[i].itemValue) && arr[i].itemValue.length) {
						obj.itemValue = this.handleReserveValue(arr[i].itemValue)
					}
					values.push(obj)
				}
        
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
	padding: 20px 20px 20px;
}
/* .d-detail-headers .d-detail-headers-content >>> .el-table__expand-icon{
	transition: none;
	transform: none;
}
.d-detail-headers .d-detail-headers-content >>> .el-table__expand-icon .el-icon-arrow-right::before{
	content: '+';
	width: 14px;
	height: 14px;
	border: 1px solid rgba(0, 0, 0,0.2);
	font-size: 12px;
	text-align: center;
	box-sizing: border-box;
	display: inline-block;
	color: rgba(0, 0, 0,0.2);
	line-height: 0.9;
}
.d-detail-headers .d-detail-headers-content >>> .el-table__expand-icon.el-table__expand-icon--expanded .el-icon-arrow-right::before{
	content: '-';
} */
</style>
