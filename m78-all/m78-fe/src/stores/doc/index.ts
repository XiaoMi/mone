import type { DocStatus } from './helper'
import { defineStore } from 'pinia'
export const useExcleStore = defineStore('doc-store', {
  state: (): DocStatus => {
    return {
      value: [
        { code: 3, text: 'excle.failed', tagType: 'danger', tagClass: 'tag-item-danger' },
        { code: 2, text: 'excle.parsing', tagType: '', tagClass: 'tag-item-primary' },
        { code: 1, text: 'excle.success', tagType: '', tagClass: 'normal' }
      ]
    }
  }
})
