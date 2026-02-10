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
│   │   ├── java/com/microboxlabs/miot/
│   │   │   ├── core/                # Shared kernel: exceptions, annotations, utilities (no Alfresco deps)
│   │   │   │   ├── annotation/      #   @Internal marker
│   │   │   │   ├── config/          #   Shared @Configuration beans
│   │   │   │   ├── exception/       #   MiotException hierarchy
│   │   │   │   ├── model/           #   OperationResult, PagedResult value objects
│   │   │   │   └── util/            #   Preconditions, JsonUtil helpers
│   │   │   ├── feature/             # Self-contained feature modules
│   │   │   │   ├── auth/            #   JWT/token utilities
│   │   │   │   ├── content/         #   Node content management
│   │   │   │   ├── http/            #   Generic REST client
│   │   │   │   ├── job/             #   Async job scheduling
│   │   │   │   ├── messagetemplates/ #  Dynamic message template rendering
│   │   │   │   ├── messaging/       #   Internal event/message bus
│   │   │   │   ├── monitoring/      #   Metrics/observability
│   │   │   │   ├── notification/    #   Pluggable notification framework
│   │   │   │   ├── properties/      #   Configuration management
│   │   │   │   ├── sign/            #   PDF digital signing
│   │   │   │   ├── sse/             #   Server-Sent Events
│   │   │   │   ├── tasklistener/    #   Workflow task event dispatcher
│   │   │   │   └── transform/       #   Document transformation (PDF, PNG)
│   │   │   ├── integration/         # External system connectors
│   │   │   │   ├── auth0/           #   Auth0 authentication
│   │   │   │   └── pgrest/          #   PostgreSQL REST API
│   │   │   └── platform/            # Alfresco-specific wiring
│   │   │       ├── action/          #   Repository actions
│   │   │       ├── bootstrap/       #   MiotModuleComponent (module init)
│   │   │       ├── policy/          #   Content model behaviors
│   │   │       ├── webscript/       #   AbstractMiotWebScript + health check
│   │   │       └── workflow/        #   Workflow listeners/delegates
│   │   ├── docker/                  # Dockerfile and ACS container configuration
│   │   ├── assembly/                # AMP assembly descriptor and web resources
│   │   └── resources/
│   │       └── alfresco/
│   │           ├── extension/templates/webscripts/  # Web script descriptors and FTL templates
│   │           └── module/miot-coordinator/         # Module definition root
│   │               ├── module.properties            # Module metadata
│   │               ├── module-context.xml           # Spring context root (component scanning enabled)
│   │               ├── context/                     # Minimal XML for Alfresco bootstrap wiring
│   │               ├── model/                       # Custom content and workflow models
│   │               ├── workflow/                    # BPMN 2.0 process definitions
│   │               └── messages/                    # i18n resource bundles
│   └── test/java/com/microboxlabs/miot/
│       ├── core/                    # Core unit tests
│       ├── platform/                # Platform unit tests
│       └── testutil/                # IntegrationTestBase for future ITs
└── .github/workflows/ci.yaml       # CI/CD pipeline
```

### Package Dependency Rules

```
core  <──  feature  <──  integration
  ^           ^               ^
  └───────────┴───────────────┴─── platform (Alfresco-specific shell)
```

- **`core`** — Zero Alfresco dependencies. Depends on nothing else in the module.
- **`feature`** — Depends only on `core`. Each feature is self-contained.
- **`integration`** — Depends on `core` and optionally `feature` interfaces.
- **`platform`** — Depends on everything above plus Alfresco APIs.

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
- **Root package:** `com.microboxlabs.miot`
- **Top-level packages:** `core`, `feature`, `integration`, `platform`
- **Class naming:** PascalCase (`MiotModuleComponent`, `HealthCheckWebScript`)
- **Method/variable naming:** camelCase
- **Test classes:** `<ClassName>Test.java` for unit tests, `<ClassName>IT.java` for integration tests
- **License headers:** Apache License 2.0 on all source files
- **Encoding:** UTF-8 (`project.build.sourceEncoding` and `project.reporting.outputEncoding`)
- **No explicit linter/formatter** is configured; follow existing patterns in the codebase

### Feature Module Conventions

Each feature under `feature/<name>/` follows:
```
api/         # Public interfaces, value objects, exceptions — the contract
internal/    # @Service/@Component implementations — the details
config/      # Optional @Configuration classes
package-info.java
```
This convention makes extraction to a standalone Maven module mechanical: move the package to a new module with no renames needed. Mark implementation classes with `@Internal` to signal they are not public API.

### Spring Configuration

This project uses **annotation-based Spring configuration**. Component scanning is enabled for the `com.microboxlabs` package in `module-context.xml`. Prefer Spring annotations over XML bean definitions:

- Use `@Component`, `@Service`, `@Repository` to declare beans
- Use `@Autowired` or constructor injection for dependencies
- Use `@Qualifier` when multiple beans of the same type exist
- XML context files under `context/` should remain minimal — only use XML for Alfresco-specific bootstrap wiring (content models, workflows, i18n) that cannot be expressed via annotations

### Alfresco Conventions

- **Module ID:** `miot-coordinator` (defined in `module.properties`)
- **Spring context root:** `module-context.xml` — imports bootstrap and webscript contexts; enables `<context:component-scan>` for `com.microboxlabs`
- **XML contexts** (`context/*.xml`): Only `bootstrap-context.xml` (content models, workflows, i18n) and `webscript-context.xml` (web script bean wiring). Service beans use annotations exclusively — there is no `service-context.xml`
- **Content models:** XML-based, registered via `bootstrap-context.xml` (XML required by Alfresco)
- **Web scripts:** Consist of a descriptor (`.desc.xml`), controller (`.java` or `.js`), and template (`.ftl`). Java-backed web script controllers can use `@Component` with Alfresco's `DeclarativeWebScript` base class
- **Workflows:** Activiti BPMN 2.0 XML, registered in `bootstrap-context.xml` (XML required by Alfresco)
- **i18n:** Properties files in `messages/` directory, loaded by the bootstrap context (XML required by Alfresco)

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

## Documentation Sync

When making changes, always check whether they affect content documented in `README.md` or guidelines defined in this `AGENTS.md`. If they do, update both files as part of the same change to keep documentation in sync. Examples of changes that require a documentation update:

- Adding, removing, or renaming `run.sh` commands or Maven profiles
- Changing build commands, Docker image names, registries, or tagging strategy
- Modifying CI/CD pipeline triggers, jobs, or required secrets
- Altering project structure, package names, or module configuration
- Updating dependency versions referenced in documentation (Alfresco SDK, Java, etc.)
- Changing development conventions (Spring config approach, testing patterns, code style)
- Adding or modifying service endpoints, ports, or environment variables

## Additional Notes

- This is a single-module Maven project with no parent POM
- JARs (not WARs) are the deployment unit; they are placed in Tomcat's classpath inside the Docker image
- The AMP assembly plugin is present but commented out in `pom.xml`; enable it if third-party library bundling is needed
- Maven repositories for Alfresco artifacts are configured directly in `pom.xml` (both release and snapshot channels)
- The `docker-compose.yml` creates named Docker volumes (`coordinator-acs-volume`, `coordinator-db-volume`, `coordinator-ass-volume`) for data persistence across restarts
