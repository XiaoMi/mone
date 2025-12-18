# migrate-jdk25

## Description

Automated toolkit for migrating Java projects from JDK8 to JDK25 with Spring Boot 3.x upgrade, enabling ZGC, virtual threads, and reactive programming best practices.

This skill provides comprehensive migration assistance including:
- Dependency upgrades (Spring Boot 3.5.5, Dubbo 3.3.4, etc.)
- JVM configuration for ZGC and virtual threads
- javax to jakarta namespace migration
- Spring Boot 3.x configuration updates
- Common issue detection and resolution
- Reactive programming pattern transformation

## When to Use

Use this skill when:
- Migrating existing Java projects from JDK8/11/17 to JDK25
- Upgrading Spring Boot 2.x to Spring Boot 3.x
- Enabling ZGC garbage collector and virtual threads
- Converting traditional blocking code to reactive patterns
- Troubleshooting JDK25 migration issues

## Prerequisites

- Python 3.10+ with `uv` package manager installed
- Git repository (for tracking changes)
- Target project uses Maven for build management
- Backup your project before running migration scripts

## Features

### 1. Automated Dependency Upgrade

Upgrades Maven dependencies in `pom.xml`:
- Spring Boot → 3.5.5
- Spring Framework → 6.2.11
- Dubbo → 3.3.4-mone-v2-SNAPSHOT
- Lombok → 1.18.40
- Nacos → 2.1.2-XIAOMI
- commons-pool2 → 2.12.0
- mapstruct → 1.5.3.Final

### 2. Namespace Migration (javax → jakarta)

Automatically converts:
- `javax.validation.*` → `jakarta.validation.*`
- `javax.annotation.*` → `jakarta.annotation.*`
- `javax.persistence.*` → `jakarta.persistence.*`
- And more...

### 3. Spring Boot 3.x Configuration Migration

- Migrates `META-INF/spring.factories` to `AutoConfiguration.imports`
- Updates application properties for circular dependency support
- Fixes `@Bean` method signatures (void detection)
- Updates setter method visibility for `@Value` annotations

### 4. JVM Configuration Generator

Generates optimized JVM parameters for:
- ZGC garbage collector
- Virtual thread scheduler
- Module system (--add-opens, --add-exports)
- GC logging and heap dump configuration

### 5. Reactive Programming Templates

Provides code templates and migration guides for:
- Converting blocking I/O to reactive Mono/Flux
- WebFlux integration
- R2DBC database access
- Reactive Redis operations
- Reactive Dubbo RPC calls

## Usage

### Quick Start

```bash
# Navigate to your Java project root
cd /path/to/your/project

# Run the migration wizard
uv run migrate-jdk25/migrate.py --interactive

# Or run specific migration steps
uv run migrate-jdk25/migrate.py --step dependencies
uv run migrate-jdk25/migrate.py --step namespace
uv run migrate-jdk25/migrate.py --step config
```

### Migration Steps

1. **Analyze Project**
   ```bash
   uv run migrate-jdk25/analyze.py --project-dir .
   ```
   Scans your project and generates a migration report with potential issues.

2. **Upgrade Dependencies**
   ```bash
   uv run migrate-jdk25/upgrade_dependencies.py --pom pom.xml
   ```
   Updates Maven dependencies to JDK25-compatible versions.

3. **Migrate Namespaces**
   ```bash
   uv run migrate-jdk25/migrate_namespace.py --source-dir src/main/java
   ```
   Converts javax.* imports to jakarta.* across all Java files.

4. **Update Configurations**
   ```bash
   uv run migrate-jdk25/migrate_config.py --project-dir .
   ```
   Migrates Spring Boot configuration files and auto-configuration.

5. **Generate JVM Parameters**
   ```bash
   uv run migrate-jdk25/generate_jvm_params.py --memory 8G --threads 10000
   ```
   Creates JVM startup parameters for your deployment.

6. **Validate Migration**
   ```bash
   uv run migrate-jdk25/validate.py --project-dir .
   ```
   Checks for common migration issues and missing configurations.

## Common Issues Handled

### Build & Compilation
- ✅ Unsupported class file major version 69
- ✅ Lombok and MapStruct annotation processor conflicts
- ✅ Maven compiler plugin configuration

### Runtime Issues
- ✅ NoClassDefFoundError for moved classes
- ✅ Spring auto-configuration not loading
- ✅ @Bean method void return type
- ✅ @Value on private setter methods
- ✅ Redis lettuce version compatibility

### Dependency Conflicts
- ✅ Jedis version conflicts (4.x → 3.8.0)
- ✅ SnakeYAML version issues
- ✅ Prometheus client conflicts
- ✅ RocketMQ Spring Boot Starter compatibility

## Virtual Thread Best Practices

The skill includes guidance on:
- Converting thread pools to virtual thread executors
- Avoiding synchronized pinning (fixed in JDK25)
- Configuring virtual thread scheduler parallelism
- Dubbo virtual thread configuration

## Reactive Programming Guide

Comprehensive examples for:
- Mono/Flux creation and transformation
- Error handling in reactive streams
- Converting blocking code to reactive
- Database access with R2DBC
- Redis reactive templates
- Reactive Dubbo integration

## Output

Migration scripts will:
- Create backup branches in Git
- Generate detailed migration logs
- Produce JVM parameter files
- Create issue tracking reports
- Suggest manual review points

## Safety Features

- Automatic Git backup before changes
- Dry-run mode for preview
- Rollback capability
- Validation checks at each step
- Detailed logging for audit trail

## References

Based on the official migration guide: "升级JDK25&响应式最佳实践.pdf"

Key documents:
- JDK 25 Release Notes: https://jdk.java.net/25/
- Spring Boot 3.x Migration: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide
- JEP 491: Synchronization improvements for virtual threads

## Support

For issues and questions:
- Check the generated migration report
- Review common issues in the skill guide
- Consult the original PDF documentation
