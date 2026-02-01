# ModularIoT Coordinator

Alfresco Content Services (ACS) extension module for ModularIoT coordination.

[![Build and Publish](https://github.com/microboxlabs/miot-coordinator/actions/workflows/ci.yaml/badge.svg)](https://github.com/microboxlabs/miot-coordinator/actions/workflows/ci.yaml)
[![Maven Central](https://img.shields.io/maven-central/v/com.microboxlabs/miot-coordinator.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.microboxlabs/miot-coordinator)

## Installation

### Maven Dependency

Add the dependency to your `pom.xml`:

```xml
<dependency>
  <groupId>com.microboxlabs</groupId>
  <artifactId>miot-coordinator</artifactId>
  <version>1.0.0</version>
</dependency>
```

No additional repository configuration needed - artifacts are published to Maven Central.

### Gradle

```groovy
implementation 'com.microboxlabs:miot-coordinator:1.0.0'
```

---

## Docker Images

Docker images are published to:

- **GitHub Container Registry (Primary)**: `ghcr.io/microboxlabs/miot-coordinator`
- **Docker Hub (Mirror)**: `docker.io/microboxlabs/miot-coordinator`

### Pull the Image

```bash
# From GitHub Container Registry (recommended)
docker pull ghcr.io/microboxlabs/miot-coordinator:latest

# From Docker Hub
docker pull microboxlabs/miot-coordinator:latest
```

### Available Tags

| Tag Pattern | Description | Example |
|-------------|-------------|---------|
| `latest` | Latest stable release from main/trunk branch | `ghcr.io/microboxlabs/miot-coordinator:latest` |
| `1.0.0` | Semantic version releases | `ghcr.io/microboxlabs/miot-coordinator:1.0.0` |
| `sha-*` | Specific commit SHA | `ghcr.io/microboxlabs/miot-coordinator:sha-abc1234` |
| `pr-*` | Pull request builds (GHCR only) | `ghcr.io/microboxlabs/miot-coordinator:pr-42` |

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

The CI/CD pipeline automatically:
- **Builds and tests** on every push and pull request
- **Publishes Maven artifacts** to Maven Central (on release tags only)
- **Publishes Docker images** to GHCR and Docker Hub (on push to main/trunk and tags)

### Required Repository Secrets

| Secret | Required For | Description |
|--------|--------------|-------------|
| `DOCKERHUB_USERNAME` | Docker Hub | Your Docker Hub username |
| `DOCKERHUB_TOKEN` | Docker Hub | Docker Hub access token |
| `SONATYPE_USERNAME` | Maven Central | Sonatype Central portal username or token |
| `SONATYPE_PASSWORD` | Maven Central | Sonatype Central portal password or token |
| `GPG_PRIVATE_KEY` | Maven Central | ASCII-armored GPG private key for signing |
| `GPG_PASSPHRASE` | Maven Central | GPG key passphrase |

### Setting Up Secrets

1. Go to your repository on GitHub
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add each secret from the table above

### GPG Key Setup

To export your GPG private key for GitHub Actions:

```bash
# List your keys
gpg --list-secret-keys --keyid-format LONG

# Export the private key (ASCII-armored)
gpg --armor --export-secret-keys YOUR_KEY_ID > private-key.asc

# Copy the contents of private-key.asc to GPG_PRIVATE_KEY secret
cat private-key.asc

# Upload public key to keyserver (required for Maven Central verification)
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
```

### Sonatype Central Token

1. Log in to [central.sonatype.com](https://central.sonatype.com)
2. Go to **Account** → **Generate User Token**
3. Use the generated username as `SONATYPE_USERNAME`
4. Use the generated password as `SONATYPE_PASSWORD`

### Publishing Strategy

The CI workflow automatically publishes based on the Git event:

| Event | Maven Central | Docker Images (GHCR) | Docker Images (Docker Hub) |
|-------|---------------|----------------------|---------------------------|
| Pull Request | *(not published)* | `pr-<number>`, `sha-<sha>` | *(not published)* |
| Push to trunk/main | *(not published)* | `latest`, `sha-<sha>` | `latest`, `sha-<sha>` |
| Tag `v1.2.3` | `1.2.3` | `1.2.3`, `1.2`, `1`, `sha-<sha>` | `1.2.3`, `1.2`, `1`, `sha-<sha>` |

### Creating a Release

```bash
# Tag a release
git tag v1.0.0
git push origin v1.0.0
```

This will:
1. Build and test the project
2. Publish JAR, sources, and javadoc to Maven Central
3. Build and push Docker images to GHCR and Docker Hub

### Multi-Architecture Support

Images are built for both `linux/amd64` and `linux/arm64` architectures, supporting:

- Standard x86_64 servers and desktops
- Apple Silicon Macs (M1/M2/M3)
- ARM-based cloud instances (AWS Graviton, etc.)

---

## License

Apache-2.0
