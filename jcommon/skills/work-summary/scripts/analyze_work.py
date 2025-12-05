#!/usr/bin/env python3
"""
Work Summary Analyzer - Git Repository Analysis Tool
Analyzes git commits and generates performance review summaries
"""

import argparse
import json
import os
import sys
from datetime import datetime, timedelta
from collections import defaultdict, Counter
from typing import List, Dict, Any, Optional
import subprocess
import re


class GitAnalyzer:
    """Analyzes git repositories for work summary generation"""

    def __init__(self, repo_paths: List[str], start_date: str, end_date: str,
                 author: Optional[str] = None):
        self.repo_paths = repo_paths
        self.start_date = self._parse_date(start_date)
        self.end_date = self._parse_date(end_date)
        self.author = author

    def _parse_date(self, date_str: str) -> datetime:
        """Parse date string in various formats"""
        # Try YYYY-MM-DD format
        try:
            return datetime.strptime(date_str, '%Y-%m-%d')
        except ValueError:
            pass

        # Try relative dates
        if 'last' in date_str.lower():
            if 'month' in date_str:
                months = int(re.search(r'\d+', date_str).group())
                return datetime.now() - timedelta(days=months * 30)
            elif 'week' in date_str:
                weeks = int(re.search(r'\d+', date_str).group())
                return datetime.now() - timedelta(weeks=weeks)
            elif 'day' in date_str:
                days = int(re.search(r'\d+', date_str).group())
                return datetime.now() - timedelta(days=days)

        raise ValueError(f"Cannot parse date: {date_str}")

    def _run_git_command(self, repo_path: str, command: List[str]) -> str:
        """Execute git command in repository"""
        try:
            result = subprocess.run(
                ['git', '-C', repo_path] + command,
                capture_output=True,
                text=True,
                check=True
            )
            return result.stdout.strip()
        except subprocess.CalledProcessError as e:
            print(f"Git command failed in {repo_path}: {e.stderr}", file=sys.stderr)
            return ""

    def _get_author_name(self, repo_path: str) -> str:
        """Get git author name from config if not specified"""
        if self.author:
            return self.author

        name = self._run_git_command(repo_path, ['config', 'user.name'])
        email = self._run_git_command(repo_path, ['config', 'user.email'])
        return name or email or "Unknown"

    def analyze(self) -> Dict[str, Any]:
        """Main analysis method"""
        all_commits = []
        repo_stats = {}

        for repo_path in self.repo_paths:
            if not os.path.exists(os.path.join(repo_path, '.git')):
                print(f"Warning: {repo_path} is not a git repository", file=sys.stderr)
                continue

            repo_name = os.path.basename(os.path.abspath(repo_path))
            commits = self._analyze_repository(repo_path, repo_name)
            all_commits.extend(commits)
            repo_stats[repo_name] = len(commits)

        # Sort commits by date
        all_commits.sort(key=lambda x: x['date'], reverse=True)

        # Generate statistics
        statistics = self._generate_statistics(all_commits)

        # Extract achievements
        achievements = self._extract_achievements(all_commits)

        # Generate summary
        summary = self._generate_summary(all_commits, repo_stats)

        return {
            'summary': summary,
            'commits': all_commits,
            'statistics': statistics,
            'achievements': achievements
        }

    def _analyze_repository(self, repo_path: str, repo_name: str) -> List[Dict[str, Any]]:
        """Analyze single repository"""
        author = self._get_author_name(repo_path)

        # Build git log command
        git_log_format = '--pretty=format:%H|%ai|%an|%ae|%s'
        git_log_cmd = [
            'log',
            git_log_format,
            f'--since={self.start_date.strftime("%Y-%m-%d")}',
            f'--until={self.end_date.strftime("%Y-%m-%d")}',
            '--no-merges'
        ]

        if self.author:
            git_log_cmd.append(f'--author={self.author}')

        log_output = self._run_git_command(repo_path, git_log_cmd)

        if not log_output:
            return []

        commits = []
        for line in log_output.split('\n'):
            if not line:
                continue

            parts = line.split('|')
            if len(parts) < 5:
                continue

            commit_hash = parts[0]
            commit_date = parts[1]
            commit_author = parts[2]
            commit_email = parts[3]
            commit_message = '|'.join(parts[4:])  # Message might contain |

            # Get commit stats
            stat_output = self._run_git_command(
                repo_path,
                ['show', '--stat', '--format=', commit_hash]
            )

            files_changed = []
            insertions = 0
            deletions = 0

            for stat_line in stat_output.split('\n'):
                if '|' in stat_line:
                    file_path = stat_line.split('|')[0].strip()
                    files_changed.append(file_path)
                elif 'insertion' in stat_line or 'deletion' in stat_line:
                    # Parse summary line: "3 files changed, 120 insertions(+), 30 deletions(-)"
                    insertions_match = re.search(r'(\d+) insertion', stat_line)
                    deletions_match = re.search(r'(\d+) deletion', stat_line)
                    if insertions_match:
                        insertions = int(insertions_match.group(1))
                    if deletions_match:
                        deletions = int(deletions_match.group(1))

            commits.append({
                'hash': commit_hash[:8],
                'date': commit_date,
                'author': commit_author,
                'email': commit_email,
                'message': commit_message,
                'repository': repo_name,
                'files_changed': len(files_changed),
                'files': files_changed,
                'insertions': insertions,
                'deletions': deletions,
                'net_lines': insertions - deletions
            })

        return commits

    def _generate_statistics(self, commits: List[Dict[str, Any]]) -> Dict[str, Any]:
        """Generate statistical analysis"""
        if not commits:
            return {}

        # Commits by week
        commits_by_week = defaultdict(int)
        commits_by_month = defaultdict(int)

        # File extensions
        file_extensions = Counter()

        # Most modified files
        file_modifications = Counter()

        for commit in commits:
            # Parse date
            commit_date = datetime.fromisoformat(commit['date'].replace(' ', 'T', 1).rsplit(' ', 1)[0])

            # Week key: YYYY-WW
            week_key = f"{commit_date.year}-W{commit_date.isocalendar()[1]:02d}"
            commits_by_week[week_key] += 1

            # Month key: YYYY-MM
            month_key = f"{commit_date.year}-{commit_date.month:02d}"
            commits_by_month[month_key] += 1

            # File extensions and modifications
            for file_path in commit['files']:
                file_modifications[file_path] += 1

                ext = os.path.splitext(file_path)[1]
                if ext:
                    file_extensions[ext] += 1
                else:
                    file_extensions['[no extension]'] += 1

        # Largest commits
        largest_commits = sorted(
            commits,
            key=lambda x: x['insertions'] + x['deletions'],
            reverse=True
        )[:10]

        return {
            'commits_by_week': dict(sorted(commits_by_week.items())),
            'commits_by_month': dict(sorted(commits_by_month.items())),
            'files_by_extension': dict(file_extensions.most_common(10)),
            'most_modified_files': dict(file_modifications.most_common(10)),
            'largest_commits': [
                {
                    'hash': c['hash'],
                    'message': c['message'],
                    'lines_changed': c['insertions'] + c['deletions'],
                    'date': c['date']
                }
                for c in largest_commits
            ]
        }

    def _extract_achievements(self, commits: List[Dict[str, Any]]) -> List[str]:
        """Extract key achievements from commit messages"""
        achievements = []

        # Group commits by similar topics using keywords
        feature_keywords = ['add', 'implement', 'create', 'new', 'feature']
        fix_keywords = ['fix', 'bug', 'issue', 'resolve', 'patch']
        refactor_keywords = ['refactor', 'improve', 'optimize', 'clean', 'update']

        feature_commits = []
        fix_commits = []
        refactor_commits = []

        for commit in commits:
            message_lower = commit['message'].lower()

            if any(kw in message_lower for kw in feature_keywords):
                feature_commits.append(commit)
            elif any(kw in message_lower for kw in fix_keywords):
                fix_commits.append(commit)
            elif any(kw in message_lower for kw in refactor_keywords):
                refactor_commits.append(commit)

        # Generate achievement summaries
        if feature_commits:
            total_lines = sum(c['insertions'] for c in feature_commits)
            achievements.append(
                f"Implemented new features across {len(feature_commits)} commits "
                f"({total_lines:,} lines added)"
            )

        if fix_commits:
            achievements.append(
                f"Fixed bugs and resolved issues ({len(fix_commits)} commits)"
            )

        if refactor_commits:
            total_files = sum(c['files_changed'] for c in refactor_commits)
            achievements.append(
                f"Code improvements and refactoring "
                f"({len(refactor_commits)} commits, {total_files} files)"
            )

        # Add significant individual commits
        significant_commits = [
            c for c in commits
            if c['insertions'] + c['deletions'] > 500 or c['files_changed'] > 10
        ]

        for commit in significant_commits[:5]:
            achievements.append(
                f"{commit['message'][:60]}... "
                f"({commit['files_changed']} files, "
                f"+{commit['insertions']}/-{commit['deletions']} lines)"
            )

        return achievements

    def _generate_summary(self, commits: List[Dict[str, Any]],
                         repo_stats: Dict[str, int]) -> Dict[str, Any]:
        """Generate overall summary statistics"""
        if not commits:
            return {
                'time_range': {
                    'start': self.start_date.strftime('%Y-%m-%d'),
                    'end': self.end_date.strftime('%Y-%m-%d')
                },
                'total_commits': 0,
                'total_files_changed': 0,
                'total_insertions': 0,
                'total_deletions': 0,
                'repositories': list(repo_stats.keys())
            }

        total_files_changed = sum(c['files_changed'] for c in commits)
        total_insertions = sum(c['insertions'] for c in commits)
        total_deletions = sum(c['deletions'] for c in commits)

        # Unique files touched
        unique_files = set()
        for commit in commits:
            unique_files.update(commit['files'])

        return {
            'time_range': {
                'start': self.start_date.strftime('%Y-%m-%d'),
                'end': self.end_date.strftime('%Y-%m-%d')
            },
            'total_commits': len(commits),
            'total_files_changed': total_files_changed,
            'unique_files_touched': len(unique_files),
            'total_insertions': total_insertions,
            'total_deletions': total_deletions,
            'net_lines': total_insertions - total_deletions,
            'repositories': list(repo_stats.keys()),
            'commits_per_repository': repo_stats
        }


def format_markdown(data: Dict[str, Any]) -> str:
    """Format analysis data as Markdown"""
    summary = data['summary']
    commits = data['commits']
    stats = data['statistics']
    achievements = data['achievements']

    md = []

    # Header
    md.append("# Work Summary Report\n")
    md.append(f"**Period**: {summary['time_range']['start']} to {summary['time_range']['end']}\n")
    md.append(f"**Generated**: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}\n")

    # Overview
    md.append("## Overview\n")
    md.append(f"- **Total Commits**: {summary['total_commits']}")
    md.append(f"- **Files Changed**: {summary['total_files_changed']} changes across {summary['unique_files_touched']} unique files")
    md.append(f"- **Code Changes**: +{summary['total_insertions']:,} / -{summary['total_deletions']:,} lines (net: {summary['net_lines']:+,})")
    md.append(f"- **Repositories**: {', '.join(summary['repositories'])}\n")

    # Key Achievements
    if achievements:
        md.append("## Key Achievements\n")
        for achievement in achievements:
            md.append(f"- {achievement}")
        md.append("")

    # Activity Statistics
    md.append("## Activity Statistics\n")

    if stats.get('commits_by_month'):
        md.append("### Commits by Month\n")
        for month, count in stats['commits_by_month'].items():
            md.append(f"- **{month}**: {count} commits")
        md.append("")

    if stats.get('files_by_extension'):
        md.append("### File Types Modified\n")
        for ext, count in stats['files_by_extension'].items():
            md.append(f"- **{ext}**: {count} files")
        md.append("")

    if stats.get('most_modified_files'):
        md.append("### Most Modified Files\n")
        for file_path, count in list(stats['most_modified_files'].items())[:10]:
            md.append(f"- `{file_path}`: {count} modifications")
        md.append("")

    # Largest Commits
    if stats.get('largest_commits'):
        md.append("## Significant Commits\n")
        for commit in stats['largest_commits'][:5]:
            md.append(f"### {commit['hash']} - {commit['message'][:80]}")
            md.append(f"- **Date**: {commit['date'].split()[0]}")
            md.append(f"- **Lines Changed**: {commit['lines_changed']:,}")
            md.append("")

    # Recent Commits Timeline
    md.append("## Recent Activity\n")
    for commit in commits[:20]:
        date = commit['date'].split()[0]
        md.append(f"- **{date}** [{commit['hash']}] {commit['message']}")
        md.append(f"  - {commit['files_changed']} files, +{commit['insertions']}/-{commit['deletions']} lines")

    if len(commits) > 20:
        md.append(f"\n*... and {len(commits) - 20} more commits*\n")

    return '\n'.join(md)


def format_text(data: Dict[str, Any]) -> str:
    """Format analysis data as plain text"""
    # Simple text version
    summary = data['summary']
    achievements = data['achievements']

    text = []
    text.append("=" * 60)
    text.append("WORK SUMMARY REPORT")
    text.append("=" * 60)
    text.append(f"Period: {summary['time_range']['start']} to {summary['time_range']['end']}")
    text.append("")
    text.append(f"Total Commits: {summary['total_commits']}")
    text.append(f"Files Changed: {summary['total_files_changed']}")
    text.append(f"Lines Added: +{summary['total_insertions']:,}")
    text.append(f"Lines Deleted: -{summary['total_deletions']:,}")
    text.append(f"Repositories: {', '.join(summary['repositories'])}")
    text.append("")
    text.append("KEY ACHIEVEMENTS:")
    text.append("-" * 60)
    for achievement in achievements:
        text.append(f"• {achievement}")
    text.append("=" * 60)

    return '\n'.join(text)


def main():
    parser = argparse.ArgumentParser(
        description='Analyze git repositories for work summary and performance reviews'
    )
    parser.add_argument(
        '--repo',
        nargs='+',
        required=True,
        help='Path(s) to git repository/repositories'
    )
    parser.add_argument(
        '--start-date',
        required=True,
        help='Start date (YYYY-MM-DD or "last N months/weeks/days")'
    )
    parser.add_argument(
        '--end-date',
        required=True,
        help='End date (YYYY-MM-DD)'
    )
    parser.add_argument(
        '--author',
        help='Filter by git author name or email (optional)'
    )
    parser.add_argument(
        '--output',
        required=True,
        help='Output file path for JSON results'
    )
    parser.add_argument(
        '--format',
        choices=['json', 'markdown', 'text'],
        default='json',
        help='Output format (default: json)'
    )

    args = parser.parse_args()

    # Analyze repositories
    analyzer = GitAnalyzer(
        repo_paths=args.repo,
        start_date=args.start_date,
        end_date=args.end_date,
        author=args.author
    )

    try:
        results = analyzer.analyze()

        # Write output based on format
        if args.format == 'markdown':
            output_content = format_markdown(results)
            output_file = args.output.replace('.json', '.md')
        elif args.format == 'text':
            output_content = format_text(results)
            output_file = args.output.replace('.json', '.txt')
        else:
            output_content = json.dumps(results, indent=2, ensure_ascii=False)
            output_file = args.output

        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(output_content)

        print(f"Analysis complete. Results written to: {output_file}")

        # Print summary to stdout
        summary = results['summary']
        print(f"\nSummary: {summary['total_commits']} commits, "
              f"{summary['unique_files_touched']} files, "
              f"+{summary['total_insertions']:,}/-{summary['total_deletions']:,} lines")

    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


if __name__ == '__main__':
    main()
