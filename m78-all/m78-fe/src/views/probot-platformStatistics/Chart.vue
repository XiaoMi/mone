<template>
  <div ref="chart" class="chart-content" @click="handleChartClick"></div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import * as Echarts from 'echarts'

const props = defineProps({
  title: {
    type: String
  },
  subtext: {
    type: String
  },
  data: {
    type: Array,
    default: () => []
  }
})
const chart = ref(null)
let chartInstance = null

const getOption = () => {
  try {
    return {
      title: {
        text: props.title,
        subtext: props.subtext,
        left: 'center'
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b} : {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: props.title,
          type: 'pie',
          radius: '50%',
          top: '10%',
          data: props.data,
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
  } catch (e) {
    console.log(e)
    return {}
  }
}

const resize = () => {
  chartInstance?.resize()
}

const renderChart = () => {
  const option = getOption()
  chartInstance.setOption(option)
}
watch(
  () => props.data,
  () => {
    renderChart()
  }
)
const emits = defineEmits(['chatClick'])

onMounted(() => {
  chartInstance = Echarts.init(chart.value)
  renderChart()
  chartInstance.on('click', function (param) {
    emits('chatClick', param)
  })
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
})
</script>

<style lang="scss" scoped>
.chart-content {
  width: 100%;
  height: 100%;
  overflow: auto;
}
</style>
