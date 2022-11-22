<template>
	<div class="u-user-list">
		<div class="user-header">
			<!-- <h4>成员管理</h4> -->
			<el-button @click="handleChangeDialog" type="primary">{{$i18n.t('addMember')}}</el-button>
		</div>
		<el-table
			:data="userList"
			class="user-table"
			header-cell-class-name="table-header-cell"
			style="width: 100%">
			<el-table-column
				show-overflow-tooltip
				prop="name"
				width="240"
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
					<el-checkbox-group @change="handleChanage($event,scope.row)" v-model="scope.row.role">
						<el-checkbox v-for="v in Object.keys(role)" :key="v" :label="v" >{{role[v]}}</el-checkbox>
					</el-checkbox-group>
				</template>
			</el-table-column>
			<el-table-column
				width="130">
        <template #header>
          <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
        </template>
				<template #default="scope">
					<el-button @click.stop="handleDel(scope.row)" text type="primary" size="small">{{$i18n.t('btnText.delete')}}</el-button>
				</template>
			</el-table-column>
      <template #empty>
        <div>
          <Empty/>
        </div>
      </template>
		</el-table>
		<el-dialog
      :destroy-on-close="true"
      :center="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :title="$i18n.t('addMember')"
    	v-model="dialog"
      width="540px"
      append-to-body
    >
      <ModifyUser :userData="userData" :role="role" @onSubmit="onSubmit" @handleClose="handleChangeDialog"/>
    </el-dialog>
	</div>
</template>
<script>
import { getPartnerList, invitePartner, editPartnerRole, removePartner } from '@/api/projectuser'
import ModifyUser from './ModifyUser'
import { ROLE, AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import Empty from '@/components/Empty'
export default {
  name: 'UserList',
  components: {
    ModifyUser,
    Empty
  },
  data () {
    return {
      userData: {},
      dialog: false,
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
    projectID () {
      return this.$utils.getQuery('projectID')
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      getPartnerList({ projectID: this.projectID }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.userData = data.data || {}
        }
      }).catch(e => {})
    },
    handleChangeDialog () {
      this.dialog = !this.dialog
    },
    handleDel (row) {
      this.$confirm(this.$i18n.t('deleteUser'), this.$i18n.t('prompt'), {
        confirmButtonText: this.$i18n.t('btnText.ok'),
        cancelButtonText: this.$i18n.t('btnText.cancel'),
        type: 'warning'
      }).then(() => removePartner({ projectID: this.projectID, userID: row.id })).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
					 this.$message({
            type: 'success',
            message: this.$i18n.t('successDeleted')
          })
          this.init()
        }
      }).catch(e => {})
    },
    handleCommand (role, row) {
      // let roleType = role === 'Admin' ? 0 : (role === 'Member' ? 1 : 2)
      // invitePartner({
      // 	inviterUserID:this.$store.getters.selfUserInfo.id,
      // 	userIds:val.map(item=>Number(item)),
      // 	projectID: this.projectID,
      // 	roleType
      // }).then((data)=>{
      //   if (data.message === AJAX_SUCCESS_MESSAGE) {
      //     this.init()
      //     this.$message.success('添加成功')
      //   }
      // }).catch(e=>{})
    },
    handleChanage (val, row) {
      let roleTypes = []
      Object.values(ROLE).forEach(item => {
        if (val.includes(item.key)) {
          roleTypes.push(item.value)
        }
      })
      editPartnerRole({
        userID: row.id,
        projectID: this.projectID,
        roleTypes: JSON.stringify(roleTypes)
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.init()
          this.$message.success(this.$i18n.t('successfullyModified'))
        } else {
          row.role.pop()
        }
      }).catch(e => {
        row.role.pop()
      })
    },
    onSubmit ({ selectedRole, value }) {
      let roleType
      Object.values(ROLE).forEach(item => {
        if (item.key === selectedRole) {
          roleType = item.value
        }
      })

      invitePartner({
        inviterUserID: this.$store.getters.selfUserInfo.id,
        userIds: value,
        projectID: this.projectID,
        roleType
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.init()
          this.$message.success(this.$i18n.t('addedSuccessfully'))
          this.handleChangeDialog()
        }
      }).catch(e => {})
    }
  }
}
</script>
<style scoped>
.u-user-list{
	padding: 18px 24px 0;
  height: 100%;
  overflow-y: auto;
}
.u-user-list::-webkit-scrollbar {
  display: none;
}
.u-user-list .user-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 18px;
}
.u-user-list .user-table {
	font-size: 14px;
}
.u-user-list .user-table .el-button {
	font-size: 14px;
	font-weight: 400;
}
.u-user-list >>> .table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border: 1px solid rgba(232, 232, 232, 1);
	border-right: none;
	border-left: none;
	height: 52px;
}
.u-user-list >>> .table-header-cell:first-child{
	border-left: 1px solid rgba(232, 232, 232, 1);
	border-top-left-radius: 4px;
}
.u-user-list >>> .table-header-cell:last-child{
	border-right: 1px solid rgba(232, 232, 232, 1);
	border-top-right-radius: 4px;
}
.u-user-list .user-table .el-dropdown-link {
	cursor: pointer;
}
.u-user-list >>> .el-table td {
  padding: 10px 0;
}
</style>
