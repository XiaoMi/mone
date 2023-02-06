<template>
  <span>
    {{
      ApplyTypeEnum.filter((itemIn)=>itemIn.k === typeProps) &&
      ApplyTypeEnum.filter((itemIn)=>itemIn.k === typeProps)[0] &&
      ApplyTypeEnum.filter((itemIn)=>itemIn.k === typeProps)[0].v
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
      ApplyTypeEnum: (state) => state.NodeModule.ApplyTypeEnum,
    }),
  },
};
</script>
