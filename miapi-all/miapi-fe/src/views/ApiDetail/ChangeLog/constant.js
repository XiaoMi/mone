import i18n from '../../../lang'
export const DUBBO_KEY = [{
  key: 'apidocname',
  name: i18n.t('interfaceDescription')
}, {
  key: 'apigroup',
  name: i18n.t('serviceGrouping')
}, {
  key: 'apimodelclass',
  name: i18n.t('ApiClass.serviceName')
}, {
  key: 'apiname',
  name: i18n.t('ApiClass.methodName')
}, {
  key: 'apiversion',
  name: i18n.t('serviceVersion')
}]

export const HTTP_KEY = [{
  key: 'apiStatus',
  name: i18n.t('ApiClass.apiStatus')
}, {
  key: 'apiName',
  name: i18n.t('ApiClass.name')
}, {
  key: 'apiURI',
  name: i18n.t('ApiClass.path')
}, {
  key: 'apiDesc',
  name: i18n.t('ApiClass.apiDescription')
}]

export const GATEWAY_KEY = [{
  key: 'apiStatus',
  name: i18n.t('ApiClass.apiStatus')
}, {
  key: 'apiName',
  name: i18n.t('ApiClass.name')
}, {
  key: 'apiURI',
  name: `URL ${i18n.t('path')}`
}, {
  key: 'apiDesc',
  name: i18n.t('ApiClass.apiDescription')
}]
