<template>
  <div class="mode-container">
    <div class="left">
      <div class="form-container">
        <div class="form-content">
          <slot name="left" />
        </div>
        <div class="form-btn">
          <el-button
            type="primary"
            @click="emits('onSubmit')"
            color="#262845"
            :loading="props.loading"
            >{{ props.submitBtnTxt }}</el-button
          >
        </div>
      </div>
    </div>
    <div class="center">
      <div class="center-container" v-if="props.result">
        <template v-if="Object.keys(props.result).length == 0">
          <EmptyBox />
        </template>
        <template v-else>
          <LoadingBox v-if="props.result.runStatus == 0" :loading="props.result.runStatus == 0" />
          <template v-else>
            <template v-if="!slotCenter">
              <CenterImags
                v-if="props.type == 'img'"
                :list="props.result.multiModalResourceOutput"
                :status="item.runStatus"
              />
              <TextItem v-else :text="props.result.multiModalResourceOutput[0]" />
            </template>
            <slot name="center" v-else />
          </template>
        </template>
      </div>
    </div>
    <div class="right">
      <slot name="right"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useSlots, defineProps, watch, ref, computed } from 'vue'
import ImgItem from './components/ImgItem.vue'
import MultipleImg from './components/MultipleImg.vue'
import TextItem from './components/TextItem.vue'
import LoadingBox from './components/LoadingBox.vue'
import EmptyBox from './components/EmptyBox.vue'
import CenterImags from './components/CenterImgs.vue'

const slotCenter = !!useSlots().center

const mulImgRef = ref(null)

const emits = defineEmits(['onSubmit'])
const props = defineProps({
  type: {
    default: 'text'
  },
  result: {},
  loading: {
    default: false
  },
  submitBtnTxt: {
    type: String,
    default: '立即生成'
  }
})
</script>

<style lang="scss" scoped>
.mode-container {
  flex: 1;
  display: flex;
}
.left {
  width: 350px;
  background: #fff;
  padding: 10px 0;

  .form-container {
    height: 100%;
    display: flex;
    flex-direction: column;
  }
  .form-content {
    flex: 1;
    overflow: auto;
    padding: 0 10px;
  }
  .form-btn {
    text-align: right;
    padding-bottom: 10px;
    padding-right: 20px;
    padding: 10px 20px 10px 0;
  }
}
.center {
  flex: 1;
  background: #fff;
  padding: 10px;
  border-left: 1px solid #eee;
  .center-container {
    padding: 10px;
    height: 100%;
    box-shadow:
      (0 0 #0000, 0 0 #0000),
      (0 0 #0000, 0 0 #0000),
      0 10px 15px -3px rgba(0, 0, 0, 0.1),
      0 4px 6px -4px rgba(0, 0, 0, 0.1);
  }
}
.right {
  border-left: 1px solid #eee;
  // width: 150px;
  background: #fff;
  // padding: 10px;
  .right-container {
    display: flex;
    flex-direction: column;
    height: 100%;
    h2 {
      font-size: 14px;
    }
  }
}

.center-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.tips {
  display: flex;
  justify-content: center;
  align-items: center;
}

.smile-img {
  width: 70px;
}
</style>
