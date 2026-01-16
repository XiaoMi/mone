# Flowchart

Flowchart & Flowchart designer component for Vue.js.

English | [中文](https://github.com/joyceworks/flowchart-vue/blob/master/README-zh_CN.md)

## Usage

```shell script
yarn add flowchart-vue
```

```vue
<template>
    <div id="app">
        <button type="button" @click="$refs.chart.add({id: +new Date(), x: 10, y: 10, name: 'New', type: 'operation', approvers: []})">
            Add
        </button>
        <button type="button" @click="$refs.chart.remove()">
            Del
        </button>
        <button type="button" @click="$refs.chart.editCurrent()">
            Edit
        </button>
        <button type="button" @click="$refs.chart.save()">
            Save
        </button>
        <flowchart :nodes="nodes" :connections="connections" @editnode="handleEditNode"
                    @dblclick="handleDblClick" @editconnection="handleEditConnection" 
                    @save="handleChartSave" ref="chart">
        </flowchart>
    </div>
</template>
<script>
  import Vue from 'vue';
  import FlowChart from 'flowchart-vue';

  Vue.use(FlowChart);

  export default {
    name: 'App',
    data: function() {
      return {
        nodes: [
          // Basic fields
          {id: 1, x: 140, y: 270, name: 'Start', type: 'start'},
          // You can add any generic fields to node, for example: description
          // It will be passed to @save, @editnode
          {id: 2, x: 540, y: 270, name: 'End', type: 'end', description: 'I'm here'},
        ],
        connections: [
          {
            source: {id: 1, position: 'right'},
            destination: {id: 2, position: 'left'},
            id: 1,
            type: 'pass',
          },
        ],
      };
    },
    methods: {
      handleChartSave(nodes, connections) {
        // axios.post(url, {nodes, connections}).then(resp => {
        //   this.nodes = resp.data.nodes;
        //   this.connections = resp.data.connections;
        //   // Flowchart will refresh after this.nodes and this.connections changed
        // });
      },
      handleEditNode(node) {
        if (node.id === 2) {
          console.log(node.description);
        }
      },
      handleEditConnection(connection) {
      },
      handleDblClick(position) {
        this.$refs.chart.add({
          id: +new Date(),
          x: position.x,
          y: position.y,
          name: 'New',
          type: 'operation',
          approvers: [],
        });
      },
    }
  };
</script>
```

See more at [src/views/App.vue](https://github.com/joyceworks/flowchart-vue/blob/master/src/views/App.vue).

## Demo

- [GitHub Pages](https://joyceworks.github.io/flowchart-vue/)

- Development Environment

  ``` shell
  git clone https://github.com/joyceworks/flowchart-vue.git
  cd flowchart-vue
  yarn install
  yarn run serve
  ```
  
  Then open http://localhost:yourport/ in browser.

## API

See [Wiki](https://github.com/joyceworks/flowchart-vue/wiki).
