<template>
	<el-container>
		<el-header><Header/></el-header>
		<el-container class="content-wrap">
			<div v-show="show" class="fixed-project-group-wrap">
				<el-drawer
					modal-class="project-drawer"
					v-model="show"
					:withHeader="false"
					:destroy-on-close="false"
					:append-to-body="false"
					:modal="false"
					:size="870"
					@closed="handleClose"
					direction="ltr">
					<div class="fixed-project-group">
						<ProjectGroup :isStatic="true"/>
						<div class="project-list-wrap">
							<ProjectItemList :isStatic="true" @handleClose="handleClose"/>
						</div>
					</div>
				</el-drawer>
			</div>
			<AppMain />
		</el-container>
	</el-container>
</template>

<script>

import Header from '@/components/Header'
import AppMain from '@/components/AppMain'
import ProjectGroup from '@/views/ProjectList/Group'
import ProjectItemList from '@/views/ProjectList/ProjectItemList'
import { mapGetters } from 'vuex'
import { GROUP_TYPE } from '@/views/ApiList/constant'
import { PATH } from '@/router/constant'
import { PROTOCOL_TYPE } from './constant'

export default {
  name: 'Layout',
  components: {
    Header,
    AppMain,
    ProjectGroup,
    ProjectItemList
  },
  data () {
    return {
      curProjectID: '',
      show: false
    }
  },
  computed: {
    ...mapGetters([
      'showFixedGroup',
      'projectList',
      'selectedProjectGroup',
      'groupID'
    ])
  },
  methods: {
    handleClose () {
      this.$store.dispatch('apilist.group/changeGroupType', GROUP_TYPE.API)
      this.$store.dispatch('projectlist/changeFixedGroup', false)
    }
  },
  watch: {
    showFixedGroup (bool) {
      this.show = bool
      if (bool && !this.projectList.length && this.selectedProjectGroup.groupId === -1) {
      	this.$store.dispatch('projectlist/getList', '999')
      }
    },
    $route: {
      handler (route) {
        if (route.query.projectID && Number(this.curProjectID) !== Number(route.query.projectID)) {
          this.$store.dispatch('apiindex/changeIndexApiList', [])
          this.$store.dispatch('apiindex/changeIndexGroupList', [])
          this.$store.dispatch('apilist/changeGroupList', [])
          this.$store.dispatch('apilist/changeApiList', [])
          this.$store.dispatch('apilist.group/changeGroupComp', { uniqueOpened: true, defaultOpeneds: [], searchWord: '', searchSort: false })
          this.$store.dispatch('apilist/changeGroupId', -1)
          this.$store.dispatch('apitest/getHostEnvList', route.query.projectID)
        	this.$store.dispatch('apilist/changeGroupDesc', '')
          this.$store.dispatch('apilist/groupList', route.query.projectID).then(() => {
            this.$store.dispatch('apiindex/getIndexGroupList', this.$utils.getQuery('projectID'))
            this.$store.dispatch('apilist.group/getGroupViewList', this.$utils.getQuery('projectID'))
            this.$store.dispatch('apilist.group/getAllIndexGroupViewList', this.$utils.getQuery('projectID'))
          })
        }
        this.curProjectID = route.query.projectID

        // 初始化store数据
        if (route.path === PATH.HOME) {
          this.$store.dispatch('apilist/changeGroupList', [])
          this.$store.dispatch('apilist/changeGroupId', undefined)
        	this.$store.dispatch('apilist/changeGroupDesc', '')
          this.$store.dispatch('apiindex/changeGroupIndexId', undefined)
          this.$store.dispatch('apilist.group/changeGroupType', GROUP_TYPE.API)
        } else if (route.path === PATH.API_TEST) {
          this.$store.dispatch('apitest/changeApiTestTarget', { apiTestProtocol: PROTOCOL_TYPE.HTTP })
        }
      },
      immediate: true,
      deep: true
    }
  }
}
</script>

<style lang="scss">
@import '@/styles/variables.scss';
	.el-header {
		background: transparent;
		width: 100%;
		padding: 0;
		height: $headerHeight !important;
		position: fixed;
		z-index: 10;
		padding: 0 !important;
	}
	.el-aside{
		background: transparent;
	}
	.el-main{
		background: #e9eef3;
		height: 100%;
	}
	.content-wrap {
		position: relative;
		padding-top: $headerHeight;
		min-width: 1200px !important;
		display: block !important;
	}
	.content-wrap .fixed-project-group-wrap{
		position: absolute;
		left: 0;
		top: 56px;
		bottom: 0;
		z-index: 8;
		right: 0;
		background: rgba(0,0,0,0.5);
	}
	.content-wrap .fixed-project-group{
		position: relative;
		width: 279px;
		display: flex;
		align-items: flex-start;
		justify-content: flex-start;
	}
	.content-wrap .fixed-project-group .project-list-wrap {
		margin-left: 279px;
		background: #fff;
		height: 100%;
	}
	.content-wrap .fixed-project-group .project-list-wrap .project-item-list {
		width: 591px;
		height: calc(100vh - #{$headerHeight});
		overflow-y: auto;
	}
	.content-wrap .fixed-project-group-wrap .project-drawer{
		padding-top: $headerHeight;
		margin-left: 0 !important;
	}
</style>
<style scope>
	.content-wrap .fixed-project-group-wrap .project-drawer .el-drawer{
  position: relative !important;
	}
</style>
