<template>
	<div class="request-comp">
		<el-tabs v-model="activeName">
			<el-tab-pane :label="$i18n.t('apiTest.requestHeader')" name="header"><RequestHeader/></el-tab-pane>
			<el-tab-pane :label="$i18n.t('apiTest.requestBody')" name="body"><RequestBody/></el-tab-pane>
			<el-tab-pane :label="$i18n.t('apiTest.queryParameters')" name="query"><RequestQuery/></el-tab-pane>
		</el-tabs>
	</div>
</template>

<script>
import RequestHeader from './RequestHeader.vue'
import RequestBody from './RequestBody.vue'
import RequestQuery from './RequestQuery.vue'
export default {
  name: 'RequestComp',
  components: {
    RequestHeader,
    RequestBody,
    RequestQuery
  },
  data () {
    return {
      activeName: 'header'
    }
  }
}
</script>
<style scoped>
.request-comp {
	background: #fff;
	padding: 0 0 20px;
	margin-bottom: 20px;
}
.request-comp >>> .el-tabs__header {
	margin-bottom: 0;
}
.request-comp >>> .el-tabs__nav-wrap::after {
	height: 1px;
}
.request-comp >>> .el-tabs__header .el-tabs__item {
	height: 46px;
	line-height: 46px;
	box-sizing: content-box;
	text-align: center;
	padding: 0 16px;
}
.request-comp >>> .el-tabs__header .el-tabs__item:nth-child(2){
	padding-left: 0;
}
.request-comp >>> .el-tabs__nav-wrap::after {
	height: 1px;
}
.request-comp >>> .el-tabs__nav {
	margin-left: 16px;
}
</style>
