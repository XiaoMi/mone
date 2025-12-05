#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import requests
import logging
from urllib.parse import urlencode
from typing import Optional, Dict, Any

logger = logging.getLogger(__name__)


class PrometheusUtil:
    """Prometheus查询工具类"""
    
    def __init__(self, prometheus_url: str, use_https: bool = False):
        """
        初始化Prometheus工具类
        
        Args:
            prometheus_url: Prometheus服务地址（不包含协议）
            use_https: 是否使用HTTPS协议，默认False使用HTTP
        """
        self.prometheus_url = prometheus_url
        self.protocol = "https" if use_https else "http"
        self.uri_query_moment = "/api/v1/query"
        self.uri_query_range = "/api/v1/query_range"
    
    def query_moment(
        self, 
        query: str, 
        time: Optional[str] = None,
        timeout: int = 30
    ) -> Optional[Dict[str, Any]]:
        try:
            # 构建查询参数
            params = {
                'query': query
            }
            
            # 如果指定了时间，添加time参数
            if time is not None:
                params['time'] = time
            
            # 构建完整URL
            url = f"{self.protocol}://{self.prometheus_url}{self.uri_query_moment}?{urlencode(params)}"
            
            logger.info(f"Moment查询URL: {url}")
            
            # 发送HTTP GET请求
            headers = {
                'Accept': '*/*'
            }
            
            response = requests.get(url, headers=headers, timeout=timeout)
            response.raise_for_status()
            
            # 解析JSON响应
            result = response.json()
            
            if result.get('status') != 'success':
                logger.error(f"Prometheus返回状态异常: {result.get('status')}")
                return None
            
            logger.info(f"Moment查询成功")
            return result
            
        except requests.exceptions.RequestException as e:
            logger.error(f"Moment查询请求失败: {e}")
            return None
        except Exception as e:
            logger.error(f"Moment查询处理失败: {e}")
            return None
    
    def query_range(
        self, 
        query: str, 
        start: str, 
        end: str,
        step: Optional[str] = None,
        auto_step: bool = True,
        timeout: int = 30
    ) -> Optional[Dict[str, Any]]:
        try:
            # 如果auto_step为True且未指定step，则自动计算
            if auto_step and step is None:
                multi = (int(end) - int(start)) // 3600
                if multi < 1:
                    multi = 1
                step = str(multi * 15)
            
            # 构建查询参数
            params = {
                'query': query,
                'start': start,
                'end': end
            }
            
            # 如果有step，添加到参数中
            if step is not None:
                params['step'] = step
            
            # 构建完整URL
            url = f"{self.protocol}://{self.prometheus_url}{self.uri_query_range}?{urlencode(params)}"
            
            logger.info(f"Range查询URL: {url}")
            
            # 发送HTTP GET请求
            headers = {
                'Accept': '*/*'
            }
            
            response = requests.get(url, headers=headers, timeout=timeout)
            response.raise_for_status()
            
            # 解析JSON响应
            result = response.json()
            
            if result.get('status') != 'success':
                logger.error(f"Prometheus返回状态异常: {result.get('status')}")
                return None
            
            logger.info(f"Range查询成功")
            return result
            
        except requests.exceptions.RequestException as e:
            logger.error(f"Range查询请求失败: {e}")
            return None
        except Exception as e:
            logger.error(f"Range查询处理失败: {e}")
            return None
    
    def get_latest_value(self, result: Dict[str, Any]) -> Optional[float]:
        """
        从查询结果中获取最新的值（适用于moment查询）
        
        Args:
            result: Prometheus查询结果
            
        Returns:
            最新的值，失败返回None
        """
        try:
            data = result.get('data', {})
            results = data.get('result', [])
            
            if not results:
                logger.warning("查询结果为空")
                return None
            
            # 取第一个结果的value
            first_result = results[0]
            value = first_result.get('value', [])
            
            if len(value) < 2:
                logger.warning("value格式不正确")
                return None
            
            return float(value[1])
            
        except Exception as e:
            logger.error(f"获取最新值失败: {e}")
            return None
    
    def get_range_diff(self, result: Dict[str, Any]) -> Optional[int]:
        """
        从范围查询结果中计算差值（最后一条 - 第一条）
        
        Args:
            result: Prometheus范围查询结果
            
        Returns:
            差值，失败返回None
        """
        try:
            data = result.get('data', {})
            results = data.get('result', [])
            
            if not results:
                logger.warning("查询结果为空")
                return None
            
            # 取第一个结果
            first_result = results[0]
            values = first_result.get('values', [])
            
            if len(values) < 1:
                logger.warning("values列表为空")
                return None
            
            # 获取第一条和最后一条的值
            first_value = int(float(values[0][1]))
            last_value = int(float(values[-1][1]))
            
            # 计算差值
            diff = last_value - first_value
            
            logger.info(f"第一条值: {first_value}, 最后一条值: {last_value}, 差值: {diff}")
            
            return diff
            
        except Exception as e:
            logger.error(f"计算差值失败: {e}")
            return None


# 使用示例
if __name__ == "__main__":
    # 配置日志
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(levelname)s - %(message)s'
    )

