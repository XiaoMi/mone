const observer = new MutationObserver(function (mutations) {
  mutations.forEach(function (mutation) {
    // 检查是否有子元素变化
    if (mutation.type === 'childList') {
      mutation.addedNodes.forEach(function (addedNode) {
        // 检查是否有class为aie-codeblock-tools-comments的子元素
        const commentsElement = (addedNode as HTMLElement).querySelector(
          '.aie-codeblock-tools-comments'
        )
        console.log('commentsElement', commentsElement)
        if (commentsElement) {
          const newChild = document.createElement('div')
          newChild.className = 'new-child'
          newChild.innerText = 'New Comment'
          commentsElement.replaceWith(newChild)
        }
      })
    }
  })
})

const config = { childList: true, subtree: true }

export function addListener(container: any) {
  observer.observe(container, config)
}
