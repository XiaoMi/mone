import FlowChart from './Flowchart';

FlowChart.install = function(Vue) {
  Vue.component(FlowChart.name, FlowChart);
};

export default FlowChart;