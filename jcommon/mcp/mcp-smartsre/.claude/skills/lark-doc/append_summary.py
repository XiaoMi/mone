#!/usr/bin/env python3
"""Append troubleshooting summary to Lark/Feishu document using official SDK."""

import argparse
import json
import os
import re
import sys
from datetime import datetime

import lark_oapi as lark
from lark_oapi.api.docx.v1 import (
    Block,
    CreateDocumentBlockChildrenRequest,
    CreateDocumentBlockChildrenRequestBody,
    CreateDocumentBlockChildrenResponse,
    Divider,
    Text,
    TextElement,
    TextElementStyle,
    TextRun,
    TextStyle,
)
from dotenv import load_dotenv

# Load environment variables
load_dotenv(override=True)

LARK_APP_ID = os.getenv("LARK_APP_ID", "")
LARK_APP_SECRET = os.getenv("LARK_APP_SECRET", "")
LARK_DOC_URL = os.getenv("LARK_DOC_URL", "")
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


def create_text_block(content: str, bold: bool = False) -> Block:
    """Create a text block with the given content."""
    style = TextElementStyle.builder()
    if bold:
        style = style.bold(True)

    return Block.builder() \
        .block_type(2) \
        .text(Text.builder()
            .style(TextStyle.builder().build())
            .elements([
                TextElement.builder()
                    .text_run(TextRun.builder()
                        .content(content)
                        .text_element_style(style.build())
                        .build())
                    .build()
            ])
            .build()) \
        .build()


def create_heading_block(content: str, level: int = 3) -> Block:
    """Create a heading block (level 3-5 = block_type 5-7)."""
    # block_type: 3=H1, 4=H2, 5=H3, 6=H4, 7=H5
    block_type = 2 + level
    heading_key = f"heading{level}"

    block = Block.builder() \
        .block_type(block_type)

    text_content = Text.builder() \
        .style(TextStyle.builder().build()) \
        .elements([
            TextElement.builder()
                .text_run(TextRun.builder()
                    .content(content)
                    .text_element_style(TextElementStyle.builder().build())
                    .build())
                .build()
        ]) \
        .build()

    # Set the appropriate heading attribute based on level
    if level == 1:
        block = block.heading1(text_content)
    elif level == 2:
        block = block.heading2(text_content)
    elif level == 3:
        block = block.heading3(text_content)
    elif level == 4:
        block = block.heading4(text_content)
    elif level == 5:
        block = block.heading5(text_content)
    else:
        block = block.heading3(text_content)  # default to H3

    return block.build()


def create_bullet_block(content: str) -> Block:
    """Create a bullet list item block."""
    return Block.builder() \
        .block_type(12) \
        .bullet(Text.builder()
            .style(TextStyle.builder().build())
            .elements([
                TextElement.builder()
                    .text_run(TextRun.builder()
                        .content(content)
                        .text_element_style(TextElementStyle.builder().build())
                        .build())
                    .build()
            ])
            .build()) \
        .build()


def create_divider_block() -> Block:
    """Create a divider block. Divider requires an empty structure {}."""
    return Block.builder() \
        .block_type(22) \
        .divider(Divider.builder().build()) \
        .build()


def append_troubleshooting_summary(
    title: str,
    problem: str,
    steps: list[str],
    solution: str,
    notes: str | None = None,
) -> dict:
    """
    Append a troubleshooting summary to the Lark document.

    Args:
        title: Title of the troubleshooting case
        problem: Description of the problem
        steps: List of troubleshooting steps taken
        solution: The solution that resolved the issue
        notes: Optional additional notes

    Returns:
        dict with success status and message
    """
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

    # Create Lark client
    client_builder = lark.Client.builder() \
        .app_id(LARK_APP_ID) \
        .app_secret(LARK_APP_SECRET) \
        .log_level(lark.LogLevel.ERROR)

    if LARK_DOMAIN:
        client_builder = client_builder.domain(LARK_DOMAIN)

    client = client_builder.build()

    # Build blocks for the summary
    blocks = []

    # Add divider first
    blocks.append(create_divider_block())

    # Add title with timestamp
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M")
    blocks.append(create_heading_block(f"{title} ({timestamp})", level=3))

    # Add problem description
    blocks.append(create_heading_block("问题描述", level=4))
    blocks.append(create_text_block(problem))

    # Add troubleshooting steps
    blocks.append(create_heading_block("排查步骤", level=4))
    for step in steps:
        blocks.append(create_bullet_block(step))

    # Add solution
    blocks.append(create_heading_block("解决方案", level=4))
    blocks.append(create_text_block(solution))

    # Add notes if provided
    if notes:
        blocks.append(create_heading_block("备注", level=4))
        blocks.append(create_text_block(notes))

    # Create request to append blocks to document
    # Using document_id as block_id appends to the root of the document
    request = CreateDocumentBlockChildrenRequest.builder() \
        .document_id(document_id) \
        .block_id(document_id) \
        .document_revision_id(-1) \
        .request_body(CreateDocumentBlockChildrenRequestBody.builder()
            .children(blocks)
            .index(-1)  # Append at the end
            .build()) \
        .build()

    response: CreateDocumentBlockChildrenResponse = client.docx.v1.document_block_children.create(request)

    if not response.success():
        return {
            "success": False,
            "error": f"Failed to append summary: code={response.code}, msg={response.msg}",
        }

    return {
        "success": True,
        "documentId": document_id,
        "url": LARK_DOC_URL,
        "message": f"Successfully appended troubleshooting summary: {title}",
    }


def main():
    parser = argparse.ArgumentParser(
        description="Append troubleshooting summary to Lark document"
    )
    parser.add_argument(
        "-t", "--title",
        required=True,
        help="Title of the troubleshooting case"
    )
    parser.add_argument(
        "-p", "--problem",
        required=True,
        help="Description of the problem"
    )
    parser.add_argument(
        "-s", "--steps",
        required=True,
        help="JSON array of troubleshooting steps"
    )
    parser.add_argument(
        "-o", "--solution",
        required=True,
        help="The solution that resolved the issue"
    )
    parser.add_argument(
        "-n", "--notes",
        help="Optional additional notes"
    )

    args = parser.parse_args()

    try:
        steps = json.loads(args.steps)
        if not isinstance(steps, list):
            raise ValueError("Steps must be a JSON array")
    except json.JSONDecodeError as e:
        print(json.dumps({
            "success": False,
            "error": f"Invalid JSON for steps: {e}"
        }, ensure_ascii=False, indent=2))
        sys.exit(1)

    result = append_troubleshooting_summary(
        title=args.title,
        problem=args.problem,
        steps=steps,
        solution=args.solution,
        notes=args.notes,
    )

    print(json.dumps(result, ensure_ascii=False, indent=2))
    if not result.get("success"):
        sys.exit(1)


if __name__ == "__main__":
    main()
