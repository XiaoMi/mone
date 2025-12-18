---
name: knowledge
description: Search WeKnora knowledge base for documentation, guides, and best practices. Use when user asks "how to", "what is", or needs configuration guides, feature docs, usage instructions, or troubleshooting steps.
---

# Knowledge Base Skill

Search the WeKnora knowledge base with intelligent tag filtering for precise results. Supports hybrid search (keyword + vector).

## When to Use

- User asks "how to use", "how to configure", "what is"
- Questions about product features, usage guides, API docs
- Best practices, configuration examples
- Keywords: usage, configuration, feature, guide, document, how to, 如何, 怎么, 配置, 使用

## Workflow

1. **Get tags first** - Understand available knowledge categories
2. **Match tags** - Select appropriate tags based on user query
3. **Search** - Use tag filtering for better precision

## Scripts

### 1. Get Available Tags

Fetch all tags to understand knowledge structure:

```bash
uv run python .claude/skills/knowledge/scripts/get_tags.py
```

**Output**: JSON with `tags` array containing `name` and `description` for each tag

### 2. Search Knowledge

Search with optional tag filtering:

```bash
uv run python .claude/skills/knowledge/scripts/search.py -q "your query" --tags '["tesla"]'
```

| Parameter | Required | Description |
|-----------|----------|-------------|
| `-q, --query` | Yes | Search query |
| `-t, --tags` | No | JSON array of tag names, e.g., `'["tesla", "miline"]'` |
| `-m, --tag-mode` | No | `any` (default) or `all` |
| `--kb-id` | No | Knowledge base ID (has default) |

**Output**: JSON with `results` array containing `content`, `score`, `metadata`

## Tag Mapping Guide

| User Query Keywords | Suggested Tags |
|---------------------|----------------|
| Tesla, gateway, routing, 网关 | `tesla` |
| Dayu, microservice, governance, 微服务 | `Dayu` |
| CI/CD, pipeline, deployment, 部署, 流水线 | `miline` |
| Task scheduling, cron, 调度 | `moon` |
| General questions, 常见问题 | `常见问题` |

## Examples

### Search with tag filtering (recommended)
```bash
uv run python .claude/skills/knowledge/scripts/search.py -q "如何配置网关" --tags '["tesla"]'
```

### Search with multiple tags
```bash
uv run python .claude/skills/knowledge/scripts/search.py -q "部署流水线" --tags '["miline"]' --tag-mode any
```

### Search without tags (fallback)
```bash
uv run python .claude/skills/knowledge/scripts/search.py -q "系统架构设计"
```