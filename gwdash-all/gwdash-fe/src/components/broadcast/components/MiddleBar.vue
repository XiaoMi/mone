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
  <div class="con">
      <p class="connetion-info">{{message.message}}</p>
  </div>
</template>

<script>
export default {
  name: "middle-message",
  props: {
    message: {
      type: Object,
      required: true
    }
  }
}
</script>

<style lang="scss" scoped>
.con{
  margin-top: 5px;
  text-align: center;

}
.connetion-info{
  display:inline;
  background: #dedede;
  padding: 3px;
  font-size: 12px;
  text-align: center;
  border-radius: 4px;
  overflow: hidden;
}

</style>
