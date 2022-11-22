<template>
  <div id="app">
    <el-config-provider :locale="configZh">
      <router-view></router-view>
    </el-config-provider>
  </div>
</template>

<script>
import zhCn from 'element-plus/lib/locale/lang/zh-cn'

export default {
  name: 'App',
  data () {
    return {
      preProjectID: undefined,
      configZh: zhCn,
    }
  },
  mounted () {
    // let language = window.localStorage.getItem('apiLocaleLanguage') || 'zh'
    this.$store.dispatch('permission/changeLanguage', 'zh')
  },
  watch: {
    $route: {
      handler: function () {
        let curProjectID = this.$utils.getQuery('projectID')
        window.sessionStorage.setItem('projectID', curProjectID)
        if (Number(curProjectID) && (Number(curProjectID) !== Number(this.preProjectID))) {
          this.preProjectID = curProjectID
          this.$store.dispatch('summary.base/getDetail', curProjectID)
        }
      },
      immediate: true
    }
  }
}

</script>

<style>
  @import './common/css/main.scss';
</style>
