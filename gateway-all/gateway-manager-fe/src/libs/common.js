// 添加成员接口/project/addMembers 入参 members参数改成数组对象类型 [{id:*}]
export const membersParamChange = (value) => {
  const param = []
  value.forEach(id => {
    param.push({ id })
  })
  return param
}
