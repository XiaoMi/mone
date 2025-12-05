import { useInput } from 'ink'
import { useEffect, useState } from 'react'

export const useKeyboard = (handlers: {
  onEnter?: () => void
  onEscape?: () => void
  onTab?: () => void
  onKey?: (key: string) => void
}) => {
  useInput((input, key) => {
    if (key.return && handlers.onEnter) {
      handlers.onEnter()
    }
    if (key.escape && handlers.onEscape) {
      handlers.onEscape()
    }
    if (key.tab && handlers.onTab) {
      handlers.onTab()
    }
    if (handlers.onKey) {
      handlers.onKey(input)
    }
  })
}

export const useDebounce = <T,>(value: T, delay: number): T => {
  const [debouncedValue, setDebouncedValue] = useState<T>(value)

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value)
    }, delay)

    return () => {
      clearTimeout(handler)
    }
  }, [value, delay])

  return debouncedValue
}
