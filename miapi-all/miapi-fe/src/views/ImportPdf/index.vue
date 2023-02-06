<template>
	<div class="container-wrap">
		<template v-if="loading">
			<div class="loading-wrap">
				<el-skeleton :rows="10" animated />
			</div>
		</template>
		<template v-else>
			<div class="pdf-container watermarked">
				<template v-for="(v, i) in apiInfos">
					<Http v-if="v.isHttp" :key="i" :apiInfo="v.data"/>
					<Dubbo v-if="v.isDubbo" :key="i" :apiInfo="v.data"/>
					<Gateway v-if="v.isGateway" :key="i" :apiInfo="v.data"/>
					<Grpc v-if="v.isGrpc" :key="i" :apiInfo="v.data"/>
				</template>
			</div>
			<el-button @click="handleImport" class="import-btn" type="primary" round>{{$i18n.t('export')}}</el-button>
		</template>
	</div>
</template>

<script lang="ts">
import { defineComponent, toRefs, onMounted, ref, reactive, nextTick } from 'vue'
import { html2pdf } from "@/common/html2pdf"
import Http from "./Http/index.vue"
import Dubbo from "./Dubbo/index.vue"
import Gateway from "./Gateway/index.vue"
import Grpc from "./Grpc/index.vue"
import drawWaterMark from '@/common/waterMark'
import { getQuery } from "@/utils"
import { getHttpApi, getGatewayApiDetail, getDubboApiDetail, getGrpcApiDetail } from "@/api/apilist"
import { ElMessage } from "element-plus"

export default defineComponent({
	components: {
		Http,
		Dubbo,
		Gateway,
		Grpc
	},
	setup() {
		const timer = ref(null)
		const msgRef = ref(null)
		const canDownload = ref(false)
		let httpIds = getQuery("http")
		let dubboIds = getQuery("dubbo")
		let gatewayIds = getQuery("gateway")
		let grpcIds = getQuery("grpc")
		let projectID = getQuery("projectID")
		const state = reactive({
			loading: true,
			apiInfos:[]
		})
		let ids = {
			httpIds: [],
			dubboIds: [],
			gatewayIds: [],
			grpcIds: [],
		}
		ids.httpIds = httpIds && httpIds.split(',') || []
		ids.dubboIds = dubboIds && dubboIds.split(',') || []
		ids.gatewayIds = gatewayIds && gatewayIds.split(',') || []
		ids.grpcIds = grpcIds && grpcIds.split(',') || []
		Promise.all(Object.keys(ids).map((k) => {
			if (k === "httpIds") {
				return Promise.all(ids[k].map((id)=> getHttpApi({projectID, apiID: id})))
			}else if (k === "dubboIds") {
				return Promise.all(ids[k].map((id)=> getDubboApiDetail({projectID, apiID: id})))
			}else if (k === "gatewayIds") {
				return Promise.all(ids[k].map((id)=> getGatewayApiDetail({projectID, apiID: id})))
			}else if (k === "grpcIds") {
				return Promise.all(ids[k].map((id)=> getGrpcApiDetail({projectID, apiID: id})))
			}
		})).then((resp) => {
			resp[0].forEach(r => {
				state.apiInfos.push({
					isHttp: true,
					data: r.data || {}
				})
			})
			resp[1].forEach(r => {
				state.apiInfos.push({
					isDubbo: true,
					data: r.data || {}
				})
			})
			resp[2].forEach(r => {
				state.apiInfos.push({
					isGateway: true,
					data: r.data || {}
				})
			})
			resp[3].forEach(r => {
				state.apiInfos.push({
					isGrpc: true,
					data: r.data || {}
				})
			})
			state.loading = false
			nextTick(() => {
				init()
			})
		}).catch(e=>{
			console.log(e)
		})

		const handlePdf = async ():Promise<void> => {
			msgRef.value && msgRef.value.close && msgRef.value.close()
			canDownload.value = false
      msgRef.value = ElMessage({
        dangerouslyUseHTMLString: true,
        message: '正在保存PDF...',
				type: 'warning',
        center: true,
        duration: 0
      })
			drawImg()
			await html2pdf(".pdf-container", "apis")
			msgRef.value && msgRef.value.close && msgRef.value.close()
		}

		const handleImport = ():void => {
			canDownload.value = true
			let dom = document.querySelector('.pdf-container')
			let scrollH = dom.scrollHeight
			let clientHeight = document.body.clientHeight
			if ((scrollH - clientHeight) < dom.scrollTop) {
				handlePdf()
			} else{
				dom.scroll({ top: dom.scrollHeight, left: 0, behavior: 'smooth' })
			}
		}

		const drawImg = ():void => {
			const option = {
				content: "api",
				color: "rgba(156, 162, 169, 0.15)",
				className: 'watermarked',
				bgHeight: `${document.querySelector('.pdf-container').scrollHeight}px`
			}
			drawWaterMark(option)
		}

		const init = ():void => {
			let dom = document.querySelector('.pdf-container')
			let scrollH = dom.scrollHeight
			let clientHeight = document.body.clientHeight
			dom.addEventListener("scroll", function (){
				if ((scrollH - clientHeight) < dom.scrollTop && canDownload.value) {
					clearTimeout(timer.value)
					timer.value = setTimeout(() => {
						handlePdf()
					}, 500);
				}
			}, false)
			drawImg()
		}
		
		return {
			handleImport,
			...toRefs(state)
		}
	},
})
</script>

<style lang="scss" scoped>
.container-wrap {
	height: 100%;
	.loading-wrap{
		background: #fff;
		width: 100%;
		height: 100%;
		padding: 40px 20px;
	}
	.pdf-container{
		margin: 0 20px;
		background: #fff;
		height: 100%;
		overflow-y: auto;
		padding: 40px 0 20px;

		&:deep(.el-divider--horizontal){
			border-top-width: 4px;
			border-top-color: #ccc;
			border-top-style: double;
		}
	}
	.import-btn {
		position: fixed;
		bottom: 20px;
		right: 20px;
		z-index: 99;
	}
	&:deep(.el-form-item){
		margin-bottom: 0 !important;
	}
}
</style>
