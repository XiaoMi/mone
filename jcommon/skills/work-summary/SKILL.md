---
name: work-summary
description: Generate performance review summaries from git repositories by analyzing commits, code changes, and contribution patterns over a specified time range. Use when the user needs to create work reports, performance reviews, or contribution summaries from git history.
allowed-tools: Read, Bash, Write, Glob
---

# Work Summary Skill

Comprehensive git repository analysis tool for generating performance review content and work summaries.

## When to Use

- User asks to generate a work summary or performance review
- User needs to analyze git contributions over a time period
- User wants statistics about their work in one or more repositories
- User requests a report of code changes, commits, or development activity

## How to Use

### Step 1: Gather Information

Ask the user for:
- **Repository path(s)**: One or more local git repository paths (absolute or relative)
- **Time range**: Start and end dates (e.g., "2024-01-01 to 2024-12-31", "last 3 months", "Q4 2024")
- **Author filter** (optional): Git author name/email to filter commits (defaults to current git user)
- **Output format** (optional): "markdown", "text", or "json" (defaults to markdown)

### Step 2: Validate Repository

Use the Bash tool to verify the repository exists and is a valid git repository:
```bash
cd <repo_path> && git rev-parse --git-dir
```

### Step 3: Run Analysis Script

Execute the work summary analysis script:
```bash
python $SKILL_ROOT/scripts/analyze_work.py \
  --repo <absolute_repo_path> \
  --start-date <YYYY-MM-DD> \
  --end-date <YYYY-MM-DD> \
  --author "<author_name_or_email>" \
  --output <output_json_path> \
  --format <markdown|text|json>
```

For multiple repositories:
```bash
python $SKILL_ROOT/scripts/analyze_work.py \
  --repo <repo1> <repo2> <repo3> \
  --start-date <YYYY-MM-DD> \
  --end-date <YYYY-MM-DD> \
  --author "<author_name_or_email>" \
  --output <output_json_path>
```

### Step 4: Parse and Present Results

Read the generated output file and present the work summary to the user in a well-formatted report. Include:

1. **Overview Section**
   - Time period covered
   - Total commits, files changed, lines added/removed
   - Repositories analyzed

2. **Key Achievements**
   - Major features or changes based on commit messages
   - Significant file modifications
   - Pattern analysis of work type

3. **Contribution Statistics**
   - Commit frequency over time
   - Code churn metrics
   - File type breakdown
   - Most active areas of codebase

4. **Detailed Timeline** (optional)
   - Week-by-week or month-by-month breakdown
   - Notable commits and changes

### Step 5: Offer Enhancements

Ask the user if they want:
- To filter by specific file patterns or directories
- To exclude certain types of commits (e.g., merges, automated commits)
- To add more repositories to the analysis
- To export in a different format
- To generate visualizations (commit heatmap, language breakdown, etc.)

## Script Dependencies

The analysis script requires:
- Python 3.8+
- GitPython library for git operations

Install with:
```bash
pip install -r $SKILL_ROOT/scripts/requirements.txt
```

## Output Structure

The script generates a JSON file with the following structure:

```json
{
  "summary": {
    "time_range": {"start": "...", "end": "..."},
    "total_commits": 123,
    "total_files_changed": 456,
    "total_insertions": 7890,
    "total_deletions": 1234,
    "repositories": ["repo1", "repo2"]
  },
  "commits": [
    {
      "hash": "abc123",
      "date": "2024-01-15T10:30:00",
      "message": "Add new feature",
      "files_changed": 5,
      "insertions": 120,
      "deletions": 30,
      "files": ["path/to/file.py", ...]
    }
  ],
  "statistics": {
    "commits_by_week": {...},
    "files_by_extension": {...},
    "most_modified_files": [...],
    "largest_commits": [...]
  },
  "achievements": [
    "Implemented authentication system (23 commits)",
    "Refactored database layer (15 files changed)",
    ...
  ]
}
```

## Tips for Best Results

1. **Meaningful Commit Messages**: The quality of the summary depends on commit message quality
2. **Time Alignment**: Align time ranges with review periods (quarters, months, etc.)
3. **Multiple Repos**: Analyze all relevant repositories for complete picture
4. **Author Matching**: Ensure author filter matches git config user (name or email)
5. **Exclude Noise**: Consider filtering out automated commits, merges, or trivial changes

## Example Usage

**User**: "Generate my work summary for Q4 2024 from the ~/projects/my-app repository"

**Claude**:
1. Confirms repository path and time range (Oct 1 - Dec 31, 2024)
2. Runs analysis script with appropriate parameters
3. Generates comprehensive markdown report with:
   - 47 commits over 3 months
   - Key features: user authentication, API optimization, bug fixes
   - 2,345 lines added across 89 files
   - Primary work areas: backend services, database migrations
   - Contribution timeline with weekly breakdown

## Troubleshooting

- **Invalid git repository**: Ensure path points to a directory with `.git` folder
- **No commits found**: Check author filter matches git user configuration
- **Date parsing errors**: Use YYYY-MM-DD format for dates
- **Permission errors**: Ensure read access to git repository

## Related

See `reference.md` for detailed explanation of metrics and `examples.md` for sample outputs.
