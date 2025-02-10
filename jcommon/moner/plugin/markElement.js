// 为页面元素添加标记
function markElements(configs) {
  try {
    // 获取当前页面信息
    const currentUrl = window.location.href;
    const currentDomain = window.location.hostname;
    
    // 过滤匹配当前页面的配置
    const matchedConfigs = configs.filter(config => {
      if (config.url) {
        // 如果配置了具体URL，进行精确匹配
        return currentUrl.includes(config.url);
      } else if (config.domain) {
        // 如果只配置了域名，进行域名匹配
        return currentDomain.includes(config.domain);
      }
      return false;
    });

    console.log('匹配的配置:', matchedConfigs);
    
    // 为匹配的配置添加标记
    matchedConfigs.forEach(config => {
      const elements = document.querySelectorAll(config.selector);
      console.log('找到的元素:', elements);
      
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
