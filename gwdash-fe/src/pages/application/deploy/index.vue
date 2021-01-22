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
    <d2-module v-if="pipeline.length !== 0">
      <div class="pipeline-header">
        <div class="project-info">
          <div class="h2">{{ projectInfo && projectInfo.name }}</div>
          <div class='env'>
            <span>部署环境：</span>
            <span>{{ envInfo && envInfo.name }}</span>
          </div>
          <div v-if="rollback" class='env'>
            <span>回滚commit id：</span>
            <span>{{ pipelineInfo && pipelineInfo.deploySetting && pipelineInfo.deploySetting.commitId }}</span>
          </div>
          <div v-if="rollback" class='env'>
            <span>回滚包：</span>
            <span>{{ pipelineInfo && pipelineInfo.projectCompileRecord && pipelineInfo.projectCompileRecord.jarName }}</span>
          </div>
        </div>
        <div class="pipleline-operation">
          <el-form
            v-if="!rollback"
            :model="commitForm"
            size="mini"
            label-width="110px"
            style="height:28px;margin-right:12px">
            <el-form-item label='commitId:'>
              <el-select
                v-model="deplyType"
                placeholder="">
                <el-option
                  label="当前分支"
                  :value="1"
                ></el-option>
                <el-option
                  label="过去部署分支"
                  :value="2"
                ></el-option>
              </el-select>
              <el-select
                v-model="commitForm.commitId"
                placeholder="请选择commitId"
                @change='commitIdChange'
                style="width:200px"
                :disabled='!isStart'>
                <el-option
                  v-for="item in commitListShow"
                  :key="item.id"
                  :value="item.id"
                  :label="`${item.id.slice(0,10)}`"
                  class="commitOption"
                  :title='`提交信息:\n\n${item.message}`'>
                  <span>{{ item.id.slice(0,10) }}</span>
                  <span>{{ item.message.length > 20 ? `${item.message.slice(0,20)}...` : item.message}}</span>
                  <span>{{ item.committer_name }}</span>
                  <span>{{ item.committed_date }}</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-form>
          <el-button
            class='run_button'
            size="mini"
            :disabled="!isStart"
            @click="startPipeline">{{rollback ? '回滚' : '运行'}}</el-button>
          <el-button
            v-if='showUrgentButton'
            class='worry_button'
            size="mini"
            style='margin-left:10px'
            :disabled="!isStart || (!testAuditProgress && !testAuditPass)"
            @click='urgentRelease'>紧急发布</el-button>
          <el-button
            v-if='pipeline[pipeline.length-1].stage.status === 1'
            type="primary"
            size="mini"
            @click='refresh'>刷新</el-button>
          <el-dropdown
            v-else
            class="last-item"
            @command="closePipeline">
            <el-button
              class='back_button'
              size="mini"
              :disabled="isStart">
              退出<i class="el-icon-arrow-down el-icon--right"></i>
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="success">部署成功</el-dropdown-item>
              <el-dropdown-item command="fail">部署失败</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <div class="pipeline-graph-container">
        <div v-if='showTestAudit'
             :class='`test_audit card 
                   ${stepTestActive == 0 || stepTestActive == 1  ? "running": ""}
                   ${testAuditPass ? "" : ""}`'>
          <div class='title'>1 发起测试审核</div>
          <div class='content'>
            <el-select multiple v-model='reviewers' placeholder='请选择测试审核人员' size='mini' filterable default-first-option class='select'>
              <el-option 
                v-for='item in reviewerList'
                :key='item.userName'
                :value='item.userName'
                :label='item.name'/>
            </el-select>
            <el-button class='button' 
                @click='reviewerSubmit' 
                :disabled='stepTestActive == -1 || stepTestActive == 2'>提交</el-button>
          </div>
          <div class='footer'>
            <div class='no-start' v-if='stepTestActive == -1'>未开始</div>
            <div class='no-start' v-else-if='stepTestActive == 0'>开始</div>
            <div class='no-start' v-else-if='stepTestActive == 1'>审核中</div>
            <div class='result' v-else>
              <div class='left'>审核结果</div>
              <div class='right'>
                <span>{{testAuditPass ? "通过" : '不通过'}}</span>
                <span :class='`icon ${testAuditPass ? "pass" : "nopass"}`'>
                  <div class='reason'>
                    <span>驳回原因：</span>
                    <p>{{noPassReason}}</p>
                  </div>
                </span>
              </div>
            </div>
          </div>
        </div>
        <div v-for="(item, index) in pipeline" :key="item.id" 
             :class='`deploy_process ${!showTestAudit ? "no_test" : ""}`'>
          <div class='line' v-if='!showTestAudit && index !== 0'></div>
          <div class='line' v-else-if='showTestAudit'></div>
          <div :class='`pipeline-stage card
               ${index == currentIndx ? "active": ""}
               ${item.stage.status == 0 ? "running" : 
                (item.stage.status == 2 ? "error" :
                (item.stage.status == 1 ? "success" : ""))}`'
                @click="switchTab(index)">
            <div class="pipeline-stage-header title">
              <div class="stage-name">
                <span v-if='showTestAudit'>{{index+2}}</span>
                <span v-else>{{index+1}}</span>
                {{item.name}}
              </div>
              <div class="extra-info">
                <span>{{item.stage.time}}秒</span>
              </div>
            </div>
            <div class="pipeline-stage-progress">
              <!-- <div class="progress-step">({{index + 1}})</div> -->
              <div class="progress-label desc" v-if="index == 2">
                {{envInfo.deployType === 2 ? 'docker部署': item.desc}}
              </div>
              <div class="progress-label desc" v-else>{{item.desc}}</div>
            </div>
            <div class='pipeline-stage-footer footer'>
              <div v-if="item.stage.status == -1" class='no-start'>未开始</div>
              <div v-else-if="item.stage.status == 0" class='running'>运行中</div>
              <div class='result' v-else>
                 <el-tag type="success" size='mini' v-if="item.stage.status == 1">成功</el-tag>
                 <el-tag type="danger" size='mini' v-if="item.stage.status == 2">失败</el-tag>
                 <div class='right'>
                   <div v-for="(action, index) in item.actions" :key="index">
                    <div
                      v-if="(action.step == null
                        || action.step == item.stage.active)
                        && action.status == item.stage.status"
                      @click.stop="actions(action.cmd, item)">{{action.name}}</div>
                   </div>
                 </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class='pipeline-steps'>
        <div class='pipeline-steps-test' v-if='showTestAudit && !testAuditPass'>
          <div class='title'>发起测试审核</div>
          <el-steps :active='stepTestActive' :process-status="stepTestStatus" finish-status="success">
            <el-step title='开始'></el-step>
            <el-step title='审核中'></el-step>
            <el-step title='完成'></el-step>
          </el-steps>
        </div>
        <div class='pipeline-steps-deploy' v-else-if='!showTestAudit || testAuditPass'>
          <div class='title'>{{pipeline[currentIndex] && pipeline[currentIndex].name}}</div>
          <el-steps
            :active="stage.active"
            finish-status="success"
            :process-status="stage.processStatus">
            <el-step v-for="item in stage.steps" :key="item.id" :title="item.name" />
          </el-steps>
        </div>
      </div>
      <div v-if="footer && !showTestAudit || (showTestAudit && testAuditPass)" class="footer">
        <el-tabs>
          <el-tab-pane
            v-for="(item, index) in footer"
            :key="index"
            :label="item.name"
            :name="`${index}`">
            <div v-if="item.type == 'log'">
              <ConsoleBox :text="item.data" />
                <div>
                  <el-link type="primary" style="marginTop:10px" @click='hrefAllConsole'>查看所有日志</el-link>
                 </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
      <div v-if="deployInfo && currentIndex == 2 && (!showTestAudit || (showTestAudit && testAuditPass))" class="footer">
        <div v-if='deployInfo.deployBatches && deployInfo.deployBatches.length == 0'>
          <el-alert title="暂时'无机器部署'，请联系架构组同学" type="warning"/>
        </div>
        <div
          v-for="(item, index) in deployInfo.deployBatches"
          :key="index"
          :class="`batch-box
          ${item.status == 2 ? 'batch-box-active' : ''}
          ${item.status == 3 || item.status == 4 ? 'batch-box-part-fail' : ''}
          ${item.status == 5 ? 'batch-box-fail' : ''}
          ${item.status == 6 ? 'batch-box-success' : ''}`">
          <div class="batch-box-header">
            <div v-if="item.fort" class="batch-box-title">堡垒批次</div>
            <div v-else class="batch-box-title">批次{{item.batch}}</div>
            <div class="batch-item-options">
              <el-button
                v-if="item.status == 1 || isSuperRole"
                size="mini"
                type="primary"
                @click="startBatch(item)"
              >部署</el-button>
              <el-button
                @click="retryBatch(item)"
                size="mini"
                type="primary"
                v-else-if="item.status == 3
                || item.status == 4
                || item.status == 5"
              >重试</el-button>
            </div>
          </div>
          <div class="batch-box-body">
            <div
              v-for="(deployMachine, index) in item.deployMachineList"
              :key="index"
              class="batch-item">
              <div class="batch-item-name">
                {{deployMachine.ip}}
                <span
                  class="tag tag-success"
                  v-if="deployMachine.status == 1"
                >部署成功</span>
                <span class="tag tag-fail" v-else-if="deployMachine.status == 2">部署失败</span>
                <span class="tag tag-active" v-else-if="deployMachine.status == 0">部署中</span>
                <span class="tag tag-info" v-else-if="deployMachine.status == 4">等待部署</span>
                <span class="tag">耗时{{(deployMachine.time / 1000).toFixed(2)}}秒</span>
              </div>
              <div class="batch-item-options">
                <div
                  v-if="deployMachine.status == 0
                  || deployMachine.status == 1
                  || deployMachine.status == 2"
                  @click="deployLog(deployInfo, deployMachine)">
                  <el-popover placement="right" width="600" trigger="click">
                    <ConsoleBox v-if="deployInfo.log" :text="deployInfo.log" />
                    <span slot="reference">查看日志</span>
                  </el-popover>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </d2-module>
    <d2-module v-if='pipelineNoExist'>
      <el-alert
        title="部署方式对应pipeline不存在"
        type="error"
        effect="dark"
        :closable="false">
      </el-alert>
    </d2-module>
  </d2-container>
</template>
<script>
import qs from "qs";
import request from "@/plugin/axios/index";
import bizutil from "@/common/bizutil";
import SockJS from "sockjs-client";
import { taskStep, deployStep, status } from "./status-map.js";

import ConsoleBox from "./components/console.vue";
import pipelineCategory from './pipeline-category/index';
let query = {};
let ws = {};

const CODE_CHECK_SUBSCRIBE = "code-check-subscribe";
const COMPILE_SUBSCRIBE = "compile-subscribe";
const DOCKER_BUILD_SUBSCRIBE = "docker-build-subscribe";
const DEPLOY_SUBSCRIBE = "deploy-subscribe";

const CODE_CHECK_STAGE = "projectCodeCheckRecord";
const COMPILE_STAGE = "projectCompileRecord";
const DEPLOY_STAGE = "deployInfo";

export default {
  name: "deployPage",
  data() {
    return {
      isSuperRole: (((userInfo && userInfo.roles) || []).findIndex(it => it.name === 'SuperRole') !== -1),
      deployInfo: {},
      projectInfo: {},
      envInfo: {},
      pipelineInfo: {},
      rollback: this.$route.meta.rollback,
      pipeline: [],
      isStart: false,
      currentIndex: 0,
      commitForm: {
        commitId: ''
      },
      commitList: [],
      showTestAudit: false,
      showUrgentButton: false,
      reviewerList: [],
      reviewers: [],
      stepTestActive: -1,
      stepTestStatus: '',
      testAuditResult: false,
      testAuditProgress: false,
      testAuditPass: false,
      noPassReason: '',
      force: false,
      pipelineNoExist: false,
      deplyList: [],
      commitListShow: [],
      deplyType: 1
    }
  },
  computed: {
    stage() {
      const index = this.currentIndex;
      return this.pipeline[index].stage;
    },
    footer() {
      const index = this.currentIndex;
      return this.pipeline[index].footer;
    }
  },
  created() {
    this.getProjectInfo();
    // this.getEnvInfo();
    this.rollback && this.getPipelineInfo();
    // this.initWebSocket().then(() => {
    //   this.initPipeline();
    // }),
    Promise.all([this.initWebSocket(), this.getEnvInfo(), this.getReviewerList()]).then(() => {
      this.initPipeline();
    })
    this.getCommitList();
    this.getDeplyHistory();
  },
  beforeDestroy() {
    this.socket && this.socket.close();
  },
  watch:{
    deplyType: function( ){
      console.log('deplyType change')
      this.deplyType == 1 ? this.commitListShow = this.commitList : this.commitListShow = this.deplyList
    }
  },
  methods: {
     getDeplyHistory () {
      request({
        url: "/project/env/history/commits",
        method: "POST",
        data: qs.stringify({
          envId: this.$route.query.envId
        })
      }).then(res => {
            this.deplyList = res
      });
    },
    hrefAllConsole(){
      let storage = window.localStorage
      storage.setItem('a', JSON.stringify(this.footer))
      let routeUrl = this.$router.resolve({
        path: "/deploy/allConsole",
        // query : this.footer[0]
      });
      window.open(routeUrl.href, '_blank');
    },
    getProjectInfo() {
      const query = this.$route.query;
      request({
        url: `/project/info?id=${query.id}`
      }).then(projectInfo => {
        this.projectInfo = projectInfo
      });
    },
    getEnvInfo() {
      const query = this.$route.query;
      return request({
        url: `/project/env/info?id=${query.envId}`
      }).then(envInfo => {
        if (envInfo) {
          this.envInfo = envInfo;
          this.pipeline = pipelineCategory[envInfo.deployType] && pipelineCategory[envInfo.deployType]() || [];
          this.pipeline.length === 0 && (this.pipelineNoExist = true)
          // 测试审核
          const proEnv = ['intranet','c3','c4','production']
          if (proEnv.indexOf(envInfo.group) !== -1) {
            this.showTestAudit = this.rollback ? false : true
            this.showUrgentButton = true;
            // this.getReviewerList()
          }
        }
      });
    },
    getPipelineInfo() {
      const query = this.$route.query;
      request({
        url: `/pipeline/info?projectId=${query.id}&id=${query.pipelineId}`
      }).then(pipelineInfo => {
        this.pipelineInfo = pipelineInfo
      });
    },
    initWebSocket() {
      return new Promise((resolve, reject) => {
        const query = this.$route.query;
        let socket = (this.socket = ws = new SockJS(
          `//${window.location.host}/ws/cicd/`
        ));
        socket.onopen = () => {
          console.log("socket开启");
          resolve();
        };
        socket.onmessage = ({ data }) => {
          const dataWrap = JSON.parse(data || "{}");
          const content = JSON.parse(dataWrap.data || "{}")
          switch (dataWrap.msgType) {
            case "code-check-update":
              this.updateStep(
                content,
                CODE_CHECK_STAGE
              );
              break;
            case "code-check-logs":
              this.updateLog(
                content,
                CODE_CHECK_STAGE
              );
              break;
            case "compile-status":
              this.updateStep(
                content,
                COMPILE_STAGE
              );
              break;
            case "compile-logs":
              this.updateLog(
                content,
                COMPILE_STAGE
              );
              break;
            case "docker-build-info":
              this.updateStep(
                content,
                COMPILE_STAGE
              );
              break;
            case "docker-build-logs":
              this.updateLog(
                content,
                COMPILE_STAGE
              );
              break;
            case "deploy-update":
              this.updateDeployInfo(content)
              this.updateStep(content[DEPLOY_STAGE], DEPLOY_STAGE)
              break;
          }
        };
        socket.onclose = () => {
          console.log("socket已关闭");
          reject();
        };
        socket.onerror = () => {
          console.log("socket发生了错误");
          reject();
          this.$message({
            message: "webSocket异常",
            type: "warning"
          });
        };
      });
    },
    switchTab(index) {
      this.currentIndex = index
    },
    initPipeline() {
      const query = this.$route.query;
      request({
        url: "/pipeline/present",
        method: "POST",
        data: qs.stringify({
          projectId: query.id,
          envId: query.envId,
          pipelineId: query.pipelineId || 0
        })
      }).then(initData => {
        if (initData) {
          if (this.showTestAudit) {
            this.updateTestInfo(initData.review);
          } 
          this.isStart = false;
          this.updateDeployInfo(initData);
          this.commitForm.commitId = initData.deploySetting && initData.deploySetting.commitId
          this.updatePipeline(initData);
          if(JSON.parse(JSON.stringify(this.pipeline[0].stage)).status != -1 &&  !this.testAuditPass){
            this.showTestAudit = false
          }

          return
        }
        this.isStart = true
      });
    },
    updateDeployInfo(pipeline) {
      if (pipeline.deployInfo) {
        this.deployInfo = {
          log: '',
          id: pipeline.id,
          ...pipeline.deployInfo
        }
      }
    },
    updatePipeline(initData) {
      if (initData) {
        const pipeline = this.pipeline;
        let len = pipeline.length;
        let lastIndex = len;
        for (let i = len - 1; i >= 0; i--) {
          const it = pipeline[i];
          const stage = initData[it.key];
          if (stage) {
            this.sendSubscribe(it.subscribe);
            this.updateStepContent(it, stage, it.key, i);
          } else {
            lastIndex = i;
          }
        }
        this.currentIndex = lastIndex > 0 ? lastIndex - 1 : lastIndex;
      }
    },
    actions(cmd, item) {
      if (typeof this[cmd] == "function") {
        this[cmd](item);
      }
    },
    sendSubscribe(msgType) {
      const query = this.$route.query;
      this.socket.send(
        JSON.stringify({
          msgType,
          data: JSON.stringify({
            projectId: query.id,
            envId: query.envId
          })
        })
      )
    },
    startCompile() {
      const query = this.$route.query;
      request({
        url: "/pipeline/startCompile",
        method: "post",
        data: qs.stringify({
          projectId: query.id,
          envId: query.envId
        })
      }).then(pipeline => {
        if (pipeline) {
          this.isStart = false;
          this.sendSubscribe(COMPILE_SUBSCRIBE);
          this.updateStep(pipeline[COMPILE_STAGE], COMPILE_STAGE);
          this.$message.success("开始构建");
        }
      });
    },
    startDockerBuild() {
      const query = this.$route.query;
      request({
        url: "/pipeline/startCompile",
        method: "post",
        data: qs.stringify({
          projectId: query.id,
          envId: query.envId
        })
      }).then(pipeline => {
        if (pipeline) {
          this.isStart = false;
          this.sendSubscribe(DOCKER_BUILD_SUBSCRIBE);
          this.updateStep(pipeline[COMPILE_STAGE], COMPILE_STAGE);
          this.$message.success("开始构建");
        }
      });
    },
    updateStep(data, key) {
      const len = this.pipeline.length;
      for (let i = 0; i < len; i++) {
        const item = this.pipeline[i];
        if (item && item.key == key) {
          this.updateStepContent(item, data, key, i);
          break;
        }
      }
    },
    updateStepContent(pipelineItem, updateItem, key, i) {
      pipelineItem.downloadUrl = updateItem.url || "";
      pipelineItem.stage.active = updateItem.step;
      pipelineItem.stage.time = (updateItem.time / 1000).toFixed(2);
      pipelineItem.stage.status = updateItem.status;
      pipelineItem.stage.processStatus = status[updateItem.status].type;

      // 是否执行下一个阶段
      const nextStage = this.pipeline[i + 1] && this.pipeline[i + 1].stage;
      if (
        updateItem.status == 1 &&
        nextStage &&
        nextStage.active == -1 &&
        nextStage.status == -1
      ) {
        // this.$message.success("开启下一阶段")
        this.currentIndex = i + 1;
        typeof this[nextStage.startAction] == "function" &&
          this[nextStage.startAction]();
      }
    },
    updateLog(data, key) {
      const len = this.pipeline.length;
      for (let i = 0; i < len; i++) {
        const item = this.pipeline[i];
        if (item && item.key == key) {
          const footerItem = item.footer.find(it => it.type == "log");
          if (footerItem) {
            footerItem.data = `${footerItem.data || ""}${data.message || ""}`;
          }
          break;
        }
      }
    },
    startDeploy() {
      const query = this.$route.query;
      request({
        url: "/pipeline/startDeploy",
        method: "post",
        data: qs.stringify({
          projectId: query.id,
          envId: query.envId
        })
      }).then(pipeline => {
        if (pipeline) {
          this.updateDeployInfo(pipeline)
          this.sendSubscribe(DEPLOY_SUBSCRIBE)
          this.updateStep(pipeline[DEPLOY_STAGE], DEPLOY_STAGE)
        }
      });
    },
    startPipeline() {
      if (!this.rollback && this.commitForm.commitId === "") {
        this.$message.warning('请先选择commitId');
        return
      }
      if (this.showTestAudit) {
        if (!this.testAuditProgress && !this.testAuditPass) {
          this.$message.warning('审核未通过，后续流程不进行');
          return;
        }
        if (this.testAuditProgress) {
          this.$message.warning('审核流程中，后续流程暂不进行');
          return
        }
      }
      const query = this.$route.query;
      const commitForm = this.commitForm;
      const force = this.force;
      this.isStart = false;
      request({
        url: "/pipeline/startPipeline",
        method: "post",
        data: qs.stringify({
          projectId: query.id,
          envId: query.envId,
          pipelineId: query.pipelineId,
          commitId: commitForm.commitId,
          force
        })
      }).then(initData => {
        if (!initData) return;
        this.updatePipeline(initData);
        const firstStage = this.pipeline[0];
        if (initData[firstStage.key] == null) {
          // 开始执行
          typeof this[firstStage.stage.startAction] == "function" &&
            this[firstStage.stage.startAction]();
        }
      })
    },
    closePipeline(command) {
      const query = this.$route.query;
      this.$confirm("此操作将退出当前运行pipeline, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          request({
            url: "/pipeline/closePipeline",
            method: "post",
            data: qs.stringify({
              projectId: query.id,
              envId: query.envId,
              cmd: command
            })
          }).then(isPass => {
            if (isPass) {
              this.$message({
                type: "success",
                message: "退出成功"
              });
              window.location.reload();
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消"
          });
        });
    },
    deployLog(deployInfo, deployMachine) {
      request({
        url: `/log/deploy?id=${deployInfo.id}&ip=${deployMachine.ip}`
      }).then(log => {
        deployInfo.log = log;
      });
    },
    downloadJar(item) {
      if (item.downloadUrl) {
        window.location.href = item.downloadUrl;
      }
    },
    startBatch(item) {
      const query = this.$route.query;
      let url = "/pipeline/startBatch";
      if (item.fort) {
        url = "/pipeline/startFort";
      }
      request({
        url,
        method: "post",
        data: qs.stringify({
          projectId: query.id,
          envId: query.envId,
          batchNum: item.batch
        })
      }).then(pipeline => {
        if (pipeline) {
          this.updateDeployInfo(pipeline)
          this.updateStep(pipeline[DEPLOY_STAGE], DEPLOY_STAGE)
        }
      });
    },
    retryBatch(item) {
      const query = this.$route.query;
      const url = "/pipeline/retryBatch";
      request({
        url,
        method: "post",
        data: qs.stringify({
          projectId: query.id,
          envId: query.envId,
          batchNum: item.batch
        })
      }).then(pipeline => {
        if (pipeline) {
          this.updateDeployInfo(pipeline)
          this.updateStep(pipeline[DEPLOY_STAGE], DEPLOY_STAGE);
        }
      });
    },
    startCodeCheck() {
      const query = this.$route.query;
      let url = "/pipeline/startCodeCheck";
      request({
        url,
        method: "post",
        data: qs.stringify({
          projectId: query.id,
          envId: query.envId
        })
      }).then(pipeline => {
        if (pipeline) {
          this.sendSubscribe(CODE_CHECK_SUBSCRIBE);
          this.updateStep(pipeline[CODE_CHECK_STAGE], CODE_CHECK_STAGE);
        }
      });
    },
    offlineLastDeployMachine() {
      const query = this.$route.query;
      let url = "/pipeline/offline/last/machine";
      request({
        url,
        method: "post",
        data: qs.stringify({
          projectId: query.id,
          envId: query.envId
        })
      }).then(res => {
        console.log(res);
      });
    },
    getCommitList() {
      request({
        url: "/project/env/allow/commits",
        method: "POST",
        data: qs.stringify({
          envId: this.$route.query.envId
        })
      }).then(res => {
        this.commitList = res;
        this.commitListShow = res;
      })
    },
    refresh() {
      window.location.reload();
    },
    updateTestInfo({status,reviewers,remarks,operator}) {
      this.stepTestStatus = 'success';
      if (status == 3) {
         this.testAuditProgress = true;
         this.stepTestActive = 0;
         return
      }
      this.reviewers = reviewers || [];
      if (status == 0) {
        this.testAuditProgress = true;
        this.stepTestActive = 1
      } 
      // 审核通过 or 驳回
      if (status == 1 || status == 2) {
         this.stepTestActive = 2;
         this.testAuditPass = status == 1 ? true : false;
         this.noPassReason = remarks;
         // 更新审核所有人中的'最终审核者'
         this.updateOperator(operator)
      }
    },
    updateOperator(operator) {
      const operatorName = this.reviewerList.length !== 0 && this.reviewerList.find( item => item.userName === operator).name;
      this.$nextTick( () => {
        const nodeList = document.querySelectorAll('.test_audit .el-select__tags-text');
        nodeList.forEach( item => {
          item.nextElementSibling && item.nextElementSibling.remove();
          item.parentElement.style.width = '58px';
          item.parentElement.style.padding = '0px';
          item.parentElement.style.textAlign = 'center'
          if (item.innerHTML === operatorName) {
            item.classList.add('operator_color');
            item.parentElement.classList.add('operator_bgcolor')
          }
        })
      })
    },
    getReviewerList() {
      const query = this.$route.query;
      return request({
        url: `/test/review/users?projectId=${query.id}`,
      }).then(res => {
        if (!Array.isArray(res)) return;
        this.reviewerList = res;
      })
    },
    reviewerSubmit() {
      const query = this.$route.query;
      const commitId = this.commitForm.commitId;
      const reviewers = this.reviewers;
      if (reviewers.length == 0) {
        this.$message.warning('请选择测试审核人员');
        return
      }
      request({
        url: '/test/review/initiate',
        method: 'POST',
        data: {
          projectId: +query.id,
          commitId,
          reviewers
        }
      }).then(res => {
        if (res) {
          let reviewerArr = [];
          this.reviewerList.forEach( item => {
            reviewers.forEach(userName => {
              if(userName === item.userName) {
                reviewerArr.push(item.name)
                return;
              }
            })
          })
          this.$message.success(`测试审核已发起，并已发送飞书通知 ${reviewerArr.join(',')} 审核人员`);
          this.stepTestStatus = 'success';
        }
      }).catch(() => {
        this.stepTestStatus = 'error'
      })
      this.stepTestActive = 1;
    },
    commitIdChange(id) {
      if (!this.showTestAudit) return;
      this.testAuditProgress = false;
      this.testAuditPass = false;
      this.reviewers = [];//初始化
      const data = this.commitList.find( item => item.id == id)
      if (data && data.review) {
        setTimeout(() => {
          this.updateTestInfo(data.review)
        },10)  
      }
    },
    async urgentRelease() {
      if (!this.rollback && this.commitForm.commitId === "") {
        this.$message.warning('请先选择commitId');
        return
      }
      
      const num = await this.getUrgentRemainNum();
      if (num == 0) {
        this.$message({
          message: '该项目紧急发布次数已用完，不可紧急发布',
          type: 'warning',
          duration: 0,
          showClose: true
        })
        return
      }

      this.$confirm(`该项目的紧急发布剩余${num}次，请再次确认是否要紧急发布？`,'提示',{
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then( () => {
        this.showTestAudit = false;
        this.force = true;
        this.startPipeline()
      }).catch(() => {
        this.$message.info('已取消')
      })
    },
    getUrgentRemainNum() {
      const query = this.$route.query
      return request({
        url: `/project/leftDeployNum?projectId=${+query.id}`,
      })
    }
  },
  components: {
    ConsoleBox
  }
};
</script>

<style lang="scss" scoped>
.pipeline-header {
  border-bottom: 1px solid #eff0f4;
  padding-bottom: 10px;
  .project-info {
    font-family: PingFang SC;
    color: #333333;
    .h2 {
      font-weight: semibold;
      font-size: 20px
    }
    .env {
      margin-top: 5px;
      font-weight: regular;
      font-size: 16px
    }
  }
  .pipleline-operation {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    font-family: PingFang SC;
    font-weight: regular;
    .run_button {
      background: #457bfc;
      border-radius: 2px;
      padding: 5px 26px;
      color: #fff;
      font-size: 12px;
      line-height: normal
    }
    .worry_button {
      background: #f56c6c;
      color: #fff;
    }
    .back_button {
      margin-left: 10px;
      border-radius: 2px;
      background: #f56c6c;
      color: #fff;
      font-size: 12px;
      padding: 5px 5px 5px 26px;
      line-height: normal
    }
  }
}
.pipeline-graph-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #eff0f4;
  .deploy_process {
    display: flex;
    align-items: center;
    .line {
        width: 39px;
        height: 90px;
        border-top: 1px solid #d8d8d8;
    }
    .pipeline-stage {
      cursor: pointer;
      &-header {
        display: flex;
        justify-content: space-between;
        .extra-info {
          font-size: 12px;
        }
      }
      &-progress {
        margin-top: 10px;
        .desc {
          color: #333;
          font-family: PingFang SC;
          font-weight: regular;
          font-size: 13px;
        }
      }
    }
  }
  .no_test {
    .line {
      width: 38px;
      height: 8px;
      border-top: 1px solid #d8d8d8;
    }
    .pipeline-stage {
      width: 311px;
      height: 238px;
    }
    .card {
      .footer {
        width: 291px;
      }
    }
   }
  
  .card {
    box-sizing: border-box;
    width: 222px;
    height: 238px;
    border-radius: 4px;
    background: #FFFFFF;
    border: 1px solid #D8D8D8;
    border-top: 8px solid #c4c6cf;
    padding: 10px;
    position: relative;
    .title {
      color: #333;
      font-family: PingFang SC;
      font-weight: medium;
      font-size: 14px;
      line-height: normal;
    }
    .content {
      margin-top: 10px;
      display: flex;
      .button {
        margin-left: 10px;
        height: 28px;
        border-radius: 2px;
        background: #457bfc;
        color: #FFFFFF;
        font-family: PingFang SC;
        font-weight: regular;
        font-size: 12px;
        line-height: normal;
        padding: 6px 15px 5px 15px;
        border: 0px;
      }
    }
    .footer {
      position: absolute;
      bottom: 0px;
      border-top: 1px solid #d8d8d8;
      width: 202px;
      height: 40px;
      display: flex;
      justify-content: center;
      align-items: center;
      .no-start,.running {
        color: #999999;
        font-family: PingFang SC;
        font-weight: regular;
        font-size: 12px;
        line-height: normal;
      }
      .result {
        width: 100%;
        display: flex;
        justify-content: space-between;
        align-items: center;
        color: #333;
        font-family: PingFang SC;
        font-weight: medium;
        .left {
          font-size: 14px;
        }
        .right {
         font-size: 13px;
         display: flex;
        }
        .icon {
          display: block;
          width: 14px;
          height: 14px;
          margin-left: 4px;
          background-size: 100% 100%;
          position: relative;
        }
        .pass {
           background-image: url(https://img.youpin.mi-img.com/middlewareGroup/b1ccadb21bf1951b79b18b8a0ed2a94d.png?w=28&h=28);
        }
        .nopass {
          background-image: url(https://img.youpin.mi-img.com/middlewareGroup/403b48a5fe135139c4b8b391cae03198.png?w=28&h=28);
        }
        .reason {
          display: none;
          position: absolute;
          left: 8px;
          top: 8px;
          z-index: 99;
          width: 183px;
          box-sizing: border-box;
          padding: 9px 10px 10px 13px;
          background: #fff;
          box-shadow: 0px 2px 7px 0 #4A90E2;
          color: #333333;
          font-family: PingFang SC;
          font-weight: regular;
          font-size: 12px;
          line-height: 12px;
          p {
            margin-top: 5px;
            line-height: 1.5;
            word-break: break-all
          }
        }
        .nopass:hover .reason {
          display: block;
        }
      }
    }
  }
  .card.acitve {
    border-top-color: #c4c6cf;
  }
  .card.running {
    border-top-color: #4578FC
  }
  .card.success {
    border-top-color: #75c940
  }
  .card.error {
    border-top-color: #ff4f3e
  }
}
.pipeline-steps {
  margin-top: 20px;
  .title {
    color: #333333;
    font-family: PingFang SC;
    font-weight: medium;
    font-size: 14px;
    margin-bottom: 19px;
  }
}
.footer {
  margin-top: 30px;
}
.disabledColor {
  background: #abc4ff;
}
.commitOption.el-select-dropdown__item {
  height: 88px;
  border-bottom: 0.5px solid #ccc;
  padding-top: 5px;
  span {
    display: block;
    line-height: 20px;
  }
}
.batch-box {
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #e6e7eb;
  border-radius: 4px;
  color: #595959;
  &-active {
    border-color: #3da8f5;
  }
  &-part-fail {
    border-color: #dd8c85;
  }
  &-fail {
    border-color: #ff4f3e;
  }
  &-success {
    border-color: #75c940;
  }
  &-header {
    display: flex;
    justify-content: space-between;
  }
  .batch-item {
    padding: 10px 0;
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    &-options {
      cursor: pointer;
      color: #3da8f5;
    }
  }
}
.tag {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;

  &-info {
    color: #8c8c8c;
    background-color: #f2f3f7;
  }

  &-active {
    color: #3da8f5;
    background-color: #ebf4fb;
  }

  &-fail {
    color: #ff4f3e;
    background-color: #fcefee;
  }

  &-success {
    color: #75c940;
    background-color: #edfcf1;
  }
}
</style>
<style lang="scss">
.pipeline-graph-container .card {
  .el-select {
    width: 137px;
  }
  .el-input--suffix .el-input__inner {
    padding-right: 20px;
    padding-left: 5px;
    &::placeholder {
      // width: 108px;
      color: #999;
      font-family: PingFang SC;
      font-weight: regular;
      font-size: 12px;
      line-height: 12px;
    }
  }
  .el-input__suffix {
    right: 1px;
  }
  .el-tag.el-tag--info {
    display: block;
    width: 69px;
    height: 23px;
    padding-left: 10px;
    box-sizing: border-box;
    line-height: 23px;
    span {
      color: #666666;
      font-family: PingFang SC;
      font-weight: regular;
      font-size: 12px;
      line-height: 12px;
    }
    .operator_color {
      color: #23ba90
    }
  }
  .operator_bgcolor {
    background-color: #ecfbf7
  }
  
}
.pipeline-steps {
  .el-step__icon.is-text {
    width: 15px;
    height: 15px;
    display: flex;
    justify-content: center;
    align-items: center;
    .el-step__icon-inner {
      font-size: 10px;
      line-height: 10px;
      font-weight: normal;
    }
  }
  .el-step__title{
    font-family: PingFang SC;
    font-weight: regular;
    font-size: 12px;
    line-height: 12px;
    margin-top: 5px;
  }
  .el-step.is-horizontal .el-step__line {
    top: 7px;
  }
}
</style>