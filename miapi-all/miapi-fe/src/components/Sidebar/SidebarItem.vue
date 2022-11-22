<template>
  <el-menu-item 
    v-if="isShow && item.meta.title" 
    :index="resolvePath(onlyOneChild.path)" 
    :route="resolveRoute(onlyOneChild.path)" 
    :class="{'submenu-title-noDropdown':!isNest, 'header-list': true}"
  >
    <span>{{onlyOneChild.meta.title}}</span>
  </el-menu-item>
</template>

<script lang="ts">
import path from 'path'
import { isExternal } from '@/utils/validate'
import { PATH } from "@/router/constant"
import { defineComponent, reactive, toRefs, onBeforeMount, watch } from "vue"

export default defineComponent({
  props: {
    // route object
    item: {
      type: Object,
      required: true
    },
    isNest: {
      type: Boolean,
      default: false
    },
    basePath: {
      type: String,
      default: ''
    },
    projectID: {
      type: Number,
      default: 0
    }
  },
  setup(props, ctx){
    let isShow = true
    if (props.item.meta && props.item.meta.type === 'submenu') {
      isShow = !!props.projectID && !props.item.hidden
    } else if (props.item.meta && props.item.meta.type === 'menu') {
      isShow = !props.projectID && !props.item.hidden
    } else {
      // 首页
      isShow = !props.item.hidden
    }

    const state = reactive({
      isShow,
      onlyOneChild: null
    })

    //method
    const hasOneShowingChild = (children = [], parent) => {
      const showingChildren = children.filter(item => {
        if (item.hidden) {
          return false
        } else {
          state.onlyOneChild = item
          return true
        }
      })


      if (showingChildren.length === 1) {
        return true
      }

      if (showingChildren.length === 0) {
        state.onlyOneChild = { ...parent, path: '', noShowingChildren: true }
        return true
      }

      return false
    }
    const resolvePath = (routePath) => {
      if (isExternal(routePath)) {
        return routePath
      }
      if (isExternal(props.basePath)) {
        return props.basePath
      }
      return path.resolve(props.basePath, routePath)
    }
    const resolveRoute = (routePath) => {
      let o = {path: path.resolve(props.basePath, routePath), query: {}}

      if (props.projectID && props.projectID > 0) {
        o.query = {
          projectID: props.projectID
        }
      }
      if (isExternal(routePath)) {
        o.path = routePath
      }else if (isExternal(props.basePath)) {
        o.path = props.basePath
      } else if(routePath === PATH.HOME){
        o = {path: '/', query: {}}
      }
      return o
    }

    watch(() => props.projectID, (newValue) =>{
      if (props.item.meta && props.item.meta.type === 'submenu') {
        state.isShow = !!newValue && !props.item.hidden
      } else if (props.item.meta && props.item.meta.type === 'menu') {
        state.isShow = !newValue && !props.item.hidden
      } else {
        state.isShow = !props.item.hidden
      }
    })

    onBeforeMount(() => {
      hasOneShowingChild(props.item.children, props.item)
    })

    return {
      resolvePath,
      resolveRoute,
      ...toRefs(state)
    }
  }
})
</script>

<style lang="scss">
@import '@/styles/variables.scss';
  .header-list.el-menu-item{
    height: $headerHeight !important;
    line-height: $headerHeight !important;
    padding: 0 16px !important;
    min-width: 99px;
    text-align: center;
    &:focus{
      background: #fff !important;
    }
  }
  .header-list.el-menu-item, .header-list .el-sub-menu__title {
    &:hover {
      background-color: rgba(230, 247, 255, 1) !important;
      color: #108EE9 !important;
    }
    span{
      color: #555555;
      font-size: 16px;
      &:hover {
        color: #108EE9 !important;
      }
    }
    &.is-active {
      span {
        color: #108EE9 !important;
      }
    }
  }
</style>
