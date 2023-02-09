<template>
  <span>
    {{
      UserTypeEnum.filter((itemIn)=>itemIn.k === typeProps) &&
      UserTypeEnum.filter((itemIn)=>itemIn.k === typeProps)[0] &&
      UserTypeEnum.filter((itemIn)=>itemIn.k === typeProps)[0].v
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
      UserTypeEnum: (state) => state.NodeModule.UserTypeEnum,
    }),
  },
};
</script>
