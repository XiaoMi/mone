#!/usr/bin/env python3
"""
Gateway API Query Script

This script provides functionality to query Gateway API information.
It's a Python implementation of the ApiFunction Java class.

Author: Converted from ApiFunction.java
"""

import json
import sys
import os
import argparse
from typing import Optional, List
import requests


def get_gateway_config():
    """
    Get gateway URL configuration from environment variable.

    Returns:
        dict: Mapping of environment names to URLs

    Raises:
        ValueError: If GATEWAY_URL is not set or invalid
    """
    gateway_url_json = os.environ.get("GATEWAY_URL")
    if not gateway_url_json:
        raise ValueError("GATEWAY_URL environment variable not set")

    try:
        return json.loads(gateway_url_json)
    except json.JSONDecodeError as e:
        raise ValueError(f"Invalid JSON in GATEWAY_URL: {e}")


def list_api_info(
    env: str,
    keyword: Optional[str] = None,
    applications: Optional[List[str]] = None,
    gateway_url: Optional[dict] = None,
    api_key: Optional[str] = None,
    api_user: Optional[str] = None
) -> str:
    """
    List Gateway API information with optional filters.

    Args:
        env: Environment type, either "staging" or "online" (required)
        keyword: Fuzzy search keyword for API URL matching (optional)
        applications: List of application names to filter APIs (optional)
        gateway_url: Gateway URL configuration dict (optional, uses env var if not provided)
        api_key: API key for authentication (optional, uses env var if not provided)
        api_user: Username for authentication (optional, uses env var if not provided)

    Returns:
        JSON response string from Gateway API

    Raises:
        ValueError: If required parameters are invalid
        requests.RequestException: If HTTP request fails
    """

    # Validate environment
    if env not in ["staging", "online"]:
        raise ValueError("env must be 'staging' or 'online'")

    # Get configuration
    if gateway_url is None:
        gateway_url = get_gateway_config()

    if api_key is None:
        api_key = os.environ.get("GATEWAY_API_KEY", "")

    if api_user is None:
        api_user = os.environ.get("GATEWAY_API_USER", "")

    # Get host URL for environment
    if env not in gateway_url:
        raise ValueError(f"Environment '{env}' not found in gateway URL configuration")

    host = gateway_url[env]
    path = "/open/v1/private/api/apiinfo/list"
    url = host + path

    # Build request body
    request_body = {
        "pageNo": 1,
        "pageSize": 100,
        "name": "",
        "path": ""
    }
    if keyword:
        request_body["url"] = keyword
    else:
        request_body["url"] = ""
    if applications:
        request_body["applications"] = applications

    # Convert to JSON string
    request_json = json.dumps(request_body, ensure_ascii=False)

    print(f"Sending Gateway API list request to {env} environment", file=sys.stderr)
    print(f"URL: {url}", file=sys.stderr)
    print(f"Body: {request_json}", file=sys.stderr)

    try:
        # Send HTTP POST request
        headers = {
            "Content-Type": "application/json",
            "gw-tenant-id": "1"
        }

        if api_key:
            headers["x-api-key"] = api_key
        if api_user:
            headers["mone-skip-mi-dun-username"] = api_user

        response = requests.post(
            url,
            data=request_json.encode('utf-8'),
            headers=headers,
            timeout=30
        )

        # Check response status
        response.raise_for_status()

        # Get response text
        response_text = response.text

        print(f"Received Gateway API list response", file=sys.stderr)
        print(f"Status: {response.status_code}", file=sys.stderr)

        return response_text

    except requests.exceptions.RequestException as e:
        error_msg = f"error: {str(e)}"
        print(error_msg, file=sys.stderr)
        return error_msg


def detail_by_url(
    env: str,
    url: str,
    gateway_url: Optional[dict] = None,
    api_key: Optional[str] = None,
    api_user: Optional[str] = None
) -> str:
    """
    Get detailed information for a specific API by URL.

    Args:
        env: Environment type, either "staging" or "online" (required)
        url: API URL to query details for (required)
        gateway_url: Gateway URL configuration dict (optional, uses env var if not provided)
        api_key: API key for authentication (optional, uses env var if not provided)
        api_user: Username for authentication (optional, uses env var if not provided)

    Returns:
        JSON response string from Gateway API

    Raises:
        ValueError: If required parameters are invalid
        requests.RequestException: If HTTP request fails
    """

    # Validate parameters
    if env not in ["staging", "online"]:
        raise ValueError("env must be 'staging' or 'online'")

    if not url or not isinstance(url, str):
        raise ValueError("url cannot be empty")

    # Get configuration
    if gateway_url is None:
        gateway_url = get_gateway_config()

    if api_key is None:
        api_key = os.environ.get("GATEWAY_API_KEY", "")

    if api_user is None:
        api_user = os.environ.get("GATEWAY_API_USER", "")

    # Get host URL for environment
    if env not in gateway_url:
        raise ValueError(f"Environment '{env}' not found in gateway URL configuration")

    host = gateway_url[env]
    path = f"/open/v1/private/api/apiinfo/detailByUrl?url={url}"
    request_url = host + path

    print(f"Sending Gateway API detail request to {env} environment", file=sys.stderr)
    print(f"URL: {request_url}", file=sys.stderr)

    try:
        # Send HTTP GET request
        headers = {
            "gw-tenant-id": "1"
        }

        if api_key:
            headers["x-api-key"] = api_key
        if api_user:
            headers["mone-skip-mi-dun-username"] = api_user

        response = requests.get(
            request_url,
            headers=headers,
            timeout=30
        )

        # Check response status
        response.raise_for_status()

        # Get response text
        response_text = response.text

        print(f"Received Gateway API detail response", file=sys.stderr)
        print(f"Status: {response.status_code}", file=sys.stderr)

        return response_text

    except requests.exceptions.RequestException as e:
        error_msg = f"error: {str(e)}"
        print(error_msg, file=sys.stderr)
        return error_msg


def main():
    """
    Main function for command-line usage.

    Usage:
        python gateway_api_query.py list <env> [--keyword <keyword>] [--applications <app1,app2>]
        python gateway_api_query.py detail <env> --url <url>

    Examples:
        python gateway_api_query.py list staging
        python gateway_api_query.py list staging --keyword user
        python gateway_api_query.py list staging --applications "user-service,auth-service"
        python gateway_api_query.py detail online --url "/api/v1/user/login"
    """

    parser = argparse.ArgumentParser(
        description="Query Gateway API information",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # List all APIs in staging
  python gateway_api_query.py list staging

  # Search for user-related APIs
  python gateway_api_query.py list staging --keyword user

  # Filter by applications
  python gateway_api_query.py list staging --applications "user-service,auth-service"

  # Get API details
  python gateway_api_query.py detail online --url "/api/v1/user/login"
        """
    )

    subparsers = parser.add_subparsers(dest="operation", help="Operation to perform")

    # List command
    list_parser = subparsers.add_parser("list", help="List API information")
    list_parser.add_argument("env", choices=["staging", "online"], help="Environment type")
    list_parser.add_argument("--keyword", help="Fuzzy search keyword")
    list_parser.add_argument(
        "--applications",
        help="Comma-separated list of application names"
    )

    # Detail command
    detail_parser = subparsers.add_parser("detail", help="Get API details by URL")
    detail_parser.add_argument("env", choices=["staging", "online"], help="Environment type")
    detail_parser.add_argument("--url", required=True, help="API URL")

    args = parser.parse_args()

    if not args.operation:
        parser.print_help()
        sys.exit(1)

    try:
        if args.operation == "list":
            # Parse applications if provided
            applications = None
            if args.applications:
                applications = [app.strip() for app in args.applications.split(",")]

            # Query API list
            result = list_api_info(
                env=args.env,
                keyword=args.keyword,
                applications=applications
            )

            # Print result
            print(result)

        elif args.operation == "detail":
            # Query API details
            result = detail_by_url(
                env=args.env,
                url=args.url
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