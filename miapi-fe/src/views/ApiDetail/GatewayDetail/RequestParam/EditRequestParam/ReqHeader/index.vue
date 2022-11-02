<template>
	<div class="req-header">
		<el-table
			:data="gatewayParam.apiHeader"
			border
			row-key="drap-table"
			header-cell-class-name="table-header-cell"
			class="base-table"
			style="width: 100%">
			<el-table-column
				prop="headerName"
				width="220">
        <template #header>
          <span class="common-table-title">{{$i18n.t('label')}}</span>
        </template>
				<template #default="scope">
					<el-select
						v-model="scope.row.headerName"
						:placeholder="$i18n.t('placeholder.pleaseChoose')">
						<el-option
							v-for="item in headers"
							:key="item"
              :disabled="handleDisabledHeader(item)"
							:label="item"
							:value="item">
						</el-option>
					</el-select>
				</template>
			</el-table-column>
			<el-table-column
				prop="headerValue"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('content')}}</span>
        </template>
				<template #default="scope">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="scope.row.headerValue" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				:width="isEN ? '86px' : '54px'"
				align="center">
        <template #header>
          <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
        </template>
				<template #default="scope">
					<span @click="handledelete(scope)" class="icon-modify">
						{{$i18n.t('btnText.delete')}}
					</span>
				</template>
			</el-table-column>
		</el-table>
	</div>
</template>
<script>
import { HEADER } from '@/views/constant'

export default {
  name: 'ReqHeader',
  data () {
    return {
      isEN: false,
      tableData: [{
        headerName: '',
        headerValue: ''
      }]
    }
  },
  computed: {
    headers () {
      return HEADER
    },
    gatewayParam () {
      return this.$store.getters.gatewayParam
    }
  },
  watch: {
    'gatewayParam.apiHeader': {
      handler: function (val, oldVal) {
        if (this.gatewayParam.apiHeader.every(item => !!item.headerName)) {
          this.gatewayParam.apiHeader.push({
            headerName: '',
            headerValue: ''
          })
        }
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
    const table = document.querySelector('.base-table tbody')
    const self = this
    // self.$Sortable.create(table, {
    //   onEnd({ newIndex, oldIndex }) {
    //     console.log(newIndex, oldIndex)
    //     const targetRow = self.tableData.splice(oldIndex, 1)[0]
    // 		console.log(self.tableData);
    //     self.tableData.splice(newIndex, 0, targetRow)
    // 		console.log(self.tableData);
    //   }
    // })
  },
  methods: {
    handleDisabledHeader (val) {
      return this.gatewayParam.apiHeader.some(item => item.headerName === val)
    },
    handledelete (scope) {
      if (this.gatewayParam.apiHeader.length > 1) {
        this.gatewayParam.apiHeader = this.gatewayParam.apiHeader.filter(item => !(item.headerValue === scope.row.headerValue && item.headerName === scope.row.headerName))
      }
    }
  }
}
</script>
<style scoped>
.req-header .base-table >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 50px;
}
.req-header .base-table .icon-modify {
	cursor: pointer;
	color: #1890FF;
	font-size: 14px;
}
</style>
