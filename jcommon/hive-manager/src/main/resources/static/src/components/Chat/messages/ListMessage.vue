<!--
 * @Description: 
 * @Date: 2023-12-26 17:49:36
 * @LastEditTime: 2023-12-27 11:07:24
-->
<template>
  <div class="sc-message--text">
    <div class="list-lable">请选择需要回退的步骤</div>
    <div class="list-container">
      <el-radio-group v-model="ask">
        <div
          v-for="(option, index) in askData?.addonList"
          :key="index"
          class="list-item"
        >
          <el-radio
            :label="index"
            size="large"
            :disabled="index + 1 >= askData.step"
            >{{ option }}</el-radio
          >
        </div>
      </el-radio-group>
    </div>
    <el-button type="primary" @click="back">回退</el-button>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import util from "@/libs/util";
import { ElMessage } from "element-plus";

const ask = ref();

const props = defineProps({
  message: {
    type: Object,
    required: true,
  },
});
const askData =
  props.message.data?.text && JSON.parse(props.message.data?.text);

const back = async function () {
  if (ask.value === undefined) {
    ElMessage.error("请选择需要回退的步骤");
    return;
  }
  try {
    console.log("ask.value + 1", ask.value + 1);
    const res = await util.setStateFullback(ask.value + 1);
    ElMessage.success("操作成功");
    if (res) {
      window.showErrorCode(
        window.decodeURIComponent(
          JSON.stringify({
            code: 0,
            message: res,
          })
        )
      );
      window.showErrorCode(
        window.decodeURIComponent(
          JSON.stringify({
            code: 0,
            message: askData?.addonList[ask.value],
          })
        )
      );
    }
  } catch (e) {
    console.error(e);
    ElMessage.error("操作失败");
  }
};
</script>

<style scoped>
.list-lable {
  padding-top: 10px;
}
.list-item {
  display: block;
  width: 100%;
}
.list-container {
  padding: 10px 0;
}
</style>
