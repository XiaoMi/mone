import LLMImg from '../imgs/icon-LLM.png'
import CodeImg from '../imgs/icon-Code.png'
import KnowledgeImg from '../imgs/icon-Knowledge.png'
import ConditionImg from '../imgs/icon-Condition.png'
import StartImg from '../imgs/icon-Start.png'
import EndImg from '../imgs/icon-End.png'
import PluginImg from '../imgs/icon-plugin.png'
const returnSpotPostion = (nodeId) => {
  const startNode = document.getElementById(nodeId)
  const startNodeH = startNode.offsetHeight
  const startNodeW = startNode.offsetWidth
  const startTransform = startNode.style.transform
  const numStr = startTransform
    .replace(/translate\(/g, '')
    .replace(/\)/g, '')
    .replace(/px/g, '')
  const startArr = numStr.split(',').map((item) => Number(item))
  const spotsY = startArr[1] + startNodeH / 2
  const spots = {
    left: {
      x: startArr[0],
      y: spotsY
    },
    right: {
      x: startArr[0] + startNodeW,
      y: spotsY
    }
  }
  return spots
}
const validateRef = (rule, value, callback, curObj) => {
  const { referenceInfo, type } = curObj
  if (type == 'reference' && (!referenceInfo || (referenceInfo && referenceInfo.length == 0))) {
    callback(new Error('参数值不可为空'))
  } else if (type == 'value' && !curObj.value) {
    callback(new Error('参数值不可为空'))
  } else {
    callback()
  }
}

const validateRef2 = (rule, value, callback, curObj) => {
  const { referenceInfo2, type2 } = curObj
  if (type2 == 'reference' && (!referenceInfo2 || (referenceInfo2 && referenceInfo2.length == 0))) {
    callback(new Error('参数值不可为空'))
  } else if (type2 == 'value' && !curObj.value2) {
    callback(new Error('参数值不可为空'))
  } else {
    callback()
  }
}

const validPName = (rule, value, callback, arr) => {
  const reg = /^[a-zA-Z_]([a-zA-Z0-9_]+)?$/
  if (!value) {
    callback(new Error('参数名不可为空'))
  } else if (!reg.test(value)) {
    callback(new Error('以字母或下划线开头且仅包含字母,数字,下划线'))
  } else {
    const newA = arr || []
    const names = newA.map((item) => item.name)
    const newL = new Set(names).size
    const preL = newA.length
    if (newL != preL) {
      callback(new Error('参数不可重复!'))
    } else {
      callback()
    }
  }
}
// type 用户后端传值
const nodesBase = {
  llm: {
    type: 'llm',
    width: 500,
    imgSrc: LLMImg,
    title: '大模型',
    desc: '调用大语言模型，使用变量和提示词生成回复'
  },
  code: {
    type: 'code',
    width: 500,
    imgSrc: CodeImg,
    title: '代码',
    desc: '编写代码，处理输入变量来生成返回值'
  },
  plugin: {
    type: 'plugin',
    width: 500,
    imgSrc: PluginImg,
    title: '插件',
    desc: '填写输入输出参数'
  },
  precondition: {
    type: 'precondition',
    width: 600,
    imgSrc: ConditionImg,
    title: '选择器',
    desc: '连接两个上下游分支，如果设定的条件成立则只运行“如果”分支，不成立则只运行否定分支'
  },
  knowledge: {
    type: 'knowledge',
    width: 420,
    imgSrc: KnowledgeImg,
    title: '知识库',
    desc: '在选定的知识中，根据输入变量召回最匹配的信息，并以列表形式返回'
  },
  begin: {
    type: 'begin',
    width: 320,
    imgSrc: StartImg,
    title: '开始',
    desc: '工作流的起始节点，用于设定启动工作流需要的信息'
  },
  end: {
    type: 'end',
    width: 470,
    imgSrc: EndImg,
    title: '结束',
    desc: '工作流的最终节点，用于返回工作流运行后的结果信息'
  },
  nodeif: {
    type: 'nodeif',
    width: 470,
    imgSrc: StartImg,
    title: 'IF',
    desc: '条件-if'
  },
  nodeelse: {
    type: 'nodeelse',
    width: 470,
    imgSrc: StartImg,
    title: 'ELSE',
    desc: '条件-else'
  }
}
const knowledgeTree = [
  {
    value: 'outputList',
    label: 'outputList',
    desc: 'Array<Object>',
    children: [
      {
        value: 'output',
        label: 'output',
        desc: 'String'
      }
    ]
  }
]

const opList = [
  {
    value: 'EQUALS',
    label: '等于'
  },
  {
    value: 'NOT_EQUALS',
    label: '不等于'
  },
  {
    value: 'GREATER_THAN',
    label: '大于'
  },
  {
    label: '小于',
    value: 'LESS_THAN'
  },
  {
    label: '包含',
    value: 'CONTAINS'
  }
]
export {
  returnSpotPostion,
  nodesBase,
  validateRef,
  validateRef2,
  validPName,
  knowledgeTree,
  opList
}
