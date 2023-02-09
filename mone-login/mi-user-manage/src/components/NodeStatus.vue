<template>
  <span :class="statusProps==0?'active':'de-active'">
    {{
      NodeStatusEnum.filter((itemIn)=>itemIn.k === statusProps) &&
      NodeStatusEnum.filter((itemIn)=>itemIn.k === statusProps)[0] &&
      NodeStatusEnum.filter((itemIn)=>itemIn.k === statusProps)[0].v
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
      NodeStatusEnum: (state) => state.NodeModule.NodeStatusEnum,
    }),
  },
};
</script>
