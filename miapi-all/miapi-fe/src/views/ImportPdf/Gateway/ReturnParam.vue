<template>
  <div class="d-detail-returns">
		<h4><el-icon><Reading /></el-icon>{{$i18n.t('returnParameter')}}</h4>
		<div class="d-detail-returns-content">
			<div>
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
						prop="paramType"
						show-overflow-tooltip
            width="90"
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
            width="55"
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
            width="140"
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
					<JsonEditor :content="apiInfo.apiResponseRaw ? JSON.parse(apiInfo.apiResponseRaw) : {}" :jsonEditorOptions="jsonEditorOptions"/>
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
  name: 'ReturnParam',
  components: {
    JsonEditor
  },
  data () {
    return {
      radioType: RADIO_TYPE.FORM,
      tableData: [],
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
          resultInfo: [],
          gatewayApiBaseInfo: {}
        }
      }
    }
  },
  computed: {
    dataType () {
      return DATA_TYPE
    },
    radio_type () {
      return RADIO_TYPE
    }
  },
  watch: {
    'apiInfo': {
      handler (val) {
        if (val.gatewayApiBaseInfo && val.gatewayApiBaseInfo.url) {
          this.radioType = val.apiResponseParamType
        }
        if (val.resultInfo && val.resultInfo.length) {
          this.tableData = val.resultInfo.map((item, index) => {
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
    },
  }
}
</script>
<style scoped>
.d-detail-returns >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 20px;
}
.d-detail-returns .d-detail-returns-content {
	padding: 10px 20px 20px;
}
.d-detail-returns .d-detail-returns-content .return-btns {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.d-detail-returns .d-detail-returns-content .return-btns .copy-icon {
  cursor: pointer;
}
.d-detail-returns .d-detail-returns-content .radio-wrap{
  margin-bottom: 8px;
}
.d-detail-returns .request-params-json{
	height: 200px;
}
.d-detail-returns .request-params-json >>> .jsoneditor {
	border-color: #e6e6e6;
}
.d-detail-returns .request-params-json >>> .ace_gutter {
	background: #f1f0f0;
}
</style>
