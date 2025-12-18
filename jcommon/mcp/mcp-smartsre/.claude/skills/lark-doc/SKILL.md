---
name: lark-doc
description: Fetch Lark/Feishu document and convert to Markdown. Use this skill to get troubleshooting guides and operation instructions from Lark documents.
---

# Lark Document Skill

This skill provides two capabilities:
1. Fetch a Lark (Feishu) document and convert its content to Markdown format
2. Append troubleshooting summaries to the document for future reference

## When to Use

**Fetch Document:**
- User needs troubleshooting steps or operation guides
- Before diagnosing issues, check if there are relevant operation instructions
- When user asks about specific procedures or step-by-step guides
- Keywords: troubleshooting, steps, guide, procedure, operation, how to fix

**Append Summary:**
- After completing a troubleshooting session
- When user wants to document a resolved issue for future reference
- To add new troubleshooting cases to the guide document
- Keywords: save, record, document, write summary, add to guide

## How to Use

### Fetch Document

```bash
uv run python .claude/skills/lark-doc/fetch_doc.py
```

### Append Troubleshooting Summary

```bash
uv run python .claude/skills/lark-doc/append_summary.py \
  -t "问题标题" \
  -p "问题描述" \
  -s '["排查步骤1", "排查步骤2", "排查步骤3"]' \
  -o "解决方案" \
  -n "可选备注"
```

**Parameters:**
- `-t, --title`: Title of the troubleshooting case (required)
- `-p, --problem`: Description of the problem (required)
- `-s, --steps`: JSON array of troubleshooting steps taken (required)
- `-o, --solution`: The solution that resolved the issue (required)
- `-n, --notes`: Optional additional notes

## Environment Variables

- `LARK_APP_ID`: Lark application ID
- `LARK_APP_SECRET`: Lark application secret
- `LARK_DOC_URL`: The Lark document URL to fetch/append to
- `LARK_DOMAIN`: (Optional) Custom domain for enterprise Lark

## Output Format

### Fetch Document

Returns Markdown formatted content of the Lark document, including:
- Headings (H1-H9)
- Text paragraphs
- Ordered and unordered lists
- Code blocks
- Quotes
- Todo items (checkboxes)

### Append Summary

Returns JSON with success status:
```json
{
  "success": true,
  "documentId": "xxx",
  "url": "https://...",
  "message": "Successfully appended troubleshooting summary: ..."
}
```

## Example Output

### Fetched Document

```markdown
# Troubleshooting Guide

## Common Issues

### Issue 1: Service Not Starting

1. Check the logs
2. Verify configuration
3. Restart the service

### Issue 2: Connection Timeout

- Check network connectivity
- Verify firewall rules
```

### Appended Summary Structure

The summary will be appended to the document with the following structure:

```markdown
---

### 问题标题 (2025-01-15 14:30)

#### 问题描述

问题的详细描述...

#### 排查步骤

- 排查步骤1
- 排查步骤2
- 排查步骤3

#### 解决方案

解决方案的详细说明...

#### 备注

可选的备注信息...
```