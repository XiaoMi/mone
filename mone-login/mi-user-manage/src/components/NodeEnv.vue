<template>
  <span>
    {{
      NodeEnvFlagEnum.filter((itemIn)=>itemIn.k === typeProps) &&
      NodeEnvFlagEnum.filter((itemIn)=>itemIn.k === typeProps)[0] &&
      NodeEnvFlagEnum.filter((itemIn)=>itemIn.k === typeProps)[0].v
    }}
  </span>
</template>
<script>
import { mapState } from 'vuex';

export default {
  props: {
    value: {
      required: true,
    },
  },
  computed: {
    typeProps() {
      return this.value;
    },
    ...mapState({
      NodeEnvFlagEnum: (state) => state.NodeModule.NodeEnvFlagEnum,
    }),
  },
};
</script>
