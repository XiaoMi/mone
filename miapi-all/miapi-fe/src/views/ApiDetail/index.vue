<template>
	<div class="api-detail-tab-wrap">
    <el-tabs v-model="activeName">
      <el-tab-pane :label="`API ${$i18n.t('details')}`" :name="apiDetailTab.DETAIL">
        <div id="api-detail-container" class="api-detail-container">
          <div class="api-detail-container-flex">
            <div :class="{'api-detail-container-flex-detail': true, 'isDetail': !isEditDetail}">
              <HttpDetail v-if="apiProtocol === protocolType.HTTP"/>
              <DubboDetail v-else-if="apiProtocol === protocolType.Dubbo"/>
              <GatewayDetail v-else-if="apiProtocol === protocolType.Gateway"/>
              <GrpcDetail v-else-if="apiProtocol === protocolType.Grpc"/>
            </div>
            <div v-show="!isEditDetail" class="api-detail-container-flex-list">
              <dl>
                <dt>{{$i18n.t('catalog')}}</dt>
                <dd :class="{'active': activeIndex === 0}"><a @click="handleGo(0)" href="javascript:;">{{$i18n.t('detailList.basicInformation')}}</a></dd>
                <dd :class="{'active': activeIndex === 1}"><a @click="handleGo(1)" href="javascript:;">{{$i18n.t('detailList.requestParameter')}}</a></dd>
                <dd :class="{'active': activeIndex === 2}"><a @click="handleGo(2)" href="javascript:;">{{$i18n.t('detailList.returnParameter')}}</a></dd>
                <template v-if="apiProtocol === protocolType.Grpc">
                  <dd :class="{'active': activeIndex === 3}"><a @click="handleGo(3)" href="javascript:;">{{$i18n.t('detailList.errorCodeExplanation')}}</a></dd>
                  <dd :class="{'active': activeIndex === 4}"><a @click="handleGo(4)" href="javascript:;">{{$i18n.t('detailList.instructionManual')}}</a></dd>
                </template>
                <template v-else>
                  <dd :class="{'active': activeIndex === 3}"><a @click="handleGo(3)" href="javascript:;">{{$i18n.t('detailList.requestExample')}}</a></dd>
                  <dd :class="{'active': activeIndex === 4}"><a @click="handleGo(4)" href="javascript:;">{{$i18n.t('detailList.returnExample')}}</a></dd>
                  <dd :class="{'active': activeIndex === 5}"><a @click="handleGo(5)" href="javascript:;">{{$i18n.t('detailList.errorCodeExplanation')}}</a></dd>
                  <dd :class="{'active': activeIndex === 6}"><a @click="handleGo(6)" href="javascript:;">{{$i18n.t('detailList.instructionManual')}}</a></dd>
                </template>
              </dl>
            </div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane :label="`API ${$i18n.t('test')}`" :name="apiDetailTab.TEST">
        <div class="api-detail-container">
          <ApiTest v-if="activeName === apiDetailTab.TEST"/>
        </div>
      </el-tab-pane>
      <template v-if="apiProtocol !== protocolType.Grpc">
        <el-tab-pane label="Mock API" :name="apiDetailTab.MOCK">
          <div class="api-detail-container">
            <MockList/>
          </div>
        </el-tab-pane>
      </template>
      <el-tab-pane :label="$i18n.t('changeLog')" :name="apiDetailTab.LOG">
        <div class="api-detail-container">
          <ChangeLog/>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import HttpDetail from './HttpDetail'
import GatewayDetail from './GatewayDetail'
import DubboDetail from './DubboDetail'
import GrpcDetail from './GrpcDetail'
import MockList from './MockList'
import ApiTest from './ApiTest'
import ChangeLog from './ChangeLog'
import { PROTOCOL_TYPE } from '@/views/constant'
import { API_DETAIL_TAB } from '@/views/ApiList/constant'
import { mapGetters } from 'vuex'
import debounce from '@/common/debounce'

let timer

export default {
  name: 'ApiDetail',
  components: {
    HttpDetail,
    GatewayDetail,
    DubboDetail,
    ApiTest,
    MockList,
    ChangeLog,
    GrpcDetail
  },
  data () {
    return {
      apiProtocol: '',
      activeName: API_DETAIL_TAB.DETAIL,
      activeIndex: 0
    }
  },
  beforeUnmount () {
    if (document.querySelector('#api-detail-container')) {
      document.querySelector('#api-detail-container').removeEventListener('scroll', this.handleScrollCurentIndex, false)
    }
  },
  beforeCreate () {
    this.$store.dispatch('apilist/changeEditDetail', false)
  },
  mounted () {
    if (this.activeName !== this.apiDetailActiveTab) {
      this.activeName = this.apiDetailActiveTab
    }
    this.$nextTick(() => {
      this.handleAddEvent()
    })
    if (this.$utils.getQuery('tab')) {
      this.activeName = this.$utils.getQuery('tab')
      let hash = decodeURIComponent(window.location.hash)
      if (hash.includes("tab=mock")) {
        window.location.hash = hash.replace(/&?tab=mock/gi,'')
      }
    }
  },
  watch: {
    $route: {
      handler: function (route) {
        this.activeIndex = 0
        this.handleScroll()
        this.apiProtocol = this.$utils.getQuery('apiProtocol')
        this.$store.dispatch('apitest/changeApiTestTarget', { apiTestProtocol: `${this.$utils.getQuery('apiProtocol')}` })
      },
      immediate: true
    },
    activeName: {
      handler (val, old) {
        if (val !== old) {
          this.$store.dispatch('apilist/changeApiDetailActiveTab', val)
        }
      },
      immediate: true
    }
  },
  computed: {
    ...mapGetters([
      'apiDetailActiveTab',
      'isEditDetail'
    ]),
    protocolType () {
      return PROTOCOL_TYPE
    },
    apiDetailTab () {
      return API_DETAIL_TAB
    }
  },
  methods: {
    handleAddEvent () {
      clearTimeout(timer)
      timer = setTimeout(() => {
        let $dom = document.querySelector('#api-detail-container');
        if ($dom) {
          $dom.removeEventListener('scroll', this.handleScrollCurentIndex, false)
          $dom.addEventListener('scroll', this.handleScrollCurentIndex, false)
        }
      }, 500)
    },
    handleScrollCurentIndex: debounce(function (e) {
      if (!e.target) {
        return
      }
      let hasScroll = e.target.scrollTop
      let doms = document.querySelectorAll('.api-detail-container-flex .api-detail-container .api-detail-content')
      let arr = []
      for (let i = 0; i < doms.length; i++) {
        let t = doms[i].offsetTop + doms[i].offsetHeight
        arr.push(t)
      }
      let currentHei = 0
      for (let j = 0; j < arr.length; j++) {
        currentHei = arr[j]
        if (hasScroll < currentHei) {
          this.activeIndex = j
          break
        }
      }
    }, 300, false),
    handleGo (index) {
      document.querySelector('#api-detail-container').removeEventListener('scroll', this.handleScrollCurentIndex, false)
      this.activeIndex = index
      let doms = document.querySelectorAll('.api-detail-container-flex .api-detail-container .api-detail-content')
      this.handleScroll((doms[index] && doms[index].offsetTop) || 0)
    },
    handleScroll (top = 0) {
      if (document.querySelector('#api-detail-container')) {
        document.querySelector('#api-detail-container').scrollTo({ top })
        this.$nextTick(() => {
          this.handleAddEvent()
        })
      }
    }
  }
}
</script>

<style scoped>
.api-detail-tab-wrap >>> .el-tabs__header {
	padding: 0 30px 0;
	background: #fff;
	border-bottom: 1px solid #e6e6e6;
	margin-bottom: 0;
}
.api-detail-tab-wrap >>> .el-tabs__header .el-tabs__nav-wrap::after {
	content: none;
}
.api-detail-container {
	height: calc(100vh - 124px);
	overflow-y: auto;
}
.api-detail-container .api-detail-container-flex {
  display: flex;
}
.api-detail-container .api-detail-container-flex .api-detail-container-flex-detail {
  width: 100%;
}
.api-detail-container .api-detail-container-flex .api-detail-container-flex-detail.isDetail {
  width: calc(100% - 110px);
}
@media only screen and (max-width:1200px){
  .api-detail-container .api-detail-container-flex .api-detail-container-flex-detail.isDetail {
    width: 100%;
  }
  .api-detail-container .api-detail-container-flex .api-detail-container-flex-list{
    display: none;
  }
}
.api-detail-container .api-detail-container-flex .api-detail-container-flex-detail {
  flex: 1;
  flex-shrink: 1;
}
.api-detail-container .api-detail-container-flex .api-detail-container-flex-list {
  width: 110px;
  position: relative;
  padding: 50px 0 0 20px;
}
.api-detail-container .api-detail-container-flex .api-detail-container-flex-list dl {
  position: fixed;
  color: rgba(0, 0, 0, 0.65);
  border-left: 2px solid #e6e6e6;
  padding: 4px 0 2px 12px;
}
.api-detail-container .api-detail-container-flex .api-detail-container-flex-list dl dt {
  font-weight: 650;
  font-size: 16px;
}
.api-detail-container .api-detail-container-flex .api-detail-container-flex-list dl dd{
  font-size: 14px;
  margin-top: 16px;
  position: relative;
  cursor: pointer;
}
.api-detail-container .api-detail-container-flex .api-detail-container-flex-list dl dd a:hover {
  color: #1890FF;
}
.api-detail-container .api-detail-container-flex .api-detail-container-flex-list dl dd.active a{
  color: #1890FF;
}
.api-detail-container .api-detail-container-flex .api-detail-container-flex-list dl dd.active::before {
  position: absolute;
  content: '';
  left: -20px;
  top: 50%;
  width: 14px;
  height: 14px;
  border: 4px solid #1890FF;
  background: #fff;
  border-radius: 50%;
  margin-top: -7px;
}
.api-detail-container::-webkit-scrollbar{
	display: none;
}
</style>
