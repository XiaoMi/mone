<template>
	<div class="base-info">
		<el-form :label-position="isEN ? 'top': 'right'" ref="ruleForm" :rules="rules" label-width="86px" :model="isHttpNewAdd ? httpParam : {...batchAddHttpParam, serviceName}" >
			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item prop="addMethod" :label="`${$i18n.t('newMethod')}:`">
						<el-radio-group v-model="addMethod">
							<el-radio-button v-for="(item, index) in radioBtn" :key="index" :label="item.value">{{item.name}}</el-radio-button>
						</el-radio-group>
					</el-form-item>
				</el-col>
			</el-row>

      <!-- 单个http -->
      <template v-if="isHttpNewAdd">
        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item prop="groupID" :label="`${$i18n.t('ApiClass.category')}:`">
              <el-select :style="styleWidth" v-model="httpParam.groupID" :placeholder="`${$i18n.t('placeholder.pleaseChoose')}`">
                <el-option v-for="item in groupOption" :key="item.groupID" :label="item.groupName" :value="item.groupID"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item :label="`${$i18n.t('APIenvironment')}:`">
              <el-radio-group v-model="httpParam.apiEnv">
                <el-radio-button v-for="v in Object.keys(environmentOption)" :key="v" :label="environmentOption[v]">{{environmentOption[v]}}</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item prop="apiName" :label="`${$i18n.t('ApiClass.name')}:`">
              <el-input :style="styleWidth" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="httpParam.apiName" autocomplete="off"></el-input>
              <!-- <p class="service-tips"><span @click="handleNewAdd">{{$i18n.t('loadAddress')}}</span></p> -->
            </el-form-item>
          </el-col>
        </el-row>

        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item prop="apiURI" :label="`${$i18n.t('ApiClass.path')}:`">
              <el-input :style="styleWidth" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="httpParam.apiURI">
                <template #prepend>
                  <el-select style="width: 100px" v-model="httpParam.apiRequestType" :placeholder="`${$i18n.t('placeholder.pleaseChoose')}`">
                    <el-option v-for="v in Object.keys(requestType)" :key="v" :label="requestType[v]" :value="v"></el-option>
                  </el-select>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item prop="apiDesc" :label="`${$i18n.t('description')}:`">
              <el-input :style="styleWidth" type="textarea"  :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`" v-model="httpParam.apiDesc"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </template>

      <!-- 多个 -->
      <template v-else>
        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item class="http-service-name">
              <template #label>
                <span>{{$i18n.t('controllerName')}}:</span>
              </template>
              <div class="service-wrap">
                <el-select :style="styleWidth" filterable remote v-model="serviceName" :remote-method="remoteMethod" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`">
                  <el-option v-for="v in serviceNameList" :key="v.name" :label="v.name" :value="v.name"></el-option>
                </el-select>
                <!-- <el-button type="primary" :disabled="!this.serviceName" @click="handleClickLoad">{{$i18n.t('load')}}</el-button> -->
              </div>
              <!-- <p class="service-tips"><span @click="handleNewAdd">{{$i18n.t('createManually')}}</span></p> -->
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item :label="`${$i18n.t('APIenvironment')}:`">
              <el-radio-group v-model="batchAddHttpParam.apiEnv">
                <el-radio-button v-for="v in Object.keys(environmentOption)" :key="v" :label="environmentOption[v]">{{environmentOption[v]}}</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item :label="`${$i18n.t('importDetails')}:`">
              <ControllerTable ref="controllerTable" :serviceName="serviceName" :list="getControllerList"/>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item class="http-contriller-name" prop="httpModuleClassName">
              <span slot="label">{{$i18n.t('projectRelatedController')}}:</span>
              <el-select style="width: 100%" :disabled="!moduleClassNameList.length" v-model="batchAddHttpParam.httpModuleClassName" :placeholder="`${$i18n.t('placeholder.enterProjectName')}`">
                <el-option v-for="v in moduleClassNameList" :key="v.httpModuleDocName" :label="v.httpModuleDocName" :value="v.httpModuleClassName"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item prop="apiNameArr" :label="`${$i18n.t('ApiClass.methodName')}:`">
              <el-select @change="()=>{}" popper-class="add-dubbo-option-wrap" :clearable="false" multiple :disabled="!batchAddHttpParam.httpModuleClassName" style="width: 100%" v-model="batchAddHttpParam.apiNames" :placeholder="`${$i18n.t('ApiClass.methodName')}`">
                <div style="height: 40px;border-bottom: 1px solid #e6e6e6">
                  <el-option style="padding: 0;" value="selectAllDubbo">
                    <el-checkbox v-model="selectAllApiName" @change="handleSelectAllApiName" style="width: 100%; padding: 0 20px">{{$i18n.t('selectAllMethod')}}</el-checkbox>
                  </el-option>
                </div>
                <el-option style="padding: 0" v-for="item in groupServiceOption" :key="item.apiName" :value="item.apiName">
                  <el-checkbox @change="handleSelectApiName($event, item.apiName)" v-model="item.checked" style="width: 100%; padding: 0 20px">{{item.apiName}}</el-checkbox>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row> -->

        <el-row type="flex" justify="center" :gutter="20">
          <el-col :span="20">
            <el-form-item prop="forceUpdate" :label="`${$i18n.t('dataSynchronization')}:`">
              <el-radio-group v-model="batchAddHttpParam.forceUpdate" size="mini">
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

			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item v-if="isHttpNewAdd">
						<el-button @click="handleBack">{{$i18n.t('btnText.saveReturn')}}</el-button>
						<el-button @click="handleOk" type="primary">{{$i18n.t('btnText.nextStep')}}</el-button>
					</el-form-item>
          <el-form-item v-else>
						<el-button @click="handleBack" type="primary">{{$i18n.t('btnText.confirmSave')}}</el-button>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
	</div>
</template>

<script>
import { PROTOCOL, REQUEST_TYPE, AJAX_SUCCESS_MESSAGE, ENVIRONMENT } from '@/views/constant'
import { mapGetters } from 'vuex'
import { loadHttpApiServices, batchAddHttpApi, loadDubboApiServices } from '@/api/apilist'
import debounce from '@/common/debounce'
import { PATH } from '@/router/constant'
import { RADIO_BTN } from '../../constant'
import ControllerTable from '../../ControllerTable'

export default {
  name: 'HttpBaseInfo',
  components: {
    ControllerTable
  },
  data () {
    return {
      styleWidth: {
        width: '500px'
      },
      isEN: false,
      addMethod: false,
      serviceName: '',
      serviceNameList: [],
      moduleClassNameList: [],
      serviceList: [],
      apiNameArr: [],
      groupServiceOption: [],
      selectAllApiName: false,
      rules: {
        apiName: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterName'), trigger: ['blur', 'change'] }
        ],
        groupID: [
          { required: true, message: this.$i18n.t('placeholder.selectGroup'), trigger: ['blur', 'change'] }
        ],
        apiURI: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnter'), trigger: ['blur', 'change'] }
        ],
        serviceName: [
          { required: true, message: this.$i18n.t('errorMessage.controllerNameNotEmpty'), trigger: ['blur', 'change'] }
        ],
        forceUpdate: [
          { required: true, message: this.$i18n.t('placeholder.serviceNotEmpty'), trigger: ['blur', 'change'] }
        ],
        apiNameArr: [
          { validator: (_, value, cb) => {
            if (!this.batchAddHttpParam.apiNames.length) {
              cb(new Error(this.$i18n.t('placeholder.pleaseSelectMethodName')))
            } else {
              cb()
            }
          },
          trigger: ['blur', 'change'] }
        ],
        httpModuleClassName: [
          { required: true, message: this.$i18n.t('placeholder.enterProjectName'), trigger: ['blur', 'change'] },
          { validator: (_, value, cb) => {
            if (/(^\s+)|(\s+$)|\s+/g.test(value)) {
              cb(new Error(this.$i18n.t('placeholder.enterProjectName')))
            } else {
              cb()
            }
          },
          trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  beforeMount () {
    this.isEN = this.$utils.languageIsEN()
    if (this.httpParam.apiID) {
      this.$store.dispatch('apilist.add/resetHttpParam')
    }
  },
  watch: {
    isHttpNewAdd: {
      handler (bool) {
        if (this.addMethod !== bool) {
          this.addMethod = bool
        }
        if (bool) {
          let groupID = this.batchAddHttpParam.groupID || (this.groupID > 0 ? this.groupID : undefined)
          this.$store.dispatch('apilist.add/resetBatchHttpParam')
          this.$store.dispatch('apilist.add/changeHttpParam', { groupID })
          this.apiNameArr = []
        } else {
          let groupID = this.httpParam.groupID || (this.groupID > 0 ? this.groupID : undefined)
          this.$store.dispatch('apilist.add/resetHttpParam')
          this.$store.dispatch('apilist.add/changeBatchHttpParam', { groupID })
        }
      },
      immediate: true
    },
    addMethod (val) {
      this.$refs.ruleForm.clearValidate()
      this.$store.dispatch('apilist.add/changeHttpNewAdd', val)
    },
    apiNameArr: {
      handler (val) {
        if (!val.includes('selectAllDubbo')) {
          this.changeApiName(val)
        }
      },
      deep: true
    },
    serviceName: {
      handler (val, old) {
        this.moduleClassNameList = []
        this.serviceList = []
        this.apiNameArr = []
        this.groupServiceOption = []
        this.selectAllApiName = false
        if (old) {
          this.$store.dispatch('apilist.add/changeBatchHttpParam', { httpModuleClassName: undefined })
        }
        if (val && (val !== old)) {
          this.handleClickLoad()
        }
      },
      deep: true
    },
    "batchAddHttpParam.httpModuleClassName": {
      handler (val) {
        if (val) {
          let apiNameArr = []
          let arr = this.moduleClassNameList.filter(item => item.httpModuleClassName === val)
          if (arr.length) {
            this.groupServiceOption = arr[0].httpModuleApiList.map(v => {
              apiNameArr.push(v.apiName)
              return {
                ...v,
                checked: true
              }
            })
            this.apiNameArr = apiNameArr
            this.selectAllApiName = true
            this.$store.dispatch('apilist.add/changeBatchHttpParam', { apiNames: apiNameArr })
          }
        }
      },
      deep: true
    }
  },
  computed: {
    ...mapGetters([
      'groupList',
      'groupID',
      'isHttpNewAdd',
      'batchAddHttpParam',
      'httpParam'
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
    groupOption () {
      return this.groupList.filter(item => item.groupID > 0) || []
    },
    radioBtn () {
      return RADIO_BTN
    },
    environmentOption () {
      return ENVIRONMENT
    },
    getControllerList () {
      return this.moduleClassNameList.map(item => {
        return {
          apiList: item.httpModuleApiList,
          moduleClassName: item.httpModuleClassName
        }
      })
    }
  },
  mounted () {
    if (!this.batchAddHttpParam.groupID) {
      this.$store.dispatch('apilist.add/changeBatchHttpParam', { groupID: this.groupID > 0 ? this.groupID : undefined })
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
        loadDubboApiServices({ env: 'staging', serviceName: word }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.serviceNameList = data.data || []
          }
        }).catch(e => {})
      }
    },
    handleClickLoad () {
      loadHttpApiServices({ serviceName: this.serviceName }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          let obj = {}
          this.moduleClassNameList = data.data.list || []
          let curClassArr = (data.data.list || []).filter(v => v.httpModuleClassName === this.serviceName)
          if (curClassArr.length) {
            obj.httpModuleClassName = curClassArr[0].httpModuleClassName
          } else if (data.data.list && data.data.list.length) {
            obj.httpModuleClassName = data.data.list[0].httpModuleClassName
          }
          if (data.data.ipAndPort) {
            let ipPort = data.data.ipAndPort.split(":")
            obj.ip = ipPort[0]
            obj.port = ipPort[1]
          }
          if (Object.keys(obj).length) {
            this.$store.dispatch('apilist.add/changeBatchHttpParam', obj)
          }
        }
      }).catch(e => {})
    },
    handleSelectAllApiName (val) {
      let apiNameArr = []
      if (val) {
        this.groupServiceOption.forEach(v => {
          apiNameArr.push(v.apiName)
          v.checked = true
        })
      } else {
        this.groupServiceOption.forEach(v => {
          v.checked = false
        })
      }
      this.apiNameArr = apiNameArr
    },
    handleSelectApiName (val, name) {
      if (val) {
        if (!this.apiNameArr.includes(name)) {
          this.apiNameArr.push(name)
        }
      } else {
        this.apiNameArr = this.apiNameArr.filter(v => v !== name)
      }
      if (this.groupServiceOption.length) {
        this.selectAllApiName = this.apiNameArr.length === this.groupServiceOption.length
      }
    },
    changeApiName: debounce(function (val) {
      if (this.groupServiceOption.length) {
        this.selectAllApiName = val.length === this.groupServiceOption.length
        this.groupServiceOption.forEach(v => {
          if (val.includes(v.apiName)) {
            v.checked = true
          } else {
            v.checked = false
          }
        })
      }
      this.$store.dispatch('apilist.add/changeBatchHttpParam', { apiNames: val || [] })
    }, 300),
    handleCheck () {
      return new Promise((resolve, reject) => {
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            resolve()
          } else {
            reject()
          }
        })
      })
    },
    handleNewAdd () {
      this.$store.dispatch('apilist.add/changeHttpNewAdd', !this.isHttpNewAdd)
      this.$refs.ruleForm.clearValidate()
    },
    handleHttpSave () {
      if (this.isHttpNewAdd) {
        this.$store.dispatch('apilist.add/handleSubmit')
        return
      }
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
      let projectID = this.$utils.getQuery("projectID")
      arr = arr.map(item => {
        let apiNames = []
        item.apiList.forEach(v => {
          if (v.checked) {
            apiNames.push(v.apiMethodName || v.apiName)
          }
        })
        return {
          ...this.batchAddHttpParam,
          forceUpdate: !!this.batchAddHttpParam.forceUpdate,
          projectID,
          httpModuleClassName: item.moduleClassName,
          apiNames,
          groupID: item.groupId
        }
      })
      batchAddHttpApi({ bos: arr }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message.success(this.$i18n.t('addedSuccessfully'))
          this.$store.dispatch('apilist.add/resetBatchHttpParam')
          this.$store.dispatch('apilist.group/getGroupViewList', projectID)
          this.$router.push({ path: PATH.API, query: { projectID } })
        }
      }).catch(e => {})
    },
    handleBack () {
      this.handleCheck().then(this.handleHttpSave).catch(e => {})
    },
    handleOk () {
      this.handleCheck().then(() => {
        this.$store.dispatch('apilist.add/changeStep', 2)
      })
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
.base-info .service-wrap {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.base-info .service-wrap .el-button {
 border-radius: 0;
}
.base-info >>> .http-service-name .el-form-item__label span{
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-left: -22px;
  width: 102px;
  text-align: left;
  vertical-align: middle;
  height: 100%;
}
/* .base-info >>> .http-contriller-name .el-form-item__label span{
  width: 125px;
  display: inline-block;
}
.base-info >>> .http-contriller-name .el-form-item__label::before{
  margin-left: -125px !important;
} */
</style>
