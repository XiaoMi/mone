function distanceOfPointToLine(beginX, beginY, endX, endY, ptX, ptY) {
  const k = (endY - beginY || 1) / (endX - beginX || 1);
  const b = beginY - k * beginX;
  return Math.abs(k * ptX - ptY + b) / Math.sqrt(k * k + 1);
}

function between(num1, num2, num) {
  return (num > num1 && num < num2) || (num > num2 && num < num1);
}

function approximatelyEquals(n, m) {
  return Math.abs(m - n) <= 3;
}

function getEdgeOfPoints(points) {
  let minX = points.reduce((prev, point) => {
    return point.x < prev ? point.x : prev;
  }, Infinity);
  let maxX = points.reduce((prev, point) => {
    return point.x > prev ? point.x : prev;
  }, 0);
  let minY = points.reduce((prev, point) => {
    return point.y < prev ? point.y : prev;
  }, Infinity);
  let maxY = points.reduce((prev, point) => {
    return point.y > prev ? point.y : prev;
  }, 0);
  return {start: {x: minX, y: minY}, end: {x: maxX, y: maxY}};
}

function pointRectangleIntersection(p, r) {
  return p.x > r.start.x && p.x < r.end.x && p.y > r.start.y && p.y < r.end.y;
}

function roundTo20(number) {
  return number < 20 ? 20 : number;
}

export {
  distanceOfPointToLine,
  between,
  approximatelyEquals,
  getEdgeOfPoints,
  pointRectangleIntersection,
  roundTo20,
};
