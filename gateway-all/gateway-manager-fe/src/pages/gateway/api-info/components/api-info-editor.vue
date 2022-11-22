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
    <el-dialog
        :title="title"
        :visible="apiInfoEditorVisible"
        @update:visible="updateApiInfoEditorVisible"
        @close="dialogClose"
        width="840px"
        :close-on-click-modal=false
        class="el-dialog-v-align-middle"
    >
      <el-form v-loading="loading" :model="form" :rules="rules" label-width="170px" ref="form" size="mini">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <!-- <el-form-item label="应用名" prop="application">
          <el-select v-model="form.application"
                     filterable
                     clearable
                     placeholder="请选择应用名"
                     style="width: 50%">
            <el-option
                v-for="item in appOptions"
                :key="item"
                :label="item"
                :value="item">
            </el-option>
          </el-select>
          <span class="tip-info"><em class="el-alert__icon el-icon-warning" /> 应用名请与mione中创建的项目名保持一致</span>
        </el-form-item> -->

        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="连接数限制" prop="invokeLimit">
          <el-input v-model="form.invokeLimit" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="使用并发限制" prop="useQpsLimit">
          <el-switch v-model="form.useQpsLimit"></el-switch>
        </el-form-item>

        <el-form-item v-if="form.useQpsLimit" label="并发限制(QPS)" prop="qpsLimit">
          <el-input v-model="form.qpsLimit" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="超时(毫秒)" prop="timeout">
          <el-input-number v-model="form.timeout" :min="1" :max="20000"></el-input-number>
        </el-form-item>

        <el-form-item label="HTTP方法" prop="httpMethod">
          <el-select v-model="form.httpMethod" placeholder="请选择方法">
            <el-option
              v-for='item in httpMethodOptions'
              :key='item.value'
              :label='item.label'
              :value='item.value'/>
          </el-select>
        </el-form-item>

        <el-form-item label="所属分组" prop="groupId">
          <el-select v-model="form.groupId" filterable placeholder="请选择所属分组">
            <el-option
                v-for="item in apiGroups"
                :key="item.id"
                :label="item.id + '-' + item.name"
                :value="item.id"
            ></el-option>
          </el-select>
          <el-button
              style="margin-left: 10px;"
              v-if="apiGroups.length == 0"
              @click="getApiGroups"
              icon="el-icon-refresh"></el-button>
        </el-form-item>

        <el-form-item prop="url">
          <span slot="label">
            请求路径
            <el-popover
                trigger="hover"
                width="100"
            >
              <div style="text-align:center;">
                查看文档:
                <a
                    style="color: #409EFF;"
                    href="http://"
                    type="primary"
                    target="_blank"
                    icon="el-icon-question"
                >
                  域名
                </a>
              </div>
              <em slot="reference" class="el-icon-question"></em>
            </el-popover>
          </span>
          <el-input v-model="form.url" autocomplete="off" @blur="urlBlueEvent">
            <template slot="prepend">{{baseUrl}}</template>
          </el-input>
        </el-form-item>

        <el-form-item label="路由类型" prop="routeType">
          <el-select v-model="form.routeType" placeholder="请选择方式">
            <el-option
              v-for='item in routeTypeOptions'
              :key='item.value'
              :label='item.label'
              :value='item.value'/>
          </el-select>
        </el-form-item>

        <template v-if="form.routeType === '3'">
          <el-form-item label>
            <el-transfer
                filterable
                filter-placeholder="搜索id或name"
                v-model="form.plugins"
                :titles="['你的数据源', '选中数据源']"
                :data="pluginsList"
            ></el-transfer>
          </el-form-item>
        </template>

        <el-form-item v-if="isComposeType" label="接口编排">
          <template>
            <codemirror style="line-height:25px" v-model="form.groupConfig" :options="cmOptions"></codemirror>
          </template>
          <el-button
              @click="getDag(form.groupConfig)"
              icon="el-icon-refresh"
              size="small"
              style="margin-left: 2px"
          >生成编排模型</el-button>
        </el-form-item>

        <!-- http -->
        <el-form-item key="http-type" v-if="isHttpType" label="目标路径" prop="path">
          <el-input v-model="form.path" autocomplete="off"></el-input>
        </el-form-item>

        <!-- http group
        <el-form-item key="http-type-group" v-if="isHttpType" label="目标路径 group" prop="httpGroup">
          <el-input v-model="form.httpGroup" autocomplete="off"></el-input>
        </el-form-item>
        -->

        <!-- dubbo -->
        <el-form-item v-if="isDubboType" label="服务名称" prop="serviceName">
          <el-input v-model="form.serviceName" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item v-if="isDubboType" label="方法名称" prop="methodName">
          <el-input v-model="form.methodName" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item v-if="isDubboType" label="服务分组" prop="serviceGroup">
          <el-input v-model="form.serviceGroup" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item v-if="isDubboType" label="服务版本" prop="serviceVersion">
          <el-input v-model="form.serviceVersion" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item v-if="isDubboType" label="参数模板" prop="paramTemplate">
          <template>
            <codemirror v-model="form.paramTemplate" :options="cmOptions"></codemirror>
          </template>
        </el-form-item>

        <el-form-item label="通用filter">
          <span slot="label">
            通用filter
            <el-popover
                trigger="hover"
                width="200"
            >
              <div style="text-align:center;">
                查看文档:
                <a style="color: #409EFF;"
                   href="https://"
                   target="_blank">
                  自定义filter汇总
                </a>
              </div>
              <em slot="reference" class="el-icon-question"></em>
            </el-popover>
          </span>
          <!-- <common-filter ref="commonFilter" v-model="form.filterParams"/> -->
        </el-form-item>

        <el-form-item label="是否离线" prop="offline">
          <el-switch v-model="form.offline"></el-switch>
        </el-form-item>

        <el-form-item label="是否支持Preview" prop="allowPreview">
          <el-switch v-model="form.allowPreview"></el-switch>
        </el-form-item>

        <el-form-item label="使用Token" prop="allowToken">
          <el-switch v-model="form.allowToken"></el-switch>
        </el-form-item>

        <el-form-item v-if="isToken" label="Token" prop="token">
          <el-input v-model="form.token" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="提取UID" prop="allowAuth">
          <el-switch v-model="form.allowAuth"></el-switch>
        </el-form-item>

        <el-form-item label="是否缓存" prop="allowCache">
          <el-switch v-model="form.allowCache"></el-switch>
        </el-form-item>

        <el-form-item v-if="isAllowCache" label="缓存过期时间(毫秒)" prop="cacheExpire">
          <el-input v-model="form.cacheExpire" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="是否基于IP防刷" prop="allowIpAntiBrush">
          <el-switch v-model="form.allowIpAntiBrush"></el-switch>
        </el-form-item>

        <el-form-item v-if="isAllowIpAntiBrush" label="基于IP防刷限制" prop="ipAntiBrushLimit">
          <el-input v-model="form.ipAntiBrushLimit" placeholder="n/每分钟" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="是否基于UID防刷" prop="allowUidAntiBrush">
          <el-switch v-model="form.allowUidAntiBrush"></el-switch>
        </el-form-item>

        <el-form-item v-if="isAllowUidAntiBrush" label="基于UID防刷限制" prop="uidAntiBrushLimit">
          <el-input v-model="form.uidAntiBrushLimit" placeholder="单个uid次数限制" autocomplete="off"></el-input>
        </el-form-item>

        <el-form-item label="是否打印日志" prop="allowLog">
          <el-switch v-model="form.allowLog"></el-switch>
        </el-form-item>

        <el-form-item label="是否允许跨域" prop="allowLog">
          <el-switch v-model="form.allowCors"></el-switch>
        </el-form-item>

        <el-form-item label="是否使用脚本" prop="allowScript" v-if="!hiddenUseScript">
          <el-switch v-model="form.allowScript" style="margin-right:20px"></el-switch>
        </el-form-item>

        <el-form-item v-if="isScript" label="脚本执行类型" prop="scriptType">
          <el-select v-model="form.scriptType" placeholder="请选择方式">
            <el-option label="直接执行" value="0"></el-option>
            <el-option label="前置" value="1"></el-option>
            <el-option label="后置" value="2"></el-option>
            <el-option label="包裹" value="3"></el-option>
            <el-option label="jar文件" value="4"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item v-if="isScript && form.scriptType === '4'" label="git group" prop="gitGroup">
          <el-input v-model="form.gitGroup" placeholder="请输入组名"></el-input>
        </el-form-item>

        <el-form-item v-if="isScript && form.scriptType === '4'" label="git name" prop="gitName">
          <el-input v-model="form.gitName" placeholder="请输入项目名"></el-input>
        </el-form-item>

        <el-form-item v-if="isScript && form.scriptType === '4'" label="main类" prop="gitName">
          <el-input v-model="form.entryClassName" placeholder="指定执行类名"></el-input>
        </el-form-item>

        <el-form-item v-if="isScript && form.scriptType === '4'" label="jar文件" prop="jar">
          <el-select v-model="form.jarName" placeholder="请选择jar包">
            <el-option
                v-for="item in jarGroups"
                :key="item.jarName"
                :label="item.jarName"
                :value="item.jarName">
            </el-option>
          </el-select>
          <el-button
              @click="getScriptJars"
              icon="el-icon-refresh"
              size="small"
              style="margin-left: 2px"
          >获取jar文件</el-button>
        </el-form-item>
        <el-form-item v-if="isScript && form.scriptType === '4'">
          <el-button @click="editserverLess" size="small">更新执行脚本</el-button>
        </el-form-item>
        <div v-if="form.scriptType !== '4'">
          <el-form-item v-if="isScript" label="git projectId" prop="gitProjectId">
            <el-input v-model="form.gitProjectId" autocomplete="off"></el-input>
          </el-form-item>

          <el-form-item v-if="isScript" label="git token" prop="gitToken">
            <el-input type="password" v-model="form.gitToken"></el-input>
          </el-form-item>

          <el-form-item v-if="isScript" label="git path" prop="gitPath">
            <el-input v-model="form.gitPath" autocomplete="off"></el-input>
          </el-form-item>

          <el-form-item v-if="isScript" label="git branch" prop="gitBranch">
            <el-input v-model="form.gitBranch" autocomplete="off"></el-input>
          </el-form-item>

          <el-form-item v-if="isScript" label="commits" prop="commit">
            <el-select
                v-model="form.commit"
                filterable
                placeholder="请选择提交"
                @change="getScriptFile(form)"
            >
              <el-option
                  v-for="item in commits"
                  :key="item.id"
                  :label="item.id + ' - [' + item.title + '][' + item.committed_date + ']'"
                  :value="item.id"
              ></el-option>
            </el-select>
            <el-button
                @click="getCommits(form)"
                icon="el-icon-refresh"
                size="small"
                style="margin-left: 2px"
            >获取commits</el-button>
          </el-form-item>

          <el-form-item v-if="isScript" label="脚本">
           <span slot="label">
            脚本
            <el-popover
                trigger="hover"
                width="100">
              <div style="text-align:center;">
                <a style="color: #409EFF;"
                   href="https://"
                   type="primary"
                   target="_blank"
                   icon="el-icon-question">点击查看脚本说明</a>
              </div>
              <em slot="reference" class="el-icon-question"></em>
            </el-popover>
          </span>
            <el-link v-if="title.startsWith('编辑')" type="primary" href="http://" target="_blank">点击编辑</el-link>
            <template v-else>
              <codemirror v-model="form.script" :options="common_cmOptions"></codemirror>
            </template>
          </el-form-item>

          <el-form-item v-if="isScript" label="脚本方法名" prop="scriptMethodName">
            <el-input v-model="form.scriptMethodName" autocomplete="off"></el-input>
          </el-form-item>

          <el-form-item v-if="isScript" label="脚本参数">
            <template>
              <codemirror v-model="form.scriptParams" :options="common_cmOptions"></codemirror>
            </template>
          </el-form-item>
        </div>

        <el-form-item label="是否Mock" prop="allowMock">
          <el-switch v-model="form.allowMock"></el-switch>
        </el-form-item>

        <el-form-item v-if="isMock" label="ContentType" prop="contentType">
          <el-select v-model="form.contentType" placeholder="请选择ContenType">
            <el-option label="application/json" value="application/json"></el-option>
            <el-option
                label="application/x-www-form-urlencoded"
                value="application/x-www-form-urlencoded"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item v-if="isMock" label="mock数据">
          <template>
            <codemirror style="line-height:25px;" v-model="form.mockData" :options="cmOptions"></codemirror>
          </template>
        </el-form-item>

        <el-form-item v-if="isMock" label="mock数据描述" prop="mockDataDesc">
          <el-input type="textarea" v-model="form.mockDataDesc"></el-input>
        </el-form-item>
        <el-form-item>
          <span slot="label">
            dubbo支持传递header
            <el-popover
                trigger="hover"
                width="230"
            >
              <div style="text-align:center;">
                dubbo路由方式的情况下，支持通过RpcContext方式向下传递header
              </div>
              <em slot="reference" class="el-icon-question"></em>
            </el-popover>
          </span>
          <el-switch v-model="form.allowHeaders"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="onCancel" size="mini">取 消</el-button>
        <el-button type="primary" @click="onSubmit" size="mini">确 定</el-button>
      </div>
    </el-dialog>
    <!-- API编排绘制 -->
    <template>
      <el-dialog
          title="API编排"
          :visible.sync="viewApiComposeDialogVisible"
          @close="handleDagClose"
          width="80vw"
          :close-on-click-modal=false
      >
        <el-card shadow="never" class="d2-mb">
          <json-dag
              v-if="viewApiComposeDialogVisible"
              :value="dagData"
              @jsonDagUpdate="updateJsonHandler"
          ></json-dag>
        </el-card>
      </el-dialog>
    </template>
  </div>
</template>
<style lang="scss" scoped>
.tip-info {
  display: inline-block;
  margin: 0 10px;
  font-size: 12px;
  color: #909399;
  em {
    display: inline-block;
    vertical-align: -1px;
  }
}
</style>
<script>
import { mapState } from 'vuex'
import bizutil from '@/common/bizutil'
import service from '@/plugin/axios'
// import CommonFilter from './common-filter'
import { routeType as routeTypeOptions, httpMethod as httpMethodOptions } from '../info_map'

export default {
  name: 'api-info-editor',
  // components: {
  //   CommonFilter
  // },
  data () {
    // // 校验器
    // var urlChecker = (rule, value, callback) => {
    //   value = String(value).toLowerCase()
    //   const rurl = /^(\w+\|)?https?:\/\//
    //   if (!value || value === 'undefined') {
    //     return callback(new Error('URL不能为空'))
    //   } else if (!rurl.test(value)) {
    //     return callback(new Error('URL格式错误'))
    //   } else {
    //     return callback()
    //   }
    // }
    var numberChecker = (rule, value, callback) => {
      if (
        String(value).indexOf('.') !== -1 ||
          isNaN(value) ||
          !Number.isInteger(parseInt(value))
      ) {
        return callback(new Error('请输入整数值'))
      }
      return callback()
    }
    return {
      apiGroups: [],
      viewApiComposeDialogVisible: false,
      dagData: {},
      commits: [],
      pluginsList: [],
      jarGroups: [],
      routeTypeOptions,
      httpMethodOptions,
      rules: {
        name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        application: [{ pattern: /^[A-Za-z0-9_-]+$/, message: '请输入英文、数字、下划线或中划线', trigger: 'blur' }],
        // description: [
        //   { required: true, message: '请输入描述', trigger: 'blur' }
        // ],

        url: [{ required: true, message: '请输入请求路径', trigger: 'blur' }],

        httpMethod: [
          { required: true, message: '请选择请求方法', trigger: 'change' }
        ],

        groupId: [
          { required: true, message: '请选择所属分组', trigger: 'change' }
        ],

        routeType: [
          { required: true, message: '请选择路由方式', trigger: 'change' }
        ],

        // invokeLimit: [
        //     { required: true, validator: numberChecker, trigger: 'blur' }
        // ],

        qpsLimit: [
          { required: true, validator: numberChecker, trigger: 'blur' }
        ],

        // timeout: [
        //     { required: true, validator: numberChecker, trigger: 'blur' }
        // ],

        path: [{ required: true, message: '请输入目标路径', trigger: 'blur' }],

        serviceName: [
          { required: true, message: '请输入服务名称', trigger: 'blur' }
        ],

        methodName: [
          { required: true, message: '请输入方法名称', trigger: 'blur' }
        ],

        token: [{ required: true, message: '请输入token', trigger: 'blur' }],

        contentType: [
          { required: true, message: '请选择ContentType', trigger: 'change' }
        ],

        scriptType: [
          { required: true, message: '请选择脚本执行类型', trigger: 'change' }
        ]
      },
      cmOptions: {
        tabSize: 4,
        indentUnit: 4,
        // mode: 'text/javascript',
        theme: 'base16-dark',
        lineNumbers: true,
        line: true,
        smartIndent: true,
        // json校验
        mode: 'application/json',
        gutters: ['CodeMirror-lint-markers'],
        lint: true
      },
      common_cmOptions: {
        tabSize: 4,
        indentUnit: 4,
        mode: 'text/javascript',
        theme: 'base16-dark',
        lineNumbers: true,
        line: true,
        smartIndent: true
      },
      // appOptions: [], // 应用名下拉数据
      hiddenUseScript: false // 是否隐藏是否使用脚本
    }
  },
  props: {
    title: {
      type: String,
      default: ''
    },
    loading: {
      type: Boolean,
      default: false
    },
    form: {
      type: Object,
      default: () => ({})
    },
    apiInfoEditorVisible: {
      type: Boolean,
      default: false
    },
    width: {
      type: String,
      default: '800px'
    }
  },
  computed: {
    ...mapState('d2admin/dealUserInfo', ['userInfo']),
    baseUrl () {
      const groupId = this.form.groupId
      const apiGroups = this.apiGroups
      return ((apiGroups || []).find(item => item.id === groupId) || {}).baseUrl || ''
    },
    isHttpType () {
      return this.form.routeType === '0'
    },
    isDubboType () {
      return this.form.routeType === '1' || this.form.routeType === '4'
    },
    isComposeType () {
      return this.form.routeType === '2'
    },
    isMock () {
      return this.form.allowMock === true
    },
    isScript () {
      return this.form.allowScript === true && !this.hiddenUseScript
    },
    isToken () {
      return this.form.allowToken === true
    },
    isAllowCache () {
      return this.form.allowCache === true
    },
    isAllowIpAntiBrush () {
      return this.form.allowIpAntiBrush === true
    },
    isAllowUidAntiBrush () {
      return this.form.allowUidAntiBrush === true
    }
  },
  created () {
    this.getApiGroups()
    this.getPliuginList()
    // this.getAppOption() // 获取应用名下拉数据
    this.getGatewayCustomConfig() // 获取gateway自定义配置
  },
  methods: {
    // 获取gateway自定义配置
    getGatewayCustomConfig () {
      service({
        url: '/custom/config/get?key=gateway',
        method: 'get'
      }).then((res) => {
        const data = JSON.parse(res.content)
        this.hiddenUseScript = data && data.hidden_api_admin_use_script // 是否隐藏是否使用脚本
      })
    },
    getScriptJars () {
      if (this.form.gitName === undefined || this.form.gitGroup === undefined) return
      service({
        url: `/cloud/build/jar/list?gitName=${this.form.gitName}&&gitGroup=${this.form.gitGroup}&&page=1&&pageSize=10`,
        method: 'GET',
        data: {
          gitName: this.form.gitName,
          gitGroup: this.form.gitGroup,
          page: 1,
          pageSize: 10
        }
      }).then(res => {
        this.jarGroups = res || []
      })
    },
    editserverLess () {
      service({
        url: `apiinfo/modify/jarscript`,
        method: 'post',
        data: {
          id: this.form.id,
          jarName: this.form.jarName,
          gitGroup: this.form.gitGroup,
          gitName: this.form.gitName,
          entryClassName: this.form.entryClassName,
          groupId: this.form.groupId
        }
      }).then(res => {
        console.log(res)
      }).catch((res) => {
        this.$message.error(res)
      })
    },
    getApiGroups () {
      service({
        url: '/apigroup/listall',
        method: 'get',
        data: {}
      }).then(res => {
        var apiGroups = res.groupList || []
        this.apiGroups = apiGroups.map(item => {
          return {
            ...item,
            ctime: bizutil.timeFormat(item.ctime),
            utime: bizutil.timeFormat(item.utime)
          }
        })
      })
    },
    // 获取提交历史
    getCommits (param) {
      service({
        url: '/gitlab/commits',
        method: 'post',
        data: {
          projectId: param.gitProjectId,
          token: param.gitToken,
          path: param.gitPath,
          branch: param.gitBranch
        }
      })
        .then(res => {
          this.commits = res.commits
          if (this.commits && this.commits.length) {
            this.form.commit = this.commits[0].id
            this.getScriptFile(this.form)
          }
        })
        .catch(() => {})
        .finally(() => {})
    },
    // 获取脚本文件
    getScriptFile (param) {
      service({
        url: '/gitlab/file',
        method: 'post',
        data: {
          projectId: param.gitProjectId,
          token: param.gitToken,
          path: param.gitPath,
          branch: param.commit
        }
      })
        .then(res => {
          this.form.script = res.content
        })
        .catch(() => {})
        .finally(() => {})
    },
    getPliuginList () {
      service({
        url: '/ds/list',
        method: 'get'
      }).then(({ list = [] }) => {
        this.pluginsList = list.map(item => {
          return {
            key: item.id,
            label: `(${item.id})${item.name}`
          }
        })
      })
    },
    getDag (param) {
      this.dagData = param || ''
      this.viewApiComposeDialogVisible = true
    },
    // tips: /xxxxx/开头的url前面不加bashUrl
    handleValidate () {
      let baseUrl = this.baseUrl
      if (baseUrl == null || baseUrl === '') {
        this.$message({
          message: '所属分组为空, 请刷新',
          type: 'warning'
        })
        return
      }
      const form = this.form
      this.urlBlueEvent() // 请求路径，baseUrl最后一个字符是/，输入时第一个字符如果是/就删除
      baseUrl = baseUrl && baseUrl.endsWith('/') ? baseUrl : `${baseUrl}/`
      const data = {}
      let url = '/apiinfo/new'
      let showMessage = '添加成功'
      if (form.id) {
        data.id = form.id
        url = '/apiinfo/update'
        showMessage = '编辑成功'
      }

      const formUrl = form.url
      service({
        url: '/apiinfo/urlexist',
        method: 'post',
        data: {
          ...data,
          url: formUrl.startsWith('/xxxx/') ? formUrl : `${baseUrl}${formUrl}`
        }
      }).then(res => {
        if (res) {
          this.$message({
            message: '该请求路径已经存在，请更换',
            type: 'warning'
          })
          return
        }

        form[form.id ? 'updatorId' : 'creatorId'] = this.userInfo.uuid
        if (form.plugins && form.plugins.length !== 0) {
          form.dsIds = form.plugins.join(',')
        } else {
          form.dsIds = ''
        }

        service({
          url,
          method: 'post',
          data: {
            ...form,
            url: formUrl.startsWith('xxxx') ? formUrl : `${baseUrl}${formUrl}`,
            filterParams: this.stringifyFilterParams(form.filterParams || [])
          }
        }).then(res => {
          this.$message.success(showMessage)
          this.$emit('submitSuccess')
          this.$emit('update:apiInfoEditorVisible', false)
        }).catch((res) => {
          this.$message.error(res)
        })
      }).catch((res) => {
        this.$message.error(res)
      })
    },
    stringifyFilterParams (params) {
      return JSON.stringify(params.map(item => {
        const newItem = { ...item }
        // 避免响应式数据导致不必要的更新
        newItem.params = { ...item.params }
        newItem.stringifyParams.forEach((key) => {
          newItem.params[key] = JSON.stringify(newItem.params[key])
        })
        return newItem
      }))
    },
    onSubmit () {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        // this.$refs.commonFilter.$refs.commonFilterForm.validate(valid => { // 交验通用过滤器
        //   if (!valid) {
        //     this.$message({
        //       message: '请检查参数',
        //       type: 'warning'
        //     })
        //     return false
        //   }
        //   this.handleValidate()
        // })
        this.handleValidate()
      })
    },
    onCancel () {
      const form = this.form
      this.$emit('update:apiInfoEditorVisible', false)
      this.$emit('cancel', form)
      this.$message({
        message: form.id ? '取消编辑' : '取消新增',
        type: 'warning'
      })
    },
    dialogClose (...args) {
      this.$emit('apiInfoEditorClose', args)
    },
    updateApiInfoEditorVisible (newApiInfoEditorVisible) {
      this.$emit('update:apiInfoEditorVisible', newApiInfoEditorVisible)
    },
    handleDagClose () {
      this.viewApiComposeDialogVisible = false
    },
    updateJsonHandler (str) {
      this.$set(this.form, 'groupConfig', str)
      this.viewApiComposeDialogVisible = false
    },
    // 获取应用名下拉数据
    // getAppOption () {
    //   service({
    //     url: '/project/getApplicationNames',
    //     method: 'get',
    //     data: {}
    //   }).then(res => {
    //     this.appOptions = res // 应用名下拉数据
    //   })
    // },
    // 输入请求路径时，baseUrl最后一个字符是/，输入时第一个字符如果是/就删除
    urlBlueEvent () {
      if (this.baseUrl.substr(-1) === '/' && this.form.url.substr(0, 1) === '/') {
        this.form.url = this.form.url.substr(1, this.form.url.length)
      }
    }
  }
}
</script>
