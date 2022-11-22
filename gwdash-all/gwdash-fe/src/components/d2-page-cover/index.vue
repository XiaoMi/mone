<!--
  Copyright 2020 Xiaomi

     Licensed under the Apache License, Version 2.0 (the "License");
     you may not use this file except in compliance with the License.
     You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

     Unless required by applicable law or agreed to in writing, software
     distributed under the License is distributed on an "AS IS" BASIS,
     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     See the License for the specific language governing permissions and
     limitations under the License.
  -->

<template>
  <div class="d2-page-cover">
    <div class="d2-page-cover__logo">
      <slot/>
    </div>
    <p class="d2-page-cover__title">{{title}}</p>
    <p class="d2-page-cover__sub-title d2-mt-0">{{subTitle}}</p>
    <slot name="footer"/>
    <!--<a target="blank" href="https://github.com/d2-projects/d2-admin">-->
      <!--<img-->
        <!--style="position: absolute; top: 0; right: 0; border: 0; width: 150px;"-->
        <!--src="./image/darkblue@2x.png"-->
        <!--alt="Fork me on GitHub">-->
    <!--</a>-->
  </div>
</template>

<script>
export default {
  props: {
    title: {
      type: String,
      required: false,
      default: 'Title'
    },
    subTitle: {
      type: String,
      required: false,
      default: 'subTitle'
    }
  }
}
</script>

<style lang="scss" scoped>
@import '~@/assets/style/public.scss';
.d2-page-cover {
  @extend %full;
  @extend %unable-select;
  display: flex;
  flex-flow: column nowrap;
  justify-content: center;
  align-items: center;
  .d2-page-cover__logo {
    img {
      width: 200px;
    }
  }
  .d2-page-cover__title {
    color: $color-text-main;
  }
  .d2-page-cover__sub-title {
    color: $color-text-sub;
  }
}
</style>
