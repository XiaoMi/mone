<template>
	<div>
		<div class="import-gateway-json-container">
      <JsonEditor :jsonEditorOptions="jsonEditorOptions" @json-change="handleJsonChange" @json-error="handleError"/>
    </div>
		<div style="margin-top: 10px; text-align: right">
			<el-button @click="handleSubmit" :disabled="isDisabled" type="primary">{{$i18n.t('btnText.ok')}}</el-button>
			<el-button @click="handleChangeVisible">{{$i18n.t('btnText.cancel')}}</el-button>
		</div>
	</div>
</template>

<script>
import JsonEditor from '@/components/JsonEditor'
import { batchAddGatewayApi } from '@/api/apilist'
import { mapGetters } from 'vuex'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { PATH } from '@/router/constant'

export default {
  name: 'ImportDialog',
  components: {
    JsonEditor
  },
  data () {
    return {
      content: [],
      jsonEditorOptions: {
        mainMenuBar: false
      },
      isDisabled: true
    }
  },
  computed: {
    ...mapGetters([
      'gatewayParam'
    ])
  },
  methods: {
    isType (val) {
      return Object.prototype.toString.call(val)
    },
    handleChangeVisible () {
      this.$emit('handleCancel')
    },
    getUrl (arg) {
      let arr = []
      switch (this.isType(arg)) {
        case '[object Object]':
          if (arg.url) {
            arr.push(arg.url)
          }
          Object.keys(arg).forEach(key => {
            arr = arr.concat(this.getUrl(arg[key]))
          })
          break
        case '[object Array]':
          arg.forEach(item => {
            arr = arr.concat(this.getUrl(item))
          })
          break
        default:
          break
      }
      return arr
    },
    handleSubmit () {
      if (JSON.stringify(this.content) === '{}' || JSON.stringify(this.content) === '[]') {
        this.$message.error(this.$i18n.t('errorMessage.incorrectFormatImportedData'))
        return
      }
      switch (this.isType(this.content)) {
        case '[object Object]':
        case '[object Array]':
          let arr = this.getUrl(this.content)
          if (arr.length) {
            this.handleSave(JSON.stringify(arr))
          } else {
            this.$message.error(this.$i18n.t('errorMessage.importAddressEmpty'))
          }
          break
        default:
          this.$message.error(this.$i18n.t('errorMessage.incorrectFormatImportedData'))
          break
      }
    },
    handleSave (urlList) {
      let projectID = this.$utils.getQuery('projectID')
      batchAddGatewayApi({
        projectID,
        groupID: this.gatewayParam.groupId,
        env: this.gatewayParam.env,
        urlList
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.handleChangeVisible()
          this.$store.dispatch('apilist.group/getGroupViewList', projectID)
          this.$router.push({ path: PATH.API, query: { projectID } })
        }
      }).catch(e => {})
    },
    handleJsonChange (val) {
      this.isDisabled = !val
      this.content = val
    },
    handleError () {
      this.isDisabled = true
    }
  }
}
</script>
<style>
.import-gateway-json-container{
	height: 300px;
}
.import-gateway-json-container .jsoneditor {
	border-color: #e6e6e6;
}
.import-gateway-json-container .ace_gutter {
	background: #f1f0f0;
}
</style>
