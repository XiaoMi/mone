import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

interface Edit {
    isEdit: boolean
}

export const useEditStore = defineStore('edit', () => {
  const edit  = ref<Edit>({
    isEdit: true
  })

  const _showApprove = ref<boolean>(false);

  const showApprove = computed(() => {
    return _showApprove.value;
  })

  function setShowApprove (value: boolean) {
    _showApprove.value = value;
  }

  function disableEdit () {
    edit.value.isEdit = false
  }

  function enableEdit () {
    edit.value.isEdit = true
  }

  return { edit, enableEdit, disableEdit, showApprove, setShowApprove }
})
