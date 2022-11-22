import i18n from '../lang'
export const PROTOCOL = {
  '1': 'Http',
  // '2': 'HTTPS',
  '3': 'Dubbo',
  '4': 'Gateway',
  '5': 'Grpc'
}

export const ENVIRONMENT = {
  staging: 'staging',
  online: 'online'
}

export const DUBBO_ENVIRONMENT = {
  staging: {
    name: `${i18n.t('China')}-staging`,
    value: 'staging',
    label: 'staging'
  },
  online: {
    name: `${i18n.t('China')}-online`,
    value: 'online',
    label: 'online'
  },
  cloud_dev: {
    name: `${i18n.t('clique')}-dev`,
    value: 'cloud_dev',
    label: 'dev'
  }
}

export const PROTOCOL_TYPE = {
  HTTP: '1',
  Dubbo: '3',
  Gateway: '4',
  Grpc: '5'
}

export const API_STATUS = {
  '0': i18n.t('undone'),
  '1': i18n.t('completed'),
  '2': i18n.t('deprecated')
}

export const SHOW_STATUS = {
  'NOT': 0,
  'DISABLED': 2
}

export const REQUEST_TYPE = {
  '0': 'POST',
  '1': 'GET',
  '2': 'PUT',
  '3': 'DELETE',
  '4': 'HEAD',
  '5': 'OPTS',
  '6': 'PATCH'
}

export const REQUEST_TYPE_EX = {
  'POST': '0',
  'GET': '1',
  'PUT': '2',
  'DELETE': '3',
  'HEAD': '4',
  'OPTS': '5',
  'PATCH': '6'
}

export const GATEWAY_REQUEST_TYPE = {
  '0': 'POST',
  '1': 'GET'
}

export const DATA_TYPE = {
  '0': '[string]',
  '1': '[file]',
  // '2': '[json]',
  '3': '[int]',
  '4': '[float]',
  '5': '[double]',
  '6': '[date]',
  '7': '[datetime]',
  '8': '[boolean]',
  '9': '[byte]',
  '10': '[short]',
  '11': '[long]',
  '12': '[array]',
  '13': '[object]',
  '14': '[number]'
}

export const SINGLE_DATA_TYPE = {
  '0': '[string]',
  '8': '[boolean]',
  '3': '[int]',
  '4': '[float]',
  '5': '[double]',
  '11': '[long]',
  '14': '[number]'
}

export const DATA_TYPE_KEY = {
  'string': '0',
  'json': '2',
  'boolean': '8',
  'array': '12',
  'object': '13',
  'number': '14',
  'int': '3',
  'double': '5',
  'long': '11',
  'dubboArray': '15'
}

export const PARAM_TYPE = {
  '0': i18n.t('yes'),
  '1': i18n.t('no')
}

export const ROUTING_TYPE = {
  '0': 'HTTP',
  '1': i18n.t('DubboType'),
  '4': i18n.t('NonDubboType')
}

export const HEADER = [
  'Accept', 'Accept-Charset', 'Accept-Encoding', 'Accept-Language', 'Accept-Ranges', 'Authorization',
  'Cache-Control', 'Connection', 'Cookie', 'Content-Length', 'Content-Type', 'Content-MD5',
  'Date',
  'Expect',
  'From',
  'Host',
  'If-Match', 'If-Modified-Since', 'If-None-Match', 'If-Range', 'If-Unmodified-Since',
  'Max-Forwards',
  'Origin',
  'Pragma', 'Proxy-Authorization',
  'Range', 'Referer',
  'TE',
  'Upgrade', 'User-Agent',
  'Via',
  'Warning'
]

export const POWER = {
  0: i18n.t('administrator'),
  1: i18n.t('collaborationAdministrator'),
  2: i18n.t('ordinaryMember')
}

export const AJAX_SUCCESS_MESSAGE = 'success'

export const ROLE = {
  ADMIN: { name: i18n.t('administrator'), value: 0, key: 'Admin' },
  MEMBER: { name: i18n.t('member'), value: 1, key: 'Member' },
  GUEST: { name: i18n.t('visitor'), value: 2, key: 'Guest' }
}

export const PROJECT_AUTHORITY = {
  PUBLIC: {
    value: 1,
    name: i18n.t('sharedPermissions')
  },
  PRIVATE: {
    value: 0,
    name: i18n.t('privatePermissions')
  }
}

export const API_REQUEST_PARAM_TYPE = {
  FORM_DATA: 0,
  JSON: 1,
  RAW: 2
}

export const SHOW_CODE_TYPE = {
  1: {
    type: 1,
    mode: 'text/x-java',
    label: 'JAVA'
  },
  2: {
    type: 2,
    mode: 'text/javascript',
    label: 'CURL'
  },
  3: {
    type: 3,
    mode: 'text/javascript',
    label: 'JSON'
  },
  4: {
    type: 4,
    mode: 'text/javascript',
    label: 'customize'
  },
  99: {
    mode: 'text/javascript',
    label: 'JavaScript'
  }
}

export const SHOW_RESULT_TYPE = {
  2: {
    type: 2,
    mode: 'text/javascript',
    label: 'JSON'
  },
  4: {
    type: 4,
    mode: 'text/javascript',
    label: 'customize'
  },
}

export const outerHostName = false
