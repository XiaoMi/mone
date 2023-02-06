<template>
	<div class="d-detail-headers">
		<h4>{{$i18n.t('detailList.basicInformation')}}</h4>
		<EditBaseInfo v-if="isEditDetail"/>
		<el-form :label-position="isEN ? 'top': 'left'" class="detail-base-info" v-else label-width="88px" >
			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="13">
					<el-form-item prop="apiStatus" :label="`${$i18n.t('ApiClass.serviceStatus')}:`">
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
					<el-form-item prop="name" :label="`${$i18n.t('interfaceName')}:`">
						<span class="not-edit-info">{{getApiInfo.name}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10" :offset="1">
					<el-form-item prop="apidocname" :label="`${$i18n.t('interfaceDescription')}:`">
						<span class="not-edit-info">{{getApiInfo.dubboApiBaseInfo.apidocname}}</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="13">
					<el-form-item prop="apimodelclass" :label="`${$i18n.t('ApiClass.serviceName')}:`">
						<span class="not-edit-info">{{getApiInfo.dubboApiBaseInfo.apimodelclass}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10" :offset="1">
					<el-form-item prop="apiname" :label="`${$i18n.t('ApiClass.methodName')}:`">
						<span class="not-edit-info">{{getApiInfo.dubboApiBaseInfo.apiname}}</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="13">
					<el-form-item prop="apiversion" :label="`${$i18n.t('serviceVersion')}:`">
						<span class="not-edit-info">{{getApiInfo.dubboApiBaseInfo.apiversion}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10" :offset="1">
					<el-form-item prop="apigroup" :label="`${$i18n.t('serviceGrouping')}:`">
						<span class="not-edit-info">{{getApiInfo.dubboApiBaseInfo.apigroup}}</span>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="13">
					<el-form-item prop="async" :label="`${$i18n.t('asynchronousCall')}:`">
						<span class="not-edit-info">{{getApiInfo.dubboApiBaseInfo.async ? $i18n.t('yes') : $i18n.t('no')}}</span>
					</el-form-item>
				</el-col>
				<el-col :span="10" :offset="1">
					<el-form-item prop="apiDesc" :label="`${$i18n.t('serviceDescription')}:`">
						<span class="not-edit-info">{{getApiInfo.apiDesc || $i18n.t('noDescription')}}</span>
					</el-form-item>
				</el-col>
			</el-row>
			<!-- <el-row type="flex" justify="space-between" :gutter="0">
				<el-col :span="24">
					<el-form-item label="MockUrl:">
						<div class="tips-info">
							<a :href="getApiInfo.mockUrl || 'javascript:;'" target="_blank" class="not-edit-info link">{{getApiInfo.mockUrl || $i18n.t('noAddress')}}</a>
              <el-popover trigger="hover" placement="top">
                <div style="font-size: 12px">{{$i18n.t('detailMockTips.first')}}<br/>{{$i18n.t('detailMockTips.sec')}}</div>
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
			<el-row type="flex" justify="space-between" :gutter="0">
				<el-col v-show="getApiInfo.mavenAddr" :span="13">
					<el-form-item prop="mavenAddr" :label="`${$i18n.t('mavenDependencies')}:`">
            <div class="code-maven">
              <MarkdownDoc :content="handleContent(getApiInfo.mavenAddr || {})"/>
            </div>
					</el-form-item>
				</el-col>
        <el-col :span="10" :offset="getApiInfo.mavenAddr ? 1 : 0">
					<el-form-item prop="serviceVersion" :label="`${$i18n.t('lastUpdater')}:`">
						<span class="not-edit-info">{{getApiInfo.updateUsername}}</span>
					</el-form-item>
				</el-col>
			</el-row>
		</el-form>
	</div>
</template>

<script>
import { PROTOCOL, REQUEST_TYPE, API_STATUS, SHOW_CODE_TYPE } from '@/views/constant'
import { mapGetters } from 'vuex'
import EditBaseInfo from './EditBaseInfo'
import { GROUP_TYPE } from '@/views/ApiList/constant'
import MarkdownDoc from '@/components/MarkdownDoc'

export default {
  name: 'BaseInfo',
  components: {
    EditBaseInfo,
    MarkdownDoc
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
      'groupType',
      'groupComp'
    ]),
    getApiInfo () {
      return this.$store.getters.apiDetail.dubboApiBaseInfo ? this.$store.getters.apiDetail : {
        dubboApiBaseInfo: {},
        name: ''
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
        groupName = this.groupOption.filter(item => item.groupID === this.getApiInfo.groupID)[0].groupName
      } catch (error) {}
      return groupName
    }
  },
  watch: {
    'getApiInfo': {
      handler (val) {
        if (val.dubboApiBaseInfo.apiname) {
          let indexInitSubMenu = Object.values(this.groupComp.indexInitSubMenu)
          if (this.groupType === GROUP_TYPE.API) {
          	this.$store.dispatch('apilist.group/changeGroupComp', { defaultOpeneds: [`${val.groupID}`] })
          } else if (indexInitSubMenu.length) {
            let target = indexInitSubMenu.flat().filter(v => `${v.apiID}` === this.$utils.getQuery('apiID'))
            if (target.length) {
              this.$store.dispatch('apilist.group/changeGroupComp', { defaultOpeneds: [`${target[0].index_id}`] })
            }
          }
          let paramType = ''
          let methodparaminfo = val.dubboApiBaseInfo.methodparaminfo || []
          try {
            methodparaminfo = JSON.parse(methodparaminfo)
          } catch (error) {}
          if (Array.isArray(methodparaminfo) && methodparaminfo.length) {
            paramType = []
            methodparaminfo.forEach(item => {
              paramType.push(item.itemClassStr)
            })
            paramType = JSON.stringify(paramType)
          }
          let parameter = []
          if (val.reqExpList && val.reqExpList.length) {
            let arr = val.reqExpList.filter(item => item.requestParamExpType === SHOW_CODE_TYPE['3'].type)
            if (arr.length) {
              try {
                parameter = JSON.parse(arr[0].codeGenExp || "[]")
              } catch (error) {
                parameter = arr[0].codeGenExp
              }
            }
          }
          this.$store.dispatch('apitest/changeApiTestTarget', {
            version: val.dubboApiBaseInfo.apiversion,
            interfaceName: val.dubboApiBaseInfo.apimodelclass,
            methodName: val.dubboApiBaseInfo.apiname,
            isGenParam: false,
            hasAttachment: false,
            attachments: [],
            selectCaseId: null,
            response: {
              content: {}
            },
            paramType,
            group: val.dubboApiBaseInfo.apigroup,
            isRequestEnd: new Date().getTime(),
            parameter
          })
        }
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
  },
  methods: {
    handleContent (c) {
      let strs = ["<artifactId>","<groupId>", "<version>", "<exclusions>", "<scope>", "<type>", "<classifier>", "<optional>", "<systemPath>"]
      if (typeof c === 'string') {
        try {
          c = c.replace(/\s/g,"").replace(/\"/g,"")
          for (let i = 0; i < strs.length; i++) {
            c = c.replace(strs[i],`\n   ${strs[i]}`)
          }
          c = c.replace("</dependency>","\n</dependency>")
        } catch (error) {}
      }
      return "```\n"+`${c}`+"\n```"
    },
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
.code-maven {
  width: 100%;
}
</style>
