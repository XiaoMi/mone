<template>
	<div class="edit-codes-container">
		<el-table
			:data="tableData"
			border
			header-cell-class-name="table-header-cell"
			style="width: 100%">
			<el-table-column
				prop="errorCodeName"
				width="220">
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.errorCode')}}</span>
        </template>
				<template #default="scope">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnterErrorCode')" v-model="scope.row.errorCodeName" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="errorDesc"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.wrongInformation')}}</span>
        </template>
				<template #default="scope">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnterWrongInformation')" v-model="scope.row.errorDesc" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="plan"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.solution')}}</span>
        </template>
				<template #default="scope">
					<el-input :rows="1" type="textarea" :placeholder="$i18n.t('placeholder.pleaseEnterSolution')" v-model="scope.row.plan" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				:width="isEN ? '86px' : '54px'"
				align="center">
        <template #header>
          <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
        </template>
				<template #default="scope">
					<el-button text type="primary" size="mini" @click="handledelete(scope.row)">{{$i18n.t('btnText.delete')}}</el-button>
				</template>
			</el-table-column>
		</el-table>
	</div>
</template>

<script>
export default {
  name: 'EditErrorCode',
  data () {
    return {
      tableData: [],
      isEN: false
    }
  },
  props: {
    defaultApiErrorCodes: {
      type: Array,
      default () {
        return []
      }
    }
  },
  watch: {
    defaultApiErrorCodes: {
      handler (val) {
        if (val && Array.isArray(val) && val.length) {
        	let tableData = val.map((v, index) => {
            return {
              ...v,
              random: new Date().getTime() + index
            }
          })
          if (tableData.every(item => !!item.errorCodeName && !!item.errorDesc)) {
            tableData.push({
              errorCodeName: '',
              errorDesc: '',
              plan: '',
              random: new Date().getTime()
            })
          }
          this.tableData = tableData
        } else {
          this.tableData = [{
            errorCodeName: '',
            errorDesc: '',
            plan: '',
            random: 1
          }]
        }
      },
      immediate: true,
      deep: true
    },
    tableData: {
      handler (val) {
        if (val.every(item => !!item.errorCodeName && !!item.errorDesc)) {
          this.tableData.push({
            errorCodeName: '',
            errorDesc: '',
            plan: '',
            random: new Date().getTime()
          })
        }
        let arr = val.filter(v => !!v.errorCodeName && !!v.errorDesc).map(v => {
          let o = { ...v }
          delete o.random
          return o
        })
        try {
          this.$emit('onChange', arr)
        } catch (error) {}
      },
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
  },
  methods: {
    handledelete (row) {
      if (this.tableData.length > 1) {
        this.tableData = this.tableData.filter(item => (item.random !== row.random))
      }
    },
  }
}
</script>

<style scoped>
.edit-codes-container >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	height: 50px;
}
</style>
