#!/usr/bin/env python3
"""Execute commands inside Kubernetes pod containers using Service Account."""

import argparse
import json
import os

from dotenv import load_dotenv
from kubernetes import client, config

load_dotenv(override=True)
from kubernetes.client.rest import ApiException
from kubernetes.stream import stream


def load_k8s_config():
    """Load Kubernetes configuration from env vars, in-cluster, or kubeconfig."""
    # 优先使用环境变量中的 Service Account Token
    k8s_api_server = os.getenv("K8S_API_SERVER")
    k8s_sa_token = os.getenv("K8S_SA_TOKEN")

    if k8s_api_server and k8s_sa_token:
        # 使用 Service Account Token 配置
        configuration = client.Configuration()
        configuration.host = k8s_api_server
        configuration.api_key = {"authorization": f"Bearer {k8s_sa_token}"}

        # 证书验证配置
        if os.getenv("K8S_SKIP_TLS_VERIFY", "false").lower() == "true":
            configuration.verify_ssl = False
        else:
            ca_cert = os.getenv("K8S_CA_CERT")
            if ca_cert:
                configuration.ssl_ca_cert = ca_cert

        client.Configuration.set_default(configuration)
        return True

    try:
        # Try in-cluster config (Pod 内运行时)
        config.load_incluster_config()
        return True
    except config.ConfigException:
        try:
            # Fall back to kubeconfig file
            kubeconfig_path = os.getenv("KUBECONFIG", None)
            config.load_kube_config(config_file=kubeconfig_path)
            return True
        except config.ConfigException as e:
            return str(e)


def exec_in_pod(
    pod_name: str,
    command: str,
    namespace: str = "default",
    container: str | None = None,
) -> dict:
    """Execute a command inside a pod container."""
    config_result = load_k8s_config()
    if config_result is not True:
        return {"success": False, "error": f"Failed to load K8s config: {config_result}"}

    if not pod_name:
        return {"success": False, "error": "pod_name is required"}
    if not command:
        return {"success": False, "error": "command is required"}

    try:
        v1 = client.CoreV1Api()

        # Build exec command
        exec_command = ["/bin/sh", "-c", command]

        # Execute command in pod
        resp = stream(
            v1.connect_get_namespaced_pod_exec,
            pod_name,
            namespace,
            container=container,
            command=exec_command,
            stderr=True,
            stdin=False,
            stdout=True,
            tty=False,
        )

        return {
            "success": True,
            "pod": pod_name,
            "namespace": namespace,
            "container": container,
            "command": command,
            "output": resp,
        }

    except ApiException as e:
        error_msg = e.reason
        # Try to extract more details from the response body
        if e.body:
            try:
                body = json.loads(e.body)
                error_msg = body.get("message", e.reason)
            except json.JSONDecodeError:
                error_msg = e.body if len(e.body) < 500 else e.reason

        return {
            "success": False,
            "pod": pod_name,
            "namespace": namespace,
            "command": command,
            "error": f"K8s API error: {error_msg}",
            "code": e.status,
        }
    except Exception as e:
        return {
            "success": False,
            "pod": pod_name,
            "namespace": namespace,
            "command": command,
            "error": str(e),
        }


def main():
    parser = argparse.ArgumentParser(description="Execute command in K8s pod")
    parser.add_argument("--pod", "-p", required=True, help="Pod name")
    parser.add_argument("--namespace", "-n", default="default", help="Namespace")
    parser.add_argument("--container", "-c", help="Container name (optional)")
    parser.add_argument("--command", "-cmd", required=True, help="Command to execute")

    args = parser.parse_args()

    result = exec_in_pod(
        pod_name=args.pod,
        command=args.command,
        namespace=args.namespace,
        container=args.container,
    )
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()
