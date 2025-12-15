#!/usr/bin/env python3
"""
JDK25 Migration - Spring Boot 3 Configuration Migrator

Migrates Spring Boot 2.x configurations to Spring Boot 3.x format:
- spring.factories → AutoConfiguration.imports
- application.properties updates
- Java configuration class fixes
"""

import argparse
import re
import sys
from pathlib import Path
from typing import List, Dict, Set
from rich.console import Console
from rich.panel import Panel
from rich.table import Table

console = Console()


class ConfigMigrator:
    """Spring Boot 3 configuration migrator"""

    def __init__(self, project_dir: Path, dry_run: bool = False):
        self.project_dir = project_dir
        self.dry_run = dry_run
        self.changes: List[str] = []
        self.warnings: List[str] = []

    def migrate(self) -> bool:
        """Execute migration"""
        try:
            console.print(Panel("[bold cyan]Spring Boot 3 Configuration Migration[/bold cyan]"))

            # 1. Migrate spring.factories
            self._migrate_spring_factories()

            # 2. Update application.properties
            self._update_application_properties()

            # 3. Fix @Bean void methods
            self._fix_bean_void_methods()

            # 4. Fix @Value on private setters
            self._fix_value_setters()

            # Display summary
            self._display_summary()

            return True

        except Exception as e:
            console.print(f"[red]Error during migration: {e}[/red]")
            return False

    def _migrate_spring_factories(self):
        """Migrate META-INF/spring.factories to AutoConfiguration.imports"""
        # Find all spring.factories files
        factories_files = list(self.project_dir.rglob("**/META-INF/spring.factories"))

        for factories_file in factories_files:
            try:
                content = factories_file.read_text(encoding='utf-8')

                # Extract auto-configuration classes
                auto_config_pattern = r'org\.springframework\.boot\.autoconfigure\.EnableAutoConfiguration\s*=\s*([^\n]+(?:\\\s*\n\s*[^\n]+)*)'
                matches = re.search(auto_config_pattern, content)

                if matches:
                    # Parse class names
                    class_names_str = matches.group(1)
                    # Remove line continuations and split by comma
                    class_names_str = re.sub(r'\\\s*\n\s*', '', class_names_str)
                    class_names = [name.strip() for name in class_names_str.split(',') if name.strip()]

                    if class_names:
                        # Create AutoConfiguration.imports file
                        imports_dir = factories_file.parent.parent / "spring"
                        imports_file = imports_dir / "org.springframework.boot.autoconfigure.AutoConfiguration.imports"

                        if not self.dry_run:
                            imports_dir.mkdir(parents=True, exist_ok=True)
                            imports_file.write_text('\n'.join(class_names) + '\n', encoding='utf-8')

                        self.changes.append(f"Created {imports_file.relative_to(self.project_dir)}")
                        self.changes.append(f"Migrated {len(class_names)} auto-configuration classes")

                        # Optionally remove or comment out spring.factories
                        self.warnings.append(
                            f"Review and potentially remove {factories_file.relative_to(self.project_dir)} after verification"
                        )

            except Exception as e:
                self.warnings.append(f"Error processing {factories_file}: {e}")

    def _update_application_properties(self):
        """Update application.properties for Spring Boot 3"""
        props_files = list(self.project_dir.rglob("**/application.properties"))
        props_files.extend(self.project_dir.rglob("**/application.yml"))
        props_files.extend(self.project_dir.rglob("**/application.yaml"))

        required_props = {
            "spring.main.allow-circular-references": "true",
        }

        for props_file in props_files:
            try:
                content = props_file.read_text(encoding='utf-8')
                original_content = content
                file_changes = []

                # Check and add missing properties
                for prop, value in required_props.items():
                    if prop not in content:
                        if props_file.suffix == '.properties':
                            content += f"\n# Spring Boot 3 configuration\n{prop}={value}\n"
                        else:  # YAML
                            content += f"\n# Spring Boot 3 configuration\nspring:\n  main:\n    allow-circular-references: {value}\n"

                        file_changes.append(f"Added {prop}={value}")

                # Write changes
                if content != original_content and not self.dry_run:
                    props_file.write_text(content, encoding='utf-8')

                if file_changes:
                    self.changes.extend([f"{props_file.name}: {change}" for change in file_changes])

            except Exception as e:
                self.warnings.append(f"Error processing {props_file}: {e}")

    def _fix_bean_void_methods(self):
        """Find and report @Bean methods with void return type"""
        java_files = list(self.project_dir.rglob("**/*.java"))

        # Pattern to match @Bean methods with void return
        bean_void_pattern = r'@Bean\s+(?:public\s+)?void\s+(\w+)\s*\('

        for java_file in java_files:
            try:
                content = java_file.read_text(encoding='utf-8')
                matches = re.finditer(bean_void_pattern, content)

                for match in matches:
                    method_name = match.group(1)
                    line_num = content[:match.start()].count('\n') + 1

                    self.warnings.append(
                        f"{java_file.relative_to(self.project_dir)}:{line_num} - "
                        f"@Bean method '{method_name}' has void return type (must return bean instance)"
                    )

            except Exception as e:
                self.warnings.append(f"Error scanning {java_file}: {e}")

    def _fix_value_setters(self):
        """Find @Value annotations on private setter methods"""
        java_files = list(self.project_dir.rglob("**/*.java"))

        # Pattern to match @Value on private setters
        private_setter_pattern = r'@Value\([^)]+\)\s+private\s+void\s+(set\w+)\s*\('

        for java_file in java_files:
            try:
                content = java_file.read_text(encoding='utf-8')
                matches = re.finditer(private_setter_pattern, content)

                for match in matches:
                    method_name = match.group(1)
                    line_num = content[:match.start()].count('\n') + 1

                    self.warnings.append(
                        f"{java_file.relative_to(self.project_dir)}:{line_num} - "
                        f"@Value on private setter '{method_name}' (change to public)"
                    )

                    # Automatically fix if not dry-run
                    if not self.dry_run:
                        content = content.replace(
                            match.group(0),
                            match.group(0).replace('private void', 'public void')
                        )

                if not self.dry_run:
                    java_file.write_text(content, encoding='utf-8')

            except Exception as e:
                self.warnings.append(f"Error processing {java_file}: {e}")

    def _display_summary(self):
        """Display migration summary"""
        console.print("\n[bold]Migration Summary[/bold]")

        if self.changes:
            console.print("\n[green]Changes Applied:[/green]")
            for change in self.changes:
                console.print(f"  [green]✓[/green] {change}")

        if self.warnings:
            console.print("\n[yellow]Warnings (Manual Review Required):[/yellow]")
            for warning in self.warnings:
                console.print(f"  [yellow]⚠[/yellow] {warning}")

        if not self.changes and not self.warnings:
            console.print("[green]✓[/green] No configuration changes needed")

        if self.dry_run:
            console.print("\n[yellow]⚠ Dry run mode - no files were modified[/yellow]")

        # Show additional guidance
        self._show_guidance()

    def _show_guidance(self):
        """Show post-migration guidance"""
        console.print("\n[bold cyan]Post-Migration Steps:[/bold cyan]")
        console.print("""
1. Review and test auto-configuration changes
2. Fix any @Bean methods with void return type
3. Verify circular dependency configurations
4. Run unit tests to identify issues
5. Check for third-party libraries still using spring.factories

[bold yellow]Common Issues to Check:[/bold yellow]
- RocketMQ spring-boot-starter may need manual @ImportAutoConfiguration
- Custom auto-configuration classes might need manual registration
- Some third-party libraries may not be Spring Boot 3 compatible
        """)


def main():
    parser = argparse.ArgumentParser(
        description="Migrate Spring Boot 2.x configurations to Spring Boot 3.x"
    )
    parser.add_argument(
        "--project-dir",
        type=Path,
        default=Path("."),
        help="Project directory to migrate (default: current directory)"
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Preview changes without modifying files"
    )

    args = parser.parse_args()

    migrator = ConfigMigrator(args.project_dir, dry_run=args.dry_run)
    success = migrator.migrate()

    sys.exit(0 if success else 1)


if __name__ == "__main__":
    main()
