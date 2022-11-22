<template>
	<el-dialog custom-class="importpdf-dialog" v-model="show" @close="handleClose" destroy-on-close>
		<template #header>
			<div>
				<span>{{$i18n.t('chooseAPI')}}</span>
				<span style="font-size: 12px; color: #ec6617">（{{$i18n.t('importPdfTips')}}，已选择{{hasSelectedNum}}个）</span>
			</div>
		</template>
		<el-table
      :data="tableList"
      style="width: 100%;"
			height="440"
      row-key="id"
    >
      <el-table-column width="74">
				<template #default="scope">
					<el-checkbox v-model="scope.row.checked" :indeterminate="scope.row.indeterminate" @change="handleCheckBox($event, scope.row)" />
				</template>
			</el-table-column>
			<el-table-column property="label" :label="$i18n.t('table.name')" width="200"/>
			<el-table-column property="apiProtocol" :label="$i18n.t('ApiClass.type')" width="100">
				<template #default="scope">
					<span>{{protocol[scope.row.apiProtocol]}}</span>
				</template>
			</el-table-column>
			<el-table-column property="apiURI" :label="$i18n.t('ApiClass.interfacePath')" />
    </el-table>
		<template #footer>
			<el-button type="primary" @click="handleImportPdf">{{$i18n.t('btnText.ok')}}</el-button>
		</template>
	</el-dialog>
</template>

<script lang="ts">
import { defineComponent, reactive, toRefs, ref, watch, computed } from 'vue'
import { AJAX_SUCCESS_MESSAGE, PROTOCOL, REQUEST_TYPE, PROTOCOL_TYPE, DATA_TYPE_KEY, API_REQUEST_PARAM_TYPE } from '@/views/constant'
import { useStore } from 'vuex'
import i18n from "@/lang"
import * as utils from "@/utils"
import { ElMessage } from 'element-plus'
import { PATH } from '@/router/constant'

export default defineComponent({
	props: {
		visible: {
			type: Boolean,
			default: false
		}
	},
	emits: ['onCancel'],
	setup(props, ctx) {
    const store = useStore()

		const state = reactive({
			show: props.visible,
			pdfApiList: computed(() => store.getters.groupComp.initSubMenu),
			groupList: computed(() => store.getters.groupList),
			tableList: []
		})

		const handleImportPdf = ():void => {
			let apis = state.tableList.map(v => v.children).flat().filter(v => v.checked)
      if (apis.length > 30) {
        ElMessage.error(i18n.t("errorMessage.importPdfErrorNum"))
      }else if (apis.length) {
        let http = apis.filter(v => v.apiProtocol === Number(PROTOCOL_TYPE.HTTP)).map(v => v.apiID)
        let dubbo = apis.filter(v => v.apiProtocol === Number(PROTOCOL_TYPE.Dubbo)).map(v => v.apiID)
        let gateway = apis.filter(v => v.apiProtocol === Number(PROTOCOL_TYPE.Gateway)).map(v => v.apiID)
        let grpc = apis.filter(v => v.apiProtocol === Number(PROTOCOL_TYPE.Grpc)).map(v => v.apiID)
        window.open(`${window.location.origin}/#${PATH.IMPORT_PDF}?http=${http.join(',')}&dubbo=${dubbo.join(',')}&gateway=${gateway.join(',')}&grpc=${grpc.join(',')}&projectID=${utils.getQuery('projectID')}`)
      } else {
        ElMessage.info(i18n.t("errorMessage.pleaseSelectAnAPI"))
      }
    }

		const handleClose = ():void => {
			ctx.emit('onCancel')
		}

		const handleCheckBox = (bool, row):void => {
			if (row.isApi) {
				for (let i = 0; i < state.tableList.length; i++) {
					if (state.tableList[i].id === row.groupID) {
						for (let j = 0; j < state.tableList[i].children.length; j++) {
							if (state.tableList[i].children[j].id === row.id){
								state.tableList[i].children[j].checked = bool
								break;
							}
						}
						state.tableList[i].checked = state.tableList[i].children.some(v=>v.checked)
						state.tableList[i].indeterminate = state.tableList[i].children.some(v=>v.checked) && state.tableList[i].children.some(v=>!v.checked)
						break;
					}
				}
			} else {
				for (let i = 0; i < state.tableList.length; i++) {
					if (state.tableList[i].id === row.id) {
						state.tableList[i].checked = bool
						state.tableList[i].indeterminate = false
						state.tableList[i].children.forEach(v => {
							v.checked = bool
						})
						break;
					}
				}
			}
		}

		const hasSelectedNum = computed(() => (state.tableList.map(v => v.children).flat().filter(v => v.checked)).length)

		const handleInit = ():void => {
			if (Object.keys(state.pdfApiList).length && state.groupList.length) {
				state.tableList = state.groupList.map(g => {
					return {
						label: g.groupName,
						id: g.groupID,
						indeterminate: false,
						checked: false,
						children: (state.pdfApiList[g.groupID] || []).map(v => {
							return {
								...v,
								isApi: true,
								checked: false,
								label: v.apiName,
								id: `${g.groupID}-${v.apiID}`
							}
						})
					}
				})
			} else {
				state.tableList = []
			}
		}

		watch(() => [state.groupList, state.pdfApiList], ([groups, apis]) => {
			if (Object.keys(apis).length && groups.length) {
				handleInit()
			}
    })

    watch(() => props.visible, (val) => {
			state.show = val
			if (val){
				handleInit()
			}
    })

		return{
			hasSelectedNum,
			protocol: PROTOCOL,
			handleImportPdf,
			handleClose,
			handleCheckBox,
			...toRefs(state)
		}
	},
})
</script>

<style lang="scss" scoped>
// .importpdf-dialog{
// 	&:deep(th.el-table__cell){
// 		background-color: #f5f7fa;
// 		color: #333;
// 	}
// }
</style>