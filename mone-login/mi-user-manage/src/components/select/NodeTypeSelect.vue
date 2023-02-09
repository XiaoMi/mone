<template>
  <el-select v-model="type" placeholder="节点类型" v-bind="$attrs">
    <el-option
      v-for="item in
      canShowList?NodeTypeEnum.filter(item=>canShowList.indexOf(item.k)>-1):NodeTypeEnum"
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
      NodeTypeEnum: (state) => state.NodeModule.NodeTypeEnum,
    }),
    type: {
      get() {
        return this.value;
      },
      set(val) {
        this.$emit('input', val);
        this.$emit('change', val);
      },
    },
    canShowList() {
      return this.initList;
    },
  },
};
</script>
