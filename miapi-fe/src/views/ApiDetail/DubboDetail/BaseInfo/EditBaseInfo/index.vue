<template>
	<el-form :label-position="isEN ? 'top': 'left'" ref="ruleForm" :rules="rules" label-width="auto" :model="dubboParam" >
		<el-row type="flex" justify="space-between" :gutter="20">
			<el-col :span="12">
				<el-form-item prop="apiStatus" :label="`${$i18n.t('ApiClass.serviceStatus')}:`">
					<el-select style="width: 100%" v-model="dubboParam.apiStatus" :placeholder="$i18n.t('placeholder.pleaseChoose')">
						<el-option v-for="v in Object.keys(apiStatus)" :key="v" :label="apiStatus[v]" :value="Number(v)"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item prop="groupId" :label="`${$i18n.t('ApiClass.category')}:`">
					<el-select disabled style="width: 100%" v-model="dubboParam.groupId" :placeholder="$i18n.t('placeholder.pleaseChoose')">
						<el-option v-for="item in groupOption" :key="item.groupID" :label="item.groupName" :value="item.groupID"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="space-between" :gutter="20">
			<el-col :span="12">
				<el-form-item prop="name" :label="`${$i18n.t('interfaceName')}:`">
						<el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="dubboParam.name" autocomplete="off"></el-input>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item prop="apiDocName" :label="`${$i18n.t('interfaceDescription')}:`">
						<el-input disabled :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="dubboParam.apiDocName" autocomplete="off"></el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="space-between" :gutter="20">
			<el-col :span="12">
				<el-form-item prop="apiModelClass" :label="`${$i18n.t('ApiClass.serviceName')}:`">
					<el-input disabled :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="dubboParam.apiModelClass" autocomplete="off"></el-input>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item prop="apiName" :label="`${$i18n.t('ApiClass.methodName')}:`">
					<el-input disabled :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="dubboParam.apiName" autocomplete="off"></el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="space-between" :gutter="20">
			<el-col :span="12">
				<el-form-item :label="`${$i18n.t('serviceVersion')}:`">
					<el-input disabled :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="dubboParam.apiVersion" autocomplete="off"></el-input>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item prop="apiGroup" :label="`${$i18n.t('serviceGrouping')}:`">
					<el-input disabled :placeholder="`${$i18n.t('placeholder.enterAPIGroup')},staging or online`" v-model="dubboParam.apiGroup" autocomplete="off"></el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="space-between" :gutter="20">
			<el-col :span="12">
				<el-form-item prop="apiDesc" :label="`${$i18n.t('serviceDescription')}:`">
					<el-input type="textarea"  :placeholder="$i18n.t('placeholder.enterDesc')" v-model="dubboParam.apiDesc"></el-input>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item prop="async" :label="`${$i18n.t('asynchronousCall')}:`">
					<el-switch
						v-model="dubboParam.async">
					</el-switch>
					<span style="color: rgba(0, 0, 0, 0.44); font-size: 12px;margin-left: 16px">{{$i18n.t('definitionTips')}}</span>
				</el-form-item>
			</el-col>
		</el-row>
	</el-form>
</template>

<script>
import { ENVIRONMENT, REQUEST_TYPE, API_STATUS, AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { mapGetters } from 'vuex'

export default {
  name: 'EditBaseInfo',
  data () {
    return {
      isEN: false,
      rules: {
        apiStatus: [
          { required: true, message: this.$i18n.t('placeholder.pleaseSelectStatus'), trigger: ['blur', 'change'] }
        ],
        groupId: [
          { required: true, message: this.$i18n.t('placeholder.pleaseSelectCategory'), trigger: ['blur', 'change'] }
        ],
        name: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterInterfaceName'), trigger: ['blur', 'change'] }
        ],
        apiName: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterName'), trigger: ['blur', 'change'] }
        ],
        apiDocName: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterMethodName'), trigger: ['blur', 'change'] }
        ],
        apiModelClass: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterModule'), trigger: ['blur', 'change'] }
        ],
        apiVersion: [
          { required: true, message: this.$i18n.t('placeholder.enterVersion'), trigger: ['blur', 'change'] }
        ]
      }
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
    this.$store.dispatch('apilist.add/changeCanSave', this.$refs.ruleForm)
  },
  computed: {
    ...mapGetters([
      'detailGroupList',
      'dubboParam'
    ]),
    apiStatus () {
      return API_STATUS
    },
    groupOption () {
      return this.detailGroupList.filter(item => item.groupID > 0) || []
    },
    requestType () {
      return REQUEST_TYPE
    },
    environmentOption () {
      return ENVIRONMENT
    },
    getGroupName () {
      let groupName = ''
      try {
        groupName = this.groupOption.filter(item => item.groupID === this.dubboParam.groupId)[0].groupName
      } catch (error) {}
      return groupName
    }
  }
}
</script>

<style scoped>
.d-detail-headers .el-form {
	padding: 30px 60px 12px;
}
</style>
