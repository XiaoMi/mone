# Gateway API Query Skill

A Python-based skill for querying Gateway API information. This is a Python implementation of the `ApiFunction` Java class.

## Overview

This skill provides functionality to query Gateway API information with various filters including:
- Environment selection (staging/online)
- Keyword-based fuzzy search
- Application name filtering
- URL-based detail lookup

## Quick Start

### Command-Line Usage

```bash
cd .claude/skills/gateway/scripts

# List all APIs in staging environment
python gateway_api_query.py list staging

# Search APIs by keyword
python gateway_api_query.py list staging --keyword user

# Filter by applications
python gateway_api_query.py list staging --applications "user-service,auth-service"

# Get API details by URL
python gateway_api_query.py detail online --url "/api/v1/user/login"
```

### Python Usage

```python
from gateway_api_query import list_api_info, detail_by_url

# List APIs
result = list_api_info(env="staging", keyword="user")
print(result)

# Get API details
result = detail_by_url(env="online", url="/api/v1/user/login")
print(result)
```

## Files

- `SKILL.md` - Main skill documentation and usage guide
- `reference.md` - Technical reference and detailed API documentation
- `scripts/gateway_api_query.py` - Python implementation of the API query functionality

## Features

- List APIs by environment (staging/online)
- Search APIs with keyword filters
- Filter APIs by application names
- Get detailed API information by URL
- Configurable Gateway endpoints via environment variables
- Comprehensive error handling
- Command-line and programmatic usage

## Requirements

- Python 3.6+
- `requests` library

Install dependencies:
```bash
pip install requests
```

## Environment Variables

Set up the following environment variables before using:

```bash
export GATEWAY_URL='{"staging":"http://staging-gateway.com","online":"http://online-gateway.com"}'
export GATEWAY_API_KEY=your_api_key_here
export GATEWAY_API_USER=your_username_here
```

## Documentation

- For quick start and common tasks, see `SKILL.md`
- For technical details and API reference, see `reference.md`

## Original Java Implementation

This skill is based on:
- `run.mone.mcp.gateway.function.ApiFunction`
- `run.mone.mcp.gateway.service.GatewayService`

Location: `src/main/java/run/mone/mcp/gateway/function/ApiFunction.java`

## Supported Operations

### 1. listApiInfo
Query API list with optional filters:
- Environment (required): staging or online
- Keyword (optional): fuzzy search for API URLs
- Applications (optional): filter by application names

### 2. detailByUrl
Get detailed information for a specific API:
- Environment (required): staging or online
- URL (required): the API endpoint URL

## Example Workflows

### Find User-Related APIs
```python
from gateway_api_query import list_api_info

# Search for user APIs in staging
result = list_api_info(env="staging", keyword="user")
print(result)
```

### Get Details for Specific Endpoint
```python
from gateway_api_query import detail_by_url

# Get details for login API
result = detail_by_url(
    env="online",
    url="/api/v1/user/login"
)
print(result)
```

### Filter APIs by Multiple Applications
```python
from gateway_api_query import list_api_info

# Get APIs from specific services
result = list_api_info(
    env="staging",
    applications=["user-service", "auth-service", "order-service"]
)
print(result)
```