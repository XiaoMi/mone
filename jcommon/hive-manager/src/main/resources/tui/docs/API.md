# API Integration Guide

This document describes how Hive Manager TUI integrates with the backend API.

## Base Configuration

The TUI uses two base URLs configured via environment variables:

```env
API_BASE_URL=http://localhost:8080/agent-manager
WS_BASE_URL=ws://localhost:8080/agent-manager
```

## ✅ Actual Backend API Endpoints (Updated)

All API paths have been updated to match the actual backend controllers.

## Health Check

### Ping

**Endpoint:** `GET /ping` ✅

**Response:** `"pong"` (plain text)

**Usage:**
```bash
curl http://localhost:8080/agent-manager/ping
# Response: pong
```

This is used to test if the backend is running.

## Authentication

### Login

**Endpoint:** `POST /api/v1/users/login` ✅ (Updated)

**Request:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response:**
```json
{
  "code": 200,
  "data": {
    "user": {
      "id": 1,
      "username": "user",
      "cname": "User Name",
      "avatar": "base64..."
    },
    "token": "jwt-token-here"
  }
}
```

**Usage in TUI:**
```typescript
const response = await login(username, password)
if (response.data.code === 200) {
  setUser(response.data.data.user)
  setToken(response.data.data.token)
  http.setToken(token) // Set for subsequent requests
}
```

### Get User Info

**Endpoint:** `GET /api/v1/users/info` ✅ (Updated)

**Headers:**
```
Authorization: Bearer {token}
```

**Response:**
```json
{
  "code": 200,
  "data": {
    "id": 1,
    "username": "user",
    "cname": "User Name"
  }
}
```

## Agent Management

### Get Agent List

**Endpoint:** `GET /api/v1/agents/list` ✅ (Updated)

**Query Parameters:**
- `searchQuery` (optional): Search term
- `isFavorite` (optional): Boolean to filter favorites

**Response:**
```json
{
  "code": 200,
  "data": [
    {
      "agent": {
        "id": 1,
        "name": "MyAgent",
        "description": "Description",
        "isPublic": true,
        "group": "default",
        "version": "1.0.0",
        "ctime": "2024-01-01T00:00:00Z",
        "utime": "2024-01-01T00:00:00Z"
      },
      "instances": [
        {
          "ip": "127.0.0.1",
          "port": 8080,
          "status": "running"
        }
      ],
      "isFavorite": false
    }
  ]
}
```

### Get Agent Detail

**Endpoint:** `GET /api/v1/agents/{id}` ✅ (Updated)

**Response:**
```json
{
  "code": 200,
  "data": {
    "agent": { /* Agent object */ },
    "instances": [ /* Array of instances */ ]
  }
}
```

### Create Agent

**Endpoint:** `POST /api/v1/agents/create` ✅ (Updated)

**Request:**
```json
{
  "name": "string",
  "description": "string",
  "agentUrl": "string",
  "isPublic": boolean,
  "image": "base64-string (optional)"
}
```

### Update Agent

**Endpoint:** `PUT /api/v1/agents/{id}` ✅ (Updated)

**Request:** Same as Create

### Delete Agent

**Endpoint:** `DELETE /api/v1/agents/{id}` ✅ (Updated)

### Favorite Operations

**Add Favorite:** `POST /api/favorite/add`
```json
{
  "userId": 1,
  "type": 1,
  "targetId": 1
}
```

**Delete Favorite:** `POST /api/favorite/remove` ✅ (Updated)
```json
{
  "userId": 1,
  "type": 1,
  "targetId": 1
}
```

## Task Management

### Get Task List

**Endpoint:** `GET /api/v1/tasks` ✅ (Updated)

**Query Parameters:**
- `serverAgentId` (optional): Filter by agent ID

**Response:**
```json
{
  "code": 200,
  "data": [
    {
      "taskUuid": "uuid",
      "title": "Task Title",
      "description": "Description",
      "status": "pending",
      "serverAgentId": 1,
      "ctime": "2024-01-01T00:00:00Z",
      "utime": "2024-01-01T00:00:00Z"
    }
  ]
}
```

### Get Task Detail

**Endpoint:** `GET /api/v1/tasks/{taskUuid}` ✅ (Updated)

### Create Task

**Endpoint:** `POST /api/v1/tasks` ✅ (Updated)

**Request:**
```json
{
  "title": "string",
  "description": "string",
  "serverAgentId": number,
  "clientAgentId": number (optional),
  "skillId": number (optional)
}
```

### Update Task

**Endpoint:** `PUT /api/v1/tasks/{taskUuid}/update` ✅ (Updated)

**Request:**
```json
{
  "description": "string",
  "serverAgentId": number
}
```

### Execute Task

**Endpoint:** `POST /api/v1/tasks/execute` ✅ (Updated)

**Request:**
```json
{
  "id": "task-uuid",
  "metadata": {
    "input": "string",
    "serverAgentId": number
  }
}
```

## WebSocket Communication

### Connection

**URL Format:**
```
ws://{host}/ws/{name}:{group}:{version}:{ip}:{port}
```

**Example:**
```
ws://localhost:8080/agent-manager/ws/MyAgent:default:1.0.0:127.0.0.1:8080
```

### Message Format

**Outgoing (Client → Server):**
```json
{
  "mapData": {
    "outerTag": "use_mcp_tool",
    "server_name": "MyAgent:default:1.0.0:127.0.0.1:8080",
    "tool_name": "stream_MyAgent_chat",
    "arguments": "{\"message\":\"Hello\",\"__owner_id__\":\"user\"}"
  },
  "agentId": 1,
  "agentInstance": {
    "ip": "127.0.0.1",
    "port": 8080
  },
  "conversationId": "uuid"
}
```

**Incoming (Server → Client):**
- Raw text responses
- Structured data with tags (parsed by client)
- Usage data in `<usage>...</usage>` tags

### WebSocket Events

**On Open:**
```typescript
ws.on('open', () => {
  // Connection established
  // Can send initial messages
})
```

**On Message:**
```typescript
ws.on('message', (data: Buffer) => {
  const message = data.toString()
  // Parse and handle message
})
```

**On Error:**
```typescript
ws.on('error', (err) => {
  // Handle error
})
```

**On Close:**
```typescript
ws.on('close', () => {
  // Clean up
})
```

## HTTP Client Configuration

### Interceptors

**Request Interceptor:**
```typescript
instance.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  }
)
```

**Response Interceptor:**
```typescript
instance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized - clear token, redirect to login
    }
    return Promise.reject(error)
  }
)
```

### Error Handling

**Network Errors:**
```typescript
try {
  const response = await api.call()
} catch (err: any) {
  const message = err.response?.data?.message || 'Network error'
  // Display error to user
}
```

## Response Codes

Standard HTTP status codes are used:

- `200` - Success
- `400` - Bad Request
- `401` - Unauthorized (token invalid/expired)
- `403` - Forbidden
- `404` - Not Found
- `500` - Server Error

## API Response Format

All API responses follow this format:

```typescript
interface ApiResponse<T> {
  code: number      // 200 for success, error code otherwise
  message?: string  // Error message if code !== 200
  data?: T         // Response data if successful
}
```

## Rate Limiting

(To be documented based on backend implementation)

## Pagination

(To be documented based on backend implementation)

## Best Practices

1. **Always check response codes**
   ```typescript
   if (response.data.code === 200) {
     // Success
   } else {
     // Handle error
   }
   ```

2. **Handle network errors**
   ```typescript
   try {
     await apiCall()
   } catch (err) {
     // Show user-friendly error
   }
   ```

3. **Use TypeScript types**
   ```typescript
   const response = await getAgentList()
   // response is typed as ApiResponse<AgentListItem[]>
   ```

4. **Clean up WebSocket connections**
   ```typescript
   useEffect(() => {
     connectWebSocket()
     return () => {
       disconnectWebSocket()
     }
   }, [])
   ```

5. **Debounce search requests**
   ```typescript
   const debouncedSearch = useDebounce(searchQuery, 300)
   useEffect(() => {
     fetchAgents(debouncedSearch)
   }, [debouncedSearch])
   ```
