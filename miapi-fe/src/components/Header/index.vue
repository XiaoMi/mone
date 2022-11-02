<template>
  <div class="navbar">
    <div class="navbar-left">
      <div @click="handleGoHome" class="system-title">
        <img src="./assets/logo.png" />
        <span>MI API</span>
      </div>
      <div class="cur-group">
        <div @click.stop="toggleFixedGroup" v-if="showProjectName">
          <span>{{projectDetail.projectName}}</span>
          <el-icon><Menu /></el-icon>
        </div>
      </div>
      <div class="router-list">
        <Sidebar/>
      </div>
    </div>
    <div class="right-menu">
      <!-- <el-popover
        placement="bottom"
        width="300"
        trigger="click">
        <Feedback/>
        <el-button style="margin:0 8px; color: #5a5e66" size="medium" type="text" slot="reference">意见与反馈</el-button>
      </el-popover> -->
      <el-tooltip effect="dark" placement="bottom">
        <template #content> <span style="color: orange; font-size: 14px">{{$i18n.t('router.browserTips')}}</span> </template>
        <div v-if="showBorwserTips" class="remind">
          <svg t="1656492493237" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2643" width="16" height="16"><path d="M616.2 887.4H385.5c-20.5 0-37.3 16.8-37.3 37.3S365 962 385.5 962h230.7c20.5 0 37.3-16.8 37.3-37.3s-16.7-37.3-37.3-37.3zM895 794.1h-23.9V511c0-198.8-141.6-361.5-315.3-363.5v-45.8c0-21.4-17.5-39-39-39-21.4 0-39 17.5-39 39v45.6h-1.7C300.9 147.3 157.5 311 157.5 511v283.2h-23.9c-12.9 0-23.4 10.5-23.4 23.4 0 12.9 10.5 23.4 23.4 23.4H895c12.9 0 23.4-10.5 23.4-23.4 0-13-10.5-23.5-23.4-23.5zM730.8 441c0-58.5-16.3-101.7-48.4-128.6-54.7-45.7-138-31.2-138.9-31.1l-7.1-39.2c4.1-0.8 102.7-17.8 171.5 39.7 41.6 34.8 62.7 88.3 62.7 159.2h-39.8z" fill="#ED7C6C" p-id="2644"></path></svg>
        </div>
      </el-tooltip>
      <el-tooltip v-model="showDocTips" effect="dark" :content="$i18n.t('router.useDocumentation')" placement="bottom">
        <a
          class="doc-icon"
          href="javascript:;"
          target="_blank"
        >
          <svg
            t="1653621828228"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="3514"
            width="16"
            height="16"
          >
            <path
              d="M463.99957 784.352211c0 26.509985 21.490445 48.00043 48.00043 48.00043s48.00043-21.490445 48.00043-48.00043c0-26.509985-21.490445-48.00043-48.00043-48.00043S463.99957 757.842226 463.99957 784.352211z"
              p-id="3515"
              fill="#8a8a8a"
            ></path>
            <path
              d="M512 960c-247.039484 0-448-200.960516-448-448S264.960516 64 512 64 960 264.960516 960 512 759.039484 960 512 960zM512 128.287273c-211.584464 0-383.712727 172.128262-383.712727 383.712727 0 211.551781 172.128262 383.712727 383.712727 383.712727 211.551781 0 383.712727-172.159226 383.712727-383.712727C895.712727 300.415536 723.551781 128.287273 512 128.287273z"
              p-id="3516"
              fill="#8a8a8a"
            ></path>
            <path
              d="M512 673.695256c-17.664722 0-32.00086-14.336138-32.00086-31.99914l0-54.112297c0-52.352533 39.999785-92.352318 75.32751-127.647359 25.887273-25.919957 52.67249-52.67249 52.67249-74.016718 0-53.343368-43.07206-96.735385-95.99914-96.735385-53.823303 0-95.99914 41.535923-95.99914 94.559333 0 17.664722-14.336138 31.99914-32.00086 31.99914s-32.00086-14.336138-32.00086-31.99914c0-87.423948 71.775299-158.559333 160.00086-158.559333s160.00086 72.095256 160.00086 160.735385c0 47.904099-36.32028 84.191695-71.424378 119.295794-27.839699 27.776052-56.575622 56.511974-56.575622 82.3356l0 54.112297C544.00086 659.328155 529.664722 673.695256 512 673.695256z"
              p-id="3517"
              fill="#8a8a8a"
            ></path>
          </svg>
        </a>
      </el-tooltip>
      <div class="user-info">
        <el-image
          class="user-avatar"
          :src="photo"
          :preview-src-list="[photo]">
        </el-image>
      </div>
      <div>
        <div class="avatar-wrapper">
          <el-dropdown placement="bottom" class="avatar-container right-menu-item hover-effect" trigger="hover">
            <div>
              <span class="user-name">{{selfUserInfo.name}}</span><el-icon :size="12"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="header-dropdown-list">
                <!-- <router-link to="/profile/index">
                  <el-dropdown-item>Profile</el-dropdown-item>
                </router-link>
                <router-link to="/">
                  <el-dropdown-item>Dashboard</el-dropdown-item>
                </router-link>
                <a target="_blank" href="https://github.com/PanJiaChen/vue-element-admin/">
                  <el-dropdown-item>Github</el-dropdown-item>
                </a>
                <a target="_blank" href="https://panjiachen.github.io/vue-element-admin-site/#/">
                  <el-dropdown-item>Docs</el-dropdown-item>
                </a> -->
                <el-dropdown-item>
                  <div class="header-item">
                    <img :src="Person"/>
                    <span style="display:block;" @click="handleSelf">{{$i18n.t('personalCenter')}}</span>
                  </div>
                </el-dropdown-item>
                <el-dropdown-item>
                  <div class="header-item">
                    <img :src="Out"/>
                    <span style="display:block;" @click="logout">{{$i18n.t('signOut')}}</span>
                  </div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
    <!-- <div class="language">
      <el-dropdown @command="handleCommand">
        <span class="el-dropdown-link">
          {{languageList[language]}}
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item v-for="item in Object.keys(languageList)" :key="item" :command="item">{{languageList[item]}}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div> -->
  </div>
</template>

<script lang="ts">
import { useStore } from 'vuex'
import { defineComponent, reactive, toRefs, computed, watch, onBeforeUnmount, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import { getUserInfo } from '@/api/projectuser'
import Sidebar from '@/components/Sidebar/index.vue'
import { PATH } from '@/router/constant'
import jsMd5 from 'js-md5'
import Feedback from '@/components/Feedback/index.vue'
import Person from './assets/person.png'
import Out from './assets/out.png'
import * as utils from "@/utils"
let timer = null

export default defineComponent({
  components: {
    Sidebar,
    Feedback,
  },
  setup(props, ctx){
    const store = useStore()
    const route = useRoute()
    const router = useRouter()
    const state= reactive({
      showProjectName: false,
      showTooltip: false,
      photo: '',
      showDocTips: false,
      languageList: {
        zh: '中文简体',
        en: 'English'
      },
      selfUserInfo: computed(() => store.getters.selfUserInfo),
      projectDetail: computed(() => store.getters.projectDetail),
      language: computed(() => store.getters.language),
      showFixedGroup: computed(() => store.getters.showFixedGroup),
      showBorwserTips: navigator.userAgent.indexOf('Chrome') === -1
    })

    // method
    const handleShowTip = () => {
      state.showTooltip = true
    }
    const handleGoHome = () => {
      router.push({ path: PATH.HOME })
    }
    const handleHideTip = () => {
      state.showTooltip = false
    }
    const toggleFixedGroup = () => {
      store.dispatch('projectlist/changeFixedGroup', !state.showFixedGroup)
    }
    const toggleSideBar = () => {
      store.dispatch('app/toggleSideBar')
    }
    const logout = () => {
      window.location.href = ``
    }
    const handleSelf = () => {
      router.push({ path: PATH.ACCOUNT })
    }
    const handleCommand = (command) => {
      window.localStorage.setItem('apiLocaleLanguage', command)
      window.localStorage.setItem('selectedLang', state.languageList[command])
      store.dispatch('permission/changeLanguage', command)
      window.location.reload()
    }

    // watch
    watch(()=>route, (val) => {
        if (val.meta.type === 'submenu') {
          state.showProjectName = true
        } else {
          state.showProjectName = false
          store.dispatch('projectlist/changeFixedGroup', false)
        }
    }, {
      immediate: true,
      deep: true
    })

    watch(()=>state.selfUserInfo.name, (val) => {
      if (val) {
        if (!utils.Cookie.get("docTips")) {
          state.showDocTips = true
          clearTimeout(timer)
          timer = setTimeout(() => {
            state.showDocTips = false
            utils.Cookie.set("docTips", 1, 365)
          }, 3000)
        }
      }
    })

    onBeforeUnmount(() => {
      clearTimeout(timer)
    })

    onMounted(() => {
      getUserInfo().then((data) => {
        if (data.message === 'success') {
          store.dispatch('users/changeSelfUser', data.data)
          state.photo = `xxxx`
        }
      }).then(() => {
        if (!utils.Cookie.get('operateTip')) {
          state.showTooltip = true
          setTimeout(() => {
            state.showTooltip = false
          }, 3000)
          utils.Cookie.set('operateTip', 1, 1)
        }
      }).catch(e => {})
    })

    return {
      Person,
      Out,
      handleGoHome,
      toggleFixedGroup,
      handleSelf,
      logout,
      ...toRefs(state)
    }
  }
})
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';
.navbar {
  user-select: none;
  height: $headerHeight;
  overflow: hidden;
  box-shadow: 0px 3px 5px rgba(170,170,170 , 0.27);
  background: #fff;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  white-space: nowrap;
  overflow-x: auto;
  &::-webkit-scrollbar {
    display: none;
  }
  .navbar-left {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    &:deep(.pro-name){
      font-size: 18px;
    }
  }
  .system-title {
    height: 100%;
    line-height: 50px;
    font-size: 20px;
    position: relative;
    padding: 0 20px 0 0;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: #1890FF;
    // &::after{
    //   position: absolute;
    //   right: 10px;
    //   top: 50%;
    //   margin-top: -10px;
    //   content: '';
    //   width: 1px;
    //   height: 20px;
    //   background: #ccc;
    // }
    img {
      width: 33px;
      height: 26px;
      margin-right: 8px;
    }
  }
  .cur-group{
    display: flex;
    align-items: center;
    justify-content: center;
    height: $headerHeight;
    margin-right: 16px;
    >div{
      color: #fff;
      background: #1890FF;
      cursor: pointer;
      height: 32px;
      line-height: 32px;
       font-size: 14px;
       display: flex;
       align-items: center;
       justify-content: center;
       padding: 0 10px;
       border-radius: 2px;
      >span{
       display: inline-block;
       margin-right: 10px;
      }
    }
  }
  .router-list {
    min-width: 594px;
  }
  .language {
    height: 100%;
    display: flex;
    align-items: center;
    padding-right: 20px;
    .el-dropdown {
      cursor: pointer;
    }
    .el-dropdown-link {
      i {
        font-size: 12px;
        color: #1890FF;
      }
    }
  }
  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    height: 100%;
    line-height: 50px;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    &:focus {
      outline: none;
    }

    .remind {
      margin-right: 46px;
      display: flex;
      align-items: center;
      cursor: pointer;
      svg{
        animation: remind 1.5s 3;
      }
    }

    @keyframes remind{
      0% {
        transform: scale(1);
      }
      50% {
        transform: scale(2);
      }
      100% {
        transform: scale(1);
      }
    }

    .doc-icon {
      display: flex;
      align-items: center;
      margin-right: 8px;
    }

    .user-info {
      display: flex;
      align-items: center;
      .user-avatar {
        cursor: pointer;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        border: 1px solid #E9E9E9;
      }
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;
      }
    }
    .avatar-wrapper {
      padding: 0;
      margin-left: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
      .avatar-container {
        height: 20px;
        line-height: 20px;
      }
      .user-name {
        font-size: 14px;
        margin-right: 6px;
      }
    }
  }
}
.header-dropdown-list {
  .header-item {
    display: flex;
    align-items: center;
    justify-content: flex-start;
    padding: 4px 0 2px;
    img {
      width: 12px;
      height: 12px;
      display: inline-block;
      margin-right: 4px;
    }
  }
}
</style>
