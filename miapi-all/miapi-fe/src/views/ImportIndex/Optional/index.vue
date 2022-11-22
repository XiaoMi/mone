<template>
	<div class="optional-container">
		<div class="serach-wrap">
			<dl>
				<dt>{{$i18n.t('titleProject')}}:</dt>
				<dd>
					<el-select style="width: 100%" @change="handleChangeProjectId" v-model="selectProjectID" :placeholder="$i18n.t('placeholder.pleaseChoose')">
						<el-option v-for="item in projectList" :key="item.id" :label="item.name" :value="item.id"></el-option>
					</el-select>
				</dd>
			</dl>
			<dl>
				<dt>{{$i18n.t('ApiClass.name')}}:</dt>
				<dd>
					<el-input v-model.trim="searchNmae" :placeholder="$i18n.t('placeholder.pleaseEnterName')" style="small"/>
				</dd>
			</dl>
			<dl>
				<dt>{{$i18n.t('ApiClass.path')}}:</dt>
				<dd>
					<el-input v-model.trim="searchPath" :placeholder="$i18n.t('placeholder.pleaseEnterPath')" style="small"/>
				</dd>
			</dl>
			<dl>
				<dd>
					<el-button @click="handleSearch" type="primary">{{$i18n.t('btnText.search')}}</el-button>
				</dd>
			</dl>
		</div>
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
				<template  v-for="(v, index) in importGroupList" :key="`${index}${v.groupID}`">
					<li>
						<span class="checkbox-item">
							<el-checkbox :indeterminate="v.indeterminate" :disabled="!showSubMenu[v.groupID] || !showSubMenu[v.groupID].length" @change="handleChange($event,'group',v)" v-model="v.checked"></el-checkbox>
						</span>
						<span class="header-name"><el-icon v-if="showSubMenu[v.groupID] && showSubMenu[v.groupID].length" @click.stop="handleChangeUnfold(v.groupID)"><CaretBottom v-show="unfoldGroupIDs.includes(v.groupID)" /><CaretRight v-show="!unfoldGroupIDs.includes(v.groupID)" /></el-icon>{{v.groupName}}</span>
						<span class="header-path"></span>
						<span class="header-type"></span>
						<span class="header-status"></span>
						<span class="header-time"></span>
					</li>
					<transition-group v-if="showSubMenu[v.groupID] && showSubMenu[v.groupID].length" :key="v.groupID">
						<li v-for="item in showSubMenu[v.groupID]" :key="item.apiID" v-show="unfoldGroupIDs.includes(v.groupID)">
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
					</transition-group>
				</template>
			</ul>
		</div>
	</div>
</template>
<script>
import { mapGetters, useStore } from 'vuex'
import { PROTOCOL, API_STATUS, SHOW_STATUS, REQUEST_TYPE } from '@/views/constant'
import { toRefs, reactive, watch, computed, onMounted, onUpdated } from "vue"
import moment from 'moment'
import * as utils from "@/utils"

export default {
  name: 'Optional',
  props: {
    otherArr: {
      type: Array,
      default () {
        return []
      }
    },
    selectAllApiIds: {
      type: Array,
      default (){
        return []
      }
    },
    selectGroups: {
      type: Array,
      default (){
        return []
      }
    }
  },
  setup(props, context) {
    const store = useStore();

    // 获取需要展示的分组和api
    store.dispatch('apilist.import/getGroupAndSubMenu', utils.getQuery('projectID'))
    const state = reactive({
      unfoldGroupIDs: [], // 展开api组ids
      searchNmae: '',
      searchPath: '',
      showSubMenu: [], // 当前展示出来的所有api(搜索时可能部分隐藏)
      selectProjectID: undefined,
      protocol: PROTOCOL,
      api_status: API_STATUS,
      showStatus: SHOW_STATUS,
      requestType: REQUEST_TYPE,
      moment: moment
    })

    const curSubMenu = computed(() => store.getters.curSubMenu) // 需要展示的所有分组api
    const importGroupList = computed(() => store.getters.importGroupList) // 当前要展示的所有分组（不包含api）
    const projectList = computed(() => store.getters.projectList) // 项目列表



    onMounted(() => {
      state.selectProjectID = Number(utils.getQuery('projectID'))
    })

    watch(() => [props.selectAllApiIds.value], () => {
      let items = []
      Object.keys(state.showSubMenu).forEach(k => {
        state.showSubMenu[k].forEach(v => {
          if (props.selectAllApiIds.includes(v.apiID)) {
            items.push(v)
          }
        })
      })
      context.emit("changeHasSelectd", items)
    }, {
      deep: true
    })

    // 自定义方法start
    const handleChangeProjectId = (val) => {
      // 切换项目时保存上次项目的api
      let items = []
      Object.keys(state.showSubMenu).forEach(k => {
        state.showSubMenu[k].forEach(v => {
          if (props.selectAllApiIds.includes(v.apiID)) {
            items.push(v)
          }
        })
      })
      context.emit('changeOther', utils.deRepeat(props.otherArr.concat(items), 'apiID')) // 非当前项目选中的api

      store.dispatch('apilist.import/getGroupAndSubMenu', val)
    }
    /**
     * @description 更新api选中状态
     */
    const handlerRefresh = (arr = []) => {
      let ids = []
      let curGroupId = new Set()
      arr.forEach(v => {
        curGroupId.add(v.groupID)
        ids.push(v.apiID)
      })
      curGroupId = Array.from(curGroupId)
      Object.keys(state.showSubMenu).forEach(key => {
        if (state.showSubMenu[key] && state.showSubMenu[key].length) {
          state.showSubMenu[key].forEach(item => {
            item.checked = ids.includes(item.apiID)
            item.projectID = state.selectProjectID
          })
        }
      })
      context.emit("changeState", "selectAllApiIds", [].concat(ids))
      context.emit("changeState", "selectGroups", [].concat(curGroupId))
      setTimeout(() => {
        importGroupList.value.forEach(groupItem => {
        let has = curGroupId.includes(groupItem.groupID)
        groupItem.checked = has
        if (has) {
          let curArr = state.showSubMenu[groupItem.groupID] || []
          groupItem.indeterminate = curArr.some(v => v.checked) && curArr.some(v => !v.checked)
        } else {
          groupItem.indeterminate = false
        }
      })
      }, 0);
      // this.$forceUpdate()
    }
    /***
     * @description 更新api或者api分组选中状态
     */
    const handleChange = (bool, type, item) => {
      switch (type) {
        case 'api':
          if (state.showSubMenu[item.groupID] && state.showSubMenu[item.groupID].length) {
            state.showSubMenu[item.groupID].forEach(v => {
              if (v.apiID === item.apiID) {
                v.checked = bool
                v.projectID = state.selectProjectID
                if (bool) {
                  context.emit("changeState", "selectAllApiIds", props.selectAllApiIds.concat([item.apiID]))
                } else {
                  context.emit("changeState", "selectAllApiIds", props.selectAllApiIds.filter(v => v !== item.apiID))
                }
              }
            })
          }
          handleChangeGroupStatusFromApi(item)
          break
        case 'group':
          if (bool) {
            context.emit("changeState", "selectGroups", props.selectGroups.concat([item.groupID]))
            handleSelectAllGroupApi(item.groupID, 'add')
          } else {
            context.emit("changeState", "selectGroups", props.selectGroups.filter(v => v !== item.groupID))
            handleSelectAllGroupApi(item.groupID, 'delete')
          }
          handleChangeIndeterminate(item.groupID)
          break

        default:
          break
      }
      // this.$forceUpdate()
    }
    /**
     * @description 切换分组选中状态时 更改对应分组中api选中状态
     */
    const handleSelectAllGroupApi = (id, type) => {
      let arr = []
      if (state.showSubMenu[id] && state.showSubMenu[id].length) {
        state.showSubMenu[id].forEach(v => {
          arr.push(v.apiID)
          v.checked = type === 'add'
          v.projectID = state.selectProjectID
        })
      }
      if (type === 'add') {
        arr = arr.concat(props.selectAllApiIds)
        context.emit("changeState", "selectAllApiIds", Array.from(new Set(arr)))
      } else {
        context.emit("changeState", "selectAllApiIds", props.selectAllApiIds.filter(v => !arr.includes(v)))
      }
      // this.$forceUpdate()
    }
    /**
     * @description 切换分组展开收起状态
     */
    const handleChangeUnfold = (groupid) => {
      if (state.unfoldGroupIDs.includes(groupid)) {
        state.unfoldGroupIDs = state.unfoldGroupIDs.filter(id => id !== groupid)
      } else {
        state.unfoldGroupIDs = state.unfoldGroupIDs.concat([groupid])
      }
    }
    /**
     * 切换分组是否为半选中状态
     */
    const handleChangeIndeterminate = (id) => {
      let arr = state.showSubMenu[id] ? [...state.showSubMenu[id]] : []
      importGroupList.value.forEach(groupItem => {
        if (groupItem.groupID === id) {
          groupItem.indeterminate = arr.some(v => v.checked) && arr.some(v => !v.checked)
          groupItem.checked = arr.some(v => v.checked)
        }
      })
    }
    /**
     * 根据api状态改变，判断该分组是选中还是半选中
     */
    const handleChangeGroupStatusFromApi = (item) => {
      let arr = state.showSubMenu[item.groupID] || []
      let checked = arr.some(v => v.checked)

      if (!checked && props.selectGroups.includes(item.groupID)) {
        context.emit("changeState", "selectGroups", props.selectGroups.filter(v => v !== item.groupID))
      } else if (checked && !props.selectGroups.includes(item.groupID)) {
        context.emit("changeState", "selectGroups",props.selectGroups.concat([item.groupID]))
      }

      handleChangeIndeterminate(item.groupID)
    }
    /**
     * 查询api
     */
    const handleSearch = () => {
      if (!state.searchNmae && !state.searchPath) {
        state.showSubMenu = Object.assign({}, curSubMenu)
        return
      }
      let newObj = {}
      Object.keys(curSubMenu).forEach(v => {
        newObj[v] = curSubMenu[v].filter(item => {
          if (state.searchPath && state.searchNmae) {
            return ((item.apiName.indexOf(state.searchNmae) !== -1) || (item.apiURI.indexOf(state.searchPath) !== -1))
          } else if (state.searchPath) {
            return (item.apiURI.indexOf(state.searchPath) !== -1)
          } else if (state.searchNmae) {
            return (item.apiName.indexOf(state.searchNmae) !== -1)
          }
        })
      })
      state.showSubMenu = newObj
    }
    // 自定义方法end


    watch(curSubMenu, (val) => {
      let keys = Object.keys(val);
      if (keys.length){
        keys.forEach(k => {
          val[k].forEach(item => {
            item.checked = false
            item.projectID = state.selectProjectID
          })
        })
      }
      state.showSubMenu = {...val}
      handlerRefresh(props.otherArr)
    }, {
      immediate: true,
    })

    return {
      ...toRefs(state),
      importGroupList,
      projectList,
      handlerRefresh,
      handleSearch,
      handleChange,
      handleChangeProjectId,
      handleChangeUnfold
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
