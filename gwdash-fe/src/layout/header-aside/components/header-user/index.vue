<!--
  Copyright 2020 Xiaomi

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  -->

<template>
  <div style="display:flex; align-items:center">
    <div class="name">{{info.name}}</div>
    <el-dropdown size="small" class="d2-mr">
      <div class="user">
        <div class="head-image">
            <img :src="miniedAvatar" alt="">
          </div>
        <div class="el-icon-caret-bottom el-icon-arrow-down user-dropdown-array" style="color: #F1F1F1;"></div>
      </div>
      <el-dropdown-menu slot="dropdown" v-if='aside.length !== 0'>
        <el-dropdown-item @click.native="toAuditList">
          <d2-icon name="bookmark" class="d2-mr-5"/>
          我的审核
        </el-dropdown-item>
        <el-dropdown-item @click.native="generatorToken">
          <d2-icon name="cube" class="d2-mr-5"/>
          生成 token
        </el-dropdown-item>
        <el-dropdown-item @click.native="tofilterAudit">
          <d2-icon name="filter" class="d2-mr-5"/>
          filter 审核
        </el-dropdown-item>
        <el-dropdown-item @click.native="toUserCenter">
          <d2-icon name="user-circle-o" class="d2-mr-5"/>
          个人中心
        </el-dropdown-item>
        <el-dropdown-item @click.native="toGitToken">
          <d2-icon name="gitlab" class="d2-mr-5"/>
          gitlab token
        </el-dropdown-item>
        <el-dropdown-item >
          <d2-icon name="terminal" class="d2-mr-5"/>
          <router-link
            style="color: inherit"
            :to="{path: `/sre/agent/ssh`}"
          >远程控制台(Beta)</router-link>
        </el-dropdown-item>
        <el-dropdown-item v-if="isSuperRole">
          <d2-icon name="cog" class="d2-mr-5"/>
          <router-link
            style="color: inherit"
            :to="{path: `/config/feature`}"
          >功能开关</router-link>
        </el-dropdown-item>
        <el-dropdown-item v-if="isSuperRole">
          <d2-icon name="compass" class="d2-mr-5"/>
          <router-link
            style="color: inherit"
            :to="{path: `/config/menu`}"
          >菜单配置</router-link>
        </el-dropdown-item>
        <!-- <el-dropdown-item v-if='isTester' @click.native="toTestPage">
          <d2-icon name="text-width" class="d2-mr-5"/>
          测试人员管理
        </el-dropdown-item> -->
        <el-dropdown-item @click.native="logOff">
          <d2-icon name="power-off" class="d2-mr-5"/>
          退出
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex'
import service from '@/plugin/axios/index'
import jsMd5 from "js-md5"

export default {
  data () {
    return {
      isSuperRole: (((userInfo && userInfo.roles) || []).findIndex(it => it.name === 'SuperRole') !== -1)
    }
  },
  computed: {
    ...mapState('d2admin/user', [
      'info'
    ]),
    ...mapState('d2admin/menu', [
      'aside'
    ]),
    miniedAvatar: function () {
      if (!this.info.avatar) return ""
      return [this.info.avatar, "&", "width=72"].join("")
    }
  },
  methods: {
    ...mapActions('d2admin/account', [
      'logout'
    ]),
    logOff () {
      this.logout({
        vm: this,
        confirm: true
      })
    },
    generatorToken () {
      service({
        url: '/account/token',
        method: 'post',
        data: {
          id: this.info.uuid
        }
      }).then(token => {
        this.$alert(token, '每次生成一个新 token 请妥善保存', {
          confirmButtonText: '确定',
          callback: action => {
            this.$message({
              type: 'info',
              message: `action: ${action}`
            })
          }
        })
      })
    },
    toAuditList () {
      this.$router.push('/application/audit/list')
    },
    toUserCenter () {
      this.$router.push('/account/settings')
    },
    toGitToken () {
      this.$router.push('/account/gitlab')
    },
    tofilterAudit () {
      this.$router.push('/gateway/cfilter/audit/list')
    },
    toTestPage () {
      if (this.$route.path === '/test') return
      this.$router.push('/test')
    }
  }
}
</script>

<style lang="scss" scoped>
.user {
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  .head-image {
    margin-left: 10px;
    margin-right: 6px;
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #a3a3a3;
    color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    // background-image: url(xx_replace_xx);
    // background-repeat: no-repeat;
    // background-position: -155px -94px
    img{
      width: 100%;
      max-width: 100%;
    }
  }
  .drop {
    width: 8.2px;
    height: 4.5px;
    background-color: #F1F1F1;
  }
}
.name {
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #FFFFFF;
}
</style>
