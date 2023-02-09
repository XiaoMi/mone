<template>
	<div class="test-case-wrap">
		<span class="test-case-btn" @click="visible = true">{{$i18n.t("testCase")}}</span>
		<el-drawer
      custom-class="test-case-drawer"
      append-to-body
      destroy-on-close
      :withHeader="true"
      :size="540"
      v-model="visible"
      :title="$i18n.t('testCase')"
      direction="rtl">
      <section class="test-case-container">
        <div class="search-wrap">
          <el-input v-model="filterText" :placeholder="$i18n.t('placeholder.pleaseEnterUseCaseName')"/>
          <el-button type="primary">{{$i18n.t('btnText.search')}}</el-button>
        </div>
        <div class="new-pak">
          <el-button @click="handleAddNewGroup" text type="primary" size="small"><el-icon :size="12"><Plus /></el-icon>{{$i18n.t("createNewGroup")}}</el-button>
        </div>
        <div class="exmp-list">
          <el-tree
            v-if="list.length"
            v-loading="loading"
            :data="list"
            node-key="id"
            @node-click="handleSelectNode"
            :props="defaultProps"
            :filter-node-method="filterNode"
            ref="treeRef">
            <template #default="{ node, data }">
              <span class="custom-tree-node">
                <span class="custom-tree-node-label">
                  <span>
                    <span class="protocol">{{renderProto(data.apiProtocal)}}</span>
                    <var v-if="!!renderLabel(node, data)">{{renderLabel(node, data)}}</var>
                  </span>
                  <template v-if="handleShowFolder(data)">
                    <el-icon v-if="node.expanded"><FolderOpened /></el-icon>
                    <el-icon v-else><Folder /></el-icon>
                  </template>
                  <span v-if="!data.isEdit" class="custom-tree-node-name">{{ node.label }}</span>
                  <span @click.stop="handleStop" v-else class="custom-tree-node-name">
                    <el-input ref="inputNameRef" @blur.native="handleBlur(data)" @keydown.enter.native="handleChangeSubmit($event ,node, data)" v-model="editData.name"></el-input>
                  </span>
                </span>
                <span>
                  <!-- <span class="protocol">{{protocol[data.apiProtocal]}}</span> -->
                  <el-button
                    text 
                    type="primary"
                    size="small"
                    @click.stop="handleChangeCaseNmae(node, data)"
                  >
                    {{$i18n.t("btnText.modify")}}
                  </el-button>
                  <el-popconfirm
                    @confirm="handleDelete(data)"
                    @cancel="() => {}"
                    :title="`${$i18n.t('testCaseConfirm.text1')}${node.label}${$i18n.t('testCaseConfirm.text2')}`"
                    confirm-button-type="text"
                    cancel-button-type="text"
                  >
                    <template #reference>
                      <el-button
                        text 
                        type="primary"
                        size="small"
                        @click.stop="()=>{}"
                      >
                        {{$i18n.t("btnText.delete")}}
                      </el-button>
                    </template>
                  </el-popconfirm>
                  <!-- <el-dropdown :hide-on-click="false">
                    <span class="el-dropdown-link"><el-icon :size="12"><MoreFilled /></el-icon></span>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item>
                          <el-button
                            text 
                            type="primary"
                            size="small"
                            @click.stop="handleChangeCaseNmae(node, data)"
                          >
                            {{$i18n.t("btnText.modify")}}
                          </el-button>
                        </el-dropdown-item>
                        <el-dropdown-item>
                          <el-popconfirm
                              @confirm="handleDelete(data)"
                              @cancel="() => {}"
                              :title="`${$i18n.t('testCaseConfirm.text1')}${node.label}${$i18n.t('testCaseConfirm.text2')}`"
                              confirm-button-type="text"
                              cancel-button-type="text"
                            >
                              <template #reference>
                                <el-button
                                  text 
                                  type="primary"
                                  size="small"
                                  @click.stop="()=>{}"
                                >
                                  {{$i18n.t("btnText.delete")}}
                                </el-button>
                              </template>
                            </el-popconfirm>
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown> -->
                </span>
              </span>
            </template>
          </el-tree>
          <Empty v-else />
        </div>
      </section>
    </el-drawer>
	</div>
</template>

<script lang="ts" >
import createCaseGroup from "@/common/createCaseGroup"
import Empty from "@/components/Empty/index.vue"
import { getCasesByApi, getCasesByProject, saveTestCaseDir, deleteCaseGroup, deleteCaseById, updateCaseName, updateCaseDirName } from "@/api/apitest"
import { AJAX_SUCCESS_MESSAGE, PROTOCOL_TYPE, API_REQUEST_PARAM_TYPE, PROTOCOL } from "@/views/constant"
import { getDataType } from "@/store/utils"
import { RADIO_TYPE } from '@/views/TestApi/constant'
import { useStore } from "vuex"
import { defineComponent, reactive, toRefs, computed, watch, ref, nextTick } from "vue"
import { ElMessage } from 'element-plus'
import i18n from "@/lang"
import * as utils from "@/utils"
export default defineComponent({
  components: {
    Empty
  },
  props: {
    isGlobal: {
      type: Boolean,
      default: true
    }
  },
  setup(props, ctx){
    const store = useStore()
    const treeRef = ref(null)
    const inputNameRef = ref(null)
    const state = reactive({
      visible: false,
      loading: false,
      filterText: '',
      list: [],
      defaultProps: {
        children: 'caseList',
        label: 'caseGroupName',
        id: "id"
      },
      editData: {
        id: "",
        name: "",
        isGroup: false
      },
      protocol: PROTOCOL
    })

    //method

    const renderProto = (pro) => {
      if (PROTOCOL[pro]) {
        return PROTOCOL[pro].toLocaleUpperCase()
      }
    }
    const renderLabel = (node, item) => {
      if (node.isLeaf) {
        switch (`${item.apiProtocal}`) {
          case PROTOCOL_TYPE.HTTP:
          case PROTOCOL_TYPE.Gateway:
            return item.httpMethod.toLocaleUpperCase()
          case PROTOCOL_TYPE.Dubbo:
            return item.dubboEnv.toLocaleUpperCase()
          case PROTOCOL_TYPE.Grpc:
            break
          default:
            break
        }
      }
    }
    const handleShowFolder = (item) => {
      return !item.apiProtocal
    }
    const handleStop = (e) => {}
    const handleBlur = (item) => {
      item.isEdit = false
    }
    const handleChangeSubmit = (e, node, item) => {
      state.loading = true
      item.isEdit = false
      if (state.editData.isGroup) {
        updateCaseDirName({
          dirId: state.editData.id,
          dirName: state.editData.name
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            ElMessage.success(i18n.t("successfullyModified"))
            getList()
          } else {
            ElMessage.error(data.message)
          }
        }).catch(e => {}).finally(() => {
          state.loading = false
        })
      } else {
        updateCaseName({
          caseId: state.editData.id,
          caseName: state.editData.name
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            ElMessage.success(i18n.t("successfullyModified"))
            getList()
          } else {
            ElMessage.error(data.message)
          }
        }).catch(e => {}).finally(() => {
          state.loading = false
        })
      }
    }
    const handleChangeCaseNmae = (node, item) => {
      item.isEdit = true
      nextTick(() => {
        inputNameRef.value.focus()
      })
      state.editData = {
        id: item.apiProtocal ? item.id : item.caseGroupId,
        name: item.caseName || item.caseGroupName,
        isGroup: !item.apiProtocal
      }
    }
    const handleSelectNode = (item, node, _) => {
      if (node.isLeaf && item.apiProtocal) {
        // 避免部分相同 不更新问题
        store.dispatch('apitest/resetApiTest')
        store.dispatch('apitest/changeApiTestTarget', {
          apiTestProtocol: `${item.apiProtocal}`,
        })
        let param = {
          selectCaseId: item.id
        }
        nextTick(() => {
          store.dispatch('apitest/changeApiTestTarget', param)
          setTimeout(() => {
            switch (`${item.apiProtocal}`) {
              case PROTOCOL_TYPE.HTTP:
                handleEchoHttp(item)
                break
              case PROTOCOL_TYPE.Dubbo:
                handleEchoDubbo(item)
                break
              case PROTOCOL_TYPE.Gateway:
                handleEchoGateway(item)
                break
              case PROTOCOL_TYPE.Grpc:
                handleEchoGrpc(item)
                break
              default:
                break
            }
          }, 0)
        })
      }
    }
    const handleEchoGrpc = (val) => {
      try {
        val.grpcParamBody = JSON.parse(val.grpcParamBody || {})
      } catch (error) {}
		  store.dispatch('apitest/changeGrpcParam', {
        packageName: val.grpcPackageName,
        interfaceName: val.grpcInterfaceName,
        methodName: val.grpcMethodName,
        parameter: val.grpcParamBody,
        timeout: val.requestTimeout,
        addrs: val.grpcServerAddr
      })
      store.dispatch('apitest/changeApiTestTarget', {
        isRequestEnd: new Date().getTime()
      })
    }
    const handleEchoDubbo = (val) => {
      try {
        val.dubboAttachment = JSON.parse(val.dubboAttachment || {})
      } catch (error) {}
      try {
        val.dubboParamBody = JSON.parse(val.dubboParamBody || {})
      } catch (error) {}
      store.dispatch('apitest/changeApiTestTarget', {
        version: val.dubboVersion,
        interfaceName: val.dubboInterface,
        methodName: val.dubboMethodName,
        isGenParam: val.dubboIsGeneric,
        hasAttachment: val.dubboUseAttachment,
        attachments: val.dubboAttachment,
        response: {
          content: {}
        },
        dubboEnv: val.dubboEnv,
        paramType: val.dubboParamType,
        group: val.dubboGroup,
        addr: val.dubboAddr,
        parameter: val.dubboParamBody || {},
        retries: val.dubboRetryTime,
        isRequestEnd: new Date().getTime(),
        isClickCase: true
      })
    }
    const handleEchoGateway = (val) => {
      handleEchoHttp(val)
    }
    const handleEchoHttp = (val) => {
      try {
        val.httpHeaders = JSON.parse(val.httpHeaders || {})
      } catch (error) {}

      try {
        val.httpRequestBody = JSON.parse(val.httpRequestBody || {})
      } catch (error) {}

      let headerInfo = Object.keys(val.httpHeaders).map(key => {
        return {
          paramKey: key,
          paramValue: val.httpHeaders[key]
        }
      })
      let defaultHeader = [];
      ((store.getters.apiTestDefaultHeader || []) as any[]).forEach(v => {
        let has = false
        for (let i = 0; i < headerInfo.length; i++) {
          if (v.paramKey === headerInfo[i].paramKey) {
            has = true
            break
          }
        }
        if (!has) {
          defaultHeader.push(v)
        }
      })
      let query = []
      let jsonBody = {}
      let body = {}
      let apiURI = val.url
      if (val.httpMethod && val.httpMethod.toLocaleLowerCase() === 'get') {
        let q = ''
        let keys = Object.keys(val.httpRequestBody || {})
        keys.forEach((k, index) => {
          q += `${k}=${val.httpRequestBody[k]}`
          if (keys.length !== (index + 1)) {
            q += '&'
          }
        })
        apiURI += `?${q}`
      } else if (val.httpReqBodyType === API_REQUEST_PARAM_TYPE.RAW) {
        jsonBody = val.httpRequestBody || {}
      } else {
        body = Object.keys(val.httpRequestBody || {}).map(key => {
          return {
            paramKey: key,
            paramValue: val.httpRequestBody[key],
            paramType: getDataType(val.httpRequestBody[key])
          }
        })
      }
      let param = {
        headers: headerInfo,
        body,
        jsonBody,
        httpEnv: `${val.apiProtocal}` === PROTOCOL_TYPE.HTTP ? val.envId : val.httpDomian,
        query,
        response: {
          content: {}
        },
        defaultHeader: defaultHeader,
        requestType: val.httpMethod.toLocaleLowerCase(),
        apiTestBodyType: val.httpReqBodyType === API_REQUEST_PARAM_TYPE.RAW ? RADIO_TYPE.RAW : RADIO_TYPE.FORM,
        url: apiURI,
        isRequestEnd: new Date().getTime()
      }
      store.dispatch('apitest/changeApiTestTarget', param)
      store.dispatch('apitest/changeX5Param', {
        useX5Filter: val.useX5Filter,
        appID: val.x5AppId,
        appkey: val.x5AppKey,
        x5Method: val.x5Method
      })
    }
    const filterNode = (value, data) => {
      if (!value) return true
      return data.caseGroupName.indexOf(value) !== -1
    }
    const handleDelete = (item) => {
      state.loading = true
      if (item.accountId) {
        return deleteCaseById({
          caseId: item.id
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            ElMessage.success(i18n.t("successDeleted"))
            getList()
          } else {
            ElMessage.error(data.message)
          }
        }).catch(e => {}).finally(() => {
          state.loading = false
        })
      }
      return deleteCaseGroup({
        groupId: item.caseGroupId
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          ElMessage.success(i18n.t("successDeleted"))
          getList()
        } else {
          ElMessage.error(data.message)
        }
      }).catch(e => {}).finally(() => {
        state.loading = false
      })
    }
    const handleAddNewGroup = () => {
      let apiId = utils.getQuery("apiID")
      let param = {
        projectId: utils.getQuery("projectID"),
        globalCase: props.isGlobal
      }
      if (apiId) {
        param = Object.assign({},param,{apiId})
      }
      createCaseGroup(function (name) {
        param = Object.assign({},param,{name})
        saveTestCaseDir(param).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            getList()
          } else {
            ElMessage.error(data.message)
          }
        }).catch(e => {})
      })
    }
    const handleTestChildren = (arr = []) => {
      return arr.map(item => {
        return {
          id: item.caseGroupId,
          isEdit: false,
          ...item,
          caseList: (item.caseList || []).map(v => ({
            ...v,
            caseGroupName: v.caseName,
            isEdit: false,
            caseList: []
          }))
        }
      })
    }
    const getList = () => {
      if (treeRef.value && treeRef.value.filter) {
        state.filterText = undefined
      }
      state.loading = true
      if (props.isGlobal) {
        return getCasesByProject({
          projectId: utils.getQuery("projectID")
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            state.list = handleTestChildren(data.data)
          } else {
            ElMessage.error(data.message)
          }
        }).catch(e => {}).finally(() => {
          state.loading = false
        })
      } else {
        return getCasesByApi({
          apiId: utils.getQuery("apiID"),
          projectId: utils.getQuery("indexProjectID") || utils.getQuery("projectID")
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            state.list = handleTestChildren(data.data)
          } else {
            ElMessage.error(data.message)
          }
        }).catch(e => {}).finally(() => {
          state.loading = false
        })
      }
    }

    //watch
    watch(() => state.filterText, (val) => {
      treeRef.value.filter(val)
    })
    watch(()=>state.visible, (val) => {
      if (val) {
        getList()
      }
    },{
      immediate: true
    })

    return {
      treeRef,
      inputNameRef,
      renderProto,
      renderLabel,
      handleShowFolder,
      handleStop,
      handleBlur,
      handleChangeSubmit,
      handleChangeCaseNmae,
      handleSelectNode,
      filterNode,
      handleDelete,
      handleAddNewGroup,
      ...toRefs(state)
    }
  }
})
</script>

<style scoped>
.test-case-wrap {
	position: fixed;
	right: 0px;
	top: 40%;
}
.test-case-wrap .test-case-btn {
	display: inline-block;
	font-size: 14px;
	border: 1px solid #DCDFE6;
	border-right: 0;
	width: 26px;
	white-space: pre-wrap;
	word-break: break-all;
	background: #fff;
	padding: 3px 4px;
	cursor: pointer;
	border-radius: 4px 0 0 4px;
	text-align: center;
	user-select: none;
	color: #108EE9;
}
.test-case-drawer .search-wrap {
	display: flex;
	align-items: center;
	justify-content: flex-start;
	margin: 0 20px 10px;
}
.test-case-drawer .new-pak {
  margin: 0 20px;
}
.test-case-drawer .new-pak .el-button {
  padding: 4px 0;
}
.test-case-drawer .search-wrap .el-button{
	margin-left: 12px;
}
.test-case-drawer .exmp-list {
	margin: 0 20px;
  padding-bottom: 12px;
}
.test-case-drawer .exmp-list .custom-tree-node {
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding-right: 20px;
}
.test-case-drawer .exmp-list .custom-tree-node .protocol {
  color: #108EE9;
  font-size: 12px;
}
.test-case-drawer .exmp-list .custom-tree-node .custom-tree-node-label {
  font-size: 15px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.test-case-drawer .exmp-list .custom-tree-node .custom-tree-node-label .custom-tree-node-name {
  padding-left: 4px;
}
.test-case-drawer .exmp-list .custom-tree-node .custom-tree-node-label var {
  font-weight: 500;
  font-style: normal;
  /* background: #00A854; */
  /* color: #fff; */
  color: #00A854;
  border-radius: 3px;
  font-size: 12px;
  padding: 2px 4px;
  display: inline-block;
  height: 16px;
  line-height: 14px;
}
.test-case-drawer .test-case-container{
  height: 100%;
  padding-top: 16px;
}
.test-case-drawer .test-case-container .exmp-list {
  height: calc(100% - 82px);
  overflow-y: auto;
}
.test-case-drawer .test-case-container .exmp-list >>> .el-tree-node__content {
  height: 32px;
}
.test-case-drawer .test-case-container .exmp-list::-webkit-scrollbar{
  display: none;
}
</style>
