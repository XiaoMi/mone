<template>
	<div class="g-copy-dialog">
		<el-form :label-position="isEN ? 'top': 'left'" ref="ruleForm" :rules="rules" label-width="auto" :model="httpParam" >
			<el-row :gutter="20">
				<el-col :span="16">
					<el-form-item prop="apiStatus" :label="`${$i18n.t('ApiClass.apiStatus')}:`">
						<el-select style="width: 100%" v-model="httpParam.apiStatus" :placeholder="$i18n.t('placeholder.pleaseChoose')">
							<el-option v-for="v in Object.keys(apiStatus)" :key="v" :label="apiStatus[v]" :value="Number(v)"></el-option>
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row :gutter="20">
				<el-col :span="16">
					<el-form-item prop="groupID" :label="`${$i18n.t('ApiClass.category')}:`">
						<el-select style="width: 100%" v-model="httpParam.groupID" :placeholder="$i18n.t('placeholder.pleaseChoose')">
							<el-option v-for="item in groupOption" :key="item.groupID" :label="item.groupName" :value="item.groupID"></el-option>
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row :gutter="20">
				<el-col :span="16">
					<el-form-item prop="apiName" :label="`${$i18n.t('ApiClass.name')}:`">
						<el-input :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="httpParam.apiName" autocomplete="off"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row :gutter="20">
				<el-col :span="16">
					<el-form-item prop="apiURI" :label="`${$i18n.t('ApiClass.path')}:`">
						<el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model.trim="httpParam.apiURI">
							<template #prepend>
                <el-select style="width: 100px" v-model="httpParam.apiRequestType" :placeholder="$i18n.t('placeholder.pleaseChoose')">
                  <el-option v-for="v in Object.keys(requestType)" :key="v" :label="requestType[v]" :value="Number(v)"></el-option>
                </el-select>
              </template>
						</el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row :gutter="20">
				<el-col :span="16">
					<el-form-item prop="apiDesc" :label="`${$i18n.t('ApiClass.apiDescription')}:`">
						<el-input type="textarea"  :placeholder="$i18n.t('placeholder.enterDesc')" v-model="httpParam.apiDesc"></el-input>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>

    <HeaderData :httpParam="httpParam" @onChange="handleHttpParam"/>

    <RequestData :httpParam="httpParam" @onChange="handleHttpParam"/>

    <ReturnData :httpParam="httpParam" @onChange="handleHttpParam"/>

		<div class="dialog-footer">
			<el-button @click="$emit('onClose')">{{$i18n.t('btnText.cancel')}}</el-button>
			<el-button type="primary" @click="handleSubmit">{{$i18n.t('btnText.ok')}}</el-button>
		</div>
	</div>
</template>

<script>
import { getHttpApi, addHttpApi } from '@/api/apilist'
import { PROTOCOL, REQUEST_TYPE, API_STATUS, AJAX_SUCCESS_MESSAGE, API_REQUEST_PARAM_TYPE, PROTOCOL_TYPE } from '@/views/constant'

import { mapGetters } from 'vuex'
import RequestData from "./Http/RequestData.vue"
import ReturnData from "./Http/ReturnData.vue"
import HeaderData from "./Http/HeaderData.vue"
import { handleFilter, handleCheckValue } from '@/store/utils'
import handleCommit from '@/common/commitInfo'

export default {
  name: 'CopyHttp',
  components: {
    RequestData,
    ReturnData,
    HeaderData
  },
  data () {
    return {
      isEN: false,
      httpParam: {},
      submitData: {},
      rules: {
        apiStatus: [
          { required: true, message: this.$i18n.t('placeholder.pleaseSelectStatus'), trigger: ['blur', 'change'] }
        ],
        groupID: [
          { required: true, message: this.$i18n.t('placeholder.pleaseSelectCategory'), trigger: ['blur', 'change'] }
        ],
        apiName: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterName'), trigger: ['blur', 'change'] }
        ],
        apiURI: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterPath'), trigger: ['blur', 'change'] },
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
  props: {
    defaultData: {
      type: Object,
      default () {
        return { }
      }
    }
  },
  watch: {
    defaultData: {
      handler (newVal) {
        if (newVal.apiID) {
          getHttpApi({ projectID: this.$utils.getQuery('projectID'), apiID: newVal.apiID }).then((data) => {
            if (data.message === AJAX_SUCCESS_MESSAGE) {
              let obj = { ...data.data.baseInfo }
              this.httpParam = {
                ...obj,
                apiURI: "",
                apiHeader: data.data.headerInfo || [],
                apiRequestParam: data.data.requestInfo || [],
                apiResultParam: data.data.resultInfo || [],
                apiErrorCodes: data.data.apiErrorCodes || []
              }
              this.submitData = this.httpParam
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
      'groupList'
    ]),
    apiStatus () {
      return API_STATUS
    },
    groupOption () {
      return this.groupList.filter(item => item.groupID > 0) || []
    },
    requestType () {
      return REQUEST_TYPE
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
  },
  methods: {
    handleHttpParam (param) {
      Object.keys(param).forEach((key) => {
        this.submitData[key] = param[key]
      })
    },
    handleSubmit () {
      let params = {
        ...this.submitData,
        apiRequestRaw: typeof this.submitData.apiRequestRaw === 'string' ? this.submitData.apiRequestRaw : JSON.stringify(this.submitData.apiRequestRaw),
        apiHeader: this.submitData.apiHeader.filter(item => !!item.headerName || !!item.headerValue),
        apiRequestParam: handleFilter(this.submitData.apiRequestParam),
        apiResultParam: handleFilter(this.submitData.apiResultParam),
        projectID: this.$utils.getQuery('projectID')
      }
      if (handleCheckValue(params, PROTOCOL_TYPE.HTTP)) {
        return
      }
      params = {
        ...params,
        apiHeader: JSON.stringify(params.apiHeader),
        apiRequestParam: JSON.stringify(params.apiRequestParam),
        apiResultParam: JSON.stringify(params.apiResultParam),
        apiResponseParamType: params.apiResponseParamType || API_REQUEST_PARAM_TYPE.JSON,
        apiErrorCodes: JSON.stringify(params.apiErrorCodes),
        apiNoteRaw: "",
        apiNote: ""
      }
      delete params.dubboApiId
      delete params.starred
      delete params.gatewayApiId
      delete params.httpControllerPath
      delete params.apiUpdateTime
      delete params.groupName
      delete params.removed
      delete params.updateUsername
      delete params.apiID

      handleCommit((updateMsg) => {
        params.updateMsg = updateMsg
        addHttpApi(params).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.$emit("onClose")
            this.$message.success(this.$i18n.t('addedSuccessfully'))
            this.$store.dispatch('apilist.group/getGroupViewList', this.$utils.getQuery('projectID'))
          }
        }).catch(e => {})
      })
    }
  }
}
</script>

<style lang="scss" scoped>
	.g-copy-dialog{
		padding: 16px 16px 0;
	}
	.dialog-footer{
		text-align: right;
    margin-top: 20px;
	}
</style>
