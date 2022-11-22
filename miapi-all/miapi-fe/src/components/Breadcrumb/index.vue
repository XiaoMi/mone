<template>
  <div class="a-api-breadcrumb-wrap">
    <div class="a-api-breadcrumb">
      <div class="breadcrumb-wrap">
        <!-- <el-breadcrumb v-if="!isAdd && !isImport && !isAddMock && !isApiTest && !isIndexList && !isAddIndexDoc" separator="-">
          <el-breadcrumb-item v-for="(v, index) in breadcrumb" :key="index"><span class="title">{{v}}</span></el-breadcrumb-item>
        </el-breadcrumb> -->
        <el-breadcrumb v-if="isAddMock || isAdd" separator="-">
          <el-breadcrumb-item ><span class="title"><el-icon v-if="isAddMock" @click="handleBacnMockList" class="add-mock-back"><Back /></el-icon>{{isAddMock? (!utils.getQuery("mockExpectID") ? $i18n.t('breadcrumb.newMockExpectations') : $i18n.t('breadcrumb.editMockExpectations')) : $i18n.t('breadcrumb.newApi')}}</span></el-breadcrumb-item>
        </el-breadcrumb>
        <el-breadcrumb v-else-if="isImport" separator="-">
          <el-breadcrumb-item ><span class="title">{{$i18n.t('breadcrumb.importApi')}}</span></el-breadcrumb-item>
        </el-breadcrumb>
        <el-breadcrumb v-else-if="isApiTest" separator="-">
          <el-breadcrumb-item ><span class="title">{{$i18n.t('breadcrumb.apiTesting')}}</span></el-breadcrumb-item>
        </el-breadcrumb>
        <el-breadcrumb v-else-if="isAddIndexDoc" separator="-">
          <el-breadcrumb-item ><span class="title">{{$i18n.t('breadcrumb.newCollectionDocument')}}</span></el-breadcrumb-item>
        </el-breadcrumb>
        <el-breadcrumb v-else-if="isIndexList" separator="-">
          <el-breadcrumb-item ><span class="title">{{getIndexGroupName}}</span></el-breadcrumb-item>
        </el-breadcrumb>
        <!-- <el-breadcrumb v-else separator="-">
          <el-breadcrumb-item ><span class="title">{{isAddMock? '新建Mock API期望' : '新建API'}}</span></el-breadcrumb-item>
        </el-breadcrumb> -->
        <el-breadcrumb v-else separator="-">
          <el-breadcrumb-item v-for="(v, index) in breadcrumbTabs" :key="index">
            <template v-if="v.isCopy">
              <el-tooltip effect="dark" :content="$i18n.t('copyText')" placement="top">
                <span class="title" style="cursor: pointer;" @click="handleCustomCopy(v.name)">{{v.name}}</span>
              </el-tooltip>
            </template>
            <span v-else class="title">{{v.name}}</span>
          </el-breadcrumb-item>
        </el-breadcrumb>
        <div v-if="isDetail" class="tag-wrap">
          <span v-for="(v,index) in tags" :key="index">{{v}}</span>
        </div>
      </div>
      <div :class="{'api-breadcrumb-btns': true, 'is-import': isImport}">
        <el-button :disabled="isGrpcDetail" v-if="!isEditDetail && isDetail && apiDetailActiveTab === apiDetailTab.DETAIL" @click="handleSync" type="primary">{{$i18n.t("btnText.manualSync")}}</el-button>
        <el-button v-if="isDetail && apiDetailActiveTab === apiDetailTab.DETAIL" @click="handleSubmit" type="primary">{{isEditDetail ? $i18n.t('btnText.submitApi') : $i18n.t('btnText.editApi')}}</el-button>
        <el-button v-if="isEditDetail && isDetail && apiDetailActiveTab === apiDetailTab.DETAIL" @click="handleCancelEdit">{{$i18n.t('btnText.cancelEdit')}}</el-button>
        <el-button v-if="isApiList || isIndexList" :disabled="groupType === group_type.INDEX && !indexGroupList.length" @click="handleAddApi" type="primary">
          <el-icon v-if="groupType === group_type.API"><Plus /></el-icon>
          <el-icon v-else><Upload /></el-icon>
          {{groupType === group_type.API ? $i18n.t('btnText.newApi') : $i18n.t('importAPI')}}
        </el-button>
        <el-button v-if="isApiList" @click="handleToPdf">
          <el-icon><Upload /></el-icon>
          {{$i18n.t('importPdf')}}
        </el-button>
        <el-button v-if="isIndexList" :disabled="!indexGroupList.length" @click="handleShare">{{$i18n.t('btnText.shareCollection')}}</el-button>
        <el-button v-if="isImport" :disabled="!selectApiIds.length" @click="handleSubmitImport" type="primary">{{$i18n.t('btnText.submit')}}</el-button>
        <el-button v-if="isImport" @click="handleBack">{{$i18n.t('btnText.cancel')}}</el-button>
      </div>
    </div>
    <!-- <p v-show="isDetail" class="api-desc">{{curApiDesc || '暂无描述'}}</p> -->
    <p v-if="isApiList || isIndexList" class="api-desc">{{groupDesc || $i18n.t('noDescription')}}</p>
    <ImportPdf :visible="pdfVisible" @onCancel="pdfVisible = false"/>
  </div>
</template>
<script lang="ts">
import { PATH } from '@/router/constant'
import { updateGrpcApi, editHttpApi, updateGatewayApi, updateDubboApi, manualUpdateDubboApi, manualUpdateHttpApi, manualUpdateGatewayApi } from '@/api/apilist'
import { addApiToIndex } from '@/api/apiindex'
import { AJAX_SUCCESS_MESSAGE, PROTOCOL, REQUEST_TYPE, PROTOCOL_TYPE, DATA_TYPE_KEY, API_REQUEST_PARAM_TYPE } from '@/views/constant'
import { useStore } from 'vuex'
import { defineComponent, reactive, toRefs, computed, watch, ref } from "vue"
import { useRoute, useRouter } from "vue-router"
import { GROUP_TYPE, API_DETAIL_TAB } from '@/views/ApiList/constant'
import { handleFilter, handleCheckValue } from '@/store/utils'
import customCopy from "@/common/customCopy"
import handleCommit from '@/common/commitInfo'
import i18n from "@/lang"
import * as utils from "@/utils"
import { ElMessage } from 'element-plus'
import ImportPdf from "./ImportPdf.vue"

export default defineComponent({
  components: {
    ImportPdf
  },
  setup(props, ctx){
    const store = useStore()
    const route = useRoute()
    const router = useRouter()
    const state = reactive({
      pdfVisible: false,
      isDetail: false,
      isApiList: false,
      isAddMock: false,
      breadcrumbTabs: [],
      projectName: '',
      curPath: '',
      tags: [],
      isAdd: false,
      curApiDesc: '',
      isImport: false,
      isApiTest: false,
      isIndexList: false,
      isAddIndexDoc: false,
      isGrpcDetail: false,
      httpParam: computed(() => store.getters.httpParam),
      projectDetail: computed(() => store.getters.projectDetail),
      gatewayParam: computed(() => store.getters.gatewayParam),
      groupID: computed(() => store.getters.groupID),
      apiRawIsError: computed(() => store.getters.apiRawIsError),
      indexGroupID: computed(() => store.getters.indexGroupID),
      groupDesc: computed(() => store.getters.groupDesc),
      selectApiIds: computed(() => store.getters.selectApiIds),
      isEditDetail: computed(() => store.getters.isEditDetail),
      groupType: computed(() => store.getters.groupType),
      apiList: computed(() => store.getters.apiList),
      groupList: computed(() => store.getters.groupList),
      indexGroupList: computed(() => store.getters.indexGroupList),
      apiDetailActiveTab: computed(() => store.getters.apiDetailActiveTab),
      editValid: computed(() => store.getters.editValid),
      dubboParam: computed(() => store.getters.dubboParam),
      apiDetail: computed(() => store.getters.apiDetail),
      updateGrpcParam: computed(() => store.getters.updateGrpcParam),
      requestType: REQUEST_TYPE,
      group_type: GROUP_TYPE,
      protocol: PROTOCOL,
      apiDetailTab: API_DETAIL_TAB,
      
      getIndexGroupName: computed(() => {
        let arr = state.indexGroupList.filter(v => v.groupID === state.indexGroupID)
        if (arr.length) {
          return arr[0].groupName
        }
        return i18n.t('noName')
      })
    })



    // method
    const handleCustomCopy = (val) => {
      customCopy(val)
    }
    const handleUpdateDesc = (type) => {
      if (type === GROUP_TYPE.API) {
        if (state.groupList.length) {
          store.dispatch('apilist/changeGroupDesc', state.groupList[0].groupDesc)
          store.dispatch('apilist/changeGroupId', state.groupList[0].groupID)
        } else {
          store.dispatch('apilist/changeGroupDesc', '')
        }
      } else {
        if (state.indexGroupList.length) {
          store.dispatch('apilist/changeGroupDesc', state.indexGroupList[0].groupDesc)
          store.dispatch('apiindex/changeGroupIndexId', state.indexGroupList[0].groupID)
        } else {
          store.dispatch('apilist/changeGroupDesc', '')
        }
      }
    }
    const handleAddApi = () => {
      if (state.groupType === GROUP_TYPE.API) {
        store.dispatch('apilist.add/resetHttpParam')
        store.dispatch('apilist.add/resetDubboParam')
        store.dispatch('apilist.add/resetGatewayParam')
        store.dispatch('apilist.add/changeStep', 1)
        store.dispatch('apilist.add/changeDubboNewAdd', false)
        store.dispatch('apilist.add/changeHttpNewAdd', false)
        router.push({ path: PATH.ADD_API, query: { projectID: utils.getQuery('projectID') } })
      } else {
        router.push({ path: PATH.IMPORT_INDEX, query: { projectID: utils.getQuery('projectID'), indexId: state.indexGroupID } })
      }
    }
    const handleArrValue = (arr) => {
      return arr.map(v => {
        if (v.paramType === DATA_TYPE_KEY.array) {
          return handleArrValue(v.paramValue)
        } else if (v.paramType === DATA_TYPE_KEY.object || v.paramType === DATA_TYPE_KEY.json) {
          return handleObjValue(v.paramValue)
        } else if (v.paramType === DATA_TYPE_KEY.boolean) {
          return v.paramValue === 'true'
        }
        return v.paramValue
      })
    }
    const handleObjValue = (arr) => {
      let obj = {}
      arr.forEach(v => {
        if (v.paramType === DATA_TYPE_KEY.array) {
          obj[v.paramKey] = handleArrValue(v.paramValue)
        } else if (v.paramType === DATA_TYPE_KEY.object || v.paramType === DATA_TYPE_KEY.json) {
          obj[v.paramKey] = handleObjValue(v.paramValue)
        } else if (v.paramType === DATA_TYPE_KEY.boolean) {
          obj[v.paramKey] = v.paramValue === 'true'
        } else {
          obj[v.paramKey] = v.paramValue
        }
      })
      return obj
    }
    const handleSubmit = () => {
      let bool = true
      if (state.editValid.validate) {
        state.editValid.validate((valid) => {
          bool = valid
        })
      }
      if (state.apiRawIsError) {
        ElMessage.error(i18n.t('errorMessage.jsonFormatWrong'))
        return
      }
      if (!bool) {
        return
      }
      switch (utils.getQuery('apiProtocol')) {
        case PROTOCOL_TYPE.HTTP:
          if (!state.isEditDetail) {
            let obj = { ...state.httpParam, ...state.apiDetail.baseInfo }
            store.dispatch('apilist.add/changeHttpParam', {
              ...obj,
              apiResponseParamType: obj.apiResponseParamType || API_REQUEST_PARAM_TYPE.JSON,
              apiHeader: state.apiDetail.headerInfo || [],
              apiRequestParam: state.apiDetail.requestInfo || [],
              apiResultParam: state.apiDetail.resultInfo || [],
              apiErrorCodes: state.apiDetail.apiErrorCodes || []
            })
            store.dispatch('apilist/changeEditDetail', true)
          } else {
            handleHttpSave()
          }
          break
        case PROTOCOL_TYPE.Dubbo:
          if (!state.isEditDetail) {
            let methodparaminfo = state.apiDetail.dubboApiBaseInfo.methodparaminfo
            try {
              methodparaminfo = JSON.parse(methodparaminfo)
              if (methodparaminfo.length > 1 || (methodparaminfo.length && methodparaminfo[0].itemName !== 'root')) {
                let newObj = {
                  allowableValues: [],
                  defaultValue: "",
                  desc: "",
                  exampleValue: "",
                  itemClassStr: "",
                  itemName: "root",
                  itemTypeStr: "",
                  itemValue: methodparaminfo,
                  required: false
                }
                methodparaminfo = [newObj]
              }
            } catch (error) {}
            let response = state.apiDetail.dubboApiBaseInfo.response || {}
            try {
              response = JSON.parse(response)
            } catch (error) {}
            let errorcodes = state.apiDetail.dubboApiBaseInfo.errorcodes || []
            try {
              errorcodes = JSON.parse(errorcodes)
            } catch (error) {}

            store.dispatch('apilist.add/changeDubboParam', {
              apiDocName: state.apiDetail.dubboApiBaseInfo.apidocname,
              apiGroup: state.apiDetail.dubboApiBaseInfo.apigroup,
              apiRemark: state.apiDetail.apiRemark,
              apiDesc: state.apiDetail.apiDesc,
              name: state.apiDetail.name,
              apiModelClass: state.apiDetail.dubboApiBaseInfo.apimodelclass,
              apiName: state.apiDetail.dubboApiBaseInfo.apiname,
              apiRespDec: state.apiDetail.dubboApiBaseInfo.apirespdec,
              apiVersion: state.apiDetail.dubboApiBaseInfo.apiversion,
              async: state.apiDetail.dubboApiBaseInfo.async,
              description: state.apiDetail.dubboApiBaseInfo.description,
              apiNoteType: state.apiDetail.apiNoteType,
              apiNoteRaw: state.apiDetail.apiNoteRaw,
              apiNote: state.apiDetail.apiNote,
              projectId: state.apiDetail.projectID,
              groupId: state.apiDetail.groupID,
              apiStatus: state.apiDetail.apiStatus,
              paramsLayerList: methodparaminfo,
              responseLayer: [response],
              apiErrorCodes: errorcodes,
              mavenAddr: state.apiDetail.mavenAddr
            })
            store.dispatch('apilist/changeEditDetail', true)
          } else {
            handleDubboSave()
          }
          break
        case PROTOCOL_TYPE.Gateway:
          if (!state.isEditDetail) {
            let obj = { ...state.gatewayParam, ...state.apiDetail.gatewayApiBaseInfo }
            let apiRequestRaw = state.apiDetail.apiRequestRaw || ''
            let apiRequestParam = state.apiDetail.requestInfo || []
            let apiErrorCodes = ''
            if (state.apiDetail.requestInfo && !Array.isArray(state.apiDetail.requestInfo)) {
              apiRequestParam = []
              apiRequestRaw = state.apiDetail.requestInfo
              try {
                apiRequestRaw = JSON.parse(apiRequestRaw)
              } catch (error) {}
            }
            if (state.apiDetail.apiErrorCodes) {
              apiErrorCodes = state.apiDetail.apiErrorCodes
              try {
                apiErrorCodes = JSON.parse(apiErrorCodes as string)
              } catch (error) {}
            }
            store.dispatch('apilist.add/changeGatewayParam', {
              ...obj,
              apiDesc: state.apiDetail.apiDesc,
              apiRemark: state.apiDetail.apiRemark,
              apiRequestParamType: state.apiDetail.apiRequestParamType,
              apiRequestRaw,
              apiResponseParamType: state.apiDetail.apiResponseParamType || API_REQUEST_PARAM_TYPE.JSON,
              apiResponseRaw: state.apiDetail.apiResponseRaw,
              apiStatus: state.apiDetail.apiStatus,
              groupId: state.apiDetail.groupID,
              groupName: state.apiDetail.groupName,
              projectId: state.apiDetail.projectID,
              apiHeader: state.apiDetail.headerInfo || [],
              apiRequestParam: apiRequestParam,
              apiResultParam: state.apiDetail.resultInfo || [],
              apiErrorCodes: apiErrorCodes
            })
            store.dispatch('apilist/changeEditDetail', true)
          } else {
            handleGatewaySave()
          }
          break
        case PROTOCOL_TYPE.Grpc:
          if (!state.isEditDetail) {
            let apiErrorCodes = state.apiDetail.errorCodes || ""
            try {
              apiErrorCodes = JSON.parse(state.apiDetail.errorCodes)
            } catch (error) {}
            store.dispatch('apilist.add/changeGrpcUpdateParam', {
              apiPath: state.apiDetail.fullApiPath,
              apiDesc: state.apiDetail.apiDesc,
              appName: state.apiDetail.appName,
              apiRemark: state.apiDetail.apiRemark,
              requestParam: state.apiDetail.requestInfo || [],
              responseParam: state.apiDetail.resultInfo || [],
              apiErrorCodes
            })
            store.dispatch('apilist/changeEditDetail', true)
          } else {
            handleGrpcSave()
          }
          break
        default:
          break
      }
    }
    const handleDubboSave = () => {
      let paramsLayerList = handleFilter(state.dubboParam.paramsLayerList, 'dubbo')
      let responseLayer = handleFilter(state.dubboParam.responseLayer, 'dubbo')
      if (handleCheckValue({ apiResultParam: responseLayer, apiRequestParam: paramsLayerList }, utils.getQuery('apiProtocol'))) {
        return
      }
      let projectId = utils.getQuery('projectID')
      if (utils.getQuery('indexProjectID')) {
        projectId = utils.getQuery('indexProjectID')
      }
      handleCommit((updateMsg) => {
        let obj = { ...state.dubboParam }
        delete obj.apiEnv
        updateDubboApi({
          ...obj,
          paramsLayerList,
          responseLayer: responseLayer[0] || {},
          apiID: utils.getQuery('apiID'),
          projectId: Number(projectId),
          apiErrorCodes: JSON.stringify(state.dubboParam.apiErrorCodes),
          updateMsg
        }).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            ElMessage.success(i18n.t('editSuccessfully'))
            store.dispatch('apilist.add/resetDubboParam')
            handleBack()
          }
        }).catch(e => {})
      })
    }
    const handleGatewaySave = () => {
      let apiHeader = state.gatewayParam.apiHeader.filter(item => !!item.headerName || !!item.headerValue)
      let apiResultParam = handleFilter(state.gatewayParam.apiResultParam)
      let apiRequestParam = handleFilter(state.gatewayParam.apiRequestParam)
      if (handleCheckValue({ apiHeader, apiResultParam, apiRequestParam }, utils.getQuery('apiProtocol'))) {
        return
      }
      let projectId = utils.getQuery('projectID')
      if (utils.getQuery('indexProjectID')) {
        projectId = utils.getQuery('indexProjectID')
      }

      let param = {
        ...state.gatewayParam,
        apiRequestRaw: typeof state.gatewayParam.apiRequestRaw === 'string' ? state.gatewayParam.apiRequestRaw : JSON.stringify(state.gatewayParam.apiRequestRaw),
        apiHeader: JSON.stringify(apiHeader),
        apiResultParam: JSON.stringify(apiResultParam),
        apiRequestParam: JSON.stringify(apiRequestParam),
        mockData: JSON.stringify(state.gatewayParam.mockData),
        status: state.gatewayParam.status ? 1 : 0,
        projectId: Number(projectId),
        apiNoteType: 0,
        allowMock: true,
        apiErrorCodes: JSON.stringify(state.gatewayParam.apiErrorCodes)
      }
      if (param.routeType === 0) { // 非dubbo
        param.methodName = ''
        param.serviceName = ''
        param.serviceGroup = ''
        param.serviceVersion = ''
      } else {
        param.path = ''
      }
      delete param.apiUpdateTime
      delete param.mockData
      delete param.mockDataDesc
      param.apiID = utils.getQuery('apiID')
      handleCommit((updateMsg) => {
        param.updateMsg = updateMsg
        updateGatewayApi(param).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            ElMessage.success(i18n.t('editSuccessfully'))
            store.dispatch('apilist.add/resetGatewayParam')
            handleBack()
          }
        }).catch(e => {})
      })
    }
    const handleHttpSave = () => {
      let projectID = utils.getQuery('projectID')
      if (utils.getQuery('indexProjectID')) {
        projectID = Number(utils.getQuery('indexProjectID'))
      }
      let obj = { ...state.httpParam }
      delete obj.apiEnv
      let param = {
        ...obj,
        apiRequestRaw: typeof state.httpParam.apiRequestRaw === 'string' ? state.httpParam.apiRequestRaw : JSON.stringify(state.httpParam.apiRequestRaw),
        apiHeader: state.httpParam.apiHeader.filter(item => !!item.headerName || !!item.headerValue),
        apiRequestParam: handleFilter(state.httpParam.apiRequestParam),
        apiResultParam: handleFilter(state.httpParam.apiResultParam),
        apiErrorCodes: JSON.stringify(state.httpParam.apiErrorCodes),
        projectID
      }
      if (handleCheckValue(param, utils.getQuery('apiProtocol'))) {
        return
      }
      param = {
        ...param,
        apiHeader: JSON.stringify(param.apiHeader),
        apiRequestParam: JSON.stringify(param.apiRequestParam),
        apiResultParam: JSON.stringify(param.apiResultParam)
      }
      param.apiID = utils.getQuery('apiID')
      delete param.apiUpdateTime
      handleCommit((msg) => {
        param.updateMsg = msg
        editHttpApi(param).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            ElMessage.success(i18n.t('editSuccessfully'))
            store.dispatch('apilist.add/resetHttpParam')
            handleBack()
          }
        }).catch(e => {})
      })
    }
    const handleGrpcSave = () => {
      let projectID = utils.getQuery('projectID')
      if (utils.getQuery('indexProjectID')) {
        projectID = Number(utils.getQuery('indexProjectID'))
      }
      let param = {
        ...state.updateGrpcParam,
        requestParam: handleFilter(state.updateGrpcParam.requestParam),
        responseParam: handleFilter(state.updateGrpcParam.responseParam),
        apiErrorCodes: JSON.stringify(state.updateGrpcParam.apiErrorCodes),
        projectID
      }
      if (handleCheckValue({
        apiRequestParam: param.requestParam,
        apiResultParam: param.responseParam
      }, PROTOCOL_TYPE.Grpc)) {
        return
      }
      let requestParam = {}
      if (param.requestParam.length && Object.keys(param.requestParam[0]).length) {
        Object.keys(param.requestParam[0]).forEach(k => {
          requestParam[k] = param.requestParam[0][k]
        })
      }
      let responseParam = {}
      if (param.responseParam.length && Object.keys(param.responseParam[0]).length) {
        Object.keys(param.responseParam[0]).forEach(k => {
          responseParam[k] = param.responseParam[0][k]
        })
      }
      param = {
        ...param,
        requestParam,
        responseParam
      }
      handleCommit((msg) => {
        param.updateMsg = msg
        updateGrpcApi(param).then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            ElMessage.success(i18n.t('editSuccessfully'))
            store.dispatch('apilist.add/resetGrpcParam')
            handleBack()
          }
        }).catch(e => {})
      })
    }
    const handleBack = () => {
      if (state.groupType === GROUP_TYPE.API) {
        router.push({ path: PATH.API, query: { projectID: utils.getQuery('projectID') } })
      } else {
        router.push({ path: PATH.API_INDEX, query: { projectID: utils.getQuery('projectID') } })
      }
    }
    const handleCancelEdit = () => {
      store.dispatch('apilist/changeEditDetail', false)
    }
    const handleSubmitImport = () => {
      addApiToIndex({
        projectID: utils.getQuery('projectID'),
        indexID: utils.getQuery('indexId'),
        apiIDs: JSON.stringify(state.selectApiIds)
      }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          ElMessage.success(i18n.t('apiAddedSuccessfully'))
          store.dispatch('apilist.group/getAllIndexGroupViewList', utils.getQuery('projectID'))
          handleBack()
        }
      }).catch(e => {})
    }
    const handleShare = () => {
      store.dispatch('apiindex/changeShareDialogBool', true)
    }
    const handleBacnMockList = () => {
      if (utils.getQuery("mockExpectID")) {
        let obj = { ...route.query }
        delete obj.mockExpectID
        router.push({ path: PATH.API_DETAIL, query: { ...obj, tab: 'mock' } })
      } else {
        router.push({ path: PATH.API_DETAIL, query: { ...route.query, tab: 'mock' } })
      }
    }
    const handleSync = () => {
      handleCommit((updateMsg) => {
        switch (utils.getQuery('apiProtocol')) {
          case PROTOCOL_TYPE.HTTP:
            manualUpdateHttpApi({
              apiID: utils.getQuery('apiID'),
              projectID: utils.getQuery('projectID'),
              updateMsg
            }).then((data) => {
              if (data.message === AJAX_SUCCESS_MESSAGE) {
                ElMessage.success(i18n.t("errorMessage.manualUpdateSucceeded"))
                router.replace({ path: route.path, query: { ...route.query, t: new Date().getTime() } })
              } else {
                ElMessage.error(data.message)
              }
            }).catch(e => {})
            break
          case PROTOCOL_TYPE.Dubbo:
            manualUpdateDubboApi({
              serviceName: state.apiDetail.dubboApiBaseInfo.apimodelclass,
              methodName: state.apiDetail.dubboApiBaseInfo.apiname,
              group: state.apiDetail.dubboApiBaseInfo.apigroup,
              version: state.apiDetail.dubboApiBaseInfo.apiversion,
              env: state.apiDetail.apiEnv,
              updateMsg
            }).then((data) => {
              if (data.message === AJAX_SUCCESS_MESSAGE) {
                ElMessage.success(i18n.t("errorMessage.manualUpdateSucceeded"))
                router.replace({ path: route.path, query: { ...route.query, t: new Date().getTime() } })
              } else {
                ElMessage.error(data.message)
              }
            }).catch(e => {})
            break
          case PROTOCOL_TYPE.Gateway:
            manualUpdateGatewayApi({
              apiID: utils.getQuery('apiID'),
              projectID: utils.getQuery('projectID'),
              env: state.apiDetail.apiEnv,
              updateMsg
            }).then((data) => {
              if (data.message === AJAX_SUCCESS_MESSAGE) {
                ElMessage.success(i18n.t("errorMessage.manualUpdateSucceeded"))
                router.replace({ path: route.path, query: { ...route.query, t: new Date().getTime() } })
              } else {
                ElMessage.error(data.message)
              }
            }).catch(e => {})
            break
          default:
            break
        }
      })
    }

    const handleToPdf = ():void => {
      state.pdfVisible = true
    }
    // watch
    watch(()=> route.path, (val) => {
      state.isDetail = val === PATH.API_DETAIL
      state.isAdd = val === PATH.ADD_API
      state.isApiList = val === PATH.API
      state.isAddMock = val === PATH.ADD_MOCK
      state.curPath = val
      state.isImport = val === PATH.IMPORT_INDEX
      state.isApiTest = val === PATH.API_TEST
      state.isIndexList = val === PATH.API_INDEX
      state.isAddIndexDoc = val === PATH.ADD_INDEX_DOC
      state.breadcrumbTabs = [{ name: state.projectName, isCopy: false }, {}]
      state.curApiDesc = ''
    }, {
      immediate: true
    })

    watch(()=> state.groupType, (val, old) => {
      if (val !== old) {
        handleUpdateDesc(val)
      }
    }, {
      immediate: true
    })

    watch(()=> state.projectDetail, (val) => {
      if (val.projectName) {
        state.projectName = val.projectName
        state.breadcrumbTabs.splice(0, 1, { name: val.projectName, isCopy: false })
      } else {
        state.projectName = ""
        state.breadcrumbTabs.splice(0, 1, { name: "", isCopy: false })
      }
    }, {
      immediate: true,
      deep: true
    })

    watch(()=> state.apiList, (val) => {
      if (val && val.length) {
        if (state.breadcrumbTabs.length > 1) {
          state.breadcrumbTabs.splice(1, 1, { name: val[0].groupName, isCopy: false })
        } else {
          state.breadcrumbTabs.push({ name: val[0].groupName, isCopy: false })
        }
      } else {
        let arr = state.groupList.filter(v => v.groupID === Number(state.groupID))
        if (state.groupType === GROUP_TYPE.INDEX) {
          arr = state.indexGroupList.filter(v => v.groupID === Number(state.indexGroupID))
        }
        if (state.breadcrumbTabs.length > 1 && arr.length) {
          state.breadcrumbTabs.splice(1, 1, { name: arr[0].groupName, isCopy: false })
        } else if (arr.length) {
          state.breadcrumbTabs.push({ name: arr[0].groupName, isCopy: false })
        } else {
          state.breadcrumbTabs.pop()
        }
      }
    }, {
      immediate: true,
      deep: true
    })

    watch(()=> state.apiDetail, (val) => {
      let isGrpcDetail = false
      if (val.baseInfo && val.baseInfo.apiID && state.curPath === PATH.API_DETAIL) {
        state.curApiDesc = val.baseInfo.apiDesc
        state.breadcrumbTabs.splice(1, 1, { name: val.baseInfo.groupName, isCopy: false })
        if (state.breadcrumbTabs.length === 3) {
          state.breadcrumbTabs.splice(2, 1, { name: val.baseInfo.apiURI, isCopy: true })
        } else {
          state.breadcrumbTabs.push({ name: val.baseInfo.apiURI, isCopy: true })
        }
        state.tags = [state.protocol[val.baseInfo.apiProtocol], state.requestType[val.baseInfo.apiRequestType], val.baseInfo.apiEnv]
      } else if (val.dubboApiBaseInfo && val.dubboApiBaseInfo.id && state.curPath === PATH.API_DETAIL) {
        state.curApiDesc = val.apiDesc
        state.breadcrumbTabs.splice(1, 1, { name: val.groupName, isCopy: false })
        if (state.breadcrumbTabs.length === 3) {
          state.breadcrumbTabs.splice(2, 1, { name: val.dubboApiBaseInfo.apiname, isCopy: true })
        } else {
          state.breadcrumbTabs.push({ name: val.dubboApiBaseInfo.apiname, isCopy: true })
        }
        state.tags = [state.protocol[utils.getQuery('apiProtocol')], val.apiEnv]
      } else if (val.gatewayApiBaseInfo && val.gatewayApiBaseInfo.id && state.curPath === PATH.API_DETAIL) {
        state.curApiDesc = val.apiDesc
        state.breadcrumbTabs.splice(1, 1, { name: val.groupName, isCopy: false })
        if (state.breadcrumbTabs.length === 3) {
          state.breadcrumbTabs.splice(2, 1, { name: val.gatewayApiBaseInfo.url, isCopy: true })
        } else {
          state.breadcrumbTabs.push({ name: val.gatewayApiBaseInfo.url, isCopy: true })
        }
        state.tags = [state.protocol[utils.getQuery('apiProtocol')], (val.gatewayApiBaseInfo.httpMethod || '').toUpperCase(), val.apiEnv]
      } else if (val.fullApiPath) {
        isGrpcDetail = true
        state.curApiDesc = val.apiDesc
        state.breadcrumbTabs.splice(1, 1, { name: val.groupName, isCopy: false })
        if (state.breadcrumbTabs.length === 3) {
          state.breadcrumbTabs.splice(2, 1, { name: val.methodName, isCopy: true })
        } else {
          state.breadcrumbTabs.push({ name: val.methodName, isCopy: true })
        }
        state.tags = [state.protocol[utils.getQuery('apiProtocol')], val.apiEnv]
      }
      state.isGrpcDetail = isGrpcDetail
    }, {
      deep: true
    })

    return {
      utils,
      handleSync,
      handleBacnMockList,
      handleShare,
      handleSubmitImport,
      handleCancelEdit,
      handleSubmit,
      handleAddApi,
      handleCustomCopy,
      handleBack,
      handleToPdf,
      ...toRefs(state)
    }
  }
})
</script>
<style scoped>
.a-api-breadcrumb-wrap{
  display: flex;
  width: 100%;
  white-space: nowrap;
  flex-flow: column;
}
.a-api-breadcrumb-wrap p {
  margin: 0;
  padding: 7px 0 0;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.64);
  line-height: 22px;
}
.a-api-breadcrumb {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 32px;
  position: relative;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}
.a-api-breadcrumb .api-breadcrumb-btns {
  white-space: nowrap;
}
.a-api-breadcrumb .api-breadcrumb-btns:deep() .el-button>span {
  font-size: 12px;
}
.a-api-breadcrumb .api-breadcrumb-btns:deep() .el-button>span i {
  margin-right: 2px;
}
.a-api-breadcrumb .api-breadcrumb-btns.is-import {
  bottom: 0;
}
.a-api-breadcrumb .breadcrumb-wrap{
  display: flex;
  align-items: center;
  justify-content: flex-start;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.a-api-breadcrumb .breadcrumb-wrap >>> .el-breadcrumb{
  display: flex;
  align-items: center;
  justify-content: flex-start;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}
.a-api-breadcrumb .breadcrumb-wrap >>> .el-breadcrumb .el-breadcrumb__item:last-child{
  overflow: hidden;
  text-overflow: ellipsis;
  display: inline-block;
}
.a-api-breadcrumb .breadcrumb-wrap >>> .el-breadcrumb .el-breadcrumb__item:last-child span{
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
  display: inline-block;
  line-height: 18px;
  vertical-align: middle;
}
.a-api-breadcrumb .breadcrumb-wrap .add-mock-back {
  padding-right: 8px;
  cursor: pointer;
  color: #1890FF;
  font-weight: bold;
  font-size: 18px;
  display: inline-block;
  vertical-align: bottom;
  margin-right: 4px;
}
.a-api-breadcrumb .breadcrumb-wrap .tag-wrap{
  display: flex;
  padding-right: 4px;
}
.a-api-breadcrumb .breadcrumb-wrap .tag-wrap span{
  margin-left: 8px;
  font-size: 12px;
  border-radius: 3px;
  display: inline-block;
  height: 20px;
  line-height: 18px;
  text-align: center;
  padding: 0 10px;
}
.a-api-breadcrumb .breadcrumb-wrap .tag-wrap span:first-child{
  background: #1890FF;
  color: #fff;
  line-height: 20px;
}
.a-api-breadcrumb .breadcrumb-wrap .tag-wrap span:nth-child(2){
  background: #eef8ff;
  color: #1890FF;
  border: 1px solid #3c9cf7;
}
.a-api-breadcrumb .breadcrumb-wrap .tag-wrap span:last-child{
  background: #00A854;
  color: #fff;
  border: 1px solid #00A854;
}
.a-api-breadcrumb >>> .el-breadcrumb__inner .title,.a-api-breadcrumb >>> .el-breadcrumb__separator {
  font-size: 16px;
  font-weight: bold;
	color: #000;
}
</style>
