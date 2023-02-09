<template>
  <div class="main api-container">
    <section class="api-group">
      <ApiGroup @handleCopy="handleCopy" @handleSubMenuDelete="handleSubMenuDelete" @handleAdd="handleOpen({})" @handleCustomClick="handleCustomClick" @handleSelectGroup="handleSelectGroup" @handleGroupDelete="handleGroupDelete" @handleGroupEdit="handleGroupEdit"/>
    </section>
    <section class="api-content">
      <div v-if="!isSummary" :class="{'api-breadcrumb': true, 'api-detail-breadcrumb': isDetail}">
        <Breadcrumb/>
      </div>
      <div class="api-wrap">
        <!-- 调整路由 -->
        <router-view></router-view>
      </div>
    </section>
    <el-dialog
      :destroy-on-close="true"
      :center="false"
      width="640px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :title="dialog.title"
      v-model="dialog.visible"
      append-to-body
    >
      <GroupDialog :defaultData="dialog.data" :type="groupType" @submitAndGo="handleSubmitAndGo" @submit="handleSubmit" @handlechangevisible="handleClose"/>
    </el-dialog>
    <el-dialog
      :destroy-on-close="true"
      :center="false"
      width="1000px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :title="$i18n.t('copyAPI')"
      v-model="copyObj.visible"
      append-to-body
    >
      <CopyDialog @onClose="handleCloseCopy" :defaultData="copyObj.data" />
    </el-dialog>
  </div>
</template>
<script>
import ApiGroup from '@/components/ApiGroup'
import Breadcrumb from '@/components/Breadcrumb'
import GroupDialog from './components/GroupDialog'
import CopyDialog from "./components/CopyDialog"
import { deleteGroup, addGroup, editGroup, cleanRecyclingStation, deleteApi } from '@/api/apilist'
import { addIndex, editIndex, deleteIndex, removeApiFromIndex } from '@/api/apiindex'
import { GROUP_TYPE, GROUP_DIALOG } from './constant'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import SummaryComp from '@/views/Summary'
import IndexList from '@/views/IndexList'
import { PATH } from '@/router/constant'
import { mapGetters } from 'vuex'

export default {
  name: 'AppListComp',
  components: {
    ApiGroup,
    Breadcrumb,
    GroupDialog,
    SummaryComp,
    IndexList,
    CopyDialog
  },
  data () {
    return {
      dialog: {
        visible: false,
        title: GROUP_DIALOG.ADD_API_TITLE,
        type: GROUP_DIALOG.ADD,
        data: {}
      },
      copyObj: {
        visible: false,
        data: {}
      },
      isSummary: false,
      isDetail: false,
    }
  },
  computed: {
    ...mapGetters([
      'groupType',
      'groupList',
      'indexGroupID',
      'groupID',
      'indexGroupList'
    ])
  },
  watch: {
    $route: {
      handler: function (route) {
        this.isSummary = route.path === PATH.SUMMARY
        this.isDetail = route.path === PATH.API_DETAIL
        if (route.path === PATH.API_INDEX || route.path === PATH.ADD_INDEX_DOC) {
          this.$store.dispatch('apilist.group/changeGroupType', GROUP_TYPE.INDEX)
        } else if (route.path === PATH.API) {
          this.$store.dispatch('apilist.group/changeGroupType', GROUP_TYPE.API)
        }
      },
      immediate: true
    }
  },
  methods: {
    handleCustomClick (item) {
      cleanRecyclingStation({ projectID: this.$utils.getQuery('projectID') }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message.success('清空完成')
        }
      }).catch(e => {})
    },
    handleChangeCurrentGroupId (groupID) {
      this.$store.dispatch('apilist/changeGroupId', groupID)
      let obj = this.groupList.filter(item => item.groupID === groupID)
      if (obj.length) {
        this.$store.dispatch('apilist/changeGroupDesc', obj[0].groupDesc)
      } else {
        this.$store.dispatch('apilist/changeGroupDesc', '')
      }
    },
    handleChangeCurrentIndexGroupId (groupID) {
      this.$store.dispatch('apiindex/changeGroupIndexId', groupID)
      let obj = this.indexGroupList.filter(item => item.groupID === groupID)
      if (obj.length) {
        this.$store.dispatch('apilist/changeGroupDesc', obj[0].groupDesc)
      } else {
        this.$store.dispatch('apilist/changeGroupDesc', '')
      }
    },
    handleSelectGroup (groupID, jump = true) {
      if (this.groupType === GROUP_TYPE.API) {
        this.handleChangeCurrentGroupId(groupID)
        jump && this.$router.push({ path: PATH.API, query: { projectID: this.$utils.getQuery('projectID') } })
      } else {
        this.handleChangeCurrentIndexGroupId(groupID)
        jump && this.$router.push({ path: PATH.API_INDEX, query: { projectID: this.$utils.getQuery('projectID') } })
      }
    },
    removeGroup (item) {
      if (this.groupType === GROUP_TYPE.API) {
        return deleteGroup({ projectID: this.$utils.getQuery('projectID'), groupID: item.groupID })
      } else {
        return deleteIndex({ indexID: item.groupID })
      }
    },
    handleSubMenuDelete (item) {
      let projectID = this.$utils.getQuery('projectID')
      switch (this.groupType) {
        case GROUP_TYPE.API:
          if (item.apiID) {
            this.$confirm(this.$i18n.t('operationDeleteApi'), this.$i18n.t('prompt'), {
              confirmButtonText: this.$i18n.t('btnText.ok'),
              cancelButtonText: this.$i18n.t('btnText.cancel'),
              type: 'warning'
            }).then(() => deleteApi({ projectID, apiIDs: JSON.stringify([Number(item.apiID)]) })).then((data) => {
              if (data.message === AJAX_SUCCESS_MESSAGE) {
                this.$store.dispatch('apilist.group/getGroupViewList', projectID)
                this.$store.dispatch('apilist/apiList', { projectID, pageNo: 1, pageSize: 15 })
                if (this.$route.path !== PATH.API) {
                  this.$router.push({ path: PATH.API, query: { projectID } })
                }
                this.$message({
                  type: 'success',
                  message: this.$i18n.t('successDeleted')
                })
              }
            }).catch(e => {})
          }
          break
        case GROUP_TYPE.INDEX:
          this.$confirm(this.$i18n.t('operationRemoveCollectionContinue'), this.$i18n.t('prompt'), {
            confirmButtonText: this.$i18n.t('btnText.ok'),
            cancelButtonText: this.$i18n.t('btnText.cancel'),
            type: 'warning'
          }).then(() => removeApiFromIndex({ projectID, apiID: item.apiID, indexID: item.index_id })).then((data) => {
            if (data.message === AJAX_SUCCESS_MESSAGE) {
              this.$store.dispatch('apilist.group/getAllIndexGroupViewList', projectID)
              this.$store.dispatch('apiindex/getApiList', { projectID })
              if (this.$route.path !== PATH.API) {
                this.$router.push({ path: PATH.API_INDEX, query: { projectID } })
              }
              this.$message({
                type: 'success',
                message: this.$i18n.t('successDeleted')
              })
            }
          }).catch(() => {})
          break
        default:
          break
      }
    },
    handleGroupDelete (item) {
      this.$confirm(`${this.$i18n.t('deleteGroupTips.first')} ${item.groupName}, ${this.$i18n.t('deleteGroupTips.sec')}?`, this.$i18n.t('prompt'), {
        confirmButtonText: this.$i18n.t('btnText.ok'),
        cancelButtonText: this.$i18n.t('btnText.cancel'),
        type: 'warning'
      }).then(() => this.removeGroup(item)).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message({
            type: 'success',
            message: this.$i18n.t('successDeleted')
          })
          if (this.groupType === GROUP_TYPE.API) {
            this.$store.dispatch('apilist/groupList', this.$utils.getQuery('projectID')).then(() => {
              if (this.groupList.length) {
                this.handleChangeCurrentGroupId(this.groupList[0].groupID)
              }
            }).catch(e => {})
          } else {
            this.$store.dispatch('apiindex/getIndexGroupList', this.$utils.getQuery('projectID')).then(() => {
              if (this.indexGroupList.length) {
                this.handleChangeCurrentIndexGroupId(this.indexGroupList[0].groupID)
              }
            }).catch(e => {})
          }
        }
      }).catch(() => {})
    },
    handleOpen (obj) {
      this.dialog = {
        data: {
          groupID: undefined,
          groupName: "",
          isChild: 0,
          childGroupList: []
        },
        title: this.groupType === GROUP_TYPE.API ? GROUP_DIALOG.ADD_API_TITLE : GROUP_DIALOG.ADD_INDEX_TITLE,
        type: GROUP_DIALOG.ADD,
        ...obj,
        visible: true
      }
    },
    handleCopy (v) {
      this.copyObj = {
        visible: true,
        data: v
      }
    },
    handleCloseCopy () {
      this.copyObj = {
        visible: false,
        data: {}
      }
    },
    handleClose () {
      this.dialog = Object.assign({}, this.dialog, {
        visible: false
      })
    },
    handleGroupEdit (item) {
      this.handleOpen({
        title: this.groupType === GROUP_TYPE.API ? GROUP_DIALOG.EDIT_API_TITLE : GROUP_DIALOG.EDIT_INDEX_TITLE,
        type: GROUP_DIALOG.EDIT,
        data: item
      })
    },
    handleSubmitAndGo (formData) {
      this.handleSubmit(formData, true)
    },
    handleSubmit (formData, bool) {
      let param = {
        [this.groupType === GROUP_TYPE.API ? 'groupName' : 'indexName']: formData.groupName,
        projectID: this.$utils.getQuery('projectID'),
        [this.groupType === GROUP_TYPE.API ? 'groupDesc' : 'description']: formData.groupDesc
      }
      switch (this.dialog.type) {
        case 'add':
          if (this.groupType === GROUP_TYPE.API) {
            addGroup(param).then((data) => {
              if (data.message === AJAX_SUCCESS_MESSAGE) {
                this.$message.success(this.$i18n.t('successfullyCreatedCategory'))
                this.$store.dispatch('apilist/groupList', this.$utils.getQuery('projectID'))
              }
            }).catch(e => {})
          } else {
            addIndex({
              ...param,
              indexDoc: ''
            }).then((data) => {
              if (data.message === AJAX_SUCCESS_MESSAGE) {
                this.$message.success(this.$i18n.t('successfullyCreateInterfaceCollection'))
                this.$store.dispatch('apiindex/getIndexGroupList', this.$utils.getQuery('projectID'))
                if (bool) {
                  this.$router.push({ path: PATH.IMPORT_INDEX, query: { projectID: this.$utils.getQuery('projectID'), indexId: data.data } })
                }
              }
            }).catch(e => {})
          }
          break
        case 'edit':
          if (this.groupType === GROUP_TYPE.API) {
            param.groupID = formData.group
            editGroup(param).then((data) => {
              if (data.message === AJAX_SUCCESS_MESSAGE) {
                this.$message.success(this.$i18n.t('editSuccessfully'))
                this.$store.dispatch('apilist/groupList', this.$utils.getQuery('projectID')).then(() => {
                  let arr = this.groupList.filter(v => v.groupID === this.groupID)
                  if (arr.length) {
                    this.$store.dispatch('apilist/changeGroupDesc', arr[0].groupDesc)
                  }
                })
              }
            }).catch(e => {})
          } else {
            param.indexID = formData.group
            editIndex({ ...param, indexDoc: formData.indexDoc }).then((data) => {
              if (data.message === AJAX_SUCCESS_MESSAGE) {
                this.$message.success(this.$i18n.t('editSuccessfully'))
                this.$store.dispatch('apiindex/getIndexGroupList', this.$utils.getQuery('projectID')).then(() => {
                  let arr = this.indexGroupList.filter(v => v.groupID === this.indexGroupID)
                  if (arr.length) {
                    this.$store.dispatch('apilist/changeGroupDesc', arr[0].groupDesc)
                  }
                })
                if (bool) {
                  this.$router.push({ path: PATH.IMPORT_INDEX, query: { projectID: this.$utils.getQuery('projectID'), indexId: data.data } })
                }
              }
            }).catch(e => {})
          }
          break
        default:
          break
      }
    }
  }
}
</script>

<style scoped>
.api-container {
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  height: 100%;
  width: 100%;
}
.api-container .api-group{
  width: 279px;
  height: 100%;
  background: #fff;
  position: relative;
}
.api-container .api-content{
  width: calc(100% - 279px);
}
.api-container .api-content .api-wrap {
  width: 100%;
}
.api-content .api-breadcrumb {
  background: #fff;
  padding: 10px 40px 10px 30px;
  width: 100%;
  border-bottom: 1px solid #e6e6e6;
}
.api-content .api-breadcrumb.api-detail-breadcrumb {
  border: none;
  padding-bottom: 0;
}
</style>
