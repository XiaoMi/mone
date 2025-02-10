<template>
  <div class="chart-box">
    <div ref="chart" class="chart-content"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as Echarts from 'echarts'

const props = defineProps({
  chartType: {
    type: String
  },
  data: {
    type: String
  }
})
const chart = ref(null)
let chartInstance = null

const getOption = () => {
  try {
    const tryJson = JSON.parse(props.data)
    if (!tryJson) return
    return {
      xAxis: {
        type: 'category',
        data: tryJson.xSeries,
        name: tryJson.xAxisName
      },
      yAxis: {
        type: 'value',
        name: tryJson.yAxisName
      },
      series: [
        {
          data: tryJson.ySeries,
          type: props.chartType === 'chartColumnar' ? 'bar' : ''
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
  chartInstance = Echarts.init(chart.value)
  chartInstance.setOption(option)
}

onMounted(() => {
  renderChart()
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
})
</script>

<style lang="scss" scoped>
.chart-box {
  margin-bottom: 25px;
  .chart-content {
    width: 400px;
    height: 300px;
    overflow: auto;
  }
}
</style>
