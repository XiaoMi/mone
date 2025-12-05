# Work Summary Skill

A Claude Code skill for generating performance review summaries and work reports from git repository analysis.

## Overview

This skill analyzes git commit history to automatically generate comprehensive work summaries suitable for:
- Performance reviews and self-evaluations
- Sprint retrospectives
- Project status reports
- Contribution tracking
- Team reporting

## Features

- **Multi-repository analysis**: Analyze one or more git repositories simultaneously
- **Flexible time ranges**: Support for exact dates, quarters, or relative periods ("last 3 months")
- **Author filtering**: Focus on specific contributors or teams
- **Multiple output formats**: JSON, Markdown, or plain text
- **Comprehensive metrics**:
  - Commit counts and frequency patterns
  - Lines added/removed/changed
  - File modification statistics
  - Activity timelines (weekly/monthly)
  - Achievement extraction from commit messages
  - Largest and most significant commits
  - File type distribution

## Installation

### As a Project Skill (Team-shared)

1. Copy the `work-summary` folder to your project's `.claude/skills/` directory:
```bash
cp -r work-summary /path/to/your/project/.claude/skills/
```

2. Commit to git for team sharing:
```bash
git add .claude/skills/work-summary
git commit -m "Add work-summary skill for performance reviews"
```

### As a Personal Skill

1. Copy to your personal Claude skills directory:
```bash
mkdir -p ~/.claude/skills
cp -r work-summary ~/.claude/skills/
```

### Dependencies

The skill uses Python 3.8+ with only standard library modules. No external packages required!

Optional: Install development dependencies:
```bash
cd work-summary/scripts
pip install -r requirements.txt  # Currently empty, reserved for future enhancements
```

## Usage

### With Claude Code

Once installed, Claude will automatically use this skill when you request work summaries:

**Example prompts:**
- "Generate my work summary for Q4 2024"
- "Create a performance review report from my git commits this year"
- "Show me what I accomplished in the last 3 months"
- "Analyze my contributions across all my projects since September"

Claude will:
1. Ask for necessary details (repository path, time range, etc.)
2. Run the analysis script
3. Generate a formatted report with insights

### Direct Script Usage

You can also run the Python script directly:

```bash
python work-summary/scripts/analyze_work.py \
  --repo /path/to/repository \
  --start-date 2024-01-01 \
  --end-date 2024-12-31 \
  --author "Your Name" \
  --output results.json \
  --format markdown
```

#### Parameters

- `--repo`: One or more repository paths (space-separated for multiple)
- `--start-date`: Start date in YYYY-MM-DD format
- `--end-date`: End date in YYYY-MM-DD format
- `--author`: (Optional) Git author name or email to filter commits
- `--output`: Output file path
- `--format`: Output format: `json`, `markdown`, or `text` (default: json)

#### Examples

**Single repository, markdown output:**
```bash
python scripts/analyze_work.py \
  --repo ~/projects/my-app \
  --start-date 2024-10-01 \
  --end-date 2024-12-31 \
  --output q4-summary.md \
  --format markdown
```

**Multiple repositories, specific author:**
```bash
python scripts/analyze_work.py \
  --repo ~/work/frontend ~/work/backend ~/work/mobile \
  --start-date 2024-01-01 \
  --end-date 2024-12-31 \
  --author "john.doe@company.com" \
  --output annual-review.json \
  --format json
```

**Quick text summary:**
```bash
python scripts/analyze_work.py \
  --repo . \
  --start-date 2024-11-01 \
  --end-date 2024-11-30 \
  --output november.txt \
  --format text
```

## Output Formats

### Markdown (Best for reports and sharing)

Generates a comprehensive, well-formatted report with:
- Executive overview
- Key achievements
- Activity statistics with charts
- Significant commits
- Recent activity timeline

Perfect for: Performance reviews, project documentation, team sharing

### JSON (Best for data processing)

Structured data including:
- Complete commit details
- Statistical breakdowns
- All metrics in machine-readable format

Perfect for: Further analysis, custom visualizations, integrations

### Text (Best for quick summaries)

Simple, plain-text format suitable for:
- Email bodies
- Chat messages
- Quick console output

## Generated Metrics

The skill provides comprehensive analytics:

### Summary Metrics
- Total commits
- Files changed (total and unique)
- Lines added/removed/net change
- Time period analyzed
- Repositories included

### Activity Patterns
- Commits by week/month
- File type distribution
- Most frequently modified files
- Commit frequency trends

### Achievement Categories
Automatically categorized from commit messages:
- **Features**: New implementations
- **Fixes**: Bug fixes and issue resolutions
- **Refactoring**: Code improvements and optimization

### Significance Analysis
- Largest commits by lines changed
- Most impactful file modifications
- Key development milestones

## Documentation

- **SKILL.md**: Skill definition and Claude instructions
- **examples.md**: Detailed usage examples and sample outputs
- **reference.md**: In-depth explanation of metrics and best practices
- **README.md**: This file - quick start guide

## Tips for Best Results

1. **Use meaningful commit messages**: Better messages = better achievement extraction
2. **Align time ranges with review periods**: Quarters, months, sprints
3. **Analyze all relevant repositories**: Get complete picture of contributions
4. **Check author matching**: Ensure filter matches your git config
5. **Review largest commits**: May contain generated code or bulk changes

## Troubleshooting

### "No commits found"
- Check author filter matches `git config user.name` or `user.email`
- Verify date range includes commits: `git log --since=YYYY-MM-DD --until=YYYY-MM-DD`

### "Not a git repository"
- Ensure path contains `.git` folder
- Use absolute paths or verify current directory

### "Date parsing error"
- Use YYYY-MM-DD format for dates
- Check that start date is before end date

### Script permissions
- Ensure script is executable: `chmod +x scripts/analyze_work.py`
- Verify Python 3.8+ is installed: `python3 --version`

## Privacy and Security

The script:
- ✅ Analyzes only commit metadata (messages, stats, dates)
- ✅ Runs entirely locally on your machine
- ✅ Requires only read access to git repositories
- ❌ Does NOT read file contents or code
- ❌ Does NOT send data to external services
- ❌ Does NOT require network access

## Limitations

- Merge commits are excluded by default
- Cannot distinguish generated code from manual code
- Accuracy depends on commit message quality
- Pair programming shows only one author
- Large repositories may take time to process

## Future Enhancements

Potential additions:
- Visual charts and graphs
- Code complexity metrics
- Collaboration analysis (co-authors)
- Path filtering (analyze specific directories)
- Automated insight generation
- Integration with issue trackers

## Contributing

To extend or improve this skill:

1. Modify `scripts/analyze_work.py` for new features
2. Update `SKILL.md` with new instructions for Claude
3. Add examples to `examples.md`
4. Document changes in `reference.md`

## License

This skill is part of the jcommon/skills collection.

## Support

For issues or questions:
1. Check `reference.md` for detailed documentation
2. Review `examples.md` for usage patterns
3. Examine the Python script source code for technical details

---

**Quick Start**: Just ask Claude to "generate my work summary" and let the skill handle the rest!
