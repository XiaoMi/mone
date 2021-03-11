<!--
  Copyright 2020 Xiaomi

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  -->

<template>
  <el-dialog title='单步回放' :visible.sync='oneStepDialogVisible' width='1000px' :before-close="handleStepClose" class='step-dialog'>
    <el-card class='card'>
      <div v-for='(value,key) in card' :key='key' class='card-row'>
        <span class='card-row_key'>{{ key }}:</span>
        <span class='card-row_value' :title='`${JSON.stringify(value)}`'>{{ value }}</span>
      </div>
    </el-card>
    <el-divider content-position="center">代码Diff</el-divider>
    <div v-if='showCodeDiff'>
      <div class='result-row'>
        <div class='result-row_left'>原始结果：</div>
        <div class='result-row_right'>回放结果：</div>
      </div>
      <div id='stepView' ref='stepView'></div>
    </div>
    <div class='diff-loading' v-else>
      <i class='el-icon-loading'></i>
      <span>{{ diffLoadingText }}</span>
    </div>
  </el-dialog>
</template>

<script>
import service from "@/plugin/axios"
// 代码diff
import CodeMirror from 'codemirror'
import 'codemirror/lib/codemirror.css'
import 'codemirror/addon/merge/merge.js'
import 'codemirror/addon/merge/merge.css'
import DiffMatchPatch from 'diff-match-patch'

window.diff_match_patch = DiffMatchPatch
window.DIFF_DELETE = -1
window.DIFF_INSERT = 1
window.DIFF_EQUAL = 0

export default {
  props: {
    show: {
      type: Boolean,
      required: true
    },
    content: {
      type: Object,
      default: () => ({})
    }
  },
  data () {
    return {
      oneStepDialogVisible: false,
      card: {},
      showCodeDiff: false,
      diffLoadingText: '代码diff中.....'
    }
  },
  watch: {
    show () {
      if (this.show && this.content) {
        const { sourceType, httpTraffic, dubboTraffic } = this.content
        this.card = sourceType === 1 ? httpTraffic : dubboTraffic
        this.oneStepDialogVisible = true
        this.oneStepPlayBack(this.content)
      }
    }
  },
  methods: {
    async oneStepPlayBack (content) {
      let { id, response } = content
      let newResponse = await service({
        url: '/traffic/recording/replay',
        method: 'POST',
        data: { id }
      })
      try {
        response = JSON.stringify(JSON.parse(response), null, 2)
        let target = JSON.stringify(JSON.parse(newResponse.data), null, 2)
        this.showCodeDiff = true
        this.$nextTick(() => {
          this.diffCodeUI(response, target)
        })
      } catch (e) {
        this.diffLoadingText = '数据拉取失败......'
      }
    },
    diffCodeUI (origin, target) {
      if (target == null) return
      let container = document.getElementById('stepView')
      container.innerHTML = ''
      CodeMirror.MergeView(container, {
        value: origin, // 原始值
        origLeft: null,
        orig: target, // 目标值
        lineNumbers: true,
        mode: 'shell',
        highlightDifferences: true,
        styleActiveLine: true,
        matchBrackets: true,
        connect: 'align',
        readOnly: 'nocursor',
        revertButtons: false // 关闭'revert chunk'还原模块
      })
    },
    handleStepClose () {
      this.$emit('doCloseDialog', false)
      this.oneStepDialogVisible = false
      this.card = {}
      this.showCodeDiff = false
      this.diffLoadingText = '代码diff中.....'
      let stepView = this.$refs.stepView
      stepView && (stepView.innerHTML = '')
    }
  }
}
</script>
<style lang="scss" scoped>
.card {
  &-row {
    height: 22px;
    display: flex;
    &_key {
      display: inline-block;
      height: 22px;
      line-height: 22px;
      font-size: 15px;
      color: #909399;
      font-weight: 700;
      margin-right: 10px;
    }
    &_value {
      display: inline-block;
      cursor: pointer;
      font-size: 14px;
      width: 600px;
      height: 22px;
      line-height: 22px;
      overflow: hidden;
      color: rgb(102, 102, 102);
    }
  }
}
.result-row {
  display: flex;
  margin-bottom: 10px;
  div {
    width: 50%;
    text-align: left;
    font-size: 15px;
    color: #909399;
    font-weight: 700;
  }
  &_right {
    padding-left: 50px
  }
}
.card:hover {
  box-shadow: 10px 10px 15px 0 rgba(0,0,0,.1)
}
.diff-loading {
  height: 500px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  i {
    font-size: 28px;
  }
  span {
    font-size: 16px;
    color: #909399;
    margin-top: 12px;
  }
}
</style>
<style lang="scss">
.step-dialog {
  .el-dialog__body {
    padding: 30px 35px;
  }
}
#stepView {
  .CodeMirror-merge, .CodeMirror-merge .CodeMirror {
    height: 500px;
  }
}
</style>
