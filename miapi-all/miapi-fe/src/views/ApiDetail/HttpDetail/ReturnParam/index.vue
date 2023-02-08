<template>
  <div class="d-detail-returns">
		<h4>{{$i18n.t('returnParameter')}}</h4>
		<div class="d-detail-returns-content">
			<EditReturnParam v-if="isEditDetail"/>
			<div v-else>
        <div class="return-btns">
          <el-radio-group class="radio-wrap" v-model="radioType">
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
					v-if="radioType === radio_type.FORM"
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
				<div v-else class="request-params-json">
					<JsonEditor :content="apiInfo.baseInfo.apiResponseRaw ? JSON.parse(apiInfo.baseInfo.apiResponseRaw) : {}" :jsonEditorOptions="jsonEditorOptions"/>
				</div>
      </div>
		</div>
  </div>
</template>
<script>
import { DATA_TYPE } from '@/views/constant'
import EditReturnParam from './EditReturnParam'
import JsonEditor from '@/components/JsonEditor'
import { mapGetters } from 'vuex'
import { copyParam } from "@/store/utils"
import debounce from '@/common/debounce'
import customCopy from "@/common/customCopy"

const RADIO_TYPE = {
  FORM: 1,
  RAW: 2
}

export default {
  name: 'ReturnParam',
  components: {
    EditReturnParam,
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
      radioType: RADIO_TYPE.FORM,
      tableData: [],
      jsonEditorOptions: {
        mainMenuBar: false,
        onEditable: () => false
      },
      copyData: "{}",
      showTip
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
  watch: {
    'apiInfo': {
      handler (val) {
        if (val.baseInfo && val.baseInfo.apiID) {
          this.radioType = val.baseInfo.apiResponseParamType
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
      if (this.radioType === RADIO_TYPE.FORM) {
        this.copyData = JSON.stringify(copyParam(this.apiInfo.resultInfo) || [])
      } else {
        this.copyData = this.apiInfo.baseInfo.apiResponseRaw
      }
    },
    handleCustomCopy(val){
      customCopy(val)
    },
  }
}
</script>
<style scoped>
.d-detail-returns >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 50px;
}
.d-detail-returns .d-detail-returns-content {
	padding: 20px;
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
