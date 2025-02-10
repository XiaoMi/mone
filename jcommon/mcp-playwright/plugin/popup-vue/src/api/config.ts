import axios from 'axios'
import type { SelectorConfig } from '../model/config'

const BASE_URL = 'http://localhost:8181/config'

export const configApi = {
  create: (config: SelectorConfig) => 
    axios.post(`${BASE_URL}/create`, config),
    
  update: (config: SelectorConfig) =>
    axios.post(`${BASE_URL}/update`, config),
    
  delete: (id: string) =>
    axios.post(`${BASE_URL}/delete?id=${encodeURIComponent(id)}`),
    
  get: (id: string) =>
    axios.get(`${BASE_URL}/get?id=${encodeURIComponent(id)}`),
    
  list: () =>
    axios.get<SelectorConfig[]>(`${BASE_URL}/list`)
} 