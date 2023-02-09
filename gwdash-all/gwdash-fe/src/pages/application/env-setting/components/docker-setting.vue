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
  <div>
    <transition>
      <el-card
        class='card'
        v-if='errorTitle'>
        <div class='title'>{{ errorTitle }}</div>
        <div v-for='item in errorMsgList' :key='item.ip' class='card-row'>
          <i class="el-alert__icon el-icon-error" style='color: #f56c6c;margin-right:8px'></i>
          <div class='card-row_entry' v-for='(value,key) in item' :key='key'>
            <div class='key'>{{ key }}：</div>
            <div class='value'>{{ value }}</div>
          </div>
        </div>
      </el-card>
    </transition>
    <el-card style="margin-bottom:20px">
      <el-form :model="form" :rules="rulesTop" size="mini" label-width="110px" ref='formTop'>
        <el-form-item label="cpu" prop="cpu">
          <el-input-number v-model="form.cpu" controls-position="right" placeholder="cpu个数" style="width: 15%" :min="1" :max="4"></el-input-number>
        </el-form-item>
        <el-form-item label="内存(M)" prop="memory">
          <el-input-number v-model="form.memory" step=1024 controls-position="right" placeholder="内存大小" style="width: 15%" :min="2048" :max="(form.cpu || 1) * 4096"></el-input-number>
        </el-form-item>
        <el-form-item label="实例数" prop="replicate">
            <el-input-number v-model="form.replicate" controls-position="right" placeholder="启动实例数" style="width: 15%" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item label="最大实例数" prop="maxReplicate">
          <el-input-number v-model="form.maxReplicate" controls-position="right" placeholder="启动实例数" style="width: 15%" :min="1"></el-input-number>
        </el-form-item>
        <el-form-item>
          <el-button @click="updateComputingResource" type="primary" :disabled="isSave">更新</el-button>
          <el-button @click="checkUpgrade" type="primary" :disabled="isSave">校验升配能否成功</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card>
      <el-form :model="form" :rules="rulesBottom" size="mini" label-width="110px" ref='formBottom'>
        <el-form-item label="日志路径" prop="logPath">
          <el-input v-model="form.logPath" placeholder="项目配置的日志路径" style="width: 50%"></el-input>
        </el-form-item>
        <el-form-item label="数据卷" prop="volume">
          <el-input v-model="form.volume" placeholder="配置数据卷" style="width: 50%"></el-input>
        </el-form-item>
        <el-form-item label="jvm参数" prop="jvmParams">
          <el-input v-model="form.jvmParams" placeholder="非必填, 长度0-1024" style="width: 50%"></el-input>
        </el-form-item>
        <el-form-item label="健康检查" prop="healthCheckUrl">
          <span slot="label">
            健康检查
            <el-popover
              trigger="hover"
              width="100">
              <div style="text-align:center;">
                <a style="color: #409EFF;"
                   href="xx_replace_xx"
                   type="primary"
                   target="_blank"
                   icon="el-icon-question">点击查看健康检测接口</a>
              </div>
              <i slot="reference" class="el-icon-question"></i>
            </el-popover>
          </span>
          <el-input v-model="form.healthCheckUrl" placeholder="必填, 长度0-120" style="width: 50%"></el-input>
        </el-form-item>
        <el-form-item label="labels" prop="labels">
            <span slot="label">
            labels
              <el-popover
                trigger="hover"
                width="300">
                <div style="fontSize:12px">
                  支持标签说明：<br>
                   &nbsp;暴露端口相关标签，支持暴露3个端口：<br>
                    &nbsp;&nbsp;http_port=端口号<br>
                   &nbsp;&nbsp;dubbo_port=端口号<br>
                   &nbsp;&nbsp;third_port=端口号<br>
                  &nbsp;注：端口号为应用中指定的端口，若为-1时不指定<br>

                  &nbsp;需要keycenter：<br>
                   &nbsp;&nbsp;keycenter=true
                </div>
                <i slot="reference" class="el-icon-question"></i>
              </el-popover>
            </span>
          <el-input v-model="form.labels" placeholder="非必填, 长度0-200" style="width: 50%"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="updateDeploymentSetting" type="primary" :disabled="isSave">更新</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-dialog title='校验明细' :visible.sync='dialogCheckVisible' width='700px' @closed='handleClose'>
      <div class="expense-details-container">
        <div class="expense-details-table">
          <el-table stripe :data="checkTableData" class="table-list">
            <el-table-column prop="ip" label="IP地址" width="160"></el-table-column>
            <el-table-column prop="cpu" label="CPU" width="100">
              <template slot-scope="scope">
                  <span v-if="scope.row.cpu > 0">{{ '仍需'+scope.row.cpu + '核' }}</span>
                  <span v-if="scope.row.cpu == 0">{{ scope.row.cpu  + '核' }}</span>
                  <span v-if="scope.row.cpu < 0">{{ '剩余' + Math.abs(scope.row.cpu)  + '核' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="mem" label="MEM" width="210">
              <template slot-scope="scope">
                  <span v-if="scope.row.mem > 0">{{ '仍需' + (Math.abs(scope.row.mem)/1048576).toFixed(2) + 'M' }}</span>
                  <span v-if="scope.row.mem == 0">{{ (Math.abs(scope.row.mem)/1048576).toFixed(2)  + 'M' }}</span>
                  <span v-if="scope.row.mem < 0">{{ '剩余' + (Math.abs(scope.row.mem)/1048576).toFixed(2)  + 'M' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="canUpgrade" label="升配结果" width="190">
             <template slot-scope="scope">
                <el-tag
                  :type="scope.row.canUpgrade ? 'success' : 'danger'"
                 >
                  <i :class="scope.row.canUpgrade ? 'el-icon-success' : 'el-icon-error'"></i>
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import service from '@/plugin/axios/index'
import qs from 'qs'
export default {
  props: {
    projectId: {
      type: [Number, String],
      required: true
    },
    envId: {
      type: [Number, String],
      required: true
    }
  },
  data () {
    var checkLogPath = (rule, value, callback) => {
      if (!value || value.length <= 0 || value.length > 100) {
        callback(new Error('长度在 0 到 100 个字符'))
      } else if (!(value.startsWith("/home/work/log/")) || !(value.endsWith("/"))) {
        callback(new Error('路径必须以 /home/work/log/ 开头、以 / 结尾'))
      } else if (!!value.slice(15, value.length - 1) && !(/^[\w-/]+$/.test(value.slice(15, value.length - 1)))) {
        callback(new Error('log/后子路径只能为数字字母下划线或-'))
      } else {
        callback()
      }
    }
    var checkHealth = (rule, value, callback) => {
      if (!value || value.length <= 0 || value.length > 120) {
        callback(new Error('长度在 0 到 120 个字符'))
      } else if (!(value.startsWith("dubbo://%s/")) && !(value.startsWith("http://%s"))) {
        callback(new Error('路径必须以 dubbo://%s/ 或 http://%s 开头'))
      } else {
        callback()
      }
    }
    return {
      form: {},
      rulesTop: {
        cpu: [
          { required: true, message: '请输入', trigger: 'blur' }
        ],
        memory: [
          { required: true, message: '请输入', trigger: 'blur' }
        ],
        replicate: [
          { required: true, message: '请输入', trigger: 'blur' }
        ],
        maxReplicate: [
          { required: true, message: '请输入', trigger: 'blur' }
        ]
      },
      rulesBottom: {
        logPath: [
          {
            required: true,
            validator: checkLogPath,
            trigger: "blur"
          }
        ],
        // healthCheckUrl: [
        //   {
        //     required: true,
        //     validator: checkHealth,
        //     trigger: "blur"
        //   }
        // ],
        jvmParams: [
          {
            min: 0,
            max: 1024,
            message: "长度在 0 到 1024 个字符",
            trigger: "blur"
          }
        ],
        labels: [
          {
            min: 0,
            max: 200,
            message: "长度在 0 到 200 个字符",
            trigger: "blur"
          }
        ]
      },
      errorTitle: '',
      errorMsgList: [],
      checkTableData: [],
      dialogCheckVisible: false
    }
  },
  created () {
    this.getDockerSetting()
  },
  methods: {
    checkUpgrade () {
      this.errorTitle = ''
      this.errorMsgList = ''
      this.$refs['formTop'].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        service({
          url: '/project/env/setting/deployment/upgrade/',
          method: 'POST',
          data: {
            ...this.form
          }
        }).then(res => {
          this.checkTableData = res
          this.dialogCheckVisible = true
        }, err => {
          console.log('err', err.msg)
        })
      })
    },
    handleClose () {
      this.checkTableData = []
    },
    updateComputingResource () {
      // 错误信息初始化
      this.errorTitle = ''
      this.errorMsgList = ''
      this.$refs['formTop'].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        service({
          url: '/project/env/setting/deployment/resource/',
          method: 'POST',
          data: {
            ...this.form,
            envId: this.envId
          }
        }).then(res => {
          if (Array.isArray(res)) {
            this.errorTitle = '升级配置失败'
            this.errorMsgList = res.map(item => ({
              ip: item.ip,
              cpu: item.cpu,
              'memory(M)': item.mem && item.mem / 1024 / 1024
            }))
            return
          }
          this.$message.success('保存成功')
          setTimeout(() => {
            this.getDockerSetting()
          }, 300)
        }, err => {
          console.log('err', err.msg)
        })
      })
    },
    updateDeploymentSetting () {
      // 错误信息初始化
      this.errorTitle = ''
      this.errorMsgList = ''
      this.$refs['formBottom'].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        service({
          url: '/project/env/setting/deployment/other/',
          method: 'POST',
          data: {
            ...this.form,
            envId: this.envId
          }
        }).then(res => {
          if (Array.isArray(res)) {
            this.errorTitle = '升级配置失败'
            this.errorMsgList = res.map(item => ({
              ip: item.ip,
              cpu: item.cpu,
              'memory(M)': item.mem && item.mem / 1024 / 1024
            }))
            return
          }
          this.$message.success('保存成功')
          setTimeout(() => {
            this.getDockerSetting()
          }, 300)
        }, err => {
          console.log('err', err.msg)
        })
      })
    },
    getDockerSetting () {
      const envId = this.envId
      service({
        url: '/project/env/setting/deployment/get',
        method: 'POST',
        data: qs.stringify({ envId })
      }).then(res => {
        if (res) {
          let labels = res.labels || ''
          // 提出labels中的log_path
          const logPath = this.getLabelValue(labels, 'log_path')
          if (logPath) {
            res.logPath = logPath
            labels = labels.replace(",log_path=" + logPath, "")
            labels = labels.replace("log_path=" + logPath + ",", "")
            labels = labels.replace("log_path=" + logPath, "")
            res.labels = labels
          }
          this.form = {
            ...res
          }
        }
      })
    },
    getLabelValue (labels, key) {
      if (!labels || !key) {
        return ""
      }
      const arr = labels.split(",").map(it => it.split("=")).filter(it => it[0] === key)
      return arr[0] && arr[0][1] || ""
    }
  }
}
</script>

<style lang="scss" scoped>
.card {
  width: 80%;
  transition: 0.5s;
  margin-bottom: 16px;
  .title {
    font-size: 16px;
    color: #909399;
    margin-bottom: 12px;
    font-weight: bold;
    color: #f56c6c
  }
  .card-row {
    display: flex;
    align-items: center;
    background: #fef0f0;
    margin-bottom: 16px;
    padding-left: 10px;
    border-radius: 8px;
    &_entry {
      display: flex;
      margin-right: 20px;
      padding: 6px 8px;
      display: flex;
      justify-content: center;
      width: 100px;
      color: #f56c6c;
      .key {
        font-size: 15px;
        font-weight: 700
      }
      .value {
        font-size: 14px
      }
    }
  }
}
.card:hover {
  box-shadow: 15px 15px 15px 0 rgba(0,0,0,.1)
}
.v-enter,
.v-leave-to {
  opacity: 0;
  transform: translateX(150px)
}
.v-enter-active,
.v-leave-active {
  transition: all 0.8s
}
.v-enter-to  {
  opacity: 1;
  transform: translateX(0px)
}
</style>
