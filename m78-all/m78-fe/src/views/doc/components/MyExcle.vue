<template>
  <div class="excle-box" v-loading="loading" :element-loading-text="t('common.loading')">
    <div id="divExcle" class="excle-dom"></div>
  </div>
</template>
<script lang="ts" setup>
import Spreadsheet, { type Options } from 'x-data-spreadsheet'
import 'x-data-spreadsheet/dist/locale/zh-cn'
import { ref, onMounted, nextTick, watch, onBeforeUnmount } from 'vue'
import * as XLSX from 'xlsx'
import { xtos, stox } from '@/views/xlsx/xlsxspread.ts'
import {
  updataCellApi,
  changeColNameApi,
  getColInfoApi,
  delColApi,
  delRowApi,
  addRowApi,
  appendColApi
} from '@/api/excle.ts'
import { ElMessage } from 'element-plus'
import { t } from '@/locales'

Spreadsheet.locale('zh-cn')
let grid = null

const props = defineProps({
  uuid: {}
})

const loading = ref(false)

const preData = ref([]) // 保存历史数据，每次数据更新的时候保存一份数据
const saveData = () => {
  preData.value = JSON.parse(JSON.stringify(grid.getData())) // 保存一份数据
}

const options: Options = {
  mode: 'read', // edit | read
  showToolbar: false,
  showGrid: true,
  showContextmenu: false,
  view: {
    width: () => document.getElementById('divExcle')?.offsetWidth,
    height: () => document.getElementById('divExcle')?.offsetHeight
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
}
const selectCell = ref({})

const exportData = () => {
  // 导出
  if (grid) {
    const data = grid.getData()
    const wb = xtos(data)
    XLSX.writeFile(wb, 'exported_data.xlsx')
  }
}

// 是否找到缺少m78_id属性的列
const lackKeyFn = (data, keyName) => {
  return data.find((item) => {
    return Object.keys(item).indexOf(keyName) < 0
  })
}
const setCol1Hide = () => {
  // 隐藏m78_id
  const { datas, sheet } = grid
  const activeIndex = getActiveSheetIndex()
  const { selector } = sheet
  const activeData = getActiveData()
  const endRowI = activeData?.rows.len ? activeData?.rows.len - 1 : 0
  selector.set(-1, 0)
  selector.setEnd(endRowI, 0)
  datas[activeIndex]?.hideRowsOrCols()
  updateExcle()
}

const transformData = (data) => {
  const lackM78Id = lackKeyFn(data, 'm78_id')
  const lackM78IdClinet = lackKeyFn(data, 'm78_Id_client')
  if (lackM78Id && lackM78IdClinet) {
    const newData = data.map((item) => {
      // 在客户端增加一个标识符：m78_Id_client
      const newItem = { m78_Id_client: true, ...item }
      return newItem
    })
    return newData
  }
  return data
}
const addSheet = (data) => {
  const transData = transformData(data)
  const curData = grid.getData()
  const ws = XLSX.utils.json_to_sheet(transData)
  const wb = XLSX.utils.book_new(ws, `Sheet${curData.length + 1}`)
  const content = stox(wb)
  const newData = [...content, ...curData]
  grid.loadData(newData)
  setCol1Hide()
  saveData()
}

const initSheet = (data) => {
  const transData = transformData(data)
  const ws = XLSX.utils.json_to_sheet(transData)
  const wb = XLSX.utils.book_new(ws, `Sheet1`)
  const content = stox(wb)
  grid.loadData(content)
  setCol1Hide()
  saveData()
}

const emits = defineEmits(['uploadSuccess'])
const uploadSuccess = (params) => {
  emits('uploadSuccess', params)
}

const menus = [
  { action: 'delRow', text: '删除行' },
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
  const delRowDom = retDom('delRow')
  const delColDom = retDom('delCol')
  // 选择了整列 或者 选择标题那一行 不展示删除行 其他情况都可展示删除行
  changeStatus([delRowDom], ri == -1 || ri == 0 ? 'none' : 'block')
  // 选择了整行的时候 不展示删除列
  changeStatus([delColDom], ci == -1 ? 'none' : 'block')
}
const clickMenu = (action) => {
  grid.sheet.contextMenu.hide()
  if (action === 'delCol') {
    // 删除列
    delColFn()
  } else if (action === 'delRow') {
    // 删除行
    delRowFn()
  }
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
const insertRowFn = (rows, { num, type }) => {
  let newRowsArr = objToArr(rows)
  newRowsArr.splice(type == 'insertRowAfter' ? num + 1 : num, 0, { cells: {} })
  let newRows = arrToObj(newRowsArr)
  return newRows
}

const getActiveSheetIndex = () => {
  const activeSheetName = grid.sheet.data.name
  const all = grid.getData()
  return all.findIndex((item) => item.name == activeSheetName)
}

const getActiveData = () => {
  const all = grid.getData()
  return all[getActiveSheetIndex()]
}

const appendRow = () => {
  // 添加一个空行
  const actice = getActiveSheetIndex()
  const all = grid.getData()
  // 新增行
  const curData = all[actice]
  const { rows } = curData
  //  在最后一个后面加一个
  const newRows = insertRowFn(rows, { num: rows.len - 1, type: 'insertRowAfter' })
  curData.rows = newRows
  grid.loadData(all)
  saveData()
}

const delRowExcl = (rows, ri) => {
  const allData = grid.getData()
  const rowsArr = objToArr(rows)
  rowsArr.splice(ri, 1)
  const newRows = arrToObj(rowsArr)
  const curSheetIndex = getCurShIndex()
  allData[curSheetIndex].rows = newRows
  grid.loadData(allData)
}

// 删除行
const delRowFn = async () => {
  loading.value = true
  const curSheetIndex = getCurShIndex()
  const allData = grid.getData()
  const rows = allData[curSheetIndex].rows
  const { ri } = selectCell.value
  const m78Id = Number(rows[ri]?.cells[0]?.text)
  const params = {
    documentId: props.uuid,
    rowIds: [m78Id]
  }
  const { code } = await delRowApi(params)
  if (code == 0) {
    delRowExcl(rows, ri)
  }
  loading.value = false
}

const updateExcle = () => {
  const data = grid.getData()
  grid.loadData(data)
}

const delColExcl = () => {
  const { ci } = selectCell.value
  const curRow = grid.sheet.data.rows
  curRow.deleteColumn(ci, ci)
  updateExcle()
}
// 删除列
const delColFn = async () => {
  loading.value = true
  const curData = getDataProxy()
  const rowKey = curData.getCell(0, selectCell.value.ci)
  const enKey = getRealColName(rowKey.text)
  const params = {
    documentId: props.uuid,
    columnNames: [enKey || rowKey.text]
  }
  const { code } = await delColApi(params)
  if (code != 0) return
  loading.value = false
  delColExcl()
}

const cellSelected = (cell, ri, ci) => {
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
// 获取当前sheet索引
const getCurShIndex = () => {
  const sheetName = grid.sheet.data.name
  const allData = grid.getData()
  return allData.findIndex((item) => item.name == sheetName)
}

const getDataProxy = () => {
  return grid.datas[getCurShIndex()]
}
const getRealColName = (keyName) => {
  // 根据keyName获取对应的key
  const obj = columnOptions.value.find((item) => item.columnComment == "'" + keyName + "'")
  return obj?.columnName || ''
}
const retRow = ({ ci, ri, cell }) => {
  const curData = getDataProxy()
  const rowKey = curData.getCell(0, ci)
  const obj = columnOptions.value.find((item) => item.columnComment == "'" + rowKey.text + "'")
  const val = obj?.columnType?.indexOf('int') > -1 ? Number(cell) : cell
  const m78Id = curData.getCell(ri, 0)?.text
  const keyName = obj?.columnName || rowKey.text
  return m78Id
    ? {
        [keyName]: cell,
        m78_id: Number(m78Id)
      }
    : { [keyName]: cell }
}

const updateCell = async (cell, ri, ci) => {
  const row = retRow({ ri, ci, cell })
  const params = {
    documentId: props.uuid,
    rowData: [row]
  }
  return updataCellApi(params)
}
const setCellTextFn = (ri, ci, text) => {
  const active = getActiveSheetIndex()
  const { rows } = grid.datas[active]
  const { editor } = grid.sheet
  editor.clear()
  rows.setCellText(ri, ci, text)
  grid.reRender()
}
// 获取单元格值
const getCellTextFn = (ri, ci) => {
  const active = getActiveSheetIndex()
  const { rows } = grid.datas[active]
  const { editor } = grid.sheet
  const obj = rows.getCell(ri, ci)
  return obj.text
}
// 修改列名
const changeColName = async (newV, ri, ci) => {
  const active = getActiveSheetIndex()
  const preColName = preData.value[active]?.rows[0].cells[ci].text
  const colNameEn = getRealColName(preColName)
  const params = {
    documentId: props.uuid,
    updateColumnNames: [
      {
        column: colNameEn || preColName,
        columnComment: newV
      }
    ]
  }
  return changeColNameApi(params)
}

const cellEditTimer = ref(null)
const clearTimer = () => {
  clearTimeout(cellEditTimer.value)
  cellEditTimer.value = null
}
const cellEditFn = async (cell, ri, ci) => {
  if (cellEditTimer.value) clearTimer()
  const m78IDName = getCellTextFn(0, 0)
  const active = getActiveSheetIndex()
  if (m78IDName !== 'm78_id') {
    ElMessage.error('此处数据不能修改')
    const preV = preData.value[active]?.rows[ri].cells[ci].text
    setCellTextFn(ri, ci, preV)
    return
  }
  cellEditTimer.value = setTimeout(() => {
    const row = retRow({ ri, ci, cell })
    if (row.m78_id || ri == 0) {
      // 修改列名或者修改普通数据
      const apiName = ri == 0 ? changeColName : updateCell
      apiName(cell, ri, ci)
        .then((res) => {
          if (res.code != 0) {
            ElMessage.error('更新数据失败')
            console.log('preData.value[active]?.rows', preData.value[active]?.rows)
            const preColName = preData.value[active]?.rows[ri]?.cells[ci]?.text || ''
            setCellTextFn(ri, ci, preColName)
          } else {
            if (ri == 0) {
              getColNameMap()
            }
          }
        })
        .finally(() => {
          saveData()
          clearTimer()
        })
    } else {
      // 用户自己增加的行
      loading.value = true
      const row = retRow({ ri, ci, cell })
      const params = {
        documentId: props.uuid,
        rowData: [row]
      }
      addRowApi(params)
        .then((res) => {
          //  获取m78_id
          if (res.code != 0) {
            // 失败了重置数据
            ElMessage.error('更新数据失败')
          } else {
            // 设置m78_id
            setCellTextFn(ri, 0, res.data)
          }
        })
        .finally(() => {
          saveData()
          loading.value = false
        })
    }
  }, 1000)
}

const init = (data) => {
  grid = new Spreadsheet('#divExcle', options)
    .loadData(data)
    .on('cell-edited', (cell, ri, ci) => {
      cellEditFn(cell, ri, ci)
    })
    .on('cell-selected', (cell, ri, ci) => {
      cellSelected(cell, ri, ci)
    })
  setCol1Hide()
  // nextTick(() => {
  // 初始化之后往menu里面加入自己的元素
  const menuDom = document.querySelector('.x-spreadsheet-sheet .x-spreadsheet-contextmenu')
  menus.forEach((item) => {
    menuDom.appendChild(createDiv(item))
  })
  // })
  saveData()
}
const changeMode = (mode) => {
  // 修改模式，重新初始化一个
  options.mode = mode
  options.showContextmenu = mode === 'edit' ? true : false
  var elem = document.querySelector('#divExcle')
  elem.innerHTML = ''
  const newData = grid.getData()
  init(newData)
}

const getColNameMap = (uuid = props.uuid) => {
  // 每次修改列名 、插入列、uuid变化 的时候要更新一下
  getColInfoApi(uuid).then(({ data }) => {
    columnOptions.value = data
  })
}

const columnOptions = ref([])
watch(
  () => props.uuid,
  () => {
    if (props.uuid) {
      getColNameMap(props.uuid)
    }
  },
  {
    deep: true,
    immediate: true
  }
)
const appendColFn = async (keyName) => {
  // 插入列
  loading.value = true
  const params = {
    documentId: props.uuid,
    columnNames: [keyName]
  }
  const res = await appendColApi(params)
  loading.value = false
  if (res.code != 0) {
    ElMessage.error(res.message)
    return
  }
  getColNameMap()
  const data = grid.getData()
  const activeIndex = getCurShIndex()
  const cellObj = data[activeIndex].rows[0].cells
  const rowLen = Object.keys(cellObj).length
  const rowObj = grid.sheet.data.rows
  rowObj.insertColumn(rowLen, 1)
  rowObj.setCell(0, rowLen, { text: keyName })
  updateExcle()
  saveData()
}

onBeforeUnmount(() => {
  clearTimer()
})
onMounted(() => {
  init([])
})

defineExpose({
  exportData,
  addSheet,
  initSheet,
  changeMode,
  appendRow,
  appendColFn
})
</script>
<style lang="scss" scoped>
.excle-box {
  flex: 1;
  overflow: hidden;
  position: relative;
  :deep(.x-spreadsheet-menu li:first-child) {
    display: none;
  }
  :deep(.x-spreadsheet-scrollbar) {
    background: #fff;
    border: solid 1px #fff;
    cursor: pointer;
    div {
      background: #fff;
    }
    &::-webkit-scrollbar {
      /* 隐藏默认的滚动条 */
      -webkit-appearance: none;
    }
    &::-webkit-scrollbar:vertical {
      /* 设置垂直滚动条宽度 */
      width: 10px;
    }

    &::-webkit-scrollbar:horizontal {
      /* 设置水平滚动条厚度 */
      height: 10px;
    }

    &::-webkit-scrollbar-thumb {
      /* 滚动条的其他样式定制，注意，这个一定也要定制，否则就是一个透明的滚动条 */
      border-radius: 8px;
      background-color: rgba(0, 0, 0, 0.5);
    }
  }
}
.excle-dom {
  height: 100%;
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
  :deep(.x-spreadsheet-selector .x-spreadsheet-selector-area) {
    box-sizing: content-box;
  }
}
</style>
