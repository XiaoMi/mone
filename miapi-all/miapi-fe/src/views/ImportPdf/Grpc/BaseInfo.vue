<template>
	<div class="d-detail-headers">
		<h4><el-icon><Reading /></el-icon>{{$i18n.t('detailList.basicInformation')}}</h4>
		<el-form label-position="left" class="detail-base-info" label-width="88px" >
			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="14">
					<el-form-item prop="apiStatus" :label="`${$i18n.t('ApiClass.serviceStatus')}:`">
						<span class="not-edit-info">{{apiStatus[getApiInfo.apiStatus] || ''}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10">
					<el-form-item prop="groupName" :label="`${$i18n.t('ApiClass.category')}:`">
						<span class="not-edit-info">{{getApiInfo.groupName}}</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="14">
					<el-form-item prop="fullApiPath" :label="`${$i18n.t('interfaceAddress')}:`">
						<span class="not-edit-info">{{getApiInfo.fullApiPath}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10">
					<el-form-item prop="methodName" :label="`${$i18n.t('ApiClass.methodName')}:`">
						<span class="not-edit-info">{{getApiInfo.methodName}}</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="14">
					<el-form-item prop="serviceName" :label="`${$i18n.t('ApiClass.serviceName')}:`">
						<span class="not-edit-info">{{getApiInfo.serviceName}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10">
					<el-form-item prop="apiDesc" :label="`${$i18n.t('serviceDescription')}:`">
						<span class="not-edit-info">{{getApiInfo.apiDesc || $i18n.t('noDescription')}}</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="24">
					<el-form-item prop="updateUsername" :label="`${$i18n.t('lastUpdater')}:`">
						<span class="not-edit-info">{{getApiInfo.updateUsername}}</span>
					</el-form-item>
				</el-col>
			</el-row>

		</el-form>
	</div>
</template>

<script>
import { REQUEST_TYPE, API_STATUS } from '@/views/constant'

export default {
  name: 'BaseInfo',
  data () {
    return {}
  },
  props: {
    getApiInfo: {
      type: Object,
      default: () => {
        return {}
      }
    }
  },
  computed: {
    apiStatus () {
      return API_STATUS
    },
  },
}
</script>

<style scoped>
.d-detail-headers .el-form {
	padding: 10px 16px 12px;
}
.d-detail-headers .el-form .not-edit-info {
	display: inline-block;
	white-space: pre-wrap;
	word-break: break-all;
}
.d-detail-headers .el-form .not-edit-info.link {
  color: #1890FF;
}
.detail-base-info >>> .el-form-item__label {
	text-align: left;
}
.d-detail-headers .el-form .tips-info {
	display: flex;
  align-items: flex-start;
}
.d-detail-headers .el-form .tips-info i {
  margin-left: 4px;
  color: #ff9318;
  cursor: pointer;
}
</style>
