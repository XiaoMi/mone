<template>
	<div v-loading="loading" class="editor-doc-container">
		<wangEditor v-if="contentType===0" @onClose="$emit('onClose')" :title="title" :defaultValue="defaultValue" @onTitle="handleChangeTitle" @input="handleInputRaw"/>
    <template v-else>
      <el-input class="title-input" v-model="title" placeholder="title..."/>
      <div  class="markdown-container">
        <markdown-editor :content="content" @changeContent="handleChangeContent" height="100%" />
      </div>
    </template>
		<div class="btns">
			<el-button @click="$emit('onClose')" round>{{$i18n.t('btnText.close')}}</el-button><br/>
		  <el-button @click="handleChangeType" round>{{contentType === 0 ? "MarkDown" : "Document"}}</el-button><br/>
			<el-button @click="handleAdd" type="primary" round>{{$i18n.t('btnText.save')}}</el-button>
		</div>
  </div>
</template>
<script>
import wangEditor from "./editor.vue"
import MarkdownEditor from '@/components/MarkdownEditor'
import { addDocument, editDocument, getDocumentDetail } from '@/api/projectdoc'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
export default {
  name: "EditDialog",
  components: {
    wangEditor,
    MarkdownEditor
  },
  props: {
    documentID: {
      default: ''
    }
  },
  data () {
    return {
      contentType: 0,
      contentRaw: '',
      defaultValue: '',
      content: '',
      title: '',
      loading: false
    }
  },
  watch: {
    documentID: {
      handler (val) {
        if (val) {
          this.loading = true
          getDocumentDetail({ documentID: val }).then((data) => {
            if (data.message === AJAX_SUCCESS_MESSAGE) {
              this.contentType = data.data.contentType
              this.defaultValue = data.data.contentRaw
              this.content = data.data.content
              this.title = data.data.title
            }
          }).catch(e => {}).finally(() => {
            this.loading = false
          })
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleChangeTitle (val) {
      this.title = val
    },
    handleInputRaw (data) {
      this.contentRaw = data
    },
    handleChangeContent (val) {
      this.content = val
    },
    handleAdd () {
      if (this.documentID) {
        editDocument({
          projectID: this.$utils.getQuery('projectID'),
          documentID: this.documentID,
          contentType: this.contentType,
          contentRaw: this.contentRaw,
          content: this.content,
          title: this.title
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            this.$message.success(this.$i18n.t('editSuccessfully'))
            this.$emit('onClose')
          }
        }).catch(e => {})
        return
      }
      addDocument({
        contentRaw: this.contentRaw, // 富文本内容
        content: this.content, // Markdown内容
        title: this.title,
        projectID: this.$utils.getQuery('projectID'),
        contentType: this.contentType
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message.success(this.$i18n.t('savedSuccessfully'))
          this.$emit('onClose')
        }
      }).catch(e => {})
    },
    handleChangeType () {
      this.contentType = this.contentType === 0 ? 1 : 0
    }
  }
}
</script>

<style scoped>
.editor-doc-container {
  position: relative;
}
.editor-doc-container .markdown-container {
  background: #fff;
  height: calc(100vh - 80px);
  margin: 0 auto 0;
  width: 84%;
}
.editor-doc-container .markdown-container >>> ol li {
	list-style: auto
}
.editor-doc-container .markdown-container >>> ul li {
	list-style: initial
}
.editor-doc-container .title-input {
  margin: 20px auto -2px;
  width: 84%;
  display: block;
  position: relative;
  z-index: 1;
}
.editor-doc-container .title-input >>> .el-input__wrapper {
  border-bottom: none;
  border-radius: 0;
  width: 100%;
  padding-bottom: 0;
}
.editor-doc-container .title-input >>> .el-input__wrapper:hover,
.editor-doc-container .title-input >>> .el-input__wrapper:focus{
  border-color: #DCDFE6;
}
.editor-doc-container .toggle-type {
  position: fixed;
  left: 30px;
  bottom: 20px;
}
.editor-doc-container .btns {
	position: fixed;
	right: 30px;
	bottom: 20px;
}
.editor-doc-container .btns .el-button {
  width: 87px;
  margin-top: 10px;
}
</style>
