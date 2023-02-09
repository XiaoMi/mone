<template>
	<div>
		<div class="apitest-import-container">
      <JsonEditor :jsonEditorOptions="jsonEditorOptions" @json-change="handleJsonChange" @json-error="handleError"/>
    </div>
		<div style="margin-top: 10px">
			<el-button @click="handleSubmit" :disabled="isDisabled" type="primary">确定</el-button>
			<el-button @click="handleClose">取消</el-button>
		</div>
	</div>
</template>

<script>
import JsonEditor from '@/components/JsonEditor'
import { DATA_TYPE_KEY } from '@/views/constant'

export default {
  name: 'ApiTestImportDialog',
  components: {
    JsonEditor
  },
  data () {
    return {
      content: {},
      jsonEditorOptions: {
        mainMenuBar: false
      },
      isDisabled: false
    }
  },
  props: {
    type: {
      type: String,
      default: ''
    }
  },
  methods: {
    handleClose () {
      this.$emit('onCancel')
    },
    isType (val) {
      return Object.prototype.toString.call(val)
    },
    handleSubmit () {
      if (this.type === 'header' && (this.isType(this.content) !== '[object Object]')) {
        this.$message.error('数据格式有误，只支持Object')
        return
      } else if (this.type === 'query' && (this.isType(this.content) !== '[object String]' && this.isType(this.content) !== '[object Object]')) {
        this.$message.error('数据格式有误，只支持String或Object')
        return
      }
      this.handleClose()
      this.$emit('onOk', this.content)
    },
    handleJsonChange (val) {
      this.isDisabled = false
      this.content = val
    },
    handleError (arr) {
      this.isDisabled = true
    }
  }
}
</script>
<style>
.apitest-import-container{
	height: 200px;
}
.apitest-import-container .jsoneditor {
	border-color: #e6e6e6;
}
.apitest-import-container .ace_gutter {
	background: #f1f0f0;
}
</style>
