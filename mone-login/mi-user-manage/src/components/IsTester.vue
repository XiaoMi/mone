<template>
  <span>
    {{
      options.filter((itemIn)=>itemIn.k === typeProps) &&
      options.filter((itemIn)=>itemIn.k === typeProps)[0] &&
      options.filter((itemIn)=>itemIn.k === typeProps)[0].v
    }}
  </span>
</template>
<script>

export default {
  data() {
    return {
      options: [
        {
          k: 1,
          v: '是',
        }, {
          k: 0,
          v: '否',
        },
      ],
    };
  },
  props: {
    type: {
      required: true,
    },
  },
  computed: {
    typeProps() {
      return this.type;
    },
  },
};
</script>
