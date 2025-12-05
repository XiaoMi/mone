# Work Summary Skill - Examples

This document provides example usage scenarios and sample outputs for the work-summary skill.

## Example 1: Single Repository Analysis

### User Request
"Generate my work summary for the last 3 months from my project repository"

### Interaction Flow

1. **Claude asks for clarification:**
   - Repository path: `/home/user/projects/my-app`
   - Time range: Last 3 months (auto-calculated)
   - Author: Current git user (auto-detected)
   - Format: Markdown

2. **Claude executes:**
```bash
python $SKILL_ROOT/scripts/analyze_work.py \
  --repo /home/user/projects/my-app \
  --start-date 2024-09-01 \
  --end-date 2024-12-01 \
  --output /tmp/work-summary-result.json \
  --format markdown
```

3. **Sample Output:**

```markdown
# Work Summary Report

**Period**: 2024-09-01 to 2024-12-01
**Generated**: 2024-12-01 10:30:00

## Overview

- **Total Commits**: 47
- **Files Changed**: 156 changes across 89 unique files
- **Code Changes**: +2,345 / -876 lines (net: +1,469)
- **Repositories**: my-app

## Key Achievements

- Implemented new features across 23 commits (1,567 lines added)
- Fixed bugs and resolved issues (12 commits)
- Code improvements and refactoring (8 commits, 34 files)
- Implement user authentication system (15 files, +456/-123 lines)
- Add API rate limiting middleware (8 files, +234/-45 lines)

## Activity Statistics

### Commits by Month

- **2024-09**: 18 commits
- **2024-10**: 15 commits
- **2024-11**: 14 commits

### File Types Modified

- **.py**: 67 files
- **.js**: 34 files
- **.md**: 12 files
- **.json**: 8 files
- **.yaml**: 5 files

### Most Modified Files

- `src/auth/authentication.py`: 12 modifications
- `src/api/middleware.py`: 8 modifications
- `tests/test_auth.py`: 7 modifications
- `README.md`: 6 modifications
- `src/models/user.py`: 5 modifications

## Significant Commits

### a1b2c3d4 - Implement OAuth2 authentication flow
- **Date**: 2024-09-15
- **Lines Changed**: 567

### e5f6g7h8 - Refactor database connection pooling
- **Date**: 2024-10-03
- **Lines Changed**: 423

### i9j0k1l2 - Add comprehensive API documentation
- **Date**: 2024-10-22
- **Lines Changed**: 389

## Recent Activity

- **2024-11-28** [m3n4o5p6] Fix race condition in cache invalidation
  - 3 files, +45/-23 lines
- **2024-11-25** [q7r8s9t0] Update dependencies to latest versions
  - 2 files, +12/-8 lines
...
```

## Example 2: Multiple Repositories

### User Request
"Generate my Q4 performance review summary from all my projects"

### Parameters
- Repositories: `/home/user/projects/frontend`, `/home/user/projects/backend`, `/home/user/projects/mobile`
- Time range: Q4 2024 (Oct 1 - Dec 31)
- Author: john.doe@company.com
- Format: Markdown

### Command Executed
```bash
python $SKILL_ROOT/scripts/analyze_work.py \
  --repo /home/user/projects/frontend /home/user/projects/backend /home/user/projects/mobile \
  --start-date 2024-10-01 \
  --end-date 2024-12-31 \
  --author "john.doe@company.com" \
  --output /tmp/q4-performance-review.json \
  --format markdown
```

### Key Results
```
Summary: 142 commits across 3 repositories
- frontend: 56 commits
- backend: 68 commits
- mobile: 18 commits

Total: 267 unique files touched, +8,934/-3,245 lines
```

## Example 3: Specific Author Filter

### User Request
"Show me what Jane contributed to the auth-service repo this year"

### Parameters
- Repository: `/repos/auth-service`
- Time range: 2024-01-01 to 2024-12-31
- Author: "Jane Smith" or "jane@company.com"
- Format: JSON

### Command
```bash
python $SKILL_ROOT/scripts/analyze_work.py \
  --repo /repos/auth-service \
  --start-date 2024-01-01 \
  --end-date 2024-12-31 \
  --author "Jane Smith" \
  --output /tmp/jane-contributions.json \
  --format json
```

## Example 4: Text Format for Email

### User Request
"Generate a brief summary I can paste into an email for my manager"

### Format Selection
Claude chooses `--format text` for simple, email-friendly output:

```
============================================================
WORK SUMMARY REPORT
============================================================
Period: 2024-11-01 to 2024-11-30

Total Commits: 23
Files Changed: 67
Lines Added: +1,234
Lines Deleted: -456
Repositories: web-app

KEY ACHIEVEMENTS:
------------------------------------------------------------
• Implemented new features across 12 commits (890 lines added)
• Fixed bugs and resolved issues (8 commits)
• Code improvements and refactoring (3 commits, 15 files)
• Complete user dashboard redesign (12 files, +567/-234 lines)
• Optimize database queries for performance (5 files, +123/-89 lines)
============================================================
```

## Example 5: JSON Output for Further Processing

### Use Case
User wants raw data to create custom visualizations

### Command
```bash
python $SKILL_ROOT/scripts/analyze_work.py \
  --repo /my/repo \
  --start-date 2024-01-01 \
  --end-date 2024-12-31 \
  --output analysis.json \
  --format json
```

### Sample JSON Output
```json
{
  "summary": {
    "time_range": {
      "start": "2024-01-01",
      "end": "2024-12-31"
    },
    "total_commits": 156,
    "total_files_changed": 423,
    "unique_files_touched": 198,
    "total_insertions": 12456,
    "total_deletions": 4567,
    "net_lines": 7889,
    "repositories": ["my-project"],
    "commits_per_repository": {
      "my-project": 156
    }
  },
  "commits": [
    {
      "hash": "a1b2c3d4",
      "date": "2024-12-01 10:30:00 +0000",
      "author": "John Doe",
      "email": "john@example.com",
      "message": "Add user authentication",
      "repository": "my-project",
      "files_changed": 8,
      "files": ["src/auth.py", "src/models.py", ...],
      "insertions": 234,
      "deletions": 45,
      "net_lines": 189
    }
  ],
  "statistics": {
    "commits_by_week": {
      "2024-W01": 5,
      "2024-W02": 3,
      ...
    },
    "commits_by_month": {
      "2024-01": 18,
      "2024-02": 15,
      ...
    },
    "files_by_extension": {
      ".py": 89,
      ".js": 45,
      ".md": 23
    },
    "most_modified_files": {
      "src/main.py": 15,
      "README.md": 12
    },
    "largest_commits": [...]
  },
  "achievements": [
    "Implemented new features across 67 commits (5,678 lines added)",
    "Fixed bugs and resolved issues (34 commits)",
    ...
  ]
}
```

## Tips for Using the Skill

1. **For Performance Reviews**: Use quarterly or monthly time ranges with markdown format
2. **For Daily Standups**: Use "last 1 week" with text format for quick summaries
3. **For Team Reports**: Analyze multiple repositories to show cross-project contributions
4. **For Debugging**: Use JSON format to extract specific commit details
5. **For Presentations**: Markdown format provides ready-to-use content for slides

## Common Time Range Formats

- **Exact dates**: `--start-date 2024-01-01 --end-date 2024-12-31`
- **Quarters**: Q1 (Jan-Mar), Q2 (Apr-Jun), Q3 (Jul-Sep), Q4 (Oct-Dec)
- **Relative**: "last 3 months", "last 2 weeks", "last 30 days"
- **Current period**: Use today's date as end-date

## Troubleshooting Common Issues

### Issue: "No commits found"
**Cause**: Author filter doesn't match git user
**Solution**: Check git config with `git config user.name` and `git config user.email`

### Issue: "Not a git repository"
**Cause**: Path doesn't contain `.git` folder
**Solution**: Verify path with `ls -la /path/to/repo/.git`

### Issue: "Date parsing error"
**Cause**: Invalid date format
**Solution**: Use YYYY-MM-DD format (e.g., 2024-12-01)
