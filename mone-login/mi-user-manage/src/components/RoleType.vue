<template>
  <span>
    {{
      NodeUserRelTypeEnum.filter((itemIn)=>itemIn.k === typeProps) &&
      NodeUserRelTypeEnum.filter((itemIn)=>itemIn.k === typeProps)[0] &&
      NodeUserRelTypeEnum.filter((itemIn)=>itemIn.k === typeProps)[0].v
    }}
  </span>
</template>
<script>
import { mapState } from 'vuex';

export default {
  props: {
    type: {
      required: true,
    },
  },
  computed: {
    typeProps() {
      return this.type;
    },
    ...mapState({
      NodeUserRelTypeEnum: (state) => state.NodeModule.NodeUserRelTypeEnum,
    }),
  },
};
</script>
