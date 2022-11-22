<template>
	<div class="req-param">
    <div class="radio-types">
			<el-radio-group v-model="type">
				<el-radio :label="radioTypes.FORM_DATA">Form-data</el-radio>
				<el-radio :label="radioTypes.JSON">Json</el-radio>
				<el-radio :label="radioTypes.RAW">Raw</el-radio>
			</el-radio-group>
      <el-button v-show="type === radioTypes.JSON" @click="handleOpenVisible" type="primary" size="small">{{$i18n.t('importJson')}}</el-button>
		</div>
		<el-table
			:data="tableData"
			border
			v-if="type === radioTypes.FORM_DATA"
			header-cell-class-name="table-header-cell"
			default-expand-all
			class="base-table"
			style="width: 100%">
			<el-table-column
				prop="paramKey"
				class-name="table-paramKey"
				width="220">
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.name')}}</span>
        </template>
				<template #default="scope">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnterName')" v-model="scope.row.paramKey" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="paramType"
				width="140px"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterType')}}</span>
        </template>
				<template #default="scope">
					<el-select
						v-model="scope.row.paramType"
						:placeholder="$i18n.t('placeholder.pleaseEnterType')">
						<el-option
							v-for="(name, value) in dataType"
							:key="value"
              :disabled="disabledOption(value)"
							:label="name"
							:value="value">
						</el-option>
					</el-select>
				</template>
			</el-table-column>
			<el-table-column
				prop="paramNotNull"
				:width="isEN ? '86px' : '54px'"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.required')}}</span>
        </template>
				<template #default="scope">
          <div style="width: 100%; text-align: center">
					  <el-checkbox v-model="scope.row.paramNotNull"></el-checkbox>
          </div>
				</template>
			</el-table-column>
			<el-table-column
				prop="paramValue"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.parameterValue')}}</span>
        </template>
				<template #default="scope">
					<el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="scope.row.paramValue" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				prop="paramNote"
				>
        <template #header>
          <span class="common-table-title">{{$i18n.t('table.instruction')}}</span>
        </template>
				<template #default="scope">
					<el-input :rows="1" type="textarea" :placeholder="$i18n.t('placeholder.enterDesc')" v-model="scope.row.paramNote" autocomplete="off"></el-input>
				</template>
			</el-table-column>
			<el-table-column
				:width="isEN ? '86px' : '54px'"
				align="center">
        <template #header>
          <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
        </template>
				<template #default="scope">
					<span @click="handledelete(scope)" class="icon-modify">
						{{$i18n.t('btnText.delete')}}
					</span>
				</template>
			</el-table-column>
		</el-table>
    <div v-else-if="type === radioTypes.JSON">
      <FormJsonDubbo v-if="isGatewayDubbo" ref="formJson" :formJsonData="formJsonData" @changeFormJsonData="changeFormJsonData"/>
      <FormJson v-else ref="formJson" :apiProtocol="protocolType.Gateway" :routingType="Number(gatewayParam.routeType)" :formJsonData="formJsonData" @changeFormJsonData="changeFormJsonData"/>
    </div>
    <div v-else>
      <div class="edit-request-json">
        <JsonEditor @json-change="handleInputJson" @json-error="handleError" :content="content" :jsonEditorOptions="jsonEditorOptions"/>
      </div>
		</div>
    <el-dialog
      :destroy-on-close="true"
      :center="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :title="$i18n.t('importJson')"
      v-model="importDialogVisible"
			width="540px"
      append-to-body
    >
      <ImportDialog @handleOk="handleImportOk" @handleCancel="handleCancel"/>
			<!-- <div slot="title">{{importDialog.title}}</div> -->
    </el-dialog>
	</div>
</template>
<script>

import { DATA_TYPE, DATA_TYPE_KEY, API_REQUEST_PARAM_TYPE, PROTOCOL_TYPE } from '@/views/constant'
import JsonEditor from '@/components/JsonEditor'
import FormJson from '@/views/ApiDetail/HttpDetail/RequestParam/EditRequestParam/ReqParam/formJson.vue'
import FormJsonDubbo from './formJson.vue'
import ImportDialog from '@/views/ApiDetail/HttpDetail/ReturnParam/EditReturnParam/ImportDialog'

export default {
  name: 'ReqParam',
  components: {
    JsonEditor,
    FormJson,
    FormJsonDubbo,
    ImportDialog
  },
  data () {
    return {
      isEN: false,
      type: API_REQUEST_PARAM_TYPE.FORM_DATA,
      tableData: [{
        paramNotNull: false,
        paramType: '',
        paramKey: '',
        paramValue: '',
        paramNote: ''
      }],
      jsonEditorOptions: {
        mainMenuBar: false
      },
      content: {},
      formJsonData: [],
      importDialogVisible: false
    }
  },
  computed: {
    dataType () {
      return DATA_TYPE
    },
    gatewayParam () {
      return this.$store.getters.gatewayParam
    },
    radioTypes () {
      return API_REQUEST_PARAM_TYPE
    },
    protocolType () {
      return PROTOCOL_TYPE
    },
    isGatewayDubbo () {
      return Number(this.gatewayParam.routeType) === 1 || Number(this.gatewayParam.routeType) === 4
    }
  },
  watch: {
    tableData: {
      handler: function (val, oldVal) {
        if (this.tableData.every(item => !!item.paramType && !!item.paramKey)) {
          this.tableData.push({
            paramNotNull: false,
            paramType: '',
            paramKey: '',
            paramValue: '',
            paramNote: ''
          })
        }

        val = val.map(item => {
          return {
            paramNotNull: !!item.paramNotNull,
            paramType: item.paramType,
            paramKey: item.paramKey,
            paramValue: item.paramValue,
            paramNote: item.paramNote
          }
        })
        this.$store.dispatch('apilist.add/changeGatewayParam', {
          apiRequestParam: val
        })
      },
      deep: true
    },
    formJsonData: {
      handler (newVal) {
        this.$store.dispatch('apilist.add/changeGatewayParam', {
          apiRequestParam: newVal
        })
      },
      deep: true
    },
    type (val, old) {
      if (val !== old) {
        this.$store.dispatch('apilist.add/changeGatewayParam', {
          apiRequestParamType: val
        })
      }
      switch (val) {
        case API_REQUEST_PARAM_TYPE.FORM_DATA:
          this.$store.dispatch('apilist.add/changeGatewayParam', {
            apiRequestParam: this.tableData
          })
          break
        case API_REQUEST_PARAM_TYPE.JSON:
          this.$store.dispatch('apilist.add/changeGatewayParam', {
            apiRequestParam: this.formJsonData
          })
          break
        case API_REQUEST_PARAM_TYPE.RAW:
          let content = this.gatewayParam.apiRequestRaw || {}
          try {
            content = JSON.parse(content)
          } catch (error) {}
          this.content = content
          break
        default:
          break
      }
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
    if (
      this.gatewayParam.apiRequestParamType === API_REQUEST_PARAM_TYPE.JSON ||
      (
        this.gatewayParam.apiRequestParam &&
        Array.isArray(this.gatewayParam.apiRequestParam) &&
        this.gatewayParam.apiRequestParam.length &&
        this.gatewayParam.apiRequestParam.some(v => Array.isArray(v.childList))
      )
    ) {
      this.formJsonData = this.gatewayParam.apiRequestParam
    } else if (
      this.gatewayParam.apiRequestParam &&
      Array.isArray(this.gatewayParam.apiRequestParam) &&
      this.gatewayParam.apiRequestParam.length &&
      this.gatewayParam.apiRequestParam.some(v => Array.isArray(v.paramValue))
    ) {
      let tableData = this.gatewayParam.apiRequestParam.map((v) => {
        return {
          paramNotNull: v.paramNotNull,
          paramType: v.paramType,
          paramKey: v.paramKey,
          paramNote: v.paramNote,
          default: 0,
          paramValue: Array.isArray(v.paramValue) ? '' : v.paramValue,
          childList: this.handleChildList(v.paramValue)
        }
      })
      this.formJsonData = tableData
    } else if (Array.isArray(this.gatewayParam.apiRequestParam)) {
      let tableData = this.gatewayParam.apiRequestParam.map((v) => {
        if (v.paramType === DATA_TYPE_KEY.json || v.paramType === DATA_TYPE_KEY.object || v.paramType === DATA_TYPE_KEY.array) {
          v.paramValue = JSON.stringify(v.paramValue)
        }
        return {
          paramNotNull: !!v.paramNotNull,
          paramType: v.paramType,
          paramKey: v.paramKey,
          paramValue: v.paramValue,
          paramNote: v.paramNote
        }
      })
      if (tableData.every(item => !!item.paramType && !!item.paramKey)) {
        tableData.push({
          paramNotNull: false,
          paramType: '',
          paramKey: '',
          paramValue: '',
          paramNote: ''
        })
      }
      this.tableData = tableData
    }
    let content = this.gatewayParam.apiRequestRaw || {}
    try {
      content = JSON.parse(content)
    } catch (error) {}
    this.content = content
    this.type = this.gatewayParam.apiRequestParamType || API_REQUEST_PARAM_TYPE.FORM_DATA
  },
  methods: {
    handleOpenVisible () {
      this.importDialogVisible = true
    },
    handleCancel () {
      this.importDialogVisible = false
    },
    handleImportOk (data) {
      this.formJsonData = [...data]
      this.$refs.formJson.init(data)
    },
    handleChildList (arr) {
      if (!Array.isArray(arr)) {
        return []
      }
      let array = []
      arr.forEach(v => {
        let o = {
          paramNotNull: v.paramNotNull,
          paramType: v.paramType,
          paramKey: v.paramKey,
          paramNote: v.paramNote,
          default: 0,
          paramValue: Array.isArray(v.paramValue) ? '' : v.paramValue,
          childList: this.handleChildList(v.paramValue)
        }
        array.push(o)
      })
      return array
    },
    handledelete (scope) {
      this.tableData = this.tableData.filter(v => !(v.paramKey === scope.row.paramKey && v.paramType === scope.row.paramType && v.paramValue === scope.row.paramValue))
    },
    handleInputJson (content) {
      this.$store.dispatch('apilist.add/changeIsRawBool', false)
      this.$store.dispatch('apilist.add/changeGatewayParam', {
        apiRequestRaw: content
      })
    },
    handleError () {
      this.$store.dispatch('apilist.add/changeIsRawBool', true)
      this.$store.dispatch('apilist.add/changeGatewayParam', {
        apiRequestRaw: {}
      })
    },
    disabledOption (type) {
      return type === DATA_TYPE_KEY.json || type === DATA_TYPE_KEY.object || type === DATA_TYPE_KEY.array
    },
    changeFormJsonData (val) {
      this.formJsonData = val
    }
  }
}
</script>
<style scoped>
.req-param .radio-types {
  height: 28px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.req-param .base-table >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border-right: 1px solid rgba(232, 232, 232, 1);
	height: 50px;
}
.req-param .icon-modify {
	cursor: pointer;
	color: #1890FF;
	font-size: 14px;
}
.req-param .active_btn{
	background: #646669;
	border-color: #1890FF;
	color: #FFF;
}
.req-param .base-table >>> .cell {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.req-param .edit-request-json {
  height: 200px;
}
.req-param .edit-request-json >>> .jsoneditor{
  border-color: #e6e6e6;
}
.req-param .edit-request-json >>> .ace_gutter {
	background: #f1f0f0;
}
</style>
