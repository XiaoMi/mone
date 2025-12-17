#!/usr/bin/env python3
"""Search Kubernetes pods by label selector using Service Account."""

import argparse
import json
import os

from dotenv import load_dotenv
from kubernetes import client, config

load_dotenv(override=True)
from kubernetes.client.rest import ApiException


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


def search_pods(label_selector: str, namespace: str = "default") -> dict:
    """Search pods by label selector."""
    config_result = load_k8s_config()
    if config_result is not True:
        return {"success": False, "error": f"Failed to load K8s config: {config_result}"}

    if not label_selector:
        return {"success": False, "error": "label_selector is required"}

    try:
        v1 = client.CoreV1Api()

        if namespace.lower() == "all":
            pod_list = v1.list_pod_for_all_namespaces(label_selector=label_selector)
        else:
            pod_list = v1.list_namespaced_pod(namespace=namespace, label_selector=label_selector)

        pods = []
        for pod in pod_list.items:
            pod_info = {
                "name": pod.metadata.name,
                "namespace": pod.metadata.namespace,
            }

            if pod.status:
                pod_info["phase"] = pod.status.phase

                # Container status
                if pod.status.container_statuses:
                    containers = []
                    for cs in pod.status.container_statuses:
                        containers.append({
                            "name": cs.name,
                            "ready": cs.ready,
                            "restartCount": cs.restart_count,
                        })
                    pod_info["containers"] = containers

            pods.append(pod_info)

        return {
            "success": True,
            "labelSelector": label_selector,
            "namespace": namespace,
            "podCount": len(pods),
            "pods": pods,
        }

    except ApiException as e:
        return {
            "success": False,
            "error": f"K8s API error: {e.reason}",
            "code": e.status,
        }
    except Exception as e:
        return {"success": False, "error": str(e)}


def main():
    parser = argparse.ArgumentParser(description="Search K8s pods by label selector")
    parser.add_argument(
        "--label-selector", "-l", required=True,
        help="Label selector, e.g., 'app=nginx' or 'project-id=123,pipeline-id=456'"
    )
    parser.add_argument(
        "--namespace", "-n", default="default",
        help="Namespace (default: 'default'). Use 'all' for all namespaces"
    )

    args = parser.parse_args()

    result = search_pods(
        label_selector=args.label_selector,
        namespace=args.namespace,
    )
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()