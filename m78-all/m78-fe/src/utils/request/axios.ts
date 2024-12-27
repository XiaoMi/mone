import axios, { type AxiosResponse } from 'axios'
import { useAuthStore } from '@/stores/auth'

const service = axios.create({})

service.interceptors.request.use(
  (config) => {
    config.baseURL = config.baseURL || import.meta.env.VITE_GLOB_API_URL
    const token = useAuthStore().token
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
  },
  (error) => {
    return Promise.reject(error.response)
  }
)

service.interceptors.response.use(
  (response: AxiosResponse): AxiosResponse => {
    if (response.status === 200) return response

    throw new Error(response.status.toString())
  },
  (error) => {
    return Promise.reject(error)
  }
)

export default service
