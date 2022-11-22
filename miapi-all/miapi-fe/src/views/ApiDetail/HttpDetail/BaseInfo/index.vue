<template>
	<div class="d-detail-headers">
		<h4>{{$i18n.t('detailList.basicInformation')}}</h4>
		<EditBaseInfo v-if="isEditDetail"/>
		<el-form :label-position="isEN ? 'top': 'left'" v-else label-width="88px" class="detail-base-info">
			<el-row type="flex" justify="space-between" :gutter="20">
				<el-col :span="12">
					<el-form-item prop="apiStatus" :label="`${$i18n.t('ApiClass.apiStatus')}:`">
						<span class="not-edit-info">{{apiStatus[getApiInfo.baseInfo.apiStatus] || ''}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item prop="groupID" :label="`${$i18n.t('ApiClass.category')}:`">
						<span class="not-edit-info">{{getGroupName}}</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="space-between" :gutter="20">
				<el-col :span="12">
					<el-form-item prop="apiName" :label="`${$i18n.t('ApiClass.name')}:`">
						<span class="not-edit-info">{{getApiInfo.baseInfo.apiName}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="12">
					<el-form-item prop="apiURI" :label="`${$i18n.t('ApiClass.path')}:`">
						<span class="not-edit-info">{{getApiInfo.baseInfo.apiURI}}</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="space-between" :gutter="20">
				<el-col :span="24">
					<el-form-item prop="apiDesc" :label="`${$i18n.t('ApiClass.apiDescription')}:`">
						<span class="not-edit-info">{{getApiInfo.baseInfo.apiDesc || $i18n.t('noDescription')}}</span>
					</el-form-item>
				</el-col>
			</el-row>
			<!-- <el-row type="flex" justify="space-between" :gutter="20">
				<el-col :span="24">
					<el-form-item label="MockUrl:">
						<div class="tips-info">
              <a :href="getApiInfo.mockInfo.mockUrl || 'javascript:;'" target="_blank" class="not-edit-info link">{{getApiInfo.mockInfo.mockUrl || '暂无地址'}}</a>
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
						<span class="not-edit-info">{{getApiInfo.baseInfo.updateUsername}}</span>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
	</div>
</template>

<script>
import { REQUEST_TYPE, REQUEST_TYPE_EX, API_STATUS, API_REQUEST_PARAM_TYPE } from '@/views/constant'
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
      return this.$store.getters.apiDetail.baseInfo ? this.$store.getters.apiDetail : {
        baseInfo: {},
        mockInfo: {},
        requestInfo: [],
        headerInfo: []
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
    getGroupName () {
      let groupName = ''
      try {
        groupName = this.detailGroupList.filter(item => item.groupID === this.getApiInfo.baseInfo.groupID)[0].groupName
      } catch (error) {}
      return groupName
    }
  },
  watch: {
    'getApiInfo': {
      handler (val) {
        if (val.baseInfo.apiURI) {
          let indexInitSubMenu = Object.values(this.groupComp.indexInitSubMenu)
          if (this.groupType === GROUP_TYPE.API) {
            this.$store.dispatch('apilist.group/changeGroupComp', { defaultOpeneds: [`${val.baseInfo.groupID}`] })
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
          let requestInfo = (val.requestInfo || []).map(item => {
            return {
              paramKey: item.paramKey,
              paramValue: item.paramValue,
              paramType: item.paramType
            }
          })
          let jsonBody = val.baseInfo.apiRequestRaw || {}
          try {
            jsonBody = JSON.parse(jsonBody)
          } catch (error) {}
          let requestType = REQUEST_TYPE[val.baseInfo.apiRequestType].toLocaleLowerCase()
          let apiURI = val.baseInfo.apiURI
          if (val.baseInfo.apiRequestParamType === API_REQUEST_PARAM_TYPE.FORM_DATA && `${val.baseInfo.apiRequestType}` === REQUEST_TYPE_EX.GET) {
            let q = ''
            requestInfo.forEach((v, index) => {
              q += `${v.paramKey}=${v.paramValue}`
              if (requestInfo.length !== (index + 1)) {
                q += '&'
              }
            })
            if (val.baseInfo.apiURI.indexOf("?") === -1) {
              apiURI += `?${q}`
            } else {
              apiURI += `&${q}`
            }
          }
          this.$store.dispatch('apitest/changeApiTestTarget', {
            headers: headerInfo,
            defaultHeader: defaultHeader,
            body: (val.baseInfo.apiRequestParamType === API_REQUEST_PARAM_TYPE.FORM_DATA && `${val.baseInfo.apiRequestType}` !== REQUEST_TYPE_EX.GET) ? requestInfo : [],
            jsonBody,
            query: [],
            selectCaseId: null,
            response: {
              content: {}
            },
            httpEnv: undefined,
            requestType,
            url: apiURI,
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
  align-items: center;
  justify-content: flex-start;
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
