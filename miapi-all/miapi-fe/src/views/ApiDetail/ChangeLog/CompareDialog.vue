<template>
	<div class="compare-log-wrap">
		<Empty description="业务逻辑变更" v-if="!hasDiff"/>
		<div v-else class="compare-log-container">
			<div class="compare-item">
				<div class="compare-item-title">
					<span>
            {{$i18n.t('difference')}}({{compareData.preId}})
            <el-popover
              width="230"
              placement="right"
              trigger="hover">
              <div style="font-size: 12px">
                <p>{{$i18n.t('versionDiffInfo.desc')}}</p>
                <p style="padding: 8px 0"><span style="background: #ffbbbb; line-height: 1.2; display: inline-block">{{$i18n.t('versionDiffInfo.color1')}}</span>{{$i18n.t('versionDiffInfo.exp1')}}</p>
                <p><span style="background: #bbffbb; line-height: 1.2; display: inline-block">{{$i18n.t('versionDiffInfo.color2')}}</span>{{$i18n.t('versionDiffInfo.exp2')}}</p>
              </div>
              <template #reference>
                <el-icon :size="14"><InfoFilled /></el-icon>
              </template>
            </el-popover>
          </span>
				</div>
				<div class="compare-content">
					<template v-for="item in diffArr">
						<div v-if="item.html" :key="item.key" class="diff-info">
							<h4>{{item.name}}</h4>
							<div v-html="item.html"></div>
						</div>
					</template>
					<div v-if="headerHtml" class="diff-info">
						<h4>headers</h4>
						<div v-html="headerHtml"></div>
					</div>
					<div v-if="reqParamHtml" class="diff-info">
						<h4>{{$i18n.t('requestParameter')}}</h4>
						<div v-html="reqParamHtml"></div>
					</div>
					<div v-if="resParamHtml" class="diff-info">
						<h4>{{$i18n.t('returnParameter')}}</h4>
						<div v-html="resParamHtml"></div>
					</div>
					<div v-if="remarkHtml" class="diff-info">
						<h4>{{$i18n.t('instructionManual')}}</h4>
						<div v-html="remarkHtml"></div>
					</div>
				</div>
			</div>
			<div class="compare-item">
				<div class="compare-item-title">
					<span>{{$i18n.t('latestVersion')}}({{compareData.nowId}})</span>
				</div>
				<div class="compare-content original">
					<template v-for="item in diffArr">
						<div v-if="item.html" :key="item.key" class="diff-info">
							<h4>{{item.name}}</h4>
							<div v-html="item.html"></div>
						</div>
					</template>
					<div v-if="headerHtml" class="diff-info">
						<h4>headers</h4>
						<div v-html="headerHtml"></div>
					</div>
					<div v-if="reqParamHtml" class="diff-info">
						<h4>{{$i18n.t('requestParameter')}}</h4>
						<div v-html="reqParamHtml"></div>
					</div>
					<div v-if="resParamHtml" class="diff-info">
						<h4>{{$i18n.t('returnParameter')}}</h4>
						<div v-html="resParamHtml"></div>
					</div>
					<div v-if="remarkHtml" class="diff-info">
						<h4>{{$i18n.t('instructionManual')}}</h4>
						<div v-html="remarkHtml"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import loadJS from '@/common/loadJS'
import { DUBBO_KEY, HTTP_KEY, GATEWAY_KEY } from './constant'
import { PROTOCOL_TYPE, API_REQUEST_PARAM_TYPE, API_STATUS } from '@/views/constant'
import Empty from '@/components/Empty'

export default {
  name: 'CompareDialog',
  components: {
    Empty
  },
  data () {
    return {
      diffArr: [],
      headerHtml: '',
      reqParamHtml: '',
      resParamHtml: '',
      remarkHtml: '',
      hasDiff: false
    }
  },
  props: {
    compareData: {
      type: Object,
      default () {
        return {
          currentApi: {},
          oldApi: {},
          nowId: '',
          preId: ''
        }
      }
    }
  },
  watch: {
    compareData: {
      handler (val) {
        if (val.currentApi && val.oldApi) {
        	this.renderJsondiffpatch(val.oldApi, val.currentApi)
        }
      },
      immediate: true,
      deep: true
    }
  },
  methods: {
    renderJsondiffpatch (left, right) {
      loadJS('https://cdn.jsdelivr.net/npm/jsondiffpatch/dist/jsondiffpatch.umd.min.js').then(() => {
        if (window.jsondiffpatch) {
          let instance = window.jsondiffpatch.create({
            objectHash: function (obj, index) {
              if (typeof obj.apiDesc !== 'undefined') {
                return obj.apiDesc
              }
              return '$$index:' + index
            }
            // textDiff: {
            //   minLength: 1 // default value 60
            // }
          })
          let hasDiff = false
          switch (`${this.$utils.getQuery('apiProtocol')}`) {
            case PROTOCOL_TYPE.HTTP:
            case PROTOCOL_TYPE.Gateway:
              let keys = `${this.$utils.getQuery('apiProtocol')}` === PROTOCOL_TYPE.HTTP ? HTTP_KEY : GATEWAY_KEY
              this.diffArr = keys.map(v => {
                let o = {
                  ...v
                }
                if (v.key === 'apiStatus') {
                  o.html = window.jsondiffpatch.formatters.html.format(instance.diff(API_STATUS[left.baseInfo[v.key]], API_STATUS[right.baseInfo[v.key]]), API_STATUS[left.baseInfo[v.key]])
                } else {
                  o.html = window.jsondiffpatch.formatters.html.format(instance.diff(left.baseInfo[v.key], right.baseInfo[v.key]), left.baseInfo[v.key])
                }
                if (!hasDiff) {
                  hasDiff = !!o.html
                }
                return o
              })
          		this.remarkHtml = window.jsondiffpatch.formatters.html.format(instance.diff(left.baseInfo.apiRemark, right.baseInfo.apiRemark), left.baseInfo.apiRemark)
          		this.headerHtml = window.jsondiffpatch.formatters.html.format(instance.diff(left.headerInfo, right.headerInfo), left.headerInfo)
              let lrequestInfo = left.requestInfo
              let rrequestInfo = right.requestInfo
              if (left.baseInfo.apiRequestParamType === API_REQUEST_PARAM_TYPE.RAW || left.baseInfo.apiRequestParamType === API_REQUEST_PARAM_TYPE.JSON) {
                lrequestInfo = left.baseInfo.apiRequestRaw
                try {
                  lrequestInfo = JSON.parse(lrequestInfo)
                } catch (error) {}
              }
              if (right.baseInfo.apiRequestParamType === API_REQUEST_PARAM_TYPE.RAW || right.baseInfo.apiRequestParamType === API_REQUEST_PARAM_TYPE.JSON) {
                rrequestInfo = right.baseInfo.apiRequestRaw
                try {
                  rrequestInfo = JSON.parse(rrequestInfo)
                } catch (error) {}
              }

              let lresultInfo = left.resultInfo
              let rresultInfo = right.resultInfo
              if (left.baseInfo.apiResponseParamType === API_REQUEST_PARAM_TYPE.RAW || left.baseInfo.apiResponseParamType === API_REQUEST_PARAM_TYPE.JSON) {
                lresultInfo = left.baseInfo.apiResponseRaw
                try {
                  lresultInfo = JSON.parse(lresultInfo)
                } catch (error) {}
              }
              if (right.baseInfo.apiResponseParamType === API_REQUEST_PARAM_TYPE.RAW || right.baseInfo.apiResponseParamType === API_REQUEST_PARAM_TYPE.JSON) {
                rresultInfo = right.baseInfo.apiResponseRaw
                try {
                  rresultInfo = JSON.parse(rresultInfo)
                } catch (error) {}
              }
              this.resParamHtml = window.jsondiffpatch.formatters.html.format(instance.diff(lresultInfo, rresultInfo), lresultInfo)
          		this.reqParamHtml = window.jsondiffpatch.formatters.html.format(instance.diff(lrequestInfo, rrequestInfo), lrequestInfo)
              if (!hasDiff) {
                hasDiff = !!this.remarkHtml || this.headerHtml
              }
						 break
            case PROTOCOL_TYPE.Dubbo:
              this.diffArr = DUBBO_KEY.map(v => {
                let o = {
                  ...v,
                  html: window.jsondiffpatch.formatters.html.format(instance.diff(left.dubboInfo[v.key], right.dubboInfo[v.key]), left.dubboInfo[v.key])
                }
                if (!hasDiff) {
                  hasDiff = !!o.html
                }
                return o
              })
              this.resParamHtml = window.jsondiffpatch.formatters.html.format(instance.diff(left.resultInfo, right.resultInfo), left.resultInfo)
          		this.reqParamHtml = window.jsondiffpatch.formatters.html.format(instance.diff(left.requestInfo, right.requestInfo), left.requestInfo)
              break
            case PROTOCOL_TYPE.Grpc:
              this.resParamHtml = window.jsondiffpatch.formatters.html.format(instance.diff(left.resultInfo, right.resultInfo), left.resultInfo)
          		this.reqParamHtml = window.jsondiffpatch.formatters.html.format(instance.diff(left.requestInfo, right.requestInfo), left.requestInfo)
              break
            default:
              break
          }
          if (!hasDiff) {
            hasDiff = !!this.reqParamHtml || this.resParamHtml
          }
          this.hasDiff = hasDiff
        }
      }).catch(e => {})
    }
  }
}
</script>

<style scoped>
@import './diff/formatters-styles/annotated.css';
@import './diff/formatters-styles/html.css';
.compare-log-wrap {
	width: 100%;
	overflow-y: auto;
	height: 70vh;
}
.compare-log-wrap::-webkit-scrollbar {
	display: none;
}
.compare-log-wrap .compare-log-container{
	display: flex;
	align-items: flex-start;
	justify-content: flex-start;
	width: 100%;
	position: relative;
	min-height: 100%;
}
.compare-log-wrap .compare-log-container::after{
	position: absolute;
	left: 50%;
	top: 50px;
	width: 1px;
	height: calc(100% - 50px);
	content: '';
	background-color: #e6e6e6;
}
.compare-log-wrap .compare-log-container .compare-item{
	width: 50%;
}
.compare-log-wrap .compare-log-container .compare-item .compare-item-title{
	padding: 8px 0;
	border-bottom: 1px solid #e6e6e6;
}
.compare-log-wrap .compare-log-container .compare-item .compare-item-title > span {
	height: 32px;
	font-size: 14px;
	color: #FFFFFF;
	background: #1890FF;
	border-radius: 3px;
	text-align: center;
	display: inline-block;
	line-height: 32px;
	margin-left: 16px;
  padding: 0 8px;
}
.compare-log-wrap .compare-log-container .compare-item .compare-content{
	padding: 16px 16px 16px 4px;
}
.compare-log-wrap .compare-log-container .compare-item .compare-content.original >>> .jsondiffpatch-property-name,
.compare-log-wrap .compare-log-container .compare-item .compare-content.original >>> pre{
	/* text-decoration: none !important; */
	/* background: #fff; */
}
.compare-log-wrap .compare-log-container .compare-item .compare-content.original >>> .jsondiffpatch-property-name,
.compare-log-wrap .compare-log-container .compare-item .compare-content.original >>> pre{
	background: #fff;
}
.compare-log-wrap .compare-log-container .compare-item .compare-content.original >>> .jsondiffpatch-left-value,
.compare-log-wrap .compare-log-container .compare-item .compare-content.original >>> .jsondiffpatch-deleted
{
	display: none;
}
.compare-log-wrap .compare-log-container .compare-item .compare-content.original >>> .jsondiffpatch-right-value pre {
	/* background: #fff; */
}
.compare-log-wrap .compare-log-container .compare-item .compare-content .diff-info {
	margin-bottom: 20px;
}
.compare-log-wrap .compare-log-container .compare-item .compare-content .diff-info:last-child{
	margin-bottom: 0;
}
.compare-log-wrap .compare-log-container .compare-item .compare-content h4 {
	padding-left: 12px;
	margin-bottom: 8px;
}
.compare-log-wrap .compare-log-container .compare-item .compare-content >>> .jsondiffpatch-delta pre {
	white-space: pre-wrap !important;
}
.compare-log-wrap .compare-log-container .compare-item:last-child .compare-item-title span {
	color: #1890FF;
	background: rgba(230, 247, 255, 1);
	border: 1px solid rgba(145, 213, 255, 1);
}
</style>
