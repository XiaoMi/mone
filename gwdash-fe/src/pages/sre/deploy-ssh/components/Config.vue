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
  <div class="dialog-modal" v-show="visible" @click.self="() => $emit('update:visible', false)">
    <div class="config-container">
      <el-tabs v-model="activeName">
        <el-tab-pane label="主题" name="first">
          <el-select v-model="theme" placeholder="请选择主题" style="width: 100%">
            <el-option v-for="item in themes" :key="item" :label="item" :value="item"></el-option>
          </el-select>
          <div v-if="theme">
            <p class="title">基础颜色</p>
            <el-row type="flex" class="theme-colors">
              <el-col :span="8">
                <div class="show-color" :style="{backgroundColor: colors.background}"></div>
                <div>Background</div>
              </el-col>
              <el-col :span="8">
                <div class="show-color" :style="{backgroundColor: colors.foreground}"></div>
                <div>Foreground</div>
              </el-col>
              <el-col :span="8">
                <div class="show-color" :style="{backgroundColor: colors.cursor}"></div>
                <div>Cursor</div>
              </el-col>
            </el-row>
            <p class="title">ANSI Colors</p>
            <el-row type="flex" class="theme-colors">
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.black}"></div>
                <div>Black</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.red}"></div>
                <div>Red</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.green}"></div>
                <div>Green</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.yellow}"></div>
                <div>Yellow</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.blue}"></div>
                <div>Blue</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.magenta}"></div>
                <div>Magenta</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.cyan}"></div>
                <div>Cyan</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.white}"></div>
                <div>White</div>
              </el-col>
            </el-row>
            <el-row type="flex" class="theme-colors">
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.brightBlack}"></div>
                <div>BrightBlack</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.brightRed}"></div>
                <div>BrightRed</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.brightGreen}"></div>
                <div>BrightGreen</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.brightYellow}"></div>
                <div>BrightYellow</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.brightBlue}"></div>
                <div>BrightBlue</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.brightMagenta}"></div>
                <div>BrightMagenta</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.brightCyan}"></div>
                <div>BrightCyan</div>
              </el-col>
              <el-col :span="3">
                <div class="show-color" :style="{backgroundColor: colors.brightWhite}"></div>
                <div>BrightWhite</div>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
import xtermTheme from "xterm-theme"
const themes = Object.keys(xtermTheme)

export default {
  name: "ConfigModal",
  props: {
    visible: Boolean
  },
  data () {
    return {
      activeName: "first",
      themes: themes,
      theme: window.localStorage.getItem("themeName") || null,
      bgimg: window.localStorage.getItem("bgimg") || null
    }
  },
  computed: {
    colors () {
      if (this.theme) {
        return xtermTheme[this.theme]
      } else {
        return null
      }
    }
  },
  watch: {
    theme (val) {
      window.localStorage.setItem("themeName", val)
      this.$emit("setTheme", xtermTheme[val])
    }
  },
  methods: {
    handleUpload (e) {
      let file = event.target.files[0]
      var reader = new FileReader()
      reader.readAsDataURL(file)
      reader.onload = e => {
        this.bgimg = e.target.result
        window.localStorage.setItem("bgimg", e.target.result)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.dialog-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2000;
  text-align: center;
  background-color: rgba(0, 0, 0, 0.1);
  color: #000;

  &::after {
    content: "";
    display: inline-block;
    height: 100%;
    width: 0;
    vertical-align: middle;
  }
}
.config-container {
  position: relative;
  display: inline-block;
  min-width: 640px;
  min-height: 380px;
  padding: 10px 10px 20px;
  vertical-align: middle;
  background-color: #fff;
  border-radius: 4px;
  border: 1px solid #ebeef5;
  font-size: 18px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  text-align: left;
  overflow: hidden;
  backface-visibility: hidden;

  .btn-close {
    position: absolute;
    top: 5px;
    right: 10px;
    width: 10px;
    height: 10px;
    cursor: pointer;
  }

  .title {
    font-size: 14px;
  }

  .theme-colors {
    font-size: 12px;

    .show-color {
      width: 100%;
      height: 24px;
      margin-bottom: 10px;
    }

    .el-col {
      text-align: center;
    }
  }

  .bgimg-btn {
    width: 600px;
    height: 300px;

    img {
      width: 100%;
      height: 100%;
    }
  }
}

.upload-input {
  z-index: 100000;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100%;
  font-size: 0;
  opacity: 0;
}
</style>
