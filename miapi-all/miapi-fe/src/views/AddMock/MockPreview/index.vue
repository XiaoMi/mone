<template>
	<div class="mock-preview-container">
		<div class="mock-preview-table">
			<p class="mock-title">{{$i18n.t('requestParameter')}}</p>
			<el-table
				:data="tableData"
        v-if="mockRequestParamType === radio_type.FORM"
				border
				row-key="random"
				:tree-props="{children: 'children', hasChildren: 'hasChildren'}"
				style="width: 100%">
				<el-table-column
					show-overflow-tooltip
					prop="paramKey"
					width="180"
				>
          <template #header>
            <span class="common-table-title">{{$i18n.t('table.name')}}</span>
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
			</el-table>
      <div v-else class="mock-preview-json-edit">
				<JsonEditor :content="requestJson" :jsonEditorOptions="jsonEditorOptions" />
			</div>
		</div>
		<div class="mock-preview-json">
			<p class="mock-title">{{$i18n.t('returnParameter')}}</p>
			<div class="mock-preview-json-edit">
				<div class="copy-btn" @click="handleCustomCopy(JSON.stringify(defaultContent))"><el-icon :size="14"><CopyDocument /></el-icon> {{$i18n.t('copy')}}</div>
				<JsonEditor :content="defaultContent" :jsonEditorOptions="jsonEditorOptions" />
			</div>
		</div>
		<div class="mock-preview-btns">
			<el-button @click="handleCancel" size="mini">{{$i18n.t('btnText.close')}}</el-button>
		</div>
	</div>
</template>
<script>
import JsonEditor from '@/components/JsonEditor'
import { RADIO_TYPE } from '@/views/ApiList/constant'
import { DATA_TYPE_KEY } from '@/views/constant'
import customCopy from "@/common/customCopy"
export default {
  name: 'MockPreview',
  components: {
    JsonEditor
  },
  data () {
    return {
      tableData: [],
      jsonEditorOptions: {
        mainMenuBar: false,
        onEditable: () => false
      },
      defaultContent: {}
    }
  },
  computed: {
    radio_type () {
      return RADIO_TYPE
    }
  },
  props: {
    mockRequestParamType: {
      default: RADIO_TYPE.FORM
    },
    requestJson: {
      default: ''
    },
    content: {
      default: {}
    }
  },
  watch: {
    requestJson: {
      handler (val) {
        if (!val || (this.mockRequestParamType === RADIO_TYPE.JSON)) {
          return
        }
        let paramsJson = val
        try {
          paramsJson = JSON.parse(paramsJson)
        } catch (error) {}
        if (Array.isArray(paramsJson)) {
          this.tableData = paramsJson
        } else if (paramsJson && Object.keys(paramsJson).length) {
          let tableData = Object.keys(paramsJson).map((key, index) => {
            let obj = {
              paramType: '',
              paramKey: key,
              paramValue: paramsJson[key],
              random: new Date().getTime() + (index + 1) * Math.random() * 10,
              children: [],
              level: 1
            }
            if (Object.prototype.toString.call(paramsJson[key]) === '[object Object]') {
              obj.paramValue = ''
              obj.children = this.handleReserveValue(paramsJson[key], 1)
            }
            return obj
          })
          this.tableData = tableData
        }
      },
      immediate: true,
      deep: true
    },
    content: {
      handler (val) {
        if (Array.isArray(val)) {
          let obj = {}
          val.forEach(item => {
            obj[item.paramKey] = this.handleValue(item)
          })
          this.defaultContent = obj
        } else {
          if (typeof val === 'string') {
            this.defaultContent = this.$utils.jsonParse(val)
          } else {
            this.defaultContent = this.$utils.deepParse(val)
          }
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleCustomCopy(val){
      customCopy(val)
    },
    handleValue (item) {
      let target = {}
      switch (item.paramType) {
        case DATA_TYPE_KEY.array:
          target = item.childList.map(v => {
            return this.handleValue(v)
          })
          break
        case DATA_TYPE_KEY.json:
        case DATA_TYPE_KEY.object:
          item.childList.forEach(v => {
            target[v.paramKey] = this.handleValue(v)
          })
          break
        default:
          target = item.paramValue
          break
      }
      return target
    },
    handleReserveValue (obj, level) {
      let values = []
      level++
      Object.keys(obj).forEach((key, i) => {
        let t = new Date().getTime() + (i + 1) * Math.random() * 1000
        let o = {
          paramType: '',
          paramKey: key,
          paramValue: obj[key],
          random: t,
          children: [],
          level
        }
        if (Object.prototype.toString.call(obj[key]) === '[object Object]') {
          o.paramValue = ''
          o.children = this.handleReserveValue(obj[key], level)
        }
        values.push(o)
      })
      return values
    },
    handleCancel () {
      this.$emit('handleCancel')
    }
  }
}
</script>
<style>
.mock-preview-container {
	padding-top: 10px;
}
.mock-preview-container .mock-preview-table {
	margin-bottom: 20px;
}
.mock-preview-container .mock-preview-table .el-table th{
	background: rgba(250, 250, 250, 1);
	height: 50px;
}
.mock-preview-container .mock-preview-table .el-table td {
	height: 50px;
}
.mock-preview-container .mock-preview-btns {
	padding-top: 20px;
	text-align: right;
}
.mock-preview-json-edit{
	position: relative;
	height: 200px;
}
.mock-preview-json-edit .copy-btn {
	position: absolute;
	right: 12px;
	top: 8px;
	z-index: 1;
	color: #108EE9;
	font-size: 14px;
	cursor: pointer;
	user-select: none;
  display: flex;
  align-items: center;
}
.mock-preview-json-edit .copy-btn i {
  margin-right: 2px;
}
.mock-preview-json-edit .jsoneditor {
	border-color: #e6e6e6;
}
.mock-preview-json-edit .ace_gutter {
	background: #f1f0f0;
}
.mock-preview-container .mock-title{
	border: 1px solid #EBEEF5;
	border-bottom: none;
	padding: 0 16px;
	height: 50px;
	line-height: 50px;
	font-size: 16px;
	color: rgba(0, 0, 0, 0.84);
}
</style>
