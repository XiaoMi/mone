#!/bin/bash
# Convenience wrapper for running migration scripts with uv

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPS="--with lxml --with rich --with click --with pyyaml --with gitpython --with colorama"

case "$1" in
    migrate|m)
        shift
        cd "$SCRIPT_DIR" && uv run $DEPS migrate.py "$@"
        ;;
    upgrade-deps|deps|d)
        shift
        cd "$SCRIPT_DIR" && uv run --with lxml --with rich upgrade_dependencies.py "$@"
        ;;
    migrate-namespace|ns|n)
        shift
        cd "$SCRIPT_DIR" && uv run --with rich migrate_namespace.py "$@"
        ;;
    migrate-config|config|c)
        shift
        cd "$SCRIPT_DIR" && uv run --with rich --with pyyaml migrate_config.py "$@"
        ;;
    gen-jvm|jvm|j)
        shift
        cd "$SCRIPT_DIR" && uv run --with rich generate_jvm_params.py "$@"
        ;;
    help|h|-h|--help)
        echo "JDK25 Migration Toolkit - Convenience Runner"
        echo ""
        echo "Usage: $0 <command> [options]"
        echo ""
        echo "Commands:"
        echo "  migrate, m              Run full migration wizard"
        echo "  upgrade-deps, deps, d   Upgrade Maven dependencies"
        echo "  migrate-namespace, ns, n  Migrate javax to jakarta"
        echo "  migrate-config, config, c Update Spring Boot configs"
        echo "  gen-jvm, jvm, j         Generate JVM parameters"
        echo "  help, h                 Show this help"
        echo ""
        echo "Examples:"
        echo "  $0 migrate --interactive         # Run interactive wizard"
        echo "  $0 deps --pom pom.xml --dry-run  # Preview dependency upgrades"
        echo "  $0 jvm --memory 16G              # Generate JVM params for 16G heap"
        echo ""
        echo "For detailed help on each command, run:"
        echo "  $0 <command> --help"
        ;;
    *)
        echo "Unknown command: $1"
        echo "Run '$0 help' for usage information"
        exit 1
        ;;
esac
