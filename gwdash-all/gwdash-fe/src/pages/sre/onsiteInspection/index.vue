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
    <el-tabs v-model="activeName" type="border-card" @tab-click="handleClick">
    <el-tab-pane label="部署巡检" name="first">
      <DeployInspection :actived="activeName==='first'"></DeployInspection>
    </el-tab-pane>
    <el-tab-pane label="agent巡检" name="second">
      <AgentInspection :actived="activeName==='second'"></AgentInspection>
    </el-tab-pane>
     <el-tab-pane label="Cpu&&Memory" name="third">
      <ProjectUsageInspection :actived="activeName==='third'"></ProjectUsageInspection>
    </el-tab-pane>
  </el-tabs>
</d2-container>
</template>
<script>
import DeployInspection from './components/deployInspection.vue'
import AgentInspection from './components/agentInspection.vue'
import ProjectUsageInspection from './components/projectUsage.vue'

export default {
  name: 'onSiteInspection',
  data () {
    return {
      activeName: ''
    }
  },
  methods: {
    handleClick (tab, event) {
      // console.log(this.activeName);
      // console.log(tab, event);
    }
  },
  mounted () {
    this.activeName = 'first'
  },
  components: {
    DeployInspection,
    AgentInspection,
    ProjectUsageInspection
  }
}
</script>
