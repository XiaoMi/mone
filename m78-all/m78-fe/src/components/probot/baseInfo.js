const ymdOptions = [
  {
    value: 'day',
    label: '每天'
  },
  {
    value: 'week',
    label: '每周'
  },
  {
    value: 'month',
    label: '每月'
  }
]
const arr = Array.from({ length: 31 }, (_, i) => 1 + i)
const daysOps = arr.map((item) => {
  return {
    label: item + '号',
    value: item + ''
  }
})
const weekDays = [
  {
    value: '0',
    label: '周一'
  },
  {
    value: '1',
    label: '周二'
  },
  {
    value: '2',
    label: '周三'
  },
  {
    value: '3',
    label: '周四'
  },
  {
    value: '4',
    label: '周五'
  },
  {
    value: '5',
    label: '周六'
  },
  {
    value: '6',
    label: '周日'
  }
]
export { ymdOptions, daysOps, weekDays }
