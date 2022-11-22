<template>
	<div class="exmple-container">
		<div>
			<h3>{{$i18n.t("customMockDescription.title1")}}</h3>
			<ul class="variable-desc">
				<li v-for="v in descList" :key="v.name">
					<span>{{v.name}}</span>
					<span>{{v.desc}} <el-link v-if="v.link" :href="v.link" target="_blank" type="primary"><el-icon :size="14"><Link /></el-icon></el-link></span>
				</li>
			</ul>
		</div>
		<div class="use-method">
			<h3>{{$i18n.t("customMockDescription.title2")}}</h3>
			<p>{{$i18n.t("customMockDescription.method1")}}</p>
			<p>{{$i18n.t("customMockDescription.method2")}}</p>
		</div>
		<div class="exmp">
			<h3>{{$i18n.t("customMockDescription.exmp1")}}</h3>
			<p>
				if(params.type == 1){<br/>
				&emsp;mockJson.errcode = 400;<br/>
				&emsp;mockJson.errmsg = 'error';<br/>
				}<br/>

				if(header.token == 't'){<br/>
				&emsp;mockJson.errcode = 300;<br/>
				&emsp;mockJson.errmsg = 'error';<br/>
				}<br/>

				if(cookie.type == 'a'){<br/>
				&emsp;mockJson.errcode = 500;<br/>
				&emsp;mockJson.errmsg = 'error';<br/>
				}<br/>
			</p>
		</div>
		<div class="exmp">
			<h3>{{$i18n.t("customMockDescription.exmp2")}}</h3>
			<p>
				var a = [1,1,1,1,1,1,1,1,1,1]<br/>

				mockJson = {<br/>
				&emsp;errcode: 0,<br/>
				&emsp;email: Random.email('qq.com'),<br/>
				&emsp;data: a.map(function(item){<br/>
				&emsp;&emsp;return Random.city() + '银行'<br/>
				&emsp;})<br/>
				}<br/>
			</p>
		</div>
	</div>
</template>
<script>
export default {
  name: "ExmpleMock",
  data () {
    return {
      descList: [{
        name: 'header',
        desc: this.$i18n.t('customMockDescription.headerDesc')
      }, {
        name: 'params',
        desc: this.$i18n.t('customMockDescription.paramsDesc')
      }, {
        name: 'cookie',
        desc: this.$i18n.t('customMockDescription.cookieDesc')
      }, {
        name: 'mockJson',
        desc: this.$i18n.t('customMockDescription.mockJsonDesc')
      }, {
        name: 'Random',
        desc: this.$i18n.t('customMockDescription.randomDesc'),
        link: "https://github.com/nuysoft/Mock/wiki/Mock.Random"
      }]
    }
  }
}
</script>
<style scoped>
.exmple-container > div{
	margin-bottom: 12px;
}
.exmple-container h3 {
	color: #333;
	margin-bottom: 6px;
}
.exmple-container .variable-desc li	{
	display: flex;
	align-items: center;
	justify-content: flex-start;
	margin-bottom: 6px;
}
.exmple-container .variable-desc li span:first-child{
	background: rgba(27, 31, 35, 0.05);
	padding: 1px 4px;
	border-radius: 4px;
	margin-right: 4px;
}
.exmple-container .use-method p{
	margin: 0 0 6px;
}
.exmple-container .exmp p{
	background: rgba(27, 31, 35, 0.05);
	padding: 4px 8px;
	border-radius: 4px;
}
</style>
