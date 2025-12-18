#!/usr/bin/env python3
"""Fetch Lark/Feishu document and convert to Markdown using official SDK."""

import json
import os
import re
import sys

import lark_oapi as lark
from lark_oapi.api.docx.v1 import ListDocumentBlockRequest, ListDocumentBlockResponse
from dotenv import load_dotenv

# Load environment variables (override=True to ensure .env takes precedence)
load_dotenv(override=True)

LARK_APP_ID = os.getenv("LARK_APP_ID", "")
LARK_APP_SECRET = os.getenv("LARK_APP_SECRET", "")
LARK_DOC_URL = os.getenv("LARK_DOC_URL", "")
# Custom domain for enterprise Lark (e.g., "https://mi.feishu.cn")
LARK_DOMAIN = os.getenv("LARK_DOMAIN", "")


def extract_document_id(url: str) -> str | None:
    """Extract document ID from Lark document URL."""
    patterns = [
        r"/wiki/([a-zA-Z0-9]+)",
        r"/docx/([a-zA-Z0-9]+)",
        r"/docs/([a-zA-Z0-9]+)",
    ]
    for pattern in patterns:
        match = re.search(pattern, url)
        if match:
            return match.group(1)
    return None


def fetch_document_blocks(client: lark.Client, document_id: str) -> tuple[list[dict], str | None]:
    """Fetch all blocks from a Lark document using SDK."""
    all_blocks = []
    page_token = None

    while True:
        request_builder = ListDocumentBlockRequest.builder() \
            .document_id(document_id) \
            .page_size(500) \
            .document_revision_id(-1)

        if page_token:
            request_builder = request_builder.page_token(page_token)

        request: ListDocumentBlockRequest = request_builder.build()
        response: ListDocumentBlockResponse = client.docx.v1.document_block.list(request)

        if not response.success():
            error_msg = f"code: {response.code}, msg: {response.msg}"
            return [], error_msg

        items = response.data.items or []
        for item in items:
            # Convert SDK object to dict using JSON serialization
            item_json = lark.JSON.marshal(item)
            all_blocks.append(json.loads(item_json))

        page_token = response.data.page_token
        if not page_token or not response.data.has_more:
            break

    return all_blocks, None


def extract_text_from_elements(elements: list[dict]) -> str:
    """Extract text content from text elements."""
    if not elements:
        return ""
    text_parts = []
    for element in elements:
        if "text_run" in element:
            content = element["text_run"].get("content", "")
            text_parts.append(content)
        elif "mention_user" in element:
            text_parts.append("@user")
        elif "mention_doc" in element:
            title = element["mention_doc"].get("title", "document")
            text_parts.append(f"[{title}]")
        elif "equation" in element:
            content = element["equation"].get("content", "")
            text_parts.append(f"${content}$")
    return "".join(text_parts)


def block_to_markdown(block: dict, blocks_map: dict) -> str:
    """Convert a single block to Markdown."""
    block_type = block.get("block_type")

    # Block type mappings
    # 1: Page, 2: Text, 3-11: Heading 1-9, 12: Bullet, 13: Ordered
    # 14: Code, 15: Quote, 17: Todo, 22: Divider

    if block_type == 1:  # Page (document root)
        return ""

    elif block_type == 2:  # Text
        text_block = block.get("text", {})
        elements = text_block.get("elements", [])
        return extract_text_from_elements(elements)

    elif 3 <= block_type <= 11:  # Heading 1-9
        level = block_type - 2  # H1=3, H2=4, etc.
        heading_key = f"heading{level}"
        heading_block = block.get(heading_key, {})
        elements = heading_block.get("elements", [])
        text = extract_text_from_elements(elements)
        return f"{'#' * level} {text}"

    elif block_type == 12:  # Bullet list
        bullet_block = block.get("bullet", {})
        elements = bullet_block.get("elements", [])
        text = extract_text_from_elements(elements)
        return f"- {text}"

    elif block_type == 13:  # Ordered list
        ordered_block = block.get("ordered", {})
        elements = ordered_block.get("elements", [])
        text = extract_text_from_elements(elements)
        return f"1. {text}"

    elif block_type == 14:  # Code block
        code_block = block.get("code", {})
        elements = code_block.get("elements", [])
        text = extract_text_from_elements(elements)
        style = code_block.get("style", {})
        lang_map = {
            1: "", 7: "bash", 22: "go", 28: "json", 29: "java",
            30: "javascript", 49: "python", 56: "sql", 63: "typescript",
            66: "xml", 67: "yaml",
        }
        lang_code = style.get("language", 1)
        lang = lang_map.get(lang_code, "")
        return f"```{lang}\n{text}\n```"

    elif block_type == 15:  # Quote
        quote_block = block.get("quote", {})
        elements = quote_block.get("elements", [])
        text = extract_text_from_elements(elements)
        return f"> {text}"

    elif block_type == 17:  # Todo
        todo_block = block.get("todo", {})
        elements = todo_block.get("elements", [])
        text = extract_text_from_elements(elements)
        style = todo_block.get("style", {})
        done = style.get("done", False)
        checkbox = "[x]" if done else "[ ]"
        return f"- {checkbox} {text}"

    elif block_type == 22:  # Divider
        return "---"

    elif block_type == 31:  # Table
        return "[Table]"

    else:
        return ""


def blocks_to_markdown(blocks: list[dict]) -> str:
    """Convert all blocks to Markdown document."""
    blocks_map = {block.get("block_id"): block for block in blocks}

    children_map: dict[str, list[str]] = {}
    root_blocks: list[str] = []

    for block in blocks:
        block_id = block.get("block_id")
        parent_id = block.get("parent_id")

        if parent_id:
            if parent_id not in children_map:
                children_map[parent_id] = []
            if block_id not in children_map[parent_id]:
                children_map[parent_id].append(block_id)

        if block.get("block_type") == 1:  # Page block
            root_blocks = block.get("children", [])

    markdown_lines = []

    def process_block(block_id: str, indent_level: int = 0):
        if block_id not in blocks_map:
            return

        block = blocks_map[block_id]
        md = block_to_markdown(block, blocks_map)

        if md:
            if indent_level > 0 and block.get("block_type") in (12, 13, 17):
                md = "  " * indent_level + md
            markdown_lines.append(md)

        children = block.get("children", [])
        for child_id in children:
            next_indent = indent_level + 1 if block.get("block_type") in (12, 13, 17) else 0
            process_block(child_id, next_indent)

    for block_id in root_blocks:
        process_block(block_id)

    return "\n\n".join(markdown_lines)


def fetch_lark_document() -> dict:
    """Main function to fetch and convert Lark document."""
    if not LARK_APP_ID or not LARK_APP_SECRET:
        return {
            "success": False,
            "error": "LARK_APP_ID or LARK_APP_SECRET not configured",
        }

    if not LARK_DOC_URL:
        return {
            "success": False,
            "error": "LARK_DOC_URL not configured",
        }

    document_id = extract_document_id(LARK_DOC_URL)
    if not document_id:
        return {
            "success": False,
            "error": f"Cannot extract document ID from URL: {LARK_DOC_URL}",
        }

    # Create Lark client using SDK
    client_builder = lark.Client.builder() \
        .app_id(LARK_APP_ID) \
        .app_secret(LARK_APP_SECRET) \
        .log_level(lark.LogLevel.ERROR)

    # Set custom domain if configured (for enterprise Lark)
    if LARK_DOMAIN:
        client_builder = client_builder.domain(LARK_DOMAIN)

    client = client_builder.build()

    blocks, error = fetch_document_blocks(client, document_id)
    if error:
        return {
            "success": False,
            "error": f"Failed to fetch document blocks: {error}",
        }
    if not blocks:
        return {
            "success": False,
            "error": "Document is empty",
        }

    markdown_content = blocks_to_markdown(blocks)

    return {
        "success": True,
        "documentId": document_id,
        "url": LARK_DOC_URL,
        "content": markdown_content,
    }


def main():
    result = fetch_lark_document()
    print(json.dumps(result, ensure_ascii=False, indent=2))
    if not result.get("success"):
        sys.exit(1)


if __name__ == "__main__":
    main()
