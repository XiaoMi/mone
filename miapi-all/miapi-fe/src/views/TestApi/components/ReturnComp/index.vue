<template>
	<div id="api-test-return-container" class="api-test-return-container">
		<el-tabs v-model="activeName">
			<el-tab-pane :label="$i18n.t('apiTest.returnResult')" name="response">
				<div class="return-common-wrap">
					<ResponseJson/>
				</div>
			</el-tab-pane>
		</el-tabs>
	</div>
</template>
<script>
import ResponseJson from './json.vue'
export default {
  name: 'ReturnComp',
  components: {
    ResponseJson
  },
  data () {
    return {
      activeName: 'response'
    }
  }
}
</script>
<style scoped>
.api-test-return-container {
	background: #fff;
}
.api-test-return-container >>> .el-tabs__header {
	margin-bottom: 20px !important;
	padding: 0 !important;
}
.api-test-return-container >>> .el-tabs__header .el-tabs__item {
	height: 46px;
	line-height: 46px;
	box-sizing: content-box;
	text-align: center;
	padding: 0 16px;
}
.api-test-return-container >>> .el-tabs__header .el-tabs__item:nth-child(2){
	padding: 0;
}
.api-test-return-container >>> .el-tabs__nav-wrap::after {
	height: 1px;
}
.api-test-return-container .return-common-wrap {
	padding: 0 20px 20px;
}
.api-test-return-container >>> .el-tabs__nav {
	margin-left: 16px;
}
</style>
