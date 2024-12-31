<!--
 * @Description: 
 * @Date: 2024-02-20 10:59:44
 * @LastEditTime: 2024-02-22 15:58:10
-->
<template>
  <el-dialog
    v-model="dialogVisible"
    :title="'编辑数据库(' + (props.databaseData?.data?.tableName || '-') + ')'"
    width="800"
    draggable
    :append-to-body="true"
    @open="open"
  >
    <el-table :data="tableData" style="width: 100%">
      <el-table-column
        v-for="(item, index) in Object.keys(tableData[0] || {})"
        :key="index"
        :width="Math.max(160, 1 / Object.keys(tableData[0] || {}).length)"
        :label="item"
        v-slot="{ row }"
      >
        <div class="column-flex">
          <span v-if="item === 'columnType'" class="columnType-icon">
            <i :class="' iconfont icon-' + getType(row, item)"></i>
          </span>
          <el-input
            v-model="row[item]"
            :disabled="
              (row[item] && row[item] === row.isPrimaryKey) ||
              item === 'columnType' ||
              item === 'isPrimaryKey' ||
              item === 'columnDefault' ||
              item === 'isNullable'
                ? true
                : false
            "
          >
          </el-input>
        </div>
      </el-table-column>
      <el-table-column fixed="right" label="操作" v-slot="{ row, $index }" width="140">
        <el-button type="primary" plain size="small" @click="add($index)">添加</el-button>
        <el-button
          type="danger"
          plain
          size="small"
          @click="remove($index)"
          v-if="row['columnName'] !== row.isPrimaryKey"
          >删除</el-button
        >
      </el-table-column>
    </el-table>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">{{ t('common.cancle') }}</el-button>
        <el-button type="primary" @click="save"> {{ t('common.confirm') }} </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { t } from '@/locales'
import { queryTableStructure, alterTableColumns } from '@/api/data-source'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  databaseData: {}
})
const emits = defineEmits(['update:modelValue'])

const dialogVisible = computed({
  get() {
    return props.modelValue
  },
  set(v) {
    emits('update:modelValue', v)
  }
})

const originTableData = ref([])
const tableData = ref([])
const typeObj = {
  // 数字类型
  TINYINT: 'number123',
  SMALLINT: 'number123',
  MEDIUMINT: 'number123',
  INT: 'number123',
  BIGINT: 'number123',
  FLOAT: 'number123',
  DOUBLE: 'number123',
  DECIMAL: 'number123',
  NUMERIC: 'number123',
  // 字符串类型
  CHAR: 'stringABC',
  VARCHAR: 'stringABC',
  TEXT: 'stringABC',
  MEDIUMTEXT: 'stringABC',
  LONGTEXT: 'stringABC',
  BINARY: 'stringABC',
  VARBINARY: 'stringABC',
  // JSON 类型（假设存在，如MySQL）
  JSON: 'json',
  // 日期和时间类型
  DATE: 'date',
  TIME: 'date',
  DATETIME: 'date',
  TIMESTAMP: 'date',
  YEAR: 'date'
}
const getType = (row, item) => {
  if (typeObj[row[item].toUpperCase()]) {
    return typeObj[row[item].toUpperCase()]
  } else {
    return typeObj[row[item].split('(')[0].toUpperCase()]
  }
}
const save = () => {
  let arr1 = tableData.value
    .filter((item) => originTableData.value.some((ele) => item.columnName === ele.columnName))
    ?.map((item) => {
      return { ...item, operationType: 'MODIFY' }
    })
  let arr2 = tableData.value
    .filter((item) => !originTableData.value.some((ele) => item.columnName === ele.columnName))
    ?.map((item) => {
      return { ...item, operationType: 'ADD' }
    })
  let arr3 = originTableData.value
    .filter((item) => !tableData.value.some((ele) => item.columnName === ele.columnName))
    ?.map((item) => {
      return { ...item, operationType: 'DROP' }
    })
  tableData.value = tableData.value.map((item) => {
    const v = arr1.find((ele) => item.columnName === ele.columnName)
    if (v) {
      return v
    }
    const v2 = arr2.find((ele) => item.columnName === ele.columnName)
    if (v2) {
      return v2
    }
  })
  alterTableColumns({
    datasourceId: props.databaseData.node.parent.data.id,
    tableName: props.databaseData.data.tableName,
    columnOperations: [...tableData.value, ...arr3]
  }).then((res) => {
    if (res.code === 0) {
      dialogVisible.value = false
      ElMessage.success('编辑成功')
    } else {
      ElMessage.error(res.message)
    }
  })
}
const open = () => {
  queryTableStructure({
    datasourceId: props.databaseData?.node.parent.data.id,
    tableName: props.databaseData.data.tableName
  }).then((res) => {
    tableData.value = JSON.parse(JSON.stringify(res.data))
    originTableData.value = JSON.parse(JSON.stringify(res.data))
  })
}
const add = (index) => {
  let itemObj = {}
  Object.keys(tableData.value[0]).forEach((item) => {
    itemObj[item] = ''
  })
  tableData.value.splice(index + 1, 0, itemObj)
}
const remove = (index) => {
  tableData.value.splice(index, 1)
}
</script>

<style lang="scss">
.column-flex {
  display: flex;
  align-items: center;
}
.columnType-icon {
  width: 16px;
  color: #00a9ff;
  padding-right: 2px;
}
</style>
