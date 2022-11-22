<template>
	<div class="d-detail-headers">
		<h4>{{$i18n.t('detailList.basicInformation')}}</h4>
		<EditBaseInfo v-if="isEditDetail"/>
		<el-form :label-position="isEN ? 'top': 'left'" v-else label-width="88px" class="detail-base-info">
			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="13">
					<el-form-item prop="apiStatus" :label="`${$i18n.t('ApiClass.apiStatus')}:`">
						<span class="not-edit-info">{{apiStatus[getApiInfo.apiStatus] || ''}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10" :offset="1">
					<el-form-item prop="groupID" :label="`${$i18n.t('ApiClass.category')}:`">
						<span class="not-edit-info">{{getGroupName}}</span>
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
			<el-row v-if="customMockUrl" type="flex" justify="space-between" :gutter="20">
				<el-col :span="24">
					<el-form-item :label="`${$i18n.t('mockAddress')}:`">
						<div class="tips-info">
              <a :href="customMockUrl || 'javascript:;'" target="_blank" class="not-edit-info link">{{customMockUrl}}</a>
            </div>
					</el-form-item>
				</el-col>
			</el-row>
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
import { mapGetters } from 'vuex'
import EditBaseInfo from './EditBaseInfo'
import { GROUP_TYPE } from '@/views/ApiList/constant'
import { DEFAULT_HEADER } from "@/views/TestApi/constant"

export default {
  name: 'BaseInfo',
  components: {
    EditBaseInfo
  },
  data () {
    return {
      isEN: false
    }
  },
  computed: {
    ...mapGetters([
      'detailGroupList',
      'isEditDetail',
      'customMockUrl',
      'apiTestDefaultHeader',
      'groupComp',
      'groupType'
    ]),
    getApiInfo () {
      return this.$store.getters.apiDetail.gatewayApiBaseInfo ? this.$store.getters.apiDetail : {
        gatewayApiBaseInfo: {}
      }
    },
    apiStatus () {
      return API_STATUS
    },
    groupOption () {
      return this.detailGroupList.filter(item => item.groupID > 0) || []
    },
    requestType () {
      return REQUEST_TYPE
    },
    routing_type () {
      return ROUTING_TYPE
    },
    getGroupName () {
      let groupName = ''
      try {
        groupName = this.groupOption.filter(item => item.groupID === this.getApiInfo.groupID)[0].groupName
      } catch (error) {}
      return groupName
    }
  },
  watch: {
    'getApiInfo': {
      handler (val) {
        if (val.gatewayApiBaseInfo.url) {
          let indexInitSubMenu = Object.values(this.groupComp.indexInitSubMenu)
          if (this.groupType === GROUP_TYPE.API) {
            this.$store.dispatch('apilist.group/changeGroupComp', { defaultOpeneds: [`${val.groupID}`] })
          } else if (indexInitSubMenu.length) {
            let target = indexInitSubMenu.flat().filter(v => `${v.apiID}` === this.$utils.getQuery('apiID'))
            if (target.length) {
              this.$store.dispatch('apilist.group/changeGroupComp', { defaultOpeneds: [`${target[0].index_id}`] })
            }
          }
          let headerInfo = (val.headerInfo || []).map(v => {
            return {
              paramKey: v.headerName,
              paramValue: v.headerValue
            }
          })
          let defaultHeader = [];
          (Object.keys(DEFAULT_HEADER) || []).forEach(key => {
            let has = false
            for (let i = 0; i < headerInfo.length; i++) {
              const element = headerInfo[i]
              if (key === element.paramKey) {
                has = true
                break
              }
            }
            if (!has) {
              defaultHeader.push({
                paramKey: key,
                paramValue: DEFAULT_HEADER[key],
                default: true
              })
            }
          })
          let requestInfo = []
          let jsonBody = val.apiRequestRaw || {}
          try {
            jsonBody = JSON.parse(jsonBody)
          } catch (error) {}
          if (Array.isArray(val.requestInfo)) {
            requestInfo = val.requestInfo.map(item => {
              return {
                paramKey: item.paramKey,
                paramValue: item.paramValue,
                paramType: item.paramType
              }
            })
          }
          this.$store.dispatch('apitest/changeApiTestTarget', {
            headers: headerInfo,
            body: val.apiRequestParamType === API_REQUEST_PARAM_TYPE.FORM_DATA ? requestInfo : [],
            jsonBody,
            selectCaseId: null,
            response: {
              content: {}
            },
            httpEnv: undefined,
            defaultHeader: defaultHeader,
            requestType: val.gatewayApiBaseInfo.httpMethod.toLocaleLowerCase(),
            url: val.gatewayApiBaseInfo.url,
            isRequestEnd: new Date().getTime()
          })
        }
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
  }
}
</script>

<style scoped>
.d-detail-headers .el-form {
	padding: 30px 16px 12px;
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
