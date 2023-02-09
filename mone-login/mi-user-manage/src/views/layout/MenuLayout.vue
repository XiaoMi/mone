<template lang="">
  <div class="menu-layout">
    <MiHeader/>
    <div class="app-main">
      <MiMenu/>
      <div class="main">
        <router-view/>
      </div>
    </div>
  </div>
</template>
<script>
import MiMenu from '@/components/MiMenu.vue';
import MiHeader from '@/components/MiHeader.vue';

export default {
  components: {
    MiMenu, MiHeader,
  },
};
</script>
<style lang="less" scoped>
.menu-layout{
  height: 100%;
}
.app-main{
  height: calc(100% - 55px);
  display: flex;
  .el-menu{
    width: 150px;
  }
  .main{
    width: calc(100% - 150px);
    height: 100%;
    overflow-y: auto;
  }
}
</style>
