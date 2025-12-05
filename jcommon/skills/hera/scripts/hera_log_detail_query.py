#!/usr/bin/env python3
"""
Hera Log Detail Query Script

This script provides functionality to query Hera log details with various filters.
It's a Python implementation of the HeraLogDetailFunction Java class.

Author: Converted from HeraLogDetailFunction.java
"""

import json
import sys
import time
import os
from typing import Optional
import requests


# Default API URL (can be overridden by environment variable or parameter)
DEFAULT_API_URL = "http://xxxxxx"


def query_log_detail(
    space_id: int,
    store_id: int,
    tail_name: str,
    input_text: str = "",
    start_time: Optional[str] = None,
    end_time: Optional[str] = None,
    api_url: Optional[str] = None
) -> str:
    """
    Query Hera log details.

    Args:
        space_id: Space ID (required)
        store_id: Store ID (required)
        tail_name: Tail name, e.g., "test-tail-name" (required)
        input_text: Search input content, may contain special characters (optional)
        start_time: Query start time in milliseconds timestamp string (optional, defaults to 1 hour ago)
        end_time: Query end time in milliseconds timestamp string (optional, defaults to current time)
        api_url: API endpoint URL (optional, uses default from environment or constant)

    Returns:
        JSON response string from Hera API

    Raises:
        ValueError: If required parameters are invalid
        requests.RequestException: If HTTP request fails
    """

    # Validate required parameters
    if not isinstance(space_id, int) or space_id == 0:
        raise ValueError("spaceId cannot be empty or invalid")

    if not isinstance(store_id, int) or store_id == 0:
        raise ValueError("storeId cannot be empty or invalid")

    if not tail_name or not isinstance(tail_name, str):
        raise ValueError("tailName cannot be empty")

    # Get API URL from parameter, environment, or use default
    if api_url is None:
        api_url = os.environ.get("HERA_LOG_DETAIL_API_URL", DEFAULT_API_URL)

    # Handle time parameters
    current_time_ms = int(time.time() * 1000)

    if end_time is None or end_time == "":
        end_time = str(current_time_ms)

    if start_time is None or start_time == "":
        # Default to 1 hour ago
        start_time = str(current_time_ms - 3600000)

    # Build request body as a JSON array containing one object
    request_body = [
        {
            "spaceId": space_id,
            "storeId": store_id,
            "input": input_text,
            "tailName": tail_name,
            "startTime": start_time,
            "endTime": end_time
        }
    ]

    # Convert to JSON string
    request_json = json.dumps(request_body, ensure_ascii=False)

    print(f"Sending Hera log detail query request")
    print(f"URL: {api_url}")
    print(f"Body: {request_json}", file=sys.stderr)

    try:
        # Send HTTP POST request
        headers = {
            "Content-Type": "application/json",
            "x-debug": "true"
        }

        response = requests.post(
            api_url,
            data=request_json.encode('utf-8'),
            headers=headers,
            timeout=30
        )

        # Check response status
        response.raise_for_status()

        # Get response text
        response_text = response.text

        print(f"Received Hera log detail query response", file=sys.stderr)
        print(f"Status: {response.status_code}", file=sys.stderr)

        return response_text

    except requests.exceptions.RequestException as e:
        error_msg = f"Query failed: {str(e)}"
        print(error_msg, file=sys.stderr)
        raise


def main():
    """
    Main function for command-line usage.

    Usage:
        python hera_log_detail_query.py <space_id> <store_id> <tail_name> [input] [start_time] [end_time]

    Example:
        python hera_log_detail_query.py 123 456 "test-tail-name"
        python hera_log_detail_query.py 123 456 "test-tail-name" "error" "1638316800000" "1638320400000"
    """

    if len(sys.argv) < 4:
        print("Usage: python hera_log_detail_query.py <space_id> <store_id> <tail_name> [input] [start_time] [end_time]")
        print("\nRequired arguments:")
        print("  space_id     - Space ID (integer)")
        print("  store_id     - Store ID (integer)")
        print("  tail_name    - Tail name (string)")
        print("\nOptional arguments:")
        print("  input        - Search input content (string)")
        print("  start_time   - Query start time in milliseconds (string)")
        print("  end_time     - Query end time in milliseconds (string)")
        sys.exit(1)

    # Parse command-line arguments
    try:
        space_id = int(sys.argv[1])
        store_id = int(sys.argv[2])
        tail_name = sys.argv[3]
        input_text = sys.argv[4] if len(sys.argv) > 4 else ""
        start_time = sys.argv[5] if len(sys.argv) > 5 else None
        end_time = sys.argv[6] if len(sys.argv) > 6 else None

        # Query log details
        result = query_log_detail(
            space_id=space_id,
            store_id=store_id,
            tail_name=tail_name,
            input_text=input_text,
            start_time=start_time,
            end_time=end_time
        )

        # Print result
        print(result)

    except ValueError as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
