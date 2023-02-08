<template>
  <div class="doc_detail_container">
    <div class="header_list">
      <p class="content_title">{{$i18n.t('title')}}:{{doc.title}}</p>
      <ul v-if="headerList.length">
        <li
          v-for="item in headerList"
          :key="item.id"
          :class="item.type"
          :title="item.content"
          >
          <a @click.stop="handleAnchor(item.id)">{{(item.content || "").trim()}}</a>
        </li>
      </ul>
      <p v-else class="empty">{{$i18n.t('noDirectory')}}</p>
    </div>
		<div class="doc_detail_container_word">
			<div class="doc_detail_container_wrap">
        <div v-if="doc.contentRaw || doc.content">
          <div v-if="doc.contentType === 0" class="doc_detail_container_word_content">
            <Wangeditor @onHeaders="setHeaders" :defaultValue="doc.contentRaw" :options="wOptions"/>
          </div>
          <MarkdownDoc v-else :content="doc.content"/>
        </div>
        <Empty v-else/>
      </div>
		</div>
    <el-button @click="handleEdit" class="detail-edit" type="primary" circle><el-icon><Edit /></el-icon></el-button>
	</div>
</template>
<script>

import MarkdownDoc from '@/components/MarkdownDoc'
import { getDocumentDetail } from '@/api/projectdoc'
import { AJAX_SUCCESS_MESSAGE } from '@/views/constant'
import Empty from '@/components/Empty'
import Wangeditor from '@/components/Wangeditor'
import moment from 'moment'
import { PATH } from "@/router/constant"

export default {
  name: 'DocDetail',
  components: { MarkdownDoc, Empty, Wangeditor },
  data () {
    return {
      doc: {
        content: "",
        contentRaw: "",
        contentType: 0,
        groupID: 0,
        projectID: 0,
        title: "",
        updateTime: "",
        userID: 0
      },
      headerList: [],
      wOptions: {
        readonly: true,
        height: 'auto'
      }
    }
  },
  props: {
    row: {
      type: Object,
      default () {
        return {}
      }
    }
  },
  computed: {
    moment () {
      return moment
    }
  },
  mounted () {
    if (this.row.documentID) {
      getDocumentDetail({ documentID: this.row.documentID }).then((data) => {
        if (data.message === AJAX_SUCCESS_MESSAGE) {
          this.doc = { ...data.data, projectName: this.row.projectName }
        }
      })
    }
  },
  methods: {
    setHeaders (val = []) {
      this.headerList = val
    },
    handleEdit () {
      window.open(`${window.location.origin}#${PATH.SUMMARY}?projectID=${this.doc.projectID}&editDocID=${this.row.documentID}&activeName=fourth`)
    },
    handleAnchor (id) {
      let dom = document.getElementById(id)
      if (dom) {
        dom.scrollIntoView()
      }
    }
  }
}
</script>
<style scoped>
.doc_detail_container {
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
}
.doc_detail_container_word {
  padding-bottom: 20px;
  color: #000;
  border-left: 1px dashed #ddd;
  width: 82%;
  height: calc(100vh - 50px);
  overflow-y: auto;
}
.doc_detail_container_word::-webkit-scrollbar {
  display: none;
}
.doc_detail_container_word_content >>> .mce-panel {
  border: none;
}
.doc_detail_container_word_content >>> .mce-top-part.mce-container.mce-stack-layout-item.mce-first::before {
  content: none;
}
.doc_detail_container_word_content >>> .w-e-toolbar{
  border: none !important;
}
.doc_detail_container_word_content >>> .w-e-text-container{
  border: none !important;
}

.doc_detail_container .header_list {
  background: #fff;
  width: 18%;
  height: calc(100vh - 70px);
  overflow-y: auto;
  border-radius: 4px;
  padding: 4px 0 4px;
  margin-top: 15px;
}
.doc_detail_container .header_list::-webkit-scrollbar {
  display: none;
}
.doc_detail_container .content_title {
  font-weight: bold;
  font-size: 16px;
  padding: 0 6px;
  color: #000;
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.doc_detail_container .header_list ul li {
  list-style: none;
  width: 100%;
  padding: 0 6px;
  margin: 8px 0;
  color: #333;
  font-size: 14px;
}
.doc_detail_container .header_list ul li a {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
  padding: 0;
  margin: 0;
  text-align: left;
  cursor: pointer;
  display: inline-block;
}
.doc_detail_container .header_list ul li a:hover {
  color: #5897ff;
}
.doc_detail_container .header_list ul li.header2 {
  padding-left: 14px;
}
.doc_detail_container .header_list ul li.header3 {
  padding-left: 28px;
}
.doc_detail_container .header_list ul li.header4 {
  padding-left: 42px;
}
.doc_detail_container .header_list ul li.header5 {
  padding-left: 56px;
}
.doc_detail_container .header_list p.empty {
  padding: 20px 6px;
  color: #ccc;
}
.doc_detail_container .doc_detail_container_wrap >>> ol li {
	list-style: auto
}
.doc_detail_container .doc_detail_container_wrap >>> ul li {
	list-style: initial
}
.doc_detail_container .detail-edit {
  position: fixed;
  right: 40px;
  bottom: 40px;
}
</style>
