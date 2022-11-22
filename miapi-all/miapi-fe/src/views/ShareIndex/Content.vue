<template>
  <div id="right-content-container" class="right-content-container">
    <div v-if="shareApiDetail.protocol" class="right-content-container-flex">
      <div class="right-content-container-flex-detail">
        <HttpDetailComp v-if="`${shareApiDetail.protocol}` === protocol_type.HTTP" />
        <GatewayDetailComp v-else-if="`${shareApiDetail.protocol}` === protocol_type.Gateway"/>
        <DubboDetailComp v-else-if="`${shareApiDetail.protocol}` === protocol_type.Dubbo"/>
        <GrpcDetailComp v-else-if="`${shareApiDetail.protocol}` === protocol_type.Grpc"/>
      </div>
      <div class="right-content-container-flex-list">
        <dl>
          <dt>{{$i18n.t('catalog')}}</dt>
          <dd v-for="(v, index) in tabList" :key="index" :class="{'active': activeIndex === index}"><a @click="handleGo(index)" href="javascript:;">{{v}}</a></dd>
        </dl>
      </div>
    </div>
    <Empty v-else/>
	</div>
</template>

<script>
import HttpDetailComp from './HttpDetailComp'
import GatewayDetailComp from './GatewayDetailComp'
import DubboDetailComp from './DubboDetailComp'
import GrpcDetailComp from './GrpcDetailComp'
import debounce from '@/common/debounce'
import Empty from '@/components/Empty'
import { PROTOCOL_TYPE } from '@/views/constant'
import { mapGetters } from 'vuex'

let timer
export default {
  name: 'RightContent',
  components: {
    Empty,
    HttpDetailComp,
    GatewayDetailComp,
    DubboDetailComp,
    GrpcDetailComp
  },
  data () {
    return {
      activeIndex: 0,
      tabList: [this.$i18n.t('detailList.requestParameter'), this.$i18n.t('detailList.returnParameter'), this.$i18n.t('detailList.requestExample'), this.$i18n.t('detailList.returnExample'), this.$i18n.t('detailList.errorCodeExplanation'), this.$i18n.t('detailList.instructionManual')]
    }
  },
  beforeUnmount () {
    if (document.querySelector('#right-content-container')) {
      document.querySelector('#right-content-container').removeEventListener('scroll', this.handleScrollCurentIndex, false)
    }
  },
  computed: {
    ...mapGetters([
      'shareApiDetail'
    ]),
    protocol_type () {
      return PROTOCOL_TYPE
    }
  },
  watch: {
    "shareApiDetail.protocol": {
      handler (val) {
        if (val && (`${val}` === PROTOCOL_TYPE.Grpc)) {
          this.tabList = [
            this.$i18n.t('detailList.requestParameter'),
            this.$i18n.t('detailList.returnParameter'),
            this.$i18n.t('detailList.errorCodeExplanation'),
            this.$i18n.t('detailList.instructionManual')
          ]
        } else if (val && (`${val}` === PROTOCOL_TYPE.Dubbo)) {
          this.tabList.unshift(this.$i18n.t('detailList.basicInformation'))
        } else if (this.tabList.length === 7) {
          this.tabList.shift()
        }
      },
      immediate: true
    }
  },
  mounted () {
    if (this.shareApiDetail.protocol) {
      this.$nextTick(() => {
        this.handleAddEvent()
      })
    }
  },
  methods: {
    handleAddEvent () {
      clearTimeout(timer)
      timer = setTimeout(() => {
        let $dom = document.querySelector('#right-content-container');
        if ($dom){
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
      let doms = document.querySelectorAll('.right-content-container-flex .api-detail-container .api-detail-content')
      let arr = []
      for (let i = 0; i < doms.length; i++) {
        let t = doms[i].offsetTop - 91 + doms[i].offsetHeight
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
      document.querySelector('#right-content-container').removeEventListener('scroll', this.handleScrollCurentIndex, false)
      this.activeIndex = index
      let doms = document.querySelectorAll('.right-content-container-flex .api-detail-container .api-detail-content')
      this.handleScroll((doms[index] && (doms[index].offsetTop - 91)) || 0)
    },
    handleScroll (top = 0) {
      if (document.querySelector('#right-content-container')) {
        document.querySelector('#right-content-container').scrollTo({ top })
        this.$nextTick(() => {
          this.handleAddEvent()
        })
      }
    }
  }
}
</script>
<style scoped>
.right-content-container {
	height: calc(100vh - 128px);
	overflow-y: auto;
}
.right-content-container .right-content-container-flex {
  display: flex;
}
.right-content-container .right-content-container-flex .right-content-container-flex-detail.isDetail {
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
.right-content-container .right-content-container-flex .right-content-container-flex-detail {
  flex: 1;
}
.right-content-container .right-content-container-flex .right-content-container-flex-list {
  width: 110px;
  position: relative;
  padding: 50px 0 0 20px;
}
.right-content-container .right-content-container-flex .right-content-container-flex-list dl {
  position: fixed;
  color: rgba(0, 0, 0, 0.65);
  border-left: 2px solid #e6e6e6;
  padding: 4px 0 2px 12px;
}
.right-content-container .right-content-container-flex .right-content-container-flex-list dl dt {
  font-weight: 650;
  font-size: 16px;
}
.right-content-container .right-content-container-flex .right-content-container-flex-list dl dd{
  font-size: 14px;
  margin-top: 16px;
  position: relative;
  cursor: pointer;
}
.right-content-container .right-content-container-flex .right-content-container-flex-list dl dd a:hover {
  color: #1890FF;
}
.right-content-container .right-content-container-flex .right-content-container-flex-list dl dd.active a{
  color: #1890FF;
}
.right-content-container .right-content-container-flex .right-content-container-flex-list dl dd.active::before {
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
.right-content-container::-webkit-scrollbar{
	display: none;
}
</style>
