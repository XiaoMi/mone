<template>
  <span :class="'status-'+statusProps">
    {{
      ApplyStatusEnum.filter((itemIn)=>itemIn.k === statusProps) &&
      ApplyStatusEnum.filter((itemIn)=>itemIn.k === statusProps)[0] &&
      ApplyStatusEnum.filter((itemIn)=>itemIn.k === statusProps)[0].v
    }}
  </span>
</template>
<script>
import { mapState } from 'vuex';

export default {
  props: {
    status: {
      required: true,
    },
  },
  computed: {
    statusProps() {
      return this.status;
    },
    ...mapState({
      ApplyStatusEnum: (state) => state.NodeModule.ApplyStatusEnum,
    }),
  },
};
</script>
<style lang="less" scoped>
.status-1{
  color: #67C23A; // 完成
}
.status-2{
  color: #F56C6C; // 驳回
}

</style>
