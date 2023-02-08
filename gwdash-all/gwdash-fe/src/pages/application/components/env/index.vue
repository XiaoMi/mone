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
      <div class="header">
        <div class="header-env">环境配置</div>
        <div class="header-options">
          <!--
          <span @click="showApproveDialog">
            <el-tooltip content="发起上线审批" placement="top">
              <el-link icon="el-icon-menu" :underline="false" style="font-size: 18px"></el-link>
            </el-tooltip>
          </span>
          -->
          <span @click="handleEnvDialog('添加环境','/project/env/add')">
            <el-link icon="el-icon-circle-plus-outline" :underline="false" style="font-size:18px"></el-link>
          </span>
        </div>
      </div>
      <div class="header-grouping">
          <span>环境分组： </span>
          <div
            v-for="item in grouping" :key="item.id"
            :class="item.isShow == true ? item.isActive == true? 'showProfile isActive': 'showProfile' : 'hiddenProfile'"
            @click="groupingFilter(item)"
          >
           {{item.profile}}
          </div>
      </div>
      <el-alert
          v-if="envList.length === 0"
          type="warning"
          title="项目未配置环境, 请添加环境"
          :closable="false"/>
      <div v-else>
         <el-card :body-style="{ padding: '0px' , position : relative}" class="project-env"  v-for="(item, index) in showEnvList" :key="index" shadow='hover'>
            <!-- <div :class="item.group == 'dev' ? 'project-env-dev' : 'project-env-online'"></div> -->
            <div class="project-env-header">
              <div class="category">{{ item.name }}</div>
              <div class="project-env-header-ops">
                <span v-if="isSuperRole" @click="getBillReport(item.id)">
                  <el-link icon="el-icon-price-tag" :underline="false" style="font-size: 14px"></el-link>
                </span>
                <span @click="handleEnvDialog('环境编辑', '/project/env/edit', item)">
                  <el-link icon="el-icon-edit-outline" :underline="false" style="font-size: 14px"></el-link>
                </span>
                <span
                  @click="delProjectEnv(item.id)">
                  <el-link icon="el-icon-delete" :underline="false" style="font-size: 14px"></el-link>
                </span>
                <span @click="buildProject(item)">
                  <el-tooltip content="部署" placement="right">
                    <el-link icon="el-icon-video-play" :underline="false" style="font-size: 16px"></el-link>
                  </el-tooltip>
                </span>
              </div>
            </div>
            <div v-if="item.deployType === 1" class="project-env-footer">
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}`}"
                    >当前部署</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=2`}"
                    >部署历史</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=3`}"
                    >构建配置</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=4`}"
                    >部署策略</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=5&type=${projectForm.type}`}"
                    >部署配置</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                        style="color: #666666"
                        :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=6`}"
                    >资源管理</router-link>
              </el-button>
            </div>
            <div v-else-if="item.deployType === 2" class="project-env-footer">
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}`}"
                  >当前部署</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=2`}"
                  >部署历史</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=4`}"
                    >部署策略</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=3`}"
                  >构建配置</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=7`}"
                  >部署配置</router-link>
              </el-button>
              <el-button v-if="isSuperRole" size="mini" class="cursor-pointer project-env-tag">
                <router-link
                        style="color: #666666"
                        :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=6`}"
                    >周期部署</router-link>
              </el-button>
            </div>
            <div v-else-if="item.deployType === 3" class="project-env-footer">
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}`}"
                  >当前部署</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=2`}"
                  >部署历史</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=4`}"
                    >部署策略</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                 <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=7`}"
                  >部署配置</router-link>
              </el-button>
              <el-button v-if="isSuperRole" size="mini" class="cursor-pointer project-env-tag">
                <router-link
                        style="color: #666666"
                        :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=6`}"
                    >周期部署</router-link>
              </el-button>
            </div>
            <div v-else-if="item.deployType === 4" class="project-env-footer">
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                    style="color: #666666"
                    :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=2`}"
                  >部署历史</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=4`}"
                    >构建配置</router-link>
              </el-button>
              <el-button size="mini" class="cursor-pointer project-env-tag">
                 <router-link
                      style="color: #666666"
                      :to="{path: `/application/env_setting/${projectId}/${item.id}/${item.deployType}?active=7`}"
                  >发布配置</router-link>
              </el-button>
            </div>
            <div class="project-env-price"><div>本月CPU花销:</div><span style="float:right">{{item.price}}元</span></div>
            <div class="project-env-cost">
                <i class="el-icon-question" @click="getCostDetail(item)"></i>
            </div>
         </el-card>
      </div>

      <!-- 环境添加/编辑 -->
      <el-dialog :title="handleEnvTitle" :visible.sync="dialogEditEnvVisible" width="800px">
        <el-form :model="editEnvForm" ref="editEnvForm" label-width="110px" :rules="editEnvRules">
          <el-form-item label="环境名称" prop="name">
            <el-input v-model="editEnvForm.name" placeholder="请输入环境名称" style="width:50%"></el-input>
          </el-form-item>
          <el-form-item label="环境分组" prop="group">
            <el-select v-model="editEnvForm.group" style="width:50%" @change="groupChange" :disabled="handleEnvTitle === '环境编辑'">
              <el-option v-for="item in envGroupOptions" :key="item.key" :label="item.value" :value="item.key"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="部署权限" prop="authority">
            <el-select v-model="editEnvForm.authority" placeholder="请选择" style="width:50%">
              <el-option v-if="editEnvForm.deployType == 1" label="work" :value="2"></el-option>
              <el-option label="root" :value="1"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="部署方式" prop="deployType">
            <el-select v-model="editEnvForm.deployType" placeholder="请选择" style="width:50%" :disabled="handleEnvTitle === '环境编辑'">
              <el-option v-if="handleEnvTitle == '环境编辑'" label="物理机" :value="1"></el-option>
              <el-option label="docker" :value="2"></el-option>
              <el-option label="dockerfile" :value="3"></el-option>
              <el-option label="前端静态资源" :value="4"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="测试用例" prop="testService">
            <el-select v-model="editEnvForm.testService" placeholder="请选择" style="width:50%">
              <el-option
                v-for="item in testServiceList"
                :key="item"
                :label="item"
                :value="item">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="branch">
            <el-input v-model="envBranch"  style="width:50%"/>
          </el-form-item>
          <el-form-item label="profile">
            <el-input v-model="editEnvForm.profile" style="width:50%"/>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
            <el-button @click="dialogEditEnvVisible = false">取 消</el-button>
            <el-button type="primary" @click="submitProjectEnv('editEnvForm')">确 定</el-button>
        </span>
      </el-dialog>

      <!-- commit id审批 -->
      <el-dialog title="线上环境审批"  :visible.sync="dialogApproveVisible" width="800px" @close='approveDialogClosed'>
        <el-form :model="approveForm" :rules="approveRules"  label-width="110px">
          <el-form-item label="分支" prop="branch">
            <el-input
              style="width:50%"
              v-model="approveForm.branch"
              @change="showApproveCommits"
              placeholder="填写审核分支"></el-input>
          </el-form-item>
          <el-form-item label="版本选择">
            <el-select v-model="approveForm.commitId" style="width:50%">
              <el-option
                 v-for="item in commitIds"
                 :key="item.id"
                 :value="item.id"
                 :label="`${item.id}`"
                 class="commitOption" >
                <span>{{ item.id}}</span>
                <span>{{ item.message && item.message.slice(0,80) }}</span>
                <span>{{ item.committed_date }}</span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item class="form-footer">
            <el-button type="primary" @click="approveApply">审批申请</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
      <el-dialog title='花销明细' :visible.sync='dialogCostVisible' width='800px'>
        <div class="expense-details-container">
            <div class="expense-details-total">
              <span>总花销： </span>
              <span>{{totalPrice}}元</span>
            </div>
            <div class="expense-details-table">
              <el-table stripe :data="tableCostList" class="table-list">
                <el-table-column prop="cpu" label="CPU数量/个" width="100"></el-table-column>
                <el-table-column prop="cpuPrice" label="CPU价格/小时" width="100"></el-table-column>
                <el-table-column prop="ctime" label="变更日期" width="188"></el-table-column>
                <el-table-column prop="duration" label="使用时长" width="190"></el-table-column>
                <el-table-column prop="sumPrice" label="花销" max-width="170"></el-table-column>
              </el-table>
            </div>
          </div>
      </el-dialog>
   </div>
</template>

<script>
import service from '@/plugin/axios/index'
import envMap from '../../env_map'
import { mapState, mapMutations } from 'vuex'
import qs from 'qs'
import bizutil from '@/common/bizutil'
import store from '@/store/index'
const isSuperuser = (userInfo.roles || []).find(item => item.name === 'project-superuser')
// const isSuperuser = true
export default {
  props: {
    id: {
      type: Number,
      required: true
    },
    projectForm: {
      type: Object,
      required: true
    },
    envPrice: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      isSuperuser: !!isSuperuser,
      isSuperRole: (((userInfo && userInfo.roles) || []).findIndex(it => it.name === 'SuperRole') !== -1),
      dialogEditEnvVisible: false,
      dialogApproveVisible: false,
      handleEnvTitle: '',
      envList: [],
      editEnvForm: {},
      envGroupOptions: envMap.options,
      dialogPublishVisible: false,
      approveForm: {
        commitId: '',
        branch: ''
      },
      commitIds: [],
      bpRules: {
        envId: [
          { required: true, message: '选择环境', trigger: 'blur' }
        ]
      },
      editEnvRules: {
        name: [
          { required: true, message: '请输入', trigger: 'blur' },
          { min: 1, max: 10, message: '长度在 1 到 10 个字符', trigger: 'blur' }
        ],
        group: [
          { required: true, message: '请选择部署环境', trigger: 'blur' }
        ],
        authority: [
          { required: true, message: '请选择部署权限', trigger: 'blur' }
        ],
        deployType: [
          { required: true, message: '请选择部署方式', trigger: 'blur' }
        ]
      },
      approveRules: {
        branch: [
          { required: true, message: '请输入', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字符', trigger: 'blur' }
        ]
      },
      projectId: this.id,
      handleUrl: '',
      envBranch: '',
      grouping: [
        {
          id: 0,
          profile: '全部',
          flag: 'all',
          isShow: true,
          isActive: true
        },
        {
          id: 1,
          profile: '日常',
          flag: 'dev',
          isShow: true,
          isActive: false
        },
        {
          id: 2,
          profile: 'staging',
          flag: 'staging',
          isShow: true,
          isActive: false
        },
        {
          id: 3,
          profile: '内网',
          flag: 'intranet',
          isShow: true,
          isActive: false
        },
        {
          id: 4,
          profile: '预发',
          flag: 'preview',
          isShow: true,
          isActive: false
        },
        {
          id: 5,
          profile: '线上集群',
          flag: 'production',
          isShow: true,
          isActive: false
        },
        {
          id: 6,
          profile: '线上c3集群',
          flag: 'c3',
          isShow: true,
          isActive: false
        },
        {
          id: 7,
          profile: '线上c4集群',
          flag: 'c4',
          isShow: true,
          isActive: false
        }
      ],
      testServiceList: [],
      showEnvList: [],
      totalPrice: Number,
      tableCostList: [],
      dialogCostVisible: false,
      applicationActive: 0
    }
  },
  created () {
    this.getProjectEnvs(this.id)
    this.testServices()
  },
  watch: {
    id: function () {
      this.envList = []
      this.getProjectEnvs(this.id)
      this.projectId = this.id
    },
    envPrice () {
      this.getProjectEnvs(this.id)
    }
  },
  methods: {
    // 获取环境列表
    getProjectEnvs (id) {
      service({
        url: `/project/env/list?projectId=${id}`
      }).then(list => {
        if (!Array.isArray(list)) return
        list.forEach((item) => {
          let priceArr = this.envPrice.filter((ele) => {
            return ele.subBizId === item.id
          })
          if (priceArr.length !== 0) {
            item.price = priceArr[0].price
            item.subBizId = priceArr[0].subBizId
          } else {
            item.price = 0
          }
        })
        this.envList = list
        this.initSelect(list)
        if (localStorage.getItem("applicationActive") != null) {
          this.applicationActive = localStorage.getItem("applicationActive")
        }
        this.groupingFilter(this.grouping[this.applicationActive])
      })
    },
    testServices () {
      return service({
        url: '/test/case/services',
        method: 'GET'
      }).then(testServiceList => {
        this.testServiceList = testServiceList
      })
    },
    initSelect (list) {
      this.grouping.forEach(item => {
        list.some(e => e.group === item.flag)
          ? item.isShow = true
          : item.isShow = false
      })
      this.grouping[0].isShow = true
    },
    groupingFilter (item) {
      localStorage.setItem("applicationActive", item.id)
      this.applicationActive = item.id
      this.grouping.forEach((item1) => { item1.isActive = false })
      item.isActive = true
      item.flag === 'all'
        ? this.showEnvList = this.envList
        : this.showEnvList = this.envList.filter(e => e.group === item.flag)
    },
    // 环境添加/编辑
    handleEnvDialog (title, url, form) {
      this.editEnvForm = {}
      this.envBranch = ''
      this.handleEnvTitle = title
      this.handleUrl = url
      if (title === '环境编辑') {
        this.envBranch = form.branch
        this.editEnvForm = { ...form }
      }
      this.dialogEditEnvVisible = true
    },
    // 环境操作-> 提交
    submitProjectEnv (formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning'
          })
          return
        }
        this.dialogEditEnvVisible = false
        service({
          url: this.handleUrl,
          method: 'POST',
          data: { ...this.editEnvForm, projectId: this.id, branch: this.envBranch }
        }).then(res => {
          this.editEnvForm = {}
          this.$message({
            type: 'success',
            message: '操作成功'
          })
          this.getProjectEnvs(this.id)
        }).catch(() => {
          this.$message({
            type: 'warning',
            message: '请求出错：/project/env/add'
          })
        })
      })
    },
    // 环境删除
    delProjectEnv (id) {
      this.$confirm('此操作将永久删除该环境，是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        service({
          url: `/project/env/del`,
          method: 'POST',
          data: qs.stringify({ id })
        }).then(res => {
          this.$message.success('删除成功')
          this.getProjectEnvs(this.id)
        })
      }).catch((e) => {
        this.$message({
          type: 'info',
          message: '已取消操作'
        })
      })
    },

    buildProject (item) {
      this.$router.push(`/application/deploy?id=${this.id}&envId=${item.id}`)
    },

    groupChange (val) {
      this.envBranch = val
      this.editEnvForm = {
        ...this.editEnvForm,
        profile: val
      }
    },

    showApproveDialog () {
      this.dialogApproveVisible = true
    },

    showApproveCommits () {
      const approveForm = this.approveForm
      const branch = approveForm.branch
      if (!approveForm.branch) return
      const projectId = this.projectId
      service({
        url: '/project/env/commits',
        method: 'POST',
        data: qs.stringify({
          projectId: projectId,
          branch: branch
        })
      }).then(commitIds => {
        if (Array.isArray(commitIds)) {
          this.commitIds = commitIds
          this.approveForm.commitId = commitIds[0] && commitIds[0].id
        }
      }).catch(() => {
        this.$message.error("获取审核commit id失败")
      })
    },

    approveApply () {
      const approveForm = this.approveForm
      const branch = approveForm.branch
      const commitId = approveForm.commitId
      const projectId = this.id
      if (!branch) return
      service({
        url: '/flow/create',
        method: 'POST',
        data: {
          type: 2,
          projectId: projectId,
          branch: branch,
          commitId: commitId
        }
      }).then(res => {
        this.dialogApproveVisible = false
      })
    },
    approveDialogClosed () {
      this.approveForm.branch = ''
      this.approveForm.commitId = ''
      this.commitIds = []
    },
    getBillReport (envId) {
      const projectId = this.id
      service({
        url: `/project/bill/report/test?projectId=${projectId}&envId=${envId}`,
        method: "GET"
      }).then(res => {
        this.$alert(JSON.stringify(res), '账单报表', {
          confirmButtonText: '确定',
          callback: action => {}
        })
      })
    },
    getCostDetail (item) {
      let projectId = item.projectId
      let envId = item.id
      service({
        url: `/billing/detail/look?projectId=${projectId}&envId=${envId}`,
        method: "get"
      }).then(res => {
        this.totalPrice = res.price
        this.tableCostList = (res.list || []).map((ele) => {
          ele.duration = this.getDuration(ele)
          ele.ctime = bizutil.timeFormat(ele.ctime)
          return ele
        })
        this.dialogCostVisible = true
      })
    },
    getDuration (ele) {
      let arr = [
        { num: ele.year, flag: '年' },
        { num: ele.month, flag: '个月' },
        { num: ele.day, flag: '天' },
        { num: ele.hour, flag: '小时' }
      ]
      let newarr = arr.filter((item) => {
        return item.num !== 0
      })
      let a = ''
      newarr.forEach((item, index) => {
        a = a + String(item.num) + item.flag
      })
      if (!a) {
        a = '不足一小时'
      }
      return a
    }
  }
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: space-between;
  padding: 0px 6px 10px 0px;
  &-env {
    color: #333333;
    font-family: PingFang SC;
    font-weight: regular;
    font-size: 14px;
    line-height: normal;
    letter-spacing: 0px;
    text-align: left;
  }
  &-options {
    span {
      display: inline-block;
      margin-left: 6px;
    }
  }
}

.header-grouping {
  padding: 4px 0px 8px 0px;
  overflow: visible;
  // height: 36px;
  span {
    color: #666666;
    font-family: PingFang SC;
    font-weight: regular;
    font-size: 12px;
    line-height: normal;
    letter-spacing: 0px;
    text-align: center;
  }
  .showProfile {
    display: inline-block;
    margin: 0 10px 10px 0;
    border-radius: 2px;
    background: #EFF0F4;
     color: #666666;
    font-family: PingFang SC;
    font-weight: regular;
    font-size: 12px;
    line-height: normal;
    letter-spacing: 0px;
    text-align: center;
    padding: 4px 12px;
  }
  .showProfile:hover,.isActive{
      color: #457BFC;
      background: rgba(69,123,252,0.2);
  }
  .hiddenProfile {
    display: none;
  }
}

.project-env {
    position: relative;
    padding: 10px;
    margin-bottom: 14px;
    background: #EFF0F4;
    height: 90px;
    &-dev {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        background-color: #409EFF;
        height: 3px;
    }
    &-online {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        background-color: #94abdb;
        height: 3px;
    }
    &-header {
        margin-bottom: 8px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px;

        &-ops {
          & > span {
            margin-left: 6px;
            display: inline-block;
          }
        }
    }
    &-footer {

        padding: 10px 5px;
        display: flex;
    }
    &-tag {
        margin: 3px;
        border-radius: 2px;
        background: #FFFFFF;
        border: 1px solid #D8D8D8;
    }
    &-price {
      position: absolute;
      left: 612px;
      bottom: 18px;
      color: #999999;
      font-family: PingFang SC;
      font-weight: regular;
      font-size: 12px;
      line-height: normal;
      letter-spacing: 0px;
      text-align: left;
    }
    &-cost {
      position: absolute;
      left: 593px;
      bottom: 34px;
      .el-icon-question {
        color: #C1C1C1;

      }
    }
}
/deep/.el-dialog {
  .el-dialog__header {
    border-bottom: 1px solid #EFF0F4;
    border-left: 4.1px solid #457BFC;
    span {
      color: #333333;
      font-family: PingFang SC;
      font-weight: medium;
      font-size: 14px;
    }
  }
  .el-dialog__body {
    padding: 20px 20px;
    .expense-details-container {
      .expense-details-total {
        margin-bottom: 20px;
        color: #333333;
        font-family: PingFang SC;
        font-weight: medium;
        font-size: 14px;
      }
      .expense-details-table {
        max-height: 400px;
        overflow-y: auto;
        .el-table {
           border: 1px solid #EFF0F4;
        }
      }
    }
  }
}
.category {
  font-size: 14px;
  color: #909399;
  font-weight: bold;
  margin-bottom: 0px
}
.form-footer {
  text-align: right;
}
.commitOption.el-select-dropdown__item {
  height: 66px;
  border-bottom: 0.5px solid #ccc;
  padding-top: 5px;
  span{
    display: block;
    line-height: 21px;
  }
}

</style>
