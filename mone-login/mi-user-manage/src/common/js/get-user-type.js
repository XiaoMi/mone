export function retUserNameType(userName, type) { // 获取用户来源
  const { UserTypeEnum } = this.$store.state.NodeModule;
  const res = UserTypeEnum.filter((item) => item.k === type);
  if (res.length !== 1) return '';
  return `${userName}(${res[0].v})`;
}
export function test() {
  console.log('test---->>>>');
}
