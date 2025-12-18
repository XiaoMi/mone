#!/usr/bin/env python3
"""Search WeKnora knowledge base with tag filtering."""

import argparse
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


def search_knowledge(
    query: str,
    filter_tags: list[str] | None = None,
    tag_mode: str = "any",
    knowledge_base_id: str | None = None,
) -> dict:
    """Search the knowledge base with optional tag filtering."""
    kb_id = knowledge_base_id or DEFAULT_KNOWLEDGE_BASE_ID

    if not WEKNORA_API_KEY:
        return {"success": False, "error": "WEKNORA_API_KEY not configured"}

    if not query:
        return {"success": False, "error": "Query is required"}

    url = f"{WEKNORA_BASE_URL}/api/v1/knowledge-search"

    request_body = {
        "query": query,
        "knowledge_base_id": kb_id,
    }

    if filter_tags:
        request_body["filter_tags"] = filter_tags
        if tag_mode in ("any", "all"):
            request_body["tag_mode"] = tag_mode

    try:
        with httpx.Client(timeout=60) as client:
            response = client.post(
                url,
                headers={
                    "X-API-Key": WEKNORA_API_KEY,
                    "Content-Type": "application/json",
                },
                json=request_body,
            )
            response.raise_for_status()
            data = response.json()

            if data.get("success") is False:
                return {"success": False, "error": data.get("error", "Search failed")}

            search_results = data.get("data", [])

            # Process results - extract key information
            processed_results = []
            for item in search_results:
                processed_item = {
                    "id": item.get("id", ""),
                    "content": item.get("content", ""),
                    "score": item.get("score", 0.0),
                }

                # Process metadata (exclude imageInfo)
                metadata = item.get("metadata", {})
                if metadata:
                    processed_metadata = {}
                    for key in ("source", "page", "chunk_index", "document_id", "created_at"):
                        if key in metadata:
                            processed_metadata[key] = metadata[key]
                    processed_item["metadata"] = processed_metadata

                processed_results.append(processed_item)

            result = {
                "success": True,
                "query": query,
                "knowledgeBaseId": kb_id,
                "resultCount": len(processed_results),
                "results": processed_results,
            }

            if filter_tags:
                result["filterTags"] = filter_tags
                result["tagMode"] = tag_mode

            return result

    except httpx.HTTPStatusError as e:
        return {"success": False, "error": f"HTTP error: {e.response.status_code}"}
    except Exception as e:
        return {"success": False, "error": str(e)}


def main():
    parser = argparse.ArgumentParser(description="Search WeKnora knowledge base")
    parser.add_argument("--query", "-q", required=True, help="Search query")
    parser.add_argument("--tags", "-t", help="JSON array of tag names")
    parser.add_argument("--tag-mode", "-m", default="any", choices=["any", "all"])
    parser.add_argument("--kb-id", help="Knowledge base ID")

    args = parser.parse_args()

    filter_tags = None
    if args.tags:
        try:
            filter_tags = json.loads(args.tags)
        except json.JSONDecodeError:
            print(json.dumps({"success": False, "error": "Invalid tags JSON format"}))
            sys.exit(1)

    result = search_knowledge(
        query=args.query,
        filter_tags=filter_tags,
        tag_mode=args.tag_mode,
        knowledge_base_id=args.kb_id,
    )
    print(json.dumps(result, ensure_ascii=False, indent=2))


if __name__ == "__main__":
    main()