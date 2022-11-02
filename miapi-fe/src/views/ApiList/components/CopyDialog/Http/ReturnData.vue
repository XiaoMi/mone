<template>
  <div class="return-param">
    <h4>返回参数</h4>
    <div v-show="type === radioTypes.JSON">
      <FormJson ref="formJson" :formJsonData="formJsonData" @changeFormJsonData="changeFormJsonData"/>
    </div>
    <div v-show="type === radioTypes.RAW">
      <div class="edit-request-json">
        <JsonEditor @json-change="handleInputJson" @json-error="handleError" :content="content" :jsonEditorOptions="jsonEditorOptions"/>
      </div>
		</div>
  </div>
</template>
<script>
import { DATA_TYPE, API_REQUEST_PARAM_TYPE } from '@/views/constant'
import JsonEditor from '@/components/JsonEditor'
import FormJson from '@/views/ApiDetail/HttpDetail/ReturnParam/EditReturnParam/formJson.vue'

export default {
  name: 'ReturnData',
  components: {
    FormJson,
    JsonEditor
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
  props: {
    httpParam: {
      type: Object,
      default () {
        return {
          apiID: undefined
        }
      }
    }
  },
  computed: {
    radioTypes () {
      return API_REQUEST_PARAM_TYPE
    }
  },
  watch: {
    "httpParam.apiID": {
      handler (val) {
        if (!val) {
          return
        }
        this.formJsonData = this.httpParam.apiResultParam || []
        let apiResponseRaw = this.httpParam.apiResponseRaw || {}
        try {
          apiResponseRaw = JSON.parse(apiResponseRaw)
        } catch (error) {}
        this.content = apiResponseRaw
        this.type = this.httpParam.apiResponseParamType || API_REQUEST_PARAM_TYPE.JSON
      },
      immediate: true,
      deep: true
    },
    type: {
      handler (newVal, old) {
        if (newVal !== old) {
          // this.$emit("onChange", { apiResponseParamType: newVal })
        }
        switch (newVal) {
          case API_REQUEST_PARAM_TYPE.JSON:
            this.$emit("onChange", { apiResultParam: this.formJsonData })
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
        this.$emit("onChange", { apiResultParam: newVal })
      },
      deep: true
    }
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
      this.$emit("onChange", { apiResponseRaw: JSON.stringify(content) })
    },
    handleError () {
    }
  }
}
</script>
<style scoped>
.return-param h4 {
  margin-bottom: 8px;
}
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
