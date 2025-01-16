# -*- coding: utf-8 -*-

import sys
import urllib.request
import json

def extract_from_response(url, request_body):
    # 将请求体编码为字节
    request_body_bytes = json.dumps(request_body).encode('utf-8')

    # 创建请求对象
    request = urllib.request.Request(url, data=request_body_bytes, method='POST')

    try:
        # 发送请求并获取响应
        with urllib.request.urlopen(request) as response:
            # 读取响应数据
            response_data = response.read().decode('utf-8')

            # 将响应数据解析为 JSON
            response_json = json.loads(response_data)

            # 从响应中获取 data 键的值
            data_str = response_json.get("data")

            # 将 data 字符串解析为 JSON
            data_json = json.loads(data_str)

            # 从解析后的 JSON 中提取 ok 和 suggest 键的值
            ok = data_json.get("result", {}).get("ok")
            suggest = data_json.get("result", {}).get("suggest")

            # 打印提取的值
            print("AI based commit messsage check! Powered by M78 ~\n")
            print(" __  __   _____    ___  ")
            print("|  \/  | |___  |  ( _ ) ")
            print("| |\/| |    / /   / _ \ ")
            print("| |  | |   / /   | (_) |")
            print("|_|  |_|  /_/     \___/ ")
            print("")
            # print(f"body: {request_body}")
            print(f"ok: {ok}")
            print(f"suggest: {suggest}, 可安装athena插件后,使用athena的智能提交功能(在右侧athena copilot中执行!push),一键生成规范的提交记录~")
            print("")
            if ok == "0":
                return 1
            else:
                return 0
    except urllib.error.HTTPError as e:
        print(f"Error: {e.code} - {e.reason}")
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    # 获取命令行参数
    if len(sys.argv) != 3:
        print("Usage: python script.py <url> <request_body>")
        sys.exit(1)

    url = sys.argv[1]
    request_body = json.loads(sys.argv[2])

    # 调用函数
    res = extract_from_response(url, request_body)
    # print(f"res: {res}")
    sys.exit(res)
