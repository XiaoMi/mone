<template>
  <span>
    {{
      OutIdTypeEnum.filter((itemIn)=>itemIn.k === typeProps) &&
      OutIdTypeEnum.filter((itemIn)=>itemIn.k === typeProps)[0] &&
      OutIdTypeEnum.filter((itemIn)=>itemIn.k === typeProps)[0].v
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
      OutIdTypeEnum: (state) => state.NodeModule.OutIdTypeEnum,
    }),
  },
};
</script>
