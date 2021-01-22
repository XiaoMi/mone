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
    <d2-module>
      <el-tabs v-model="activeName" style="height: 100%">
        <el-tab-pane label="当前部署" name="1">
          <current-deploy v-if="deployType == 1" :project-id="projectId" :env-id="envId" />
          <current-docker v-if="deployType == 2 || deployType == 3" :project-id="projectId" :env-id="envId" />
        </el-tab-pane>
        <el-tab-pane label="部署历史" name="2">
          <deploy-history :project-id="projectId" :env-id="envId" :deploy-type='deployType'/>
        </el-tab-pane>
        <el-tab-pane label="构建配置" name="3" v-if="deployType == 1 || deployType == 2">
          <build-setting :project-id="projectId" :env-id="envId" />
        </el-tab-pane>
        <el-tab-pane label="部署策略" name="4">
          <deploy-policy :project-id="projectId" :env-id="envId" />
        </el-tab-pane>
        <el-tab-pane label="部署配置" name="5" v-if="deployType == 1">
          <deploy-setting :project-id="projectId" :env-id="envId" :pro-type="proType" />
        </el-tab-pane>
        <el-tab-pane label="资源管理" name="6" v-if="deployType == 1">
          <deploy-machie :project-id="projectId" :env-id="envId" />
        </el-tab-pane>
        <el-tab-pane label="周期部署" name="6" v-if="(deployType == 2 || deployType == 3) && isSuperRole">
          <periodic-deployment :project-id="projectId" :env-id="envId" />
        </el-tab-pane>
        <el-tab-pane label="容器配置" name="7" v-if="deployType == 2">
          <docker-setting :project-id="projectId" :env-id="envId" />
        </el-tab-pane>
        <el-tab-pane label="dockerfile配置" name="7" v-if="deployType == 3">
          <dockerfile-setting :project-id="projectId" :env-id="envId" />
        </el-tab-pane>
      </el-tabs>
    </d2-module>
  </d2-container>
</template>
<script>
import DeployHistory from './components/deploy-history'
import CurrentDeploy from './components/current-deploy'
import CurrentDocker from './components/current-docker'
import DeployPolicy from './components/deploy-policy'
import DeploySetting from './components/deploy-setting'
import DeployMachie from './components/deploy-machine'
import BuildSetting from './components/build-setting'
import DockerSetting from './components/docker-setting'
import DockerfileSetting from './components/dockerfile-setting'
import periodicDeployment from './components/periodic-deployment/index'

export default {
  data () {
    return {
      isSuperRole: (((userInfo && userInfo.roles) || []).findIndex(it => it.name == 'SuperRole') != -1),
      projectId: this.$route.params.projectId,
      envId: this.$route.params.envId,
      deployType: +this.$route.params.deployType,
      activeName: this.$route.query.active || '1',
      proType: this.$route.query.type || ''
    }
  },
  components: {
    DeployHistory,
    CurrentDeploy,
    CurrentDocker,
    DeploySetting,
    DeployPolicy,
    DeployMachie,
    BuildSetting,
    DockerfileSetting,
    DockerSetting,
    periodicDeployment
  }
}
</script>
<style lang="scss" scoped>
.header {
  display: flex;
  justify-content: space-between;
}
</style>
