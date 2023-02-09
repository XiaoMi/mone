<template>
  <el-menu :default-active="defaultActive" class="mi-menu">
    <el-menu-item v-for="item in options" :index="item.value" :key="item.value"
    @click="jump(item.routerTo)">
      <!-- <router-link :to="item.routerTo">
        <div> -->
          <i :class="item.icon"></i>
          <span slot="title">{{item.label}}</span>
        <!-- </div>
      </router-link> -->
    </el-menu-item>
  </el-menu>
</template>
<script>

export default {
  data() {
    return {
      defaultActive: '1',
      options: [
        {
          label: '节点',
          value: '1',
          icon: 'iconfont icon-permission-jiedian',
          routerTo: { name: 'nodeDetail' },

        }, {
          label: '我的申请',
          value: '2',
          icon: 'iconfont icon-permission-wodeshenqing',
          routerTo: { name: 'MyApply' },
        }, {
          label: '待审核工单',
          value: '3',
          icon: 'iconfont icon-permission-daishenhegongdan',
          routerTo: { name: 'ToAudit' },
        },
        {
          label: '用户管理',
          value: '4',
          icon: 'iconfont icon-permission-yonghuguanli',
          routerTo: { name: 'UserManage' },
        },
      ],
    };
  },
  methods: {
    jump(to) {
      this.$router.push(to);
    },
    setActive() {
      const actvieName = this.$route.name;
      const activeVal = this.options.filter((item) => item.routerTo.name === actvieName);
      if (activeVal.length > 0) {
        this.defaultActive = activeVal[0].value;
      }
    },
    testTopMgr() {
      const { userInfo } = this.$store.state.UserModule;
      const topMrg = userInfo?.topMrg;
      if (!topMrg) {
        this.options.splice(3, 1);
      }
    },
  },
  mounted() {
    this.setActive();
    this.testTopMgr();
  },
  watch: {
    $route(to, from) {
      if (to.name !== from.name) {
        this.setActive();
      }
    },
  },
};
</script>
<style lang="less" scoped>
.mi-menu{
  .iconfont {
    margin: 0 8px 0 5px;
  }
}
</style>
