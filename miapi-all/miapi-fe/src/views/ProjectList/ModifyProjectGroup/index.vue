<template>
	<div class="m-modify-project-group">
		<el-form label-width="auto">

			<el-row v-if="projectGroupID !== undefined && projectGroupID !== ''" type="flex" justify="center" :gutter="20">
				<el-col :span="18">
					<el-form-item prop="projectGroupID" :label="`${$i18n.t('grouping')}:`">
						<el-select disabled style="width: 100%" v-model="projectGroupID" :placeholder="`${$i18n.t('placeholder.selectGroup')}`">
							<el-option v-for="(v, index) in projectGroups" :key="index" :label="v.groupName" :value="v.groupId"></el-option>
						</el-select>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="18">
					<el-form-item prop="groupName" :label="`${$i18n.t('name')}:`">
						<el-input :placeholder="`${$i18n.t('placeholder.enterGroupName')}`" v-model="groupName" autocomplete="off"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

			<el-row type="flex" justify="center" :gutter="20">
				<el-col :span="18">
					<el-form-item prop="groupDesc" :label="`${$i18n.t('description')}:`">
						<el-input type="textarea" :placeholder="`${$i18n.t('placeholder.enterDesc')}`" v-model="groupDesc"></el-input>
					</el-form-item>
				</el-col>
			</el-row>

      <el-row type="flex" justify="center" :gutter="20">
				<el-col :span="18">
					<el-form-item prop="pubGroup" :label="`${$i18n.t('authority')}:`">
						<el-radio-group v-model="pubGroup">
							<el-radio-button :label="projectAuthority.PUBLIC.value">{{projectAuthority.PUBLIC.name}}</el-radio-button>
							<el-radio-button :label="projectAuthority.PRIVATE.value">{{projectAuthority.PRIVATE.name}}</el-radio-button>
						</el-radio-group>
						<p class="authority">{{$i18n.t('addProjectDialogTips')}}</p>
					</el-form-item>
				</el-col>
			</el-row>

      <!-- <el-row v-if="projectGroupID !== undefined && projectGroupID !== ''" type="flex" justify="center" :gutter="20">
				<el-col :span="18">
          <p class="add-btn">
            <el-button text type="primary" size="small" @click="addDialog = true">添加成员</el-button>
          </p>
					<el-table
            :data="userList"
            max-height="240"
            border
            stripe
            header-cell-class-name="table-header-cell"
            style="width: 100%; margin-bottom: 10px">
            <el-table-column
              show-overflow-tooltip
              prop="name"
              width="100"
            >
              <template #header>
                <span class="common-table-title">{{$i18n.t('userList.name')}}</span>
              </template>
            </el-table-column>
            <el-table-column
              show-overflow-tooltip
              prop="role"
            >
              <template #header>
                <span class="common-table-title">{{$i18n.t('userList.role')}}</span>
              </template>
              <template #default="scope">
                <el-checkbox-group size="mini" @change="handleChanage($event,scope.row)" v-model="scope.row.role">
                  <el-checkbox size="mini" v-for="v in Object.keys(role)" :key="v" :label="v" >{{role[v]}}</el-checkbox>
                </el-checkbox-group>
              </template>
            </el-table-column>
            <el-table-column
              width="80">
              <template #header>
                <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
              </template>
              <template #default="scope">
                <el-button @click.stop="handleDel(scope.row)" text type="primary" size="small">{{$i18n.t('btnText.delete')}}</el-button>
              </template>
            </el-table-column>
          </el-table>
				</el-col>
			</el-row> -->

		</el-form>

		<div class="dialog-footer">
			<el-button @click="handleClose">{{$i18n.t('btnText.cancel')}}</el-button>
			<el-button :disabled="!groupName" type="primary" @click="handleSubmit">{{$i18n.t('btnText.ok')}}</el-button>
		</div>

    <el-dialog
      :destroy-on-close="true"
      :center="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :title="$i18n.t('addMember')"
    	v-model="addDialog"
      width="540px"
      append-to-body
    >
      <ModifyUser :userData="userData" :role="role" @onSubmit="onSubmit" @handleClose="handleChangeDialog"/>
    </el-dialog>

	</div>
</template>

<script>
import { ROLE, AJAX_SUCCESS_MESSAGE, PROJECT_AUTHORITY } from '@/views/constant'
import { getGroupPartnerList, inviteGroupPartner, editGroupPartnerRole, removeGroupPartner } from "@/api/projectuser"
import ModifyUser from "@/views/Summary/components/UserList/ModifyUser"

export default {
  name: 'ModifyProjectGroup',
  components: {
    ModifyUser
  },
  data () {
    return {
      groupName: '',
      groupDesc: '',
      projectGroupID: undefined,
      userData: {},
      pubGroup: PROJECT_AUTHORITY.PRIVATE.value,
      addDialog: false,
      role: {
        [ROLE.ADMIN.key]: ROLE.ADMIN.name,
        [ROLE.MEMBER.key]: ROLE.MEMBER.name,
        [ROLE.GUEST.key]: ROLE.GUEST.name
      }
    }
  },
  computed: {
    userList () {
      let userObj = {}
      Object.keys(this.userData).forEach(v => {
        for (let i = 0; i < this.userData[v].length; i++) {
          let obj = this.userData[v][i]
          let id = Object.keys(obj)[0]
          if (!userObj[id]) {
            userObj[id] = {
              name: obj[id],
              id,
              role: [v]
            }
          } else {
            userObj[id].role.push(v)
          }
        }
      })
      return Object.values(userObj)
    },
    projectAuthority () {
      return PROJECT_AUTHORITY
    }
  },
  props: {
    name: {
      type: String,
      default: ''
    },
    desc: {
      type: String,
      default: ''
    },
    projectGroups: {
      type: Array,
      default () {
        return []
      }
    },
    groupId: {
      type: Number,
      default: undefined
    },
    isPubGroup: {
      type: Number,
      default: PROJECT_AUTHORITY.PRIVATE.value
    }
  },
  watch: {
    name: {
      handler (val) {
        this.groupName = val
      },
      immediate: true
    },
    desc: {
      handler (val) {
        this.groupDesc = val
      },
      immediate: true
    },
    groupId: {
      handler (val) {
        this.projectGroupID = val
      },
      immediate: true
    },
    isPubGroup: {
      handler (val) {
        this.pubGroup = val
      },
      immediate: true
    },
    projectGroupID: {
      handler (val, old) {
        if (val && val !== old) {
          this.getUserList(val)
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    handleClose () {
      this.$emit('handleClose')
    },
    handleSubmit () {
      this.$emit('handleSubmit', { groupName: this.groupName, groupDesc: this.groupDesc, groupID: this.projectGroupID, pubGroup: !!this.pubGroup })
    },
    handleChangeDialog () {
      this.addDialog = !this.addDialog
    },
    onSubmit ({ selectedRole, value }) {
      let roleType
      Object.values(ROLE).forEach(item => {
        if (item.key === selectedRole) {
          roleType = item.value
        }
      })

      inviteGroupPartner({
        inviterUserID: this.$store.getters.selfUserInfo.id,
        userIds: value,
        groupID: this.projectGroupID,
        roleType
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.getUserList(this.projectGroupID)
          this.$message.success(this.$i18n.t('addedSuccessfully'))
          this.handleChangeDialog()
        }
      }).catch(e => {})
    },
    handleChanage (val, row) {
      let roleTypes = []
      Object.values(ROLE).forEach(item => {
        if (val.includes(item.key)) {
          roleTypes.push(item.value)
        }
      })
      editGroupPartnerRole({
        userID: row.id,
        groupID: this.projectGroupID,
        roleTypes: JSON.stringify(roleTypes)
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.getUserList(this.projectGroupID)
          this.$message.success(this.$i18n.t('successfullyModified'))
        } else {
          row.role.pop()
        }
      }).catch(e => {
        row.role.pop()
      })
    },
    handleDel (row) {
      this.$confirm(this.$i18n.t('deleteUser'), this.$i18n.t('prompt'), {
        confirmButtonText: this.$i18n.t('btnText.ok'),
        cancelButtonText: this.$i18n.t('btnText.cancel'),
        type: 'warning'
      }).then(() => removeGroupPartner({ groupID: this.projectGroupID, userID: row.id })).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message.success(this.$i18n.t('successDeleted'))
          this.getUserList(this.projectGroupID)
        }
      }).catch(e => {})
    },
    getUserList (val) {
      getGroupPartnerList({
        groupID: val
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.userData = data.data
        } else {
          this.$message.error(data.message)
        }
      }).catch(e => {
        console.log(e)
      })
    }
  }
}
</script>
<style scoped>
.m-modify-project-group .dialog-footer{
	text-align: right;
}
.m-modify-project-group .el-table,
.m-modify-project-group .el-table >>> .el-checkbox__label {
  font-size: 12px;
}
.m-modify-project-group >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	height: 30px;
}
.m-modify-project-group .add-btn {
  text-align: right;
}
.m-modify-project-group .add-btn .el-button {
  padding: 0 0 4px;
}
.m-modify-project-group .authority {
	font-size: 12px;
}
</style>
