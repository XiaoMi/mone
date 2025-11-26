<template>
  <div class="header">
    <el-icon
      :size="20"
      @click.native.prevent="showImageDialog"
    >
      <Picture />
    </el-icon>
  </div>
  <el-dialog
    v-model="dialogFormVisible"
    title="图片描述"
    width="600px"
  >
    <el-form :model="form" label-width="60px">
      <el-form-item label="图片">
        <div
          ref="imageContent"
          style="
            overflow: auto;
            width: 100%;
            min-height: 40px;
            max-height: 260px;
            border: 1px solid #636466;
            border-radius: 2px;"
          contentEditable="true"
        ></div>
      </el-form-item>
      <el-form-item label="描述" props="text">
        <el-input type="textarea" v-model="form.text" autocomplete="off" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitImage">
          发送
        </el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>
<script setup lang="ts">
import { ref, onMounted } from "vue";

const emits = defineEmits(['submitImage']);

const imageContent = ref<HTMLImageElement | null>(null);

const form = ref({
    text: '',
    image_url: '',
});

const dialogFormVisible = ref(false);

onMounted(() => {
  
});

async function updateImage() {
  setTimeout(async () => {
    if (imageContent.value) {
      imageContent.value.innerHTML = "";
      const clipboardItems = await navigator.clipboard.read();
      for (const clipboardItem of clipboardItems) {
        for (const type of clipboardItem.types) {
          if (type.startsWith("image")) {
            const blob = await clipboardItem.getType(type);
            const fr = new FileReader();
            fr.onload = (e) => {
              const base64Data:string = e.target?.result as string;
              (imageContent.value as HTMLImageElement).innerHTML = `<img style="width:100%;" src='${base64Data}'>`;
            }
            fr.readAsDataURL(blob);
          } 
        }
      }
    }
  }, 10)
}

async function showImageDialog() {
  form.value = {
    text: '',
    image_url: '',
  };
  dialogFormVisible.value = true;
  updateImage();
}

async function submitImage() {
  const img = imageContent.value?.querySelector('img');
  form.value.image_url = "";
  if (img) {
    form.value.image_url = img.src;
  }
  emits("submitImage", {
    ...form.value
  });
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
