<template>
  <div class="excle-box">
    <div ref="myElement" class="excle-container">
      <div id="divExcle" class="excle-dom"></div>
    </div>
    <!-- 初始化 -->
    <div v-if="!props.uuid" class="empty-box">
      <ImportDoc type="drag" @uploadSuccess="uploadSuccess" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import Spreadsheet, { type Options } from 'x-data-spreadsheet'
import 'x-data-spreadsheet/dist/locale/zh-cn'
import { ref, onMounted, nextTick } from 'vue'
import * as XLSX from 'xlsx'
import { xtos, stox } from '@/views/xlsx/xlsxspread.ts'
import { onBeforeUnmount } from 'vue'
import ImportDoc from './ImportDoc.vue'

Spreadsheet.locale('zh-cn')
let grid = null
const tableData = ref()

const props = defineProps({
  uuid: {}
})

const myElement = ref(null)
const options: Options = ref({
  mode: 'edit', // edit | read
  showToolbar: false,
  showGrid: true,
  showContextmenu: true,
  // showBottomBar: false,
  view: {
    height: () => 500,
    width: () => 500
  },
  row: {
    len: 100,
    height: 25
  },
  col: {
    len: 100,
    width: 100,
    indexWidth: 60,
    minWidth: 60
  },
  style: {
    bgcolor: '#FFF',
    align: 'left',
    valign: 'middle',
    textwrap: false,
    strike: false,
    underline: false,
    color: '#0a0a0a',
    font: {
      name: 'Helvetica',
      size: 10,
      bold: false,
      italic: false
    }
  }
})
const selectCell = ref({})
const getW = () => {
  // 设置excle的宽高
  const elementWidth = myElement.value?.offsetWidth
  const elementH = myElement.value?.offsetHeight
  options.value.view = {
    height: () => elementH - 20,
    width: () => elementWidth
  }
}
const exportData = () => {
  // 导出
  if (grid) {
    const data = grid.getData()
    const wb = xtos(data)
    XLSX.writeFile(wb, 'exported_data.xlsx')
  }
}
const addSheet = (data) => {
  const curData = grid.getData()
  const ws = XLSX.utils.json_to_sheet(data)
  const wb = XLSX.utils.book_new(ws, `Sheet${curData.length + 1}`)
  const content = stox(wb)
  const newData = [...content, ...curData]
  grid.loadData(newData)
}

const initSheet = (data) => {
  const ws = XLSX.utils.json_to_sheet(data)
  const wb = XLSX.utils.book_new(ws, `Sheet1`)
  const content = stox(wb)
  grid.loadData(content)
}
const emits = defineEmits(['uploadSuccess'])
const uploadSuccess = (params) => {
  emits('uploadSuccess', params)
}
const resizeWindow = () => {
  window.addEventListener('resize', getW)
}

const menus = [
  { action: 'insertRowBefore', text: '向上插入行' },
  { action: 'insertRowAfter', text: '向下插入行' },
  { action: 'delRow', text: '删除行' },
  { action: 'divider', text: '', isDivide: true },
  { action: 'insertColLeft', text: '向左插入列' },
  { action: 'insertColRight', text: '向右插入列' },
  { action: 'delCol', text: '删除列' }
]
const contextMenuChange = (ri, ci) => {
  const retDom = (action) => {
    return document.querySelector(`div[action-name="${action}"]`)
  }
  const changeStatus = (doms, statusText) => {
    doms.forEach((dom) => {
      dom?.setAttribute('style', `display: ${statusText}`)
    })
  }
  const insertRowBefore = retDom('insertRowBefore')
  const insertRowAfter = retDom('insertRowAfter')
  const delRow = retDom('delRow')
  const insertColLeft = retDom('insertColLeft')
  const insertColRight = retDom('insertColRight')
  const delCol = retDom('delCol')
  const dividerDom = retDom('divider')
  const rowEditor = [insertRowBefore, insertRowAfter, delRow]
  const colEditor = [insertColLeft, insertColRight, delCol]
  const allEditor = [...rowEditor, ...colEditor]
  if (ri != -1 && ci != -1) {
    changeStatus([...allEditor, dividerDom], 'block')
  } else {
    if (ri == -1 && ci == -1) {
      changeStatus([...allEditor, dividerDom], 'none')
    } else if (ri == -1) {
      changeStatus(colEditor, 'block')
      changeStatus([...rowEditor, dividerDom], 'none')
    } else if (ci == -1) {
      changeStatus(rowEditor, 'block')
      changeStatus([...colEditor, dividerDom], 'none')
    }
  }
}
const clickMenu = (action) => {
  const contextmenuDom = document.querySelector('.x-spreadsheet-sheet .x-spreadsheet-contextmenu')
  contextmenuDom.setAttribute('style', 'display: none')
  console.log('action ', action, selectCell.value)

  const curData = grid.getData()
  if (action.startsWith('insertRow')) {
    curData[0].rows = insertRow(curData[0].rows, { num: selectCell.value.ri, type: action })
  } else if (action.startsWith('insertCol')) {
    curData[0].rows = insertCol(curData[0].rows, { num: selectCell.value.ci, type: action })
  }
  console.log('curData', curData)
  grid.loadData(curData)
}

const arrToObj = (arr, hasLen = true) => {
  const obj = {}
  if (hasLen) {
    obj.len = arr.length
  }
  arr.map((item, index) => {
    obj[index] = item
  })
  return obj
}

const objToArr = (obj) => {
  const arr = []
  const numKeys = Object.keys(obj).filter((item) => item != 'len')
  numKeys.map((item) => {
    arr.push(obj[item])
  })
  return arr
}

//  插入行
const insertRow = (rows, { num, type }) => {
  let newRowsArr = objToArr(rows)
  newRowsArr.splice(type == 'insertRowAfter' ? num + 1 : num, 0, { cells: {} })
  let newRows = arrToObj(newRowsArr)
  return newRows
}
// 插入一列
const insertCol = (rows, { num, type }) => {
  const numKeys = Object.keys(rows).filter((item) => item != 'len')
  const newRowsArr = []
  numKeys.forEach((item) => {
    const { cells } = rows[item]
    const cellsArr = objToArr(cells)
    cellsArr.splice(type == 'insertColRight' ? num + 1 : num, 0, { text: '' })
    newRowsArr.push({ cells: arrToObj(cellsArr, false) })
  })
  const newRowsObj = arrToObj(newRowsArr)
  return newRowsObj
}

const cellSelected = (cell, ri, ci) => {
  console.log('ri', ri, 'ci', ci)
  selectCell.value = { ri, ci, cell }
  contextMenuChange(ri, ci)
}
const createDiv = ({ action, text, isDivide = false }) => {
  // 创建右键菜单中的元素
  var div = document.createElement('div')
  div.setAttribute('class', `x-spreadsheet-item ${isDivide ? 'divider' : ''}`)
  div.setAttribute('action-name', action)
  div.innerHTML = text
  div.addEventListener('click', () => {
    clickMenu(action)
  })
  return div
}
const init = () => {
  grid = new Spreadsheet('#divExcle', options.value)
    .loadData([])
    .on('cell-edited', (cell, ri, ci, state) => {
      console.log('cell cell-edited事件', cell, 'ri', ri, 'ci', ci, state)
    })
    .on('cell-selected', (cell, ri, ci) => {
      cellSelected(cell, ri, ci)
    })
  nextTick(() => {
    // 初始化之后往menu里面加入自己的元素
    const menuDom = document.querySelector('.x-spreadsheet-sheet .x-spreadsheet-contextmenu')
    menus.forEach((item) => {
      menuDom.appendChild(createDiv(item))
    })
  })
}
onMounted(() => {
  getW()
  init()
  resizeWindow()
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', getW)
})
defineExpose({
  exportData,
  addSheet,
  // uploadFile,
  initSheet
})
</script>
<style lang="scss" scoped>
.excle-container {
  width: 100%;
  overflow-x: scroll;
  background: #fff;
  height: calc(100vh - 148px);

  :deep(.x-spreadsheet-menu li:first-child) {
    display: none;
  }
}
.excle-box {
  flex: 1;
  position: relative;
  .empty-box {
    position: absolute;
    width: 100%;
    height: 100%;
    left: 0;
    top: 0;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 20;
    background: #fff;
  }
}
.excle-dom {
  border: solid 1px #e6e6e6;
  overflow: hidden;
}

#divExcle {
  :deep(.x-spreadsheet-bottombar.x-spreadsheet-contextmenu) {
    // display: none;
  }
  :deep(.x-spreadsheet-sheet .x-spreadsheet-contextmenu) {
    .x-spreadsheet-item :nth-child(0),
    :nth-child(1),
    :nth-child(2),
    :nth-child(3),
    :nth-child(4),
    :nth-child(5),
    :nth-child(6),
    :nth-child(7),
    :nth-child(8),
    :nth-child(9),
    :nth-child(10),
    :nth-child(11),
    :nth-child(12),
    :nth-child(13),
    :nth-child(14),
    :nth-child(15),
    :nth-child(16),
    :nth-child(17),
    :nth-child(18),
    :nth-child(19),
    :nth-child(20),
    :nth-child(21) {
      display: none !important;
    }
  }
  :deep(.x-spreadsheet-selector-corner) {
    display: none;
  }
}
</style>
