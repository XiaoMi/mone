<template>
  <div class="screenshot">
    <!-- <div class="button-icon" @click="callScreenshot">
      <font-awesome-icon color="#FFF" :icon="['fas', 'scissors']" />
    </div> -->
    <div class="image-preview" v-if="props.modelValue.length">
      <ul class="el-upload-list el-upload-list--picture">
        <li class="el-upload-list__item is-success" tabindex="0">
          <img class="el-upload-list__item-thumbnail" :src="props.modelValue[0].url" alt="">
          <div class="el-upload-list__item-info">
            <a class="el-upload-list__item-name">
              <i class="el-icon el-icon--document">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
                  <path fill="currentColor"
                    d="M832 384H576V128H192v768h640zm-26.496-64L640 154.496V320zM160 64h480l256 256v608a32 32 0 0 1-32 32H160a32 32 0 0 1-32-32V96a32 32 0 0 1 32-32m160 448h384v64H320zm0-192h160v64H320zm0 384h384v64H320z">
                  </path>
                </svg>
              </i>
              <span class="el-upload-list__item-file-name" title=""></span>
            </a><!--v-if-->
          </div>
          <label class="el-upload-list__item-status-label">
            <i class="el-icon el-icon--upload-success el-icon--check">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
                <path fill="currentColor"
                  d="M406.656 706.944 195.84 496.256a32 32 0 1 0-45.248 45.248l256 256 512-512a32 32 0 0 0-45.248-45.248L406.592 706.944z">
                </path>
              </svg>
            </i>
          </label>
          <i class="el-icon el-icon--close" @click="deleteImage">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1024 1024">
              <path fill="currentColor"
                d="M764.288 214.592 512 466.88 259.712 214.592a31.936 31.936 0 0 0-45.12 45.12L466.752 512 214.528 764.224a31.936 31.936 0 1 0 45.12 45.184L512 557.184l252.288 252.288a31.936 31.936 0 0 0 45.12-45.12L557.12 512.064l252.288-252.352a31.936 31.936 0 1 0-45.12-45.184z">
              </path>
            </svg>
          </i>
          <!-- Due to close btn only appears when li gets focused disappears after li gets blurred, thus keyboard navigation can never reach close btn--><!-- This is a bug which needs to be fixed --><!-- TODO: Fix the incorrect navigation interaction -->
          <i class="el-icon--close-tip">press delete to remove</i>
          <!--v-if-->
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import util from '@/libs/util';

type ModelValue = {
  mediaType: string,
  input: string,
  url: string
}

const props = defineProps<{
  modelValue: ModelValue[]
}>();

const emits = defineEmits(['update:modelValue']);

function deleteImage() {
  emits("update:modelValue", []);
}

async function callScreenshot() {
  // await util.pasteImage()
}

onMounted(() => {
  // window.setScreenshot = (res: string) => {
  //   console.log("callScreenshot", res);
  //   try {
  //     const jRes = JSON.parse(decodeURIComponent(res));
  //     console.log(jRes);
  //     emits("update:modelValue", [{
  //       mediaType: jRes.mediaType,
  //       input: jRes.input,
  //       url: `data:${jRes.mediaType};base64,${jRes.input}`
  //     }]);
  //   } catch (error) {
  //     console.log(error);
  //   }
  // }
});
</script>

<style lang="scss" scoped>
.screenshot {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;

  .button-icon {
    display: inline-block;
    transform: rotate(270deg);
  }
}

.image-preview {
  position: absolute;
  // width: 104px;
  // left: 10px;
  right: -43px;
  bottom: 43px;
  display: inline-block;
}

.image-preview img {
  max-width: 200px;
  max-height: 200px;
}
</style>
