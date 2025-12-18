#!/usr/bin/env python3
"""Get all available tags from WeKnora knowledge base."""

import json
import os
import sys

import httpx
from dotenv import load_dotenv

# Load environment variables
load_dotenv()

WEKNORA_BASE_URL = os.getenv("WEKNORA_BASE_URL", "http://localhost:8080")
WEKNORA_API_KEY = os.getenv("WEKNORA_API_KEY", "")
DEFAULT_KNOWLEDGE_BASE_ID = "3c69d393-de3c-4d5f-9ab4-ccc9f1616592"


def get_tags(knowledge_base_id: str | None = None) -> dict:
    """Fetch all tags from the knowledge base."""
    kb_id = knowledge_base_id or DEFAULT_KNOWLEDGE_BASE_ID

    if not WEKNORA_API_KEY:
        return {"success": False, "error": "WEKNORA_API_KEY not configured"}

    url = f"{WEKNORA_BASE_URL}/api/v1/knowledge-bases/{kb_id}/tags"

    try:
        with httpx.Client(timeout=30) as client:
            response = client.get(
                url,
                headers={
                    "X-API-Key": WEKNORA_API_KEY,
                    "Content-Type": "application/json",
                },
            )
            response.raise_for_status()
            data = response.json()

            if data.get("success"):
                tags = data.get("data", [])
                return {
                    "success": True,
                    "knowledgeBaseId": kb_id,
                    "tagCount": len(tags),
                    "tags": [
                        {"name": tag.get("name"), "description": tag.get("description")}
                        for tag in tags
                    ],
                }
            else:
                return {"success": False, "error": data.get("error", "Unknown error")}

    except httpx.HTTPStatusError as e:
        return {"success": False, "error": f"HTTP error: {e.response.status_code}"}
    except Exception as e:
        return {"success": False, "error": str(e)}


if __name__ == "__main__":
    kb_id = sys.argv[1] if len(sys.argv) > 1 else None
    result = get_tags(kb_id)
    print(json.dumps(result, ensure_ascii=False, indent=2))