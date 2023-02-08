/*
 * Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/* eslint-disable */
/**
 * @class 流程节点
 * @param {Object} container      节点容器（画布），jquery对象
 * @param {String} id      节点id
 * @param {String} name    节点名称
 * @param {Number} x       节点x坐标
 * @param {Number} y       节点y坐标
 * @param {Object} [options] 节点附加属性
 * @param {String} [options.color] 节点文字颜色
 * @param {String} [options.bgColor] 节点背景色
 * @param {Number} [options.radius] 节点圆角大小
 * @param {Number} [options.data] 绑定到节点的附加数据
 * @param {Number} [options.container] 节点容器（画布），若设置此选项则会自动将节点添加到画布上
 * @param {Boolean} [options.removable=true] 是否支持删除功能（鼠标放上去显示关闭图标）
 */
import $ from "jquery";
import jsPlumb from "jsplumb"
let ChartNode = function(id, name, x, y, options) {
    this._jsPlumb = null;
    this._container = null;
    this._id = id;
    this._name = name;
    this._x = x;
    this._y = y;
    this._clsName = options.class || '';
    this._data = options && options.data || {};
    this._data.nodeId = id;
    this._options = $.extend({ // 默认属性
        removable: true
    }, options);
    this._el = null;

    if (options && options.container) {
        this.appendTo(options.container);
    }
};

/**
 * 连线样式
 * @type {Object}
 */
ChartNode.lineStyle = {
    lineWidth: 3,
    joinstyle: "Bezier",
    strokeStyle: "#0096f2"
};

/**
 * 标签位置
 */
ChartNode.labelPos = {
    'Bottom': [6, 2.5],
    'Top': [6, -2.5],
};

ChartNode.prototype.setPlumb = function (plumb) {
    this._jsPlumb = plumb;
};

ChartNode.prototype._px = (value) => {
    return value + 'px';
};

ChartNode.prototype.getId = function() {
    return this._id;
};

ChartNode.prototype.getData = function() {
    return this._data || {};
};

ChartNode.prototype.appendTo = function(container) {
    if (! container) {
        console.error('node container is null !');
        return;
    }

    let self = this;
    let options = self._options;
    let px = self._px;

    // 创建并插入 dom 节点
    let node = $('<div>').addClass(`window task ${self._clsName}`)
        .attr('id', self._id)
        .css({
            left: px(self._x),
            top: px(self._y)
        })
        .text(self._name)
        .data('node', this._data)
        .data('__node', this);

    if (options.removable) {
        let removeIcon = $('<div>').addClass('remove');
        node.append(removeIcon);
    }

    container.append(node);
    this._jsPlumb.draggable(node, 
        {
        grid: [10, 10] ,
        containment:"parent"
        });

    this._el = node;
};

/**
 * 添加连接端口
 * @param {Object} options 连接端口参数
 * @param {String} [options.color=#0096f2] 端口颜色
 * @param {Boolean} [options.isSource=false] 是否为源端口
 * @param {Boolean} [options.isTarget=false] 是否为目标端口
 * @param {String} [options.label] 端口名称
 * @param {String} [options.position=bottom] 端口位置，可设置为 'Top'
 */
ChartNode.prototype.addPort = function(options) {
    let pos = options.position || 'Bottom';
    let labelPos = ChartNode.labelPos[pos];
    let endpointConf = {
        endpoint: "Dot",
        paintStyle: {
            strokeStyle: options.color || '#0096f2',
            radius: 2,
            lineWidth: 4
        },
        anchor: pos,
        isSource: !!options.isSource,
        isTarget: !!options.isTarget,
        maxConnections: -1,
       connector: ["Straight", { stub: [0, 0], gap: 0, cornerRadius: 10, alwaysRespectStubs: true }],
      // connector:['Bezier'],
        connectorStyle: ChartNode.lineStyle,
        // connectorOverlays: [[ "Arrow", {location: 1}]],
        // hoverPaintStyle: endpointHoverStyle,
        // connectorHoverStyle: connectorHoverStyle,
        dragOptions: {},
        overlays: [
            ["Label", {
                location: labelPos,
                label: options.label || '',
                cssClass: "endpoint-label-lkiarest"
            }]
        ],
        allowLoopback:false
    };

    this._jsPlumb.addEndpoint(this._el, endpointConf);
};

/**
 * 更新坐标
 */
ChartNode.prototype.updatePos = function() {
    let el = this._el;
    this._x = parseInt(el.css("left"),10);
    this._y = parseInt(el.css("top"), 10);
};

ChartNode.prototype.getPos = function() {
    return {
        x: this._x,
        y: this._y
    };
};

ChartNode.prototype.toPlainObj = function() {
    let item = this;
    item.updatePos();

    let data = $.extend({}, item._data);
    data.nodeId = item._id;
    data.positionX = item._x;
    data.positionY = item._y;
    data.className = item._clsName;
    data.removable = item._options.removable;

    return data;
};

ChartNode.prototype.dispose = function() {
    let el = this._el;
    let domEl = el.get(0);
    this._jsPlumb.detachAllConnections(domEl);
    this._jsPlumb.remove(domEl);
    el.remove();
};

/**
 * @class 画布
 */
let Chart = function(container, options) {
    this._jsPlumb = null; // 多实例支持！
    this._container = container;
    this._nodes = [];
    this._seedName = 'flow-chart-node';
    this._seedId = 0;

    this.init(options);
};

Chart.prototype.nodeId = function() {
    return this._seedName + (this._seedId++) + (new Date).valueOf();
};

/**
 * 初始化方法
 * @param  {Object} [options] 初始化参数
 * @param {Function} [options.onNodeClick] 节点点击事件回调函数，参数为节点绑定的数据
 */
Chart.prototype.init = function(options) {
    this._jsPlumb = jsPlumb.getInstance();
    this._jsPlumb.importDefaults({
        // DragOptions: { cursor: 'pointer', zIndex: 2000 },
        ConnectionOverlays: [ ['Arrow', { width: 12, length: 12, location: 0.5 }] ],
        DragOptions : { cursor: 'pointer', zIndex:2000 },
        EndpointStyles : [{ fillStyle:'#225588' }, { fillStyle:'#558822' }],
        Endpoints : [ [ "Dot", { radius:2 } ], [ "Dot", { radius: 2 } ]],
        Connector:[ "Straight", { stub:[0, 0], gap:0, cornerRadius:5, alwaysRespectStubs:true } ],
        // ConnectionOverlays : [
        //     [ "Arrow", { 
        //         location:1,
        //         id:"arrow",
        //         length:20,
        //         foldback:0.4
        //     } ]
        // ]
    });

    this._container.addClass('flow-chart-canvas-lkiarest');

    // 点击事件
    if (options && options.onNodeClick) {
        this._container.on('click', '.task', event => {
            let target = $(event.target);
            options.onNodeClick.call(this, target.data('node'));
        });
    }
    // 删除节点
    this._container.on('click', '.remove', event => {
        let delNode = $(event.target).parent().data('__node');
        if (delNode) {
            let data = delNode.getData();
            let nodeId = delNode.getId();
            delNode.dispose();

            this.removeNode(nodeId);

            if (options && options.onNodeDel) {
                options.onNodeDel.call(this, data);
            }
        }

        event.stopPropagation();
    });
};

/**
 * 添加新节点
 * @param {String} name    节点名称
 * @param {Number} x       节点x坐标
 * @param {Number} y       节点y坐标
 * @param {Object} options 节点参数，可参考 {class ChartNode} 构造参数
 * @param {String} [options.id] 节点id，若未定义则由系统自动分配
 */
Chart.prototype.addNode = function(name, x, y, options) {
    let id = options && options.id || this.nodeId();
    let node = new ChartNode(id, name, x, y, options);
    node.setPlumb(this._jsPlumb);
    node.appendTo(this._container);
    this._nodes.push(node);
    return node;
};

Chart.prototype.removeNode = function(nodeId) {
    let nodes = this._nodes;
    for (let i = 0, len = nodes.length; i < len; i++) {
        let node = nodes[i];
        if (node.getId() === nodeId) {
            node.dispose();
            nodes.splice(i, 1);
            return node;
        }
    }
};

Chart.prototype.getNodes = function() {
    return this._nodes;
};

/**
 * 序列化以保存
 */
Chart.prototype.toJson = function() {
    // 获取所有节点
    let nodes = [];
    this._nodes.forEach(item => {
        nodes.push(item.toPlainObj());
    });

    // 获取所有连接
    let connections = this._jsPlumb.getConnections().map(connection => {
        return {
            connectionId: connection.id,
            pageSourceId: connection.sourceId,
            pageTargetId: connection.targetId
        };
    });

    return {
        nodes: nodes,
        connections: connections
    };
};

/**
 * 反序列化保存的数据并绘制流程图
 */
Chart.prototype.fromJson = function(jsonStr) {
    if (!jsonStr || jsonStr === '') {
        console.error('draw from json failed: empty json string');
        return;
    }

    let jsonObj = null;

    try {
        jsonObj = JSON.parse(jsonStr);
    } catch (e) {
        console.error('invalid json string', e);
        return;
    }

    this.clear();

    let nodes = jsonObj.nodes;
    let connections = jsonObj.connections;

    nodes && nodes.forEach(item => {
        let node = this.addNode(item.name, item.positionX, item.positionY, {
            class: item.className,
            removable: item.removable,
            id: item.nodeId,
            data: item
        });

        switch(item.className) {
            case 'node-start': {
                node.addPort({
                    isSource: true
                });
                break;
            }
            case 'node-end': {
                node.addPort({
                    isTarget: true,
                    position: 'Top'
                });
                break;
            }
            default: {
                node.addPort({
                    isSource: true
                });

                node.addPort({
                    isTarget: true,
                    position: 'Top'
                });
            }
        }

        this._jsPlumb.repaint(node.getId());
    });

    connections && connections.forEach(item => {
        this._jsPlumb.connect({
            source: item.pageSourceId,
            target: item.pageTargetId,
            deleteEndpointsOnDetach:false,
            paintStyle: ChartNode.lineStyle,
            anchors: ["Bottom", [0.5, 0, 0, -1]]
        });
       
    });
  
    this._jsPlumb.repaintEverything();
};

/**
 * 清除画布中的元素
 */
Chart.prototype.clear = function() {
    this._nodes && this._nodes.forEach(item => {
        item.dispose();
    });

    this._nodes = [];
    this._jsPlumb.detachAllConnections(this._container);
    this._jsPlumb.removeAllEndpoints(this._container);
};

/**
 * 销毁释放
 */
Chart.prototype.dispose = function () {
    this.clear();
    this._container.off('click'); // unbind events
    this._container = null;
};

Chart.ready = (callback) => {
    jsPlumb.ready(callback);
};

if (typeof module === 'object' && module && typeof module.exports === 'object') {
    module.exports = Chart;
}
export default Chart
