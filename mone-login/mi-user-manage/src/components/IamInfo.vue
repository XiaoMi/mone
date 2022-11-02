<template>
  <div class="iam-info">
    <el-input v-model="iamInfo.id" @input="changeId"></el-input>
    <p class="iam-info-name"
    >Iam名称：<el-input v-model="iamInfo.name" :disabled="true"></el-input></p>
  </div>
</template>
<script>
import { getIamInfoApi } from '@/common/service/list/node';

export default {
  props: {
    value: {
      type: Object,
    },
  },
  computed: {
    iamInfo: {
      get() {
        return this.value;
      },
      set(val) {
        this.$emit('input', val);
      },
    },
  },
  methods: {
    getIamInfo(e) {
      const val = e.target.value;
      if (!val) {
        const sendV = { ...this.iamInfo, ...{ name: '', id: val } };
        this.$emit('input', sendV);
        return;
      }
      const params = { iamId: e.target.value };
      getIamInfoApi(params).then(({ data }) => {
        let resSendV = {};
        if (data) {
          resSendV = { ...this.iamInfo, ...{ name: data.name, id: val } };
        } else {
          resSendV = { ...this.iamInfo, ...{ name: '', id: val } };
        }
        this.$emit('input', resSendV);
      });
    },
    changeId(val) {
      if (!val) {
        const sendV = { ...this.iamInfo, ...{ name: '', id: val } };
        this.$emit('input', sendV);
        return;
      }
      const params = { iamId: val };
      getIamInfoApi(params).then(({ data }) => {
        let resSendV = {};
        if (data) {
          resSendV = { ...this.iamInfo, ...{ name: data.name, id: val } };
        } else {
          resSendV = { ...this.iamInfo, ...{ name: '', id: val } };
        }
        this.$emit('input', resSendV);
      });
    },
  },
};
</script>
<style lang="less" scoped>
.iam-info{
  display: flex;
}
.iam-info-name{
  white-space: nowrap;
  padding-left: 30px;
  /deep/ .el-input.is-disabled .el-input__inner{
    color: inherit;
  }
}
</style>
