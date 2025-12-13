# JDK25 Migration Toolkit

Automated toolkit for migrating Java projects from JDK8 to JDK25 with Spring Boot 3.x upgrade, ZGC garbage collector, virtual threads, and reactive programming support.

## Quick Start

### Prerequisites

```bash
# Install uv (if not already installed)
curl -LsSf https://astral.sh/uv/install.sh | sh

# Verify installation
uv --version
```

### Basic Usage

1. **Navigate to your Java project:**
   ```bash
   cd /path/to/your/java/project
   ```

2. **Run interactive migration wizard:**
   ```bash
   uv run /path/to/migrate-jdk25/migrate.py --interactive
   ```

3. **Or run specific steps:**
   ```bash
   # Upgrade dependencies only
   uv run /path/to/migrate-jdk25/migrate.py --step dependencies

   # Migrate namespaces only
   uv run /path/to/migrate-jdk25/migrate.py --step namespace

   # Update Spring Boot configs
   uv run /path/to/migrate-jdk25/migrate.py --step config

   # Generate JVM parameters
   uv run /path/to/migrate-jdk25/migrate.py --step jvm
   ```

4. **Dry run mode (preview changes):**
   ```bash
   uv run /path/to/migrate-jdk25/migrate.py --interactive --dry-run
   ```

## Migration Steps

### 1. Dependency Upgrade

Upgrades Maven `pom.xml` dependencies:

```bash
uv run upgrade_dependencies.py --pom pom.xml
```

**What it does:**
- ✅ Spring Boot → 3.5.5
- ✅ Spring Framework → 6.2.11
- ✅ Dubbo → 3.3.4-mone-v2-SNAPSHOT
- ✅ Lombok → 1.18.40
- ✅ Updates JDK version to 25
- ✅ Configures Maven compiler plugin

### 2. Namespace Migration

Converts `javax.*` imports to `jakarta.*`:

```bash
uv run migrate_namespace.py --source-dir src/main/java
```

**What it does:**
- ✅ javax.validation → jakarta.validation
- ✅ javax.annotation → jakarta.annotation
- ✅ javax.persistence → jakarta.persistence
- ✅ And more...

### 3. Configuration Migration

Migrates Spring Boot 2.x configs to 3.x:

```bash
uv run migrate_config.py --project-dir .
```

**What it does:**
- ✅ Migrates `META-INF/spring.factories` → `AutoConfiguration.imports`
- ✅ Updates `application.properties` for circular dependencies
- ✅ Detects `@Bean` void methods (requires manual fix)
- ✅ Identifies `@Value` on private setters

### 4. JVM Parameters Generation

Generates optimized JVM parameters:

```bash
uv run generate_jvm_params.py --memory 8G --vthread-num 10000 --output jvm-params.txt
```

**What it generates:**
- ✅ ZGC configuration
- ✅ Virtual thread settings
- ✅ Module system parameters
- ✅ GC logging configuration
- ✅ Error handling setup

## Individual Script Usage

### upgrade_dependencies.py

```bash
# Upgrade dependencies in pom.xml
uv run upgrade_dependencies.py --pom pom.xml

# Preview changes without modifying
uv run upgrade_dependencies.py --pom pom.xml --dry-run
```

### migrate_namespace.py

```bash
# Migrate all Java files in source directory
uv run migrate_namespace.py --source-dir src/main/java

# Preview changes
uv run migrate_namespace.py --source-dir src/main/java --dry-run
```

### migrate_config.py

```bash
# Migrate Spring Boot configurations
uv run migrate_config.py --project-dir .

# Preview changes
uv run migrate_config.py --project-dir . --dry-run
```

### generate_jvm_params.py

```bash
# Generate JVM parameters with custom settings
uv run generate_jvm_params.py \
  --memory 8G \
  --vthread-num 10000 \
  --output jvm-params.txt

# For smaller deployments
uv run generate_jvm_params.py --memory 4G --vthread-num 5000
```

## Post-Migration Checklist

### 1. Review Changes

```bash
# Check what was modified
git status
git diff

# Review POM changes
diff pom.xml.bak pom.xml
```

### 2. Update IDE Configuration

**IntelliJ IDEA:**
1. File → Project Structure → Project → SDK: JDK 25
2. File → Project Structure → Modules → Language level: 25
3. File → Settings → Build, Execution, Deployment → Compiler → Java Compiler → Target bytecode version: 25
4. Reload Maven project

**VS Code:**
1. Update `.vscode/settings.json`:
   ```json
   {
     "java.configuration.runtimes": [
       {
         "name": "JavaSE-25",
         "path": "/path/to/jdk-25"
       }
     ]
   }
   ```

### 3. Build & Test

```bash
# Clean build with detailed output
mvn clean install -X

# Run tests
mvn test

# Package application
mvn package
```

### 4. Common Build Issues

**Issue: "Unsupported class file major version 69"**
- Solution: Ensure Spring Boot version is 3.5.5+

**Issue: "Cannot find javax.validation"**
- Solution: Add jakarta.validation-api dependency

**Issue: "@Bean method must not be void"**
- Solution: Change void return type to return the bean instance

**Issue: Lombok compilation errors**
- Solution: Verify Lombok version is 1.18.40 and annotation processor is configured

### 5. Deploy & Monitor

**Local Testing:**
```bash
# Use generated JVM parameters
java @jvm-params.txt -jar target/your-app.jar
```

**Production Deployment:**
```bash
# Copy JVM parameters to deployment scripts
# Monitor GC logs in /home/work/log/gc-*.log
# Check heap dumps if OOM occurs
```

## Reactive Programming Guide

See `reactive_examples.md` for code templates:
- Converting blocking I/O to reactive Mono/Flux
- Database access with R2DBC
- Redis reactive operations
- Dubbo reactive RPC calls

## Performance Expectations

### Phase 1: JDK25 + ZGC + Virtual Threads
- **Expected Improvement:** 30-40%
- **Benefits:**
  - Better GC performance with ZGC
  - Lower latency with virtual threads
  - Improved throughput under high concurrency

### Phase 2: + Reactive Programming
- **Expected Improvement:** 100%+ (varies by use case)
- **Benefits:**
  - Non-blocking I/O operations
  - Better resource utilization
  - Improved scalability

## Troubleshooting

### Migration Script Errors

**Error: "Module 'lxml' not found"**
```bash
# Install dependencies manually
uv pip install lxml rich click pyyaml gitpython colorama
```

**Error: "Permission denied"**
```bash
# Make scripts executable
chmod +x migrate-jdk25/*.py
```

### Common Runtime Issues

Refer to `skill.md` "Common Issues Handled" section for:
- Build & compilation errors
- Runtime exceptions
- Dependency conflicts
- Spring Boot 3 compatibility issues

## Project Structure

```
migrate-jdk25/
├── skill.md                    # Skill description for Claude
├── README.md                   # This file
├── pyproject.toml             # uv dependencies
├── migrate.py                 # Main orchestrator
├── upgrade_dependencies.py    # POM upgrader
├── migrate_namespace.py       # Namespace migrator
├── migrate_config.py          # Spring Boot config migrator
├── generate_jvm_params.py     # JVM params generator
└── reactive_examples.md       # Reactive programming templates
```

## References

- **Original Guide:** 升级JDK25&响应式最佳实践.pdf
- **JDK 25:** https://jdk.java.net/25/
- **Spring Boot 3 Migration:** https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide
- **Jakarta EE 9:** https://jakarta.ee/specifications/platform/9/
- **JEP 491:** https://openjdk.org/jeps/491 (Virtual Thread improvements)

## Support

For issues and questions:
1. Check the generated migration logs
2. Review `skill.md` for common issues
3. Consult the original PDF documentation
4. Review Spring Boot 3 migration guide

## License

This toolkit is based on internal migration guidelines and best practices.
