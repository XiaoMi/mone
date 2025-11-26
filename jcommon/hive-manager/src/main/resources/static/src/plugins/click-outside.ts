import type { DirectiveBinding } from 'vue'

interface ClickOutsideElement extends HTMLElement {
  _clickOutside?: (event: Event) => void;
}

const clickOutsideElements: Set<{el: ClickOutsideElement, callback: Function}> = new Set()

export const vClickOutside = {
  mounted(el: ClickOutsideElement, binding: DirectiveBinding) {
    if (!clickOutsideElements.size) {
      document.addEventListener('click', handleClickOutside)
    }
    clickOutsideElements.add({el, callback: binding.value})
  },
  unmounted(el: ClickOutsideElement) {
    clickOutsideElements.forEach(item => {
      if (item.el === el) {
        clickOutsideElements.delete(item)
      }
    })
    if (!clickOutsideElements.size) {
      document.removeEventListener('click', handleClickOutside)
    }
  }
}

function handleClickOutside(event: Event) {
  clickOutsideElements.forEach(({el, callback}) => {
    if (!(el === event.target || el.contains(event.target as Node))) {
      callback(event)
    }
  })
} 