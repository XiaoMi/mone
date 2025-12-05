---
name: hera
description: Hera log detail query toolkit for querying log details by spaceId, storeId, tailName and time range. Use this when you need to query Hera log information programmatically.
---

# Hera Log Query Guide

## Overview

This guide covers Hera log detail query operations using Python scripts. The skill provides functionality to query Hera log details with various filters including spaceId, storeId, input search terms, tailName, and time ranges.

## Quick Start

```python
import sys
sys.path.append('scripts')
from hera_log_detail_query import query_log_detail

# Query logs with basic parameters
result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="test-tail-name"
)

# Query logs with all parameters
result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="test-tail-name",
    input_text="error message",
    start_time="1638316800000",
    end_time="1638320400000"
)
```

## Main Function

### query_log_detail - Query Hera Log Details

Query Hera log details with filters.

**Parameters:**
- `space_id` (int, required): Space ID
- `store_id` (int, required): Store ID
- `tail_name` (str, required): Tail name, e.g., "test-tail-name"
- `input_text` (str, optional): Search input content, may contain special characters like quotes
- `start_time` (str, optional): Query start time in milliseconds timestamp string. Defaults to 1 hour ago
- `end_time` (str, optional): Query end time in milliseconds timestamp string. Defaults to current time
- `api_url` (str, optional): API endpoint URL. Uses default from environment if not specified

**Returns:**
- JSON response from Hera API containing log details

**Example:**
```python
from hera_log_detail_query import query_log_detail

# Basic query
result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="my-app-logs"
)

# Query with search term
result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="my-app-logs",
    input_text="ERROR"
)

# Query with custom time range
import time
end_time = int(time.time() * 1000)
start_time = end_time - 3600000  # 1 hour ago

result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="my-app-logs",
    start_time=str(start_time),
    end_time=str(end_time)
)
```

## Common Tasks

### Query Recent Logs
```python
from hera_log_detail_query import query_log_detail

# Query logs from the last hour (default)
result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="application-logs"
)

print(result)
```

### Search for Specific Log Messages
```python
from hera_log_detail_query import query_log_detail

# Search for error logs
result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="application-logs",
    input_text="Exception"
)

print(result)
```

### Query Logs in Custom Time Range
```python
from hera_log_detail_query import query_log_detail
import time

# Query logs from 2 hours ago to 1 hour ago
current_time = int(time.time() * 1000)
end_time = str(current_time - 3600000)  # 1 hour ago
start_time = str(current_time - 7200000)  # 2 hours ago

result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="application-logs",
    start_time=start_time,
    end_time=end_time
)

print(result)
```

## Configuration

The default API URL is configured in the script. You can override it by:

1. Setting environment variable `HERA_LOG_DETAIL_API_URL`
2. Passing `api_url` parameter to the function

## Error Handling

The script includes comprehensive error handling:
- Parameter validation for required fields
- HTTP request error handling
- JSON parsing error handling
- Detailed error messages for troubleshooting

## Quick Reference

| Task | Function | Required Parameters |
|------|----------|---------------------|
| Query recent logs | `query_log_detail()` | space_id, store_id, tail_name |
| Search logs | `query_log_detail()` | space_id, store_id, tail_name, input_text |
| Custom time range | `query_log_detail()` | space_id, store_id, tail_name, start_time, end_time |

## Next Steps

- For detailed API documentation, see reference.md
- For troubleshooting, check the error messages in the response
