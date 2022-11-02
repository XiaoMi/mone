<template>
  <el-select v-model="type" placeholder="请选择用户类型" v-bind="$attrs">
    <el-option
      v-for="item in UserTypeEnum"
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
      UserTypeEnum: (state) => state.NodeModule.UserTypeEnum,
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
