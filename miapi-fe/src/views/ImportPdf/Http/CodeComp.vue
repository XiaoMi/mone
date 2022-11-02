<template>
	<div class="code-container">
		<h4><el-icon><Reading /></el-icon>{{$i18n.t('detailList.requestExample')}}</h4>
		<div class="code-wrap">
			<!-- <el-tabs :key="refreshTab" v-if="list.length" type="border-card" v-model="activeName">
				<el-tab-pane v-for="item in list" :key="item.label" :label="`${item.name || item.label}`" :name="item.label" lazy>
          <div v-if="item.requestParamExpType === showCodeType['3'].type" style="height: 300px">
            <JsonEditor :content="handleContent(item.codeGenExp)" :jsonEditorOptions="jsonEditorOptions"/>
          </div>
          <div v-else-if="item.requestParamExpType === showCodeType['4'].type" style="height: 300px">
            <codeMirror :content="item.codeGenExp" :codeMirrorOptions="{...codeMirrorOptions, readOnly: !editCustomize}"/>
          </div>
          <codeMirror v-else :content="item.codeGenExp" :codeMirrorOptions="codeMirrorOptions"/>
        </el-tab-pane>
      </el-tabs> -->
      <ul :key="refreshTab" v-if="list.length" class="">
        <li v-for="item in list" :key="item.label">
          <span>{{item.label}}</span>
          <p v-if="item.requestParamExpType === showCodeType['2'].type">{{item.codeGenExp}}</p>
          <p v-else v-html="item.codeGenExp.replace(/\n/ig, '<br/>')"></p>
        </li>
      </ul>
      <Empty :imageSize="200" v-else/>
		</div>
	</div>
</template>

<script>
import codeMirror from '@/components/CodeMirror'
import JsonEditor from '@/components/JsonEditor'
import { SHOW_CODE_TYPE, PROTOCOL_TYPE, DATA_TYPE, API_REQUEST_PARAM_TYPE, REQUEST_TYPE, DATA_TYPE_KEY, AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import Empty from '@/components/Empty'
export default {
  name: 'Code',
  components: {
    codeMirror,
    JsonEditor,
    Empty
  },
  props: {
    reqExpList: {
      type: Array,
      default () {
        return []
      }
    },
    apiDetail: {
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
  computed: {
    showCodeType () {
      return SHOW_CODE_TYPE
    }
  },
  data () {
    return {
      codeMirrorOptions: {
        theme: 'default',
        readOnly: true,
        mode: 'text/x-java',
        lineNumbers: false,
        lineWrapping: false,
        line: false,
        lint: false,
        autoCloseBrackets: false,
        styleActiveLine: false,
        autoRefresh: false,
        smartIndent: false,
        selfContain: false,
        spellcheck: false,
        autocorrect: false,
        hintOptions: {
          completeSingle: true
        }
      },
      refreshTab: new Date().getTime(),
      editCustomize: false,
      jsonEditorOptions: {
        mainMenuBar: false,
        onEditable: () => false,
        showLineNumbers: false
      },
      activeName: undefined,
      list: []
    }
  },
  watch: {
    reqExpList: {
      handler (val) {
        let list = []
        let hasCustom = false; // 首次没有数据
        if (val && val.length) {
          list = val.map(v => {
            if (v.requestParamExpType === SHOW_CODE_TYPE['4'].type) {
              hasCustom = true
              v.name = this.$i18n.t("customize")
              v.label = this.$i18n.t("customize")
            }
            return {
              label: SHOW_CODE_TYPE[v.requestParamExpType].label,
              ...v,
              mode: SHOW_CODE_TYPE[v.requestParamExpType].mode
            }
          })
        }

        // if (JSON.stringify(this.apiDetail) !== "{}") {
        //   let codeGenExp = ""
        //   try {
        //     codeGenExp = this.handleJavaScript()
        //   } catch (error) {
        //     // console.error(error)
        //   }
        //   list.push({
        //     codeGenExp,
        //     label: SHOW_CODE_TYPE['99'].label,
        //     mode: SHOW_CODE_TYPE['99'].mode
        //   })
        // }
        this.refreshTab = new Date().getTime()
        if (list.length) {
          this.activeName = list[0].label
          this.codeMirrorOptions = {
            ...this.codeMirrorOptions,
            mode: list[0].mode
          }
        }
        this.list = list
      },
      immediate: true,
      deep: true
    },
    activeName: {
      handler (val, old) {
        if (val && old && (old !== '0') && val !== old) {
          let mode = this.list.filter(v => v.label === val)[0].mode
          this.codeMirrorOptions = {
            ...this.codeMirrorOptions,
            mode
          }
        }
      }
    }
  },
  methods: {
    handleContent (c) {
      if (typeof c === 'string') {
        try {
          c = JSON.parse(c)
        } catch (error) {}
      }
      return c
    },
    handleJavaScript () {
      let description = ""
      let apiDescUrl = ""
      let params = []
      let url = ""
      let method = ""
      let data = {}
      let headers = []
      switch (this.$utils.getQuery('apiProtocol')) {
        case PROTOCOL_TYPE.HTTP:
          description = this.apiDetail.baseInfo.apiDesc || this.apiDetail.baseInfo.apiName
          apiDescUrl = window.location.href
          if (this.apiDetail.baseInfo.apiRequestParamType === API_REQUEST_PARAM_TYPE.FORM_DATA) {
            params = (this.apiDetail.requestInfo || []).map(v => {
              data[v.paramKey] = v.paramValue || ""
              return {
                type: DATA_TYPE[v.paramType],
                key: v.paramKey
              }
            })
          } else {
            if (this.apiDetail.baseInfo.apiRequestParamType === API_REQUEST_PARAM_TYPE.JSON) {
              (this.apiDetail.requestInfo || []).map(v => {
                if (v.paramKey === "root") {
                  params = v.childList.map(item => {
                    return {
                      type: DATA_TYPE[item.paramType],
                      key: item.paramKey
                    }
                  })
                }
              })
            }
            data = JSON.parse(this.apiDetail.baseInfo.apiRequestRaw || "{}")
          }
          url = this.apiDetail.baseInfo.apiURI
          method = REQUEST_TYPE[this.apiDetail.baseInfo.apiRequestType]
          headers = this.apiDetail.headerInfo || []
          break
        case PROTOCOL_TYPE.Gateway:
          data = []
          description = this.apiDetail.apiDesc || this.apiDetail.gatewayApiBaseInfo.name
          apiDescUrl = window.location.href
          if (this.apiDetail.apiRequestParamType === API_REQUEST_PARAM_TYPE.FORM_DATA) {
            let arr = this.apiDetail.requestInfo || [];
            (arr[0]?.childList || []).forEach(v => {
              if (v.paramType === DATA_TYPE_KEY.object) {
                let obj = {}
                v[0].childList.forEach(item => {
                  obj[item.paramKey] = item.paramValue || ""
                  params.push({
                    type: DATA_TYPE[item.paramType],
                    key: item.paramKey
                  })
                })
                data.push(obj)
              } else if (v.paramType === DATA_TYPE_KEY.array) {
                let arr = []
                v[0].childList.forEach(item => {
                  arr.push(item.paramValue || "")
                })
                data.push(arr)
              }
            })
          } else {
            data.push(JSON.parse(this.apiDetail.apiRequestRaw || "{}"))
          }
          headers = this.apiDetail.headerInfo || []
          url = this.apiDetail.gatewayApiBaseInfo.url
          method = this.apiDetail.gatewayApiBaseInfo.httpMethod
          break
        default:
          break
      }
      if (!url) {
        return ""
      }
      let str = "/**\n"
      str += `* @description ${description || "暂无描述"}\n`
      str += `* @url ${apiDescUrl}\n`
      if (params.length) {
        params.forEach(v => {
          str += `* @param {${v.type.replace("[", "").replace("]", "")}} ${v.key}\n`
        })
      }
      str += "*/\nexport default () => Axios({\n"
      if (headers.length) {
        str += `  headers: {\n`
        headers.forEach(v => {
          str += `    '${v.headerName}': '${v.headerValue}',\n`
        })
        str += `  },\n`
      }
      str += `  url: '${url}',\n`
      str += `  method: '${method.toLocaleLowerCase()}',\n`
      str += `  data: ${JSON.stringify(data)}\n`
      str += `})\n`
      return str
    }
  }
}
</script>
<style scoped>
.code-container .code-wrap {
  position: relative;
	padding: 0 20px 10px;
}
.code-container .code-wrap ul li{
  border: 1px solid #f1f0f0;
  margin-block: 8px;
}
.code-container .code-wrap ul li span {
  display: inline-block;
  width: 100%;
  height: 32px;
  background: #f5f7fa;
  line-height: 32px;
  padding: 0 8px;
  font-size: 14px;
}
.code-container .code-wrap ul li p {
  padding: 8px;
  font-size: 14px;
}
.code-container .code-wrap .el-tabs--border-card {
	box-shadow: none;
	border-top: none;
}
.code-container .code-wrap >>> .el-tabs__header {
	padding: 0;
}
.code-container .code-wrap .el-tabs--border-card >>> .el-tabs__header .el-tabs__item {
	position: relative;
	margin-top: 0;
	border-top: none;
	border-right: 1px solid #e6e6e6;
	color: rgba(0, 0, 0, 0.65);
}
.code-container .code-wrap >>> .el-tabs__nav-scroll {
	overflow: initial;
	border-top: 1px solid #e6e6e6;
}
.code-container .code-wrap .el-tabs--border-card >>> .el-tabs__header .el-tabs__item.is-active::before {
	content: '';
	position: absolute;
	left: 0;
	right: 0;
	top: -1px;
	width: 100%;
	height: 2px;
	background-color: #1890FF;
	border-radius: 1px 1px 0 0;
}
.code-container .code-wrap >>> .el-tabs__content {
	font-size: 14px;
}
.code-container .code-wrap >>> .el-tabs__content .CodeMirror-sizer {
	margin-left: 0 !important;
  margin-top: 0;
}
.code-container .code-wrap >>> .el-tabs__content .CodeMirror-gutters {
  display: none;
}
.code-container .code-wrap .el-empty{
  padding: 0;
}

.code-container .code-wrap >>> .jsoneditor {
	/* border-color: #e6e6e6; */
  border: none;
}
.code-container .code-wrap >>> .ace_gutter {
	background: #f1f0f0;
  display: none;
}
.code-container .code-wrap >>> .ace_scroller {
  left: 0 !important;
}
</style>
