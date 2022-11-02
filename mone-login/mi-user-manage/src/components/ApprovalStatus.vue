<template>
  <span :class="'status-'+statusProps">
    {{
      ApprovalStatusEnum.filter((itemIn)=>itemIn.k === statusProps) &&
      ApprovalStatusEnum.filter((itemIn)=>itemIn.k === statusProps)[0] &&
      ApprovalStatusEnum.filter((itemIn)=>itemIn.k === statusProps)[0].v
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
      ApprovalStatusEnum: (state) => state.NodeModule.ApprovalStatusEnum,
    }),
  },
  mounted() {
    console.log('00-0-0-0-');
  },
};
</script>
<style lang="less" scoped>
.status-2{
  color: #67C23A; // 完成
}
.status-1{
  color: #F56C6C; // 驳回
}

</style>
