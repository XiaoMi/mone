<template>
	<div class="optional-container">
		<div class="optional-wrap">
			<div class="custom-header">
				<span>
					<!-- <el-checkbox @change="handleChangeAll"></el-checkbox> -->
				</span>
				<strong class="header-name">{{$i18n.t('ApiClass.name')}}</strong>
				<strong class="header-path">{{$i18n.t('ApiClass.path')}}</strong>
				<strong class="header-type">{{$i18n.t('ApiClass.type')}}</strong>
				<strong class="header-status">{{$i18n.t('ApiClass.interfaceStatus')}}</strong>
				<strong class="header-time">{{$i18n.t('uptateTime')}}</strong>
			</div>
			<ul>
				<template v-if="list && list.length">
					<li v-for="item in list" :key="item.apiID">
						<span class="checkbox-item">
							<el-checkbox @change="handleChange($event,'api',item)" v-model="item.checked"></el-checkbox>
						</span>
						<span class="header-name">{{item.apiName}}</span>
						<span class="header-path">{{item.apiURI}}</span>
						<span class="header-type">{{protocol[item.apiProtocol]}}<em :class="[requestType[item.apiRequestType]]">{{requestType[item.apiRequestType]}}</em></span>
						<span class="header-status">
              <var :class="{not:item.apiStatus === showStatus.NOT,disabled:item.apiStatus === showStatus.DISABLED}">·</var>{{api_status[item.apiStatus]}}
            </span>
						<span class="header-time">{{moment(new Date(item.apiUpdateTime)).format('YYYY-MM-DD HH:mm:ss')}}</span>
					</li>
				</template>
				<Empty v-else/>
			</ul>
		</div>
	</div>
</template>
<script>
import Empty from '@/components/Empty'
import { PROTOCOL, REQUEST_TYPE, SHOW_STATUS, API_STATUS } from '@/views/constant'
import moment from 'moment'
import { computed, reactive, toRefs, watch, onMounted } from "vue"
import { useStore } from "vuex"

export default {
  name: 'HasSelected',
  components: {
    Empty
  },
  props: {
    hasSelectd: {
      type: Array,
      default () {
        return []
      }
    },
  },
	setup(props, context){
		const store = useStore()
    const selectApiIds = computed(() => store.getters.selectApiIds)
		const state = reactive({
			selectAllApiIds: [],
			list: []
		})

		watch(() => [props.hasSelectd.value], () => {
			// if (props.hasSelectd.length !== ) {

			// }
			state.list = [].concat(props.hasSelectd)
			let ids = []
      props.hasSelectd.forEach(v => {
        ids.push(v.apiID)
      })
      state.selectAllApiIds = [].concat(ids)
			// context.emit("changeOptional", [...props.hasSelectd])
		}, {
			deep: true
		})

		// const handleChangeAll = (bool) => {
    //   if (bool) {
    //     let arr = []
    //     props.hasSelectd.forEach(v => {
    //       arr.push(v.apiID)
    //       v.checked = true
    //     })
    //     state.selectAllApiIds = [].concat(arr)
    //   } else {
    //     state.selectAllApiIds = []
    //     props.hasSelectd.forEach(v => {
    //       v.checked = false
    //     })
    //   }
    //   // this.$forceUpdate()
    // }
    const handleChange = (bool, type, item) => {
			if (state.list && state.list.length) {
				state.list.forEach(v => {
					if (v.apiID === item.apiID) {
						v.checked = bool
					}
				})
			}
			let arr = state.list.filter(v => v.checked)
			let newSelectApiIds = selectApiIds.value || []
			if (bool) {
        state.selectAllApiIds = state.selectAllApiIds.concat([item.apiID])
				newSelectApiIds.push(item.apiID)
			} else {
        state.selectAllApiIds = state.selectAllApiIds.filter(v => v !== item.apiID)
				newSelectApiIds = newSelectApiIds.filter(v => v !== item.apiID)
			}
      store.dispatch('apilist.import/changeSelectApiIds', newSelectApiIds)
			context.emit("changeOptional", arr)
      // this.$forceUpdate()
    }
		return {
			handleChange,
			...toRefs(state),
			protocol: PROTOCOL,
			api_status: API_STATUS,
			moment: moment,
			showStatus: SHOW_STATUS,
			requestType: REQUEST_TYPE
		}
	}
}
</script>

<style scoped>
.optional-container{
	background: #fff;
}
.optional-container .serach-wrap{
	padding: 10px 0 20px;
}
.optional-container .serach-wrap,.optional-container .serach-wrap dl {
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.optional-container .serach-wrap dl {
	margin-right: 40px;
}
.optional-container .serach-wrap dl dt {
	font-size: 14px;
	color: rgba(0, 0, 0, 0.64);
	margin-right: 8px;
	white-space: nowrap;
}
.optional-wrap .custom-header {
	height: 54px;
	line-height: 54px;
	background: rgba(250, 250, 250, 1);
	border: 1px solid rgba(232, 232, 232, 1);
	border-radius: 3px 3px 0 0;
	padding: 0 20px;
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.optional-wrap .custom-header span, .optional-wrap ul li span.checkbox-item {
	display: inline-block;
	width: 20px;
}
.optional-wrap .custom-header strong, .optional-wrap ul li span:not(.checkbox-item) {
	font-size: 14px;
	display: inline-block;
	line-height: 20px;
}
.optional-wrap .custom-header strong.header-name, .optional-wrap ul li span.header-name {
	width: calc(((100% - 80px) * 0.3) + 20px);
	padding-left: 30px;
	position: relative;
}
.optional-wrap .custom-header strong.header-name{
	padding-left: 20px;
}
.optional-wrap ul li span.header-name  i {
	position: absolute;
	left: 10px;
	top: 50%;
	font-size: 12px;
	margin-top: -6px;
	cursor: pointer;
}

.optional-wrap .custom-header strong.header-path, .optional-wrap ul li span.header-path {
	width: calc((100% - 80px) * 0.25);
	white-space: wrap;
	word-break: break-all;
	padding-right: 16px;
}
.optional-wrap .custom-header strong.header-type, .optional-wrap ul li span.header-type {
	width: calc((100% - 80px) * 0.15);
}
.optional-wrap .custom-header strong.header-status, .optional-wrap ul li span.header-status {
	width: calc((100% - 80px) * 0.15);
}
.optional-wrap .custom-header strong.header-time, .optional-wrap ul li span.header-time {
	width: calc((100% - 80px) * 0.15);
	text-align: center;
}
.optional-wrap ul li {
	border-bottom: 1px solid rgba(232, 232, 232, 1);
	padding: 19px 20px;
	display: flex;
	align-items: center;
	justify-content: flex-start;
	font-size: 14px;
	color: rgba(0, 0, 0, 0.64);
}
.optional-wrap ul li span.header-type em {
	border-radius: 3px;
	margin-right: 8px;
	font-size: 12px;
	height: 18px;
	padding: 0 4px;
	line-height: 18px;
	color: #1890FF;
	background-color: #eef8ff;
	border: 1px solid #3c9cf7;
	text-align: center;
	white-space: nowrap;
	display: inline-block;
	font-style: normal;
	margin-left: 4px;
}
.optional-wrap ul li span.header-type em.GET{
	background-color: rgba(246, 255, 237, 1);
	border: 1px solid rgba(183, 235, 143, 1);
	color: #52C41A;
}
.optional-wrap ul li span.header-type em.PUT{
	background-color: rgba(246, 255, 237, 1);
	border: 1px solid rgba(183, 235, 143, 1);
	color: #52C41A;
}
.optional-wrap ul li span.header-type em.DELETE{
	background-color: rgba(240, 54, 7, 0.1);
	border: 1px solid rgba(240, 54, 7, 1);
	color: rgba(240, 54, 7, 1);
}
.optional-wrap ul li span.header-status var {
	font-size: 34px;
	color: #00A854;
	font-style: normal;
	display: inline-block;
	vertical-align: middle;
}
.optional-wrap ul li span.header-status var.not{
	color: #F04134;
}
.optional-wrap ul li span.header-status var.disabled{
	color: #ccc;
}
</style>
