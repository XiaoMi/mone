import { ss } from '@/utils/storage'
import { fetchUser } from '@/api'

const LOCAL_NAME = 'userStorage'

export interface UserInfo {
  admin: boolean
  avatar: string
  username: string
  name: string
  ztoken: string
  loginUrl: string
  logoutUrl: string
  description: string
  fullAccount: string
}

export interface UserState {
  userInfo: UserInfo
}

export function defaultSetting(): UserState {
  return {
    userInfo: {
      admin: false,
      avatar: '',
      name: '',
      username: '',
      ztoken: '',
      loginUrl: '',
      logoutUrl: '',
      description: '欢迎您使用',
      fullAccount: ''
    }
  }
}

export async function getUser(): Promise<UserInfo> {
  const user: UserState = {
    ...defaultSetting()
  }
  try {
    const { code, data } = await fetchUser()
    if (code == 0) {
      user.userInfo.admin = data.admin || user.userInfo.admin
      user.userInfo.avatar = data.avatar || user.userInfo.avatar
      user.userInfo.name = data.name || user.userInfo.name
      user.userInfo.username = data.username || user.userInfo.username
      user.userInfo.ztoken = data.ztoken || user.userInfo.ztoken
      user.userInfo.loginUrl = data.loginUrl || user.userInfo.loginUrl
      user.userInfo.logoutUrl = data.logoutUrl || user.userInfo.logoutUrl
      user.userInfo.fullAccount = data.fullAccount || user.userInfo.fullAccount
    }
  } catch (e) {
    console.error(e)
  }
  return user.userInfo
}

export function getLocalState(): UserState {
  const localSetting: UserState | undefined = ss.get(LOCAL_NAME)
  return { ...defaultSetting(), ...localSetting }
}

export function setLocalState(setting: UserState): void {
  ss.set(LOCAL_NAME, setting)
}
