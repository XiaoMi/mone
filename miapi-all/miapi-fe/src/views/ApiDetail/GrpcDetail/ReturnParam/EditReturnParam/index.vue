
<template>
  <div class="return-param">
    <div>
      <FormJson ref="formJson" :formJsonData="formJsonData" @changeFormJsonData="changeFormJsonData"/>
    </div>
  </div>
</template>
<script>
import FormJson from './formJson.vue'
import { mapGetters } from "vuex"

export default {
  name: 'EditReturnParam',
  components: {
    FormJson
  },
  data () {
    return {
      formJsonData: [],
      importDialogVisible: false
    }
  },
  computed: {
    ...mapGetters([
      'updateGrpcParam'
    ])
  },
  watch: {
    formJsonData: {
      handler (newVal) {
        this.$store.dispatch('apilist.add/changeGrpcUpdateParam', {
          responseParam: newVal
        })
      },
      deep: true
    }
  },
  mounted () {
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
    changeFormJsonData (val) {
      this.formJsonData = val
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
    }
  }
}
</script>
<style scoped>
.return-param .radio-types {
  height: 28px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.return-param .edit-request-json {
  height: 200px;
}
.return-param .edit-request-json >>> .jsoneditor{
  border-color: #e6e6e6;
}
.return-param .edit-request-json >>> .ace_gutter {
	background: #f1f0f0;
}
</style>
