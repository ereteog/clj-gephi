# GitHub Actions Workflows

This directory contains GitHub Actions workflows for continuous integration and deployment.

## CI Workflow (`ci.yml`)

The main CI workflow runs on every push and pull request to the main branches.

### Jobs

#### 1. Test
- **Matrix Strategy**: Tests on Java 11 and 17 (LTS versions)
- **Steps**:
  - Checkout code
  - Setup Java environment
  - Install Leiningen
  - Cache Maven and Leiningen dependencies
  - Fetch dependencies
  - Run test suite (`lein test`)
  - Check code compilation (`lein check`)

#### 2. Lint
- **Code Quality Checks**:
  - Verify idiomatic namespace declarations (no old-style `(require ...)`)
  - Check for trailing whitespace
  - Runs on Java 11

#### 3. Build
- **Artifact Creation**:
  - Builds JAR file
  - Uploads artifact for 30 days
  - Only runs if tests and linting pass
  - Uses Java 11

### Caching

The workflow caches:
- Maven dependencies (`~/.m2/repository`)
- Leiningen dependencies (`~/.lein`)

This significantly speeds up subsequent builds.

### Triggers

The workflow runs on:
- Push to `main`, `master`, or `develop` branches
- Pull requests targeting `main`, `master`, or `develop` branches

### Build Status

View the build status: [![CI](https://github.com/ereteog/clj-gephi/workflows/CI/badge.svg)](https://github.com/ereteog/clj-gephi/actions)

## Local Testing

To run the same checks locally:

```bash
# Run tests
lein test

# Check compilation
lein check

# Build JAR
lein jar

# Check for old-style namespace declarations
grep -r "^(require " src/ || echo "✓ No old-style requires"
grep -r "^(import " src/ || echo "✓ No old-style imports"

# Check for trailing whitespace
grep -r '[[:blank:]]$' src/ test/ --exclude-dir=target || echo "✓ No trailing whitespace"
```

## Modifying Workflows

When modifying workflows:
1. Test changes in a feature branch
2. Verify the workflow runs successfully
3. Check all jobs pass before merging
4. Use [act](https://github.com/nektos/act) for local testing (optional)
