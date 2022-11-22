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
    <div class="name">{{userInfo.name}}</div>
    <el-dropdown size="small" class="d2-mr">
      <div class="user">
        <div class="head-image">
            <img :src="avatarImg" alt="">
          </div>
        <div class="el-icon-caret-bottom el-icon-arrow-down user-dropdown-array" style="color: #F1F1F1;"></div>
      </div>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item v-if="isSuperRole">
          <d2-icon name="cog" class="d2-mr-5"/>
          <router-link
            style="color: inherit"
            :to="{path: `/config/custom`}"
          >自定义配置</router-link>
        </el-dropdown-item>
        <el-dropdown-item>
          <a style="color:red"
            :href="`https://${domain}`">
            <d2-icon name="power-off" class="d2-mr-5"/>退出
          </a>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import service from '@/plugin/axios'

export default {
  data () {
    return {
      domain: document.domain
    }
  },
  computed: {
    ...mapState('d2admin/dealUserInfo', ['userInfo', 'avatarImg', 'isSuperRole']),
    ...mapState('d2admin/menu', [
      'aside'
    ])
  },
  methods: {
    generatorToken () {
      service({
        url: '/account/token',
        method: 'post',
        data: {
          id: this.userInfo.uuid
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
      this.$router.push('/plugin/audit/list')
    },
    toUserCenter () {
      this.$router.push('/account/settings')
    },
    toGitToken () {
      this.$router.push('/account/gitlab')
    },
    tofilterAudit () {
      this.$router.push('/cfilter/audit/list')
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
  font-size: 12px;
  .head-image {
    margin-left: 10px;
    margin-right: 6px;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: #a3a3a3;
    color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    border: 1px solid #e5e5e5;
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
  color: #606266;
  margin-left: 32px;
}
</style>
