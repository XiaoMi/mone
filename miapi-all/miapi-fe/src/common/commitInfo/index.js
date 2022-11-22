import { ElMessageBox } from 'element-plus'
import i18n from '../../lang'
export default function (cb) {
  ElMessageBox.prompt(i18n.t('errorMessage.pleaseEnterCommitInformation'), i18n.t('prompt'), {
    confirmButtonText: i18n.t('btnText.ok'),
    cancelButtonText: i18n.t('btnText.cancel'),
    inputPattern: /\S/,
    inputErrorMessage: i18n.t('errorMessage.pleaseEnterCommitInformation')
  }).then(({ value }) => {
    cb && cb(value)
  }).catch(() => {})
}
