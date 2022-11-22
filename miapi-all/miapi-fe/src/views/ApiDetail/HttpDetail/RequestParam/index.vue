<template>
  <div class="d-detail-headers">
		<h4>{{$i18n.t('requestParameter')}}</h4>
		<div class="d-detail-headers-content">
			<EditRequestParam v-if="isEditDetail"/>
			<div v-else>
        <div class="request-btns header-copy">
				  <h5>Headers</h5>
					<el-tooltip effect="dark" content="Copy Json" placement="top">
            <el-icon :size="14" ref="copyIcon" @click="handleCustomCopy(copyHeader)" class="copy-icon"><DocumentCopy /></el-icon>
					</el-tooltip>
				</div>
				<el-table
					:data="apiInfo.headerInfo"
					border
					header-cell-class-name="table-header-cell"
					style="width: 100%">
					<el-table-column
						fixed
						show-overflow-tooltip
						prop="headerName"
					>
            <template #header>
              <span class="common-table-title">{{$i18n.t('label')}}</span>
            </template>
					</el-table-column>
					<el-table-column
						prop="headerValue"
						show-overflow-tooltip
					>
            <template #header>
              <span class="common-table-title">{{$i18n.t('content')}}</span>
            </template>
					</el-table-column>
				</el-table>
				<h5 style="padding-bottom: 0px">Params</h5>
				<div class="request-btns">
					<el-radio-group v-model="radioType">
						<el-radio :label="radio_type.FORM">Form</el-radio>
						<el-radio :label="radio_type.RAW">Raw</el-radio>
					</el-radio-group>
					<el-tooltip v-model="showTip" effect="dark" content="Copy Json" placement="top">
            <el-icon :size="14" ref="copyIcon" @click="handleCustomCopy(copyData)" class="copy-icon"><DocumentCopy /></el-icon>
					</el-tooltip>
				</div>
				<el-table
					:data="tableData"
					border
					row-key="random"
					default-expand-all
					v-if="radioType === radio_type.FORM"
					:tree-props="{children: 'childList', hasChildren: 'hasChildren'}"
					style="width: 100%">
					<el-table-column
						fixed
						show-overflow-tooltip
						prop="paramKey"
					>
            <template #header>
              <span class="common-table-title">{{$i18n.t('table.name')}}</span>
            </template>
					<template #default="scope">
						<span>{{scope.row.paramKey || '--'}}</span>
					</template>
					</el-table-column>
					<el-table-column
						prop="paramName"
						show-overflow-tooltip
					>
            <template #header>
              <span class="common-table-title">{{$i18n.t('table.parameterName')}}</span>
            </template>
					</el-table-column>
					<el-table-column
						prop="paramType"
						show-overflow-tooltip
					>
            <template #header>
              <span class="common-table-title">{{$i18n.t('table.parameterType')}}</span>
            </template>
					<template #default="scope">
							<span>{{dataType[scope.row.paramType]}}</span>
						</template>
					</el-table-column>
					<el-table-column
						prop="paramNotNull"
						show-overflow-tooltip
					>
            <template #header>
              <span class="common-table-title">{{$i18n.t('table.required')}}</span>
            </template>
					<template #default="scope">
						<span>{{scope.row.paramNotNull ? $i18n.t('yes'):$i18n.t('no')}}</span>
					</template>
					</el-table-column>
					<el-table-column
						prop="paramValue"
						show-overflow-tooltip
					>
            <template #header>
              <span class="common-table-title">{{$i18n.t('table.parameterValue')}}</span>
            </template>
					</el-table-column>
					<el-table-column
						prop="paramNote"
					>
            <template #header>
              <span class="common-table-title">{{$i18n.t('table.instruction')}}</span>
            </template>
					</el-table-column>
				</el-table>
				<div v-else class="request-params-json">
					<JsonEditor :content="apiInfo.baseInfo.apiRequestRaw ? JSON.parse(apiInfo.baseInfo.apiRequestRaw) : {}" :jsonEditorOptions="jsonEditorOptions"/>
				</div>
			</div>
		</div>
  </div>
</template>
<script>
import EditRequestParam from './EditRequestParam'
import { DATA_TYPE } from '@/views/constant'
import { mapGetters } from 'vuex'
import JsonEditor from '@/components/JsonEditor'
import { copyParam } from "@/store/utils"
import customCopy from "@/common/customCopy"
import debounce from '@/common/debounce'

const RADIO_TYPE = {
  FORM: 1,
  RAW: 2
}

export default {
  name: 'RequestParam',
  components: {
    EditRequestParam,
    JsonEditor
  },
  data () {
    let showTip = false
    if (this.$utils.diffDate("2022-3-31", "2022-4-8") && !window.sessionStorage.getItem("showTip")) {
      window.sessionStorage.setItem("showTip", 1)
      showTip = true
    } else if (!this.$utils.diffDate("2022-3-31", "2022-4-8")) {
      window.sessionStorage.removeItem("showTip")
    }
    return {
      tableData: [],
      radioType: RADIO_TYPE.FORM,
      jsonEditorOptions: {
        mainMenuBar: false,
        onEditable: () => false
      },
      copyData: '{}',
      copyHeader: '{}',
      showTip
    }
  },
  watch: {
    'apiInfo': {
      handler (val) {
        if (val.baseInfo && val.baseInfo.apiID) {
          this.radioType = val.baseInfo.apiRequestParamType || RADIO_TYPE.FORM
        }
        if (val.requestInfo && val.requestInfo.length) {
          this.tableData = val.requestInfo.map((item, index) => {
            let obj = {
              childList: [],
              ...item,
              random: new Date().getTime() + (index + 1) * Math.random() * 10
            }
            let paramValue = item.paramValue
            try {
              paramValue = JSON.parse(item.paramValue)
            } catch (error) {}
            if (Array.isArray(paramValue) && paramValue.length) {
              obj.paramValue = ''
              obj.childList = this.handleReserveValue(paramValue)
            } else if (Array.isArray(item.childList) && item.childList.length) {
              obj.paramValue = ''
              obj.childList = this.handleReserveValue(item.childList)
            }
            return obj
          })
        } else {
          this.tableData = []
        }
        this.handleCopy()
      },
      immediate: true,
      deep: true
    },
    radioType: {
      handler () {
        this.handleCopy()
      }
    }
  },
  computed: {
    ...mapGetters([
      'isEditDetail'
    ]),
    apiInfo () {
      return this.$store.getters.apiDetail.baseInfo ? this.$store.getters.apiDetail : {
        baseInfo: {},
        headerInfo: [],
        mockInfo: {},
        requestInfo: [],
        resultInfo: [],
        testHistory: []
      }
    },
    dataType () {
      return DATA_TYPE
    },
    radio_type () {
      return RADIO_TYPE
    }
  },
  mounted () {
    if (this.showTip) {
      document.querySelector('#api-detail-container').addEventListener("scroll", this.handleScroll)
    }
  },
  methods: {
    handleScroll: debounce(function () {
      if (this.$utils.isInToView(this.$refs.copyIcon, document.querySelector('#api-detail-container'))) {
        document.querySelector('#api-detail-container').removeEventListener("scroll", this.handleScroll)
        setTimeout(() => {
          this.showTip = false
        }, 1200)
      }
    }, 300),
    handleReserveValue (arr) {
      let values = []
      for (let i = 0; i < arr.length; i++) {
        let t = new Date().getTime() + (i + 1) * Math.random() * 1000
        let obj = {
          ...arr[i],
          random: t,
          childList: []
        }

        let paramValue = arr[i].paramValue
        try {
          paramValue = JSON.parse(arr[i].paramValue)
        } catch (error) {}
        if (Array.isArray(paramValue) && paramValue.length) {
          obj.paramValue = ''
          obj.childList = this.handleReserveValue(paramValue)
        } else if (Array.isArray(arr[i].childList) && arr[i].childList.length) {
          obj.paramValue = ''
          obj.childList = this.handleReserveValue(arr[i].childList)
        }
        values.push(obj)
      }
      return values
    },
    handleCopy () {
      if (this.apiInfo.headerInfo && this.apiInfo.headerInfo.length) {
        let o = {}
        this.apiInfo.headerInfo.forEach(v => {
          o[v.headerName] = v.headerValue
        })
        this.copyHeader = JSON.stringify(o)
      } else {
        this.copyHeader = JSON.stringify({})
      }
      if (this.radioType === RADIO_TYPE.FORM) {
        this.copyData = JSON.stringify(copyParam(this.apiInfo.requestInfo) || [])
      } else {
        this.copyData = this.apiInfo.baseInfo.apiRequestRaw
      }
    },
    handleCustomCopy(val){
      customCopy(val)
    },
  }
}
</script>
<style scoped>
.d-detail-headers >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 50px;
}
.d-detail-headers .d-detail-headers-content {
	padding: 0 20px 20px;
}
.d-detail-headers .d-detail-headers-content .request-btns {
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.d-detail-headers .d-detail-headers-content .request-btns .copy-icon {
	cursor: pointer;
}
.d-detail-headers .d-detail-headers-content .request-btns.header-copy {
  margin-bottom: 8px;
}
.d-detail-headers .d-detail-headers-content .request-btns.header-copy .copy-icon{
  align-self: flex-end;
}
.d-detail-headers .d-detail-headers-content h5 {
	padding: 20px 0 0;
}
.d-detail-headers .d-detail-headers-content .sub-title {
	font-size:12px;
	margin: 8px 0 4px;
	padding-left: 8px;
	position: relative;
}
.d-detail-headers .d-detail-headers-content .sub-title::before{
	position: absolute;
	content: "";
	left: 0px;
	top: 50%;
	transform: translateY(-50%);
	width: 2px;
	height: 8px;
	background: #1890FF;
	border-radius: 3px;
}
.d-detail-headers .request-params-json{
	height: 200px;
}
.d-detail-headers .request-params-json >>> .jsoneditor {
	border-color: #e6e6e6;
}
.d-detail-headers .request-params-json >>> .ace_gutter {
	background: #f1f0f0;
}
.d-detail-headers .d-detail-headers-content .el-radio-group {
	margin: 8px 0;
}
</style>
