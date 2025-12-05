import { Service } from '@/common/req'
import type { IResponse } from '@/common/req'


export interface IReportListItem {
  appName: string;
  businessName: string;
  className: string;
  createdAt: number;
  description: string;
  errorMessage: string | null;
  executionTime: number;
  host: string;
  inputParams: string;
  invokeWay: number;
  methodName: string;
  success: boolean;
  type: number;
}


export const getReportList = (data: {
    appName:string;
    page:number;
    pageSize:number;
}) => {
    return Service<IResponse<Array<IReportListItem>>>({
      url: `/v1/report/list`,
      method: 'post',
      data,
      headers: {
        'Content-Type': 'application/json',
      }
    })
  }
