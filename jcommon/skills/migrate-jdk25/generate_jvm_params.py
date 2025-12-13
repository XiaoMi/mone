#!/usr/bin/env python3
"""
JDK25 Migration - JVM Parameters Generator

Generates optimized JVM parameters for JDK25 with ZGC and virtual threads.
"""

import argparse
import sys
from pathlib import Path
from rich.console import Console
from rich.panel import Panel

console = Console()


class JVMParamsGenerator:
    """Generates JVM parameters for JDK25 deployment"""

    def __init__(self, memory: str = "8G", vthread_num: int = 10000, output_file: Path = None):
        self.memory = memory
        self.vthread_num = vthread_num
        self.output_file = output_file or Path("jvm-params.txt")

        # Parse memory
        self.heap_size = self._parse_memory(memory)

    def _parse_memory(self, memory: str) -> int:
        """Parse memory string (e.g., '8G') to MB"""
        memory = memory.upper().strip()
        if memory.endswith('G'):
            return int(memory[:-1]) * 1024
        elif memory.endswith('M'):
            return int(memory[:-1])
        else:
            return int(memory)

    def generate(self) -> str:
        """Generate JVM parameters"""
        # Calculate memory allocations
        heap_mb = self.heap_size
        direct_memory_mb = max(2048, heap_mb // 4)  # 1/4 of heap or 2GB minimum
        metaspace_mb = 1024

        # Virtual thread scheduler parallelism (default to 16 or CPU cores)
        parallelism = 16

        params = f"""# JDK25 JVM Parameters - Generated
# Heap Memory: {self.memory}
# Virtual Threads: {self.vthread_num}

# ===== Memory Configuration =====
-Xms{heap_mb}m
-Xmx{heap_mb}m
-XX:MaxDirectMemorySize={direct_memory_mb}M
-XX:MetaspaceSize={metaspace_mb}M
-XX:MaxMetaspaceSize={metaspace_mb}M

# ===== ZGC Configuration =====
# Enable ZGC with generational mode (JDK 21+)
-XX:+UseZGC
-XX:+ZGenerational

# ===== GC Logging =====
# Logs will be written to /home/work/log/ with rotation
-Xlog:safepoint,classhisto*=trace,age*,gc*=info:file=/home/work/log/gc-%t.log:time,level,tid,tags:filecount=5,filesize=50m

# ===== Error Handling =====
# Preserve stack traces in exceptions
-XX:-OmitStackTraceInFastThrow

# Auto-generate heap dump on OutOfMemoryError
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/home/work/log/errorDump.hprof

# Show detailed exception messages (helpful for debugging)
-XX:+ShowCodeDetailsInExceptionMessages

# ===== JDK Module System (Required for JDK9+) =====
# These are required for reflection and framework compatibility
--add-opens=java.base/java.time=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/java.math=ALL-UNNAMED
--add-opens=java.base/sun.reflect=ALL-UNNAMED
--add-opens=java.base/java.nio=ALL-UNNAMED
--add-exports=java.base/sun.reflect.annotation=ALL-UNNAMED
--add-exports=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED

# ===== Virtual Thread Configuration =====
# Platform threads available for virtual thread scheduling
# Default is CPU cores, can be set to match old dubbo thread count
-Djdk.virtualThreadScheduler.parallelism={parallelism}

# ===== Application Configuration =====
# Set application-specific properties here
# -Dapp.name=your-application
# -Dspring.profiles.active=production
"""

        return params

    def save(self) -> bool:
        """Save parameters to file"""
        try:
            params = self.generate()

            # Save to file
            self.output_file.write_text(params, encoding='utf-8')

            console.print(Panel(
                f"[bold green]JVM Parameters Generated[/bold green]\n"
                f"Output: {self.output_file}",
                border_style="green"
            ))

            # Display preview
            console.print("\n[bold cyan]Parameters Preview:[/bold cyan]")
            console.print(params)

            # Show usage instructions
            self._show_usage_instructions()

            return True

        except Exception as e:
            console.print(f"[red]Error generating JVM parameters: {e}[/red]")
            return False

    def _show_usage_instructions(self):
        """Show how to use the generated parameters"""
        console.print("\n[bold yellow]Usage Instructions:[/bold yellow]")
        console.print(f"""
[bold]Local Development (IDEA):[/bold]
1. Run → Edit Configurations
2. Select your application
3. VM options: Copy content from {self.output_file}

[bold]Production Deployment:[/bold]
1. Copy parameters to your startup script:
   java [JVM_PARAMS] -jar your-application.jar

2. For Docker:
   ENV JAVA_OPTS="[paste parameters here]"
   CMD java $JAVA_OPTS -jar app.jar

3. For Kubernetes:
   Add to deployment.yaml under containers.env

[bold cyan]Important Notes:[/bold cyan]
• Ensure /home/work/log/ directory exists (or modify paths)
• Adjust -Djdk.virtualThreadScheduler.parallelism based on CPU cores
• Monitor GC logs after deployment
• Tune heap size based on actual memory usage

[bold]Performance Expectations:[/bold]
• 30-40% improvement from JDK25 + ZGC + Virtual Threads
• Additional 100%+ improvement possible with reactive programming

[bold]Monitoring:[/bold]
• GC logs: /home/work/log/gc-*.log
• Heap dumps: /home/work/log/errorDump.hprof
• Use GCViewer or similar tools to analyze GC behavior
        """)


def main():
    parser = argparse.ArgumentParser(
        description="Generate JVM parameters for JDK25 deployment"
    )
    parser.add_argument(
        "--memory",
        default="8G",
        help="Heap memory size (e.g., 8G, 4096M) - default: 8G"
    )
    parser.add_argument(
        "--vthread-num",
        type=int,
        default=10000,
        help="Number of virtual threads (for Dubbo configuration) - default: 10000"
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=Path("jvm-params.txt"),
        help="Output file path - default: jvm-params.txt"
    )

    args = parser.parse_args()

    generator = JVMParamsGenerator(
        memory=args.memory,
        vthread_num=args.vthread_num,
        output_file=args.output
    )

    success = generator.save()
    sys.exit(0 if success else 1)


if __name__ == "__main__":
    main()
