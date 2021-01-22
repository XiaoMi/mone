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
    <d2-container>
      <div class="personal">
        <div class="personal-head">
          个人账户：
        </div>
        <div class="personal-main">
          <div class="personal-main-left">
           <img :src="personalDetail.avatar" alt="">
          </div>
          <div class="personal-main-right">
               <div class="personal-username strip">
          <div class="key">用户名：</div>
          <div class="value">{{ personalDetail.username }}</div>
        </div>
        <div class="personal-name strip">
          <div class="key">姓名：</div>
          <div class="value">{{ personalDetail.name }}</div>
        </div>
        <div class="personal-role strip">
          <div class="key">角色：</div>
          <div class="value">{{ personalDetail.roleDisplay }}</div>
        </div>
        <div class="persoanl-group strip">
          <div class="key">所属分组：</div>
          <div class="value">
            <el-tag class="tags" v-for="group in personalDetail.gidInfos" :key="group.id">
              {{group.name}}
            </el-tag>
          </div>
        </div>
        <div class="personal-email strip">
          <div class="key">Email：</div>
          <div class="value">{{ personalDetail.email }}</div>
        </div>
        <div class="personal-tel strip">
          <div class="key">电话：</div>
          <div class="value">{{ personalDetail.phone }}</div>
        </div>
          </div>
        </div>
     
        <div class="personal-footer">
           <el-divider content-position="right">
                  <div style="margin-bottom:2px"> 
                    Mone不支持编辑，若需修改请联系
                    <a :href="url" style="color:#409EFF" title="点击直达Hermes权限管理系统">Hermes权限管理系统</a>
                  </div>
                  <a href="mailto:zhangxiuhua@xxxx.com" class="admin-mailto" title="发送邮件">管理员邮箱：zhangxiuhua@xxxx.com</a>
            </el-divider>
        </div>
      </div>
    </d2-container>
</template>

<script>
import service from '@/plugin/axios/index'
import bizutil from '@/common/bizutil'
import qs from 'qs'
import jsMd5 from "js-md5"


export default {
  name: 'settings',
  data () {
    return {
      tableData: [],
      editFormVisible: false,
      formEdit: {},
      personalDetail: {}
    }
  },
  computed:{
    url:function(){
      let isDev=process.env.NODE_ENV === 'development';
      //return ?``:``;
      return `http://${isDev?'st.':''}xxxx/#/userConfig`
    },
    
  },
  mounted: function () {
    this.getOwnerList();
  },
  methods: {
    getOwnerList () {
      service({
        url: '/account/own',
        method: 'GET'
      }).then(res => {
        let accounts = [res];
        this.tableData = bizutil.accountProcess(accounts);
        this.personalDetail = this.tableData[0];
        let usernameMd5 = jsMd5(this.personalDetail.username);
        this.personalDetail.avatar = `https://mier.mi.com/user/getAvatarurlnocheck?user_name=${this.personalDetail.username}&checkSign=${usernameMd5}`
      })
    },
    // 编辑
    editFormRow (row) {
      this.editFormVisible = true;
      this.formEdit = { ...row };
    },
    // 编辑 -> 提交
    submitEditForm (formName) {
      this.$refs[formName].validate((valid) => {
        if (!valid) {
          this.$message({
            message: '请检查参数',
            type: 'warning',
          })
          return false;
        }
        this.editFormVisible = false;
        service({
          url: '/account/update',
          method: 'POST',
          data: this.formEdit,
        }).then( res => {
          this.formEdit = {};
          this.$message({
            message: '编辑成功',
            type: 'success',
          });
          this.getOwnerList();
        }).catch( () => {
          this.$message({
            type: 'warning',
            message: '请求出错：/account/update',
          })
        })
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.personal {
    width: 900px;
    border: 1px solid #eee;
    padding: 16px;
    box-shadow: rgba(0, 0, 0, 0.12) 0px 2px 4px, rgba(0, 0, 0, 0.04) 0px 0px 6px;
    margin: 80px auto;
    position: relative;
    background: #f8f8f8;
    &-top {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        background-color: #409EFF;
        height: 3px;
        border-top-left-radius: 8px;
        border-top-right-radius: 8px;
    }
    &-head {
        font-size: 16px;
        color: #909399;
        font-weight: bold;
        margin-bottom: 16px;
        margin-left: 3px;
    }
    &-footer {
      .el-divider__text {
          background: #f8f8f8;
          padding: 0 8px;
          right: 25px;
          font-size: 14px;
          color: #909399;
          font-weight: bold;
      }
    }
    .strip {
        padding: 10px;
        background: #fff;
        margin-bottom: 10px;
        border-radius: 8px;
    }
    .key {
        font-size: 14px;
        color: #909399;
        font-weight: bold;
        margin-bottom: 0px;
        display: inline-block;
        width: 70px;
        margin-right: 40px;
    }
    .value {
         font-size: 14px;
        color: #909399;
        font-weight: bold;
        margin-bottom: 0px;
        display: inline-block;
    }
}
.admin-mailto {
  color: #909399; 
  margin-left: 102px; 
  margin-top: 5px
}
.admin-mailto:hover {
  color: #409EFF;
}
.tags{
  margin-right:10px;
}
.personal-main{
  position: relative;
  background: #fff;
  display: flex;
  .personal-main-left{
    width: 400px;
    height: 400px;
  }
  .personal-main-right{
    margin-left: 10px;
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content:flex-end;
  }
}
</style>