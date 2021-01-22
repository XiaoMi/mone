/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

/**
 * 拖拽、自动贴边插件
 */
let drag = options => {

  let { domShell } = options; 
  let disX, disY;

  domShell.addEventListener('mousedown', e => {
    disX = e.pageX - parseInt(domShell.getElePosition().x);
    disY = e.pageY - parseInt(domShell.getElePosition().y);

    // 阻止[文档中文字选中]默认行为
    document.addEventListener('selectstart', e => e.preventDefault());

    document.addEventListener('mousemove', mouseMove);

    document.addEventListener('mouseup', e => {
      document.removeEventListener('mousemove',mouseMove);
      domShell.style.cursor = 'pointer';
      domShell.style.color = '#1890ff';

      // 贴边处理
      if (domShell.getElePosition().x >= window.innerWidth/2) {
        domShell.style.left = window.innerWidth - domShell.offsetWidth + 'px'
      } else {
        domShell.style.left = '0px'
      }

      if (domShell.getElePosition().y <= 0) {
        domShell.style.top = '0px'
      } else if (domShell.getElePosition().y + domShell.offsetHeight >= window.innerHeight) {
        domShell.style.top = window.innerHeight - domShell.offsetHeight + 'px'
      }
    })

    function mouseMove(e) {
      domShell.style.cursor = 'not-allowed';
      domShell.style.color = '#b0d8fd';
      domShell.style.left = e.pageX - disX + 'px';
      domShell.style.top = e.pageY - disY + 'px'
    }
  })
}
// 获取元素相对于文档的距离
Element.prototype.getElePosition = function() {
  if(!this.offsetParent) {
    return {
      x: this.offsetLeft,
      y: this.offsetTop
    }
  }

  let nowLeft = this.offsetLeft,
      nowTop = this.offsetTop,
      ele = this.offsetParent;
  while (ele) {
    nowLeft += ele.offsetLeft;
    nowTop += ele.offsetTop;
    ele = ele.offsetParent;
  }
  return {
    x: nowLeft,
    y: nowTop
  }
}
export default drag