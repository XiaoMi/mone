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
      <el-alert
        v-if="alertInfo.isShow"
        title="资源配置待优化,请调整配置"
        type="warning"
        :description="alertInfo.text"
        show-icon>
      </el-alert>
      <div class="header">
        <el-form inline size="mini" label-width="56px" style='height:28px'>
          <el-form-item :model="form" label="副本数">
            <el-input v-model="form.replicate"/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="scale">更新</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refresh">刷新</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="checkScaleNum">查看可扩容副本数</el-button>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="toLogPage">查看日志</el-button>
          </el-form-item>
        </el-form>
        <el-button
          v-if="healthStatus == 0"
          @click="closeHealthCheck"
          type="danger"
          size='mini'>关闭监测</el-button>
        <el-button
          v-else-if="healthStatus == 1"
          @click="openHealthCheck"
          type="success"
          size="mini">开启监测</el-button>
      </div>
      <div class='totalQps' v-if="totalQps!=0">总Qps : {{totalQps}}</div>
      <el-table stripe :data='tableList' class='table-list docker-current-deploy-table'>
        <el-table-column label='机器名' prop='name' width='160'></el-table-column>
        <el-table-column label='ip' prop='ip' width='130'></el-table-column>
        <el-table-column label='qps' prop='healthQps' width='110'></el-table-column>
        <el-table-column label='健康状态' width='100'>
          <template slot-scope="scope">
            <el-tag v-if='scope.row.healthState === 0' size='mini' style="width:60px" type='success'>健康</el-tag>
            <el-tag v-if='scope.row.healthState === 1' size='mini' style="width:60px" type='danger'>挂掉</el-tag>
          </template>
        </el-table-column>
        <el-table-column label='hostname' prop='hostname' width='160'></el-table-column>
        <el-table-column label='操作' fixed='right' width='320'>
          <template slot-scope="scope">
            <el-button
               class='danger'
               size='mini'
               type='danger'
               @click="machineOnline(scope.row)">上线</el-button>
            <el-button
               class='danger'
               size='mini'
               type='danger'
               @click="machineOffine(scope.row)">下线</el-button>
            <el-button
               class='danger'
               size='mini'
               type='danger'
               @click="machineNuke(scope.row)">nuke</el-button>
            <el-button
              v-if="isSuperuser"
              size="mini"
              class='danger'
              type='danger'
              @click="showDriftDialog(scope.row)">手动漂移</el-button>
            <el-dropdown class="el-dropdown-styled"  size="mini"  @command="commandHandler($event,scope.row)">
                    <el-button class="el-button--blue" >
                      更多<i class="el-icon-arrow-down el-icon--right"></i>
                    </el-button>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item command="logSnapshot">日志快照</el-dropdown-item>
                      <el-dropdown-item command="showResource">查看资源</el-dropdown-item>
                      <el-dropdown-item command="showUtilRate"  :disabled='rateDisabled'>使用率</el-dropdown-item>
                      <el-dropdown-item command="showUsage" >负载状态</el-dropdown-item>
                      <el-dropdown-item command="showMoreInfo">更多信息</el-dropdown-item>
                      <el-dropdown-item v-if="isSuperuser" command="powerOn">开机</el-dropdown-item>
                      <el-dropdown-item v-if="isSuperuser" command="powerOff">关机</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
             <!-- <el-button
               size='mini'
               @click='showUtilRate(scope.row)'>查看资源</el-button>
            <el-button
               size='mini'
               :disabled='rateDisabled'
               @click='showUtilRate(scope.row)'>使用率</el-button>
            <el-button
               size='mini'
               @click='showMoreInfo(scope.row)'>更多信息</el-button> -->
          </template>
        </el-table-column>
      </el-table>

      <el-dialog title='详细信息' :visible.sync='dialogInfoVisible' width='800px' :before-close="dialogClose">
        <div>
          <div class='codeMirror_title'>部署详情:</div>
          <codemirror v-model="codeMirrorMachine" :options="codeMirrorOptions" class='codeMirror_content'/>
        </div>
        <div>
          <div class='codeMirror_title'>健康检查:</div>
          <codemirror v-model="codeMirrorHealth" :options="codeMirrorOptions" class='codeMirror_content'/>
        </div>
      </el-dialog>
      <el-dialog title='资源信息' :visible.sync='dialogResourceVisiable' width='800px' :before-close="dialogReourceClose">
       <codemirror v-model="codeMirrorResource" :options="codeMirrorOptions" class='codeMirror_content'/>
      </el-dialog>

      <el-dialog title='应用漂移' :visible.sync='showDriftForm' width='800px'>
        <el-form label-width="100px" size="mini" :model="driftForm" :rules="ruleDriftForm" ref="ruleDriftForm">
          <el-form-item label="环境">
            <el-input disabled v-model="driftForm.envId"></el-input>
          </el-form-item>
          <el-form-item label="要迁移ip">
            <el-input disabled v-model="driftForm.ip"></el-input>
          </el-form-item>
          <el-form-item label="指定目标ip" prop="targetIp">
            <el-input v-model="driftForm.targetIp"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="danger" @click="machineDrift('ruleDriftForm')">漂移</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      <el-dialog
        title='负载'
        :visible.sync='dialogUsageVisible'
        @open='dialogUtilRateOpen'
        @close='dialogUtilRateClose'
        width='800px'>
        <!-- <div class='loading_deal' v-if='loading'>
          <i class='el-icon-loading'/>
          <span>图表绘制中...</span>
        </div> -->
        <div class='usage-chart'>
          <div class='usage-mem-chart' ref='usageMemChart'></div>
          <div class='usage-cpu-chart' ref='usageCpuChart'></div>
        </div>
      </el-dialog>
      <el-dialog
        title='使用率'
        :visible.sync='dialogUtilRateVisible'
        @open='dialogUtilRateOpen'
        @close='dialogUtilRateClose'
        width='800px'>
        <div class='loading_deal' v-if='loading'>
          <i class='el-icon-loading'/>
          <span>图表绘制中...</span>
        </div>
        <div class='content_chart' v-if='dialogUtilRateVisible'>
          <div class='cpu_chart' ref='cpuChart'></div>
          <div class='memory_chart' ref='memoryChart'></div>
        </div>
      </el-dialog>
      <el-dialog title='日志快照' :visible.sync='dialogLogVisible' width='800px' :before-close="dislogClose">
        <codemirror v-model="codeMirrorContent" :options="codeMirrorOptions1"/>
      </el-dialog>
    </div>
</template>
<script>
import request from '@/plugin/axios/index'
import qs from 'qs'
import 'codemirror/mode/javascript/javascript.js'
import 'codemirror/theme/base16-dark.css'

// import echarts from 'echarts'
// import 'echarts/lib/chart/gauge'

// import 'echarts/lib/chart/line'
// import 'echarts/lib/component/tooltip'

import echarts from 'echarts'

import { config, usageConfig } from './gaugeChart-config'

const ALERT_SECTION = {
  MIN: 0.2,
  MAX: 0.8
}
const isSuperuser = (window.userInfo.roles || []).find(item => item.name === 'SuperRole')
export default {
  name: 'current-docker',
  data () {
    return {
      isSuperuser: !!isSuperuser,
      form: { replicate: 0 },
      showDriftForm: false,
      driftForm: {
        envId: 0,
        ip: '',
        targetIp: ''
      },
      ruleDriftForm: {
        targetIp: [
          { pattern: /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/, message: 'ip格式[127.0.0.1]', trigger: 'blur' }
        ]
      },
      healthStatus: -1,
      tableList: [],
      tableCostList: [],
      dialogResourceVisiable: false,
      dialogInfoVisible: false,
      dialogUsageVisible: false,
      dialogLogVisible: false,
      codeMirrorResource: '',
      codeMirrorMachine: '',
      codeMirrorHealth: '',
      codeMirrorContent: '',
      codeMirrorOptions: {
        tabSize: 2,
        indentUnit: 2,
        mode: 'text/javascript',
        theme: 'base16-dark',
        readOnly: 'nocursor',
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
      codeMirrorOptions1: {
        tabSize: 2,
        indentUnit: 2,
        mode: 'text/javascript',
        theme: 'base16-dark',
        readOnly: 'nocursor',
        lineNumbers: true,
        line: true,
        smartIndent: true,
        // lineSeparator : '\n',
        lineWrapping: true
      },
      dialogUtilRateVisible: false,
      ip: '',
      timer: 0,
      rateDisabled: false,
      cpuCount: 1,
      loading: true,
      alertInfo: {
        isShow: false,
        cpu: 0,
        cpuUsage: '0%',
        text: ''
      },
      totalQps: 0
    }
  },
  created () {
    this.getInfo()
    try {
      this.getCpuAndMemoryAlertInfo()
    } catch (error) {

    }
    this.getHealthStatus()
  },
  props: {
    projectId: {
      type: [Number, String],
      required: true
    },
    envId: {
      type: [String, Number],
      required: true
    }
  },
  methods: {
    getUsage () {
      request({
        url: `/onSiteInspection/getDailyUsage?envId=${this.envId}`
        // url: `/onSiteInspection/getDailyUsage?envId=105`
      })
        .then(res => {
          this.showUsage(res)
        })
    },
    getCpuAndMemoryAlertInfo () {
      request({
        url: `/project/getCpuAndMemoryAlertInfo?envId=${this.envId}`
      })
        .then(res => {
          //  res={"dockerInfo":{"serverVersion":"19.03.8","status":["2.08%","88.86%"]},"cpu":4,"envId":141}
          if (res) {
            this.fixAlertData(res)
          }
        })
    },
    fixAlertData (data) {
      if (!data.dockerInfo || !data.dockerInfo.status || data.dockerInfo.status.length < 2) {
        this.alertInfo.isShow = false
        return
      }
      let cpuAndMemory = data.dockerInfo.status
      this.alertInfo.cpu = parseInt(data.cpu)
      this.alertInfo.cpuUsage = this.getDecimal(cpuAndMemory[0])
      this.alertInfo.memory = this.getDecimal(cpuAndMemory[1])
      this.isShowAlert()
    },
    isShowAlert () {
      let text = ''
      let flag = false
      if (this.alertInfo.cpu < 1 || isNaN(this.alertInfo.cpu)) {
        this.alertInfo.cpu = 1
      }
      let singleCpuUsage = this.alertInfo.cpuUsage / this.alertInfo.cpu

      if (singleCpuUsage <= ALERT_SECTION.MIN) {
        if (this.alertInfo.cpu === 1) return
        text += `cpu核数:${this.alertInfo.cpu}, 使用率${this.getPercent(singleCpuUsage * this.alertInfo.cpu)},单核低于20%\n`
        flag = true
      }
      if (singleCpuUsage >= ALERT_SECTION.MAX) {
        text += `cpu核数:${this.alertInfo.cpu}, 使用率${this.getPercent(singleCpuUsage * this.alertInfo.cpu)},单核高于80%\n`
        flag = true
      }
      if (this.alertInfo.memory <= ALERT_SECTION.MIN) {
        text += `内存当前使用率${this.getPercent(this.alertInfo.memory)},低于20%\n`
        flag = true
      }
      if (this.alertInfo.memory >= ALERT_SECTION.MAX) {
        text += `内存当前使用率${this.getPercent(this.alertInfo.memory)},高于80%\n`
        flag = true
      }
      this.alertInfo.text = text
      this.alertInfo.isShow = flag
    },
    getDecimal (str) {
      if (str === '0' || str === '0.00%') return 0
      if (str.indexOf('%' > -1)) {
        return +(str.replace('%', '') / 100).toFixed(4)
      }
    },
    getPercent (decimalNum) {
      return (decimalNum * 100).toFixed(2) + '%'
    },
    getInfo () {
      request({
        url: '/project/env/current/docker',
        method: 'post',
        data: qs.stringify({
          envId: this.envId
        })
      }).then(res => {
        if (res) {
          this.form.replicate = res.replicate
          this.cpuCount = res.cpu || 1
          let dockerMachineList = res.deployInfo && res.deployInfo.dockerMachineList || []
          let healthResultList = res.healthResult && res.healthResult.serviceInfoList || []
          this.tableList = dockerMachineList.map((machineItem) => {
            let healthResult = healthResultList.find(item => item.ip === machineItem.ip) || {}
            return Object.assign({}, {
              healthGroup: healthResult.group,
              healthIp: healthResult.ip,
              healthQps: healthResult.qps,
              healthSerName: healthResult.serviceName,
              healthState: healthResult.status,
              healthType: healthResult.type
            }, machineItem)
          })
          this.totalQps = 0
          this.tableList.forEach((it) => {
            if (it.healthQps !== undefined) {
              this.totalQps = it.healthQps + Number(this.totalQps)
            }
          })
        }
      })
    },
    showUtilRate (info) {
      this.loading = true
      this.ip = info.ip
      this.dialogUtilRateVisible = true
    },
    showUsage (info) {
      this.dialogUsageVisible = true
      this.drawUsageChart(info)
    },
    dialogUtilRateOpen () {
      this.$nextTick(() => {
        this.getUtilRate()
      })
    },
    dialogUtilRateClose () {
      clearTimeout(this.timer)
      this.rateDisabled = true
      setTimeout(() => {
        this.rateDisabled = false
      }, 2500)
    },
    showLogSnapshot (msg) {
      this.dialogLogVisible = true
      this.codeMirrorContent = '加载中...'
      let ip = msg.ip
      request({
        url: `/project/env/log/snapshot`,
        method: 'post',
        data: qs.stringify({
          envId: this.envId,
          ip: ip
        })
      })
        .then(res => {
          this.codeMirrorContent = res
        })
    },
    dislogClose () {
      this.codeMirrorContent = ''
      this.dialogLogVisible = false
    },
    getUtilRate () {
      if (this.dialogUtilRateVisible === false) {
        clearTimeout(this.timer)
        return
      }
      request({
        url: '/project/env/current/dockerUsageRate',
        method: 'POST',
        data: qs.stringify({
          envId: this.envId,
          ip: this.ip
        })
      }).then(res => {
        let status = res && res.status
        if (!Array.isArray(status)) {
          this.loading = false
          this.$message.error('数据获取错误~')
          return
        };
        this.cpuValue = +(status[0].slice(0, status[0].indexOf('%')))
        this.memoryValue = +(status[1].slice(0, status[1].indexOf('%')))
        this.dialogUtilRateVisible && this.drawChart()
        this.loading = false
      })
    },

    drawChart () {
      let cpuChart = echarts.init(this.$refs.cpuChart)
      cpuChart.setOption(config({
        name: 'CPU使用率',
        value: this.cpuValue,
        cpuCount: this.cpuCount * 100
      }))

      let memoryChart = echarts.init(this.$refs.memoryChart)
      memoryChart.setOption(config({
        name: '内存使用率',
        value: this.memoryValue
      }))

      this.timer = setTimeout(() => {
        this.getUtilRate()
      }, 1000)
    },
    drawUsageChart (list) {
      if (!list || list.length === 0) {
        return
      }
      let filtedList = list.filter(it => it.cpuUsage !== 0 || it.memoryUsage !== 0)
      this.$nextTick(() => {
        let usageMemChart = echarts.init(this.$refs.usageMemChart)
        let usageCpuChart = echarts.init(this.$refs.usageCpuChart)
        usageMemChart.setOption(usageConfig(filtedList, true))
        usageCpuChart.setOption(usageConfig(filtedList))
      })
    },
    showMoreInfo (info) {
      let { appDeployStatus, cpuCore, ctime, desc, failNum, group, hostname, id, ip, labels, name, prepareLabels, status, step, time, utime, version } = info
      let codeMirrorMachine = { appDeployStatus, cpuCore, ctime, desc, failNum, group, hostname, id, ip, labels, name, prepareLabels, status, step, time, utime, version }
      this.codeMirrorMachine = JSON.stringify(codeMirrorMachine, null, 4)

      let { healthGroup, healthIp, healthQps, healthSerName, healthState, healthType } = info
      let codeMirrorHealth = { group: healthGroup, ip: healthIp, qps: healthQps, serviceName: healthSerName, status: healthState, type: healthType }
      this.codeMirrorHealth = JSON.stringify(codeMirrorHealth, null, 4)

      this.dialogInfoVisible = true
    },
    dialogClose () {
      this.codeMirrorMachine = ''
      this.codeMirrorHealth = ''
      this.dialogInfoVisible = false
    },
    dialogReourceClose () {
      this.codeMirrorResource = ''
      this.dialogResourceVisiable = false
    },
    refresh () {
      this.getInfo()
    },
    openHealthCheck () {
      request({
        url: '/project/env/health/check/init',
        method: 'post',
        data: qs.stringify({
          envId: this.envId
        })
      }).then(isSuccess => {
        if (isSuccess) {
          this.getHealthStatus()
          this.$message({
            type: 'success',
            message: '打开监控成功'
          })
        }
      })
    },
    getHealthStatus () {
      request({
        url: '/project/env/health/check/status',
        method: 'post',
        data: qs.stringify({
          envId: this.envId
        })
      }).then(status => {
        this.healthStatus = status
      })
    },
    closeHealthCheck () {
      request({
        url: '/project/env/health/check/clear',
        method: 'post',
        data: qs.stringify({
          envId: this.envId
        })
      }).then(isSuccess => {
        if (isSuccess) {
          this.getHealthStatus()
          this.$message({
            type: 'success',
            message: '关闭监控成功'
          })
        }
      })
    },
    scale () {
      const form = this.form
      request({
        url: '/pipeline/docker/scale',
        method: 'post',
        data: qs.stringify({
          projectId: this.projectId,
          envId: this.envId,
          replicate: form.replicate
        })
      }).then(bool => {
        if (bool) {
          this.$message.success('操作成功')
          this.getInfo()
        }
      })
    },
    checkScaleNum () {
      request({
        url: '/pipeline/docker/scaleNum',
        method: 'post',
        data: qs.stringify({
          projectId: this.projectId,
          envId: this.envId
        })
      }).then(res => {
        this.$alert(`可扩容副本数: ${res && res.num || 0}`, '可扩容副本数', {
          confirmButtonText: '确定'
        })
      })
    },
    showDriftDialog (item) {
      this.showDriftForm = true
      this.driftForm = {
        envId: this.envId,
        ip: item.ip,
        targetIp: ''
      }
    },
    machineDrift (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          const driftForm = this.driftForm
          this.$confirm(`此操作将应用从${driftForm.ip}漂移到其他机器, 是否继续?`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            request({
              url: '/project/env/docker/drift',
              method: 'POST',
              data: qs.stringify({
                envId: driftForm.envId,
                ip: driftForm.ip,
                targetIp: driftForm.targetIp
              })
            }).then(isSuccess => {
              if (isSuccess) {
                this.$message.success('手动漂移成功')
                this.refresh()
              } else {
                this.$message.error('漂移失败')
              }
            })
          }).catch(() => {
            this.$message({
              type: 'info',
              message: '已取消操作'
            })
          })
        } else {
          return false
        }
      })
    },
    machineNuke (item) {
      this.$confirm('此操作将下线及删除应用, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: '/project/env/docker/nuke',
          method: 'post',
          data: qs.stringify({
            envId: this.envId,
            ip: item.ip
          })
        }).then(isSuccess => {
          if (isSuccess) {
            this.$message.success('nuke成功, 需刷新页面')
          } else {
            this.$message.error('nuke失败')
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        })
      })
    },
    machineOnline (item) {
      this.$confirm('此操作将触发上线动作, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: '/project/env/docker/online',
          method: 'post',
          data: qs.stringify({
            envId: this.envId,
            ip: item.ip
          })
        }).then(isSuccess => {
          if (isSuccess) {}
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        })
      })
    },
    machineOffine (item) {
      this.$confirm('此操作将触发下线动作, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request({
          url: '/project/env/docker/shutDown',
          method: 'post',
          data: qs.stringify({
            envId: this.envId,
            ip: item.ip
          })
        }).then(isSuccess => {
          if (isSuccess) {}
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        })
      })
    },
    commandHandler (cmd, row) {
      switch (cmd) {
        case 'logSnapshot':
          this.showLogSnapshot(row)
          break
        case 'showResource':
          this.showResource(row)
          break
        case 'showUtilRate':
          this.showUtilRate(row)
          break
        case 'showUsage':
          this.getUsage(row)
          break
        case 'showMoreInfo':
          this.showMoreInfo(row)
          break
        case 'powerOn':
          this.powerOn(row)
          break
        case 'powerOff':
          this.powerOff(row)
          break
        default:break
      }
    },
    showResource (row) {
      request({
        url: `/resource/getByIp?ip=${row.ip}`,
        method: 'POST'
      })
        .then(res => {
          try {
            this.codeMirrorResource = JSON.stringify(res, null, 2)
          } catch (error) {
            this.$message.warn('数据解析失败')
          }
        })
      this.dialogResourceVisiable = true
    },
    powerOn (row) {
      this.$confirm(`确定开机?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          request({
            url: `/project/env/power/on?ip=${row.ip}&envId=${this.envId}`,
            method: 'get'
          }).then(res => {
            this.$message.success('开机成功')
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消'
          })
        })
    },
    powerOff (row) {
      this.$confirm(`确定关机?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          request({
            url: `/project/env/power/off?ip=${row.ip}&envId=${this.envId}`,
            method: 'get'
          }).then(res => {
            this.$message.success('关机成功')
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消'
          })
        })
    },
    toLogPage () {
      this.$router.push(`/application/log/view/${this.projectId}/${this.envId}`)
    }
  }
}
</script>
<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  margin-top: 10px;
}
.totalQps {
  margin-bottom: 15px;
  font-size: 14px;
  margin-left: 2px;
}
.d2-layout-header-aside-group .table-list .el-button.danger {
  background-color: #F56C6C;
  border-color: #F56C6C;
  margin-top: 2px;
  margin-bottom: 2px;
}
.codeMirror_title {
  font-size: 16px;
  color: #909399;
  font-weight: 700;
  margin-bottom: 10px;
}
.codeMirror_content {
  margin-left: 16px;
  margin-bottom: 20px;
}
.content_chart {
  display: flex;
  justify-content: space-around;
  align-items: center;
  .cpu_chart {
    width: 48%;
    height: 400px;
  }
  .memory_chart {
    width: 48%;
    height: 400px;
  }
}
.loading_deal {
  height: 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #05A9F3;
  i {
    font-size: 20px;
  }
  span {
    margin-top: 10px;
  }
}
.el-dropdown-styled{
  margin-left: 10px;
}
.usage-chart{
   height: 620px;
   .usage-mem-chart,.usage-cpu-chart{
     height: 300px;
   }
   .usage-cpu-chart{
     margin-top: 15px;
   }
}
</style>
<style lang="scss">
.docker-current-deploy-table{
  .el-table__fixed-right{
   height: 100% !important;
  }
}
</style>
