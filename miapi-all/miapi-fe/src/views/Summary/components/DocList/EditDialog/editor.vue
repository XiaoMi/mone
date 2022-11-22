<template>
	<div class="wangeditor-container">
    <div class="custom-toolbar">
			<Toolbar
				:editor="editor"
				:defaultConfig="toolbarConfig"
				:mode="mode"
			/>
		</div>
    <div class="header-list">
      <p class="content-title">{{pageTitle}}</p>
      <el-upload
        style="position: absolute; left: -999px"
        ref="upload"
        action="/dev/OpenApi/uploadFile"
        :show-file-list="false"
        :before-upload="handleBeforeUpload"
        :on-success="uploadSuccess"
        :on-error="uploadError"
      >
      </el-upload>
      <ul v-if="headerList.length">
        <li
          v-for="item in headerList"
          :key="item.id"
          :class="item.type"
          :title="item.content"
          >
          <a @click.stop="handleAnchor(item.id)">{{(item.content || "").trim()}}</a>
        </li>
      </ul>
      <p v-else class="empty">{{$i18n.t('noDirectory')}}</p>
    </div>
		<div class="content">
			<div class="title-container">
        <input v-model="pageTitle" placeholder="title...">
      </div>
      <div class="editor-text-area">
        <Editor
          v-model="html"
          :defaultConfig="editorConfig"
          :mode="mode"
          @onCreated="onCreated"
        />
      </div>
		</div>
	</div>
</template>
<script>
import '@wangeditor/editor/dist/css/style.css'
import { i18nChangeLanguage, SlateNode, Boot } from '@wangeditor/editor'
import attachmentModule from '@wangeditor/plugin-upload-attachment'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import { uploadImg } from '@/api/upload'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { FONT_SIZE, FONT_NAMES, COLORS } from "./editorConf"
import debounce from "@/common/debounce"
import { Loading } from "@element-plus/icons-vue"
Boot.registerModule(attachmentModule)
let msgRef

export default {
  name: "wangEditor",
  components: { Editor, Toolbar },
  data () {
    let that = this
    return {
      editor: null,
      headerList: [],
      toolbarConfig: {
        excludeKeys: ['fullScreen', 'emotion', 'uploadVideo']
      },
      mode: "default", // simple default
      pageTitle: "",
      html: '',
      editorConfig: {
        placeholder: 'Type here...',
        scroll: false, // 禁止编辑器滚动
        MENU_CONF: {
          uploadImage: {
            maxFileSize: 10 * 1024 * 1024,
            // 最多可上传几个文件，默认为 100
            maxNumberOfFiles: 2,
            customUpload (file, insertFn) {
              let param = new FormData()
              param.append('file', file, file.name)
              uploadImg(param).then((data) => {
                if (data.message === AJAX_SUCCESS_MESSAGE) {
                  insertFn(data.data, file.name, data.data)
                } else {
                  this.$message.error(data.message)
                }
              }).catch(e => {})
            }
          },
          uploadAttachment: {
            customBrowseAndUpload (insertFn) {
              that.$refs.upload.$refs['upload-inner'].handleClick()
              // 上传之后用 insertFn(fileName, link) 插入到编辑器
            }
          },
          fontFamily: {
            fontFamilyList: FONT_NAMES
          },
          fontSize: {
            fontSizeList: FONT_SIZE
          },
          color: {
            colors: COLORS
          }
        }
      }
    }
  },
  props: {
    title: {
      default: ''
    },
    defaultValue: {
      default: ''
    }
  },
  watch: {
    title: {
      handler (val) {
        this.pageTitle = val
      },
      immediate: true
    },
    pageTitle: {
      handler (val, old) {
        if (val !== old) {
          this.$emit("onTitle", val)
        }
      }
    },
    html: {
      handler (val, old) {
        if (val !== old) {
          this.handleHeaders()
          this.$emit("input", val)
        }
      }
    },
    defaultValue: {
      handler (val) {
        if (this.editor) {
          this.html = val
        }
      },
      immediate: true
    }
  },
  methods: {
    onCreated (editor) {
      this.editor = Object.seal(editor) // 一定要用 Object.seal() ，否则会报错
      if (this.defaultValue !== this.html) {
      	this.html = this.defaultValue
      }
      this.handleHeaders()
      i18nChangeLanguage(this.$utils.languageIsEN() ? 'en' : 'zh-CN')
    },
    handleBeforeUpload () {
      msgRef && msgRef.close && msgRef.close()
      msgRef = this.$message({
        dangerouslyUseHTMLString: true,
        message: `${this.$i18n.t('uploading')}...`,
        icon: <Loading color="#5897ff" />,
        customClass: 'test-loading-message',
        center: true,
        duration: 0
      })
    },
    uploadError () {
      msgRef && msgRef.close && msgRef.close()
      this.$refs.upload && this.$refs.upload.clearFiles()
    },
    uploadSuccess (res) {
      let name = '附件'
      if (this.$refs.upload.uploadFiles[0]) {
        name = this.$refs.upload.uploadFiles[0].name
      }
      if (res.data) {
        res.data = res.data.replace(/^http:/, window.location.protocol)
      }
      let str = `<a data-download="${name}" href="${res.data}" target="_blank" data-name="${name}">${name}</a>`
      this.editor.dangerouslyInsertHtml(str)
      msgRef && msgRef.close && msgRef.close()
      this.$refs.upload && this.$refs.upload.clearFiles()
    },
    handleHeaders: debounce(function () {
      this.$nextTick(() => {
        let headers = this.editor.getElemsByTypePrefix('header')
        if (headers && headers.length) {
          headers = headers.map(v => {
            return {
              type: v.type,
              id: v.id,
              content: SlateNode.string(v)
            }
          })
          this.headerList = headers
        }
      })
    }, 500),
    handleAnchor (id) {
      let dom = document.getElementById(id)
      if (dom) {
        dom.scrollIntoView()
      }
    }
  },
  beforeUnmount () {
    this.pageTitle = ""
    this.html = ""
    if (this.editor == null) return
    this.editor.destroy() // 组件销毁时，及时销毁编辑器
  }
}
</script>
<style scoped>
@import url('../../../../../plugins/normal.css');
.wangeditor-container {
	position: relative;
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  height: 100%;
}
.wangeditor-container .header-list {
  position: fixed;
  left: 0;
  top: 41px;
  bottom: 0;
  background: #fff;
  z-index: 1;
  width: 14%;
  height: calc(100vh - 41px);
  overflow-y: auto;
  padding: 4px 0;
	box-shadow: 0 2px 10px rgb(0 0 0 / 12%);
}
.wangeditor-container .header-list::-webkit-scrollbar {
  display: none;
}
.wangeditor-container .header-list .content-title {
  font-weight: bold;
  margin-bottom: 8px;
  font-size: 16px;
  color: #000;
  padding: 0 6px;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.wangeditor-container .header-list p.empty {
  padding: 10px 6px;
  color: #ccc;
}
.wangeditor-container .header-list ul li {
  list-style: none;
  width: 100%;
  padding: 0 6px;
  margin: 8px 0;
  color: #333;
  font-size: 14px;
}
.wangeditor-container .header-list ul li a {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
  padding: 0;
  margin: 0;
  text-align: left;
  cursor: pointer;
  display: inline-block;
}
.wangeditor-container .header-list ul li a:hover {
  color: #5897ff;
}
.wangeditor-container .header-list ul li.header2 {
  padding-left: 14px;
}
.wangeditor-container .header-list ul li.header3 {
  padding-left: 28px;
}
.wangeditor-container .header-list ul li.header4 {
  padding-left: 42px;
}
.wangeditor-container .header-list ul li.header5 {
  padding-left: 56px;
}
.wangeditor-container .custom-toolbar {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	border-bottom: 1px solid #ccc;
	z-index: 1;
  min-width: 1300px;
}
.wangeditor-container .content {
  width: 86%;
  margin-top: 40px;
	height: calc(100vh - 40px);
	overflow-y: auto;
	position: relative;
  margin-left: 14%;
	background-color: #fff;
	box-shadow: 0 2px 10px rgb(0 0 0 / 12%);
	padding: 20px 50px 50px 50px;
}
.wangeditor-container .content::-webkit-scrollbar {
  display: none;
}

.wangeditor-container .title-container {
	padding: 20px 0;
	border-bottom: 1px solid #e8dbdb;
}

.wangeditor-container .title-container input {
	font-size: 30px;
	border: 0;
	outline: none;
	width: 100%;
	line-height: 1;
}

.wangeditor-container .editor-text-area {
	min-height: 450px;
	margin-top: 20px;
}
.wangeditor-container >>> ol li {
	list-style: auto
}
.wangeditor-container >>> ul li {
	list-style: initial
}
.wangeditor-container >>> .w-e-text-container {
	overflow: inherit;
}
.wangeditor-container >>> .w-e-bar-bottom .w-e-select-list{
	top: 0;
	bottom: initial;
}
.wangeditor-container .editor-text-area >>> a{
	color: #5897ff !important;
	cursor: pointer;
}
</style>
