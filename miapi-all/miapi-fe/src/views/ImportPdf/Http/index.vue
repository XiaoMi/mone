<template>
	<div class="http-container">
		<el-divider content-position="center" style="margin: 20px 0 20px">
			<el-icon color="#ff0000"><star-filled /></el-icon> Http<el-divider direction="vertical" />{{apiInfo.baseInfo.apiURI}}
    </el-divider>
		<Request :apiInfo="apiInfo"/>
		<ReturnParam :apiInfo="apiInfo"/>
		<CodeComp :apiDetail="apiInfo" :reqExpList="apiInfo.reqExpList || []"/>
		<ReturnExmp :respExpList="apiInfo.respExpList || []"/>
		<ErrorCode :apiErrorCodes="apiInfo.apiErrorCodes || []"/>
		<Remark :getApiInfo="apiInfo"/>
	</div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import Request from "./Request.vue";
import ReturnParam from "./ReturnParam.vue"
import CodeComp from "./CodeComp.vue"
import ReturnExmp from "./ReturnExmp.vue"
import ErrorCode from "./ErrorCode.vue"
import Remark from "./Remark.vue"

export default defineComponent({
	components: {
		Request,
		ReturnParam,
		CodeComp,
		ReturnExmp,
		ErrorCode,
		Remark
	},
	props: {
		apiInfo: {
			type: Object,
			default: () => {
				return {
					baseInfo: {},
					headerInfo: [],
					mockInfo: {},
					requestInfo: [],
					resultInfo: [],
					testHistory: []
				}
			}
		}
	},
	setup(props) {
		return {
			apiInfo: props.apiInfo
		}
	},
})
</script>

<style lang="scss" scoped>
.http-container:deep(h4) {
	padding: 0 16px 0 20px;
	font-size: 16px;
	position: relative;
	i{
		display: inline-block;
		vertical-align: -2px;
		margin-right: 4px;
	}
}
</style>