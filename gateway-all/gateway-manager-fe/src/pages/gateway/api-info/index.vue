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
    <d2-container>
      <d2-module margin-bottom>
        <div class="apiInfo-header">
          <div class="apiInfo-header_left">
            <el-dropdown type="primary" size="mini" plain @command="handleBatch">
              <el-button size="mini">
                批量操作<em class="el-icon-arrow-down el-icon--right"></em>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="1">批量删除</el-dropdown-item>
                <el-dropdown-item command="3">批量导出</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
          <div class="apiInfo-header_right">
            <el-button size="mini" class="umami--click--apiinfo-refresh" :disabled="refreshDisabled" @click="refresh">刷新</el-button>
            <el-button size="mini" class="umami--click--apiinfo-new" @click="addRow">新增</el-button>
            <el-button size="mini" @click="importConfigure">导入</el-button>
          </div>
        </div>
        <div class="header">
          <el-dropdown @command="handleSelect" class='dropdown_margin'>
            <div class="el-dropdown-link">
              {{classificationQueryConfigTitle}}<em class="el-icon-arrow-down el-icon--right"></em>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item
                v-for="(cqItem,index) in  classificationQueryConfig.list"
                :key="index"
                :index="cqItem.key"
                :disabled="cqItem.disabled"
                :command="cqItem.key"
                >{{cqItem.displayName}}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-input
            :placeholder="classificationQueryConfig.activeIndex===`4`?`收藏不支持搜索`:`搜索 回车确认`" :disabled="classificationQueryConfig.activeIndex===`4`"
            size="mini"
            @change="handleSearch"
            @clear="refresh"
            v-model="searchKeyWord"
            clearable
            class='input_margin'/>
          <el-select
            size='mini'
            v-model='searchObj.httpMethod'
            class='input_margin'
            placeholder="请求方法"
            @change='searchChange'>
            <el-option
              v-for='item in httpMethodOptions'
              :key='item.value'
              :label='item.label'
              :value='item.value'/>
          </el-select>
          <el-select
            size='mini'
            v-model.number='searchObj.groupId'
            class='input_margin'
            placeholder="所属分组"
            @change='searchChange'>
            <el-option
              v-for='item in groupOptions'
              :key='item.value'
              :label='item.label'
              :value='item.value'/>
          </el-select>
          <el-select
            size='mini'
            v-model='searchObj.routeType'
            class='input_margin'
            placeholder="路由类型"
            @change='searchChange'>
            <el-option
              v-for='item in routeTypeOptions'
              :key='item.value'
              :label='item.label'
              :value='item.value'/>
          </el-select>
          <el-button
            size='mini'
            @click="handleInputSearch">查询</el-button>
        </div>
      </d2-module>

      <d2-module>
        <el-table stripe :data="tableData" style="width:100%" @selection-change="handleSelectionChange" class="table-list api-table-list">
          <el-table-column type="selection" width="45"></el-table-column>
          <el-table-column fixed type="expand" width="15">
            <template slot-scope="props">
              <el-form label-position="left" class="api-table-expand">
                <el-form-item label="是否离线">
                    <span>{{ props.row.offlineDisplay }}</span>
                </el-form-item>
                <el-form-item label="是否缓存">
                    <span>{{ props.row.allowCacheDisplay }}</span>
                </el-form-item>
                <el-form-item label="是否支持Preview">
                    <span>{{ props.row.allowPreviewDisplay }}</span>
                </el-form-item>
                <el-form-item label="缓存过期时间(毫秒)">
                    <span>{{ props.row.cacheExpire }}</span>
                </el-form-item>
                <el-form-item label="是否基于IP防刷">
                    <span>{{ props.row.allowIpAntiBrush }}</span>
                </el-form-item>
                <el-form-item label="基于IP防刷限制">
                    <span>{{ props.row.ipAntiBrushLimit }}</span>
                </el-form-item>
                <el-form-item label="是否基于UID防刷">
                    <span>{{ props.row.allowUidAntiBrush }}</span>
                </el-form-item>
                <el-form-item label="基于UID防刷限制">
                    <span>{{ props.row.uidAntiBrushLimit }}</span>
                </el-form-item>
                <el-form-item label="提取UID">
                    <span>{{ props.row.allowAuthDisplay }}</span>
                </el-form-item>
                <el-form-item label="是否打印日志">
                    <span>{{ props.row.allowLogDisplay }}</span>
                </el-form-item>
                <el-form-item label="是否允许跨域">
                    <span>{{ props.row.allowCorsDisplay }}</span>
                </el-form-item>
                <el-form-item label="是否使用脚本">
                    <span>{{ props.row.allowScriptDisplay }}</span>
                </el-form-item>
                <el-form-item v-if="props.row.allowScript" label="脚本数据">
                    <span>
                      <el-button @click="showScript(props.row)" type="text" size="small">查看详情</el-button>
                    </span>
                </el-form-item>
                <el-form-item label="是否Mock">
                    <span>{{ props.row.allowMockDisplay }}</span>
                </el-form-item>
                <el-form-item v-if="props.row.allowMock" label="Mock数据">
                    <span>
                      <el-button @click="showMockdata(props.row)" type="text" size="small">查看详情</el-button>
                    </span>
                </el-form-item>
                <el-form-item v-if="props.row.allowMock" label="Mock数据描述">
                    <span>{{ props.row.mockDataDesc }}</span>
                </el-form-item>
                <el-form-item v-if="props.row.routeTypeCompose" label="接口编排">
                    <span>
                      <el-button @click="showGroupConfig(props.row)" type="text" size="small">查看详情</el-button>
                    </span>
                </el-form-item>
                <el-form-item v-if="props.row.contentType" label="ContentType">
                    <span>{{ props.row.contentType }}</span>
                </el-form-item>
                <el-form-item v-if="props.row.routeTypeHttp" label="目标路径">
                    <span>{{ props.row.path }}</span>
                </el-form-item>
                <el-form-item v-if="props.row.routeTypeDubbo" label="服务名称">
                    <span>{{ props.row.serviceName }}</span>
                </el-form-item>
                <el-form-item v-if="props.row.routeTypeDubbo" label="方法名称">
                    <span>{{ props.row.methodName }}</span>
                </el-form-item>
                <el-form-item v-if="props.row.routeTypeDubbo" label="dubbo分组">
                    <span>{{ props.row.serviceGroup }}</span>
                </el-form-item>
                <el-form-item v-if="props.row.routeTypeDubbo" label="服务版本">
                    <span>{{ props.row.serviceVersion }}</span>
                </el-form-item>
                <el-form-item v-if="props.row.routeTypeDubbo" label="参数模板">
                    <span>
                      <el-button @click="showParamTpl(props.row)" type="text" size="small">查看详情</el-button>
                    </span>
                </el-form-item>
                <el-form-item label="连接数限制">
                    <span>{{ props.row.invokeLimit }}</span>
                </el-form-item>
                <el-form-item label="使用并发限制">
                    <span>{{ props.row.useQpsLimitDisplay }}</span>
                </el-form-item>
                <el-form-item v-if="props.row.useQpsLimit" label="并发限制(QPS)">
                    <span>{{ props.row.qpsLimit }}</span>
                </el-form-item>
                <el-form-item label="超时(毫秒)">
                    <span>{{ props.row.timeout }}</span>
                </el-form-item>
                <el-form-item label="使用Token">
                    <span>{{ props.row.allowTokenDisplay }}</span>
                </el-form-item>
                <el-form-item v-if="props.row.allowToken" label="Token">
                    <span>{{ props.row.token }}</span>
                </el-form-item>
              </el-form>
            </template>
          </el-table-column>
          <el-table-column fixed prop="id" label="ID" width="60"></el-table-column>
          <el-table-column prop="name" label="名称" width="250" align="center" header-align="center"></el-table-column>
          <el-table-column prop="description" label="描述" width="100" show-overflow-tooltip></el-table-column>
          <el-table-column prop="url" align="center" header-align="center" label="请求路径" width="300"></el-table-column>
          <el-table-column prop="httpMethod" label="请求方法" width="80"></el-table-column>
          <el-table-column label="所属分组" width="110">
            <template slot-scope="scope">
              <el-tag>
                {{scope.row.groupName}}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="routeTypeDisplay" label="路由类型" width="135"></el-table-column>
          <el-table-column prop="creator" label="创建人" width="100"></el-table-column>
          <el-table-column prop="ctime" label="创建时间" width="160"></el-table-column>
          <el-table-column prop="utime" label="更新时间" width="160"></el-table-column>
          <el-table-column prop="updater" label="更新人" width="100"></el-table-column>
          <el-table-column label="api重要度" width="150">
            <template slot-scope="scope">
              <el-rate
                v-model="scope.row.priority"
                @change="setApiPriority(scope.row)"
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']">
              </el-rate>
            </template>
          </el-table-column>
          <el-table-column fixed="right" label="操作" width="185">
            <template slot-scope="scope">
              <el-button @click="editRow(scope.row.id)" type="text" class="el-button--blue umami--click--apiinfo-edit" size="small" v-if="scope.row.readOnly!==true">编辑</el-button>
              <el-button @click="copyRow(scope.row.id)" type="text" class="el-button--blue" size="small">复制</el-button>
              <el-dropdown class="el-dropdown-styled" size="mini" @command="commandHandler($event, scope.row)">
                <el-button class="el-button--blue">
                  更多<em class="el-icon-arrow-down el-icon--right"></em>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="debug">调试</el-dropdown-item>
                  <el-dropdown-item command="delete" v-if="scope.row.readOnly!==true">删除</el-dropdown-item>
                  <el-dropdown-item command="export">导出</el-dropdown-item>
                  <el-dropdown-item command="updateCreator">修改创建人</el-dropdown-item>
                  <el-dropdown-item command="collect">{{scope.row.hasCollected?`取消收藏`:`收藏`}}</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </template>
          </el-table-column>
        </el-table>
        <d2-pagination
          marginTop
          :currentPage='pageNo'
          :pageSize='pageSize'
          :total='total'
          :pageDisabled='pageDisabled'
          @doCurrentChange='handleCurrentChange'/>
      </d2-module>

      <!--添加&编辑, v-if保证组件每次都创建新的组件-->
      <api-info-editor
        v-if="apiInfoEditorVisible"
        :form="formApiInfo"
        :title="apiInfoTitle"
        :loading="apiInfoEditorLoading"
        :api-info-editor-visible.sync="apiInfoEditorVisible"
        @submitSuccess="handleCurrentChange"
      />
      <serverLess-info-editor
        v-if="serverLessEditorVisible"
        :form="formServerLessInfo"
        :title="apiInfoTitle"
        :serverLessEditorVisible.sync="serverLessEditorVisible"
        @submitSuccess="handleCurrentChange"
        @serverLessEditorVisible="serverLessEditorVisibleChange"
      />

      <!--调试-->
      <template>
        <debug-dialog
          :visible.sync="debugFormVisible"
          :form.sync="formDebug"
          @resetDebugForm="resetDebugFormHandler"
        />
      </template>

      <!-- 数据浏览框 -->
      <template>
        <el-dialog :title="viewDataDialogTitle" :visible.sync="viewDataDialogVisible" @close="handleViewClose" :close-on-click-modal=false>
          <el-card shadow="never" class="d2-mb">
            <d2-highlight :code="viewData"/>
          </el-card>
        </el-dialog>
      </template>

      <!-- 参数模板浏览框 -->
      <template>
        <el-dialog
          :title="viewParamTplDialogTitle"
          :visible.sync="viewParamTplDialogVisible"
          @close="handleViewClose"
          width="1000px"
          :close-on-click-modal=false>
          <el-card shadow="never" class="d2-mb">
            <el-table stripe :data="paramTpl" border style="width: 100%">
              <el-table-column fixed prop="itemNo" label="序号" width="50">
              </el-table-column>

              <el-table-column prop="type" label="参数类型" width="650">
              </el-table-column>

              <el-table-column prop="expression" label="参数名" width="217">
              </el-table-column>
            </el-table>
          </el-card>
        </el-dialog>
      </template>

      <!-- 脚本浏览框 -->
      <template>
        <el-dialog :title="viewScriptDialogTitle" :visible.sync="viewScriptDialogVisible" @close="handleViewClose"
                    width="1000px" :close-on-click-modal=false>
            <el-card shadow="never" class="d2-mb">
                <el-form :model="scriptData" status-icon>
                    <el-form-item label="git projectId:" :label-width="formLabelWidth">
                        <d2-highlight :code="scriptData.gitProjectId"/>
                    </el-form-item>

                    <el-form-item label="git path:" :label-width="formLabelWidth">
                        <d2-highlight :code="scriptData.gitPath"/>
                    </el-form-item>

                    <el-form-item label="git branch:" :label-width="formLabelWidth">
                        <d2-highlight :code="scriptData.gitBranch"/>
                    </el-form-item>

                    <el-form-item label="git commit:" :label-width="formLabelWidth">
                        <d2-highlight :code="scriptData.commit"/>
                    </el-form-item>

                    <el-form-item label="脚本:" :label-width="formLabelWidth">
                        <d2-highlight :code="scriptData.script"/>
                    </el-form-item>

                    <el-form-item label="方法名称:" :label-width="formLabelWidth">
                        <d2-highlight :code="scriptData.methodName"/>
                    </el-form-item>

                    <el-form-item label="执行类型:" :label-width="formLabelWidth">
                        <d2-highlight :code="scriptData.scriptTypeDisplay"/>
                    </el-form-item>

                    <el-form-item label="参数列表:" :label-width="formLabelWidth">
                        <d2-highlight :code="scriptData.params"/>
                    </el-form-item>
                </el-form>
            </el-card>
        </el-dialog>
      </template>
      <template>
        <el-dialog
          title="修改创建人"
          :visible.sync="viewUpdateCreatorVisiable"
          width="30%"
          :close-on-click-modal=false>
          <el-form ref="form" :model="updateCreatorForm" label-width="80px">
            <el-form-item label="url">
              <el-input disabled size="mini" v-model="updateCreatorForm.url"></el-input>
            </el-form-item>
            <el-form-item label="创建人">
              <el-select size="mini" filterable @change="creatorChange" v-model="updateCreatorForm.username" placeholder="请选择创建人">
                <el-option v-for="(user,index) in userList" :key="index" :label="user.userName" :value="user.userName"></el-option>
              </el-select>
            </el-form-item>
              <el-form-item>
                <el-button type="primary" size="mini" @click="onSubmitUpdateCreator">更改</el-button>
                <el-button size="mini"  @click="viewUpdateCreatorVisiable = false">取消</el-button>
              </el-form-item>
          </el-form>
        </el-dialog>
      </template>

      <el-dialog :title="`${configureTitle}配置`" :visible.sync="configureVisible" width="800px" @close='handleConfigureClosed' :close-on-click-modal=false>
        <el-form ref="configureForm" :model="configureForm" label-width="110px" :rules="rules">
          <el-form-item label="配置" prop="str">
            <el-input type="textarea"
                      autosize
                      placeholder="请输入内容"
                      v-model="configureForm.str"
                      style="width: 85%"></el-input>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer" v-if="this.configureTitle === '导入'">
          <el-button size='mini' @click="configureVisible = false">取 消</el-button>
          <el-button type="primary" size='mini' @click="submitConfigureForm('configureForm')">确 定</el-button>
        </span>
    </el-dialog>
  </d2-container>
</template>

<script>
import service from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'
import qs from 'qs'
import 'codemirror/lib/codemirror.js'
import 'codemirror/theme/base16-dark.css'
import '@/common/jsonlint-z.js'
import 'codemirror/addon/lint/lint.js'
import 'codemirror/addon/lint/json-lint.js'
import 'codemirror/lib/codemirror.css'
import 'codemirror/addon/lint/lint.css'

import Vue from 'vue'
import { mapState } from 'vuex'
import time2Date from '@/libs/time2Date'
import ApiInfoEditor from './components/api-info-editor'
import ServerLessInfoEditor from './components/serverLess-info-editor'

import DebugDialog from './components/debug-dialog'
import { routeType as routeTypeOptions, httpMethod as httpMethodOptions } from './info_map.js'
export default {
  name: 'apiInfo',
  components: {
    ApiInfoEditor,
    ServerLessInfoEditor,
    DebugDialog
  },
  data () {
    return {
      searchKeyWord: '',
      tableData: [],
      total: 0,
      pageNo: 1,
      pageSize: 10,
      curUser: '', // 是否当前用户的task
      running: 0, // 是否筛选running
      // showCurUserTask: showCurUserTask, // 是否筛选当前用户的创建的task
      warningClosable: false,
      pageDisabled: false,
      debugFormVisible: false,
      debugRowInfo: [],
      editFormTitle: '',
      viewDataDialogVisible: false,
      viewParamTplDialogVisible: false,
      viewScriptDialogVisible: false,
      viewUpdateCreatorVisiable: false,
      refreshDisabled: false,
      formApiInfo: {
        script: ''
      },
      formServerLessInfo: {
      },
      apiInfoTitle: '',
      apiInfoEditorVisible: false,
      serverLessEditorVisible: false,
      apiInfoEditorLoading: false,
      gatewaySvrUrl: '',
      formDebug: {
        script: '',
        gatewaySvrUrl: ''
      },
      formLabelWidth: '120px',
      viewData: '',
      paramTpl: [],
      scriptData: {},
      viewDataDialogTitle: '',
      viewParamTplDialogTitle: '',
      viewScriptDialogTitle: '',
      batchOptRows: [],
      classificationQueryConfig: {
        activeIndex: '1',
        list: [
          {
            key: '1',
            displayName: '显示全部'
          },
          {
            key: '2',
            displayName: '我的创建'
          },
          {
            key: '3',
            displayName: '我的更新'
          },
          {
            key: '4',
            displayName: '我的收藏'
          }
          //  ,{
          //   key:"5",
          //   displayName:"分组查询",
          //   group:[
          //     {
          //       key:"5-1",
          //       displayName:'业务架构组'
          //     },
          //     {
          //       key:"5-2",
          //       displayName:'商业平台组'
          //     }
          //   ]
          // }
        ]
      },
      configureVisible: false,
      configureForm: { str: '' },
      configureId: [],
      configureTitle: '',
      rules: {
        str: [
          { required: true, message: '请输入', trigger: 'blur' }
        ]
      },
      updateCreatorForm: {
        id: null,
        url: '',
        creator: ''
      },
      userList: [],
      searchObj: {
        httpMethod: '',
        groupId: '',
        routeType: ''
      },
      groupOptions: [],
      routeTypeOptions,
      httpMethodOptions
    }
  },
  beforeRouteEnter (to, form, next) {
    if (form.path === '/application/list') {
      next(vm => {
        if (to.query.id) {
          vm.getInfo(to.query.id)
        }
      })
      return
    }
    next()
  },
  computed: {
    ...mapState('d2admin/dealUserInfo', ['userInfo']),
    classificationQueryConfigTitle: function () {
      let { activeIndex, list } = this.classificationQueryConfig
      return list.filter(item => item.key === activeIndex)[0].displayName
    }
  },
  async mounted () {
    const db = await this.$store.dispatch('d2admin/db/database')
    let groupType = db.get('groupType').value()
    if (typeof groupType === 'undefined') {
      db.set('groupType', '1').write()
      groupType = '1'
    }
    this.classificationQueryConfig.activeIndex = groupType
    this.getList()
  },
  created () {
    this.getGroupOptions()
    this.getGatewaySvrUrl()
  },
  methods: {
    getInfo (id) {
      service({
        url: `/apiinfo/getinfo/${id}`
      }).then(res => {
        this.formApiInfo = {
          ...res,
          timeout: 1000,
          httpMethod: 'post',
          routeType: '1',
          filterParams: [],
          plugins: [],
          script: ''
        }
        setTimeout(() => {
          this.apiInfoEditorVisible = true
        }, 500)
      })
    },
    importConfigure () {
      this.configureVisible = true
      this.configureTitle = '导入'
    },
    submitConfigureForm (formName) {
      this.$refs[formName].validate((valid) => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return false
        }
        service({
          url: '/apiimport',
          method: 'POST',
          data: {
            json: this.configureForm.str
          }
        }).then(res => {
          let message = ` 导入${res ? '成功' : '失败'}`
          if (res) {
            this.$message.success(message + ',若配置filter请重新配置')
          } else {
            this.$message.error(message)
          }
          this.configureVisible = false
          this.getList()
        })
      })
    },
    exportConfigure (data) {
      let ids = []
      if (data.constructor === Array) {
        // 批量导出复用这个方法,参数改为id列表  调用在 handleBatchExport
        ids = data.map(it => it.id)
      } else {
        ids.push(data.id)
      }
      service({
        url: '/apiexport',
        method: 'POST',
        data: { ids }
      }).then(res => {
        res.paramTemplate = data.paramTemplate
        // 抹掉 gid
        if (res.groupId !== 0) {
          res.groupId = 0
        }
        let resultStr = JSON.stringify(res)

        this.configureForm = { str: resultStr }
        // this.configureVisible = true;
        // this.configureTitle = '导出';
        bizutil.copyText(resultStr) && this.$message.success('已复制到剪切板')
      })
    },
    getUserList () {
      return service({
        url: '/account/all/list',
        method: 'get'
      })
        .then(res => {
          this.userList = res
        })
    },
    creatorChange () {
      this.$forceUpdate()
    },
    onSubmitUpdateCreator () {
      service({
        url: '/apiInfo/updateCreator',
        method: 'post',
        data: { ...this.updateCreatorForm }
      })
        .then(res => {
          let isSuccess = res === true
          if (res) {
            this.getList()
          }
          this.$message({
            type: `${isSuccess ? 'success' : 'fail'}`,
            message: `修改${isSuccess ? '成功' : '失败'}`
          })
        })
        .finally(_ => {
          this.viewUpdateCreatorVisiable = false
        })
    },
    getList () {
      this.pageDisabled = true
      service({
        url: '/apiinfo/list',
        method: 'post',
        data: {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          serviceName: this.searchKeyWord,
          pathString: this.searchKeyWord,
          urlString: this.searchKeyWord,
          name: this.searchKeyWord,
          ...this.searchObj,
          // showMine: this.showCurUserTask
          groupType: +this.classificationQueryConfig.activeIndex
        }
      }).then(res => {
        this.total = res.total
        this.tableData = bizutil.apiListProcess(res.infoList)
        this.pageDisabled = false
      })
      setTimeout(() => {
        this.pageDisabled = false
      }, 2000)
    },

    handleSearch (e) {
      this.searchKeyWord = e
      this.pageNo = 1
      this.getList()
    },

    handleCurrentChange (val) {
      if (val) {
        this.pageNo = val
      }
      this.getList()
    },
    async handleSelect (key) {
      const db = await this.$store.dispatch('d2admin/db/database')
      // db.set("groupType",1).write();
      // console.log(db.get("groupType").value());
      // return;
      let activeItem = this.classificationQueryConfig.list.filter(item => item.key === key)[0]
      this.classificationQueryConfig.activeIndex = activeItem.key
      db.set('groupType', activeItem.key).write()
      this.getList()
    },

    // 新增按钮
    addRow () {
      this.apiInfoTitle = '新建API'
      this.apiInfoEditorVisible = true
      this.formApiInfo = {
        plugins: [],
        filterParams: [],
        routeType: '0',
        script: ''
      }
    },

    // 刷新
    refresh () {
      this.refreshDisabled = true
      setTimeout(() => {
        this.refreshDisabled = false
      }, 2000)
      this.searchKeyWord = ''
      this.searchObj = {
        httpMethod: '',
        groupId: '',
        routeType: ''
      }
      this.pageNo = 1
      this.pageSize = 10
      this.getList()
    },

    // 复制按钮
    copyRow (id) {
      this.apiInfoEditorLoading = true
      this.apiInfoEditorVisible = true
      this.getApiInfoDetail(id).then(row => {
        this.doCopyRow(row)
      })
    },
    doCopyRow (row) {
      this.apiInfoTitle = '复制API'
      this.formApiInfo = { ...row, plugins: [] }
      this.formApiInfo.name = 'copy of ' + this.formApiInfo.name
      delete this.formApiInfo.id
      delete this.formApiInfo.groupId
      delete this.formApiInfo.url
      this.formApiInfo.scriptType = this.formApiInfo.scriptType.toString()
      this.formApiInfo.routeType = this.formApiInfo.routeType.toString()
      this.formApiInfo.filterParams = this.parseFilterParams(row.filterParams || '[]')

      service({
        url: `/ds/query?ids=${this.formApiInfo.dsIds}`
      }).then((e) => {
        this.apiInfoEditorLoading = false
        this.formApiInfo.plugins = e.map(item => item.id)
      }, res => {
        this.$message.error(res)
      })
    },
    getGatewaySvrUrl () {
      return service({
        url: '/custom/config/get?key=gatewaySvrUrl',
        method: 'get'
      }).then((res) => {
        this.gatewaySvrUrl = (res && res.content) || ''
      })
    },
    // 调试按钮
    debugRow (row) {
      service({
        url: '/apiinfo/getdebug',
        method: 'post',
        data: {
          id: row.id
        }
      }).then(e => {
        console.log(e)
        this.debugFormVisible = true
        this.debugRowInfo = row
        this.formDebug.aid = row.id
        this.formDebug.url = row.url
        this.formDebug.httpMethod = row.httpMethod
        this.formDebug.timeout = row.timeout
        if (e === null) {
          this.formDebug.ctime = ''
          this.formDebug.utime = ''
          this.formDebug = { ...this.formDebug, gatewaySvrUrl: this.gatewaySvrUrl }
        } else {
          e.ctime = bizutil.timeFormat(e.ctime)
          e.utime = bizutil.timeFormat(e.utime)
          // 接口返回不一致时，使用列表数据
          if (e.url !== row.url) {
            e.url = row.url
          }
          e.gatewaySvrUrl = e.gatewaySvrUrl || this.gatewaySvrUrl
          if (e.httpMethod !== row.httpMethod) {
            e.url = row.httpMethod
          }
          if (e.timeout !== row.timeout) {
            e.timeout = row.timeout
          }
          if (e.ext) {
            let ext = JSON.parse(e.ext)
            e.scriptLog = ext.scriptDebug
          }
          this.formDebug = { ...e }
        }
      })
    },
    resetDebugFormHandler () {
      this.formDebug = {
        script: ''
      }
    },
    beautifyJsonStr (str) {
      let ret = ''
      try {
        ret = JSON.stringify(JSON.parse(str), null, 4)
      } catch (error) {
        ret = str
      }
      return ret
    },
    getApiInfoDetail (id) {
      return service({
        url: `/apiinfo/detail?id=${id}`,
        method: 'GET'
      }).then(apiInfoDetail => apiInfoDetail)
    },
    // 编辑按钮
    editRow (id) {
      this.apiInfoEditorLoading = true
      this.apiInfoEditorVisible = true
      this.getApiInfoDetail(id).then(row => {
        this.doEditRow(row)
      })
    },
    doEditRow (row) {
      this.formApiInfo = { ...row, plugins: [] }
      this.formApiInfo.groupConfig = this.beautifyJsonStr(row.groupConfig)
      this.formApiInfo.paramTemplate = this.beautifyJsonStr(row.paramTemplate)
      this.formApiInfo.script = row.script || ''
      this.formApiInfo.scriptType = `${row.scriptType}`
      this.formApiInfo.routeType = `${row.routeType}`
      this.formApiInfo.filterParams = this.parseFilterParams(row.filterParams || '[]')

      // todo:
      // 处理baseUrl, 去处/mtop/[groupName]/ ???
      this.formApiInfo.baseUrl = ''
      if (this.formApiInfo.url.indexOf('/mtop/') !== -1) {
        var formUrl = this.formApiInfo.url
        formUrl = formUrl.replace('/mtop/', '')
        var groupName = formUrl
        if (formUrl.indexOf('/') !== -1) {
          groupName = formUrl.substring(0, formUrl.indexOf('/')) + '/'
        }
        formUrl = formUrl.replace(groupName, '')
        this.formApiInfo.url = formUrl
      }

      service({
        url: `/ds/query?ids=${row.dsIds}`
      }).then((e) => {
        this.apiInfoTitle = '编辑API ' + this.formApiInfo.id
        this.formApiInfo.plugins = e.map(item => item.id)
        this.apiInfoEditorLoading = false
      }, res => {
        this.$message.error(res)
      })
    },
    parseFilterParams (paramsStr) {
      let params = []
      try {
        params = JSON.parse(paramsStr)
        params.forEach(item => {
          const stringifyParams = item.stringifyParams || []
          stringifyParams.forEach((key) => {
            const tParams = item.params
            tParams[key] = tParams[key] && JSON.parse(tParams[key])
          })
        })
      } catch (e) {
        params = []
      }
      return params
    },

    handleViewClose () {
      this.viewData = ''
      this.paramTpl = []
      this.scriptData = {}
      this.viewDataDialogVisible = false
      this.viewDataDialogTitle = ''
      this.viewParamTplDialogVisible = false
      this.viewParamTplDialogTitle = ''
      this.viewScriptDialogVisible = false
      this.viewScriptDialogTitle = ''
    },

    // 显示Mock数据
    showMockdata (row) {
      this.viewData = row.mockData
      this.viewDataDialogVisible = true
      this.viewDataDialogTitle = 'mock数据：id - ' + row.id
    },

    // 显示接口编排配置
    showGroupConfig (row) {
      this.viewData = row.groupConfig
      this.viewDataDialogVisible = true
      this.viewDataDialogTitle = '接口编排：id - ' + row.id
    },

    // 显示参数模板
    showParamTpl (row) {
      this.paramTpl = bizutil.apiDubboParamTplProcess(row.paramTemplate)
      this.viewParamTplDialogVisible = true
      this.viewParamTplDialogTitle = '参数模板：id - ' + row.id
    },

    // 显示参数模板
    showScript (row) {
      this.scriptData = {
        script: row.script,
        methodName: row.scriptMethodName,
        params: row.scriptParams,
        scriptTypeDisplay: row.scriptTypeDisplay,
        gitProjectId: row.gitProjectId,
        gitPath: row.gitPath,
        gitBranch: row.gitBranch,
        commit: row.commit
      }
      this.viewScriptDialogVisible = true
      this.viewScriptDialogTitle = '脚本信息：id - ' + row.id
    },
    commandHandler (command, data) {
      switch (command) {
        case 'debug':
          this.debugRow(data)
          break
        case 'delete':
          this.delRow(data)
          break
        case 'export':
          this.exportConfigure(data)
          break
        case 'collect':
          this.collectRow(data)
          break
        case 'updateCreator':
          this.updateCreator(data)
          break
        default:
      }
    },
    updateCreator (row) {
      let { id, url, creator } = row
      this.getUserList()
        .then(() => {
          this.updateCreatorForm.id = id
          this.updateCreatorForm.url = url
          this.updateCreatorForm.username = creator
          this.viewUpdateCreatorVisiable = true
        })
    },
    // 删除
    delRow (row) {
      this.$confirm('确认删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        service({
          url: '/apiinfo/del',
          method: 'post',
          data: {
            ids: [row.id]
          }
        }).then(res => {
          this.$message({
            message: '删除成功',
            type: 'success'
          })
          this.getList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    collectRow (row) {
      let { id } = row
      if (row.hasCollected) {
        this.cancelCollect(id)
      } else {
        this.doCollect(id)
      }
    },
    doCollect (id) {
      service({
        url: '/collection/new',
        method: 'post',
        data: {
          apiInfoId: id
        }
      }).then(res => {
        this.$message({
          message: '收藏成功',
          type: 'success'
        })
        setTimeout(() => {
          this.getList()
        }, 1000)
      })
    },
    cancelCollect (id) {
      this.$confirm('取消收藏?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        service({
          url: '/collection/cancel',
          method: 'post',
          data: {
            apiInfoId: id
          }
        }).then(res => {
          this.$message({
            message: '取消收藏成功',
            type: 'success'
          })
          this.getList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        })
      })
    },
    // 批量操作
    handleBatch (cmd) {
      const batchOptRows = this.batchOptRows || []
      if (batchOptRows.length <= 0) {
        this.$message({
          message: '勾选要操作数据',
          type: 'warning'
        })
        return
      }
      switch (cmd) {
        case '1':
          this.handleBatchDel(batchOptRows)
          break
        case '3':
          this.handleBatchExport(batchOptRows)
          break
        default:
          this.$message({
            message: '无效的批量操作',
            type: 'warning'
          })
      }
    },

    // 批量选择按钮处理
    handleSelectionChange (val) {
      var arr = []
      val.forEach(item => {
        arr.push(item.id)
      })
      this.configureId = arr
      this.batchOptRows = val
    },

    // 批量删除服务
    handleBatchDel (batchOptRows) {
      let ids = bizutil.getBatchIdsForOpt(batchOptRows)
      service({
        url: '/apiinfo/del',
        method: 'post',
        data: {
          ids: ids,
          uid: this.userInfo.uuid
        }
      }).then(res => {
        this.$message({
          message: 'API Group删除成功',
          type: 'success'
        })
        setTimeout(() => {
          this.getList()
        }, 1000)
      })
    },
    handleBatchExport (batchOptRows) {
      this.exportConfigure(batchOptRows)
    },
    // handleBatchExport (batchOptRows) { // 复制到粘贴板
    //   let txt = document.createElement('textarea')
    //   txt.value = JSON.stringify(batchOptRows.map(it => {
    //     return {
    //       url: it.url,
    //       groupConfig: this.beautifyJsonStr(it.groupConfig),
    //       paramTemplate: this.beautifyJsonStr(it.paramTemplate),
    //       script: it.script || '',
    //       scriptType: `${it.scriptType}`,
    //       routeType: `${it.routeType}`,
    //       filterParams: '[]',
    //       plugins: []
    //     }
    //   }))
    //   document.body.appendChild(txt)
    //   txt.select()
    //   document.execCommand('Copy')
    //   document.body.removeChild(txt)
    // },
    setApiPriority (item) {
      service({
        url: '/apiinfo/priority',
        method: 'post',
        data: qs.stringify({
          id: item.id,
          priority: item.priority
        })
      }).then(res => {
        this.$message({
          message: '操作成功',
          type: 'success'
        })
        setTimeout(() => {
          this.getList()
        }, 1000)
      })
    },
    // toggleShowCurUserTaskHandler () {
    //   this.getList()
    //   this.setLocalConfig('showCurUserTask', this.showCurUserTask)
    // }
    handleConfigureClosed () {
      this.configureForm.str = ''
    },
    serverLessEditorVisibleChange () {
      this.serverLessEditorVisible = false
    },
    getGroupOptions () {
      service({
        url: '/apigroup/listall'
      }).then(res => {
        const list = res && res.groupList || []
        this.groupOptions = list.map(item => {
          return {
            label: `${item.id} - ${item.name}`,
            value: item.id
          }
        })
      })
    },
    handleInputSearch () {
      this.getList()
    },
    searchChange () {
      this.pageNo = 1
      this.pageSize = 10
    }
  }
}
</script>
<style lang="scss" scoped>
.apiInfo-header {
  display: flex;
  justify-content: space-between;
  &_left {
    width: 20%;
  }
  &_right {
    width: 92%;
    display: flex;
    align-items: center;
    justify-content: flex-end
  }
}
.header {
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  .title {
    font-size: 13px;
    color: #333
  }
  .input_margin {
    width: 20%;
    margin-right: 10px
  }
  .dropdown_margin {
    margin-left: 5px;
    cursor: pointer
  }
}
.right-op{
  .el-dropdown-link {
    width: 82px;
    cursor: pointer;
    color: #409EFF;
  }
  .el-icon-arrow-down {
    font-size: 12px;
  }
}
</style>
<style>
.CodeMirror-lint-tooltip{
  position: relative;
  z-index: 3000;
  background-color: black;
  color: #f4bf75;
}
.CodeMirror-lint-marker-error{
  padding-left: 10px;
}
.CodeMirror-lint-mark-error{
  border-bottom: 3px solid red;
  background-image: none;
}
/* .CodeMirror-cursors .CodeMirror-cursor{
  height: 30px !important;
} */
.api-table-expand {
  margin-left: 65px;
  font-size: 0;
}
.api-table-expand label {
  width: 150px;
  color: #99a9bf;
}
.api-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 40%;
}
.el-dropdown-styled{
  margin-left: 10px;
}
</style>
