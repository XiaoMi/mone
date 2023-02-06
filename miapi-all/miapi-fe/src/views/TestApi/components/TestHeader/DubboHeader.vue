<template>
  <div class="test-header-container">
		<el-row :gutter="0" type="flex" justify="space-between">
			<el-col :span="22">
				<el-row :gutter="0" type="flex" justify="space-between">
					<el-col :span="3">
						<el-select :disabled="isDetail" class="method-select first-method-select" v-model="protocolValue">
							<el-option v-for="v in Object.keys(protocol)" :key="v" :label="protocol[v]" :value="v"></el-option>
						</el-select>
					</el-col>
					<el-col :span="3">
						<el-select class="method-select sec-method-select" v-model="env" :placeholder="`${$i18n.t('expm')}:staging`">
							<el-option v-for="k in Object.keys(environment)" :key="k" :label="environment[k].label" :value="environment[k].value">{{environment[k].name}}</el-option>
						</el-select>
					</el-col>
					<el-col v-if="!isDetail" :span="13">
						<div class="name-method">
              <el-input v-show="env === environment.cloud_dev.value" v-model="namespace" placeholder="namespace" style="width: 130px"/>
              <el-select class="method-select sec-method-select" style="width: 100%; margin-left: -1px" filterable remote v-model="serviceName" :remote-method="remoteMethod" :placeholder="$i18n.t('placeholder.pleaseEnterServiceName')">
                <el-option v-for="v in serviceNameList" :key="v.name" :label="v.name" :value="v.name"></el-option>
              </el-select>
            </div>
					</el-col>
					<el-col v-else :span="13">
            <el-input v-model="serviceName" disabled :placeholder="$i18n.t('placeholder.pleaseEnterServiceName')" style="margin: 0 0 0 -2px" class="method-input "/>
					</el-col>
					<el-col v-if="!isDetail" :span="5">
            <el-select class="method-select dubbo-method-select" style="margin: 0 0 0 -5px;width: 100%" :disabled="!serviceName" v-model="apiName" :placeholder="$i18n.t('placeholder.pleaseEnter')">
							<el-option v-for="item in serviceList" :key="item" :label="item" :value="item"></el-option>
						</el-select>
					</el-col>
          <el-col v-else :span="5">
            <el-input v-model="apiName" disabled :placeholder="$i18n.t('placeholder.pleaseEnter')" style="margin: 0 0 0 -5px" class="method-input "/>
					</el-col>
				</el-row>
			</el-col>
			<el-col :span="2" style="text-align: right" :offset="0">
        <Btns :isGlobal="isGlobal" @onApply="handleApply" @onTest="handleSubmit" @onSave="handleSave" @onSaveAs="handleSaveAs"/>
				<!-- <el-button @click="handleSubmit" style="margin-left: 10px" type="primary">{{$i18n.t('btnText.test')}}</el-button> -->
			</el-col>
		</el-row>
	</div>
</template>

<script>
import { PROTOCOL, REQUEST_TYPE, AJAX_SUCCESS_MESSAGE, DUBBO_ENVIRONMENT } from '@/views/constant'
import { mapGetters } from 'vuex'
import { dubboTest, getServiceMethod, saveDubboTestCase, updateDubboTestCase, applyOnlineDubboTest } from '@/api/apitest'
import { loadDubboApiServices } from '@/api/apilist'
import { PATH } from '@/router/constant'
import Btns from './Btns.vue'
import { Loading } from "@element-plus/icons-vue"

let msgRef

export default {
  name: 'DubboHeader',
  components: {
    Btns
  },
  data () {
    return {
      protocolValue: '3',
      env: DUBBO_ENVIRONMENT.staging.value,
      serviceName: undefined,
      serviceNameList: [],
      serviceList: [],
      apiName: undefined,
      namespace: 'default',
      isDetail: false
    }
  },
  props: {
    isGlobal: {
      type: Boolean,
      default: false
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
    env: {
      handler () {
        this.namespace = 'default'
      }
    },
    apiTestProtocol: {
      handler (val) {
        this.protocolValue = val
      },
      immediate: true
    },
    protocolValue: {
      handler (val, old) {
        if (val !== old && !this.isDetail) {
          this.$store.dispatch('apitest/resetApiTest')
    	    this.$store.dispatch('apitest/changeApiTestTarget', { apiTestProtocol: val })
        }
      }
    },
    serviceName: {
      handler (val) {
        if (this.isClickCase) {
          this.$nextTick(() => {
            this.$store.dispatch('apitest/changeApiTestTarget', {
              isClickCase: false
            })
          })
        }else if (val && !this.isDetail) {
          this.serviceList = []
          let arr = []
          if (val.indexOf(':') !== -1) {
            arr = val.split(':')
          } else {
            arr.push(val)
          }
          let obj = {
            interfaceName: arr[1] || arr[0],
            methodName: undefined
          }
          if (arr.length === 4) {
            obj.version = arr[2]
            obj.group = arr[3]
          } else if (/\d/.test(arr[2])) {
            obj.version = arr[2]
            obj.group = ''
          } else {
            obj.group = arr[2]
            obj.version = ''
          }
          getServiceMethod({
            serviceName: val,
            env: this.env
          }).then((data) => {
            if (data.message === AJAX_SUCCESS_MESSAGE) {
              this.serviceList = data.data || []
              return data.data || []
            }
          }).catch(e => {}).finally(() => {
            if (this.serviceList.includes(this.apiTestMethodName)) {
              obj.methodName = this.apiTestMethodName
            } else {
              this.apiName = undefined
            }
            this.$store.dispatch('apitest/changeApiTestTarget', obj)
          })
        }
      },
      immediate: true,
      deep: true
    },
    dubboTestEnv: {
      handler (val) {
        if (val) {
          this.env = val
        }
      },
      immediate: true
    },
    apiName: {
      handler (val) {
        if (!this.isDetail) {
          this.$store.dispatch('apitest/changeApiTestTarget', {
            methodName: val
          })
        }
      },
      immediate: true
    },
    serviceNameGroup: {
      handler (newVal, old) {
        if (newVal !== old && this.isDetail) {
          this.env = newVal
        }
      },
      immediate: true
    },
    apiTestInterfaceName: {
      handler (newVal, old) {
        // if (newVal !== old && this.isDetail) {
        if (newVal !== old && newVal) {
          let str = `providers:${newVal}`
          if (this.apiTestVersion) {
            str += `:${this.apiTestVersion}`
          }
          if (this.apiTestGroup) {
            str += `:${this.apiTestGroup}`
          }
          this.serviceName = str
        }
      },
      immediate: true
    },
    namespace: {
      handler (val) {
        if (!this.isDetail) {
          this.$store.dispatch('apitest/changeApiTestTarget', {
            dubboNamespace: val
          })
        }
      },
      immediate: true
    },
    dubboNamespace: {
      handler (newVal, old) {
        if (newVal !== old && this.isDetail) {
          this.namespace = newVal
        }
      },
      immediate: true
    },
    apiTestMethodName: {
      handler (newVal, old) {
        // if (newVal !== old && this.isDetail) {
        if (newVal !== old) {
          this.apiName = newVal
        }
      },
      immediate: true,
      deep: true
    }
  },
  computed: {
    ...mapGetters([
      'apiTestProtocol',
      'apiTestVersion',
      'apiTestGroup',
      'serviceNameGroup',
      'apiTestInterfaceName',
      'apiTestRetries',
      'selectCaseId',
      'apiTestIsGenParam',
      'apiTestMethodName',
      'apiTestDubboTimeout',
      'apiTestParameter',
      "isClickCase",
      'apiTestAddr',
      'hasAttachment',
      'dubboTestTag',
      'dubboNamespace',
      'apiTestAttachments',
      'apiTestParamType',
      'dubboTestEnv'
    ]),
    requestType () {
      return REQUEST_TYPE
    },
    protocol () {
      return PROTOCOL
    },
    environment () {
      return DUBBO_ENVIRONMENT
    }
  },
  beforeUnmount () {
    msgRef && msgRef.close && msgRef.close()
  },
  methods: {
    remoteMethod (word) {
      if (/\p{Unified_Ideograph}/u.test(word) || /(^\s+)|(\s+$)|\s+/g.test(word)) {
        this.$message.error(this.$i18n.t('errorMessage.serviceNameNotSpacesCharacters'))
        return
      }
      if (word !== '') {
        loadDubboApiServices({ env: this.env, serviceName: word, namespace: this.namespace }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.serviceNameList = data.data || []
          }
        }).catch(e => {})
      }
    },
    handleApply () {
      applyOnlineDubboTest({
        group: this.apiTestGroup,
        version: this.apiTestVersion,
        serviceName: this.apiTestInterfaceName,
        projectId: this.$utils.getQuery("projectID")
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message.success(this.$i18n.t("successfulApplication"))
        } else {
          this.$message.error(data.message)
        }
      }).catch(e => {})
    },
    handleSubmit () {
      let ip
      if (this.apiTestAddr && (this.apiTestAddr.indexOf(':') !== -1)) {
        ip = this.apiTestAddr.split(':')[0]
      }
      if (!this.apiTestInterfaceName) {
        this.$message.error(this.$i18n.t('placeholder.pleaseEnterServiceName'))
        return
      } else if (!this.apiTestMethodName) {
        this.$message.error(this.$i18n.t('placeholder.pleaseSelectMethodName'))
        return
      } else if (isNaN(parseInt(this.apiTestRetries))) {
        this.$message.error(this.$i18n.t('errorMessage.pleaseEnterCorrectRetries'))
        return
      }
      msgRef && msgRef.close && msgRef.close()
      msgRef = this.$message({
        dangerouslyUseHTMLString: true,
        message: `${this.$i18n.t('loading')}...`,
        icon: <Loading color="#5897ff" />,
        customClass: 'test-loading-message',
        center: true,
        duration: 0
      })
      dubboTest({
        interfaceName: this.apiTestInterfaceName,
        methodName: this.apiTestMethodName,
        group: this.apiTestGroup,
        version: this.apiTestVersion,
        production: this.env === DUBBO_ENVIRONMENT.online.value,
        ip,
        retries: this.apiTestRetries || 0,
        paramType: this.apiTestParamType,
        parameter: JSON.stringify(this.apiTestParameter),
        timeout: this.apiTestDubboTimeout,
        genParam: this.apiTestIsGenParam,
        addr: this.apiTestAddr || undefined,
        attachment: this.hasAttachment ? JSON.stringify(this.apiTestAttachments) : undefined,
        dubboTag: this.dubboTestTag || undefined
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$store.dispatch('apitest/changeApiTestTarget', { response: {
            status: data.data.status || 200,
            cost: data.data.cost,
            size: data.data.size,
            content: data.data.res
          } })
        }
        let dom = document.querySelector('#test-wrap')
        let top = document.querySelector('#api-test-return-container').offsetTop
        if (dom && top) {
          dom.scrollTo({ top })
        }
      }).catch(e => {}).finally(() => {
        msgRef.close()
      })
    },
    handleSave () {
      updateDubboTestCase({
        id: this.selectCaseId,
        requestTimeout: this.apiTestDubboTimeout,
        dubboInterface: this.apiTestInterfaceName,
        dubboMethodName: this.apiTestMethodName,
        dubboGroup: this.apiTestGroup,
        dubboVersion: this.apiTestVersion,
        retry: this.apiTestRetries || 0,
        env: this.env,
        dubboAddr: this.apiTestAddr || undefined,
        dubboParamType: this.apiTestParamType,
        dubboParamBody: JSON.stringify(this.apiTestParameter),
        useGenericParam: this.apiTestIsGenParam,
        useAttachment: this.hasAttachment,
        attachment: this.hasAttachment ? JSON.stringify(this.apiTestAttachments) : ""
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message.success(this.$i18n.t("updateCompleted"))
        } else {
          this.$message.error(data.message)
        }
      }).catch(e => {})
    },
    handleSaveAs (selectGroup) {
      saveDubboTestCase({
        caseName: selectGroup.caseName,
        apiId: this.$utils.getQuery("apiID") || null,
        requestTimeout: this.apiTestDubboTimeout,
        dubboInterface: this.apiTestInterfaceName,
        dubboMethodName: this.apiTestMethodName,
        dubboGroup: this.apiTestGroup,
        dubboVersion: this.apiTestVersion,
        retry: this.apiTestRetries || 0,
        env: this.env,
        dubboAddr: this.apiTestAddr || undefined,
        dubboParamType: this.apiTestParamType,
        dubboParamBody: JSON.stringify(this.apiTestParameter),
        useGenericParam: this.apiTestIsGenParam,
        useAttachment: this.hasAttachment,
        attachment: this.hasAttachment ? JSON.stringify(this.apiTestAttachments) : "",
        caseGroupId: selectGroup.caseGroupId
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
</script>

<style scoped>
.test-header-container .method-select >>> .el-input__wrapper{
	border-radius: 0;
}
.test-header-container .method-select >>> input:focus, .test-header-container .method-select >>> input:hover {
	position: relative;
	z-index: 1;
}
.test-header-container .first-method-select >>> .el-input__wrapper {
	border-top-left-radius: 4px;
	border-bottom-left-radius: 4px;
}
.test-header-container .sec-method-select >>> .el-input__wrapper {
	margin-left: -1px;
}
.test-header-container .dubbo-method-select  >>> .el-input__wrapper {
	border-top-right-radius: 4px;
	border-bottom-right-radius: 4px;
}
.test-header-container .method-input >>> input:focus, .test-header-container .method-input >>> input:hover {
	position: relative;
	z-index: 1;
}
.test-header-container .name-method {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-left: -2px;
}
.test-header-container .name-method .el-input >>> .el-input__wrapper {
  border-radius: 0;
}
</style>
