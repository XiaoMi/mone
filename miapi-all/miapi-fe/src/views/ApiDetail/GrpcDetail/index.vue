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
import BaseInfo from "./BaseInfo"
import RequestParam from "./RequestParam"
import ReturnParam from "./ReturnParam"
import ErrorCode from '@/views/ApiDetail/HttpDetail/ErrorCode'
import DocDetail from "./DocDetail"
export default {
  name: 'GrpcDetail',
  components: {
    BaseInfo,
    RequestParam,
    ReturnParam,
    ErrorCode,
    DocDetail
  },
  computed: {
    ...mapGetters([
      'isEditDetail',
      'apiDetail'
    ])
  },
  data () {
    return {
      apiErrorCodes: []
    }
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
        if (val && val.errorCodes) {
          let errorcodes = val.errorCodes || []
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
      this.$store.dispatch('apilist.add/changeGrpcUpdateParam', {
        apiErrorCodes: val
      })
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
