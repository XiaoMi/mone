<template>
  <div class="d-detail-headers">
		<h4><el-icon><Reading /></el-icon>{{$i18n.t('requestParameter')}}</h4>
		<div class="d-detail-headers-content">
			<div>
        <div class="request-btns header-copy">
				  <h5>Headers</h5>
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
				<h5 style="padding-bottom: 4px">Params</h5>
				<el-table
					:data="tableData"
					border
          v-if="radioType === 1"
					row-key="random"
					default-expand-all
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
				<div v-if="radioType === 2" class="request-params-json">
					<JsonEditor :content="apiInfo.baseInfo.apiRequestRaw ? JSON.parse(apiInfo.baseInfo.apiRequestRaw) : {}" :jsonEditorOptions="jsonEditorOptions"/>
				</div>
			</div>
		</div>
  </div>
</template>
<script>
import { DATA_TYPE } from '@/views/constant'
import JsonEditor from '@/components/JsonEditor'

const RADIO_TYPE = {
  FORM: 1,
  RAW: 2
}

export default {
  name: 'RequestParam',
  components: {
    JsonEditor
  },
  data () {
    return {
      tableData: [],
      radioType: RADIO_TYPE.FORM,
      jsonEditorOptions: {
        mainMenuBar: false,
        onEditable: () => false,
        canResize: true
      },
    }
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
      },
      immediate: true,
      deep: true
    },
  },
  computed: {
    dataType () {
      return DATA_TYPE
    }
  },
  methods: {
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
    }
  }
}
</script>
<style scoped>
.d-detail-headers >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 20px;
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
	padding: 10px 0 0;
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
