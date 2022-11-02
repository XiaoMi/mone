
<template>
  <div class="return-param">
    <div class="radio-types">
			<el-radio-group v-model="type">
				<el-radio :label="radioTypes.JSON">Json</el-radio>
				<el-radio :label="radioTypes.RAW">Raw</el-radio>
			</el-radio-group>
      <el-button v-show="type === radioTypes.JSON" @click="handleOpenVisible" type="primary" size="small">{{$i18n.t('importJson')}}</el-button>
		</div>
    <div v-show="type === radioTypes.JSON">
      <FormJson ref="formJson" :formJsonData="formJsonData" @changeFormJsonData="changeFormJsonData"/>
    </div>
    <div v-show="type === radioTypes.RAW">
      <div class="edit-request-json">
        <JsonEditor @json-change="handleInputJson" @json-error="handleError" :content="content" :jsonEditorOptions="jsonEditorOptions"/>
      </div>
		</div>
    <el-dialog
      :destroy-on-close="true"
      :center="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :title="$i18n.t('importJson')"
      v-model="importDialogVisible"
			width="540px"
      append-to-body
    >
      <ImportDialog @handleOk="handleImportOk" @handleCancel="handleCancel"/>
			<!-- <div slot="title">{{importDialog.title}}</div> -->
    </el-dialog>
  </div>
</template>
<script>
import { DATA_TYPE, API_REQUEST_PARAM_TYPE } from '@/views/constant'
import JsonEditor from '@/components/JsonEditor'
import FormJson from './formJson.vue'
import ImportDialog from './ImportDialog'

export default {
  name: 'EditReturnParam',
  components: {
    FormJson,
    JsonEditor,
    ImportDialog
  },
  data () {
    return {
      type: API_REQUEST_PARAM_TYPE.JSON,
      formJsonData: [],
      content: {},
      jsonEditorOptions: {
        mainMenuBar: false
      },
      importDialogVisible: false
    }
  },
  computed: {
    httpParam () {
      return this.$store.getters.httpParam
    },
    radioTypes () {
      return API_REQUEST_PARAM_TYPE
    }
  },
  watch: {
    type: {
      handler (newVal, old) {
        if (newVal !== old) {
          this.$store.dispatch('apilist.add/changeHttpParam', {
            apiResponseParamType: newVal
          })
        }
        switch (newVal) {
          case API_REQUEST_PARAM_TYPE.JSON:
            this.$store.dispatch('apilist.add/changeHttpParam', {
              apiResultParam: this.formJsonData
            })
            break
          case API_REQUEST_PARAM_TYPE.RAW:
            let apiResponseRaw = this.httpParam.apiResponseRaw || {}
            try {
              apiResponseRaw = JSON.parse(apiResponseRaw)
            } catch (error) {}
            this.content = apiResponseRaw
            break
          default:
            break
        }
      },
      deep: true
    },
    formJsonData: {
      handler (newVal) {
        this.$store.dispatch('apilist.add/changeHttpParam', {
          apiResultParam: newVal
        })
      },
      deep: true
    }
  },
  mounted () {
    this.formJsonData = this.httpParam.apiResultParam || []
    let apiResponseRaw = this.httpParam.apiResponseRaw || {}
    try {
      apiResponseRaw = JSON.parse(apiResponseRaw)
    } catch (error) {}
    this.content = apiResponseRaw
    this.type = this.httpParam.apiResponseParamType || API_REQUEST_PARAM_TYPE.JSON
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
    handleInputJson (content) {
      this.$store.dispatch('apilist.add/changeIsRawBool', false)
      this.$store.dispatch('apilist.add/changeHttpParam', {
        apiResponseRaw: JSON.stringify(content)
      })
    },
    handleError () {
      this.$store.dispatch('apilist.add/changeIsRawBool', true)
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
