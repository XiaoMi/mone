<template>
	<div class="gateway-container">
		<el-divider content-position="center" style="margin: 20px 0 20px">
			<el-icon color="#ff0000"><star-filled /></el-icon> Gateway<el-divider direction="vertical" />{{apiInfo.gatewayApiBaseInfo.methodName}}
    </el-divider>
		<BaseInfo :getApiInfo="apiInfo"/>
		<Request :apiInfo="apiInfo"/>
		<ReturnParam :apiInfo="apiInfo"/>
		<CodeComp :apiDetail="apiInfo" :reqExpList="apiInfo.reqExpList || []"/>
		<ReturnExmp :respExpList="apiInfo.respExpList || []"/>
		<ErrorCode :apiErrorCodes="apiErrorCodes"/>
		<Remark :getApiInfo="apiInfo"/>
	</div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, watch } from 'vue'
import BaseInfo from "./BaseInfo.vue"
import Request from "./Request.vue"
import ReturnParam from "./ReturnParam.vue"
import CodeComp from "../Http/CodeComp.vue"
import ReturnExmp from "../Http/ReturnExmp.vue"
import ErrorCode from "../Http/ErrorCode.vue"
import Remark from "./Remark.vue"

export default defineComponent({
	components: {
		BaseInfo,
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
				return {}
			}
		}
	},
	setup(props) {
		const state = reactive({
			apiErrorCodes: []
		})
		watch(() => props.apiInfo, (val) => {
			if (val.apiErrorCodes) {
				let apiErrorCodes = val.apiErrorCodes || []
				try {
					apiErrorCodes = JSON.parse(apiErrorCodes)
				} catch (error) {}
				state.apiErrorCodes = apiErrorCodes
			} else {
				state.apiErrorCodes = []
			}
		}, {
			immediate: true,
			deep: true
		})

		return {
			...toRefs(state)
		}
	},
})
</script>

<style lang="scss" scoped>
.gateway-container:deep(h4) {
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