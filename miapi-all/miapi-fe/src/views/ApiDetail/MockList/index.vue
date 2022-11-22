<template>
  <div class="mock-content-container">
    <section class="mock-content">
      <router-link :to="{path: path.ADD_MOCK,query: $route.query}">
        <el-button class="add-new-mock" @click="handleAddMock" type="primary"><el-icon :size="12"><Plus /></el-icon>{{$i18n.t('btnText.newExpectations')}}</el-button>
      </router-link>
      <div class="mock-common-url">
        <p><span>{{$i18n.t('mockAddress')}}： {{mockUrl}}</span><a @click="handleCustomCopy(mockUrl)" href="javascript:;">{{$i18n.t('btnText.copy')}}</a></p>
        <p>
          <span>
            {{$i18n.t('customMock')}}：
          </span>
          <span>{{customOrg}}&nbsp;</span>
          <el-input :placeholder="$i18n.t('placeholder.pleaseEnter')" v-model="newUrl"></el-input>
          <a :class="{'btn-ok': true, disabled: disabledCustomOk}" @click="handleCustomMock" href="javascript:;">{{$i18n.t('btnText.ok')}}</a>
          <a @click="handleCustomCopy(`${customOrg}${newUrl}`)" href="javascript:;">{{$i18n.t('btnText.copy')}}</a>
        </p>
      </div>
      <el-table
        :data="tableList"
        class="mock-list-table"
        header-cell-class-name="mock-table-header-cell"
        style="width: 100%">
        <el-table-column
          show-overflow-tooltip
          prop="mockExpName"
        >
          <template #header>
            <span class="common-table-title">{{$i18n.t('mockTable.expectedName')}}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="updateUser"
          show-overflow-tooltip
        >
          <template #header>
            <span class="common-table-title">{{$i18n.t('mockTable.updater')}}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="updateTime"
        >
          <template #header>
            <span class="common-table-title">{{$i18n.t('mockTable.lastUpdateTime')}}</span>
          </template>
        <template #default="scope">
          {{moment(new Date(scope.row.updateTime)).format('YYYY-MM-DD HH:mm:ss')}}
        </template>
        </el-table-column>
        <el-table-column
        >
          <template #header>
            <span class="common-table-title">{{$i18n.t('btnText.operate')}}</span>
          </template>
        <template #default="scope">
          <span class="mock-operate" @click="handlePreview(scope.row)">{{$i18n.t('mockTable.preview')}}</span>
          <span @click="handleEdit(scope.row)" class="mock-operate">{{$i18n.t('edit')}}</span>
          <span @click="handleChangeStop(scope.row)" class="mock-operate">{{scope.row.enable ? $i18n.t('mockTable.stop') : $i18n.t('mockTable.enable')}}</span>
          <span @click="handleDel(scope.row)" :class="{'mock-operate': true,'disabled': scope.row.isDefault}">{{$i18n.t('mockTable.delete')}}</span>
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
        :fullscreen="false"
        :show-close="false"
        top="50px"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :title="`${$i18n.t('previewMoackExpectations')}: ${previewDialog.title}`"
        v-model="previewDialog.visible"
        width="640px"
        append-to-body
      >
        <MockPreview :content="previewDialog.content" :mockRequestParamType="previewDialog.mockRequestParamType" :requestJson="previewDialog.requestJson" @handleCancel="handleCancel"/>
      </el-dialog>
    </section>
  </div>
</template>
<script>
import { AJAX_SUCCESS_MESSAGE, PROTOCOL, REQUEST_TYPE } from '@/views/constant'
import Empty from '@/components/Empty'
import { mapGetters } from 'vuex'
import { PATH } from '@/router/constant'
import { getMockExpectList, deleteMockExpect, enableMockExpect, getMockExpectDetail, previewMockData, selfConfMockUrl } from '@/api/apimock'
import moment from 'moment'
import MockPreview from '@/views/AddMock/MockPreview'
import customCopy from "@/common/customCopy"
import { RADIO_TYPE } from '@/views/ApiList/constant'

export default {
  name: 'MockList',
  components: {
    Empty,
    MockPreview
  },
  data () {
    return {
      tableList: [],
      mockUrl: '',
      isLoading: false,
      newUrl: "",
      disabledCustomOk: true,
      previewDialog: {
        visible: false,
        title: '',
        requestJson: '',
        mockRequestParamType: RADIO_TYPE.FORM,
        content: {}
      }
    }
  },
  computed: {
    ...mapGetters([
      'apiDetail'
    ]),
    path () {
      return PATH
    },
    moment () {
      return moment
    },
    customOrg () {
      return "xxx"
    }
  },
  watch: {
    newUrl: {
      handler (val, old) {
        this.disabledCustomOk = !val || val === old
      },
      immediate: true,
      deep: true
    },
    apiDetail: {
      handler (val) {
        if (!val) {
          return
        }
        if (this.$utils.getQuery('apiID')) {
          this.init(this.$utils.getQuery('apiID'))
        }
        if (val.baseInfo && val.mockInfo) {
          this.mockUrl = val.mockInfo.mockUrl
        } else if (val.dubboApiBaseInfo) {
          this.mockUrl = val.mockUrl
        } else if (val.gatewayApiBaseInfo) {
          this.mockUrl = val.mockUrl
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    init (apiID) {
      let projectID = this.$utils.getQuery('projectID')
      if (this.$utils.getQuery('indexProjectID')) {
        projectID = this.$utils.getQuery('indexProjectID')
      }
      if (!projectID || this.isLoading) {
        return
      }
      this.isLoading = true
      getMockExpectList({ apiID, projectID }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.tableList = data.data.expectList || []
          this.newUrl = data.data.proxyUrl || ""
          if (data.data.proxyUrl) {
            this.$store.dispatch("apilist/changeCustomMockUrl", `${this.customOrg}${data.data.proxyUrl}`)
          } else {
            this.$store.dispatch("apilist/changeCustomMockUrl", "")
          }
        }
      }).catch(e => {}).finally(() => {
        this.isLoading = false
      })
    },
    handleCustomCopy(val){
      customCopy(val)
    },
    handleCancel () {
      this.previewDialog = {
        ...this.previewDialog,
        visible: false
      }
    },
    handleCustomMock () {
      if (!this.mockUrl || this.disabledCustomOk) {
        return
      }
      let originUrl = this.mockUrl
      if (this.mockUrl.indexOf('.com/') !== 0) {
        originUrl = this.mockUrl.split('.com/')[1]
      }
      let expectId = null
      if (this.tableList.length && this.tableList.filter(v => v.isDefault).length) {
        expectId = this.tableList.filter(v => v.isDefault)[0].id
      }
      selfConfMockUrl({
        originUrl,
        newUrl: this.newUrl,
        expectId
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message.success("success")
          this.disabledCustomOk = true
        }
      }).catch(e => {
        console.error(e)
      })
    },
    handleDel (row) {
      if (row.isDefault) {
        return
      }
      let projectID = this.$utils.getQuery('projectID')
      if (this.$utils.getQuery('indexProjectID')) {
        projectID = this.$utils.getQuery('indexProjectID')
      }
      this.$confirm(`${this.$i18n.t('mockWarn.deleteTip')} ${row.mockExpName}, ${this.$i18n.t('mockWarn.continue')}?`, this.$i18n.t('prompt'), {
        confirmButtonText: this.$i18n.t('btnText.ok'),
        cancelButtonText: this.$i18n.t('btnText.cancel'),
        type: 'warning'
      }).then(() => deleteMockExpect({ mockExpectID: row.id, projectID })).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.tableList = this.tableList.filter(item => item.id !== row.id)
          this.$message.success(this.$i18n.t('successDeleted'))
        }
      }).catch(() => {})
    },
    handleChangeStop (row) {
      let projectID = this.$utils.getQuery('projectID')
      if (this.$utils.getQuery('indexProjectID')) {
        projectID = this.$utils.getQuery('indexProjectID')
      }
      enableMockExpect({ projectID, mockExpectID: row.id, enable: row.enable ? 0 : 1 }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.$message.success(this.$i18n.t('successfullyModified'))
          this.tableList = this.tableList.map(item => {
            if (item.id === row.id) {
              return {
                ...item,
                enable: row.enable ? 0 : 1
              }
            }
            return item
          })
        }
      }).catch(e => {})
    },
    handlePreview (row) {
      let requestJson
      let mockRequestParamType = RADIO_TYPE.FORM
      getMockExpectDetail({ mockExpectID: row.id }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          mockRequestParamType = data.data.mockRequestParamType
          requestJson = mockRequestParamType === RADIO_TYPE.FORM ? data.data.mockParams : data.data.mockRequestRaw
          try {
            requestJson = JSON.parse(requestJson)
          } catch (error) {}
          return previewMockData({ mockRule: data.data.mockRule, mockDataType: row.mockDataType })
        } else {
          return false
        }
      }).then((data) => {
        if (data && data.message === AJAX_SUCCESS_MESSAGE) {
          let content = data.data
          try {
            content = JSON.parse(content)
          } catch (error) {}
          this.previewDialog = {
            visible: true,
            title: row.mockExpName,
            requestJson,
            mockRequestParamType: mockRequestParamType || RADIO_TYPE.FORM,
            content
          }
        }
      }).catch(e => {})
    },
    handleEdit (row) {
      this.$router.push({ path: PATH.ADD_MOCK, query: { ...this.$route.query, mockExpectID: row.id } })
    }
  }
}
</script>
<style scoped>
.mock-content-container{
  height: 100%;
  padding: 20px 20px 0;
  overflow-y: auto;
}
.mock-content-container::-webkit-scrollbar{
  display: none;
}
.mock-content{
  background: #fff;
	padding: 16px;
  min-height: 100%;
}
.mock-content .add-new-mock {
	margin-bottom: 16px;
}
.mock-content .mock-common-url {
	margin: 0;
	padding: 0;
	font-size: 14px;
	color: #666;
	white-space: nowrap;
	border: 1px solid rgba(232, 232, 232, 1);
	border-bottom: none;
	user-select: none;
	padding: 10px 16px 10px 16px;
}
.mock-content .mock-common-url p{
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.mock-content .mock-common-url p .btn-ok {
  margin-right: -8px;
}

.mock-content .mock-common-url p .btn-ok.disabled{
  color: #ccc;
}
.mock-content .mock-common-url p span {
	display: inline-block;
}
.mock-content .mock-common-url p:first-child{
  margin-bottom: 10px;
}
.mock-content .mock-common-url p:first-child span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.mock-content .mock-common-url p a{
	color: #108EE9;
	cursor: pointer;
	display: inline-block;
	margin-left: 20px;
}
.mock-content >>> .mock-table-header-cell {
	background: rgba(250, 250, 250, 1) !important;
	border: 1px solid rgba(232, 232, 232, 1);
	border-right: none;
	border-left: none;
	height: 53px;
}
.mock-content >>> .mock-table-header-cell .cell {
	padding-left: 16px;
}
.mock-content >>> .mock-table-header-cell:first-child{
	border-left: 1px solid rgba(232, 232, 232, 1);
}
.mock-content >>> .mock-table-header-cell:last-child{
	border-right: 1px solid rgba(232, 232, 232, 1);
}
.mock-content .mock-list-table >>> .el-table__row {
	height: 58px;
}
.mock-content .mock-list-table >>> .el-table__row td {
	padding-left: 8px;
}
.mock-content .mock-list-table .mock-operate {
	color: #108EE9;
	font-size: 14px;
	display: inline-block;
	vertical-align: middle;
	margin-right: 10px;
	cursor: pointer;
}
.mock-content .mock-list-table .mock-operate:last-child{
	margin-right: 0;
}
.mock-content .mock-list-table .mock-operate.disabled {
	color: rgba(0, 0, 0, 0.45);
	cursor: no-drop;
}
</style>
