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
  <d2-container id="dc1">
    <d2-module margin-bottom>
      <div class="header">
        <div class="header-select">
          <el-form inline size="mini">
            <el-form-item>
              <div>选择项目：</div>
            </el-form-item>
            <el-form-item>
              <el-select
                @change="changeShowAll"
                v-model="showAll">
                <el-option
                  v-if="isSuperuser"
                  label="全部项目"
                  :value="true">
                </el-option>
                <el-option
                  label="我的项目"
                  :value="false">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item style="font-size: 14px; color: #909399">
              <el-select
                v-model="projectId"
                placeholder="支持按项目名检索"
                default-first-option
                filterable
                remote
                :remote-method='remoteSearch'
                @change='changeProject'>
                <el-option
                  v-for="item in projectListOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"/>
              </el-select>
            </el-form-item>
          </el-form>
        </div>
        <div class="header-add-pro">
          <el-button size="mini" type="primary" @click="createProject">新增项目</el-button>
        </div>
      </div>
    </d2-module>
    <div class="wrapper-bottom" v-if="projectId !== ''">
      <div class="clearfix" id='content-left'>
        <div id="dm2">
          <div class="container" v-if="projectForm">
            <div class="container-left" style="width: 587px">
              <div class="container-left_header">
                <div>
                  <span class="project-name">{{ projectForm.name }}</span>
                  <span class="project-icon">
                    <el-link
                      @click="updateProject(projectForm)"
                      :underline="false"
                      title="编辑">
                          <img src="xx_replace_xx" class='project-icon-item' alt="">
                      </el-link>
                    <el-link
                      v-if="projectForm.type != 'other'"
                      @click="genProject(projectForm)"
                      :underline="false"
                      title="代码生成">
                          <img src="xx_replace_xx" class='project-icon-item' alt="">
                      </el-link>
                    <el-link
                      @click="goApiInfo(projectForm.id)"
                      :underline="false"
                      title="新建API">
                          <img src="xx_replace_xx" class='project-icon-item' alt="">
                      </el-link>
                    <el-link
                      @click="delProject(projectForm)"
                      :underline="false"
                      title="删除"
                      type="danger"
                      style="margin-left:6px">
                          <img src="xx_replace_xx" class='project-icon-item' alt="">
                      </el-link>
                      <el-link
                        v-if="isSuperRole"
                        @click="initBilling(projectForm)"
                        :underline="false"
                        icon="el-icon-bangzhu"
                        title="初始化"
                        style="margin-left:6px"/>
                  </span>
                </div>
                <div class="project-info project-info-first">
                  <span class="prop-key">项目类型：</span>
                  <span class="prop-value">{{ projectForm.type || 'spring' }}</span>
                </div>
                <div class="project-info">
                  <span class="prop-key">创建时间：</span>
                  <span class="prop-value">{{ projectForm.ctimeFormat }}</span>
                </div>
                <div class="project-info">
                  <span class="prop-key">更新时间：</span>
                  <span class="prop-value">{{ projectForm.utimeFormat }}</span>
                </div>
                <div class="project-info">
                  <span class="prop-key">项目描述：</span>
                  <span class="prop-value">{{ projectForm.desc }}</span>
                </div>
                <div class="project-info project-info-last">
                  <span class="prop-key">本月cpu花销：</span>
                  <span class="prop-value">{{ monthPrice }}元</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <d2-module id="dm3" >
              <el-tabs v-model="activeName" @tab-click="handleTabClick">
                <el-tab-pane label="环境配置" name="env">
                  <div class="container-left_all"
                      v-if="projectForm.type !== 'filter' && projectForm.type !== 'plugin'"
                    >
                      <project-env :id="projectId" :projectForm="projectForm" :envPrice='envPrice'></project-env>
                  </div>
                  </el-tab-pane>
                <el-tab-pane label="流量预测" name="predict">
                  <div class="container-left_all">
                    <project-prediction :id="projectId" v-if="activeName === 'predict'"></project-prediction>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="成员" name="member">
                  <div class="container-left_all">
                    <project-member :id="projectId"></project-member>
                  </div>
                </el-tab-pane>
                <!-- <el-tab-pane label="项目文档" name="doc">
                  <div class="container-left_all" v-if="!isOnline">
                    <project-doc :id="projectId" v-if="activeName === doc"></project-doc>
                  </div>
                </el-tab-pane> -->
                <el-tab-pane label="javadoc" name="javadoc">
                  <div class="container-left_all">
                    <project-java-doc v-if="projectForm.type !== 'other'" :id="projectId"></project-java-doc>
                  </div>
                </el-tab-pane>
                <el-tab-pane label="账单统计" name="billing">
                  <div class="container-left_all">
                    <project-bill :id="projectId" v-if="activeName === 'billing'"></project-bill>
                  </div>
                </el-tab-pane>
                <el-tab-pane v-if="isSuperRole" label="副本状态" name="copy">
                  <div class="container-left_all">
                    <project-copy :id="projectId" v-if="activeName === 'copy'"></project-copy>
                  </div>
                </el-tab-pane>
              </el-tabs>

              <div v-if='projectForm === "" && projectListOptions.length === 0'>
                <el-alert type="warning" title="您还没有项目，点击右上角进行创建 ~ " show-icon :closable="false"></el-alert>
              </div>
        </d2-module>

      </div>
      <d2-module
        id="dm4"
        >
          <div class="container-right">
            <div class="commit-header">项目动态</div>
            <project-commit :projectForm="projectForm" v-if="projectForm!==''" :id="projectId"></project-commit>
          </div>
      </d2-module>
    </div>
    <!-- 创建/编辑项目/生成代码 -->
    <el-dialog
      :title="`${proTitle}`"
      :visible.sync="createProjectVisible"
      width="900px"
      class="dialog-createpage"
    >
      <el-form :model="form" ref="form" label-width="112px" :rules="createProRules" size="mini">
        <el-form-item label="项目名" prop="name">
          <el-input :disabled="projectAction == 'gen'" v-model="form.name" placeholder="请输入项目名" style="width:80%"></el-input>
        </el-form-item>
        <el-form-item label="git地址" class='gitaddress'>
          <el-input value="xx_replace_xx" placeholder disabled style="width:25%"></el-input>
          <el-form-item style="display:inline-block; width:25%" prop="gitGroup">
            <el-input :disabled="projectAction == 'gen'" v-model="form.gitGroup" placeholder="请输入组名"></el-input>
          </el-form-item>
          <el-input value="/" placeholder disabled style="width: 5%"></el-input>
          <el-form-item style="display:inline-block; width:25%" prop="gitName">
            <el-input :disabled="projectAction == 'gen'" v-model="form.gitName" placeholder="请输入项目名"></el-input>
          </el-form-item>
        </el-form-item>
        <el-form-item label="项目类型" prop="projectGen.type">
          <el-select :disabled="projectAction == 'gen'" v-model="form.projectGen.type" placeholder="请选择" style="width:35%">
            <el-option label="spring" value="spring"></el-option>
            <el-option label="docean" value="docean"></el-option>
            <el-option label="filter" value="filter"></el-option>
            <el-option label="plugin" value="plugin"></el-option>
            <el-option label='mesh' value='rcurve'></el-option>
            <el-option label='planet-spring' value='mispring'></el-option>
            <el-option label="其他" value="other"></el-option>
          </el-select>
        </el-form-item>
        <div v-if="projectAction == 'gen' && (form.projectGen.type == 'spring' || form.projectGen.type == 'docean')">
          <el-form-item label="packageName" prop="projectGen.packageName" key="4">
            <el-input v-model="form.projectGen.packageName" style="width: 35%" placeholder="请输入包名"></el-input>
          </el-form-item>
          <el-form-item label="groupId" prop="projectGen.groupId" key="1">
            <el-input v-model="form.projectGen.groupId" style="width: 35%" placeholder="请输入组Id"></el-input>
          </el-form-item>
          <el-form-item label="author" prop="projectGen.author" key="2">
            <el-input v-model="form.projectGen.author" style="width: 35%" placeholder="请输入作者"></el-input>
          </el-form-item>
          <el-form-item label="version" prop="projectGen.version" key="3">
            <el-input v-model="form.projectGen.version" style="width: 35%" placeholder="请输入版本"></el-input>
          </el-form-item>
          <el-form-item
            label="needTomcat"
            v-if="form.projectGen.type == 'spring'"
            prop="projectGen.needTomcat"
            key="5"
          >
            <el-switch
              v-model="form.projectGen.needTomcat"
              active-color="#13ce66"
              active-text="YES"
              inactive-text="NO"
            ></el-switch>
          </el-form-item>
        </div>
        <el-form-item
          v-if="projectAction === 'gen' && form.projectGen.type === 'spring'"
          label='dubbo版本'
          prop='projectGen.dubboVersion'>
          <el-select v-model='form.projectGen.dubboVersion' placeholder='请选择dubbo版本' style='width: 35%'>
            <el-option label='dubbo2.0' :value='2'></el-option>
            <el-option label='dubbo3.0' :value='3'></el-option>
            <!-- <el-option label='' :value='0'></el-option> -->
          </el-select>
        </el-form-item>
        <div v-else-if="projectAction == 'gen' && form.projectGen.type == 'filter'">
          <el-form-item label="groupId" prop="projectGen.groupId" key="6">
            <el-input v-model="form.projectGen.groupId" style="width:35%" placeholder="请输入组Id"></el-input>
          </el-form-item>
          <el-form-item label="packageName" prop="projectGen.packageName" key="8">
            <el-input v-model="form.projectGen.packageName" style="width:35%" placeholder="请输入包名"></el-input>
          </el-form-item>
          <el-form-item label="author" prop="projectGen.author" key="9">
            <el-input v-model="form.projectGen.author" style="width:35%" placeholder="请输入作者"></el-input>
          </el-form-item>
          <el-form-item label="version" prop="projectGen.version" key="10">
            <el-input v-model="form.projectGen.version" style="width:35%" placeholder="请输入版本"></el-input>
          </el-form-item>
          <el-form-item label="filterOrder" prop="projectGen.filterOrder" key="11">
            <el-input
              v-model="form.projectGen.filterOrder"
              style="width:35%"
              placeholder="请输入filterOrder"
            ></el-input>
          </el-form-item>
          <el-form-item label="cname" prop="projectGen.cname" key="12">
            <el-input v-model="form.projectGen.cname" style="width:35%" placeholder="请输入cname"></el-input>
          </el-form-item>
          <el-form-item label="filter参数" prop="projectGen.params" key="12">
            <el-input v-model="form.projectGen.params" style="width:35%" placeholder="[]"></el-input>
          </el-form-item>
          <el-form-item label="filter描述" prop="projectGen.desc" key="13">
            <el-input
              style="width:80%"
              type="textarea"
              :rows="5"
              v-model="form.projectGen.filterDesc"
              placeholder="请输入filter描述"
              maxlength="100"
              show-word-limit
              key="12"
            ></el-input>
          </el-form-item>
          <el-form-item label="isSystem" prop="projectGen.need" key="15">
            <el-switch
              v-model="form.projectGen.need"
              active-color="#13ce66"
              active-text="YES"
              inactive-text="NO"
            ></el-switch>
          </el-form-item>
        </div>
        <div v-else-if="projectAction == 'gen' && form.projectGen.type == 'plugin'">
          <el-form-item label="projectPath" prop="projectGen.projectPath" key="16">
            <el-input
              v-model="form.projectGen.projectPath"
              style="width: 35%"
              placeholder="请输入项目路径"
            ></el-input>
          </el-form-item>
          <el-form-item label="projectName" prop="projectGen.projectName" key="17">
            <el-input v-model="form.projectGen.projectName" style="width: 35%" placeholder="请输入"></el-input>
          </el-form-item>
          <el-form-item label="groupId" prop="projectGen.groupId" key="18">
            <el-input v-model="form.projectGen.groupId" style="width: 35%" placeholder="请输入组Id"></el-input>
          </el-form-item>
          <el-form-item label="packageName" prop="projectGen.packageName" key="19">
            <el-input v-model="form.projectGen.packageName" style="width: 35%" placeholder="请输入包名"></el-input>
          </el-form-item>
          <el-form-item label="author" prop="projectGen.author" key="20">
            <el-input v-model="form.projectGen.author" style="width: 35%" placeholder="请输入作者"></el-input>
          </el-form-item>
          <el-form-item label="versionId" prop="projectGen.versionId" key="21">
            <el-input
              v-model="form.projectGen.versionId"
              style="width: 35%"
              placeholder="请输入versionId"
            ></el-input>
          </el-form-item>
          <el-form-item label="url" prop="projectGen.url" key="22">
            <el-input v-model="form.projectGen.url" style="width: 35%" placeholder="请输入url"></el-input>
          </el-form-item>
        </div>
        <div v-if="projectAction == 'gen' && form.projectGen.type == 'rcurve'">
          <el-form-item label="packageName" prop="projectGen.packageName" key="30">
            <el-input v-model="form.projectGen.packageName" style="width: 35%" placeholder="请输入包名"></el-input>
          </el-form-item>
          <el-form-item label="groupId" prop="projectGen.groupId" key="31">
            <el-input v-model="form.projectGen.groupId" style="width: 35%" placeholder="请输入组Id"></el-input>
          </el-form-item>
          <el-form-item label="author" prop="projectGen.author" key="32">
            <el-input v-model="form.projectGen.author" style="width: 35%" placeholder="请输入作者"></el-input>
          </el-form-item>
          <el-form-item label="version" prop="projectGen.version" key="33">
            <el-input v-model="form.projectGen.version" style="width: 35%" placeholder="请输入版本"></el-input>
          </el-form-item>
        </div>
        <div v-if="projectAction == 'gen' && form.projectGen.type == 'mispring'">
          <el-form-item label="packageName" prop="projectGen.packageName" key="41">
            <el-input v-model="form.projectGen.packageName" style="width: 35%" placeholder="请输入包名"></el-input>
          </el-form-item>
          <el-form-item label="groupId" prop="projectGen.groupId" key="42">
            <el-input v-model="form.projectGen.groupId" style="width: 35%" placeholder="请输入组Id"></el-input>
          </el-form-item>
          <el-form-item label="author" prop="projectGen.author" key="43">
            <el-input v-model="form.projectGen.author" style="width: 35%" placeholder="请输入作者"></el-input>
          </el-form-item>
          <el-form-item label="version" prop="projectGen.version" key="44">
            <el-input v-model="form.projectGen.version" style="width: 35%" placeholder="请输入版本"></el-input>
          </el-form-item>
        </div>
        <el-form-item label="项目描述" prop="desc" key="23">
          <el-input
            style="width:80%"
            type="textarea"
            rows="5"
            v-model="form.desc"
            placeholder="请输入相关描述"
            maxlength="100"
            show-word-limit
          ></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="createProjectVisible = false" size="mini">取消</el-button>
        <el-button
          type="primary"
          @click="submitAddFormUpload('form')"
          :disabled="form.projectGen.type === 'other' && projectAction === 'gen'"
          size="mini"
        >确定</el-button>
      </span>
    </el-dialog>
  </d2-container>
</template>
<script>
import service from '@/plugin/axios/index'
import axios from 'axios'
import time2Date from '@/libs/time2Date'
import qs from 'qs'
import bizutil from '@/common/bizutil'
import { mapState, mapMutations } from 'vuex'
import statusMap from '../ststus_map'

import projectEnv from '../components/env'
import projectPrediction from '../components/prediction'
import projectMember from '../components/member'
import projectDoc from '../components/doc'
import projectCommit from '../components/commit'
import projectJavaDoc from '../components/javadoc'
import projectBill from '../components/bill'
import projectCopy from '../components/copy'
const userInfo = window.userInfo
const serverEnv = window.serverEnv
const isSuperuser = (userInfo.roles || []).find(item => item.name === 'project-superuser')
// const isSuperuser = true
const getProjectNameByGitlabAddress = gitlabAddress => {
  const s = gitlabAddress.split('/')
  return s[s.length - 1]
}

export default {
  name: 'ProjectList',
  data () {
    return {
      isOnline: !!(
        serverEnv === 'c3' ||
        serverEnv === 'c4' ||
        serverEnv === 'intranet'
      ),
      isSuperuser: !!isSuperuser,
      isSuperRole: (((userInfo && userInfo.roles) || []).findIndex(it => it.name === 'SuperRole') !== -1),
      showAll: false,
      projectId: '',
      projectListOptions: [],
      projectAction: 'create',
      createProjectVisible: false,
      dialogMemberVisible: false,
      form: {
        projectGen: {
          type: 'spring',
          need: false,
          gen: false
        }
      },
      projectForm: '',
      dialogEditProVisible: false,
      editForm: {},
      editRules: {
        name: [{ required: true, message: '请输入项目名', trigger: 'blur' }],
        gitGroup: [{ required: true, message: '请输入组名', trigger: 'blur' }],
        gitName: [{ required: true, message: '请输入项目名', trigger: 'blur' }],
        desc: [{ required: true, message: '请输入相关描述', trigger: 'blur' }]
      },
      createProRules: {
        name: [{ required: true, message: '请输入项目名', trigger: 'blur' }],
        gitGroup: [{ required: true, message: '请输入组名', trigger: 'blur' }],
        gitAdress: [{ required: true, message: '请输入', trigger: 'blur' }],
        projectGen: {
          gitName: [
            { required: true, message: '请输入项目名', trigger: 'blur' }
          ],
          desc: [
            { required: false, message: '请输入相关描述', trigger: 'blur' }
          ],
          type: [
            { required: true, message: '请选择type类型', trigger: 'blur' }
          ],
          groupId: [
            { required: true, message: '请输入groupId', trigger: 'blur' }
          ],
          packageName: [
            { required: true, message: '请输入包名', trigger: 'blur' }
          ],
          author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
          version: [{ required: true, message: '请输入版本', trigger: 'blur' }],
          needTomcat: [{ required: false, message: '请选择', trigger: 'blur' }],
          filterName: [
            { required: true, message: '请输入name', trigger: 'blur' }
          ],
          filterOrder: [
            { required: true, message: '请输入filterOrder', trigger: 'blur' }
          ],
          cname: [{ required: true, message: '请输入', trigger: 'blur' }],
          filterDesc: [
            { required: true, message: '请输入相关描述', trigger: 'blur' }
          ],
          projectPath: [
            { required: true, message: '请输入projectPath', trigger: 'blur' }
          ],
          projectName: [
            { required: true, message: '请输入projectName', trigger: 'blur' }
          ],
          versionId: [
            { required: true, message: '请输入groupId', trigger: 'blur' }
          ],
          url: [{ required: true, message: '请输入groupId', trigger: 'blur' }],
          gen: [{ required: false, message: '请选择', trigger: 'blur' }],
          dubboVersion: [ { required: true, message: '请选择dubbo版本', trigger: 'blur' } ]
        }
      },
      searchWord: '',
      activeName: 'env',
      monthPrice: 0,
      envPrice: []
    }
  },
  watch: {
    $route (to, from) {
      this.activeName = to.query.category || 'env'
    }
  },
  beforeRouteEnter (to, from, next) {
    next(vm => {
      vm.activeName = to.query.category || 'env'
    })
  },
  computed: {
    ...mapState('d2admin/user', ['info']),
    proTitle: function () {
      const projectAction = this.projectAction
      if (projectAction === 'create') {
        return '创建项目'
      }
      if (projectAction === 'update') {
        return '更新项目'
      }
      if (projectAction === 'gen') {
        return '代码生成'
      }
      return ''
    }
  },
  created () {
    this.$store.dispatch('d2admin/db/database').then(db => {
      const showAll = db.get('projectListShowAll').value()
      this.showAll = isSuperuser && showAll || false
    }).then(() => {
      this.getProList()
    }, () => {
      this.getProList()
    })
  },
  components: {
    projectEnv,
    projectPrediction,
    projectMember,
    projectDoc,
    projectCommit,
    projectJavaDoc,
    projectBill,
    projectCopy
  },
  methods: {
    getProjectById (id) {
      return service({
        url: `/project/info?id=${id}`,
        method: 'GET'
      }).then(project => {
        return project
      })
    },
    remoteSearch (query) {
      this.searchWord = query
      this.getProjectListOptions()
    },
    getCpuPrice (id) {
      return service({
        url: `/billing/project/month?projectId=${id}`,
        method: 'GET'
      }).then(res => {
        this.monthPrice = res.price
        this.envPrice = res.list
      })
    },
    getProjectListOptions () {
      return service({
        url: '/project/list',
        method: 'POST',
        data: {
          showAll: this.showAll,
          search: this.searchWord
        }
      }).then(res => {
        const list = res.list
        if (!Array.isArray(list)) return []
        this.projectListOptions = list.map(item => {
          item.ctimeFormat = bizutil.timeFormat(item.ctime)
          item.utimeFormat = bizutil.timeFormat(item.utime)
          if (item.gitGroup == null && item.gitName == null && item.gitAddress) {
            const rGitAddress = xx_replace_xx
            const match = item.gitAddress.match(rGitAddress)
            if (match && match[1] && match[2]) {
              item.gitGroup = match[1]
              item.gitName = match[2]
            }
          }
          if (!item.projectGen) {
            item.type = 'spring'
          } else {
            item.type = item.projectGen.type
          }
          return item
        })
        return this.projectListOptions
      })
    },

    getProList () {
      this.getProjectListOptions().then((list) => {
        const defaultProject = list[0]
        return this.$store.dispatch('d2admin/db/database').then(db => {
          const id = db.get('projectListPageProjectId').value()
          if (id == null) {
            return Promise.reject(defaultProject)
          }
          this.getCpuPrice(id)
          let project = list.find(it => it.id === id)
          if (project) {
            this.projectId = project.id
            this.projectForm = project
            return Promise.resolve(project)
          }
          return this.getProjectById(id).then((project) => {
            if (!project) {
              return Promise.reject(defaultProject)
            }
            this.projectId = project.id
            this.projectForm = project
            project.ctimeFormat = bizutil.timeFormat(project.ctime)
            project.utimeFormat = bizutil.timeFormat(project.utime)
            project.type = (project.projectGen && project.projectGen.type) || 'spring'
            list.push(project)
            return Promise.resolve(project)
          })
        })
      }).catch((defaultProject) => {
        if (defaultProject) {
          this.projectId = defaultProject.id
          this.projectForm = defaultProject
        }
      })
    },

    // 创建项目
    createProject () {
      this.createProjectVisible = true
      this.projectAction = 'create'
      this.form = {
        projectGen: {
          type: 'spring',
          gen: false,
          need: false
        }
      }
    },

    // 项目更新
    updateProject (item) {
      this.projectAction = 'update'
      this.createProjectVisible = true
      this.form = {
        ...item,
        projectGen: item.projectGen
          ? item.projectGen
          : {
            type: 'spring',
            gen: false,
            need: false,
            version: '1.0.0'
          }
      }
    },
    genProject (item) {
      this.projectAction = 'gen'
      this.createProjectVisible = true
      this.form = {
        ...item,
        projectGen: item.projectGen ? item.projectGen : {
          type: 'spring',
          gen: false,
          needTomcat: false,
          version: '1.0.0'
        }
      }
      // 默认版本号
      if (item.projectGen && !item.projectGen.version) {
        this.form.projectGen.version = '1.0.0'
      }
      // 兼容旧项目[未添加dubbo版本配置项]
      if (item.projectGen && item.projectGen.dubboVersion === 0) {
        this.form.projectGen.dubboVersion = ''
      }
    },
    // 创建/更新项目 -> 提交
    submitAddFormUpload (formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        const form = this.form
        const projectAction = this.projectAction
        let url = '/project/create'
        if (projectAction === 'update') {
          url = '/project/update'
        } else if (projectAction === 'gen') {
          url = '/project/generateCode'
        }
        service({
          url: url,
          method: 'POST',
          data: form
        }).then(isPass => {
          if (isPass) {
            this.createProjectVisible = false
            this.getProList()
          }
        })
      })
    },
    delProject (item) {
      this.$confirm('此操作将永久删除该项目, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          service({
            url: '/project/delete',
            method: 'POST',
            data: item
          }).then(isPass => {
            if (isPass) {
              this.getProList()
              this.$message({
                type: 'success',
                message: '删除成功!'
              })
            }
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    changeShowAll (showAll) {
      this.showAll = showAll
      this.$store.dispatch('d2admin/db/database').then(db => {
        db.set('projectListShowAll', showAll).write()
      })
      this.getProList()
    },
    goApiInfo (proId) {
      this.$router.push(`/gateway/apiinfo/list?id=${proId}`)
    },
    changeProject (projectId) {
      localStorage.setItem("applicationActive", 0)
      let project = this.projectListOptions.find(item => item.id === projectId)
      this.projectForm = project
      this.searchWord = project.name
      this.$store.dispatch('d2admin/db/database').then(db => {
        db.set('projectListPageProjectName', this.searchWord).write()
      })
      this.$store.dispatch('d2admin/db/database').then(db => {
        db.set('projectListPageProjectId', projectId).write()
      })
      this.getCpuPrice(projectId)
    },
    initBilling (item) {
      service({
        url: '/project/init/bill',
        method: 'POST',
        data: item
      }).then(res => {
      })
    },
    handleTabClick () {
      this.$router.push({
        path: this.$route.path,
        query: {
          category: this.activeName
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.wrapper-bottom {
  display: flex;
  flex-direction: row;
}
.clearfix {
  padding-right: 12px;
  flex: 1;
  display: flex;
  flex-direction: column;
}
 /deep/.clearfix::after{
  content: '';
  display: block;
  clear:both
}
#dm2 {
    margin-bottom: 10px;
    background: #eff0f4;
    /deep/ .top-padding {
      height: 128px;
    }
}
.header {
  display: flex;
  justify-content: space-between;
  height: 30px;
  .header-add-pro {
    /deep/ .el-button {
      border-radius: 2px;
      background: #457BFC;
      color: #FFFFFF;
      font-family: PingFang SC;
      font-weight: 400;
      font-size: 12px;
      line-height: normal;
      letter-spacing: 0px;
      text-align: center;
    }
  }
}
.container {
  display: flex;
  background : #fff;
  padding : 20px;
  /deep/.container-left {
    flex-grow: 1;
    .el-tabs__active-bar{
      background-color: #457BFC;
    }
    .el-tabs__item {
      color: #999999;
    }
    .el-tabs__item:hover {
      color:#333333;
    }
    .el-tabs__item.is-active {
      color:#333333;
    }
  }
}
.container-left_header {
  .project-info {
    padding-top:5px;
    padding-left:20px;
    &-first {
      margin-top:5px;
      border-top: 1px solid #EFF0F4;
      padding-top:5px;
    }
    &-last {
    padding-left:0;
    }
    .prop-key {
      color: #999999;
      font-family: PingFang SC;
      font-weight: regular;
      font-size: 12px;
      line-height: normal;
      letter-spacing: 0px;
      text-align: left;
    }
    .prop-value {
      font-size: 13px;
      color: #606266;
    }
  }
  .project-name {
      color: #333333;
      font-family: PingFang SC;
      font-weight: bolder;
      font-size: 20px;
      line-height: normal;
      letter-spacing: 0px;
      text-align: left;
  }
  .project-icon {
    float: right;
    padding-top: 3px;
    &-item {
      height:23px;
    }
  }
}
.container-left_header .el-link.el-link--default {
  font-size: 14px;
  margin-left: 6px;
}
  .container-right {
    padding: 0 16px 10px;
    .commit-header {
      font-size: 14px;
      font-weight: 600;
      color: #202d40;
      margin-top: 20px;
      margin-bottom: 13px;
    }
  }
.el-divider--horizontal {
  margin-bottom: 12px;
  margin-top: 0;
}
.gitaddress .el-form-item--mini.el-form-item, .el-form-item--small.el-form-item {
  margin-bottom: 0px;
}
/deep/ #dm3 {
    flex: 1;
    display: flex;
      .d2-module-container {
        padding: 0px;
        min-height: 607px;
        flex: 1;
        .el-tabs__header {
            margin: 0px;
            padding: 20px 0px 0 0px;
            .el-tabs__nav-wrap {
              padding: 0 20px 0 20px;
              .el-tabs__nav {
                .el-tabs__item{
                  color: rgba(153,153,153,1);
                }
                .is-active {
                  color: rgba(51,51,51,1);
                }
              }
            }
        }
        .el-tabs__content {
          padding: 20px;
          .el-tabs__header {

              padding: 0px;
              padding-bottom: 20px;
              .el-tabs__nav-wrap {
                padding: 0;
                .el-tabs__nav {
                  .el-tabs__item{
                    color: rgba(153,153,153,1);
                  }
                  .is-active {
                    color: rgba(69,123,252,1);
                  }
                  .el-tabs__active-bar {
                    background: rgba(69,123,252,1);
                  }
                }
              }
          }
          .el-tabs__content {
            padding: 0px;

          }

        }
      }
}
/deep/ #dm4 {
      display: flex;
      width : 274px;
  .d2-module-container {
    min-height: 766px;
    width: 234px;
  }
}
</style>

<style lang="scss">
.dialog-updatepage .el-textarea .el-input__count,
.dialog-createpage .el-textarea .el-input__count {
  right: 22px;
  bottom: 6px;
  height: 18px;
  line-height: 18px;
}
#dc1 .d2-container-full .d2-container-full__body {
    background:#eff0f4;
}
</style>
