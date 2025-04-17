<template>
  <div class="paste-image">
    <div class="button-icon" @click="callScreenshot">
      <svg t="1744885664074" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="2354" width="16" height="16"><path d="M866.4576 102.4H157.5424C98.816 102.4 51.2 150.016 51.2 208.7424v567.1424c0 58.7264 47.616 106.3424 106.3424 106.3424h708.9152c58.7264 0 106.3424-47.616 106.3424-106.3424V208.7424C972.8 150.016 925.184 102.4 866.4576 102.4zM157.5424 173.312h708.9152c19.5584 0 35.4304 15.872 35.4304 35.4304v339.5584l-181.4528-181.4528-177.9712 182.8864-108.4416-108.4928-311.9104 320.4608v-552.96c0-19.5584 15.872-35.4304 35.4304-35.4304z m546.56 638.0032H172.4416l262.2976-269.3632 269.3632 269.3632z m197.7856-35.4304c0 19.5584-15.872 35.4304-35.4304 35.4304h-62.3616l-212.6848-212.6848 133.2736-130.4064 180.736 180.736-3.5328 126.9248zM263.8848 385.9456a70.912 70.912 0 1 1 0-141.7728 70.912 70.912 0 0 1 0 141.824z" p-id="2355" fill="#ffffff"></path></svg>
    </div>
  </div>
</template>

<script setup lang="ts">
import util from '@/libs/util';

async function callScreenshot() {
  // await util.pasteImage()
}
</script>

<style lang="scss" scoped>
.paste-image {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;

  .button-icon {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
