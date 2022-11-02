<template>
	<div class="project-item-list">
		<div class="drawer-title">
			<span>
				{{curProjectGroup.groupName}} {{$i18n.t('haveAltogether')}} <var>{{getTotalCount}}</var> {{$i18n.t('projectItems')}}
			</span>
			<el-button v-if="!isStatic" @click.stop="handleAdd" type="primary" text>{{$i18n.t('newProject')}}</el-button>
		</div>
		<h6 v-if="currentKey === '999'">{{$i18n.t('IManage')}}({{list.length}})</h6>
		<ul v-if="list && list.length">
			<li v-for="item in list" :key="item.id" @click.stop="handleGo(item)">
        <el-icon @click.stop="handleFocus(item)" :size="16">
          <StarFilled v-if="item.isFocus" />
          <Star v-else />
        </el-icon>
				<p class="title">{{item.name}}</p>
				<p class="api-num">{{$i18n.t('projectItemList.have')}} {{item.apiCount}} {{$i18n.t('projectItemList.unit')}}</p>
				<p class="update">{{$i18n.t('updatedOn')}}{{moment(item.utime).format('YYYY-MM-DD HH:mm:ss')}}</p>
			</li>
		</ul>
    <Empty v-else :description="`${$i18n.t('noData')}`"/>
    <template v-if="currentKey === '999'">
      <h6>{{$i18n.t('myFavorite')}}({{focusList.length}})</h6>
      <ul v-if="focusList && focusList.length">
        <li v-for="item in focusList" :key="item.id" @click.stop="handleGo(item)">
          <el-icon @click.stop="handleFocus(item, 'focusList')" :size="16">
            <StarFilled v-if="item.isFocus"/>
            <Star v-else />
          </el-icon>
          <p class="title">{{item.name}}</p>
          <p class="api-num">{{$i18n.t('projectItemList.have')}} {{item.apiCount}} {{$i18n.t('projectItemList.unit')}}</p>
          <p class="update">{{$i18n.t('updatedOn')}}{{moment(item.utime).format('YYYY-MM-DD HH:mm:ss')}}</p>
        </li>
      </ul>
		  <Empty v-else :description="`${$i18n.t('noData')}`"/>
    </template>
		<el-dialog
			:destroy-on-close="true"
			:center="false"
			:close-on-click-modal="false"
			:close-on-press-escape="false"
			:title="dialog.title"
			v-model="dialog.visible"
			width="640px"
			append-to-body
		>
			<AddProjectDialog :curProjectGroupId="curProjectGroup.groupId" :row="dialog.row" @submit="handleSubmit" @handlechangevisible="handlechangevisible"/>
		</el-dialog>
	</div>
</template>
<script>
import moment from 'moment'
import AddProjectDialog from '../AddProjectDialog'
import { modifyProject, addProject, focusProject, unFocusProject } from '@/api/main'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { PATH } from '@/router/constant'
import Empty from '@/components/Empty'
import { mapGetters } from 'vuex'
export default {
  name: 'ProjectItemList',
  components: {
    AddProjectDialog,
    Empty
  },
  data () {
    return {
      hasFollow: false,
      list: [],
      focusList: [],
      curProjectGroup: {},
      dialog: {
        visible: false,
        title: this.$i18n.t('newProject'),
        row: {}
      }
    }
  },
  props: {
    isStatic: {
      type: Boolean,
      default: false
    }
  },
  watch: {
    projectList: {
      handler (val = []) {
        this.list = val.map(item => {
          return {
            ...item,
            isFocus: this.focusProjectList.some(v => v.id === item.id)
          }
        })
      },
      immediate: true,
      deep: true
    },
    focusProjectList: {
      handler (val = []) {
        this.focusList = val.map(item => {
          return {
            ...item,
            isFocus: true
          }
        })
      },
      immediate: true,
      deep: true
    },
    selectedProjectGroup: {
      handler (val) {
        this.curProjectGroup = val
      },
      immediate: true,
      deep: true
    }
  },
  computed: {
    ...mapGetters([
      'projectList',
      'focusProjectList',
      'currentKey',
      'selectedProjectGroup'
    ]),
    moment () {
      return moment
    },
    getTotalCount () {
      if (this.currentKey === '999') {
        return this.projectList.length + this.focusProjectList.length
      }
      return this.projectList.length
    }
  },
  methods: {
    handleClose () {
      this.$emit('handleClose')
    },
    // handleModify(row) {
    //   this.dialog = {
    //     visible: !this.dialog.visible,
    //     title: '修改项目',
    //     row: {
    //       projectName: row.name,
    // 			projectVersion: row.version,
    //       description: row.description,
    //       id: row.id,
    //     },
    //     type: 'modify'
    //   }
    // },
    handleGo (item) {
      this.handleClose()
      this.$router.push({ path: PATH.API, query: { projectID: item.id } })
    },
    handleFocus (item, type) {
      if (item.isFocus) {
        unFocusProject({ projectID: item.id }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            if (type === 'focusList') {
              this.focusList = this.focusList.map(v => {
                if (v.id === item.id) {
                  v.isFocus = false
                }
                return v
              })
            } else {
              this.list = this.list.map(v => {
                if (v.id === item.id) {
                  v.isFocus = false
                }
                return v
              })
            }
            this.$message.success(this.$i18n.t('unfavorite'))
          }
        }).catch(e => {})
      } else {
        focusProject({ projectID: item.id }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            if (type === 'focusList') {
              this.focusList = this.focusList.map(v => {
                if (v.id === item.id) {
                  v.isFocus = true
                }
                return v
              })
            } else {
              this.list = this.list.map(v => {
                if (v.id === item.id) {
                  v.isFocus = true
                }
                return v
              })
            }
            this.$message.success(this.$i18n.t('collectionSuccess'))
          }
        }).catch(e => {})
      }
    },
    handleAdd () {
      this.dialog = {
        visible: !this.dialog.visible,
        title: this.$i18n.t('newProject'),
        type: 'add',
        row: {
          projectName: '',
          projectVersion: ''
        }
      }
    },
    handlechangevisible () {
      this.dialog = {
        visible: !this.dialog.visible,
        title: this.$i18n.t('newProject')
      }
    },
    handleSubmit (formdata) {
      // formdata.projectUpdateTime = new Date().getTime()
      switch (this.dialog.type) {
        case 'modify':
          formdata.projectID = this.dialog.row.id
          modifyProject(formdata).then((data) => {
            if (data.message === AJAX_SUCCESS_MESSAGE) {
              this.$message.success(this.$i18n.t('editSuccessfully'))
              this.$store.dispatch('projectlist/getList')
            }
          }).catch(e => {})
          break
        case 'add':
          addProject(formdata).then((data) => {
            if (data.message === AJAX_SUCCESS_MESSAGE) {
              this.$message.success(this.$i18n.t('addedSuccessfully'))
              this.$store.dispatch('projectlist/getList')
            }
          }).catch(e => {})
          break
        default:
          break
      }
    }
  }
}
</script>
<style scoped>
.drawer-title {
	border-bottom: 1px solid rgba(233, 233, 233, 1);
	height: 46px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 18px;
	color: #333;
	font-size: 14px;
}
.drawer-title var {
	color: #1890FF;
	font-style: normal;
}
.project-item-list h6 {
	font-size: 12px;
	color: #aaa;
	line-height: 30px;
	height: 30px;
	border-bottom: 1px solid rgba(233, 233, 233, 1);
	padding: 0 18px;
	background: rgba(249, 249, 249, 1);
	font-weight: 400;
}
.project-item-list ul{
	display: flex;
	align-items: center;
	justify-content: flex-start;
	flex-wrap: wrap;
}
.project-item-list ul li {
	border-bottom: 1px solid rgba(233, 233, 233, 1);
	border-right: 1px solid rgba(233, 233, 233, 1);
	width: 50%;
	padding: 12px 18px;
	position: relative;
	cursor: pointer;
}
.project-item-list ul li:hover{
	background: rgba(230, 247, 255, 1);
}
.project-item-list ul li:nth-child(2n){
	border-right: none;
}
.project-item-list ul li i {
	position: absolute;
	right: 8px;
	top: 8px;
	cursor: pointer;
	color: #1890FF;
}
.project-item-list ul li .title {
	font-size: 14px;
	color: #333;
	width: 90%;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
.project-item-list ul li .api-num,.project-item-list ul li .update{
	font-size: 12px;
	color: #aaa;
	margin-top: 10px;
	width: 100%;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
.project-item-list ul li .update {
	text-align: right;
}
.drawer-title .el-button {
	color: #1890FF;
	font-size: 14px;
	font-weight: 400;
}
.project-item-list + .v-modal{
	z-index: 8 !important;
}
</style>
