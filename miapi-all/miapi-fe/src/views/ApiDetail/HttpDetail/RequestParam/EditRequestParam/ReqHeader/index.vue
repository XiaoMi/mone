<template>
	<div class="req-header">
		<el-table
			:data="httpParam.apiHeader"
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
						allow-create
						filterable
						default-first-option
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
    httpParam () {
      return this.$store.getters.httpParam
    }
  },
  watch: {
    'httpParam.apiHeader': {
      handler: function (val, oldVal) {
        if (this.httpParam.apiHeader.every(item => !!item.headerName)) {
          this.$store.dispatch('apilist.add/changeHttpParam', {
            apiHeader: [...val, {
              headerName: '',
              headerValue: ''
            }]
          })
        }
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
  },
  methods: {
    handleDisabledHeader (val) {
      return this.httpParam.apiHeader.some(item => item.headerName === val)
    },
    handledelete (scope) {
      if (this.httpParam.apiHeader.length > 1) {
        this.httpParam.apiHeader = this.httpParam.apiHeader.filter(item => !(item.headerValue === scope.row.headerValue && item.headerName === scope.row.headerName))
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
