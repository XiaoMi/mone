<template>
  <div class="test-header-container">
		<el-row :gutter="0" type="flex" justify="space-between">
			<el-col :span="22">
				<el-row :gutter="0" type="flex" justify="space-between">

					<el-col :span="!isDetail ? 2 : 3">
						<el-select :disabled="isDetail" class="method-select first-method-select" v-model="protocolValue">
							<el-option v-for="v in Object.keys(protocol)" :key="v" :label="protocol[v]" :value="v"></el-option>
						</el-select>
					</el-col>

          <el-col :span="3" v-if="!isDetail">
            <el-select class="package-name-type" style="width: 100%; margin-left: -1px" v-model="packageNameType">
                <el-option
                  v-for="item in packageNameOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
          </el-col>

					<el-col v-if="!isDetail" :span="packageNameType === getPackageNameType.NOT ? 9 : 14">
						<el-input v-if="packageNameType === getPackageNameType.NOT" v-model="grpcParams.packageName" :placeholder="$i18n.t('placeholder.PleaseEnterPackageName')" style="margin: 0 0 0 -2px" class="method-input service-type">
            </el-input>
            <div v-else class="complex-wrap">
              <el-cascader
                v-model="packageNameInterfaceName"
                style="width: 100%; margin-left: -2px"
                :props="cascaderProps"
                :placeholder="$i18n.t('placeholder.enterProjectName')"
                :show-all-levels="false"
                filterable
                :before-filter="remoteMethod"
                :options="packageNameList"
              ></el-cascader>
            </div>
					</el-col>
					<el-col v-else :span="11">
						<el-input v-model="grpcParams.packageName" disabled :placeholder="$i18n.t('placeholder.PleaseEnterPackageName')" style="margin: 0 0 0 -5px" class="method-input "/>
					</el-col>

          <template v-if="packageNameType === getPackageNameType.NOT">
            <el-col v-if="!isDetail" :span="5">
              <el-input  v-model="grpcParams.interfaceName" :placeholder="$i18n.t('placeholder.pleaseEnterInterfaceName')" style="margin: 0 0 0 -6px" class="method-input "/>
            </el-col>
            <el-col v-else :span="5">
              <el-input v-model="grpcParams.interfaceName" disabled :placeholder="$i18n.t('placeholder.pleaseEnterInterfaceName')" style="margin: 0 0 0 -6px" class="method-input "/>
            </el-col>
          </template>

					<el-col v-if="!isDetail" :span="5">
            <el-input v-if="packageNameType === getPackageNameType.NOT" v-model="grpcParams.methodName" :placeholder="$i18n.t('placeholder.pleaseEnterMethodName')" style="margin: 0 0 0 -7px" class="method-input "/>
            <div v-else class="complex-wrap">
              <el-select style="width: 100%; margin-left: -4px" filterable v-model="grpcParams.methodName" :placeholder="$i18n.t('placeholder.pleaseSelectMethodName')">
                <el-option
                  v-for="item in getMethodList"
                  :key="item"
                  :label="item"
                  :value="item">
                </el-option>
              </el-select>
            </div>
          </el-col>
          <el-col v-else :span="5">
            <el-input v-model="grpcParams.methodName" disabled :placeholder="$i18n.t('placeholder.pleaseEnterMethodName')" style="margin: 0 0 0 -7px" class="method-input "/>
					</el-col>

				</el-row>
			</el-col>
			<el-col :span="2" style="text-align: right" :offset="0">
        <Btns :isGlobal="isGlobal" @onTest="handleSubmit" @onSave="handleSave" @onSaveAs="handleSaveAs"/>
				<!-- <el-button @click="handleSubmit" style="margin-left: 10px" type="primary">{{$i18n.t('btnText.test')}}</el-button> -->
			</el-col>
		</el-row>
	</div>
</template>

<script>
import { PROTOCOL, PROTOCOL_TYPE, AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { mapGetters } from 'vuex'
import { grpcTest, updateGrpcTestCase, saveGrpcTestCase } from '@/api/apitest'
import { loadGrpcServerAddr, loadGrpcService, loadGrpcApiInfos } from '@/api/apilist'
import { PATH } from '@/router/constant'
import debounce from "@/common/debounce"
import { Loading } from "@element-plus/icons-vue"
import Btns from './Btns.vue'

let msgRef

const packageNameValue = {
  /** 未配置 */
  NOT: 1,
  /** 已配置 */
  IS: 2
}

export default {
  name: 'GRPCHeader',
  components: {
    Btns
  },
  props: {
    isGlobal: {
      type: Boolean,
      default: false
    }
  },
  data () {
    let that = this
    return {
      protocolValue: PROTOCOL_TYPE.Grpc,
      isDetail: false,
      packageNameType: packageNameValue.NOT,
      packageNameInterfaceName: [],
      packageNameOptions: [{
        label: this.$i18n.t("grpcClass.projectNotConfigured"),
        value: packageNameValue.NOT
      }, {
        label: this.$i18n.t("grpcClass.projectIsConfigured"),
        value: packageNameValue.IS
      }],
      appName: "",
      packageNameList: [],
      moduleClassNameList: [],
      cascaderProps: {
        lazy: true,
        async lazyLoad (node, resolve) {
          if (node.value) {
            let arr = await that.handleLoadInfo(node.value)
            const nodes = arr.map(v => ({
              ...v,
              leaf: true
            }))
            resolve(nodes)
          } else {
            resolve([])
          }
        }
      }
    }
  },
  watch: {
    $route: {
      handler (val) {
        this.isDetail = val.path === PATH.API_DETAIL
        this.handleLoad()
      },
      immediate: true,
      deep: true
    },
    packageNameType: {
      handler (val, old) {
        if (val !== old && !this.isDetail) {
          this.$store.dispatch('apitest/resetApiTest')
    	    this.$store.dispatch('apitest/changeApiTestTarget', { apiTestProtocol: this.protocolValue, addrs: "" })
          this.packageNameInterfaceName = []
          this.moduleClassNameList = []
          this.packageNameList = []
        }
      }
    },
    protocolValue: {
      handler (val, old) {
        if (val !== old && !this.isDetail) {
          this.$store.dispatch('apitest/resetApiTest')
    	    this.$store.dispatch('apitest/changeApiTestTarget', { apiTestProtocol: val })
        }
      }
    },
    packageNameInterfaceName: {
      handler (val) {
        if (val.length) {
          let strs = val[1].split(".")
          let interfaceName = strs.splice(strs.length - 1, strs.length)
    	    this.$store.dispatch('apitest/changeGrpcParam', { packageName: strs.join("."), interfaceName: interfaceName[0] })
        }
      }
    },
    "grpcParams.interfaceName": {
      handler () {
        this.handleLoad()
      }
    },
    "grpcParams.packageName": {
      handler (val) {
        if (this.packageNameType === packageNameValue.IS && val) {
    	    this.$store.dispatch('apitest/changeGrpcParam', { methodName: undefined })
        }
      }
    }
  },
  computed: {
    ...mapGetters([
      "grpcParams",
      'apiTestProtocol',
      'selectCaseId',
      'apiDetail'
    ]),
    protocol () {
      return PROTOCOL
    },
    getPackageNameType () {
      return packageNameValue
    },
    getMethodList () {
      if (!this.moduleClassNameList.length || !this.packageNameInterfaceName.length) {
        return []
      }
      return this.moduleClassNameList.filter(v => v.value === this.packageNameInterfaceName[1])[0].apiList
    }
  },
  beforeUnmount () {
    msgRef && msgRef.close && msgRef.close()
  },
  methods: {
    remoteMethod: debounce(function (val) {
      if (val) {
        loadGrpcService({ serviceName: val }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.packageNameList = (data.data || []).map(v => {
              return {
                label: v.name,
                value: v.name,
                childern: []
              }
            })
          }
        }).catch(e => {})
      }
    }, 300),
    handleLoad: debounce(function () {
      if (this.isDetail) {
        this.appName = this.apiDetail.appName
        loadGrpcServerAddr({ appName: this.apiDetail.appName }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            if (data.data) {
              this.$store.dispatch('apitest/changeGrpcParam', {
                addrs: data.data
              })
            }
          }
        }).catch(e => {})
      }
    }, 200),
    handleLoadInfo (val) {
      this.appName = val
      return loadGrpcApiInfos({ appName: val }).then((data) => {
        let arr = []
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          if (data.data.grpcApiInfos) {
            arr = Object.keys(data.data.grpcApiInfos).map(key => {
              return {
                label: key,
                value: key,
                apiList: data.data.grpcApiInfos[key] || []
              }
            })
            this.$store.dispatch('apitest/changeGrpcParam', { addrs: `${data.data.ip}:${data.data.port}` })
          } else {
            arr = []
          }
        }
        this.moduleClassNameList = arr
        return arr
      }).catch(e => {})
    },
    handleSubmit () {
      if (!this.grpcParams.packageName) {
        this.$message.error(this.$i18n.t("placeholder.PleaseEnterPackageName"))
        return
      }
      if (!this.grpcParams.interfaceName) {
        this.$message.error(this.$i18n.t("placeholder.pleaseEnterInterfaceName"))
        return
      }
      if (!this.grpcParams.methodName) {
        this.$message.error(this.$i18n.t("placeholder.pleaseEnterMethodName"))
        return
      }
      if (!this.grpcParams.addrs) {
        this.$message.error(this.$i18n.t("errorMessage.pleaseEnterServiceAddress"))
        return
      }
      let parameter = this.grpcParams.parameter
      if (parameter) {
        if (Object.prototype.toString.call(parameter) === '[object Object]') {
          parameter = JSON.stringify(parameter)
        } else {
          this.$message.error(this.$i18n.t("errorMessage.parameterBodyFormatError"))
          return
        }
      } else {
        parameter = JSON.stringify({})
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
      grpcTest({
        packageName: this.grpcParams.packageName,
        interfaceName: this.grpcParams.interfaceName,
        methodName: this.grpcParams.methodName,
        parameter,
        timeout: this.grpcParams.timeout,
        addrs: this.grpcParams.addrs
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$store.dispatch('apitest/changeApiTestTarget', { response: {
            status: data.data.status || 200,
            cost: data.data.cost,
            size: data.data.size,
            content: data.data.content
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
      let parameter = this.grpcParams.parameter
      if (parameter) {
        if (Object.prototype.toString.call(parameter) === '[object Object]') {
          parameter = JSON.stringify(parameter)
        } else {
          this.$message.error(this.$i18n.t("errorMessage.parameterBodyFormatError"))
          return
        }
      } else {
        parameter = JSON.stringify({})
      }
      updateGrpcTestCase({
        id: this.selectCaseId,
        requestTimeout: this.grpcParams.timeout,
        appName: this.appName,
        packageName: this.grpcParams.packageName,
        interfaceName: this.grpcParams.interfaceName,
        methodName: this.grpcParams.methodName,
        grpcAddr: this.grpcParams.addrs,
        grpcParamBody: parameter
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message.success(this.$i18n.t("updateCompleted"))
        } else {
          this.$message.error(data.message)
        }
      }).catch(e => {})
    },
    handleSaveAs (selectGroup) {
      let parameter = this.grpcParams.parameter
      if (parameter) {
        if (Object.prototype.toString.call(parameter) === '[object Object]') {
          parameter = JSON.stringify(parameter)
        } else {
          this.$message.error(this.$i18n.t("errorMessage.parameterBodyFormatError"))
          return
        }
      } else {
        parameter = JSON.stringify({})
      }
      saveGrpcTestCase({
        caseName: selectGroup.caseName,
        apiId: this.$utils.getQuery("apiID") || null,
        requestTimeout: this.grpcParams.timeout,
        appName: this.appName,
        packageName: this.grpcParams.packageName,
        interfaceName: this.grpcParams.interfaceName,
        methodName: this.grpcParams.methodName,
        grpcAddr: this.grpcParams.addrs,
        grpcParamBody: parameter,
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
.test-header-container .method-select >>> input:focus, .test-header-container .method-select >>> .el-input__wrapper:hover {
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
.test-header-container .method-input >>> .el-input__wrapper{
	border-radius: 0;
}
.test-header-container .method-input.service-type >>> .el-input-group__prepend {
  border-radius: 0;
}
.test-header-container .method-input >>> .el-input__wrapper:focus, .test-header-container .method-input >>> .el-input__wrapper:hover {
	position: relative;
	z-index: 1;
}
.test-header-container .method-input.sec-method-input >>> .el-input__wrapper {
	border-top-right-radius: 4px;
	border-bottom-right-radius: 4px;
	margin-left: -1px;
}
.test-header-container .package-name-type >>> .el-input__wrapper {
  border-radius: 0;
}
.test-header-container .complex-wrap >>> .el-input__wrapper {
  border-radius: 0;
}
</style>
