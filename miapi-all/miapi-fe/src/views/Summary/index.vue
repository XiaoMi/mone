<template>
  <section class="summary-content">
    <div class="summary-breadcrumb">
      <el-breadcrumb separator="-">
        <el-breadcrumb-item><span class="title">{{projectDetail.projectName}}</span></el-breadcrumb-item>
        <el-breadcrumb-item><span class="title">{{$i18n.t('projectSummary')}}</span></el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <el-tabs v-model="activeName">
      <el-tab-pane :label="$i18n.t('summaryBasicInformation')" name="first">
        <div class="summary-doc-container">
          <BaseInfo/>
        </div>
      </el-tab-pane>
      <el-tab-pane :label="$i18n.t('environmentConfiguration')" name="five">
        <div class="summary-doc-container">
          <AddEnv/>
        </div>
      </el-tab-pane>
      <el-tab-pane :label="$i18n.t('projectDynamics')" name="second">
        <div class="summary-doc-container">
          <Dynamic/>
        </div>
      </el-tab-pane>
      <!-- <el-tab-pane :label="$i18n.t('memberManagement')" name="third">
        <div class="summary-doc-container">
          <UserList/>
        </div>
      </el-tab-pane> -->
      <el-tab-pane :label="$i18n.t('dataImport')" name="six">
        <div class="summary-doc-container">
          <ImportJson/>
        </div>
      </el-tab-pane>
      <el-tab-pane :label="$i18n.t('projectDocumentation')" name="fourth">
        <div class="summary-doc-container">
          <DocList ref="docListRef"/>
        </div>
      </el-tab-pane>
    </el-tabs>
  </section>
</template>
<script>
import BaseInfo from './components/BaseInfo'
import UserList from './components/UserList'
import DocList from './components/DocList'
import Dynamic from './components/Dynamic'
import ImportJson from './components/ImportJson'
import AddEnv from './components/AddEnv'
import { mapGetters } from 'vuex'

export default {
  name: 'SummaryComp',
  components: {
    BaseInfo,
    UserList,
    DocList,
    Dynamic,
    ImportJson,
    AddEnv
  },
  data () {
    let activeName = this.$utils.getQuery('activeName') || 'first'
    return {
      activeName
    }
  },
  computed: {
    ...mapGetters([
      'projectDetail'
    ]),
    projectID () {
      return this.$utils.getQuery('projectID')
    }
  }
}
</script>
<style scoped>
.summary-content{
  width: 100%;
}
.summary-content .summary-breadcrumb {
  background: #fff;
  padding: 18px 20px 0px 30px;
  width: 100%;
}
.summary-content >>> .el-breadcrumb__inner .title,.summary-content >>> .el-breadcrumb__separator {
	font-size: 16px;
	font-weight: bold;
	color: #000;
}
.summary-content>>>.el-tabs__header{
  padding: 8px 30px 0;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  margin: 0 0 20px;
}
.summary-content>>>.el-tabs__nav-wrap::after{
  content: none;
}
.summary-content .summary-doc-container {
  background: #fff;
  margin: 0 20px;
  height: calc(100vh - 158px);
  overflow-y: auto;
}
.summary-content .summary-doc-container::-webkit-scrollbar{
  display: none;
}
</style>
