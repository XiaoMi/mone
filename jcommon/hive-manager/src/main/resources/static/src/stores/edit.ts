import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

interface Edit {
  isEdit: boolean
  showFollow: boolean
  isFollow: boolean
}

export const useEditStore = defineStore('edit', () => {
  // 从 localStorage 读取 isFollow 设置，默认为 true
  const savedFollowSetting = localStorage.getItem('chatFollowScroll');
  const initialIsFollow = savedFollowSetting ? JSON.parse(savedFollowSetting) : true;
  
  const edit = ref<Edit>({
    isEdit: true,
    showFollow: true,
    isFollow: initialIsFollow,
  })

  const _showApprove = ref<boolean>(false);

  const showApprove = computed(() => {
    return _showApprove.value;
  })

  const showFollow = computed(() => {
    return edit.value.showFollow;
  })

  const isFollow = computed(() => {
    return edit.value.isFollow;
  })

  function setShowApprove(value: boolean) {
    _showApprove.value = value;
  }

  function setShowFollow(value: boolean) {
    edit.value.showFollow = value;
  }

  function setIsFollow(value: boolean) {
    edit.value.isFollow = value;
    // 将设置保存到 localStorage
    localStorage.setItem('chatFollowScroll', JSON.stringify(value));
  }

  function disableEdit() {
    edit.value.isEdit = false
  }

  function enableEdit() {
    edit.value.isEdit = true
  }

  return { edit, enableEdit, disableEdit, showApprove, setShowApprove, setShowFollow, setIsFollow, isFollow, showFollow }
})
