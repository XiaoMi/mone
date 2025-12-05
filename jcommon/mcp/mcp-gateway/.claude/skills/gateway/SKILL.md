---
name: gateway
description: Gateway API query toolkit for querying and managing API information. Use this when you need to query Gateway API details, list APIs, or search for specific APIs programmatically.
---

# Gateway API Query Guide

## Overview

This guide covers Gateway API query operations using Python scripts. The skill provides functionality to query Gateway API information with various filters including environment (staging/online), keyword search, URL lookup, and application filtering.

## Quick Start

```python
import sys
sys.path.append('scripts')
from gateway_api_query import list_api_info, detail_by_url

# List APIs with keyword search
result = list_api_info(
    env="staging",
    keyword="user"
)

# Get API details by URL
result = detail_by_url(
    env="online",
    url="/api/v1/user/login"
)

# List APIs filtered by applications
result = list_api_info(
    env="staging",
    applications=["app1", "app2"]
)
```

## Main Functions

### list_api_info - List Gateway APIs

Query Gateway API list with filters.

**Parameters:**
- `env` (str, required): Environment type, either "staging" or "online"
- `keyword` (str, optional): Fuzzy search keyword for API URL matching
- `applications` (list, optional): List of application names to filter APIs

**Returns:**
- JSON response from Gateway API containing API list

**Example:**
```python
from gateway_api_query import list_api_info

# Basic query for all APIs
result = list_api_info(env="staging")

# Search APIs by keyword
result = list_api_info(
    env="staging",
    keyword="user"
)

# Filter by applications
result = list_api_info(
    env="staging",
    applications=["user-service", "auth-service"]
)

# Combine keyword and applications
result = list_api_info(
    env="online",
    keyword="login",
    applications=["auth-service"]
)
```

### detail_by_url - Get API Details by URL

Get detailed information for a specific API by its URL.

**Parameters:**
- `env` (str, required): Environment type, either "staging" or "online"
- `url` (str, required): API URL to query details for

**Returns:**
- JSON response from Gateway API containing detailed API information

**Example:**
```python
from gateway_api_query import detail_by_url

# Get details for a specific API
result = detail_by_url(
    env="staging",
    url="/api/v1/user/profile"
)

# Query online environment
result = detail_by_url(
    env="online",
    url="/api/v1/order/create"
)
```

## Common Tasks

### List All APIs in an Environment
```python
from gateway_api_query import list_api_info

# List all staging APIs
result = list_api_info(env="staging")
print(result)

# List all online APIs
result = list_api_info(env="online")
print(result)
```

### Search for Specific APIs
```python
from gateway_api_query import list_api_info

# Search for user-related APIs
result = list_api_info(
    env="staging",
    keyword="user"
)
print(result)

# Search for order APIs in online environment
result = list_api_info(
    env="online",
    keyword="order"
)
print(result)
```

### Filter APIs by Application
```python
from gateway_api_query import list_api_info

# Get APIs from specific applications
result = list_api_info(
    env="staging",
    applications=["user-service", "product-service"]
)
print(result)
```

### Get Detailed API Information
```python
from gateway_api_query import detail_by_url

# Get full details for a specific endpoint
result = detail_by_url(
    env="staging",
    url="/api/v1/user/login"
)
print(result)
```

## Configuration

The Gateway URLs and credentials are configured via environment variables:

1. `GATEWAY_URL` - JSON object mapping environments to URLs:
   ```bash
   export GATEWAY_URL='{"staging":"http://staging-gateway.com","online":"http://online-gateway.com"}'
   ```

2. `GATEWAY_API_KEY` - API key for authentication
   ```bash
   export GATEWAY_API_KEY=your_api_key_here
   ```

3. `GATEWAY_API_USER` - Username for authentication
   ```bash
   export GATEWAY_API_USER=your_username_here
   ```

You can also pass `gateway_url`, `api_key`, and `api_user` parameters directly to the functions to override environment variables.

## Error Handling

The script includes comprehensive error handling:
- Parameter validation for required fields
- HTTP request error handling
- JSON parsing error handling
- Detailed error messages for troubleshooting

All errors are returned in the format: `"error: <error message>"`

## Quick Reference

| Task | Function | Required Parameters |
|------|----------|---------------------|
| List all APIs | `list_api_info()` | env |
| Search APIs | `list_api_info()` | env, keyword |
| Filter by apps | `list_api_info()` | env, applications |
| Get API details | `detail_by_url()` | env, url |

## Next Steps

- For detailed API documentation, see reference.md
- For troubleshooting, check the error messages in the response
- For command-line usage, see README.md