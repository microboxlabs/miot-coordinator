# ModularIoT Coordinator

Alfresco Content Services (ACS) extension module for ModularIoT Coordinator.

[![Build and Publish](https://github.com/microboxlabs/ecm-coordinator/actions/workflows/ci.yaml/badge.svg)](https://github.com/microboxlabs/ecm-coordinator/actions/workflows/ci.yaml)

## Docker Images

Docker images are published to:

- **GitHub Container Registry (Primary)**: `ghcr.io/microboxlabs/ecm-coordinator`
- **Docker Hub (Mirror)**: `docker.io/microboxlabs/ecm-coordinator`

### Pull the Image

```bash
# From GitHub Container Registry (recommended)
docker pull ghcr.io/microboxlabs/ecm-coordinator:latest

# From Docker Hub
docker pull microboxlabs/ecm-coordinator:latest
```

### Available Tags

| Tag Pattern | Description | Example |
|-------------|-------------|---------|
| `latest` | Latest stable release from main/trunk branch | `ghcr.io/microboxlabs/ecm-coordinator:latest` |
| `v*.*.*` | Semantic version releases | `ghcr.io/microboxlabs/ecm-coordinator:1.0.0` |
| `sha-*` | Specific commit SHA | `ghcr.io/microboxlabs/ecm-coordinator:sha-abc1234` |
| `pr-*` | Pull request builds (GHCR only) | `ghcr.io/microboxlabs/ecm-coordinator:pr-42` |

---

## Development

This is an ACS project for Alfresco SDK 4.11.0.

### Quick Start

Run with `./run.sh build_start` or `./run.bat build_start` and verify that it:

- Runs Alfresco Content Service (ACS)
- (Optional) Runs Alfresco Share
- Runs Alfresco Search Service (ASS)
- Runs PostgreSQL database
- Deploys the JAR assembled module

### Available Commands

All services run as Docker containers. The run script offers these tasks:

| Command | Description |
|---------|-------------|
| `build_start` | Build the whole project, recreate the ACS docker image, start the dockerised environment and tail logs |
| `build_start_it_supported` | Build with IT dependencies, recreate ACS image, start environment and tail logs |
| `start` | Start the dockerised environment without building and tail logs |
| `stop` | Stop the dockerised environment |
| `purge` | Stop containers and delete all persistent data (docker volumes) |
| `tail` | Tail the logs of all containers |
| `reload_acs` | Build the ACS module, recreate the ACS docker image and restart the ACS container |
| `build_test` | Build, start environment, execute integration tests and stop |
| `test` | Execute integration tests (environment must be already started) |

### Project Structure

- No parent pom
- No WAR projects - JARs are included in custom docker images
- No runner project - Alfresco environment is managed through Docker
- Standard JAR packaging and layout
- Works seamlessly with Eclipse and IntelliJ IDEA
- JRebel for hot reloading
- AMP as an assembly
- Persistent test data through Docker volumes
- Resources loaded from META-INF
- Web Fragment support (includes sample servlet)

---

## CI/CD Setup

### Required Repository Secrets

To enable Docker Hub publishing, configure these secrets in your GitHub repository:

| Secret | Description | How to Get |
|--------|-------------|------------|
| `DOCKERHUB_USERNAME` | Your Docker Hub username | Your Docker Hub account username |
| `DOCKERHUB_TOKEN` | Docker Hub access token | [Create at Docker Hub](https://hub.docker.com/settings/security) → New Access Token |

> **Note**: GHCR authentication uses the built-in `GITHUB_TOKEN` - no additional secrets needed.

### Setting Up Secrets

1. Go to your repository on GitHub
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add `DOCKERHUB_USERNAME` with your Docker Hub username
5. Add `DOCKERHUB_TOKEN` with your Docker Hub access token

### Making GHCR Packages Public

By default, GHCR packages inherit repository visibility. To make them public:

1. Go to your GitHub profile/organization
2. Navigate to **Packages**
3. Select the `ecm-coordinator` package
4. Go to **Package settings**
5. Scroll to **Danger Zone** → **Change package visibility**
6. Select **Public** and confirm

Alternatively, add this to your workflow (already included):

```yaml
permissions:
  packages: write
```

And ensure your repository is public, or configure package visibility via the GitHub API.

### Tagging Strategy

The CI workflow automatically generates tags based on the Git event:

| Event | GHCR Tags | Docker Hub Tags |
|-------|-----------|-----------------|
| Pull Request | `pr-<number>`, `sha-<sha>` | *(not published)* |
| Push to trunk/main | `latest`, `sha-<sha>` | `latest`, `sha-<sha>` |
| Tag `v1.2.3` | `1.2.3`, `1.2`, `1`, `sha-<sha>` | `1.2.3`, `1.2`, `1`, `sha-<sha>` |

### Multi-Architecture Support

Images are built for both `linux/amd64` and `linux/arm64` architectures, supporting:

- Standard x86_64 servers and desktops
- Apple Silicon Macs (M1/M2/M3)
- ARM-based cloud instances (AWS Graviton, etc.)

---

## License

Apache-2.0
