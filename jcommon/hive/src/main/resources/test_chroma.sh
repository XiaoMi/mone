#!/bin/bash

echo "Testing ChromaDB connection..."
response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8000/api/v1/heartbeat)

if [ "$response" = "200" ]; then
    echo "✅ ChromaDB is running and responsive"
    echo "Heartbeat response:"
    curl -s http://localhost:8000/api/v1/heartbeat | python -m json.tool
else
    echo "❌ ChromaDB is not responding (HTTP code: $response)"
    exit 1
fi
