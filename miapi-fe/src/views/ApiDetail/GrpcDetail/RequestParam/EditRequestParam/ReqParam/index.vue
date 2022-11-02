<template>
	<div class="req-param">
		<div >
      <FormJson ref="formJson" :formJsonData="formJsonData" @changeFormJsonData="changeFormJsonData"/>
		</div>
	</div>
</template>
<script>

import { DATA_TYPE } from '@/views/constant'
import FormJson from './formJson.vue'
import { mapGetters } from "vuex"

export default {
  name: 'ReqParam',
  components: {
    FormJson
  },
  data () {
    return {
      isEN: false,
      formJsonData: [],
      importDialogVisible: false
    }
  },
  computed: {
    ...mapGetters([
      'updateGrpcParam'
    ]),
    dataType () {
      return DATA_TYPE
    }
  },
  watch: {
    formJsonData: {
      handler (newVal) {
        this.$store.dispatch('apilist.add/changeGrpcUpdateParam', {
          requestParam: newVal
        })
      },
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
    let tableData = this.updateGrpcParam.requestParam.map((v) => {
      return {
        paramNotNull: v.paramNotNull,
        paramType: v.paramType,
        paramKey: v.paramKey,
        paramNote: v.paramNote,
        paramValue: v.paramValue,
        childList: this.handleChildList(v.childList)
      }
    })
    this.formJsonData = tableData
  },
  methods: {
    handleOpenVisible () {
      this.importDialogVisible = true
    },
    handleCancel () {
      this.importDialogVisible = false
    },
    handleImportOk (data) {
      this.formJsonData = [...data]
      this.$refs.formJson.init(data)
    },
    handleChildList (arr) {
      if (!Array.isArray(arr)) {
        return []
      }
      let array = []
      arr.forEach(v => {
        let o = {
          paramNotNull: v.paramNotNull,
          paramType: v.paramType,
          paramKey: v.paramKey,
          paramNote: v.paramNote,
          paramValue: Array.isArray(v.paramValue) ? '' : v.paramValue,
          childList: this.handleChildList(v.childList)
        }
        array.push(o)
      })
      return array
    },
    changeFormJsonData (val) {
      this.formJsonData = val
    },
    renderHeader (h, { column, $index }) {
      return h('span', {}, [
        h('span', { style: { color: 'rgba(0, 0, 0, 0.64)', fontSize: '14px' } }, column.label)
      ])
    }
  }
}
</script>
<style scoped>
.req-param .radio-types {
  height: 28px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.req-param .base-table >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 50px;
}
.req-param .icon-modify {
	cursor: pointer;
	color: #1890FF;
	font-size: 14px;
}
.req-param .active_btn{
	background: #646669;
	border-color: #1890FF;
	color: #FFF;
}
.req-param .base-table >>> .cell {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.req-param .edit-request-json {
  height: 200px;
}
.req-param .edit-request-json >>> .jsoneditor{
  border-color: #e6e6e6;
}
.req-param .edit-request-json >>> .ace_gutter {
	background: #f1f0f0;
}
</style>
