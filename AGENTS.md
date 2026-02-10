# AGENTS.md

## Project Overview

MIOT Coordinator is an Alfresco Content Services (ACS) extension module for ModularIoT coordination. It is a Java-based project built with Maven and the Alfresco SDK 4.13.0-A8, packaged as a JAR that gets deployed into a custom ACS Docker image. The module extends Alfresco with custom content models, web scripts, workflows, and Spring-managed components.

**Key technologies:** Java 17, Maven, Alfresco SDK, Spring Framework, Activiti BPMN, FreeMarker templates, Docker, PostgreSQL, Apache SOLR.

**Maven coordinates:** `com.microboxlabs:miot-coordinator`

**Repository:** https://github.com/microboxlabs/miot-coordinator

## Project Structure

```
miot-coordinator/
├── pom.xml                          # Maven build configuration (single-module, no parent POM)
├── run.sh / run.bat                 # Build and Docker environment management scripts
├── docker/
│   └── docker-compose.yml           # Local dev environment (ACS, PostgreSQL, SOLR, ActiveMQ)
├── src/
│   ├── main/
│   │   ├── java/.../platformsample/ # Java source code (components, web scripts)
│   │   ├── docker/                  # Dockerfile and ACS container configuration
│   │   ├── assembly/                # AMP assembly descriptor and web resources
│   │   └── resources/
│   │       ├── META-INF/            # Static web resources (Web Fragment)
│   │       └── alfresco/
│   │           ├── extension/templates/webscripts/  # Web script templates (JS, FTL)
│   │           └── module/miot-coordinator/         # Module definition root
│   │               ├── module.properties            # Module metadata
│   │               ├── module-context.xml           # Spring context root
│   │               ├── context/                     # Spring bean definitions
│   │               ├── model/                       # Custom content and workflow models
│   │               ├── workflow/                    # BPMN 2.0 process definitions
│   │               └── messages/                    # i18n resource bundles
│   └── test/java/.../platformsample/  # Unit and integration tests
└── .github/workflows/ci.yaml         # CI/CD pipeline
```

## Setup Commands

**Prerequisites:** Java 17 (Temurin recommended), Maven 3.x, Docker with Compose.

```bash
# Build the project (compile, run unit tests, package JAR)
mvn clean package

# Build and start the full local environment (ACS + PostgreSQL + SOLR + ActiveMQ)
./run.sh build_start

# Start environment without rebuilding
./run.sh start

# Stop the environment
./run.sh stop

# Stop and delete all persistent Docker volumes (full reset)
./run.sh purge
```

## Development Workflow

The local development environment runs entirely in Docker via `docker-compose.yml`. The `run.sh` script manages the lifecycle.

| Command | Description |
|---------|-------------|
| `./run.sh build_start` | Build, create ACS Docker image, start all containers, tail logs |
| `./run.sh build_start_it_supported` | Same as above but also prepares integration test dependencies |
| `./run.sh start` | Start existing containers and tail logs (no rebuild) |
| `./run.sh stop` | Stop all containers |
| `./run.sh purge` | Stop containers and delete all Docker volumes |
| `./run.sh tail` | Tail logs from all containers |
| `./run.sh build_test` | Full cycle: build, start, run integration tests, stop |
| `./run.sh test` | Run integration tests (environment must already be running) |

**Local service endpoints (when running):**
- ACS: `http://localhost:8080/alfresco`
- SOLR Admin: `http://localhost:8983/solr`
- ActiveMQ Web Console: `http://localhost:8161`
- Remote debug (JDWP): `localhost:8888`

**Hot reloading:** JRebel is configured for classpath hot-reload during development. HotSwap Agent is also supported.

**Web script caching** is disabled in the development Docker image for faster iteration.

## Testing Instructions

### Unit Tests

- Framework: JUnit 4.13.1 + Mockito 4.2.0
- Naming convention: `*Test.java`
- Location: `src/test/java/`

```bash
# Run unit tests only
mvn test
```

### Integration Tests

- Framework: JUnit with Alfresco RAD (AlfrescoTestRunner)
- Naming convention: `*IT.java` or `*ITCase.java` (Maven Failsafe convention)
- Location: `src/test/java/`
- **Requirement:** Integration tests need a running Alfresco environment

```bash
# Run integration tests (environment must be running via ./run.sh start)
./run.sh test
# or
mvn verify

# Skip integration tests during build
mvn verify -DskipITs

# Full automated cycle: build, start env, test, stop
./run.sh build_test
```

Integration tests communicate with the ACS instance over HTTP. The endpoint is configurable via the `test.acs.endpoint.path` Maven property.

**CI note:** Integration tests are skipped in the GitHub Actions CI pipeline because they require a running Alfresco instance. They should be run locally before submitting changes.

## Code Style

- **Language:** Java (JDK 17 target in CI; auto-selects 8/11/17 based on local JDK)
- **Package structure:** `com.microboxlabs.miot.platformsample`
- **Class naming:** PascalCase (`DemoComponent`, `HelloWorldWebScript`)
- **Method/variable naming:** camelCase
- **Test classes:** `<ClassName>Test.java` for unit tests, `<ClassName>IT.java` for integration tests
- **License headers:** Apache License 2.0 on all source files
- **Encoding:** UTF-8 (`project.build.sourceEncoding` and `project.reporting.outputEncoding`)
- **No explicit linter/formatter** is configured; follow existing patterns in the codebase

### Alfresco Conventions

- **Module ID:** `miot-coordinator` (defined in `module.properties`)
- **Spring contexts:** XML-based bean definitions under `src/main/resources/alfresco/module/miot-coordinator/context/`
- **Content models:** XML-based, registered via `bootstrap-context.xml`
- **Web scripts:** Consist of a descriptor (`.desc.xml`), controller (`.java` or `.js`), and template (`.ftl`)
- **Workflows:** Activiti BPMN 2.0 XML, registered in `bootstrap-context.xml`
- **i18n:** Properties files in `messages/` directory, loaded by the bootstrap context

## Build and Deployment

### Build Process

```bash
# Standard build (JAR output in target/)
mvn clean package

# Build for release to Maven Central (requires GPG key and Sonatype credentials)
mvn deploy -Prelease
```

The build produces:
- `target/miot-coordinator-<version>.jar` - The extension JAR
- `target/miot-coordinator-<version>-tests.jar` - Test JAR for integration testing
- `target/Dockerfile` - Processed Dockerfile with Maven property substitution
- `target/extensions/` - Collected JARs and AMPs for Docker image assembly

### Docker Image

The custom ACS Docker image is built from `src/main/docker/Dockerfile`. It extends `alfresco/alfresco-content-repository-community:25.2.0` and:
1. Copies extension JARs to Tomcat's `WEB-INF/lib/`
2. Installs AMP modules via Alfresco Module Management Tool (MMT)
3. Applies custom `alfresco-global.properties` and logging configuration

### CI/CD Pipeline

Defined in `.github/workflows/ci.yaml`. Triggers on push to `trunk`/`main`, pull requests, and version tags (`v*`).

**Jobs:**
1. **Build and Test** - Compiles with Maven, skips integration tests, uploads artifacts
2. **Publish to Maven Central** - Release tags only; signs with GPG, publishes via Sonatype Central Portal
3. **Publish Docker Images** - Builds multi-arch images (`linux/amd64`, `linux/arm64`) and pushes to:
   - GHCR: `ghcr.io/microboxlabs/miot-coordinator`
   - Docker Hub: `docker.io/microboxlabs/miot-coordinator`

**Tagging strategy:**
| Event | Docker Tags |
|-------|-------------|
| Pull request | `pr-<number>`, `sha-<sha>` |
| Push to trunk/main | `latest`, `sha-<sha>` |
| Tag `v1.2.3` | `1.2.3`, `1.2`, `1`, `sha-<sha>` |

**Security:** Trivy vulnerability scanning runs on pushed images (non-PR), with results uploaded to the GitHub Security tab.

### Creating a Release

```bash
git tag v1.0.0
git push origin v1.0.0
```

This triggers the full pipeline: build, Maven Central publish, and Docker image publish.

## Pull Request Guidelines

- **Branch:** Target `trunk` (primary development branch)
- **CI checks:** The build job must pass (Maven compile + unit tests)
- **Integration tests:** Run locally with `./run.sh build_test` before submitting
- **Commit style:** Follow conventional commit format as seen in history (e.g., `feat:`, `fix:`, `chore:`, `refactor:`)

## Debugging and Troubleshooting

- **Remote debugging:** Attach a debugger to `localhost:8888` (JDWP) when the Docker environment is running
- **Logs:** Use `./run.sh tail` to stream all container logs
- **Database:** PostgreSQL is accessible at `localhost:5555` (user: `alfresco`, password: `alfresco`, database: `alfresco`)
- **SOLR reindex issues:** Try `./run.sh purge` followed by `./run.sh build_start` for a clean slate
- **Maven property substitution:** Resource files (`.properties`, `.xml`) under `src/main/docker/` and `docker/` are filtered by Maven during the `validate` phase. Verify processed output in `target/`

## Additional Notes

- This is a single-module Maven project with no parent POM
- JARs (not WARs) are the deployment unit; they are placed in Tomcat's classpath inside the Docker image
- The AMP assembly plugin is present but commented out in `pom.xml`; enable it if third-party library bundling is needed
- Maven repositories for Alfresco artifacts are configured directly in `pom.xml` (both release and snapshot channels)
- The `docker-compose.yml` creates named Docker volumes (`coordinator-acs-volume`, `coordinator-db-volume`, `coordinator-ass-volume`) for data persistence across restarts
