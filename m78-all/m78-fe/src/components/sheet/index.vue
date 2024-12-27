<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import Spreadsheet, { type Options } from 'x-data-spreadsheet'
import * as XLSX from 'xlsx'
import { stox, xtos } from './xlsxspread'

const props = defineProps<{
  json: Record<string, string>[]
}>()

const emits = defineEmits(['change'])

const preData = ref<Record<string, any>[] | null>()
const grid = ref<Spreadsheet | null>(null)

const options: Options = {
  mode: 'edit', // edit | read
  showToolbar: false,
  showGrid: true,
  showContextmenu: true,
  showBottomBar: false,
  view: {
    height: () => document.getElementById('x-spreadsheet-demo')!.offsetHeight,
    width: () => document.getElementById('x-spreadsheet-demo')!.offsetWidth
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

const jsonToData = (json: Record<string, string>[]) => {
  const ws = XLSX.utils.json_to_sheet(json)
  const wb = XLSX.utils.book_new(ws, `Sheet1`)
  return stox(wb)
}

const dataToJson = (data: any[]) => {
  const wb = xtos(data)
  return XLSX.utils.sheet_to_json(wb.Sheets[wb.SheetNames[0]])
}

// warning：id必须存在
onMounted(async () => {
  const s = (grid.value = new Spreadsheet('#x-spreadsheet-demo', options).loadData({})) // load data
  s.change(function (data) {
    const nData = dataToJson([{ ...data }])
    // add
    const addedData = nData.filter(
      ({ id: nId }) => preData.value?.findIndex(({ id }) => id == nId) == -1
    )
    console.log(addedData)
    // remove
    const delData = preData.value?.filter(
      ({ id: oId }) => nData.findIndex(({ id }) => id == oId) == -1
    )
    console.log(delData)
    // update
    const updateData = nData.filter((nIt) => {
      const data = preData.value?.find((it) => it.id == nIt.id)
      if (!data) return false
      const keys = Object.keys(data)
      const nItKeys = Object.keys(nIt)
      const allKeys = Array.from(new Set([...keys, ...nItKeys]))
      for (const key of allKeys) {
        if (data[key] != nIt[key]) return true
      }
      return false
    })
    console.log(updateData)
    emits('change', {
      insert: addedData,
      delete: delData,
      update: updateData
    })
  })
})

watch(
  () => props.json,
  (json, preJson) => {
    if (json != preJson && grid.value) {
      preData.value = json.map((it) => {
        return { ...it }
      })
      grid.value.loadData(jsonToData([...json]))
    }
  }
)
</script>

<template>
  <div class="h-full w-full flex flex-col">
    <div class="shrink-0"></div>
    <div class="flex flex-1 overflow-hidden">
      <div class="flex-1 overflow-height" id="x-spreadsheet-demo"></div>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
