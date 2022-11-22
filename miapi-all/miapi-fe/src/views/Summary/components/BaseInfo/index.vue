<template>
	<div class="b-base-info">
		<div class="b-base-info-container">
      <el-form :label-position="isEN ? 'top': 'left'" label-width="auto">
        <el-row type="flex" justify="center">
          <el-col :span="10">
            <el-form-item :label="`${$i18n.t('projectName')}:`">
              <el-input :placeholder="$i18n.t('placeholder.enterProjectName')" v-model="projectName" autocomplete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex" justify="center">
          <el-col :span="10">
            <el-form-item :label="`${$i18n.t('belongingGroup')}:`">
              <el-input disabled :placeholder="$i18n.t('placeholder.PleaseFillGroup')" v-model="groupName" autocomplete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex" justify="center">
          <el-col :span="10">
            <el-form-item :label="`${$i18n.t('projectPermissions')}:`">
              <el-radio-group v-model="isPublic" size="small">
                <el-radio-button  size="small" :label="project_authority.PUBLIC.value">{{project_authority.PUBLIC.name}}</el-radio-button>
                <el-radio-button  size="small" :label="project_authority.PRIVATE.value">{{project_authority.PRIVATE.name}}</el-radio-button>
              </el-radio-group>
              <p class="tip-info">{{$i18n.t('addProjectDialogTips')}}</p>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex" justify="center">
          <el-col :span="10">
            <el-form-item :label="`${$i18n.t('projectDescription')}:`">
              <el-input :placeholder="$i18n.t('placeholder.enterDesc')" v-model="desc" type="textarea"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row type="flex" justify="center">
          <el-col :span="10">
            <el-form-item>
              <el-button :disabled="isDisabled" type="primary" @click="handleModify">{{$i18n.t('btnText.modify')}}</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div v-if="isAdmin" class="delete-info-wrap">
        <p class="danger"><el-icon :size="16" color="#e6a23c"><Warning  /></el-icon><span>{{$i18n.t('dangerousOperation')}}</span></p>
        <p><el-button class="show-btn" @click="handleChangeDeleteVisible">{{$i18n.t('btnText.check')}}<el-icon :size="12"><ArrowUp v-if="showDeleteInfo" /><ArrowDown v-else /></el-icon></el-button></p>
        <transition-group v-if="showDeleteInfo">
          <div class="delete-alert" key="delete-alert">
            <div class="delete-alert-left">
              <el-icon :size="16"><CircleClose /></el-icon>
              <div class="delete-alert-left-info">
                <p>{{$i18n.t('deleteItem')}}</p>
                <p>{{$i18n.t('deleteProjectInfo')}}</p>
              </div>
            </div>
            <button class="del-btn" @click="handleDelProject">{{$i18n.t('btnText.confirmDelete')}}</button>
          </div>
        </transition-group>
      </div>
    </div>
    <el-dialog
			:destroy-on-close="true"
			:center="false"
			:close-on-click-modal="false"
			:close-on-press-escape="false"
			:title="$i18n.t('deleteDialogTitle')"
			v-model="deleteVisible"
			width="540px"
			append-to-body
		>
    <DeleDialog @onCancel="handleCloseDele"/>
    </el-dialog>
	</div>
</template>

<script>
import { modifyProject } from '@/api/main'
import { PROJECT_AUTHORITY, AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import DeleDialog from './DeleteDialog'
import { mapGetters } from 'vuex'

export default {
  name: 'BaseInfo',
  components: {
    DeleDialog
  },
  data () {
    return {
      isEN: false,
      projectName: '',
      path: '',
      groupID: undefined,
      isPublic: PROJECT_AUTHORITY.PUBLIC.value,
      desc: '',
	    groupName: '',
      showDeleteInfo: false,
      deleteVisible: false,
      isAdmin: false
    }
  },
  computed: {
    ...mapGetters([
      'selfUserInfo',
      'projectDetail',
      'projectGroups'
    ]),
    projectID: function () {
      return this.$utils.getQuery('projectID')
    },
    isDisabled () {
      return (this.curGroupId !== this.groupID && this.projectName === this.projectDetail.projectName && this.desc === this.projectDetail.desc && this.isPublic === this.projectDetail.isPublic)
    },
    project_authority () {
      return PROJECT_AUTHORITY
    }
  },
  watch: {
    projectDetail: {
      handler (val) {
        this.projectName = val.projectName
        this.desc = val.desc
        this.isPublic = val.isPublic
        this.groupID = val.busGroupID
      },
      immediate: true,
      deep: true
    },
    projectGroups: {
      handler () {
        this.handleGroupName()
      },
      immediate: true,
      deep: true
    },
    groupID: {
      handler () {
        this.handleGroupName()
      },
      immediate: true,
      deep: true
    },
    selfUserInfo: {
      handler (val) {
        if (val && val.roleBos) {
          this.isAdmin = val.roleBos.some(v => v.name === 'admin')
        }
      },
      immediate: true,
      deep: true
    }
  },
  mounted () {
    this.isEN = this.$utils.languageIsEN()
    this.groupID = this.projectDetail.busGroupID
    if (!this.projectGroups || !this.projectGroups.length) {
      this.$store.dispatch('projectlist/getProjectGroup')
    }
  },
  methods: {
    handleCloseDele () {
      this.deleteVisible = false
    },
    handleDelProject () {
      this.deleteVisible = true
    },
    handleChangeDeleteVisible () {
      this.showDeleteInfo = !this.showDeleteInfo
    },
    handleGroupName () {
      if (this.projectGroups && this.projectGroups.length && this.groupID) {
        let arr = this.projectGroups.filter(v => v.groupId === this.groupID)
        if (arr.length) {
          this.groupName = arr[0].groupName
        }
      }
    },
    handleModify () {
      modifyProject({
        projectID: this.projectID,
        projectName: this.projectName,
        isPublic: this.isPublic,
        desc: this.desc,
        groupID: this.groupID
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message.success(this.$i18n.t('editSuccessfully'))
          this.$store.dispatch('summary.base/getDetail', this.projectID)
        }
      }).catch(e => {})
    }
  }
}
</script>
<style scoped>
.b-base-info {
	padding: 6% 24px;
}
.b-base-info .el-form-item {
  margin-bottom: 28px;
}
.b-base-info h4 {
	margin-bottom: 20px;
}
.b-base-info .tip-info {
	font-size: 12px;
	color: #aaa;
	white-space: nowrap;
}
.b-base-info .delete-info-wrap >p {
  text-align: center;
}
.b-base-info .delete-info-wrap .danger {
  font-size: 16px;
  color: rgba(0, 0, 0, 0.85);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.b-base-info .delete-info-wrap .danger i {
  margin-right: 4px;
}
.b-base-info .delete-info-wrap .show-btn i{
  margin-left: 4px;
}
.b-base-info .delete-info-wrap .delete-alert{
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid rgba(245, 34, 45, 1);
  border-radius: 4px;
  padding: 16px 20px;
  background: rgba(245, 34, 45, 0.05);
  margin-top: 16px;
}
.b-base-info .delete-info-wrap .delete-alert .delete-alert-left {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.b-base-info .delete-info-wrap .delete-alert .delete-alert-left i{
  color: #f56c6c;
  align-self: flex-start;
  font-size: 24px;
  margin: 2px 12px 0 0;
  font-weight: 550;
}
.b-base-info .delete-info-wrap .delete-alert .delete-alert-left .delete-alert-left-info p:first-child {
  font-size: 16px;
  color: rgba(0, 0, 0, 0.85);
  line-height: 22px;
}
.b-base-info .delete-info-wrap .delete-alert .delete-alert-left .delete-alert-left-info p:last-child {
  font-size: 14px;
  line-height: 26px;
  color: rgba(0, 0, 0, 0.43);
}
.b-base-info .delete-info-wrap .delete-alert .del-btn {
  font-size: 14px;
  color: #F5222D;
  border: 1px solid rgba(245, 34, 45, 1);
  border-radius: 4px;
  width: 88px;
  height: 32px;
  text-align: center;
  background: #fff;
  cursor: pointer;
}
</style>
