#!/usr/bin/env python3
"""
JDK25 Migration - Maven Dependency Upgrader

Automatically upgrades Maven dependencies in pom.xml to JDK25-compatible versions.
"""

import argparse
import re
import sys
from pathlib import Path
from typing import Dict, List, Tuple
import xml.etree.ElementTree as ET
from rich.console import Console
from rich.table import Table
from rich.panel import Panel

console = Console()

# Dependency version mappings based on migration guide
DEPENDENCY_UPGRADES = {
    "spring-boot-starter-parent": "3.5.5",
    "spring-boot-dependencies": "3.5.5",
    "spring-boot": "3.5.5",
    "spring-framework-bom": "6.2.11",
    "spring-core": "6.2.11",
    "spring-context": "6.2.11",
    "spring-web": "6.2.11",
    "spring-webmvc": "6.2.11",
    "spring-webflux": "6.2.11",
    "dubbo": "3.3.4-mone-v2-SNAPSHOT",
    "dubbo-spring-boot-starter": "3.3.4",
    "dubbo-reactive": "3.3.4",
    "lombok": "1.18.40",
    "nacos-client": "2.1.2-XIAOMI",
    "commons-pool2": "2.12.0",
    "mapstruct": "1.5.3.Final",
    "mapstruct-processor": "1.5.3.Final",
    "lombok-mapstruct-binding": "0.2.0",
    "jakarta.annotation-api": "2.1.1",
    "jakarta.validation-api": "3.0.2",
    "hibernate-validator": "8.0.1.Final",
    "snakeyaml": "1.26",
    "jedis": "3.8.0",
    "simpleclient": "0.11.0",
}

# Property name mappings
PROPERTY_UPGRADES = {
    "jdk.version": "25",
    "java.version": "25",
    "maven.compiler.source": "25",
    "maven.compiler.target": "25",
    "lombok.version": "1.18.40",
    "mapstruct.version": "1.5.3.Final",
    "lombok.mapstruct.binding.version": "0.2.0",
    "maven-compiler-plugin.version": "3.11.0",
    "spring-boot.version": "3.5.5",
    "spring.version": "6.2.11",
    "dubbo.version": "3.3.4",
}


class PomUpgrader:
    """Maven POM.xml dependency upgrader"""

    def __init__(self, pom_path: Path, dry_run: bool = False):
        self.pom_path = pom_path
        self.dry_run = dry_run
        self.changes: List[Tuple[str, str, str]] = []
        self.namespace = {"maven": "http://maven.apache.org/POM/4.0.0"}

    def upgrade(self) -> bool:
        """Execute the upgrade process"""
        try:
            if not self.pom_path.exists():
                console.print(f"[red]Error: {self.pom_path} not found[/red]")
                return False

            console.print(Panel(f"[bold cyan]Upgrading {self.pom_path}[/bold cyan]"))

            # Parse XML
            tree = ET.parse(self.pom_path)
            root = tree.getroot()

            # Backup original
            if not self.dry_run:
                backup_path = self.pom_path.with_suffix('.xml.bak')
                self.pom_path.rename(backup_path)
                console.print(f"[green]✓[/green] Backup created: {backup_path}")

            # Upgrade properties
            self._upgrade_properties(root)

            # Upgrade dependencies
            self._upgrade_dependencies(root)

            # Update compiler plugin
            self._upgrade_compiler_plugin(root)

            # Write changes
            if not self.dry_run:
                tree.write(self.pom_path, encoding='utf-8', xml_declaration=True)
                console.print(f"[green]✓[/green] POM file updated")

            # Display summary
            self._display_summary()

            return True

        except Exception as e:
            console.print(f"[red]Error upgrading POM: {e}[/red]")
            return False

    def _upgrade_properties(self, root: ET.Element):
        """Upgrade version properties"""
        properties = root.find(".//maven:properties", self.namespace)
        if properties is None:
            properties = root.find(".//properties")

        if properties is not None:
            for prop_name, new_version in PROPERTY_UPGRADES.items():
                prop = properties.find(f".//{prop_name}")
                if prop is not None and prop.text != new_version:
                    old_version = prop.text
                    prop.text = new_version
                    self.changes.append(("property", prop_name, f"{old_version} → {new_version}"))

    def _upgrade_dependencies(self, root: ET.Element):
        """Upgrade dependency versions"""
        # Find all dependencies
        for dep in root.findall(".//maven:dependency", self.namespace):
            self._upgrade_dependency(dep)

        # Also check without namespace (for simpler pom files)
        for dep in root.findall(".//dependency"):
            self._upgrade_dependency(dep)

    def _upgrade_dependency(self, dep: ET.Element):
        """Upgrade a single dependency"""
        artifact_id = dep.find(".//maven:artifactId", self.namespace)
        if artifact_id is None:
            artifact_id = dep.find(".//artifactId")

        version = dep.find(".//maven:version", self.namespace)
        if version is None:
            version = dep.find(".//version")

        if artifact_id is not None and version is not None:
            artifact_name = artifact_id.text
            if artifact_name in DEPENDENCY_UPGRADES:
                old_version = version.text
                new_version = DEPENDENCY_UPGRADES[artifact_name]

                # Handle property references like ${lombok.version}
                if old_version and not old_version.startswith("${"):
                    if old_version != new_version:
                        version.text = new_version
                        self.changes.append(("dependency", artifact_name, f"{old_version} → {new_version}"))

    def _upgrade_compiler_plugin(self, root: ET.Element):
        """Upgrade Maven compiler plugin configuration"""
        plugins = root.findall(".//maven:plugin", self.namespace)
        if not plugins:
            plugins = root.findall(".//plugin")

        for plugin in plugins:
            artifact_id = plugin.find(".//maven:artifactId", self.namespace)
            if artifact_id is None:
                artifact_id = plugin.find(".//artifactId")

            if artifact_id is not None and artifact_id.text == "maven-compiler-plugin":
                config = plugin.find(".//maven:configuration", self.namespace)
                if config is None:
                    config = plugin.find(".//configuration")

                if config is not None:
                    self._update_compiler_config(config)

    def _update_compiler_config(self, config: ET.Element):
        """Update compiler plugin configuration elements"""
        updates = {
            "source": "25",
            "target": "25",
            "compilerVersion": "25",
            "encoding": "UTF-8",
        }

        for key, value in updates.items():
            elem = config.find(f".//{key}")
            if elem is not None and elem.text != value:
                old_value = elem.text
                elem.text = value
                self.changes.append(("compiler", key, f"{old_value} → {value}"))

        # Ensure fork is enabled
        fork = config.find(".//fork")
        if fork is None:
            fork = ET.SubElement(config, "fork")
            fork.text = "true"
            self.changes.append(("compiler", "fork", "added: true"))

        # Ensure verbose is enabled
        verbose = config.find(".//verbose")
        if verbose is None:
            verbose = ET.SubElement(config, "verbose")
            verbose.text = "true"
            self.changes.append(("compiler", "verbose", "added: true"))

    def _display_summary(self):
        """Display upgrade summary"""
        if not self.changes:
            console.print("[yellow]No changes needed[/yellow]")
            return

        table = Table(title="Upgrade Summary")
        table.add_column("Type", style="cyan")
        table.add_column("Name", style="magenta")
        table.add_column("Change", style="green")

        for change_type, name, change in self.changes:
            table.add_row(change_type, name, change)

        console.print(table)
        console.print(f"\n[green]Total changes: {len(self.changes)}[/green]")

        if self.dry_run:
            console.print("[yellow]⚠ Dry run mode - no files were modified[/yellow]")


def main():
    parser = argparse.ArgumentParser(
        description="Upgrade Maven dependencies to JDK25-compatible versions"
    )
    parser.add_argument(
        "--pom",
        type=Path,
        default=Path("pom.xml"),
        help="Path to pom.xml file (default: pom.xml)"
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Preview changes without modifying files"
    )

    args = parser.parse_args()

    upgrader = PomUpgrader(args.pom, dry_run=args.dry_run)
    success = upgrader.upgrade()

    sys.exit(0 if success else 1)


if __name__ == "__main__":
    main()
