<template>
  <div class="app-header">
    <div class="logo">
      <i class="iconfont icon-permission-quanxianguanli"></i>
      <span class="title">Tpc</span>
    </div>
    <div class="more">
      <el-button type="text" @click="logoutClick">退出</el-button>
    </div>
  </div>
</template>
<script>
import { logoutReq } from '@/common/service/list/user-manage';

export default {
  methods: {
    logoutClick() {
      logoutReq().then(() => {
        const redirectUrl = window.location.href;
        this.$router.push({
          name: 'UserLogin',
          query: {
            redirectUrl,
          },
        });
      });
    },
  },
};
</script>
<style lang="less" scoped>
.app-header{
  position: relative;
  z-index: 1;
  padding: 0 40px;
  height: 55px;
  background: #fff;
  box-shadow: 0px 3px 5px rgb(170 170 170 / 27%);
  color: #1a3d60;
  display: flex;
  justify-content: space-between;
  .title{
    height: 100%;
    text-align: left;
    line-height: 55px;
    margin-left: 10px;
    font-size: 20px;
  }
  .icon-permission-quanxianguanli{
    font-size: 25px;
    vertical-align: middle;
  }
}
.more{
  display: flex;
  align-items: center;
}
</style>
