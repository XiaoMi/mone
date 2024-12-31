window.utils = {}
window.utils.getQuery = (key, url) => {
  url = url || window.location.href + ''
  if (url.indexOf('#') !== -1) url = url.substring(url.indexOf('#'))
  let rts = []
  let rt
  const queryReg = new RegExp('(^|\\?|&)' + key + '=([^&]*)(?=&|#|$)', 'g')
  while ((rt = queryReg.exec(url)) !== null) {
    rts.push(decodeURIComponent(rt[2]))
  }
  if (rts.length === 0) return null
  if (rts.length === 1) return rts[0]
  return rts
}