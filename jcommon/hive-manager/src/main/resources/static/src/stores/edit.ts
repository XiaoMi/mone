import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

interface Edit {
  isEdit: boolean
  showFollow: boolean
  isFollow: boolean
}

export const useEditStore = defineStore('edit', () => {
  const edit = ref<Edit>({
    isEdit: true,
    showFollow: false,
    isFollow: true,
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
  }

  function disableEdit() {
    edit.value.isEdit = false
  }

  function enableEdit() {
    edit.value.isEdit = true
  }

  return { edit, enableEdit, disableEdit, showApprove, setShowApprove, setShowFollow, setIsFollow, isFollow, showFollow }
})
