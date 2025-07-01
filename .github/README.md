# GitHub Actions CI/CD and Dependency Management

This repository includes comprehensive GitHub Actions workflows for automated dependency management, continuous integration, security scanning, and releases across all language implementations.

## 🔄 Automated Dependency Updates

### Dependabot Configuration

- **File**: `.github/dependabot.yml`
- **Schedule**: Weekly updates on Sundays at 2:00 AM UTC
- **Languages**: Python, Node.js, Rust, Java, Kotlin, C#, Swift, GitHub Actions
- **Auto-labeling**: All dependency PRs are labeled with `dependencies`, language-specific labels, and `auto-merge`

### Custom Dependency Update Workflow

- **File**: `.github/workflows/dependency-updates.yml`
- **Trigger**: Weekly schedule + manual dispatch
- **Features**:
  - Updates dependencies for all language implementations
  - Creates separate PRs for each language
  - Automatic labeling for auto-merge eligibility

## 🚀 Continuous Integration

### CI Pipeline

- **File**: `.github/workflows/ci.yml`
- **Triggers**: Push to main/develop, Pull requests
- **Coverage**: Tests and builds for all 7 language implementations
- **Languages**:
  - **Python**: pytest with coverage
  - **Node.js/TypeScript**: npm test + build
  - **Rust**: cargo test + build
  - **Java**: Maven test + compile
  - **Kotlin**: Gradle test + build
  - **C#**: dotnet test + build
  - **Swift**: swift test + build

## 🤖 Auto-Merge System

### Auto-Merge Workflow

- **File**: `.github/workflows/auto-merge.yml`
- **Triggers**: PR events, check suite completion
- **Conditions for auto-merge**:
  - PR has `auto-merge` and `dependencies` labels
  - All CI checks pass
  - PR is not a draft
  - PR is mergeable
  - From dependency update branch

### How Auto-Merge Works

1. Dependency update PR is created (Dependabot or custom workflow)
2. CI tests run automatically
3. If all tests pass, PR is auto-approved
4. PR is automatically merged with squash commit

## 🔒 Security Scanning

### Security Workflow

- **File**: `.github/workflows/security.yml`
- **Schedule**: Weekly on Mondays at 3:00 AM UTC
- **Tools**:
  - **Python**: Safety + Bandit
  - **Node.js**: npm audit
  - **Rust**: cargo audit
  - **Java**: OWASP Dependency Check
  - **All languages**: CodeQL analysis

## 📦 Release Management

### Release Workflow

- **File**: `.github/workflows/release.yml`
- **Triggers**: Git tags (v\*) or manual dispatch
- **Artifacts**: Builds and packages for all languages
- **Distribution**: GitHub Releases with cross-platform binaries

## 🏷️ Labels and Organization

### Automatic Labels

- `dependencies`: All dependency-related PRs
- `auto-merge`: PRs eligible for automatic merging
- Language-specific: `python`, `nodejs`, `rust`, `java`, `kotlin`, `csharp`, `swift`
- `github-actions`: GitHub Actions workflow updates

## 📋 Setup Instructions

### 1. Repository Settings

Ensure the following repository settings are configured:

#### Branch Protection Rules (main branch)

```
✅ Require status checks to pass before merging
✅ Require branches to be up to date before merging
✅ Require review from code owners
✅ Dismiss stale PR approvals when new commits are pushed
✅ Restrict pushes that create files larger than 100MB
```

#### Required Status Checks

- `test-python`
- `test-nodejs`
- `test-rust`
- `test-java`
- `test-kotlin`
- `test-csharp`
- `test-swift`

### 2. Secrets Configuration

No additional secrets required - uses default `GITHUB_TOKEN` with appropriate permissions.

### 3. Enable Dependabot

Dependabot is configured via `.github/dependabot.yml` and will automatically start creating PRs.

## 🔧 Workflow Permissions

Each workflow has minimal required permissions:

- **dependency-updates.yml**: `contents: write`, `pull-requests: write`
- **ci.yml**: `contents: read` (default)
- **auto-merge.yml**: `contents: write`, `pull-requests: write`, `checks: read`
- **security.yml**: `security-events: write`, `contents: read`
- **release.yml**: `contents: write`, `packages: write`

## 📊 Monitoring and Notifications

### Success Indicators

- ✅ Weekly dependency PRs created and auto-merged
- ✅ All CI checks passing
- ✅ Security scans running without critical issues
- ✅ Releases built successfully for all platforms

### Troubleshooting

If auto-merge isn't working:

1. Check that PRs have correct labels (`auto-merge` + `dependencies`)
2. Verify all CI checks are passing
3. Ensure branch protection rules allow auto-merge
4. Check workflow run logs for specific errors

## 🎯 Customization

### Adjusting Update Frequency

Edit the `cron` schedule in:

- `.github/dependabot.yml` (Dependabot)
- `.github/workflows/dependency-updates.yml` (Custom updates)
- `.github/workflows/security.yml` (Security scans)

### Adding New Languages

1. Add dependency management in `dependabot.yml`
2. Add build/test job in `ci.yml`
3. Add security scanning in `security.yml`
4. Add build artifacts in `release.yml`

### Disabling Auto-Merge

Remove the `auto-merge` label from Dependabot configuration or modify the auto-merge workflow conditions.

## 📈 Benefits

- **🚀 Faster Development**: Automated dependency updates reduce manual maintenance
- **🔒 Enhanced Security**: Regular updates and security scanning
- **🤖 Reduced Toil**: Auto-merge eliminates manual PR reviews for dependencies
- **🌍 Multi-Language Support**: Consistent process across all implementations
- **📊 Visibility**: Clear labeling and consistent commit messages
- **🛡️ Safety**: Comprehensive testing before any automatic changes
