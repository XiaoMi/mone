<template>
  <el-select v-model="type" placeholder="请选择环境" v-bind="$attrs"  style="width:200px">
    <el-option
      v-for="item in NodeEnvFlagEnum"
      :key="item.k"
      :label="item.v"
      :value="item.k">
    </el-option>
  </el-select>
</template>
<script>
import { mapState } from 'vuex';

export default {
  props: {
    value: {},
  },
  computed: {
    ...mapState({
      NodeEnvFlagEnum: (state) => state.NodeModule.NodeEnvFlagEnum,
    }),
    type: {
      get() {
        return this.value;
      },
      set(val) {
        this.$emit('input', val);
      },
    },
  },
};
</script>
