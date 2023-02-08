<template>
  <div class="project-group-container">
		<el-menu
      class="el-menu-vertical-demo"
      :default-active="defaultActive"
      @select="handleOpen">
      <el-menu-item index="000" class="group-all">
				<span>{{$i18n.t('groupAll')}}</span>
				<span class="add-project-btn-wrap" @click.stop="handleAddOpen">
          <el-icon :size="12">
            <Plus />
          </el-icon>
          <span>{{$i18n.t('projectGrouping')}}</span>
				</span>
      </el-menu-item>
      <el-menu-item index="999">
				<div class="group-name">
					<span class="group-name-item">{{$i18n.t('myProject')}}</span>
					<!-- <el-dropdown @command="handleCommand">
						<span class="el-dropdown-link">
						</span>
						<el-dropdown-menu slot="dropdown">
							<el-dropdown-item :command="menuType.EDIT">编辑</el-dropdown-item>
							<el-dropdown-item :command="menuType.DEL">删除</el-dropdown-item>
						</el-dropdown-menu>
					</el-dropdown> -->
				</div>
      </el-menu-item>
			<el-menu-item v-for="(v, index) in projectGroups" :key="index" :index="`${index}`">
				<div class="group-name">
					<span class="group-name-item">{{v.groupName}}</span>
					<el-dropdown @command="handleCommand">
						<span class="el-dropdown-link">
              <el-icon size="16px"><More /></el-icon>
						</span>
            <template #dropdown>
						<el-dropdown-menu>
							<el-dropdown-item :command="{type:menuType.EDIT,data: v}">{{$i18n.t('edit')}}</el-dropdown-item>
							<el-dropdown-item :command="{type:menuType.DEL,data: v}">{{$i18n.t('delete')}}</el-dropdown-item>
						</el-dropdown-menu>

            </template>
					</el-dropdown>
				</div>
      </el-menu-item>
    </el-menu>
		<Drawer :visible="visible" @handleClose="handleClose"/>
		<el-dialog
			:destroy-on-close="true"
			:center="false"
			:show-close="false"
      top="40px"
			:close-on-click-modal="false"
			:close-on-press-escape="false"
			:title="groupDialog.title"
			v-model="groupDialog.visible"
      width="720px"
			append-to-body
		>
			<ModifyProjectGroup :isPubGroup="groupDialog.pubGroup" :projectGroups="projectGroups" :groupId="groupDialog.groupId" :name="groupDialog.groupName" :desc="groupDialog.groupDesc" @handleClose="handleChangeAddDialog" @handleSubmit="handleSubmit"/>
		</el-dialog>
  </div>
</template>

<script>
import Drawer from '../Drawer'
import ModifyProjectGroup from '../ModifyProjectGroup'
import { addProjectGroup, editProjectGroup, deleteProjectGroupById } from '@/api/main'
import { AJAX_SUCCESS_MESSAGE, PROJECT_AUTHORITY } from '@/views/constant'
import { GROUP_DIALOG } from '../constant'
import { mapGetters } from 'vuex'

const MENU_TYPE = {
  EDIT: GROUP_DIALOG.EDIT,
  DEL: 'del'
}

export default {
  name: 'ProjectGroup',
  components: {
    Drawer,
    ModifyProjectGroup
  },
  data () {
    return {
      visible: false,
      defaultActive: undefined,
      groupDialog: {
        visible: false,
        type: GROUP_DIALOG.ADD,
        title: GROUP_DIALOG.ADD_TITLE,
        groupName: '',
        groupDesc: '',
        groupId: undefined,
        pubGroup: PROJECT_AUTHORITY.PRIVATE.value
      }
    }
  },
  props: {
    isStatic: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    ...mapGetters([
      'projectGroups'
    ]),
    menuType () {
      return MENU_TYPE
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    handleCommand ({ type, data }) {
      switch (type) {
        case MENU_TYPE.EDIT:
          this.handleChangeAddDialog({
            type: GROUP_DIALOG.EDIT,
            title: GROUP_DIALOG.EDIT_TITLE,
            ...data,
            pubGroup: data.pubGroup ? PROJECT_AUTHORITY.PUBLIC.value : PROJECT_AUTHORITY.PRIVATE.value
          })
          break
        case MENU_TYPE.DEL:
          this.$confirm(this.$i18n.t('deleteProjectGroupTip'), this.$i18n.t('prompt'), {
            confirmButtonText: this.$i18n.t('btnText.ok'),
            cancelButtonText: this.$i18n.t('btnText.cancel'),
            type: 'warning',
            showClose: false
          }).then(() => {
            deleteProjectGroupById({ projectGroupID: data.groupId }).then((data) => {
              if (data.message === AJAX_SUCCESS_MESSAGE) {
                this.init()
                this.visible = false
                this.$message({
                  type: 'success',
                  message: this.$i18n.t('successDeleted')
                })
              }
            }).catch(e => {})
          }).catch(() => {
            this.$message({
              type: 'info',
              message: this.$i18n.t('undelete')
            })
          })
          break
        default:
          break
      }
    },
    handleAddOpen () {
      this.handleChangeAddDialog({
        type: GROUP_DIALOG.ADD,
        title: GROUP_DIALOG.ADD_TITLE,
        groupName: '',
        groupDesc: '',
        groupId: undefined,
        pubGroup: PROJECT_AUTHORITY.PRIVATE.value
      })
    },
    handleChangeAddDialog (obj = {}) {
      this.groupDialog = {
        ...this.groupDialog,
        visible: !this.groupDialog.visible,
        ...obj
      }
    },
    handleSubmit (formData) {
      switch (this.groupDialog.type) {
        case GROUP_DIALOG.ADD:
          addProjectGroup(formData).then((data) => {
            if (data.message === AJAX_SUCCESS_MESSAGE) {
              this.init()
              this.handleChangeAddDialog()
            }
          }).catch(e => {})
          break
        case GROUP_DIALOG.EDIT:
          editProjectGroup(formData).then((data) => {
            if (data.message === AJAX_SUCCESS_MESSAGE) {
              this.init()
              this.handleChangeAddDialog()
            }
          }).catch(e => {})
          break
        default:
          break
      }
    },
    handleOpen (key, keyPath) {
      let hash = `#/`
      if (key === '000') {
        window.location.hash = hash
        return
      }
      if (!this.isStatic) {
        this.visible = true
      }
      if (key !== '999') {
        let groupId = this.projectGroups[Number(key)].groupId
        if (groupId) {
          hash += `?project_group_id=${groupId}`
        }
      }
      window.location.hash = hash
      this.$store.dispatch('projectlist/getList', key)
    },
    handleClose () {
      if (!this.isStatic) {
        this.visible = false
      }
      this.$store.dispatch('projectlist/changeFixedGroup', false)
    },
    handleFindIdxByGroupId (id) {
      let idx = null;
      (this.projectGroups || []).forEach((item, i) => {
        if (item.groupId === Number(id)) {
          idx = i
        }
      })
      return idx
    },
    init () {
      this.$store.dispatch('projectlist/getProjectGroup').then(() => {
        let groupId = this.$utils.getQuery("project_group_id")
        if (groupId) {
          let idx = this.handleFindIdxByGroupId(groupId)
          if (idx !== null) {
            this.defaultActive = `${idx}`
            this.handleOpen(idx)
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.project-group-container {
  user-select: none;
	background: #fff;
	position: absolute;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
	overflow-y: auto;
	border-right: solid 1px #e6e6e6;
	z-index: 9;
}
.project-group-container::-webkit-scrollbar{
	display: none;
}
.project-group-container .el-menu {
	border: none;
}
.project-group-container .group-all {
	background: rgba(249, 249, 249, 1);
	border-bottom: 1px solid rgba(233, 233, 233, 1);
  cursor: default;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.project-group-container .el-menu-item {
	height: 46px;
	line-height: 46px;
}
.project-group-container .add-project-btn {
	display: flex;
	align-items: center;
	justify-content: flex-end;
	font-size: 14px;
	color: #1890FF;
	font-weight: normal;
}
.project-group-container .add-project-btn-wrap {
	vertical-align: middle;
  height: 28px;
  display: flex;
  align-items: center;
  color: #1890FF;
  cursor: pointer;
}
.project-group-container .add-project-btn-wrap .el-icon {
  margin-right: 0;
}
.dialog-footer{
	text-align: right;
}

.project-group-container .el-menu-item .group-name {
	width: 100%;
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.project-group-container .el-menu-item .group-name >>> .el-dropdown-link{
	vertical-align: baseline;
}
.project-group-container .el-menu-item .group-name .group-name-item{
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
</style>>
