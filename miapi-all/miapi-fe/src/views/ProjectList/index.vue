<template>
  <div class="project-list-container">
    <div class="project-left">
      <ProjectGroup/>
    </div>
    <div class="project-wrap">
      <div class="search-wrap">
        <h2>MI API</h2>
        <div class="project-search">
          <el-select
            size="large"
            popper-class="lazy-search-options"
            v-model="searchVal"
            filterable
            :teleported="false"
            remote
            reserve-keyword
            :placeholder="`${$i18n.t('placeholder.searchProject')}`"
            :remote-method="handleSearchProject">
            <div @click.stop="()=>{}" class="project-option-wrap">
              <el-tabs v-model="optionActiveName">
                <el-tab-pane :label="`${this.$i18n.t('titleProject')}(${searchResult.projectList ? searchResult.projectList.length : 0})`" name="1">
                  <div v-if="searchResult.projectList && searchResult.projectList.length">
                    <el-option
                      v-for="item in searchResult.projectList"
                      :key="`${item.gitName}-${item.name}`"
                      :value="`${item.gitName}-${item.name}`">
                      <router-link style="display: inline-block; width: 100%" :to="{path: path.API,query: { projectID:item.id }}">
                        <p class="search-option" v-html="handleReplace($i18n.t('name'), item.name)"></p>
                        <p class="search-option-desc" v-html="handleReplace($i18n.t('description'),item.description)"></p>
                        <p class="search-option-desc" v-html="handleReplace($i18n.t('belong'),`${item.projectGroupName}/${item.name}`)"></p>
                      </router-link>
                    </el-option>
                  </div>
                  <Empty v-else :imageSize="300" description=" "/>
                </el-tab-pane>
                <el-tab-pane :label="`API(${searchResult.apiList ? searchResult.apiList.length : 0})`" name="2">
                  <div v-if="searchResult.apiList && searchResult.apiList.length">
                    <el-option
                      v-for="item in searchResult.apiList"
                      :key="item.apiID"
                      :value="item.apiName">
                      <router-link style="display: inline-block; width: 100%" :to="{path: path.API_DETAIL,query: { projectID:item.projectID,apiID:item.apiID,apiProtocol:item.apiProtocol }}">
                        <p class="search-option" v-html="handleReplace($i18n.t('name'),item.apiName)"></p>
                        <p class="search-option-desc" v-html="handleReplace($i18n.t('url'),item.apiURI)"></p>
                        <p class="search-option-desc" v-html="handleReplace($i18n.t('belong'),item.projectName)"></p>
                      </router-link>
                    </el-option>
                  </div>
                  <Empty v-else :imageSize="300" description=" "/>
                </el-tab-pane>
                <el-tab-pane :label="`文档(${searchResult.documentList ? searchResult.documentList.length : 0})`" name="3">
                  <div v-if="searchResult.documentList && searchResult.documentList.length">
                    <el-option
                      v-for="item in searchResult.documentList"
                      :key="`${item.groupID}-${item.title}`"
                      :value="item.title">
                      <router-link target="_blank" style="display: inline-block; width: 100%" :to="{path: path.SHARE_DOC,query: { documentID:item.documentID,projectName:''}}">
                        <p class="search-option" v-html="handleReplace($i18n.t('name'),item.title)"></p>
                      </router-link>
                    </el-option>
                  </div>
                  <Empty v-else :imageSize="300" description=" "/>
                </el-tab-pane>
              </el-tabs>
              <!-- <p class="page-bar">
                <el-pagination
                  background
                  small
                  :pager-count="5"
                  :page-size="5"
                  :current-page="1"
                  :page-sizes="[5, 10, 20, 50]"
                  layout="prev, pager, next, jumper"
                  :total="1000">
                </el-pagination>
              </p> -->
            </div>
            <template #empty>
              <div class="project-option-wrap is-empty">
                <div v-if="searchLoading" class="loadin-wrap">
                  <el-icon :size="16"><Loading /></el-icon>
                </div>
                <Empty v-else-if="!isinit" description=" "/>
              </div>
            </template>
          </el-select>
          <el-button type="primary">{{$i18n.t('btnText.search')}}</el-button>
        </div>
      </div>
      <div class="history-list">
        <div v-if="!!recentlyProjectList.length || !!recentlyApiList.length" class="history-list-content">
          <div v-if="!!recentlyProjectList.length" class="recent-access">
            <h4>{{$i18n.t('recentVisit')}}</h4>
            <ul>
              <li v-for="(v, index) in recentlyProjectList" :key="index" @click="handleGoProject(v)">
                <ItemCard :data="v"/>
              </li>
            </ul>
          </div>
          <div v-if="!!recentlyApiList.length" class="look-api">
            <h4>{{$i18n.t('recentlyViewed')}}</h4>
            <ul>
              <li v-for="(v, index) in recentlyApiList" :key="index" @click="handleGoApi(v)">
                <ItemCard type="api" :data="{name: v.apiProtocol === 3 ? v.apiName : v.apiURI, apiName: v.apiProtocol === 3 ? v.apiDesc : v.apiName}" :border="false" :star="false"/>
              </li>
            </ul>
          </div>
        </div>
        <div v-else class="history-list-content">
          <Empty/>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { deleteProject, getRecentlyProjectList, getRecentlyApiList, indexSearch } from '@/api/main'
import debounce from '@/common/debounce'
import ProjectGroup from './Group'
import ItemCard from './ItemCard'
import { PATH } from '@/router/constant'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import Empty from '@/components/Empty'

export default {
  components: {
    ProjectGroup,
    ItemCard,
    Empty
  },
  data () {
    return {
      optionActiveName: '1',
      showProjectList: [],
      projectList: [],
      searchVal: '',
      searchkey: '',
      searchLoading: false,
      searchResult: [],
      recentlyProjectList: [],
      recentlyApiList: [],
      isinit: true
    }
  },
  computed: {
    path () {
      return PATH
    }
  },
  mounted () {
    getRecentlyProjectList().then(data => {
      if (data.message === AJAX_SUCCESS_MESSAGE) {
        this.recentlyProjectList = data.data || []
      }
    }).catch(e => {})
    getRecentlyApiList().then(data => {
      if (data.message === AJAX_SUCCESS_MESSAGE) {
        this.recentlyApiList = data.data || []
      }
    }).catch(e => {})
  },
  methods: {
    init () {
      // getProjectList().then((data)=>{
      //   if (data.message === AJAX_SUCCESS_MESSAGE) {
      //     this.projectList = data.data
      //     this.showProjectList = data.data
      //     this.$store.dispatch('projectlist/changeList', data.data)
      //   }
      // }).catch(e=>{})
    },
    handleReplace (title, str = "") {
      if (this.searchkey) {
        let res = str.replace(this.searchkey, `<span>${this.searchkey}</span>`)
        return `${title}: ${res}`
      } else {
        return `${title}: ${str}`
      }
    },
    handleDel (row) {
      this.$confirm(`${this.$i18n.t('projectList.deleteTip')} ${row.name}, ${this.$i18n.t('projectList.continue')}?`, this.$i18n.t('prompt'), {
        confirmButtonText: this.$i18n.t('btnText.ok'),
        cancelButtonText: this.$i18n.t('btnText.cancel'),
        type: 'warning'
      }).then(() => deleteProject({ projectID: row.id })).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message({
            type: 'success',
            message: this.$i18n.t('successDeleted')
          })
          this.init()
        }
      }).catch(() => {})
    },
    handleSearchProject (val) {
      this.searchLoading = true
      this.handleDebounce(val)
    },
    handleDebounce: debounce(function (val) {
      this.searchkey = val
      indexSearch({ keyword: val }).then(data => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.searchResult = data.data || {
            apiList: [],
            documentList: [],
            projectList: []
          }
        }
      }).catch(e => {}).finally(() => {
        this.isinit = false
        this.searchLoading = false
      })
      // if (val) {
      //   this.showProjectList = this.projectList.filter(item=>item.name.indexOf(val) >= 0)
      // } else {
      //   this.showProjectList = this.projectList
      // }
    }, 300, false),
    handleGoProject (item) {
      this.$router.push({ path: PATH.API, query: { projectID: item.id } })
    },
    handleGoApi (item) {
      this.$router.push({ path: PATH.API_DETAIL, query: { projectID: item.projectID, apiID: item.apiID, apiProtocol: item.apiProtocol } })
    }
  }
}
</script>

<style scoped>
.project-list-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
}
.project-list-container .project-left {
  width: 279px;
  height: 100%;
  position: relative;
}
.project-list-container .project-wrap{
  width: calc(100% - 279px);
}
.project-list-container .project-wrap .project-search {
  width: 592px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.project-list-container .project-wrap .project-search .el-select {
  width: 512px;
}
.project-option-wrap {
  padding: 0 16px;
  width: 592px;
  /* height: 262px; */
  height: 322px;
}
.project-option-wrap .loadin-wrap {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1890FF;
}
.project-option-wrap.is-empty {
  padding: 6px 16px;
}
.project-option-wrap.is-empty >>> .el-tabs__content{
  display: flex;
  align-items: center;
  justify-content: center;
}
.project-option-wrap >>> .el-tabs__content .el-empty{
  padding: 0;
}
.project-option-wrap >>>.el-tabs__header {
  margin-bottom: 0;
}
.project-option-wrap>>>.el-select-dropdown__item{
  border-bottom: 1px solid rgba(233, 233, 233, 1);
  height: auto;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 8px 20px;
}
.project-option-wrap .page-bar {
  text-align: right;
  margin: 20px 0 16px;
}
.project-option-wrap>>>.el-tabs__item {
  font-weight: 400;
  width: 115px;
  text-align: center;
}
.project-option-wrap>>>.el-tabs__content{
  height: 282px;
  overflow-y: auto;
}
.project-option-wrap>>>.el-tabs__content::-webkit-scrollbar{
  display: none;
}
.project-list-container .project-wrap .project-search >>> .el-input__wrapper {
  border-radius: 5px 0 0 5px;
}
.project-list-container .project-wrap .project-search >>> .el-input__inner {
  height: 38px;
}
.project-list-container .project-wrap .project-search .el-button {
  font-size: 18px;
  color: #fff;
  background: rgba(24, 144, 255, 1);
  width: 80px;
  height: 40px;
  border-radius: 0;
  font-weight: 400;
  border-radius: 0 5px 5px 0;
}
.project-list-container .project-wrap .project-search>>>.el-input-group__append {
  border: none;
}
.project-list-container .project-wrap .search-wrap{
  width: 100%;
  height: 179px;
  border-bottom: 1px solid rgba(233, 233, 233, 1);
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-flow: column;
}
.project-list-container .project-wrap .search-wrap h2{
  font-size: 28px;
  color: #1890FF;
  font-weight: 400;
  margin-bottom: 24px;
}
.project-list-container .project-wrap .history-list {
  height: calc(100vh - 234px);
  overflow-y: auto;
}
.project-list-container .project-wrap .history-list::-webkit-scrollbar{
  display: none;
}
.project-list-container .project-wrap .history-list .history-list-content {
  margin: 20px 20px 0;
  background: #fff;
  padding: 20px;
}
.project-list-container .project-wrap .history-list::-webkit-scrollbar{
  display: none;
}
.project-list-container .project-wrap .history-list .recent-access h4,.project-list-container .project-wrap .history-list .look-api h4{
  margin-bottom: 20px;
}
.project-list-container .project-wrap .history-list .recent-access ul,.project-list-container .project-wrap .history-list .look-api ul {
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  flex-wrap: wrap;
}
.project-list-container .project-wrap .history-list .recent-access ul li,.project-list-container .project-wrap .history-list .look-api ul li {
  width: 18%;
  margin-right: 2.5%;
  margin-bottom: 16px;
}
.project-list-container .project-wrap .history-list .recent-access ul li:nth-child(5n), .project-list-container .project-wrap .history-list .look-api ul li:nth-child(4n){
  margin-right: 0;
}
.project-list-container .project-wrap .history-list .look-api{
  margin-top: 20px;
}
.project-list-container .project-wrap .history-list .look-api ul li {
  width: 23%;
  margin-right: 2.66%;
}
.project-option-wrap .search-option >>> span, .project-option-wrap .search-option-desc >>> span{
  color: #1890FF;
}
.project-option-wrap .search-option-desc {
  color: #aaa;
  line-height: 1;
  padding: 4px 0;
}
.project-option-wrap .search-option{
  line-height: 1;
  padding: 4px 0;
}
.project-search >>> .el-scrollbar__wrap{
  overflow: hidden !important;
  max-height: 300px;
}
.project-search >>> .el-select-dropdown {
  height: 300px;
}
.project-search >>>.popper__arrow{
  display: none;
}
.project-search >>> .el-select-dropdown {
  top: 34px !important;
}
.lazy-search-options >>> .el-select-dropdown__wrap {
  height: 364px;
}
</style>
