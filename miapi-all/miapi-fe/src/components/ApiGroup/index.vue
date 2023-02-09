<template>
  <div class="api-group-container">
		<el-menu
      class="el-menu-vertical-demo"
      ref="menuRef"
			:unique-opened="groupComp.uniqueOpened"
			@open="handleOpen"
			@close="handleClose"
      @select="handleSelect">
      <el-menu-item index="000" class="group-all">
				<el-input v-model="groupComp.searchWord" :placeholder="`${$i18n.t('groupSearchAPI')}`"></el-input>
				<div class="group-all-search-btn">
					<el-button style="font-size: 14px;font-weight: normal;padding: 0" @click.stop="handleAdd" text type="primary">{{type === group_type.API ? $i18n.t('newCategory') : $i18n.t('newCollection')}}</el-button>
					<el-switch
						active-color="#13ce66"
						inactive-color="#1890FF"
						:active-text="$i18n.t('path')"
						:inactive-text="$i18n.t('name')"
            inline-prompt
            :width="50"
						v-model="groupComp.searchSort">
					</el-switch>
				</div>
      </el-menu-item>
			<template v-for="item in groupList" :key="item.groupID">
				<el-sub-menu :class="{'select-group': activeGroupID === item.groupID && !activeItem}" :index="`${item.groupID}`">
					<template #title>
						<div class="group-name">
							<span class="group-name-item">{{item.groupName}} ({{handleShowNum(item.groupID)}}个)</span>
							<span v-if="item.groupID>0" class="el-dropdown-link">
								<el-tooltip v-if="!item.systemGroup" effect="dark" :content="$i18n.t('editGroup')" placement="top">
                  <el-icon :size="14" style="margin-right: 4px" @click.stop="handleGroupEdit(item)"><Edit /></el-icon>
								</el-tooltip>
								<el-tooltip v-if="!item.systemGroup" effect="dark" :content="$i18n.t('deleteGroup')" placement="top">
                  <el-icon :size="14" @click.stop="handleGroupDelete(item)"><Delete /></el-icon>
								</el-tooltip>
								<el-tooltip v-if="type === group_type.INDEX" effect="dark" :content="$i18n.t('importAPI')" placement="top">
                  <el-icon :size="14" @click.stop="handleGroupImport(item)"><Upload /></el-icon>
								</el-tooltip>
                <el-tooltip v-else effect="dark" :content="$i18n.t('newAPI')" placement="top">
                  <el-icon :size="14" @click.stop="handleAddApi(item)"><Plus /></el-icon>
								</el-tooltip>
							</span>
							<span v-if="item.groupID===0" class="el-dropdown-link">
								<el-tooltip effect="dark" :content="$i18n.t('emptyRecycle')" placement="top">
                  <el-icon :size="14" @click.stop="handleClearCommand"><Delete /></el-icon>
								</el-tooltip>
							</span>
						</div>
					</template>
					<el-menu-item v-for="(v, ind) in renderMenuItem(item.groupID)" :class="{'sec-menu-item': true,isActive: activeItem == v.apiID}" :key="v.apiID" :index="`${item.groupID},${ind}`">
            <div class="group-name">
              <el-tooltip :open-delay="800" effect="dark" :content="groupComp.searchSort ? v.apiURI:v.apiName" placement="right">
							  <span class="group-name-item">{{groupComp.searchSort ? v.apiURI:v.apiName}}</span>
              </el-tooltip>
              <div class="api-item-operate">
                <span v-if="groupType === group_type.API && `${v.apiProtocol}` === protocolType.HTTP" class="el-dropdown-link copy">
                  <el-tooltip effect="dark" :content="$i18n.t('copyAPI')" placement="top">
                    <el-icon :size="14" @click.stop="handleCopy(v)"><CopyDocument /></el-icon>
                  </el-tooltip>
                </span>
                <span v-if="groupType === group_type.API" class="el-dropdown-link">
                  <el-tooltip effect="dark" :content="$i18n.t('deleteAPI')" placement="top">
                    <el-icon :size="14" @click.stop="handleSubMenuDelete(v)"><Delete /></el-icon>
                  </el-tooltip>
                </span>
                <span v-else class="el-dropdown-link">
                  <el-tooltip effect="dark" :content="$i18n.t('removeCollection')" placement="top">
                    <img @click.stop="handleSubMenuDelete(v)" class="custom-delete" :src="require('./images/yichu.png')"/>
                  </el-tooltip>
                </span>
              </div>
						</div>
          </el-menu-item>
				</el-sub-menu>
			</template>
    </el-menu>
  </div>
</template>

<script lang="ts">
import debounce from '@/common/debounce'
import { PATH } from '@/router/constant'
import { GROUP_TYPE } from '@/views/ApiList/constant'
import { PROTOCOL_TYPE } from "@/views/constant"
import { useStore } from 'vuex'
import { defineComponent, reactive, watch, computed, toRefs, ref, nextTick, onMounted, onBeforeUpdate } from "vue"
import { useRoute, useRouter } from 'vue-router'
import * as utils from "@/utils"

export default defineComponent({
  emits:[], // 不会将方法绑定到子组件的根节点上，避免触发多次
  setup(props, ctx){
    const store = useStore();
    const router = useRouter()
    const currentRoute = useRoute()
    const menuRef = ref(null)
    let hasInitOpen = ref(false)
    let timer = ref(null)
    const state = reactive({
      type: GROUP_TYPE.API,
      activeGroupID: 0,
      activeItem: '',
      groupComp: computed(()=> store.getters.groupComp),
      groupID:computed(()=> store.getters.groupID),
      indexGroupID:computed(()=> store.getters.indexGroupID),
      groupType:computed(()=> store.getters.groupType),
      group_type: GROUP_TYPE,
      protocolType: PROTOCOL_TYPE,
      groupList: computed(()=> {
        if (state.groupType === GROUP_TYPE.API) {
          return store.getters.groupList
        } else if (state.groupType === GROUP_TYPE.INDEX) {
          return store.getters.indexGroupList
        }
        return []
      })
    })

    //method

    const renderMenuItem = (groupID) => {
      if (state.type === GROUP_TYPE.API && state.groupComp.subMenu && state.groupComp.subMenu[groupID]) {
        return state.groupComp.subMenu[groupID]
      } else if (state.type === GROUP_TYPE.INDEX && state.groupComp.indexSubMenu && state.groupComp.indexSubMenu[groupID]) {
        return state.groupComp.indexSubMenu[groupID]
      }
      return []
    }
    const handleShowNum = (groupID) => {
      if (state.type === GROUP_TYPE.API && state.groupComp.subMenu && state.groupComp.subMenu[groupID] && state.groupComp.subMenu[groupID].length) {
        return state.groupComp.subMenu[groupID].length
      } else if (state.type === GROUP_TYPE.INDEX && state.groupComp.indexSubMenu && state.groupComp.indexSubMenu[groupID] && state.groupComp.indexSubMenu[groupID].length) {
        return state.groupComp.indexSubMenu[groupID].length
      }
      return 0
    }
    const handleAddApi = (item) => {
      if (currentRoute.path !== PATH.ADD_API) {
        store.dispatch('apilist/changeGroupId', item.groupID)
        store.dispatch('apilist/changeGroupDesc', item.groupDesc)
        store.dispatch('apilist.add/resetHttpParam')
        store.dispatch('apilist.add/resetDubboParam')
        store.dispatch('apilist.add/resetGatewayParam')
        store.dispatch('apilist.add/changeStep', 1)
        store.dispatch('apilist.add/changeDubboNewAdd', false)
        store.dispatch('apilist.add/changeHttpNewAdd', false)
        router.push({ path: PATH.ADD_API, query: { projectID: utils.getQuery('projectID') } })
      }
    }
    const handleClose = (groupID) => {
      store.dispatch('apilist.group/changeGroupComp', { defaultOpeneds: [] })
      handleSelectGroup(groupID)
    }
    const handleOpen = (groupID) => {
      // 点击分组
      if (groupID !== "000") {
        handleSelectGroup(groupID, hasInitOpen.value)
        store.dispatch('apilist.group/changeGroupComp', { defaultOpeneds: [`${groupID}`] })
      }
    }
    const handleSelect = (key, keyPath) => {
      // 点击全部 一级分组id  二级为index
      if (key !== '000') {
        if (key.indexOf(',') === -1) {
          store.dispatch('apilist.group/changeGroupComp', { defaultOpeneds: [] })
          handleSelectGroup(key)
        } else {
          store.dispatch('apilist/changeEditDetail', false)
          let obj = state.groupComp[state.groupType === GROUP_TYPE.API ? 'subMenu' : 'indexSubMenu'][key.split(',')[0]][key.split(',')[1]]
          let projectID = utils.getQuery('projectID')
          let query = {
            projectID,
            apiID: obj.apiID,
            apiProtocol: obj.apiProtocol
          }
          if (obj.projectID !== Number(projectID)) {
            query = Object.assign({}, query, {indexProjectID: obj.projectID})
          }
          router.push({ path: PATH.API_DETAIL, query: query })
        }
      }
    }
    const handleSelectGroup = (groupID, jump = true) => {
      ctx.emit('handleSelectGroup', Number(groupID), jump)
    }
    const handleSearch = debounce(function (val) {
      let isApi = state.type === GROUP_TYPE.API
      if (state.groupComp.curPath !== PATH.API) {
        router.push({ path: PATH.API, query: { projectID: utils.getQuery('projectID') } })
      }
      let defaultOpeneds = []
      if (val) {
        let newObj = {}
        let indexNewObj = {}
        Object.keys(state.groupComp.initSubMenu).forEach(v => {
          newObj[v] = state.groupComp.initSubMenu[v].filter(item => ((item.apiName.indexOf(val) !== -1) || (item.apiURI.indexOf(val) !== -1)))
        })
        Object.keys(state.groupComp.indexInitSubMenu).forEach(v => {
          indexNewObj[v] = state.groupComp.indexInitSubMenu[v].filter(item => ((item.apiName.indexOf(val) !== -1) || (item.apiURI.indexOf(val) !== -1)))
        })
        defaultOpeneds = isApi ? Object.keys(newObj) : Object.keys(indexNewObj)
        store.dispatch('apilist.group/changeGroupComp', {
          uniqueOpened: false,
          defaultOpeneds,
          subMenu: newObj,
          indexSubMenu: indexNewObj
        })
      } else {
        defaultOpeneds = [`${state.groupID}`];
        store.dispatch('apilist.group/changeGroupComp', {
          uniqueOpened: true,
          defaultOpeneds,
          subMenu: state.groupComp.initSubMenu,
          indexSubMenu: state.groupComp.indexInitSubMenu
        })
      }

      let allGroup = state.groupList.map(v => v.groupID)
      let closeGroups = allGroup.filter(v => !defaultOpeneds.includes(`${v}`))
      nextTick(() => {
        closeGroups.forEach(v => {
          menuRef.value?.close(`${v}`)
        })
        defaultOpeneds.forEach(v => {
          menuRef.value?.open(v)
        })
      })
    }, 300, false)
    const handleAdd = () => {
      ctx.emit('handleAdd')
    }
    const handleClearCommand = () => {
      ctx.emit('handleCustomClick')
    }
    const handleGroupImport = (item) => {
      router.push({ path: PATH.IMPORT_INDEX, query: { projectID: utils.getQuery('projectID'), indexId: item.groupID } })
    }
    const handleGroupDelete = (data) => {
      ctx.emit('handleGroupDelete', data)
    }
    const handleGroupEdit = (data) => {
      ctx.emit('handleGroupEdit', data)
    }
    const handleSubMenuDelete = (item) => {
      ctx.emit('handleSubMenuDelete', item)
    }
    const handleCommand = ({ handleName, item }) => {
      ctx.emit(handleName, item)
    }
    const handleCopy = (item) => {
      ctx.emit('handleCopy', item)
    }


    // watch
    watch(() => state.groupComp.searchWord, (val) =>{
      handleSearch(val)
    })

    watch(() => currentRoute.path, (val) =>{
      store.dispatch('apilist.group/changeGroupComp', { curPath: val })
    },{
      immediate: true
    })

    let stopInitOpeneds = watch([
      () => state.groupComp.defaultOpeneds, 
      () => state.groupList,
      () => state.activeGroupID
      ], ([defaultOpeneds, list, id]) => {
        if (currentRoute.path === PATH.API_DETAIL) {
          clearTimeout(timer)
          timer = setTimeout(() => {
            if (defaultOpeneds.length && list.length){
              nextTick(() => {
                defaultOpeneds.forEach(v => {
                  menuRef.value?.open(v)
                })
                hasInitOpen.value = true
                stopInitOpeneds()
              })
            } else {
              stopInitOpeneds?.()
              hasInitOpen.value = true
            }
          }, 1000);
        } else if (id && list.length){
          nextTick(() => {
            menuRef.value?.open(id)
            hasInitOpen.value = true
            stopInitOpeneds()
          })
        }
    })

    watch(() => currentRoute.query, () =>{
        state.activeItem = utils.getQuery('apiID') as string
    },{
      immediate: true
    })


    watch(() => state.activeGroupID, (val) =>{
      clearTimeout(timer)
      if (hasInitOpen.value && val && val>0) {
        nextTick(() => {
          menuRef.value?.open(val)
        })
      }
    },{
      immediate: true
    })

    watch(() => state.groupType, (val) =>{
      state.type = val
      if (val === GROUP_TYPE.API) {
        state.activeGroupID = state.groupID
      } else {
        state.activeGroupID = state.indexGroupID
      }
      
      if (state.groupComp.searchWord) {
        store.dispatch('apilist.group/changeGroupComp', {
          defaultOpeneds: val === GROUP_TYPE.API ? Object.keys(state.groupComp.subMenu) : Object.keys(state.groupComp.indexSubMenu)
        })
      }
    },{
      immediate: true
    })

    watch(() => state.groupID, (val) => {
      if (state.groupType === GROUP_TYPE.API) {
        state.activeGroupID = val
      }
    })

    watch(() => state.indexGroupID, (val) => {
      if (state.groupType === GROUP_TYPE.INDEX) {
        state.activeGroupID = val
      }
    })

    return {
      menuRef,
      ...toRefs(state),
      handleCopy,
      handleCommand,
      handleSubMenuDelete,
      handleGroupEdit,
      handleGroupDelete,
      handleGroupImport,
      handleClearCommand,
      handleAdd,
      handleSelect,
      handleOpen,
      handleClose,
      handleAddApi,
      handleShowNum,
      renderMenuItem
    }
  }
})
</script>

<style scoped>
.api-group-container {
	background: #fff;
	position: absolute;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
	overflow-y: auto;
	border-right: solid 1px #e6e6e6;
	z-index: 7;
}
.api-group-container .group-type-wrap {
	height: 46px;
	border-bottom: 1px solid rgba(233, 233, 233, 1);
}
.api-group-container .group-type-wrap ul{
	display: flex;
	align-items: center;
	justify-content: flex-start;
	user-select: none;
}
.api-group-container .group-type-wrap ul li {
	width: 50%;
	cursor: pointer;
	font-size: 14px;
	color: rgba(0, 0, 0, 0.64);
	line-height: 46px;
	text-align: center;
}
.api-group-container .group-type-wrap ul li.active {
	color: #1890FF;
	font-weight: 650;
	position: relative;
}
.api-group-container .group-type-wrap ul li.active::after{
	position: absolute;
	bottom: 0;
	content: '';
	left: 0;
	right: 0;
	height: 2px;
	background: #1890FF;
}
.api-group-container::-webkit-scrollbar{
	display: none;
}
.api-group-container .el-menu {
	border: none;
}
.api-group-container .el-menu-item.group-all {
	background: rgba(249, 249, 249, 1);
	border-bottom: 1px solid rgba(233, 233, 233, 1);
	height: auto;
	padding: 5px 10px 12px !important;
	cursor: auto;
  display: block;
}
.api-group-container .el-menu-item {
	height: 46px;
	line-height: 46px;
}
.api-group-container .el-menu-item.is-active {
	color: #303133;
}
.api-group-container .el-menu-item.isActive {
	background: rgba(230, 247, 255, 1);
	color: #108EE9;
}
.api-group-container .el-menu-item.isActive:focus{
	background: rgba(230, 247, 255, 1) !important;
}
.api-group-container .el-menu-item.group-all .group-all-search-btn{
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding-top: 4px;
}
.api-group-container .group-name {
	width: 100%;
	display: flex;
	align-items: center;
	justify-content: space-between;
}
.api-group-container .el-menu-item.select-all,
.api-group-container .select-group >>> .el-sub-menu__title {
	background-color: rgba(230, 247, 255, 1);
	border-right: 2px solid rgba(16, 142, 233, 1);
	color: #108EE9;
}
.api-group-container .select-group:deep() .el-sub-menu__title .el-sub-menu__icon-arrow {
  margin-top: -6px;
}
.api-group-container .el-menu-item.all-group {
	height: 56px;
	line-height: 56px;
}
.api-group-container >>> .el-sub-menu__title .el-submenu__icon-arrow {
	margin-top: -6px;
}
.api-group-container >>> .el-sub-menu__title:hover .el-dropdown-link{
	display: block;
}
.api-group-container .group-name >>> .el-dropdown-link{
	vertical-align: baseline;
	margin-right: 20px;
	display: none;
}
.api-group-container .group-name >>> .el-dropdown-link .custom-delete {
  display: inline-block;
  width: 14px;
  height: 14px;
  vertical-align: middle;
}
.api-group-container .group-name >>> .el-dropdown-link i {
  margin: 0 3px;
}
.api-group-container .group-name .group-name-item{
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
	padding-right: 20px;
}
.api-group-container .group-name .api-item-operate {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
.api-group-container .group-name .api-item-operate >>> .el-dropdown-link.copy{
  margin-right: 0;
}
.api-group-container .el-menu-item.group-all .group-all-search-btn >>> .el-switch__label--right.is-active {
	color: #13ce66;
}
.api-group-container .sec-menu-item {
  padding-right: 0;
}
.api-group-container .sec-menu-item:hover .el-dropdown-link{
	display: block;
}
</style>>
