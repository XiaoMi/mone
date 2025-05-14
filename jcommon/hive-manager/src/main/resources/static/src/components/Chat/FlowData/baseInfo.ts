const oneM: number = 60 * 1000;
const oneH: number = 60 * oneM;
const oneDay: number = 24 * oneH;

const formatTime = (mss: number): string => {
  let days: number = 0,
    hours: number = 0,
    minutes: number = 0,
    seconds: number = 0;

  seconds = Math.floor((mss % oneM) / 1000);
  if (mss > oneM) {
    minutes = Math.floor((mss % oneH) / oneM);
    if (mss > oneH) {
      hours = Math.floor((mss % oneDay) / oneH);
      if (mss > oneDay) {
        days = Math.floor(mss / oneDay);
      }
    }
  }

  return `${days ? days + 'd' : ''}${hours ? hours + 'h' : ''}${
    minutes ? minutes + 'm' : ''
  }${seconds}s`;
};

const flowStatusArr = [
  {
    code: 1,
    label: '运行开始',
    tagType: 'primary'
  },
  {
    code: 2,
    label: '运行成功',
    tagType: 'success'
  },
  {
    code: 3,
    label: '运行失败',
    tagType: 'danger'
  },
  {
    code: 4,
    label: '运行取消',
    tagType: 'danger'
  },
  {
    code: 5,
    label: '等待手动确认',
    tagType: ''
  }
]
const flowHeaderStatus = [
  {
    code: 0,
    text: '运行中',
    tagType: 'primary'
  },
  {
    code: 2,
    text: '运行成功',
    tagType: 'success'
  },
  {
    code: 3,
    text: '运行失败',
    tagType: 'danger'
  },
  {
    code: 4,
    text: '运行已取消',
    tagType: 'danger'
  }
]

export {
  formatTime,
  flowStatusArr,
  flowHeaderStatus,
}
