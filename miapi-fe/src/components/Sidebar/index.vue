<template>
  <div class="menu-container">
    <el-menu
      :default-active="currentActive"
      :collapse="isCollapse"
      :unique-opened="false"
      :collapse-transition="false"
      :ellipsis="false"
      router
      mode="horizontal"
    >
      <SidebarItem :projectID="projectID" v-for="route in permission_routes" :key="route.path" :item="route" :base-path="route.path" />
    </el-menu>
  </div>
</template>

<script lang="ts">
import { mapGetters, useStore} from 'vuex'
import SidebarItem from './SidebarItem.vue'
import variables from '@/styles/variables.scss'
import { useRoute } from "vue-router"
import { routes } from '@/router'
import * as utils from "@/utils"
import { defineComponent, reactive, toRefs, onMounted, computed, watch, onBeforeUpdate } from "vue"


export default defineComponent({
  components: { SidebarItem },
  setup(){
    const store = useStore()
    const route = useRoute()
    const state = reactive({
      projectID: -1,
      currentActive: '',
      permission_routes: routes,
      sidebar: computed(() => store.getters.sidebar),
      variables,
      isCollapse: computed(() => !state.sidebar.opened),
    })

    const activeMenu = () => {
      const { meta, path, fullPath } = route
      if (meta.activeMenu) {
        window.sessionStorage.setItem('currentActive', path)
        state.currentActive = path
      }
      return state.currentActive || window.sessionStorage.getItem('currentActive')
    }

    onMounted(() => {
      store.dispatch('tagsView/addView', route)
    })

    watch(() => route.query, () => {
      let id = utils.getQuery('projectID')
      state.projectID = id ? Number(id) : 0
      activeMenu()
    },{
      immediate: true
    })

    return {
      ...toRefs(state)
    }
  }
})
</script>

<style lang="scss">
@import '@/styles/variables.scss';
.menu-container {
  position: relative;
  user-select: none;
  display: flex;
  .el-menu--horizontal>.el-menu-item.is-active{
    border-bottom-color: transparent !important;
  }
  .el-menu{
    border: none !important;
  }
}
</style>
