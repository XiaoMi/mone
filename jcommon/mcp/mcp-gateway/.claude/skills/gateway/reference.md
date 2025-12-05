# Gateway API Query Reference

## Technical Details

### API Endpoints

The Gateway API endpoints are configured via the `GATEWAY_URL` environment variable as a JSON object:

```json
{
  "staging": "http://staging-gateway.com",
  "online": "http://online-gateway.com"
}
```

This can be overridden by:
1. Setting the `GATEWAY_URL` environment variable
2. Passing the `gateway_url` parameter to functions

### Authentication

The API uses two authentication parameters:
- `GATEWAY_API_KEY` - API key for authentication
- `GATEWAY_API_USER` - Username for authentication

These can be set as environment variables or passed as function parameters.

### Request Formats

#### 1. List API Info

**Endpoint:** `/open/v1/private/api/apiinfo/list`

**Method:** POST

**Headers:**
- `Content-Type: application/json`
- `X-API-KEY: {GATEWAY_API_KEY}`
- `X-API-USER: {GATEWAY_API_USER}`

**Body:**
```json
{
  "url": "keyword",
  "applications": ["app1", "app2"]
}
```

**Field Descriptions:**
- `url` (string, optional): Keyword for fuzzy search on API URLs
- `applications` (array, optional): List of application names to filter results

**Response Format:**
```json
{
  "code": 0,
  "message": "success",
  "data": [
    {
      "id": 1,
      "url": "/api/v1/user/login",
      "method": "POST",
      "application": "user-service",
      "description": "User login API"
    }
  ]
}
```

#### 2. Detail By URL

**Endpoint:** `/open/v1/private/api/apiinfo/detailByUrl?url={url}`

**Method:** GET

**Headers:**
- `X-API-KEY: {GATEWAY_API_KEY}`
- `X-API-USER: {GATEWAY_API_USER}`

**Query Parameters:**
- `url` (string, required): The API URL to query details for

**Response Format:**
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "id": 1,
    "url": "/api/v1/user/login",
    "method": "POST",
    "application": "user-service",
    "description": "User login API",
    "requestSchema": {...},
    "responseSchema": {...},
    "examples": [...]
  }
}
```

### Error Handling

The script handles the following error cases:

1. **Parameter Validation Errors:**
   - Invalid or empty environment (must be "staging" or "online")
   - Empty URL for detail_by_url operation
   - Invalid applications format (must be a list)

2. **HTTP Request Errors:**
   - Connection timeout (30 seconds)
   - HTTP status code errors (4xx, 5xx)
   - Network connectivity issues

3. **Configuration Errors:**
   - Missing GATEWAY_URL environment variable
   - Missing authentication credentials
   - Invalid JSON in GATEWAY_URL

All errors are returned in the format: `"error: <error message>"`

## Implementation Details

### HTTP Client

The script uses the `requests` library with:
- 30-second timeout
- Custom headers for authentication
- UTF-8 encoding for request body
- Automatic status code validation via `raise_for_status()`

### Environment Configuration

Environment variables are loaded in the following priority:
1. Function parameters (highest priority)
2. Environment variables
3. No defaults (will raise error if not provided)

### Character Encoding

- Request body is encoded in UTF-8
- The `ensure_ascii=False` flag is used in JSON serialization to properly handle non-ASCII characters
- Special characters in parameters are automatically escaped by the JSON encoder

## Command-Line Usage

### Basic Syntax

```bash
# List APIs
python gateway_api_query.py list <env> [--keyword <keyword>] [--applications <app1,app2>]

# Get API details
python gateway_api_query.py detail <env> --url <url>
```

### Examples

1. **List all APIs in staging:**
```bash
python gateway_api_query.py list staging
```

2. **Search for user-related APIs:**
```bash
python gateway_api_query.py list staging --keyword user
```

3. **Filter by applications:**
```bash
python gateway_api_query.py list staging --applications "user-service,auth-service"
```

4. **Combine keyword and applications:**
```bash
python gateway_api_query.py list online --keyword login --applications "auth-service"
```

5. **Get API details:**
```bash
python gateway_api_query.py detail online --url "/api/v1/user/login"
```

## Programmatic Usage

### Basic Usage

```python
from gateway_api_query import list_api_info, detail_by_url

# List all APIs
result = list_api_info(env="staging")
print(result)

# Search APIs
result = list_api_info(env="staging", keyword="user")
print(result)

# Get details
result = detail_by_url(env="online", url="/api/v1/user/login")
print(result)
```

### With Custom Configuration

```python
from gateway_api_query import list_api_info, detail_by_url

# Override gateway URL
gateway_url = {
    "staging": "https://custom-staging.com",
    "online": "https://custom-online.com"
}

result = list_api_info(
    env="staging",
    keyword="user",
    gateway_url=gateway_url,
    api_key="custom_key",
    api_user="custom_user"
)
```

### With Application Filtering

```python
from gateway_api_query import list_api_info

# Filter by multiple applications
result = list_api_info(
    env="staging",
    applications=["user-service", "order-service", "payment-service"]
)
print(result)
```

### Error Handling

```python
from gateway_api_query import list_api_info, detail_by_url
import json

try:
    result = list_api_info(env="staging", keyword="user")

    # Parse JSON response
    data = json.loads(result)

    if "error" in result:
        print(f"API error: {result}")
    else:
        print(f"Found {len(data.get('data', []))} APIs")

except ValueError as e:
    print(f"Invalid parameter: {e}")
except Exception as e:
    print(f"Query failed: {e}")
```

## Dependencies

The script requires the following Python packages:
- `requests` - HTTP client library

Install with:
```bash
pip install requests
```

## Comparison with Java Implementation

This Python script is a direct translation of the Java implementation:

| Java Class/Method | Python Equivalent |
|------------------|-------------------|
| `ApiFunction.apply()` | `list_api_info()` / `detail_by_url()` |
| `GatewayService.listApiInfo()` | `list_api_info()` |
| `GatewayService.detailByUrl()` | `detail_by_url()` |
| `HttpClient.get()` | `requests.get()` |
| `HttpClient.post()` | `requests.post()` |
| `@Value("${GATEWAY_URL}")` | `os.environ.get("GATEWAY_URL")` |

### Key Differences

1. **Error Handling:** Python uses exceptions and error strings instead of Flux error handling
2. **Configuration:** Uses environment variables instead of Spring properties
3. **HTTP Client:** Uses `requests` library instead of OkHttp
4. **Async:** Synchronous implementation instead of reactive Flux
5. **Return Type:** Returns JSON strings instead of CallToolResult objects

## Troubleshooting

### Common Issues

1. **Missing Configuration:**
   ```
   error: GATEWAY_URL environment variable not set
   ```
   **Solution:** Set the GATEWAY_URL environment variable with proper JSON format

2. **Authentication Failed:**
   ```
   error: 401 Unauthorized
   ```
   **Solution:** Verify GATEWAY_API_KEY and GATEWAY_API_USER are set correctly

3. **Invalid Environment:**
   ```
   error: env must be 'staging' or 'online'
   ```
   **Solution:** Use only "staging" or "online" as environment values

4. **Network Timeout:**
   ```
   error: Connection timeout
   ```
   **Solution:** Check network connectivity and gateway URL accessibility

### Debug Mode

To see detailed request/response information, modify the script to include:
```python
import logging
logging.basicConfig(level=logging.DEBUG)
```

This will output:
- Request URL and headers
- Request body
- Response status
- Any error messages

## Future Enhancements

Potential improvements for future versions:
- Batch query support for multiple URLs
- Response caching for repeated queries
- Async/await support for concurrent queries
- Advanced filtering options (by method, by tags, etc.)
- API versioning support
- Rate limiting and retry logic
- Export results to different formats (CSV, Excel, etc.)