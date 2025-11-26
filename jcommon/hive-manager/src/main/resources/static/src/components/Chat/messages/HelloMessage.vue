<template>
    <div class="sc-message--text">
      <div class="sc-message--toolbox">
        <slot name="text-message-toolbox" :message="message"></slot>
      </div>
  
      <slot :message="message" :messageText="helloText">
        <div class="sc-message--text-content">
          <div v-html="helloText" />
          <template v-for="item of links">
            <div>
              {{ item.prefix }}
              <template v-if="item.src">
                <el-link
                  target="_blank"
                  rel="noopener noreferrer" 
                  type="primary"
                  :href="item.src"
                >{{item.label}}</el-link>
              </template>
              <template v-else-if="item.label">
                <el-link
                  type="primary"
                  @click="handleClick(item)"
                >{{item.label}}</el-link>
              </template>
              {{ item.suffix }}
            </div>
          </template>
        </div>
      </slot>
    </div>
  </template>
  
  <script lang="ts">
  import { htmlEscape } from 'escape-goat'
  import Autolinker from 'autolinker'
  
  import fmt from 'msgdown'
  
  export default {
    props: {
      message: {
        type: Object,
        required: true
      },
      onMessageClick: {
        type: Function,
        required: true
      }
    },
    computed: {
      helloText () {
        const map:Record<string, string> = {}
        let escaped = htmlEscape(this.message.data.hello)
        const reg = /\$\{(.+?)\}/g
        const result = escaped.match(reg)
        if (result) {
          for (let i = 0; i < result.length; i++) {
            const item = result[i].trim()
            const first:number = item.indexOf('url:')
            const middle:number = item.indexOf(',')
            const last:number = item.indexOf('title:')
            if (first != -1 && last != -1) {
              const herf = item.substring(first + 4, middle).trim()
              const title = item.substring(last + 6, item.length - 1).trim()
              map[herf] = title
              escaped = escaped.replace(item, herf)
            }
          }
        }
        return Autolinker.link(fmt(escaped), {
          className: 'chat-link',
          truncate: {length: 30, location: 'smart'},
          replaceFn: function (match) {
            if (match.type === 'url'
              && map[match.getAnchorHref()]) {
              const tag = match.buildTag()
              tag.setInnerHTML(map[match.getAnchorHref()])
              return tag
            }
          }
        })
      },
      links () {
        return this.message.data.links
      }
    },
    methods: {
      handleClick (item: {
        type: string,
        label: string,
        params: any
      }) {
        this.onMessageClick({
          type: item.type,
          text: item.label,
          params: item.params
        })
      }
    }
  }
  </script>
  
  <style scoped lang="scss">
  .sc-message--text {
    // padding: 5px 20px;
    // border-radius: 6px;
    font-weight: 300;
    font-size: 14px;
    position: relative;
    -webkit-font-smoothing: subpixel-antialiased;
    background-color: rgb(39, 39, 39);

    .sc-message--text-content {
      white-space: pre-wrap;
      line-height: 2;
    }
    &:hover .sc-message--toolbox {
      left: -20px;
      opacity: 1;
    }
    .sc-message--toolbox {
      transition: left 0.2s ease-out 0s;
      white-space: normal;
      opacity: 0;
      position: absolute;
      left: 0px;
      width: 25px;
      top: 0;
      button {
        background: none;
        border: none;
        padding: 0px;
        margin: 0px;
        outline: none;
        width: 100%;
        text-align: center;
        cursor: pointer;
        &:focus {
          outline: none;
        }
      }
      & :deep(svg) {
        margin-left: 5px;
      }
    }
    code {
      font-family: 'Courier New', Courier, monospace !important;
    }
  }
  
  .sc-message--content.sent .sc-message--text {
    color: #FFF;
    background-color: rgb(58, 58, 58);
    max-width: calc(100% - 120px);
    word-wrap: break-word;
  }
  
  .sc-message--content.received .sc-message--text {
    color: #FFF;
    background-color: rgb(39, 39, 39);
    margin-right: 40px;
  }
  
  a.chatLink {
    color: inherit !important;
  }
  </style>
  