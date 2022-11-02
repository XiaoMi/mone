<template>
  <span>
    {{
      ResourceTypeEnum.filter((itemIn)=>itemIn.k === typeProps) &&
      ResourceTypeEnum.filter((itemIn)=>itemIn.k === typeProps)[0] &&
      ResourceTypeEnum.filter((itemIn)=>itemIn.k === typeProps)[0].v
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
      ResourceTypeEnum: (state) => state.NodeModule.ResourceTypeEnum,
    }),
  },
};
</script>
