<template>
  <div ref="chart"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import * as Echarts from 'echarts'

const props = defineProps({
  data: {}
})
const chart = ref(null)
let chartInstance = null

const resize = () => {
  chartInstance?.resize()
}

const renderChart = async (val) => {
  if (val && val.xSeries) {
    const option = {
      xAxis: {
        type: 'category',
        data: val.xSeries
        // name: tryJson.xAxisName
      },
      yAxis: {
        type: 'value'
        // name: tryJson.yAxisName
      },
      series: [
        {
          data: val.ySeries,
          type: 'line'
        }
      ],
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'cross',
          crossStyle: {
            color: '#999'
          }
        },
        formatter: function (params) {
          let firstParams = params[0]
          return (
            '日期：' +
            firstParams.name +
            '  ' +
            '<br>' +
            '数量：' +
            firstParams.data 
          )
        }
      }
    }
    chartInstance = Echarts.init(chart.value)
    chartInstance.setOption(option)
  }
}

onMounted(() => {
  renderChart(props.data)
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
})

watch(
  () => props.data,
  (newVal) => {
    nextTick(() => {
      renderChart(newVal)
    })
  },
  {
    immediate: true,
    deep: true
  }
)
</script>

<style lang="scss" scoped></style>
