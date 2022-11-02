<template>
  <el-select v-model="type" placeholder="请选择" v-bind="$attrs" >
    <el-option
      v-for="item in NodeUserRelTypeEnum.filter((itemIn)=>filterArr.indexOf(itemIn.k)>-1)"
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
    canShowList: {},
  },
  computed: {
    ...mapState({
      NodeUserRelTypeEnum: (state) => state.NodeModule.NodeUserRelTypeEnum,
    }),
    type: {
      get() {
        return this.value;
      },
      set(val) {
        this.$emit('input', val);
      },
    },
    filterArr() {
      return this.canShowList;
    },
  },
};
</script>
