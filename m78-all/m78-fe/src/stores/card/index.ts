import { ref } from 'vue'
import { defineStore } from 'pinia'
import {
  getCardTypes,
  getCardStatus,
  getCardVariableClassTypes,
  getBackgroundTypes,
  getPositions,
  getVisibilityOptions,
  getClickEventTypes
} from '@/api/probot-card'

export const useProbotCardStore = defineStore('card', () => {
  const initSetting = ref({
    id: null,
    uniqueKey: '',
    workspaceId: '',
    cardId: '',
    type: '',
    property: {
      form: {
        size: 'large',
        rowGap: 10, //行间距
        padding: 15, //内间距
        vertical: 'center', //垂直对齐
        horizontal: 'left', //水平对齐
        width: 'auto', //宽度比例
        weight: 1, //宽度按权重分配
        enableShrink: 0, //内容过长时，挤压当前列宽
        roundedSize: 5, //圆角
        enableShowBorder: 0 //是否显示边框
      },
      layoutType: 0, // 0是固定格数，1是动态格数,
      itemsPerRow: '', // 每行展示数目是Integer,
      boundArrayVariable: '', //动态格数下的绑定数组类型变量
      background: {
        backgroundType: 'Color',
        backgroundColor: '#FFFFFF',
        backgroundImageUrl: '',
        backgroundImageTransparency: '100',
        backgroundImageHorizontalPosition: 'HorizontalMiddle',
        backgroundImageVerticalPosition: 'VerticalMiddle'
      },
      operate: {
        enableClickEvent: 0,
        clickEventType: 'OPEN_URL',
        clickUrl: '',
        message: ''
      },
      content: {
        type: 'expression',
        value: '',
        total: '',
        score: ''
      },
      loopRending: {
        enableLoopRending: 0,
        boundArrayVariable: ''
      },
      slots: [
        // {
        //   type: 'container', //
        //   children: ['afadsfsaffad'],
        //   form: {
        //     size: 'large',
        //     rowGap: 10,
        //     padding: 15,
        //     vertical: 'center',
        //     horizontal: 'left',
        //     width: 'auto',
        //     weight: 1,
        //     enableShrink: 0
        //   }
        // }
      ],
      visibilitySetting: {
        visibilityType: 'AlwaysDisplay',
        key: '',
        operator: '',
        value: '',
        valueType: ''
      }
    },
    children: []
  })
  const cardData = ref({})
  const setCardInfo = (value: any) => {
    cardData.value.card = value
  }
  const setCardData = (value: any) => {
    cardData.value = value
    console.log('cardData.value ----', cardData.value)
  }

  const cardList = ref([])
  const setCardList = (value: any) => {
    cardList.value = value
    console.log(' cardList.value----', cardList.value)
  }

  const dataTypeMap = ref({
    CARD_ROOT: {
      title: '根节点',
      treeLabel: 'Card',
      row: 1,
      defaultChildrenLength: 1,
      children: [
        {
          column: 24
        }
      ]
    },
    SLOT: {},
    LAYOUT_SINGLE_ROW_1: {
      title: '单列布局',
      treeLabel: 'singlecolumn',
      row: 2,
      defaultChildrenLength: 1,
      children: [
        {
          column: 24
        }
      ]
    },
    LAYOUT_MULTI_ROW_1_1_1: {
      title: '多列布局1:1:1',
      treeLabel: '1:1:1',
      row: 2,
      defaultChildrenLength: 3,
      children: [
        {
          column: 8
        },
        {
          column: 8
        },
        {
          column: 8
        }
      ]
    },
    LAYOUT_ROW: {
      title: '列表布局',
      treeLabel: 'ListLayout',
      row: 1,
      defaultChildrenLength: 4,
      smallChildren: [
        {
          column: 24
        },
        {
          column: 24
        }
      ],
      children: [
        {
          column: 24
        },
        {
          column: 24
        },
        {
          column: 24
        },
        {
          column: 24
        }
      ]
    },
    LAYOUT_GRID: {
      title: '网格布局',
      treeLabel: 'GridLayout',
      row: 1,
      defaultChildrenLength: 4,
      itemsPerRow: 2,
      children: [
        {
          column: 12
        },
        {
          column: 12
        },
        {
          column: 12
        },
        {
          column: 12
        }
      ]
    },
    LAYOUT_SIDESLIP: {
      title: '横滑布局',
      treeLabel: 'ScrollLayout',
      row: 2,
      defaultChildrenLength: 4,
      weight: 136,
      smallChildren: [
        {
          column: 7
        },
        {
          column: 7
        },
        {
          column: 7
        },
        {
          column: 3
        }
      ],
      children: [
        {
          column: 12
        },
        {
          column: 12
        },
        {
          column: 12
        },
        {
          column: 12
        }
      ]
    },
    LAYOUT_FLOAT: {
      title: '浮动布局',
      treeLabel: 'FloatLayout',
      row: 1,
      defaultChildrenLength: 4,
      weight: 136,
      smallChildren: [
        {
          column: 8
        },
        {
          column: 16
        },
        {
          column: 16
        },
        {
          column: 8
        }
      ],
      children: [
        {
          column: 12
        },
        {
          column: 12
        },
        {
          column: 12
        },
        {
          column: 12
        }
      ]
    },
    BASE_COMPONENT_TITLE: {
      title: '标题',
      treeLabel: 'Title',
      content: 'The title'
    },
    BASE_COMPONENT_TEXT: {
      title: '文本',
      treeLabel: 'Text',
      content: 'The plain text'
    },
    BASE_COMPONENT_BUTTON: {
      title: '按钮',
      treeLabel: 'Button',
      content: 'button'
    },
    BASE_COMPONENT_IMAGE: {
      title: '图片',
      treeLabel: 'Image',
      content: 'image'
    },
    BASE_COMPONENT_ICON: {
      title: '图标',
      treeLabel: 'Icon',
      content: 'icon'
    },
    BASE_COMPONENT_TAG: {
      title: '标签',
      treeLabel: 'Tag',
      content: 'tag'
    },
    BASE_COMPONENT_SCORE: {
      title: '评分',
      treeLabel: 'Rating',
      content: 'rating'
    },
    BASE_COMPONENT_DIVIDER: {
      title: '分割线',
      treeLabel: 'Divider',
      content: 'divider'
    },
    FORM_INPUT_BOX: {},
    FORM_DROPDOWN_SELECTION: {},
    FORM_BUTTON_SELECTION: {}
  })
  const setDataTypeMap = (value: any) => {
    dataTypeMap.value = value
  }
  const typeOptions = ref([])
  const getTypeOptions = async () => {
    if (!Object.keys(typeOptions.value).length) {
      const { data } = await getCardTypes()
      typeOptions.value = data
    }
    return typeOptions.value
  }

  const statusOptions = ref([])
  const getStatusOptions = async () => {
    if (!Object.keys(statusOptions.value).length) {
      const { data } = await getCardStatus()
      statusOptions.value = data
    }
    return statusOptions.value
  }
  // 变量列表
  const variableList = ref([])
  const setVariableList = (value: any) => {
    variableList.value = value
  }
  // 变量类型
  const variableTypes = ref([])
  const getVariableTypes = async () => {
    if (!Object.keys(variableTypes.value).length) {
      const { data } = await getCardVariableClassTypes()
      variableTypes.value = data
    }
    return variableTypes.value
  }
  //当前数据
  const currentItem = ref({})
  const setCurrentItem = (value: any) => {
    currentItem.value = value
  }
  // 鼠标移入的key
  const currentOverKey = ref('')
  const setOverKey = (value: string) => {
    currentOverKey.value = value
  }
  const horizontal = {
    HorizontalLeft: 'left',
    HorizontalMiddle: 'center',
    HorizontalRight: 'right'
  }
  const vertical = {
    VerticalLower: 'top',
    VerticalMiddle: 'center',
    VerticalUpper: 'bottom'
  }

  // 背景
  const backgroundOptions = ref({})
  const getBackgroundOptions = async () => {
    if (!Object.keys(backgroundOptions.value).length) {
      const { data } = await getBackgroundTypes()
      backgroundOptions.value = data
    }
    return backgroundOptions.value
  }
  // 垂直位置
  const verticalOptions = ref({})
  const getVerticalOptions = async () => {
    if (!Object.keys(verticalOptions.value).length) {
      const { data } = await getPositions({
        type: 'Vertical'
      })
      verticalOptions.value = data
    }
    return verticalOptions.value
  }
  // 水平位置
  const horizontalOptions = ref({})
  const getHorizontalOptions = async () => {
    if (!Object.keys(horizontalOptions.value).length) {
      const { data } = await getPositions({
        type: 'Horizontal'
      })
      horizontalOptions.value = data
    }
    return horizontalOptions.value
  }
  // 显示
  const visibilityOptions = ref({})
  const getVisibility = async () => {
    if (!Object.keys(visibilityOptions.value).length) {
      const { data } = await getVisibilityOptions()
      visibilityOptions.value = data
    }
    return visibilityOptions.value
  }
  //点击事件
  const clickEventTypeOptions = ref({})
  const getClickEventTypeOptions = async () => {
    if (!Object.keys(clickEventTypeOptions.value).length) {
      const { data } = await getClickEventTypes()
      clickEventTypeOptions.value = data
    }
    return clickEventTypeOptions.value
  }
  // 格数
  const FRAME_ENUM = {
    0: '固定格数',
    1: '动态格数'
  }
  // 宽度比例
  const WIDTH_RATIO_ENUM = {
    weight: '宽度按权重分配',
    auto: '宽度自适应内容'
  }
  const WIDTH_ENUM = {
    weight: '宽度固定',
    auto: '列宽自适应内容'
  }
  const horizontal_arr = {
    left: '左对齐',
    center: '水平居中',
    right: '右对齐'
  }
  const vertical_arr = {
    top: '上对齐',
    center: '垂直居中',
    bottom: '下对齐'
  }
  const elementTypes = ref({})
  const setElementTypes = (value: any) => {
    elementTypes.value = value
  }
  return {
    initSetting,
    cardList,
    setCardList,
    setCardInfo,
    cardData,
    setCardData,
    dataTypeMap,
    setDataTypeMap,
    getTypeOptions,
    getStatusOptions,
    variableList,
    setVariableList,
    getVariableTypes,
    currentItem,
    setCurrentItem,
    currentOverKey,
    setOverKey,
    getBackgroundOptions,
    getVerticalOptions,
    getHorizontalOptions,
    getVisibility,
    getClickEventTypeOptions,
    horizontal,
    vertical,
    FRAME_ENUM,
    WIDTH_RATIO_ENUM,
    WIDTH_ENUM,
    horizontal_arr,
    vertical_arr,
    elementTypes,
    setElementTypes
  }
})
