#!/usr/bin/env python3
"""
JDK25 Migration - Namespace Migration (javax → jakarta)

Automatically converts javax.* imports to jakarta.* in Java source files.
"""

import argparse
import re
import sys
from pathlib import Path
from typing import Dict, List, Tuple
from rich.console import Console
from rich.progress import Progress, SpinnerColumn, TextColumn
from rich.table import Table
from rich.panel import Panel

console = Console()

# Namespace mappings based on Jakarta EE 9+
NAMESPACE_MAPPINGS = {
    "javax.validation": "jakarta.validation",
    "javax.annotation": "jakarta.annotation",
    "javax.persistence": "jakarta.persistence",
    "javax.transaction": "jakarta.transaction",
    "javax.servlet": "jakarta.servlet",
    "javax.servlet.http": "jakarta.servlet.http",
    "javax.websocket": "jakarta.websocket",
    "javax.json": "jakarta.json",
    "javax.json.bind": "jakarta.json.bind",
    "javax.ws.rs": "jakarta.ws.rs",
    "javax.xml.bind": "jakarta.xml.bind",
    "javax.inject": "jakarta.inject",
    "javax.ejb": "jakarta.ejb",
    "javax.enterprise": "jakarta.enterprise",
    "javax.interceptor": "jakarta.interceptor",
    "javax.jms": "jakarta.jms",
    "javax.mail": "jakarta.mail",
}


class NamespaceMigrator:
    """Migrates javax.* namespaces to jakarta.*"""

    def __init__(self, source_dir: Path, dry_run: bool = False):
        self.source_dir = source_dir
        self.dry_run = dry_run
        self.changes: Dict[str, List[Tuple[str, str]]] = {}
        self.processed_files = 0
        self.modified_files = 0

    def migrate(self) -> bool:
        """Execute the migration process"""
        try:
            if not self.source_dir.exists():
                console.print(f"[red]Error: {self.source_dir} not found[/red]")
                return False

            console.print(Panel(f"[bold cyan]Migrating namespaces in {self.source_dir}[/bold cyan]"))

            # Find all Java files
            java_files = list(self.source_dir.rglob("*.java"))

            if not java_files:
                console.print("[yellow]No Java files found[/yellow]")
                return True

            # Process files with progress bar
            with Progress(
                SpinnerColumn(),
                TextColumn("[progress.description]{task.description}"),
                console=console
            ) as progress:
                task = progress.add_task(f"Processing {len(java_files)} files...", total=len(java_files))

                for java_file in java_files:
                    self._process_file(java_file)
                    progress.advance(task)

            # Display summary
            self._display_summary()

            return True

        except Exception as e:
            console.print(f"[red]Error during migration: {e}[/red]")
            return False

    def _process_file(self, file_path: Path):
        """Process a single Java file"""
        try:
            self.processed_files += 1

            # Read file content
            content = file_path.read_text(encoding='utf-8')
            original_content = content
            file_changes = []

            # Apply namespace replacements
            for old_ns, new_ns in NAMESPACE_MAPPINGS.items():
                # Match import statements
                import_pattern = rf'import\s+{re.escape(old_ns)}(\.[a-zA-Z0-9_.*]+)?;'
                matches = re.findall(import_pattern, content)

                if matches:
                    for match in matches:
                        old_import = f"{old_ns}{match}"
                        new_import = f"{new_ns}{match}"
                        file_changes.append((old_import, new_import))

                    # Replace in content
                    content = re.sub(
                        import_pattern,
                        lambda m: f'import {new_ns}{m.group(1) if m.group(1) else ""};',
                        content
                    )

                # Also replace fully qualified class names in code (not in imports)
                # Be careful not to replace in comments or strings
                fqcn_pattern = rf'\b{re.escape(old_ns)}(\.[A-Z][a-zA-Z0-9_]*)\b'
                if re.search(fqcn_pattern, content):
                    content = re.sub(fqcn_pattern, f'{new_ns}\\1', content)

            # Write changes if any
            if content != original_content:
                if not self.dry_run:
                    # Backup original
                    backup_path = file_path.with_suffix('.java.bak')
                    file_path.write_text(content, encoding='utf-8')

                self.changes[str(file_path.relative_to(self.source_dir))] = file_changes
                self.modified_files += 1

        except Exception as e:
            console.print(f"[red]Error processing {file_path}: {e}[/red]")

    def _display_summary(self):
        """Display migration summary"""
        console.print(f"\n[bold]Migration Summary[/bold]")
        console.print(f"Processed files: {self.processed_files}")
        console.print(f"Modified files: {self.modified_files}")

        if not self.changes:
            console.print("[green]✓[/green] No namespace migrations needed")
            return

        # Display detailed changes
        table = Table(title="Namespace Changes")
        table.add_column("File", style="cyan", no_wrap=False)
        table.add_column("Old Import", style="red")
        table.add_column("New Import", style="green")

        for file_path, changes in list(self.changes.items())[:20]:  # Show first 20
            for old_import, new_import in changes:
                table.add_row(file_path, old_import, new_import)

        console.print(table)

        if len(self.changes) > 20:
            console.print(f"\n[yellow]... and {len(self.changes) - 20} more files[/yellow]")

        if self.dry_run:
            console.print("\n[yellow]⚠ Dry run mode - no files were modified[/yellow]")
        else:
            console.print("\n[green]✓[/green] Namespace migration completed")

        # Show required dependency updates
        self._show_dependency_requirements()

    def _show_dependency_requirements(self):
        """Display required dependency updates"""
        console.print("\n[bold yellow]Required POM.xml Updates:[/bold yellow]")
        console.print("""
Add the following dependencies to your pom.xml:

[cyan]<dependency>
    <groupId>jakarta.annotation</groupId>
    <artifactId>jakarta.annotation-api</artifactId>
    <version>2.1.1</version>
</dependency>

<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
    <version>3.0.2</version>
</dependency>

<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>8.0.1.Final</version>
</dependency>[/cyan]
        """)


def main():
    parser = argparse.ArgumentParser(
        description="Migrate javax.* imports to jakarta.* in Java source files"
    )
    parser.add_argument(
        "--source-dir",
        type=Path,
        default=Path("src/main/java"),
        help="Source directory to scan (default: src/main/java)"
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Preview changes without modifying files"
    )

    args = parser.parse_args()

    migrator = NamespaceMigrator(args.source_dir, dry_run=args.dry_run)
    success = migrator.migrate()

    sys.exit(0 if success else 1)


if __name__ == "__main__":
    main()
