<template>
  <div class="test-header-container">
		<el-row :gutter="0" type="flex" justify="space-between">
			<el-col :span="22">
				<el-row :gutter="0" type="flex" justify="space-between">
					<el-col :span="4">
						<el-select :disabled="isDetail" class="method-select first-method-select" v-model="protocolValue" :placeholder="$i18n.t('placeholder.pleaseChooseType')" size="medium">
							<el-option v-for="v in Object.keys(protocol)" :key="v" :label="protocol[v]" :value="v"></el-option>
						</el-select>
					</el-col>
					<el-col :span="4">
              <template v-if="protocolValue === protocol_type.HTTP">
                <el-select :disabled="isDetail" style="margin: 0 -1px" class="method-select" v-model="requestTypeValue" :placeholder="$i18n.t('placeholder.pleaseEnter')" size="medium">
                  <el-option v-for="v in Object.keys(requestType)" :key="v" :label="requestType[v].toLocaleLowerCase()" :value="requestType[v].toLocaleLowerCase()"></el-option>
                </el-select>
              </template>
              <template v-else>
                <el-select :disabled="isDetail" style="margin: 0 -1px" class="method-select" v-model="requestTypeValue" :placeholder="$i18n.t('placeholder.pleaseEnter')" size="medium">
                  <el-option v-for="v in Object.keys(gateway_protocol_type)" :key="v" :label="gateway_protocol_type[v].toLocaleLowerCase()" :value="gateway_protocol_type[v].toLocaleLowerCase()"></el-option>
                </el-select>
              </template>
					</el-col>
          <el-col v-if="protocolValue === protocol_type.Gateway" :span="3">
						<el-select class="method-select" style="margin-right: -1px" size="medium" v-model="httpType" placeholder="请选择">
              <el-option
                v-for="item in httpOptions"
                :key="item.value"
                :label="item.name"
                :value="item.value">
              </el-option>
            </el-select>
					</el-col>
					<el-col :span="6">
            <el-select
              v-model="env"
              filterable
              default-first-option
              class="method-select"
              size="medium"
              style="width: 100%"
              :placeholder="$i18n.t('placeholder.testEnvironment')">
              <template v-if="protocolValue === protocol_type.HTTP">
                <el-option  v-for="v in hostEnvList" :key="v.id" :label="v.httpDomain" :value="v.id" style="height: auto;padding:0">
                  <p :title="v.httpDomain" style="line-height: 18px; padding: 8px 20px;maxWidth: 400px; overflow: hidden;text-overflow:ellipsis;white-space:nowrap;border-bottom: 1px solid #e6e6e6;">
                    {{ v.envName }}<br/>
                    <span style="font-size: 12px;color:#909399">{{v.httpDomain}}</span>
                  </p>
                </el-option>
              </template>
              <template v-else>
                <el-option  v-for="v in gatewayOptions" :key="v.httpDomain" :label="v.httpDomain" :value="v.httpDomain" style="height: auto;padding:0">
                  <p :title="v.httpDomain" style="line-height: 18px; padding: 8px 20px;maxWidth: 400px; overflow: hidden;text-overflow:ellipsis;white-space:nowrap;border-bottom: 1px solid #e6e6e6;">
                    {{ v.envName }}<br/>
                    <span style="font-size: 12px;color:#909399">{{v.httpDomain}}</span>
                  </p>
                </el-option>
              </template>
              <div style="width: 100%; text-align: center;padding: 6px 0 0">
                <el-button @click="handleOpenAdd" style="font-size: 14px" text type="primary">{{$i18n.t('btnText.newTestEnvironment')}}</el-button>
              </div>
              <template #empty>
                <div>
                  <div style="width: 100%; text-align: center;padding: 2px 0">
                    <!-- <el-button @click="handleOpenAdd" style="font-size: 14px" type="text">{{$i18n.t('btnText.newTestEnvironment')}}</el-button> -->
                  </div>
                </div>
              </template>
            </el-select>
					</el-col>
					<el-col :span="protocolValue === protocol_type.Gateway ? 7 : 10">
						<el-input :disabled="isDetail" v-model="url" :placeholder="$i18n.t('placeholder.pleaseEnterAddress')" class="method-input sec-method-input" size="medium">
              <template v-if="protocolValue === protocol_type.Gateway && !isDetail" #prepend>{{gatewayPrepend}}</template>
            </el-input>
					</el-col>
				</el-row>
			</el-col>
			<el-col :span="2" style="text-align: right" :offset="0">
        <Btns :isGlobal="isGlobal" @onTest="handleSubmit" @onSave="handleSave" @onSaveAs="handleSaveAs"/>
				<!-- <el-button @click="handleSubmit" style="margin-left: 10px" type="primary" size="medium">{{$i18n.t('btnText.test')}}</el-button> -->
			</el-col>
		</el-row>
     <el-dialog
      :destroy-on-close="true"
      :center="false"
      width="480px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :title="$i18n.t('apiTest.newEnvironment')"
      v-model="addEnvVisible"
      append-to-body
    >
      <AddEnvDialog @onCancel="handleCancel" @onOk="handleAddOk"/>
    </el-dialog>
	</div>
</template>

<script>
import { PROTOCOL, REQUEST_TYPE, GATEWAY_REQUEST_TYPE, PROTOCOL_TYPE, AJAX_SUCCESS_MESSAGE, API_REQUEST_PARAM_TYPE } from '@/views/constant'
import { mapGetters } from 'vuex'
import AddEnvDialog from './AddEnvDialog.vue'
import { httpTest, getApiEnvById, updateHttpTestCase, saveHttpTestCase, saveGatewayTestCase, updateGatewayTestCase } from '@/api/apitest'
import { RADIO_TYPE } from '@/views/TestApi/constant'
import { PATH } from '@/router/constant'
import Btns from './Btns.vue'
import { Loading } from "@element-plus/icons-vue"
let msgRef

const GatewayPrepend = "/mtop"
const reqTimeOut = 20000

export default {
  name: 'HttpHeader',
  components: {
    AddEnvDialog,
    Btns
  },
  props: {
    isGlobal: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      protocolValue: PROTOCOL_TYPE.HTTP,
      requestTypeValue: undefined,
      env: undefined,
      url: '',
      httpType: "http://",
      addEnvVisible: false,
      isDetail: false,
      gatewayOptions: [{
        httpDomain: "xxxx",
        envName: this.$i18n.t("apiTestGatewayEnvOption.testEnvironmentSupportHttp&Https")
      }, {
        httpDomain: "xxxx",
        envName: this.$i18n.t("apiTestGatewayEnvOption.testEnvironmentSupportHttp")
      }, {
        httpDomain: "xxxx",
        envName: this.$i18n.t("apiTestGatewayEnvOption.testEnvironmentExternalDomainName")
      }, {
        httpDomain: "xxxx",
        envName: this.$i18n.t("apiTestGatewayEnvOption.intranetEnvironmentSupportHttpProtocol")
      }, {
        httpDomain: "xxxx",
        envName: this.$i18n.t("apiTestGatewayEnvOption.intranetEnvironmentSupportHttp&HttpsProtocols")
      }, {
        httpDomain: "xxxx",
        envName: this.$i18n.t("apiTestGatewayEnvOption.onlineEnvironment")
      }],
      httpOptions: [{
        name: "http://",
        value: "http://"
      }, {
        name: "https://",
        value: "https://"
      }]
    }
  },
  watch: {
    $route: {
      handler (val) {
        this.isDetail = val.path === PATH.API_DETAIL
      },
      immediate: true,
      deep: true
    },
    apiTestRequestType: {
      handler (val, old) {
        if ((val && !old) || (val && old && (val.toLocaleUpperCase() !== old.toLocaleUpperCase()))) {
          this.requestTypeValue = val
        }
      },
      immediate: true
    },
    apiTestUrl: {
      handler (val, old) {
        if (val !== old) {
          this.url = val
        }
      },
      immediate: true
    },
    httpEnv: {
      handler (val, old) {
        if (val !== old) {
          this.env = val
        }
      },
      immediate: true
    },
    apiTestProtocol: {
      handler (val) {
        this.protocolValue = val || PROTOCOL_TYPE.HTTP
      },
      immediate: true
    },
    protocolValue: {
      handler (val, old) {
        this.requestTypeValue = undefined
        this.env = undefined
        this.url = ''
        if (val !== old) {
          this.$store.dispatch('apitest/resetApiTest')
    	    this.$store.dispatch('apitest/changeApiTestTarget', { apiTestProtocol: val, httpEnv: undefined })
        }
      }
    },
    url: {
      handler (val) {
        if (this.requestTypeValue && this.requestTypeValue.toLocaleUpperCase() === 'GET') {
          let arr = []
          if (val && val.indexOf('?') !== -1) {
            let query = val.split('?')[1].split('&')
            query.forEach(item => {
              if (item.indexOf('=') !== -1) {
                arr.push({
                  paramKey: item.split('=')[0],
                  paramValue: item.split('=')[1]
                })
              }
            })
          }
    	    this.$store.dispatch('apitest/changeApiTestTarget', { query: arr, isRequestEnd: new Date().getTime() })
        }
      },
      immediate: true
    },
    env: {
      handler (val) {
        if (val && this.protocolValue === PROTOCOL_TYPE.HTTP) {
          this.$store.dispatch('apitest/resetDefaultHeader')
          getApiEnvById({
            envID: val
          }).then((data) => {
            if (data.message === AJAX_SUCCESS_MESSAGE) {
              let headers = data.data.headers || []
              try {
                headers = JSON.parse(headers)
              } catch (error) {}
              headers = headers.map(v => {
                return {
                  paramKey: v.headerName,
                  paramValue: v.headerValue,
                  isRequest: true
                }
              })
              // let header = !this.selectCaseId ? [] : this.apiTestHeaders
              let arr = this.$utils.deRepeat([...this.apiTestHeaders, ...headers, ...this.apiTestDefaultHeader], 'paramKey')
              this.$store.dispatch('apitest/changeApiTestTarget', {
                requestEnvHeader: arr.filter(v => v.isRequest),
                defaultHeader: arr.filter(v => v.default),
                headers: arr.filter(v => !v.default && !v.isRequest),
                isRequestEnd: new Date().getTime()
              })
            }
          }).catch(e => {})
        }
      },
      immediate: true,
      deep: true
    }
  },
  computed: {
    ...mapGetters([
      'apiTestProtocol',
      'apiTestHeaders',
      'apiTestQuery',
      'apiTestRequestType',
      'requestEnvHeader',
      'selectCaseId',
      'httpEnv',
      'apiTestUrl',
      'apiTestBody',
      'hostEnvList',
      'apiTestBodyType',
      'apiTestDefaultHeader',
      'apiTestJsonBody',
      'X5Param'
    ]),
    requestType () {
      return REQUEST_TYPE
    },
    protocol () {
      return PROTOCOL
    },
    protocol_type () {
      return PROTOCOL_TYPE
    },
    gateway_protocol_type () {
      return GATEWAY_REQUEST_TYPE
    },
    gatewayPrepend () {
      return GatewayPrepend
    }
  },
  beforeUnmount () {
    msgRef && msgRef.close && msgRef.close()
  },
  methods: {
    handleOpenAdd () {
      this.addEnvVisible = true
    },
    handleCancel () {
      this.addEnvVisible = false
    },
    handleAddOk () {
      let projectID = this.$utils.getQuery('projectID')
      this.$store.dispatch('apitest/getHostEnvList', projectID)
    },
    handleCheckSameKey (arg = [], key) {
      arg = this.handleEmpty(arg)
      let arr = this.$utils.unique(arg, key)
      return arg.length !== arr.length
    },
    handleEmpty (arr) {
      if (Array.isArray(arr)) {
        return arr.filter(v => v.paramKey && v.paramValue)
      } else {
        return []
      }
    },
    handleArrToObj (arr) {
      let obj = {}
      arr.forEach(v => {
        obj[v.paramKey] = v.paramValue
      })
      return obj
    },
    handleSubmit () {
      let { body, headers, requestTypeValue, url, host } = this.handleParam()
      if (!url) {
        return
      } else if (this.X5Param.useX5Filter && (!this.X5Param.appID || !this.X5Param.appkey)) {
        this.$message.error(this.$i18n.t('errorMessage.X5ProtocolNotEmpty'))
        return
      }
      msgRef && msgRef.close && msgRef.close()
      msgRef = this.$message({
        dangerouslyUseHTMLString: true,
        message: `${this.$i18n.t('loading')}...`,
        icon: <Loading color="#5897ff" />,
        customClass: 'test-loading-message',
        center: true,
        duration: 0,
      })
      if (this.apiTestBodyType === RADIO_TYPE.FORM) {
        headers['form_data'] = true
      }
      httpTest({
        method: requestTypeValue,
        url: `${this.protocolValue === PROTOCOL_TYPE.Gateway ? this.httpType : ""}${host.httpDomain}${(this.protocolValue === PROTOCOL_TYPE.Gateway && !this.isDetail) ? this.gatewayPrepend : ""}${url}`,
        timeout: reqTimeOut,
        headers: JSON.stringify(headers),
        body,
        ...this.X5Param
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$store.dispatch('apitest/changeApiTestTarget', { response: data.data })
          let dom = document.querySelector('#test-wrap')
          let top = document.querySelector('#api-test-return-container').offsetTop
          if (dom && top) {
            dom.scrollTo({ top })
          }
        }
      }).catch(e => {}).finally(() => {
        msgRef.close()
      })
    },
    handleParam () {
      if (!this.requestTypeValue) {
        this.$message.error(this.$i18n.t('errorMessage.selectRequestMethod'))
        return { body: '', headers: [], requestTypeValue: '', url: '', host: {} }
      }
      if (!this.env) {
        this.$message.error(this.$i18n.t('errorMessage.selectTestEnvironment'))
        return { body: '', headers: [], requestTypeValue: '', url: '', host: {} }
      }
      if (!this.url) {
        this.$message.error(this.$i18n.t('placeholder.pleaseEnterAddress'))
        return { body: '', headers: [], requestTypeValue: '', url: '', host: {} }
      }
      if (this.handleCheckSameKey([...this.apiTestDefaultHeader, ...this.requestEnvHeader, ...this.apiTestHeaders], 'paramKey')) {
        this.$message.error(this.$i18n.t('errorMessage.requestHeaderSameKey'))
        return { body: '', headers: [], requestTypeValue: '', url: '', host: {} }
      }
      if (this.requestTypeValue.toLocaleUpperCase() === 'GET' && this.handleCheckSameKey(this.apiTestQuery, 'paramKey')) {
        this.$message.error(this.$i18n.t('errorMessage.queryParameterSameKey'))
        return { body: '', headers: [], requestTypeValue: '', url: '', host: {} }
      } else if (this.apiTestBodyType === RADIO_TYPE.FORM && this.handleCheckSameKey(this.apiTestBody, 'paramKey')) {
        this.$message.error(this.$i18n.t('errorMessage.requestBodySameKey'))
        return { body: '', headers: [], requestTypeValue: '', url: '', host: {} }
      }
      let body = JSON.stringify(this.apiTestJsonBody)
      let headers = this.handleArrToObj(this.handleEmpty([...this.apiTestDefaultHeader, ...this.requestEnvHeader, ...this.apiTestHeaders]))
      let requestTypeValue = this.requestTypeValue.toLocaleLowerCase()
      if (requestTypeValue === 'get') {
        body = JSON.stringify(this.handleArrToObj(this.handleEmpty(this.apiTestQuery)))
      } else if (this.apiTestBodyType === RADIO_TYPE.FORM) {
        body = JSON.stringify(this.handleArrToObj(this.handleEmpty(this.apiTestBody)))
      }
      let url = this.url
      if (url.indexOf('?') !== -1) {
        url = url.split('?')[0]
      }
      let host = ''
      if (this.protocolValue === PROTOCOL_TYPE.HTTP) {
        host = this.hostEnvList.filter(v => v.id === this.env)[0]
      } else {
        host = this.gatewayOptions.filter(v => v.httpDomain === this.env)[0]
      }
      return { body, headers, requestTypeValue, url, host }
    },
    handleSave () {
      let { body, headers, requestTypeValue, url, host } = this.handleParam()
      if (!url) {
        return
      }
      if (this.protocolValue === PROTOCOL_TYPE.Gateway) {
        updateGatewayTestCase({
          id: this.selectCaseId,
          httpMethod: requestTypeValue,
          // url: `${this.httpType}${host.httpDomain}${(!this.isDetail) ? this.gatewayPrepend : ""}${url}`,
          url: url,
          requestTimeout: reqTimeOut,
          httpHeaders: JSON.stringify(headers),
          httpRequestBody: body,
          useX5Filter: this.X5Param.useX5Filter,
          x5AppKey: this.X5Param.useX5Filter ? this.X5Param.appkey : undefined,
          x5AppId: this.X5Param.useX5Filter ? this.X5Param.appID : undefined,
          x5Method: this.X5Param.useX5Filter ? this.X5Param.x5Method : undefined,
          gatewayDomain: this.env,
          httpReqBodyType: this.apiTestBodyType === RADIO_TYPE.FORM ? API_REQUEST_PARAM_TYPE.FORM_DATA : API_REQUEST_PARAM_TYPE.RAW
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.$message.success(this.$i18n.t("updateCompleted"))
          } else {
            this.$message.error(data.message)
          }
        }).catch(e => {})
      } else {
        updateHttpTestCase({
          id: this.selectCaseId,
          httpMethod: requestTypeValue,
          url: url,
          envId: this.env,
          requestTimeout: reqTimeOut,
          httpHeaders: JSON.stringify(headers),
          httpRequestBody: body,
          useX5Filter: this.X5Param.useX5Filter,
          x5AppKey: this.X5Param.useX5Filter ? this.X5Param.appkey : undefined,
          x5AppId: this.X5Param.useX5Filter ? this.X5Param.appID : undefined,
          x5Method: this.X5Param.useX5Filter ? this.X5Param.x5Method : undefined,
          httpReqBodyType: this.apiTestBodyType === RADIO_TYPE.FORM ? API_REQUEST_PARAM_TYPE.FORM_DATA : API_REQUEST_PARAM_TYPE.RAW
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.$message.success(this.$i18n.t("updateCompleted"))
          } else {
            this.$message.error(data.message)
          }
        }).catch(e => {})
      }
    },
    handleSaveAs (selectGroup) {
      let { body, headers, requestTypeValue, url, host } = this.handleParam()
      if (!url) {
        return
      }
      if (this.protocolValue === PROTOCOL_TYPE.Gateway) {
        saveGatewayTestCase({
          caseName: selectGroup.caseName,
          apiId: this.$utils.getQuery("apiID") || null,
          httpMethod: requestTypeValue,
          url: url,
          gatewayDomain: this.env,
          httpHeaders: JSON.stringify(headers),
          httpRequestBody: body,
          requestTimeout: reqTimeOut,
          useX5Filter: this.X5Param.useX5Filter,
          x5AppKey: this.X5Param.useX5Filter ? this.X5Param.appkey : undefined,
          x5AppId: this.X5Param.useX5Filter ? this.X5Param.appID : undefined,
          x5Method: this.X5Param.useX5Filter ? this.X5Param.x5Method : undefined,
          caseGroupId: selectGroup.caseGroupId,
          httpReqBodyType: this.apiTestBodyType === RADIO_TYPE.FORM ? API_REQUEST_PARAM_TYPE.FORM_DATA : API_REQUEST_PARAM_TYPE.RAW
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.$message.success(this.$i18n.t("savedSuccessfully"))
          } else {
            this.$message.error(data.message)
          }
        }).catch(e => {})
      } else {
        saveHttpTestCase({
          caseName: selectGroup.caseName,
          apiId: this.$utils.getQuery("apiID") || null,
          httpMethod: requestTypeValue,
          url: url,
          envId: this.env,
          requestTimeout: reqTimeOut,
          httpHeaders: JSON.stringify(headers),
          httpRequestBody: body,
          useX5Filter: this.X5Param.useX5Filter,
          x5AppKey: this.X5Param.useX5Filter ? this.X5Param.appkey : undefined,
          x5AppId: this.X5Param.useX5Filter ? this.X5Param.appID : undefined,
          x5Method: this.X5Param.useX5Filter ? this.X5Param.x5Method : undefined,
          caseGroupId: selectGroup.caseGroupId,
          httpReqBodyType: this.apiTestBodyType === RADIO_TYPE.FORM ? API_REQUEST_PARAM_TYPE.FORM_DATA : API_REQUEST_PARAM_TYPE.RAW
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.$message.success(this.$i18n.t("savedSuccessfully"))
          } else {
            this.$message.error(data.message)
          }
        }).catch(e => {})
      }
    }
  }
}
</script>

<style scoped>
.test-header-container .method-select >>> .el-input__wrapper{
	border-radius: 0;
}
.test-header-container .method-select >>> input:focus, .test-header-container .method-select >>> .el-input__wrapper:hover {
	position: relative;
	z-index: 1;
}
.test-header-container .first-method-select >>> .el-input__wrapper {
	border-top-left-radius: 4px;
	border-bottom-left-radius: 4px;
}
.test-header-container .method-input >>> .el-input__wrapper{
	border-radius: 0;
}
.test-header-container .method-input >>> input:focus, .test-header-container .method-input >>> .el-input__wrapper:hover {
	position: relative;
	z-index: 1;
}
.test-header-container .method-input.sec-method-input >>> .el-input__wrapper {
	border-top-right-radius: 4px;
	border-bottom-right-radius: 4px;
	margin-left: -1px;
}
.test-header-container .method-input.sec-method-input >>> .el-input-group__prepend{
  border-radius: 0;
  border-left: none;
}
</style>
