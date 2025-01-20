<template>
  <div class="inputs-box">
    <el-collapse v-model="activeNames">
      <el-collapse-item name="1">
        <template #title>
          <div class="t-box">
            <div class="t-left">
              <CollapseTitle
                :activeNames="activeNames"
                title="输入"
                :content="titleDes || '输入需要添加到提示词的信息，这些信息可以被下方的提示词引用'"
                tipClass="title-tooltip"
                :showAdd="false"
              />
            </div>
            <el-button link @click.stop="addInput" size="small" v-if="showAdd">
              <i class="iconfont icon-plus1"></i>
            </el-button>
          </div>
        </template>
        <div v-for="(item, i) in inputs" :key="item" class="input-item">
          <el-form-item
            :label="i < 1 ? '变量名' : ''"
            :prop="'inputs.' + i + '.name'"
            :rules="{
              validator: (rule, value, cb) => {
                if (nameDisabled) {
                  return cb()
                }
                validPName(rule, value, cb, inputs)
              },
              trigger: 'blur'
            }"
          >
            <el-input
              v-model="item.name"
              placeholder="请输入参数名"
              style="width: 200px"
              maxlength="20"
              v-if="!nameDisabled"
            />
            <span
              v-else
              class="name-span"
              :class="showValueType ? 'show-value-type' : 'hide-value-type'"
              >{{ showName[item.name] || item.name }}</span
            >
            <span class="value-type" v-if="showValueType"> {{ item.valueType }} </span>
          </el-form-item>
          <div class="val-box">
            <el-form-item
              :label="i < 1 ? '变量值' : ''"
              :prop="'inputs.' + i + '.type'"
              :rules="{
                required: true,
                message: '参数值不可为空',
                trigger: 'blur'
              }"
            >
              <OutputTypeSel v-model="item.type" />
            </el-form-item>
            <el-form-item
              label=""
              :class="i == 0 ? 'empty-item' : ''"
              :prop="'inputs.' + i + '.value'"
              :rules="{
                validator: (rule, value, cb) => {
                  validateRef(rule, value, cb, inputs[i])
                },
                trigger: 'blur'
              }"
              v-if="item.type == 'value'"
            >
              <el-input v-model="item.value" placeholder="请输入参数值" :style="refreStyle" />
            </el-form-item>
            <el-form-item
              v-else
              label=""
              :class="i == 0 ? 'empty-item' : ''"
              :prop="'inputs.' + i + '.referenceInfo'"
              :rules="{
                validator: (rule, value, cb) => {
                  validateRef(rule, value, cb, inputs[i])
                },
                trigger: 'blur'
              }"
            >
              <QuotaCas v-model="item.referenceInfo" :style="refreStyle" :options="referOps" />
            </el-form-item>
            <el-form-item :class="i == 0 ? 'empty-item' : ''">
              <el-button
                link
                @click.stop="
                  () => {
                    delInFn(i)
                  }
                "
              >
                <i class="iconfont icon-jian" style="font-size: 14px"></i>
              </el-button>
            </el-form-item>
          </div>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, defineProps, defineEmits } from 'vue'
import OutputTypeSel from '@/views/workflow/work-flow/components/components/OutputTypeSel.vue'
import QuotaCas from '@/views/workflow/work-flow/components/components/QuotaCas.vue'
import CollapseTitle from '@/views/workflow/work-flow/components/components/CollapseTitle.vue'
import { validateRef, validPName } from '../../baseInfo'

const props = defineProps({
  modelValue: {},
  referOps: {},
  showAdd: {
    default: true,
    type: Boolean
  },
  titleDes: {},
  showValueType: {
    default: false
  },
  nameDisabled: {
    default: false
  } // 名称不可修改
})
const emits = defineEmits(['update:modelValue'])
const inputs = computed({
  get() {
    return props.modelValue
  },
  set(val) {
    emits('update:modelValue', val)
  }
})
const activeNames = ref(['1'])
const refreStyle = ref({
  width: '170px'
})

const addInput = () => {
  inputs.value.push({ name: '', type: 'reference' })
}
const delInFn = (i) => {
  inputs.value.splice(i, 1)
}
const showName = ref({
  $$TY_INTENT_QUERY$$: 'query'
})
</script>

<style scoped lang="scss">
.inputs-box {
  width: 100%;
  margin-bottom: 10px;
}
.t-left {
  display: flex;
  align-items: center;
}
.input-item {
  display: flex;
  justify-content: space-between;

  .name-box {
    flex: 1;
  }
}
.val-box {
  display: flex;
}
.value-type {
  display: inline-block;
  font-size: 12px;
  padding: 0 5px;
  background-color: #e7e7e7;
  border-radius: 3px;
  height: 18px;
  line-height: 18px;
  margin-left: 3px;
}
.name-span {
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  word-wrap: none;
}
.show-value-type {
  max-width: 100px;
}
.hide-value-type {
  max-width: 300px;
}
</style>
