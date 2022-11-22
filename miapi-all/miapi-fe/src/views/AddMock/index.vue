<template>
	<div class="add-mock-wrap">
		<div class="add-mock-container">
			<div class="add-mock-content">
				<span :class="{'title': true, isEn: isEN}">{{$i18n.t('expectedName')}}：</span>
				<div class="add-mock-content-right">
					<el-input v-model="mockData.mockExpName" style="width: 400px" :placeholder="$i18n.t('placeholder.pleaseEnter')"/>
				</div>
			</div>
			<div class="add-mock-content">
				<span :class="{'title': true, isEn: isEN}">{{$i18n.t('parameter')}}：</span>
				<div class="add-mock-content-right">
					<MockReqParam @changeIsError="changeIsError"/>
				</div>
			</div>
			<div class="add-mock-content">
				<span :class="{'title': true, isEn: isEN}">{{$i18n.t('responseContent')}}：</span>
				<div class="add-mock-content-right">
					<MockRetuenParam @changeIsError="changeIsError"/>
				</div>
			</div>
			<div class="add-mock-content" v-if="mockData.isDefault">
				<span :class="{'title': true, isEn: isEN}">{{$i18n.t('mockScript')}}：</span>
				<div class="add-mock-content-right">
					<div class="custom-mock">
            <p>
              <el-checkbox v-model="mockData.enableMockScript">{{$i18n.t('useUustomMock')}}</el-checkbox>
              <el-button @click="dialogVisible = true" text type="primary" size="small">{{$i18n.t('exampleDescription')}}</el-button>
            </p>
            <codeMirror :codeInit="codeInit" :content="content" @json-change="handleChangeJS" :codeMirrorOptions="codeMirrorOptions"/>
          </div>
				</div>
			</div>
			<div :class="{'add-mock-btns': true, isEn: isEN}">
				<el-button :disabled="disabledSubmit" @click="handleSubmit" type="primary">{{$i18n.t('btnText.submit')}}</el-button>
				<el-button @click="handlePreview" plain>{{$i18n.t('btnText.preview')}}</el-button>
				<el-button @click="handleCancel">{{$i18n.t('btnText.cancel')}}</el-button>
			</div>
		</div>
		<el-dialog
			:destroy-on-close="true"
			:center="false"
			:show-close="false"
			:close-on-click-modal="false"
			:close-on-press-escape="false"
			:title="previewDialog.title"
			v-model="previewDialog.visible"
			width="640px"
      top="10vh"
			append-to-body
		>
			<MockPreview :content="previewDialog.content" :mockRequestParamType="mockData.mockRequestParamType" :requestJson="previewDialog.requestJson" @handleCancel="handleClose"/>
		</el-dialog>
    <el-dialog
      v-model="dialogVisible"
      width="640px">
      <ExmpleMock/>
    </el-dialog>
	</div>
</template>
<script>
import MockReqParam from './MockReqParam'
import MockRetuenParam from './MockRetuenParam'
import { PATH } from '@/router/constant'
import { editApiMockExpect, previewMockData, getMockExpectDetail } from '@/api/apimock'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { RADIO_TYPE } from '@/views/ApiList/constant'
import { mapGetters } from 'vuex'
import MockPreview from '@/views/AddMock/MockPreview'
import { handleFilter, checkHaveSameKey, handleDeepCheck } from '@/store/utils'
import codeMirror from '@/components/CodeMirror'
import ExmpleMock from "./MockRetuenParam/exmple.vue"

export default {
  name: 'AddMock',
  components: {
    MockReqParam,
    MockRetuenParam,
    MockPreview,
    codeMirror,
    ExmpleMock
  },
  data () {
    return {
      apiID: 0,
      apiProtocol: 0,
      projectID: 0,
      jsonIsError: false,
      isEN: false,
      dialogVisible: false,
      previewDialog: {
        visible: false,
        title: '',
        requestJson: '',
        content: {}
      },
      codeMirrorOptions: {
        theme: "material",
        mode: 'text/javascript',
        lineNumbers: true,
        lineWrapping: false,
        electricChars: true,
        tabindex: 2
      },
      codeInit: false,
      content: ""
    }
  },
  watch: {
    "mockData": {
      handler (val) {
        if (val) {
          this.content = val.mockScript || ""
        }
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
    this.apiID = this.$utils.getQuery('apiID')
    this.apiProtocol = this.$utils.getQuery('apiProtocol')
    this.projectID = this.$utils.getQuery('projectID')
    if (this.$utils.getQuery('indexProjectID')) {
      this.projectID = this.$utils.getQuery('indexProjectID')
    }
    let mockExpectID = this.$utils.getQuery('mockExpectID')
    if (mockExpectID) {
      this.$store.dispatch('addmock/changeAddMockData', {
        mockExpID: mockExpectID
      })
      getMockExpectDetail({ mockExpectID }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          let mockRule = data.data.mockRule
          try {
            mockRule = JSON.parse(mockRule)
          } catch (error) { }
          this.$store.dispatch('addmock/changeAddMockData', {
            mockRule: data.data.mockDataType === RADIO_TYPE.FORM ? mockRule : '',
            mockExpName: data.data.mockExpName,
            mockJsonData: data.data.mockDataType === RADIO_TYPE.FORM ? '' : mockRule,
            paramsJson: data.data.mockParams,
            mockRequestRaw: data.data.mockRequestRaw || '',
            mockRequestParamType: data.data.mockRequestParamType || RADIO_TYPE.FORM,
            requestTime: new Date().getTime(),
            mockDataType: data.data.mockDataType,
            isDefault: !!data.data.isDefault,
            mockScript: data.data.mockScript,
            enableMockScript: !!data.data.useMockScript
          })
          this.codeInit = true
        }
      }).catch(e => {})
    } else {
      this.codeInit = true
    }
  },
  computed: {
    ...mapGetters([
      'mockData'
    ]),
    radioType () {
      return RADIO_TYPE
    },
    disabledSubmit () {
      return (this.mockData.mockDataType === RADIO_TYPE.JSON && this.jsonIsError) || (this.mockData.mockRequestParamType === RADIO_TYPE.JSON && this.jsonIsError) || !this.apiID || !this.projectID || !this.apiProtocol
    }
  },
  beforeUnmount () {
    this.$store.dispatch('addmock/resetMockData')
  },
  methods: {
    changeIsError (bool) {
      this.jsonIsError = bool
    },
    handleClose () {
      this.previewDialog = {
        ...this.previewDialog,
        visible: false
      }
    },
    handleCancel () {
      let q = this.$route.query
      delete q.mockExpectID
      this.$router.push({ path: PATH.API_DETAIL, query: q })
    },
    handleCheckParamsJson () {
      let paramsJson = this.mockData.paramsJson || []
      try {
        paramsJson = JSON.parse(paramsJson)
      } catch (error) {}
      return checkHaveSameKey(handleFilter(paramsJson))
    },
    handleCheck () {
      let bool = false
      let msg = ''
      if (!this.mockData.mockExpName) {
        bool = true
        msg = this.$i18n.t('errorMessage.errorRequestParameters')
      } else if (this.mockData.mockRequestParamType === RADIO_TYPE.JSON && !this.mockData.mockRequestRaw) {
        bool = true
        msg = this.$i18n.t('errorMessage.errorRequestParameters')
      } else if (
        this.mockData.mockRequestParamType === RADIO_TYPE.FORM &&
        this.handleCheckParamsJson()
      ) {
        bool = true
        msg = this.$i18n.t('errorMessage.parameterNameNotRepeated')
      } else if (
        this.mockData.mockDataType === RADIO_TYPE.FORM &&
        handleDeepCheck(handleFilter(this.mockData.mockRule))
      ) {
        bool = true
        msg = this.$i18n.t('errorMessage.responseContentFormat')
      } else if (
        this.mockData.mockDataType === RADIO_TYPE.FORM &&
        checkHaveSameKey(handleFilter(this.mockData.mockRule))
      ) {
        bool = true
        msg = this.$i18n.t('errorMessage.ResponseContentNameNotRepeated')
      }
      bool && this.$message.error(msg)
      return bool
    },
    handleSubmit () {
      if (!this.mockData.isDefault && this.handleCheck()) {
        return
      }
      let mockRule = this.mockData.mockDataType === RADIO_TYPE.FORM ? handleFilter(this.mockData.mockRule) : this.mockData.mockJsonData
      if (typeof mockRule !== 'string') {
        mockRule = JSON.stringify(mockRule)
      }
      let param = {
        mockExpID: this.mockData.mockExpID,
        mockExpName: this.mockData.mockExpName,
        paramsJson: (this.mockData.paramsJson && (this.mockData.mockRequestParamType === RADIO_TYPE.FORM)) ? JSON.stringify(this.mockData.paramsJson) : null,
        mockRequestRaw: this.mockData.mockRequestParamType === RADIO_TYPE.FORM ? null : this.mockData.mockRequestRaw,
        mockRequestParamType: this.mockData.mockRequestParamType,
        mockRule,
        mockDataType: this.mockData.mockDataType,
        projectID: this.projectID,
        apiID: this.apiID,
        defaultSys: this.mockData.isDefault,
        apiType: this.apiProtocol, // 1 http 2 dubbo 3 gateway
        mockScript: this.mockData.mockScript,
        enableMockScript: this.mockData.enableMockScript
      }
      editApiMockExpect(param).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message.success(this.$i18n.t('submittedSuccessfully'))
          this.handleCancel()
        } else {
          this.$message.error(data.data.message)
        }
      }).catch(e => {})
    },
    isType (val) {
      return Object.prototype.toString.call(val)
    },
    handleChangeJS (val) {
      this.$store.dispatch('addmock/changeAddMockData', {
        mockScript: val
      })
    },
    handlePreview () {
      if (!this.mockData.isDefault && this.handleCheck()) {
        return
      }
      let mockRule = this.mockData.mockDataType === RADIO_TYPE.FORM ? handleFilter(this.mockData.mockRule) : this.mockData.mockJsonData
      previewMockData({ mockRule: mockRule ? JSON.stringify(mockRule) : null, mockDataType: this.mockData.mockDataType }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          let requestJson = this.mockData.mockRequestParamType === RADIO_TYPE.FORM ? this.mockData.paramsJson : this.mockData.mockRequestRaw
          try {
            requestJson = JSON.parse(requestJson)
          } catch (error) {}
          this.previewDialog = {
            visible: true,
            title: this.mockData.mockExpName,
            requestJson,
            content: data.data
          }
        }
      }).catch(e => {})
    }
  }
}
</script>
<style scoped>
.add-mock-wrap {
	margin: 20px 20px 0;
	background: #fff;
	padding: 24px 0 0 0;
	height: calc(100vh - 130px);
}
.add-mock-container {
	height: 100%;
	padding: 0 20px 20px 16px;
	overflow-y: auto;
}
.add-mock-container::-webkit-scrollbar {
  display: none;
}
.add-mock-container .add-mock-content {
	display: flex;
	align-items: flex-start;
	justify-content: flex-start;
	width: 100%;
	margin-bottom: 30px;
}
.add-mock-container .add-mock-content .add-mock-content-right {
	width: calc(100% - 90px);
}
.add-mock-container .add-mock-content .title {
	display: inline-block;
	font-size: 14px;
	color: #333;
	line-height: 32px;
	margin-right: 8px;
  white-space: nowrap;
  width: 78px;
}
.add-mock-container .add-mock-content .title.isEn {
  width: 80px;
  text-align: right;
}
.add-mock-container .add-mock-btns {
	padding-left: 90px;
}
.add-mock-container .add-mock-btns.isEn {
  padding-left: 92px;
}
.add-mock-container .custom-mock > p{
  color: #333;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.add-mock-container .custom-mock > p .el-button {
  font-size: 14px;
  padding: 0;
}
</style>
