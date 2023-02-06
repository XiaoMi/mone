<template>
  <span :class="statusProps==0?'active':'de-active'">
    {{
      ResourceStatusEnum.filter((itemIn)=>itemIn.k === statusProps) &&
      ResourceStatusEnum.filter((itemIn)=>itemIn.k === statusProps)[0] &&
      ResourceStatusEnum.filter((itemIn)=>itemIn.k === statusProps)[0].v
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
      ResourceStatusEnum: (state) => state.NodeModule.ResourceStatusEnum,
    }),
  },
};
</script>
