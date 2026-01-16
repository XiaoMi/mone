# Flowchart

Vue.js 流程图&流程图设计器。

[English](https://github.com/joyceworks/flowchart-vue/blob/master/README-zh_CN.md) | 中文

## 使用

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
          // 仅必须字段
          {id: 1, x: 140, y: 270, name: 'Start', type: 'start'},
          // 你可以添加任意字段，例如: description
          // 该字段可以从 @save, @editnode 事件中访问到
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
        //   // 当 this.nodes 或 this.connections 改变时流程图会自动刷新
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

查看更多：[src/views/App.vue](https://github.com/joyceworks/flowchart-vue/blob/master/src/views/App.vue).

## Demo

- [GitHub Pages](https://joyceworks.github.io/flowchart-vue/)

- 开发环境

  ``` shell
  git clone https://github.com/joyceworks/flowchart-vue.git
  cd flowchart-vue
  yarn install
  yarn run serve
  ```
  
  然后在浏览器打开 http://localhost:yourport/ .

## API

见 [Wiki](https://github.com/joyceworks/flowchart-vue/wiki).
