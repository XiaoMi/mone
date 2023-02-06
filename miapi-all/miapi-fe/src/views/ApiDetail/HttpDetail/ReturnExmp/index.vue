<template>
	<div class="code-container">
		<h4>{{$i18n.t('detailList.returnExample')}}</h4>
		<div class="code-wrap">
      <span v-show="activeName === showCodeType['4'].label" class="copy-icon">
        <el-button @click="handleEditExp" size="small" type="primary">{{editCustomize ? 'Save' : 'Edit'}}</el-button>
      </span>
			<el-tabs :key="refreshTab" v-if="respExpList.length" type="border-card" v-model="activeName">
				<el-tab-pane v-for="item in list" :key="item.label" :label="`${item.name || item.label}`" :name="item.label" lazy>
          <div v-if="item.respGenExpType === showCodeType['4'].type" style="height: 300px">
            <codeMirror :content="item.respGenExp" @json-change="handleCustomReq" :codeMirrorOptions="{...codeMirrorOptions, readOnly: !editCustomize}"/>
          </div>
          <div v-else class="return-exmp" >
            <JsonEditor :key="moveData.initHeight" :content="handleContent(item.respGenExp)" :jsonEditorOptions="jsonEditorOptions"/>
            <span @mousedown.stop="handleDown($event)" class="lashen-img" />
          </div>
        </el-tab-pane>
			</el-tabs>
      <Empty :imageSize="300" v-else/>
		</div>
	</div>
</template>

<script>
import codeMirror from '@/components/CodeMirror'
import JsonEditor from '@/components/JsonEditor'
import { SHOW_RESULT_TYPE, AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import Empty from '@/components/Empty'
import debounce from "@/common/debounce"
import { editApiDiyExp } from "@/api/apilist"
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
      moveData: {
        y1: null,
        y2: null,
        initHeight: 300
      }
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
            }
            return {
              ...v,
              label: SHOW_RESULT_TYPE[v.respGenExpType].label,
              mode: SHOW_RESULT_TYPE[v.respGenExpType].mode
            }
          })
          if (!hasCustom) {
            list.push({
              respGenExp: "",
              respGenExpType: SHOW_RESULT_TYPE['4'].type,
              label: SHOW_RESULT_TYPE['4'].label,
              mode: SHOW_RESULT_TYPE['4'].mode,
              name: this.$i18n.t("customize")
            })
          } else {
            this.refreshTab = new Date().getTime()
          }
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
    moveData: {
      handler (newVal) {
        if (newVal.y2 !== null) {
          let reduce = newVal.y2 - newVal.y1 + newVal.initHeight
          if (reduce > 300) {
            document.querySelector('.return-exmp').style.height = `${reduce}px`
          }
        }
      },
      deep: true
    }
  },
  mounted () {
    window.addEventListener('mouseup', this.handleUp, true)
  },
  destroyed () {
    window.removeEventListener('mouseup', this.handleUp, true)
  },
  methods: {
    handleCustomReq: debounce(function(val){
      this.list.filter(v => v.label === SHOW_RESULT_TYPE['4'].label)[0].respGenExp = val
    }, 300),
    handleEditExp(){
      if (!this.editCustomize){
        this.editCustomize= true
        this.refreshTab = new Date().getTime()
        return
      }
      let content = this.list.filter(v => v.label === SHOW_RESULT_TYPE['4'].label)[0].respGenExp
      editApiDiyExp({
        apiID: this.$utils.getQuery("apiID"),
        expType: 4,
        type: 2,
        content
      }).then((data) => {
        if(data.message === AJAX_SUCCESS_MESSAGE){
          this.$message.success(this.$i18n.t("successfullyModified"))
          this.editCustomize = false
          this.refreshTab = new Date().getTime()
        } else{
          this.$message.error(data.message)
        }
      })
    },
    handleContent (c) {
      if (typeof c === 'string') {
        try {
          c = JSON.parse(c)
        } catch (error) {}
      }
      return c
    },
    handleDown (e) {
      this.moveData.y1 = e.pageY
      document.onmousemove = this.mouseMove
    },
    mouseMove (e) {
      this.moveData.y2 = e.pageY
    },
    handleUp (e) {
      document.onmousemove = null
      if (this.moveData.y1 && this.moveData.y2) {
        let initHeight = this.moveData.y2 - this.moveData.y1 + this.moveData.initHeight
        this.moveData = {
          y1: null,
          y2: null,
          initHeight: initHeight >= 300 ? initHeight : 300
        }
      }
    }
  }
}
</script>
<style scoped>
.code-container .code-wrap {
	padding: 20px;
  position: relative;
}
.code-container .code-wrap .copy-icon {
  position: absolute;
  right: 30px;
  top: 70px;
  z-index: 100;
  cursor: pointer;
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
