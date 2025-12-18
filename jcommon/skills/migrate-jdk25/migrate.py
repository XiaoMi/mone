#!/usr/bin/env python3
"""
JDK25 Migration Orchestrator

Main entry point for automated JDK8 to JDK25 migration with Spring Boot 3.x.
Coordinates all migration steps and provides interactive workflow.
"""

import argparse
import subprocess
import sys
from pathlib import Path
from typing import Optional
from rich.console import Console
from rich.panel import Panel
from rich.prompt import Confirm, Prompt
from rich.progress import Progress, SpinnerColumn, TextColumn
import git

console = Console()


class MigrationOrchestrator:
    """Orchestrates the complete JDK25 migration process"""

    def __init__(self, project_dir: Path, dry_run: bool = False):
        self.project_dir = project_dir.resolve()
        self.dry_run = dry_run
        self.skill_dir = Path(__file__).parent
        self.git_repo: Optional[git.Repo] = None

        # Check if project is a git repo
        try:
            self.git_repo = git.Repo(self.project_dir)
        except git.InvalidGitRepositoryError:
            console.print("[yellow]Warning: Not a git repository. Backup features disabled.[/yellow]")

    def run_interactive(self):
        """Run interactive migration wizard"""
        console.print(Panel.fit(
            "[bold cyan]JDK25 Migration Wizard[/bold cyan]\n"
            "Automated migration from JDK8 to JDK25 with Spring Boot 3.x",
            border_style="cyan"
        ))

        console.print(f"\n[bold]Project Directory:[/bold] {self.project_dir}")

        if self.dry_run:
            console.print("[yellow]Running in DRY-RUN mode (no files will be modified)[/yellow]")

        # Pre-flight checks
        if not self._preflight_checks():
            return False

        # Confirm start
        if not Confirm.ask("\n[bold]Ready to start migration?[/bold]"):
            console.print("[yellow]Migration cancelled[/yellow]")
            return False

        # Create backup branch
        if self.git_repo and not self.dry_run:
            self._create_backup_branch()

        # Run migration steps
        steps = [
            ("Upgrade Dependencies", self._step_upgrade_dependencies),
            ("Migrate Namespaces", self._step_migrate_namespaces),
            ("Update Configurations", self._step_migrate_config),
            ("Generate JVM Parameters", self._step_generate_jvm_params),
        ]

        for step_name, step_func in steps:
            console.print(f"\n[bold cyan]{'='*60}[/bold cyan]")
            console.print(f"[bold cyan]Step: {step_name}[/bold cyan]")
            console.print(f"[bold cyan]{'='*60}[/bold cyan]")

            if not step_func():
                console.print(f"[red]✗ {step_name} failed[/red]")
                if not Confirm.ask("Continue with next step?"):
                    return False
            else:
                console.print(f"[green]✓ {step_name} completed[/green]")

        # Final summary
        self._show_final_summary()

        return True

    def _preflight_checks(self) -> bool:
        """Run pre-migration checks"""
        console.print("\n[bold]Running preflight checks...[/bold]")

        checks = []

        # Check pom.xml exists
        pom_path = self.project_dir / "pom.xml"
        if pom_path.exists():
            checks.append(("pom.xml found", True))
        else:
            checks.append(("pom.xml found", False))

        # Check source directory
        src_dir = self.project_dir / "src" / "main" / "java"
        if src_dir.exists():
            checks.append(("Source directory found", True))
        else:
            checks.append(("Source directory found", False))

        # Check git repo status
        if self.git_repo:
            if not self.git_repo.is_dirty():
                checks.append(("Git working directory clean", True))
            else:
                checks.append(("Git working directory clean", False))
                console.print("[yellow]Warning: Uncommitted changes detected[/yellow]")

        # Display results
        for check, passed in checks:
            status = "[green]✓[/green]" if passed else "[red]✗[/red]"
            console.print(f"  {status} {check}")

        # All critical checks must pass
        critical_checks = [checks[0], checks[1]]  # pom.xml and source dir
        if not all(passed for _, passed in critical_checks):
            console.print("\n[red]Critical checks failed. Cannot proceed.[/red]")
            return False

        return True

    def _create_backup_branch(self):
        """Create a backup branch before migration"""
        try:
            current_branch = self.git_repo.active_branch.name
            backup_branch = f"backup-before-jdk25-migration-{current_branch}"

            console.print(f"\n[cyan]Creating backup branch: {backup_branch}[/cyan]")
            self.git_repo.create_head(backup_branch)
            console.print(f"[green]✓[/green] Backup branch created: {backup_branch}")

        except Exception as e:
            console.print(f"[yellow]Warning: Could not create backup branch: {e}[/yellow]")

    def _step_upgrade_dependencies(self) -> bool:
        """Step 1: Upgrade dependencies"""
        pom_path = self.project_dir / "pom.xml"
        cmd = [
            "uv", "run",
            str(self.skill_dir / "upgrade_dependencies.py"),
            "--pom", str(pom_path)
        ]

        if self.dry_run:
            cmd.append("--dry-run")

        return self._run_command(cmd)

    def _step_migrate_namespaces(self) -> bool:
        """Step 2: Migrate namespaces"""
        src_dir = self.project_dir / "src" / "main" / "java"
        cmd = [
            "uv", "run",
            str(self.skill_dir / "migrate_namespace.py"),
            "--source-dir", str(src_dir)
        ]

        if self.dry_run:
            cmd.append("--dry-run")

        return self._run_command(cmd)

    def _step_migrate_config(self) -> bool:
        """Step 3: Migrate Spring Boot configuration"""
        cmd = [
            "uv", "run",
            str(self.skill_dir / "migrate_config.py"),
            "--project-dir", str(self.project_dir)
        ]

        if self.dry_run:
            cmd.append("--dry-run")

        return self._run_command(cmd)

    def _step_generate_jvm_params(self) -> bool:
        """Step 4: Generate JVM parameters"""
        # Ask for configuration
        console.print("\n[bold]JVM Configuration:[/bold]")
        memory = Prompt.ask("Heap memory size (e.g., 8G)", default="8G")
        vthread_num = Prompt.ask("Virtual thread count", default="10000")

        cmd = [
            "uv", "run",
            str(self.skill_dir / "generate_jvm_params.py"),
            "--memory", memory,
            "--vthread-num", vthread_num,
            "--output", str(self.project_dir / "jvm-params.txt")
        ]

        return self._run_command(cmd)

    def _run_command(self, cmd: list) -> bool:
        """Run a command and return success status"""
        try:
            result = subprocess.run(cmd, cwd=self.skill_dir, check=False)
            return result.returncode == 0
        except Exception as e:
            console.print(f"[red]Error running command: {e}[/red]")
            return False

    def _show_final_summary(self):
        """Show final migration summary and next steps"""
        console.print("\n" + "="*60)
        console.print("[bold green]Migration Completed![/bold green]")
        console.print("="*60)

        console.print("\n[bold cyan]Next Steps:[/bold cyan]")
        console.print("""
1. [bold]Review Changes[/bold]
   - Check modified pom.xml
   - Review namespace changes in Java files
   - Verify Spring Boot configuration updates

2. [bold]Update IDE Configuration[/bold]
   - Set Project JDK to JDK 25
   - Update Java compiler settings to JDK 25
   - Refresh Maven dependencies

3. [bold]Build & Test[/bold]
   - Run: mvn clean install -X
   - Fix any compilation errors
   - Run unit tests

4. [bold]Update Deployment[/bold]
   - Copy JVM parameters from jvm-params.txt
   - Update CI/CD pipeline to use JDK 25
   - Deploy to test environment first

5. [bold]Monitor Performance[/bold]
   - Check GC logs
   - Monitor virtual thread usage
   - Measure performance improvements

[bold yellow]Important Files:[/bold yellow]
- {project}/jvm-params.txt - JVM startup parameters
- {project}/pom.xml.bak - Backup of original POM
- Backup git branch created for rollback

[bold red]Common Issues to Watch:[/bold red]
- Check for @Bean methods with void return type
- Verify third-party library compatibility
- Test RocketMQ and other integrations
- Ensure redis lettuce version is compatible

For detailed troubleshooting, refer to:
migrate-jdk25/skill.md
        """)


def main():
    parser = argparse.ArgumentParser(
        description="JDK25 Migration Orchestrator - Automated migration from JDK8 to JDK25"
    )
    parser.add_argument(
        "--project-dir",
        type=Path,
        default=Path.cwd(),
        help="Project directory to migrate (default: current directory)"
    )
    parser.add_argument(
        "--interactive",
        action="store_true",
        help="Run interactive migration wizard"
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Preview changes without modifying files"
    )
    parser.add_argument(
        "--step",
        choices=["dependencies", "namespace", "config", "jvm"],
        help="Run a specific migration step"
    )

    args = parser.parse_args()

    orchestrator = MigrationOrchestrator(args.project_dir, dry_run=args.dry_run)

    if args.interactive:
        success = orchestrator.run_interactive()
    elif args.step:
        # Run specific step
        step_methods = {
            "dependencies": orchestrator._step_upgrade_dependencies,
            "namespace": orchestrator._step_migrate_namespaces,
            "config": orchestrator._step_migrate_config,
            "jvm": orchestrator._step_generate_jvm_params,
        }
        success = step_methods[args.step]()
    else:
        console.print("[yellow]Please specify --interactive or --step[/yellow]")
        console.print("Run with --help for usage information")
        success = False

    sys.exit(0 if success else 1)


if __name__ == "__main__":
    main()
