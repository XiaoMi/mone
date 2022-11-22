<template>
	<div class="api-detail-wrap">
		<div class="api-detail-container">
			<div class="api-detail-content">
				<RequestParam/>
			</div>
			<div class="api-detail-content">
				<ReturnParam/>
			</div>
			<div class="api-detail-content">
        <ErrorCode :apiErrorCodes="apiErrorCodes"/>
			</div>
			<div class="api-detail-content">
				<DocDetail/>
			</div>
		</div>
	</div>
</template>

<script>
import { mapGetters } from 'vuex'
import RequestParam from '@/views/ApiDetail/GrpcDetail/RequestParam'
import ReturnParam from '@/views/ApiDetail/GrpcDetail/ReturnParam'
import DocDetail from '@/views/ApiDetail/GrpcDetail/DocDetail'
import ErrorCode from '@/views/ApiDetail/HttpDetail/ErrorCode'
export default {
  name: 'GrpcDetailComp',
  components: {
    RequestParam,
    ReturnParam,
    DocDetail,
    ErrorCode
  },
  computed: {
    ...mapGetters([
      'shareApiDetail'
    ])
  },
  data () {
    return {
      reqExpList: [],
      respExpList: [],
      apiErrorCodes: []
    }
  },
  watch: {
    shareApiDetail: {
      handler (val) {
        if (val.apiInfo) {
          this.$store.dispatch('apilist/changeApiDetail', {
            ...val.apiInfo,
            requestInfo: val.apiInfo.requestInfo ? [val.apiInfo.requestInfo] : [],
            resultInfo: val.apiInfo.resultInfo ? [val.apiInfo.resultInfo] : []
          })
        }
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
