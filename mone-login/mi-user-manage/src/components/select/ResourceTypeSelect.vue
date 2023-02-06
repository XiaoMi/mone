<template>
  <el-select v-model="type" placeholder="请选择资源类型" v-bind="$attrs" style="width:200px">
    <el-option
      v-for="item in ResourceTypeEnum"
      :key="item.k"
      :label="item.v"
      :value="item.k"
      >
    </el-option>
  </el-select>
</template>
<script>
import { mapState } from 'vuex';

export default {
  props: {
    value: {},
    initList: {},
  },
  data() {
    return {
      options: [],
    };
  },
  computed: {
    ...mapState({
      ResourceTypeEnum: (state) => state.NodeModule.ResourceTypeEnum,
    }),
    type: {
      get() {
        return this.value;
      },
      set(val) {
        this.$emit('input', val);
      },
    },
    canShowList() {
      return this.initList;
    },
  },
};
</script>
