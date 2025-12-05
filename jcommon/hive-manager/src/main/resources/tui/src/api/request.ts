import axios, { type AxiosInstance, type AxiosRequestConfig } from 'axios'
import { API_BASE_URL } from '../utils/config'

class HttpClient {
  private instance: AxiosInstance

  constructor() {
    this.instance = axios.create({
      baseURL: API_BASE_URL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    })

    // Request interceptor
    this.instance.interceptors.request.use(
      (config) => {
        const token = this.getToken()
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        return config
      },
      (error) => Promise.reject(error)
    )

    // Response interceptor
    this.instance.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          // Handle unauthorized
          this.clearToken()
        }
        return Promise.reject(error)
      }
    )
  }

  private getToken(): string | null {
    // In a real app, you might want to use a more persistent storage
    return globalThis.__token__ || null
  }

  private clearToken(): void {
    globalThis.__token__ = undefined
  }

  public setToken(token: string): void {
    globalThis.__token__ = token
  }

  public get<T = any>(url: string, config?: AxiosRequestConfig) {
    return this.instance.get<T>(url, config)
  }

  public post<T = any>(url: string, data?: any, config?: AxiosRequestConfig) {
    return this.instance.post<T>(url, data, config)
  }

  public put<T = any>(url: string, data?: any, config?: AxiosRequestConfig) {
    return this.instance.put<T>(url, data, config)
  }

  public delete<T = any>(url: string, config?: AxiosRequestConfig) {
    return this.instance.delete<T>(url, config)
  }
}

export const http = new HttpClient()

// Extend global for token storage
declare global {
  var __token__: string | undefined
}
