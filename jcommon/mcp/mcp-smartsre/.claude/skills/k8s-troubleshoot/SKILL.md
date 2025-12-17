---
name: k8s-troubleshoot
description: Kubernetes troubleshooting toolkit - search pods by labels and execute diagnostic commands inside containers. Use when user reports service errors, exceptions, crashes, timeouts, or needs to check logs, processes, network, or resource usage in K8s pods.
---

# Kubernetes Troubleshooting Skill

A complete toolkit for diagnosing Kubernetes applications. Find pods by labels, then execute commands inside containers for deep diagnostics.

## When to Use

- User reports service errors, exceptions, failures, timeouts
- Need to check application logs or process status
- Diagnose network, memory, or disk issues
- Keywords: error, exception, failed, timeout, crash, not working, logs, troubleshoot, diagnose, pod, container

## Workflow

1. **Search pods** - Find target pods by label selector
2. **Execute diagnostics** - Run commands inside containers

## Scripts

### 1. Search Pods

Find pods by label selector:

```bash
uv run python .claude/skills/k8s-troubleshoot/scripts/search_pods.py -l "app=nginx" -n default
```

| Parameter | Required | Description |
|-----------|----------|-------------|
| `-l, --label-selector` | Yes | Label selector, e.g., `app=nginx` or `project-id=123,pipeline-id=456` |
| `-n, --namespace` | No | Namespace (default: `default`). Use `all` for all namespaces |

**Output**: JSON with `success`, `podCount`, `pods` (name, namespace, phase, containers)

### 2. Execute Command in Pod

Run diagnostic commands inside a container:

```bash
uv run python .claude/skills/k8s-troubleshoot/scripts/exec_pod.py -p "pod-name" -n default -cmd "tail -n 100 /root/logs/app.log"
```

| Parameter | Required | Description |
|-----------|----------|-------------|
| `-p, --pod` | Yes | Pod name |
| `-n, --namespace` | No | Namespace (default: `default`) |
| `-c, --container` | No | Container name (for multi-container pods) |
| `-cmd, --command` | Yes | Command to execute |

**Output**: JSON with `success`, `pod`, `namespace`, `command`, `output`

## Common Diagnostic Patterns

### View application logs
```bash
uv run python .claude/skills/k8s-troubleshoot/scripts/exec_pod.py -p my-pod -n default -cmd "tail -n 100 /root/logs/app.log"
```

### Check Nacos config (dubbo3 issues)
```bash
uv run python .claude/skills/k8s-troubleshoot/scripts/exec_pod.py -p my-pod -n default -cmd "cat /root/logs/nacos/config.log | grep nacos"
```

### Check processes
```bash
uv run python .claude/skills/k8s-troubleshoot/scripts/exec_pod.py -p my-pod -n default -cmd "ps aux | head -20"
```

### Check network
```bash
uv run python .claude/skills/k8s-troubleshoot/scripts/exec_pod.py -p my-pod -n default -cmd "netstat -tlnp"
```

### Check disk and memory
```bash
uv run python .claude/skills/k8s-troubleshoot/scripts/exec_pod.py -p my-pod -n default -cmd "df -h && free -m"
```

## Troubleshooting Tips

| Issue | Diagnostic Command |
|-------|-------------------|
| dubbo3 no provider | Check `/root/logs/nacos/config.log` for nacos address |
| Service not responding | Check process status with `ps aux` and logs |
| Connection issues | Check network with `netstat -tlnp` |
| OOM errors | Check memory with `free -m` |