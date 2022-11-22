<template>
	<el-form :label-position="isEN ? 'top': 'left'" ref="ruleForm" :rules="rules" label-width="auto" :model="gatewayParam" >
		<el-row type="flex" justify="space-between" :gutter="20">
			<el-col :span="12">
				<el-form-item prop="apiStatus" :label="`${$i18n.t('ApiClass.apiStatus')}:`">
					<el-select style="width: 100%" v-model="gatewayParam.apiStatus" :placeholder="$i18n.t('placeholder.pleaseChoose')">
						<el-option v-for="v in Object.keys(apiStatus)" :key="v" :label="apiStatus[v]" :value="Number(v)"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item prop="groupId" :label="`${$i18n.t('ApiClass.category')}:`">
					<el-select disabled style="width: 100%" v-model="gatewayParam.groupId" :placeholder="$i18n.t('placeholder.pleaseSelectCategory')">
						<el-option v-for="item in groupOption" :key="item.groupID" :label="item.groupName" :value="item.groupID"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="space-between" :gutter="20">
			<el-col :span="12">
				<el-form-item prop="name" :label="`${$i18n.t('ApiClass.name')}:`">
					<el-input disabled :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="gatewayParam.name" autocomplete="off"></el-input>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item prop="httpMethod" :label="`${$i18n.t('ApiClass.requestName')}:`">
					<el-select style="width: 100%" v-model="gatewayParam.httpMethod" :placeholder="$i18n.t('placeholder.pleaseChoose')">
						<el-option v-for="v in Object.keys(requestType)" :key="v" :label="requestType[v]" :value="v"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="space-between" :gutter="20">
			<!-- <el-col :span="12">
				<el-form-item prop="methodName" :label="`${$i18n.t('ApiClass.methodName')}:`">
					<el-input size="small" :placeholder="$i18n.t('placeholder.pleaseEnterMethodName')" v-model="gatewayParam.methodName">
					</el-input>
				</el-form-item>
			</el-col> -->
			<el-col :span="12">
				<el-form-item prop="application" :label="`${$i18n.t('appName')}:`">
					<el-input disabled :placeholder="$i18n.t('placeholder.pleaseEnterAppName')" v-model="gatewayParam.application" autocomplete="off"></el-input>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item prop="routeType" :label="`${$i18n.t('routingType')}:`">
					<el-select disabled style="width: 100%" v-model="gatewayParam.routeType">
						<el-option v-for="key in Object.keys(routing_type)" :key="routing_type[key]" :label="routing_type[key]" :value="Number(key)"></el-option>
					</el-select>
				</el-form-item>
			</el-col>
		</el-row>

    <el-row v-if="gatewayParam.routeType !== 0" type="flex" justify="space-between" :gutter="20">
			<el-col :span="12">
				<el-form-item prop="apiDesc" :label="`${$i18n.t('ApiClass.apiDescription')}:`">
					<el-input type="textarea" :placeholder="$i18n.t('placeholder.enterDesc')" v-model="gatewayParam.apiDesc"></el-input>
				</el-form-item>
			</el-col>
			<el-col :span="12">
				<el-form-item prop="serviceVersion" :label="`${$i18n.t('serviceVersion')}:`">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnterServiceVersion')" v-model="gatewayParam.serviceVersion">
					</el-input>
				</el-form-item>
			</el-col>
		</el-row>

		<el-row type="flex" justify="space-between" :gutter="20">
			<el-col v-if="gatewayParam.routeType === 0" :span="12">
				<el-form-item prop="path" :label="`URL${$i18n.t('path')}:`">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnterPath')" v-model="gatewayParam.path">
					</el-input>
				</el-form-item>
			</el-col>
			<el-col v-else :span="12">
				<el-form-item prop="serviceName" :label="`${$i18n.t('ApiClass.serviceName')}:`">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnterServiceName')" v-model="gatewayParam.serviceName">
					</el-input>
				</el-form-item>
			</el-col>
		</el-row>
	</el-form>
</template>

<script>
import { API_STATUS, ROUTING_TYPE } from '@/views/constant'
import { mapGetters } from 'vuex'

export default {
  name: 'EditBaseInfo',
  data () {
    return {
      isEN: false,
      rules: {
        apiStatus: [
          { required: true, message: this.$i18n.t('placeholder.pleaseChoose'), trigger: ['blur', 'change'] }
        ],
        groupId: [
          { required: true, message: this.$i18n.t('placeholder.pleaseSelectCategory'), trigger: ['blur', 'change'] }
        ],
        name: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterName'), trigger: ['blur', 'change'] }
        ],
        httpMethod: [
          { required: true, message: this.$i18n.t('placeholder.pleaseChoose'), trigger: ['blur', 'change'] }
        ],
        routeType: [
          { required: true, message: this.$i18n.t('placeholder.pleaseChoose'), trigger: ['blur', 'change'] }
        ],
        path: [
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterPath'), trigger: ['blur', 'change'] },
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
          { required: true, message: this.$i18n.t('placeholder.pleaseEnterServiceName'), trigger: ['blur', 'change'] },
		  { validator: (_, value, cb) => {
            if (/(^\s+)|(\s+$)|\s+/g.test(value)) {
              cb(new Error(this.$i18n.t('errorMessage.pathError')))
            } else {
              cb()
            }
          },
          trigger: ['blur', 'change'] }
        ]
      //   methodName: [
      //     { required: true, message: this.$i18n.t('placeholder.pleaseEnterMethodName'), trigger: ['blur', 'change'] },
		  // { validator: (_, value, cb) => {
      //       if (/(^\s+)|(\s+$)|\s+/g.test(value)) {
      //         cb(new Error(this.$i18n.t('errorMessage.pathError')))
      //       } else {
      //         cb()
      //       }
      //     },
      //     trigger: ['blur', 'change'] }
      //   ]
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
      'gatewayParam'
    ]),
    apiStatus () {
      return API_STATUS
    },
    groupOption () {
      return this.detailGroupList.filter(item => item.groupID > 0) || []
    },
    routing_type () {
      return ROUTING_TYPE
    },
    requestType () {
      return {
        'post': 'POST',
        'get': 'GET'
      }
    },
    getGroupName () {
      let groupName = ''
      try {
        groupName = this.groupOption.filter(item => item.groupID === this.gatewayParam.groupId)[0].groupName
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
.el-form .time-ms >>> .el-input-group__append {
	padding: 0 10px;
}
</style>
