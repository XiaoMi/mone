<template lang="">
  <div class="third-redirect">
    <div class="loading-box">
      <LoadingCom/>
    </div>
  </div>
</template>
<script>
import LoadingCom from '@/views/user-manage/LoadingCom.vue';
import { loginCodereq } from '@/common/service/list/user-manage';

export default {
  components: {
    LoadingCom,
  },
  mounted() {
    this.getLoginOcde();
  },
  methods: {
    getLoginOcde() {
      const { query } = this.$route;
      loginCodereq(query).then(({ data }) => {
        if (data.setCookUrl) {
          window.location.href = data.setCookUrl;
        }
      });
    },
  },
};
</script>
<style lang="less" scoped>
.ball-scale-multiple > div {
  background-color: orange;
  transform: scale(2);
}
.ball-scale-multiple{
  transform: scale(2);
}
.loading-box{
  position: absolute;
  left:50%;
  top: 50%;
  transform: translate(-50%,-50%);
  display: flex;
  justify-content: space-between;
}

.third-redirect{
  height: 100%;
}
</style>
