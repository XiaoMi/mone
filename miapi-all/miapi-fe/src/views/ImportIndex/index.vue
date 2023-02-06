<template>
	<div class="import-wrap">
		<el-tabs v-model="activeName">
			<el-tab-pane :label="`${$i18n.t('importApi.optional')} (${canSelectNum})`" name="optional">
				<div class="import-api-content">
					<Optional 
            ref="optionalRef" 
            :selectAllApiIds="selectAllApiIds"
            @changeState="changeState"
            :otherArr="otherArr" 
            @changeCanSelectNum="changeCanSelectNum" 
            @changeOther="changeOther" 
            @changeHasSelectd="changeHasSelectd"/>
				</div>
			</el-tab-pane>
			<el-tab-pane :label="`${$i18n.t('importApi.selected')} (${selectApiIds.length})`" name="hasSelect">
				<div class="import-api-content">
					<HasSelected  @changeOptional="changeOptional" :hasSelectd="hasSelectd"/>
				</div>
			</el-tab-pane>
		</el-tabs>
	</div>
</template>
<script>
import Optional from './Optional'
import HasSelected from './HasSelected'
import { mapGetters } from 'vuex'
import { getProjectListByProjectGroupId, getMyProjects } from '@/api/main'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import { ref, reactive, toRefs, computed, watch } from "vue"
import { useStore } from "vuex"
import { deRepeat, getQuery } from "@/utils"

export default {
  name: 'ImportIndex',
  components: {
    Optional,
    HasSelected
  },
  setup(){
    const store = useStore();
    const optionalRef=ref('');

    const projectList = computed(() => store.getters.projectList)
    const selectApiIds = computed(() => store.getters.selectApiIds)

    const state = reactive({
      activeName: 'optional',
      hasSelectd: [],
      optionalArr: [],
      otherArr: [],
      canSelectNum: 0,
      selectAllApiIds: [], // 所有选中的api
      selectGroups: [], // 选中api所在的组ids
      hasChange: false,
    })

    if (!projectList || !projectList.length) {
      let groupId = window.localStorage.getItem('groupId')
      if (groupId === '999') {
        getMyProjects().then((data) => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            store.dispatch('projectlist/changeList', data.data.myAdmin || [])
          }
        }).catch(e => {})
      } else {
        getProjectListByProjectGroupId({ projectGroupID: groupId }).then(data => {
          if (data.message === AJAX_SUCCESS_MESSAGE) {
            store.dispatch('projectlist/changeList', data.data)
          }
        }).catch(e => {})
      }
    }

    watch(() => state.activeName, (val) => {
      if (val === 'optional' && state.hasChange) {
        state.hasChange = false
        let ids = state.optionalArr.filter(v => v.apiID)
        state.otherArr = state.otherArr.filter(v => ids.includes(v.apiID))
        optionalRef.value.handlerRefresh(state.optionalArr)
      }
    })

    watch(() => [state.hasSelectd.value], () => {
      let arr = deRepeat(state.hasSelectd, "apiID").map(v => v.apiID)
      store.dispatch('apilist.import/changeSelectApiIds', arr)
    })


    const changeHasSelectd = (arr) => {
      arr = deRepeat(arr.concat(state.otherArr), "apiID")
      state.hasSelectd = arr
    }
    const changeOptional = (arr) => {
      state.hasChange = true
      state.optionalArr = [].concat(arr)
    }
    const changeOther = (arr) => {
      state.otherArr = [].concat(arr)
    }
    const changeCanSelectNum = (num) => {
      state.canSelectNum = num || 0
    }
    const changeState = (key, val) => {
      state[key] = val
    }
    return {
      optionalRef,
      changeState,
      changeHasSelectd,
      changeOptional,
      changeOther,
      changeCanSelectNum,
      selectApiIds,
      ...toRefs(state)
    }
  }
}
</script>

<style scoped>
.import-wrap{
	background: #fff;
	margin: 20px;
	border-radius: 4px 4px 0 0;
}
.import-wrap >>> .el-tabs__item {
	height: 46px;
	line-height: 46px;
	width: 140px;
	padding: 0;
	text-align: center;
	font-size: 14px;
}
.import-wrap >>> .el-tabs__nav {
	margin: 0 24px;
}
.import-wrap >>> .el-tabs__nav-wrap::after{
	height: 1px;
}
.import-wrap .import-api-content {
	padding: 0 20px;
	height: calc(100vh - 194px);
	overflow-y: auto;
}
</style>
