<template>
  <div class="h-full flex flex-col">
    <div class="flex-1 overflow-x-auto">
      <el-tree
        ref="treeRef"
        :data="props.treeData"
        :props="treeProps"
        :load="loadNode"
        :default-expanded-keys="defaultExpandedKeys"
        :expand-on-click-node="false"
        class="py-20px"
        node-key="id"
        lazy
        @check-change="handleCheckChange"
      >
        <template #default="{ node, data }">
          <div v-if="node.level == 1" class="w-full flex justify-between items-center">
            <div class="flex item-center">
              <el-radio v-model="connectionId" :label="String(data.id)" :name="String(data.id)">
                <span class="custom-tree-node">
                  <el-icon><Notebook /></el-icon>
                  <span class="custom-tree-text">{{ node.label }}</span>
                </span></el-radio
              >
            </div>
            <div>
              <el-icon :size="16" class="mr-[6px]">
                <Edit @click="editDataBase(data)" />
              </el-icon>
              <el-popconfirm
                confirm-button-text="是"
                cancel-button-text="否"
                icon-color="#626AEF"
                title="确定要删除数据库么?"
                @confirm="handleDel(node, data)"
                @cancel="() => {}"
              >
                <template #reference>
                  <el-icon :size="16">
                    <Delete />
                  </el-icon>
                  <el-button :icon="Delete"></el-button>
                </template>
              </el-popconfirm>
            </div>
          </div>
          <div
            v-else
            v-contextmenu:contextmenu
            @contextmenu="contextmenuClick(node, data)"
            class="w-full flex justify-between items-center"
            style="height: 100%"
          >
            <span class="custom-tree-node" @click="handleNodeClick(node)">
              <el-icon><Notebook /></el-icon>
              <el-tooltip class="box-item" effect="dark" :content="node.label" placement="left">
                <span class="custom-tree-text truncate w-[200px]">{{ node.label }}</span>
              </el-tooltip>
            </span>
            <!-- <el-icon :size="16" class="mr-[6px]">
              <Edit @click="editTable(node)" />
            </el-icon> -->
          </div>
        </template></el-tree
      >
    </div>
  </div>
  <NewDataSource @submit="submit" v-model="dialogVisible" :init-form="initForm" />
  <v-contextmenu ref="contextmenu">
    <v-contextmenu-item @click="editDatabase">编辑数据库</v-contextmenu-item>
  </v-contextmenu>
  <Database v-model="databaseVisible" :databaseData="databaseData"></Database>
  <!-- <NewTable
    v-if="dbTableVisible"
    v-model="dbTableVisible"
    :database-id="connectionId"
    :table-name="tableName"
  ></NewTable> -->
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { getTables, deleteDataSource } from '@/api/data-source'
import { Delete, Edit } from '@element-plus/icons-vue'
import { t } from '@/locales'
import type Node from 'element-plus/es/components/tree/src/model/node'
import { useDataSourceStore } from '@/stores/data-source'
import NewDataSource from './NewDataSource.vue'
// import NewTable from './NewTable.vue'
import { ElMessage } from 'element-plus'
import Database from './Database.vue'
import { useRoute } from 'vue-router'

const dataSourceStore = useDataSourceStore()
const treeRef = ref()
const { setActiveDbId, setCurDbInfo, setActiveTableName } = dataSourceStore
const route = useRoute()
const treeProps = {
  label: 'name',
  children: 'children',
  isLeaf: 'leaf'
}

const props = defineProps<{
  treeData: DataSource.Tree[]
}>()

const emits = defineEmits(['updateTreeData', 'nodeClick'])

const connectionId = computed({
  get() {
    return dataSourceStore.activeDbId
  },
  set(value: string) {
    const db = props.treeData.find((it) => it.id == value)
    setActiveDbId(value)
    setCurDbInfo(db)
    setActiveTableName('')
  }
})

// const tableName = computed({
//   get() {
//     return dataSourceStore.activeTableName
//   },
//   set(value: string) {
//     setActiveTableName(value)
//   }
// })

const dialogVisible = ref(false)
// const dbTableVisible = ref(false)
const initForm = ref()
const databaseVisible = ref(false)
const databaseData = ref()
const defaultExpandedKeys = ref([])
const defaultWatch = ref()

const editDataBase = (data: any) => {
  initForm.value = { ...data }
  dialogVisible.value = true
}

// const editTable = (node: Node) => {
//   console.log(node)
//   tableName.value = node.data.tableName
//   connectionId.value = String(node.parent.data.id)
//   dbTableVisible.value = true
// }

const submit = async () => {
  emits('updateTreeData')
}

const handleDel = async (node: Node, data: DataSource.Tree) => {
  const { code } = await deleteDataSource({
    id: data.id
  })
  if (code == 0) {
    ElMessage.success('成功')
    const parent = node.parent
    console.log(parent)
    const childNodes = parent.childNodes
    const index = childNodes.findIndex((d) => d.data.id == data.id)
    childNodes.splice(index, 1)
  } else {
    ElMessage.error('失败')
  }
}

const handleCheckChange = (data: DataSource.Tree, checked: boolean, indeterminate: boolean) => {
  console.log(data, checked, indeterminate)
}

defaultWatch.value = watch(
  () => [route.query, props.treeData],
  ([query, list]) => {
    if (query.id && query.tableName && list?.length) {
      let db = props.treeData.find((it) => it.id == query.id)
      setActiveDbId(query.id)
      setCurDbInfo(db)
      setTimeout(() => {
        const node = treeRef.value?.getNode(db)
        node.loaded = false
        node.loadData(() => {
          defaultExpandedKeys.value = [query.id]
          let childNodes = treeRef.value?.getNode(db)?.childNodes
          if (childNodes?.length) {
            let node = childNodes.find((v) => v.data.tableName === query.tableName)
            handleNodeClick(node)
            defaultWatch.value()
          }
        })
      }, 1000)
    }
  },
  {
    deep: true,
    immediate: true
  }
)

const loadNode = (node: Node, resolve: (data: DataSource.Tree[]) => void) => {
  if (node.level === 0) {
    resolve([...node.data])
  } else if (node.level === 1) {
    const data = node.data
    getTables({
      connectionId: data.id
    }).then(
      ({ code, data }) => {
        if (code == 0 && data) {
          resolve(
            data.map((it: { tableName: string }) => {
              return {
                ...it,
                name: it.tableName,
                leaf: true
              }
            })
          )
        }
      },
      () => {
        resolve([])
      }
    )
  } else {
    resolve([])
  }
}

const handleNodeClick = (node: {
  level: number
  parent: { data: { id: string } }
  data: { id: string; tableName: string }
}) => {
  console.log(node)
  connectionId.value = String(node.parent.data.id)
  setActiveTableName(node.data.tableName)
  emits('nodeClick', node.data, 1)
}

const contextmenuClick = (node, data) => {
  databaseData.value = { node, data }
}
const editDatabase = () => {
  databaseVisible.value = true
}
</script>

<style lang="scss" scoped>
.custom-tree-node {
  display: flex;
  align-items: center;
  height: 100%;
}
.custom-tree-text {
  padding-left: 5px;
}
</style>
