export { errorHandler } from "./error";
export { successHandler, imageHandler } from "./success";
export { soundHandler } from "./sound";

export type AiMessage = {
  id: string
  code: number
  state: number
  utime: number
  ctime: number
  role: any
  message: string
  data: any
  meta: any
  type: any
  sound: string
}
