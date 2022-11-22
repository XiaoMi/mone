<template>
	<div class="base-info">
		<el-form :label-position="isEN ? 'top': 'right'" ref="ruleForm" :rules="rules" label-width="86px" :model="gatewayParam" >
			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item prop="groupId" :label="`${$i18n.t('ApiClass.category')}:`">
						<el-select :style="styleWidth" v-model="gatewayParam.groupId" :placeholder="`${$i18n.t('placeholder.pleaseChoose')}`">
							<el-option v-for="item in groupOption" :key="item.groupID" :label="item.groupName" :value="item.groupID"></el-option>
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item class="load-form-item" prop="url" :label="`${$i18n.t('loadGateway')}:`">
						<el-input :style="styleWidth" :placeholder="`${$i18n.t('expm')}:/mtop/tesla/testApiDoc`" v-model="gatewayParam.url">
							<template #prepend>
                <el-select style="width: 100px" v-model="gatewayParam.env" :placeholder="`${$i18n.t('placeholder.pleaseChoose')}`">
                  <el-option v-for="v in Object.keys(environmentOption)" :key="v" :label="v" :value="environmentOption[v]">{{$i18n.t('China')}}-{{v}}</el-option>
                  <el-option key="outer" :label="$i18n.t('extranet')" value="outer">{{$i18n.t('China')}}-{{$i18n.t('extranet')}}</el-option>
                </el-select>
              </template>
              <template #append>
							  <el-button :disabled="!gatewayParam.url" @click="handleClickLoad">{{$i18n.t('load')}}</el-button>
              </template>
						</el-input>
            <p class="service-tips"><span @click="handleNewAdd">{{$i18n.t('bulkLoad')}}</span></p>
					</el-form-item>
				</el-col>
			</el-row>

			<GatewayForm v-if="!!this.gatewayParam.name"/>

			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="20">
					<el-form-item>
						<el-button :disabled="isCanNext" @click="handleBack">{{$i18n.t('btnText.saveReturn')}}</el-button>
						<!-- <el-button :disabled="isCanNext" @click="handleok" type="primary">{{$i18n.t('btnText.nextStep')}}</el-button> -->
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
    <el-dialog
      :destroy-on-close="true"
      :center="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :title="$i18n.t('importData')"
      v-model="createDialog"
			width="640px"
      append-to-body
    >
      <ImportDialog @handleCancel="handleCancel"/>
    </el-dialog>
	</div>
</template>

<script>
import { ENVIRONMENT, AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { mapGetters } from 'vuex'
import GatewayForm from '../FormData'
import { loadGatewayApiInfo } from '@/api/apilist'
import ImportDialog from './ImportDialog'

export default {
  name: 'GatewayBaseInfo',
  components: {
    GatewayForm,
    ImportDialog
  },
  data () {
    return {
      styleWidth: {
        width: '450px'
      },
      isEN: false,
      createDialog: false,
      rules: {
        groupId: [
          { required: true, message: this.$i18n.t('placeholder.selectGroup'), trigger: ['blur', 'change'] }
        ],
        url: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnter'), trigger: ['blur', 'change'] }
        ],
        name: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnter'), trigger: ['blur', 'change'] }
        ],
        httpMethod: [
          { required: true, message: this.$i18n.t('placeholder.pleaseChoose'), trigger: ['blur', 'change'] }
        ],
        routeType: [
          { required: true, message: this.$i18n.t('placeholder.pleaseChoose'), trigger: ['blur', 'change'] }
        ],
        path: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnter'), trigger: ['blur', 'change'] },
          { validator: (_, value, cb) => {
            if (/(^\s+)|(\s+$)|\s+/g.test(value)) {
              cb(new Error(this.$i18n.t('errorMessage.pathError')))
            } else {
              cb()
            }
          },
          trigger: ['blur', 'change'] }
        ],
        methodName: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnter'), trigger: ['blur', 'change'] },
          { validator: (_, value, cb) => {
            if (/(^\s+)|(\s+$)|\s+/g.test(value)) {
              cb(new Error(this.$i18n.t('errorMessage.pathError')))
            } else {
              cb()
            }
          },
          trigger: ['blur', 'change'] }
        ],
        serviceName: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnter'), trigger: ['blur', 'change'] },
          { validator: (_, value, cb) => {
            if (/(^\s+)|(\s+$)|\s+/g.test(value)) {
              cb(new Error(this.$i18n.t('errorMessage.pathError')))
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
    // if (this.httpParam.apiID) {
    // 	this.$store.dispatch('apilist.add/resetHttpParam')
    // }
    this.isEN = this.$utils.languageIsEN()
    if (!this.gatewayParam.groupId) {
      this.$store.dispatch('apilist.add/changeGatewayParam', { groupId: this.groupID > 0 ? this.groupID : undefined })
    }
  },
  computed: {
    ...mapGetters([
      'groupList',
      'gatewayParam',
      'addApiStep',
      'groupID'
    ]),
    projectID () {
      return this.$utils.getQuery('projectID')
    },
    environmentOption () {
      return ENVIRONMENT
    },
    groupOption () {
      return this.groupList.filter(item => item.groupID > 0) || []
    },
    isCanNext () {
      return !this.gatewayParam.url || !this.gatewayParam.name
    }
  },
  methods: {
    handleClickLoad () {
      loadGatewayApiInfo({ url: this.gatewayParam.url, env: this.gatewayParam.env }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          let obj = { ...data.data }
          delete obj.baseInfo
          this.$store.dispatch('apilist.add/changeGatewayParam', Object.assign({}, this.gatewayParam, data.data.baseInfo, obj, {
            status: !!data.data.baseInfo.status,
            mockData: data.data.baseInfo.mockData ? JSON.parse(data.data.baseInfo.mockData) : {}
          }))
        }
      }).catch(e => {})
    },
    handleNewAdd () {
      this.createDialog = true
    },
    handleCancel () {
      this.createDialog = false
    },
    handleCheck () {
      return new Promise((resolve, reject) => {
        this.$refs.ruleForm.validate((valid) => {
          if (valid) {
            let obj = this.gatewayParam
            if (obj.routeType === 0) { // 非dubbo
              obj.methodName = ''
              obj.serviceName = ''
              obj.serviceGroup = ''
              obj.serviceVersion = ''
            } else {
              obj.path = ''
            }
            this.$store.dispatch('apilist.add/changeGatewayParam', obj)
            resolve()
          } else {
            reject()
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
.base-info .load-form-item:deep() .el-form-item__content{
  align-items: flex-start;
  flex-direction: column;
}
</style>
