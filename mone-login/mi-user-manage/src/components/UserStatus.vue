<template>
  <span :class="statusProps==0?'active':'de-active'">
    {{
      UserStatusEnum.filter((itemIn)=>itemIn.k === statusProps) &&
      UserStatusEnum.filter((itemIn)=>itemIn.k === statusProps)[0] &&
      UserStatusEnum.filter((itemIn)=>itemIn.k === statusProps)[0].v
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
      UserStatusEnum: (state) => state.NodeModule.UserStatusEnum,
    }),
  },
};
</script>
