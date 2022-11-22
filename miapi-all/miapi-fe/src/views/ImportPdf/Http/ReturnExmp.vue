<template>
	<div class="code-container">
		<h4><el-icon><Reading /></el-icon>{{$i18n.t('detailList.returnExample')}}</h4>
		<div class="code-wrap">
			<!-- <el-tabs :key="refreshTab" v-if="respExpList.length" type="border-card" v-model="activeName">
				<el-tab-pane v-for="item in list" :key="item.label" :label="`${item.name || item.label}`" :name="item.label" lazy>
          <div v-if="item.respGenExpType === showCodeType['4'].type" style="height: 300px">
            <codeMirror :content="item.respGenExp" :codeMirrorOptions="{...codeMirrorOptions, readOnly: !editCustomize}"/>
          </div>
          <div v-else class="return-exmp" >
            <JsonEditor :content="handleContent(item.respGenExp)" :jsonEditorOptions="jsonEditorOptions"/>
          </div>
        </el-tab-pane>
			</el-tabs> -->
      <ul :key="refreshTab" v-if="respExpList.length" class="">
        <li v-for="item in list" :key="item.label">
          <span>{{item.label}}</span>
          <!-- <p>{{item.respGenExp}}</p> -->
          <p v-html="item.respGenExp.replace(/\n/ig, '<br/>')"></p>
        </li>
      </ul>
      <Empty :imageSize="200" v-else/>
		</div>
	</div>
</template>

<script>
import codeMirror from '@/components/CodeMirror'
import JsonEditor from '@/components/JsonEditor'
import { SHOW_RESULT_TYPE } from '@/views/constant'
import Empty from '@/components/Empty'
export default {
  name: 'ReturnExmp',
  components: {
    codeMirror,
    JsonEditor,
    Empty
  },
  props: {
    respExpList: {
      type: Array,
      default () {
        return []
      }
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
      activeName: undefined,
      showCodeType: SHOW_RESULT_TYPE,
      list: [],
      jsonEditorOptions: {
        mainMenuBar: false,
        onEditable: () => false,
        showLineNumbers: false
      },
    }
  },
  watch: {
    respExpList: {
      handler (val) {
        if (val && val.length) {
          let list = []
          let hasCustom = false; // 首次没有数据
          list = val.map(v => {
            if (v.respGenExpType === SHOW_RESULT_TYPE['4'].type) {
              hasCustom = true
              v.name = this.$i18n.t("customize")
              v.label = this.$i18n.t("customize")
            }
            return {
              label: SHOW_RESULT_TYPE[v.respGenExpType].label,
              ...v,
              mode: SHOW_RESULT_TYPE[v.respGenExpType].mode
            }
          })
          this.refreshTab = new Date().getTime()
          this.list = list
          this.activeName = this.list[0].label
          this.codeMirrorOptions = {
            ...this.codeMirrorOptions,
            mode: this.list[0].mode
          }
        } else {
          this.list = []
        }
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
    },
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
  }
}
</script>
<style scoped>
.code-container .code-wrap {
	padding: 0 20px 20px;
  position: relative;
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
/* .code-container .code-wrap >>> .el-tabs__content .CodeMirror-sizer {
	margin-left: 17px !important;
} */
.code-container .return-exmp{
	height: 300px;
  position: relative;
}
.code-container .return-exmp .lashen-img{
  position: absolute;
  right: -16px;
  bottom: -15px;
  z-index: 100;
  width: 16px;
  height: 16px;
  cursor: pointer;
}
.code-container .return-exmp .lashen-img::before{
  position: absolute;
  width: 10px;
  height: 1px;
  background: #333;
  content: '';
  transform: rotate(-45deg);
  right: 1px;
  bottom: 4px;
  opacity: 0.8;
}
.code-container .return-exmp .lashen-img::after{
  position: absolute;
  width: 4px;
  height: 1px;
  background: #333;
  content: '';
  transform: rotate(-45deg);
  right: 2px;
  bottom: 2px;
  opacity: 0.8;
}
.code-container .return-exmp >>> .jsoneditor {
	/* border-color: #e6e6e6; */
  border: none;
}
.code-container .return-exmp >>> .ace_gutter {
	background: #f1f0f0;
  display: none;
}
.code-container .return-exmp >>> .ace_scroller {
  left: 0 !important;
}
.code-container .code-wrap .el-empty{
  padding: 0;
}
.code-container .code-wrap >>> .el-tabs__content .CodeMirror-sizer {
	margin-left: 0 !important;
  margin-top: 0;
}
.code-container .code-wrap >>> .el-tabs__content .CodeMirror-gutters {
  display: none;
}
</style>
