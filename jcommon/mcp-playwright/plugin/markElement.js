// 为页面元素添加标记
function markElements(configs) {
  try {
    // 遍历每个配置
    configs.forEach(config => {
      // 查找匹配的元素
      const elements = document.querySelectorAll(config.selector);
      console.log('elements:', elements);
      
      // 为每个元素添加标记
      elements.forEach(element => {
        element.setAttribute(config.key, config.value);
        console.log('标记element:', element);
      });
    });
  } catch (error) {
    console.error('标记元素失败:', error);
  }
}

// 导出函数到全局作用域,供background.js调用
window.markElements = markElements;
