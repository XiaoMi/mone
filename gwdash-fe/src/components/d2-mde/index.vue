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
  <div class="simplemde" ref="simplemde">
    <input
      ref="file"
      type="file"
      name="file"
      @change="imgChange"
      accept="image/png, image/jpeg"
      style="display:none"/>
    <textarea ref="mde" v-model="value"></textarea>
  </div>
</template>

<script>
import SimpleMDE from 'simplemde'
import service from '@/plugin/axios/index'
import 'simplemde/dist/simplemde.min.css'

export default {
  name: 'd2-mde',
  props: {
    // 值
    value: {
      type: String,
      required: false,
      default: ''
    },
    // 配置参数
    config: {
      type: Object,
      required: false,
      default: () => ({})
    }
  },
  data () {
    return {
      // 编辑器实例
      mde: null,
      // 编辑器默认参数
      // 详见 https://github.com/sparksuite/simplemde-markdown-editor#configuration
      defaultConfig: {
        autoDownloadFontAwesome: false,
        toolbar: [
          'bold',
          'italic',
          'heading',
          '|',
          'quote',
          'code',
          'unordered-list',
          'ordered-list',
          'table',
          'horizontal-rule',
          '|',
          'link',
          'image',
          {
			      name: "myImage",
			      action: (editor) => {
              this.myImageAction(editor);
			      },
			      className: "fa fa-cloud-upload",
			      title: "本地图片"
          },
          '|',
          'preview',
          'side-by-side',
          'fullscreen'
        ] 
      },
      imgFile: {}
    }
  },
  mounted () {
    // 初始化
    this.init()
  },
  // beforeUpdate () {
  //   console.log('beforeUpdate');
  // },
  // updated() {
  //   console.log('updated');
  //   this.mde = null;
  //   // 合并参数
  //   const config = Object.assign({}, this.defaultConfig, this.config)
  //   // 初始化
  //   this.mde = new SimpleMDE({
  //     ...config,
  //     // 初始值
  //     initialValue: this.value,
  //     // 挂载元素
  //     element: this.$refs.mde
  //   })
  // },

  destroyed () {
    // 在组件销毁后销毁实例
    this.mde = null
  },
  methods: {
    // 初始化
    init () {
      // 合并参数
      const config = Object.assign({}, this.defaultConfig, this.config)
      // 初始化
      this.mde = new SimpleMDE({
        ...config,
        // 初始值
        initialValue: this.value,
        // 挂载元素
        element: this.$refs.mde
      })
      this.mde.codemirror.on('change', () => {
        this.$emit('input', this.mde.value())
      })
      // 图片粘贴
      this.mde.codemirror.on('paste',(editor,e) => {
        if(!(e.clipboardData && e.clipboardData.items)) {
          this.$message.warning('此浏览器不支持粘贴操作，请升级浏览器')
          return;
        }
        try {
          let dataList = e.clipboardData.items;
          if (dataList[0].kind === 'file' && dataList[0].getAsFile().type.indexOf('image') !== -1) {
            this.imgFile = dataList[0].getAsFile();
            this.imageUpload();
          }
        } catch(e){
          this.$message.warning('只能粘贴图片')
        }
      })
    },
    imageUpload () {
      const editor = this.mde;
      const file = this.imgFile
      if (!file || file.size <= 0) {
        this.$message.error('图片大小为0～')
        return
      }
      if (file.size >= 1024 * 1024 *5) {
        this.$message.error('图片太大～')
        return
      }
      var formData = new FormData()
      formData.append('file', file)
      formData.append('name', file.name)
      service({
        url: '/comment/uplaod/image',
        method: 'post',
        data: formData
      }).then(url => {
        const cm = editor.codemirror
        const options = editor.options
        const insertImage = options.insertTexts.image
        var start = insertImage[0]
        var end = insertImage[1].replace('#url#', url)
        var startPoint = cm.getCursor("start")
        var endPoint = cm.getCursor("end")
        cm.replaceRange(`${start}${end}`, {
          line: startPoint.line,
			    ch: endPoint.ch
        })
        cm.setSelection(startPoint, endPoint)
        cm.focus()
      })
    },
    imgChange(e) {
       this.imgFile = e.target.files[0];
       this.imageUpload();
    },
    myImageAction (editor) {
      this.editor = editor;
      this.$refs.file.click();
    }
  }
}
</script>