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
export default {
  name: 'GatewayDetail',
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
          this.reqExpList = val.reqExpList
        } else {
          this.reqExpList = []
        }
        if (val.respExpList && val.respExpList.length) {
          this.respExpList = val.respExpList
        } else {
          this.respExpList = []
        }
        if (val.apiErrorCodes) {
          let apiErrorCodes = val.apiErrorCodes || []
          try {
            apiErrorCodes = JSON.parse(apiErrorCodes)
          } catch (error) {}
          this.apiErrorCodes = apiErrorCodes
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
      this.$store.dispatch('apilist.add/changeGatewayParam', { apiErrorCodes: val })
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
