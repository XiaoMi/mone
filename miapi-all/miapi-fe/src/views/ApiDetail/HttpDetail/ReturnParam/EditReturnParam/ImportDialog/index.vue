<template>
	<div>
		<div class="return-json-container">
      <JsonEditor :jsonEditorOptions="jsonEditorOptions" @json-change="handleJsonChange" @json-error="handleError"/>
    </div>
		<div style="margin-top: 10px">
			<!-- <el-button :disabled="isDisabled" size="small" type="primary" @click="handleChangeVisible">插 入</el-button> -->
			<el-button @click="handleSubmit" :disabled="isDisabled" type="primary">{{$i18n.t('btnText.ok')}}</el-button>
			<el-button @click="handleChangeVisible">{{$i18n.t('btnText.cancel')}}</el-button>
		</div>
	</div>
</template>

<script>
import JsonEditor from '@/components/JsonEditor'
import { DATA_TYPE_KEY } from '@/views/constant'

export default {
  name: 'ImportDialog',
  components: {
    JsonEditor
  },
  data () {
    return {
      content: [],
      jsonEditorOptions: {
        mainMenuBar: false
      },
      isDisabled: false
    }
  },
  props: {
  },
  methods: {
    handleChangeVisible () {
      this.$emit('handleCancel')
    },
    isType (val) {
      return Object.prototype.toString.call(val)
    },
    handleObjectValue (val, k = '') {
      let obj = {
        paramNotNull: false,
        paramType: '',
        paramKey: k,
        paramValue: '',
        paramNote: '',
        default: 0,
        childList: []
      }
      switch (this.isType(val)) {
        case '[object Null]':
        case '[object Undefined]':
        case '[object Number]':
          obj.paramType = DATA_TYPE_KEY.number
          obj.paramValue = val || 0
          break
        case '[object Boolean]':
          obj.paramType = DATA_TYPE_KEY.boolean
          obj.paramValue = val.toString()
          break
        case '[object Object]':
          obj.paramType = DATA_TYPE_KEY.object
          obj.paramValue = ''
          obj.childList = Object.keys(val).map(key => this.handleObjectValue(val[key], key))
          break
        case '[object Array]':
          obj.paramType = DATA_TYPE_KEY.array
          obj.paramValue = ''
          obj.childList = val.map(v => this.handleObjectValue(v))
          break
        default:
          // string
          if (/^[0-9]*$/g.test(val)) {
            obj.paramType = DATA_TYPE_KEY.number
            obj.paramValue = Number(val)
          } else {
            obj.paramType = DATA_TYPE_KEY.string
            obj.paramValue = val
          }
          break
      }
      return obj
    },
    handleSubmit () {
      let val = []
      let paramType = DATA_TYPE_KEY.object
      switch (this.isType(this.content)) {
        case '[object Object]':
          val = Object.keys(this.content).map(key => this.handleObjectValue(this.content[key], key))
          break
        case '[object Array]':
          paramType = DATA_TYPE_KEY.array
          val = this.content.map(v => this.handleObjectValue(v))
          break
        default:
          break
      }
      if (!val.length) {
        this.$message.error('数据格式有误')
        return
      }
      let data = [{
        paramNotNull: false,
        paramType,
        paramKey: 'root',
        paramValue: '',
        paramNote: '',
        default: 0,
        childList: val
      }]
      this.$emit('handleOk', data)
      this.handleChangeVisible()
    },
    handleJsonChange (val) {
      this.isDisabled = false
      this.content = val
    },
    handleError () {
      this.isDisabled = true
    }
  },
  computed: {

  }
}
</script>
<style>
.return-json-container{
	height: 200px;
}
.return-json-container .jsoneditor {
	border-color: #e6e6e6;
}
.return-json-container .ace_gutter {
	background: #f1f0f0;
}
</style>
