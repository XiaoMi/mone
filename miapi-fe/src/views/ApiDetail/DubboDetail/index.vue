<template>
	<div class="api-detail-wrap">
		<div class="api-detail-container">
			<div class="api-detail-content">
				<BaseInfo/>
			</div>
			<div class="api-detail-content">
				<RequestParam/>
			</div>
			<div class="api-detail-content">
				<ReturnParam/>
			</div>
			<div v-show="!isEditDetail" class="api-detail-content">
				<Code :reqExpList="reqExpList"/>
			</div>
			<div v-show="!isEditDetail" class="api-detail-content">
				<ReturnExmp :respExpList="respExpList"/>
			</div>
			<div class="api-detail-content">
				<ErrorCode :apiErrorCodes="apiErrorCodes" @onChange="handleChangeCode"/>
			</div>
			<div class="api-detail-content">
				<DocDetail/>
			</div>
		</div>
	</div>
</template>

<script>
import { mapGetters } from 'vuex'
import BaseInfo from './BaseInfo'
import RequestParam from './RequestParam'
import ReturnParam from './ReturnParam'
import DocDetail from './DocDetail'
import Code from '@/views/ApiDetail/HttpDetail/Code'
import ReturnExmp from '@/views/ApiDetail/HttpDetail/ReturnExmp'
import ErrorCode from '@/views/ApiDetail/HttpDetail/ErrorCode'
import { SHOW_CODE_TYPE } from "@/views/constant"
export default {
  name: 'DubboDetail',
  components: {
    BaseInfo,
    RequestParam,
    ReturnParam,
    DocDetail,
    Code,
    ReturnExmp,
    ErrorCode
  },
  data () {
    return {
      reqExpList: [],
      respExpList: [],
      apiErrorCodes: []
    }
  },
  computed: {
    ...mapGetters([
      'isEditDetail',
      'apiDetail'
    ])
  },
  watch: {
    $route: {
      handler () {
        let projectID = this.$utils.getQuery('projectID')
        let indexProjectID = this.$utils.getQuery('indexProjectID')
        if (projectID) {
          this.$store.dispatch('apilist/apiDetail', { apiID: this.$utils.getQuery('apiID'), apiProtocol: this.$utils.getQuery('apiProtocol'), projectID, indexProjectID })
        }
      },
      immediate: true,
      deep: true
    },
    apiDetail: {
      handler (val) {
        if (val.reqExpList && val.reqExpList.length) {
          let reqExpList = val.reqExpList || []
          if (!reqExpList.some(item => item.requestParamExpType === SHOW_CODE_TYPE['3'].type)) {
            reqExpList.push({
              apiId: this.$utils.getQuery("apiID"),
              codeGenExp: "[]",
              id: new Date().getTime(),
              requestParamExpType: SHOW_CODE_TYPE['3'].type
            })
          }
          this.reqExpList = reqExpList
        }
        if (val.respExpList && val.respExpList.length) {
          this.respExpList = val.respExpList || []
        }
        if (val.dubboApiBaseInfo && val.dubboApiBaseInfo.errorcodes) {
          let errorcodes = val.dubboApiBaseInfo.errorcodes || []
          try {
            errorcodes = JSON.parse(errorcodes)
          } catch (error) {}
          this.apiErrorCodes = errorcodes
        } else {
          this.apiErrorCodes = []
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleChangeCode (val) {
      this.$store.dispatch('apilist.add/changeDubboParam', { apiErrorCodes: val })
    }
  }
}
</script>

<style scoped>
.api-detail-container {
	margin: 20px;
}
.api-detail-container .api-detail-content{
	background: #fff;
	margin-bottom: 28px;
}
.api-detail-container .api-detail-content >>> h4 {
	padding: 16px;
	font-size: 16px;
	border-bottom: 1px solid #e6e6e6;
}
</style>
