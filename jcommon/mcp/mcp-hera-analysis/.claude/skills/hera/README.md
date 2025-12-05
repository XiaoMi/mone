# Hera Log Query Skill

A Python-based skill for querying Hera log details. This is a Python implementation of the `HeraLogDetailFunction` Java class.

## Overview

This skill provides functionality to query Hera log details with various filters including:
- Space ID and Store ID
- Tail name
- Search input text
- Time range (start and end timestamps)

## Quick Start

### Command-Line Usage

```bash
cd .claude/skills/hera/scripts
python hera_log_detail_query.py 123 456 "my-app-logs"
```

### Python Usage

```python
from hera_log_detail_query import query_log_detail

result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="my-app-logs"
)
print(result)
```

## Files

- `SKILL.md` - Main skill documentation and usage guide
- `reference.md` - Technical reference and detailed API documentation
- `scripts/hera_log_detail_query.py` - Python implementation of the log query functionality

## Features

- Query logs by space ID, store ID, and tail name
- Search logs with text filters
- Specify custom time ranges
- Configurable API endpoint
- Comprehensive error handling
- Command-line and programmatic usage

## Requirements

- Python 3.6+
- `requests` library

Install dependencies:
```bash
pip install requests
```

## Documentation

- For quick start and common tasks, see `SKILL.md`
- For technical details and API reference, see `reference.md`

## Original Java Implementation

This skill is based on:
- `run.mone.mcp.hera.analysis.function.HeraLogDetailFunction`
- `run.mone.mcp.hera.analysis.service.HeraLogDetailService`

Location: `src/main/java/run/mone/mcp/hera/analysis/function/HeraLogDetailFunction.java`
