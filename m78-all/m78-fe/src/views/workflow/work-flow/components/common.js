//定义一个getValueType方法；这个方法有两个参数：级联结果数组arrRes(结构为['11','dd'])，树形结构的数组arrTree（结构为[{name: 'str', type: 'String', referenceInfo: Array(0), referenceNodeId: 0, referenceName: '',children:[{name: 'str', type: 'String', referenceInfo: Array(0), referenceNodeId: 0, referenceName: '',children:[]]}]，children的结构和arrTree结构相同），arrTree的key是name；根据arrRes获取出arrTree中所选择的元素,这个返回的元素应该是个对象；这应该是个递归方法；
function getValueType(arrRes, arrTree) {
  let res = null
  for (let i = 0; i < arrRes.length; i++) {
    const item = arrRes[i]
    for (let j = 0; j < arrTree.length; j++) {
      const treeItem = arrTree[j]
      if (treeItem.name === item) {
        res = treeItem
        break
      } else {
        if (treeItem.children && treeItem.children.length > 0) {
          res = getValueType(arrRes, treeItem.children)
          if (res) {
            break
          }
        }
      }
    }
    if (res) {
      break
    }
  }
  return res
}

export { getValueType }
