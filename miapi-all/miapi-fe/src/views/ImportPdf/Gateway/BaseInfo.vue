<template>
	<div class="d-detail-headers">
		<h4><el-icon><Reading /></el-icon>{{$i18n.t('detailList.basicInformation')}}</h4>
		<el-form label-position="left" label-width="88px" class="detail-base-info">
			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="13">
					<el-form-item prop="apiStatus" :label="`${$i18n.t('ApiClass.apiStatus')}:`">
						<span class="not-edit-info">{{apiStatus[getApiInfo.apiStatus] || ''}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10" :offset="1">
					<el-form-item prop="groupID" :label="`${$i18n.t('ApiClass.category')}:`">
						<span class="not-edit-info">--</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="13">
					<el-form-item prop="apiName" :label="`${$i18n.t('ApiClass.name')}:`">
						<span class="not-edit-info">{{getApiInfo.gatewayApiBaseInfo.name}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10" :offset="1">
					<el-form-item prop="httpMethod" :label="`${$i18n.t('ApiClass.requestName')}:`">
						<span class="not-edit-info">{{getApiInfo.gatewayApiBaseInfo.httpMethod}}</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="13">
					<el-form-item prop="application" :label="`${$i18n.t('appName')}:`">
						<span class="not-edit-info">{{getApiInfo.gatewayApiBaseInfo.application}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10" :offset="1">
					<el-form-item :label="`${$i18n.t('overtimeTime')}:`">
						<span class="not-edit-info">{{getApiInfo.gatewayApiBaseInfo.timeout}}ms</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col v-if="getApiInfo.gatewayApiBaseInfo.routeType === 0" :span="13">
					<el-form-item prop="path" :label="`URL${$i18n.t('path')}:`">
						<span class="not-edit-info">{{getApiInfo.gatewayApiBaseInfo.path}}</span>
					</el-form-item>
				</el-col>
				<el-col v-else :span="13">
					<el-form-item prop="serviceName" :label="`${$i18n.t('ApiClass.serviceName')}:`">
						<span class="not-edit-info">{{getApiInfo.gatewayApiBaseInfo.serviceName}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10" :offset="1">
					<el-form-item prop="routeType" :label="`${$i18n.t('routingType')}:`">
						<span class="not-edit-info">{{routing_type[getApiInfo.gatewayApiBaseInfo.routeType]}}</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row v-if="getApiInfo.gatewayApiBaseInfo.routeType !== 0" type="flex" justify="space-between" :gutter="0">
				<el-col :span="13">
					<el-form-item prop="methodName" :label="`${$i18n.t('ApiClass.methodName')}:`">
						<span class="not-edit-info">{{getApiInfo.gatewayApiBaseInfo.methodName}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10" :offset="1">
					<el-form-item prop="serviceVersion" :label="`${$i18n.t('serviceVersion')}:`">
						<span class="not-edit-info">{{getApiInfo.gatewayApiBaseInfo.serviceVersion}}</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="24">
					<el-form-item prop="apiDesc" :label="`${$i18n.t('ApiClass.apiDescription')}:`">
						<span class="not-edit-info">{{getApiInfo.apiDesc || $i18n.t('noDescription')}}</span>
					</el-form-item>
				</el-col>
			</el-row>
			<!-- <el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="24">
					<el-form-item label="MockUrl:">
						<div class="tips-info">
							<a :href="getApiInfo.mockUrl || 'javascript:;'" target="_blank" class="not-edit-info link">{{getApiInfo.mockUrl || '暂无地址'}}</a>
              <el-popover trigger="hover" placement="top">
                <div style="font-size: 12px">{{$i18n.t('detailMockTips.first')}}<br/>{{$i18n.t('detailMockTips.sec')}}</div>
                <i slot="reference" class="el-icon-info"/>
              </el-popover>
            </div>
					</el-form-item>
				</el-col>
			</el-row> -->
			<el-row type="flex" justify="space-between" :gutter="20">
				<el-col :span="24">
					<el-form-item prop="serviceVersion" :label="`${$i18n.t('lastUpdater')}:`">
						<span class="not-edit-info">{{getApiInfo.updateUsername}}</span>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
	</div>
</template>

<script>
import { REQUEST_TYPE, API_STATUS, API_REQUEST_PARAM_TYPE, ROUTING_TYPE } from '@/views/constant'

export default {
  name: 'BaseInfo',
  data () {
    return {
    }
  },
  props: {
    getApiInfo: {
      type: Object,
      default: () => {
        return {
          gatewayApiBaseInfo: {}
        }
      }
    }
  },
  computed: {
    apiStatus () {
      return API_STATUS
    },
    routing_type () {
      return ROUTING_TYPE
    }
  }
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
