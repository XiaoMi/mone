#!/bin/bash

# 测试后端连接脚本

cd "$(dirname "$0")/.."

# 读取 .env 文件
if [ -f .env ]; then
    export $(cat .env | grep -v '^#' | xargs)
fi

API_URL=${API_BASE_URL:-http://localhost:8080/agent-manager}

echo "════════════════════════════════════════════"
echo "    Backend Connection Test"
echo "════════════════════════════════════════════"
echo ""
echo "Testing: $API_URL"
echo ""

# 测试基本连接
echo "1. Testing basic connection..."
if curl -s --connect-timeout 5 "$API_URL" > /dev/null 2>&1; then
    echo "   ✅ Backend is reachable"
else
    echo "   ❌ Cannot connect to backend"
    echo ""
    echo "Troubleshooting:"
    echo "  • Check if backend is running"
    echo "  • Verify API_BASE_URL in .env: $API_URL"
    echo "  • Test manually: curl $API_URL"
    exit 1
fi

# 测试 health 端点
echo ""
echo "2. Testing /api/health endpoint..."
HEALTH_RESPONSE=$(curl -s -w "\n%{http_code}" "$API_URL/api/health" 2>/dev/null)
HTTP_CODE=$(echo "$HEALTH_RESPONSE" | tail -n1)
RESPONSE_BODY=$(echo "$HEALTH_RESPONSE" | head -n-1)

if [ "$HTTP_CODE" = "200" ]; then
    echo "   ✅ Health check passed (200 OK)"
    if [ -n "$RESPONSE_BODY" ]; then
        echo "   Response: $RESPONSE_BODY"
    fi
else
    echo "   ⚠️  Health endpoint returned: $HTTP_CODE"
fi

# 测试登录端点
echo ""
echo "3. Testing /api/auth/login endpoint..."
LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d '{"username":"test","password":"test"}' \
    "$API_URL/api/auth/login" 2>/dev/null)
LOGIN_CODE=$(echo "$LOGIN_RESPONSE" | tail -n1)

if [ "$LOGIN_CODE" = "200" ] || [ "$LOGIN_CODE" = "401" ] || [ "$LOGIN_CODE" = "400" ]; then
    echo "   ✅ Login endpoint is responding ($LOGIN_CODE)"
else
    echo "   ⚠️  Login endpoint returned: $LOGIN_CODE"
fi

# DNS 解析测试
echo ""
echo "4. Testing DNS resolution..."
HOST=$(echo "$API_URL" | sed -e 's|^[^/]*//||' -e 's|/.*$||' -e 's|:.*$||')
if nslookup "$HOST" > /dev/null 2>&1; then
    echo "   ✅ DNS resolution successful for $HOST"
else
    echo "   ❌ Cannot resolve hostname: $HOST"
fi

# 端口测试
echo ""
echo "5. Testing port connectivity..."
PORT=$(echo "$API_URL" | grep -oP ':\K[0-9]+' || echo "80")
if timeout 5 bash -c "cat < /dev/null > /dev/tcp/$HOST/$PORT" 2>/dev/null; then
    echo "   ✅ Port $PORT is open"
else
    echo "   ❌ Cannot connect to port $PORT"
fi

echo ""
echo "════════════════════════════════════════════"
echo "Test completed!"
echo ""
echo "Summary:"
echo "  Backend URL: $API_URL"
echo "  Status: Check results above"
echo ""
