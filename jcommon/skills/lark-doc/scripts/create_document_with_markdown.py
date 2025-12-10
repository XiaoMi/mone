#!/usr/bin/env python3
"""
Create a Lark/Feishu document with markdown content.
This script combines document creation, permission setting, and markdown insertion.

Usage:
    python create_document_with_markdown.py --title "Document Title" [--markdown "content" | --file "path.md"]

Environment variables required:
    YOUR_APP_ID: Lark app ID
    YOUR_APP_SECRET: Lark app secret
"""

from dotenv import load_dotenv
load_dotenv()

import sys
import os
import json
import argparse
import lark_oapi as lark
from lark_oapi.api.docx.v1 import *
from lark_oapi.api.drive.v2 import *
from typing import List, Tuple, Optional


def parse_arguments():
    """Parse command line arguments."""
    parser = argparse.ArgumentParser(
        description='Create a Lark document with markdown content',
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
    # Create document from markdown file
    python create_document_with_markdown.py --title "My Document" --file document.md

    # Create document from inline markdown
    python create_document_with_markdown.py --title "My Document" --markdown "# Title\\n\\nContent here"

    # Create document with custom permissions
    python create_document_with_markdown.py --title "My Document" --file document.md --link-share anyone_readable
        """
    )

    # Required arguments
    parser.add_argument(
        '--title',
        type=str,
        required=True,
        help='Document title (required)'
    )

    # Markdown content (one of these is required)
    content_group = parser.add_mutually_exclusive_group(required=True)
    content_group.add_argument(
        '--markdown',
        type=str,
        help='Markdown text to convert and insert'
    )
    content_group.add_argument(
        '--file',
        type=str,
        help='Path to markdown file to convert and insert'
    )

    # Optional folder location
    parser.add_argument(
        '--folder',
        type=str,
        help='Folder token where the document will be created (optional)'
    )

    # Permission settings (all optional with sensible defaults)
    parser.add_argument(
        '--external-access',
        type=str,
        default='closed',
        choices=['open', 'closed', 'allow_share_partner_tenant'],
        help='External access permission (default: closed)'
    )
    parser.add_argument(
        '--link-share',
        type=str,
        default='tenant_editable',
        choices=['tenant_readable', 'tenant_editable', 'partner_tenant_readable', 'partner_tenant_editable',
                 'anyone_readable', 'anyone_editable', 'closed'],
        help='Link sharing permission (default: tenant_editable)'
    )
    parser.add_argument(
        '--security',
        type=str,
        default='anyone_can_view',
        choices=['anyone_can_view', 'anyone_can_edit', 'only_full_access'],
        help='Who can create copy, print, download (default: anyone_can_view)'
    )
    parser.add_argument(
        '--comment',
        type=str,
        default='anyone_can_view',
        choices=['anyone_can_view', 'anyone_can_edit'],
        help='Who can comment (default: anyone_can_view)'
    )

    # Debug and logging
    parser.add_argument(
        '--debug',
        action='store_true',
        help='Show debug information'
    )
    parser.add_argument(
        '--log-level',
        type=str,
        choices=['DEBUG', 'INFO', 'WARNING', 'ERROR'],
        default='INFO',
        help='Log level (default: INFO)'
    )

    return parser.parse_args()


def get_log_level(level_str):
    """Convert string log level to lark LogLevel."""
    levels = {
        'DEBUG': lark.LogLevel.DEBUG,
        'INFO': lark.LogLevel.INFO,
        'WARNING': lark.LogLevel.WARNING,
        'ERROR': lark.LogLevel.ERROR
    }
    return levels.get(level_str, lark.LogLevel.INFO)


def read_markdown_content(args):
    """Read markdown content from arguments."""
    if args.file:
        if not os.path.exists(args.file):
            print(f"Error: File '{args.file}' not found.")
            sys.exit(1)

        with open(args.file, 'r', encoding='utf-8') as f:
            return f.read()
    else:
        return args.markdown


def create_document(client, title, folder_token=None):
    """Create a new Lark document."""
    print(f"\n📄 Creating document: {title}")

    # Build request
    request_body_builder = CreateDocumentRequestBody.builder()
    request_body_builder.title(title)

    if folder_token:
        request_body_builder.folder_token(folder_token)

    request = CreateDocumentRequest.builder() \
        .request_body(request_body_builder.build()) \
        .build()

    # Send request
    response = client.docx.v1.document.create(request)

    # Handle response
    if not response.success():
        print(f"❌ Document creation failed: {response.msg}")
        if response.raw and response.raw.content:
            error_details = json.loads(response.raw.content)
            print(f"Error details:\n{json.dumps(error_details, indent=2, ensure_ascii=False)}")
        return None, None

    # Extract document info
    if response.data and response.data.document:
        document_id = response.data.document.document_id
        revision_id = response.data.document.revision_id
        print(f"✅ Document created: {document_id}")
        return document_id, revision_id

    return None, None


def set_document_permissions(client, document_id, args):
    """Set permissions for the document."""
    print(f"\n🔒 Setting document permissions...")

    # Build request
    request = PatchPermissionPublicRequest.builder() \
        .token(document_id) \
        .type('docx') \
        .request_body(PermissionPublic.builder()
            .external_access_entity(args.external_access)
            .security_entity(args.security)
            .comment_entity(args.comment)
            .share_entity('anyone')  # Default
            .manage_collaborator_entity('collaborator_can_view')  # Default
            .link_share_entity(args.link_share)
            .copy_entity('anyone_can_view')  # Default
            .build()) \
        .build()

    # Send request
    response = client.drive.v2.permission_public.patch(request)

    # Handle response
    if not response.success():
        print(f"⚠️ Failed to set permissions: {response.msg}")
        # Continue anyway - document is created
        return False

    print(f"✅ Permissions set: link_share={args.link_share}, external_access={args.external_access}")
    return True


def convert_markdown_to_blocks(client, markdown_content, debug=False):
    """Convert markdown content to document blocks."""
    print(f"\n📝 Converting markdown ({len(markdown_content)} characters)...")

    # Build request
    request = ConvertDocumentRequest.builder() \
        .request_body(ConvertDocumentRequestBody.builder()
            .content_type("markdown")
            .content(markdown_content)
            .build()) \
        .build()

    # Send request
    response = client.docx.v1.document.convert(request)

    # Handle response
    if not response.success():
        print(f"❌ Markdown conversion failed: {response.msg}")
        return None, None

    print("✅ Markdown converted successfully")

    # Extract blocks and ordering
    blocks = None
    first_level_block_ids = None

    if response.data:
        if hasattr(response.data, 'blocks'):
            blocks = response.data.blocks
        if hasattr(response.data, 'first_level_block_ids'):
            first_level_block_ids = response.data.first_level_block_ids

        if debug and blocks:
            print(f"  - Total blocks: {len(blocks)}")
            if first_level_block_ids:
                print(f"  - Top-level blocks: {len(first_level_block_ids)}")

    return blocks, first_level_block_ids


def clean_blocks_for_insertion(blocks, debug=False):
    """
    Clean blocks before insertion by removing read-only fields like merge_info.
    This function processes all blocks and removes merge_info from table properties.
    """
    if not blocks:
        return blocks

    cleaned_blocks = []
    merge_info_removed_count = 0

    for original_block in blocks:
        # Check if block has a 'table' attribute
        if hasattr(original_block, 'table') and original_block.table:
            # Access the table object
            table_obj = original_block.table

            # Check if table has a property attribute with merge_info
            if hasattr(table_obj, 'property') and table_obj.property:
                property_obj = table_obj.property

                # Remove merge_info from the property if it exists
                if hasattr(property_obj, 'merge_info'):
                    delattr(property_obj, 'merge_info')
                    merge_info_removed_count += 1

        cleaned_blocks.append(original_block)

    if debug and merge_info_removed_count > 0:
        print(f"  - Removed merge_info from {merge_info_removed_count} table blocks")

    return cleaned_blocks


def insert_blocks_into_document(client, document_id, blocks, first_level_block_ids=None, debug=False):
    """Insert blocks into the document."""
    if not blocks:
        print("No blocks to insert.")
        return False

    print(f"\n📥 Inserting blocks into document...")

    # Clean blocks to remove read-only fields like merge_info
    blocks = clean_blocks_for_insertion(blocks, debug)

    # Prepare block IDs for insertion
    if first_level_block_ids:
        children_ids = first_level_block_ids
    else:
        # Fallback: use all block IDs
        children_ids = [block.block_id for block in blocks if hasattr(block, 'block_id')]

    if debug:
        print(f"  - Inserting {len(children_ids)} top-level blocks")

    # Build request
    request = CreateDocumentBlockDescendantRequest.builder() \
        .document_id(document_id) \
        .block_id(document_id) \
        .document_revision_id(-1) \
        .request_body(CreateDocumentBlockDescendantRequestBody.builder()
            .children_id(children_ids)
            .index(0)  # Insert at the beginning
            .descendants(blocks)
            .build()) \
        .build()

    # Send request
    response = client.docx.v1.document_block_descendant.create(request)

    # Handle response
    if not response.success():
        print(f"❌ Block insertion failed: {response.msg}")
        if response.raw and response.raw.content:
            error_details = json.loads(response.raw.content)
            print(f"Error details:\n{json.dumps(error_details, indent=2, ensure_ascii=False)}")
        return False

    print("✅ Content inserted successfully")
    return True


def main():
    # Parse arguments
    args = parse_arguments()

    # Get credentials
    app_id = os.environ.get("YOUR_APP_ID")
    app_secret = os.environ.get("YOUR_APP_SECRET")

    if not app_id or not app_secret:
        print("Error: YOUR_APP_ID and YOUR_APP_SECRET must be set in environment variables.")
        print("Please create a .env file with these values or set them as environment variables.")
        sys.exit(1)

    # Read markdown content
    markdown_content = read_markdown_content(args)

    # Create client
    client = lark.Client.builder() \
        .app_id(app_id) \
        .app_secret(app_secret) \
        .log_level(get_log_level(args.log_level)) \
        .build()

    print("=" * 60)
    print(f"Creating document: {args.title}")
    print("=" * 60)

    # Step 1: Create document
    document_id, revision_id = create_document(client, args.title, args.folder)
    if not document_id:
        print("\n❌ Failed to create document")
        sys.exit(1)

    # Step 2: Set permissions
    set_document_permissions(client, document_id, args)

    # Step 3: Convert markdown to blocks
    blocks, first_level_block_ids = convert_markdown_to_blocks(client, markdown_content, args.debug)
    if not blocks:
        print("\n⚠️ No content to insert, document created but empty")
        print(f"\n📎 Document URL: https://mi.feishu.cn/docx/{document_id}")
        return

    # Step 4: Insert blocks into document
    success = insert_blocks_into_document(client, document_id, blocks, first_level_block_ids, args.debug)

    # Final result
    print("\n" + "=" * 60)
    if success:
        print("🎉 Document created successfully!")
    else:
        print("⚠️ Document created but content insertion had issues")

    print(f"\n📎 Document URL: https://mi.feishu.cn/docx/{document_id}")
    print("=" * 60)


if __name__ == "__main__":
    main()