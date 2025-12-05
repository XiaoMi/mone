import { format, parseISO } from 'date-fns'

export const formatDate = (dateString: string): string => {
  try {
    const date = parseISO(dateString)
    return format(date, 'yyyy-MM-dd HH:mm:ss')
  } catch {
    return dateString
  }
}

export const truncate = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) return text
  return text.slice(0, maxLength - 3) + '...'
}
