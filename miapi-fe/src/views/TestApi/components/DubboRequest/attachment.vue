<template>
	<div class="attachment-container">
		<el-table
			:data="tableData"
			border
			row-key="drap-table"
			header-cell-class-name="table-header-cell"
			class="base-table">
			<el-table-column
				prop="key"
				width="200">
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.name')}}</span>
        </template>
				<template #default="scope">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="scope.row.key" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="value"
				width="240"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterValue')}}</span>
        </template>
				<template #default="scope">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="scope.row.value" autocomplete="off"></el-input>
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
import { mapGetters } from 'vuex'

export default {
  name: 'Attachment',
  data () {
    return {
      isEN: false,
      tableData: [{
        key: '',
        value: '',
        random: new Date().getTime()
      }]
    }
  },
  computed: {
    ...mapGetters([
      'apiTestAttachments'
    ])
  },
  watch: {
    'tableData': {
      handler: function (val, oldVal) {
        if (this.tableData.every(item => !!item.key)) {
          this.tableData.push({
            key: '',
            value: '',
            random: new Date().getTime()
          })
        }
        let arr = val.filter(item => item.key)
        this.$store.dispatch('apitest/changeApiTestTarget', { attachments: arr })
      },
      deep: true
    }
  },
  beforeUnmount () {
    this.$store.dispatch('apitest/changeApiTestTarget', { attachments: [] })
  },
  mounted () {
    if (this.apiTestAttachments && this.apiTestAttachments.length) {
      let tableList = []
      tableList = this.apiTestAttachments.map((item, index) => {
        return {
          ...item,
          random: new Date().getTime() + index
        }
      })
      tableList.push({
        key: '',
        value: '',
        random: 999
      })
      this.tableData = tableList
    }
  },
  methods: {
    handledelete (scope) {
      if (this.tableData.length > 1) {
        this.tableData = this.tableData.filter(item => item.random !== scope.row.random)
      }
    }
  }
}
</script>
<style scoped>
.attachment-container .base-table >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 50px;
}
.attachment-container .base-table .icon-modify {
	cursor: pointer;
	color: #1890FF;
	font-size: 14px;
}
</style>
