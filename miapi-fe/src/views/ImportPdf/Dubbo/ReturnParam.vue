<template>
  <div class="d-detail-returns">
		<h4><el-icon><Reading /></el-icon>{{$i18n.t('returnParameter')}}</h4>
		<div class="d-detail-returns-content">
			<el-table
				:data="tableData"
				header-cell-class-name="table-header-cell"
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
            width="120"
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
					<template #default="scope">
						<span>{{scope.row.itemClassStr}}</span>
					</template>
				</el-table-column>
        <el-table-column
					prop="required"
					show-overflow-tooltip
          width="55"
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
          width="140"
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
          dubboApiBaseInfo: {}
        }
      }
    }
  },
  watch: {
    'apiInfo.dubboApiBaseInfo': {
      handler (val) {
        this.tableData = []
        let response = val.response || {}
        try {
          response = JSON.parse(val.response)
        } catch (error) {}

        if (response && response.itemName) {
          this.tableData = [response].map((item, index) => {
            let obj = {
              ...item,
              random: new Date().getTime() + (index + 1) * Math.random() * 10
            }
            if (Array.isArray(item.itemValue) && item.itemValue.length) {
              obj.itemValue = this.handleReserveValue(item.itemValue)
            }
            return obj
          })
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleReserveValue (arr) {
      let values = []
      if (!Array.isArray(arr)) {
        return values
      }
      for (let i = 0; i < arr.length; i++) {
        if (!arr[i]) {
          continue
        }
        let obj = {
          ...arr[i],
          random: new Date().getTime() + (i + 1) * Math.random() * 1000
        }
        if (Array.isArray(arr[i].itemValue) && arr[i].itemValue.length) {
          obj.itemValue = this.handleReserveValue(arr[i].itemValue)
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
	padding: 10px 20px 20px;
}
</style>
