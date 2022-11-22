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
  <div class="d2-container-card-bs">
    <div v-if="$slots.header" class="d2-container-card-bs__header" ref="header">
      <slot name="header"/>
    </div>
    <div class="d2-container-card-bs__body" ref="wrapper">
      <div class="d2-container-card-bs__body-wrapper-inner">
        <div class="d2-container-card-bs__body-card">
          <slot/>
        </div>
      </div>
    </div>
    <div v-if="$slots.footer" class="d2-container-card-bs__footer" ref="footer">
      <slot name="footer"/>
    </div>
  </div>
</template>

<script>
import bs from './mixins/bs'
export default {
  name: 'd2-container-card-bs',
  mixins: [
    bs
  ]
}
</script>
