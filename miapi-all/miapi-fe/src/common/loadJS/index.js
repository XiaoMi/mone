function loadJS (src, attr) {
  return new Promise((resolve, reject) => {
    if (document.querySelector(`script[src="${src}"]`)) {
      resolve()
    } else {
      let script = document.createElement('script')
      if (attr && Object.keys(attr)) {
        Object.keys(attr).forEach(key => {
          script[key] = attr[key]
        })
      }
      script.type = "text/javascript"
      script.src = src
      document.body.appendChild(script)

      script.onload = () => {
        resolve()
      }
      script.onerror = () => {
        reject()
      }
    }
  })
}

export default loadJS
