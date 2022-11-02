<template>
	<div class="add-env-header">
		<el-table
			:data="envData.headers"
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
						{{$i18n.t('delete')}}
					</span>
				</template>
			</el-table-column>
		</el-table>
	</div>
</template>
<script>
import { HEADER } from '@/views/constant'
import { mapGetters } from 'vuex'

export default {
  name: 'Headers',
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
    ...mapGetters([
      'envData'
    ]),
    headers () {
      return HEADER
    }
  },
  watch: {
    'envData.headers': {
      handler: function (val, oldVal) {
        if (this.envData.headers.every(item => !!item.headerName)) {
          this.$store.dispatch('addEnv/changeAddEnvData', {
            headers: [...val, {
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
      return this.envData.headers.some(item => item.headerName === val)
    },
    handledelete (scope) {
      if (this.envData.headers.length > 1) {
        this.envData.headers = this.envData.headers.filter(item => !(item.headerValue === scope.row.headerValue && item.headerName === scope.row.headerName))
      }
    }
  }
}
</script>
<style scoped>
.add-env-header{
	padding: 0 20px 20px;
}
.add-env-header .base-table >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 50px;
}
.add-env-header .base-table .icon-modify {
	cursor: pointer;
	color: #1890FF;
	font-size: 14px;
}
</style>
