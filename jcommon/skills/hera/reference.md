# Hera Log Query Reference

## Technical Details

### API Endpoint

The default API endpoint is:
```
http://xxxx
```

This can be overridden by:
1. Setting the `HERA_LOG_DETAIL_API_URL` environment variable
2. Passing the `api_url` parameter to `query_log_detail()`

### Request Format

The API expects a POST request with:

**Headers:**
- `Content-Type: application/json`
- `x-debug: true`

**Body:**
A JSON array containing a single object with the following fields:

```json
[
  {
    "spaceId": 123,
    "storeId": 456,
    "input": "search term",
    "tailName": "test-tail-name",
    "startTime": "1638316800000",
    "endTime": "1638320400000"
  }
]
```

**Field Descriptions:**
- `spaceId` (integer, required): The space ID to query logs from
- `storeId` (integer, required): The store ID to query logs from
- `input` (string, optional): Search term or filter text, can contain special characters including quotes
- `tailName` (string, required): The tail name identifier, e.g., "test-tail-name"
- `startTime` (string, optional): Start time in milliseconds since epoch. Defaults to 1 hour ago if not provided
- `endTime` (string, optional): End time in milliseconds since epoch. Defaults to current time if not provided

### Response Format

The API returns a JSON response containing the log query results. The exact format depends on the Hera API implementation.

### Error Handling

The script handles the following error cases:

1. **Parameter Validation Errors:**
   - Invalid or zero space_id
   - Invalid or zero store_id
   - Empty or null tail_name

2. **HTTP Request Errors:**
   - Connection timeout (30 seconds)
   - HTTP status code errors (4xx, 5xx)
   - Network connectivity issues

3. **Other Errors:**
   - JSON parsing errors
   - Invalid timestamp formats

All errors are logged to stderr and raised as exceptions.

## Implementation Details

### Time Handling

- All timestamps are in milliseconds since Unix epoch
- Default time range is the last 1 hour
- Current time is calculated using `time.time() * 1000`
- 1 hour offset is 3600000 milliseconds

### Character Encoding

- Request body is encoded in UTF-8
- The `ensure_ascii=False` flag is used in JSON serialization to properly handle non-ASCII characters
- Special characters in the `input` field (including quotes) are automatically escaped by the JSON encoder

### HTTP Client

The script uses the `requests` library with:
- 30-second timeout
- Automatic status code validation via `raise_for_status()`
- UTF-8 encoding for request body

## Command-Line Usage

### Basic Syntax

```bash
python hera_log_detail_query.py <space_id> <store_id> <tail_name> [input] [start_time] [end_time]
```

### Examples

1. **Query recent logs (last 1 hour):**
```bash
python hera_log_detail_query.py 123 456 "my-app-logs"
```

2. **Search for specific term:**
```bash
python hera_log_detail_query.py 123 456 "my-app-logs" "ERROR"
```

3. **Query with custom time range:**
```bash
python hera_log_detail_query.py 123 456 "my-app-logs" "" "1638316800000" "1638320400000"
```

4. **Search with custom time range:**
```bash
python hera_log_detail_query.py 123 456 "my-app-logs" "Exception" "1638316800000" "1638320400000"
```

## Programmatic Usage

### Basic Usage

```python
from hera_log_detail_query import query_log_detail

result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="my-app-logs"
)
print(result)
```

### With Search Term

```python
result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="my-app-logs",
    input_text="ERROR"
)
```

### With Custom Time Range

```python
import time

current_time = int(time.time() * 1000)
one_hour_ago = current_time - 3600000

result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="my-app-logs",
    start_time=str(one_hour_ago),
    end_time=str(current_time)
)
```

### With Custom API URL

```python
result = query_log_detail(
    space_id=123,
    store_id=456,
    tail_name="my-app-logs",
    api_url="https://custom.api.endpoint/path"
)
```

### Error Handling

```python
from hera_log_detail_query import query_log_detail

try:
    result = query_log_detail(
        space_id=123,
        store_id=456,
        tail_name="my-app-logs"
    )
    print(f"Success: {result}")
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
| `HeraLogDetailFunction.apply()` | `query_log_detail()` |
| `HeraLogDetailService.queryLogDetail()` | Integrated into `query_log_detail()` |
| `HeraLogDetailService.sendHttpPostRequest()` | `requests.post()` |
| `getIntParam()` | Type validation in function signature |
| `getStringParam()` | Default parameter values |
| `@Value("${hera.log.detail.api.url}")` | Environment variable or constant |

### Key Differences

1. **Error Handling:** Python uses exceptions instead of Flux error handling
2. **Configuration:** Uses environment variables instead of Spring properties
3. **HTTP Client:** Uses `requests` library instead of Apache HttpClient
4. **Async:** Synchronous implementation instead of reactive Flux

## Troubleshooting

### Common Issues

1. **Connection Timeout:**
   - Check network connectivity
   - Verify API endpoint is accessible
   - Increase timeout if needed

2. **Invalid Parameters:**
   - Ensure space_id and store_id are positive integers
   - Verify tail_name is not empty
   - Check timestamp format (should be milliseconds)

3. **API Errors:**
   - Check HTTP status code in error message
   - Verify API endpoint URL is correct
   - Ensure x-debug header is being sent

### Debug Mode

To see detailed request/response information:
```bash
python hera_log_detail_query.py 123 456 "test" 2>&1
```

This will output:
- Request URL
- Request body
- Response status
- Any error messages

## Future Enhancements

Potential improvements for future versions:
- Batch query support for multiple tail names
- Response parsing and formatting
- Caching layer for repeated queries
- Async/await support for concurrent queries
- Configuration file support
- Retry logic with exponential backoff
