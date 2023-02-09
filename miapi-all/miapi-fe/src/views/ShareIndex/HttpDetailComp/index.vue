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
				<Code :reqExpList="reqExpList"/>
			</div>
			<div class="api-detail-content">
				<ReturnExmp :respExpList="respExpList"/>
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
import RequestParam from '@/views/ApiDetail/HttpDetail/RequestParam'
import ReturnParam from '@/views/ApiDetail/HttpDetail/ReturnParam'
import DocDetail from '@/views/ApiDetail/HttpDetail/DocDetail'
import Code from '@/views/ApiDetail/HttpDetail/Code'
import ReturnExmp from '@/views/ApiDetail/HttpDetail/ReturnExmp'
import ErrorCode from '@/views/ApiDetail/HttpDetail/ErrorCode'
export default {
  name: 'HttpDetailComp',
  components: {
    RequestParam,
    ReturnParam,
    DocDetail,
    Code,
    ReturnExmp,
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
          this.$store.dispatch('apilist/changeApiDetail', { ...val.apiInfo })
          if (val.apiInfo.reqExpList && val.apiInfo.reqExpList.length) {
            this.reqExpList = val.apiInfo.reqExpList
          } else {
            this.reqExpList = []
          }
          if (val.apiInfo.respExpList && val.apiInfo.respExpList.length) {
            this.respExpList = val.apiInfo.respExpList
          } else {
            this.respExpList = []
          }
          if (val.apiInfo.apiErrorCodes && val.apiInfo.apiErrorCodes.length) {
            this.apiErrorCodes = val.apiInfo.apiErrorCodes
          } else {
            this.apiErrorCodes = []
          }
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
