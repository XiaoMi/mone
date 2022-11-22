<template>
	<div class="x5-comp">
		<el-tabs v-model="activeName">
			<el-tab-pane :label="$i18n.t('apiTest.X5Permissions')" name="x5"><X5Auth/></el-tab-pane>
		</el-tabs>
	</div>
</template>

<script>
import X5Auth from './X5permissions.vue'
export default {
  name: 'X5Comp',
  components: {
    X5Auth
  },
  data () {
    return {
      activeName: 'x5'
    }
  }
}
</script>
<style scoped>
.x5-comp {
	background: #fff;
	padding: 0;
	margin-bottom: 20px;
}
.x5-comp >>> .el-tabs__header {
	margin-bottom: 0;
}
.x5-comp >>> .el-tabs__nav-wrap::after {
	height: 1px;
}
.x5-comp >>> .el-tabs__header .el-tabs__item {
	height: 46px;
	line-height: 46px;
	box-sizing: content-box;
	text-align: center;
	padding: 0 16px;
}
.x5-comp >>> .el-tabs__header .el-tabs__item:nth-child(2){
	padding: 0;
}
.x5-comp >>> .el-tabs__nav-wrap::after {
	height: 1px;
}
.x5-comp >>> .el-tabs__nav {
	margin-left: 16px;
}
</style>
