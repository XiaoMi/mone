<template>
  <el-select v-model="type" placeholder="请选择资源状态" v-bind="$attrs"
     style="width:200px">
    <el-option
      v-for="item in ResourceStatusEnum"
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
      ResourceStatusEnum: (state) => state.NodeModule.ResourceStatusEnum,
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
