import Flowchart from './components/flowchart/Flowchart';

const components = [
  Flowchart,
];

const install = function(Vue) {
  components.map(component => {
    Vue.component(component.name, component);
  });
};

if (typeof window !== 'undefined' && window.Vue) {
  install(window.Vue);
}

export default {
  install,
  Flowchart,
};
