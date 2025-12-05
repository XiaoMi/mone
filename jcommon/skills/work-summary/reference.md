# Work Summary Skill - Reference Documentation

## Metrics Explained

### Commit Metrics

#### Total Commits
The number of individual commits made during the time period. Excludes merge commits by default to focus on actual development work.

#### Files Changed
Total count of file modifications across all commits. A single file modified in multiple commits is counted multiple times to represent development effort.

#### Unique Files Touched
The number of distinct files that were modified at least once during the period. Useful for understanding the breadth of work across the codebase.

#### Lines Added (Insertions)
Total number of lines added across all commits. Includes:
- New code
- New comments and documentation
- New configuration
- Whitespace changes (may inflate numbers)

#### Lines Deleted (Deletions)
Total number of lines removed across all commits. Includes:
- Removed code
- Deleted comments
- Removed configuration
- Refactored code that was replaced

#### Net Lines
The difference between insertions and deletions (`insertions - deletions`).
- Positive number: Codebase grew
- Negative number: Codebase shrunk (often due to refactoring or code cleanup)
- Near zero: Significant refactoring or modifications without major growth

### Activity Patterns

#### Commits by Week/Month
Distribution of commits over time. Useful for:
- Identifying busy vs. quiet periods
- Showing consistent contribution patterns
- Highlighting sprint cycles or project phases

#### Files by Extension
Breakdown of file types modified. Indicates:
- Primary programming languages used
- Configuration vs. code work
- Documentation efforts (.md, .txt files)
- Test coverage (.test.js, test_*.py files)

#### Most Modified Files
Files that were changed most frequently. May indicate:
- Core areas of development focus
- Files that needed multiple iterations
- Potential refactoring opportunities (if excessively modified)
- Integration points or frequently updated modules

### Achievement Categories

The skill automatically categorizes commits into achievement types based on commit message keywords:

#### Features (New Implementations)
**Keywords**: add, implement, create, new, feature
**Examples**:
- "Add user authentication"
- "Implement payment processing"
- "Create admin dashboard"

#### Bug Fixes
**Keywords**: fix, bug, issue, resolve, patch
**Examples**:
- "Fix race condition in cache"
- "Resolve login timeout issue"
- "Patch security vulnerability"

#### Refactoring/Improvements
**Keywords**: refactor, improve, optimize, clean, update
**Examples**:
- "Refactor database queries"
- "Optimize image loading"
- "Update dependencies"

### Commit Significance

#### Largest Commits
Commits ranked by total lines changed (insertions + deletions). Large commits may indicate:
- Major feature implementations
- Large-scale refactoring
- Database migrations
- Generated code or asset files (be cautious)

**Interpretation**:
- 500+ lines: Major work, significant feature or refactoring
- 100-500 lines: Moderate feature or multiple related changes
- <100 lines: Small feature, bug fix, or minor improvement

## Understanding the Data

### What Makes a Good Commit Message?

For best results with achievement extraction, use clear commit messages:

✅ **Good Examples**:
- "Implement OAuth2 authentication flow"
- "Fix memory leak in image processing"
- "Refactor API error handling for better UX"
- "Add comprehensive unit tests for payment module"

❌ **Poor Examples**:
- "updates"
- "fix stuff"
- "WIP"
- "asdfasdf"

### Code Churn vs. Value

**High Lines Changed ≠ High Value**

Consider context:
- **High insertions + High deletions** = Likely refactoring or rewriting
- **High insertions + Low deletions** = New features or expansion
- **Low insertions + High deletions** = Code cleanup or feature removal
- **Many files + Few lines each** = Configuration or small fixes across codebase
- **Few files + Many lines** = Focused feature development

### Time Distribution Analysis

**Consistent Pattern** (even commits throughout):
- Steady development pace
- Good sprint planning
- Sustainable workload

**Burst Pattern** (clusters of commits):
- Sprint deadlines
- Feature completion pushes
- Bug fix cycles

**Declining Pattern** (fewer commits over time):
- Project winding down
- Maintenance phase
- Need for re-prioritization

## Git Command Details

The script uses these git commands internally:

### Main Analysis Command
```bash
git log \
  --pretty=format:'%H|%ai|%an|%ae|%s' \
  --since=YYYY-MM-DD \
  --until=YYYY-MM-DD \
  --no-merges \
  --author=NAME_OR_EMAIL
```

**Flags explained**:
- `--pretty=format:'%H|%ai|%an|%ae|%s'`: Custom format (hash, date, author, email, subject)
- `--since/--until`: Date range filters
- `--no-merges`: Exclude merge commits
- `--author`: Filter by specific contributor

### Commit Statistics Command
```bash
git show --stat --format= COMMIT_HASH
```

Provides file-level statistics for each commit:
- Files modified
- Lines added/removed per file
- Summary statistics

## Best Practices for Analysis

### 1. Choose Appropriate Time Ranges

**For Performance Reviews**:
- Annual: Full year perspective
- Quarterly: Aligned with business cycles
- Monthly: Detailed recent activity

**For Project Tracking**:
- Sprint duration: 2-4 weeks
- Release cycles: Match your deployment schedule
- Milestone-based: From feature start to completion

### 2. Multi-Repository Analysis

When analyzing multiple repositories:
- Include all related projects
- Use consistent time ranges
- Same author filter across all repos
- Consider monorepo vs. microservices patterns

### 3. Author Filtering

**Important**: Git author matching is exact. Ensure consistency:

```bash
# Check your git identity
git config user.name
git config user.email

# Historical commits may use different emails
git log --format='%an <%ae>' | sort -u
```

Use the author filter that matches your commits:
- Full name: "John Doe"
- Email: "john.doe@company.com"
- Partial: "john" (matches both john.doe and johnny)

### 4. Interpreting Results

**Red Flags** (investigate further):
- Extremely large commits (>1000 lines) - might be generated code
- Very few commits with high line counts - possible batch commits
- Many commits with same message - poor commit hygiene
- All deletions - code removal without replacement

**Positive Indicators**:
- Consistent commit frequency
- Varied file types (code, tests, docs)
- Clear, descriptive commit messages
- Mix of features, fixes, and improvements

## Data Privacy and Security

### What Data is Analyzed?

The script analyzes:
- ✅ Commit metadata (dates, authors, messages)
- ✅ File paths and names
- ✅ Line change statistics
- ❌ **NOT** file contents or code itself
- ❌ **NOT** sensitive data within commits

### Sharing Analysis Results

Be mindful when sharing:
- **Commit messages** may contain internal project names
- **File paths** might reveal architecture details
- **Author emails** are personal information
- Consider sanitizing before sharing externally

### Repository Access

The script requires:
- Read access to git repository
- No write or network access needed
- All analysis is local
- No data sent to external services

## Limitations and Caveats

### 1. Merge Commits Excluded
By default, merge commits are excluded. This means:
- Integration work isn't counted
- Some tooling updates might be missed
- Focus is on direct contributions

### 2. Commit Granularity
Results depend on commit practices:
- **Atomic commits** = More accurate activity tracking
- **Batch commits** = Less detailed timeline
- **Squashed commits** = Lost granularity

### 3. Generated Code
The script can't distinguish:
- Hand-written code vs. generated code
- Copied/pasted code vs. original work
- Refactored code vs. new code

**Tip**: Review "largest commits" for anomalies

### 4. Collaborative Work
Attribution challenges:
- Pair programming: Only one author recorded
- Code reviews: Reviewers not counted
- Team features: Individual contributions blurred

### 5. Time Zone Considerations
Commit times use git's recorded timezone. For distributed teams:
- Timestamps may span multiple timezones
- Activity patterns reflect author's local time
- Date boundaries may vary by timezone

## Advanced Usage

### Custom Date Ranges

The script supports flexible date formats:

```bash
# Exact dates
--start-date 2024-01-01 --end-date 2024-12-31

# Relative dates (parsed by script)
--start-date "last 3 months" --end-date 2024-12-31
```

### Filtering Strategies

**Focus on specific work**:
```bash
# Only Python files
git log --since=... -- "*.py"

# Specific directory
git log --since=... -- src/authentication/

# Exclude tests
git log --since=... -- "*.py" ":(exclude)**/test_*.py"
```

**Note**: Current script version doesn't support git pathspec filters. Consider enhancing the script for these use cases.

## Metric Benchmarks

These are general guidelines, not rules:

### Commit Frequency
- **Active development**: 10-30 commits/month
- **Maintenance**: 5-15 commits/month
- **New features**: 15-40 commits/month

### Lines Changed
- **Small features**: 100-500 lines
- **Medium features**: 500-2000 lines
- **Large features**: 2000+ lines

### Code Churn
- **Healthy**: 20-40% deletions relative to insertions
- **Refactoring heavy**: 60%+ deletions
- **Expansion phase**: <20% deletions

**Remember**: Quality > Quantity. These numbers are context-dependent.

## Extending the Analysis

The Python script can be extended to include:

1. **File Pattern Analysis**: Focus on specific directories or file types
2. **Collaboration Metrics**: Co-authorship analysis
3. **Complexity Metrics**: Integration with code analysis tools
4. **Visualization**: Generate charts and graphs
5. **Automated Insights**: ML-based pattern recognition

See the script source code for extension points.
