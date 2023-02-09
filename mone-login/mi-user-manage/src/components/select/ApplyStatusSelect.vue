<template>
  <el-select v-model="type" placeholder="请选择" v-bind="$attrs" >
    <el-option
      v-for="item in ApplyStatusEnum"
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
      ApplyStatusEnum: (state) => state.NodeModule.ApplyStatusEnum,
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
