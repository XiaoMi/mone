<template>
  <div class="sc-message--text">
    <div v-if="formUi.type == 'form'">
      <div v-if="question" class="question">{{ question }}</div>
      <el-form
        ref="ruleFormRef"
        :model="form"
        :rules="rules"
        :label-width="formUi.labelWidth"
      >
        <el-form-item
          v-for="item of formUi.listUi"
          :prop="item.value"
          :label="item.label"
          :key="item.value"
        >
          <template v-if="item.type === 'input'">
            <el-input v-model="form[item.value]"></el-input>
          </template>
          <template v-else-if="item.type === 'radio'">
            <el-radio-group v-model="form[item.value]">
              <div
                v-for="option of item.options"
                :key="option.index"
                class="message-item"
              >
                <el-radio :label="option.index">{{
                  `${option.index}. ${option.label}`
                }}</el-radio>
              </div>
            </el-radio-group>
          </template>
          <template v-else-if="item.type === 'boolean'">
            <el-switch v-model="form[item.value]" />
          </template>
          <template v-else-if="item.type === 'select'">
            <el-select v-model="form[item.value]">
              <el-option
                v-for="option in item.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              />
            </el-select>
          </template>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit(ruleFormRef)"
            >确认</el-button
          >
        </el-form-item>
      </el-form>
    </div>
    <div v-else-if="formUi.type === 'string'">
      {{ message.data.data }}
    </div>
    <div v-else>不支持展示类型</div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import util from "@/libs/util";
import { ElMessage } from "element-plus";
import type { FormInstance, FormRules } from "element-plus";

const question = ref<string>("");
const formUi = ref<Record<string, any>>({
  type: "unknown",
  labelWidth: "0",
  listUi: [],
});
const form = ref<Record<string, string>>({});
const ruleFormRef = ref<FormInstance>();
const rules = reactive<FormRules<Record<string, string>>>({});

const props = defineProps({
  message: {
    type: Object,
    required: true,
  },
});

const msgType = props.message.meta?.type || "";
formUi.value.type = msgType;
if (msgType === "map") {
  const meta = props.message.data?.meta || {};
  question.value = meta.question || "";
  formUi.value.type = "form";

  const listUi = [];
  const map = props.message.data.data.map;
  const memaryMap = props.message.data.data?.memaryMap || {};
  let max = 0;
  for (const key of Object.keys(map)) {
    rules[key] = [
      { required: true, message: `${key}是必填字段`, trigger: "blur" },
    ];
    let options = null;
    // 确定是否选型
    const optionsStr = meta[`__${key}`];
    if (optionsStr) {
      try {
        options = JSON.parse(optionsStr).map((it: string) => {
          return {
            label: it,
            value: it,
            key: it,
          };
        });
      } catch (e) {
        options = [];
        console.error(optionsStr);
        console.error(e);
      }
    }
    const label =
      (memaryMap[key] &&
        memaryMap[key]["memaryName"] &&
        `${key}(${memaryMap[key]["memaryName"]})`) ||
      key;
    max = max > label.length ? max : label.length;
    listUi.push({
      label: label,
      value: key,
      type: options ? "select" : "input",
      options: options,
    });
  }
  formUi.value.labelWidth = `${max * 15}px`;
  formUi.value.listUi = listUi;
  form.value = {
    ...map,
  };
} else if (msgType === "list") {
  question.value = props.message.data.meta?.question || "";

  formUi.value.type = "form";
  formUi.value.labelWidth = "0px";
  formUi.value.listUi = [
    {
      label: "",
      value: "index",
      type: "radio",
      options: (props.message.data?.data || []).map(
        (it: { index: string; title: string; value: string }) => {
          return {
            index: it.index,
            label: it.title,
            value: it.value,
          };
        }
      ),
    },
  ];
  rules.index = [{ required: true, message: "必须选择", trigger: "blur" }];
  form.value = {
    index: "",
  };
} else if (props.message.meta.type === "bool") {
  const data = props.message.data.data;
  question.value = data.question || "";

  formUi.value.type = "form";
  formUi.value.labelWidth = "10px";
  formUi.value.listUi = [
    {
      label: "",
      value: "anwser",
      type: "boolean",
    },
  ];
  form.value = {
    question: data.question,
    anwser: data.anwser,
  };
}

const submit = async function (formEl: FormInstance | undefined) {
  if (!formEl) return;
  await formEl.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const res = await util.eventMessage({
          id: props.message.meta.serverId,
          eventType: "submit",
          meta: {
            ...form.value,
          },
          mapData: {
            ...form.value,
          },
        });
        ElMessage.success("操作成功");
        console.log("event submint", res);
        if (res.show) {
          window.showErrorCode(
            window.decodeURIComponent(
              JSON.stringify({
                code: 0,
                id: res.messageId,
                message: res.data,
              })
            )
          );
        }
      } catch (e) {
        console.error(e);
        ElMessage.error("操作失败");
      }
    }
  });
};
</script>

<style scoped>
.question {
  margin: 10px 0;
}

.sc-message--text {
  /* padding: 5px 20px;
  border-radius: 6px; */
  font-weight: 300;
  font-size: 14px;
  position: relative;
  -webkit-font-smoothing: subpixel-antialiased;
  background-color: rgb(39, 39, 39);
}
.message-item {
  display: flex;
  width: 100%;
  align-items: center;
  height: 36px;
}
</style>
