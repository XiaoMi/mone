<template>
	<div class="d-detail-headers">
		<h4>{{$i18n.t('detailList.basicInformation')}}</h4>
		<EditBaseInfo v-if="isEditDetail"/>
		<el-form :label-position="isEN ? 'top': 'left'" class="detail-base-info" v-else label-width="88px" >
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

			<!-- <el-row v-if="customMockUrl" type="flex" justify="space-between" :gutter="20">
				<el-col :span="24">
					<el-form-item :label="`${$i18n.t('mockAddress')}:`">
						<div class="tips-info">
              <a :href="customMockUrl || 'javascript:;'" target="_blank" class="not-edit-info link">{{customMockUrl}}</a>
            </div>
					</el-form-item>
				</el-col>
			</el-row> -->
		</el-form>
	</div>
</template>

<script>
import { REQUEST_TYPE, API_STATUS } from '@/views/constant'
import { mapGetters } from 'vuex'
import EditBaseInfo from './EditBaseInfo'
import { GROUP_TYPE } from '@/views/ApiList/constant'

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
      'groupComp',
      'groupType'
    ]),
    getApiInfo () {
      return this.$store.getters.apiDetail || {}
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
        groupName = this.groupOption.filter(item => item.groupID === this.getApiInfo.groupID)[0].groupName
      } catch (error) {}
      return groupName
    }
  },
  watch: {
    'getApiInfo': {
      handler (val) {
        if (val.methodName) {
          let indexInitSubMenu = Object.values(this.groupComp.indexInitSubMenu)
          if (this.groupType === GROUP_TYPE.API) {
            this.$store.dispatch('apilist.group/changeGroupComp', { defaultOpeneds: [`${val.groupID}`] })
          } else if (indexInitSubMenu.length) {
            let target = indexInitSubMenu.flat().filter(v => `${v.apiID}` === this.$utils.getQuery('apiID'))
            if (target.length) {
              this.$store.dispatch('apilist.group/changeGroupComp', { defaultOpeneds: [`${target[0].index_id}`] })
            }
          }
          let packageName = val.fullApiPath.split(".")
          packageName.splice(packageName.length - 2, packageName.length - 1)
          this.$store.dispatch('apitest/changeGrpcParam', {
            packageName: packageName.join("."),
            interfaceName: val.serviceName,
            methodName: val.methodName,
            parameter: "",
            timeout: 1000,
            addrs: "",
            isRequestEnd: new Date().getTime()
          })
          this.$store.dispatch('apitest/changeApiTestTarget', {
            selectCaseId: null
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
