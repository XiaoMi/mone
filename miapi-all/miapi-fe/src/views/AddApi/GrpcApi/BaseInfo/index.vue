<template>
	<div class="base-info">
		<el-form :label-position="isEN ? 'top': 'right'" ref="ruleForm" :rules="rules" label-width="86px" :model="grpcParam">
			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item prop="addMethod" :label="`${$i18n.t('newMethod')}:`">
						<el-radio-group v-model="addMethod">
							<el-radio-button :label="1">{{$i18n.t('importFromProject')}}</el-radio-button>
						</el-radio-group>
					</el-form-item>
				</el-col>
			</el-row>

      <el-row type="flex" justify="center" :gutter="20">
        <el-col :span="20">
          <el-form-item>
            <template #label>
              <span>{{$i18n.t('appName')}}:</span>
            </template>
            <div class="service-wrap">
              <el-select :style="styleWidth" filterable remote v-model="serviceName" :remote-method="remoteMethod" :placeholder="`${$i18n.t('placeholder.pleaseEnter')}`">
                <el-option v-for="v in serviceNameList" :key="v.name" :label="v.name" :value="v.name"></el-option>
              </el-select>
            </div>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" justify="center" :gutter="20">
        <el-col :span="20">
          <el-form-item :label="`${$i18n.t('APIenvironment')}:`">
            <el-radio-group v-model="grpcParam.env">
              <el-radio-button v-for="v in Object.keys(environmentOption)" :key="v" :label="environmentOption[v]">{{environmentOption[v]}}</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row type="flex" justify="center" :gutter="20">
        <el-col :span="20">
          <el-form-item :label="`${$i18n.t('importDetails')}:`">
            <ControllerTable :isDubbo="true" :onlyControler="false" ref="controllerTable" :serviceName="serviceName" :list="moduleClassNameList"/>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row type="flex" justify="center" :gutter="20">
        <el-col :span="20">
          <el-form-item prop="forceUpdate" :label="`${$i18n.t('dataSynchronization')}:`">
            <el-radio-group v-model="grpcParam.forceUpdate" size="mini">
              <el-radio-button :label="true">{{$i18n.t('fullCoverage')}}</el-radio-button>
              <el-radio-button :label="false">{{$i18n.t('normalMode')}}</el-radio-button>
            </el-radio-group>
            <el-tooltip placement="top" effect="dark">
              <template #content>
                <div>
                  <p style="font-size: 14px">{{$i18n.t('normalMode')}}:</p>
                  <p style="font-size: 12px;margin-bottom: 8px">{{$i18n.t('notImportExistingInterfaces')}}</p>
                  <p style="font-size: 14px">{{$i18n.t('fullCoverage')}}:</p>
                  <p style="font-size: 12px">{{$i18n.t('notImportExistingInterfacesTips')}}</p>
                </div>
              </template>
              <el-icon style="margin-left: 8px" color="rgba(0, 0, 0, 0.44)" :size="14"><InfoFilled /></el-icon>
            </el-tooltip>
          </el-form-item>
        </el-col>
      </el-row>

			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="20">
          <el-form-item>
						<el-button @click="handleConfirm" type="primary">{{$i18n.t('btnText.confirmSave')}}</el-button>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
	</div>
</template>

<script>
import { AJAX_SUCCESS_MESSAGE, ENVIRONMENT } from '@/views/constant'
import { mapGetters } from 'vuex'
import { loadGrpcApiInfos, loadGrpcService, batchAddGrpcApi } from '@/api/apilist'
import ControllerTable from '../../ControllerTable'
import { PATH } from "@/router/constant"

export default {
  name: 'GrpcBaseInfo',
  components: {
    ControllerTable
  },
  data () {
    return {
      styleWidth: {
        width: '500px'
      },
      isEN: false,
      addMethod: 1,
      serviceName: '',
      serviceNameList: [],
      moduleClassNameList: [],
      rules: {
        serviceName: [
          { required: true, message: this.$i18n.t('errorMessage.controllerNameNotEmpty'), trigger: ['blur', 'change'] }
        ],
        forceUpdate: [
          { required: true, message: this.$i18n.t('placeholder.serviceNotEmpty'), trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  beforeMount () {
    this.isEN = this.$utils.languageIsEN()
    if (this.grpcParam.symbol) {
      this.$store.dispatch('apilist.add/resetGrpcParam')
    }
  },
  watch: {
    serviceName: {
      handler (val, old) {
        this.moduleClassNameList = []
        if (old) {
          this.$store.dispatch('apilist.add/changeGrpcParam', {
            serviceMethods: []
          })
        }
        if (val && (val !== old)) {
          this.handleClickLoad()
        }
      },
      deep: true
    }
  },
  computed: {
    ...mapGetters([
      'grpcParam'
    ]),
    environmentOption () {
      return ENVIRONMENT
    }
  },
  mounted () {
    this.$store.dispatch('apilist.add/changeGrpcParam', { projectID: this.$utils.getQuery("projectID") })
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
        loadGrpcService({ serviceName: word }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.serviceNameList = data.data || []
          }
        }).catch(e => {})
      }
    },
    handleClickLoad () {
      loadGrpcApiInfos({ appName: this.serviceName }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          if (data.data.grpcApiInfos) {
            this.moduleClassNameList = Object.keys(data.data.grpcApiInfos).map(key => {
              return {
                apiList: (data.data.grpcApiInfos[key] || []).map(v => {
                  return {
                    apiName: v,
                    moduleClassName: key
                  }
                }),
                moduleClassName: key
              }
            })
          } else {
            this.moduleClassNameList = []
          }
          // let curClassArr = this.moduleClassNameList.filter(v => v.moduleClassName === this.serviceName)
          // if (curClassArr.length) {
          //   obj.httpModuleClassName = curClassArr[0].httpModuleClassName
          // } else if (data.data.list && data.data.list.length) {
          //   obj.httpModuleClassName = data.data.list[0].httpModuleClassName
          // }

          this.$store.dispatch('apilist.add/changeGrpcParam', {
            ip: data.data.ip,
            port: data.data.port,
            symbol: data.data.symbol
          })
        }
      }).catch(e => {})
    },
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
    handleGrpcSave () {
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
      let serviceMethods = arr.map(item => {
        let methodNames = []
        item.apiList.forEach(v => {
          if (v.checked) {
            methodNames.push(v.apiName)
          }
        })
        return {
          serviceName: item.moduleClassName,
          groupId: item.groupId,
          methodNames
        }
      })
      let projectID = this.grpcParam.projectID
      batchAddGrpcApi({
        env: this.grpcParam.env,
        symbol: this.grpcParam.symbol,
        appName: this.serviceName,
        ip: this.grpcParam.ip,
        port: this.grpcParam.port,
        projectID,
        forceUpdate: this.grpcParam.forceUpdate,
        serviceMethods
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message.success(this.$i18n.t('addedSuccessfully'))
          this.$store.dispatch('apilist.add/resetGrpcParam')
          this.$store.dispatch('apilist.group/getGroupViewList', projectID)
          this.$router.push({ path: PATH.API, query: { projectID } })
        }
      }).catch(e => {})
    },
    handleConfirm () {
      this.handleCheck().then(this.handleGrpcSave).catch(e => {})
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
.base-info >>> .grpc-service-name .el-form-item__label span{
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-left: -22px;
  width: 102px;
  text-align: left;
}
/* .base-info >>> .http-contriller-name .el-form-item__label span{
  width: 125px;
  display: inline-block;
}
.base-info >>> .http-contriller-name .el-form-item__label::before{
  margin-left: -125px !important;
} */
</style>
