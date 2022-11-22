<template>
	<div class="grpc-container">
		<el-divider content-position="center" style="margin: 20px 0 20px">
			<el-icon color="#ff0000"><star-filled /></el-icon> Grpc<el-divider direction="vertical" />{{apiInfo.apiName}}
    </el-divider>
		<BaseInfo :getApiInfo="apiInfo"/>
		<Request :apiInfo="apiInfo"/>
		<ReturnParam :apiInfo="apiInfo"/>
		<ErrorCode :apiErrorCodes="apiErrorCodes"/>
		<Remark :getApiInfo="apiInfo"/>
	</div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, watch } from 'vue'
import BaseInfo from "./BaseInfo.vue"
import Request from "./Request.vue"
import ReturnParam from "./ReturnParam.vue"
import ErrorCode from "../Http/ErrorCode.vue"
import Remark from "./Remark.vue"

export default defineComponent({
	components: {
		BaseInfo,
		Request,
		ReturnParam,
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
			if (val && val.errorCodes) {
				let errorcodes = val.errorCodes || []
				try {
					errorcodes = JSON.parse(errorcodes)
				} catch (error) {}
				state.apiErrorCodes = errorcodes
			} else {
				state.apiErrorCodes = []
			}
		},{
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
.grpc-container:deep(h4) {
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
