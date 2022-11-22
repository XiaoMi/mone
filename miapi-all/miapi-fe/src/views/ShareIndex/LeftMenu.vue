<template>
	<div class="left-menu-container">
		<h4>{{$i18n.t('apiCollectionList')}}</h4>
		<div class="search">
			<el-input v-model="searchVal" :placeholder="$i18n.t('placeholder.searchApiPath')"/>
		</div>
		<div class="menu-list">
			<el-collapse v-if="pageInfoList.length" v-model="activeNames" @change="handleChange">
				<el-collapse-item v-for="(v, index) in pageInfoList" :key="index+1" :name="`${index+1}`">
					<template #title>
						<span class="custom-title">
							{{v.indexName}}（{{v.apiList ? v.apiList.length : 0}} {{$i18n.t('items')}}）
						</span>
					</template>
					<ul v-if="v.apiList && v.apiList.length" class="menu-list-wrap">
						<li v-for="(item, ind) in v.apiList" :key="ind" :class="{'active': selected === `${index}-${ind}`}" @click="handleSelect(item, `${index}-${ind}`)">
							<dl>
								<dt>
									{{renderInfo(item).url}}
								</dt>
								<dd>
									{{renderInfo(item).apiName}}
								</dd>
							</dl>
						</li>
					</ul>
				</el-collapse-item>
			</el-collapse>
		</div>
	</div>
</template>

<script>
import { PROTOCOL_TYPE } from '@/views/constant'
import { mapGetters } from 'vuex'
import debounce from '@/common/debounce'
import { PATH } from "@/router/constant"
import { isExtranet } from "@/utils"
export default {
  name: 'LeftMenu',
  data () {
    return {
      activeNames: [],
      selected: '',
      searchVal: undefined
    }
  },
  computed: {
    ...mapGetters([
      'pageInfoList',
      'initPageInfoList',
      'shareApiDetail'
    ])
  },
  watch: {
    searchVal: {
      handler (val) {
        this.handleSearch(val)
      },
      immediate: true
    },
    activeNames: {
      handler (val, old) {
        let valSet = new Set(val)
        let oldSet = new Set(old)
        let comp = [...val.filter(v => !oldSet.has(v)), ...old.filter(v => !valSet.has(v))]
        if (comp.length) {
          this.$store.dispatch('shareindex/changeIndexDoc', {
            content: this.pageInfoList[Number(comp[0]) - 1].indexDoc,
            title: `${this.pageInfoList[Number(comp[0]) - 1].indexName} ${this.$i18n.t('collectionDocument')}`
          })
        }
      }
    },
    pageInfoList: {
      handler (val) {
        if (val && val.length) {
          let activeNames = []
          val.forEach((_, index) => {
            activeNames.push(`${index + 1}`)
          })
          this.activeNames = activeNames
          if (val[0] && val[0].apiList && val[0].apiList.length) {
            this.handleChangeHash(val[0].apiList[0])
      			this.selected = '0-0'
            this.$store.dispatch('shareindex/changeShareDetail', val[0].apiList[0])
          }
        }
      },
      immediate: true
    }
  },
  methods: {
    handleChange (val) {
      // console.log(val)
    },
    handleChangeHash (item) {
      if (isExtranet) {
        window.location.hash = `#${PATH.SHARE_OUTER_INDEX}?ids=${this.$utils.getQuery('ids')}&apiProtocol=${item.protocol}`
      } else {
        window.location.hash = `#${PATH.SHARE_INDEX}?project=${this.$utils.getQuery('project')}&ids=${this.$utils.getQuery('ids')}&apiProtocol=${item.protocol}`
      }
    },
    handleSearch: debounce(function (val) {
      if (!val) {
        this.$store.dispatch('shareindex/changePageInfoList', this.initPageInfoList)
        return
      }
      if (this.initPageInfoList && this.initPageInfoList.length) {
        let arr = []
        this.initPageInfoList.forEach(v => {
          let o = {
            ...v,
            apiList: []
          }
          if (v.apiList && v.apiList.length) {
            v.apiList.filter(item => {
              switch (`${item.protocol}`) {
                case PROTOCOL_TYPE.HTTP:
                  if (item.apiInfo.baseInfo.apiURI.indexOf(val) !== -1) {
                    o.apiList.push(item)
                  }
                  break
                case PROTOCOL_TYPE.Dubbo:
                  if (item.apiInfo.dubboApiBaseInfo.apiname.indexOf(val) !== -1) {
                    o.apiList.push(item)
                  }
                  break
                case PROTOCOL_TYPE.Gateway:
                  if (item.apiInfo.gatewayApiBaseInfo.url.indexOf(val) !== -1) {
                    o.apiList.push(item)
                  }
                  break
                case PROTOCOL_TYPE.Grpc:
                  if (item.apiInfo.methodName.indexOf(val) !== -1) {
                    o.apiList.push(item)
                  }
                  break
                default:
                  break
              }
            })
          }
          arr.push(o)
        })
        this.$store.dispatch('shareindex/changePageInfoList', arr)
      }
    }, 300),
    handleSelect (item, select) {
      this.handleChangeHash(item)
      this.$store.dispatch('shareindex/changeIndexDoc', {
        content: undefined,
        title: ''
      })
      this.selected = select
      this.$store.dispatch('shareindex/changeShareDetail', item)
    },
    renderInfo (item) {
      let obj = {
        url: '',
        apiName: ''
      }
      switch (`${item.protocol}`) {
        case PROTOCOL_TYPE.HTTP:
          obj.url = item.apiInfo.baseInfo.apiURI
          obj.apiName = item.apiInfo.baseInfo.apiName
          break
        case PROTOCOL_TYPE.Dubbo:
          obj.url = item.apiInfo.dubboApiBaseInfo.apiname
          obj.apiName = item.apiInfo.dubboApiBaseInfo.apidocname
          break
        case PROTOCOL_TYPE.Gateway:
          obj.url = item.apiInfo.gatewayApiBaseInfo.url
          obj.apiName = item.apiInfo.gatewayApiBaseInfo.name
          break
        case PROTOCOL_TYPE.Grpc:
          obj.url = item.apiInfo.methodName
          obj.apiName = item.apiInfo.apiName
          break
        default:
          break
      }
      return obj
    }
  }
}
</script>
<style lang="scss">
@import '@/styles/variables.scss';
.left-menu-container {
	width: 300px;
	background: #fff;
	height: calc(100vh - #{$headerHeight});
	overflow-y: auto;
	border-right: 1px solid #e6e6e6;
}
</style>

<style lang="scss">
.left-menu-container::-webkit-scrollbar {
	display: none;
}
.left-menu-container h4 {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.85);
	font-weight: normal;
	padding: 20px 16px;
}
.left-menu-container .search {
	margin: 0 16px 20px;
}
.left-menu-container .menu-list .custom-title {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.65);
	display: inline-block;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	word-break: keep-all;
  padding-left: 8px;
}
.left-menu-container .menu-list >>> .el-collapse-item__header {
	height: 45px;
	line-height: 45px;
	background: rgba(250, 250, 250, 1);
	padding: 0 16px;
}
.left-menu-container .menu-list >>> .el-collapse-item__header.is-active{
	border-bottom: 1px solid #e6e6e6;
}
.left-menu-container .menu-list .el-collapse {
	border-color: #e6e6e6;
}
.left-menu-container .menu-list >>> .el-collapse-item__content {
	padding: 4px 0;
}
.left-menu-container .menu-list ul.menu-list-wrap li{
	padding: 8px 16px;
	cursor: pointer;
}
.left-menu-container .menu-list ul.menu-list-wrap li.active dl dt {
	color: #1890FF;
}
.left-menu-container .menu-list ul.menu-list-wrap li:hover {
	background: #ecf5ff;
}
.left-menu-container .menu-list ul.menu-list-wrap li dl{
	display: flex;
	align-items: flex-start;
	justify-content: flex-start;
	flex-flow: column;
}
.left-menu-container .menu-list ul.menu-list-wrap li dl dt,
.left-menu-container .menu-list ul.menu-list-wrap li dl dd {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.85);
	display: inline-block;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	word-break: keep-all;
	width: 100%;
}
.left-menu-container .menu-list ul.menu-list-wrap li dl dd {
	color: rgba(0, 0, 0, 0.44);
}
</style>
