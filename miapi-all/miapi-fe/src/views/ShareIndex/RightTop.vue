<template>
	<div class="right-top-container">
		<div class="left-wrap">
      <div class="title-left">
        <span class="url">{{getObj.url}}&nbsp;&nbsp;{{getObj.apiName}}&nbsp;</span>
        <span v-if="getObj.type" class="method url-method">{{getObj.type}}</span>
        <span v-if="getObj.method" class="method request-method">{{getObj.method}}</span>
      </div>
      <p class="url-desc">{{getObj.desc}}</p>
    </div>
    <div>
      <el-button v-show="!getObj.isGrpc && !isExtranet" @click="handleToMock" plain size="medium">Mock API</el-button>
      <el-button v-if="!isExtranet" @click="handleToTest" plain size="medium">{{$i18n.t('btnText.test')}} API</el-button>
    </div>
	</div>
</template>

<script>
import { PROTOCOL, PROTOCOL_TYPE, REQUEST_TYPE } from '@/views/constant'
import { mapGetters } from 'vuex'
import { PATH } from '@/router/constant'
import { API_DETAIL_TAB } from '@/views/ApiList/constant'
import { isExtranet } from "@/utils"
export default {
  name: 'RightTop',
  computed: {
    ...mapGetters([
      'shareApiDetail'
    ]),
    isExtranet () {
      return isExtranet
    },
    getObj () {
      let obj = {
        url: this.$i18n.t('noAddress'),
        apiName: '',
        desc: this.$i18n.t('noDescription'),
        type: '',
        method: '',
        isGrpc: false
      }
      let item = this.shareApiDetail
      if (item.protocol) {
        obj.type = PROTOCOL[item.protocol]
        switch (`${item.protocol}`) {
          case PROTOCOL_TYPE.HTTP:
            obj.url = item.apiInfo.baseInfo.apiURI
            obj.apiName = `(${item.apiInfo.baseInfo.apiName})`
            obj.desc = item.apiInfo.baseInfo.apiDesc || this.$i18n.t('noDescription')
            obj.method = REQUEST_TYPE[item.apiInfo.baseInfo.apiRequestType]
            break
          case PROTOCOL_TYPE.Dubbo:
            obj.url = item.apiInfo.dubboApiBaseInfo.apiname
            obj.apiName = `(${item.apiInfo.dubboApiBaseInfo.apidocname})`
            obj.desc = item.apiInfo.apiDesc || this.$i18n.t('noDescription')
            break
          case PROTOCOL_TYPE.Gateway:
            obj.url = item.apiInfo.gatewayApiBaseInfo.url
            obj.apiName = `(${item.apiInfo.gatewayApiBaseInfo.name})`
            obj.desc = item.apiInfo.apiDesc || this.$i18n.t('noDescription')
            obj.method = item.apiInfo.gatewayApiBaseInfo.httpMethod.toLocaleUpperCase()
            break
          case PROTOCOL_TYPE.Grpc:
            obj.url = item.apiInfo.methodName
            obj.apiName = `(${item.apiInfo.apiName})`
            obj.desc = item.apiInfo.apiDesc || this.$i18n.t('noDescription')
            obj.isGrpc = true
            break
          default:
            break
        }
      }
      return obj
    }
  },
  methods: {
    handGetUrl () {
      let item = this.shareApiDetail
      let apiID
      let projectID = this.$utils.getQuery('project')
      let indexProjectID
      switch (`${item.protocol}`) {
        case PROTOCOL_TYPE.HTTP:
          apiID = item.apiInfo.baseInfo.apiID
          if (Number(projectID) !== item.apiInfo.baseInfo.projectID) {
            indexProjectID = item.apiInfo.baseInfo.projectID
          }
          break
        case PROTOCOL_TYPE.Dubbo:
          apiID = item.apiInfo.apiID
          if (Number(projectID) !== item.apiInfo.projectID) {
            indexProjectID = item.apiInfo.projectID
          }
          break
        case PROTOCOL_TYPE.Gateway:
          apiID = item.apiInfo.apiID
          if (Number(projectID) !== item.apiInfo.projectID) {
            indexProjectID = item.apiInfo.projectID
          }
          break
        case PROTOCOL_TYPE.Grpc:
          apiID = item.apiInfo.apiID
          if (Number(projectID) !== item.apiInfo.projectID) {
            indexProjectID = item.apiInfo.projectID
          }
          break
        default:
          break
      }
      let url = `${window.location.origin}/#${PATH.API_DETAIL}?projectID=${projectID}&apiID=${apiID}&apiProtocol=${item.protocol}`
      if (indexProjectID) {
        url += `&indexProjectID=${indexProjectID}`
      }
      return url
    },
    handleToMock () {
      let url = this.handGetUrl() + `&tab=${API_DETAIL_TAB.MOCK}`
      window.open(url)
    },
    handleToTest () {
      let url = this.handGetUrl() + `&tab=${API_DETAIL_TAB.TEST}`
      window.open(url)
    }
  }
}
</script>
<style scoped>
.right-top-container{
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 40px 12px 16px;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
}
.right-top-container .left-wrap {
  width: calc(100% - 266px);
}
.right-top-container .title-left{
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
}
.right-top-container .title-left .url{
  font-size: 18px;
  color: rgba(0, 0, 0, 0.85);
  font-weight: 650;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  word-break: keep-all;
  display: inline-block;
}
.right-top-container .title-left .method {
  margin-left: 8px;
  font-size: 12px;
  border-radius: 3px;
  display: inline-block;
  height: 20px;
  line-height: 20px;
  text-align: center;
  padding: 0 10px;
}
.right-top-container .title-left .url-method {
  background: #1890FF;
  color: #fff;
}
.right-top-container .title-left .request-method {
  background: #eef8ff;
  color: #1890FF;
  border: 1px solid #3c9cf7;
}
.right-top-container .url-desc {
  font-size: 14px;
  width: 100%;
  color: rgba(0, 0, 0, 0.45);
  margin-top: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  word-break: keep-all;
  display: inline-block;
}
</style>
