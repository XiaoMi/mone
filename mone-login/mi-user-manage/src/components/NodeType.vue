<template>
  <span>
    {{
      NodeTypeEnum.filter((itemIn)=>itemIn.k === typeProps) &&
      NodeTypeEnum.filter((itemIn)=>itemIn.k === typeProps)[0] &&
      NodeTypeEnum.filter((itemIn)=>itemIn.k === typeProps)[0].v
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
      NodeTypeEnum: (state) => state.NodeModule.NodeTypeEnum,
    }),
  },
};
</script>
