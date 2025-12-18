# Quick Start Guide

Get started with JDK25 migration in 5 minutes!

## Prerequisites

```bash
# Install uv (Python package manager)
curl -LsSf https://astral.sh/uv/install.sh | sh

# Verify installation
uv --version
```

## Option 1: Use the Convenience Script (Recommended)

The `run.sh` script makes it easy to run any migration tool.

### Interactive Migration Wizard

```bash
# Navigate to your Java project
cd /path/to/your/java/project

# Run the interactive wizard
/path/to/migrate-jdk25/run.sh migrate --interactive
```

The wizard will:
1. ✅ Check prerequisites
2. ✅ Create backup branch (if git repo)
3. ✅ Upgrade dependencies
4. ✅ Migrate namespaces
5. ✅ Update configurations
6. ✅ Generate JVM parameters

### Run Individual Steps

```bash
# Preview dependency upgrades
/path/to/migrate-jdk25/run.sh deps --pom pom.xml --dry-run

# Actually upgrade dependencies
/path/to/migrate-jdk25/run.sh deps --pom pom.xml

# Migrate namespaces
/path/to/migrate-jdk25/run.sh ns --source-dir src/main/java

# Update Spring Boot configs
/path/to/migrate-jdk25/run.sh config --project-dir .

# Generate JVM parameters
/path/to/migrate-jdk25/run.sh jvm --memory 8G --output jvm-params.txt
```

## Option 2: Use Python Scripts Directly

```bash
cd /path/to/migrate-jdk25

# Run interactive wizard
uv run --with lxml --with rich --with click --with pyyaml --with gitpython --with colorama \
  migrate.py --interactive

# Or run specific scripts
uv run --with lxml --with rich upgrade_dependencies.py --pom /path/to/pom.xml
uv run --with rich migrate_namespace.py --source-dir /path/to/src/main/java
uv run --with rich migrate_config.py --project-dir /path/to/project
uv run --with rich generate_jvm_params.py --memory 8G
```

## Complete Example Workflow

Let's migrate a sample project step by step:

```bash
# 1. Navigate to your project
cd ~/projects/my-java-app

# 2. Ensure you're on a clean git branch
git status
git checkout -b jdk25-migration

# 3. Run dry-run first to preview changes
/path/to/migrate-jdk25/run.sh migrate --interactive --dry-run

# Review the preview output...

# 4. Run actual migration
/path/to/migrate-jdk25/run.sh migrate --interactive

# 5. Review changes
git status
git diff

# 6. Update IDE settings to JDK 25

# 7. Build the project
mvn clean install -X

# 8. Fix any compilation errors

# 9. Run tests
mvn test

# 10. Commit changes
git add .
git commit -m "Migrate to JDK25 with Spring Boot 3.x"
```

## What Each Tool Does

### 1. upgrade_dependencies.py

**Updates your pom.xml:**
- Spring Boot → 3.5.5
- Dubbo → 3.3.4
- Lombok → 1.18.40
- JDK version → 25
- And more...

**Usage:**
```bash
./run.sh deps --pom pom.xml
```

### 2. migrate_namespace.py

**Converts Java imports:**
- `javax.validation.*` → `jakarta.validation.*`
- `javax.annotation.*` → `jakarta.annotation.*`
- And more...

**Usage:**
```bash
./run.sh ns --source-dir src/main/java
```

### 3. migrate_config.py

**Updates Spring Boot configs:**
- Migrates `META-INF/spring.factories` to `AutoConfiguration.imports`
- Adds circular dependency support
- Detects issues with `@Bean` methods
- Identifies `@Value` on private setters

**Usage:**
```bash
./run.sh config --project-dir .
```

### 4. generate_jvm_params.py

**Creates optimized JVM parameters:**
- ZGC configuration
- Virtual thread settings
- Module system parameters
- GC logging setup

**Usage:**
```bash
./run.sh jvm --memory 8G --vthread-num 10000
```

## Dry-Run Mode

**Always preview changes first:**

```bash
# Preview all changes without modifying files
./run.sh migrate --interactive --dry-run

# Preview specific operations
./run.sh deps --pom pom.xml --dry-run
./run.sh ns --source-dir src/main/java --dry-run
./run.sh config --project-dir . --dry-run
```

## After Migration

### 1. Update IDE Configuration

**IntelliJ IDEA:**
- File → Project Structure → Project → SDK: Select JDK 25
- File → Project Structure → Modules → Language level: 25
- Reload Maven project (right-click pom.xml → Maven → Reload Project)

### 2. Build & Test

```bash
# Clean build
mvn clean install -X

# Run tests
mvn test

# Package
mvn package
```

### 3. Use Generated JVM Parameters

**Local Testing:**
```bash
# The tool generates jvm-params.txt
java @jvm-params.txt -jar target/your-app.jar
```

**Production Deployment:**
- Copy parameters from `jvm-params.txt` to your deployment scripts
- Update CI/CD pipeline configurations
- Deploy to test environment first

### 4. Monitor Performance

```bash
# Check GC logs
tail -f /home/work/log/gc-*.log

# Monitor application metrics
# - Memory usage
# - GC pause times
# - Throughput improvements
```

## Common Issues

### Issue: Build fails with "Unsupported class file major version 69"

**Solution:** Ensure Spring Boot version is 3.5.5 or higher

### Issue: "Cannot find javax.validation"

**Solution:** The namespace migrator should have added jakarta dependencies. Check pom.xml for:
```xml
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
    <version>3.0.2</version>
</dependency>
```

### Issue: "@Bean method must not be void"

**Solution:** The config migrator will report these. Manually fix by changing void methods to return the bean instance.

### Issue: Lombok compilation errors

**Solution:** Verify Lombok version is 1.18.40 and annotation processors are correctly configured in maven-compiler-plugin.

## Performance Expectations

### After JDK25 + ZGC + Virtual Threads
- **30-40% performance improvement**
- Lower GC pause times
- Better throughput under high load

### After Adding Reactive Programming
- **100%+ additional improvement** (varies by use case)
- Non-blocking I/O
- Better resource utilization
- See `reactive_examples.md` for code templates

## Getting Help

```bash
# Show help for any command
./run.sh help
./run.sh migrate --help
./run.sh deps --help
./run.sh ns --help
./run.sh config --help
./run.sh jvm --help
```

## Next Steps

1. ✅ Complete the migration
2. 📖 Read `reactive_examples.md` for reactive programming patterns
3. 📖 Read `skill.md` for comprehensive documentation
4. 🚀 Start converting blocking code to reactive (for additional performance gains)

## Need More Help?

- Check `README.md` for detailed documentation
- Review `skill.md` for common issues and solutions
- Consult the original PDF: "升级JDK25&响应式最佳实践.pdf"
