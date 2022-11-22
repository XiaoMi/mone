/**
 * Cookie相关操作
 */
export default {
  get (name) {
    let nameEQ = name + '='
    let ca = document.cookie.split(';')
    for (let i = 0; i < ca.length; i++) {
      let c = ca[i]
      while (c.charAt(0) === ' ') c = c.substring(1, c.length)
      if (c.indexOf(nameEQ) === 0) {
        return decodeURIComponent(c.substring(nameEQ.length, c.length))
      }
    }
    return null
  },
  set (name, value, days, path, domain, secure) {
    let expires
    if (isNaN(days)) {
      let date = new Date()
      date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000))
      expires = date.toUTCString()
    } else {
      expires = false
    }
    document.cookie = name + '=' + encodeURIComponent(value) +
        (expires ? (';expires=' + expires) : '') +
        (path ? (';path=' + path) : '') +
        (domain ? (';domain=' + domain) : '') +
        (secure ? ';secure' : '')
  },
  del (name, path, domain, secure) {
    this.set(name, '', -1, path, domain, secure)
  }
}
