<template>
	<div class="base-info">
		<el-form :label-position="isEN ? 'top': 'right'" ref="ruleForm" :rules="rules" label-width="86px" :model="{...dubboParam, serviceName, env, inputApiName }" >
			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item prop="addMethod" :label="`${$i18n.t('newMethod')}:`">
						<el-radio-group v-model="addMethod">
							<el-radio-button v-for="(item, index) in radioBtn" :key="index" :label="item.value">{{item.name}}</el-radio-button>
						</el-radio-group>
					</el-form-item>
				</el-col>
			</el-row>

      <template v-if="!isDubboNewAdd">
        <el-row type="flex" justify="center">
          <el-col :span="20">
            <el-form-item prop="serviceName" :label="`${$i18n.t('serviceAddress')}:`">
              <div class="service-name-selects">
                <div :class="{'service-wrap': true, 'multInput':env === environmentOption.cloud_dev.value }">
                  <el-select style="width: 90px" v-model="env" :placeholder="`${$i18n.t('placeholder.pleaseChoose')}`">
                    <el-option v-for="k in Object.keys(environmentOption)" :key="k" :label="environmentOption[k].label" :value="environmentOption[k].value">{{environmentOption[k].name}}</el-option>
                  </el-select>
                  <el-input placeholder="namespace" v-model="namespace" v-show="env === environmentOption.cloud_dev.value" style="width: 120px"/>
                  <el-select style="flex-grow: 1" filterable remote v-model="serviceName" :remote-method="remoteMethod" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`">
                    <el-option v-for="v in serviceNameList" :key="v.name" :label="v.name" :value="v.name"></el-option>
                  </el-select>
                  <!-- <el-button type="primary" :disabled="!this.serviceName" @click="handleClickLoad">{{$i18n.t('load')}}</el-button> -->
                </div>
                <el-select class="ip-port-list" :disabled="!ipPortList.length" v-model="curIpPort" :placeholder="`${$i18n.t('placeholder.pleaseChoose')}`">
                  <el-option
                    v-for="item in ipPortList"
                    :key="item"
                    :label="item"
                    :value="item">
                  </el-option>
                </el-select>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        <!-- <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item :label="`${$i18n.t('serviceEnvironment')}:`">
              <el-radio-group v-model="apiEnv">
                <el-radio-button v-for="v in Object.keys(environmentOption)" :key="v" :label="environmentOption[v]">{{environmentOption[v]}}</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row> -->
        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item :label="`${$i18n.t('importDetails')}:`">
              <ControllerTable :isDubbo="true" ref="controllerTable" :serviceName="getServiceName" :list="getControllerList"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item :label="`${$i18n.t('dataSynchronization')}:`">
              <el-radio-group v-model="forceUpdate" size="mini">
                <el-radio-button :label="1">{{$i18n.t('fullCoverage')}}</el-radio-button>
                <el-radio-button :label="0">{{$i18n.t('normalMode')}}</el-radio-button>
              </el-radio-group>
              <el-tooltip placement="top" effect="dark">
                <template #content>
                  <div>
                    <p style="font-size: 14px">{{$i18n.t('normalMode')}}:</p>
                    <p style="font-size: 12px;margin-bottom: 8px">{{$i18n.t('notImportExistingInterfaces')}}</p>
                    <p style="font-size: 14px">{{$i18n.t('fullCoverage')}}:</p>
                    <!-- <p style="font-size: 12px">不保留旧数据，完全使用新数据，适用于接</p> -->
                    <p style="font-size: 12px">{{$i18n.t('notImportExistingInterfacesTips')}}</p>
                  </div>
                </template>
                <el-icon style="margin-left: 8px" color="rgba(0, 0, 0, 0.44)" :size="14"><InfoFilled /></el-icon>
              </el-tooltip>
            </el-form-item>
          </el-col>
        </el-row>
      </template>

      <el-row v-if="isDubboNewAdd" type="flex" justify="center" :gutter="20">
        <el-col :span="20">
          <el-form-item :label="`${$i18n.t('serviceEnvironment')}:`">
            <el-radio-group v-model="dubboParam.apiEnv">
              <el-radio-button v-for="k in Object.keys(environmentOption)" :key="k" :label="environmentOption[k].value">{{environmentOption[k].name}}</el-radio-button>            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row v-if="isDubboNewAdd && dubboParam.apiEnv === environmentOption.cloud_dev.value" type="flex" justify="center" :gutter="20">
        <el-col :span="20">
          <el-form-item prop="namespace" :label="`${$i18n.t('nameSpace')}:`">
            <el-input :style="styleWidth" placeholder="namespace" v-model="dubboParam.namespace"/>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row v-if="isDubboNewAdd" type="flex" justify="center" :gutter="20">
        <el-col :span="20">
          <el-form-item prop="groupId" :label="`${$i18n.t('ApiClass.category')}:`">
            <el-select :style="styleWidth" v-model="dubboParam.groupId" :placeholder="`${$i18n.t('placeholder.pleaseChoose')}`">
              <el-option v-for="item in groupOption" :key="item.groupID" :label="item.groupName" :value="item.groupID"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

			<el-row v-if="isDubboNewAdd" type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item prop="apiModelClass" :label="`${$i18n.t('ApiClass.serviceName')}:`">
						<el-input :style="styleWidth" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="dubboParam.apiModelClass"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

      <el-row v-if="isDubboNewAdd" type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item prop="inputApiName" :label="`${$i18n.t('ApiClass.methodName')}:`">
						<el-input :style="styleWidth" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="inputApiName"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row v-if="showOther" type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item prop="name" :label="`${$i18n.t('interfaceName')}:`">
						<el-input :style="styleWidth" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="dubboParam.name"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row v-if="showOther" type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item prop="apiDocName" :label="`${$i18n.t('interfaceDescription')}:`">
						<el-input :style="styleWidth" :disabled="!isDubboNewAdd" :rows="3" type="textarea" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="dubboParam.apiDocName" autocomplete="off"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row v-if="showOther" type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item prop="apiVersion" :label="`${$i18n.t('serviceVersion')}:`">
						<el-input :style="styleWidth" :disabled="!isDubboNewAdd" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="dubboParam.apiVersion"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row v-if="showOther" type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item prop="apiGroup" :label="`${$i18n.t('serviceGrouping')}:`">
						<el-input :style="styleWidth" :disabled="!isDubboNewAdd" :placeholder="`${$i18n.t('placeholder.enterAPIGroup')}`" v-model="dubboParam.apiGroup"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row v-if="showOther" type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item :label="`${$i18n.t('serviceDescription')}:`">
						<el-input :style="styleWidth" :rows="3" type="textarea" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="dubboParam.apiDesc" autocomplete="off"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row v-if="showOther" type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item :label="`${$i18n.t('asynchronousCall')}:`">
						<el-switch
							v-model="dubboParam.async">
						</el-switch>
						<span style="font-size: 12px;color: rgba(0, 0, 0, 0.44);margin-left: 16px">{{$i18n.t('definitionTips')}}</span>
					</el-form-item>
				</el-col>
			</el-row>

      <el-row v-if="isDubboNewAdd" type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item>
						<el-button @click="handleBack">{{$i18n.t('btnText.saveReturn')}}</el-button>
						<el-button @click="handleok" type="primary">{{$i18n.t('btnText.nextStep')}}</el-button>
					</el-form-item>
				</el-col>
			</el-row>
			<el-row v-else type="flex" justify="center" :gutter="20">
				<el-col :span="20">
          <el-form-item v-if="!isDubboNewAdd">
						<el-button @click="handleBatchSubmit" type="primary">{{$i18n.t('btnText.confirmSave')}}</el-button>
					</el-form-item>
					<el-form-item v-else>
						<el-button @click="handleBack">{{$i18n.t('btnText.saveReturn')}}</el-button>
						<el-button @click="handleok" type="primary">{{$i18n.t('btnText.nextStep')}}</el-button>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
	</div>
</template>

<script>
import { PROTOCOL, REQUEST_TYPE, DUBBO_ENVIRONMENT, AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { mapGetters } from 'vuex'
import { loadDubboApiInfos, getDubboApiDetailRemote, loadDubboApiServices, batchAddDubboApi } from '@/api/apilist'
import { PATH } from '@/router/constant'
import { RADIO_BTN } from '../../constant'
import ControllerTable from '../../ControllerTable'
import debounce from "@/common/debounce"

export default {
  name: 'DubboBaseInfo',
  components: {
    ControllerTable
  },
  data () {
    return {
      styleWidth: {
        width: '500px'
      },
      addMethod: false,
      namespace: 'default',
      isEN: false,
      // apiEnv: ENVIRONMENT.staging,
      serviceName: '',
      serviceNameList: [],
      serviceList: [],
      env: 'staging',
      showOther: false,
      inputApiName: undefined,
      forceUpdate: 0,
      curIpPort: undefined,
      ipPortList: [],
      rules: {
        groupId: [
          { required: true, message: this.$i18n.t('placeholder.selectGroup'), trigger: ['blur', 'change'] }
        ],
        inputApiName: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnter'), trigger: ['blur', 'change'] }
        ],
        apiModelClass: [
          { required: true, message: this.$i18n.t('placeholder.serviceNotEmpty'), trigger: ['blur', 'change'] }
        ],
        name: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnter'), trigger: ['blur', 'change'] }
        ],
        apiDocName: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnter'), trigger: ['blur', 'change'] }
        ],
        apiVersion: [
          { required: false, message: this.$i18n.t('placeholder.pleaseEnter'), trigger: ['blur', 'change'] }
        ],
        serviceName: [
          { required: true, message: this.$i18n.t('errorMessage.notSpacesAndCharacters'), trigger: ['blur', 'change'] }
        ],
        apiGroup: [
          { required: false, message: this.$i18n.t('placeholder.pleaseEnter'), trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  beforeMount () {
    this.isEN = this.$utils.languageIsEN()
    if (this.isDubboNewAdd) {
      this.showOther = true
    } else {
      this.showOther = false
    }
    // if (this.dubboServiceName) {
    //   this.serviceName = this.dubboServiceName
    //   if (this.env && !this.isDubboNewAdd) {
    //     this.handleClickLoad()
    //   }
    // }
    if (!this.dubboParam.groupId) {
      this.$store.dispatch('apilist.add/changeDubboParam', { groupId: this.groupID > 0 ? this.groupID : undefined })
    }
  },
  computed: {
    ...mapGetters([
      'groupList',
      'dubboParam',
      'addApiStep',
      'isDubboNewAdd',
      'dubboServiceName',
      'groupID',
      'dubboIpPort'
    ]),
    protocol () {
      return PROTOCOL
    },
    requestType () {
      return REQUEST_TYPE
    },
    projectID () {
      return this.$utils.getQuery('projectID')
    },
    environmentOption () {
      return DUBBO_ENVIRONMENT
    },
    groupOption () {
      return this.groupList.filter(item => item.groupID > 0) || []
    },
    radioBtn () {
      return RADIO_BTN
    },
    getServiceName () {
      if (this.serviceName) {
        return this.serviceName.split(':')[1]
      } else {
        return ''
      }
    },
    getControllerList () {
      return this.serviceList.map(item => {
        return {
          apiList: item.moduleApiList,
          moduleClassName: item.moduleClassName
        }
      })
    }
  },
  watch: {
    isDubboNewAdd: {
      handler (bool) {
        if (this.addMethod !== bool) {
          this.addMethod = bool
        }
        let groupId = this.dubboParam.groupId || (this.groupID > 0 ? this.groupID : undefined)
        this.$store.dispatch('apilist.add/resetDubboParam', false)
        this.$store.dispatch('apilist.add/changeDubboParam', { groupId })
        this.showOther = bool
      },
      immediate: true
    },
    addMethod (val) {
      this.$refs.ruleForm.clearValidate()
      this.$store.dispatch('apilist.add/changeDubboNewAdd', val)
    },
    serviceName (val, old) {
      if (val) {
        this.$store.dispatch('apilist.add/changeDubboServiceName', val)
      }
      if (val && (val !== old)) {
        this.$store.dispatch('apilist.add/changeIpPort', undefined)
        this.ipPortList = []
        this.$nextTick(() => {
          this.handleClickLoad()
        })
      }
    },
    inputApiName: {
      handler (val) {
        this.$store.dispatch('apilist.add/changeDubboParam', { apiName: val })
      },
      deep: true
    },
    dubboIpPort: {
      handler (val, old) {
        if (val !== old) {
          this.curIpPort = val
        }
      },
      immediate: true
    },
    curIpPort: {
      handler (val, old) {
        if (val !== old) {
          this.$store.dispatch('apilist.add/changeIpPort', val)
        }
        if (val) {
          this.handleClickLoad()
        }
      },
      immediate: true
    },
    env: {
      handler () {
        this.namespace = 'default'
      }
    }
  },
  methods: {
    remoteMethod (word) {
      if (/\p{Unified_Ideograph}/u.test(word) || /(^\s+)|(\s+$)|\s+/g.test(word)) {
        this.$refs.ruleForm.validateField('serviceName')
        return
      } else {
        this.$refs.ruleForm.clearValidate('serviceName')
      }
      if (word !== '') {
        let param = { env: this.env, serviceName: word, namespace: this.namespace }
        loadDubboApiServices(param).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.serviceNameList = data.data || []
          }
        }).catch(e => {})
      }
    },
    handleNewAdd () {
      this.$store.dispatch('apilist.add/changeDubboNewAdd', !this.isDubboNewAdd)
      this.$refs.ruleForm.clearValidate()
    },
    handleClickLoad () {
      if (!this.serviceName) {
        return
      }
      let param = {
        serviceName: this.serviceName,
        env: this.env
      }
      if (this.curIpPort) {
        param.ip = this.curIpPort
      }
      loadDubboApiInfos(param).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          if (!this.curIpPort) {
            if (Array.isArray(data.data.ipAndPort)) {
              this.$store.dispatch('apilist.add/changeIpPort', data.data.ipAndPort[0])
              this.ipPortList = data.data.ipAndPort
            } else {
              this.ipPortList = []
              this.$store.dispatch('apilist.add/changeIpPort', data.data.ipAndPort)
            }
          }
          this.serviceList = data.data.list || []
          // if (data.data && data.data.list && data.data.list.length) {
          //   let names = this.serviceName.split(":")
          //   let arr = data.data.list.filter(v => names.includes(v.moduleClassName))
          //   if (arr.length) {
          //     this.$store.dispatch('apilist.add/changeDubboParam', { apiModelClass: arr[0].moduleClassName })
          //   } else {
          //     this.$store.dispatch('apilist.add/changeDubboParam', { apiModelClass: data.data.list[0].moduleClassName })
          //   }
          // }
        }
      }).catch(e => {
        this.serviceList = []
        this.ipPortList = []
        this.serviceName = ''
        this.$store.dispatch('apilist.add/changeIpPort', undefined)
        this.$store.dispatch('apilist.add/changeDubboServiceName', undefined)
      })
    },
    handleCheck () {
      return new Promise((resolve, reject) => {
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            resolve()
          } else {
            reject(new Error(this.$i18n.t('errorMessage.checkError')))
          }
        })
      })
    },
    handleok () {
      this.handleCheck().then(() => {
        this.$store.dispatch('apilist.add/changeStep', 2)
      }).catch(e => {})
    },
    handleBack () {
      this.handleCheck().then(this.handleSubmit).catch(e => {})
    },
    handleSubmit () {
      this.$store.dispatch('apilist.add/handleSubmit')
    },
    handleBatchSubmit () {
      this.handleCheck().then(() => {
        let arr = this.$refs.controllerTable.handleGetSelect()
        if (!arr || !arr.length) {
          this.$message.error(this.$i18n.t('errorMessage.pleaseSelectImportAPI'))
          return
        } else if (arr.some(v => !v.groupId)) {
          this.$message.error(this.$i18n.t('placeholder.selectGroup'))
          return
        } else if (arr.every(v => !v.selectedNum)) {
          this.$message.error(this.$i18n.t('errorMessage.pleaseSelectAnAPI'))
          return
        }
        arr = arr.map(item => {
          let apiNames = []
          item.apiList.forEach(v => {
            if (v.checked) {
              apiNames.push(v.apiName)
            }
          })
          return {
            port: this.curIpPort.split(':')[1],
            ip: this.curIpPort.split(':')[0],
            forceUpdate: !!this.forceUpdate,
            projectID: this.$utils.getQuery('projectID'),
            moduleClassName: item.moduleClassName,
            apiNames,
            env: this.env,
            groupID: item.groupId
          }
        })
        batchAddDubboApi({ bos: arr, apiEnv: this.env, namespace: this.namespace }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.$message.success(this.$i18n.t('savedSuccessfully'))
            this.$store.dispatch('apilist.add/changeStep', 1)
            this.$store.dispatch('apilist.add/resetGatewayParam')
            this.$store.dispatch('apilist.group/getGroupViewList', this.$utils.getQuery('projectID'))
            this.$router.push({ path: PATH.API, query: { projectID: this.$utils.getQuery('projectID') } })
          }
        }).catch(e => {})
      }).catch(e => {})
    }
  }
}
</script>

<style scoped>
.base-info .service-tips {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.64);
	line-height: 20px;
	display: inline-block;
	margin: 8px 0 0;
}
.base-info .service-tips span {
	cursor: pointer;
	color: #1890FF;
}
.base-info .service-wrap.multInput {
  width: 640px;
}
.base-info .service-name-selects {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.base-info .service-name-selects .ip-port-list >>> .el-input__wrapper {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
.base-info .service-wrap {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 500px;
}
.base-info .service-wrap .el-select:nth-child(1) >>> .el-input__wrapper {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
  border-right-color: transparent;
}
.base-info .service-wrap .el-input:nth-child(2){
  margin-right: -1px;
}
.base-info .service-wrap .el-input:nth-child(2) >>> .el-input__wrapper,
.base-info .service-wrap .el-select:nth-child(3) >>> .el-input__wrapper {
  border-radius: 0;
}
.base-info .service-wrap .el-button {
 border-radius: 0;
}
.add-dubbo-option-wrap .el-select-dropdown__item.selected::after{
  content: none;
}
</style>
