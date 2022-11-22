<template>
	<div class="dubbo-container">
		<el-divider content-position="center" style="margin: 20px 0 20px">
			<el-icon color="#ff0000"><star-filled /></el-icon> Dubbo<el-divider direction="vertical" />{{apiInfo.dubboApiBaseInfo.apiname}}
    </el-divider>
		<BaseInfo :getApiInfo="apiInfo"/>
		<Request :apiDetail="apiInfo"/>
		<ReturnParam :apiInfo="apiInfo"/>
		<CodeComp :apiDetail="apiInfo" :reqExpList="apiInfo.reqExpList || []"/>
		<ReturnExmp :respExpList="apiInfo.respExpList || []"/>
		<ErrorCode :apiErrorCodes="apiErrorCodes"/>
		<Remark :getApiInfo="apiInfo"/>
	</div>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, watch } from 'vue'
import Request from "./Request.vue"
import ReturnParam from "./ReturnParam.vue"
import Remark from "./Remark.vue"
import BaseInfo from "./BaseInfo.vue"
import CodeComp from "../Http/CodeComp.vue"
import ReturnExmp from "../Http/ReturnExmp.vue"
import ErrorCode from "../Http/ErrorCode.vue"


export default defineComponent({
	components: {
		BaseInfo,
		Request,
		ReturnParam,
		ErrorCode,
		Remark,
		CodeComp,
		ReturnExmp
	},
	props: {
		apiInfo: {
			type: Object,
			default: () => {
				return {
					dubboApiBaseInfo: {}
				}
			}
		}
	},
	setup(props) {
		const state = reactive({
			apiErrorCodes: []
		})
		
		watch(() => props.apiInfo.dubboApiBaseInfo, () => {
			if (props.apiInfo.dubboApiBaseInfo && props.apiInfo.dubboApiBaseInfo.errorcodes) {
				let errorcodes = props.apiInfo.dubboApiBaseInfo.errorcodes || []
				try {
					errorcodes = JSON.parse(errorcodes)
				} catch (error) {}
				state.apiErrorCodes = errorcodes
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
.dubbo-container:deep(h4) {
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