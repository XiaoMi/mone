import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useCounterStore = defineStore('counter', () => {
  // state
  const count = ref(0)
  const name = ref('Vue 3 + TypeScript')

  // getters
  const doubleCount = computed(() => count.value * 2)

  // actions
  function increment() {
    count.value++
  }

  function decrement() {
    count.value--
  }

  function reset() {
    count.value = 0
  }

  function setName(newName: string) {
    name.value = newName
  }

  return {
    count,
    name,
    doubleCount,
    increment,
    decrement,
    reset,
    setName
  }
})

