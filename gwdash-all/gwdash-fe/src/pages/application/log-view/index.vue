<template>
    <d2-container>
            <d2-module margin-bottom>
                <div>查看应用日志</div>
            </d2-module>
            <d2-module>
                    <el-form size="mini" :rules="rules" ref="ruleForm" :model="form" label-width="120px">
                        <el-form-item label="主机" prop="ip">
                                <el-input v-model="form.ip"></el-input>
                        </el-form-item>
                        <el-form-item label="日志路径" prop="path">
                            <el-input v-model="form.path" @change="listFiles">
                              <!-- <template slot="prepend">/home/work/</template> -->
                            </el-input>
                        </el-form-item>
                        <el-form-item label="日志文件" prop="file">
                            <el-select v-model="form.file" filterable placeholder="请选择">
                                <el-option
                                    v-for="item in fileOptions"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value">
                                </el-option>
                            </el-select>
                            <el-button @click="listFiles" round icon="el-icon-refresh"></el-button>
                        </el-form-item>
                        <el-form-item label="开始位置">
                          <el-input-number :disabled="tailStatus.start" v-model="form.pointer" :min="-1"></el-input-number>
                        </el-form-item>
                        <el-form-item label="行数">
                          <el-input-number :disabled="tailStatus.start" v-model="form.lineNum" :min="0" :max="100"></el-input-number>
                        </el-form-item>
                        <el-form-item>
                          <el-button type="primary" @click="showFile">查看日志</el-button>
                          <el-button type="success" v-if="tailStatus.start" @click="stop">关闭tail</el-button>
                          <el-button type="warning" v-else @click="start">开启tail</el-button>
                        </el-form-item>
                    </el-form>
                    <div class="app-log-view">
                      <codemirror class="codeMirror" v-model="content" :options="cmOptions"></codemirror>
                    </div>
            </d2-module>
    </d2-container>
</template>

<script>
import service from '@/plugin/axios/index'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/theme/base16-dark.css'
import 'codemirror/addon/lint/lint.js'
import 'codemirror/addon/lint/json-lint.js'

import 'codemirror/addon/lint/lint.css'

export default {
  data () {
    return {
      cmOptions: {
        tabSize: 2,
        indentUnit: 2,
        mode: 'text/javascript',
        theme: 'base16-dark',
        readOnly: 'nocursor',
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
      form: {
        ip: '',
        path: '',
        file: '',
        pointer: 0,
        lineNum: 50
      },
      rules: {
        ip: [
          { required: true, message: '输入ip', trigger: 'blur' },
          { required: true, pattern: /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/, trigger: 'blur' }
        ],
        path: [
          { required: true, message: '日志路径', trigger: 'blur' }
        ],
        file: [
          { required: true, message: '请选择文件', trigger: 'change' }
        ]
      },
      tailStatus: {
        start: false
      },
      fileOptions: [],
      content: '',
      isOver: false
    }
  },
  methods: {
    listFiles () {
      const form = this.form
      service({
        url: `/log/list/file?ip=${form.ip}&path=${form.path}`,
        method: 'GET'
      }).then(files => {
        this.file = ''
        this.fileOptions = [];
        (JSON.parse(files || '[]')).forEach(it => {
          if (it.isFile) {
            this.fileOptions.push({
              label: it.name,
              value: `${form.path}/${it.name}`
            })
          }
        })
      })
    },
    showFile () {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          const form = this.form
          const content = this.content
          const lineNum = content.split('\n').length
          service({
            url: `/log/show/file?ip=${form.ip}&path=${form.file}&pointer=${form.pointer}&lineNum=${form.lineNum}`,
            method: 'GET'
          }).then(string => {
            if (string) {
              const data = JSON.parse(string)
              if (lineNum < form.lineNum && content) {
                this.content = content + '\n' + (data.lines || []).join('\n')
              } else {
                this.content = (data.lines || []).join('\n')
              }
              this.form.pointer = data.pointer
              this.isOver = data.over
            }
          })
        } else {
          return false
        }
      })
    },
    start () {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          this.tailStatus.start = true
          this.rollFile()
        }
      })
    },
    stop () {
      this.$refs['ruleForm'].validate((valid) => {
        if (valid) {
          this.tailStatus.start = false
        }
      })
    },
    rollFile () {
      if (this.tailStatus.start) {
        this.showFile()
        setTimeout(() => {
          this.rollFile()
        }, 1000)
      }
    }
  }
}
</script>

<style lang="scss">
.app-log-view {
  .CodeMirror {
    height: 500px;
  }
}
</style>
