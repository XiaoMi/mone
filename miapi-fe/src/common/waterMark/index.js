const drawWaterMark = ({
  className = 'water-node',
  width = 220,
  height = 160,
  position = 'absolute',
  top = 0,
  left = 0,
  zIndex = 1000,
  content = '这里是水印内容',
  fontSize = 14,
  fontFamily = 'PingFang SC, sans-serif',
  color = 'rgba(156, 162, 169, 0.1)',
  rotate = -15,
  bgHeight = "100%"
}) => {
  const option = {
    width,
    height,
    content,
    fontSize,
    font: `${fontSize}px ${fontFamily}`,
    color,
    rotateDegree: (rotate * Math.PI) / 180
  }

  const imgUrl1 = drawImg({
    ...option,
    x: 120,
    y: 100
  })
  const imgUrl2 = drawImg({
    ...option,
    x: 300,
    y: 220
  })

  let style = document.createElement('style')
  style.innerHTML = `.${className}:after {
    content: '';
    display: block;
    width: 100%;
    height: ${bgHeight};
    ${top || top === 0 ? `top: ${top}px;` : ''}
    ${left || left === 0 ? `left: ${left}px;` : ''}
    background-repeat: repeat;
    pointer-events: none;
    ${position ? `position: ${position}` : ''};
    ${zIndex ? `z-index:${zIndex}` : ''};
    background-image: url(${imgUrl1}), url(${imgUrl2});
    background-size: ${option.width * 2}px ${option.height}px;
  }`
  document.head.appendChild(style)
}

const drawImg = (options) => {
  const canvas = document.createElement('canvas')
  const text = options.content
  canvas.width = options.width * 2
  canvas.height = options.height
  const ctx = canvas.getContext('2d')
  if (ctx) {
    ctx.font = options.font
    ctx.fillStyle = options.color
    ctx.rotate(options.rotateDegree)
    ctx.textAlign = 'center'
    ctx.fillText(text, options.x, options.y)
    ctx.fillText('API平台', options.x, options.y - options.fontSize - 5)
  }
  return canvas.toDataURL('image/png')
}

export default drawWaterMark
