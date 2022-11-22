<template>
	<div class="index-list-container">
		<div class="index-list-wrap">
      <div class="index-list">
        <div v-if="indexGroupID" @click="handleAddIndexDoc" class="common-frame index-list-word">
          <span>{{curentGroup.groupName}} {{$i18n.t('collectionDocument')}} ({{curentGroup.indexDoc ? $i18n.t('added') : $i18n.t('notAdded')}})</span>
        </div>
        <TableList v-if="indexGroupID"/>
        <Empty v-else/>
      </div>
    </div>
    <el-dialog
      :destroy-on-close="true"
      :center="false"
      width="724px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :title="$i18n.t('shareCollection')"
      v-model="visible"
      append-to-body
    >
      <ShareIndexDialog @onOk="handleOk"/>
    </el-dialog>
    <el-dialog
      :destroy-on-close="true"
      :center="false"
      width="480px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      v-model="successVisible"
      append-to-body
    >
      <template #title>
        <div style="display: flex; align-items: center">
          <el-icon color="#52C41A" :size="24"><SuccessFilled /></el-icon>&nbsp;<span style="font-size: 14px;color: rgba(0, 0, 0, 0.65;font-weight: 650">{{$i18n.t('successfullyCreatedSharingLink')}}</span>
        </div>
      </template>
      <div style="margin: 20px 0">
        <ul>
          <li>
            <h6>{{$i18n.t('intranet')}}：</h6>
            <el-input disabled v-model="url"/>
            <el-button style="margin: 9px 0 10px 0" text type="primary" size="small" @click="handleCustomCopy(url)" href="javascript:;">{{$i18n.t('btnText.copyAddress')}}</el-button>
            <a style="margin: 0px 0 10px 10px; font-size: 12px; color:#108EE9; font-weight: 500" :href="url" target="_blank">{{$i18n.t('btnText.check')}}</a>
          </li>
          <li>
            <h6>{{$i18n.t('extranet')}}：</h6>
            <el-input disabled v-model="outer"/>
            <el-button style="margin: 9px 0 10px 0" type="primary" text size="small" @click="handleCustomCopy(outer)" href="javascript:;">{{$i18n.t('btnText.copyAddress')}}</el-button>
            <a style="margin: 0px 0 10px 10px; font-size: 12px; color:#108EE9; font-weight: 500" :href="outer" target="_blank">{{$i18n.t('btnText.check')}}</a>
          </li>
        </ul>
      </div>
      <p style="text-align: right">
			  <el-button @click="handleCancel" type="primary">{{$i18n.t('btnText.close')}}</el-button>
      </p>
    </el-dialog>
	</div>
</template>

<script>
import TableList from '@/views/ApiList/components/TableList'
import { PATH } from '@/router/constant'
import ShareIndexDialog from './ShareIndex'
import customCopy from "@/common/customCopy"
import { mapGetters } from 'vuex'
import Empty from '@/components/Empty'

export default {
  name: 'IndexList',
  components: {
    TableList,
    ShareIndexDialog,
    Empty
  },
  computed: {
    ...mapGetters([
      'showShareDialog',
      'indexGroupID',
      'indexGroupList'
    ])
  },
  data () {
    return {
      visible: false,
      fromStore: false,
      successVisible: false,
      url: '',
      outer: '',
      curentGroup: {}
    }
  },
  watch: {
    showShareDialog (val) {
      this.fromStore = true
      this.visible = val
    },
    visible (val, old) {
      if (val !== old && !this.fromStore) {
        this.$store.dispatch('apiindex/changeShareDialogBool', val)
      } else if (this.fromStore) {
        this.fromStore = false
      }
    },
    indexGroupID: {
      handler (val) {
        let arr = this.indexGroupList.filter(v => v.groupID === Number(val))
        if (arr.length) {
          this.curentGroup = arr[0]
        }
      },
      immediate: true
    }
  },
  methods: {
    handleAddIndexDoc () {
      this.$router.push({ path: PATH.ADD_INDEX_DOC, query: { projectID: this.$utils.getQuery('projectID'), indexGroupID: this.indexGroupID } })
    },
    handleOk (obj) {
      this.url = obj.url
      this.outer = obj.outer
      this.successVisible = true
    },
    handleCancel () {
      this.successVisible = false
    },
    handleCustomCopy(val){
      customCopy(val)
    },
  }
}
</script>
<style scoped>
.index-list-container {
  width: 100%;
  height: calc(100vh - 138px);
  padding: 0 20px 0;
}
.index-list-container .index-list-wrap {
  padding: 20px 0 0;
  height: 100%;
  overflow-y: auto;
}
.index-list-container .index-list {
  background: #fff;
  padding: 20px;
  min-height: 100%;
}
.index-list-container  .index-list-wrap::-webkit-scrollbar {
  display: none;
}
.index-list-container .index-list-wrap .common-frame{
  padding: 16px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  color: #1890FF;
  font-size: 14px;
  margin-bottom: 20px;
}
.index-list-container .index-list-wrap .add-word {
  cursor: pointer;
}
.index-list-container .index-list-wrap .add-word i {
  margin-right: 4px;
  font-size: 12px;
}
.index-list-container .index-list-wrap .index-list-word {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
  cursor: pointer;
}
.index-list-container .index-list-wrap .index-list-word span {
  cursor: pointer;
}
.index-list-container .index-list-wrap .index-list-word .el-button {
  padding: 0;
  font-size: 14px;
  font-weight: normal;
}
</style>
